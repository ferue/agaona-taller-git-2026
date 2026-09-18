package py.edu.uc.lp3.ag.cs2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "Servicio API REST - CS2 Modelado (CYT646)";
    }
}