package co.edu.udes.ghostkitchen.dominio.puntos;

import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import java.time.LocalTime;

/**
 * Lo que las reglas de puntos necesitan saber sobre una caza. Al recibirlo
 * ya resuelto, las calculadoras no consultan la base de datos ni el reloj.
 *
 * @param rareza            rareza de la cocina cazada
 * @param primerCazador     true si nadie habia cazado esa cocina antes
 * @param diasDeRacha       dias seguidos con al menos una caza, sin contar hoy
 * @param hora              hora local en que ocurre la caza
 */
public record ContextoCaza(Rareza rareza, boolean primerCazador, int diasDeRacha, LocalTime hora) {

    public ContextoCaza {
        if (rareza == null || hora == null) {
            throw new IllegalArgumentException("La rareza y la hora de la caza son obligatorias");
        }
        if (diasDeRacha < 0) {
            throw new IllegalArgumentException("Los dias de racha no pueden ser negativos");
        }
    }
}
