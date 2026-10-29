package py.edu.uc.lp3.ag.cs2.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.domain.Pistola;
import py.edu.uc.lp3.ag.cs2.domain.Rifle;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaDuplicadaException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.BandoInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.CambioDeTipoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.PrecioExcedidoException;
import py.edu.uc.lp3.ag.cs2.exception.negocio.SkinNoPermitidaException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ArmaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.repository.ArmaRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la capa de negocio contra una base H2 en memoria.
 * Cada test corre en una transacción que se deshace al terminar.
 */
@SpringBootTest
@Transactional
class ArmaServiceTest {

    @Autowired
    private ArmaService armaService;

    @Autowired
    private ArmaRepository armaRepository;

    @Test
    @DisplayName("Crear guarda el arma y la herencia vuelve con su clase concreta")
    void crearYLeerPolimorfico() {
        Arma rifle = armaService.crear(new Rifle("AK-47", 2700, "T", 30, 36, false));
        Arma pistola = armaService.crear(new Pistola("USP-S", 200, "CT", 12, 35, true));
        Arma humo = armaService.crear(new GranadaHumo("Smoke", 300, "CT/T"));
        assertNotNull(rifle.getId());

        assertInstanceOf(Rifle.class, armaService.obtener(rifle.getId()));
        assertInstanceOf(Pistola.class, armaService.obtener(pistola.getId()));
        assertInstanceOf(GranadaHumo.class, armaService.obtener(humo.getId()));
        assertEquals(3, armaService.listar().size());
    }

    @Test
    @DisplayName("Regla 1: el nombre es único sin distinguir mayúsculas")
    void nombreUnico() {
        armaService.crear(new Rifle("AK-47", 2700, "T"));
        assertThrows(ArmaDuplicadaException.class, () -> armaService.crear(new Rifle("ak-47", 2500, "T")));
    }

    @Test
    @DisplayName("Regla 2: el bando debe ser T, CT o CT/T")
    void bandoValido() {
        assertThrows(BandoInvalidoException.class, () -> armaService.crear(new Rifle("AK-47", 2700, "Terroristas")));
        assertThrows(BandoInvalidoException.class, () -> armaService.listarPorBando("X"));
    }

    @Test
    @DisplayName("Regla 3: el precio no supera el dinero máximo de CS2")
    void precioMaximo() {
        assertThrows(PrecioExcedidoException.class,
                () -> armaService.crear(new Rifle("AWP Dorada", ArmaService.PRECIO_MAXIMO + 1, "CT")));
        assertNotNull(armaService.crear(new Rifle("AWP", ArmaService.PRECIO_MAXIMO, "CT")).getId());
    }

    @Test
    @DisplayName("Regla 4: las granadas no admiten skins")
    void granadaSinSkin() {
        GranadaHumo humo = new GranadaHumo("Smoke", 300, "CT");
        humo.aplicarSkin("Dorada");
        assertThrows(SkinNoPermitidaException.class, () -> armaService.crear(humo));
        assertEquals(0, armaRepository.count());
    }

    @Test
    @DisplayName("Actualizar copia los datos y recarga el cargador")
    void actualizar() {
        Rifle original = (Rifle) armaService.crear(new Rifle("M4A4", 3100, "CT", 30, 33, false));
        original.disparar();

        Rifle datos = new Rifle("M4A1-S", 2900, "CT", 20, 38, true);
        datos.aplicarSkin("Hyper Beast");
        Rifle actualizado = (Rifle) armaService.actualizar(original.getId(), datos);

        assertEquals(original.getId(), actualizado.getId());
        assertEquals("M4A1-S", actualizado.getNombre());
        assertEquals(2900, actualizado.getPrecio());
        assertEquals("Hyper Beast", actualizado.getSkin());
        assertEquals(20, actualizado.getCargadorCapacidad());
        assertEquals(20, actualizado.getMunicionRestante());
        assertTrue(actualizado.isTieneMira());
    }

    @Test
    @DisplayName("Regla 5: no se puede cambiar el tipo ni usar un nombre ajeno al actualizar")
    void actualizarInvalido() {
        Arma ak = armaService.crear(new Rifle("AK-47", 2700, "T"));
        armaService.crear(new Rifle("Galil AR", 1800, "T"));

        assertThrows(CambioDeTipoException.class,
                () -> armaService.actualizar(ak.getId(), new Pistola("AK-47", 2700, "T")));
        assertThrows(ArmaDuplicadaException.class,
                () -> armaService.actualizar(ak.getId(), new Rifle("galil ar", 2700, "T")));
        // Conservar el propio nombre sí está permitido
        assertEquals(2600, armaService.actualizar(ak.getId(), new Rifle("AK-47", 2600, "T")).getPrecio());
    }

    @Test
    @DisplayName("Regla 6: id inexistente → ArmaNoEncontradaException")
    void noEncontrada() {
        assertThrows(ArmaNoEncontradaException.class, () -> armaService.obtener(999L));
        assertThrows(ArmaNoEncontradaException.class, () -> armaService.actualizar(999L, new Rifle("X", 1, "T")));
        assertThrows(ArmaNoEncontradaException.class, () -> armaService.eliminar(999L));
    }

    @Test
    @DisplayName("Eliminar borra el arma y filtrar por bando devuelve solo ese bando")
    void eliminarYFiltrar() {
        Arma ak = armaService.crear(new Rifle("AK-47", 2700, "T"));
        armaService.crear(new Pistola("USP-S", 200, "CT"));

        List<Arma> terroristas = armaService.listarPorBando("T");
        assertEquals(1, terroristas.size());
        assertEquals("AK-47", terroristas.get(0).getNombre());

        armaService.eliminar(ak.getId());
        assertFalse(armaRepository.existsById(ak.getId()));
        assertEquals(1, armaService.listar().size());
    }
}
