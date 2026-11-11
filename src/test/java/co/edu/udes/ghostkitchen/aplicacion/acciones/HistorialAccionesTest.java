package co.edu.udes.ghostkitchen.aplicacion.acciones;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.AHORA;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cazador;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.conId;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.relojEn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import co.edu.udes.ghostkitchen.aplicacion.progreso.NotificadorProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.Novedad;
import co.edu.udes.ghostkitchen.aplicacion.progreso.ServicioProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.TipoNovedad;
import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import co.edu.udes.ghostkitchen.dominio.modelo.AccionRegistrada;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;
import co.edu.udes.ghostkitchen.persistencia.RepositorioAcciones;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

/** Invocador del patron Command: ejecutar, registrar, notificar y deshacer. */
class HistorialAccionesTest {

    private static final long CAZADOR = 1L;

    private final RepositorioAcciones acciones = mock(RepositorioAcciones.class);
    private final RepositorioCazadores cazadores = mock(RepositorioCazadores.class);
    private final FabricaComandos fabrica = mock(FabricaComandos.class);
    private final ServicioProgreso progreso = mock(ServicioProgreso.class);
    private final NotificadorProgreso notificador = mock(NotificadorProgreso.class);
    private final Cazador humoAzul = conId(cazador("humo_azul"), CAZADOR);

    private HistorialAcciones historialEn(LocalDateTime momento) {
        return new HistorialAcciones(acciones, cazadores, fabrica, progreso, notificador, relojEn(momento));
    }

    private AccionRegistrada cazaHecha(LocalDateTime momento) {
        return new AccionRegistrada(humoAzul, TipoAccion.DESCUBRIR_COCINA, 7L, "Pizza a Ciegas", 150, momento);
    }

    @Test
    @DisplayName("PU-014 Ejecutar un comando lo registra en el historial y avisa a los observadores (Mock)")
    void ejecutar_registraLaAccionYNotificaConLosPuntosDeAntes() {
        Comando comando = mock(Comando.class);
        when(comando.cazadorId()).thenReturn(CAZADOR);
        when(comando.tipo()).thenReturn(TipoAccion.DESCUBRIR_COCINA);
        when(comando.ejecutar()).thenReturn(ResultadoComando.sinDesglose(7L, "Pizza a Ciegas", 150));
        when(progreso.puntosDe(CAZADOR)).thenReturn(280);
        when(cazadores.getReferenceById(CAZADOR)).thenReturn(humoAzul);
        List<Novedad> novedades = List.of(Novedad.de(TipoNovedad.NIVEL_SUBIDO, "EXPLORADOR"));
        when(notificador.notificar(CAZADOR, 280)).thenReturn(novedades);

        ResultadoAccion resultado = historialEn(AHORA).ejecutar(comando);

        InOrder orden = inOrder(comando, acciones, notificador);
        orden.verify(comando).ejecutar();
        ArgumentCaptor<AccionRegistrada> registrada = ArgumentCaptor.forClass(AccionRegistrada.class);
        orden.verify(acciones).save(registrada.capture());
        orden.verify(notificador).notificar(CAZADOR, 280);
        assertThat(registrada.getValue().getPuntos()).isEqualTo(150);
        assertThat(registrada.getValue().getFechaHora()).isEqualTo(AHORA);
        assertThat(resultado.novedades()).isEqualTo(novedades);
        assertThat(resultado.tipo()).isEqualTo(TipoAccion.DESCUBRIR_COCINA);
    }

    @Test
    @DisplayName("PU-015 Deshacer revierte la ultima accion con el comando que reconstruye la fabrica")
    void deshacerUltima_dentroDeLaVentana_reconstruyeElComandoYLoRevierte() {
        AccionRegistrada ultima = cazaHecha(AHORA.minusMinutes(10));
        Comando reconstruido = mock(Comando.class);
        when(acciones.findFirstByCazadorIdAndDeshechaFalseOrderByFechaHoraDescIdDesc(CAZADOR))
                .thenReturn(Optional.of(ultima));
        when(fabrica.paraDeshacer(ultima)).thenReturn(reconstruido);
        when(progreso.puntosDe(CAZADOR)).thenReturn(430);

        ResultadoDeshacer resultado = historialEn(AHORA).deshacerUltima(CAZADOR);

        InOrder orden = inOrder(reconstruido, notificador);
        orden.verify(reconstruido).deshacer();
        orden.verify(notificador).notificar(CAZADOR, 430);
        assertThat(ultima.isDeshecha()).isTrue();
        assertThat(resultado.puntosRetirados()).isEqualTo(150);
        assertThat(resultado.detalle()).isEqualTo("Pizza a Ciegas");
    }

    @Test
    @DisplayName("PU-016 Pasados 15 minutos ya no se puede deshacer (Fake de reloj)")
    void deshacerUltima_fueraDeLaVentana_rechazaSinTocarNada() {
        AccionRegistrada ultima = cazaHecha(AHORA.minusMinutes(16));
        when(acciones.findFirstByCazadorIdAndDeshechaFalseOrderByFechaHoraDescIdDesc(CAZADOR))
                .thenReturn(Optional.of(ultima));

        assertThatThrownBy(() -> historialEn(AHORA).deshacerUltima(CAZADOR))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("accion.fuera-de-ventana");
        verifyNoInteractions(fabrica);
        verify(notificador, never()).notificar(any(), anyInt());
        assertThat(ultima.isDeshecha()).isFalse();
    }

    @Test
    void deshacerUltima_sinAcciones_rechaza() {
        assertThatThrownBy(() -> historialEn(AHORA).deshacerUltima(CAZADOR))
                .hasMessage("accion.nada-que-deshacer");
    }

    @Test
    void accionDeshacibleYRecientes() {
        AccionRegistrada ultima = cazaHecha(AHORA.minusMinutes(5));
        when(acciones.findFirstByCazadorIdAndDeshechaFalseOrderByFechaHoraDescIdDesc(CAZADOR))
                .thenReturn(Optional.of(ultima));
        when(acciones.findTop8ByCazadorIdOrderByFechaHoraDescIdDesc(CAZADOR)).thenReturn(List.of(ultima));

        assertThat(historialEn(AHORA).accionDeshacible(CAZADOR))
                .hasValueSatisfying(accion -> assertThat(accion.disponibleHasta()).isEqualTo(AHORA.plusMinutes(10)));
        assertThat(historialEn(AHORA.plusMinutes(11)).accionDeshacible(CAZADOR)).isEmpty();
        assertThat(historialEn(AHORA).recientes(CAZADOR)).singleElement()
                .satisfies(vista -> assertThat(vista.detalle()).isEqualTo("Pizza a Ciegas"));
    }
}
