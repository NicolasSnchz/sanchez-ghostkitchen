package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.acciones.FabricaComandos;
import co.edu.udes.ghostkitchen.aplicacion.acciones.HistorialAcciones;
import co.edu.udes.ghostkitchen.aplicacion.acciones.ResultadoAccion;
import co.edu.udes.ghostkitchen.aplicacion.catalogo.ServicioCatalogo;
import co.edu.udes.ghostkitchen.dominio.modelo.Resena;
import co.edu.udes.ghostkitchen.presentacion.web.formularios.FormularioResena;
import co.edu.udes.ghostkitchen.seguridad.CuentaAutenticada;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Ficha de una cocina y las dos acciones que se hacen desde ella. Este
 * controlador es el cliente del patron Command: pide el comando a la fabrica y se
 * lo entrega al invocador, sin saber como se caza ni como se resena.
 */
@Controller
public class CocinaControlador {

    private final ServicioCatalogo catalogo;
    private final FabricaComandos fabrica;
    private final HistorialAcciones historial;
    private final TraductorNovedades traductor;
    private final MessageSource mensajes;
    private final Clock reloj;

    public CocinaControlador(ServicioCatalogo catalogo, FabricaComandos fabrica, HistorialAcciones historial,
                             TraductorNovedades traductor, MessageSource mensajes, Clock reloj) {
        this.catalogo = catalogo;
        this.fabrica = fabrica;
        this.historial = historial;
        this.traductor = traductor;
        this.mensajes = mensajes;
        this.reloj = reloj;
    }

    @GetMapping("/cocinas/{id}")
    public String detalle(@PathVariable Long id, @AuthenticationPrincipal CuentaAutenticada cuenta, Model modelo) {
        modelo.addAttribute("detalle", catalogo.detalle(id, Sesion.cazadorId(cuenta)));
        if (!modelo.containsAttribute("formularioResena")) {
            modelo.addAttribute("formularioResena", new FormularioResena());
        }
        return "cocina";
    }

    @PostMapping("/cocinas/{id}/cazar")
    public String cazar(@PathVariable Long id, @AuthenticationPrincipal CuentaAutenticada cuenta,
                        RedirectAttributes flash) {
        ResultadoAccion resultado = historial.ejecutar(fabrica.descubrirCocina(cuenta.id(), id));
        flash.addFlashAttribute(Avisos.COMANDA, Comanda.de(resultado.resultado(), LocalDateTime.now(reloj)));
        flash.addFlashAttribute(Avisos.NOVEDADES, traductor.traducir(resultado.novedades()));
        return "redirect:/cocinas/" + id;
    }

    @PostMapping("/cocinas/{id}/resenas")
    public String resenar(@PathVariable Long id,
                          @Valid @ModelAttribute("formularioResena") FormularioResena formulario,
                          BindingResult errores, @AuthenticationPrincipal CuentaAutenticada cuenta,
                          Model modelo, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            modelo.addAttribute("detalle", catalogo.detalle(id, cuenta.id()));
            return "cocina";
        }
        ResultadoAccion resultado = historial.ejecutar(fabrica.publicarResena(cuenta.id(), id, formulario.aResena()));
        flash.addFlashAttribute(Avisos.EXITO, mensajes.getMessage("resena.publicada",
                new Object[] {Resena.PUNTOS_POR_RESENA}, LocaleContextHolder.getLocale()));
        flash.addFlashAttribute(Avisos.NOVEDADES, traductor.traducir(resultado.novedades()));
        return "redirect:/cocinas/" + id + "#resenas";
    }
}
