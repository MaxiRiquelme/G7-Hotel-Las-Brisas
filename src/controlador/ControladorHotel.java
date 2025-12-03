package controlador;

import modelo.*;
import java.util.*;
import java.util.stream.Collectors;

public class ControladorHotel {
    private GestorDatos gestor;
    private List<Habitacion> habitaciones;
    private List<Huesped> huespedes;
    private List<Reserva> reservas;

    public ControladorHotel() {
        this.gestor = GestorDatos.obtenerInstancia();
        this.habitaciones = gestor.obtenerHabitaciones();
        this.huespedes = gestor.obtenerHuespedes();
        this.reservas = gestor.obtenerReservas();
    }

    public List<Habitacion> buscarHabitacionesDisponibles(String tipo) {
        return habitaciones.stream()
                .filter(h -> h.isDisponible() && (tipo.equals("TODAS") || h.getTipo().equals(tipo)))
                .collect(Collectors.toList());
    }

    public Habitacion buscarHabitacion(String numero) {
        return habitaciones.stream()
                .filter(h -> h.getNumero().equals(numero))
                .findFirst()
                .orElse(null);
    }

    public Huesped buscarCliente(String rut) {
        return huespedes.stream()
                .filter(h -> h.getRut().equals(rut))
                .findFirst()
                .orElse(null);
    }

    public Reserva realizarReserva(String rut, String nombre, String apellido,
                                   String numeroHabitacion, String metodoPago) throws Exception {
        Habitacion hab = buscarHabitacion(numeroHabitacion);

        if (hab == null || !hab.isDisponible()) {
            throw new Exception("La habitación no está disponible o no existe.");
        }

        Huesped huesped = buscarCliente(rut);
        if (huesped == null) {
            huesped = new Huesped(rut, nombre, apellido);
            gestor.agregarHuesped(huesped);
        }

        huesped.setNumeroHabitacion(numeroHabitacion);
        hab.setDisponible(false);

        String idReserva = "RES" + System.currentTimeMillis();
        Reserva nuevaReserva = new Reserva(idReserva, huesped, hab,
                new Date(), hab.getPrecioNoche(), metodoPago);

        gestor.agregarReserva(nuevaReserva);
        return nuevaReserva;
    }

    public void liberarHabitacion(String numHabitacion) {
        Habitacion h = buscarHabitacion(numHabitacion);
        if (h != null) {
            h.setDisponible(true);
            gestor.guardarDatos();
        }
    }

    public List<Reserva> obtenerReservas() { return reservas; }
    public List<Huesped> obtenerHuespedes() { return huespedes; }
    public List<Habitacion> obtenerHabitaciones() { return habitaciones; }
}