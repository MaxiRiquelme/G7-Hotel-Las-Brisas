package modelo;

import java.io.Serializable;

public class Habitacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer numero;
    private String tipo;
    private Double precio;
    private String estado;
    private Integer capacidad;

    public Habitacion(Integer numero, String tipo, Double precio, String estado, Integer capacidad) {
        this.numero = numero;
        this.tipo = tipo;
        this.precio = precio;
        this.estado = estado;
        this.capacidad = capacidad;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public void actualizarEstadisticas(String estado) {}
    public void confirmarDisponibilidad(java.util.Date fecha) {}
    public void asignarPorSolicitud(String solicitud) {}
    public void calcularComisionBase(Double comision) {}
    public void consultarHistorico(String historico) {}
}