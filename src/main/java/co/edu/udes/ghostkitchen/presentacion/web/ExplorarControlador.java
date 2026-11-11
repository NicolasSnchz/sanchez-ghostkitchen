package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.catalogo.FiltroCocinas;
import co.edu.udes.ghostkitchen.aplicacion.catalogo.ServicioCatalogo;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.orden.TipoOrden;
import co.edu.udes.ghostkitchen.presentacion.comun.ParametrosBusqueda;
import co.edu.udes.ghostkitchen.seguridad.CuentaAutenticada;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Explorador con mapa: filtros por texto, categoria y horario, y orden elegido por el usuario (Strategy). */
@Controller
public class ExplorarControlador {

    private final ServicioCatalogo catalogo;

    public ExplorarControlador(ServicioCatalogo catalogo) {
        this.catalogo = catalogo;
    }

    /**
     * El BindingResult se recibe para que un parametro mal escrito en la URL (una
     * latitud con letras, por ejemplo) se ignore en vez de responder con error.
     */
    @GetMapping("/explorar")
    public String explorar(@ModelAttribute("busqueda") ParametrosBusqueda busqueda, BindingResult valoresIgnorados,
                           @AuthenticationPrincipal CuentaAutenticada cuenta, Model modelo) {
        FiltroCocinas filtro = busqueda.aFiltro();
        modelo.addAttribute("fichas", catalogo.explorar(filtro, Sesion.cazadorId(cuenta)));
        modelo.addAttribute("filtro", filtro);
        modelo.addAttribute("conUbicacion", busqueda.tieneUbicacion());
        modelo.addAttribute("categorias", CategoriaCocina.values());
        modelo.addAttribute("ordenes", TipoOrden.values());
        return "explorar";
    }
}
