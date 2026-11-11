package co.edu.udes.ghostkitchen.integracion;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class ApiIT extends PruebaIntegracion {

    @Autowired
    private RepositorioCocinas cocinas;

    @Test
    @DisplayName("PI-006 La API REST filtra como el explorador y responde errores en formato Problem Details")
    void api_filtraYRespondeErroresEstandar() throws Exception {
        CocinaFantasma abierta = escenario.cocinaAbierta("Pizza a Ciegas");
        escenario.cocinaCerrada("Arepa Sin Puerta");
        CocinaFantasma retirada = escenario.cocinaAbierta("Fritanga Fugitiva");
        retirada.desactivar();
        cocinas.save(retirada);

        mvc.perform(get("/api/v1/cocinas").param("abiertas", "true").param("orden", "NOVEDAD"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Pizza a Ciegas"))
                .andExpect(jsonPath("$[0].horaApertura").value("12:00"))
                .andExpect(jsonPath("$[0].abierta").value(true));
        mvc.perform(get("/api/v1/cocinas").param("categoria", "AREPAS"))
                .andExpect(jsonPath("$[*].nombre", Matchers.contains("Arepa Sin Puerta")));
        mvc.perform(get("/api/v1/cocinas/{id}", abierta.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cocina.barrio").value("La Riviera"))
                .andExpect(jsonPath("$.resenas").isArray());

        mvc.perform(get("/api/v1/cocinas/{id}", retirada.getId()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"));
        mvc.perform(get("/api/v1/cocinas/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", Matchers.containsString("id")));
    }

    @Test
    void ranking_acotaElLimite() throws Exception {
        mvc.perform(get("/api/v1/ranking").param("limite", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
