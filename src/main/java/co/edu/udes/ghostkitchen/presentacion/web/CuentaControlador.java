package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.cuentas.ServicioCuentas;
import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import co.edu.udes.ghostkitchen.presentacion.web.formularios.FormularioRegistro;
import co.edu.udes.ghostkitchen.seguridad.CuentaAutenticada;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Ingreso y registro. El ingreso lo procesa Spring Security; este controlador
 * solo muestra el formulario y crea cuentas nuevas de cazador.
 */
@Controller
public class CuentaControlador {

    /** Errores de negocio del registro que corresponden a un campo concreto del formulario. */
    private static final Map<String, String> CAMPO_POR_ERROR = Map.of(
            "cuenta.correo-en-uso", "correo",
            "cuenta.alias-en-uso", "alias");

    private final ServicioCuentas cuentas;

    public CuentaControlador(ServicioCuentas cuentas) {
        this.cuentas = cuentas;
    }

    @GetMapping("/ingresar")
    public String ingresar(@AuthenticationPrincipal CuentaAutenticada cuenta) {
        if (cuenta != null) {
            return cuenta.esCazador() ? "redirect:/explorar" : "redirect:/admin/cocinas";
        }
        return "cuenta/ingresar";
    }

    @GetMapping("/registro")
    public String registro(@AuthenticationPrincipal CuentaAutenticada cuenta, Model modelo) {
        if (cuenta != null) {
            return "redirect:/";
        }
        modelo.addAttribute("formulario", new FormularioRegistro());
        return "cuenta/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("formulario") FormularioRegistro formulario,
                            BindingResult errores, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            return "cuenta/registro";
        }
        try {
            cuentas.registrarCazador(formulario.aCuenta());
        } catch (ReglaNegocioException conflicto) {
            String campo = CAMPO_POR_ERROR.get(conflicto.getCodigo());
            if (campo == null) {
                throw conflicto;
            }
            errores.rejectValue(campo, "error." + conflicto.getCodigo());
            return "cuenta/registro";
        }
        flash.addFlashAttribute("correoRegistrado", formulario.getCorreo().strip());
        return "redirect:/ingresar?registrado";
    }
}
