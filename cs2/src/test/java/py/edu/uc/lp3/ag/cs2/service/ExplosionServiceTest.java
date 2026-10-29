package py.edu.uc.lp3.ag.cs2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHE;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;
import py.edu.uc.lp3.ag.cs2.domain.Pistola;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.domain.Rifle;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaNoExplosivaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.GranadaYaDetonadaException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ArmaNoEncontradaException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simulación completa contra H2 en memoria: GranadaHE + Jugador + Posicion.
 * Mismo escenario que la carga inicial, con la explosión en (0, 0, 0).
 */
@SpringBootTest
@Transactional
class ExplosionServiceTest {

    private static final Posicion CENTRO = new Posicion(0, 0, 0);

    @Autowired private ExplosionService explosionService;
    @Autowired private ArmaService armaService;
    @Autowired private JugadorService jugadorService;

    private GranadaHE he;
    private Jugador fenix, halcon, lobo, cuervo;
    private Arma galil, p250, tec9;

    @BeforeEach
    void escenario() {
        he = (GranadaHE) armaService.crear(new GranadaHE("HE Grenade", 300, "CT/T"));
        fenix = jugadorService.crear(new Jugador("Fénix", "CT", new Posicion(1, 0, 0)));
        halcon = jugadorService.crear(new Jugador("Halcón", "CT", 100, true, new Posicion(3, 4, 0)));
        lobo = jugadorService.crear(new Jugador("Lobo", "T", 20, false, new Posicion(0, 2, 0)));
        cuervo = jugadorService.crear(new Jugador("Cuervo", "T", new Posicion(12, 0, 0)));
        galil = tirada(new Rifle("Galil AR", 1800, "T"), new Posicion(2, 0, 0));
        p250 = tirada(new Pistola("P250", 300, "CT/T"), new Posicion(0, 0, 0));
        tec9 = tirada(new Pistola("Tec-9", 500, "T"), new Posicion(20, 0, 0));
    }

    private Arma tirada(Arma arma, Posicion donde) {
        arma.ubicarEn(donde);
        return armaService.crear(arma);
    }

    @Test
    @DisplayName("Regla 1: daña a todos dentro del radio según la distancia, con chaleco a la mitad")
    void regla1Dano() {
        ResultadoExplosion r = explosionService.explotar(he.getId(), CENTRO);

        List<String> afectados = r.jugadoresAfectados().stream().map(i -> i.jugador().getNombre()).toList();
        assertEquals(List.of("Fénix", "Halcón", "Lobo"), afectados);   // aliados incluidos, Cuervo fuera

        assertEquals(14, fenix.getVida());    // a 1 m: 86 de daño
        assertEquals(81, halcon.getVida());   // a 5 m: 37, con chaleco 19
        assertEquals(0, lobo.getVida());      // a 2 m: 74 de daño, tenía 20
        assertFalse(lobo.estaVivo());
        assertEquals(100, cuervo.getVida());  // a 12 m: fuera del radio
    }

    @Test
    @DisplayName("Regla 2: las armas tiradas dentro del radio salen volando")
    void regla2Empuje() {
        ResultadoExplosion r = explosionService.explotar(he.getId(), CENTRO);

        assertEquals(2, r.armasDesplazadas().size());
        assertEquals(new Posicion(6.5, 0, 0), galil.getPosicion());   // a 2 m: vuela 4,5 m
        assertEquals(new Posicion(0, 0, 6), p250.getPosicion());      // en el centro: hacia arriba
        assertEquals(new Posicion(20, 0, 0), tec9.getPosicion());     // fuera del radio
    }

    @Test
    @DisplayName("La granada se consume y no se puede volver a explotar")
    void seConsume() {
        explosionService.explotar(he.getId(), CENTRO);
        assertTrue(he.isDetonada());
        assertThrows(GranadaYaDetonadaException.class, () -> explosionService.explotar(he.getId(), CENTRO));
    }

    @Test
    @DisplayName("Un jugador eliminado no vuelve a recibir daño")
    void eliminadoNoRecibeDano() {
        explosionService.explotar(he.getId(), CENTRO);
        GranadaHE otra = (GranadaHE) armaService.crear(new GranadaHE("HE 2", 300, "CT/T"));
        ResultadoExplosion r = explosionService.explotar(otra.getId(), CENTRO);
        assertTrue(r.jugadoresAfectados().stream().noneMatch(i -> i.jugador().getNombre().equals("Lobo")));
    }

    @Test
    @DisplayName("Solo explotan las granadas HE que existen")
    void errores() {
        assertThrows(ArmaNoExplosivaException.class, () -> explosionService.explotar(galil.getId(), CENTRO));
        assertThrows(ArmaNoEncontradaException.class, () -> explosionService.explotar(9999L, CENTRO));
    }
}
