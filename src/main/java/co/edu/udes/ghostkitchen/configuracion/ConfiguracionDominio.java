package co.edu.udes.ghostkitchen.configuracion;

import co.edu.udes.ghostkitchen.dominio.orden.CriterioOrden;
import co.edu.udes.ghostkitchen.dominio.orden.OrdenPorCalificacion;
import co.edu.udes.ghostkitchen.dominio.orden.OrdenPorCercania;
import co.edu.udes.ghostkitchen.dominio.orden.OrdenPorNovedad;
import co.edu.udes.ghostkitchen.dominio.orden.OrdenPorPopularidad;
import co.edu.udes.ghostkitchen.dominio.puntos.BonoPrimerCazador;
import co.edu.udes.ghostkitchen.dominio.puntos.BonoRachaDiaria;
import co.edu.udes.ghostkitchen.dominio.puntos.CalculadoraPuntos;
import co.edu.udes.ghostkitchen.dominio.puntos.MultiplicadorHoraFantasma;
import co.edu.udes.ghostkitchen.dominio.puntos.PuntosBasePorRareza;
import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Raiz de composicion del dominio. Las clases del dominio no conocen Spring: aqui
 * se deciden sus implementaciones concretas y se arma la cadena de decoradores.
 * Es el unico lugar donde se escribe "new" para estas politicas.
 */
@Configuration(proxyBeanMethods = false)
public class ConfiguracionDominio {

    public static final ZoneId ZONA_CUCUTA = ZoneId.of("America/Bogota");

    /** Reloj inyectable: produccion usa la hora real de Cucuta y las pruebas uno fijo. */
    @Bean
    public Clock reloj() {
        return Clock.system(ZONA_CUCUTA);
    }

    /**
     * El orden de los decoradores es una regla del juego: los bonos fijos se suman
     * primero y el multiplicador de la hora fantasma va por fuera, para multiplicarlos tambien.
     */
    @Bean
    public CalculadoraPuntos calculadoraPuntos() {
        return new MultiplicadorHoraFantasma(
                new BonoRachaDiaria(
                        new BonoPrimerCazador(
                                new PuntosBasePorRareza())));
    }

    @Bean
    public CriterioOrden ordenPorCercania() {
        return new OrdenPorCercania();
    }

    @Bean
    public CriterioOrden ordenPorCalificacion() {
        return new OrdenPorCalificacion();
    }

    @Bean
    public CriterioOrden ordenPorPopularidad() {
        return new OrdenPorPopularidad();
    }

    @Bean
    public CriterioOrden ordenPorNovedad() {
        return new OrdenPorNovedad();
    }
}
