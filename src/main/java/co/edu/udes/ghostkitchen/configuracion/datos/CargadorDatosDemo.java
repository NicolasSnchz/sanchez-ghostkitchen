package co.edu.udes.ghostkitchen.configuracion.datos;

import co.edu.udes.ghostkitchen.aplicacion.progreso.NotificadorProgreso;
import co.edu.udes.ghostkitchen.aplicacion.retos.AvanceRetos;
import co.edu.udes.ghostkitchen.configuracion.datos.DatosDemo.CazaDemo;
import co.edu.udes.ghostkitchen.configuracion.datos.DatosDemo.CazadorDemo;
import co.edu.udes.ghostkitchen.configuracion.datos.DatosDemo.CocinaDemo;
import co.edu.udes.ghostkitchen.configuracion.datos.DatosDemo.InscripcionDemo;
import co.edu.udes.ghostkitchen.configuracion.datos.DatosDemo.ResenaDemo;
import co.edu.udes.ghostkitchen.configuracion.datos.DatosDemo.RetoDemo;
import co.edu.udes.ghostkitchen.dominio.modelo.AccionRegistrada;
import co.edu.udes.ghostkitchen.dominio.modelo.Administrador;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Descubrimiento;
import co.edu.udes.ghostkitchen.dominio.modelo.ParticipacionReto;
import co.edu.udes.ghostkitchen.dominio.modelo.Resena;
import co.edu.udes.ghostkitchen.dominio.modelo.Reto;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;
import co.edu.udes.ghostkitchen.dominio.puntos.CalculadoraPuntos;
import co.edu.udes.ghostkitchen.dominio.puntos.ContextoCaza;
import co.edu.udes.ghostkitchen.dominio.puntos.Puntaje;
import co.edu.udes.ghostkitchen.dominio.puntos.RachaDiaria;
import co.edu.udes.ghostkitchen.persistencia.RepositorioAcciones;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioParticipaciones;
import co.edu.udes.ghostkitchen.persistencia.RepositorioRetos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioUsuarios;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga un catalogo de demostracion al arrancar, salvo en el perfil de pruebas.
 * Las cocinas son ficticias pero estan ubicadas en barrios reales de Cucuta. Los
 * puntos de cada caza se calculan con la misma cadena de decoradores de la
 * aplicacion y, al final, los observadores reales sincronizan retos e insignias.
 */
@Component
@Profile("!test")
public class CargadorDatosDemo implements ApplicationRunner {

    static final String RECURSO = "datos-demo.json";
    private static final Logger LOG = LoggerFactory.getLogger(CargadorDatosDemo.class);
    private static final int HORA_DE_INSCRIPCION = 8;
    private static final int MINUTOS_HASTA_LA_RESENA = 45;

    private final Repositorios repos;
    private final ObjectMapper lectorJson;
    private final PasswordEncoder cifrador;
    private final CalculadoraPuntos calculadora;
    private final AvanceRetos avance;
    private final NotificadorProgreso notificador;
    private final Clock reloj;

    public CargadorDatosDemo(Repositorios repos, ObjectMapper lectorJson, PasswordEncoder cifrador,
                             CalculadoraPuntos calculadora, AvanceRetos avance, NotificadorProgreso notificador,
                             Clock reloj) {
        this.repos = repos;
        this.lectorJson = lectorJson;
        this.cifrador = cifrador;
        this.calculadora = calculadora;
        this.avance = avance;
        this.notificador = notificador;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments argumentos) throws IOException {
        if (repos.cocinas().count() > 0) {
            LOG.info("El catalogo ya tiene datos; no se cargan datos de demostracion");
            return;
        }
        DatosDemo datos = leer();
        LocalDate hoy = LocalDate.now(reloj);
        repos.usuarios().save(new Administrador(datos.administrador().nombre(), datos.administrador().correo(),
                cifrador.encode(datos.administrador().clave()), hoy.minusDays(60).atTime(9, 0)));
        Map<String, CocinaFantasma> cocinas = guardarCocinas(datos.cocinas(), hoy);
        Map<String, Reto> retos = guardarRetos(datos.retos(), hoy);
        List<CazaPlaneada> plan = new ArrayList<>();
        Map<Long, List<ParticipacionReto>> inscripciones = new LinkedHashMap<>();
        for (CazadorDemo demo : datos.cazadores()) {
            Cazador cazador = repos.cazadores().save(new Cazador(demo.nombre(), demo.alias(), demo.correo(),
                    cifrador.encode(demo.clave()), hoy.minusDays(demo.diasDesdeRegistro()).atTime(10, 0)));
            inscripciones.put(cazador.getId(), inscribir(cazador, demo.retos(), retos, hoy));
            demo.cazas().forEach(caza -> plan.add(planear(cazador, caza, cocinas, hoy)));
        }
        registrarEnOrdenCronologico(plan, inscripciones);
        inscripciones.keySet().forEach(cazadorId -> notificador.notificar(cazadorId, 0));
        LOG.info("Datos de demostracion cargados: {} cocinas, {} retos, {} cazadores y {} cazas",
                cocinas.size(), retos.size(), inscripciones.size(), plan.size());
    }

    private DatosDemo leer() throws IOException {
        try (InputStream entrada = new ClassPathResource(RECURSO).getInputStream()) {
            return lectorJson.readValue(entrada, DatosDemo.class);
        }
    }

    private Map<String, CocinaFantasma> guardarCocinas(List<CocinaDemo> demos, LocalDate hoy) {
        Map<String, CocinaFantasma> cocinas = new LinkedHashMap<>();
        for (CocinaDemo demo : demos) {
            CocinaFantasma cocina = CocinaFantasma.builder()
                    .nombre(demo.nombre())
                    .descripcion(demo.descripcion())
                    .categoria(demo.categoria())
                    .rareza(demo.rareza())
                    .horario(demo.apertura(), demo.cierre())
                    .ubicacion(demo.latitud(), demo.longitud(), demo.barrio())
                    .canales(demo.canales())
                    .registradaEl(hoy.minusDays(demo.diasDesdeAlta()))
                    .build();
            if (!demo.activa()) {
                cocina.desactivar();
            }
            cocinas.put(demo.nombre(), repos.cocinas().save(cocina));
        }
        return cocinas;
    }

    private Map<String, Reto> guardarRetos(List<RetoDemo> demos, LocalDate hoy) {
        Map<String, Reto> retos = new LinkedHashMap<>();
        for (RetoDemo demo : demos) {
            Reto reto = new Reto(demo.titulo(), demo.descripcion(), demo.metaCocinas(), demo.puntosRecompensa(),
                    hoy.plusDays(demo.inicioRelativo()), hoy.plusDays(demo.finRelativo()));
            retos.put(demo.titulo(), repos.retos().save(reto));
        }
        return retos;
    }

    private List<ParticipacionReto> inscribir(Cazador cazador, List<InscripcionDemo> inscripciones,
                                              Map<String, Reto> retos, LocalDate hoy) {
        List<ParticipacionReto> participaciones = new ArrayList<>();
        for (InscripcionDemo inscripcion : inscripciones) {
            Reto reto = buscar(retos, inscripcion.titulo(), "reto");
            LocalDateTime momento = hoy.minusDays(inscripcion.diasAtras()).atTime(HORA_DE_INSCRIPCION, 0);
            participaciones.add(repos.participaciones().save(new ParticipacionReto(cazador, reto, momento)));
            repos.acciones().save(new AccionRegistrada(cazador, TipoAccion.UNIRSE_A_RETO, reto.getId(),
                    reto.getTitulo(), 0, momento));
        }
        return participaciones;
    }

    private static CazaPlaneada planear(Cazador cazador, CazaDemo caza, Map<String, CocinaFantasma> cocinas,
                                        LocalDate hoy) {
        return new CazaPlaneada(cazador, buscar(cocinas, caza.cocina(), "cocina"),
                hoy.minusDays(caza.diasAtras()).atTime(caza.hora()), caza.resena());
    }

    /**
     * Se procesa en orden de tiempo para que el bono de primer cazador, las rachas
     * y la fecha en que se completa cada reto queden igual que si las cazas
     * hubieran ocurrido de verdad.
     */
    private void registrarEnOrdenCronologico(List<CazaPlaneada> plan,
                                             Map<Long, List<ParticipacionReto>> inscripciones) {
        Set<Long> cocinasYaCazadas = new HashSet<>();
        Map<Long, List<LocalDate>> diasPorCazador = new HashMap<>();
        plan.sort(Comparator.comparing(CazaPlaneada::momento));
        for (CazaPlaneada caza : plan) {
            List<LocalDate> dias = diasPorCazador.computeIfAbsent(caza.cazador().getId(), id -> new ArrayList<>());
            ContextoCaza contexto = new ContextoCaza(caza.cocina().getRareza(),
                    cocinasYaCazadas.add(caza.cocina().getId()),
                    RachaDiaria.diasConsecutivosHasta(dias, caza.momento().toLocalDate()),
                    caza.momento().toLocalTime());
            Puntaje puntaje = calculadora.calcular(contexto);
            Descubrimiento descubrimiento = Descubrimiento.registrar(caza.cazador(), caza.cocina(), caza.momento(),
                    puntaje.total());
            repos.acciones().save(new AccionRegistrada(caza.cazador(), TipoAccion.DESCUBRIR_COCINA,
                    caza.cocina().getId(), caza.cocina().getNombre(), puntaje.total(), caza.momento()));
            if (caza.resena() != null) {
                resenar(caza, descubrimiento);
            }
            repos.descubrimientos().save(descubrimiento);
            dias.add(caza.momento().toLocalDate());
            for (ParticipacionReto participacion : inscripciones.get(caza.cazador().getId())) {
                participacion.actualizarAvance(avance.cocinasCazadasPara(participacion), caza.momento());
            }
        }
    }

    private void resenar(CazaPlaneada caza, Descubrimiento descubrimiento) {
        LocalDateTime momento = caza.momento().plusMinutes(MINUTOS_HASTA_LA_RESENA);
        descubrimiento.resenar(caza.resena().calificacion(), caza.resena().comentario(), momento);
        repos.acciones().save(new AccionRegistrada(caza.cazador(), TipoAccion.PUBLICAR_RESENA, caza.cocina().getId(),
                caza.cocina().getNombre(), Resena.PUNTOS_POR_RESENA, momento));
    }

    private static <T> T buscar(Map<String, T> elementos, String nombre, String tipo) {
        T elemento = elementos.get(nombre);
        if (elemento == null) {
            throw new IllegalStateException("El archivo " + RECURSO + " menciona un " + tipo + " que no existe: "
                    + nombre);
        }
        return elemento;
    }

    private record CazaPlaneada(Cazador cazador, CocinaFantasma cocina, LocalDateTime momento, ResenaDemo resena) {
    }

    /** Agrupa los repositorios que usa la carga, para no inflar el constructor. */
    @Component
    @Profile("!test")
    public record Repositorios(RepositorioUsuarios usuarios, RepositorioCazadores cazadores,
                               RepositorioCocinas cocinas, RepositorioRetos retos,
                               RepositorioParticipaciones participaciones,
                               RepositorioDescubrimientos descubrimientos, RepositorioAcciones acciones) {
    }
}
