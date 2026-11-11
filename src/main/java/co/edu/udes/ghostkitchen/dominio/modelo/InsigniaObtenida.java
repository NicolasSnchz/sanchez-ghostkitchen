package co.edu.udes.ghostkitchen.dominio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/** Constancia de que un cazador gano una insignia y en que momento. */
@Entity
@Table(name = "insignias_obtenidas",
        uniqueConstraints = @UniqueConstraint(name = "uk_cazador_insignia", columnNames = {"cazador_id", "tipo"}))
public class InsigniaObtenida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cazador_id", nullable = false)
    private Cazador cazador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoInsignia tipo;

    @Column(nullable = false)
    private LocalDateTime fechaObtencion;

    protected InsigniaObtenida() {
        // requerido por JPA
    }

    public InsigniaObtenida(Cazador cazador, TipoInsignia tipo, LocalDateTime fechaObtencion) {
        this.cazador = Validar.presente(cazador, "cazador");
        this.tipo = Validar.presente(tipo, "tipo de insignia");
        this.fechaObtencion = Validar.presente(fechaObtencion, "fecha de obtencion");
    }

    public Long getId() {
        return id;
    }

    public Cazador getCazador() {
        return cazador;
    }

    public TipoInsignia getTipo() {
        return tipo;
    }

    public LocalDateTime getFechaObtencion() {
        return fechaObtencion;
    }
}
