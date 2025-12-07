package modelo;

import java.io.Serializable;
import java.util.Date;

public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idReserva;
    private Huesped huesped;
    private Habitacion habitacion;
    private Date fechaEntrada;
    private Date fechaSalida;
    private int diasEstadia;
    private String estado;
    private Double totalPagado;
    private String metodoPago;

    public Reserva(String idReserva, Huesped huesped, Habitacion habitacion,
                   Date fechaEntrada, int diasEstadia, Double totalPagado, String metodoPago) {
        this.idReserva = idReserva;
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.fechaEntrada = fechaEntrada;
        this.diasEstadia = diasEstadia;
        this.fechaSalida = calcularFechaSalida(fechaEntrada, diasEstadia);
        this.estado = "ACTIVA";
        this.totalPagado = totalPagado;
        this.metodoPago = metodoPago;
    }

    private Date calcularFechaSalida(Date fechaEntrada, int dias) {
        long milisegundos = fechaEntrada.getTime() + ((long) dias * 24 * 60 * 60 * 1000);
        return new Date(milisegundos);
    }

    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }

    public Habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(Habitacion habitacion) { this.habitacion = habitacion; }

    public Date getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(Date fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public Date getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(Date fechaSalida) { this.fechaSalida = fechaSalida; }

    public int getDiasEstadia() { return diasEstadia; }
    public void setDiasEstadia(int diasEstadia) {
        this.diasEstadia = diasEstadia;
        this.fechaSalida = calcularFechaSalida(this.fechaEntrada, diasEstadia);
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getTotalPagado() { return totalPagado; }
    public void setTotalPagado(Double totalPagado) { this.totalPagado = totalPagado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public void calcularCostoTotal() {}
    public void verificarDisponibilidad(Habitacion habitacion) {}
    public void registrarReserva(Reserva reserva, Habitacion habitacion) {}
    public void verificarEstado(String estado) {}
    public void solicitar(String solicitud) {}
}