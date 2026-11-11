package co.edu.udes.ghostkitchen.aplicacion.retos;

import co.edu.udes.ghostkitchen.dominio.excepcion.RecursoNoEncontradoException;
import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import co.edu.udes.ghostkitchen.dominio.modelo.ParticipacionReto;
import co.edu.udes.ghostkitchen.dominio.modelo.Reto;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioParticipaciones;
import co.edu.udes.ghostkitchen.persistencia.RepositorioRetos;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/** Receptor del comando de unirse a un reto: hace el trabajo real de inscribir y retirar. */
@Component
public class RegistroParticipaciones {

    private final RepositorioRetos retos;
    private final RepositorioCazadores cazadores;
    private final RepositorioParticipaciones participaciones;
    private final Clock reloj;

    public RegistroParticipaciones(RepositorioRetos retos, RepositorioCazadores cazadores,
                                   RepositorioParticipaciones participaciones, Clock reloj) {
        this.retos = retos;
        this.cazadores = cazadores;
        this.participaciones = participaciones;
        this.reloj = reloj;
    }

    public Reto inscribir(Long cazadorId, Long retoId) {
        Reto reto = retos.findById(retoId).orElseThrow(() -> new RecursoNoEncontradoException("Reto", retoId));
        if (!reto.estaVigente(LocalDate.now(reloj))) {
            throw new ReglaNegocioException("reto.no-vigente");
        }
        if (participaciones.existsByCazadorIdAndRetoId(cazadorId, retoId)) {
            throw new ReglaNegocioException("reto.ya-inscrito");
        }
        participaciones.save(new ParticipacionReto(cazadores.getReferenceById(cazadorId), reto,
                LocalDateTime.now(reloj)));
        return reto;
    }

    public void retirar(Long cazadorId, Long retoId) {
        participaciones.findByCazadorIdAndRetoId(cazadorId, retoId).ifPresent(participaciones::delete);
    }
}
