package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.aplicacion.progreso.Novedad;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;
import java.util.List;

/** Respuesta de deshacer: que accion se revirtio, cuantos puntos se restaron y que cambio. */
public record ResultadoDeshacer(TipoAccion tipo, String detalle, int puntosRetirados, List<Novedad> novedades) {

    public ResultadoDeshacer {
        novedades = List.copyOf(novedades);
    }
}
