package py.edu.uc.lp3.ag.cs2.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.exception.negocio.BandoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.JugadorDuplicadoException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.JugadorNoEncontradoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CoordenadaInvalidaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.VidaInvalidaException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class JugadorServiceTest {

    @Autowired
    private JugadorService jugadorService;

    @Test
    @DisplayName("Crear guarda el jugador con su posición")
    void crear() {
        Jugador j = jugadorService.crear(new Jugador("Fénix", "CT", new Posicion(1, 2, 3)));
        Jugador leido = jugadorService.obtener(j.getId());
        assertEquals(new Posicion(1, 2, 3), leido.getPosicion());
        assertEquals(1, jugadorService.listar().size());
    }

    @Test
    @DisplayName("Reglas: bando T o CT, nombre único e id inexistente")
    void reglas() {
        Posicion p = new Posicion(0, 0, 0);
        jugadorService.crear(new Jugador("Lobo", "T", p));
        assertThrows(JugadorDuplicadoException.class, () -> jugadorService.crear(new Jugador("lobo", "CT", p)));
        assertThrows(BandoInvalidoException.class, () -> jugadorService.crear(new Jugador("Otro", "CT/T", p)));
        assertThrows(JugadorNoEncontradoException.class, () -> jugadorService.obtener(999L));
    }

    @Test
    @DisplayName("Invariantes del jugador: las valida el service")
    void invariantes() {
        Posicion p = new Posicion(0, 0, 0);
        assertThrows(VidaInvalidaException.class, () -> jugadorService.crear(new Jugador("D", "T", 150, false, p)));
        assertThrows(VidaInvalidaException.class, () -> jugadorService.crear(new Jugador("D", "T", -1, false, p)));
        assertThrows(CampoObligatorioException.class, () -> jugadorService.crear(new Jugador(" ", "T", p)));
        assertThrows(CampoObligatorioException.class, () -> jugadorService.crear(new Jugador("D", null, p)));
        assertThrows(CampoObligatorioException.class, () -> jugadorService.crear(new Jugador("D", "T", null)));
        assertThrows(CoordenadaInvalidaException.class,
                () -> jugadorService.crear(new Jugador("D", "T", new Posicion(Double.NaN, 0, 0))));
        assertTrue(jugadorService.listar().isEmpty());
    }
}
