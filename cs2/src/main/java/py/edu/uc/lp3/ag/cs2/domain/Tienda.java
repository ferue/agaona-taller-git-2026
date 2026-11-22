package py.edu.uc.lp3.ag.cs2.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tienda (buy menu) con un saldo y un catálogo de armas.
 *
 * Guarda el nombre, el saldo y el catálogo. Las validaciones (nombre
 * obligatorio, saldo no negativo) y la regla de compra (el arma existe y el
 * saldo alcanza) están en TiendaService. El catálogo solo cambia con
 * agregarArma(...) y registrarCompra(...).
 */
public class Tienda {
    public static final int SALDO_INICIAL_CS2 = 800;

    private final String nombre;
    private final List<Arma> armasDisponibles = new ArrayList<>();
    private int saldo;

    // Constructor simple: dinero inicial de una partida de CS2
    public Tienda() {
        this("Tienda CS2", SALDO_INICIAL_CS2);
    }

    public Tienda(String nombre, int saldoInicial) {
        this.nombre = nombre == null ? null : nombre.trim();
        this.saldo = saldoInicial;
    }

    public void agregarArma(Arma arma) {
        armasDisponibles.add(arma);
    }

    /**
     * Cobra el arma de esa posición y la saca del catálogo. TiendaService ya
     * comprobó que la posición existe y que el saldo alcanza.
     */
    public void registrarCompra(int indice) {
        Arma arma = armasDisponibles.remove(indice);
        saldo -= arma.getPrecio();
    }

    public String mostrarCatalogo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Tienda ").append(nombre).append(" ===\n");
        sb.append("Saldo disponible: $").append(saldo).append("\n");
        sb.append("Armas disponibles:\n");
        for (int i = 0; i < armasDisponibles.size(); i++) {
            Arma a = armasDisponibles.get(i);
            sb.append(i).append(". ").append(a.obtenerDetalles())
              .append(" | Precio: $").append(a.getPrecio())
              .append(" | Bando: ").append(a.getBando()).append("\n");
        }
        return sb.toString();
    }

    /** Vista de solo lectura: el catálogo no se puede modificar desde afuera. */
    public List<Arma> getArmasDisponibles() {
        return Collections.unmodifiableList(armasDisponibles);
    }

    public int getCantidadArmas() {
        return armasDisponibles.size();
    }

    public int getSaldo() {
        return saldo;
    }

    public String getNombre() {
        return nombre;
    }
}
