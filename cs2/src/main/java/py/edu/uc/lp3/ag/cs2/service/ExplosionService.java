package py.edu.uc.lp3.ag.cs2.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHE;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaNoExplosivaException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ArmaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.repository.ArmaRepository;
import py.edu.uc.lp3.ag.cs2.repository.JugadorRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulación de la explosión de una granada HE. Participan tres clases del
 * modelo: GranadaHE (calcula daño y empuje), Jugador (recibe el daño) y
 * Posicion (mide distancias y calcula a dónde vuela un arma).
 *
 * Regla 1 – Daño: la HE daña a todos los jugadores vivos dentro de su radio,
 * aliados incluidos; el daño baja con la distancia y el chaleco lo reduce a la mitad.
 *
 * Regla 2 – Empuje: toda arma tirada en el mapa dentro del radio sale
 * despedida en dirección contraria a la explosión, más lejos cuanto más cerca estaba.
 *
 * La granada se consume: si ya explotó → GranadaYaDetonadaException (409).
 * Si el id no existe → ArmaNoEncontradaException (404); si existe pero no es
 * una HE → ArmaNoExplosivaException (400).
 */
@Service
@Transactional
public class ExplosionService {

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;

    public ExplosionService(ArmaRepository armaRepository, JugadorRepository jugadorRepository) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
    }

    public ResultadoExplosion explotar(Long granadaId, Posicion centro) {
        GranadaHE granada = buscarGranada(granadaId);
        granada.detonar();

        List<ResultadoExplosion.Impacto> impactos = aplicarDano(granada, centro);
        List<ResultadoExplosion.Desplazamiento> desplazamientos = empujarArmas(granada, centro);

        return new ResultadoExplosion(granada, centro, impactos, desplazamientos);
    }

    private GranadaHE buscarGranada(Long id) {
        Arma arma = armaRepository.findById(id)
                .orElseThrow(() -> new ArmaNoEncontradaException(id));
        // La consulta es polimórfica: devuelve la clase concreta, y solo una GranadaHE explota.
        if (!(arma instanceof GranadaHE granada)) {
            throw new ArmaNoExplosivaException(arma.getNombre(), arma.getClass().getSimpleName());
        }
        return granada;
    }

    /** Regla 1. */
    private List<ResultadoExplosion.Impacto> aplicarDano(GranadaHE granada, Posicion centro) {
        List<ResultadoExplosion.Impacto> impactos = new ArrayList<>();
        for (Jugador jugador : jugadorRepository.findAll(Sort.by("id"))) {
            double distancia = jugador.getPosicion().distanciaA(centro);
            if (!jugador.estaVivo() || !granada.alcanza(distancia)) {
                continue;
            }
            int danoCalculado = granada.calcularDano(distancia);
            int danoRecibido = jugador.recibirDano(danoCalculado);
            impactos.add(new ResultadoExplosion.Impacto(jugador, distancia, danoCalculado, danoRecibido));
        }
        return impactos;
    }

    /** Regla 2. */
    private List<ResultadoExplosion.Desplazamiento> empujarArmas(GranadaHE granada, Posicion centro) {
        List<ResultadoExplosion.Desplazamiento> desplazamientos = new ArrayList<>();
        for (Arma arma : armaRepository.findAll(Sort.by("id"))) {
            if (!arma.estaEnElMapa() || arma.getId().equals(granada.getId())) {
                continue;
            }
            Posicion desde = arma.getPosicion();
            double distancia = desde.distanciaA(centro);
            if (!granada.alcanza(distancia)) {
                continue;
            }
            double metros = granada.calcularEmpuje(distancia);
            Posicion hasta = desde.desplazarDesde(centro, metros);
            arma.ubicarEn(hasta);
            desplazamientos.add(new ResultadoExplosion.Desplazamiento(
                    arma.getId(), arma.getNombre(), arma.getClass().getSimpleName(), desde, hasta, metros));
        }
        return desplazamientos;
    }
}
