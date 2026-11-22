package py.edu.uc.lp3.ag.cs2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Arma arrojadiza con un tiempo de retardo en segundos
 * (ArmaService controla que no sea negativo).
 */
@Entity
@Table(name = "granadas")
public abstract class Granada extends Arma {

    @Column(nullable = false)
    private float tiempoRetardo;

    /** Solo para JPA: los atributos se completan con los valores de la base. */
    protected Granada() {
        super();
    }

    protected Granada(String nombre, int precio, String bando, float tiempoRetardo) {
        super(nombre, precio, bando);
        this.tiempoRetardo = tiempoRetardo;
    }

    /** En CS2 las granadas no tienen skins. */
    @Override
    public boolean admiteSkin() {
        return false;
    }

    @Override
    public void actualizarCon(Arma datos) {
        super.actualizarCon(datos);
        this.tiempoRetardo = ((Granada) datos).tiempoRetardo;
    }

    public float getTiempoRetardo() {
        return tiempoRetardo;
    }
}
