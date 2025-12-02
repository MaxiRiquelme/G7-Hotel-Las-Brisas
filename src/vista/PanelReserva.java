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

        // --- 1. Panel Superior: Botón Volver + Filtros ---
        // Usamos un BorderLayout interno para separar el botón "Volver" de los filtros
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 0));

        // A. Botón Volver (A la izquierda)
        JButton btnVolver = new JButton("< Volver al Menú");
        btnVolver.addActionListener(e -> mainFrame.mostrarVista("MENU"));
        panelSuperior.add(btnVolver, BorderLayout.WEST);

        // B. Filtros (Al centro/derecha)
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.add(new JLabel("Filtrar por Tipo:"));

        cmbTipoHabitacion = new JComboBox<>(new String[]{"TODAS", "Single", "Matrimonial", "Suite"});
        panelFiltro.add(cmbTipoHabitacion);

        JButton btnBuscar = new JButton("Verificar Disponibilidad");
        btnBuscar.addActionListener(e -> buscarHabitaciones());
        panelFiltro.add(btnBuscar);

        panelSuperior.add(panelFiltro, BorderLayout.CENTER);

        // Agregamos todo el panel superior al Norte del panel principal
        add(panelSuperior, BorderLayout.NORTH);


        // --- 2. Panel Central: Tabla de Disponibilidad ---
        modeloTabla = new DefaultTableModel(new String[]{"Número", "Tipo", "Precio", "Estado"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHabitaciones = new JTable(modeloTabla);
        tablaHabitaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tablaHabitaciones);
        scroll.setBorder(BorderFactory.createTitledBorder("Habitaciones Disponibles - Seleccione una para reservar"));
        add(scroll, BorderLayout.CENTER);


        // --- 3. Panel Inferior: Acción de Reservar ---
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReservar = new JButton("Realizar Check-in / Pago");
        btnReservar.setFont(new Font("Arial", Font.BOLD, 14));
        btnReservar.setBackground(new Color(100, 150, 200));

        btnReservar.addActionListener(e -> iniciarProcesoReserva());
        panelBoton.add(btnReservar);

        add(panelBoton, BorderLayout.SOUTH);

        // Carga inicial
        buscarHabitaciones();
    }

    private void buscarHabitaciones() {
        modeloTabla.setRowCount(0);
        String tipo = (String) cmbTipoHabitacion.getSelectedItem();
        // Asegúrate de que tu ControladorHotel tenga implementado buscarHabitacionesDisponibles
        List<Habitacion> disponibles = controlador.buscarHabitacionesDisponibles(tipo);

        for (Habitacion h : disponibles) {
            modeloTabla.addRow(new Object[]{
                    h.getNumero(),
                    h.getTipo(),
                    "$" + h.getPrecioNoche(),
                    "Disponible"
            });
        }
    }

    private void iniciarProcesoReserva() {
        int row = tablaHabitaciones.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una habitación de la lista.");
            return;
        }

        String numHabitacion = (String) modeloTabla.getValueAt(row, 0);
        String precioStr = (String) modeloTabla.getValueAt(row, 2);

        // Paso: Datos Personales
        JTextField txtRut = new JTextField();
        Object[] mensajeRut = {"Ingrese RUT del Huésped:", txtRut};
        int opcion = JOptionPane.showConfirmDialog(this, mensajeRut, "Datos del Huésped", JOptionPane.OK_CANCEL_OPTION);

        if (opcion != JOptionPane.OK_OPTION || txtRut.getText().trim().isEmpty()) return;

        String rut = txtRut.getText();
        Huesped huespedExistente = controlador.buscarCliente(rut);
        String nombre;

        if (huespedExistente != null) {
            nombre = huespedExistente.getNombre();
            JOptionPane.showMessageDialog(this, "Cliente encontrado: " + nombre);
        } else {
            nombre = JOptionPane.showInputDialog(this, "Cliente Nuevo. Ingrese Nombre Completo:");
            if (nombre == null || nombre.trim().isEmpty()) return;
        }

        // Paso: Pago
        int confirmacionPago = JOptionPane.showConfirmDialog(this,
                "Monto a Pagar: " + precioStr + "\n¿Confirmar pago y recepción?",
                "Pago de Habitación", JOptionPane.YES_NO_OPTION);

        if (confirmacionPago == JOptionPane.YES_OPTION) {
            try {
                Reserva r = controlador.realizarReserva(rut, nombre, numHabitacion, "EFECTIVO/TARJETA");
                mostrarVoucher(r);
                buscarHabitaciones(); // Refrescar tabla
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void mostrarVoucher(Reserva r) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== COMPROBANTE DE RESERVA ===\n");
        sb.append("Hotel Las Brisas\n");
        sb.append("------------------------------\n");
        sb.append("Reserva ID: ").append(r.getId()).append("\n");
        sb.append("Huésped: ").append(r.getCliente().getNombre()).append("\n");
        sb.append("Habitación: ").append(r.getHabitacion().getNumero()).append("\n");
        sb.append("Total: $").append(r.getTotalPagado()).append("\n");
        sb.append("------------------------------\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Reserva Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }
}