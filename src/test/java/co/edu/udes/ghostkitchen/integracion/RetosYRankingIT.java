package co.edu.udes.ghostkitchen.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.udes.ghostkitchen.aplicacion.progreso.ServicioProgreso;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Reto;
import co.edu.udes.ghostkitchen.persistencia.RepositorioParticipaciones;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RetosYRankingIT extends PruebaIntegracion {

    @Autowired
    private RepositorioParticipaciones participaciones;

    @Autowired
    private ServicioProgreso progreso;

    @Test
    @DisplayName("PI-004 Al cumplir la meta de un reto se suma la recompensa y el ranking la refleja")
    void completarReto_sumaRecompensaYApareceEnElRanking() throws Exception {
        Cazador cazador = escenario.cazador("lula_bites");
        Reto reto = escenario.retoVigente("Doble o nada", 2, 120);
        CocinaFantasma primera = escenario.cocinaAbierta("Wok del Callejon");
        CocinaFantasma segunda = escenario.cocinaAbierta("Bowl Secreto");

        mvc.perform(post("/retos/{id}/unirse", reto.getId()).with(como(cazador)).with(csrf()))
                .andExpect(redirectedUrl("/retos#reto-" + reto.getId()))
                .andExpect(flash().attribute("avisoExito", Matchers.containsString("Doble o nada")));
        mvc.perform(post("/cocinas/{id}/cazar", primera.getId()).with(como(cazador)).with(csrf()));
        mvc.perform(post("/cocinas/{id}/cazar", segunda.getId()).with(como(cazador)).with(csrf()))
                .andExpect(flash().attribute("novedades",
                        Matchers.hasItem(Matchers.containsString("Completaste el reto Doble o nada"))));

        assertThat(participaciones.findByCazadorIdAndRetoId(cazador.getId(), reto.getId()))
                .hasValueSatisfying(participacion -> assertThat(participacion.estaCompletada()).isTrue());
        int esperado = (150 + 50) + (150 + 50) + 120;
        assertThat(progreso.puntosDe(cazador.getId())).isEqualTo(esperado);
        mvc.perform(get("/api/v1/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].alias").value("lula_bites"))
                .andExpect(jsonPath("$[0].puntos").value(esperado))
                .andExpect(jsonPath("$[0].nivel").value("EXPLORADOR"));
    }

    @Test
    void unirse_aUnRetoQueNoEstaVigente_seRechaza() throws Exception {
        Cazador cazador = escenario.cazador("brasa_norte");
        Reto proximo = escenario.retoProximo("Ruta de los barrios");

        mvc.perform(post("/retos/{id}/unirse", proximo.getId()).with(como(cazador)).with(csrf())
                        .header("Referer", "http://localhost/retos"))
                .andExpect(redirectedUrl("/retos"))
                .andExpect(flash().attribute("avisoError", Matchers.containsString("no est")));
        assertThat(participaciones.existsByCazadorIdAndRetoId(cazador.getId(), proximo.getId())).isFalse();
    }
}
