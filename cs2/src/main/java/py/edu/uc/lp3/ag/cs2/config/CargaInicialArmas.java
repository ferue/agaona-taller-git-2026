package py.edu.uc.lp3.ag.cs2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHE;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;
import py.edu.uc.lp3.ag.cs2.domain.Pistola;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.domain.Rifle;
import py.edu.uc.lp3.ag.cs2.repository.ArmaRepository;
import py.edu.uc.lp3.ag.cs2.repository.JugadorRepository;
import py.edu.uc.lp3.ag.cs2.service.ArmaService;
import py.edu.uc.lp3.ag.cs2.service.JugadorService;

/**
 * Carga datos de ejemplo al arrancar. Todo pasa por los services, así que
 * también respeta las reglas de negocio. Se desactiva con cs2.datos-iniciales=false.
 *
 * 1. Catálogo: 5 armas, solo si la tabla de armas está vacía.
 * 2. Escenario de explosión: solo si no hay jugadores. Una granada HE, 4
 *    jugadores y 3 armas tiradas alrededor del punto (0, 0, 0), para probar
 *    POST /api/explosiones sin preparar nada:
 *    - Fénix (CT, a 1 m) recibe 86 de daño y queda con 14 de vida.
 *    - Halcón (CT, a 5 m, con chaleco) recibe 19 y queda con 81.
 *    - Lobo (T, a 2 m, con 20 de vida) queda ELIMINADO.
 *    - Cuervo (T, a 12 m) está fuera del radio y no se toca.
 *    - Galil AR (a 2 m) vuela 4,5 m; P250 (en el centro) sale hacia arriba 6 m;
 *      Tec-9 (a 20 m) no se mueve.
 */
@Component
public class CargaInicialArmas implements CommandLineRunner {

    private final ArmaRepository armaRepository;
    private final ArmaService armaService;
    private final JugadorRepository jugadorRepository;
    private final JugadorService jugadorService;
    private final boolean habilitada;

    public CargaInicialArmas(ArmaRepository armaRepository, ArmaService armaService,
                             JugadorRepository jugadorRepository, JugadorService jugadorService,
                             @Value("${cs2.datos-iniciales:true}") boolean habilitada) {
        this.armaRepository = armaRepository;
        this.armaService = armaService;
        this.jugadorRepository = jugadorRepository;
        this.jugadorService = jugadorService;
        this.habilitada = habilitada;
    }

    @Override
    public void run(String... args) {
        if (!habilitada) {
            return;
        }
        if (armaRepository.count() == 0) {
            cargarCatalogo();
        }
        if (jugadorRepository.count() == 0) {
            cargarEscenarioDeExplosion();
        }
    }

    private void cargarCatalogo() {
        Rifle ak = new Rifle("AK-47", 2700, "T", 30, 36, false);
        ak.aplicarSkin("Redline");
        crearSiNoExiste(ak);
        crearSiNoExiste(new Rifle("M4A1-S", 2900, "CT", 20, 38, true));
        crearSiNoExiste(new Pistola("USP-S", 200, "CT", 12, 35, true));
        crearSiNoExiste(new Pistola("Glock-18", 200, "T", 20, 30, false));
        crearSiNoExiste(new GranadaHumo("Smoke", 300, "CT/T"));
    }

    private void cargarEscenarioDeExplosion() {
        crearSiNoExiste(new GranadaHE("HE Grenade", 300, "CT/T"));

        Rifle galil = new Rifle("Galil AR", 1800, "T");
        galil.ubicarEn(new Posicion(2, 0, 0));
        crearSiNoExiste(galil);
        Pistola p250 = new Pistola("P250", 300, "CT/T");
        p250.ubicarEn(new Posicion(0, 0, 0));
        crearSiNoExiste(p250);
        Pistola tec9 = new Pistola("Tec-9", 500, "T");
        tec9.ubicarEn(new Posicion(20, 0, 0));
        crearSiNoExiste(tec9);

        jugadorService.crear(new Jugador("Fénix", "CT", new Posicion(1, 0, 0)));
        jugadorService.crear(new Jugador("Halcón", "CT", 100, true, new Posicion(3, 4, 0)));
        jugadorService.crear(new Jugador("Lobo", "T", 20, false, new Posicion(0, 2, 0)));
        jugadorService.crear(new Jugador("Cuervo", "T", new Posicion(12, 0, 0)));
    }

    /** Si ya creaste un arma con ese nombre desde la API, no se duplica. */
    private void crearSiNoExiste(Arma arma) {
        if (!armaRepository.existsByNombreIgnoreCase(arma.getNombre())) {
            armaService.crear(arma);
        }
    }
}
