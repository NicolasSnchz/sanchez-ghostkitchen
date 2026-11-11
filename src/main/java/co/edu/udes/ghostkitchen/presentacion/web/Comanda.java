package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.acciones.ResultadoComando;
import co.edu.udes.ghostkitchen.dominio.puntos.Puntaje;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Lo que se imprime en el ticket despues de cazar: la cocina, cada concepto de
 * puntos y el total. Es una vista, no guarda nada.
 */
public record Comanda(Long cocinaId, String cocina, List<Puntaje.Linea> lineas, int total, LocalDateTime hora) {

    public Comanda {
        lineas = List.copyOf(lineas);
    }

    static Comanda de(ResultadoComando resultado, LocalDateTime hora) {
        return new Comanda(resultado.objetivoId(), resultado.detalle(), resultado.desglose(), resultado.puntos(),
                hora);
    }

    /** Numero corto para el encabezado del ticket, derivado de la cocina y la hora. */
    public String numero() {
        return String.format("%03d-%02d%02d", cocinaId % 1000, hora.getHour(), hora.getMinute());
    }
}
