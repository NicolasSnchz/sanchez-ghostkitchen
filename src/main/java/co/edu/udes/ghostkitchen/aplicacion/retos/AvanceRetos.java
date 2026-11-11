package co.edu.udes.ghostkitchen.aplicacion.retos;

import co.edu.udes.ghostkitchen.dominio.modelo.ParticipacionReto;
import co.edu.udes.ghostkitchen.dominio.modelo.Reto;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * Cuenta cuantas cocinas lleva un cazador dentro de un reto: solo valen las cazas
 * hechas despues de unirse y antes de que el reto cierre.
 */
@Component
public class AvanceRetos {

    private final RepositorioDescubrimientos descubrimientos;

    public AvanceRetos(RepositorioDescubrimientos descubrimientos) {
        this.descubrimientos = descubrimientos;
    }

    public long cocinasCazadasPara(ParticipacionReto participacion) {
        Reto reto = participacion.getReto();
        LocalDateTime inicioDelReto = reto.getFechaInicio().atStartOfDay();
        LocalDateTime desde = participacion.getFechaUnion().isAfter(inicioDelReto)
                ? participacion.getFechaUnion()
                : inicioDelReto;
        return descubrimientos.countByCazadorIdAndFechaHoraBetween(
                participacion.getCazador().getId(), desde, reto.cierre());
    }
}
