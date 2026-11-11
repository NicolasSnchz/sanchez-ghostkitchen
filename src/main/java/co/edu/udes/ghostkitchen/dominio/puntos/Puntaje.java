package co.edu.udes.ghostkitchen.dominio.puntos;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado inmutable del calculo de puntos de una caza: el total y el desglose
 * de como se llego a el. Cada operacion devuelve un puntaje nuevo.
 */
public final class Puntaje {

    private final int total;
    private final List<Linea> desglose;

    private Puntaje(int total, List<Linea> desglose) {
        this.total = total;
        this.desglose = List.copyOf(desglose);
    }

    public static Puntaje base(int puntos) {
        if (puntos < 0) {
            throw new IllegalArgumentException("Los puntos base no pueden ser negativos");
        }
        return new Puntaje(puntos, List.of(new Linea(ConceptoPuntaje.BASE, puntos)));
    }

    /** Suma un bono fijo. */
    public Puntaje mas(ConceptoPuntaje concepto, int puntos) {
        return con(concepto, puntos);
    }

    /** Multiplica el total acumulado; en el desglose queda solo la diferencia que aporta. */
    public Puntaje multiplicadoPor(ConceptoPuntaje concepto, double factor) {
        int aumento = (int) Math.round(total * factor) - total;
        return con(concepto, aumento);
    }

    private Puntaje con(ConceptoPuntaje concepto, int puntos) {
        List<Linea> lineas = new ArrayList<>(desglose);
        lineas.add(new Linea(concepto, puntos));
        return new Puntaje(total + puntos, lineas);
    }

    public int total() {
        return total;
    }

    public List<Linea> desglose() {
        return desglose;
    }

    /** Un renglon del desglose: por que concepto y cuantos puntos. */
    public record Linea(ConceptoPuntaje concepto, int puntos) {
    }
}
