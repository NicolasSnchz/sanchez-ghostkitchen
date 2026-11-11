package co.edu.udes.ghostkitchen.dominio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Opinion de un cazador sobre una cocina que ya descubrio. Solo existe dentro de
 * un {@link Descubrimiento}: si el descubrimiento desaparece, la resena tambien.
 */
@Entity
@Table(name = "resenas")
public class Resena {

    public static final int PUNTOS_POR_RESENA = 20;
    public static final int CALIFICACION_MINIMA = 1;
    public static final int CALIFICACION_MAXIMA = 5;
    public static final int LONGITUD_MAXIMA_COMENTARIO = 300;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int calificacion;

    @Column(nullable = false, length = LONGITUD_MAXIMA_COMENTARIO)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    protected Resena() {
        // requerido por JPA
    }

    Resena(int calificacion, String comentario, LocalDateTime fecha) {
        this.calificacion = Validar.enRango(calificacion, CALIFICACION_MINIMA, CALIFICACION_MAXIMA, "calificacion");
        this.comentario = limpiarComentario(comentario);
        this.fecha = Validar.presente(fecha, "fecha");
    }

    private static String limpiarComentario(String comentario) {
        String limpio = comentario == null ? "" : comentario.strip();
        if (limpio.length() > LONGITUD_MAXIMA_COMENTARIO) {
            throw new IllegalArgumentException(
                    "El comentario admite maximo " + LONGITUD_MAXIMA_COMENTARIO + " caracteres");
        }
        return limpio;
    }

    public Long getId() {
        return id;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
