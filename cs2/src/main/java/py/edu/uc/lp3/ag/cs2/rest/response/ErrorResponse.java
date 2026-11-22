package py.edu.uc.lp3.ag.cs2.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * Cuerpo JSON de todos los errores. "tipo" es el nombre de la excepción que
 * lo produjo, así se ve qué parte de la jerarquía respondió.
 */
@Schema(description = "Cuerpo de todos los errores")
public record ErrorResponse(
        @Schema(description = "Código HTTP", example = "409") int status,
        @Schema(description = "Nombre del código HTTP", example = "Conflict") String error,
        @Schema(description = "Excepción de la jerarquía que produjo el error", example = "GranadaYaDetonadaException") String tipo,
        @Schema(description = "Explicación", example = "La granada 'HE Grenade' ya fue detonada y no se puede volver a usar") String mensaje) {
}
