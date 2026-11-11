package co.edu.udes.ghostkitchen.aplicacion.logros;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.AHORA;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cazador;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.conId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.udes.ghostkitchen.aplicacion.progreso.EventoProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.Novedad;
import co.edu.udes.ghostkitchen.aplicacion.progreso.ServicioProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.TipoNovedad;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.InsigniaObtenida;
import co.edu.udes.ghostkitchen.dominio.modelo.Nivel;
import co.edu.udes.ghostkitchen.dominio.modelo.ProgresoCazador;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoInsignia;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioInsignias;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Observadores concretos de insignias y de nivel. */
class SincronizadorInsigniasTest {

    private final ServicioProgreso progreso = mock(ServicioProgreso.class);
    private final RepositorioInsignias insignias = mock(RepositorioInsignias.class);
    private final RepositorioCazadores cazadores = mock(RepositorioCazadores.class);
    private final Cazador humoAzul = conId(cazador("humo_azul"), 1L);

    @Test
    @DisplayName("PU-020 Tras un cambio se otorgan las insignias nuevas y se retiran las que ya no se merecen")
    void alCambiarProgreso_otorgaYRetiraSegunElProgresoActual() {
        InsigniaObtenida retador = new InsigniaObtenida(humoAzul, TipoInsignia.RETADOR, AHORA.minusDays(1));
        when(insignias.findByCazadorId(1L)).thenReturn(List.of(retador));
        when(progreso.progresoDe(1L)).thenReturn(new ProgresoCazador(1, 1, 1, 0, 0, 0, 0));
        when(cazadores.getReferenceById(1L)).thenReturn(humoAzul);

        List<Novedad> novedades = new SincronizadorInsignias(progreso, insignias, cazadores)
                .alCambiarProgreso(new EventoProgreso(1L, 0, AHORA));

        verify(insignias).delete(retador);
        ArgumentCaptor<InsigniaObtenida> nueva = ArgumentCaptor.forClass(InsigniaObtenida.class);
        verify(insignias).save(nueva.capture());
        assertThat(nueva.getValue().getTipo()).isEqualTo(TipoInsignia.PRIMER_HALLAZGO);
        assertThat(novedades).containsExactly(
                Novedad.de(TipoNovedad.INSIGNIA_RETIRADA, "RETADOR"),
                Novedad.de(TipoNovedad.INSIGNIA_GANADA, "PRIMER_HALLAZGO"));
    }

    @Test
    void detectorDeNivel_reportaSubidasYBajadas() {
        DetectorCambioNivel detector = new DetectorCambioNivel(progreso);
        when(progreso.nivelDe(1L)).thenReturn(Nivel.EXPLORADOR);

        assertThat(detector.alCambiarProgreso(new EventoProgreso(1L, 250, AHORA)))
                .containsExactly(Novedad.de(TipoNovedad.NIVEL_SUBIDO, "EXPLORADOR"));
        assertThat(detector.alCambiarProgreso(new EventoProgreso(1L, 900, AHORA)))
                .containsExactly(Novedad.de(TipoNovedad.NIVEL_BAJADO, "EXPLORADOR"));
        assertThat(detector.alCambiarProgreso(new EventoProgreso(1L, 400, AHORA))).isEmpty();
    }

    @Test
    void servicioInsignias_listaLasNueveConLasObtenidasMarcadas() {
        when(insignias.findByCazadorId(1L))
                .thenReturn(List.of(new InsigniaObtenida(humoAzul, TipoInsignia.NOCTAMBULO, AHORA)));

        List<InsigniaVista> vistas = new ServicioInsignias(insignias).insigniasDe(1L);

        assertThat(vistas).hasSize(TipoInsignia.values().length);
        assertThat(vistas).filteredOn(InsigniaVista::obtenida).singleElement()
                .satisfies(vista -> assertThat(vista.tipo()).isEqualTo(TipoInsignia.NOCTAMBULO));
    }
}
