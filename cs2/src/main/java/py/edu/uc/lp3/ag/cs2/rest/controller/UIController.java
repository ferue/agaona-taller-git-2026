package py.edu.uc.lp3.ag.cs2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.ag.cs2.model.Arma;
import py.edu.uc.lp3.ag.cs2.model.Rifle;
import py.edu.uc.lp3.ag.cs2.model.Pistola;
import py.edu.uc.lp3.ag.cs2.model.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.rest.response.UIElementResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UIController {

    private final Map<String, Arma> catalogoArmas = new HashMap<>();

    public UIController() {
        // Registramos las instancias asociadas a nombres clave en la URL
        Rifle ak = new Rifle("AK-47", 2700, "Terrorist", 30, 35, true);
        Pistola usp = new Pistola("USP-S", 200, "Counter-Terrorist", 12, 25, true);
        GranadaHumo humo = new GranadaHumo("Smoke", 300, "Universal", 2.0f, 15.0f);

        catalogoArmas.put("ak47", ak);
        catalogoArmas.put("rifle", ak);
        catalogoArmas.put("pistola", usp);
        catalogoArmas.put("usp", usp);
        catalogoArmas.put("smoke", humo);
        catalogoArmas.put("humo", humo);
    }

    @GetMapping("/ui/{nombre}")
    public UIElementResponse consultarUI(@PathVariable String nombre) {
        Arma arma = catalogoArmas.get(nombre.toLowerCase());

        if (arma == null) {
            return null; // O manejar un 404 si prefieres
        }

        // POLIMORFISMO: El controlador no sabe qué clase hija es, solo le exige el contrato UICapable
        return new UIElementResponse(
            arma.getClass().getSimpleName(),
            arma.obtenerIconoHud(),
            "HUD_SLOT_PRINCIPAL",
            arma.emitirSonidoInspeccion(),
            200.0,
            "Cargado correctamente para CS2"
        );
    }
}