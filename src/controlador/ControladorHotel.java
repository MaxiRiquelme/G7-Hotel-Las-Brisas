package controlador;

import modelo.Huesped;
import modelo.Habitacion;
import modelo.Reserva;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorHotel {
    private static final String ARCHIVO_DATOS = "datos_hotel.bin";

    private List<Habitacion> habitaciones;
    private List<Huesped> huespedes;
    private List<Reserva> reservas;

    public ControladorHotel() {
        cargarDatos();
    }

    // --- Persistencia ---
    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        File archivo = new File(ARCHIVO_DATOS);
        if (archivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                habitaciones = (List<Habitacion>) ois.readObject();
                huespedes = (List<Huesped>) ois.readObject();
                reservas = (List<Reserva>) ois.readObject();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inicializarDatos() {
        habitaciones = new ArrayList<>();
        huespedes = new ArrayList<>();
        reservas = new ArrayList<>();

        // Inicializar las 37 habitaciones mencionadas en el texto
        // Ejemplo simplificado:
        for (int i = 101; i <= 115; i++) habitaciones.add(new Habitacion(String.valueOf(i), "Single", 45000));
        for (int i = 201; i <= 215; i++) habitaciones.add(new Habitacion(String.valueOf(i), "Matrimonial", 65000));
        for (int i = 301; i <= 307; i++) habitaciones.add(new Habitacion(String.valueOf(i), "Suite", 120000));

        guardarDatos();
    }

    // --- Lógica del Proceso de Reserva ---

    // Paso 1: Verificar disponibilidad
    public List<Habitacion> buscarHabitacionesDisponibles(String tipo) {
        return habitaciones.stream()
                .filter(h -> h.isDisponible() && (tipo.equals("TODAS") || h.getTipo().equals(tipo)))
                .collect(Collectors.toList());
    }

    public Habitacion buscarHabitacion(String numero) {
        return habitaciones.stream().filter(h -> h.getNumero().equals(numero)).findFirst().orElse(null);
    }

    public Huesped buscarCliente(String rut) {
        return huespedes.stream().filter(c -> c.getRut().equals(rut)).findFirst().orElse(null);
    }

    // Pasos: Pago, Registro, Formulario
    public Reserva realizarReserva(String rut, String nombre, String numHabitacion, String metodoPago) throws Exception {
        Habitacion hab = buscarHabitacion(numHabitacion);

        if (hab == null || !hab.isDisponible()) {
            throw new Exception("La habitación no está disponible o no existe.");
        }

        // Gestión de Cliente (Formulario de datos personales)
        Huesped huesped = buscarCliente(rut);
        if (huesped == null) {
            huesped = new Huesped(rut, nombre, numHabitacion);
            huespedes.add(huesped);
        } else {
            // Actualizamos que ahora ocupa esta habitación
            huesped.setNumeroHabitacion(numHabitacion);
        }

        // Registrar Reserva y Ocupar Habitación
        hab.setDisponible(false); // Habitación ocupada

        Reserva nuevaReserva = new Reserva(System.currentTimeMillis(), huesped, hab, hab.getPrecioNoche());
        reservas.add(nuevaReserva);

        guardarDatos();
        return nuevaReserva;
    }

    // Método auxiliar para liberar habitación (Check-out futuro)
    public void liberarHabitacion(String numHabitacion) {
        Habitacion h = buscarHabitacion(numHabitacion);
        if (h != null) {
            h.setDisponible(true);
            guardarDatos();
        }
    }
}