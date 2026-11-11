package co.edu.udes.ghostkitchen.seguridad;

import co.edu.udes.ghostkitchen.aplicacion.cuentas.CuentaUsuario;
import co.edu.udes.ghostkitchen.dominio.modelo.Rol;
import java.io.Serial;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Usuario con sesion iniciada. Los controladores lo reciben con @AuthenticationPrincipal. */
public record CuentaAutenticada(Long id, String nombre, String alias, String correo, String claveCifrada, Rol rol)
        implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    public static CuentaAutenticada desde(CuentaUsuario cuenta) {
        return new CuentaAutenticada(cuenta.id(), cuenta.nombre(), cuenta.alias(), cuenta.correo(),
                cuenta.claveCifrada(), cuenta.rol());
    }

    public boolean esCazador() {
        return rol == Rol.CAZADOR;
    }

    /** Lo que se muestra en la cabecera: el alias del cazador o el nombre del administrador. */
    public String getNombreVisible() {
        return alias != null ? alias : nombre;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return claveCifrada;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "CuentaAutenticada[id=" + id + ", correo=" + correo + ", rol=" + rol + "]";
    }
}
