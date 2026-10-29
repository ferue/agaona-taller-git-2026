package py.edu.uc.lp3.ag.cs2.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CoordenadaInvalidaException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PosicionTest {

    private static final Posicion ORIGEN = new Posicion(0, 0, 0);

    @Test
    @DisplayName("distanciaA calcula la distancia en línea recta")
    void distancia() {
        assertEquals(5.0, new Posicion(3, 4, 0).distanciaA(ORIGEN), 1e-9);
        assertEquals(0.0, ORIGEN.distanciaA(ORIGEN), 1e-9);
    }

    @Test
    @DisplayName("desplazarDesde aleja el punto del centro y no modifica el original")
    void desplazar() {
        Posicion p = new Posicion(2, 0, 0);
        assertEquals(new Posicion(6.5, 0, 0), p.desplazarDesde(ORIGEN, 4.5));
        assertEquals(new Posicion(2, 0, 0), p);

        Posicion diagonal = new Posicion(3, 4, 0).desplazarDesde(ORIGEN, 5);
        assertEquals(10.0, diagonal.distanciaA(ORIGEN), 1e-9);
    }

    @Test
    @DisplayName("Un punto en el centro de la explosión sale hacia arriba")
    void desplazarDesdeElCentro() {
        assertEquals(new Posicion(1, 1, 6), new Posicion(1, 1, 0).desplazarDesde(new Posicion(1, 1, 0), 6));
    }

    @Test
    @DisplayName("Invariantes: coordenadas finitas y otra posición obligatoria")
    void invariantes() {
        assertThrows(CoordenadaInvalidaException.class, () -> new Posicion(Double.NaN, 0, 0));
        assertThrows(CoordenadaInvalidaException.class, () -> new Posicion(0, Double.POSITIVE_INFINITY, 0));
        assertThrows(CampoObligatorioException.class, () -> ORIGEN.distanciaA(null));
    }
}
