package co.edu.udes.ghostkitchen.aplicacion.cuentas;

import co.edu.udes.ghostkitchen.dominio.modelo.Rol;

/** Datos de una cuenta que necesita la seguridad para autenticar y autorizar. */
public record CuentaUsuario(Long id, String nombre, String alias, String correo, String claveCifrada, Rol rol) {

    @Override
    public String toString() {
        return "CuentaUsuario[id=" + id + ", correo=" + correo + ", rol=" + rol + "]";
    }
}
