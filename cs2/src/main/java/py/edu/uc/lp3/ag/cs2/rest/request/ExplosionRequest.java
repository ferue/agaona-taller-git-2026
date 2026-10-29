package py.edu.uc.lp3.ag.cs2.rest.request;

import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;

/** Cuerpo JSON de POST /api/explosiones: qué granada HE explota y en qué punto. */
public record ExplosionRequest(Long granadaId, Double x, Double y, Double z) {

    public Long granadaIdObligatorio() {
        if (granadaId == null) {
            throw new CampoObligatorioException("granadaId");
        }
        return granadaId;
    }

    public Posicion centro() {
        return new PosicionRequest(x, y, z).aDominio();
    }
}
