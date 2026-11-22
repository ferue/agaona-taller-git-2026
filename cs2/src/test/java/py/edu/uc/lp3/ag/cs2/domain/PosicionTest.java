package py.edu.uc.lp3.ag.cs2.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Posicion es un objeto valor: guarda x, y, z y se compara por valor.
 * Las distancias y desplazamientos se prueban en ReglasExplosionTest.
 */
class PosicionTest {

    @Test
    @DisplayName("Guarda las tres coordenadas")
    void coordenadas() {
        Posicion p = new Posicion(1, 2, 3);
        assertEquals(1, p.getX());
        assertEquals(2, p.getY());
        assertEquals(3, p.getZ());
        assertEquals("(1.0, 2.0, 3.0)", p.toString());
    }

    @Test
    @DisplayName("Dos posiciones con las mismas coordenadas son iguales")
    void igualdadPorValor() {
        assertEquals(new Posicion(6.5, 0, 0), new Posicion(6.5, 0, 0));
        assertEquals(new Posicion(1, 1, 1).hashCode(), new Posicion(1, 1, 1).hashCode());
        assertNotEquals(new Posicion(0, 0, 0), new Posicion(0, 0, 1));
    }
}
