package py.edu.uc.lp3.ag.cs2.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.domain.Pistola;
import py.edu.uc.lp3.ag.cs2.domain.Rifle;
import py.edu.uc.lp3.ag.cs2.domain.UICapable;
import py.edu.uc.lp3.ag.cs2.rest.RecursoNoEncontradoException;
import py.edu.uc.lp3.ag.cs2.rest.response.UIElementResponse;

import java.util.Locale;
import java.util.Map;

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

    @GetMapping("/ui/{nombre}")
    public UIElementResponse consultarUI(@PathVariable String nombre) {
        UICapable elemento = catalogo.get(nombre.toLowerCase(Locale.ROOT));
        if (elemento == null) {
            throw new RecursoNoEncontradoException(
                    "No hay elemento de UI para '" + nombre + "'. Valores válidos: " + catalogo.keySet());
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
