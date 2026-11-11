package co.edu.udes.ghostkitchen.dominio.puntos;

/** Premia a quien caza una cocina que nadie habia encontrado todavia. */
public final class BonoPrimerCazador extends BonificacionPuntos {

    public static final int PUNTOS = 50;

    public BonoPrimerCazador(CalculadoraPuntos envuelta) {
        super(envuelta);
    }

    @Override
    protected boolean aplica(ContextoCaza contexto) {
        return contexto.primerCazador();
    }

    @Override
    protected Puntaje bonificar(Puntaje acumulado, ContextoCaza contexto) {
        return acumulado.mas(ConceptoPuntaje.PRIMER_CAZADOR, PUNTOS);
    }
}
