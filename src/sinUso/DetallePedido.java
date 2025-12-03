package sinUso;

import java.io.Serializable;

public class DetallePedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idDetalle;
    private String cantidad;
    private Double subtotal;
    private Double impuesto;
    private String nota;

    public DetallePedido(String idDetalle, String cantidad, Double subtotal, Double impuesto, String nota) {
        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.nota = nota;
    }

    public String getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(String idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Double impuesto) {
        this.impuesto = impuesto;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public void calcular(Double subtotal) {}
    public void agregar(DetallePedido detallePedido) {}
    public void calcular(String detalle) {}
    public void eliminar(String detalle) {}
    public void validar(String pedido) {}
}