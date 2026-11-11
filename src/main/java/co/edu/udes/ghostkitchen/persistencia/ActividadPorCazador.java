package co.edu.udes.ghostkitchen.persistencia;

/** Proyeccion: resumen de la actividad de caza de un cazador, base del ranking. */
public record ActividadPorCazador(Long cazadorId, Long cazas, Long puntos, Long resenas) {
}
