package co.edu.udes.ghostkitchen.presentacion.api;

import co.edu.udes.ghostkitchen.aplicacion.ranking.ServicioRanking;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Ranking publico. El limite se acota entre 1 y 50 para no devolver listas sin fin. */
@RestController
@RequestMapping("/api/v1/ranking")
public class RankingApi {

    static final int LIMITE_MAXIMO = 50;

    private final ServicioRanking ranking;

    public RankingApi(ServicioRanking ranking) {
        this.ranking = ranking;
    }

    @GetMapping
    public List<PosicionRankingDto> clasificacion(@RequestParam(defaultValue = "10") int limite) {
        int acotado = Math.max(1, Math.min(limite, LIMITE_MAXIMO));
        return ranking.clasificacion(acotado).stream().map(PosicionRankingDto::desde).toList();
    }
}
