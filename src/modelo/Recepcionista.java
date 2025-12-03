package modelo;

import java.io.Serializable;

public class Recepcionista extends Empleado implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idTelefonico;
    private Integer codigoAgenda;
    private String pedidoCurso;
    private String idioma;
    private String nombreLlamadas;

    public Recepcionista(String idEmpleado, String nombre, String apellido, String puesto, String turno,
                         String idTelefonico, String nombreLlamadas, String idioma,
                         String pedidoCurso, Integer codigoAgenda) {
        super(idEmpleado, nombre, apellido, puesto, turno);
        this.idTelefonico = idTelefonico;
        this.nombreLlamadas = nombreLlamadas;
        this.idioma = idioma;
        this.pedidoCurso = pedidoCurso;
        this.codigoAgenda = codigoAgenda;
    }

    public String getIdTelefonico() { return idTelefonico; }
    public void setIdTelefonico(String idTelefonico) { this.idTelefonico = idTelefonico; }

    public String getNombreLlamadas() { return nombreLlamadas; }
    public void setNombreLlamadas(String nombreLlamadas) { this.nombreLlamadas = nombreLlamadas; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getPedidoCurso() { return pedidoCurso; }
    public void setPedidoCurso(String pedidoCurso) { this.pedidoCurso = pedidoCurso; }

    public Integer getCodigoAgenda() { return codigoAgenda; }
    public void setCodigoAgenda(Integer codigoAgenda) { this.codigoAgenda = codigoAgenda; }

    public void registrarReserva(Reserva reserva, Habitacion habitacion) {
        if (reserva != null && habitacion != null) {
            habitacion.setDisponible(false);
            reserva.setHabitacion(habitacion);
        }
    }

    public void verificarDisponibilidad(Habitacion habitacion) {
        if (habitacion != null) {
            habitacion.verificarEstado();
        }
    }

    public void verificarEstado(String estado) {}
    public void solicitarPreparacion(Integer pedido, String cocina) {}
}