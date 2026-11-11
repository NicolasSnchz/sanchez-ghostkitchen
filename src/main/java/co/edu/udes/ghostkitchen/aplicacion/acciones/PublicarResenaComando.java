package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.dominio.modelo.Resena;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;

/**
 * Comando concreto: publicar una resena. El historial solo persiste lo necesario
 * para deshacer (cazador y cocina), asi que un comando reconstruido desde el
 * historial no trae contenido y solo sirve para revertir.
 */
final class PublicarResenaComando implements Comando {

    private final RegistroResenas receptor;
    private final Long cazadorId;
    private final Long cocinaId;
    private final NuevaResena resena;

    PublicarResenaComando(RegistroResenas receptor, Long cazadorId, Long cocinaId, NuevaResena resena) {
        this.receptor = receptor;
        this.cazadorId = cazadorId;
        this.cocinaId = cocinaId;
        this.resena = resena;
    }

    static PublicarResenaComando soloParaDeshacer(RegistroResenas receptor, Long cazadorId, Long cocinaId) {
        return new PublicarResenaComando(receptor, cazadorId, cocinaId, null);
    }

    @Override
    public TipoAccion tipo() {
        return TipoAccion.PUBLICAR_RESENA;
    }

    @Override
    public Long cazadorId() {
        return cazadorId;
    }

    @Override
    public ResultadoComando ejecutar() {
        if (resena == null) {
            throw new IllegalStateException("Un comando reconstruido desde el historial solo puede deshacerse");
        }
        String nombreCocina = receptor.publicar(cazadorId, cocinaId, resena);
        return ResultadoComando.sinDesglose(cocinaId, nombreCocina, Resena.PUNTOS_POR_RESENA);
    }

    @Override
    public void deshacer() {
        receptor.retirar(cazadorId, cocinaId);
    }
}
