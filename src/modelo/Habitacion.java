package modelo;

import java.io.Serializable;

public class Habitacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String numero;
    private String tipo;
    private Double precio;
    private String estado;
    private Integer capacidad;

    // Constructor
    public Habitacion(String numero, String tipo, Double precio) {
        this.numero = numero;
        this.tipo = tipo;
        this.precio = precio;
        this.estado = "DISPONIBLE";
        this.capacidad = obtenerCapacidad(tipo);
    }

    private Integer obtenerCapacidad(String tipo) {
        switch(tipo) {
            case "Single": return 1;
            case "Matrimonial": return 2;
            case "Suite": return 4;
            default: return 1;
        }
    }

    // Getters y Setters
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getPrecioNoche() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getCapacidad() { return capacidad; }

    public boolean isDisponible() {
        return estado.equals("DISPONIBLE");
    }

    public void setDisponible(boolean disponible) {
        this.estado = disponible ? "DISPONIBLE" : "OCUPADA";
    }

    // Métodos del diagrama
    public void verificarEstado() {
        System.out.println("Estado de habitación " + numero + ": " + estado);
    }

    public void asignarPorSolicitud(String solicitud) {}
    public void calcularComisionBase(Double comision) {}
    public void consultarHistorico(String historico) {}
}