package py.edu.uc.lp3.ag.cs2.model;

import java.util.ArrayList;
import java.util.List;

public class Tienda {
    private String nombre;
    private List<Arma> armasDisponibles;
    protected int saldo;

    public Tienda() {
    }

    public Tienda(String nombre, int saldoInicial) {
        this.nombre = nombre;
        this.saldo = saldoInicial;
        this.armasDisponibles = new ArrayList<>();
    }

    public void agregarArma(Arma arma) {
        armasDisponibles.add(arma);
    }

    public boolean comprarArma(int index) {
        if (index < 0 || index >= armasDisponibles.size()) {
            return false;
        }
        Arma arma = armasDisponibles.get(index);
        if (saldo >= arma.getPrecio()) {
            saldo -= arma.getPrecio();
            armasDisponibles.remove(index);
            return true;
        }
        return false;
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

    public List<Arma> getArmasDisponibles() {
        return armasDisponibles;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
