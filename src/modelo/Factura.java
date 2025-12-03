package modelo;

import java.io.Serializable;

public class Factura implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rutEmpresa;
    private String giro;
    private String direccionFiscal;
    private String condiciónVenta;
    private Integer numFolio;

    public Factura(String condiciónVenta, String direccionFiscal, String giro, Integer numFolio, String rutEmpresa) {
        this.condiciónVenta = condiciónVenta;
        this.direccionFiscal = direccionFiscal;
        this.giro = giro;
        this.numFolio = numFolio;
        this.rutEmpresa = rutEmpresa;
    }

    public String getCondiciónVenta() {
        return condiciónVenta;
    }

    public void setCondiciónVenta(String condiciónVenta) {
        this.condiciónVenta = condiciónVenta;
    }

    public String getDireccionFiscal() {
        return direccionFiscal;
    }

    public void setDireccionFiscal(String direccionFiscal) {
        this.direccionFiscal = direccionFiscal;
    }

    public String getGiro() {
        return giro;
    }

    public void setGiro(String giro) {
        this.giro = giro;
    }

    public Integer getNumFolio() {
        return numFolio;
    }

    public void setNumFolio(Integer numFolio) {
        this.numFolio = numFolio;
    }

    public String getRutEmpresa() {
        return rutEmpresa;
    }

    public void setRutEmpresa(String rutEmpresa) {
        this.rutEmpresa = rutEmpresa;
    }

    public void calcularImpuesto(Double monto) {}
    public void registrarFactura(String factura) {}
    public void validarDatosFIscales(String rut) {}
    public void obtenerDetalleEmpresa(String detalles) {}
    public void imprimir() {}
}