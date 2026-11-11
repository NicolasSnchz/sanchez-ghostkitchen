package co.edu.udes.ghostkitchen.dominio.orden;

import java.util.Comparator;

/** Primero las mejor calificadas; a igual promedio, la que tenga mas resenas que lo respalden. */
public final class OrdenPorCalificacion implements CriterioOrden {

    @Override
    public TipoOrden tipo() {
        return TipoOrden.CALIFICACION;
    }

    @Override
    public Comparator<FichaCocina> comparador() {
        return Comparator.comparingDouble(FichaCocina::calificacionPromedio).reversed()
                .thenComparing(Comparator.comparingLong(FichaCocina::totalResenas).reversed());
    }
}
