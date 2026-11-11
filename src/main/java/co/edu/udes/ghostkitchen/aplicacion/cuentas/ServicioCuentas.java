package co.edu.udes.ghostkitchen.aplicacion.cuentas;

import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.Usuario;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioUsuarios;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Registro de cazadores y consulta de cuentas para iniciar sesion. */
@Service
@Transactional(readOnly = true)
public class ServicioCuentas {

    private final RepositorioUsuarios usuarios;
    private final RepositorioCazadores cazadores;
    private final PasswordEncoder cifrador;
    private final Clock reloj;

    public ServicioCuentas(RepositorioUsuarios usuarios, RepositorioCazadores cazadores,
                           PasswordEncoder cifrador, Clock reloj) {
        this.usuarios = usuarios;
        this.cazadores = cazadores;
        this.cifrador = cifrador;
        this.reloj = reloj;
    }

    @Transactional
    public Long registrarCazador(NuevaCuenta cuenta) {
        String correo = Usuario.normalizarCorreo(cuenta.correo());
        String alias = Cazador.validarAlias(cuenta.alias());
        if (usuarios.existsByCorreo(correo)) {
            throw new ReglaNegocioException("cuenta.correo-en-uso");
        }
        if (cazadores.existsByAliasIgnoreCase(alias)) {
            throw new ReglaNegocioException("cuenta.alias-en-uso");
        }
        Cazador cazador = new Cazador(cuenta.nombre(), alias, correo, cifrador.encode(cuenta.clave()),
                LocalDateTime.now(reloj));
        return cazadores.save(cazador).getId();
    }

    public Optional<CuentaUsuario> cuentaPorCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return Optional.empty();
        }
        return usuarios.findByCorreo(Usuario.normalizarCorreo(correo)).map(ServicioCuentas::aCuenta);
    }

    public Optional<CuentaUsuario> cuentaPorId(Long usuarioId) {
        return usuarios.findById(usuarioId).map(ServicioCuentas::aCuenta);
    }

    private static CuentaUsuario aCuenta(Usuario usuario) {
        String alias = usuario instanceof Cazador cazador ? cazador.getAlias() : null;
        return new CuentaUsuario(usuario.getId(), usuario.getNombre(), alias, usuario.getCorreo(),
                usuario.getClaveCifrada(), usuario.rol());
    }
}
