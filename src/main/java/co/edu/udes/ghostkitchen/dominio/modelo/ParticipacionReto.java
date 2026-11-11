package co.edu.udes.ghostkitchen.dominio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * Inscripcion de un cazador en un reto. Guarda desde cuando cuenta su avance y,
 * si ya cumplio la meta, cuando lo hizo.
 */
@Entity
@Table(name = "participaciones_reto",
        uniqueConstraints = @UniqueConstraint(name = "uk_cazador_reto", columnNames = {"cazador_id", "reto_id"}))
public class ParticipacionReto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cazador_id", nullable = false)
    private Cazador cazador;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reto_id", nullable = false)
    private Reto reto;

    @Column(nullable = false)
    private LocalDateTime fechaUnion;

    private LocalDateTime fechaCompletado;

    protected ParticipacionReto() {
        // requerido por JPA
    }

    public ParticipacionReto(Cazador cazador, Reto reto, LocalDateTime fechaUnion) {
        this.cazador = Validar.presente(cazador, "cazador");
        this.reto = Validar.presente(reto, "reto");
        this.fechaUnion = Validar.presente(fechaUnion, "fecha de union");
    }

    /**
     * Ajusta el estado al avance real. Devuelve true solo cuando el reto pasa de
     * pendiente a completado, que es el momento en que hay algo que celebrar.
     */
    public boolean actualizarAvance(long cocinasCazadas, LocalDateTime ahora) {
        boolean metaCumplida = cocinasCazadas >= reto.getMetaCocinas();
        if (metaCumplida && fechaCompletado == null) {
            fechaCompletado = ahora;
            return true;
        }
        if (!metaCumplida) {
            fechaCompletado = null;
        }
        return false;
    }

    public boolean estaCompletada() {
        return fechaCompletado != null;
    }

    public Long getId() {
        return id;
    }

    public Cazador getCazador() {
        return cazador;
    }

    public Reto getReto() {
        return reto;
    }

    public LocalDateTime getFechaUnion() {
        return fechaUnion;
    }

    public LocalDateTime getFechaCompletado() {
        return fechaCompletado;
    }
}
