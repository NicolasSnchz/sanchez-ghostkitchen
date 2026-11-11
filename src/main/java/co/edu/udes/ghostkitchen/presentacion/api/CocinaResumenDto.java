package co.edu.udes.ghostkitchen.presentacion.api;

import co.edu.udes.ghostkitchen.dominio.modelo.CanalPedido;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import co.edu.udes.ghostkitchen.dominio.orden.FichaCocina;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.Set;
import java.util.TreeSet;

/** Representacion publica de una cocina en la API. No expone entidades ni datos de cazadores. */
public record CocinaResumenDto(
        Long id,
        String nombre,
        CategoriaCocina categoria,
        Rareza rareza,
        String barrio,
        double latitud,
        double longitud,
        @JsonFormat(pattern = "HH:mm") LocalTime horaApertura,
        @JsonFormat(pattern = "HH:mm") LocalTime horaCierre,
        boolean abierta,
        Set<CanalPedido> canales,
        double calificacionPromedio,
        long totalResenas,
        long vecesCazada,
        double distanciaKm) {

    static CocinaResumenDto desde(FichaCocina ficha) {
        return new CocinaResumenDto(ficha.id(), ficha.nombre(), ficha.categoria(), ficha.rareza(), ficha.barrio(),
                ficha.latitud(), ficha.longitud(), ficha.horaApertura(), ficha.horaCierre(), ficha.abierta(),
                new TreeSet<>(ficha.canales()), redondear(ficha.calificacionPromedio(), 1), ficha.totalResenas(),
                ficha.vecesCazada(), redondear(ficha.distanciaKm(), 2));
    }

    static double redondear(double valor, int decimales) {
        double escala = Math.pow(10, decimales);
        return Math.round(valor * escala) / escala;
    }
}
