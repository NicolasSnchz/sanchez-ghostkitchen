package co.edu.udes.ghostkitchen.dominio.modelo;

import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Registro de que un cazador encontro una cocina: cuando ocurrio y cuantos puntos
 * le dio. Resuelve la relacion muchos a muchos entre cazadores y cocinas aportando
 * datos propios, y es duena de la resena opcional.
 */
@Entity
@Table(name = "descubrimientos",
        uniqueConstraints = @UniqueConstraint(name = "uk_cazador_cocina", columnNames = {"cazador_id", "cocina_id"}))
public class Descubrimiento {

    /** Tope de cazas por cazador en un mismo dia; evita marcar cocinas en masa sin visitarlas. */
    public static final int MAXIMO_CAZAS_POR_DIA = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cazador_id", nullable = false)
    private Cazador cazador;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cocina_id", nullable = false)
    private CocinaFantasma cocina;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private int puntosObtenidos;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "resena_id")
    private Resena resena;

    protected Descubrimiento() {
        // requerido por JPA
    }

    private Descubrimiento(Cazador cazador, CocinaFantasma cocina, LocalDateTime fechaHora, int puntosObtenidos) {
        if (puntosObtenidos < 0) {
            throw new IllegalArgumentException("Los puntos de un descubrimiento no pueden ser negativos");
        }
        this.cazador = Validar.presente(cazador, "cazador");
        this.cocina = Validar.presente(cocina, "cocina");
        this.fechaHora = Validar.presente(fechaHora, "fecha y hora");
        this.puntosObtenidos = puntosObtenidos;
    }

    public static Descubrimiento registrar(Cazador cazador, CocinaFantasma cocina,
                                           LocalDateTime fechaHora, int puntosObtenidos) {
        return new Descubrimiento(cazador, cocina, fechaHora, puntosObtenidos);
    }

    /** Solo se puede resenar una vez cada cocina descubierta. */
    public Resena resenar(int calificacion, String comentario, LocalDateTime fecha) {
        if (resena != null) {
            throw new ReglaNegocioException("resena.duplicada");
        }
        this.resena = new Resena(calificacion, comentario, fecha);
        return resena;
    }

    public void retirarResena() {
        this.resena = null;
    }

    public boolean tieneResena() {
        return resena != null;
    }

    public Optional<Resena> resena() {
        return Optional.ofNullable(resena);
    }

    public boolean ocurrioEnHoraFantasma() {
        return HoraFantasma.incluye(fechaHora.toLocalTime());
    }

    public Long getId() {
        return id;
    }

    public Cazador getCazador() {
        return cazador;
    }

    public CocinaFantasma getCocina() {
        return cocina;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public int getPuntosObtenidos() {
        return puntosObtenidos;
    }
}
