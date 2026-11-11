package co.edu.udes.ghostkitchen.aplicacion.progreso;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Sujeto del patron Observer. Recibe de Spring la lista de observadores ya
 * ordenada con {@code @Order}: primero se sincronizan los retos, porque una
 * recompensa cambia los puntos, despues las insignias y al final el nivel.
 */
@Component
public class NotificadorProgreso {

    private final List<ObservadorProgreso> observadores;
    private final Clock reloj;

    public NotificadorProgreso(List<ObservadorProgreso> observadores, Clock reloj) {
        this.observadores = List.copyOf(observadores);
        this.reloj = reloj;
    }

    public List<Novedad> notificar(Long cazadorId, int puntosAntes) {
        EventoProgreso evento = new EventoProgreso(cazadorId, puntosAntes, LocalDateTime.now(reloj));
        return observadores.stream()
                .flatMap(observador -> observador.alCambiarProgreso(evento).stream())
                .toList();
    }

    public int cantidadDeObservadores() {
        return observadores.size();
    }
}
