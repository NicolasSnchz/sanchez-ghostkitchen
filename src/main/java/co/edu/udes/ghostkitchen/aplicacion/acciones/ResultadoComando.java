package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.dominio.puntos.Puntaje;
import java.util.List;

/**
 * Lo que produjo un comando al ejecutarse.
 *
 * @param objetivoId identificador de la cocina o del reto sobre el que se actuo
 * @param detalle    nombre legible del objetivo
 * @param puntos     puntos que la accion sumo de inmediato
 * @param desglose   como se compusieron esos puntos; vacio si no hay nada que desglosar
 */
public record ResultadoComando(Long objetivoId, String detalle, int puntos, List<Puntaje.Linea> desglose) {

    public ResultadoComando {
        desglose = List.copyOf(desglose);
    }

    public static ResultadoComando sinDesglose(Long objetivoId, String detalle, int puntos) {
        return new ResultadoComando(objetivoId, detalle, puntos, List.of());
    }
}
