package co.edu.udes.ghostkitchen.soporte;

import co.edu.udes.ghostkitchen.dominio.modelo.CanalPedido;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import co.edu.udes.ghostkitchen.dominio.orden.FichaCocina;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Datos de prueba reutilizables (patron Test Data Builder). Cada prueba parte de
 * un ejemplo valido y cambia solo lo que le interesa verificar.
 */
public final class Ejemplos {

    public static final ZoneId ZONA = ZoneId.of("America/Bogota");
    /** Martes 10 de noviembre de 2026, 9:30 p. m.: noche, pero antes de la hora fantasma. */
    public static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 10, 21, 30);

    private Ejemplos() {
    }

    /** Reloj falso (Fake): el tiempo no avanza y cada prueba es repetible. */
    public static Clock relojEn(LocalDateTime momento) {
        return Clock.fixed(momento.atZone(ZONA).toInstant(), ZONA);
    }

    public static CocinaFantasma.Builder cocina() {
        return CocinaFantasma.builder()
                .nombre("Pizza a Ciegas")
                .descripcion("Pizza de masa madre que se pide por mensaje.")
                .categoria(CategoriaCocina.PIZZA)
                .rareza(Rareza.COMUN)
                .horario(LocalTime.of(12, 0), LocalTime.of(23, 0))
                .ubicacion(7.8977, -72.4930, "La Riviera")
                .canal(CanalPedido.RAPPI)
                .registradaEl(LocalDate.of(2026, 9, 1));
    }

    public static Cazador cazador(String alias) {
        return new Cazador("Cazador " + alias, alias, alias + "@correo.co", "clave-cifrada",
                LocalDateTime.of(2026, 9, 1, 10, 0));
    }

    /** Asigna el identificador que normalmente pone la base de datos. */
    public static <T> T conId(T entidad, long id) {
        ReflectionTestUtils.setField(entidad, "id", id);
        return entidad;
    }

    public static FichaCocina ficha(String nombre, double calificacion, long resenas, long cazas, double distancia,
                                    LocalDate fechaAlta) {
        return new FichaCocina(1L, nombre, CategoriaCocina.PIZZA, Rareza.COMUN, "Centro", 7.88, -72.50,
                LocalTime.NOON, LocalTime.MIDNIGHT, Set.of(CanalPedido.RAPPI), fechaAlta, true, calificacion,
                resenas, cazas, distancia, false);
    }
}
