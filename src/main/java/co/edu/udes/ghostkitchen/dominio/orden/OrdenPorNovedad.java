package co.edu.udes.ghostkitchen.dominio.orden;

import java.util.Comparator;

/** Primero las cocinas que entraron mas recientemente al catalogo. */
public final class OrdenPorNovedad implements CriterioOrden {

    @Override
    public TipoOrden tipo() {
        return TipoOrden.NOVEDAD;
    }

    @Override
    public Comparator<FichaCocina> comparador() {
        return Comparator.comparing(FichaCocina::fechaAlta).reversed();
    }
}
