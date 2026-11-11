package co.edu.udes.ghostkitchen.aplicacion.retos;

import co.edu.udes.ghostkitchen.aplicacion.progreso.EventoProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.Novedad;
import co.edu.udes.ghostkitchen.aplicacion.progreso.ObservadorProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.TipoNovedad;
import co.edu.udes.ghostkitchen.dominio.modelo.ParticipacionReto;
import co.edu.udes.ghostkitchen.persistencia.RepositorioParticipaciones;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Observador concreto: recalcula el avance de cada reto en que participa el
 * cazador. Va primero porque completar un reto suma la recompensa, y de esos
 * puntos dependen la insignia de retador y el nivel.
 */
@Component
@Order(1)
public class SincronizadorRetos implements ObservadorProgreso {

    private final RepositorioParticipaciones participaciones;
    private final AvanceRetos avance;

    public SincronizadorRetos(RepositorioParticipaciones participaciones, AvanceRetos avance) {
        this.participaciones = participaciones;
        this.avance = avance;
    }

    @Override
    public List<Novedad> alCambiarProgreso(EventoProgreso evento) {
        List<Novedad> novedades = new ArrayList<>();
        for (ParticipacionReto participacion : participaciones.deCazador(evento.cazadorId())) {
            boolean estabaCompletada = participacion.estaCompletada();
            boolean seCompleto = participacion.actualizarAvance(
                    avance.cocinasCazadasPara(participacion), evento.momento());
            if (seCompleto) {
                novedades.add(new Novedad(TipoNovedad.RETO_COMPLETADO, participacion.getReto().getTitulo(),
                        participacion.getReto().getPuntosRecompensa()));
            } else if (estabaCompletada && !participacion.estaCompletada()) {
                novedades.add(Novedad.de(TipoNovedad.RETO_REVERTIDO, participacion.getReto().getTitulo()));
            }
        }
        return novedades;
    }
}
