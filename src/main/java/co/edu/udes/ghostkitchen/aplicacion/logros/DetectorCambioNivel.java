package co.edu.udes.ghostkitchen.aplicacion.logros;

import co.edu.udes.ghostkitchen.aplicacion.progreso.EventoProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.Novedad;
import co.edu.udes.ghostkitchen.aplicacion.progreso.ObservadorProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.ServicioProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.TipoNovedad;
import co.edu.udes.ghostkitchen.dominio.modelo.Nivel;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Observador concreto: compara el nivel de antes con el de ahora. Va de ultimo
 * porque las recompensas de retos, calculadas por un observador anterior, tambien
 * suman puntos.
 */
@Component
@Order(3)
public class DetectorCambioNivel implements ObservadorProgreso {

    private final ServicioProgreso progreso;

    public DetectorCambioNivel(ServicioProgreso progreso) {
        this.progreso = progreso;
    }

    @Override
    public List<Novedad> alCambiarProgreso(EventoProgreso evento) {
        Nivel anterior = Nivel.paraPuntos(evento.puntosAntes());
        Nivel actual = progreso.nivelDe(evento.cazadorId());
        int comparacion = actual.compareTo(anterior);
        if (comparacion > 0) {
            return List.of(Novedad.de(TipoNovedad.NIVEL_SUBIDO, actual.name()));
        }
        if (comparacion < 0) {
            return List.of(Novedad.de(TipoNovedad.NIVEL_BAJADO, actual.name()));
        }
        return List.of();
    }
}
