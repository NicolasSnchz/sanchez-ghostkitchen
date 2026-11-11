package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;
import java.time.LocalDateTime;

/** Entrada del historial lista para mostrar en la actividad reciente. */
public record AccionVista(Long id, TipoAccion tipo, String detalle, int puntos,
                          LocalDateTime fechaHora, boolean deshecha) {
}
