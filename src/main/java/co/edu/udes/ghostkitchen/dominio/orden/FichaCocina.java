package co.edu.udes.ghostkitchen.dominio.orden;

import co.edu.udes.ghostkitchen.dominio.modelo.CanalPedido;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * Vista de lectura de una cocina tal como la ve quien explora: datos propios de
 * la cocina mas los que dependen del momento y del visitante (si esta abierta,
 * a que distancia queda, si ya la cazo). Los criterios de orden trabajan sobre ella.
 */
public record FichaCocina(
        Long id,
        String nombre,
        CategoriaCocina categoria,
        Rareza rareza,
        String barrio,
        double latitud,
        double longitud,
        LocalTime horaApertura,
        LocalTime horaCierre,
        Set<CanalPedido> canales,
        LocalDate fechaAlta,
        boolean abierta,
        double calificacionPromedio,
        long totalResenas,
        long vecesCazada,
        double distanciaKm,
        boolean descubierta) {

    public FichaCocina {
        canales = Set.copyOf(canales);
    }

    public boolean tieneResenas() {
        return totalResenas > 0;
    }
}
