package co.edu.udes.ghostkitchen.persistencia;

import co.edu.udes.ghostkitchen.dominio.modelo.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a las cuentas de usuario, sin importar si son cazadores o administradores. */
public interface RepositorioUsuarios extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
