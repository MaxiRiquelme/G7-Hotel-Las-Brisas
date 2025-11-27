package vista;

import javax.swing.*;
import java.awt.*;

public class VistaPrincipal extends JFrame {

    private JPanel panelContenido;
    private CardLayout cardLayout;

    public VistaPrincipal() {
        inicializarUI();
    }

    private void inicializarUI() {
        setTitle("Sistema Hotel Las Brisas");
        setSize(800, 600); // Tamaño inicial
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla

        // Usamos CardLayout para intercambiar entre el menú y las otras pantallas
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);

        // 1. Crear las "Cartas" (Paneles)
        JPanel panelMenu = crearPanelMenu();
        JPanel panelReserva = crearPanelSimulado("Módulo de Reserva de Habitación", new Color(230, 240, 255));
        JPanel panelCafeteria = crearPanelSimulado("Módulo de Venta en Cafetería", new Color(255, 250, 200));

        // 2. Añadirlas al contenedor principal con un nombre clave
        panelContenido.add(panelMenu, "MENU");
        panelContenido.add(panelReserva, "RESERVA");
        panelContenido.add(panelCafeteria, "CAFETERIA");

        // Agregamos el panel contenedor a la ventana
        add(panelContenido);
    }

    // --- Creador del Menú Principal ---
    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Espacio entre elementos
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titulo = new JLabel("Bienvenido al Hotel", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(titulo, gbc);

        // Botón Reserva
        JButton btnReserva = crearBotonEstilizado("Reserva de Habitación");
        btnReserva.addActionListener(e -> mostrarVista("RESERVA")); // Acción cambiar vista
        gbc.gridy = 1;
        panel.add(btnReserva, gbc);

        // Botón Cafetería
        JButton btnCafeteria = crearBotonEstilizado("Venta en Cafetería");
        btnCafeteria.addActionListener(e -> mostrarVista("CAFETERIA")); // Acción cambiar vista
        gbc.gridy = 2;
        panel.add(btnCafeteria, gbc);

        // Botón Salir
        JButton btnSalir = crearBotonEstilizado("Salir");
        btnSalir.setBackground(new Color(255, 100, 100)); // Rojo suave
        btnSalir.setForeground(Color.WHITE);
        btnSalir.addActionListener(e -> System.exit(0));
        gbc.gridy = 3;
        panel.add(btnSalir, gbc);

        return panel;
    }

    // --- Método auxiliar para crear botones bonitos ---
    private JButton crearBotonEstilizado(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.setPreferredSize(new Dimension(300, 50));
        btn.setFocusPainted(false);
        return btn;
    }

    // --- Método para simular las pantallas vacías de Reserva y Cafetería ---
    // (Esto reemplaza a tus clases externas por ahora para que el código funcione)
    private JPanel crearPanelSimulado(String tituloTexto, Color colorFondo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(colorFondo);

        JLabel lblTitulo = new JLabel(tituloTexto, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        panel.add(lblTitulo, BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver al Menú Principal");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolver.setPreferredSize(new Dimension(200, 50));

        // Acción para volver
        btnVolver.addActionListener(e -> mostrarVista("MENU"));

        panel.add(btnVolver, BorderLayout.SOUTH);

        return panel;
    }

    // Método para cambiar entre pantallas (CardLayout)
    public void mostrarVista(String nombreVista) {
        cardLayout.show(panelContenido, nombreVista);
    }

}