package py.edu.uc.lp3.ag.cs2.rest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import py.edu.uc.lp3.ag.cs2.domain.GranadaHE;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.domain.Rifle;
import py.edu.uc.lp3.ag.cs2.rest.response.HudElementoResponse;
import py.edu.uc.lp3.ag.cs2.service.ArmaService;
import py.edu.uc.lp3.ag.cs2.service.JugadorService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** GET /api/hud: armas y jugadores de la base, tratados como UICapable. */
@SpringBootTest
@Transactional
class HudControllerTest {

    @Autowired private HudController controller;
    @Autowired private ArmaService armaService;
    @Autowired private JugadorService jugadorService;

    @Test
    @DisplayName("Lista dos familias distintas con los mensajes del protocolo")
    void hud() {
        Rifle ak = new Rifle("AK-47", 2700, "T");
        ak.aplicarSkin("Redline");
        armaService.crear(ak);
        armaService.crear(new GranadaHE("HE Grenade", 300, "CT/T"));
        jugadorService.crear(new Jugador("Fénix", "CT", new Posicion(1, 0, 0)));

        List<HudElementoResponse> hud = controller.hud();

        assertEquals(3, hud.size());
        assertEquals(new HudElementoResponse("Rifle", "AK-47 | Redline", "ui/hud/icons/rifle.png",
                "clack-clack_metalico_rifle.wav"), hud.get(0));
        assertEquals("ui/hud/icons/he_grenade.png", hud.get(1).icono());
        assertEquals(new HudElementoResponse("Jugador", "Fénix (CT) 100 HP", "ui/hud/icons/player_ct.png",
                "radio_ct_listo.wav"), hud.get(2));
    }
}
