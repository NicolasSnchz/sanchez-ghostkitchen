package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.aplicacion.progreso.Novedad;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;
import java.util.List;

/** Respuesta completa de una accion: que se hizo, cuanto sumo y que cambio en el progreso. */
public record ResultadoAccion(TipoAccion tipo, ResultadoComando resultado, List<Novedad> novedades) {

    public ResultadoAccion {
        novedades = List.copyOf(novedades);
    }
}
