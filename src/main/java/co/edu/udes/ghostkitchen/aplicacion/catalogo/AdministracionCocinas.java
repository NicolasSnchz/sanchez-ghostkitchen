package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import co.edu.udes.ghostkitchen.dominio.excepcion.RecursoNoEncontradoException;
import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.persistencia.ConteoPorCocina;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Mantenimiento del catalogo por parte del administrador. */
@Service
@Transactional
public class AdministracionCocinas {

    private final RepositorioCocinas cocinas;
    private final RepositorioDescubrimientos descubrimientos;
    private final Clock reloj;

    public AdministracionCocinas(RepositorioCocinas cocinas, RepositorioDescubrimientos descubrimientos, Clock reloj) {
        this.cocinas = cocinas;
        this.descubrimientos = descubrimientos;
        this.reloj = reloj;
    }

    @Transactional(readOnly = true)
    public List<CocinaAdmin> listar() {
        Map<Long, Long> cazas = descubrimientos.cazasPorCocina().stream()
                .collect(Collectors.toMap(ConteoPorCocina::cocinaId, ConteoPorCocina::total));
        return cocinas.findAllByOrderByNombreAsc().stream()
                .map(cocina -> new CocinaAdmin(cocina.getId(), cocina.getNombre(), cocina.getCategoria(),
                        cocina.getRareza(), cocina.getUbicacion().getBarrio(), cocina.getHoraApertura(),
                        cocina.getHoraCierre(), cocina.isActiva(), cazas.getOrDefault(cocina.getId(), 0L)))
                .toList();
    }

    @Transactional(readOnly = true)
    public DatosCocina datosDe(Long cocinaId) {
        return DatosCocina.desde(buscar(cocinaId));
    }

    public Long registrar(DatosCocina datos) {
        if (cocinas.existsByNombreIgnoreCase(datos.nombre().strip())) {
            throw new ReglaNegocioException("cocina.nombre-en-uso");
        }
        CocinaFantasma cocina = datos.aplicarA(CocinaFantasma.builder())
                .registradaEl(LocalDate.now(reloj))
                .build();
        return cocinas.save(cocina).getId();
    }

    public void actualizar(Long cocinaId, DatosCocina datos) {
        CocinaFantasma cocina = buscar(cocinaId);
        if (cocinas.existsByNombreIgnoreCaseAndIdNot(datos.nombre().strip(), cocinaId)) {
            throw new ReglaNegocioException("cocina.nombre-en-uso");
        }
        cocina.actualizar(datos.aplicarA(CocinaFantasma.builder()));
    }

    public void cambiarEstado(Long cocinaId, boolean activa) {
        CocinaFantasma cocina = buscar(cocinaId);
        if (activa) {
            cocina.activar();
        } else {
            cocina.desactivar();
        }
    }

    /** Una cocina ya cazada no se borra: se perderian puntos y resenas. Se desactiva en su lugar. */
    public void eliminar(Long cocinaId) {
        CocinaFantasma cocina = buscar(cocinaId);
        if (descubrimientos.existsByCocinaId(cocinaId)) {
            throw new ReglaNegocioException("cocina.con-cazas");
        }
        cocinas.delete(cocina);
    }

    private CocinaFantasma buscar(Long cocinaId) {
        return cocinas.findById(cocinaId).orElseThrow(() -> new RecursoNoEncontradoException("Cocina", cocinaId));
    }
}
