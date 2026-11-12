package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.acciones.AccionDeshacible;
import co.edu.udes.ghostkitchen.aplicacion.acciones.HistorialAcciones;
import co.edu.udes.ghostkitchen.seguridad.CuentaAutenticada;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Datos que necesitan todas las paginas: la ruta para marcar el menu, la cuenta y la accion deshacible. */
@ControllerAdvice(basePackageClasses = AtributosComunes.class)
public class AtributosComunes {

    private final HistorialAcciones historial;
    private final Clock reloj;

    public AtributosComunes(HistorialAcciones historial, Clock reloj) {
        this.historial = historial;
        this.reloj = reloj;
    }

    @ModelAttribute("rutaActual")
    public String rutaActual(HttpServletRequest solicitud) {
        return solicitud.getRequestURI().substring(solicitud.getContextPath().length());
    }

    @ModelAttribute("cuenta")
    public CuentaAutenticada cuenta(@AuthenticationPrincipal CuentaAutenticada cuenta) {
        return cuenta;
    }

    /**
     * La accion que el cazador todavia puede deshacer y los segundos que le quedan.
     * Solo se consulta en paginas que se muestran (GET): un POST siempre redirige.
     */
    @ModelAttribute
    public void deshacible(@AuthenticationPrincipal CuentaAutenticada cuenta, HttpServletRequest solicitud,
                           Model modelo) {
        Long cazadorId = Sesion.cazadorId(cuenta);
        if (cazadorId == null || !"GET".equals(solicitud.getMethod())) {
            return;
        }
        historial.accionDeshacible(cazadorId).ifPresent(accion -> {
            modelo.addAttribute("deshacible", accion);
            modelo.addAttribute("segundosParaDeshacer", segundosHasta(accion));
        });
    }

    private long segundosHasta(AccionDeshacible accion) {
        Instant limite = accion.disponibleHasta().atZone(reloj.getZone()).toInstant();
        return Math.max(0, Duration.between(reloj.instant(), limite).toSeconds());
    }
}
