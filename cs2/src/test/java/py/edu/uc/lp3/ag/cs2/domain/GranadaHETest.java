package py.edu.uc.lp3.ag.cs2.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GranadaHE solo guarda su configuración y si ya explotó. Las reglas de la
 * explosión se prueban en ReglasExplosionTest y ExplosionServiceTest.
 */
class GranadaHETest {

    @Test
    @DisplayName("El constructor simple usa los valores estándar de CS2")
    void valoresEstandar() {
        GranadaHE he = new GranadaHE("HE", 300, "CT/T");
        assertEquals(GranadaHE.RADIO_POR_DEFECTO, he.getRadioExplosion());
        assertEquals(GranadaHE.DANO_MAXIMO_POR_DEFECTO, he.getDanoMaximo());
        assertEquals(GranadaHE.EMPUJE_POR_DEFECTO, he.getEmpujeMaximo());
        assertEquals(GranadaHE.RETARDO_POR_DEFECTO, he.getTiempoRetardo());
        assertFalse(he.isDetonada());
    }

    @Test
    @DisplayName("marcarDetonada cambia el estado y se ve en los detalles")
    void marcarDetonada() {
        GranadaHE he = new GranadaHE("HE", 300, "CT/T");
        he.marcarDetonada();
        assertTrue(he.isDetonada());
        assertTrue(he.obtenerDetalles().endsWith("DETONADA"));
    }

    @Test
    @DisplayName("Herencia: no admite skins (Granada) y tiene su propio icono")
    void herencia() {
        GranadaHE he = new GranadaHE("HE", 300, "CT/T");
        assertFalse(he.admiteSkin());
        assertEquals("ui/hud/icons/he_grenade.png", he.obtenerIconoHud());
        assertTrue(he.ejecutarAccionPrincipal().contains("¡BOOM!"));
    }

    @Test
    @DisplayName("actualizarCon copia la configuración pero no deshace la detonación")
    void actualizarConservaDetonada() {
        GranadaHE he = new GranadaHE("HE", 300, "CT/T");
        he.marcarDetonada();
        he.actualizarCon(new GranadaHE("HE Grande", 400, "CT/T", 1.6f, 12f, 120, 8f));
        assertEquals(12f, he.getRadioExplosion());
        assertEquals("HE Grande", he.getNombre());
        assertTrue(he.isDetonada());
    }
}
