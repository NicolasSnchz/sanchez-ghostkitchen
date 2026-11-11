package co.edu.udes.ghostkitchen.presentacion.web.formularios;

import co.edu.udes.ghostkitchen.aplicacion.acciones.NuevaResena;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Datos del formulario de resena. El comentario es opcional; la calificacion no. */
public class FormularioResena {

    @NotNull(message = "{formulario.resena.calificacion}")
    @Min(value = 1, message = "{formulario.resena.calificacion}")
    @Max(value = 5, message = "{formulario.resena.calificacion}")
    private Integer calificacion;

    @Size(max = 300, message = "{formulario.resena.comentario}")
    private String comentario;

    public NuevaResena aResena() {
        return new NuevaResena(calificacion, comentario == null ? "" : comentario.strip());
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
