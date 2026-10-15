package py.edu.uc.lp3.ag.cs2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import py.edu.uc.lp3.ag.cs2.model.UICapable;
import py.edu.uc.lp3.ag.cs2.model.Arma;
import py.edu.uc.lp3.ag.cs2.rest.response.UIElementResponse;

import java.util.Map;
import java.util.HashMap;

@RestController
public class UIController {

    // Un registro polimórfico general. Si agregas más armas, las registras aquí 
    // una sola vez o las obtienes de tu base de datos/servicio principal.
    private final Map<String, Arma> catalogoArmas = new HashMap<>();

    public UIController() {
        // Aquí registramos las entidades de forma polimórfica
        catalogoArmas.put("ak47", new py.edu.uc.lp3.ag.cs2.model.Rifle("AK-47", 2700, "Terrorist", 30, 35, true));
        catalogoArmas.put("pistola", new py.edu.uc.lp3.ag.cs2.model.Pistola("USP-S", 200, "Counter-Terrorist", 12, 25, true));
        catalogoArmas.put("humo", new py.edu.uc.lp3.ag.cs2.model.GranadaHumo("Smoke", 300, "Universal", 2.0f, 15.0f));
    }

    @GetMapping("/ui/{nombre}")
    public UIElementResponse consultarUI(@PathVariable String nombre) {
        // Buscamos directamente en el catálogo de forma genérica
        Arma arma = catalogoArmas.get(nombre.toLowerCase());

        if (arma == null) {
            return null; // O manejar un 404
        }

        // POLIMORFISMO PURO: El controlador no sabe si es Rifle, Pistola o Granada, 
        // solo le pide los datos a la interfaz UICapable que implementa la clase Arma.
        return new UIElementResponse(
            arma.getClass().getSimpleName(),
            arma.obtenerIconoHud(),
            "HUD_SLOT_PRINCIPAL",
            arma.emitirSonidoInspeccion(),
            200.0,
            "Elemento cargado correctamente"
        );
    }
}