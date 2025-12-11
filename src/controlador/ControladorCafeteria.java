package controlador;

import modelo.*;
import java.util.*;
import java.util.stream.Collectors;

public class ControladorCafeteria {
    private GestorDatos gestor;
    private List<Producto> productos;
    private List<Huesped> huespedes;
    private List<DetalleVenta> carritoActual;

    public ControladorCafeteria() {
        this.gestor = GestorDatos.obtenerInstancia();
        this.productos = gestor.obtenerProductos();
        this.huespedes = gestor.obtenerHuespedes();
        this.carritoActual = new ArrayList<>();
    }

    public List<Producto> obtenerProductos() { return productos; }

    public List<Producto> buscarProductos(String consulta) {
        if (consulta == null || consulta.trim().isEmpty()) return productos;
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
        if (buscarProductoPorId(id) != null) throw new Exception("El producto ya existe.");
        if (precio <= 0 || stock < 0) throw new Exception("Precio debe ser mayor a 0 y stock no puede ser negativo.");
        productos.add(new Producto(id, nombre, precio, stock));
        gestor.guardarDatos();
    }

    public void aumentarStock(String id, int cantidad) throws Exception {
        Producto p = buscarProductoPorId(id);
        if (p == null) throw new Exception("Producto no encontrado.");
        if (cantidad <= 0) throw new Exception("La cantidad debe ser mayor a 0.");
        p.agregarStock(cantidad);
        gestor.guardarDatos();
    }

    public void disminuirStock(String id, int cantidad) throws Exception {
        Producto p = buscarProductoPorId(id);
        if (p == null) throw new Exception("Producto no encontrado.");
        if (cantidad <= 0) throw new Exception("La cantidad debe ser mayor a 0.");
        if (p.getStock() < cantidad) throw new Exception("Stock insuficiente. Disponible: " + p.getStock());
        p.disminuirStock(cantidad);
        gestor.guardarDatos();
    }

    public void eliminarProducto(String id) throws Exception {
        Producto p = buscarProductoPorId(id);
        if (p == null) throw new Exception("Producto no encontrado.");
        productos.remove(p);
        gestor.guardarDatos();
    }

    public void agregarAlCarrito(Producto p, int cantidad) throws Exception {
        if (p.getStock() < cantidad) throw new Exception("Stock insuficiente. Disponible: " + p.getStock());
        carritoActual.add(new DetalleVenta(p, cantidad));
    }

    public void vaciarCarrito() { carritoActual.clear(); }
    public List<DetalleVenta> getCarrito() { return carritoActual; }
    public int calcularTotalCarrito() {
        return carritoActual.stream().mapToInt(DetalleVenta::getSubtotal).sum();
    }

    public Huesped buscarCliente(String rut) {
        return huespedes.stream()
                .filter(h -> h.getRut().equals(rut))
                .findFirst()
                .orElse(null);
    }

    public Venta finalizarVenta(String rut, String nombreCliente, String habitacion, String metodoPago,
                                int montoEfectivo, String numTarjeta) throws Exception {
        if (carritoActual.isEmpty()) throw new Exception("El carrito está vacío.");

        Huesped huesped = buscarCliente(rut);
        if (huesped == null) {
            huesped = new Huesped(rut, nombreCliente, nombreCliente);
            if (!habitacion.isEmpty()) huesped.setNumeroHabitacion(habitacion);
            gestor.agregarHuesped(huesped);
        }

        int total = calcularTotalCarrito();
        int vuelto = 0;

        if (metodoPago.equals("EFECTIVO")) {
            if (montoEfectivo < total) throw new Exception("Dinero insuficiente. Total: $" + total);
            vuelto = montoEfectivo - total;
        } else if (metodoPago.equals("TARJETA")) {
            if (numTarjeta == null || numTarjeta.trim().isEmpty()) throw new Exception("Número de tarjeta requerido.");
        } else if (metodoPago.equals("CARGO_HABITACION")) {
            if (!huesped.esHuesped()) throw new Exception("Cliente no es huésped del hotel.");
        }

        for (DetalleVenta d : carritoActual) d.getProducto().disminuirStock(d.getCantidad());

        Venta nuevaVenta = new Venta(System.currentTimeMillis(), huesped,
                new ArrayList<>(carritoActual), metodoPago, total);
        if (metodoPago.equals("EFECTIVO")) nuevaVenta.setVuelto(vuelto);

        gestor.agregarVenta(nuevaVenta);
        vaciarCarrito();

        return nuevaVenta;
    }

    public void agregarListener(ActualizacionListener listener) {
        gestor.agregarListener(listener);
    }
}