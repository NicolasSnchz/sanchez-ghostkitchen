package co.edu.udes.ghostkitchen.dominio.modelo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NivelTest {

    @ParameterizedTest(name = "{0} puntos: {1}")
    @CsvSource({
        "0, NOVATO",
        "299, NOVATO",
        "300, EXPLORADOR",
        "799, EXPLORADOR",
        "800, RASTREADOR",
        "1600, CAZADOR_ELITE",
        "2999, CAZADOR_ELITE",
        "3000, LEYENDA",
        "99999, LEYENDA"
    })
    @DisplayName("PU-006 El nivel se deriva de los puntos en cada limite")
    void paraPuntos_devuelveElNivelAlcanzado(int puntos, Nivel esperado) {
        assertThat(Nivel.paraPuntos(puntos)).isEqualTo(esperado);
    }

    @Test
    void avanceHaciaElSiguienteNivel() {
        assertThat(Nivel.EXPLORADOR.puntosParaSubir(340)).isEqualTo(460);
        assertThat(Nivel.EXPLORADOR.porcentajeHaciaElSiguiente(550)).isEqualTo(50);
        assertThat(Nivel.NOVATO.porcentajeHaciaElSiguiente(0)).isZero();
        assertThat(Nivel.NOVATO.siguiente()).contains(Nivel.EXPLORADOR);
        assertThat(Nivel.RASTREADOR.puntosMinimos()).isEqualTo(800);
    }

    @Test
    void enElUltimoNivel_noHayAQuienSubir() {
        assertThat(Nivel.LEYENDA.siguiente()).isEmpty();
        assertThat(Nivel.LEYENDA.puntosParaSubir(5000)).isZero();
        assertThat(Nivel.LEYENDA.porcentajeHaciaElSiguiente(5000)).isEqualTo(100);
    }
}
