package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.catalogo.ServicioPortada;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Pagina de inicio: el riel de comandas con las cazas recientes y las cocinas destacadas. */
@Controller
public class PortadaControlador {

    private final ServicioPortada portada;

    public PortadaControlador(ServicioPortada portada) {
        this.portada = portada;
    }

    @GetMapping("/")
    public String inicio(Model modelo) {
        modelo.addAttribute("resumen", portada.resumen());
        return "portada";
    }
}
