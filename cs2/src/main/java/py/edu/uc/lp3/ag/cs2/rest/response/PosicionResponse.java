package py.edu.uc.lp3.ag.cs2.rest.response;

import py.edu.uc.lp3.ag.cs2.domain.Posicion;

/** Posición en JSON, redondeada a 2 decimales para que se lea fácil. */
public record PosicionResponse(double x, double y, double z) {

    /** Devuelve null si no hay posición (por ejemplo, un arma del catálogo). */
    public static PosicionResponse de(Posicion p) {
        if (p == null) {
            return null;
        }
        return new PosicionResponse(redondear(p.getX()), redondear(p.getY()), redondear(p.getZ()));
    }

    public static double redondear(double valor) {
        return Math.round(valor * 100) / 100.0;
    }
}
