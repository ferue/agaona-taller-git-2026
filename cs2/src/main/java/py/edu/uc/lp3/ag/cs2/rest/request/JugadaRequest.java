package py.edu.uc.lp3.ag.cs2.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import py.edu.uc.lp3.ag.cs2.domain.Posicion;

/**
 * Cuerpo JSON de POST /api/jugadas (escenario del POO-09). La granada y el
 * centro son obligatorios; los ids de las armas de A2 y A3 son opcionales
 * (si no llegan, se usan los de application.properties).
 */
@Schema(description = "Ataque coordinado: granada HE propia + un disparo en A2 + un disparo en A3")
public record JugadaRequest(
        @Schema(description = "Id de una granada HE propia, sin detonar", example = "6") Long granadaId,
        @Schema(description = "Centro de la explosión, en metros", example = "0") Double x,
        @Schema(description = "Centro de la explosión, en metros", example = "0") Double y,
        @Schema(description = "Altura del centro, en metros", example = "0") Double z,
        @Schema(description = "Opcional: id del arma que dispara en A2 (por defecto cs2.a2.arma-id)", example = "1") Long armaA2Id,
        @Schema(description = "Opcional: id del objetivo del disparo en A2 (por defecto cs2.a2.objetivo-id)", example = "2") Long objetivoA2Id,
        @Schema(description = "Opcional: id del arma que dispara en A3 (por defecto cs2.a3.arma-id)", example = "4") Long armaA3Id) {

    public Posicion centro() {
        return new PosicionRequest(x, y, z).aDominio();
    }
}
