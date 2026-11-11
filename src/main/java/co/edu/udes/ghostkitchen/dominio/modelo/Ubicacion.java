package co.edu.udes.ghostkitchen.dominio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

/**
 * Lugar donde opera una cocina fantasma. No tiene vida propia: existe solo como
 * parte de una cocina (composicion), por eso es un objeto embebido y no una entidad.
 */
@Embeddable
public class Ubicacion {

    @Column(nullable = false)
    private double latitud;

    @Column(nullable = false)
    private double longitud;

    @Column(nullable = false, length = 60)
    private String barrio;

    protected Ubicacion() {
        // requerido por JPA
    }

    public Ubicacion(double latitud, double longitud, String barrio) {
        Coordenada validada = new Coordenada(latitud, longitud);
        this.latitud = validada.latitud();
        this.longitud = validada.longitud();
        this.barrio = Validar.texto(barrio, "barrio", 60);
    }

    public Coordenada coordenada() {
        return new Coordenada(latitud, longitud);
    }

    public double distanciaKmHasta(Coordenada punto) {
        return coordenada().distanciaKmHasta(punto);
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getBarrio() {
        return barrio;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Ubicacion that)) {
            return false;
        }
        return Double.compare(latitud, that.latitud) == 0
                && Double.compare(longitud, that.longitud) == 0
                && Objects.equals(barrio, that.barrio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitud, longitud, barrio);
    }
}
