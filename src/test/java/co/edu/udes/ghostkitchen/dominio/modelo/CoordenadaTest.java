package co.edu.udes.ghostkitchen.dominio.modelo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

class CoordenadaTest {

    @Test
    void distancia_unGradoDeLatitudSonUnos111Km() {
        Coordenada ecuador = new Coordenada(0, -72.5);
        Coordenada norte = new Coordenada(1, -72.5);

        assertThat(ecuador.distanciaKmHasta(norte)).isCloseTo(111.19, within(0.05));
        assertThat(norte.distanciaKmHasta(norte)).isZero();
    }

    @Test
    void distancia_dentroDeCucutaEsDeUnosPocosKilometros() {
        Coordenada laRiviera = new Coordenada(7.8977, -72.4930);

        assertThat(Coordenada.CENTRO_DE_CUCUTA.distanciaKmHasta(laRiviera)).isBetween(1.0, 2.5);
    }

    @Test
    void coordenadaFueraDeRango_seRechaza() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Coordenada(91, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> new Coordenada(-91, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> new Coordenada(0, 181));
        assertThatIllegalArgumentException().isThrownBy(() -> new Coordenada(0, -181));
    }

    @Test
    void ubicacion_seComparaPorValor() {
        Ubicacion una = new Ubicacion(7.89, -72.50, "Centro");

        assertThat(una).isEqualTo(new Ubicacion(7.89, -72.50, "Centro"))
                .hasSameHashCodeAs(new Ubicacion(7.89, -72.50, "Centro"))
                .isNotEqualTo(new Ubicacion(7.89, -72.50, "Sayago"))
                .isNotEqualTo("Centro");
        assertThat(una.distanciaKmHasta(una.coordenada())).isZero();
        assertThat(una.getLatitud()).isEqualTo(7.89);
        assertThat(una.getLongitud()).isEqualTo(-72.50);
    }
}
