package co.edu.udes.ghostkitchen.dominio.puntos;

import java.util.Objects;

/**
 * Decorador base. Envuelve a otra calculadora, le pide su resultado y, solo si la
 * bonificacion aplica a esta caza, lo mejora. Las subclases deciden cuando aplica
 * y cuanto aporta; el orden en que se envuelven decide como se combinan.
 */
public abstract class BonificacionPuntos implements CalculadoraPuntos {

    private final CalculadoraPuntos envuelta;

    protected BonificacionPuntos(CalculadoraPuntos envuelta) {
        this.envuelta = Objects.requireNonNull(envuelta, "La calculadora envuelta es obligatoria");
    }

    @Override
    public final Puntaje calcular(ContextoCaza contexto) {
        Puntaje acumulado = envuelta.calcular(contexto);
        return aplica(contexto) ? bonificar(acumulado, contexto) : acumulado;
    }

    /** Indica si esta bonificacion corresponde a la caza dada. */
    protected abstract boolean aplica(ContextoCaza contexto);

    /** Devuelve el puntaje acumulado con el aporte de esta bonificacion. */
    protected abstract Puntaje bonificar(Puntaje acumulado, ContextoCaza contexto);
}
