package co.edu.udes.ghostkitchen.dominio.puntos;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/** Cuenta los dias consecutivos de caza que un cazador trae hasta ayer. */
public final class RachaDiaria {

    private RachaDiaria() {
    }

    /**
     * @param diasConCaza fechas en las que el cazador registro al menos una caza
     * @param hoy         fecha actual; el dia de hoy no cuenta para la racha
     * @return dias seguidos con caza terminando ayer; cero si ayer no cazo
     */
    public static int diasConsecutivosHasta(Collection<LocalDate> diasConCaza, LocalDate hoy) {
        Set<LocalDate> dias = new HashSet<>(diasConCaza);
        int racha = 0;
        LocalDate dia = hoy.minusDays(1);
        while (dias.contains(dia)) {
            racha++;
            dia = dia.minusDays(1);
        }
        return racha;
    }
}
