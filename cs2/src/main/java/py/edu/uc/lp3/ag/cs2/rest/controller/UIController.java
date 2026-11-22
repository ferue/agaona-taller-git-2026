package py.edu.uc.lp3.ag.cs2.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.ag.cs2.rest.response.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.domain.Pistola;
import py.edu.uc.lp3.ag.cs2.domain.Rifle;
import py.edu.uc.lp3.ag.cs2.domain.UICapable;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ElementoUINoEncontradoException;
import py.edu.uc.lp3.ag.cs2.rest.response.UIElementResponse;

import java.util.Locale;
import java.util.Map;

@Tag(name = "HUD - /ui de POO-07", description = "Elementos de HUD de ejemplo, a través de la interfaz UICapable.")
@RestController
public class UIController {

    private final Map<String, UICapable> catalogo;

    public UIController() {
        Rifle ak = new Rifle("AK-47", 2700, "T", 30, 36, false);
        Pistola usp = new Pistola("USP-S", 200, "CT", 12, 35, true);
        GranadaHumo humo = new GranadaHumo("Smoke", 300, "CT/T");

        catalogo = Map.of(
                "ak47", ak,
                "rifle", ak,
                "pistola", usp,
                "usp", usp,
                "smoke", humo,
                "humo", humo);
    }

    @Operation(summary = "Consultar un elemento del HUD",
            description = "Valores: ak47, usp, smoke.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Elemento de HUD"),
        @ApiResponse(responseCode = "404", description = "ElementoUINoEncontradoException",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/ui/{nombre}")
    public UIElementResponse consultarUI(@PathVariable String nombre) {
        UICapable elemento = catalogo.get(nombre.toLowerCase(Locale.ROOT));
        if (elemento == null) {
            throw new ElementoUINoEncontradoException(nombre, catalogo.keySet());
        }

        // POLIMORFISMO: el controlador solo conoce el contrato UICapable,
        // no la clase concreta; cada arma responde con su propio icono y sonido.
        return new UIElementResponse(
                elemento.getClass().getSimpleName(),
                elemento.obtenerIconoHud(),
                "HUD_SLOT_PRINCIPAL",
                elemento.emitirSonidoInspeccion(),
                200.0,
                "Cargado correctamente para CS2");
    }
}
