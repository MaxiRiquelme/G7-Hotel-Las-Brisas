package modelo;

import java.io.Serializable;

public class Habitacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String numero;
    private String tipo; // "Single", "Matrimonial", "Suite"
    private int precioNoche;
    private boolean disponible;

    public Habitacion(String numero, String tipo, int precioNoche) {
        this.numero = numero;
        this.tipo = tipo;
        this.precioNoche = precioNoche;
        this.disponible = true; // Por defecto disponible
    }

    public String getNumero() { return numero; }
    public String getTipo() { return tipo; }
    public int getPrecioNoche() { return precioNoche; }
    public boolean isDisponible() { return disponible; }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "Habitación " + numero + " (" + tipo + ")";
    }
}