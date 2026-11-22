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
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.ArmaDeFuego;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.domain.Pistola;
import py.edu.uc.lp3.ag.cs2.domain.Rifle;
import py.edu.uc.lp3.ag.cs2.exception.negocio.ArmaNoDisparaException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CantidadDisparosInvalidaException;
import py.edu.uc.lp3.ag.cs2.rest.response.ArmaResponse;
import py.edu.uc.lp3.ag.cs2.service.FabricaArmas;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Armas - demostración POO-06", description = "Endpoints de demostración sin persistencia: crean armas en memoria para ver polimorfismo.")
@RestController
@RequestMapping("/api/armas")
public class ArmaController {

    public static final int MAX_DISPAROS = 100;

    /**
     * Construye un arma y ejecuta su acción principal.
     * Si llega "skin" se aplica con aplicarSkin(String); si además llega
     * "statTrak" se usa la sobrecarga aplicarSkin(String, boolean).
     */
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

        Arma arma = FabricaArmas.crear(tipo, nombre, precio, bando);
        if (skin != null) {
            if (statTrak == null) {
                arma.aplicarSkin(skin);
            } else {
                arma.aplicarSkin(skin, statTrak);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("arma", ArmaResponse.de(arma));
        response.put("resultadoAccion", arma.ejecutarAccionPrincipal());
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

        if (disparos < 1 || disparos > MAX_DISPAROS) {
            throw new CantidadDisparosInvalidaException(disparos, MAX_DISPAROS);
        }
        Arma arma = FabricaArmas.crear(tipo, tipo.toUpperCase(), 0, "T");
        if (!(arma instanceof ArmaDeFuego armaDeFuego)) {
            throw new ArmaNoDisparaException(tipo);
        }

        List<String> resultados = new ArrayList<>();
        for (int i = 0; i < disparos; i++) {
            resultados.add(armaDeFuego.disparar());
        }
        int municionAntesDeRecargar = armaDeFuego.getMunicionRestante();
        String recarga = armaDeFuego.recargar();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("arma", ArmaResponse.de(arma));
        response.put("disparos", resultados);
        response.put("municionAntesDeRecargar", municionAntesDeRecargar);
        response.put("recarga", recarga);
        response.put("municionFinal", armaDeFuego.getMunicionRestante());
        return response;
    }

    /**
     * Recorre una List<Arma> y llama al mismo método en cada elemento:
     * cada clase hija responde con su propia sobrescritura.
     */
    @Operation(summary = "Ver polimorfismo",
            description = "Un rifle, una pistola y una granada de humo responden al mismo mensaje.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista con el resultado de cada arma")
    })
    @GetMapping("/polimorfismo")
    public List<Map<String, Object>> polimorfismo() {
        List<Arma> arsenal = List.of(
                new Rifle("M4A4", 3100, "CT", 30, 33, true),
                new Pistola("Glock-18", 200, "T", 20, 30, false),
                new GranadaHumo("Smoke", 300, "CT/T"));

        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (Arma arma : arsenal) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("tipo", arma.getClass().getSimpleName());
            item.put("detalles", arma.obtenerDetalles());
            item.put("accionPrincipal", arma.ejecutarAccionPrincipal());
            item.put("sonidoInspeccion", arma.emitirSonidoInspeccion());
            respuesta.add(item);
        }
        return respuesta;
    }
}
