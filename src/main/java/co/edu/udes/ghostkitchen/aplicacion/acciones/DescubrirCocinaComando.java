package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.dominio.modelo.TipoAccion;

/** Comando concreto: cazar una cocina. Guarda quien caza y que cocina, lo justo para revertirlo. */
final class DescubrirCocinaComando implements Comando {

    private final RegistroCaza receptor;
    private final Long cazadorId;
    private final Long cocinaId;

    DescubrirCocinaComando(RegistroCaza receptor, Long cazadorId, Long cocinaId) {
        this.receptor = receptor;
        this.cazadorId = cazadorId;
        this.cocinaId = cocinaId;
    }

    @Override
    public TipoAccion tipo() {
        return TipoAccion.DESCUBRIR_COCINA;
    }

    @Override
    public Long cazadorId() {
        return cazadorId;
    }

    @Override
    public ResultadoComando ejecutar() {
        CazaRegistrada caza = receptor.descubrir(cazadorId, cocinaId);
        return new ResultadoComando(caza.cocinaId(), caza.nombreCocina(), caza.puntaje().total(),
                caza.puntaje().desglose());
    }

    @Override
    public void deshacer() {
        receptor.olvidar(cazadorId, cocinaId);
    }
}
