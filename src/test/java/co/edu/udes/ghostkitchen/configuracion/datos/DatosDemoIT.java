package co.edu.udes.ghostkitchen.configuracion.datos;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.udes.ghostkitchen.aplicacion.progreso.ServicioProgreso;
import co.edu.udes.ghostkitchen.aplicacion.ranking.PosicionRanking;
import co.edu.udes.ghostkitchen.aplicacion.ranking.ServicioRanking;
import co.edu.udes.ghostkitchen.aplicacion.retos.AvanceRetos;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.Descubrimiento;
import co.edu.udes.ghostkitchen.dominio.modelo.ParticipacionReto;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoInsignia;
import co.edu.udes.ghostkitchen.dominio.puntos.RachaDiaria;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioInsignias;
import co.edu.udes.ghostkitchen.persistencia.RepositorioParticipaciones;
import co.edu.udes.ghostkitchen.persistencia.RepositorioRetos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioUsuarios;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verifica que los datos de demostracion respetan las mismas reglas que impone
 * la aplicacion: horarios, limite diario, fechas coherentes y puntos consistentes.
 */
@SpringBootTest
@ActiveProfiles("demo")
@Transactional
class DatosDemoIT {

    @Autowired
    private CargadorDatosDemo cargador;

    @Autowired
    private RepositorioUsuarios usuarios;

    @Autowired
    private RepositorioCazadores cazadores;

    @Autowired
    private RepositorioCocinas cocinas;

    @Autowired
    private RepositorioRetos retos;

    @Autowired
    private RepositorioDescubrimientos descubrimientos;

    @Autowired
    private RepositorioParticipaciones participaciones;

    @Autowired
    private RepositorioInsignias insignias;

    @Autowired
    private AvanceRetos avance;

    @Autowired
    private ServicioProgreso progreso;

    @Autowired
    private ServicioRanking ranking;

    @Autowired
    private Clock reloj;

    @Test
    @DisplayName("PI-011 Los datos de demostracion cumplen las reglas del juego y la carga es idempotente")
    void datosDemo_respetanLasReglasYNoSeDuplican() throws Exception {
        assertThat(cocinas.count()).isEqualTo(16);
        assertThat(cazadores.count()).isEqualTo(6);
        assertThat(usuarios.count()).isEqualTo(7);
        assertThat(retos.count()).isEqualTo(5);
        assertThat(descubrimientos.count()).isEqualTo(36);

        LocalDateTime ahora = LocalDateTime.now(reloj);
        List<Descubrimiento> cazas = descubrimientos.findAll();
        for (Descubrimiento caza : cazas) {
            assertThat(caza.getCocina().estaAbierta(caza.getFechaHora().toLocalTime()))
                    .as("%s debe estar abierta cuando la cazan", caza.getCocina().getNombre()).isTrue();
            assertThat(caza.getFechaHora()).isBefore(ahora)
                    .isAfter(caza.getCocina().getFechaAlta().atStartOfDay())
                    .isAfter(caza.getCazador().getFechaRegistro());
        }
        Map<String, Long> cazasPorDia = cazas.stream().collect(Collectors.groupingBy(
                caza -> caza.getCazador().getAlias() + caza.getFechaHora().toLocalDate(), Collectors.counting()));
        assertThat(cazasPorDia.values()).isNotEmpty().allMatch(total -> total <= Descubrimiento.MAXIMO_CAZAS_POR_DIA);

        cargador.run(null);
        assertThat(descubrimientos.count()).isEqualTo(36);
    }

    @Test
    void cazadorDemo_tieneRachaYUnRetoAPuntoDeCompletar() {
        Cazador demo = cazadores.findAll().stream().filter(c -> c.getAlias().equals("cazador_demo")).findFirst()
                .orElseThrow();
        LocalDate hoy = LocalDate.now(reloj);
        List<LocalDate> dias = descubrimientos.fechasDeCazaDe(demo.getId()).stream()
                .map(LocalDateTime::toLocalDate).toList();
        ParticipacionReto semana = participaciones.deCazador(demo.getId()).get(0);

        assertThat(RachaDiaria.diasConsecutivosHasta(dias, hoy)).isEqualTo(2);
        assertThat(semana.getReto().getTitulo()).isEqualTo("Semana del rastreador");
        assertThat(avance.cocinasCazadasPara(semana)).isEqualTo(3);
        assertThat(semana.estaCompletada()).isFalse();
        assertThat(insignias.findByCazadorId(demo.getId())).extracting("tipo")
                .contains(TipoInsignia.PRIMER_HALLAZGO);
    }

    @Test
    void retoTerminado_quedaCompletadoEnLaFechaDeLaTerceraCaza() {
        Cazador brasa = cazadores.findAll().stream().filter(c -> c.getAlias().equals("brasa_norte")).findFirst()
                .orElseThrow();
        ParticipacionReto arranque = participaciones.deCazador(brasa.getId()).stream()
                .filter(p -> p.getReto().getTitulo().equals("Arranque de temporada")).findFirst().orElseThrow();

        assertThat(arranque.getFechaCompletado())
                .isEqualTo(LocalDate.now(reloj).minusDays(20).atTime(13, 30));
    }

    @Test
    void ranking_coincideConLosPuntosDeCadaPanel() {
        List<PosicionRanking> posiciones = ranking.clasificacion(50);

        assertThat(posiciones).hasSize(6).allSatisfy(posicion ->
                assertThat(posicion.puntos()).isEqualTo(progreso.puntosDe(posicion.cazadorId())));
    }
}
