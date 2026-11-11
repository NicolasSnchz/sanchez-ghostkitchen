package co.edu.udes.ghostkitchen.dominio.modelo;

/**
 * Que tan dificil es dar con una cocina. Cada rareza conoce sus puntos base,
 * asi ningun otro punto del sistema necesita un switch sobre este tipo.
 */
public enum Rareza {
    COMUN(100),
    RARA(150),
    LEGENDARIA(250);

    private final int puntosBase;

    Rareza(int puntosBase) {
        this.puntosBase = puntosBase;
    }

    public int puntosBase() {
        return puntosBase;
    }
}
