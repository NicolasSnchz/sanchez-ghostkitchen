package co.edu.udes.ghostkitchen.presentacion.web;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.udes.ghostkitchen.aplicacion.acciones.ResultadoComando;
import co.edu.udes.ghostkitchen.aplicacion.progreso.Novedad;
import co.edu.udes.ghostkitchen.aplicacion.progreso.TipoNovedad;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Coordenada;
import co.edu.udes.ghostkitchen.dominio.orden.TipoOrden;
import co.edu.udes.ghostkitchen.dominio.puntos.ConceptoPuntaje;
import co.edu.udes.ghostkitchen.dominio.puntos.Puntaje;
import co.edu.udes.ghostkitchen.presentacion.comun.ParametrosBusqueda;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;

/** Piezas pequenas de la capa web que no necesitan levantar Spring. */
class PresentacionTest {

    @Test
    @DisplayName("PU-023 Solo se vuelve a paginas del mismo sitio: un Referer externo no redirige afuera")
    void redireccion_soloAceptaRutasDelMismoSitio() {
        MockHttpServletRequest interna = new MockHttpServletRequest();
        interna.setServerName("localhost");
        interna.addHeader("Referer", "http://localhost:8080/cocinas/4?x=1");
        MockHttpServletRequest externa = new MockHttpServletRequest();
        externa.setServerName("localhost");
        externa.addHeader("Referer", "https://sitio-malicioso.com/robar");
        MockHttpServletRequest rara = new MockHttpServletRequest();
        rara.addHeader("Referer", "no es una url %%");

        assertThat(RedireccionSegura.haciaOrigen(interna, "/")).isEqualTo("/cocinas/4?x=1");
        assertThat(RedireccionSegura.haciaOrigen(externa, "/perfil")).isEqualTo("/perfil");
        assertThat(RedireccionSegura.haciaOrigen(rara, "/")).isEqualTo("/");
        assertThat(RedireccionSegura.haciaOrigen(new MockHttpServletRequest(), "/retos")).isEqualTo("/retos");
    }

    @Test
    void parametrosDeBusqueda_toleranValoresInvalidos() {
        ParametrosBusqueda parametros = new ParametrosBusqueda();
        parametros.setQ("  " + "a".repeat(80));
        parametros.setCategoria("pizza");
        parametros.setOrden("desconocido");
        parametros.setLat(95.0);
        parametros.setLng(-72.5);

        var filtro = parametros.aFiltro();

        assertThat(filtro.texto()).hasSize(60);
        assertThat(filtro.categoria()).isEqualTo(CategoriaCocina.PIZZA);
        assertThat(filtro.orden()).isEqualTo(TipoOrden.CERCANIA);
        assertThat(filtro.referencia()).isEqualTo(Coordenada.CENTRO_DE_CUCUTA);
        assertThat(parametros.tieneUbicacion()).isFalse();

        parametros.setLat(7.9);
        parametros.setCategoria("comida-marciana");
        assertThat(parametros.tieneUbicacion()).isTrue();
        assertThat(parametros.aFiltro().categoria()).isNull();
    }

    @Test
    void traductor_armaLaFraseConLaClaveYElNombreLegible() {
        StaticMessageSource mensajes = new StaticMessageSource();
        mensajes.addMessage("insignia.NOCTAMBULO.nombre", Locale.ROOT, "Noctambulo");
        mensajes.addMessage("novedad.INSIGNIA_GANADA", Locale.ROOT, "Ganaste la insignia {0}.");
        mensajes.addMessage("nivel.EXPLORADOR", Locale.ROOT, "Explorador");
        mensajes.addMessage("novedad.NIVEL_SUBIDO", Locale.ROOT, "Subiste a nivel {0}.");
        mensajes.addMessage("novedad.RETO_COMPLETADO", Locale.ROOT, "Completaste el reto {0} y ganaste {1} puntos.");
        LocaleContextHolder.setLocale(Locale.ROOT);
        try {
            List<String> frases = new TraductorNovedades(mensajes).traducir(List.of(
                    Novedad.de(TipoNovedad.INSIGNIA_GANADA, "NOCTAMBULO"),
                    Novedad.de(TipoNovedad.NIVEL_SUBIDO, "EXPLORADOR"),
                    new Novedad(TipoNovedad.RETO_COMPLETADO, "Doble o nada", 120)));

            assertThat(frases).containsExactly("Ganaste la insignia Noctambulo.", "Subiste a nivel Explorador.",
                    "Completaste el reto Doble o nada y ganaste 120 puntos.");
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }

    @Test
    void comanda_numeraConCocinaYHora() {
        ResultadoComando resultado = new ResultadoComando(4L, "Taqueria Invisible", 225,
                List.of(new Puntaje.Linea(ConceptoPuntaje.BASE, 150), new Puntaje.Linea(ConceptoPuntaje.HORA_FANTASMA, 75)));

        Comanda comanda = Comanda.de(resultado, LocalDateTime.of(2026, 11, 10, 22, 5));

        assertThat(comanda.numero()).isEqualTo("004-2205");
        assertThat(comanda.lineas()).hasSize(2);
        assertThat(comanda.total()).isEqualTo(225);
    }
}
