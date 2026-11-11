package co.edu.udes.ghostkitchen.presentacion.web.formularios;

import co.edu.udes.ghostkitchen.aplicacion.retos.DatosReto;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/** Formulario del administrador para crear un reto. */
public class FormularioReto {

    @NotBlank(message = "{formulario.reto.titulo}")
    @Size(max = 80, message = "{formulario.reto.titulo}")
    private String titulo;

    @NotBlank(message = "{formulario.reto.descripcion}")
    @Size(max = 240, message = "{formulario.reto.descripcion}")
    private String descripcion;

    @NotNull(message = "{formulario.reto.meta}")
    @Min(value = 1, message = "{formulario.reto.meta}")
    @Max(value = 50, message = "{formulario.reto.meta}")
    private Integer metaCocinas;

    @NotNull(message = "{formulario.reto.recompensa}")
    @Min(value = 1, message = "{formulario.reto.recompensa}")
    @Max(value = 5000, message = "{formulario.reto.recompensa}")
    private Integer puntosRecompensa;

    @NotNull(message = "{formulario.reto.fechas}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @NotNull(message = "{formulario.reto.fechas}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    /** Propone un reto de una semana que empieza hoy, para no partir de un formulario vacio. */
    public static FormularioReto paraLaSemanaDe(LocalDate hoy) {
        FormularioReto formulario = new FormularioReto();
        formulario.metaCocinas = 3;
        formulario.puntosRecompensa = 150;
        formulario.fechaInicio = hoy;
        formulario.fechaFin = hoy.plusDays(7);
        return formulario;
    }

    @AssertTrue(message = "{formulario.reto.fechas-orden}")
    public boolean isFechasEnOrden() {
        return fechaInicio == null || fechaFin == null || !fechaFin.isBefore(fechaInicio);
    }

    public DatosReto aDatos() {
        return new DatosReto(titulo.strip(), descripcion.strip(), metaCocinas, puntosRecompensa, fechaInicio,
                fechaFin);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getMetaCocinas() {
        return metaCocinas;
    }

    public void setMetaCocinas(Integer metaCocinas) {
        this.metaCocinas = metaCocinas;
    }

    public Integer getPuntosRecompensa() {
        return puntosRecompensa;
    }

    public void setPuntosRecompensa(Integer puntosRecompensa) {
        this.puntosRecompensa = puntosRecompensa;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}
