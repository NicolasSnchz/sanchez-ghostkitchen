package co.edu.udes.ghostkitchen.presentacion.api;

import co.edu.udes.ghostkitchen.aplicacion.catalogo.DetalleCocina;
import co.edu.udes.ghostkitchen.aplicacion.catalogo.ResenaVista;
import java.time.LocalDateTime;
import java.util.List;

/** Ficha completa de una cocina con sus resenas publicas. */
public record CocinaDetalleDto(CocinaResumenDto cocina, String descripcion, List<ResenaDto> resenas) {

    public CocinaDetalleDto {
        resenas = List.copyOf(resenas);
    }

    static CocinaDetalleDto desde(DetalleCocina detalle) {
        return new CocinaDetalleDto(CocinaResumenDto.desde(detalle.ficha()), detalle.descripcion(),
                detalle.resenas().stream().map(ResenaDto::desde).toList());
    }

    /** Resena tal como se publica: alias, calificacion, comentario y fecha. */
    public record ResenaDto(String alias, int calificacion, String comentario, LocalDateTime fecha) {

        static ResenaDto desde(ResenaVista resena) {
            return new ResenaDto(resena.alias(), resena.calificacion(), resena.comentario(), resena.fecha());
        }
    }
}
