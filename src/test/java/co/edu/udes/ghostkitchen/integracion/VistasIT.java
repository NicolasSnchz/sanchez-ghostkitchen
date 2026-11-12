package co.edu.udes.ghostkitchen.integracion;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.udes.ghostkitchen.dominio.modelo.Administrador;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Reto;
import co.edu.udes.ghostkitchen.dominio.orden.TipoOrden;
import co.edu.udes.ghostkitchen.dominio.puntos.ConceptoPuntaje;
import co.edu.udes.ghostkitchen.dominio.puntos.Puntaje;
import co.edu.udes.ghostkitchen.presentacion.web.Comanda;
import co.edu.udes.ghostkitchen.soporte.Ejemplos;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Renderiza cada pagina con datos reales. Si una plantilla tiene una expresion
 * mal escrita, Thymeleaf falla y la prueba lo detecta antes que un usuario.
 */
class VistasIT extends PruebaIntegracion {

    private Cazador cazador;
    private Administrador admin;
    private CocinaFantasma pizzeria;
    private Reto reto;

    @BeforeEach
    void preparar() throws Exception {
        cazador = escenario.cazador("humo_azul");
        admin = escenario.administrador();
        pizzeria = escenario.cocinaAbierta("Pizza a Ciegas");
        escenario.cocinaCerrada("Arepa Sin Puerta");
        reto = escenario.retoVigente("Semana del rastreador", 4, 200);
        escenario.retoProximo("Ruta de los barrios");
        mvc.perform(post("/retos/{id}/unirse", reto.getId()).with(como(cazador)).with(csrf()));
        mvc.perform(post("/cocinas/{id}/cazar", pizzeria.getId()).with(como(cazador)).with(csrf()));
        mvc.perform(post("/cocinas/{id}/resenas", pizzeria.getId()).with(como(cazador)).with(csrf())
                .param("calificacion", "4").param("comentario", "Masa delgada y bien horneada"));
    }

    @Test
    @DisplayName("PI-009 Todas las paginas publicas se renderizan con datos reales")
    void paginasPublicas_seRenderizan() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("no tienen letrero")))
                .andExpect(content().string(containsString("CAZADA")))
                .andExpect(content().string(containsString("Crear cuenta de cazador")));
        mvc.perform(get("/explorar").param("q", "pizza").param("orden", "CALIFICACION")
                        .param("lat", "7.9").param("lng", "-72.5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Pizza a Ciegas")))
                .andExpect(content().string(not(containsString("Arepa Sin Puerta"))))
                .andExpect(content().string(containsString("data-con-ubicacion=\"true\"")));
        mvc.perform(get("/explorar").param("lat", "abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Parque Santander")));
        mvc.perform(get("/cocinas/{id}", pizzeria.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Masa delgada y bien horneada")))
                .andExpect(content().string(containsString("Ingresa con tu cuenta de cazador")));
        mvc.perform(get("/retos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Semana del rastreador")))
                .andExpect(content().string(containsString("Ruta de los barrios")));
        mvc.perform(get("/ranking"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("humo_azul")));
        mvc.perform(get("/ingresar")).andExpect(status().isOk())
                .andExpect(content().string(containsString("cazador@ghostkitchen.co")));
        mvc.perform(get("/registro")).andExpect(status().isOk());
        mvc.perform(get("/cocinas/{id}", 999_999)).andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Esta cocina se esfum")));
    }

    /** Si a un valor de TipoOrden le faltara su estrategia registrada en Spring, esta prueba lo detecta. */
    @ParameterizedTest(name = "orden {0}")
    @EnumSource(TipoOrden.class)
    @DisplayName("PI-009 El explorador funciona con cada criterio de orden registrado en Spring")
    void explorador_funcionaConCadaCriterioDeOrden(TipoOrden orden) throws Exception {
        mvc.perform(get("/explorar").param("orden", orden.name()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Pizza a Ciegas")));
    }

    @Test
    void paginasDelCazador_muestranProgresoYLaOpcionDeDeshacer() throws Exception {
        mvc.perform(get("/perfil").with(como(cazador)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Primer hallazgo")))
                .andExpect(content().string(containsString("Semana del rastreador")))
                .andExpect(content().string(containsString("Deshacer")));
        mvc.perform(get("/explorar").with(como(cazador)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-segundos=\"900\"")))
                .andExpect(content().string(containsString("Ya la cazaste")));
        mvc.perform(get("/retos").with(como(cazador)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("1 de 4 cocinas")));
        Comanda comanda = new Comanda(pizzeria.getId(), "Pizza a Ciegas",
                List.of(new Puntaje.Linea(ConceptoPuntaje.BASE, 150), new Puntaje.Linea(ConceptoPuntaje.PRIMER_CAZADOR, 50)),
                200, Ejemplos.AHORA);
        mvc.perform(get("/cocinas/{id}", pizzeria.getId()).with(como(cazador)).flashAttr("comanda", comanda))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Primer cazador")))
                .andExpect(content().string(containsString("200 pts")));
    }

    @Test
    void paginasDelAdministrador_seRenderizan() throws Exception {
        mvc.perform(get("/admin/cocinas").with(como(admin)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Pizza a Ciegas")));
        mvc.perform(get("/admin/cocinas/nueva").with(como(admin))).andExpect(status().isOk());
        mvc.perform(get("/admin/cocinas/{id}/editar", pizzeria.getId()).with(como(admin)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"12:00\"")));
        mvc.perform(get("/admin/retos").with(como(admin)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ruta de los barrios")));
    }
}
