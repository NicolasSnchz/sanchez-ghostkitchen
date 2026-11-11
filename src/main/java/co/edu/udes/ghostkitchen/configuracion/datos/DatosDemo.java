package co.edu.udes.ghostkitchen.configuracion.datos;

import co.edu.udes.ghostkitchen.dominio.modelo.CanalPedido;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Estructura del archivo datos-demo.json. Las fechas son relativas al dia de
 * arranque, asi la demostracion siempre tiene retos vigentes y cazas recientes.
 */
record DatosDemo(AdministradorDemo administrador, List<CocinaDemo> cocinas, List<RetoDemo> retos,
                 List<CazadorDemo> cazadores) {

    record AdministradorDemo(String nombre, String correo, String clave) {
    }

    record CocinaDemo(String nombre, String descripcion, CategoriaCocina categoria, Rareza rareza,
                      LocalTime apertura, LocalTime cierre, double latitud, double longitud, String barrio,
                      Set<CanalPedido> canales, int diasDesdeAlta, boolean activa) {
    }

    record RetoDemo(String titulo, String descripcion, int metaCocinas, int puntosRecompensa,
                    int inicioRelativo, int finRelativo) {
    }

    record CazadorDemo(String nombre, String alias, String correo, String clave, int diasDesdeRegistro,
                       List<InscripcionDemo> retos, List<CazaDemo> cazas) {
    }

    record InscripcionDemo(String titulo, int diasAtras) {
    }

    record CazaDemo(String cocina, int diasAtras, LocalTime hora, ResenaDemo resena) {
    }

    record ResenaDemo(int calificacion, String comentario) {
    }
}
