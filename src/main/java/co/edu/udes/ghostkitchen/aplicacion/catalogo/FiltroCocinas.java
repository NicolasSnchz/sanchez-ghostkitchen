package co.edu.udes.ghostkitchen.aplicacion.catalogo;

import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Coordenada;
import co.edu.udes.ghostkitchen.dominio.orden.FichaCocina;
import co.edu.udes.ghostkitchen.dominio.orden.TipoOrden;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Lo que el visitante pidio al explorar: texto a buscar, categoria, si solo quiere
 * cocinas abiertas, desde donde mide la distancia y como quiere ordenar.
 */
public record FiltroCocinas(String texto, CategoriaCocina categoria, boolean soloAbiertas,
                            Coordenada referencia, TipoOrden orden) {

    private static final Pattern MARCAS_DIACRITICAS = Pattern.compile("\\p{M}");

    public FiltroCocinas {
        texto = texto == null ? "" : texto.strip();
        referencia = referencia == null ? Coordenada.CENTRO_DE_CUCUTA : referencia;
        orden = orden == null ? TipoOrden.CERCANIA : orden;
    }

    public static FiltroCocinas ordenadasPor(TipoOrden orden) {
        return new FiltroCocinas("", null, false, Coordenada.CENTRO_DE_CUCUTA, orden);
    }

    /** Busca sin distinguir mayusculas ni tildes, en el nombre y en el barrio. */
    public boolean admite(FichaCocina ficha) {
        return (categoria == null || ficha.categoria() == categoria)
                && (!soloAbiertas || ficha.abierta())
                && (texto.isEmpty() || contiene(ficha.nombre()) || contiene(ficha.barrio()));
    }

    private boolean contiene(String valor) {
        return normalizar(valor).contains(normalizar(texto));
    }

    static String normalizar(String valor) {
        String sinTildes = MARCAS_DIACRITICAS.matcher(Normalizer.normalize(valor, Normalizer.Form.NFD)).replaceAll("");
        return sinTildes.toLowerCase(Locale.ROOT);
    }
}
