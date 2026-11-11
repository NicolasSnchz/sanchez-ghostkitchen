package co.edu.udes.ghostkitchen.presentacion.web;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * Calcula a donde volver despues de una accion. Usa la cabecera Referer solo si
 * apunta a este mismo sitio y siempre conserva unicamente la ruta, asi nunca se
 * convierte en una redireccion abierta hacia otro dominio.
 */
final class RedireccionSegura {

    private RedireccionSegura() {
    }

    static String haciaOrigen(HttpServletRequest solicitud, String rutaPorDefecto) {
        URI origen = leer(solicitud.getHeader("Referer"));
        if (origen == null || !esDelMismoSitio(origen, solicitud)) {
            return rutaPorDefecto;
        }
        String ruta = sinContexto(origen.getRawPath(), solicitud.getContextPath());
        if (!esRutaLocal(ruta)) {
            return rutaPorDefecto;
        }
        return origen.getRawQuery() == null ? ruta : ruta + "?" + origen.getRawQuery();
    }

    private static URI leer(String referer) {
        if (referer == null || referer.isBlank()) {
            return null;
        }
        try {
            return URI.create(referer);
        } catch (IllegalArgumentException refererMalFormado) {
            return null;
        }
    }

    private static boolean esDelMismoSitio(URI origen, HttpServletRequest solicitud) {
        return origen.getHost() != null && origen.getHost().equalsIgnoreCase(solicitud.getServerName());
    }

    /** Una ruta que empieza con dos barras el navegador la entiende como otro dominio. */
    private static boolean esRutaLocal(String ruta) {
        return ruta != null && ruta.startsWith("/") && !ruta.startsWith("//");
    }

    private static String sinContexto(String ruta, String contexto) {
        if (ruta == null || contexto == null || contexto.isEmpty() || !ruta.startsWith(contexto)) {
            return ruta;
        }
        return ruta.substring(contexto.length());
    }
}
