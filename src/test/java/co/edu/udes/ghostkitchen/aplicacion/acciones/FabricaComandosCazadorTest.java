package co.edu.udes.ghostkitchen.aplicacion.acciones;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.AHORA;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cazador;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.conId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.udes.ghostkitchen.aplicacion.retos.RegistroParticipaciones;
import co.edu.udes.ghostkitchen.dominio.modelo.AccionRegistrada;
import co.edu.udes.ghostkitchen.dominio.modelo.Reto;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;
import co.edu.udes.ghostkitchen.dominio.puntos.Puntaje;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class FabricaComandosCazadorTest {

    private final RegistroCaza registroCaza = mock(RegistroCaza.class);
    private final RegistroResenas registroResenas = mock(RegistroResenas.class);
    private final RegistroParticipaciones registroParticipaciones = mock(RegistroParticipaciones.class);
    private final FabricaComandos fabrica =
            new FabricaComandosCazador(registroCaza, registroResenas, registroParticipaciones);

    @ParameterizedTest
    @EnumSource(TipoAccion.class)
    @DisplayName("PU-017 El metodo fabrica reconstruye desde el historial un comando del mismo tipo")
    void paraDeshacer_reconstruyeElComandoDelTipoRegistrado(TipoAccion tipo) {
        AccionRegistrada accion = new AccionRegistrada(conId(cazador("humo_azul"), 1L), tipo, 7L, "Detalle", 0,
                AHORA);

        Comando comando = fabrica.paraDeshacer(accion);

        assertThat(comando.tipo()).isEqualTo(tipo);
        assertThat(comando.cazadorId()).isEqualTo(1L);
    }

    @Test
    void cadaComandoDelegaEnSuReceptor() {
        when(registroCaza.descubrir(1L, 7L)).thenReturn(new CazaRegistrada(7L, "Pizza a Ciegas", Puntaje.base(100)));
        when(registroResenas.publicar(1L, 7L, new NuevaResena(5, "Buena"))).thenReturn("Pizza a Ciegas");
        Reto reto = conId(new Reto("Doble o nada", "Caza dos cocinas.", 2, 120, AHORA.toLocalDate(),
                AHORA.toLocalDate().plusDays(2)), 3L);
        when(registroParticipaciones.inscribir(1L, 3L)).thenReturn(reto);

        assertThat(fabrica.descubrirCocina(1L, 7L).ejecutar().puntos()).isEqualTo(100);
        assertThat(fabrica.publicarResena(1L, 7L, new NuevaResena(5, "Buena")).ejecutar().puntos()).isEqualTo(20);
        assertThat(fabrica.unirseAReto(1L, 3L).ejecutar().detalle()).isEqualTo("Doble o nada");

        fabrica.descubrirCocina(1L, 7L).deshacer();
        fabrica.publicarResena(1L, 7L, new NuevaResena(5, "Buena")).deshacer();
        fabrica.unirseAReto(1L, 3L).deshacer();
        verify(registroCaza).olvidar(1L, 7L);
        verify(registroResenas).retirar(1L, 7L);
        verify(registroParticipaciones).retirar(1L, 3L);
    }

    @Test
    void resenaReconstruida_soloSirveParaDeshacer() {
        AccionRegistrada accion = new AccionRegistrada(conId(cazador("humo_azul"), 1L), TipoAccion.PUBLICAR_RESENA,
                7L, "Pizza a Ciegas", 20, AHORA);

        assertThatIllegalStateException().isThrownBy(() -> fabrica.paraDeshacer(accion).ejecutar());
    }
}
