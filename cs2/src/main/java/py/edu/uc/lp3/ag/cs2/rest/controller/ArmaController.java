package py.edu.uc.lp3.ag.cs2.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.ag.cs2.rest.response.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ag.cs2.rest.response.ArmaResponse;
import py.edu.uc.lp3.ag.cs2.service.DemostracionArmasService;
import py.edu.uc.lp3.ag.cs2.service.DemostracionArmasService.ResultadoPolimorfismo;
import py.edu.uc.lp3.ag.cs2.service.DemostracionArmasService.ResultadoPrueba;
import py.edu.uc.lp3.ag.cs2.service.DemostracionArmasService.ResultadoRafaga;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Armas - demostración POO-06", description = "Endpoints de demostración sin persistencia: crean armas en memoria para ver polimorfismo.")
@RestController
@RequestMapping("/api/armas")
public class ArmaController {

    private final DemostracionArmasService demostracionService;

    public ArmaController(DemostracionArmasService demostracionService) {
        this.demostracionService = demostracionService;
    }

    /** Construye un arma (con skin opcional) y ejecuta su acción principal. */
    @Operation(summary = "Probar un arma",
            description = "Crea un arma en memoria (no la guarda) y ejecuta su acción principal.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos del arma y resultado de la acción"),
        @ApiResponse(responseCode = "400", description = "PrecioNegativoException, TipoArmaNoSoportadoException o CampoObligatorioException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/probar")
    public Map<String, Object> probarArma(
            @RequestParam(defaultValue = "AK-47") String nombre,
            @RequestParam(defaultValue = "2700") int precio,
            @RequestParam(defaultValue = "rifle") String tipo,
            @RequestParam(defaultValue = "T") String bando,
            @RequestParam(required = false) String skin,
            @RequestParam(required = false) Boolean statTrak) {

        ResultadoPrueba r = demostracionService.probar(tipo, nombre, precio, bando, skin, statTrak);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("arma", ArmaResponse.de(r.arma()));
        response.put("resultadoAccion", r.resultadoAccion());
        return response;
    }

    /**
     * Dispara N veces y luego recarga: muestra cómo cambia el estado
     * (munición) y que nunca baja de 0.
     */
    @Operation(summary = "Disparar una ráfaga",
            description = "Dispara varias veces con un arma de fuego en memoria.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultado de cada disparo"),
        @ApiResponse(responseCode = "400", description = "ArmaNoDisparaException, CantidadDisparosInvalidaException o ParametroDeOtroTipo",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/rafaga")
    public Map<String, Object> rafaga(
            @RequestParam(defaultValue = "pistola") String tipo,
            @RequestParam(defaultValue = "5") int disparos) {

        ResultadoRafaga r = demostracionService.rafaga(tipo, disparos);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("arma", ArmaResponse.de(r.arma()));
        response.put("disparos", r.disparos());
        response.put("municionAntesDeRecargar", r.municionAntesDeRecargar());
        response.put("recarga", r.recarga());
        response.put("municionFinal", r.municionFinal());
        return response;
    }

    /** Polimorfismo: la misma lista de mensajes a cada clase hija (ver DemostracionArmasService). */
    @Operation(summary = "Ver polimorfismo",
            description = "Un rifle, una pistola y una granada de humo responden al mismo mensaje.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista con el resultado de cada arma")
    })
    @GetMapping("/polimorfismo")
    public List<Map<String, Object>> polimorfismo() {
        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (ResultadoPolimorfismo r : demostracionService.polimorfismo()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("tipo", r.tipo());
            item.put("detalles", r.detalles());
            item.put("accionPrincipal", r.accionPrincipal());
            item.put("sonidoInspeccion", r.sonidoInspeccion());
            respuesta.add(item);
        }
        return respuesta;
    }
}
