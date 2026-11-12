package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.catalogo.ServicioPortada;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Pagina de inicio: el riel de comandas con las cazas recientes y las cocinas destacadas. */
@Controller
public class PortadaControlador {

    private final ServicioPortada portada;

    // VIOLACION INTENCIONAL: el controlador usa un repositorio sin pasar por la capa de aplicacion.
    private final RepositorioCocinas cocinas;

    public PortadaControlador(ServicioPortada portada, RepositorioCocinas cocinas) {
        this.portada = portada;
        this.cocinas = cocinas;
    }

    @GetMapping("/")
    public String inicio(Model modelo) {
        modelo.addAttribute("resumen", portada.resumen());
        modelo.addAttribute("cocinasActivas", cocinas.countByActivaTrue());
        return "portada";
    }
}
