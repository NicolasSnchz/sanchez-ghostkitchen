package co.edu.udes.ghostkitchen.dominio.orden;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.ficha;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Cada estrategia de orden, con el desempate comun por nombre. */
class CriteriosOrdenTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 10);

    private final FichaCocina cercanaYNueva = ficha("Bowl Secreto", 4.0, 2, 3, 0.4, HOY.minusDays(2));
    private final FichaCocina lejanaYPopular = ficha("Arepa Sin Puerta", 4.5, 1, 9, 3.1, HOY.minusDays(40));
    private final FichaCocina mejorCalificada = ficha("Nori Clandestino", 4.5, 6, 1, 1.2, HOY.minusDays(20));
    private final List<FichaCocina> fichas = List.of(lejanaYPopular, mejorCalificada, cercanaYNueva);

    @Test
    void cercania_ordenaDeMenorAMayorDistancia() {
        assertThat(new OrdenPorCercania().ordenar(fichas))
                .containsExactly(cercanaYNueva, mejorCalificada, lejanaYPopular);
    }

    @Test
    @DisplayName("PU-011 A igual calificacion gana la cocina con mas resenas")
    void calificacion_desempataPorCantidadDeResenas() {
        assertThat(new OrdenPorCalificacion().ordenar(fichas))
                .containsExactly(mejorCalificada, lejanaYPopular, cercanaYNueva);
    }

    @Test
    void popularidadYNovedad() {
        assertThat(new OrdenPorPopularidad().ordenar(fichas))
                .containsExactly(lejanaYPopular, cercanaYNueva, mejorCalificada);
        assertThat(new OrdenPorNovedad().ordenar(fichas))
                .containsExactly(cercanaYNueva, mejorCalificada, lejanaYPopular);
    }

    @Test
    void empateTotal_seResuelvePorNombre() {
        FichaCocina zeta = ficha("Zeta", 4.0, 1, 1, 1.0, HOY);
        FichaCocina alfa = ficha("Alfa", 4.0, 1, 1, 1.0, HOY);

        assertThat(new OrdenPorPopularidad().ordenar(List.of(zeta, alfa))).containsExactly(alfa, zeta);
        assertThat(alfa.tieneResenas()).isTrue();
    }

    @Test
    void cadaCriterioDeclaraSuTipo() {
        assertThat(new OrdenPorCercania().tipo()).isEqualTo(TipoOrden.CERCANIA);
        assertThat(new OrdenPorCalificacion().tipo()).isEqualTo(TipoOrden.CALIFICACION);
        assertThat(new OrdenPorPopularidad().tipo()).isEqualTo(TipoOrden.POPULARIDAD);
        assertThat(new OrdenPorNovedad().tipo()).isEqualTo(TipoOrden.NOVEDAD);
    }

    @ParameterizedTest(name = "\"{0}\" se interpreta como {1}")
    @CsvSource({"novedad, NOVEDAD", "' Popularidad ', POPULARIDAD", "inventado, CERCANIA", "'', CERCANIA"})
    void tipoOrden_desdeTextoDeLaUrl(String valor, TipoOrden esperado) {
        assertThat(TipoOrden.desde(valor)).isEqualTo(esperado);
        assertThat(TipoOrden.desde(null)).isEqualTo(TipoOrden.CERCANIA);
    }
}
