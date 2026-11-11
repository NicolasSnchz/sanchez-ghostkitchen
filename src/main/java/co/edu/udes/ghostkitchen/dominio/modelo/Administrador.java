package co.edu.udes.ghostkitchen.dominio.modelo;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

/** Usuario que mantiene al dia el catalogo de cocinas y los retos. No participa del juego. */
@Entity
@DiscriminatorValue("ADMIN")
public class Administrador extends Usuario {

    protected Administrador() {
        // requerido por JPA
    }

    public Administrador(String nombre, String correo, String claveCifrada, LocalDateTime fechaRegistro) {
        super(nombre, correo, claveCifrada, fechaRegistro);
    }

    @Override
    public Rol rol() {
        return Rol.ADMIN;
    }
}
