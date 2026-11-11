package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import co.edu.udes.ghostkitchen.dominio.orden.CriterioOrden;
import co.edu.udes.ghostkitchen.dominio.orden.TipoOrden;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Contexto del patron Strategy: recibe todas las estrategias registradas y entrega
 * la que corresponde al orden pedido. Agregar un criterio nuevo es registrar un
 * bean mas; esta clase no cambia.
 */
@Component
public class SelectorCriterioOrden {

    private final Map<TipoOrden, CriterioOrden> criterios = new EnumMap<>(TipoOrden.class);

    public SelectorCriterioOrden(List<CriterioOrden> disponibles) {
        for (CriterioOrden criterio : disponibles) {
            if (criterios.putIfAbsent(criterio.tipo(), criterio) != null) {
                throw new IllegalStateException("Hay dos criterios registrados para " + criterio.tipo());
            }
        }
    }

    public CriterioOrden para(TipoOrden tipo) {
        CriterioOrden criterio = criterios.get(tipo);
        if (criterio == null) {
            throw new IllegalArgumentException("No hay un criterio de orden para " + tipo);
        }
        return criterio;
    }
}
