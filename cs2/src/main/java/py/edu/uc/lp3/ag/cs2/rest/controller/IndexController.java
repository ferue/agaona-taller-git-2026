package py.edu.uc.lp3.ag.cs2.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("servicio", "Servicio API REST - CS2 Modelado (CYT646)");
        response.put("estado", "OK");
        response.put("endpoints", List.of(
                "GET  /api/armas/probar?tipo=rifle&nombre=AK-47&precio=2700&bando=T&skin=Redline&statTrak=true",
                "GET  /api/armas/rafaga?tipo=pistola&disparos=5",
                "GET  /api/armas/polimorfismo",
                "POST /tienda?nombre=CT&saldo=5000",
                "POST /tienda/{id}/agregar-arma?tipo=rifle&nombre=M4A1-S&precio=2900&bando=CT",
                "GET  /tienda/{id}/catalogo",
                "POST /tienda/{id}/comprar?indiceArma=0",
                "GET  /ui/{nombre}   (ak47, usp, smoke)"));
        return response;
    }
}
