package py.edu.uc.lp3.ag.cs2.controller;

import py.edu.uc.lp3.ag.cs2.model.*;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/armas")
public class ArmaController {

    @GetMapping("/probar")
    public Map<String, Object> probarArma(
            @RequestParam(defaultValue = "AK-47") String nombre,
            @RequestParam(defaultValue = "2700") int precio,
            @RequestParam(defaultValue = "rifle") String tipo) {

        Arma arma;

        if (tipo.equalsIgnoreCase("pistola")) {
            arma = new Pistola(nombre, precio, "CT/T", 12, 35, true);
        } else {
            arma = new Rifle(nombre, precio, "T", 30, 36, false);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("nombre", arma.getNombre());
        response.put("precio", arma.getPrecio());
        response.put("bando", arma.getBando());
        response.put("detalles", arma.obtenerDetalles());
        response.put("resultadoAccion", arma.ejecutarAccionPrincipal());

        return response;
    }
}                         