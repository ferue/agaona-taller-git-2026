package py.edu.uc.lp3.ag.cs2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.lp3.ag.cs2.model.Pistola;
import py.edu.uc.lp3.ag.cs2.model.Rifle;
import py.edu.uc.lp3.ag.cs2.model.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.model.Tienda;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tienda")
public class TiendaController {

    private Map<String, Tienda> tiendas = new HashMap<>();
    private int contador = 0;

    @PostMapping
    public Map<String, Object> crearTienda(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "5000") int saldo) {
        String id = "tienda-" + (contador++);
        Tienda tienda = new Tienda(nombre, saldo);
        tiendas.put(id, tienda);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("nombre", nombre);
        response.put("saldo", saldo);
        response.put("armasEnInventario", 0);
        return response;
    }

    @PostMapping("/{id}/agregar-arma")
    public Map<String, Object> agregarArma(
            @PathVariable String id,
            @RequestParam String tipo,
            @RequestParam(defaultValue = "Arma") String nombre,
            @RequestParam(defaultValue = "500") int precio,
            @RequestParam(defaultValue = "T") String bando,
            @RequestParam(defaultValue = "12") int cargadorCapacidad,
            @RequestParam(defaultValue = "25") int danoBase,
            @RequestParam(defaultValue = "false") boolean atributoEspecial) {

        Tienda tienda = tiendas.get(id);
        Map<String, Object> response = new HashMap<>();

        if (tienda == null) {
            response.put("error", "Tienda no encontrada");
            return response;
        }

        if (tipo.equals("pistola")) {
            Pistola arma = new Pistola(nombre, precio, bando, cargadorCapacidad, danoBase, atributoEspecial);
            tienda.agregarArma(arma);
        } else if (tipo.equals("rifle")) {
            Rifle arma = new Rifle(nombre, precio, bando, cargadorCapacidad, danoBase, atributoEspecial);
            tienda.agregarArma(arma);
        } else if (tipo.equals("granada-humo")) {
            GranadaHumo arma = new GranadaHumo(nombre, precio, bando, 20, 15);
            tienda.agregarArma(arma);
        }

        response.put("tienda", tienda.getNombre());
        response.put("armaAgregada", tipo);
        response.put("armasEnInventario", tienda.getArmasDisponibles().size());
        return response;
    }

    @GetMapping("/{id}/catalogo")
    public Map<String, Object> verCatalogo(@PathVariable String id) {
        Tienda tienda = tiendas.get(id);
        Map<String, Object> response = new HashMap<>();

        if (tienda == null) {
            response.put("error", "Tienda no encontrada");
            return response;
        }

        response.put("tienda", tienda.getNombre());
        response.put("saldo", tienda.getSaldo());
        response.put("catalogo", tienda.mostrarCatalogo());
        response.put("totalArmas", tienda.getArmasDisponibles().size());
        return response;
    }

    @PostMapping("/{id}/comprar")
    public Map<String, Object> comprarArma(
            @PathVariable String id,
            @RequestParam int indiceArma) {

        Tienda tienda = tiendas.get(id);
        Map<String, Object> response = new HashMap<>();

        if (tienda == null) {
            response.put("error", "Tienda no encontrada");
            return response;
        }

        boolean compraExitosa = tienda.comprarArma(indiceArma);

        response.put("tienda", tienda.getNombre());
        response.put("compraExitosa", compraExitosa);
        response.put("saldoRestante", tienda.getSaldo());
        response.put("armasRestantes", tienda.getArmasDisponibles().size());

        if (!compraExitosa) {
            response.put("razon", "Indice invalido o saldo insuficiente");
        }

        return response;
    }
}
