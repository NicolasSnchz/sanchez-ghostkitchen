package co.edu.udes.ghostkitchen.aplicacion.acciones;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.AHORA;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cazador;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cocina;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.conId;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.relojEn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import co.edu.udes.ghostkitchen.dominio.excepcion.RecursoNoEncontradoException;
import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Descubrimiento;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import co.edu.udes.ghostkitchen.dominio.puntos.BonoPrimerCazador;
import co.edu.udes.ghostkitchen.dominio.puntos.BonoRachaDiaria;
import co.edu.udes.ghostkitchen.dominio.puntos.CalculadoraPuntos;
import co.edu.udes.ghostkitchen.dominio.puntos.MultiplicadorHoraFantasma;
import co.edu.udes.ghostkitchen.dominio.puntos.PuntosBasePorRareza;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Receptor del comando de cazar: reglas de la caza y calculo de puntos. */
class RegistroCazaTest {

    private static final long CAZADOR = 1L;
    private static final long COCINA = 7L;

    private final RepositorioCocinas cocinas = mock(RepositorioCocinas.class);
    private final RepositorioCazadores cazadores = mock(RepositorioCazadores.class);
    private final RepositorioDescubrimientos descubrimientos = mock(RepositorioDescubrimientos.class);
    private CocinaFantasma pizzeria;

    @BeforeEach
    void prepararCocinaAbierta() {
        pizzeria = conId(cocina().rareza(Rareza.RARA).build(), COCINA);
        when(cocinas.findById(COCINA)).thenReturn(Optional.of(pizzeria));
        when(cazadores.getReferenceById(CAZADOR)).thenReturn(conId(cazador("humo_azul"), CAZADOR));
    }

    private RegistroCaza registro(CalculadoraPuntos calculadora) {
        return new RegistroCaza(cocinas, cazadores, descubrimientos, calculadora, relojEn(AHORA));
    }

    @Test
    @DisplayName("PU-012 Una cocina cerrada no se puede cazar y la calculadora ni se usa (Stub y Dummy)")
    void descubrir_cocinaCerrada_rechazaSinCalcularPuntos() {
        pizzeria.actualizar(cocina().rareza(Rareza.RARA).horario(LocalTime.of(6, 0), LocalTime.of(14, 0)));
        CalculadoraPuntos dummy = mock(CalculadoraPuntos.class);

        assertThatThrownBy(() -> registro(dummy).descubrir(CAZADOR, COCINA))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("caza.cocina-cerrada");
        verifyNoInteractions(dummy);
        verify(descubrimientos, never()).save(any());
    }

    @Test
    @DisplayName("PU-013 Al llegar a cinco cazas en el dia se rechaza la sexta (Stub)")
    void descubrir_conCincoCazasHoy_rechazaPorLimiteDiario() {
        when(descubrimientos.cazasDelDia(CAZADOR, AHORA.toLocalDate())).thenReturn(5L);

        assertThatThrownBy(() -> registro(mock(CalculadoraPuntos.class)).descubrir(CAZADOR, COCINA))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("caza.limite-diario");
    }

    @Test
    void descubrir_cocinaRepetidaOInactivaOInexistente_rechaza() {
        when(descubrimientos.existsByCazadorIdAndCocinaId(CAZADOR, COCINA)).thenReturn(true);
        assertThatThrownBy(() -> registro(mock(CalculadoraPuntos.class)).descubrir(CAZADOR, COCINA))
                .hasMessage("caza.repetida");

        pizzeria.desactivar();
        assertThatThrownBy(() -> registro(mock(CalculadoraPuntos.class)).descubrir(CAZADOR, COCINA))
                .hasMessage("caza.cocina-inactiva");

        assertThatThrownBy(() -> registro(mock(CalculadoraPuntos.class)).descubrir(CAZADOR, 99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void descubrir_valido_guardaConLosPuntosDeLaCadena() {
        when(descubrimientos.fechasDeCazaDe(CAZADOR)).thenReturn(List.of(AHORA.minusDays(1)));
        CalculadoraPuntos cadena = new MultiplicadorHoraFantasma(
                new BonoRachaDiaria(new BonoPrimerCazador(new PuntosBasePorRareza())));

        CazaRegistrada caza = registro(cadena).descubrir(CAZADOR, COCINA);

        ArgumentCaptor<Descubrimiento> guardado = ArgumentCaptor.forClass(Descubrimiento.class);
        verify(descubrimientos).save(guardado.capture());
        assertThat(caza.puntaje().total()).isEqualTo(150 + 50 + 10);
        assertThat(guardado.getValue().getPuntosObtenidos()).isEqualTo(210);
        assertThat(guardado.getValue().getFechaHora()).isEqualTo(AHORA);
        assertThat(caza.nombreCocina()).isEqualTo("Pizza a Ciegas");
    }

    @Test
    void olvidar_borraElDescubrimientoSiExiste() {
        Descubrimiento existente = Descubrimiento.registrar(cazador("humo_azul"), pizzeria, AHORA, 150);
        when(descubrimientos.findByCazadorIdAndCocinaId(CAZADOR, COCINA)).thenReturn(Optional.of(existente));

        registro(mock(CalculadoraPuntos.class)).olvidar(CAZADOR, COCINA);

        verify(descubrimientos).delete(existente);
    }
}
