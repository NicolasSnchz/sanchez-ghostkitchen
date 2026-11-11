package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.aplicacion.retos.RegistroParticipaciones;
import co.edu.udes.ghostkitchen.dominio.modelo.Reto;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;

/** Comando concreto: inscribirse en un reto. No suma puntos al instante; la recompensa llega al cumplir la meta. */
final class UnirseARetoComando implements Comando {

    private final RegistroParticipaciones receptor;
    private final Long cazadorId;
    private final Long retoId;

    UnirseARetoComando(RegistroParticipaciones receptor, Long cazadorId, Long retoId) {
        this.receptor = receptor;
        this.cazadorId = cazadorId;
        this.retoId = retoId;
    }

    @Override
    public TipoAccion tipo() {
        return TipoAccion.UNIRSE_A_RETO;
    }

    @Override
    public Long cazadorId() {
        return cazadorId;
    }

    @Override
    public ResultadoComando ejecutar() {
        Reto reto = receptor.inscribir(cazadorId, retoId);
        return ResultadoComando.sinDesglose(reto.getId(), reto.getTitulo(), 0);
    }

    @Override
    public void deshacer() {
        receptor.retirar(cazadorId, retoId);
    }
}
