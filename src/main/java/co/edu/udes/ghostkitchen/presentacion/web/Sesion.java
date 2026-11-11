package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.seguridad.CuentaAutenticada;

/** Utilidades para leer quien esta navegando. */
final class Sesion {

    private Sesion() {
    }

    /**
     * Identificador del cazador en sesion, o null si es un visitante o un
     * administrador: los dos ven el catalogo pero no juegan.
     */
    static Long cazadorId(CuentaAutenticada cuenta) {
        return cuenta != null && cuenta.esCazador() ? cuenta.id() : null;
    }
}
