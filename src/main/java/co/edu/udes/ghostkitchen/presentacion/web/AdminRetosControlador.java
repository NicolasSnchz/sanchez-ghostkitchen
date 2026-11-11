package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.retos.ServicioRetos;
import co.edu.udes.ghostkitchen.presentacion.web.formularios.FormularioReto;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Gestion de retos para el administrador: ver todos, crear y eliminar los que no tienen inscritos. */
@Controller
@RequestMapping("/admin/retos")
public class AdminRetosControlador {

    private static final String VISTA = "admin/retos";

    private final ServicioRetos retos;
    private final Clock reloj;

    public AdminRetosControlador(ServicioRetos retos, Clock reloj) {
        this.retos = retos;
        this.reloj = reloj;
    }

    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("formulario", FormularioReto.paraLaSemanaDe(LocalDate.now(reloj)));
        modelo.addAttribute("retos", retos.todos());
        return VISTA;
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("formulario") FormularioReto formulario, BindingResult errores,
                        Model modelo, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            modelo.addAttribute("retos", retos.todos());
            return VISTA;
        }
        retos.crear(formulario.aDatos());
        flash.addFlashAttribute(Avisos.EXITO_CLAVE, "admin.reto.creado");
        return "redirect:/admin/retos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        retos.eliminar(id);
        flash.addFlashAttribute(Avisos.EXITO_CLAVE, "admin.reto.eliminado");
        return "redirect:/admin/retos";
    }
}
