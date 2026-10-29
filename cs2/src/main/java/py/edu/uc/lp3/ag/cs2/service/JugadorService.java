package py.edu.uc.lp3.ag.cs2.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;
import py.edu.uc.lp3.ag.cs2.exception.negocio.BandoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.JugadorDuplicadoException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.JugadorNoEncontradoException;
import py.edu.uc.lp3.ag.cs2.repository.JugadorRepository;

import java.util.List;

/**
 * Capa de negocio de los jugadores.
 * - Un jugador juega para T o para CT (no para los dos) → BandoInvalidoException.
 * - El nombre es único, sin distinguir mayúsculas → JugadorDuplicadoException.
 * - Id inexistente → JugadorNoEncontradoException.
 */
@Service
@Transactional(readOnly = true)
public class JugadorService {

    public static final List<String> BANDOS_VALIDOS = List.of("T", "CT");

    private final JugadorRepository jugadorRepository;

    public JugadorService(JugadorRepository jugadorRepository) {
        this.jugadorRepository = jugadorRepository;
    }

    public List<Jugador> listar() {
        return jugadorRepository.findAll(Sort.by("id"));
    }

    public Jugador obtener(Long id) {
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new JugadorNoEncontradoException(id));
    }

    @Transactional
    public Jugador crear(Jugador nuevo) {
        if (!BANDOS_VALIDOS.contains(nuevo.getBando())) {
            throw new BandoInvalidoException(nuevo.getBando(), BANDOS_VALIDOS);
        }
        if (jugadorRepository.existsByNombreIgnoreCase(nuevo.getNombre())) {
            throw new JugadorDuplicadoException(nuevo.getNombre());
        }
        return jugadorRepository.save(nuevo);
    }
}
