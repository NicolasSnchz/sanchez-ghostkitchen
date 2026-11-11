package co.edu.udes.ghostkitchen.persistencia;

import co.edu.udes.ghostkitchen.dominio.modelo.InsigniaObtenida;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a las insignias que han ganado los cazadores. */
public interface RepositorioInsignias extends JpaRepository<InsigniaObtenida, Long> {

    List<InsigniaObtenida> findByCazadorId(Long cazadorId);
}
