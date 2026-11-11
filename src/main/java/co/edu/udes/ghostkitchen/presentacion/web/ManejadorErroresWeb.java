package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.dominio.excepcion.RecursoNoEncontradoException;
import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Traduce las excepciones de las capas internas a respuestas de la interfaz web.
 * Una regla de negocio incumplida no es un fallo del sistema: se vuelve a la
 * pagina anterior con un mensaje claro de que paso y que hacer.
 */
@ControllerAdvice(basePackageClasses = ManejadorErroresWeb.class)
public class ManejadorErroresWeb {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorErroresWeb.class);

    private final MessageSource mensajes;

    public ManejadorErroresWeb(MessageSource mensajes) {
        this.mensajes = mensajes;
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public String reglaIncumplida(ReglaNegocioException error, HttpServletRequest solicitud,
                                  RedirectAttributes flash) {
        flash.addFlashAttribute(Avisos.ERROR, mensajes.getMessage("error." + error.getCodigo(),
                error.getArgumentos(), LocaleContextHolder.getLocale()));
        return "redirect:" + RedireccionSegura.haciaOrigen(solicitud, "/");
    }

    @ExceptionHandler({RecursoNoEncontradoException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noEncontrado() {
        return "error/404";
    }

    /** Datos que pasaron la validacion del formulario pero no la del dominio. */
    @ExceptionHandler(IllegalArgumentException.class)
    public String datosInvalidos(IllegalArgumentException error, HttpServletRequest solicitud,
                                 RedirectAttributes flash) {
        LOG.warn("Datos rechazados por el dominio en {}: {}", solicitud.getRequestURI(), error.getMessage());
        flash.addFlashAttribute(Avisos.ERROR, mensajes.getMessage("error.datos-invalidos", null,
                LocaleContextHolder.getLocale()));
        return "redirect:" + RedireccionSegura.haciaOrigen(solicitud, "/");
    }
}
