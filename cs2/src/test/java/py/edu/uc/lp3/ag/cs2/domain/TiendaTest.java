package py.edu.uc.lp3.ag.cs2.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TiendaTest {

    private Tienda tiendaConDosArmas() {
        Tienda tienda = new Tienda("Buy Zone", 3000);
        tienda.agregarArma(new Rifle("AK-47", 2700, "T"));
        tienda.agregarArma(new Pistola("Deagle", 700, "T", 7, 53, false));
        return tienda;
    }

    @Test
    @DisplayName("Compra exitosa descuenta saldo y quita el arma")
    void compraExitosa() {
        Tienda tienda = tiendaConDosArmas();
        assertEquals(ResultadoCompra.EXITOSA, tienda.comprarArma(0));
        assertEquals(300, tienda.getSaldo());
        assertEquals(1, tienda.getCantidadArmas());
    }

    @Test
    @DisplayName("Saldo insuficiente e índice inválido no cambian el estado")
    void compraFallida() {
        Tienda tienda = tiendaConDosArmas();
        tienda.comprarArma(0);

        assertEquals(ResultadoCompra.SALDO_INSUFICIENTE, tienda.comprarArma(0));
        assertEquals(ResultadoCompra.INDICE_INVALIDO, tienda.comprarArma(99));
        assertEquals(ResultadoCompra.INDICE_INVALIDO, tienda.comprarArma(-1));
        assertEquals(300, tienda.getSaldo());
        assertEquals(1, tienda.getCantidadArmas());
    }

    @Test
    @DisplayName("El catálogo no se puede modificar desde afuera")
    void catalogoSoloLectura() {
        Tienda tienda = tiendaConDosArmas();
        assertThrows(UnsupportedOperationException.class, () -> tienda.getArmasDisponibles().clear());
        assertEquals(2, tienda.getCantidadArmas());
    }

    @Test
    @DisplayName("El constructor por defecto deja la tienda lista para usar")
    void constructorPorDefecto() {
        Tienda tienda = new Tienda();
        tienda.agregarArma(new Pistola("Glock-18", 200, "T"));
        assertEquals(Tienda.SALDO_INICIAL_CS2, tienda.getSaldo());
        assertEquals(1, tienda.getCantidadArmas());
    }

    @Test
    @DisplayName("Invariantes de la tienda")
    void invariantes() {
        assertThrows(IllegalArgumentException.class, () -> new Tienda("CT", -1));
        assertThrows(IllegalArgumentException.class, () -> new Tienda(" ", 100));
        assertThrows(NullPointerException.class, () -> new Tienda().agregarArma(null));
    }

    @Test
    @DisplayName("El catálogo muestra el precio una sola vez por arma")
    void catalogoSinPrecioDuplicado() {
        Tienda tienda = new Tienda("CT", 1000);
        tienda.agregarArma(new GranadaHumo("Smoke", 300, "CT"));
        String linea = tienda.mostrarCatalogo().lines().filter(l -> l.startsWith("0.")).findFirst().orElseThrow();
        assertEquals(linea.indexOf("Precio"), linea.lastIndexOf("Precio"));
        assertFalse(linea.isBlank());
    }
}
