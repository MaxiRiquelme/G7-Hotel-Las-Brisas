package modelo;

import java.io.Serializable;

public class Huesped implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rut;
    private String nombre;
    private String numeroHabitacion; // Nuevo campo para Hotel

    public Huesped(String rut, String nombre, String numeroHabitacion) {
        this.rut = rut;
        this.nombre = nombre;
        this.numeroHabitacion = numeroHabitacion;
    }

    public String getRut() { return rut; }
    public String getNombre() { return nombre; }
    public String getNumeroHabitacion() { return numeroHabitacion; }

    public void setNumeroHabitacion(String numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public boolean esHuesped() {
        return numeroHabitacion != null && !numeroHabitacion.isEmpty();
    }
}