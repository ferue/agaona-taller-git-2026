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
    @DisplayName("Invariantes y herencia")
    void invariantes() {
        assertThrows(RadioExplosionInvalidoException.class, () -> new GranadaHE("X", 300, "CT", 1.6f, 0f, 98, 6f));
        assertThrows(DanoInvalidoException.class, () -> new GranadaHE("X", 300, "CT", 1.6f, 8f, 0, 6f));
        assertThrows(EmpujeInvalidoException.class, () -> new GranadaHE("X", 300, "CT", 1.6f, 8f, 98, -1f));
        assertFalse(he.admiteSkin());   // heredado de Granada
        assertEquals("ui/hud/icons/he_grenade.png", he.obtenerIconoHud());
    }
}
