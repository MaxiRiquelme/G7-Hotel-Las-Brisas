import controlador.ControladorHotel;
import controlador.ControladorCafeteria;
import vista.VistaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Estilo nativo del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Inicializamos los controladores de cada módulo
            ControladorHotel ctrlHotel = new ControladorHotel();
            ControladorCafeteria ctrlCafeteria = new ControladorCafeteria();

            // Iniciamos la vista principal pasando ambos controladores
            VistaPrincipal vista = new VistaPrincipal(ctrlHotel, ctrlCafeteria);
            vista.setVisible(true);
        });
    }
}