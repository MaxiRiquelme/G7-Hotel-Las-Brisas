package controlador;

import modelo.*;
import java.io.*;
import java.util.*;

public class GestorDatos {
    private static final String ARCHIVO_DATOS = "datos_sistema.bin";
    private static GestorDatos instancia;

    private List<Huesped> huespedes;
    private List<Habitacion> habitaciones;
    private List<Reserva> reservas;
    private List<Producto> productos;
    private List<Venta> ventas;

    private List<ActualizacionListener> listeners = new ArrayList<>();

    private GestorDatos() {
        cargarDatos();
    }

    public static GestorDatos obtenerInstancia() {
        if (instancia == null) {
            instancia = new GestorDatos();
        }
        return instancia;
    }

    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        File archivo = new File(ARCHIVO_DATOS);
        if (archivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                huespedes = (List<Huesped>) ois.readObject();
                habitaciones = (List<Habitacion>) ois.readObject();
                reservas = (List<Reserva>) ois.readObject();
                productos = (List<Producto>) ois.readObject();
                ventas = (List<Venta>) ois.readObject();
            } catch (Exception e) {
                inicializarDatos();
            }
        } else {
            inicializarDatos();
        }
    }

    public void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_DATOS))) {
            oos.writeObject(huespedes);
            oos.writeObject(habitaciones);
            oos.writeObject(reservas);
            oos.writeObject(productos);
            oos.writeObject(ventas);
            notificarActualizacion("PRODUCTOS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inicializarDatos() {
        huespedes = new ArrayList<>();
        habitaciones = new ArrayList<>();
        reservas = new ArrayList<>();
        productos = new ArrayList<>();
        ventas = new ArrayList<>();

        for (int i = 101; i <= 115; i++)
            habitaciones.add(new Habitacion(String.valueOf(i), "Single", 45000.0));
        for (int i = 201; i <= 215; i++)
            habitaciones.add(new Habitacion(String.valueOf(i), "Matrimonial", 65000.0));
        for (int i = 301; i <= 307; i++)
            habitaciones.add(new Habitacion(String.valueOf(i), "Suite", 120000.0));

        productos.add(new Producto("P001", "Café Americano", 3000, 50));
        productos.add(new Producto("P002", "Capuchino", 4500, 40));
        productos.add(new Producto("P003", "Latte", 4500, 35));
        productos.add(new Producto("P004", "Sándwich Jamón", 8000, 25));
        productos.add(new Producto("P005", "Sándwich Pollo", 8500, 30));
        productos.add(new Producto("P006", "Ensalada Fresca", 7500, 20));
        productos.add(new Producto("P007", "Jugo Natural", 5000, 45));
        productos.add(new Producto("P008", "Postre Chocolate", 6000, 15));
        productos.add(new Producto("P009", "Galletas", 2500, 60));
        productos.add(new Producto("P010", "Agua Mineral", 2000, 80));

        guardarDatos();
    }

    public void agregarListener(ActualizacionListener listener) {
        listeners.add(listener);
    }


    private void notificarActualizacion(String tipo) {
        for (ActualizacionListener listener : listeners) {
            listener.onActualizacion(tipo);
        }
    }

    public List<Huesped> obtenerHuespedes() { return huespedes; }
    public List<Habitacion> obtenerHabitaciones() { return habitaciones; }
    public List<Reserva> obtenerReservas() { return reservas; }
    public List<Producto> obtenerProductos() { return productos; }

    public void agregarHuesped(Huesped h) {
        huespedes.add(h);
        guardarDatos();
    }

    public void agregarReserva(Reserva r) {
        reservas.add(r);
        guardarDatos();
    }

    public void agregarVenta(Venta v) {
        ventas.add(v);
        guardarDatos();
    }
}