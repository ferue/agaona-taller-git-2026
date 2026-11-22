package py.edu.uc.lp3.ag.cs2.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;

/** Cuerpo JSON de POST /api/explosiones: qué granada HE explota y en qué punto. */
public record ExplosionRequest(
        @Schema(description = "Id de una granada HE sin detonar", example = "6") Long granadaId,
        @Schema(description = "Centro de la explosión, en metros", example = "0") Double x,
        @Schema(description = "Centro de la explosión, en metros", example = "0") Double y,
        @Schema(description = "Altura del centro, en metros", example = "0") Double z) {

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
