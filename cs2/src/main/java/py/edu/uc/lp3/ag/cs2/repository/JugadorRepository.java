package py.edu.uc.lp3.ag.cs2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;

/**
 * Acceso a datos de los jugadores. Spring Data JPA genera la implementación.
 */
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}
