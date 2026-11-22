package py.edu.uc.lp3.ag.cs2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.client.A2ArmasClient;
import py.edu.uc.lp3.ag.cs2.client.A2ArmasClient.DisparoA2;
import py.edu.uc.lp3.ag.cs2.client.A3ArmasClient;
import py.edu.uc.lp3.ag.cs2.client.A3ArmasClient.DisparoA3;
import py.edu.uc.lp3.ag.cs2.domain.JugadaAtaque;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.JugadaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.repository.JugadaAtaqueRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Escenario del POO-09: «ataque coordinado».
 *
 * Antes de lanzar la HE, dos compañeros abren fuego: uno con un arma
 * registrada en la API de A2 contra un objetivo, otro con un rifle registrado
 * en la API de A3. La jugada es legal solo si los dos disparos lo son.
 *
 * Orden (justificado):
 * 1. Las reglas propias, sin efectos (ExplosionService.validar): si la
 *    granada no existe, no es una HE o ya explotó, se corta acá sin gastar
 *    munición de los compañeros (sus disparos sí cambian su base).
 * 2. A2: regla de disparo de Mauricio (400/404 → RechazoA2Exception).
 * 3. A3: regla 1 de Vanessa (409/400/404 o exito=false → RechazoA3Exception).
 * 4. Si los dos aceptaron: explota la HE (regla 1 daño, regla 2 empuje) y se
 *    guarda la jugada en H2.
 *
 * Si un compañero rechaza o no responde, se lanza la excepción de ese
 * compañero antes de escribir nada: la base de este servicio queda igual.
 */
@Service
public class JugadaService {

    private static final Logger log = LoggerFactory.getLogger(JugadaService.class);

    private final ExplosionService explosionService;
    private final A2ArmasClient a2;
    private final A3ArmasClient a3;
    private final JugadaAtaqueRepository jugadaRepository;

    public JugadaService(ExplosionService explosionService, A2ArmasClient a2, A3ArmasClient a3,
                         JugadaAtaqueRepository jugadaRepository) {
        this.explosionService = explosionService;
        this.a2 = a2;
        this.a3 = a3;
        this.jugadaRepository = jugadaRepository;
    }

    /**
     * Ejecuta el escenario. Los ids de las armas de los compañeros son
     * opcionales: si llegan null se usan los de application.properties.
     */
    @Transactional
    public JugadaAtaque atacar(Long granadaId, Posicion centro, Long armaA2Id, Long objetivoA2Id, Long armaA3Id) {
        long armaA2 = armaA2Id != null ? armaA2Id : a2.getArmaPorDefecto();
        long objetivoA2 = objetivoA2Id != null ? objetivoA2Id : a2.getObjetivoPorDefecto();
        long armaA3 = armaA3Id != null ? armaA3Id : a3.getArmaPorDefecto();
        log.debug("Escenario: granada {} en {}; A2 arma {} → objetivo {}; A3 arma {}",
                granadaId, centro, armaA2, objetivoA2, armaA3);

        log.debug("Paso 1/4: reglas propias sin efectos (explosión validada antes de consultar a nadie)");
        explosionService.validar(granadaId, centro);

        log.debug("Paso 2/4: consulta a A2 ({}) en {}", a2.getCompanero(), a2.getUrlBase());
        DisparoA2 disparoA2 = a2.disparar(armaA2, objetivoA2);

        log.debug("Paso 3/4: consulta a A3 ({}) en {}", a3.getCompanero(), a3.getUrlBase());
        DisparoA3 disparoA3 = a3.disparar(armaA3);

        log.debug("Paso 4/4: A2 y A3 aceptaron; explota la HE y se guarda la jugada");
        ResultadoExplosion explosion = explosionService.explotar(granadaId, centro);
        int eliminados = (int) explosion.jugadoresAfectados().stream()
                .filter(i -> i.vidaRestante() == 0).count();
        String resumen = "Ataque coordinado: A2 y A3 aceptaron y " + explosion.granada().getNombre() + " dañó a "
                + explosion.jugadoresAfectados().size() + " jugador(es) (" + eliminados + " eliminado(s)) y desplazó "
                + explosion.armasDesplazadas().size() + " arma(s).";

        JugadaAtaque jugada = jugadaRepository.save(new JugadaAtaque(LocalDateTime.now(), granadaId,
                explosion.granada().getNombre(), centro,
                "A2 (" + a2.getCompanero() + "): " + disparoA2.resumen(),
                "A3 (" + a3.getCompanero() + "): " + disparoA3.resumen(),
                explosion.jugadoresAfectados().size(), eliminados, explosion.armasDesplazadas().size(), resumen));
        log.debug("Jugada {} guardada: {}", jugada.getId(), resumen);
        return jugada;
    }

    @Transactional(readOnly = true)
    public List<JugadaAtaque> listar() {
        return jugadaRepository.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public JugadaAtaque obtener(Long id) {
        return jugadaRepository.findById(id).orElseThrow(() -> new JugadaNoEncontradaException(id));
    }
}
