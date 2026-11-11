package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import co.edu.udes.ghostkitchen.dominio.orden.FichaCocina;
import java.util.List;

/** Cifras y listas que muestra la portada. */
public record ResumenPortada(
        long cocinasActivas,
        long abiertasAhora,
        long barrios,
        long cazasRegistradas,
        long cazadores,
        List<CazaReciente> cazasRecientes,
        List<FichaCocina> destacadas) {

    public ResumenPortada {
        cazasRecientes = List.copyOf(cazasRecientes);
        destacadas = List.copyOf(destacadas);
    }
}
