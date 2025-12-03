package vista;

import controlador.ControladorCafeteria;
import modelo.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelInventario extends JPanel {
    private ControladorCafeteria controlador; // 2. Actualizar el tipo de variable
    private VistaPrincipal mainFrame;
    private DefaultTableModel modelo;

    // 3. Actualizar el constructor para recibir ControladorCafeteria
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
            @Override // Hacer que las celdas no sean editables directamente
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- Botones Inferiores ---
        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Nuevo Producto");
        JButton btnStock = new JButton("Aumentar Stock");

        btnAgregar.addActionListener(e -> agregarProducto());
        btnStock.addActionListener(e -> aumentarStock());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnStock);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void actualizarTabla() {
        modelo.setRowCount(0);
        // El controlador de cafetería debe tener el método obtenerProductos()
        for (Producto p : controlador.obtenerProductos()) {
            modelo.addRow(new Object[]{p.getIdProducto(), p.getNombre(), p.getPrecio(), p.getStock()});
        }
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
                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Producto agregado exitosamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: Datos inválidos. Verifique que precio y stock sean números.", "Error", JOptionPane.ERROR_MESSAGE);
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
                    actualizarTabla();
                    JOptionPane.showMessageDialog(this, "Stock actualizado.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar stock. Verifique el ID y la cantidad.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}