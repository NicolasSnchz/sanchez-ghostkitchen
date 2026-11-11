package co.edu.udes.ghostkitchen.dominio.modelo;

import java.util.function.Predicate;

/**
 * Catalogo de logros. Cada insignia lleva consigo su propia regla, de modo que
 * agregar una nueva es agregar una constante: ningun servicio tiene un switch
 * sobre el tipo de insignia.
 */
public enum TipoInsignia {
    PRIMER_HALLAZGO(progreso -> progreso.cocinasCazadas() >= 1),
    RASTREADOR(progreso -> progreso.cocinasCazadas() >= 5),
    CAZADOR_EXPERTO(progreso -> progreso.cocinasCazadas() >= 10),
    PALADAR_VARIADO(progreso -> progreso.categoriasDistintas() >= 4),
    EXPLORADOR_DE_BARRIOS(progreso -> progreso.barriosDistintos() >= 3),
    NOCTAMBULO(progreso -> progreso.cazasEnHoraFantasma() >= 1),
    LEYENDA_LOCAL(progreso -> progreso.cocinasLegendarias() >= 1),
    CRITICO_CULINARIO(progreso -> progreso.resenasPublicadas() >= 3),
    RETADOR(progreso -> progreso.retosCompletados() >= 1);

    private final Predicate<ProgresoCazador> regla;

    TipoInsignia(Predicate<ProgresoCazador> regla) {
        this.regla = regla;
    }

    public boolean laMerece(ProgresoCazador progreso) {
        return regla.test(progreso);
    }
}
