package py.edu.uc.lp3.ag.cs2.rest.request;

import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;

/** Coordenadas x, y, z que llegan en un JSON. Si se manda una, se mandan las tres. */
public record PosicionRequest(Double x, Double y, Double z) {

    public boolean algunaCoordenada() {
        return x != null || y != null || z != null;
    }

    public Posicion aDominio() {
        return new Posicion(requerir(x, "x"), requerir(y, "y"), requerir(z, "z"));
    }

    private static double requerir(Double valor, String campo) {
        if (valor == null) {
            throw new CampoObligatorioException(campo);
        }
        return valor;
    }
}
