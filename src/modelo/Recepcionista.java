package modelo;

import java.io.Serializable;

public class Recepcionista extends Empleado implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idTelefonico;
    private Integer codigoAgenda;
    private String pedidoCurso;
    private String idioma;
    private String nombreLlamadas;

    public Recepcionista(String idEmpleado, String nombre, String apellido, String puesto, String turno, String idTelefonico, String nombreLlamadas, String idioma, String pedidoCurso, Integer codigoAgenda) {
        super(idEmpleado, nombre, apellido, puesto, turno);
        this.idTelefonico = idTelefonico;
        this.nombreLlamadas = nombreLlamadas;
        this.idioma = idioma;
        this.pedidoCurso = pedidoCurso;
        this.codigoAgenda = codigoAgenda;
    }

    public String getIdTelefonico() {
        return idTelefonico;
    }

    public void setIdTelefonico(String idTelefonico) {
        this.idTelefonico = idTelefonico;
    }

    public String getNombreLlamadas() {
        return nombreLlamadas;
    }

    public void setNombreLlamadas(String nombreLlamadas) {
        this.nombreLlamadas = nombreLlamadas;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getPedidoCurso() {
        return pedidoCurso;
    }

    public void setPedidoCurso(String pedidoCurso) {
        this.pedidoCurso = pedidoCurso;
    }

    public Integer getCodigoAgenda() {
        return codigoAgenda;
    }

    public void setCodigoAgenda(Integer codigoAgenda) {
        this.codigoAgenda = codigoAgenda;
    }

    //aún no me queda claro si estos metodos enganchan huesped con reserva o hacerlo en otra clase
    public void registrarReserva(Reserva reserva, Integer habitacion) {}
    public void verificarDisponibilidad(Habitacion habitacion) {}
    public void verificarEstado(String estado) {}
    public void solicitarPreparacion(Integer pedido, String cocina) {}
    //public void asignarBotones(Botones botones,Huesped huesped) {}
}