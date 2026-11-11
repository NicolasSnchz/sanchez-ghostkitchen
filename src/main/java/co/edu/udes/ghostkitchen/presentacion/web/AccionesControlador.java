package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.acciones.HistorialAcciones;
import co.edu.udes.ghostkitchen.aplicacion.acciones.ResultadoDeshacer;
import co.edu.udes.ghostkitchen.seguridad.CuentaAutenticada;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Deshace la ultima accion del cazador. El invocador decide cual es y si todavia se puede. */
@Controller
public class AccionesControlador {

    private final HistorialAcciones historial;
    private final TraductorNovedades traductor;
    private final MessageSource mensajes;

    public AccionesControlador(HistorialAcciones historial, TraductorNovedades traductor, MessageSource mensajes) {
        this.historial = historial;
        this.traductor = traductor;
        this.mensajes = mensajes;
    }

    @PostMapping("/acciones/deshacer")
    public String deshacer(@AuthenticationPrincipal CuentaAutenticada cuenta, HttpServletRequest solicitud,
                           RedirectAttributes flash) {
        ResultadoDeshacer deshecha = historial.deshacerUltima(cuenta.id());
        flash.addFlashAttribute(Avisos.EXITO, mensajes.getMessage("deshacer." + deshecha.tipo(),
                new Object[] {deshecha.detalle(), deshecha.puntosRetirados()}, LocaleContextHolder.getLocale()));
        flash.addFlashAttribute(Avisos.NOVEDADES, traductor.traducir(deshecha.novedades()));
        return "redirect:" + RedireccionSegura.haciaOrigen(solicitud, "/perfil");
    }
}
