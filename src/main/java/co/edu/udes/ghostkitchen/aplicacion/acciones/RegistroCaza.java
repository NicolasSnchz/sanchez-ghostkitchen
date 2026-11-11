package co.edu.udes.ghostkitchen.aplicacion.acciones;

import co.edu.udes.ghostkitchen.dominio.excepcion.RecursoNoEncontradoException;
import co.edu.udes.ghostkitchen.dominio.excepcion.ReglaNegocioException;
import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import co.edu.udes.ghostkitchen.dominio.modelo.Descubrimiento;
import co.edu.udes.ghostkitchen.dominio.puntos.CalculadoraPuntos;
import co.edu.udes.ghostkitchen.dominio.puntos.ContextoCaza;
import co.edu.udes.ghostkitchen.dominio.puntos.Puntaje;
import co.edu.udes.ghostkitchen.dominio.puntos.RachaDiaria;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCazadores;
import co.edu.udes.ghostkitchen.persistencia.RepositorioCocinas;
import co.edu.udes.ghostkitchen.persistencia.RepositorioDescubrimientos;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Receptor del comando de cazar: valida las reglas de la caza, calcula los puntos
 * con la cadena de bonificaciones y guarda el descubrimiento.
 */
@Component
public class RegistroCaza {

    private final RepositorioCocinas cocinas;
    private final RepositorioCazadores cazadores;
    private final RepositorioDescubrimientos descubrimientos;
    private final CalculadoraPuntos calculadora;
    private final Clock reloj;

    public RegistroCaza(RepositorioCocinas cocinas, RepositorioCazadores cazadores,
                        RepositorioDescubrimientos descubrimientos, CalculadoraPuntos calculadora, Clock reloj) {
        this.cocinas = cocinas;
        this.cazadores = cazadores;
        this.descubrimientos = descubrimientos;
        this.calculadora = calculadora;
        this.reloj = reloj;
    }

    public CazaRegistrada descubrir(Long cazadorId, Long cocinaId) {
        CocinaFantasma cocina = cocinas.findById(cocinaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cocina", cocinaId));
        LocalDateTime ahora = LocalDateTime.now(reloj);
        validar(cazadorId, cocina, ahora);
        ContextoCaza contexto = new ContextoCaza(cocina.getRareza(), !descubrimientos.existsByCocinaId(cocinaId),
                diasDeRacha(cazadorId, ahora.toLocalDate()), ahora.toLocalTime());
        Puntaje puntaje = calculadora.calcular(contexto);
        descubrimientos.save(Descubrimiento.registrar(cazadores.getReferenceById(cazadorId), cocina, ahora,
                puntaje.total()));
        return new CazaRegistrada(cocina.getId(), cocina.getNombre(), puntaje);
    }

    /** Borra el descubrimiento y, con el, la resena que tuviera. */
    public void olvidar(Long cazadorId, Long cocinaId) {
        descubrimientos.findByCazadorIdAndCocinaId(cazadorId, cocinaId).ifPresent(descubrimientos::delete);
    }

    private void validar(Long cazadorId, CocinaFantasma cocina, LocalDateTime ahora) {
        if (!cocina.isActiva()) {
            throw new ReglaNegocioException("caza.cocina-inactiva");
        }
        if (descubrimientos.existsByCazadorIdAndCocinaId(cazadorId, cocina.getId())) {
            throw new ReglaNegocioException("caza.repetida");
        }
        if (!cocina.estaAbierta(ahora.toLocalTime())) {
            throw new ReglaNegocioException("caza.cocina-cerrada");
        }
        if (descubrimientos.cazasDelDia(cazadorId, ahora.toLocalDate()) >= Descubrimiento.MAXIMO_CAZAS_POR_DIA) {
            throw new ReglaNegocioException("caza.limite-diario", Descubrimiento.MAXIMO_CAZAS_POR_DIA);
        }
    }

    private int diasDeRacha(Long cazadorId, LocalDate hoy) {
        List<LocalDate> dias = descubrimientos.fechasDeCazaDe(cazadorId).stream()
                .map(LocalDateTime::toLocalDate)
                .toList();
        return RachaDiaria.diasConsecutivosHasta(dias, hoy);
    }
}
