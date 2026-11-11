package co.edu.udes.ghostkitchen.dominio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Datos de cuenta que comparten cazadores y administradores. Es abstracta: nadie
 * es "usuario" a secas, siempre se es una de las dos cosas.
 */
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", length = 12)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Column(nullable = false, length = 100)
    private String claveCifrada;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    protected Usuario() {
        // requerido por JPA
    }

    protected Usuario(String nombre, String correo, String claveCifrada, LocalDateTime fechaRegistro) {
        this.nombre = Validar.texto(nombre, "nombre", 80);
        this.correo = normalizarCorreo(correo);
        this.claveCifrada = Validar.texto(claveCifrada, "clave", 100);
        this.fechaRegistro = Validar.presente(fechaRegistro, "fecha de registro");
    }

    /** Los correos se comparan sin distinguir mayusculas ni espacios sobrantes. */
    public static String normalizarCorreo(String correo) {
        return Validar.texto(correo, "correo", 120).toLowerCase(Locale.ROOT);
    }

    /** Cada subtipo declara su rol; asi no hace falta preguntar por el tipo concreto. */
    public abstract Rol rol();

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getClaveCifrada() {
        return claveCifrada;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}
