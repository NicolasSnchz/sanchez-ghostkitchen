package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;

/**
 * Comando del patron Command. Convierte una accion del cazador en un objeto que
 * sabe ejecutarse y revertirse; por eso el historial puede guardarla, mostrarla
 * y deshacerla sin conocer los detalles de cada accion.
 */
public interface Comando {

    /** Tipo de accion que representa, para guardarla en el historial. */
    TipoAccion tipo();

    /** Cazador que solicita la accion. */
    Long cazadorId();

    /**
     * Realiza la accion delegando en su receptor.
     *
     * @return sobre que objetivo se actuo y cuantos puntos produjo
     */
    ResultadoComando ejecutar();

    /** Revierte por completo los efectos de {@link #ejecutar()}. */
    void deshacer();
}
