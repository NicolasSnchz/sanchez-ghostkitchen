package co.edu.udes.ghostkitchen.aplicacion.progreso;

import java.util.List;

/**
 * Observador del patron Observer. Cada implementacion reacciona por su cuenta a
 * un cambio en el progreso de un cazador (retos, insignias, nivel) sin que quien
 * provoco el cambio sepa cuantos observadores hay ni que hace cada uno.
 */
public interface ObservadorProgreso {

    /**
     * Reacciona al cambio y devuelve lo que hay que contarle al cazador.
     *
     * @param evento datos del cambio
     * @return novedades producidas por este observador; vacia si no hubo ninguna
     */
    List<Novedad> alCambiarProgreso(EventoProgreso evento);
}
