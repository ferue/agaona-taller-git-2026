package py.edu.uc.lp3.ag.cs2.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.ag.cs2.rest.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ag.cs2.domain.Arma;
import py.edu.uc.lp3.ag.cs2.domain.ResultadoCompra;
import py.edu.uc.lp3.ag.cs2.domain.Tienda;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.TiendaNoEncontradaException;
import py.edu.uc.lp3.ag.cs2.rest.response.ArmaResponse;
import py.edu.uc.lp3.ag.cs2.service.FabricaArmas;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Tag(name = "Tienda (POO-06, en memoria)", description = "Tienda de compra de CS2 que vive en memoria (no se guarda en H2).")
@RestController
@RequestMapping("/tienda")
public class TiendaController {

    private final Map<String, Tienda> tiendas = new ConcurrentHashMap<>();
    private final AtomicInteger contador = new AtomicInteger();

    @Operation(summary = "Crear una tienda",
            description = "Crea una tienda con nombre y saldo inicial.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tienda creada"),
        @ApiResponse(responseCode = "400", description = "SaldoInvalidoException, CampoObligatorioException o ParametroFaltante",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> crearTienda(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "5000") int saldo) {
        Tienda tienda = new Tienda(nombre, saldo);
        String id = "tienda-" + contador.getAndIncrement();
        tiendas.put(id, tienda);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", id);
        response.put("nombre", tienda.getNombre());
        response.put("saldo", tienda.getSaldo());
        response.put("armasEnInventario", tienda.getCantidadArmas());
        return response;
    }

    @Operation(summary = "Agregar un arma a la tienda",
            description = "Agrega un arma al catálogo de la tienda.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Arma agregada"),
        @ApiResponse(responseCode = "400", description = "Datos del arma inválidos o TipoArmaNoSoportadoException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "TiendaNoEncontradaException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/agregar-arma")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> agregarArma(
            @PathVariable String id,
            @RequestParam String tipo,
            @RequestParam(defaultValue = "Arma") String nombre,
            @RequestParam(defaultValue = "500") int precio,
            @RequestParam(defaultValue = "T") String bando,
            @RequestParam(defaultValue = "12") int cargadorCapacidad,
            @RequestParam(defaultValue = "25") int danoBase,
            @RequestParam(defaultValue = "false") boolean atributoEspecial) {

        Tienda tienda = buscarTienda(id);
        Arma arma = FabricaArmas.crear(tipo, nombre, precio, bando, cargadorCapacidad, danoBase, atributoEspecial);
        tienda.agregarArma(arma);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tienda", tienda.getNombre());
        response.put("armaAgregada", ArmaResponse.de(arma));
        response.put("armasEnInventario", tienda.getCantidadArmas());
        return response;
    }

    @Operation(summary = "Ver el catálogo",
            description = "Lista las armas de la tienda y su saldo.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Catálogo"),
        @ApiResponse(responseCode = "404", description = "TiendaNoEncontradaException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/catalogo")
    public Map<String, Object> verCatalogo(@PathVariable String id) {
        Tienda tienda = buscarTienda(id);
        List<ArmaResponse> armas = tienda.getArmasDisponibles().stream().map(ArmaResponse::de).toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tienda", tienda.getNombre());
        response.put("saldo", tienda.getSaldo());
        response.put("totalArmas", armas.size());
        response.put("armas", armas);
        response.put("catalogo", tienda.mostrarCatalogo());
        return response;
    }

    /**
     * 200 si la compra se hizo, 400 si el índice no existe y
     * 409 (Conflict) si el saldo no alcanza.
     */
    @Operation(summary = "Comprar un arma",
            description = "Compra el arma de esa posición del catálogo.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compra realizada"),
        @ApiResponse(responseCode = "400", description = "Índice inválido (ResultadoCompra.INDICE_INVALIDO)"),
        @ApiResponse(responseCode = "404", description = "TiendaNoEncontradaException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Saldo insuficiente (ResultadoCompra.SALDO_INSUFICIENTE)")
    })
    @PostMapping("/{id}/comprar")
    public ResponseEntity<Map<String, Object>> comprarArma(
            @PathVariable String id,
            @RequestParam int indiceArma) {

        Tienda tienda = buscarTienda(id);
        ResultadoCompra resultado = tienda.comprarArma(indiceArma);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tienda", tienda.getNombre());
        response.put("resultado", resultado);
        response.put("mensaje", resultado.getMensaje());
        response.put("saldoRestante", tienda.getSaldo());
        response.put("armasRestantes", tienda.getCantidadArmas());

        HttpStatus status = switch (resultado) {
            case EXITOSA -> HttpStatus.OK;
            case INDICE_INVALIDO -> HttpStatus.BAD_REQUEST;
            case SALDO_INSUFICIENTE -> HttpStatus.CONFLICT;
        };
        return ResponseEntity.status(status).body(response);
    }

    private Tienda buscarTienda(String id) {
        Tienda tienda = tiendas.get(id);
        if (tienda == null) {
            throw new TiendaNoEncontradaException(id);
        }
        return tienda;
    }
}
