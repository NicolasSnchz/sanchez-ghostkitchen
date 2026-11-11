package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import co.edu.udes.ghostkitchen.dominio.modelo.Descubrimiento;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/** Receptor del comando de resenar: solo se resena lo que ya se cazo, y una sola vez. */
@Component
public class RegistroResenas {

    private final RepositorioDescubrimientos descubrimientos;
    private final Clock reloj;

    public RegistroResenas(RepositorioDescubrimientos descubrimientos, Clock reloj) {
        this.descubrimientos = descubrimientos;
        this.reloj = reloj;
    }

    /** Publica la resena y devuelve el nombre de la cocina resenada. */
    public String publicar(Long cazadorId, Long cocinaId, NuevaResena resena) {
        Descubrimiento descubrimiento = descubrimientos.findByCazadorIdAndCocinaId(cazadorId, cocinaId)
                .orElseThrow(() -> new ReglaNegocioException("resena.sin-caza"));
        descubrimiento.resenar(resena.calificacion(), resena.comentario(), LocalDateTime.now(reloj));
        return descubrimiento.getCocina().getNombre();
    }

    public void retirar(Long cazadorId, Long cocinaId) {
        descubrimientos.findByCazadorIdAndCocinaId(cazadorId, cocinaId)
                .ifPresent(Descubrimiento::retirarResena);
    }
}
