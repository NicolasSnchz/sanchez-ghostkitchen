package co.edu.udes.ghostkitchen.aplicacion.progreso;

import java.time.LocalDateTime;

/**
 * Lo que el sujeto le comunica a cada observador: de quien cambio el progreso,
 * cuantos puntos tenia antes del cambio y en que momento ocurrio.
 */
public record EventoProgreso(Long cazadorId, int puntosAntes, LocalDateTime momento) {
}
