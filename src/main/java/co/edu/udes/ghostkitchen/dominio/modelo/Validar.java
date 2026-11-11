package co.edu.udes.ghostkitchen.dominio.modelo;

/**
 * Comprobaciones de invariantes compartidas por las entidades del dominio.
 * Centralizarlas evita repetir el mismo if en cada constructor.
 */
final class Validar {

    private Validar() {
    }

    static String texto(String valor, String campo, int longitudMaxima) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio");
        }
        String limpio = valor.strip();
        if (limpio.length() > longitudMaxima) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " admite maximo " + longitudMaxima + " caracteres");
        }
        return limpio;
    }

    static <T> T presente(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio");
        }
        return valor;
    }

    static int enRango(int valor, int minimo, int maximo, String campo) {
        if (valor < minimo || valor > maximo) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " debe estar entre " + minimo + " y " + maximo);
        }
        return valor;
    }
}
