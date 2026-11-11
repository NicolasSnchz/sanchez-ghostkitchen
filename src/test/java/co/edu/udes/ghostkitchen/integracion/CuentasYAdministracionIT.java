package co.edu.udes.ghostkitchen.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import co.edu.udes.ghostkitchen.dominio.modelo.Administrador;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Descubrimiento;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioRetos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioUsuarios;
import co.edu.udes.ghostkitchen.soporte.Ejemplos;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CuentasYAdministracionIT extends PruebaIntegracion {

    @Autowired
    private RepositorioUsuarios usuarios;

    @Autowired
    private RepositorioCocinas cocinas;

    @Autowired
    private RepositorioRetos retos;

    @Autowired
    private RepositorioDescubrimientos descubrimientos;

    @Test
    @DisplayName("PI-007 El registro valida el formulario, evita correos repetidos y guarda la clave cifrada")
    void registro_validaYGuardaConClaveCifrada() throws Exception {
        mvc.perform(post("/registro").with(csrf())
                        .param("nombre", "Ana Rojas").param("alias", "ana_23").param("correo", "ana@correo.co")
                        .param("clave", "Cazadora1").param("confirmacion", "Cazadora1"))
                .andExpect(redirectedUrl("/ingresar?registrado"))
                .andExpect(flash().attribute("correoRegistrado", "ana@correo.co"));
        assertThat(usuarios.findByCorreo("ana@correo.co"))
                .hasValueSatisfying(usuario -> assertThat(usuario.getClaveCifrada()).startsWith("$2"));

        mvc.perform(post("/registro").with(csrf())
                        .param("nombre", "Otra Ana").param("alias", "ana_24").param("correo", "ANA@correo.co")
                        .param("clave", "Cazadora1").param("confirmacion", "Cazadora1"))
                .andExpect(status().isOk())
                .andExpect(view().name("cuenta/registro"))
                .andExpect(model().attributeHasFieldErrorCode("formulario", "correo", "error.cuenta.correo-en-uso"));

        mvc.perform(post("/registro").with(csrf())
                        .param("nombre", "").param("alias", "a b").param("correo", "sin-arroba")
                        .param("clave", "corta").param("confirmacion", "distinta"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("formulario", "nombre", "alias", "correo", "clave",
                        "clavesCoinciden"));
    }

    @Test
    @DisplayName("PI-008 El administrador gestiona el catalogo sin poder borrar cocinas que ya tienen cazas")
    void administracion_registraValidaYProtegeCocinasConCazas() throws Exception {
        Administrador admin = escenario.administrador();

        mvc.perform(post("/admin/cocinas").with(como(admin)).with(csrf())
                        .param("nombre", "Chivo de Medianoche").param("descripcion", "Chivo guisado de madrugada.")
                        .param("categoria", "TIPICA").param("rareza", "LEGENDARIA")
                        .param("horaApertura", "19:00").param("horaCierre", "04:00")
                        .param("latitud", "7.8790").param("longitud", "-72.5180").param("barrio", "Motilones")
                        .param("canales", "WHATSAPP"))
                .andExpect(redirectedUrl("/admin/cocinas"))
                .andExpect(flash().attribute("avisoExitoClave", "admin.cocina.registrada"));
        assertThat(cocinas.existsByNombreIgnoreCase("chivo de medianoche")).isTrue();

        mvc.perform(post("/admin/cocinas").with(como(admin)).with(csrf())
                        .param("nombre", "Chivo de Medianoche").param("descripcion", "Repetida.")
                        .param("categoria", "TIPICA").param("rareza", "COMUN")
                        .param("horaApertura", "10:00").param("horaCierre", "10:00")
                        .param("latitud", "4.6").param("longitud", "-74.1").param("barrio", "Chapinero"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("formulario", "horarioValido", "latitud", "longitud",
                        "canales"));

        Cazador cazador = escenario.cazador("mango_biche");
        CocinaFantasma cazada = escenario.cocinaAbierta("Dulce Penumbra");
        descubrimientos.save(Descubrimiento.registrar(cazador, cazada, Ejemplos.AHORA.minusDays(1), 150));
        mvc.perform(post("/admin/cocinas/{id}/eliminar", cazada.getId()).with(como(admin)).with(csrf())
                        .header("Referer", "http://localhost/admin/cocinas"))
                .andExpect(redirectedUrl("/admin/cocinas"))
                .andExpect(flash().attribute("avisoError", Matchers.containsString("Desact")));
        mvc.perform(post("/admin/cocinas/{id}/estado", cazada.getId()).with(como(admin)).with(csrf())
                        .param("activa", "false"))
                .andExpect(redirectedUrl("/admin/cocinas"));
        assertThat(cocinas.findById(cazada.getId())).hasValueSatisfying(c -> assertThat(c.isActiva()).isFalse());
    }

    @Test
    void administracion_creaYEliminaRetosSinInscritos() throws Exception {
        Administrador admin = escenario.administrador();
        String hoy = Ejemplos.AHORA.toLocalDate().toString();

        mvc.perform(post("/admin/retos").with(como(admin)).with(csrf())
                        .param("titulo", "Maraton nocturna").param("descripcion", "Caza tres cocinas de noche.")
                        .param("metaCocinas", "3").param("puntosRecompensa", "180")
                        .param("fechaInicio", hoy).param("fechaFin", hoy))
                .andExpect(redirectedUrl("/admin/retos"));
        Long id = retos.findAll().stream().filter(reto -> reto.getTitulo().equals("Maraton nocturna"))
                .findFirst().orElseThrow().getId();

        mvc.perform(post("/admin/retos").with(como(admin)).with(csrf())
                        .param("titulo", "").param("metaCocinas", "0").param("puntosRecompensa", "9000")
                        .param("fechaInicio", hoy).param("fechaFin", "2026-01-01"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("formulario", "titulo", "descripcion", "metaCocinas",
                        "puntosRecompensa", "fechasEnOrden"));

        mvc.perform(post("/admin/retos/{id}/eliminar", id).with(como(admin)).with(csrf()))
                .andExpect(redirectedUrl("/admin/retos"));
        assertThat(retos.findById(id)).isEmpty();
    }
}
