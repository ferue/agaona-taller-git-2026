package py.edu.uc.lp3.ag.cs2.domain;

/**
 * Protocolo del HUD (POO-07): todo lo que se puede mostrar en la interfaz del juego.
 *
 * Lo implementan dos familias distintas: Arma (y sus hijas) y Jugador.
 * HudController habla solo con este tipo, sin saber si recibe un arma o un jugador.
 */
public interface UICapable {

    // Método abstracto obligatorio: icono que dibuja el HUD
    String obtenerIconoHud();

    // Método abstracto obligatorio: texto corto que acompaña al icono
    String etiquetaHud();

    // Método default (con comportamiento por defecto que se puede redefinir)
    default String emitirSonidoInspeccion() {
        return "Sonido genérico de equipamiento de Counter-Strike 2";
    }
}
