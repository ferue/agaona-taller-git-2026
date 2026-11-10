package py.edu.uc.lp3.ag.cs2.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.domain.UICapable;
import py.edu.uc.lp3.ag.cs2.repository.ArmaRepository;
import py.edu.uc.lp3.ag.cs2.repository.JugadorRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Junta en una sola lista, como UICapable, las armas y los jugadores guardados en H2.
 * Arma y Jugador son familias distintas pero cumplen el mismo protocolo.
 */
@Service
@Transactional(readOnly = true)
public class HudService {

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;

    public HudService(ArmaRepository armaRepository, JugadorRepository jugadorRepository) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
    }

    public List<UICapable> elementos() {
        List<UICapable> elementos = new ArrayList<>(armaRepository.findAll(Sort.by("id")));
        elementos.addAll(jugadorRepository.findAll(Sort.by("id")));
        return elementos;
    }
}
