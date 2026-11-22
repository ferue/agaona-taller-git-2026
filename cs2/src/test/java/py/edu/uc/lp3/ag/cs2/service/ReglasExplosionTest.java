package py.edu.uc.lp3.ag.cs2.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHE;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.domain.Rifle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Las dos reglas de la explosión y su geometría, sin base de datos: se
 * prueban los métodos de ExplosionService directamente.
 * Granada estándar: radio 8 m, daño máximo 98, empuje máximo 6 m.
 */
class ReglasExplosionTest {

    private static final Posicion ORIGEN = new Posicion(0, 0, 0);

    private final ExplosionService reglas = new ExplosionService(null, null);
    private final GranadaHE he = new GranadaHE("HE", 300, "CT/T");

    @Test
    @DisplayName("distancia calcula la distancia en línea recta")
    void distancia() {
        assertEquals(5.0, reglas.distancia(new Posicion(3, 4, 0), ORIGEN), 1e-9);
        assertEquals(0.0, reglas.distancia(ORIGEN, ORIGEN), 1e-9);
    }

    @Test
    @DisplayName("desplazarDesde aleja el punto del centro y no modifica el original")
    void desplazar() {
        Posicion p = new Posicion(2, 0, 0);
        assertEquals(new Posicion(6.5, 0, 0), reglas.desplazarDesde(p, ORIGEN, 4.5));
        assertEquals(new Posicion(2, 0, 0), p);

        Posicion diagonal = reglas.desplazarDesde(new Posicion(3, 4, 0), ORIGEN, 5);
        assertEquals(10.0, reglas.distancia(diagonal, ORIGEN), 1e-9);
    }

    @Test
    @DisplayName("Un punto en el centro de la explosión sale hacia arriba")
    void desplazarDesdeElCentro() {
        Posicion centro = new Posicion(1, 1, 0);
        assertEquals(new Posicion(1, 1, 6), reglas.desplazarDesde(centro, centro, 6));
    }

    @Test
    @DisplayName("Regla 1: el daño baja con la distancia y es 0 fuera del radio")
    void formulaDano() {
        assertEquals(98, reglas.calcularDano(he, 0));
        assertEquals(86, reglas.calcularDano(he, 1));   // 98 × 7/8 = 85,75
        assertEquals(49, reglas.calcularDano(he, 4));   // mitad del radio
        assertEquals(0, reglas.calcularDano(he, 8));    // borde
        assertEquals(0, reglas.calcularDano(he, 12));   // fuera
        assertTrue(reglas.alcanza(he, 8));
        assertFalse(reglas.alcanza(he, 8.01));
    }

    @Test
    @DisplayName("Regla 1: el chaleco reduce el daño a la mitad y la vida no baja de 0")
    void danoRecibido() {
        assertEquals(40, reglas.danoRecibido(new Jugador("A", "T", ORIGEN), 40));
        assertEquals(20, reglas.danoRecibido(new Jugador("B", "CT", 100, true, ORIGEN), 40));
        assertEquals(20, reglas.danoRecibido(new Jugador("C", "T", 20, false, ORIGEN), 74));
    }

    @Test
    @DisplayName("Regla 1: danar aplica el daño solo a jugadores vivos dentro del radio")
    void danar() {
        Jugador cerca = new Jugador("Cerca", "CT", new Posicion(1, 0, 0));
        Jugador conChaleco = new Jugador("Chaleco", "T", 100, true, new Posicion(3, 4, 0));
        Jugador lejos = new Jugador("Lejos", "T", new Posicion(12, 0, 0));
        Jugador muerto = new Jugador("Muerto", "T", 0, false, ORIGEN);

        assertEquals(86, reglas.danar(he, cerca, ORIGEN).orElseThrow().danoRecibido());
        assertEquals(14, cerca.getVida());
        assertEquals(19, reglas.danar(he, conChaleco, ORIGEN).orElseThrow().danoRecibido());   // 37 a la mitad
        assertTrue(reglas.danar(he, lejos, ORIGEN).isEmpty());
        assertEquals(100, lejos.getVida());
        assertTrue(reglas.danar(he, muerto, ORIGEN).isEmpty());
    }

    @Test
    @DisplayName("Regla 2: el empuje baja con la distancia y es 0 fuera del radio")
    void formulaEmpuje() {
        assertEquals(6.0, reglas.calcularEmpuje(he, 0), 1e-9);
        assertEquals(4.5, reglas.calcularEmpuje(he, 2), 1e-9);
        assertEquals(0.0, reglas.calcularEmpuje(he, 20), 1e-9);
    }

    @Test
    @DisplayName("Regla 2: empujar aleja las armas del mapa dentro del radio y deja quietas las demás")
    void empujar() {
        Rifle galil = new Rifle("Galil AR", 1800, "T");
        galil.ubicarEn(new Posicion(2, 0, 0));
        Rifle lejos = new Rifle("Lejos", 1800, "T");
        lejos.ubicarEn(new Posicion(20, 0, 0));
        Rifle enCatalogo = new Rifle("Catálogo", 1800, "T");
        he.ubicarEn(ORIGEN);

        assertEquals(4.5, reglas.empujar(he, galil, ORIGEN).orElseThrow().metros(), 1e-9);
        assertEquals(new Posicion(6.5, 0, 0), galil.getPosicion());
        assertTrue(reglas.empujar(he, lejos, ORIGEN).isEmpty());
        assertTrue(reglas.empujar(he, enCatalogo, ORIGEN).isEmpty());
        assertTrue(reglas.empujar(he, he, ORIGEN).isEmpty());   // no se empuja a sí misma
    }
}
