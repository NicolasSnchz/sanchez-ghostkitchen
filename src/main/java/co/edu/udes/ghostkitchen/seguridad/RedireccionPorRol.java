package co.edu.udes.ghostkitchen.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * Tras iniciar sesion, vuelve a la pagina que el usuario intentaba abrir. Si no
 * habia ninguna, el administrador va a su panel y el cazador al mapa.
 */
public class RedireccionPorRol extends SavedRequestAwareAuthenticationSuccessHandler {

    static final String DESTINO_ADMIN = "/admin/cocinas";
    static final String DESTINO_CAZADOR = "/explorar";

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(autoridad -> "ROLE_ADMIN".equals(autoridad.getAuthority()));
        return esAdmin ? DESTINO_ADMIN : DESTINO_CAZADOR;
    }
}
