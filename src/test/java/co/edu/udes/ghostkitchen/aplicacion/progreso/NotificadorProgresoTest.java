package co.edu.udes.ghostkitchen.aplicacion.progreso;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.AHORA;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.relojEn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

/** Sujeto del patron Observer. */
class NotificadorProgresoTest {

    @Test
    @DisplayName("PU-018 El notificador avisa a cada observador en orden y junta sus novedades")
    void notificar_avisaEnOrdenYConcatenaNovedades() {
        ObservadorProgreso retos = mock(ObservadorProgreso.class);
        ObservadorProgreso insignias = mock(ObservadorProgreso.class);
        ObservadorProgreso nivel = mock(ObservadorProgreso.class);
        Novedad reto = new Novedad(TipoNovedad.RETO_COMPLETADO, "Doble o nada", 120);
        Novedad subida = Novedad.de(TipoNovedad.NIVEL_SUBIDO, "EXPLORADOR");
        when(retos.alCambiarProgreso(any())).thenReturn(List.of(reto));
        when(insignias.alCambiarProgreso(any())).thenReturn(List.of());
        when(nivel.alCambiarProgreso(any())).thenReturn(List.of(subida));
        NotificadorProgreso notificador = new NotificadorProgreso(List.of(retos, insignias, nivel), relojEn(AHORA));

        List<Novedad> novedades = notificador.notificar(1L, 280);

        InOrder orden = inOrder(retos, insignias, nivel);
        ArgumentCaptor<EventoProgreso> evento = ArgumentCaptor.forClass(EventoProgreso.class);
        orden.verify(retos).alCambiarProgreso(evento.capture());
        orden.verify(insignias).alCambiarProgreso(any());
        orden.verify(nivel).alCambiarProgreso(any());
        assertThat(novedades).containsExactly(reto, subida);
        assertThat(evento.getValue()).isEqualTo(new EventoProgreso(1L, 280, AHORA));
        assertThat(notificador.cantidadDeObservadores()).isEqualTo(3);
    }
}
