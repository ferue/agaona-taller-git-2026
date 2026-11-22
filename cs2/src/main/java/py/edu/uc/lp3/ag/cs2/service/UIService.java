package py.edu.uc.lp3.ag.cs2.service;

import org.springframework.stereotype.Service;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHumo;
import py.edu.uc.lp3.ag.cs2.domain.Pistola;
import py.edu.uc.lp3.ag.cs2.domain.Rifle;
import py.edu.uc.lp3.ag.cs2.domain.UICapable;
import py.edu.uc.lp3.ag.cs2.exception.noencontrado.ElementoUINoEncontradoException;

import java.util.Locale;
import java.util.Map;

/**
 * Catálogo de ejemplo de /ui/{nombre} (POO-07): elementos del HUD en memoria,
 * vistos solo como UICapable. Nombre desconocido → ElementoUINoEncontradoException.
 */
@Service
public class UIService {

    private final Map<String, UICapable> catalogo;

    public UIService() {
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

    /** Busca sin distinguir mayúsculas. */
    public UICapable buscar(String nombre) {
        UICapable elemento = catalogo.get(String.valueOf(nombre).toLowerCase(Locale.ROOT));
        if (elemento == null) {
            throw new ElementoUINoEncontradoException(nombre, catalogo.keySet());
        }
        return elemento;
    }
}
