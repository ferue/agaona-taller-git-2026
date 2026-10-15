package py.edu.uc.lp3.ag.cs2.rest.response;

public record UIElementResponse(
    String tipo,
    String imagen,
    String posicion,
    String sonido,
    double velocidad,
    String mensaje
) {}