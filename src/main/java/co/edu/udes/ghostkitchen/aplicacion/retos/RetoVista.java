package co.edu.udes.ghostkitchen.aplicacion.retos;

import java.time.LocalDate;

/** Un reto vigente tal como lo ve el visitante, con su avance si esta inscrito. */
public record RetoVista(
        Long id,
        String titulo,
        String descripcion,
        int metaCocinas,
        int puntosRecompensa,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        long diasRestantes,
        long participantes,
        boolean inscrito,
        long avance,
        boolean completado) {

    /** Avance de 0 a 100 para la barra del reto. */
    public int porcentaje() {
        return (int) Math.min(100, avance * 100 / metaCocinas);
    }
}
