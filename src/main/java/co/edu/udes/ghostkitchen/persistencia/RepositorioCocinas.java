package co.edu.udes.ghostkitchen.persistencia;

import co.edu.udes.ghostkitchen.dominio.modelo.CocinaFantasma;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** Acceso al catalogo de cocinas fantasma. */
public interface RepositorioCocinas extends JpaRepository<CocinaFantasma, Long> {

    List<CocinaFantasma> findByActivaTrue();

    List<CocinaFantasma> findAllByOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    long countByActivaTrue();

    @Query("select count(distinct c.ubicacion.barrio) from CocinaFantasma c where c.activa = true")
    long barriosConCocinasActivas();
}
