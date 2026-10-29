package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import py.edu.uc.lp3.ag.cs2.exception.validacion.CargadorInvalidoException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.DanoInvalidoException;

/**
 * Arma que dispara munición desde un cargador.
 *
 * Invariantes: cargadorCapacidad > 0, danoBase > 0 y
 * 0 <= municionRestante <= cargadorCapacidad.
 */
@Entity
@Table(name = "armas_de_fuego")
public abstract class ArmaDeFuego extends Arma {

    @Column(nullable = false)
    private int cargadorCapacidad;

    @Column(nullable = false)
    private int danoBase;

    @Column(nullable = false)
    private int municionRestante;

    /** Solo para JPA: los atributos se completan con los valores de la base. */
    protected ArmaDeFuego() {
        super();
    }

    protected ArmaDeFuego(String nombre, int precio, String bando, int cargadorCapacidad, int danoBase) {
        super(nombre, precio, bando);
        if (cargadorCapacidad <= 0) {
            throw new CargadorInvalidoException(cargadorCapacidad);
        }
        if (danoBase <= 0) {
            throw new DanoInvalidoException("El daño base debe ser mayor a 0", danoBase);
        }
        this.cargadorCapacidad = cargadorCapacidad;
        this.danoBase = danoBase;
        this.municionRestante = cargadorCapacidad;
    }

    public String disparar() {
        if (municionRestante > 0) {
            municionRestante--;
            return "¡PUM! Disparo efectuado con " + getNombre() + ". Munición restante: " + municionRestante;
        }
        return "¡CLIC! Cargador vacío en " + getNombre();
    }

    public String recargar() {
        if (municionRestante == cargadorCapacidad) {
            return getNombre() + " ya tiene el cargador lleno (" + cargadorCapacidad + ")";
        }
        municionRestante = cargadorCapacidad;
        return "Recargando " + getNombre() + "... Munición: " + municionRestante + "/" + cargadorCapacidad;
    }

    /** Copia cargador y daño; el arma actualizada queda con el cargador lleno. */
    @Override
    public void actualizarCon(Arma datos) {
        super.actualizarCon(datos);
        ArmaDeFuego otra = (ArmaDeFuego) datos;
        this.cargadorCapacidad = otra.cargadorCapacidad;
        this.danoBase = otra.danoBase;
        this.municionRestante = otra.cargadorCapacidad;
    }

    public int getCargadorCapacidad() { return cargadorCapacidad; }
    public int getMunicionRestante() { return municionRestante; }
    public int getDanoBase() { return danoBase; }
}
