package co.edu.udes.ghostkitchen.aplicacion.ranking;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cazador;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.conId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.edu.udes.ghostkitchen.dominio.modelo.Nivel;
import co.edu.udes.ghostkitchen.persistencia.ActividadPorCazador;
import co.edu.udes.ghostkitchen.persistencia.RecompensaPorCazador;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioParticipaciones;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServicioRankingTest {

    @Test
    @DisplayName("PU-021 En el ranking los empates en puntos y cazas comparten puesto (Stub)")
    void clasificacion_ordenaPorPuntosYEmpatesCompartenPuesto() {
        RepositorioCazadores cazadores = mock(RepositorioCazadores.class);
        RepositorioDescubrimientos descubrimientos = mock(RepositorioDescubrimientos.class);
        RepositorioParticipaciones participaciones = mock(RepositorioParticipaciones.class);
        when(cazadores.findAll()).thenReturn(List.of(conId(cazador("brasa_norte"), 1L),
                conId(cazador("humo_azul"), 2L), conId(cazador("lula_bites"), 3L), conId(cazador("sin_cazas"), 4L)));
        when(descubrimientos.actividadPorCazador()).thenReturn(List.of(
                new ActividadPorCazador(1L, 3L, 400L, 0L),
                new ActividadPorCazador(2L, 3L, 380L, 1L),
                new ActividadPorCazador(3L, 5L, 900L, 2L)));
        when(participaciones.recompensasPorCazador()).thenReturn(List.of(new RecompensaPorCazador(1L, 0L)));

        List<PosicionRanking> ranking = new ServicioRanking(cazadores, descubrimientos, participaciones)
                .clasificacion(10);

        assertThat(ranking).extracting(PosicionRanking::alias).containsExactly("lula_bites", "brasa_norte",
                "humo_azul");
        assertThat(ranking).extracting(PosicionRanking::posicion).containsExactly(1, 2, 2);
        assertThat(ranking).extracting(PosicionRanking::puntos).containsExactly(940, 400, 400);
        assertThat(ranking.get(0).nivel()).isEqualTo(Nivel.RASTREADOR);
        assertThat(new ServicioRanking(cazadores, descubrimientos, participaciones).clasificacion(1)).hasSize(1);
    }
}
