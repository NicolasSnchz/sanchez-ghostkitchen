package co.edu.udes.ghostkitchen.aplicacion.retos;

import java.time.LocalDate;

/** Datos con los que el administrador crea un reto. */
public record DatosReto(
        String titulo,
        String descripcion,
        int metaCocinas,
        int puntosRecompensa,
        LocalDate fechaInicio,
        LocalDate fechaFin) {
}
