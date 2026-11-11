package co.edu.udes.ghostkitchen.seguridad;

import co.edu.udes.ghostkitchen.aplicacion.cuentas.ServicioCuentas;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Puente entre Spring Security y las cuentas del sistema: busca al usuario por su correo. */
@Service
public class ServicioDetallesUsuario implements UserDetailsService {

    private final ServicioCuentas cuentas;

    public ServicioDetallesUsuario(ServicioCuentas cuentas) {
        this.cuentas = cuentas;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) {
        return cuentas.cuentaPorCorreo(correo)
                .map(CuentaAutenticada::desde)
                .orElseThrow(() -> new UsernameNotFoundException("No existe una cuenta con ese correo"));
    }
}
