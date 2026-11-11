package co.edu.udes.ghostkitchen.aplicacion.logros;

import co.edu.udes.ghostkitchen.dominio.modelo.InsigniaObtenida;
import co.edu.udes.ghostkitchen.dominio.modelo.TipoInsignia;
import co.edu.udes.ghostkitchen.persistencia.RepositorioInsignias;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta el catalogo de insignias marcando cuales ya gano un cazador. */
@Service
@Transactional(readOnly = true)
public class ServicioInsignias {

    private final RepositorioInsignias insignias;

    public ServicioInsignias(RepositorioInsignias insignias) {
        this.insignias = insignias;
    }

    public List<InsigniaVista> insigniasDe(Long cazadorId) {
        Map<TipoInsignia, LocalDateTime> obtenidas = new EnumMap<>(TipoInsignia.class);
        for (InsigniaObtenida insignia : insignias.findByCazadorId(cazadorId)) {
            obtenidas.put(insignia.getTipo(), insignia.getFechaObtencion());
        }
        return Arrays.stream(TipoInsignia.values())
                .map(tipo -> new InsigniaVista(tipo, obtenidas.get(tipo)))
                .toList();
    }
}
