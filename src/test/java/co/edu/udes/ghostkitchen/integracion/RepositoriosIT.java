package co.edu.udes.ghostkitchen.integracion;

import static co.edu.udes.ghostkitchen.soporte.Ejemplos.AHORA;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cazador;
import static co.edu.udes.ghostkitchen.soporte.Ejemplos.cocina;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Descubrimiento;
import co.edu.udes.ghostkitchen.dominio.modelo.ParticipacionReto;
import co.edu.udes.ghostkitchen.dominio.modelo.Reto;
import co.edu.udes.ghostkitchen.persistencia.ActividadPorCazador;
import co.edu.udes.ghostkitchen.persistencia.CalificacionPorCocina;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioParticipaciones;
import co.edu.udes.ghostkitchen.persistencia.RepositorioRetos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

/** Consultas propias de los repositorios contra una base H2 real. */
@DataJpaTest
@ActiveProfiles("test")
class RepositoriosIT {

    @Autowired
    private RepositorioCocinas cocinas;

    @Autowired
    private RepositorioCazadores cazadores;

    @Autowired
    private RepositorioDescubrimientos descubrimientos;

    @Autowired
    private RepositorioRetos retos;

    @Autowired
    private RepositorioParticipaciones participaciones;

    private Cazador humoAzul;
    private Cazador lulaBites;
    private CocinaFantasma pizzeria;
    private CocinaFantasma taqueria;

    @BeforeEach
    void preparar() {
        humoAzul = cazadores.save(cazador("humo_azul"));
        lulaBites = cazadores.save(cazador("lula_bites"));
        pizzeria = cocinas.save(cocina().build());
        taqueria = cocinas.save(cocina().nombre("Taqueria Invisible").ubicacion(7.91, -72.49, "Colsag").build());
    }

    @Test
    @DisplayName("PI-010 Las consultas de agregacion calculan puntos, resenas y promedios en la base de datos")
    void consultas_agreganPuntosResenasYCalificaciones() {
        Descubrimiento primera = Descubrimiento.registrar(humoAzul, pizzeria, AHORA.minusDays(1), 150);
        primera.resenar(5, "Excelente", AHORA.minusDays(1));
        descubrimientos.save(primera);
        descubrimientos.save(Descubrimiento.registrar(humoAzul, taqueria, AHORA, 225));
        Descubrimiento deLula = Descubrimiento.registrar(lulaBites, pizzeria, AHORA, 100);
        deLula.resenar(3, "", AHORA);
        descubrimientos.save(deLula);

        assertThat(descubrimientos.puntosDeCazasDe(humoAzul.getId())).isEqualTo(375);
        assertThat(descubrimientos.resenasDe(humoAzul.getId())).isEqualTo(1);
        assertThat(descubrimientos.cazasDelDia(humoAzul.getId(), AHORA.toLocalDate())).isEqualTo(1);
        assertThat(descubrimientos.calificacionesPorCocina())
                .containsExactly(new CalificacionPorCocina(pizzeria.getId(), 4.0, 2L));
        assertThat(descubrimientos.actividadPorCazador())
                .contains(new ActividadPorCazador(humoAzul.getId(), 2L, 375L, 1L));
        assertThat(descubrimientos.cocinasCazadasPor(humoAzul.getId()))
                .containsExactlyInAnyOrder(pizzeria.getId(), taqueria.getId());
        assertThat(descubrimientos.masRecientes(PageRequest.of(0, 2))).hasSize(2);
        assertThat(descubrimientos.conResenaDeCocina(pizzeria.getId())).hasSize(2);
        assertThat(cocinas.barriosConCocinasActivas()).isEqualTo(2);
    }

    @Test
    void unaCocinaNoSePuedeCazarDosVecesPorElMismoCazador() {
        descubrimientos.saveAndFlush(Descubrimiento.registrar(humoAzul, pizzeria, AHORA, 150));

        assertThatThrownBy(() -> descubrimientos.saveAndFlush(
                Descubrimiento.registrar(humoAzul, pizzeria, AHORA.plusMinutes(5), 100)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void recompensasSoloCuentanRetosCompletados() {
        Reto doble = retos.save(new Reto("Doble o nada", "Caza dos cocinas.", 2, 120, AHORA.toLocalDate(),
                AHORA.toLocalDate().plusDays(2)));
        Reto semana = retos.save(new Reto("Semana", "Caza cuatro cocinas.", 4, 200, AHORA.toLocalDate(),
                AHORA.toLocalDate().plusDays(7)));
        ParticipacionReto completada = new ParticipacionReto(humoAzul, doble, AHORA);
        completada.actualizarAvance(2, AHORA);
        participaciones.save(completada);
        participaciones.save(new ParticipacionReto(humoAzul, semana, AHORA));

        assertThat(participaciones.recompensasDe(humoAzul.getId())).isEqualTo(120);
        assertThat(participaciones.countByCazadorIdAndFechaCompletadoIsNotNull(humoAzul.getId())).isEqualTo(1);
        assertThat(participaciones.deCazador(humoAzul.getId())).hasSize(2);
        assertThat(retos.vigentesEn(AHORA.toLocalDate())).hasSize(2);
        assertThat(retos.vigentesEn(AHORA.toLocalDate().plusDays(5))).extracting(Reto::getTitulo)
                .containsExactly("Semana");
    }
}
