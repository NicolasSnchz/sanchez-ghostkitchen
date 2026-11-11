package co.edu.udes.ghostkitchen.dominio.puntos;

/**
 * Componente del patron Decorator. Tanto el calculo base como cada bonificacion
 * cumplen este mismo contrato, por eso se pueden apilar en cualquier orden.
 */
public interface CalculadoraPuntos {

    /**
     * Calcula los puntos que otorga una caza.
     *
     * @param contexto datos de la caza ya resueltos
     * @return el puntaje con su desglose
     */
    Puntaje calcular(ContextoCaza contexto);
}
