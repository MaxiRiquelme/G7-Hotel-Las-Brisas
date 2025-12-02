package modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String fechaReserva;
    private Huesped huesped;
    private Habitacion habitacion;
    private int totalPagado;

    public Reserva(long id, Huesped huesped, Habitacion habitacion, int totalPagado) {
        this.id = id;
        this.fechaReserva = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.totalPagado = totalPagado;
    }

    public long getId() { return id; }
    public String getFechaReserva() { return fechaReserva; }
    public Huesped getCliente() { return huesped; }
    public Habitacion getHabitacion() { return habitacion; }
    public int getTotalPagado() { return totalPagado; }
}