package modelo;

import java.io.Serializable;

public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idProducto;
    private String nombre;
    private int precio;
    private int stock;
    private String categoria;

    public Producto(String idProducto, String nombre, int precio, int stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int calcularPrecio(int cantidad) {
        return this.precio * cantidad;
    }
    public void actualizarStock(){}

    @Override
    public String toString() {
        return nombre + " ($" + precio + ") - Stock: " + stock;
    }
}