package py.edu.uc.lp3.ag.cs2.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArmaDeFuegoTest {

    @Test
    @DisplayName("Disparar descuenta munición y nunca baja de 0")
    void dispararDescuentaMunicion() {
        Pistola pistola = new Pistola("P2000", 200, "CT", 2, 30, false);

        assertTrue(pistola.disparar().contains("Munición restante: 1"));
        assertTrue(pistola.disparar().contains("Munición restante: 0"));
        assertTrue(pistola.disparar().startsWith("¡CLIC!"));
        assertEquals(0, pistola.getMunicionRestante());
    }

    @Test
    @DisplayName("Recargar vuelve a llenar el cargador")
    void recargar() {
        Rifle rifle = new Rifle("AK-47", 2700, "T", 3, 36, false);
        rifle.disparar();
        rifle.disparar();
        assertEquals(1, rifle.getMunicionRestante());

        assertTrue(rifle.recargar().contains("3/3"));
        assertEquals(3, rifle.getMunicionRestante());
        assertTrue(rifle.recargar().contains("ya tiene el cargador lleno"));
    }
}
