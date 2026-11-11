package co.edu.udes.ghostkitchen.aplicacion.progreso;

import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import java.time.LocalDateTime;

/**
 * Una caza del historial del cazador, lista para mostrar. Indica si la cocina
 * sigue activa para no enlazar a una ficha que ya no esta en el catalogo.
 */
public record CazaVista(
        Long cocinaId,
        String nombreCocina,
        CategoriaCocina categoria,
        Rareza rareza,
        String barrio,
        LocalDateTime fechaHora,
        int puntos,
        boolean conResena,
        boolean enHoraFantasma,
        boolean cocinaActiva) {
}
