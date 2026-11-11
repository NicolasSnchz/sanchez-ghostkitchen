package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import java.time.LocalDateTime;

/** Una caza reciente de cualquier cazador, para la portada. */
public record CazaReciente(String alias, Long cocinaId, String nombreCocina, CategoriaCocina categoria,
                           String barrio, LocalDateTime fechaHora, int puntos) {
}
