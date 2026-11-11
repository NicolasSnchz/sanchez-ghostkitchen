package co.edu.udes.ghostkitchen.persistencia;

import co.edu.udes.ghostkitchen.dominio.modelo.Descubrimiento;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Acceso a los descubrimientos. Combina metodos derivados del nombre, para las
 * consultas simples, con JPQL explicito donde hay agregaciones o se necesita
 * traer asociaciones en la misma consulta.
 */
public interface RepositorioDescubrimientos extends JpaRepository<Descubrimiento, Long> {

    boolean existsByCazadorIdAndCocinaId(Long cazadorId, Long cocinaId);

    Optional<Descubrimiento> findByCazadorIdAndCocinaId(Long cazadorId, Long cocinaId);

    boolean existsByCocinaId(Long cocinaId);

    long countByCocinaId(Long cocinaId);

    long countByCazadorIdAndFechaHoraBetween(Long cazadorId, LocalDateTime desde, LocalDateTime hasta);

    /** Cazas que un cazador registro en un dia calendario. */
    default long cazasDelDia(Long cazadorId, LocalDate dia) {
        return countByCazadorIdAndFechaHoraBetween(cazadorId, dia.atStartOfDay(), dia.atTime(LocalTime.MAX));
    }

    @Query("""
            select d from Descubrimiento d
            join fetch d.cazador
            join fetch d.cocina
            order by d.fechaHora desc, d.id desc
            """)
    List<Descubrimiento> masRecientes(Pageable pagina);

    @Query("select coalesce(sum(d.puntosObtenidos), 0) from Descubrimiento d where d.cazador.id = :cazadorId")
    long puntosDeCazasDe(@Param("cazadorId") Long cazadorId);

    @Query("select count(d) from Descubrimiento d where d.cazador.id = :cazadorId and d.resena is not null")
    long resenasDe(@Param("cazadorId") Long cazadorId);

    @Query("""
            select d from Descubrimiento d
            join fetch d.cocina
            where d.cazador.id = :cazadorId
            order by d.fechaHora desc
            """)
    List<Descubrimiento> historialDe(@Param("cazadorId") Long cazadorId);

    @Query("select d.fechaHora from Descubrimiento d where d.cazador.id = :cazadorId")
    List<LocalDateTime> fechasDeCazaDe(@Param("cazadorId") Long cazadorId);

    @Query("select d.cocina.id from Descubrimiento d where d.cazador.id = :cazadorId")
    Set<Long> cocinasCazadasPor(@Param("cazadorId") Long cazadorId);

    @Query("""
            select d from Descubrimiento d
            join fetch d.cazador
            join fetch d.resena r
            where d.cocina.id = :cocinaId
            order by r.fecha desc
            """)
    List<Descubrimiento> conResenaDeCocina(@Param("cocinaId") Long cocinaId);

    @Query("""
            select new co.edu.udes.ghostkitchen.persistencia.ConteoPorCocina(d.cocina.id, count(d))
            from Descubrimiento d
            group by d.cocina.id
            """)
    List<ConteoPorCocina> cazasPorCocina();

    @Query("""
            select new co.edu.udes.ghostkitchen.persistencia.CalificacionPorCocina(d.cocina.id, avg(r.calificacion), count(r))
            from Descubrimiento d
            join d.resena r
            group by d.cocina.id
            """)
    List<CalificacionPorCocina> calificacionesPorCocina();

    @Query("""
            select new co.edu.udes.ghostkitchen.persistencia.ActividadPorCazador(
                d.cazador.id,
                count(d),
                sum(d.puntosObtenidos),
                sum(case when d.resena is not null then 1 else 0 end))
            from Descubrimiento d
            group by d.cazador.id
            """)
    List<ActividadPorCazador> actividadPorCazador();
}
