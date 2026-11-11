package co.edu.udes.ghostkitchen.integracion;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import co.edu.udes.ghostkitchen.dominio.modelo.Usuario;
import co.edu.udes.ghostkitchen.soporte.Ejemplos;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base de las pruebas de integracion: aplicacion completa con H2 en memoria,
 * MockMvc y un reloj fijo. Cada prueba corre en una transaccion que se revierte
 * al final, asi ninguna deja datos para la siguiente.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import({PruebaIntegracion.RelojDePrueba.class, Escenario.class})
public abstract class PruebaIntegracion {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected Escenario escenario;

    protected static RequestPostProcessor como(Usuario usuario) {
        return user(Escenario.cuenta(usuario));
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class RelojDePrueba {

        /** Martes 10 de noviembre de 2026, 9:30 p. m. en Cucuta. */
        @Bean
        @Primary
        Clock relojFijo() {
            return Ejemplos.relojEn(Ejemplos.AHORA);
        }
    }
}
