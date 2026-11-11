package co.edu.udes.ghostkitchen.dominio.orden;

import java.util.Comparator;

/** Primero las cocinas que mas cazadores han descubierto. */
public final class OrdenPorPopularidad implements CriterioOrden {

    @Override
    public TipoOrden tipo() {
        return TipoOrden.POPULARIDAD;
    }

    @Override
    public Comparator<FichaCocina> comparador() {
        return Comparator.comparingLong(FichaCocina::vecesCazada).reversed();
    }
}
