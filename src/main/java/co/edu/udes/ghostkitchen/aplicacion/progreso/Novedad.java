package co.edu.udes.ghostkitchen.aplicacion.progreso;

/**
 * Algo que cambio en el progreso del cazador como efecto de una accion.
 *
 * @param tipo       que clase de cambio fue
 * @param referencia a que se refiere: el nombre de la insignia o del nivel, o el titulo del reto
 * @param valor      dato numerico asociado, por ejemplo los puntos de recompensa de un reto
 */
public record Novedad(TipoNovedad tipo, String referencia, int valor) {

    public static Novedad de(TipoNovedad tipo, String referencia) {
        return new Novedad(tipo, referencia, 0);
    }
}
