package co.edu.udes.ghostkitchen.presentacion.web.formularios;

import co.edu.udes.ghostkitchen.aplicacion.cuentas.NuevaCuenta;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** Datos para crear una cuenta de cazador. Las reglas coinciden con las del dominio. */
public class FormularioRegistro {

    @NotBlank(message = "{formulario.registro.nombre}")
    @Size(max = 80, message = "{formulario.registro.nombre}")
    private String nombre;

    @NotNull(message = "{formulario.registro.alias}")
    @Pattern(regexp = "\\s*[A-Za-z0-9_]{3,20}\\s*", message = "{formulario.registro.alias}")
    private String alias;

    @NotBlank(message = "{formulario.registro.correo}")
    @Email(message = "{formulario.registro.correo}")
    @Size(max = 120, message = "{formulario.registro.correo}")
    private String correo;

    /** Entre 8 y 72 caracteres (el limite de BCrypt) con al menos una letra y un numero. */
    @NotNull(message = "{formulario.registro.clave}")
    @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d).{8,72}", message = "{formulario.registro.clave}")
    private String clave;

    private String confirmacion;

    /** BCrypt solo considera los primeros 72 bytes; una tilde ocupa dos. */
    @AssertTrue(message = "{formulario.registro.clave}")
    public boolean isClaveCabeEnBcrypt() {
        return clave == null || clave.getBytes(StandardCharsets.UTF_8).length <= 72;
    }

    @AssertTrue(message = "{formulario.registro.confirmacion}")
    public boolean isClavesCoinciden() {
        return Objects.equals(clave, confirmacion);
    }

    public NuevaCuenta aCuenta() {
        return new NuevaCuenta(nombre.strip(), alias.strip(), correo.strip(), clave);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(String confirmacion) {
        this.confirmacion = confirmacion;
    }
}
