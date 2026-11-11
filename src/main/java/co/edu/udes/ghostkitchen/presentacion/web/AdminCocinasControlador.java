package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.catalogo.AdministracionCocinas;
import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import co.edu.udes.ghostkitchen.dominio.modelo.CanalPedido;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import co.edu.udes.ghostkitchen.presentacion.web.formularios.FormularioCocina;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Gestion del catalogo para el administrador: registrar, editar, activar, desactivar y eliminar. */
@Controller
@RequestMapping("/admin/cocinas")
public class AdminCocinasControlador {

    private static final String VISTA_FORMULARIO = "admin/cocina-formulario";
    private static final String NOMBRE_EN_USO = "cocina.nombre-en-uso";
    private static final String VOLVER_A_LA_LISTA = "redirect:/admin/cocinas";

    private final AdministracionCocinas administracion;

    public AdminCocinasControlador(AdministracionCocinas administracion) {
        this.administracion = administracion;
    }

    @ModelAttribute
    public void opciones(Model modelo) {
        modelo.addAttribute("categorias", CategoriaCocina.values());
        modelo.addAttribute("rarezas", Rareza.values());
        modelo.addAttribute("canalesDisponibles", CanalPedido.values());
    }

    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("cocinas", administracion.listar());
        return "admin/cocinas";
    }

    @GetMapping("/nueva")
    public String nueva(Model modelo) {
        modelo.addAttribute("formulario", new FormularioCocina());
        return VISTA_FORMULARIO;
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("formulario") FormularioCocina formulario, BindingResult errores,
                            RedirectAttributes flash) {
        if (errores.hasErrors()) {
            return VISTA_FORMULARIO;
        }
        try {
            administracion.registrar(formulario.aDatos());
        } catch (ReglaNegocioException conflicto) {
            return nombreRepetido(conflicto, errores);
        }
        flash.addFlashAttribute(Avisos.EXITO_CLAVE, "admin.cocina.registrada");
        return VOLVER_A_LA_LISTA;
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("cocinaId", id);
        modelo.addAttribute("formulario", FormularioCocina.desde(administracion.datosDe(id)));
        return VISTA_FORMULARIO;
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("formulario") FormularioCocina formulario,
                             BindingResult errores, Model modelo, RedirectAttributes flash) {
        modelo.addAttribute("cocinaId", id);
        if (errores.hasErrors()) {
            return VISTA_FORMULARIO;
        }
        try {
            administracion.actualizar(id, formulario.aDatos());
        } catch (ReglaNegocioException conflicto) {
            return nombreRepetido(conflicto, errores);
        }
        flash.addFlashAttribute(Avisos.EXITO_CLAVE, "admin.cocina.actualizada");
        return VOLVER_A_LA_LISTA;
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam boolean activa, RedirectAttributes flash) {
        administracion.cambiarEstado(id, activa);
        flash.addFlashAttribute(Avisos.EXITO_CLAVE, activa ? "admin.cocina.activada" : "admin.cocina.desactivada");
        return VOLVER_A_LA_LISTA;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        administracion.eliminar(id);
        flash.addFlashAttribute(Avisos.EXITO_CLAVE, "admin.cocina.eliminada");
        return VOLVER_A_LA_LISTA;
    }

    private static String nombreRepetido(ReglaNegocioException conflicto, BindingResult errores) {
        if (!NOMBRE_EN_USO.equals(conflicto.getCodigo())) {
            throw conflicto;
        }
        errores.rejectValue("nombre", "error." + NOMBRE_EN_USO);
        return VISTA_FORMULARIO;
    }
}
