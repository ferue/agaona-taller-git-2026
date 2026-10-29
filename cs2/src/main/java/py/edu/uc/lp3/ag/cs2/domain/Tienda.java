package py.edu.uc.lp3.ag.cs2.domain;

import py.edu.uc.lp3.ag.cs2.exception.validacion.CampoObligatorioException;
import py.edu.uc.lp3.ag.cs2.exception.validacion.SaldoInvalidoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tienda (buy menu) con un saldo y un catálogo de armas.
 *
 * Invariantes: el nombre no es vacío, el saldo nunca es negativo y el
 * catálogo solo cambia mediante agregarArma(...) y comprarArma(...).
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
        if (nombre == null || nombre.isBlank()) {
            throw new CampoObligatorioException("nombre");
        }
        if (saldoInicial < 0) {
            throw new SaldoInvalidoException(saldoInicial);
        }
        this.nombre = nombre.trim();
        this.saldo = saldoInicial;
    }

    public void agregarArma(Arma arma) {
        if (arma == null) {
            throw new CampoObligatorioException("arma");
        }
        armasDisponibles.add(arma);
    }

    public ResultadoCompra comprarArma(int indice) {
        if (indice < 0 || indice >= armasDisponibles.size()) {
            return ResultadoCompra.INDICE_INVALIDO;
        }
        Arma arma = armasDisponibles.get(indice);
        if (saldo < arma.getPrecio()) {
            return ResultadoCompra.SALDO_INSUFICIENTE;
        }
        saldo -= arma.getPrecio();
        armasDisponibles.remove(indice);
        return ResultadoCompra.EXITOSA;
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
