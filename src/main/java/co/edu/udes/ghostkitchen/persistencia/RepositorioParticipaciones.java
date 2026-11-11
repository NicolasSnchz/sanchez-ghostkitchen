package co.edu.udes.ghostkitchen.persistencia;

import co.edu.udes.ghostkitchen.dominio.modelo.ParticipacionReto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Acceso a las inscripciones de los cazadores en los retos. */
public interface RepositorioParticipaciones extends JpaRepository<ParticipacionReto, Long> {

    @Query("select p from ParticipacionReto p join fetch p.reto where p.cazador.id = :cazadorId")
    List<ParticipacionReto> deCazador(@Param("cazadorId") Long cazadorId);

    Optional<ParticipacionReto> findByCazadorIdAndRetoId(Long cazadorId, Long retoId);

    boolean existsByCazadorIdAndRetoId(Long cazadorId, Long retoId);

    boolean existsByRetoId(Long retoId);

    long countByCazadorIdAndFechaCompletadoIsNotNull(Long cazadorId);

    @Query("""
            select coalesce(sum(p.reto.puntosRecompensa), 0)
            from ParticipacionReto p
            where p.cazador.id = :cazadorId and p.fechaCompletado is not null
            """)
    long recompensasDe(@Param("cazadorId") Long cazadorId);

    @Query("""
            select new co.edu.udes.ghostkitchen.persistencia.ConteoPorReto(p.reto.id, count(p))
            from ParticipacionReto p
            group by p.reto.id
            """)
    List<ConteoPorReto> participantesPorReto();

    @Query("""
            select new co.edu.udes.ghostkitchen.persistencia.RecompensaPorCazador(p.cazador.id, sum(r.puntosRecompensa))
            from ParticipacionReto p
            join p.reto r
            where p.fechaCompletado is not null
            group by p.cazador.id
            """)
    List<RecompensaPorCazador> recompensasPorCazador();
}
