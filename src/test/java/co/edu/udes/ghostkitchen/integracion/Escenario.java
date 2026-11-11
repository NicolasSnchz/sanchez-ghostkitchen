package co.edu.udes.ghostkitchen.integracion;

import co.edu.udes.ghostkitchen.dominio.modelo.Administrador;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import co.edu.udes.ghostkitchen.dominio.modelo.Reto;
import co.edu.udes.ghostkitchen.dominio.modelo.Usuario;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import co.edu.udes.ghostkitchen.persistencia.RepositorioRetos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioUsuarios;
import co.edu.udes.ghostkitchen.seguridad.CuentaAutenticada;
import co.edu.udes.ghostkitchen.soporte.Ejemplos;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Arma los datos de cada prueba de integracion con los repositorios reales. */
@TestComponent
public class Escenario {

    public static final String CLAVE = "Clave2026";
    private static final LocalDate HOY = Ejemplos.AHORA.toLocalDate();

    private final RepositorioCocinas cocinas;
    private final RepositorioCazadores cazadores;
    private final RepositorioUsuarios usuarios;
    private final RepositorioRetos retos;
    private final PasswordEncoder cifrador;

    public Escenario(RepositorioCocinas cocinas, RepositorioCazadores cazadores, RepositorioUsuarios usuarios,
                     RepositorioRetos retos, PasswordEncoder cifrador) {
        this.cocinas = cocinas;
        this.cazadores = cazadores;
        this.usuarios = usuarios;
        this.retos = retos;
        this.cifrador = cifrador;
    }

    /** Cocina rara abierta de 12:00 m. a 11:00 p. m.; a la hora fija de las pruebas (9:30 p. m.) esta abierta. */
    public CocinaFantasma cocinaAbierta(String nombre) {
        return cocinas.save(Ejemplos.cocina().nombre(nombre).rareza(Rareza.RARA).build());
    }

    public CocinaFantasma cocinaCerrada(String nombre) {
        return cocinas.save(Ejemplos.cocina().nombre(nombre).horario(LocalTime.of(6, 0), LocalTime.of(14, 0))
                .categoria(CategoriaCocina.AREPAS).build());
    }

    public Cazador cazador(String alias) {
        return cazadores.save(new Cazador("Cazador " + alias, alias, alias + "@correo.co", cifrador.encode(CLAVE),
                Ejemplos.AHORA.minusDays(10)));
    }

    public Administrador administrador() {
        return usuarios.save(new Administrador("Administracion", "jefe@correo.co", cifrador.encode(CLAVE),
                Ejemplos.AHORA.minusDays(30)));
    }

    /** Reto vigente: empezo ayer y termina en cinco dias. */
    public Reto retoVigente(String titulo, int meta, int recompensa) {
        return retos.save(new Reto(titulo, "Reto de prueba.", meta, recompensa, HOY.minusDays(1), HOY.plusDays(5)));
    }

    public Reto retoProximo(String titulo) {
        return retos.save(new Reto(titulo, "Reto que aun no empieza.", 3, 100, HOY.plusDays(3), HOY.plusDays(9)));
    }

    /** Usuario autenticado tal como lo deja Spring Security tras el inicio de sesion. */
    public static CuentaAutenticada cuenta(Usuario usuario) {
        String alias = usuario instanceof Cazador cazador ? cazador.getAlias() : null;
        return new CuentaAutenticada(usuario.getId(), usuario.getNombre(), alias, usuario.getCorreo(),
                usuario.getClaveCifrada(), usuario.rol());
    }
}
