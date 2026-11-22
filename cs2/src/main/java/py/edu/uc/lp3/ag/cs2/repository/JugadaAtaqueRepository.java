package py.edu.uc.lp3.ag.cs2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.ag.cs2.domain.JugadaAtaque;

/** Acceso a datos de las jugadas del escenario del POO-09. */
public interface JugadaAtaqueRepository extends JpaRepository<JugadaAtaque, Long> {
}
