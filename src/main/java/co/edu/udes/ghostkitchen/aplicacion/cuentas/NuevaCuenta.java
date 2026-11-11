package co.edu.udes.ghostkitchen.aplicacion.cuentas;

/** Datos que entrega quien se registra como cazador. La clave llega en texto plano y nunca se guarda asi. */
public record NuevaCuenta(String nombre, String alias, String correo, String clave) {

    @Override
    public String toString() {
        return "NuevaCuenta[nombre=" + nombre + ", alias=" + alias + ", correo=" + correo + ", clave=***]";
    }
}
