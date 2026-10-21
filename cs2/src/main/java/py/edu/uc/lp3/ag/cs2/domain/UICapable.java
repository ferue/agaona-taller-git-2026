package py.edu.uc.lp3.ag.cs2.domain;

public interface UICapable {
    
    // Método abstracto obligatorio
    String obtenerIconoHud();
    
    // Método default (con comportamiento por defecto que se puede redefinir)
    default String emitirSonidoInspeccion() {
        return "Sonido genérico de equipamiento de Counter-Strike 2";
    }
}