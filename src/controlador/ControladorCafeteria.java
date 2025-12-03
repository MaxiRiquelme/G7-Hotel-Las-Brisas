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
    private static final String ARCHIVO_DATOS = "datos_cafeteria.bin";

    private List<Producto> productos;
    private List<Huesped> huespedes;
    private List<Venta> ventas;
    private List<DetalleVenta> carritoActual;

    public ControladorCafeteria() {
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
                carritoActual = new ArrayList<>();
            } catch (Exception e) {
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
        carritoActual = new ArrayList<>();

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

        guardarDatosPersistentes();
    }

    // --- Métodos de Gestión de Productos ---
    public List<Producto> obtenerProductos() {
        return productos;
    }

    public List<Producto> buscarProductos(String consulta) {
        if (consulta == null || consulta.trim().isEmpty()) {
            return productos;
        }
        return productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(consulta.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Producto buscarProductoPorId(String id) {
        return productos.stream()
                .filter(p -> p.getIdProducto().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void agregarProductoNuevo(String id, String nombre, int precio, int stock) throws Exception {
        if (buscarProductoPorId(id) != null) {
            throw new Exception("El producto ya existe.");
        }
        if (precio <= 0 || stock < 0) {
            throw new Exception("Precio debe ser mayor a 0 y stock no puede ser negativo.");
        }
        productos.add(new Producto(id, nombre, precio, stock));
        guardarDatosPersistentes();
    }

    public void aumentarStock(String id, int cantidad) throws Exception {
        Producto p = buscarProductoPorId(id);
        if (p == null) {
            throw new Exception("Producto no encontrado.");
        }
        if (cantidad <= 0) {
            throw new Exception("La cantidad debe ser mayor a 0.");
        }
        p.agregarStock(cantidad);
        guardarDatosPersistentes();
    }

    public void disminuirStock(String id, int cantidad) throws Exception {
        Producto p = buscarProductoPorId(id);
        if (p == null) {
            throw new Exception("Producto no encontrado.");
        }
        if (cantidad <= 0) {
            throw new Exception("La cantidad debe ser mayor a 0.");
        }
        if (p.getStock() < cantidad) {
            throw new Exception("Stock insuficiente. Disponible: " + p.getStock());
        }
        p.disminuirStock(cantidad);
        guardarDatosPersistentes();
    }

    // --- Carrito ---
    public void agregarAlCarrito(Producto p, int cantidad) throws Exception {
        if (p.getStock() < cantidad) {
            throw new Exception("Stock insuficiente. Disponible: " + p.getStock());
        }

        DetalleVenta existente = carritoActual.stream()
                .filter(d -> d.getProducto().getIdProducto().equals(p.getIdProducto()))
                .findFirst()
                .orElse(null);

        if (existente != null) {
            if (p.getStock() < (existente.getCantidad() + cantidad)) {
                throw new Exception("Stock insuficiente para la cantidad solicitada.");
            }
        }

        carritoActual.add(new DetalleVenta(p, cantidad));
    }

    public void vaciarCarrito() {
        carritoActual.clear();
    }

    public List<DetalleVenta> getCarrito() {
        return carritoActual;
    }

    public int calcularTotalCarrito() {
        return carritoActual.stream()
                .mapToInt(DetalleVenta::getSubtotal)
                .sum();
    }

    // --- Clientes y Ventas ---
    public Huesped buscarCliente(String rut) {
        return huespedes.stream()
                .filter(h -> h.getRut().equals(rut))
                .findFirst()
                .orElse(null);
    }

    public Venta finalizarVenta(String rut, String nombreCliente, String habitacion, String metodoPago,
                                int montoEfectivo, String numTarjeta) throws Exception {

        if (carritoActual.isEmpty()) {
            throw new Exception("El carrito está vacío.");
        }

        Huesped huesped = buscarCliente(rut);
        if (huesped == null) {
            huesped = new Huesped(rut, nombreCliente, nombreCliente);
            if (!habitacion.isEmpty()) {
                huesped.setNumeroHabitacion(habitacion);
            }
            huespedes.add(huesped);
        }

        int total = calcularTotalCarrito();
        int vuelto = 0;

        if (metodoPago.equals("EFECTIVO")) {
            if (montoEfectivo < total) {
                throw new Exception("Dinero insuficiente. Total: $" + total);
            }
            vuelto = montoEfectivo - total;
        } else if (metodoPago.equals("TARJETA")) {
            if (numTarjeta == null || numTarjeta.trim().isEmpty()) {
                throw new Exception("Número de tarjeta requerido.");
            }
        } else if (metodoPago.equals("CARGO_HABITACION")) {
            if (!huesped.esHuesped()) {
                throw new Exception("Cliente no es huésped del hotel.");
            }
        }

        for (DetalleVenta d : carritoActual) {
            d.getProducto().disminuirStock(d.getCantidad());
        }

        Venta nuevaVenta = new Venta(System.currentTimeMillis(), huesped,
                new ArrayList<>(carritoActual), metodoPago, total);
        if (metodoPago.equals("EFECTIVO")) {
            nuevaVenta.setVuelto(vuelto);
        }

        ventas.add(nuevaVenta);
        vaciarCarrito();
        guardarDatosPersistentes();

        return nuevaVenta;
    }

    public List<Venta> obtenerVentas() {
        return ventas;
    }
}