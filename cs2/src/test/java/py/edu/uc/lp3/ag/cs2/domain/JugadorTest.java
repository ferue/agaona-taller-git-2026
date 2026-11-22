package py.edu.uc.lp3.ag.cs2.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JugadorTest {

    private static final Posicion P = new Posicion(0, 0, 0);

    @Test
    @DisplayName("perderVida descuenta la vida y en 0 queda eliminado")
    void perderVida() {
        Jugador j = new Jugador("C", "T", 20, false, P);
        j.perderVida(15);
        assertEquals(5, j.getVida());
        assertTrue(j.estaVivo());
        j.perderVida(5);
        assertEquals(0, j.getVida());
        assertFalse(j.estaVivo());
    }

    @Test
    @DisplayName("El constructor simple deja la vida completa y sin chaleco")
    void constructorSimple() {
        Jugador j = new Jugador(" Fénix ", "CT", P);
        assertEquals("Fénix", j.getNombre());
        assertEquals(Jugador.VIDA_MAXIMA, j.getVida());
        assertFalse(j.isTieneChaleco());
    }

    @Test
    @DisplayName("Jugador cumple el protocolo UICapable igual que un arma")
    void protocoloHud() {
        UICapable ct = new Jugador("Fénix", "CT", P);
        UICapable eliminado = new Jugador("Lobo", "T", 0, false, P);
        UICapable arma = new Rifle("AK-47", 2700, "T");

        assertEquals("ui/hud/icons/player_ct.png", ct.obtenerIconoHud());
        assertEquals("Fénix (CT) 100 HP", ct.etiquetaHud());
        assertEquals("radio_ct_listo.wav", ct.emitirSonidoInspeccion());
        assertEquals("ui/hud/icons/player_dead.png", eliminado.obtenerIconoHud());
        assertEquals("Lobo (T) ELIMINADO", eliminado.etiquetaHud());
        assertEquals("AK-47", arma.etiquetaHud());
    }
}
