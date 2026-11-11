package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.dominio.modelo.AccionRegistrada;

/**
 * Creador del patron Factory Method. Quien pide una accion obtiene el comando
 * listo, con su receptor ya conectado, sin conocer las clases concretas.
 */
public interface FabricaComandos {

    Comando descubrirCocina(Long cazadorId, Long cocinaId);

    Comando publicarResena(Long cazadorId, Long cocinaId, NuevaResena resena);

    Comando unirseAReto(Long cazadorId, Long retoId);

    /**
     * Metodo fabrica que reconstruye, a partir de una entrada del historial, el
     * comando capaz de revertirla. Es necesario porque en una aplicacion web el
     * objeto comando muere al terminar la peticion que lo creo.
     */
    Comando paraDeshacer(AccionRegistrada accion);
}
