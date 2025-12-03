package vista;

import controlador.ControladorCafeteria;
import controlador.ControladorHotel;

import javax.swing.*;
import java.awt.*;

public class VistaPrincipal extends JFrame {
    private ControladorHotel ctrlHotel;
    private ControladorCafeteria ctrlCafeteria;

    private JPanel panelContenido;
    private CardLayout cardLayout;

    public VistaPrincipal(ControladorHotel ctrlHotel, ControladorCafeteria ctrlCafeteria) {
        this.ctrlHotel = ctrlHotel;
        this.ctrlCafeteria = ctrlCafeteria;
        inicializarUI();
    }

    private void inicializarUI() {
        setTitle("Hotel Las Brisas");
        setSize(1024, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout para cambiar entre vistas (Menú, Reserva, Cafetería)
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);

        // --- Creación de los Paneles ---

        // 1. Menú Principal
        JPanel panelMenu = crearPanelMenu();

        // 2. Panel de Reservas (Módulo Hotel)
        PanelReserva panelReserva = new PanelReserva(ctrlHotel, this);

        // 3. Panel de Cafetería (Módulo Cafetería)
        PanelVenta panelCafeteria = new PanelVenta(ctrlCafeteria, this);

        // 4. Panel de Inventario (Opcional, usando el controlador de cafetería)
        PanelInventario panelInventario = new PanelInventario(ctrlCafeteria, this);

        panelContenido.add(panelMenu, "MENU");
        panelContenido.add(panelReserva, "RESERVA");
        panelContenido.add(panelCafeteria, "CAFETERIA");
        panelContenido.add(panelInventario, "INVENTARIO");

        add(panelContenido);
    }

    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 250)); // Color suave de fondo

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Márgenes entre botones
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Título y Logo
        JLabel titulo = new JLabel("Hotel Las Brisas", SwingConstants.CENTER);
        titulo.setFont(new Font("Rockwell", Font.BOLD, 75));
        titulo.setForeground(new Color(40, 60, 100));
        gbc.gridy = 0;
        panel.add(titulo, gbc);

        JLabel subtitulo = new JLabel("Seleccione una opción", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitulo.setForeground(Color.GRAY);
        gbc.gridy = 1;
        panel.add(subtitulo, gbc);

        // --- Botones del Menú ---

        // Botón 1: Recepción
        JButton btnReserva = crearBotonMenu("Reserva de Habitación", new Color(100, 150, 200));
        btnReserva.addActionListener(e -> mostrarVista("RESERVA"));
        gbc.gridy = 2;
        panel.add(btnReserva, gbc);

        // Botón 2: Cafetería
        JButton btnCafeteria = crearBotonMenu("Cafetería", new Color(200, 140, 100));
        btnCafeteria.addActionListener(e -> mostrarVista("CAFETERIA"));
        gbc.gridy = 3;
        panel.add(btnCafeteria, gbc);

        // Botón 3: Inventario (Cafetería)
        JButton btnInventario = crearBotonMenu("Inventario Cafetería", new Color(140, 140, 140));
        btnInventario.addActionListener(e -> {
            ((PanelInventario)panelContenido.getComponent(3)).actualizarTabla();
            mostrarVista("INVENTARIO");
        });
        gbc.gridy = 4;
        panel.add(btnInventario, gbc);

        // Botón 4: Salir
        JButton btnSalir = crearBotonMenu("Salir del Sistema", new Color(255, 100, 100));
        btnSalir.addActionListener(e -> System.exit(0));
        gbc.gridy = 5;
        panel.add(btnSalir, gbc);

        return panel;
    }

    private JButton crearBotonMenu(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.setPreferredSize(new Dimension(250, 50));
        btn.setBackground(colorFondo);
        return btn;
    }

    // Método para navegar entre paneles
    public void mostrarVista(String nombreVista) {
        cardLayout.show(panelContenido, nombreVista);
    }
}