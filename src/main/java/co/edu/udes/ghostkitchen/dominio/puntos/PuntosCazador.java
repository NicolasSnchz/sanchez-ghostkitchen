package co.edu.udes.ghostkitchen.dominio.puntos;

import co.edu.udes.ghostkitchen.dominio.modelo.Resena;

/**
 * Formula unica del puntaje total de un cazador. El perfil y el ranking la usan
 * por igual, asi que es imposible que muestren cifras distintas.
 */
public final class PuntosCazador {

    private PuntosCazador() {
    }

    public static int total(long puntosDeCazas, long resenasPublicadas, long recompensasDeRetos) {
        return Math.toIntExact(puntosDeCazas
                + resenasPublicadas * Resena.PUNTOS_POR_RESENA
                + recompensasDeRetos);
    }
}
