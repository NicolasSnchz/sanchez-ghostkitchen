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
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Entrada del historial de acciones de un cazador. Guarda lo minimo para poder
 * reconstruir el comando que la produjo y revertirlo: que se hizo y sobre que.
 */
@Entity
@Table(name = "acciones")
public class AccionRegistrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cazador_id", nullable = false)
    private Cazador cazador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAccion tipo;

    /** Identificador de la cocina o del reto sobre el que se actuo. */
    @Column(nullable = false)
    private Long objetivoId;

    /** Nombre legible del objetivo en el momento de la accion, para mostrar en la actividad. */
    @Column(nullable = false, length = 80)
    private String detalle;

    @Column(nullable = false)
    private int puntos;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private boolean deshecha;

    protected AccionRegistrada() {
        // requerido por JPA
    }

    public AccionRegistrada(Cazador cazador, TipoAccion tipo, Long objetivoId, String detalle,
                            int puntos, LocalDateTime fechaHora) {
        this.cazador = Validar.presente(cazador, "cazador");
        this.tipo = Validar.presente(tipo, "tipo de accion");
        this.objetivoId = Validar.presente(objetivoId, "objetivo");
        this.detalle = Validar.texto(detalle, "detalle", 80);
        this.puntos = puntos;
        this.fechaHora = Validar.presente(fechaHora, "fecha y hora");
        this.deshecha = false;
    }

    /** Una accion solo se puede deshacer mientras este dentro de la ventana de tiempo permitida. */
    public boolean sePuedeDeshacer(LocalDateTime ahora, Duration ventana) {
        return !deshecha && !ahora.isAfter(fechaHora.plus(ventana));
    }

    public void marcarDeshecha() {
        this.deshecha = true;
    }

    public Long getId() {
        return id;
    }

    public Cazador getCazador() {
        return cazador;
    }

    public TipoAccion getTipo() {
        return tipo;
    }

    public Long getObjetivoId() {
        return objetivoId;
    }

    public String getDetalle() {
        return detalle;
    }

    public int getPuntos() {
        return puntos;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public boolean isDeshecha() {
        return deshecha;
    }
}
