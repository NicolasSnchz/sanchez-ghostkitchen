package co.edu.udes.ghostkitchen.dominio.puntos;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RachaDiariaTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 10);

    @Test
    @DisplayName("PU-003 La racha cuenta dias seguidos hasta ayer y se corta con un dia sin caza")
    void diasConsecutivos_cuentaHastaAyerYSeCortaEnElPrimerHueco() {
        List<LocalDate> dias = List.of(HOY.minusDays(1), HOY.minusDays(2), HOY.minusDays(2), HOY.minusDays(4));

        assertThat(RachaDiaria.diasConsecutivosHasta(dias, HOY)).isEqualTo(2);
    }

    @Test
    void diasConsecutivos_hoyNoCuentaYSinCazaAyerEsCero() {
        assertThat(RachaDiaria.diasConsecutivosHasta(List.of(HOY, HOY.minusDays(2)), HOY)).isZero();
        assertThat(RachaDiaria.diasConsecutivosHasta(List.of(), HOY)).isZero();
    }
}
