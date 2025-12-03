package modelo;

import java.io.Serializable;

public class Huesped implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rut;
    private String nombre;
    private String apellido;
    private String telefono;
    private Integer numeroDocumento;
    private String numeroHabitacion;

    public Huesped(String rut, String nombre, String apellido) {
        this.rut = rut;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numeroHabitacion = null;
    }

    public Huesped(String rut, String nombre, String apellido, String telefono, Integer numeroDocumento) {
        this.rut = rut;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.numeroDocumento = numeroDocumento;
        this.numeroHabitacion = null;
    }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Integer getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(Integer numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNumeroHabitacion() { return numeroHabitacion; }
    public void setNumeroHabitacion(String numeroHabitacion) { this.numeroHabitacion = numeroHabitacion; }

    public boolean esHuesped() {
        return numeroHabitacion != null && !numeroHabitacion.isEmpty();
    }
}