package co.edu.udes.ghostkitchen.presentacion.api;

import co.edu.udes.ghostkitchen.dominio.excepcion.RecursoNoEncontradoException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Errores de la API en formato Problem Details (RFC 9457). Los textos salen de messages.properties. */
@RestControllerAdvice(basePackageClasses = ManejadorErroresApi.class)
public class ManejadorErroresApi {

    private final MessageSource mensajes;

    public ManejadorErroresApi(MessageSource mensajes) {
        this.mensajes = mensajes;
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail noEncontrado(RecursoNoEncontradoException error) {
        return problema(HttpStatus.NOT_FOUND, "api.no-encontrado");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail parametroInvalido(MethodArgumentTypeMismatchException error) {
        return problema(HttpStatus.BAD_REQUEST, "api.parametro-invalido", error.getName());
    }

    private ProblemDetail problema(HttpStatus estado, String clave, Object... argumentos) {
        var idioma = LocaleContextHolder.getLocale();
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado,
                mensajes.getMessage(clave + ".detalle", argumentos, idioma));
        problema.setTitle(mensajes.getMessage(clave + ".titulo", null, idioma));
        return problema;
    }
}
