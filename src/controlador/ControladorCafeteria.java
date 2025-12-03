package controlador;

import modelo.Huesped;
import modelo.DetalleVenta;
import modelo.Producto;
import modelo.Venta;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorCafeteria {
    // Cambio de nombre de archivo para no mezclar con el casino
    private static final String ARCHIVO_DATOS = "datos_cafeteria.bin";

    private List<Producto> productos;
    private List<Huesped> huespedes;
    private List<Venta> ventas;
    private List<DetalleVenta> carritoActual;

    public ControladorCafeteria() {
        this.carritoActual = new ArrayList<>();
        cargarDatosPersistentes();
    }

    @SuppressWarnings("unchecked")
    private void cargarDatosPersistentes() {
        File archivo = new File(ARCHIVO_DATOS);
        if (archivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                productos = (List<Producto>) ois.readObject();
                huespedes = (List<Huesped>) ois.readObject();
                ventas = (List<Venta>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                inicializarDatosPorDefecto();
            }
        } else {
            inicializarDatosPorDefecto();
        }
    }

    private void guardarDatosPersistentes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_DATOS))) {
            oos.writeObject(productos);
            oos.writeObject(huespedes);
            oos.writeObject(ventas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inicializarDatosPorDefecto() {
        productos = new ArrayList<>();
        huespedes = new ArrayList<>();
        ventas = new ArrayList<>();

        // Productos típicos de Cafetería de Hotel
        productos.add(new Producto("CAF01", "Café Cortado", 1800, 100));
        productos.add(new Producto("CAF02", "Medialuna", 900, 50));
        productos.add(new Producto("CAF03", "Jugo Naranja Natural", 2500, 30));
        productos.add(new Producto("ALM01", "Sandwich Club House", 6500, 20));
        productos.add(new Producto("ALM02", "Ensalada César", 5800, 15));

        guardarDatosPersistentes();
    }

    // --- Métodos de Gestión de Productos (Idénticos al base) ---
    public List<Producto> obtenerProductos() { return productos; }

    public List<Producto> buscarProductos(String consulta) {
        String lower = consulta.toLowerCase();
        return productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(lower) || p.getIdProducto().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public Producto buscarProductoPorId(String id) {
        return productos.stream().filter(p -> p.getIdProducto().equals(id)).findFirst().orElse(null);
    }

    public void agregarProductoNuevo(String id, String nombre, int precio, int stock) {
        productos.add(new Producto(id, nombre, precio, stock));
        guardarDatosPersistentes();
    }

    public void aumentarStock(String id, int cantidad) {
        Producto p = buscarProductoPorId(id);
        if (p != null) {
            p.agregarStock(cantidad);
            guardarDatosPersistentes();
        }
    }

    // --- Carrito ---
    public void agregarAlCarrito(Producto p, int cantidad) throws Exception {
        if (p.getStock() < cantidad) throw new Exception("Stock insuficiente.");
        boolean encontrado = false;
        for (DetalleVenta d : carritoActual) {
            if (d.getProducto().getIdProducto().equals(p.getIdProducto())) {
                if (d.getCantidad() + cantidad > p.getStock()) throw new Exception("Stock insuficiente.");
                // Truco para actualizar: removemos y agregamos de nuevo o mutamos (aquí simplificado)
                int nuevaCant = d.getCantidad() + cantidad;
                carritoActual.remove(d);
                carritoActual.add(new DetalleVenta(p, nuevaCant));
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            carritoActual.add(new DetalleVenta(p, cantidad));
        }
    }

    public void vaciarCarrito() { carritoActual.clear(); }
    public List<DetalleVenta> getCarrito() { return carritoActual; }
    public int calcularTotalCarrito() {
        return carritoActual.stream().mapToInt(DetalleVenta::getSubtotal).sum();
    }

    // --- Clientes y Ventas (Lógica modificada para Hotel) ---
    public Huesped buscarCliente(String rut) {
        return huespedes.stream().filter(c -> c.getRut().equals(rut)).findFirst().orElse(null);
    }

    public Venta finalizarVenta(String rut, String nombreCliente, String habitacion, String metodoPago,
                                int montoEfectivo, String numTarjeta) throws Exception {

        if (carritoActual.isEmpty()) throw new Exception("El carrito está vacío.");

        // Lógica Cliente / Huésped
        Huesped huesped = buscarCliente(rut);
        if (huesped == null) {
            huesped = new Huesped(rut, nombreCliente, habitacion);
            huespedes.add(huesped);
        } else {
            // Actualizamos habitación si viene una nueva
            if(habitacion != null && !habitacion.isEmpty()) {
                huesped.setNumeroHabitacion(habitacion);
            }
        }

        int total = calcularTotalCarrito();
        int vuelto = 0;

        // Validaciones de Pago
        if (metodoPago.equals("EFECTIVO")) {
            if (montoEfectivo < total) throw new Exception("Dinero insuficiente.");
            vuelto = montoEfectivo - total;
        } else if (metodoPago.equals("TARJETA")) {
            if (numTarjeta.isEmpty()) throw new Exception("Número de tarjeta requerido.");
        } else if (metodoPago.equals("CARGO_HABITACION")) {
            if (!huesped.esHuesped()) {
                throw new Exception("El cliente no tiene una habitación asignada para cargar la cuenta.");
            }
            // Aquí iría lógica para conectar con el sistema de Reservas y verificar crédito
        }

        // Descontar Stock
        for (DetalleVenta d : carritoActual) {
            d.getProducto().disminuirStock(d.getCantidad());
        }

        // Registrar Venta
        Venta nuevaVenta = new Venta(System.currentTimeMillis(), huesped, new ArrayList<>(carritoActual), metodoPago, total);
        if (metodoPago.equals("EFECTIVO")) nuevaVenta.setVuelto(vuelto);

        ventas.add(nuevaVenta);
        vaciarCarrito();
        guardarDatosPersistentes();

        return nuevaVenta;
    }

    public List<Venta> obtenerHistorialVentas() { return ventas; }
}