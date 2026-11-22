package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Jugada del escenario del POO-09 («ataque coordinado»): se guarda solo
 * cuando A2 y A3 aceptaron sus disparos y la HE explotó. Guarda qué granada
 * explotó, dónde, el resultado de cada compañero y el de la explosión.
 *
 * Como el resto del dominio, solo guarda el estado: las reglas y las
 * consultas a los compañeros están en JugadaService.
 */
@Entity
@Table(name = "jugadas")
public class JugadaAtaque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Long granadaId;

    @Column(nullable = false, length = 60)
    private String granada;

    @Embedded
    private Posicion centro;

    @Column(nullable = false, length = 300)
    private String resultadoA2;

    @Column(nullable = false, length = 300)
    private String resultadoA3;

    @Column(nullable = false)
    private int jugadoresDanados;

    @Column(nullable = false)
    private int jugadoresEliminados;

    @Column(nullable = false)
    private int armasDesplazadas;

    @Column(nullable = false, length = 300)
    private String resumen;

    /** Solo para JPA. */
    protected JugadaAtaque() {
    }

    public JugadaAtaque(LocalDateTime fecha, Long granadaId, String granada, Posicion centro,
                        String resultadoA2, String resultadoA3,
                        int jugadoresDanados, int jugadoresEliminados, int armasDesplazadas, String resumen) {
        this.fecha = fecha;
        this.granadaId = granadaId;
        this.granada = granada;
        this.centro = centro;
        this.resultadoA2 = recortar(resultadoA2);
        this.resultadoA3 = recortar(resultadoA3);
        this.jugadoresDanados = jugadoresDanados;
        this.jugadoresEliminados = jugadoresEliminados;
        this.armasDesplazadas = armasDesplazadas;
        this.resumen = recortar(resumen);
    }

    public Long getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public Long getGranadaId() { return granadaId; }
    public String getGranada() { return granada; }
    public Posicion getCentro() { return centro; }
    public String getResultadoA2() { return resultadoA2; }
    public String getResultadoA3() { return resultadoA3; }
    public int getJugadoresDanados() { return jugadoresDanados; }
    public int getJugadoresEliminados() { return jugadoresEliminados; }
    public int getArmasDesplazadas() { return armasDesplazadas; }
    public String getResumen() { return resumen; }

    /** La columna admite 300 caracteres. */
    private static String recortar(String texto) {
        return texto == null || texto.length() <= 300 ? texto : texto.substring(0, 297) + "...";
    }
}
