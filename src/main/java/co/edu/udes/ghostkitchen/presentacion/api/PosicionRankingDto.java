package co.edu.udes.ghostkitchen.presentacion.api;

import co.edu.udes.ghostkitchen.aplicacion.ranking.PosicionRanking;
import co.edu.udes.ghostkitchen.dominio.modelo.Nivel;

/** Fila del ranking en la API. Se omite el identificador interno del cazador. */
public record PosicionRankingDto(int posicion, String alias, Nivel nivel, int puntos, long cazas, long resenas) {

    static PosicionRankingDto desde(PosicionRanking posicion) {
        return new PosicionRankingDto(posicion.posicion(), posicion.alias(), posicion.nivel(), posicion.puntos(),
                posicion.cazas(), posicion.resenas());
    }
}
