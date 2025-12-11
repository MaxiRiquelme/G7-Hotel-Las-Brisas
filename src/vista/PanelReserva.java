package vista;

import controlador.ControladorHotel;
import modelo.Huesped;
import modelo.Habitacion;
import modelo.Reserva;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PanelReserva extends JPanel {
    private ControladorHotel controlador;
    private VistaPrincipal mainFrame;
    private JComboBox<String> cmbTipoHabitacion;
    private DefaultTableModel modeloTabla;
    private JTable tablaHabitaciones;
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public PanelReserva(ControladorHotel ctrl, VistaPrincipal frame) {
        this.controlador = ctrl;
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel Superior
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 0));

        JButton btnVolver = new JButton("< Volver al Menú");
        btnVolver.addActionListener(e -> mainFrame.mostrarVista("MENU"));
        panelSuperior.add(btnVolver, BorderLayout.WEST);

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.add(new JLabel("Filtrar por Tipo:"));

        cmbTipoHabitacion = new JComboBox<>(new String[]{"TODAS", "Single", "Matrimonial", "Suite"});
        panelFiltro.add(cmbTipoHabitacion);

        JButton btnBuscar = new JButton("Verificar Disponibilidad");
        btnBuscar.addActionListener(e -> buscarHabitaciones());
        panelFiltro.add(btnBuscar);

        JButton btnGestionarHab = new JButton("Gestionar Habitaciones");
        btnGestionarHab.addActionListener(e -> mostrarGestionHabitaciones());
        panelFiltro.add(btnGestionarHab);

        panelSuperior.add(panelFiltro, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel Central: Tabla
        modeloTabla = new DefaultTableModel(new String[]{"Número", "Tipo", "Precio/Noche", "Estado", "Rango de Ocupación"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHabitaciones = new JTable(modeloTabla);
        tablaHabitaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ajustar anchos de columnas
        tablaHabitaciones.getColumnModel().getColumn(0).setPreferredWidth(70);  // Número
        tablaHabitaciones.getColumnModel().getColumn(1).setPreferredWidth(100); // Tipo
        tablaHabitaciones.getColumnModel().getColumn(2).setPreferredWidth(100); // Precio
        tablaHabitaciones.getColumnModel().getColumn(3).setPreferredWidth(90);  // Estado
        tablaHabitaciones.getColumnModel().getColumn(4).setPreferredWidth(300); // Rango

        JScrollPane scroll = new JScrollPane(tablaHabitaciones);
        scroll.setBorder(BorderFactory.createTitledBorder("Habitaciones - Estado y Reservas"));
        add(scroll, BorderLayout.CENTER);

        // Panel Inferior: Botón Reservar
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReservar = new JButton("Realizar Reserva");
        btnReservar.setFont(new Font("Arial", Font.BOLD, 14));
        btnReservar.setBackground(new Color(100, 150, 200));
        btnReservar.addActionListener(e -> iniciarProcesoReserva());
        panelBoton.add(btnReservar);

        add(panelBoton, BorderLayout.SOUTH);

        buscarHabitaciones();
    }

    private void buscarHabitaciones() {
        modeloTabla.setRowCount(0);
        String tipo = (String) cmbTipoHabitacion.getSelectedItem();

        // Mostrar todas las habitaciones, filtradas por tipo
        List<Habitacion> todasHabitaciones = controlador.obtenerHabitaciones();

        for (Habitacion h : todasHabitaciones) {
            if (tipo.equals("TODAS") || h.getTipo().equals(tipo)) {
                // Obtener rango de ocupación de la habitación
                String rangoOcupacion = controlador.obtenerRangoOcupacion(h.getNumero());

                modeloTabla.addRow(new Object[]{
                        h.getNumero(),
                        h.getTipo(),
                        "$" + String.format("%.0f", h.getPrecioNoche()),
                        h.getEstado(),
                        rangoOcupacion.isEmpty() ? "-" : rangoOcupacion
                });
            }
        }
    }

    private void iniciarProcesoReserva() {
        int row = tablaHabitaciones.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una habitación.");
            return;
        }

        String numHabitacion = (String) modeloTabla.getValueAt(row, 0);
        String estado = (String) modeloTabla.getValueAt(row, 3);
        String rangoOcupacion = (String) modeloTabla.getValueAt(row, 4);

        // Si la habitación está ocupada, informar y sugerir reserva futura
        boolean habitacionOcupada = estado.equals("OCUPADA");

        if (habitacionOcupada) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "La habitación está actualmente OCUPADA.\n\n" +
                    "Períodos ocupados:\n" + rangoOcupacion + "\n\n" +
                    "¿Desea hacer una reserva para una fecha futura disponible?",
                    "Habitación Ocupada - Reserva Futura",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }

            // Si acepta, mostrar mensaje informativo
            JOptionPane.showMessageDialog(this,
                    "Procederá a agendar una reserva futura.\n\n" +
                    "Asegúrese de seleccionar fechas que NO se solapen con:\n" +
                    rangoOcupacion,
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        String precioStr = (String) modeloTabla.getValueAt(row, 2);
        double precioNoche = Double.parseDouble(precioStr.replace("$", ""));

        // Paso 1: Datos del Huésped (RUT, Nombre, Apellido, Teléfono)
        JTextField txtRut = new JTextField();
        JTextField txtTelefono = new JTextField();
        Object[] mensajeDatos = {
            "RUT del Huésped:", txtRut,
            "Teléfono:", txtTelefono
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensajeDatos, "Datos del Huésped",
                JOptionPane.OK_CANCEL_OPTION);

        if (opcion != JOptionPane.OK_OPTION || txtRut.getText().trim().isEmpty()) return;

        String rut = txtRut.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El teléfono es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Huesped huespedExistente = controlador.buscarCliente(rut);
        String nombre, apellido;

        if (huespedExistente != null) {
            nombre = huespedExistente.getNombre();
            apellido = huespedExistente.getApellido();
            JOptionPane.showMessageDialog(this, "Cliente encontrado: " + nombre + " " + apellido);
        } else {
            JTextField txtNombre = new JTextField();
            JTextField txtApellido = new JTextField();
            Object[] datosNuevos = {"Nombre:", txtNombre, "Apellido:", txtApellido};
            int op = JOptionPane.showConfirmDialog(this, datosNuevos, "Nuevo Cliente",
                    JOptionPane.OK_CANCEL_OPTION);
            if (op != JOptionPane.OK_OPTION) return;

            nombre = txtNombre.getText().trim();
            apellido = txtApellido.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre y apellido son requeridos.");
                return;
            }
        }

        // Paso 2: Fecha de entrada (inmediata o agendada)
        Date fechaEntrada = new Date(); // Por defecto, hoy
        int tipoReserva = 0; // 0 = inmediata, 1 = agendada

        // Si la habitación está ocupada, SOLO permitir reserva futura
        if (habitacionOcupada) {
            fechaEntrada = seleccionarFechaEntrada();
            if (fechaEntrada == null) return; // Usuario canceló
            tipoReserva = 1; // Marcar como agendada
        } else {
            // Si está disponible, dar opción
            String[] opcionesFecha = {"Entrada Inmediata (Hoy)", "Agendar Fecha de Entrada"};
            tipoReserva = JOptionPane.showOptionDialog(this,
                    "¿Cuándo será la entrada?", "Tipo de Reserva",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opcionesFecha, opcionesFecha[0]);

            if (tipoReserva == -1) return;

            if (tipoReserva == 1) { // Agendar fecha futura
                fechaEntrada = seleccionarFechaEntrada();
                if (fechaEntrada == null) return; // Usuario canceló
            }
        }

        // Paso 3: Periodo de estadía (días)
        String diasStr = JOptionPane.showInputDialog(this,
                "¿Por cuántos días se extenderá la reserva?", "Periodo de Estadía",
                JOptionPane.QUESTION_MESSAGE);

        if (diasStr == null || diasStr.trim().isEmpty()) return;

        int diasEstadia;
        try {
            diasEstadia = Integer.parseInt(diasStr.trim());
            if (diasEstadia <= 0) {
                JOptionPane.showMessageDialog(this, "El periodo debe ser al menos 1 día.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un número válido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double totalPagar = precioNoche * diasEstadia;

        // Paso 4: Seleccionar método de pago
        String[] opcionesVisuales = {"Efectivo", "Tarjeta", "Transferencia"};
        String[] opcionesInternas = {"EFECTIVO", "TARJETA", "TRANSFERENCIA"};
        int seleccion = JOptionPane.showOptionDialog(this,
                "Total a Pagar: $" + String.format("%.0f", totalPagar) +
                "\n(" + diasEstadia + " día(s) x $" + String.format("%.0f", precioNoche) + ")" +
                "\n\nSeleccione el método de pago:", "Método de Pago",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcionesVisuales, opcionesVisuales[0]);

        if (seleccion == -1) return;
        String metodoPago = opcionesInternas[seleccion];

        // Calcular fecha de salida
        long milisegundos = fechaEntrada.getTime() + ((long) diasEstadia * 24 * 60 * 60 * 1000);
        Date fechaSalida = new Date(milisegundos);

        // Paso 5: Confirmar pago
        String tipoEntrada = (tipoReserva == 0) ? "Inmediata (Hoy)" : "Agendada";
        int confirmacionPago = JOptionPane.showConfirmDialog(this,
                "Resumen de Reserva:\n" +
                "Tipo: " + tipoEntrada + "\n" +
                "Cliente: " + nombre + " " + apellido + "\n" +
                "RUT: " + rut + "\n" +
                "Teléfono: " + telefono + "\n" +
                "Habitación: " + numHabitacion + "\n" +
                "Fecha Entrada: " + formatoFecha.format(fechaEntrada) + "\n" +
                "Fecha Salida: " + formatoFecha.format(fechaSalida) + "\n" +
                "Días de Estadía: " + diasEstadia + "\n" +
                "Total a Pagar: $" + String.format("%.0f", totalPagar) + "\n" +
                "Método: " + metodoPago + "\n\n¿Confirmar?",
                "Confirmación de Reserva", JOptionPane.YES_NO_OPTION);

        if (confirmacionPago == JOptionPane.YES_OPTION) {
            try {
                Reserva r = controlador.realizarReserva(rut, nombre, apellido, telefono,
                        numHabitacion, fechaEntrada, diasEstadia, metodoPago);
                mostrarVoucher(r);
                buscarHabitaciones();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Date seleccionarFechaEntrada() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        // Campos para día, mes y año
        JSpinner spinnerDia = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        JSpinner spinnerMes = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));

        Calendar hoy = Calendar.getInstance();
        int añoActual = hoy.get(Calendar.YEAR);
        JSpinner spinnerAño = new JSpinner(new SpinnerNumberModel(añoActual, añoActual, añoActual + 2, 1));

        JSpinner spinnerHora = new JSpinner(new SpinnerNumberModel(14, 0, 23, 1));

        panel.add(new JLabel("Día:"));
        panel.add(spinnerDia);
        panel.add(new JLabel("Mes:"));
        panel.add(spinnerMes);
        panel.add(new JLabel("Año:"));
        panel.add(spinnerAño);
        panel.add(new JLabel("Hora:"));
        panel.add(spinnerHora);

        int resultado = JOptionPane.showConfirmDialog(this, panel,
                "Seleccionar Fecha de Entrada",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                int dia = (Integer) spinnerDia.getValue();
                int mes = (Integer) spinnerMes.getValue();
                int año = (Integer) spinnerAño.getValue();
                int hora = (Integer) spinnerHora.getValue();

                Calendar cal = Calendar.getInstance();
                cal.set(año, mes - 1, dia, hora, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);

                Date fechaSeleccionada = cal.getTime();

                // Validar que la fecha no sea en el pasado
                Date ahora = new Date();
                if (fechaSeleccionada.before(ahora)) {
                    JOptionPane.showMessageDialog(this,
                            "La fecha seleccionada no puede ser en el pasado.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }

                return fechaSeleccionada;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Fecha inválida. Por favor, verifique los valores.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }

        return null;
    }

    private void mostrarVoucher(Reserva r) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== COMPROBANTE DE RESERVA ===\n");
        sb.append("Hotel Las Brisas\n");
        sb.append("------------------------------\n");
        sb.append("Reserva ID: ").append(r.getIdReserva()).append("\n");
        sb.append("Estado: ").append(r.getEstado()).append("\n");
        if (r.getEstado().equals("PENDIENTE")) {
            sb.append("(La habitación se ocupará el día de entrada)\n");
        }
        sb.append("------------------------------\n");
        sb.append("Huésped: ").append(r.getHuesped().getNombre()).append(" ")
                .append(r.getHuesped().getApellido()).append("\n");
        sb.append("RUT: ").append(r.getHuesped().getRut()).append("\n");
        sb.append("Teléfono: ").append(r.getHuesped().getTelefono()).append("\n");
        sb.append("Habitación: ").append(r.getHabitacion().getNumero()).append(" (")
                .append(r.getHabitacion().getTipo()).append(")\n");
        sb.append("Días de Estadía: ").append(r.getDiasEstadia()).append("\n");
        sb.append("Precio por Noche: $").append(String.format("%.0f", r.getHabitacion().getPrecioNoche())).append("\n");
        sb.append("Total Pagado: $").append(String.format("%.0f", r.getTotalPagado())).append("\n");
        sb.append("Método: ").append(r.getMetodoPago()).append("\n");
        sb.append("------------------------------\n");
        sb.append("Entrada: ").append(formatoFecha.format(r.getFechaEntrada())).append("\n");
        sb.append("Salida: ").append(formatoFecha.format(r.getFechaSalida())).append("\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Reserva Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarGestionHabitaciones() {
        String[] opciones = {"Desocupar Habitación", "Extender Estadía", "Cancelar Reserva"};
        int seleccion = JOptionPane.showOptionDialog(this,
                "Seleccione una opción:", "Gestión de Habitaciones",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);

        if (seleccion == -1) return;

        switch (seleccion) {
            case 0:
                desocuparHabitacion();
                break;
            case 1:
                extenderEstadia();
                break;
            case 2:
                cancelarReserva();
                break;
        }
    }

    private void desocuparHabitacion() {
        String numHab = JOptionPane.showInputDialog(this,
                "Ingrese el número de habitación a desocupar (check-out):");

        if (numHab == null || numHab.trim().isEmpty()) return;

        try {
            Habitacion hab = controlador.buscarHabitacion(numHab.trim());
            if (hab == null) {
                JOptionPane.showMessageDialog(this, "Habitación no encontrada.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (hab.isDisponible()) {
                JOptionPane.showMessageDialog(this,
                        "La habitación ya está disponible.\n\n" +
                        "Nota: Use 'Cancelar Reserva' para cancelar reservas futuras.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Buscar la reserva activa de esta habitación
            Reserva reservaActiva = controlador.buscarReservaActiva(numHab.trim());

            String mensaje = "¿Está seguro de desocupar la habitación " + numHab + "?\n\n";
            if (reservaActiva != null) {
                mensaje += "Reserva actual:\n" +
                          "Cliente: " + reservaActiva.getHuesped().getNombre() + " " +
                                       reservaActiva.getHuesped().getApellido() + "\n" +
                          "Check-in: " + formatoFecha.format(reservaActiva.getFechaEntrada()) + "\n" +
                          "Check-out previsto: " + formatoFecha.format(reservaActiva.getFechaSalida()) + "\n\n" +
                          "Esto finalizará la estadía del huésped.";
            }

            int confirmar = JOptionPane.showConfirmDialog(this,
                    mensaje,
                    "Confirmar Desocupación (Check-out)",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmar == JOptionPane.YES_OPTION) {
                controlador.liberarHabitacion(numHab.trim());
                JOptionPane.showMessageDialog(this,
                        "Habitación desocupada exitosamente.\n" +
                        "La habitación está ahora DISPONIBLE.",
                        "Check-out Completado",
                        JOptionPane.INFORMATION_MESSAGE);
                buscarHabitaciones();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void extenderEstadia() {
        String numHab = JOptionPane.showInputDialog(this,
                "Ingrese el número de habitación:");

        if (numHab == null || numHab.trim().isEmpty()) return;

        String diasStr = JOptionPane.showInputDialog(this,
                "¿Cuántos días adicionales?");

        if (diasStr == null || diasStr.trim().isEmpty()) return;

        try {
            int diasAdicionales = Integer.parseInt(diasStr.trim());
            controlador.extenderEstadia(numHab.trim(), diasAdicionales);

            Reserva r = controlador.buscarReservaActiva(numHab.trim());
            if (r != null) {
                double montoAdicional = r.getHabitacion().getPrecioNoche() * diasAdicionales;
                JOptionPane.showMessageDialog(this,
                        "Estadía extendida exitosamente.\n" +
                        "Días adicionales: " + diasAdicionales + "\n" +
                        "Monto adicional: $" + String.format("%.0f", montoAdicional) + "\n" +
                        "Nueva fecha de salida: " + formatoFecha.format(r.getFechaSalida()),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            buscarHabitaciones();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un número válido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarReserva() {
        String numHab = JOptionPane.showInputDialog(this,
                "Ingrese el número de habitación de la reserva a cancelar:");

        if (numHab == null || numHab.trim().isEmpty()) return;

        try {
            // Buscar todas las reservas (activas o pendientes) para esta habitación
            List<Reserva> reservasDisponibles = new ArrayList<>();
            for (Reserva reserva : controlador.obtenerReservas()) {
                if (reserva.getHabitacion().getNumero().equals(numHab.trim()) &&
                    (reserva.getEstado().equals("ACTIVA") || reserva.getEstado().equals("PENDIENTE"))) {
                    reservasDisponibles.add(reserva);
                }
            }

            if (reservasDisponibles.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay reservas activas o pendientes para esta habitación.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Reserva reservaSeleccionada = null;

            // Si hay múltiples reservas, mostrar lista para seleccionar
            if (reservasDisponibles.size() > 1) {
                // Crear formato simple para fecha
                SimpleDateFormat formatoSimple = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                // Crear opciones para el diálogo
                String[] opciones = new String[reservasDisponibles.size()];
                for (int i = 0; i < reservasDisponibles.size(); i++) {
                    Reserva r = reservasDisponibles.get(i);
                    String tipoReserva = r.getEstado().equals("ACTIVA") ? "ACTIVA" : "PENDIENTE";
                    opciones[i] = String.format("[%d] %s - %s a %s - %s %s",
                            (i + 1),
                            tipoReserva,
                            formatoSimple.format(r.getFechaEntrada()),
                            formatoSimple.format(r.getFechaSalida()),
                            r.getHuesped().getNombre(),
                            r.getHuesped().getApellido());
                }

                String seleccion = (String) JOptionPane.showInputDialog(
                        this,
                        "Hay " + reservasDisponibles.size() + " reservas para la habitación " + numHab +
                        ".\nSeleccione cuál desea cancelar:",
                        "Seleccionar Reserva a Cancelar",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );

                if (seleccion == null) return; // Usuario canceló

                // Encontrar la reserva seleccionada usando el índice
                for (int i = 0; i < opciones.length; i++) {
                    if (opciones[i].equals(seleccion)) {
                        reservaSeleccionada = reservasDisponibles.get(i);
                        break;
                    }
                }
            } else {
                // Solo hay una reserva
                reservaSeleccionada = reservasDisponibles.get(0);
            }

            if (reservaSeleccionada == null) return;

            // Mostrar confirmación
            String tipoReserva = reservaSeleccionada.getEstado().equals("ACTIVA") ?
                                "ACTIVA (En curso)" : "PENDIENTE (Futura)";

            int confirmar = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de cancelar la reserva?\n\n" +
                    "ID: " + reservaSeleccionada.getIdReserva() + "\n" +
                    "Cliente: " + reservaSeleccionada.getHuesped().getNombre() + " " +
                                 reservaSeleccionada.getHuesped().getApellido() + "\n" +
                    "Habitación: " + numHab + "\n" +
                    "Tipo de reserva: " + tipoReserva + "\n" +
                    "Entrada: " + formatoFecha.format(reservaSeleccionada.getFechaEntrada()) + "\n" +
                    "Salida: " + formatoFecha.format(reservaSeleccionada.getFechaSalida()),
                    "Confirmar Cancelación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirmar == JOptionPane.YES_OPTION) {
                // Cancelar usando el ID de la reserva específica
                controlador.cancelarReservaPorId(reservaSeleccionada.getIdReserva());

                JOptionPane.showMessageDialog(this,
                        "Reserva cancelada exitosamente.\n" +
                        (reservaSeleccionada.getEstado().equals("ACTIVA") ?
                            "La habitación ha sido liberada." : ""),
                        "Cancelación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                buscarHabitaciones();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Para ver el error completo en consola
        }
    }

    private void mostrarReservasFuturas() {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Reservas Futuras y Activas", true);
        dialogo.setSize(900, 500);
        dialogo.setLocationRelativeTo(this);

        // Crear tabla para mostrar las reservas
        DefaultTableModel modeloReservas = new DefaultTableModel(
                new String[]{"Habitación", "Tipo", "Cliente", "Estado", "Entrada", "Salida", "Días", "Total"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tablaReservas = new JTable(modeloReservas);
        tablaReservas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaReservas.getColumnModel().getColumn(1).setPreferredWidth(90);
        tablaReservas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaReservas.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaReservas.getColumnModel().getColumn(4).setPreferredWidth(130);
        tablaReservas.getColumnModel().getColumn(5).setPreferredWidth(130);
        tablaReservas.getColumnModel().getColumn(6).setPreferredWidth(50);
        tablaReservas.getColumnModel().getColumn(7).setPreferredWidth(90);

        // Obtener todas las reservas activas y pendientes
        List<Reserva> todasReservas = controlador.obtenerReservas();
        Date hoy = new Date();

        for (Reserva r : todasReservas) {
            if (r.getEstado().equals("ACTIVA") || r.getEstado().equals("PENDIENTE")) {
                modeloReservas.addRow(new Object[]{
                    r.getHabitacion().getNumero(),
                    r.getHabitacion().getTipo(),
                    r.getHuesped().getNombre() + " " + r.getHuesped().getApellido(),
                    r.getEstado(),
                    formatoFecha.format(r.getFechaEntrada()),
                    formatoFecha.format(r.getFechaSalida()),
                    r.getDiasEstadia(),
                    "$" + String.format("%.0f", r.getTotalPagado())
                });
            }
        }

        JScrollPane scrollPane = new JScrollPane(tablaReservas);

        // Panel de información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBorder(new EmptyBorder(10, 10, 10, 10));

        int totalActivas = 0;
        int totalPendientes = 0;
        for (Reserva r : todasReservas) {
            if (r.getEstado().equals("ACTIVA")) totalActivas++;
            if (r.getEstado().equals("PENDIENTE")) totalPendientes++;
        }

        JLabel lblInfo = new JLabel(
                "Reservas Activas: " + totalActivas + "  |  " +
                "Reservas Pendientes: " + totalPendientes + "  |  " +
                "Total: " + (totalActivas + totalPendientes));
        lblInfo.setFont(new Font("Arial", Font.BOLD, 12));
        panelInfo.add(lblInfo);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> {
            dialogo.dispose();
            mostrarReservasFuturas();
        });

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialogo.dispose());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);

        // Organizar el diálogo
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.add(panelInfo, BorderLayout.NORTH);
        dialogo.add(scrollPane, BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);

        dialogo.setVisible(true);
    }
}