package py.edu.uc.lp3.ag.cs2.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.ag.cs2.exception.negocio.GranadaYaDetonadaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.DanoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.EmpujeInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.RadioExplosionInvalidoException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GranadaHETest {

    // radio 8 m, daño máximo 98, empuje máximo 6 m (valores estándar)
    private final GranadaHE he = new GranadaHE("HE", 300, "CT/T");

    @Test
    @DisplayName("El daño baja con la distancia y es 0 fuera del radio")
    void dano() {
        assertEquals(98, he.calcularDano(0));
        assertEquals(86, he.calcularDano(1));   // 98 × 7/8 = 85,75
        assertEquals(49, he.calcularDano(4));   // mitad del radio
        assertEquals(0, he.calcularDano(8));    // borde
        assertEquals(0, he.calcularDano(12));   // fuera
        assertTrue(he.alcanza(8));
        assertFalse(he.alcanza(8.01));
    }

    @Test
    @DisplayName("El empuje baja con la distancia y es 0 fuera del radio")
    void empuje() {
        assertEquals(6.0, he.calcularEmpuje(0), 1e-9);
        assertEquals(4.5, he.calcularEmpuje(2), 1e-9);
        assertEquals(0.0, he.calcularEmpuje(20), 1e-9);
    }

    @Test
    @DisplayName("Se consume: solo se puede detonar una vez")
    void seConsume() {
        assertFalse(he.isDetonada());
        he.detonar();
        assertTrue(he.isDetonada());
        assertThrows(GranadaYaDetonadaException.class, he::detonar);
    }

    @Test
    @DisplayName("Regla 1: danar aplica el daño según la distancia y el chaleco, solo a jugadores vivos en el radio")
    void regla1Danar() {
        Posicion centro = new Posicion(0, 0, 0);
        Jugador cerca = new Jugador("Cerca", "CT", new Posicion(1, 0, 0));
        Jugador conChaleco = new Jugador("Chaleco", "T", 100, true, new Posicion(3, 4, 0));
        Jugador lejos = new Jugador("Lejos", "T", new Posicion(12, 0, 0));

        GranadaHE.Impacto impacto = he.danar(cerca, centro).orElseThrow();
        assertEquals(86, impacto.danoRecibido());
        assertEquals(14, cerca.getVida());
        assertEquals(19, he.danar(conChaleco, centro).orElseThrow().danoRecibido());   // 37 a la mitad
        assertTrue(he.danar(lejos, centro).isEmpty());
        assertEquals(100, lejos.getVida());

        Jugador muerto = new Jugador("Muerto", "T", 0, false, new Posicion(0, 0, 0));
        assertTrue(he.danar(muerto, centro).isEmpty());
    }

    @Test
    @DisplayName("Regla 2: empujar aleja las armas del mapa dentro del radio y deja quietas las demás")
    void regla2Empujar() {
        Posicion centro = new Posicion(0, 0, 0);
        Rifle galil = new Rifle("Galil AR", 1800, "T");
        galil.ubicarEn(new Posicion(2, 0, 0));
        Rifle lejos = new Rifle("Lejos", 1800, "T");
        lejos.ubicarEn(new Posicion(20, 0, 0));
        Rifle enCatalogo = new Rifle("Catálogo", 1800, "T");

        GranadaHE.Desplazamiento d = he.empujar(galil, centro).orElseThrow();
        assertEquals(4.5, d.metros(), 1e-9);
        assertEquals(new Posicion(6.5, 0, 0), galil.getPosicion());
        assertTrue(he.empujar(lejos, centro).isEmpty());
        assertTrue(he.empujar(enCatalogo, centro).isEmpty());
        assertTrue(he.empujar(he, centro).isEmpty());   // no se empuja a sí misma
    }

    @Test
    @DisplayName("Invariantes y herencia")
    void invariantes() {
        assertThrows(RadioExplosionInvalidoException.class, () -> new GranadaHE("X", 300, "CT", 1.6f, 0f, 98, 6f));
        assertThrows(DanoInvalidoException.class, () -> new GranadaHE("X", 300, "CT", 1.6f, 8f, 0, 6f));
        assertThrows(EmpujeInvalidoException.class, () -> new GranadaHE("X", 300, "CT", 1.6f, 8f, 98, -1f));
        assertFalse(he.admiteSkin());   // heredado de Granada
        assertEquals("ui/hud/icons/he_grenade.png", he.obtenerIconoHud());
    }
}
