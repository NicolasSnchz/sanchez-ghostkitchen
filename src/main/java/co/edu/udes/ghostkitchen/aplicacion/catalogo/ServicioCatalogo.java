package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import co.edu.udes.ghostkitchen.dominio.excepcion.RecursoNoEncontradoException;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Coordenada;
import co.edu.udes.ghostkitchen.dominio.modelo.Descubrimiento;
import co.edu.udes.ghostkitchen.dominio.orden.FichaCocina;
import co.edu.udes.ghostkitchen.persistencia.CalificacionPorCocina;
import co.edu.udes.ghostkitchen.persistencia.ConteoPorCocina;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consulta publica del catalogo. Arma las fichas con los datos que dependen del
 * momento y del visitante, filtra y delega el orden en la estrategia elegida.
 */
@Service
@Transactional(readOnly = true)
public class ServicioCatalogo {

    private final RepositorioCocinas cocinas;
    private final RepositorioDescubrimientos descubrimientos;
    private final SelectorCriterioOrden selector;
    private final Clock reloj;

    public ServicioCatalogo(RepositorioCocinas cocinas, RepositorioDescubrimientos descubrimientos,
                            SelectorCriterioOrden selector, Clock reloj) {
        this.cocinas = cocinas;
        this.descubrimientos = descubrimientos;
        this.selector = selector;
        this.reloj = reloj;
    }

    public List<FichaCocina> explorar(FiltroCocinas filtro, Long cazadorId) {
        Estadisticas estadisticas = estadisticas();
        Set<Long> propias = cocinasCazadasPor(cazadorId);
        LocalTime hora = LocalTime.now(reloj);
        List<FichaCocina> fichas = cocinas.findByActivaTrue().stream()
                .map(cocina -> ficha(cocina, estadisticas, filtro.referencia(), hora, propias))
                .filter(filtro::admite)
                .toList();
        return selector.para(filtro.orden()).ordenar(fichas);
    }

    public DetalleCocina detalle(Long cocinaId, Long cazadorId) {
        CocinaFantasma cocina = cocinas.findById(cocinaId)
                .filter(CocinaFantasma::isActiva)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cocina", cocinaId));
        LocalDateTime ahora = LocalDateTime.now(reloj);
        FichaCocina ficha = ficha(cocina, estadisticas(), Coordenada.CENTRO_DE_CUCUTA, ahora.toLocalTime(),
                cocinasCazadasPor(cazadorId));
        List<ResenaVista> resenas = descubrimientos.conResenaDeCocina(cocinaId).stream()
                .map(descubrimiento -> resenaVista(descubrimiento, cazadorId))
                .toList();
        boolean yaReseno = resenas.stream().anyMatch(ResenaVista::propia);
        return new DetalleCocina(ficha, cocina.getDescripcion(), resenas, estadoCaza(ficha, cazadorId, ahora),
                ficha.descubierta() && !yaReseno);
    }

    private EstadoCaza estadoCaza(FichaCocina ficha, Long cazadorId, LocalDateTime ahora) {
        if (cazadorId == null) {
            return EstadoCaza.SIN_SESION;
        }
        if (ficha.descubierta()) {
            return EstadoCaza.YA_CAZADA;
        }
        if (!ficha.abierta()) {
            return EstadoCaza.CERRADA;
        }
        if (descubrimientos.cazasDelDia(cazadorId, ahora.toLocalDate()) >= Descubrimiento.MAXIMO_CAZAS_POR_DIA) {
            return EstadoCaza.LIMITE_DIARIO;
        }
        return EstadoCaza.DISPONIBLE;
    }

    private static ResenaVista resenaVista(Descubrimiento descubrimiento, Long cazadorId) {
        var resena = descubrimiento.resena().orElseThrow();
        return new ResenaVista(descubrimiento.getCazador().getAlias(), resena.getCalificacion(),
                resena.getComentario(), resena.getFecha(),
                Objects.equals(descubrimiento.getCazador().getId(), cazadorId));
    }

    private static FichaCocina ficha(CocinaFantasma cocina, Estadisticas estadisticas, Coordenada referencia,
                                     LocalTime hora, Set<Long> propias) {
        CalificacionPorCocina calificacion = estadisticas.calificaciones().get(cocina.getId());
        var ubicacion = cocina.getUbicacion();
        return new FichaCocina(cocina.getId(), cocina.getNombre(), cocina.getCategoria(), cocina.getRareza(),
                ubicacion.getBarrio(), ubicacion.getLatitud(), ubicacion.getLongitud(), cocina.getHoraApertura(),
                cocina.getHoraCierre(), cocina.getCanales(), cocina.getFechaAlta(), cocina.estaAbierta(hora),
                calificacion == null ? 0 : calificacion.promedio(),
                calificacion == null ? 0 : calificacion.total(),
                estadisticas.cazas().getOrDefault(cocina.getId(), 0L),
                ubicacion.distanciaKmHasta(referencia), propias.contains(cocina.getId()));
    }

    private Estadisticas estadisticas() {
        Map<Long, Long> cazas = descubrimientos.cazasPorCocina().stream()
                .collect(Collectors.toMap(ConteoPorCocina::cocinaId, ConteoPorCocina::total));
        Map<Long, CalificacionPorCocina> calificaciones = descubrimientos.calificacionesPorCocina().stream()
                .collect(Collectors.toMap(CalificacionPorCocina::cocinaId, Function.identity()));
        return new Estadisticas(cazas, calificaciones);
    }

    private Set<Long> cocinasCazadasPor(Long cazadorId) {
        return cazadorId == null ? Set.of() : descubrimientos.cocinasCazadasPor(cazadorId);
    }

    private record Estadisticas(Map<Long, Long> cazas, Map<Long, CalificacionPorCocina> calificaciones) {
    }
}
