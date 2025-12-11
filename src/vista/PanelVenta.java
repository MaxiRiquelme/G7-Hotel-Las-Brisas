package vista;

import controlador.ActualizacionListener;
import controlador.ControladorCafeteria;
import modelo.Huesped;
import modelo.DetalleVenta;
import modelo.Producto;
import modelo.Venta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class PanelVenta extends JPanel implements ActualizacionListener {
    private ControladorCafeteria controlador;
    private VistaPrincipal mainFrame;
    private JTextField txtBuscar;
    private DefaultTableModel modeloTablaProd;
    private DefaultTableModel modeloTablaCarrito;
    private JLabel lblTotal;
    private JTable tablaProductos;

    public PanelVenta(ControladorCafeteria ctrl, VistaPrincipal frame) {
        this.controlador = ctrl;
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- TOP: Volver y Buscador ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton btnVolver = new JButton("Menú Principal");
        btnVolver.addActionListener(e -> {
            controlador.vaciarCarrito();
            actualizarCarritoUI();
            mainFrame.mostrarVista("MENU");
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscar = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarProductos());
        txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { buscarProductos(); }
        });
        searchPanel.add(new JLabel("Producto: "));
        searchPanel.add(txtBuscar);
        searchPanel.add(btnBuscar);

        topPanel.add(btnVolver, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER: Tablas ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        modeloTablaProd = new DefaultTableModel(new String[]{"ID", "Producto", "Precio", "Stock"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaProductos = new JTable(modeloTablaProd);
        JScrollPane scrollProd = new JScrollPane(tablaProductos);
        scrollProd.setBorder(BorderFactory.createTitledBorder("Menú Cafetería"));

        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tablaProductos.getSelectedRow();
                    if (row != -1) agregarAlCarrito((String) modeloTablaProd.getValueAt(row, 0));
                }
            }
        });

        modeloTablaCarrito = new DefaultTableModel(new String[]{"Item", "Cant", "Subtotal"}, 0);
        JTable tablaCarrito = new JTable(modeloTablaCarrito);
        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        scrollCarrito.setBorder(BorderFactory.createTitledBorder("Orden Actual"));

        centerPanel.add(scrollProd);
        centerPanel.add(scrollCarrito);
        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM: Pago ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: $0");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 20));
        JButton btnPagar = new JButton("Procesar Pago");
        btnPagar.setBackground(new Color(100, 180, 100));
        btnPagar.setForeground(Color.WHITE);
        btnPagar.addActionListener(e -> iniciarProcesoPago());

        bottomPanel.add(lblTotal);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(btnPagar);
        add(bottomPanel, BorderLayout.SOUTH);

        // Registrar listener para actualizaciones automáticas
        controlador.agregarListener(this);

        buscarProductos();
    }

    @Override
    public void onActualizacion(String tipo) {
        if (tipo.equals("PRODUCTOS")) {
            SwingUtilities.invokeLater(() -> buscarProductos());
        }
    }

    private void buscarProductos() {
        modeloTablaProd.setRowCount(0);
        List<Producto> lista = controlador.buscarProductos(txtBuscar.getText());
        for (Producto p : lista) {
            modeloTablaProd.addRow(new Object[]{p.getIdProducto(), p.getNombre(), p.getPrecio(), p.getStock()});
        }
    }

    private void agregarAlCarrito(String id) {
        Producto p = controlador.buscarProductoPorId(id);
        if (p != null) {
            try {
                controlador.agregarAlCarrito(p, 1);
                actualizarCarritoUI();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void actualizarCarritoUI() {
        modeloTablaCarrito.setRowCount(0);
        for (DetalleVenta dv : controlador.getCarrito()) {
            modeloTablaCarrito.addRow(new Object[]{dv.getProducto().getNombre(), dv.getCantidad(), dv.getSubtotal()});
        }
        lblTotal.setText("Total: $" + controlador.calcularTotalCarrito());
    }

    private void iniciarProcesoPago() {
        if (controlador.getCarrito().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La orden está vacía.");
            return;
        }

        // 1. Identificar Cliente
        String rut = JOptionPane.showInputDialog(this, "RUT del Cliente:");
        if (rut == null || rut.trim().isEmpty()) return;

        Huesped c = controlador.buscarCliente(rut);
        String nombre, habitacion = "";

        if (c != null) {
            nombre = c.getNombre();
            habitacion = c.getNumeroHabitacion();
            String msg = "Cliente: " + nombre + (c.esHuesped() ? " (Hab: " + habitacion + ")" : " (Externo)");
            JOptionPane.showMessageDialog(this, msg);
        } else {
            nombre = JOptionPane.showInputDialog(this, "Nuevo Cliente - Nombre:");
            if (nombre == null) return;
            // Opción de registrar habitación si es huésped
            int esHuesped = JOptionPane.showConfirmDialog(this, "¿Es huésped del hotel?", "Tipo Cliente", JOptionPane.YES_NO_OPTION);
            if (esHuesped == JOptionPane.YES_OPTION) {
                habitacion = JOptionPane.showInputDialog("Número de Habitación:");
            }
        }

        // 2. Selección Método Pago
        String[] opciones = {"Efectivo", "Tarjeta", "Cargo a Habitación"};
        int seleccion = JOptionPane.showOptionDialog(this, "Seleccione el método de pago", "Método de Pago",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion == -1) return;

        // Mapear la selección visual al valor interno del sistema
        String[] valoresInternos = {"EFECTIVO", "TARJETA", "CARGO_HABITACION"};
        String metodo = valoresInternos[seleccion];

        // 3. Captura datos de pago
        int efectivo = 0;
        String tarjetaNum = "";

        if (metodo.equals("EFECTIVO")) {
            String m = JOptionPane.showInputDialog("Total: $" + controlador.calcularTotalCarrito() + "\nDinero recibido:");
            try { efectivo = Integer.parseInt(m); } catch(Exception ex) { return; }
        } else if (metodo.equals("TARJETA")) {
            tarjetaNum = JOptionPane.showInputDialog("Número de Tarjeta (Simulado):");
        }
        // Si es Cargo Habitación, la validación se hace en el controlador

        // 4. Finalizar
        try {
            Venta v = controlador.finalizarVenta(rut, nombre, habitacion, metodo, efectivo, tarjetaNum);
            mostrarRecibo(v);
            actualizarCarritoUI();
            buscarProductos();
            mainFrame.mostrarVista("MENU");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error Pago", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarRecibo(Venta v) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HOTEL LAS BRISAS ===\n");
        sb.append("=== TICKET CAFETERÍA ===\n");
        sb.append("Fecha: ").append(v.getFecha()).append("\n");
        sb.append("Cliente: ").append(v.getCliente().getNombre()).append("\n");
        if(v.getCliente().esHuesped()) sb.append("Habitación: ").append(v.getCliente().getNumeroHabitacion()).append("\n");
        sb.append("--------------------------\n");
        for (DetalleVenta d : v.getDetalles()) {
            sb.append(d.getCantidad()).append(" x ").append(d.getProducto().getNombre())
                    .append(" $").append(d.getSubtotal()).append("\n");
        }
        sb.append("--------------------------\n");
        sb.append("TOTAL: $").append(v.getTotal()).append("\n");
        sb.append("Pago: ").append(v.getMetodoPago()).append("\n");
        if (v.getMetodoPago().equals("EFECTIVO")) sb.append("Vuelto: $").append(v.getVuelto()).append("\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Ticket Generado", JOptionPane.INFORMATION_MESSAGE);
    }
}