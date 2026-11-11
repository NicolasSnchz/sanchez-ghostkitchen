package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import co.edu.udes.ghostkitchen.dominio.modelo.CanalPedido;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Rareza;
import java.time.LocalTime;
import java.util.Set;

/** Datos editables de una cocina, tal como llegan del formulario del administrador. */
public record DatosCocina(
        String nombre,
        String descripcion,
        CategoriaCocina categoria,
        Rareza rareza,
        LocalTime horaApertura,
        LocalTime horaCierre,
        double latitud,
        double longitud,
        String barrio,
        Set<CanalPedido> canales) {

    public DatosCocina {
        canales = Set.copyOf(canales);
    }

    public static DatosCocina desde(CocinaFantasma cocina) {
        var ubicacion = cocina.getUbicacion();
        return new DatosCocina(cocina.getNombre(), cocina.getDescripcion(), cocina.getCategoria(),
                cocina.getRareza(), cocina.getHoraApertura(), cocina.getHoraCierre(), ubicacion.getLatitud(),
                ubicacion.getLongitud(), ubicacion.getBarrio(), cocina.getCanales());
    }

    /** Vuelca estos datos en el Builder de la cocina; la validacion final la hace el Builder. */
    public CocinaFantasma.Builder aplicarA(CocinaFantasma.Builder builder) {
        return builder.nombre(nombre)
                .descripcion(descripcion)
                .categoria(categoria)
                .rareza(rareza)
                .horario(horaApertura, horaCierre)
                .ubicacion(latitud, longitud, barrio)
                .canales(canales);
    }
}
