package modelo;

import java.io.Serializable;

public class Huesped implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idHuesped;
    private String nombre;
    private String apellido;
    private String telefono;
    private Integer numeroDocumento;

    public Huesped(String idHuesped, String nombre, String apellido, String telefono, Integer numeroDocumento) {
        this.idHuesped = idHuesped;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.numeroDocumento = numeroDocumento;
    }


    public String getIdHuesped() {
        return idHuesped;
    }

    public void setIdHuesped(String idHuesped) {
        this.idHuesped = idHuesped;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(Integer numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
}