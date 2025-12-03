package controlador;

import modelo.Huesped;
import modelo.Habitacion;
import modelo.Reserva;
import modelo.Recepcionista;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorHotel {
    private static final String ARCHIVO_DATOS = "datos_hotel.bin";

    private List<Habitacion> habitaciones;
    private List<Huesped> huespedes;
    private List<Reserva> reservas;
    private List<Recepcionista> recepcionistas;

    public ControladorHotel() {
        cargarDatos();
    }

    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        File archivo = new File(ARCHIVO_DATOS);
        if (archivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                habitaciones = (List<Habitacion>) ois.readObject();
                huespedes = (List<Huesped>) ois.readObject();
                reservas = (List<Reserva>) ois.readObject();
                recepcionistas = (List<Recepcionista>) ois.readObject();
            } catch (Exception e) {
                inicializarDatos();
            }
        } else {
            inicializarDatos();
        }
    }

    private void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_DATOS))) {
            oos.writeObject(habitaciones);
            oos.writeObject(huespedes);
            oos.writeObject(reservas);
            oos.writeObject(recepcionistas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inicializarDatos() {
        habitaciones = new ArrayList<>();
        huespedes = new ArrayList<>();
        reservas = new ArrayList<>();
        recepcionistas = new ArrayList<>();

        for (int i = 101; i <= 115; i++)
            habitaciones.add(new Habitacion(String.valueOf(i), "Single", 45000.0));
        for (int i = 201; i <= 215; i++)
            habitaciones.add(new Habitacion(String.valueOf(i), "Matrimonial", 65000.0));
        for (int i = 301; i <= 307; i++)
            habitaciones.add(new Habitacion(String.valueOf(i), "Suite", 120000.0));

        guardarDatos();
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
            huespedes.add(huesped);
        }

        huesped.setNumeroHabitacion(numeroHabitacion);
        hab.setDisponible(false);

        String idReserva = "RES" + System.currentTimeMillis();
        Reserva nuevaReserva = new Reserva(idReserva, huesped, hab,
                new Date(), hab.getPrecioNoche(), metodoPago);

        reservas.add(nuevaReserva);
        guardarDatos();

        return nuevaReserva;
    }

    public void liberarHabitacion(String numHabitacion) {
        Habitacion h = buscarHabitacion(numHabitacion);
        if (h != null) {
            h.setDisponible(true);
            guardarDatos();
        }
    }

    public List<Reserva> obtenerReservas() { return reservas; }
    public List<Huesped> obtenerHuespedes() { return huespedes; }
    public List<Habitacion> obtenerHabitaciones() { return habitaciones; }
}