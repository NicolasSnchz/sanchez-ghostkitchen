package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import co.edu.udes.ghostkitchen.dominio.orden.FichaCocina;
import java.util.List;

/** Todo lo que muestra la pagina de una cocina para un visitante concreto. */
public record DetalleCocina(
        FichaCocina ficha,
        String descripcion,
        List<ResenaVista> resenas,
        EstadoCaza estadoCaza,
        boolean puedeResenar) {

    public DetalleCocina {
        resenas = List.copyOf(resenas);
    }
}
