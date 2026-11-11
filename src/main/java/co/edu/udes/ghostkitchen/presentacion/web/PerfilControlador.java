package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.panel.PanelCazadorFacade;
import co.edu.udes.ghostkitchen.seguridad.CuentaAutenticada;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Panel del cazador. Depende solo de la fachada, no de los cinco servicios que hay detras. */
@Controller
public class PerfilControlador {

    private final PanelCazadorFacade panel;

    public PerfilControlador(PanelCazadorFacade panel) {
        this.panel = panel;
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal CuentaAutenticada cuenta, Model modelo) {
        modelo.addAttribute("panel", panel.panelDe(cuenta.id()));
        return "perfil";
    }
}
