package co.edu.udes.ghostkitchen.dominio.puntos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Cadena de decoradores de puntos, armada igual que en ConfiguracionDominio. */
class CalculadoraPuntosTest {

    private static final LocalTime TARDE = LocalTime.of(15, 0);
    private static final LocalTime NOCHE_FANTASMA = LocalTime.of(23, 0);

    private final CalculadoraPuntos cadena =
            new MultiplicadorHoraFantasma(new BonoRachaDiaria(new BonoPrimerCazador(new PuntosBasePorRareza())));

    @Test
    @DisplayName("PU-001 La cadena de decoradores suma bonos en orden y la hora fantasma multiplica el acumulado")
    void calcular_todosLosBonos_aplicaCadaDecoradorEnOrden() {
        Puntaje puntaje = cadena.calcular(new ContextoCaza(Rareza.LEGENDARIA, true, 7, NOCHE_FANTASMA));

        assertThat(puntaje.total()).isEqualTo(525);
        assertThat(puntaje.desglose()).containsExactly(
                new Puntaje.Linea(ConceptoPuntaje.BASE, 250),
                new Puntaje.Linea(ConceptoPuntaje.PRIMER_CAZADOR, 50),
                new Puntaje.Linea(ConceptoPuntaje.RACHA_DIARIA, 50),
                new Puntaje.Linea(ConceptoPuntaje.HORA_FANTASMA, 175));
    }

    @Test
    @DisplayName("PU-002 Cada decorador delega una sola vez en la calculadora que envuelve (Spy)")
    void calcular_conDecorador_delegaEnLaEnvuelta() {
        CalculadoraPuntos base = spy(new PuntosBasePorRareza());
        CalculadoraPuntos conBono = new BonoPrimerCazador(base);

        Puntaje puntaje = conBono.calcular(new ContextoCaza(Rareza.RARA, false, 0, TARDE));

        verify(base, times(1)).calcular(any());
        assertThat(puntaje.total()).isEqualTo(150);
        assertThat(puntaje.desglose()).extracting(Puntaje.Linea::concepto).containsExactly(ConceptoPuntaje.BASE);
    }

    @ParameterizedTest(name = "rareza {0}, racha {1} dias: {2} puntos")
    @CsvSource({
        "COMUN, 0, 100",
        "RARA, 1, 160",
        "COMUN, 5, 150",
        "COMUN, 9, 150",
        "LEGENDARIA, 2, 270"
    })
    void calcular_sinHoraFantasma_sumaBaseYRachaConTope(Rareza rareza, int racha, int esperado) {
        assertThat(cadena.calcular(new ContextoCaza(rareza, false, racha, TARDE)).total()).isEqualTo(esperado);
    }

    @Test
    void multiplicador_redondeaAlEnteroMasCercano() {
        Puntaje puntaje = Puntaje.base(105).multiplicadoPor(ConceptoPuntaje.HORA_FANTASMA, 1.5);

        assertThat(puntaje.total()).isEqualTo(158);
        assertThat(puntaje.desglose().get(1).puntos()).isEqualTo(53);
    }

    @Test
    void contexto_invalido_seRechaza() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ContextoCaza(null, false, 0, TARDE));
        assertThatIllegalArgumentException().isThrownBy(() -> new ContextoCaza(Rareza.COMUN, false, -1, TARDE));
        assertThatIllegalArgumentException().isThrownBy(() -> Puntaje.base(-1));
        assertThatNullPointerException().isThrownBy(() -> new BonoPrimerCazador(null));
    }

    @Test
    void puntosCazador_sumaCazasResenasYRecompensas() {
        assertThat(PuntosCazador.total(430, 2, 200)).isEqualTo(670);
        assertThat(PuntosCazador.total(0, 0, 0)).isZero();
    }
}
