package modelo;

import java.io.Serializable;
import java.util.Date;

public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idReserva;
    private String diAlojamiento;
    private Date fechaEntrada;
    private Date fechaSalida;
    private String estado;
    private Integer numeroHuespedes;

    public Reserva(String idReserva, Integer numeroHuespedes, String estado, Date fechaSalida, Date fechaEntrada, String diAlojamiento) {
        this.idReserva = idReserva;
        this.numeroHuespedes = numeroHuespedes;
        this.estado = estado;
        this.fechaSalida = fechaSalida;
        this.fechaEntrada = fechaEntrada;
        this.diAlojamiento = diAlojamiento;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public String getDiAlojamiento() {
        return diAlojamiento;
    }

    public void setDiAlojamiento(String diAlojamiento) {
        this.diAlojamiento = diAlojamiento;
    }

    public Date getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(Date fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getNumeroHuespedes() {
        return numeroHuespedes;
    }

    public void setNumeroHuespedes(Integer numeroHuespedes) {
        this.numeroHuespedes = numeroHuespedes;
    }

    public void calcularCostoTotal() {}
    public void verificarDisponibilidad(Habitacion habitacion) {}
    public void registrarReserva(Reserva reserva, Integer habitacion) {}
    public void verificarEstado(String estado) {}
    public void solicitar(String solicitud) {}
    public void solicitar(Integer pedido, String cocina) {}
}