package co.edu.udes.ghostkitchen.dominio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Usuario que juega: explora el mapa, caza cocinas, escribe resenas y acumula puntos.
 * Sus puntos y su nivel no se guardan: se calculan a partir de lo que ha hecho.
 */
@Entity
@DiscriminatorValue("CAZADOR")
public class Cazador extends Usuario {

    private static final Pattern FORMATO_ALIAS = Pattern.compile("\\w{3,20}");

    @Column(unique = true, length = 20)
    private String alias;

    protected Cazador() {
        // requerido por JPA
    }

    public Cazador(String nombre, String alias, String correo, String claveCifrada, LocalDateTime fechaRegistro) {
        super(nombre, correo, claveCifrada, fechaRegistro);
        this.alias = validarAlias(alias);
    }

    /** El alias es publico (aparece en el ranking y las resenas), por eso su formato es estricto. */
    public static String validarAlias(String alias) {
        if (alias == null || !FORMATO_ALIAS.matcher(alias.strip()).matches()) {
            throw new IllegalArgumentException(
                    "El alias debe tener entre 3 y 20 caracteres: letras, numeros o guion bajo");
        }
        return alias.strip();
    }

    @Override
    public Rol rol() {
        return Rol.CAZADOR;
    }

    public String getAlias() {
        return alias;
    }
}
