package py.edu.uc.lp3.ag.cs2.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHE;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaNoExplosivaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.GranadaYaDetonadaException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ArmaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.repository.ArmaRepository;
import py.edu.uc.lp3.ag.cs2.repository.JugadorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simulación de la explosión de una granada HE. Participan tres clases del
 * modelo: GranadaHE (radio, daño y empuje máximos), Jugador (recibe el daño)
 * y Posicion (dónde está cada cosa). Las clases del dominio solo guardan el
 * estado: las dos reglas y sus fórmulas están en este service.
 *
 * Regla 1 – Daño: la HE daña a todos los jugadores vivos dentro de su radio,
 * aliados incluidos. daño = danoMaximo × (1 − distancia / radio), redondeado;
 * el chaleco lo reduce a la mitad y la vida no baja de 0.
 *
 * Regla 2 – Empuje: toda arma tirada en el mapa dentro del radio sale
 * despedida en dirección contraria a la explosión.
 * metros = empujeMaximo × (1 − distancia / radio). Si está justo en el
 * centro no hay dirección horizontal y sale hacia arriba (eje z).
 *
 * Las dos fórmulas bajan en línea recta con la distancia: en el centro valen
 * el máximo y en el borde del radio valen 0.
 *
 * validar(...) aplica las mismas reglas sin efectos: no detona la granada,
 * no baja vidas ni mueve armas (transacción de solo lectura). Es la
 * operación que consultan los compañeros en el POO-09.
 *
 * La granada se consume: si ya explotó → GranadaYaDetonadaException (409).
 * Si el id no existe → ArmaNoEncontradaException (404); si existe pero no es
 * una HE → ArmaNoExplosivaException (400).
 */
@Service
@Transactional
public class ExplosionService {

    /** El chaleco (kevlar) deja pasar la mitad del daño de una explosión. */
    public static final double FACTOR_CHALECO = 0.5;

    private final ArmaRepository armaRepository;
    private final JugadorRepository jugadorRepository;

    public ExplosionService(ArmaRepository armaRepository, JugadorRepository jugadorRepository) {
        this.armaRepository = armaRepository;
        this.jugadorRepository = jugadorRepository;
    }

    public ResultadoExplosion explotar(Long granadaId, Posicion centro) {
        GranadaHE granada = prepararExplosion(granadaId, centro);
        granada.marcarDetonada();

        List<ResultadoExplosion.Impacto> impactos = new ArrayList<>();
        for (Jugador jugador : jugadorRepository.findAll(Sort.by("id"))) {
            danar(granada, jugador, centro).ifPresent(impactos::add);
        }
        List<ResultadoExplosion.Desplazamiento> desplazamientos = new ArrayList<>();
        for (Arma arma : armaRepository.findAll(Sort.by("id"))) {
            empujar(granada, arma, centro).ifPresent(desplazamientos::add);
        }
        return new ResultadoExplosion(granada, centro, impactos, desplazamientos, false);
    }

    /**
     * Mismas validaciones y mismas reglas que explotar(...), pero sin efectos:
     * calcula a quién dañaría y qué armas empujaría, sin detonar la granada
     * ni tocar vidas o posiciones.
     */
    @Transactional(readOnly = true)
    public ResultadoExplosion validar(Long granadaId, Posicion centro) {
        GranadaHE granada = prepararExplosion(granadaId, centro);

        List<ResultadoExplosion.Impacto> impactos = new ArrayList<>();
        for (Jugador jugador : jugadorRepository.findAll(Sort.by("id"))) {
            calcularImpacto(granada, jugador, centro).ifPresent(impactos::add);
        }
        List<ResultadoExplosion.Desplazamiento> desplazamientos = new ArrayList<>();
        for (Arma arma : armaRepository.findAll(Sort.by("id"))) {
            calcularDesplazamiento(granada, arma, centro).ifPresent(desplazamientos::add);
        }
        return new ResultadoExplosion(granada, centro, impactos, desplazamientos, true);
    }

    /** Lo común a explotar y validar: datos obligatorios, granada HE y que no haya explotado. */
    private GranadaHE prepararExplosion(Long granadaId, Posicion centro) {
        if (granadaId == null) {
            throw new CampoObligatorioException("granadaId");
        }
        Validaciones.requerirPosicion(centro, "centro");
        GranadaHE granada = buscarGranada(granadaId);
        // La granada se consume: explota una sola vez.
        if (granada.isDetonada()) {
            throw new GranadaYaDetonadaException(granada.getNombre());
        }
        return granada;
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

    // ===== Regla 1: daño =====

    /**
     * Si el jugador está vivo y dentro del radio, recibe el daño que
     * corresponde a su distancia. Devuelve el impacto, o vacío si no lo alcanzó.
     */
    Optional<ResultadoExplosion.Impacto> danar(GranadaHE granada, Jugador jugador, Posicion centro) {
        Optional<ResultadoExplosion.Impacto> impacto = calcularImpacto(granada, jugador, centro);
        impacto.ifPresent(i -> jugador.perderVida(i.danoRecibido()));
        return impacto;
    }

    /** Regla 1 sin efectos: qué daño recibiría el jugador y con cuánta vida quedaría. */
    Optional<ResultadoExplosion.Impacto> calcularImpacto(GranadaHE granada, Jugador jugador, Posicion centro) {
        if (!jugador.estaVivo()) {
            return Optional.empty();
        }
        double distancia = distancia(jugador.getPosicion(), centro);
        if (!alcanza(granada, distancia)) {
            return Optional.empty();
        }
        int danoCalculado = calcularDano(granada, distancia);
        int danoRecibido = danoRecibido(jugador, danoCalculado);
        return Optional.of(new ResultadoExplosion.Impacto(jugador, distancia, danoCalculado, danoRecibido,
                jugador.getVida() - danoRecibido));
    }

    /** daño = danoMaximo × (1 − distancia / radio), redondeado; 0 fuera del radio. */
    int calcularDano(GranadaHE granada, double distancia) {
        if (!alcanza(granada, distancia)) {
            return 0;
        }
        return (int) Math.round(granada.getDanoMaximo() * (1 - distancia / granada.getRadioExplosion()));
    }

    /** El chaleco reduce el daño a la mitad y la vida nunca baja de 0. */
    int danoRecibido(Jugador jugador, int dano) {
        int danoReal = jugador.isTieneChaleco() ? (int) Math.round(dano * FACTOR_CHALECO) : dano;
        return Math.min(danoReal, jugador.getVida());
    }

    // ===== Regla 2: empuje =====

    /**
     * Si el arma está tirada en el mapa y dentro del radio, la mueve
     * alejándola del centro. La granada no se empuja a sí misma.
     * Devuelve el desplazamiento, o vacío.
     */
    Optional<ResultadoExplosion.Desplazamiento> empujar(GranadaHE granada, Arma arma, Posicion centro) {
        Optional<ResultadoExplosion.Desplazamiento> desplazamiento = calcularDesplazamiento(granada, arma, centro);
        desplazamiento.ifPresent(d -> arma.ubicarEn(d.hasta()));
        return desplazamiento;
    }

    /** Regla 2 sin efectos: a dónde volaría el arma y cuántos metros. */
    Optional<ResultadoExplosion.Desplazamiento> calcularDesplazamiento(GranadaHE granada, Arma arma,
                                                                      Posicion centro) {
        if (arma == granada || !arma.estaEnElMapa()) {
            return Optional.empty();
        }
        Posicion desde = arma.getPosicion();
        double distancia = distancia(desde, centro);
        if (!alcanza(granada, distancia)) {
            return Optional.empty();
        }
        double metros = calcularEmpuje(granada, distancia);
        return Optional.of(new ResultadoExplosion.Desplazamiento(arma, desde, desplazarDesde(desde, centro, metros),
                metros));
    }

    /** metros = empujeMaximo × (1 − distancia / radio); 0 fuera del radio. */
    double calcularEmpuje(GranadaHE granada, double distancia) {
        if (!alcanza(granada, distancia)) {
            return 0;
        }
        return granada.getEmpujeMaximo() * (1 - distancia / granada.getRadioExplosion());
    }

    // ===== Geometría =====

    /** true si un punto a esa distancia del centro está dentro del radio. */
    boolean alcanza(GranadaHE granada, double distancia) {
        return distancia <= granada.getRadioExplosion();
    }

    /** Distancia en línea recta (euclídea) entre dos puntos. */
    double distancia(Posicion a, Posicion b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Punto al que llega algo que está en "desde" si sale despedido "metros"
     * en línea recta, alejándose de "centro". Si está justo en el centro no
     * hay dirección horizontal, así que sale hacia arriba (eje z).
     */
    Posicion desplazarDesde(Posicion desde, Posicion centro, double metros) {
        double distancia = distancia(desde, centro);
        if (distancia == 0) {
            return new Posicion(desde.getX(), desde.getY(), desde.getZ() + metros);
        }
        double factor = metros / distancia;
        return new Posicion(
                desde.getX() + (desde.getX() - centro.getX()) * factor,
                desde.getY() + (desde.getY() - centro.getY()) * factor,
                desde.getZ() + (desde.getZ() - centro.getZ()) * factor);
    }
}
