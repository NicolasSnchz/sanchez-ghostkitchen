package co.edu.udes.ghostkitchen.dominio.puntos;

import co.edu.udes.ghostkitchen.dominio.modelo.HoraFantasma;

/**
 * Multiplica por 1,5 lo acumulado cuando la caza ocurre en la hora fantasma.
 * Al ser un multiplicador, su posicion en la cadena importa: envolviendo a los
 * bonos fijos tambien los multiplica.
 */
public final class MultiplicadorHoraFantasma extends BonificacionPuntos {

    public static final double FACTOR = 1.5;

    public MultiplicadorHoraFantasma(CalculadoraPuntos envuelta) {
        super(envuelta);
    }

    @Override
    protected boolean aplica(ContextoCaza contexto) {
        return HoraFantasma.incluye(contexto.hora());
    }

    @Override
    protected Puntaje bonificar(Puntaje acumulado, ContextoCaza contexto) {
        return acumulado.multiplicadoPor(ConceptoPuntaje.HORA_FANTASMA, FACTOR);
    }
}
