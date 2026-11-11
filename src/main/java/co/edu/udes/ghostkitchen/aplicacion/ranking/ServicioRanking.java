package co.edu.udes.ghostkitchen.aplicacion.ranking;

import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import co.edu.udes.ghostkitchen.dominio.modelo.Nivel;
import co.edu.udes.ghostkitchen.dominio.puntos.PuntosCazador;
import co.edu.udes.ghostkitchen.persistencia.ActividadPorCazador;
import co.edu.udes.ghostkitchen.persistencia.RecompensaPorCazador;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioParticipaciones;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tabla de posiciones de los cazadores. Usa la misma formula de puntos que el
 * panel de cada cazador, asi las dos pantallas nunca muestran cifras distintas.
 */
@Service
@Transactional(readOnly = true)
public class ServicioRanking {

    private static final ActividadPorCazador SIN_ACTIVIDAD = new ActividadPorCazador(null, 0L, 0L, 0L);

    private final RepositorioCazadores cazadores;
    private final RepositorioDescubrimientos descubrimientos;
    private final RepositorioParticipaciones participaciones;

    public ServicioRanking(RepositorioCazadores cazadores, RepositorioDescubrimientos descubrimientos,
                           RepositorioParticipaciones participaciones) {
        this.cazadores = cazadores;
        this.descubrimientos = descubrimientos;
        this.participaciones = participaciones;
    }

    /** Cazadores con al menos un punto, de mayor a menor puntaje; a igual puntaje, mas cazas primero. */
    public List<PosicionRanking> clasificacion(int limite) {
        Map<Long, ActividadPorCazador> actividad = descubrimientos.actividadPorCazador().stream()
                .collect(Collectors.toMap(ActividadPorCazador::cazadorId, Function.identity()));
        Map<Long, Long> recompensas = participaciones.recompensasPorCazador().stream()
                .collect(Collectors.toMap(RecompensaPorCazador::cazadorId, RecompensaPorCazador::puntos));
        List<Fila> filas = cazadores.findAll().stream()
                .map(cazador -> fila(cazador, actividad.getOrDefault(cazador.getId(), SIN_ACTIVIDAD),
                        recompensas.getOrDefault(cazador.getId(), 0L)))
                .filter(fila -> fila.puntos() > 0)
                .sorted(Comparator.comparingInt(Fila::puntos).reversed()
                        .thenComparing(Comparator.comparingLong(Fila::cazas).reversed())
                        .thenComparing(fila -> fila.alias().toLowerCase(Locale.ROOT)))
                .toList();
        return numerar(filas).stream().limit(limite).toList();
    }

    private static Fila fila(Cazador cazador, ActividadPorCazador actividad, long recompensas) {
        int puntos = PuntosCazador.total(actividad.puntos(), actividad.resenas(), recompensas);
        return new Fila(cazador.getId(), cazador.getAlias(), puntos, actividad.cazas(), actividad.resenas());
    }

    /** Numeracion de competencia: dos cazadores empatados en puntos y cazas comparten el puesto. */
    private static List<PosicionRanking> numerar(List<Fila> filas) {
        List<PosicionRanking> posiciones = new ArrayList<>(filas.size());
        for (int indice = 0; indice < filas.size(); indice++) {
            Fila actual = filas.get(indice);
            int posicion = indice + 1;
            if (indice > 0 && actual.empataCon(filas.get(indice - 1))) {
                posicion = posiciones.get(indice - 1).posicion();
            }
            posiciones.add(new PosicionRanking(posicion, actual.cazadorId(), actual.alias(), actual.puntos(),
                    Nivel.paraPuntos(actual.puntos()), actual.cazas(), actual.resenas()));
        }
        return posiciones;
    }

    private record Fila(Long cazadorId, String alias, int puntos, long cazas, long resenas) {

        boolean empataCon(Fila otra) {
            return puntos == otra.puntos && cazas == otra.cazas;
        }
    }
}
