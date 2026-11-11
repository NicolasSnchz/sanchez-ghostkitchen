package co.edu.udes.ghostkitchen.dominio.orden;

import java.util.Locale;

/** Formas en que el visitante puede ordenar el listado de cocinas. */
public enum TipoOrden {
    CERCANIA,
    CALIFICACION,
    POPULARIDAD,
    NOVEDAD;

    /** Interpreta el valor que llega en la URL; ante algo vacio o desconocido usa la cercania. */
    public static TipoOrden desde(String valor) {
        if (valor == null || valor.isBlank()) {
            return CERCANIA;
        }
        try {
            return valueOf(valor.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException desconocido) {
            return CERCANIA;
        }
    }
}
