package co.edu.udes.ghostkitchen.dominio.puntos;

/** Premia la constancia: diez puntos por cada dia seguido de caza, hasta cinco dias. */
public final class BonoRachaDiaria extends BonificacionPuntos {

    public static final int PUNTOS_POR_DIA = 10;
    public static final int DIAS_MAXIMOS = 5;

    public BonoRachaDiaria(CalculadoraPuntos envuelta) {
        super(envuelta);
    }

    @Override
    protected boolean aplica(ContextoCaza contexto) {
        return contexto.diasDeRacha() > 0;
    }

    @Override
    protected Puntaje bonificar(Puntaje acumulado, ContextoCaza contexto) {
        int dias = Math.min(contexto.diasDeRacha(), DIAS_MAXIMOS);
        return acumulado.mas(ConceptoPuntaje.RACHA_DIARIA, dias * PUNTOS_POR_DIA);
    }
}
