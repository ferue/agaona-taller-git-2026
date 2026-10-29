package py.edu.uc.lp3.ag.cs2.rest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.rest.request.ArmaRequest;
import py.edu.uc.lp3.ag.cs2.rest.response.ArmaGuardadaResponse;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ArmaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.PrecioNegativoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.TipoArmaNoSoportadoException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas del controlador CRUD llamando a sus métodos, igual que ControladoresTest.
 * Los códigos 400/404/409 de las excepciones los aplica ManejadorErrores.
 */
@SpringBootTest
@Transactional
class ArmaCrudControllerTest {

    @Autowired
    private ArmaCrudController controller;

    private static ArmaRequest rifle(String nombre, int precio) {
        return new ArmaRequest("rifle", nombre, precio, "T", "Asiimov", true, 30, 36, false,
                null, null, null, null, null, null, null, null);
    }

    /** Pedido solo con los campos obligatorios. */
    private static ArmaRequest basico(String tipo, String nombre, Integer precio, String bando) {
        return new ArmaRequest(tipo, nombre, precio, bando, null, null, null, null, null,
                null, null, null, null, null, null, null, null);
    }

    @Test
    @DisplayName("POST devuelve 201 con Location y el arma guardada")
    void crear() {
        ResponseEntity<ArmaGuardadaResponse> r = controller.crear(rifle("AK-47", 2700));
        ArmaGuardadaResponse body = r.getBody();

        assertEquals(201, r.getStatusCode().value());
        assertEquals("/api/armas/" + body.id(), r.getHeaders().getLocation().toString());
        assertEquals("Rifle", body.tipo());
        assertEquals("StatTrak™ Asiimov", body.skin());
    }

    @Test
    @DisplayName("Ciclo completo: crear, leer, listar, actualizar y borrar")
    void cicloCrud() {
        Long id = controller.crear(rifle("AK-47", 2700)).getBody().id();

        assertEquals("AK-47", controller.obtener(id).nombre());
        assertEquals(1, controller.listar(null).size());
        assertEquals(1, controller.listar("T").size());
        assertEquals(0, controller.listar("CT").size());

        ArmaGuardadaResponse actualizada = controller.actualizar(id, rifle("AK-47", 2500));
        assertEquals(2500, actualizada.precio());

        assertEquals(204, controller.eliminar(id).getStatusCode().value());
        assertThrows(ArmaNoEncontradaException.class, () -> controller.obtener(id));
    }

    @Test
    @DisplayName("Pistola con valores estándar y datos inválidos")
    void valoresPorDefectoYErrores() {
        ArmaRequest pistola = basico("pistola", "Glock-18", 200, "T");
        assertEquals("Pistola Glock-18 | Daño: 30 | Cargador: 12", controller.crear(pistola).getBody().detalles());

        assertThrows(TipoArmaNoSoportadoException.class,
                () -> controller.crear(basico("escopeta", "Nova", 1050, "CT")));
        assertThrows(CampoObligatorioException.class,
                () -> controller.crear(basico("rifle", "AK-47", null, "T")));
        assertThrows(PrecioNegativoException.class, () -> controller.crear(rifle("AK-47", -5)));
    }
}
