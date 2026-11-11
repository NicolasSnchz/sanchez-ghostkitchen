package co.edu.udes.ghostkitchen.persistencia;

import co.edu.udes.ghostkitchen.dominio.modelo.AccionRegistrada;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso al historial de acciones. La ultima accion vigente es la cima de la pila de deshacer. */
public interface RepositorioAcciones extends JpaRepository<AccionRegistrada, Long> {

    Optional<AccionRegistrada> findFirstByCazadorIdAndDeshechaFalseOrderByFechaHoraDescIdDesc(Long cazadorId);

    List<AccionRegistrada> findTop8ByCazadorIdOrderByFechaHoraDescIdDesc(Long cazadorId);
}
