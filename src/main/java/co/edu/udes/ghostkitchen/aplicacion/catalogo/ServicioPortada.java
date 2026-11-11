package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import co.edu.udes.ghostkitchen.dominio.orden.FichaCocina;
import co.edu.udes.ghostkitchen.dominio.orden.TipoOrden;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Reune lo que muestra la portada: cifras del catalogo, cazas recientes y cocinas destacadas. */
@Service
@Transactional(readOnly = true)
public class ServicioPortada {

    static final int CAZAS_RECIENTES = 3;
    static final int DESTACADAS = 3;

    private final ServicioCatalogo catalogo;
    private final RepositorioCocinas cocinas;
    private final RepositorioDescubrimientos descubrimientos;
    private final RepositorioCazadores cazadores;

    public ServicioPortada(ServicioCatalogo catalogo, RepositorioCocinas cocinas,
                           RepositorioDescubrimientos descubrimientos, RepositorioCazadores cazadores) {
        this.catalogo = catalogo;
        this.cocinas = cocinas;
        this.descubrimientos = descubrimientos;
        this.cazadores = cazadores;
    }

    public ResumenPortada resumen() {
        List<FichaCocina> porPopularidad = catalogo.explorar(FiltroCocinas.ordenadasPor(TipoOrden.POPULARIDAD), null);
        long abiertas = porPopularidad.stream().filter(FichaCocina::abierta).count();
        List<CazaReciente> recientes = descubrimientos.masRecientes(PageRequest.of(0, CAZAS_RECIENTES)).stream()
                .filter(descubrimiento -> descubrimiento.getCocina().isActiva())
                .map(descubrimiento -> new CazaReciente(descubrimiento.getCazador().getAlias(),
                        descubrimiento.getCocina().getId(), descubrimiento.getCocina().getNombre(),
                        descubrimiento.getCocina().getCategoria(),
                        descubrimiento.getCocina().getUbicacion().getBarrio(),
                        descubrimiento.getFechaHora(), descubrimiento.getPuntosObtenidos()))
                .toList();
        return new ResumenPortada(cocinas.countByActivaTrue(), abiertas, cocinas.barriosConCocinasActivas(),
                descubrimientos.count(), cazadores.count(), recientes,
                porPopularidad.stream().limit(DESTACADAS).toList());
    }
}
