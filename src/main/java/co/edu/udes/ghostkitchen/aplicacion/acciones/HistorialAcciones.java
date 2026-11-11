package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.aplicacion.progreso.NotificadorProgreso;
import co.edu.udes.ghostkitchen.aplicacion.progreso.Novedad;
import co.edu.udes.ghostkitchen.aplicacion.progreso.ServicioProgreso;
import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import co.edu.udes.ghostkitchen.dominio.modelo.AccionRegistrada;
import co.edu.udes.ghostkitchen.persistencia.RepositorioAcciones;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Invocador del patron Command. Ejecuta cualquier comando, deja constancia en el
 * historial y avisa a los observadores del progreso. Deshacer funciona como una
 * pila: siempre se revierte la ultima accion vigente, y solo dentro de la ventana
 * permitida.
 */
@Service
public class HistorialAcciones {

    public static final Duration VENTANA_PARA_DESHACER = Duration.ofMinutes(15);

    private final RepositorioAcciones acciones;
    private final RepositorioCazadores cazadores;
    private final FabricaComandos fabrica;
    private final ServicioProgreso progreso;
    private final NotificadorProgreso notificador;
    private final Clock reloj;

    public HistorialAcciones(RepositorioAcciones acciones, RepositorioCazadores cazadores,
                             FabricaComandos fabrica, ServicioProgreso progreso,
                             NotificadorProgreso notificador, Clock reloj) {
        this.acciones = acciones;
        this.cazadores = cazadores;
        this.fabrica = fabrica;
        this.progreso = progreso;
        this.notificador = notificador;
        this.reloj = reloj;
    }

    @Transactional
    public ResultadoAccion ejecutar(Comando comando) {
        Long cazadorId = comando.cazadorId();
        int puntosAntes = progreso.puntosDe(cazadorId);
        ResultadoComando resultado = comando.ejecutar();
        acciones.save(new AccionRegistrada(cazadores.getReferenceById(cazadorId), comando.tipo(),
                resultado.objetivoId(), resultado.detalle(), resultado.puntos(), LocalDateTime.now(reloj)));
        List<Novedad> novedades = notificador.notificar(cazadorId, puntosAntes);
        return new ResultadoAccion(comando.tipo(), resultado, novedades);
    }

    @Transactional
    public ResultadoDeshacer deshacerUltima(Long cazadorId) {
        AccionRegistrada ultima = acciones.findFirstByCazadorIdAndDeshechaFalseOrderByFechaHoraDescIdDesc(cazadorId)
                .orElseThrow(() -> new ReglaNegocioException("accion.nada-que-deshacer"));
        if (!ultima.sePuedeDeshacer(LocalDateTime.now(reloj), VENTANA_PARA_DESHACER)) {
            throw new ReglaNegocioException("accion.fuera-de-ventana", VENTANA_PARA_DESHACER.toMinutes());
        }
        int puntosAntes = progreso.puntosDe(cazadorId);
        fabrica.paraDeshacer(ultima).deshacer();
        ultima.marcarDeshecha();
        List<Novedad> novedades = notificador.notificar(cazadorId, puntosAntes);
        return new ResultadoDeshacer(ultima.getTipo(), ultima.getDetalle(), ultima.getPuntos(), novedades);
    }

    @Transactional(readOnly = true)
    public Optional<AccionDeshacible> accionDeshacible(Long cazadorId) {
        LocalDateTime ahora = LocalDateTime.now(reloj);
        return acciones.findFirstByCazadorIdAndDeshechaFalseOrderByFechaHoraDescIdDesc(cazadorId)
                .filter(accion -> accion.sePuedeDeshacer(ahora, VENTANA_PARA_DESHACER))
                .map(accion -> new AccionDeshacible(accion.getTipo(), accion.getDetalle(), accion.getPuntos(),
                        accion.getFechaHora().plus(VENTANA_PARA_DESHACER)));
    }

    @Transactional(readOnly = true)
    public List<AccionVista> recientes(Long cazadorId) {
        return acciones.findTop8ByCazadorIdOrderByFechaHoraDescIdDesc(cazadorId).stream()
                .map(accion -> new AccionVista(accion.getId(), accion.getTipo(), accion.getDetalle(),
                        accion.getPuntos(), accion.getFechaHora(), accion.isDeshecha()))
                .toList();
    }
}
