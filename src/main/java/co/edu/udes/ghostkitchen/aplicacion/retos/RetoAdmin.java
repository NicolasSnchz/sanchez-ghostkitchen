package co.edu.udes.ghostkitchen.aplicacion.retos;

import co.edu.udes.ghostkitchen.dominio.modelo.EstadoReto;
import java.time.LocalDate;

/** Fila del listado de retos para el administrador. */
public record RetoAdmin(
        Long id,
        String titulo,
        int metaCocinas,
        int puntosRecompensa,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        EstadoReto estado,
        long participantes) {
}
