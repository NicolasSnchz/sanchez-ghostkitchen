package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import java.time.LocalDateTime;

/** Resena publicada, lista para mostrar en la ficha de la cocina. */
public record ResenaVista(String alias, int calificacion, String comentario, LocalDateTime fecha, boolean propia) {
}
