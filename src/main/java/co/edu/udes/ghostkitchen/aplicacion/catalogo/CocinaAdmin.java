package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import java.time.LocalTime;

/** Fila del listado de cocinas para el administrador. */
public record CocinaAdmin(Long id, String nombre, CategoriaCocina categoria, Rareza rareza, String barrio,
                          LocalTime horaApertura, LocalTime horaCierre, boolean activa, long vecesCazada) {
}
