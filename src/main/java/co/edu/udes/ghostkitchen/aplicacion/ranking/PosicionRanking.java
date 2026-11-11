package co.edu.udes.ghostkitchen.aplicacion.ranking;

import co.edu.udes.ghostkitchen.dominio.modelo.Nivel;

/** Un renglon de la tabla de posiciones. Los empates comparten posicion. */
public record PosicionRanking(int posicion, Long cazadorId, String alias, int puntos, Nivel nivel,
                              long cazas, long resenas) {
}
