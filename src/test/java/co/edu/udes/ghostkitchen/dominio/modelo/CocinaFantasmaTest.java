package co.edu.udes.ghostkitchen.dominio.modelo;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cocina;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CocinaFantasmaTest {

    @ParameterizedTest(name = "abre {0}, cierra {1}, a las {2}: abierta = {3}")
    @CsvSource({
        "12:00, 23:00, 12:00, true",
        "12:00, 23:00, 22:59, true",
        "12:00, 23:00, 23:00, false",
        "12:00, 23:00, 11:59, false",
        "18:00, 02:00, 23:30, true",
        "18:00, 02:00, 01:59, true",
        "18:00, 02:00, 02:00, false",
        "18:00, 02:00, 17:00, false"
    })
    @DisplayName("PU-004 El horario admite cierres despues de medianoche")
    void estaAbierta_respetaHorarioInclusoCruzandoMedianoche(LocalTime apertura, LocalTime cierre, LocalTime hora,
                                                            boolean esperado) {
        CocinaFantasma pizzeria = cocina().horario(apertura, cierre).build();

        assertThat(pizzeria.estaAbierta(hora)).isEqualTo(esperado);
    }

    @Test
    @DisplayName("PU-005 El Builder rechaza una cocina sin canales de pedido o con horario vacio")
    void build_sinCanalOHorarioIgual_lanzaExcepcion() {
        assertThatIllegalArgumentException().isThrownBy(() -> cocina().canales(Set.of()).build())
                .withMessageContaining("canal");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> cocina().horario(LocalTime.NOON, LocalTime.NOON).build());
        assertThatIllegalArgumentException().isThrownBy(() -> cocina().nombre(" ").build());
        assertThatIllegalArgumentException().isThrownBy(() -> cocina().registradaEl(null).build());
        assertThatIllegalArgumentException().isThrownBy(() -> cocina().canal(null));
    }

    @Test
    void build_valido_quedaActivaConLosDatosLimpios() {
        CocinaFantasma pizzeria = cocina().nombre("  Pizza a Ciegas  ").canales(null).canal(CanalPedido.WHATSAPP)
                .build();

        assertThat(pizzeria.getNombre()).isEqualTo("Pizza a Ciegas");
        assertThat(pizzeria.isActiva()).isTrue();
        assertThat(pizzeria.getCanales()).containsExactly(CanalPedido.WHATSAPP);
        assertThat(pizzeria.getCategoria()).isEqualTo(CategoriaCocina.PIZZA);
        assertThat(pizzeria.getRareza()).isEqualTo(Rareza.COMUN);
        assertThat(pizzeria.getDescripcion()).isNotBlank();
        assertThat(pizzeria.getHoraApertura()).isEqualTo(LocalTime.NOON);
        assertThat(pizzeria.getHoraCierre()).isEqualTo(LocalTime.of(23, 0));
        assertThat(pizzeria.getUbicacion().getBarrio()).isEqualTo("La Riviera");
        assertThat(pizzeria.getId()).isNull();
    }

    @Test
    void actualizar_cambiaDatosPeroConservaFechaDeAltaYEstado() {
        CocinaFantasma pizzeria = cocina().build();
        pizzeria.desactivar();

        pizzeria.actualizar(cocina().nombre("Pizza Sin Nombre").rareza(Rareza.RARA)
                .canales(List.of(CanalPedido.DIDI_FOOD, CanalPedido.RAPPI)).registradaEl(LocalDate.of(2030, 1, 1)));

        assertThat(pizzeria.getNombre()).isEqualTo("Pizza Sin Nombre");
        assertThat(pizzeria.getRareza()).isEqualTo(Rareza.RARA);
        assertThat(pizzeria.getCanales()).containsExactlyInAnyOrder(CanalPedido.DIDI_FOOD, CanalPedido.RAPPI);
        assertThat(pizzeria.getFechaAlta()).isEqualTo(LocalDate.of(2026, 9, 1));
        assertThat(pizzeria.isActiva()).isFalse();

        pizzeria.activar();
        assertThat(pizzeria.isActiva()).isTrue();
    }
}
