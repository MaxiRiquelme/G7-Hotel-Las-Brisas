package modelo;

import java.io.Serializable;
import java.util.Date;

public class Pago implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idPago;
    private Double monto;
    private Date fechaPago;
    private String metodoPago;
    private String estadoTransaccion;

    public Pago(String idPago, Double monto, Date fechaPago, String metodoPago, String estadoTransaccion) {
        this.idPago = idPago;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.estadoTransaccion = estadoTransaccion;
    }

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public String getEstadoTransaccion() {
        return estadoTransaccion;
    }

    public void setEstadoTransaccion(String estadoTransaccion) {
        this.estadoTransaccion = estadoTransaccion;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public void procesarPagoInfos(Double monto, String metodo) {}
    public void validarMetodo(String metodo) {}
    public void registrarTransaccion(String transactado) {}
    public void obtenerDetalles(String detalles) {}
}