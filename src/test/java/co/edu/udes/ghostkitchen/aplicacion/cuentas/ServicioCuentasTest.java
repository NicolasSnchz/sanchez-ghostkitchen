package co.edu.udes.ghostkitchen.aplicacion.cuentas;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.AHORA;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cazador;
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

import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.Rol;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioUsuarios;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

class ServicioCuentasTest {

    private final RepositorioUsuarios usuarios = mock(RepositorioUsuarios.class);
    private final RepositorioCazadores cazadores = mock(RepositorioCazadores.class);
    private final PasswordEncoder cifrador = mock(PasswordEncoder.class);
    private final ServicioCuentas cuentas = new ServicioCuentas(usuarios, cazadores, cifrador, relojEn(AHORA));

    @Test
    @DisplayName("PU-022 No se registra un correo repetido y la clave ni se cifra (Dummy)")
    void registrarCazador_correoRepetido_rechazaAntesDeCifrar() {
        when(usuarios.existsByCorreo("ana@correo.co")).thenReturn(true);

        assertThatThrownBy(() -> cuentas.registrarCazador(new NuevaCuenta("Ana", "ana_23", " ANA@correo.co", "clave123")))
                .hasMessage("cuenta.correo-en-uso");
        verifyNoInteractions(cifrador);
        verify(cazadores, never()).save(any());
    }

    @Test
    void registrarCazador_aliasRepetido_rechaza() {
        when(cazadores.existsByAliasIgnoreCase("ana_23")).thenReturn(true);

        assertThatThrownBy(() -> cuentas.registrarCazador(new NuevaCuenta("Ana", "ana_23", "ana@correo.co", "clave123")))
                .hasMessage("cuenta.alias-en-uso");
    }

    @Test
    void registrarCazador_valido_guardaLaClaveCifrada() {
        when(cifrador.encode("clave123")).thenReturn("cifrada");
        when(cazadores.save(any(Cazador.class))).thenAnswer(invocacion -> conId(invocacion.getArgument(0), 9L));

        Long id = cuentas.registrarCazador(new NuevaCuenta("Ana", "ana_23", "Ana@Correo.co", "clave123"));

        ArgumentCaptor<Cazador> guardado = ArgumentCaptor.forClass(Cazador.class);
        verify(cazadores).save(guardado.capture());
        assertThat(id).isEqualTo(9L);
        assertThat(guardado.getValue().getClaveCifrada()).isEqualTo("cifrada");
        assertThat(guardado.getValue().getCorreo()).isEqualTo("ana@correo.co");
        assertThat(guardado.getValue().getFechaRegistro()).isEqualTo(AHORA);
    }

    @Test
    void cuentaPorCorreo_devuelveAliasYRolYOcultaLaClaveAlImprimir() {
        when(usuarios.findByCorreo("ana@correo.co")).thenReturn(Optional.of(conId(cazador("ana_23"), 9L)));

        CuentaUsuario cuenta = cuentas.cuentaPorCorreo(" Ana@Correo.co ").orElseThrow();

        assertThat(cuenta.alias()).isEqualTo("ana_23");
        assertThat(cuenta.rol()).isEqualTo(Rol.CAZADOR);
        assertThat(cuenta.toString()).doesNotContain("clave-cifrada");
        assertThat(cuentas.cuentaPorCorreo(" ")).isEmpty();
        assertThat(new NuevaCuenta("Ana", "ana_23", "ana@correo.co", "secreta1").toString()).doesNotContain("secreta1");
    }
}
