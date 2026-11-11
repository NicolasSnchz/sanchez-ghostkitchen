package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.aplicacion.retos.RegistroParticipaciones;
import co.edu.udes.ghostkitchen.dominio.modelo.AccionRegistrada;
import org.springframework.stereotype.Component;

/**
 * Fabrica concreta: decide que comando crear y le conecta su receptor. El switch
 * sobre el tipo de accion vive solo aqui y es exhaustivo: si se agrega un tipo
 * nuevo, el compilador obliga a cubrirlo.
 */
@Component
public class FabricaComandosCazador implements FabricaComandos {

    private final RegistroCaza registroCaza;
    private final RegistroResenas registroResenas;
    private final RegistroParticipaciones registroParticipaciones;

    public FabricaComandosCazador(RegistroCaza registroCaza, RegistroResenas registroResenas,
                                  RegistroParticipaciones registroParticipaciones) {
        this.registroCaza = registroCaza;
        this.registroResenas = registroResenas;
        this.registroParticipaciones = registroParticipaciones;
    }

    @Override
    public Comando descubrirCocina(Long cazadorId, Long cocinaId) {
        return new DescubrirCocinaComando(registroCaza, cazadorId, cocinaId);
    }

    @Override
    public Comando publicarResena(Long cazadorId, Long cocinaId, NuevaResena resena) {
        return new PublicarResenaComando(registroResenas, cazadorId, cocinaId, resena);
    }

    @Override
    public Comando unirseAReto(Long cazadorId, Long retoId) {
        return new UnirseARetoComando(registroParticipaciones, cazadorId, retoId);
    }

    @Override
    public Comando paraDeshacer(AccionRegistrada accion) {
        Long cazadorId = accion.getCazador().getId();
        Long objetivoId = accion.getObjetivoId();
        return switch (accion.getTipo()) {
            case DESCUBRIR_COCINA -> descubrirCocina(cazadorId, objetivoId);
            case PUBLICAR_RESENA -> PublicarResenaComando.soloParaDeshacer(registroResenas, cazadorId, objetivoId);
            case UNIRSE_A_RETO -> unirseAReto(cazadorId, objetivoId);
        };
    }
}
