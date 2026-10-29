package py.edu.uc.lp3.ag.cs2.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.DanoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.VidaInvalidaException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JugadorTest {

    private static final Posicion P = new Posicion(0, 0, 0);

    @Test
    @DisplayName("recibirDano descuenta vida, el chaleco lo reduce a la mitad")
    void recibirDano() {
        Jugador sinChaleco = new Jugador("A", "T", P);
        assertEquals(40, sinChaleco.recibirDano(40));
        assertEquals(60, sinChaleco.getVida());

        Jugador conChaleco = new Jugador("B", "CT", 100, true, P);
        assertEquals(20, conChaleco.recibirDano(40));
        assertEquals(80, conChaleco.getVida());
    }

    @Test
    @DisplayName("La vida nunca baja de 0 y en 0 queda eliminado")
    void eliminado() {
        Jugador j = new Jugador("C", "T", 20, false, P);
        assertEquals(20, j.recibirDano(74));
        assertEquals(0, j.getVida());
        assertFalse(j.estaVivo());
    }

    @Test
    @DisplayName("Invariantes del jugador")
    void invariantes() {
        assertThrows(VidaInvalidaException.class, () -> new Jugador("D", "T", 150, false, P));
        assertThrows(VidaInvalidaException.class, () -> new Jugador("D", "T", -1, false, P));
        assertThrows(CampoObligatorioException.class, () -> new Jugador(" ", "T", P));
        assertThrows(CampoObligatorioException.class, () -> new Jugador("D", "T", null));
        assertThrows(DanoInvalidoException.class, () -> new Jugador("D", "T", P).recibirDano(-5));
    }
}
