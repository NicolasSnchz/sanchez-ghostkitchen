package co.edu.udes.ghostkitchen.aplicacion.progreso;

import co.edu.udes.ghostkitchen.dominio.modelo.Descubrimiento;
import co.edu.udes.ghostkitchen.dominio.modelo.Nivel;
import co.edu.udes.ghostkitchen.dominio.modelo.ProgresoCazador;
import co.edu.udes.ghostkitchen.dominio.puntos.PuntosCazador;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import co.edu.udes.ghostkitchen.persistencia.RepositorioParticipaciones;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Calcula, a partir de lo que el cazador ha hecho, sus puntos, su nivel y el
 * resumen que usan las insignias. Nada de esto se guarda: siempre se deriva,
 * asi no hay dos fuentes de verdad que puedan contradecirse.
 */
@Service
@Transactional(readOnly = true)
public class ServicioProgreso {

    private final RepositorioDescubrimientos descubrimientos;
    private final RepositorioParticipaciones participaciones;

    public ServicioProgreso(RepositorioDescubrimientos descubrimientos,
                            RepositorioParticipaciones participaciones) {
        this.descubrimientos = descubrimientos;
        this.participaciones = participaciones;
    }

    public int puntosDe(Long cazadorId) {
        return PuntosCazador.total(
                descubrimientos.puntosDeCazasDe(cazadorId),
                descubrimientos.resenasDe(cazadorId),
                participaciones.recompensasDe(cazadorId));
    }

    public Nivel nivelDe(Long cazadorId) {
        return Nivel.paraPuntos(puntosDe(cazadorId));
    }

    public ProgresoCazador progresoDe(Long cazadorId) {
        int retosCompletados = Math.toIntExact(
                participaciones.countByCazadorIdAndFechaCompletadoIsNotNull(cazadorId));
        return ProgresoCazador.desde(descubrimientos.historialDe(cazadorId), retosCompletados);
    }

    public List<CazaVista> cazasDe(Long cazadorId) {
        return descubrimientos.historialDe(cazadorId).stream()
                .map(ServicioProgreso::aVista)
                .toList();
    }

    private static CazaVista aVista(Descubrimiento descubrimiento) {
        var cocina = descubrimiento.getCocina();
        return new CazaVista(cocina.getId(), cocina.getNombre(), cocina.getCategoria(), cocina.getRareza(),
                cocina.getUbicacion().getBarrio(), descubrimiento.getFechaHora(),
                descubrimiento.getPuntosObtenidos(), descubrimiento.tieneResena(),
                descubrimiento.ocurrioEnHoraFantasma(), cocina.isActiva());
    }
}
