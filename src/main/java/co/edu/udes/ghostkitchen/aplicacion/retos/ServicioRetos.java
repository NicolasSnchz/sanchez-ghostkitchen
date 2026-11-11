package co.edu.udes.ghostkitchen.aplicacion.retos;

import co.edu.udes.ghostkitchen.dominio.excepcion.RecursoNoEncontradoException;
import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import co.edu.udes.ghostkitchen.dominio.modelo.ParticipacionReto;
import co.edu.udes.ghostkitchen.dominio.modelo.Reto;
import co.edu.udes.ghostkitchen.persistencia.ConteoPorReto;
import co.edu.udes.ghostkitchen.persistencia.RepositorioParticipaciones;
import co.edu.udes.ghostkitchen.persistencia.RepositorioRetos;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta de retos para cazadores y gestion de retos para el administrador. */
@Service
@Transactional(readOnly = true)
public class ServicioRetos {

    private final RepositorioRetos retos;
    private final RepositorioParticipaciones participaciones;
    private final AvanceRetos avance;
    private final Clock reloj;

    public ServicioRetos(RepositorioRetos retos, RepositorioParticipaciones participaciones,
                         AvanceRetos avance, Clock reloj) {
        this.retos = retos;
        this.participaciones = participaciones;
        this.avance = avance;
        this.reloj = reloj;
    }

    /** Retos vigentes hoy; si llega un cazador, cada reto trae su inscripcion y avance. */
    public List<RetoVista> vigentes(Long cazadorId) {
        LocalDate hoy = LocalDate.now(reloj);
        Map<Long, Long> participantes = participantesPorReto();
        Map<Long, ParticipacionReto> propias = participacionesDe(cazadorId);
        return retos.vigentesEn(hoy).stream()
                .map(reto -> vista(reto, hoy, participantes, propias.get(reto.getId())))
                .toList();
    }

    /** Retos vigentes en los que el cazador esta inscrito, para su panel. */
    public List<RetoVista> inscritosDe(Long cazadorId) {
        return vigentes(cazadorId).stream().filter(RetoVista::inscrito).toList();
    }

    /** Retos que todavia no empiezan; se muestran para que el cazador se prepare, sin inscripcion. */
    public List<RetoVista> proximos() {
        LocalDate hoy = LocalDate.now(reloj);
        return retos.findByFechaInicioAfterOrderByFechaInicioAscTituloAsc(hoy).stream()
                .map(reto -> new RetoVista(reto.getId(), reto.getTitulo(), reto.getDescripcion(),
                        reto.getMetaCocinas(), reto.getPuntosRecompensa(), reto.getFechaInicio(), reto.getFechaFin(),
                        ChronoUnit.DAYS.between(hoy, reto.getFechaFin()), 0, false, 0, false))
                .toList();
    }

    public List<RetoAdmin> todos() {
        LocalDate hoy = LocalDate.now(reloj);
        Map<Long, Long> participantes = participantesPorReto();
        return retos.findAllByOrderByFechaFinDescTituloAsc().stream()
                .map(reto -> new RetoAdmin(reto.getId(), reto.getTitulo(), reto.getMetaCocinas(),
                        reto.getPuntosRecompensa(), reto.getFechaInicio(), reto.getFechaFin(),
                        reto.estadoEn(hoy), participantes.getOrDefault(reto.getId(), 0L)))
                .toList();
    }

    @Transactional
    public Long crear(DatosReto datos) {
        Reto reto = new Reto(datos.titulo(), datos.descripcion(), datos.metaCocinas(),
                datos.puntosRecompensa(), datos.fechaInicio(), datos.fechaFin());
        return retos.save(reto).getId();
    }

    /** Un reto con inscritos no se borra: se perderian recompensas ya ganadas. */
    @Transactional
    public void eliminar(Long retoId) {
        Reto reto = retos.findById(retoId).orElseThrow(() -> new RecursoNoEncontradoException("Reto", retoId));
        if (participaciones.existsByRetoId(retoId)) {
            throw new ReglaNegocioException("reto.con-participantes");
        }
        retos.delete(reto);
    }

    private RetoVista vista(Reto reto, LocalDate hoy, Map<Long, Long> participantes,
                            ParticipacionReto propia) {
        long cocinas = propia == null ? 0 : avance.cocinasCazadasPara(propia);
        return new RetoVista(reto.getId(), reto.getTitulo(), reto.getDescripcion(), reto.getMetaCocinas(),
                reto.getPuntosRecompensa(), reto.getFechaInicio(), reto.getFechaFin(),
                ChronoUnit.DAYS.between(hoy, reto.getFechaFin()),
                participantes.getOrDefault(reto.getId(), 0L), propia != null,
                Math.min(cocinas, reto.getMetaCocinas()), propia != null && propia.estaCompletada());
    }

    private Map<Long, Long> participantesPorReto() {
        return participaciones.participantesPorReto().stream()
                .collect(Collectors.toMap(ConteoPorReto::retoId, ConteoPorReto::total));
    }

    private Map<Long, ParticipacionReto> participacionesDe(Long cazadorId) {
        if (cazadorId == null) {
            return Map.of();
        }
        return participaciones.deCazador(cazadorId).stream()
                .collect(Collectors.toMap(p -> p.getReto().getId(), Function.identity()));
    }
}
