package co.edu.udes.ghostkitchen.presentacion.web;

/**
 * Nombres de los atributos flash que sobreviven a una redireccion. Se usan con
 * el patron Post/Redirect/Get para que recargar la pagina no repita la accion.
 */
final class Avisos {

    static final String EXITO = "avisoExito";
    /** Igual que EXITO, pero guarda la clave del mensaje y la plantilla la traduce. */
    static final String EXITO_CLAVE = "avisoExitoClave";
    static final String ERROR = "avisoError";
    static final String NOVEDADES = "novedades";
    static final String COMANDA = "comanda";

    private Avisos() {
    }
}
