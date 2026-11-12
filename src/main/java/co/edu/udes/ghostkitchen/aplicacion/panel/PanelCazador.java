package co.edu.udes.ghostkitchen.aplicacion.panel;

import co.edu.udes.ghostkitchen.aplicacion.acciones.AccionDeshacible;
import co.edu.udes.ghostkitchen.aplicacion.acciones.AccionVista;
import co.edu.udes.ghostkitchen.aplicacion.logros.InsigniaVista;
import co.edu.udes.ghostkitchen.aplicacion.progreso.CazaVista;
import co.edu.udes.ghostkitchen.aplicacion.retos.RetoVista;
import co.edu.udes.ghostkitchen.dominio.modelo.Nivel;
import co.edu.udes.ghostkitchen.dominio.modelo.ProgresoCazador;
import java.util.List;
import java.util.Optional;

/** Lo que muestra el perfil del cazador, reunido por la fachada en un solo objeto. */
public record PanelCazador(
        String alias,
        String nombre,
        int puntos,
        Nivel nivel,
        int puntosParaSubir,
        int porcentajeNivel,
        ProgresoCazador progreso,
        List<CazaVista> cazas,
        List<InsigniaVista> insignias,
        List<RetoVista> retos,
        List<AccionVista> actividad,
        Optional<AccionDeshacible> deshacible) {

    public PanelCazador {
        cazas = List.copyOf(cazas);
        insignias = List.copyOf(insignias);
        retos = List.copyOf(retos);
        actividad = List.copyOf(actividad);
    }

    public Optional<Nivel> siguienteNivel() {
        return nivel.siguiente();
    }

    public long insigniasObtenidas() {
        return insignias.stream().filter(InsigniaVista::obtenida).count();
    }
}
