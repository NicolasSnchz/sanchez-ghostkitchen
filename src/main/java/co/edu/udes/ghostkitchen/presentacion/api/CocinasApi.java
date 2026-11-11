package co.edu.udes.ghostkitchen.presentacion.api;

import co.edu.udes.ghostkitchen.aplicacion.catalogo.ServicioCatalogo;
import co.edu.udes.ghostkitchen.presentacion.comun.ParametrosBusqueda;
import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de solo lectura del catalogo. Recibe los mismos filtros que el
 * explorador web y reutiliza el mismo servicio, asi las dos interfaces nunca
 * muestran resultados distintos.
 */
@RestController
@RequestMapping("/api/v1/cocinas")
public class CocinasApi {

    private final ServicioCatalogo catalogo;

    public CocinasApi(ServicioCatalogo catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public List<CocinaResumenDto> listar(@ModelAttribute ParametrosBusqueda parametros,
                                         BindingResult valoresIgnorados) {
        return catalogo.explorar(parametros.aFiltro(), null).stream().map(CocinaResumenDto::desde).toList();
    }

    @GetMapping("/{id}")
    public CocinaDetalleDto detalle(@PathVariable Long id) {
        return CocinaDetalleDto.desde(catalogo.detalle(id, null));
    }
}
