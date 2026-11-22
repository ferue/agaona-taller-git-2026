package py.edu.uc.lp3.ag.cs2.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import py.edu.uc.lp3.ag.cs2.domain.Jugador;

/**
 * Cuerpo JSON de POST /api/jugadores.
 * Obligatorios: nombre, bando, x, y, z. Opcionales: vida (100) y tieneChaleco (false).
 */
public record JugadorRequest(
        @Schema(description = "Nombre único", example = "Tigre") String nombre,
        @Schema(description = "T o CT", example = "CT") String bando,
        @Schema(description = "Opcional, 0 a 100 (por defecto 100)", example = "100") Integer vida,
        @Schema(description = "Opcional. El chaleco reduce a la mitad el daño de una explosión", example = "true") Boolean tieneChaleco,
        @Schema(description = "Posición en metros", example = "6") Double x,
        @Schema(description = "Posición en metros", example = "0") Double y,
        @Schema(description = "Altura en metros", example = "0") Double z
) {
    public Jugador aDominio() {
        return new Jugador(
                nombre,
                bando,
                vida != null ? vida : Jugador.VIDA_MAXIMA,
                Boolean.TRUE.equals(tieneChaleco),
                new PosicionRequest(x, y, z).aDominio());
    }
}
