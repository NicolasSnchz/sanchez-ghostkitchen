package co.edu.udes.ghostkitchen.persistencia;

import co.edu.udes.ghostkitchen.dominio.modelo.Reto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Acceso a los retos. */
public interface RepositorioRetos extends JpaRepository<Reto, Long> {

    @Query("select r from Reto r where r.fechaInicio <= :hoy and r.fechaFin >= :hoy order by r.fechaFin, r.titulo")
    List<Reto> vigentesEn(@Param("hoy") LocalDate hoy);

    List<Reto> findByFechaInicioAfterOrderByFechaInicioAscTituloAsc(LocalDate hoy);

    List<Reto> findAllByOrderByFechaFinDescTituloAsc();
}
