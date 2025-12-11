package vista;

import controlador.ActualizacionListener;
import controlador.ControladorCafeteria;
import controlador.ActualizacionListener;
import modelo.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelInventario extends JPanel implements ActualizacionListener {
    private ControladorCafeteria controlador;
    private VistaPrincipal mainFrame;
    private DefaultTableModel modelo;
    private JTable tabla;

    public PanelInventario(ControladorCafeteria ctrl, VistaPrincipal frame) {
        this.controlador = ctrl;
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Panel Superior ---
        JButton btnVolver = new JButton("< Volver al Menú");
        btnVolver.addActionListener(e -> mainFrame.mostrarVista("MENU"));
        add(btnVolver, BorderLayout.NORTH);

        // --- Tabla Central ---
        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Precio", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- Botones Inferiores ---
        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Nuevo Producto");
        JButton btnAumentarStock = new JButton("Aumentar Stock");
        JButton btnReducirStock = new JButton("Reducir Stock");
        JButton btnEliminar = new JButton("Eliminar Producto");

        btnAgregar.addActionListener(e -> agregarProducto());
        btnAumentarStock.addActionListener(e -> aumentarStock());
        btnReducirStock.addActionListener(e -> reducirStock());
        btnEliminar.addActionListener(e -> eliminarProducto());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnAumentarStock);
        panelBotones.add(btnReducirStock);
        panelBotones.add(btnEliminar);
        add(panelBotones, BorderLayout.SOUTH);

        // Registrar listener para actualizaciones automáticas
        controlador.agregarListener(this);

        // Cargar datos iniciales
        actualizarTabla();
    }

    @Override
    public void onActualizacion(String tipo) {
        if (tipo.equals("PRODUCTOS")) {
            SwingUtilities.invokeLater(() -> actualizarTabla());
        }
    }

    private void refrescarTabla() {
        modelo.setRowCount(0);
        for (Producto p : controlador.obtenerProductos()) {
            modelo.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getNombre(),
                    p.getPrecio(),
                    p.getStock()
            });
        }
    }

    public void actualizarTabla() {
        refrescarTabla();
    }

    private void agregarProducto() {
        JTextField txtId = new JTextField();
        JTextField txtNom = new JTextField();
        JTextField txtPre = new JTextField();
        JTextField txtStk = new JTextField();
        Object[] msg = {"ID:", txtId, "Nombre:", txtNom, "Precio:", txtPre, "Stock:", txtStk};

        int opcion = JOptionPane.showConfirmDialog(this, msg, "Nuevo Producto Cafetería", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                controlador.agregarProductoNuevo(
                        txtId.getText(),
                        txtNom.getText(),
                        Integer.parseInt(txtPre.getText()),
                        Integer.parseInt(txtStk.getText())
                );
                refrescarTabla();
                JOptionPane.showMessageDialog(this, "Producto agregado exitosamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void aumentarStock() {
        String id = JOptionPane.showInputDialog(this, "Ingrese ID del producto:");
        if (id != null && !id.trim().isEmpty()) {
            String cant = JOptionPane.showInputDialog(this, "Cantidad a agregar:");
            try {
                if (cant != null) {
                    controlador.aumentarStock(id, Integer.parseInt(cant));
                    refrescarTabla();
                    JOptionPane.showMessageDialog(this, "Stock actualizado.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void reducirStock() {
        String id = JOptionPane.showInputDialog(this, "Ingrese ID del producto:");
        if (id != null && !id.trim().isEmpty()) {
            String cant = JOptionPane.showInputDialog(this, "Cantidad a reducir:");
            try {
                if (cant != null) {
                    controlador.disminuirStock(id, Integer.parseInt(cant));
                    refrescarTabla();
                    JOptionPane.showMessageDialog(this, "Stock reducido exitosamente.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarProducto() {
        String id = JOptionPane.showInputDialog(this, "Ingrese ID del producto a eliminar:");
        if (id != null && !id.trim().isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el producto con ID: " + id + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    controlador.eliminarProducto(id);
                    refrescarTabla();
                    JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}