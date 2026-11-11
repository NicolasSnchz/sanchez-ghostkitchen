package co.edu.udes.ghostkitchen.dominio.modelo;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cazador;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cocina;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import co.edu.udes.ghostkitchen.dominio.excepcion.RecursoNoEncontradoException;
import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UsuariosEInsigniasTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 11, 1, 10, 0);

    @Test
    void cazador_normalizaCorreoYTieneRolCazador() {
        Cazador cazador = new Cazador("Ana", " ana_23 ", "  Ana@Correo.CO ", "cifrada", FECHA);

        assertThat(cazador.getAlias()).isEqualTo("ana_23");
        assertThat(cazador.getCorreo()).isEqualTo("ana@correo.co");
        assertThat(cazador.rol()).isEqualTo(Rol.CAZADOR);
        assertThat(cazador.getNombre()).isEqualTo("Ana");
        assertThat(cazador.getClaveCifrada()).isEqualTo("cifrada");
        assertThat(cazador.getFechaRegistro()).isEqualTo(FECHA);
        assertThat(new Administrador("Admin", "admin@correo.co", "cifrada", FECHA).rol()).isEqualTo(Rol.ADMIN);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "nombre con espacio", "\u00f1andu", "este_alias_es_demasiado_largo"})
    void alias_conFormatoInvalido_seRechaza(String alias) {
        assertThatIllegalArgumentException().isThrownBy(() -> Cazador.validarAlias(alias));
    }

    @Test
    @DisplayName("PU-010 El progreso resume las cazas y cada insignia aplica su propia regla")
    void insignias_seMerecenSegunElProgreso() {
        CocinaFantasma legendaria = cocina().nombre("Garbanzo Nocturno").rareza(Rareza.LEGENDARIA)
                .categoria(CategoriaCocina.TIPICA).ubicacion(7.90, -72.51, "El Contento")
                .horario(LocalTime.of(22, 0), LocalTime.of(5, 0)).build();
        Descubrimiento nocturno = Descubrimiento.registrar(cazador("noctambula"), legendaria,
                FECHA.withHour(23), 375);
        nocturno.resenar(5, "", FECHA.withHour(23).plusMinutes(30));
        Descubrimiento diurno = Descubrimiento.registrar(cazador("noctambula"), cocina().build(), FECHA, 100);

        ProgresoCazador progreso = ProgresoCazador.desde(List.of(nocturno, diurno), 1);

        assertThat(progreso).isEqualTo(new ProgresoCazador(2, 2, 2, 1, 1, 1, 1));
        assertThat(TipoInsignia.PRIMER_HALLAZGO.laMerece(progreso)).isTrue();
        assertThat(TipoInsignia.NOCTAMBULO.laMerece(progreso)).isTrue();
        assertThat(TipoInsignia.LEYENDA_LOCAL.laMerece(progreso)).isTrue();
        assertThat(TipoInsignia.RETADOR.laMerece(progreso)).isTrue();
        assertThat(TipoInsignia.RASTREADOR.laMerece(progreso)).isFalse();
        assertThat(TipoInsignia.CAZADOR_EXPERTO.laMerece(progreso)).isFalse();
        assertThat(TipoInsignia.PALADAR_VARIADO.laMerece(progreso)).isFalse();
        assertThat(TipoInsignia.EXPLORADOR_DE_BARRIOS.laMerece(progreso)).isFalse();
        assertThat(TipoInsignia.CRITICO_CULINARIO.laMerece(progreso)).isFalse();
        assertThat(TipoInsignia.PRIMER_HALLAZGO.laMerece(ProgresoCazador.SIN_ACTIVIDAD)).isFalse();
    }

    @Test
    void insigniaObtenida_guardaTipoYFecha() {
        InsigniaObtenida insignia = new InsigniaObtenida(cazador("humo_azul"), TipoInsignia.RETADOR, FECHA);

        assertThat(insignia.getTipo()).isEqualTo(TipoInsignia.RETADOR);
        assertThat(insignia.getFechaObtencion()).isEqualTo(FECHA);
        assertThat(insignia.getCazador().getAlias()).isEqualTo("humo_azul");
        assertThat(insignia.getId()).isNull();
    }

    @Test
    void excepciones_conservanSusDatos() {
        ReglaNegocioException regla = new ReglaNegocioException("caza.limite-diario", 5);
        RecursoNoEncontradoException ausente = new RecursoNoEncontradoException("Cocina", 99L);

        assertThat(regla.getCodigo()).isEqualTo("caza.limite-diario");
        assertThat(regla.getArgumentos()).containsExactly(5);
        assertThat(ausente.getRecurso()).isEqualTo("Cocina");
        assertThat(ausente.getIdentificador()).isEqualTo(99L);
        assertThat(ausente).hasMessageContaining("99");
    }
}
