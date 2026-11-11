package co.edu.udes.ghostkitchen.presentacion.web.formularios;

import co.edu.udes.ghostkitchen.aplicacion.catalogo.DatosCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.CanalPedido;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Formulario del administrador para registrar o editar una cocina. Las
 * coordenadas se limitan al area urbana de Cucuta para evitar errores de digitacion.
 */
public class FormularioCocina {

    @NotBlank(message = "{formulario.cocina.nombre}")
    @Size(max = 80, message = "{formulario.cocina.nombre}")
    private String nombre;

    @NotBlank(message = "{formulario.cocina.descripcion}")
    @Size(max = 400, message = "{formulario.cocina.descripcion}")
    private String descripcion;

    @NotNull(message = "{formulario.cocina.categoria}")
    private CategoriaCocina categoria;

    @NotNull(message = "{formulario.cocina.rareza}")
    private Rareza rareza;

    @NotNull(message = "{formulario.cocina.horario}")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaApertura;

    @NotNull(message = "{formulario.cocina.horario}")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaCierre;

    @NotNull(message = "{formulario.cocina.latitud}")
    @DecimalMin(value = "7.80", message = "{formulario.cocina.latitud}")
    @DecimalMax(value = "8.00", message = "{formulario.cocina.latitud}")
    private Double latitud;

    @NotNull(message = "{formulario.cocina.longitud}")
    @DecimalMin(value = "-72.58", message = "{formulario.cocina.longitud}")
    @DecimalMax(value = "-72.42", message = "{formulario.cocina.longitud}")
    private Double longitud;

    @NotBlank(message = "{formulario.cocina.barrio}")
    @Size(max = 60, message = "{formulario.cocina.barrio}")
    private String barrio;

    @NotEmpty(message = "{formulario.cocina.canales}")
    private Set<CanalPedido> canales = EnumSet.noneOf(CanalPedido.class);

    public static FormularioCocina desde(DatosCocina datos) {
        FormularioCocina formulario = new FormularioCocina();
        formulario.nombre = datos.nombre();
        formulario.descripcion = datos.descripcion();
        formulario.categoria = datos.categoria();
        formulario.rareza = datos.rareza();
        formulario.horaApertura = datos.horaApertura();
        formulario.horaCierre = datos.horaCierre();
        formulario.latitud = datos.latitud();
        formulario.longitud = datos.longitud();
        formulario.barrio = datos.barrio();
        formulario.canales = datos.canales().isEmpty()
                ? EnumSet.noneOf(CanalPedido.class)
                : EnumSet.copyOf(datos.canales());
        return formulario;
    }

    /** La apertura y el cierre pueden cruzar la medianoche, pero no ser la misma hora. */
    @AssertTrue(message = "{formulario.cocina.horario-igual}")
    public boolean isHorarioValido() {
        return horaApertura == null || !horaApertura.equals(horaCierre);
    }

    public DatosCocina aDatos() {
        return new DatosCocina(nombre.strip(), descripcion.strip(), categoria, rareza, horaApertura, horaCierre,
                latitud, longitud, barrio.strip(), canales);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public CategoriaCocina getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaCocina categoria) {
        this.categoria = categoria;
    }

    public Rareza getRareza() {
        return rareza;
    }

    public void setRareza(Rareza rareza) {
        this.rareza = rareza;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(LocalTime horaApertura) {
        this.horaApertura = horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(LocalTime horaCierre) {
        this.horaCierre = horaCierre;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public Set<CanalPedido> getCanales() {
        return canales;
    }

    public void setCanales(Set<CanalPedido> canales) {
        this.canales = canales == null ? EnumSet.noneOf(CanalPedido.class) : canales;
    }
}
