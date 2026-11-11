package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.acciones.FabricaComandos;
import co.edu.udes.ghostkitchen.aplicacion.acciones.HistorialAcciones;
import co.edu.udes.ghostkitchen.aplicacion.acciones.ResultadoAccion;
import co.edu.udes.ghostkitchen.aplicacion.retos.ServicioRetos;
import co.edu.udes.ghostkitchen.seguridad.CuentaAutenticada;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Retos vigentes y proximos. Unirse a un reto tambien pasa por el invocador de comandos. */
@Controller
public class RetosControlador {

    private final ServicioRetos retos;
    private final FabricaComandos fabrica;
    private final HistorialAcciones historial;
    private final TraductorNovedades traductor;
    private final MessageSource mensajes;

    public RetosControlador(ServicioRetos retos, FabricaComandos fabrica, HistorialAcciones historial,
                            TraductorNovedades traductor, MessageSource mensajes) {
        this.retos = retos;
        this.fabrica = fabrica;
        this.historial = historial;
        this.traductor = traductor;
        this.mensajes = mensajes;
    }

    @GetMapping("/retos")
    public String retos(@AuthenticationPrincipal CuentaAutenticada cuenta, Model modelo) {
        modelo.addAttribute("vigentes", retos.vigentes(Sesion.cazadorId(cuenta)));
        modelo.addAttribute("proximos", retos.proximos());
        return "retos";
    }

    @PostMapping("/retos/{id}/unirse")
    public String unirse(@PathVariable Long id, @AuthenticationPrincipal CuentaAutenticada cuenta,
                         RedirectAttributes flash) {
        ResultadoAccion resultado = historial.ejecutar(fabrica.unirseAReto(cuenta.id(), id));
        flash.addFlashAttribute(Avisos.EXITO, mensajes.getMessage("reto.unido",
                new Object[] {resultado.resultado().detalle()}, LocaleContextHolder.getLocale()));
        flash.addFlashAttribute(Avisos.NOVEDADES, traductor.traducir(resultado.novedades()));
        return "redirect:/retos#reto-" + id;
    }
}
