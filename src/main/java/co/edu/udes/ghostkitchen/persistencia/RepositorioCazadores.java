package co.edu.udes.ghostkitchen.persistencia;

import co.edu.udes.ghostkitchen.dominio.modelo.Cazador;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a los cazadores. */
public interface RepositorioCazadores extends JpaRepository<Cazador, Long> {

    boolean existsByAliasIgnoreCase(String alias);
}
