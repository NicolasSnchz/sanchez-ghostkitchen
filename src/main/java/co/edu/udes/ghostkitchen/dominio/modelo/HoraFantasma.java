package co.edu.udes.ghostkitchen.dominio.modelo;

import java.time.LocalTime;

/**
 * Franja nocturna del juego: de 10:00 p. m. a 4:00 a. m. Cazar dentro de ella
 * multiplica los puntos y cuenta para la insignia de noctambulo.
 */
public final class HoraFantasma {

    public static final LocalTime INICIO = LocalTime.of(22, 0);
    public static final LocalTime FIN = LocalTime.of(4, 0);

    private HoraFantasma() {
    }

    public static boolean incluye(LocalTime hora) {
        return !hora.isBefore(INICIO) || hora.isBefore(FIN);
    }
}
