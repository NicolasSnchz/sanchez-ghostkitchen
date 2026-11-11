package co.edu.udes.ghostkitchen.aplicacion.logros;

import co.edu.udes.ghostkitchen.aplicacion.progreso.EventoProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.Novedad;
import co.edu.udes.ghostkitchen.aplicacion.progreso.ObservadorProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.ServicioProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.TipoNovedad;
import co.edu.udes.ghostkitchen.dominio.modelo.InsigniaObtenida;
import co.edu.udes.ghostkitchen.dominio.modelo.ProgresoCazador;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoInsignia;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioInsignias;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Observador concreto: deja las insignias del cazador exactamente como las merece
 * su progreso actual. Entrega las que gano y retira las que dejo de merecer cuando
 * se deshace una accion, para que deshacer revierta todos los efectos.
 */
@Component
@Order(2)
public class SincronizadorInsignias implements ObservadorProgreso {

    private final ServicioProgreso progreso;
    private final RepositorioInsignias insignias;
    private final RepositorioCazadores cazadores;

    public SincronizadorInsignias(ServicioProgreso progreso, RepositorioInsignias insignias,
                                  RepositorioCazadores cazadores) {
        this.progreso = progreso;
        this.insignias = insignias;
        this.cazadores = cazadores;
    }

    @Override
    public List<Novedad> alCambiarProgreso(EventoProgreso evento) {
        Set<TipoInsignia> merecidas = merecidasPor(progreso.progresoDe(evento.cazadorId()));
        Set<TipoInsignia> vigentes = EnumSet.noneOf(TipoInsignia.class);
        List<Novedad> novedades = new ArrayList<>();
        for (InsigniaObtenida obtenida : insignias.findByCazadorId(evento.cazadorId())) {
            if (merecidas.contains(obtenida.getTipo())) {
                vigentes.add(obtenida.getTipo());
            } else {
                insignias.delete(obtenida);
                novedades.add(Novedad.de(TipoNovedad.INSIGNIA_RETIRADA, obtenida.getTipo().name()));
            }
        }
        for (TipoInsignia tipo : merecidas) {
            if (!vigentes.contains(tipo)) {
                insignias.save(new InsigniaObtenida(cazadores.getReferenceById(evento.cazadorId()), tipo,
                        evento.momento()));
                novedades.add(Novedad.de(TipoNovedad.INSIGNIA_GANADA, tipo.name()));
            }
        }
        return novedades;
    }

    private static Set<TipoInsignia> merecidasPor(ProgresoCazador actual) {
        Set<TipoInsignia> merecidas = EnumSet.noneOf(TipoInsignia.class);
        Arrays.stream(TipoInsignia.values())
                .filter(tipo -> tipo.laMerece(actual))
                .forEach(merecidas::add);
        return merecidas;
    }
}
