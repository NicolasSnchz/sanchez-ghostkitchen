package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.dominio.puntos.Puntaje;

/** Lo que devuelve el receptor al registrar una caza. */
public record CazaRegistrada(Long cocinaId, String nombreCocina, Puntaje puntaje) {
}
