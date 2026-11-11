package co.edu.udes.ghostkitchen.presentacion.comun;

import co.edu.udes.ghostkitchen.aplicacion.catalogo.FiltroCocinas;
import co.edu.udes.ghostkitchen.dominio.modelo.CategoriaCocina;
import co.edu.udes.ghostkitchen.dominio.modelo.Coordenada;
import co.edu.udes.ghostkitchen.dominio.orden.TipoOrden;
import java.util.Locale;

/**
 * Parametros de busqueda que llegan por la URL, compartidos por la vista del
 * explorador y por la API. Todo es opcional y tolerante: un valor que no se
 * entiende se ignora en lugar de romper la pagina.
 */
public class ParametrosBusqueda {

    private static final int LONGITUD_MAXIMA_TEXTO = 60;

    private String q;
    private String categoria;
    private boolean abiertas;
    private String orden;
    private Double lat;
    private Double lng;

    public FiltroCocinas aFiltro() {
        return new FiltroCocinas(textoAcotado(), categoriaElegida(), abiertas, referencia(), TipoOrden.desde(orden));
    }

    /** Hay ubicacion solo si llegan ambas coordenadas y son validas. */
    public boolean tieneUbicacion() {
        return referencia() != null;
    }

    private Coordenada referencia() {
        if (lat == null || lng == null) {
            return null;
        }
        try {
            return new Coordenada(lat, lng);
        } catch (IllegalArgumentException fueraDeRango) {
            return null;
        }
    }

    private CategoriaCocina categoriaElegida() {
        if (categoria == null || categoria.isBlank()) {
            return null;
        }
        try {
            return CategoriaCocina.valueOf(categoria.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException desconocida) {
            return null;
        }
    }

    private String textoAcotado() {
        if (q == null) {
            return "";
        }
        String limpio = q.strip();
        return limpio.length() > LONGITUD_MAXIMA_TEXTO ? limpio.substring(0, LONGITUD_MAXIMA_TEXTO) : limpio;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isAbiertas() {
        return abiertas;
    }

    public void setAbiertas(boolean abiertas) {
        this.abiertas = abiertas;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
