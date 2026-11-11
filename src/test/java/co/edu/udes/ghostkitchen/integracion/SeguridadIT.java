package co.edu.udes.ghostkitchen.integracion;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.udes.ghostkitchen.dominio.modelo.Administrador;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;

class SeguridadIT extends PruebaIntegracion {

    @Test
    @DisplayName("PI-005 Cada rol solo entra a lo suyo y todo POST exige token CSRF")
    void accesos_porRolYProteccionCsrf() throws Exception {
        Cazador cazador = escenario.cazador("humo_azul");
        Administrador admin = escenario.administrador();
        CocinaFantasma cocina = escenario.cocinaAbierta("Smash Fantasma");

        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/perfil")).andExpect(redirectedUrlPattern("**/ingresar"));
        mvc.perform(post("/cocinas/{id}/cazar", cocina.getId()).with(csrf()))
                .andExpect(redirectedUrlPattern("**/ingresar"));
        mvc.perform(get("/admin/cocinas").with(como(cazador))).andExpect(status().isForbidden());
        mvc.perform(get("/perfil").with(como(admin))).andExpect(status().isForbidden());
        mvc.perform(get("/admin/cocinas").with(como(admin))).andExpect(status().isOk());
        mvc.perform(post("/cocinas/{id}/cazar", cocina.getId()).with(como(cazador)))
                .andExpect(status().isForbidden());
    }

    @Test
    void cabeceras_incluyenPoliticaDeContenido() throws Exception {
        mvc.perform(get("/"))
                .andExpect(header().string("Content-Security-Policy", Matchers.containsString("script-src 'self'")))
                .andExpect(header().string("X-Frame-Options", "DENY"));
    }

    @Test
    void ingreso_rediridePorRolYRechazaClaveIncorrecta() throws Exception {
        escenario.cazador("noctambula");
        escenario.administrador();

        mvc.perform(formLogin("/ingresar").userParameter("correo").passwordParam("clave")
                        .user("noctambula@correo.co").password(Escenario.CLAVE))
                .andExpect(redirectedUrl("/explorar"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated().withRoles("CAZADOR"));
        mvc.perform(formLogin("/ingresar").userParameter("correo").passwordParam("clave")
                        .user("jefe@correo.co").password(Escenario.CLAVE))
                .andExpect(redirectedUrl("/admin/cocinas"));
        mvc.perform(formLogin("/ingresar").userParameter("correo").passwordParam("clave")
                        .user("noctambula@correo.co").password("otra-clave"))
                .andExpect(redirectedUrl("/ingresar?error"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }
}
