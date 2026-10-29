package py.edu.uc.lp3.ag.cs2.rest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import py.edu.uc.lp3.ag.cs2.domain.ResultadoCompra;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaNoDisparaException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ElementoUINoEncontradoException;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.TiendaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CantidadDisparosInvalidaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.PrecioNegativoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.TipoArmaNoSoportadoException;
import py.edu.uc.lp3.ag.cs2.rest.response.ArmaResponse;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas de los controladores llamando directamente a sus métodos.
 * Los códigos 400/404 que generan las excepciones los aplica ManejadorErrores.
 */
class ControladoresTest {

    @Test
    @DisplayName("/api/armas/probar conserva el bando y aplica skin StatTrak")
    void probarArma() {
        Map<String, Object> r = new ArmaController().probarArma("AK-47", 2700, "rifle", "T", "Asiimov", true);
        ArmaResponse arma = (ArmaResponse) r.get("arma");
        assertEquals("Rifle", arma.tipo());
        assertEquals("T", arma.bando());
        assertEquals("StatTrak™ Asiimov", arma.skin());
    }

    @Test
    @DisplayName("/api/armas/probar rechaza precio negativo y tipo desconocido")
    void probarArmaInvalida() {
        ArmaController c = new ArmaController();
        assertThrows(PrecioNegativoException.class, () -> c.probarArma("X", -100, "rifle", "T", null, null));
        assertThrows(TipoArmaNoSoportadoException.class, () -> c.probarArma("Nova", 1050, "escopeta", "CT", null, null));
    }

    @Test
    @DisplayName("/api/armas/rafaga muestra la munición bajando y la recarga")
    void rafaga() {
        Map<String, Object> r = new ArmaController().rafaga("pistola", 3);
        assertEquals(3, ((List<?>) r.get("disparos")).size());
        assertEquals(9, r.get("municionAntesDeRecargar"));
        assertEquals(12, r.get("municionFinal"));
        assertThrows(ArmaNoDisparaException.class, () -> new ArmaController().rafaga("humo", 3));
        assertThrows(CantidadDisparosInvalidaException.class, () -> new ArmaController().rafaga("rifle", 0));
    }

    @Test
    @DisplayName("Flujo de tienda: crear, agregar, comprar y errores")
    void flujoTienda() {
        TiendaController c = new TiendaController();
        String id = (String) c.crearTienda("CT", 3000).get("id");

        c.agregarArma(id, "rifle", "M4A1-S", 2900, "CT", 25, 38, true);
        c.agregarArma(id, "granada-humo", "Smoke", 300, "CT", 12, 25, false);
        Map<String, Object> catalogo = c.verCatalogo(id);
        assertEquals(2, catalogo.get("totalArmas"));
        ArmaResponse primera = ((List<?>) catalogo.get("armas")).stream()
                .map(ArmaResponse.class::cast).findFirst().orElseThrow();
        assertEquals("CT", primera.bando());

        ResponseEntity<Map<String, Object>> ok = c.comprarArma(id, 0);
        assertEquals(200, ok.getStatusCode().value());
        assertEquals(ResultadoCompra.EXITOSA, ok.getBody().get("resultado"));
        assertEquals(100, ok.getBody().get("saldoRestante"));

        assertEquals(409, c.comprarArma(id, 0).getStatusCode().value());
        assertEquals(400, c.comprarArma(id, 5).getStatusCode().value());

        assertThrows(TipoArmaNoSoportadoException.class,
                () -> c.agregarArma(id, "escopeta", "Nova", 1050, "CT", 8, 26, false));
        assertThrows(TiendaNoEncontradaException.class, () -> c.verCatalogo("no-existe"));
    }

    @Test
    @DisplayName("/ui/{nombre} responde por polimorfismo y 404 si no existe")
    void ui() {
        UIController c = new UIController();
        assertEquals("Rifle", c.consultarUI("AK47").tipo());
        assertEquals("ui/hud/icons/smoke_grenade.png", c.consultarUI("smoke").imagen());
        assertThrows(ElementoUINoEncontradoException.class, () -> c.consultarUI("awp"));
    }
}
