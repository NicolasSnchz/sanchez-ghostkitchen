package co.edu.udes.ghostkitchen.dominio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Mision temporal: cazar cierta cantidad de cocinas antes de una fecha a cambio de puntos extra. */
@Entity
@Table(name = "retos")
public class Reto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String titulo;

    @Column(nullable = false, length = 240)
    private String descripcion;

    @Column(nullable = false)
    private int metaCocinas;

    @Column(nullable = false)
    private int puntosRecompensa;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    protected Reto() {
        // requerido por JPA
    }

    public Reto(String titulo, String descripcion, int metaCocinas, int puntosRecompensa,
                LocalDate fechaInicio, LocalDate fechaFin) {
        this.titulo = Validar.texto(titulo, "titulo", 80);
        this.descripcion = Validar.texto(descripcion, "descripcion", 240);
        this.metaCocinas = Validar.enRango(metaCocinas, 1, 50, "meta de cocinas");
        this.puntosRecompensa = Validar.enRango(puntosRecompensa, 1, 5000, "puntos de recompensa");
        this.fechaInicio = Validar.presente(fechaInicio, "fecha de inicio");
        this.fechaFin = Validar.presente(fechaFin, "fecha de fin");
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }
    }

    /** Un reto esta vigente desde su fecha de inicio hasta su fecha de fin, ambas incluidas. */
    public EstadoReto estadoEn(LocalDate hoy) {
        if (hoy.isBefore(fechaInicio)) {
            return EstadoReto.PROXIMO;
        }
        return hoy.isAfter(fechaFin) ? EstadoReto.FINALIZADO : EstadoReto.VIGENTE;
    }

    public boolean estaVigente(LocalDate hoy) {
        return estadoEn(hoy) == EstadoReto.VIGENTE;
    }

    /** Ultimo instante en que una caza todavia cuenta para el reto. */
    public LocalDateTime cierre() {
        return fechaFin.atTime(LocalTime.MAX);
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getMetaCocinas() {
        return metaCocinas;
    }

    public int getPuntosRecompensa() {
        return puntosRecompensa;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
}
