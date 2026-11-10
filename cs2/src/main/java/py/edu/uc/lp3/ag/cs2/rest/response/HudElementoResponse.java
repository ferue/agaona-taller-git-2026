package py.edu.uc.lp3.ag.cs2.rest.response;

import py.edu.uc.lp3.ag.cs2.domain.UICapable;

/**
 * Un elemento del HUD en JSON. Se arma solo con los mensajes de UICapable:
 * no importa si el objeto es un arma o un jugador.
 */
public record HudElementoResponse(String clase, String etiqueta, String icono, String sonido) {

    public static HudElementoResponse de(UICapable elemento) {
        return new HudElementoResponse(
                elemento.getClass().getSimpleName(),
                elemento.etiquetaHud(),
                elemento.obtenerIconoHud(),
                elemento.emitirSonidoInspeccion());
    }
}
