package co.edu.udes.ghostkitchen.aplicacion.panel;

import co.edu.udes.ghostkitchen.aplicacion.acciones.HistorialAcciones;
import co.edu.udes.ghostkitchen.aplicacion.cuentas.CuentaUsuario;
import co.edu.udes.ghostkitchen.aplicacion.cuentas.ServicioCuentas;
import co.edu.udes.ghostkitchen.aplicacion.logros.ServicioInsignias;
import co.edu.udes.ghostkitchen.aplicacion.progreso.ServicioProgreso;
import co.edu.udes.ghostkitchen.aplicacion.retos.ServicioRetos;
import co.edu.udes.ghostkitchen.dominio.excepcion.RecursoNoEncontradoException;
import co.edu.udes.ghostkitchen.dominio.modelo.Nivel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Patron Facade. El perfil necesita datos de cinco subsistemas (cuentas, progreso,
 * insignias, retos e historial); esta fachada los consulta y entrega un solo objeto,
 * asi el controlador depende de una clase y no de cinco.
 */
@Service
@Transactional(readOnly = true)
public class PanelCazadorFacade {

    private final ServicioCuentas cuentas;
    private final ServicioProgreso progreso;
    private final ServicioInsignias insignias;
    private final ServicioRetos retos;
    private final HistorialAcciones historial;

    public PanelCazadorFacade(ServicioCuentas cuentas, ServicioProgreso progreso, ServicioInsignias insignias,
                              ServicioRetos retos, HistorialAcciones historial) {
        this.cuentas = cuentas;
        this.progreso = progreso;
        this.insignias = insignias;
        this.retos = retos;
        this.historial = historial;
    }

    public PanelCazador panelDe(Long cazadorId) {
        CuentaUsuario cuenta = cuentas.cuentaPorId(cazadorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cazador", cazadorId));
        int puntos = progreso.puntosDe(cazadorId);
        Nivel nivel = Nivel.paraPuntos(puntos);
        return new PanelCazador(cuenta.alias(), cuenta.nombre(), puntos, nivel, nivel.puntosParaSubir(puntos),
                nivel.porcentajeHaciaElSiguiente(puntos), progreso.progresoDe(cazadorId),
                progreso.cazasDe(cazadorId), insignias.insigniasDe(cazadorId), retos.inscritosDe(cazadorId),
                historial.recientes(cazadorId), historial.accionDeshacible(cazadorId));
    }
}
