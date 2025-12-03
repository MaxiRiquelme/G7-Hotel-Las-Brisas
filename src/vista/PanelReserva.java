package vista;

import controlador.ControladorHotel;
import modelo.Huesped;
import modelo.Habitacion;
import modelo.Reserva;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelReserva extends JPanel {
    private ControladorHotel controlador;
    private VistaPrincipal mainFrame;
    private JComboBox<String> cmbTipoHabitacion;
    private DefaultTableModel modeloTabla;
    private JTable tablaHabitaciones;

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

        panelSuperior.add(panelFiltro, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel Central: Tabla
        modeloTabla = new DefaultTableModel(new String[]{"Número", "Tipo", "Precio", "Estado"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHabitaciones = new JTable(modeloTabla);
        tablaHabitaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tablaHabitaciones);
        scroll.setBorder(BorderFactory.createTitledBorder("Habitaciones Disponibles"));
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
        List<Habitacion> disponibles = controlador.buscarHabitacionesDisponibles(tipo);

        for (Habitacion h : disponibles) {
            modeloTabla.addRow(new Object[]{
                    h.getNumero(),
                    h.getTipo(),
                    "$" + h.getPrecioNoche(),
                    h.getEstado()
            });
        }
    }

    private void iniciarProcesoReserva() {
        int row = tablaHabitaciones.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una habitación.");
            return;
        }

        String numHabitacion = (String) modeloTabla.getValueAt(row, 0);
        String precioStr = (String) modeloTabla.getValueAt(row, 2);

        // Paso 1: Datos del Huésped
        JTextField txtRut = new JTextField();
        Object[] mensajeRut = {"Ingrese RUT del Huésped:", txtRut};
        int opcion = JOptionPane.showConfirmDialog(this, mensajeRut, "Datos del Huésped",
                JOptionPane.OK_CANCEL_OPTION);

        if (opcion != JOptionPane.OK_OPTION || txtRut.getText().trim().isEmpty()) return;

        String rut = txtRut.getText();
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

            nombre = txtNombre.getText();
            apellido = txtApellido.getText();

            if (nombre.trim().isEmpty() || apellido.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Los datos son requeridos.");
                return;
            }
        }

        // Paso 2: Seleccionar método de pago
        String[] opciones = {"EFECTIVO", "TARJETA", "TRANSFERENCIA"};
        int seleccion = JOptionPane.showOptionDialog(this,
                "Método de Pago para: " + precioStr, "Pago",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion == -1) return;
        String metodoPago = opciones[seleccion];

        // Paso 3: Confirmar pago
        int confirmacionPago = JOptionPane.showConfirmDialog(this,
                "Monto a Pagar: " + precioStr + "\nMétodo: " + metodoPago + "\n¿Confirmar?",
                "Confirmación de Pago", JOptionPane.YES_NO_OPTION);

        if (confirmacionPago == JOptionPane.YES_OPTION) {
            try {
                Reserva r = controlador.realizarReserva(rut, nombre, apellido, numHabitacion, metodoPago);
                mostrarVoucher(r);
                buscarHabitaciones();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarVoucher(Reserva r) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== COMPROBANTE DE RESERVA ===\n");
        sb.append("Hotel Las Brisas\n");
        sb.append("------------------------------\n");
        sb.append("Reserva ID: ").append(r.getIdReserva()).append("\n");
        sb.append("Huésped: ").append(r.getHuesped().getNombre()).append(" ")
                .append(r.getHuesped().getApellido()).append("\n");
        sb.append("RUT: ").append(r.getHuesped().getRut()).append("\n");
        sb.append("Habitación: ").append(r.getHabitacion().getNumero()).append(" (")
                .append(r.getHabitacion().getTipo()).append(")\n");
        sb.append("Precio: $").append(r.getTotalPagado()).append("\n");
        sb.append("Método: ").append(r.getMetodoPago()).append("\n");
        sb.append("------------------------------\n");
        sb.append("Fecha: ").append(r.getFechaEntrada()).append("\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Reserva Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
    }
}