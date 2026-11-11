package co.edu.udes.ghostkitchen.aplicacion.logros;

import co.edu.udes.ghostkitchen.dominio.modelo.TipoInsignia;
import java.time.LocalDateTime;

/** Una insignia del catalogo junto con la fecha en que el cazador la gano, si ya la tiene. */
public record InsigniaVista(TipoInsignia tipo, LocalDateTime fechaObtencion) {

    public boolean obtenida() {
        return fechaObtencion != null;
    }
}
