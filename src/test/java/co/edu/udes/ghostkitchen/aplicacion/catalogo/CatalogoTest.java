package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import co.edu.udes.ghostkitchen.dominio.modelo.CanalPedido;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Coordenada;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import co.edu.udes.ghostkitchen.dominio.orden.FichaCocina;
import co.edu.udes.ghostkitchen.dominio.orden.OrdenPorCercania;
import co.edu.udes.ghostkitchen.dominio.orden.OrdenPorNovedad;
import co.edu.udes.ghostkitchen.dominio.orden.TipoOrden;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CatalogoTest {

    /** Cocina cerrada en este momento, en el barrio Centro. */
    private final FichaCocina olla = new FichaCocina(1L, "La Olla Prohibida", CategoriaCocina.TIPICA, Rareza.RARA,
            "Centro", 7.88, -72.50, LocalTime.of(11, 0), LocalTime.of(16, 0), Set.of(CanalPedido.RAPPI),
            LocalDate.of(2026, 9, 1), false, 0, 0, 0, 1.0, false);

    @Test
    @DisplayName("PU-019 El selector entrega la estrategia pedida y no admite dos para el mismo orden")
    void selector_entregaLaEstrategiaDelTipoPedido() {
        SelectorCriterioOrden selector = new SelectorCriterioOrden(List.of(new OrdenPorCercania(),
                new OrdenPorNovedad()));

        assertThat(selector.para(TipoOrden.NOVEDAD)).isInstanceOf(OrdenPorNovedad.class);
        assertThatIllegalArgumentException().isThrownBy(() -> selector.para(TipoOrden.POPULARIDAD));
        assertThatIllegalStateException().isThrownBy(() -> new SelectorCriterioOrden(
                List.of(new OrdenPorCercania(), new OrdenPorCercania())));
    }

    @Test
    void filtro_buscaSinTildesNiMayusculasEnNombreYBarrio() {
        assertThat(new FiltroCocinas("olla", null, false, null, null).admite(olla)).isTrue();
        assertThat(new FiltroCocinas("CENTRO", null, false, null, null).admite(olla)).isTrue();
        assertThat(new FiltroCocinas("proh\u00edbida", null, false, null, null).admite(olla)).isTrue();
        assertThat(new FiltroCocinas("sushi", null, false, null, null).admite(olla)).isFalse();
    }

    @Test
    void filtro_porCategoriaYPorHorario() {
        assertThat(new FiltroCocinas("", CategoriaCocina.TIPICA, false, null, null).admite(olla)).isTrue();
        assertThat(new FiltroCocinas("", CategoriaCocina.PIZZA, false, null, null).admite(olla)).isFalse();
        assertThat(new FiltroCocinas(null, null, true, null, null).admite(olla)).isFalse();
    }

    @Test
    void filtro_porDefectoUsaElCentroYOrdenaPorCercania() {
        FiltroCocinas filtro = FiltroCocinas.ordenadasPor(null);

        assertThat(filtro.referencia()).isEqualTo(Coordenada.CENTRO_DE_CUCUTA);
        assertThat(filtro.orden()).isEqualTo(TipoOrden.CERCANIA);
        assertThat(filtro.texto()).isEmpty();
    }
}
