package controlador;

import modelo.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ControladorHotel {
    private GestorDatos gestor;
    private List<Habitacion> habitaciones;
    private List<Huesped> huespedes;
    private List<Reserva> reservas;

    public ControladorHotel() {
        this.gestor = GestorDatos.obtenerInstancia();
        this.habitaciones = gestor.obtenerHabitaciones();
        this.huespedes = gestor.obtenerHuespedes();
        this.reservas = gestor.obtenerReservas();
    }

    public List<Habitacion> buscarHabitacionesDisponibles(String tipo) {
        return habitaciones.stream()
                .filter(h -> h.isDisponible() && (tipo.equals("TODAS") || h.getTipo().equals(tipo)))
                .collect(Collectors.toList());
    }

    public Habitacion buscarHabitacion(String numero) {
        return habitaciones.stream()
                .filter(h -> h.getNumero().equals(numero))
                .findFirst()
                .orElse(null);
    }

    public Huesped buscarCliente(String rut) {
        return huespedes.stream()
                .filter(h -> h.getRut().equals(rut))
                .findFirst()
                .orElse(null);
    }

    public Reserva realizarReserva(String rut, String nombre, String apellido, String telefono,
                                   String numeroHabitacion, int diasEstadia, String metodoPago) throws Exception {
        // Llamar al método con fecha de entrada actual (inmediata)
        return realizarReserva(rut, nombre, apellido, telefono, numeroHabitacion,
                              new Date(), diasEstadia, metodoPago);
    }

    public Reserva realizarReserva(String rut, String nombre, String apellido, String telefono,
                                   String numeroHabitacion, Date fechaEntrada, int diasEstadia,
                                   String metodoPago) throws Exception {
        Habitacion hab = buscarHabitacion(numeroHabitacion);

        if (hab == null) {
            throw new Exception("La habitación no existe.");
        }

        if (diasEstadia <= 0) {
            throw new Exception("El periodo de estadía debe ser al menos 1 día.");
        }

        // Validar que la fecha de entrada no sea en el pasado (comparando solo la fecha, no la hora)
        if (!esHoyOFutura(fechaEntrada)) {
            throw new Exception("La fecha de entrada no puede ser en el pasado.");
        }

        // Validar disponibilidad para el rango de fechas
        if (!verificarDisponibilidadFechas(numeroHabitacion, fechaEntrada, diasEstadia)) {
            throw new Exception("La habitación no está disponible para las fechas seleccionadas.");
        }

        Huesped huesped = buscarCliente(rut);
        if (huesped == null) {
            huesped = new Huesped(rut, nombre, apellido, telefono, 0);
            gestor.agregarHuesped(huesped);
        } else {
            // Actualizar teléfono si fue proporcionado
            if (telefono != null && !telefono.trim().isEmpty()) {
                huesped.setTelefono(telefono);
            }
        }

        // Calcular fecha de desocupación
        long milisegundos = fechaEntrada.getTime() + ((long) diasEstadia * 24 * 60 * 60 * 1000);
        Date fechaSalida = new Date(milisegundos);

        String idReserva = "RES" + System.currentTimeMillis();
        double totalPagar = hab.getPrecioNoche() * diasEstadia;
        Reserva nuevaReserva = new Reserva(idReserva, huesped, hab,
                fechaEntrada, diasEstadia, totalPagar, metodoPago);

        // Si la reserva es para hoy, ocupar la habitación inmediatamente
        if (esHoy(fechaEntrada)) {
            huesped.setNumeroHabitacion(numeroHabitacion);
            hab.setDisponible(false);
            hab.setFechaDesocupacion(fechaSalida);
        } else {
            // Reserva futura - marcar como PENDIENTE
            nuevaReserva.setEstado("PENDIENTE");
        }

        gestor.agregarReserva(nuevaReserva);
        return nuevaReserva;
    }

    private boolean esHoy(Date fecha) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal2.setTime(fecha);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean esHoyOFutura(Date fecha) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal2.setTime(fecha);

        // Resetear horas para comparar solo fechas
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);

        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        return !cal2.before(cal1); // Retorna true si es hoy o futura
    }

    private boolean verificarDisponibilidadFechas(String numeroHabitacion, Date fechaEntrada, int diasEstadia) {
        long milisegundos = fechaEntrada.getTime() + ((long) diasEstadia * 24 * 60 * 60 * 1000);
        Date fechaSalida = new Date(milisegundos);

        // Verificar si hay reservas que se solapen con el rango de fechas
        for (Reserva r : reservas) {
            if (r.getHabitacion().getNumero().equals(numeroHabitacion) &&
                (r.getEstado().equals("ACTIVA") || r.getEstado().equals("PENDIENTE"))) {

                Date entradaExistente = r.getFechaEntrada();
                Date salidaExistente = r.getFechaSalida();

                // Verificar solapamiento de fechas
                if (!(fechaSalida.before(entradaExistente) || fechaEntrada.after(salidaExistente))) {
                    return false; // Hay solapamiento
                }
            }
        }

        return true; // No hay conflictos
    }

    public void liberarHabitacion(String numHabitacion) {
        Habitacion h = buscarHabitacion(numHabitacion);
        if (h != null) {
            h.setDisponible(true);
            h.setFechaDesocupacion(null);

            // Actualizar huésped que tenía la habitación
            for (Huesped huesped : huespedes) {
                if (numHabitacion.equals(huesped.getNumeroHabitacion())) {
                    huesped.setNumeroHabitacion(null);
                    break;
                }
            }

            // Marcar reserva como finalizada
            for (Reserva r : reservas) {
                if (r.getHabitacion().getNumero().equals(numHabitacion) &&
                    r.getEstado().equals("ACTIVA")) {
                    r.setEstado("FINALIZADA");
                    break;
                }
            }

            gestor.guardarDatos();
        }
    }

    public Reserva buscarReservaActiva(String numHabitacion) {
        return reservas.stream()
                .filter(r -> r.getHabitacion().getNumero().equals(numHabitacion) &&
                            r.getEstado().equals("ACTIVA"))
                .findFirst()
                .orElse(null);
    }

    public double extenderEstadia(String numHabitacion, int diasAdicionales) throws Exception {
        Reserva reserva = buscarReservaActiva(numHabitacion);
        if (reserva == null) {
            throw new Exception("No hay una reserva activa para esta habitación.");
        }

        if (diasAdicionales <= 0) {
            throw new Exception("Los días adicionales deben ser mayor a 0.");
        }

        // Calcular la nueva fecha de salida
        int nuevosDias = reserva.getDiasEstadia() + diasAdicionales;
        long milisegundos = reserva.getFechaEntrada().getTime() + ((long) nuevosDias * 24 * 60 * 60 * 1000);
        Date nuevaFechaSalida = new Date(milisegundos);

        // Verificar que la extensión no se solape con otras reservas
        for (Reserva r : reservas) {
            // Ignorar la reserva actual y solo verificar otras reservas de la misma habitación
            if (!r.getIdReserva().equals(reserva.getIdReserva()) &&
                r.getHabitacion().getNumero().equals(numHabitacion) &&
                (r.getEstado().equals("ACTIVA") || r.getEstado().equals("PENDIENTE"))) {

                Date entradaOtraReserva = r.getFechaEntrada();
                Date salidaOtraReserva = r.getFechaSalida();

                // Verificar si la nueva fecha de salida se solapa con otra reserva
                // Solapamiento ocurre si: nueva_salida > entrada_otra_reserva
                if (nuevaFechaSalida.after(entradaOtraReserva)) {
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    throw new Exception(
                        "No se puede extender la estadía.\n\n" +
                        "La extensión causaría un conflicto con otra reserva:\n" +
                        "Cliente: " + r.getHuesped().getNombre() + " " + r.getHuesped().getApellido() + "\n" +
                        "Entrada: " + formato.format(entradaOtraReserva) + "\n" +
                        "Estado: " + r.getEstado() + "\n\n" +
                        "Días máximos que puede extender sin conflicto: " +
                        calcularDiasMaximosExtension(reserva, entradaOtraReserva)
                    );
                }
            }
        }

        // Si no hay conflictos, proceder con la extensión
        reserva.setDiasEstadia(nuevosDias);

        double totalAdicional = reserva.getHabitacion().getPrecioNoche() * diasAdicionales;
        reserva.setTotalPagado(reserva.getTotalPagado() + totalAdicional);

        // Actualizar fecha de desocupación de la habitación
        reserva.getHabitacion().setFechaDesocupacion(reserva.getFechaSalida());

        gestor.guardarDatos();

        return totalAdicional; // Retornar el monto adicional a pagar
    }

    private int calcularDiasMaximosExtension(Reserva reservaActual, Date fechaEntradaSiguiente) {
        long diferenciaMillis = fechaEntradaSiguiente.getTime() - reservaActual.getFechaEntrada().getTime();
        long diasTotalesDisponibles = diferenciaMillis / (24 * 60 * 60 * 1000);
        int diasMaximos = (int) diasTotalesDisponibles - reservaActual.getDiasEstadia();
        return Math.max(0, diasMaximos);
    }

    public void cancelarReserva(String numHabitacion) throws Exception {
        // Buscar reserva activa o pendiente
        Reserva reserva = reservas.stream()
                .filter(r -> r.getHabitacion().getNumero().equals(numHabitacion) &&
                            (r.getEstado().equals("ACTIVA") || r.getEstado().equals("PENDIENTE")))
                .findFirst()
                .orElse(null);

        if (reserva == null) {
            throw new Exception("No hay una reserva activa o pendiente para esta habitación.");
        }

        reserva.setEstado("CANCELADA");

        // Solo liberar habitación si está actualmente ocupada (ACTIVA)
        if (reserva.getEstado().equals("ACTIVA") || !reserva.getHabitacion().isDisponible()) {
            liberarHabitacion(numHabitacion);
        } else {
            // Si era PENDIENTE, solo guardar los cambios
            gestor.guardarDatos();
        }
    }

    public void cancelarReservaPorId(String idReserva) throws Exception {
        // Buscar reserva por ID
        Reserva reserva = reservas.stream()
                .filter(r -> r.getIdReserva().equals(idReserva) &&
                            (r.getEstado().equals("ACTIVA") || r.getEstado().equals("PENDIENTE")))
                .findFirst()
                .orElse(null);

        if (reserva == null) {
            throw new Exception("No se encontró la reserva con ID: " + idReserva);
        }

        String numeroHabitacion = reserva.getHabitacion().getNumero();
        boolean eraActiva = reserva.getEstado().equals("ACTIVA");

        reserva.setEstado("CANCELADA");

        // Solo liberar habitación si la reserva estaba ACTIVA
        if (eraActiva) {
            liberarHabitacion(numeroHabitacion);
        } else {
            // Si era PENDIENTE, solo guardar los cambios
            gestor.guardarDatos();
        }
    }

    public void activarReservasPendientes() {
        Date hoy = new Date();

        for (Reserva r : reservas) {
            if (r.getEstado().equals("PENDIENTE") && esHoy(r.getFechaEntrada())) {
                // Activar la reserva
                r.setEstado("ACTIVA");

                // Ocupar la habitación
                Habitacion hab = r.getHabitacion();
                hab.setDisponible(false);
                hab.setFechaDesocupacion(r.getFechaSalida());

                // Asignar habitación al huésped
                r.getHuesped().setNumeroHabitacion(hab.getNumero());

                gestor.guardarDatos();
            }
        }
    }

    public String obtenerRangoOcupacion(String numeroHabitacion) {
        // Buscar todas las reservas activas o pendientes para esta habitación
        List<Reserva> reservasHabitacion = reservas.stream()
                .filter(r -> r.getHabitacion().getNumero().equals(numeroHabitacion) &&
                            (r.getEstado().equals("ACTIVA") || r.getEstado().equals("PENDIENTE")))
                .sorted((r1, r2) -> r1.getFechaEntrada().compareTo(r2.getFechaEntrada()))
                .collect(Collectors.toList());

        if (reservasHabitacion.isEmpty()) {
            return ""; // No hay reservas
        }

        // Crear una lista de todos los rangos
        StringBuilder rangos = new StringBuilder();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < reservasHabitacion.size(); i++) {
            Reserva r = reservasHabitacion.get(i);
            if (i > 0) {
                rangos.append(", ");
            }
            rangos.append(formato.format(r.getFechaEntrada()))
                  .append(" - ")
                  .append(formato.format(r.getFechaSalida()));
        }

        return rangos.toString();
    }

    public List<Reserva> obtenerReservas() { return reservas; }
    public List<Huesped> obtenerHuespedes() { return huespedes; }
    public List<Habitacion> obtenerHabitaciones() { return habitaciones; }
}