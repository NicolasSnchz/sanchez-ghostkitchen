package co.edu.udes.ghostkitchen.dominio.modelo;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cazador;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RetosYAccionesTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 11, 8);
    private static final LocalDateTime MOMENTO = LocalDateTime.of(2026, 11, 10, 21, 0);

    private final Reto reto = new Reto("Semana del rastreador", "Caza cuatro cocinas.", 4, 200, INICIO,
            INICIO.plusDays(7));

    @Test
    void reto_estadoSegunLaFecha() {
        assertThat(reto.estadoEn(INICIO.minusDays(1))).isEqualTo(EstadoReto.PROXIMO);
        assertThat(reto.estadoEn(INICIO)).isEqualTo(EstadoReto.VIGENTE);
        assertThat(reto.estaVigente(INICIO.plusDays(7))).isTrue();
        assertThat(reto.estadoEn(INICIO.plusDays(8))).isEqualTo(EstadoReto.FINALIZADO);
        assertThat(reto.cierre()).isEqualTo(INICIO.plusDays(7).atTime(LocalTime.MAX));
        assertThat(reto.getTitulo()).isEqualTo("Semana del rastreador");
        assertThat(reto.getDescripcion()).isEqualTo("Caza cuatro cocinas.");
        assertThat(reto.getPuntosRecompensa()).isEqualTo(200);
        assertThat(reto.getFechaInicio()).isEqualTo(INICIO);
    }

    @Test
    void reto_conFechasInvertidasOMetaFueraDeRango_seRechaza() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Reto("Reto", "Descripcion", 4, 200, INICIO, INICIO.minusDays(1)));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Reto("Reto", "Descripcion", 0, 200, INICIO, INICIO));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Reto("Reto", "Descripcion", 4, 5001, INICIO, INICIO));
    }

    @Test
    @DisplayName("PU-008 Un reto se completa una sola vez y se revierte si la meta deja de cumplirse")
    void actualizarAvance_completaUnaVezYRevierte() {
        ParticipacionReto participacion = new ParticipacionReto(cazador("humo_azul"), reto, MOMENTO);

        assertThat(participacion.actualizarAvance(3, MOMENTO)).isFalse();
        assertThat(participacion.actualizarAvance(4, MOMENTO.plusHours(1))).isTrue();
        assertThat(participacion.actualizarAvance(5, MOMENTO.plusHours(2))).isFalse();
        assertThat(participacion.getFechaCompletado()).isEqualTo(MOMENTO.plusHours(1));

        assertThat(participacion.actualizarAvance(3, MOMENTO.plusHours(3))).isFalse();
        assertThat(participacion.estaCompletada()).isFalse();
        assertThat(participacion.getReto()).isSameAs(reto);
        assertThat(participacion.getFechaUnion()).isEqualTo(MOMENTO);
    }

    @Test
    @DisplayName("PU-009 Una accion solo se puede deshacer dentro de la ventana y una sola vez")
    void sePuedeDeshacer_dentroDeLaVentanaYSinHaberseDeshecho() {
        AccionRegistrada accion = new AccionRegistrada(cazador("humo_azul"), TipoAccion.DESCUBRIR_COCINA, 7L,
                "Pizza a Ciegas", 150, MOMENTO);
        Duration ventana = Duration.ofMinutes(15);

        assertThat(accion.sePuedeDeshacer(MOMENTO.plusMinutes(15), ventana)).isTrue();
        assertThat(accion.sePuedeDeshacer(MOMENTO.plusMinutes(15).plusSeconds(1), ventana)).isFalse();

        accion.marcarDeshecha();
        assertThat(accion.sePuedeDeshacer(MOMENTO.plusMinutes(1), ventana)).isFalse();
        assertThat(accion.isDeshecha()).isTrue();
        assertThat(accion.getTipo()).isEqualTo(TipoAccion.DESCUBRIR_COCINA);
        assertThat(accion.getObjetivoId()).isEqualTo(7L);
        assertThat(accion.getDetalle()).isEqualTo("Pizza a Ciegas");
        assertThat(accion.getPuntos()).isEqualTo(150);
        assertThat(accion.getFechaHora()).isEqualTo(MOMENTO);
        assertThat(accion.getCazador().getAlias()).isEqualTo("humo_azul");
    }
}
