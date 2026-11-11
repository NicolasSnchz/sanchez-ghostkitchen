package co.edu.udes.ghostkitchen.dominio.puntos;

/** Componente concreto del Decorator: los puntos de partida los define la rareza de la cocina. */
public final class PuntosBasePorRareza implements CalculadoraPuntos {

    @Override
    public Puntaje calcular(ContextoCaza contexto) {
        return Puntaje.base(contexto.rareza().puntosBase());
    }
}
