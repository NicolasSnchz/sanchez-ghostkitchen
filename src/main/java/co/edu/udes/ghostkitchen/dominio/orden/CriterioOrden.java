package co.edu.udes.ghostkitchen.dominio.orden;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Estrategia del patron Strategy: cada implementacion es un algoritmo distinto e
 * intercambiable para ordenar el mismo listado. El visitante elige cual usar en
 * cada consulta.
 */
public interface CriterioOrden {

    /** Identifica el criterio para poder elegirlo desde la peticion. */
    TipoOrden tipo();

    /** Regla de comparacion propia del criterio. */
    Comparator<FichaCocina> comparador();

    /**
     * Ordena las fichas con la regla del criterio. Los empates se resuelven por
     * nombre para que el resultado sea siempre el mismo.
     */
    default List<FichaCocina> ordenar(Collection<FichaCocina> fichas) {
        return fichas.stream()
                .sorted(comparador().thenComparing(FichaCocina::nombre))
                .toList();
    }
}
