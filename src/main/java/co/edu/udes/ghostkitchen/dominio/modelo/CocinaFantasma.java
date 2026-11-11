package co.edu.udes.ghostkitchen.dominio.modelo;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Restaurante que solo existe dentro de las apps de domicilio: sin local abierto
 * al publico, sin letrero y sin fachada. Es lo que los cazadores salen a descubrir.
 *
 * <p>Se construye con {@link Builder} porque tiene muchos datos, varios opcionales,
 * y reglas que deben cumplirse todas a la vez antes de que el objeto exista.</p>
 */
@Entity
@Table(name = "cocinas")
public class CocinaFantasma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(nullable = false, length = 400)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaCocina categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private Rareza rareza;

    @Column(nullable = false)
    private LocalTime horaApertura;

    @Column(nullable = false)
    private LocalTime horaCierre;

    @Embedded
    private Ubicacion ubicacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cocina_canales", joinColumns = @JoinColumn(name = "cocina_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false, length = 20)
    private Set<CanalPedido> canales = new LinkedHashSet<>();

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false)
    private LocalDate fechaAlta;

    protected CocinaFantasma() {
        // requerido por JPA
    }

    private CocinaFantasma(Builder datos) {
        aplicar(datos);
        this.activa = true;
        this.fechaAlta = Validar.presente(datos.fechaAlta, "fecha de alta");
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Reemplaza los datos editables conservando identidad, estado y fecha de alta. */
    public void actualizar(Builder cambios) {
        aplicar(cambios);
    }

    private void aplicar(Builder datos) {
        LocalTime apertura = Validar.presente(datos.horaApertura, "hora de apertura");
        LocalTime cierre = Validar.presente(datos.horaCierre, "hora de cierre");
        if (apertura.equals(cierre)) {
            throw new IllegalArgumentException("La hora de apertura y la de cierre no pueden ser iguales");
        }
        if (datos.canales.isEmpty()) {
            throw new IllegalArgumentException(
                    "Una cocina fantasma necesita al menos un canal de pedido: no atiende en local");
        }
        this.nombre = Validar.texto(datos.nombre, "nombre", 80);
        this.descripcion = Validar.texto(datos.descripcion, "descripcion", 400);
        this.categoria = Validar.presente(datos.categoria, "categoria");
        this.rareza = Validar.presente(datos.rareza, "rareza");
        this.horaApertura = apertura;
        this.horaCierre = cierre;
        this.ubicacion = Validar.presente(datos.ubicacion, "ubicacion");
        this.canales.clear();
        this.canales.addAll(datos.canales);
    }

    /**
     * Indica si la cocina recibe pedidos a la hora dada. Contempla horarios que
     * cruzan la medianoche, por ejemplo de 6:00 p. m. a 2:00 a. m.
     */
    public boolean estaAbierta(LocalTime hora) {
        if (horaApertura.isBefore(horaCierre)) {
            return !hora.isBefore(horaApertura) && hora.isBefore(horaCierre);
        }
        return !hora.isBefore(horaApertura) || hora.isBefore(horaCierre);
    }

    public void activar() {
        this.activa = true;
    }

    public void desactivar() {
        this.activa = false;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public CategoriaCocina getCategoria() {
        return categoria;
    }

    public Rareza getRareza() {
        return rareza;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public Set<CanalPedido> getCanales() {
        return Collections.unmodifiableSet(canales);
    }

    public boolean isActiva() {
        return activa;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    /**
     * Patron Builder: reune los datos paso a paso con nombres legibles y deja
     * que {@link #build()} valide el conjunto completo. Evita un constructor de
     * nueve parametros y objetos a medio armar.
     */
    public static final class Builder {

        private String nombre;
        private String descripcion;
        private CategoriaCocina categoria;
        private Rareza rareza = Rareza.COMUN;
        private LocalTime horaApertura;
        private LocalTime horaCierre;
        private Ubicacion ubicacion;
        private final Set<CanalPedido> canales = EnumSet.noneOf(CanalPedido.class);
        private LocalDate fechaAlta;

        private Builder() {
        }

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public Builder categoria(CategoriaCocina categoria) {
            this.categoria = categoria;
            return this;
        }

        public Builder rareza(Rareza rareza) {
            this.rareza = rareza;
            return this;
        }

        public Builder horario(LocalTime apertura, LocalTime cierre) {
            this.horaApertura = apertura;
            this.horaCierre = cierre;
            return this;
        }

        public Builder ubicacion(double latitud, double longitud, String barrio) {
            this.ubicacion = new Ubicacion(latitud, longitud, barrio);
            return this;
        }

        public Builder canales(Collection<CanalPedido> canales) {
            this.canales.clear();
            if (canales != null) {
                this.canales.addAll(canales);
            }
            return this;
        }

        public Builder canal(CanalPedido canal) {
            this.canales.add(Validar.presente(canal, "canal de pedido"));
            return this;
        }

        public Builder registradaEl(LocalDate fechaAlta) {
            this.fechaAlta = fechaAlta;
            return this;
        }

        public CocinaFantasma build() {
            return new CocinaFantasma(this);
        }
    }
}
