package co.edu.udes.ghostkitchen.dominio.modelo;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cazador;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cocina;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DescubrimientoTest {

    private static final LocalDateTime NOCHE = LocalDateTime.of(2026, 11, 10, 23, 15);

    private final Descubrimiento descubrimiento =
            Descubrimiento.registrar(cazador("humo_azul"), cocina().build(), NOCHE, 150);

    @Test
    @DisplayName("PU-007 Una cocina descubierta solo admite una resena")
    void resenar_dosVeces_rechazaLaSegunda() {
        descubrimiento.resenar(5, "Muy buena", NOCHE.plusHours(1));

        assertThatThrownBy(() -> descubrimiento.resenar(4, "Otra", NOCHE.plusHours(2)))
                .isInstanceOf(ReglaNegocioException.class)
                .extracting(error -> ((ReglaNegocioException) error).getCodigo())
                .isEqualTo("resena.duplicada");
    }

    @Test
    void resenar_guardaLaResenaYSePuedeRetirar() {
        Resena resena = descubrimiento.resenar(4, "  Llego caliente  ", NOCHE.plusHours(1));

        assertThat(descubrimiento.tieneResena()).isTrue();
        assertThat(descubrimiento.resena()).containsSame(resena);
        assertThat(resena.getComentario()).isEqualTo("Llego caliente");
        assertThat(resena.getCalificacion()).isEqualTo(4);
        assertThat(resena.getFecha()).isEqualTo(NOCHE.plusHours(1));

        descubrimiento.retirarResena();
        assertThat(descubrimiento.resena()).isEmpty();
    }

    @Test
    void resena_invalida_seRechaza() {
        assertThatIllegalArgumentException().isThrownBy(() -> descubrimiento.resenar(0, "", NOCHE));
        assertThatIllegalArgumentException().isThrownBy(() -> descubrimiento.resenar(6, "", NOCHE));
        assertThatIllegalArgumentException().isThrownBy(() -> descubrimiento.resenar(3, "x".repeat(301), NOCHE));
        assertThat(descubrimiento.resenar(3, null, NOCHE).getComentario()).isEmpty();
    }

    @Test
    void horaFantasma_seCalculaConLaHoraDeLaCaza() {
        assertThat(descubrimiento.ocurrioEnHoraFantasma()).isTrue();
        assertThat(Descubrimiento.registrar(cazador("lula_bites"), cocina().build(), NOCHE.withHour(12), 100)
                .ocurrioEnHoraFantasma()).isFalse();
        assertThat(descubrimiento.getPuntosObtenidos()).isEqualTo(150);
        assertThat(descubrimiento.getFechaHora()).isEqualTo(NOCHE);
        assertThat(descubrimiento.getCazador().getAlias()).isEqualTo("humo_azul");
        assertThat(descubrimiento.getCocina().getNombre()).isEqualTo("Pizza a Ciegas");
    }

    @Test
    void puntosNegativosODatosFaltantes_seRechazan() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Descubrimiento.registrar(cazador("humo_azul"), cocina().build(), NOCHE, -1));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Descubrimiento.registrar(null, cocina().build(), NOCHE, 10));
    }

    @Test
    void horaFantasma_limites() {
        assertThat(HoraFantasma.incluye(HoraFantasma.INICIO)).isTrue();
        assertThat(HoraFantasma.incluye(HoraFantasma.FIN.minusMinutes(1))).isTrue();
        assertThat(HoraFantasma.incluye(HoraFantasma.FIN)).isFalse();
        assertThat(HoraFantasma.incluye(HoraFantasma.INICIO.minusMinutes(1))).isFalse();
    }
}
