package py.edu.uc.lp3.ag.cs2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.edu.uc.lp3.ag.cs2.domain.Arma;

import java.util.List;

/**
 * Capa de acceso a datos. Es solo una interfaz: Spring Data JPA genera la
 * implementación en tiempo de ejecución (save, findById, findAll, deleteById...).
 *
 * Como Arma es la raíz de la herencia, las consultas son polimórficas:
 * devuelven Rifle, Pistola o GranadaHumo según lo que haya en la base.
 */
public interface ArmaRepository extends JpaRepository<Arma, Long> {

    // Consultas derivadas del nombre del método (Spring arma el SQL).
    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<Arma> findByBandoOrderByIdAsc(String bando);
}
