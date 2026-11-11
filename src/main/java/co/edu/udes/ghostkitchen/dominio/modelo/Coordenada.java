package co.edu.udes.ghostkitchen.dominio.modelo;

/**
 * Punto geografico inmutable. Sabe calcular la distancia a otro punto con la
 * formula de Haversine, que trata la Tierra como una esfera.
 */
public record Coordenada(double latitud, double longitud) {

    /** Parque Santander, centro de Cucuta (7.88633, -72.50457): referencia cuando el visitante no comparte su ubicacion. */
    public static final Coordenada CENTRO_DE_CUCUTA = new Coordenada(7.8863, -72.5046);

    private static final double RADIO_TIERRA_KM = 6371.0088;

    public Coordenada {
        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90");
        }
        if (longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180");
        }
    }

    public double distanciaKmHasta(Coordenada otra) {
        double deltaLatitud = Math.toRadians(otra.latitud - latitud);
        double deltaLongitud = Math.toRadians(otra.longitud - longitud);
        double semiverseno = Math.pow(Math.sin(deltaLatitud / 2), 2)
                + Math.cos(Math.toRadians(latitud)) * Math.cos(Math.toRadians(otra.latitud))
                * Math.pow(Math.sin(deltaLongitud / 2), 2);
        return 2 * RADIO_TIERRA_KM * Math.asin(Math.sqrt(semiverseno));
    }
}
