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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
