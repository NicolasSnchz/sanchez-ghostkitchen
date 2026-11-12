package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.dominio.modelo.AccionRegistrada;

/**
 * Creador del patron Factory Method. Quien pide una accion obtiene el comando
 * listo, con su receptor ya conectado, sin conocer las clases concretas.
 */
public interface FabricaComandos {

    /** Crea el comando que registra la caza de una cocina por parte del cazador. */
    Comando descubrirCocina(Long cazadorId, Long cocinaId);

    /** Crea el comando que publica la resena del cazador sobre una cocina que ya cazo. */
    Comando publicarResena(Long cazadorId, Long cocinaId, NuevaResena resena);

    /** Crea el comando que inscribe al cazador en un reto vigente. */
    Comando unirseAReto(Long cazadorId, Long retoId);

    /**
     * Metodo fabrica que reconstruye, a partir de una entrada del historial, el
     * comando capaz de revertirla. Es necesario porque en una aplicacion web el
     * objeto comando muere al terminar la peticion que lo creo.
     */
    Comando paraDeshacer(AccionRegistrada accion);
}
