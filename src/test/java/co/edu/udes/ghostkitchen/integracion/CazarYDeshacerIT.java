package co.edu.udes.ghostkitchen.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import co.edu.udes.ghostkitchen.dominio.modelo.AccionRegistrada;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Descubrimiento;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoInsignia;
import co.edu.udes.ghostkitchen.persistencia.RepositorioAcciones;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioInsignias;
import co.edu.udes.ghostkitchen.presentacion.web.Comanda;
import co.edu.udes.ghostkitchen.soporte.Ejemplos;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Flujo completo del patron Command: controlador, invocador, receptor, historial y observadores. */
class CazarYDeshacerIT extends PruebaIntegracion {

    @Autowired
    private RepositorioDescubrimientos descubrimientos;

    @Autowired
    private RepositorioAcciones acciones;

    @Autowired
    private RepositorioInsignias insignias;

    private Cazador cazador;
    private CocinaFantasma taqueria;

    @BeforeEach
    void preparar() {
        cazador = escenario.cazador("humo_azul");
        taqueria = escenario.cocinaAbierta("Taqueria Invisible");
    }

    @Test
    @DisplayName("PI-001 Cazar una cocina abierta guarda la caza, la registra en el historial y otorga la insignia")
    void cazar_cocinaAbierta_guardaRegistraYOtorgaInsignia() throws Exception {
        mvc.perform(post("/cocinas/{id}/cazar", taqueria.getId()).with(como(cazador)).with(csrf()))
                .andExpect(redirectedUrl("/cocinas/" + taqueria.getId()))
                .andExpect(flash().attribute("comanda", Matchers.isA(Comanda.class)))
                .andExpect(flash().attribute("novedades", Matchers.hasItem(Matchers.containsString("Primer hallazgo"))));

        Descubrimiento caza = descubrimientos.findByCazadorIdAndCocinaId(cazador.getId(), taqueria.getId())
                .orElseThrow();
        assertThat(caza.getPuntosObtenidos()).isEqualTo(150 + 50);
        assertThat(caza.getFechaHora()).isEqualTo(Ejemplos.AHORA);
        assertThat(acciones.findFirstByCazadorIdAndDeshechaFalseOrderByFechaHoraDescIdDesc(cazador.getId()))
                .hasValueSatisfying(accion -> assertThat(accion.getTipo()).isEqualTo(TipoAccion.DESCUBRIR_COCINA));
        assertThat(insignias.findByCazadorId(cazador.getId())).extracting("tipo")
                .containsExactly(TipoInsignia.PRIMER_HALLAZGO);
    }

    @Test
    @DisplayName("PI-002 Deshacer la ultima caza borra la caza, marca la accion y retira la insignia")
    void deshacer_ultimaCaza_revierteTodoElProgreso() throws Exception {
        mvc.perform(post("/cocinas/{id}/cazar", taqueria.getId()).with(como(cazador)).with(csrf()));

        mvc.perform(post("/acciones/deshacer").with(como(cazador)).with(csrf())
                        .header("Referer", "http://localhost/cocinas/" + taqueria.getId()))
                .andExpect(redirectedUrl("/cocinas/" + taqueria.getId()))
                .andExpect(flash().attribute("avisoExito", Matchers.containsString("Taqueria Invisible")));

        assertThat(descubrimientos.existsByCazadorIdAndCocinaId(cazador.getId(), taqueria.getId())).isFalse();
        assertThat(acciones.findFirstByCazadorIdAndDeshechaFalseOrderByFechaHoraDescIdDesc(cazador.getId()))
                .isEmpty();
        assertThat(insignias.findByCazadorId(cazador.getId())).isEmpty();
    }

    @Test
    @DisplayName("PI-003 Una regla incumplida no guarda nada y vuelve a la pagina con el motivo")
    void cazar_cocinaCerrada_vuelveConMensajeYSinCambios() throws Exception {
        CocinaFantasma arepas = escenario.cocinaCerrada("Arepa Sin Puerta");

        mvc.perform(post("/cocinas/{id}/cazar", arepas.getId()).with(como(cazador)).with(csrf())
                        .header("Referer", "http://localhost/cocinas/" + arepas.getId()))
                .andExpect(redirectedUrl("/cocinas/" + arepas.getId()))
                .andExpect(flash().attribute("avisoError", Matchers.containsString("cerrada")));

        assertThat(descubrimientos.count()).isZero();
    }

    @Test
    void deshacer_fueraDeLaVentana_noCambiaNada() throws Exception {
        descubrimientos.save(Descubrimiento.registrar(cazador, taqueria, Ejemplos.AHORA.minusMinutes(20), 200));
        acciones.save(new AccionRegistrada(cazador, TipoAccion.DESCUBRIR_COCINA, taqueria.getId(),
                taqueria.getNombre(), 200, Ejemplos.AHORA.minusMinutes(20)));

        mvc.perform(post("/acciones/deshacer").with(como(cazador)).with(csrf()))
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("avisoError", Matchers.containsString("15 minutos")));

        assertThat(descubrimientos.existsByCazadorIdAndCocinaId(cazador.getId(), taqueria.getId())).isTrue();
    }

    @Test
    void resenar_sumaPuntosYSePuedeDeshacer() throws Exception {
        mvc.perform(post("/cocinas/{id}/cazar", taqueria.getId()).with(como(cazador)).with(csrf()));

        mvc.perform(post("/cocinas/{id}/resenas", taqueria.getId()).with(como(cazador)).with(csrf())
                        .param("calificacion", "5").param("comentario", "Llegaron calientes"))
                .andExpect(redirectedUrl("/cocinas/" + taqueria.getId() + "#resenas"));
        assertThat(descubrimientos.resenasDe(cazador.getId())).isEqualTo(1);

        mvc.perform(post("/acciones/deshacer").with(como(cazador)).with(csrf()))
                .andExpect(flash().attribute("avisoExito", Matchers.containsString("20 puntos")));
        assertThat(descubrimientos.resenasDe(cazador.getId())).isZero();
        assertThat(descubrimientos.existsByCazadorIdAndCocinaId(cazador.getId(), taqueria.getId())).isTrue();
    }
}
