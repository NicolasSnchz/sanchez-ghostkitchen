package co.edu.udes.ghostkitchen.dominio.modelo;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Fotografia de lo que un cazador ha logrado hasta ahora. Las insignias se evaluan
 * contra este resumen, no contra la base de datos, y por eso sus reglas se prueban
 * sin ningun doble de prueba.
 */
public record ProgresoCazador(
        int cocinasCazadas,
        int categoriasDistintas,
        int barriosDistintos,
        int cazasEnHoraFantasma,
        int cocinasLegendarias,
        int resenasPublicadas,
        int retosCompletados) {

    public static final ProgresoCazador SIN_ACTIVIDAD = new ProgresoCazador(0, 0, 0, 0, 0, 0, 0);

    /** Resume los descubrimientos de un cazador; los retos completados llegan ya contados. */
    public static ProgresoCazador desde(Collection<Descubrimiento> descubrimientos, int retosCompletados) {
        Set<CategoriaCocina> categorias = EnumSet.noneOf(CategoriaCocina.class);
        Set<String> barrios = new HashSet<>();
        int nocturnas = 0;
        int legendarias = 0;
        int resenas = 0;
        for (Descubrimiento descubrimiento : descubrimientos) {
            CocinaFantasma cocina = descubrimiento.getCocina();
            categorias.add(cocina.getCategoria());
            barrios.add(cocina.getUbicacion().getBarrio());
            nocturnas += descubrimiento.ocurrioEnHoraFantasma() ? 1 : 0;
            legendarias += cocina.getRareza() == Rareza.LEGENDARIA ? 1 : 0;
            resenas += descubrimiento.tieneResena() ? 1 : 0;
        }
        return new ProgresoCazador(descubrimientos.size(), categorias.size(), barrios.size(),
                nocturnas, legendarias, resenas, retosCompletados);
    }
}
