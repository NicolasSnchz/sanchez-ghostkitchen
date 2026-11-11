package co.edu.udes.ghostkitchen.presentacion.web;

import co.edu.udes.ghostkitchen.aplicacion.progreso.Novedad;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Convierte las novedades que reportan los observadores en frases para el
 * usuario. Los textos viven en messages.properties; aqui solo se eligen la clave
 * y los argumentos.
 */
@Component
public class TraductorNovedades {

    private final MessageSource mensajes;

    public TraductorNovedades(MessageSource mensajes) {
        this.mensajes = mensajes;
    }

    public List<String> traducir(List<Novedad> novedades) {
        Locale idioma = LocaleContextHolder.getLocale();
        return novedades.stream().map(novedad -> traducir(novedad, idioma)).toList();
    }

    String traducir(Novedad novedad, Locale idioma) {
        String referencia = switch (novedad.tipo()) {
            case INSIGNIA_GANADA, INSIGNIA_RETIRADA ->
                    mensajes.getMessage("insignia." + novedad.referencia() + ".nombre", null, idioma);
            case NIVEL_SUBIDO, NIVEL_BAJADO -> mensajes.getMessage("nivel." + novedad.referencia(), null, idioma);
            case RETO_COMPLETADO, RETO_REVERTIDO -> novedad.referencia();
        };
        return mensajes.getMessage("novedad." + novedad.tipo(), new Object[] {referencia, novedad.valor()}, idioma);
    }
}
