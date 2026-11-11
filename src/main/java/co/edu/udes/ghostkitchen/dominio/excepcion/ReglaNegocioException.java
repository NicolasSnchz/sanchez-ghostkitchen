package co.edu.udes.ghostkitchen.dominio.excepcion;

/**
 * Se lanza cuando una operacion valida tecnicamente incumple una regla del negocio.
 * Lleva un codigo estable (no un texto) para que la capa de presentacion decida
 * como mostrarlo al usuario; asi el dominio no conoce idiomas ni formatos.
 */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String codigo;
    private final transient Object[] argumentos;

    public ReglaNegocioException(String codigo, Object... argumentos) {
        super(codigo);
        this.codigo = codigo;
        this.argumentos = argumentos.clone();
    }

    public String getCodigo() {
        return codigo;
    }

    public Object[] getArgumentos() {
        return argumentos.clone();
    }
}
