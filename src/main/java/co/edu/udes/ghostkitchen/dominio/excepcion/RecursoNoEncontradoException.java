package co.edu.udes.ghostkitchen.dominio.excepcion;

/**
 * Se lanza cuando se pide un recurso (cocina, reto, cazador) que no existe.
 * La capa de presentacion la traduce a una respuesta 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String recurso;
    private final transient Object identificador;

    public RecursoNoEncontradoException(String recurso, Object identificador) {
        super(recurso + " no encontrado: " + identificador);
        this.recurso = recurso;
        this.identificador = identificador;
    }

    public String getRecurso() {
        return recurso;
    }

    public Object getIdentificador() {
        return identificador;
    }
}
