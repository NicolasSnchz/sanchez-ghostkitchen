package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.ranking.ServicioRanking;
import co.edu.udes.ghostkitchen.seguridad.CuentaAutenticada;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Tabla de posiciones publica. Si el visitante es cazador, se resalta su fila. */
@Controller
public class RankingControlador {

    static final int POSICIONES_VISIBLES = 50;

    private final ServicioRanking ranking;

    public RankingControlador(ServicioRanking ranking) {
        this.ranking = ranking;
    }

    @GetMapping("/ranking")
    public String ranking(@AuthenticationPrincipal CuentaAutenticada cuenta, Model modelo) {
        modelo.addAttribute("posiciones", ranking.clasificacion(POSICIONES_VISIBLES));
        modelo.addAttribute("miId", Sesion.cazadorId(cuenta));
        return "ranking";
    }
}
