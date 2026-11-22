package py.edu.uc.lp3.ag.cs2.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.ag.cs2.domain.ResultadoCompra;
import py.edu.uc.lp3.ag.cs2.domain.Tienda;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.TiendaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.PrecioNegativoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.SaldoInvalidoException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Validaciones y regla de compra de la tienda (sin base de datos). */
class TiendaServiceTest {

    private final TiendaService service = new TiendaService(new ArmaService(null));

    private String tiendaConDosArmas() {
        String id = service.crear("Buy Zone", 3000).id();
        service.agregarArma(id, "rifle", "AK-47", 2700, "T", 30, 36, false);
        service.agregarArma(id, "pistola", "Deagle", 700, "T", 7, 53, false);
        return id;
    }

    @Test
    @DisplayName("Compra exitosa descuenta saldo y quita el arma")
    void compraExitosa() {
        String id = tiendaConDosArmas();
        assertEquals(ResultadoCompra.EXITOSA, service.comprar(id, 0));
        Tienda tienda = service.buscar(id);
        assertEquals(300, tienda.getSaldo());
        assertEquals(1, tienda.getCantidadArmas());
    }

    @Test
    @DisplayName("Saldo insuficiente e índice inválido no cambian el estado")
    void compraFallida() {
        String id = tiendaConDosArmas();
        service.comprar(id, 0);

        assertEquals(ResultadoCompra.SALDO_INSUFICIENTE, service.comprar(id, 0));
        assertEquals(ResultadoCompra.INDICE_INVALIDO, service.comprar(id, 99));
        assertEquals(ResultadoCompra.INDICE_INVALIDO, service.comprar(id, -1));
        assertEquals(300, service.buscar(id).getSaldo());
        assertEquals(1, service.buscar(id).getCantidadArmas());
    }

    @Test
    @DisplayName("Validaciones: nombre, saldo, arma inválida e id inexistente")
    void validaciones() {
        assertThrows(SaldoInvalidoException.class, () -> service.crear("CT", -1));
        assertThrows(CampoObligatorioException.class, () -> service.crear(" ", 100));
        String id = service.crear("CT", 100).id();
        assertThrows(PrecioNegativoException.class,
                () -> service.agregarArma(id, "rifle", "AK-47", -5, "T", 30, 36, false));
        assertEquals(0, service.buscar(id).getCantidadArmas());
        assertThrows(TiendaNoEncontradaException.class, () -> service.buscar("no-existe"));
    }
}
