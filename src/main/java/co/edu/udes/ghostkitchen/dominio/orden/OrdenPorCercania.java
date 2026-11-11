package co.edu.udes.ghostkitchen.dominio.orden;

import java.util.Comparator;

/** Primero las cocinas mas cercanas al punto de referencia del visitante. */
public final class OrdenPorCercania implements CriterioOrden {

    @Override
    public TipoOrden tipo() {
        return TipoOrden.CERCANIA;
    }

    @Override
    public Comparator<FichaCocina> comparador() {
        return Comparator.comparingDouble(FichaCocina::distanciaKm);
    }
}
