package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;
import java.time.LocalDateTime;

/** La accion que se puede deshacer en este momento y hasta cuando. */
public record AccionDeshacible(TipoAccion tipo, String detalle, int puntos, LocalDateTime disponibleHasta) {
}
