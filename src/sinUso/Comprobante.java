package sinUso;

import java.io.Serializable;
import java.util.Date;

public class Comprobante implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idComprobante;
    private Date fechaEmision;
    private String estado;
    private double montoNeto;
    private String tipoDocumento;

    public Comprobante(String estado, Date fechaEmision, Integer idComprobante, double montoNeto, String tipoDocumento) {
        this.estado = estado;
        this.fechaEmision = fechaEmision;
        this.idComprobante = idComprobante;
        this.montoNeto = montoNeto;
        this.tipoDocumento = tipoDocumento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Integer getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(Integer idComprobante) {
        this.idComprobante = idComprobante;
    }

    public double getMontoNeto() {
        return montoNeto;
    }

    public void setMontoNeto(double montoNeto) {
        this.montoNeto = montoNeto;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void generarDocumento() {}
    public void imprimirComprobante(String comprobante) {}
    public void vincularPago(Pago pago) {}
    public void enviarPorEmail(String correo) {}
    public void eliminar(String detalles) {}
}