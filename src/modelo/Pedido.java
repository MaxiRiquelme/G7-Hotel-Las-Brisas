package modelo;

import java.io.Serializable;
import java.util.Date;

public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idPedido;
    private java.util.Date fechaDate;
    private String total;
    private String estado;
    private String origen;

    public Pedido(String idPedido, Date fechaDate, String total, String estado, String origen) {
        this.idPedido = idPedido;
        this.fechaDate = fechaDate;
        this.total = total;
        this.estado = estado;
        this.origen = origen;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public Date getFechaDate() {
        return fechaDate;
    }

    public void setFechaDate(Date fechaDate) {
        this.fechaDate = fechaDate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void registrar(String registro) {}
    public void agregar(DetallePedido detallePedido) {}
    public void calcular(String calculo) {}
    public void eliminar(DetallePedido detallePedido) {}
    public void validarPedido(String idHuesped) {}
}