package co.edu.udes.ghostkitchen.dominio.modelo;

import java.util.Optional;

/**
 * Rango del cazador segun sus puntos. Es un dato derivado: no tiene transiciones
 * ni reglas propias, por eso basta un enum ordenado y no hace falta el patron State.
 */
public enum Nivel {
    NOVATO(0),
    EXPLORADOR(300),
    RASTREADOR(800),
    CAZADOR_ELITE(1600),
    LEYENDA(3000);

    private final int puntosMinimos;

    Nivel(int puntosMinimos) {
        this.puntosMinimos = puntosMinimos;
    }

    public static Nivel paraPuntos(int puntos) {
        Nivel alcanzado = NOVATO;
        for (Nivel nivel : values()) {
            if (puntos >= nivel.puntosMinimos) {
                alcanzado = nivel;
            }
        }
        return alcanzado;
    }

    public int puntosMinimos() {
        return puntosMinimos;
    }

    public Optional<Nivel> siguiente() {
        int posicion = ordinal() + 1;
        return posicion < values().length ? Optional.of(values()[posicion]) : Optional.empty();
    }

    /** Puntos que faltan para subir; cero si ya se esta en el nivel mas alto. */
    public int puntosParaSubir(int puntos) {
        return siguiente().map(proximo -> Math.max(0, proximo.puntosMinimos - puntos)).orElse(0);
    }

    /** Avance dentro del nivel actual, de 0 a 100, para dibujar la barra de progreso. */
    public int porcentajeHaciaElSiguiente(int puntos) {
        return siguiente()
                .map(proximo -> {
                    int tramo = proximo.puntosMinimos - puntosMinimos;
                    int avance = Math.min(tramo, Math.max(0, puntos - puntosMinimos));
                    return avance * 100 / tramo;
                })
                .orElse(100);
    }
}
