package modelo;

import java.io.Serializable;
//esta clase está en el diagrama de clases me parece que no se implementará para este proyecto.
public class Cocinero extends Empleado implements Serializable {
    private static final long serialVersionUID = 1L;

    private String cedVenta;
    private Integer numRaciones;
    private String certificacion;
    private Boolean disponibilidad;
    private Integer menus;

    public Cocinero(String idEmpleado, String nombre, String apellido, String puesto, String turno, String cedVenta, Integer menus, Boolean disponibilidad, String certificación, Integer numRaciones) {
        super(idEmpleado, nombre, apellido, puesto, turno);
        this.cedVenta = cedVenta;
        this.menus = menus;
        this.disponibilidad = disponibilidad;
        this.certificacion = certificacion;
        this.numRaciones = numRaciones;
    }

    public String getCedVenta() {
        return cedVenta;
    }

    public void setCedVenta(String cedVenta) {
        this.cedVenta = cedVenta;
    }

    public Integer getNumRaciones() {
        return numRaciones;
    }

    public void setNumRaciones(Integer numRaciones) {
        this.numRaciones = numRaciones;
    }

    /*public String getCertificación() {
        return certificación;
    }

    public void setCertificación(String certificación) {
        this.certificación = certificación;
    }
*/
    public Boolean getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public Integer getMenus() {
        return menus;
    }

    public void setMenus(Integer menus) {
        this.menus = menus;
    }

    public void registrarVenta(String pedido) {}
    public void registrarOrdenOferta(String producto) {}
    public void recibir(Integer pedido, Empleado empleado) {}
    public void recibir(Integer pedido, Producto producto) {}
    public void registrarInventarioOrg(Producto producto) {}
    public void actualizarStock(Producto producto, Integer cantidad) {}
}