package py.edu.uc.lp3.ag.cs2.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArmaTest {

    @Test
    @DisplayName("El constructor conserva el bando recibido")
    void conservaElBando() {
        assertEquals("T", new Rifle("AK-47", 2700, "T").getBando());
        assertEquals("CT", new Pistola("USP-S", 200, "CT", 12, 35, true).getBando());
        assertEquals("CT/T", new GranadaHumo("Smoke", 300, "CT/T").getBando());
    }

    @Test
    @DisplayName("Constructores sobrecargados de las clases hijas usan valores estándar")
    void constructoresSobrecargados() {
        Rifle rifle = new Rifle("AK-47", 2700, "T");
        assertEquals(Rifle.CARGADOR_POR_DEFECTO, rifle.getCargadorCapacidad());
        assertEquals(Rifle.DANO_POR_DEFECTO, rifle.getDanoBase());
        assertFalse(rifle.isTieneMira());

        Pistola pistola = new Pistola("Glock-18", 200, "T");
        assertEquals(Pistola.CARGADOR_POR_DEFECTO, pistola.getCargadorCapacidad());

        GranadaHumo humo = new GranadaHumo("Smoke", 300, "CT");
        assertEquals(GranadaHumo.DURACION_POR_DEFECTO, humo.getDuracionHumo());
        assertEquals(GranadaHumo.RETARDO_POR_DEFECTO, humo.getTiempoRetardo());
    }

    @Test
    @DisplayName("aplicarSkin está sobrecargado: simple y con StatTrak")
    void sobrecargaAplicarSkin() {
        Rifle rifle = new Rifle("AK-47", 2700, "T");
        assertEquals(Arma.SKIN_POR_DEFECTO, rifle.getSkin());
        assertFalse(rifle.tieneSkinPersonalizada());

        rifle.aplicarSkin("Redline");
        assertEquals("Redline", rifle.getSkin());
        assertTrue(rifle.tieneSkinPersonalizada());

        rifle.aplicarSkin("Asiimov", true);
        assertEquals("StatTrak™ Asiimov", rifle.getSkin());

        rifle.aplicarSkin("Vulcan", false);
        assertEquals("Vulcan", rifle.getSkin());
    }

    @Test
    @DisplayName("Las invariantes rechazan datos inválidos")
    void invariantes() {
        assertThrows(IllegalArgumentException.class, () -> new Rifle("AK-47", -1, "T"));
        assertThrows(IllegalArgumentException.class, () -> new Rifle(null, 100, "T"));
        assertThrows(IllegalArgumentException.class, () -> new Rifle("  ", 100, "T"));
        assertThrows(IllegalArgumentException.class, () -> new Rifle("AK-47", 100, null));
        assertThrows(IllegalArgumentException.class, () -> new Rifle("AK-47", 100, "T", 0, 36, false));
        assertThrows(IllegalArgumentException.class, () -> new Pistola("P2000", 200, "CT", 13, -5, false));
        assertThrows(IllegalArgumentException.class, () -> new GranadaHumo("Smoke", 300, "CT", -1f, 18f));
        assertThrows(IllegalArgumentException.class, () -> new GranadaHumo("Smoke", 300, "CT", 1.5f, 0f));
        Rifle rifle = new Rifle("AK-47", 2700, "T");
        assertThrows(IllegalArgumentException.class, () -> rifle.aplicarSkin(""));
        assertEquals(Arma.SKIN_POR_DEFECTO, rifle.getSkin());
    }

    @Test
    @DisplayName("Polimorfismo: cada clase hija sobrescribe la acción principal")
    void polimorfismo() {
        List<Arma> arsenal = List.of(
                new Rifle("M4A4", 3100, "CT", 30, 33, true),
                new Pistola("USP-S", 200, "CT", 12, 35, true),
                new GranadaHumo("Smoke", 300, "CT"));

        assertTrue(arsenal.get(0).ejecutarAccionPrincipal().endsWith("[Mira activa]"));
        assertTrue(arsenal.get(1).ejecutarAccionPrincipal().endsWith("(Silenciado)"));
        assertTrue(arsenal.get(2).ejecutarAccionPrincipal().startsWith("Lanzando Smoke"));

        assertEquals("ui/hud/icons/rifle.png", arsenal.get(0).obtenerIconoHud());
        assertEquals("ui/hud/icons/default_weapon.png", arsenal.get(1).obtenerIconoHud());
        assertEquals("ui/hud/icons/smoke_grenade.png", arsenal.get(2).obtenerIconoHud());

        // Rifle redefine el método default de UICapable; Pistola lo hereda
        assertNotEquals(arsenal.get(0).emitirSonidoInspeccion(), arsenal.get(1).emitirSonidoInspeccion());
    }
}
