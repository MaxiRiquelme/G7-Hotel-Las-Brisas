package modelo;

import java.io.Serializable;
//esta clase está en el diagrama de clases me parece que no se implementará para este proyecto.

public class Vendedor extends Empleado implements Serializable {
    private static final long serialVersionUID = 1L;

    private String cedVenta;
    private Integer numVentas;
    private String especificación;
    private Boolean disponibilidad;
    private Integer comisiones;

    public Vendedor(String idEmpleado, String nombre, String apellido, String puesto, String turno, String cedVenta, Integer numVentas, String especificación, Boolean disponibilidad, Integer comisiones) {
        super(idEmpleado, nombre, apellido, puesto, turno);
        this.cedVenta = cedVenta;
        this.numVentas = numVentas;
        this.especificación = especificación;
        this.disponibilidad = disponibilidad;
        this.comisiones = comisiones;
    }

    public String getCedVenta() {
        return cedVenta;
    }

    public void setCedVenta(String cedVenta) {
        this.cedVenta = cedVenta;
    }

    public Integer getComisiones() {
        return comisiones;
    }

    public void setComisiones(Integer comisiones) {
        this.comisiones = comisiones;
    }

    public Boolean getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getEspecificación() {
        return especificación;
    }

    public void setEspecificación(String especificación) {
        this.especificación = especificación;
    }

    public Integer getNumVentas() {
        return numVentas;
    }

    public void setNumVentas(Integer numVentas) {
        this.numVentas = numVentas;
    }

    public void registrarVenta(String pedido) {}
    public void registrarOrdenOferta(String producto) {}
    public void recibir(Integer pedido, Empleado empleado) {}
    public void recibir(Integer pedido, Producto producto) {}
    public void registrarInventarioOrg(Producto producto) {}
    public void actualizarStock(Producto producto, Integer cantidad) {}
}