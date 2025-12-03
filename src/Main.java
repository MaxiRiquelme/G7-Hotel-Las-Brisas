import controlador.ControladorHotel;
import controlador.ControladorCafeteria;
import controlador.GestorDatos;
import vista.VistaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Obtener la instancia única de GestorDatos (carga datos automáticamente)
            GestorDatos gestor = GestorDatos.obtenerInstancia();

            // Crear controladores que usan la misma instancia de GestorDatos
            ControladorHotel ctrlHotel = new ControladorHotel();
            ControladorCafeteria ctrlCafeteria = new ControladorCafeteria();

            // Inicia la vista principal
            VistaPrincipal vista = new VistaPrincipal(ctrlHotel, ctrlCafeteria);
            vista.setVisible(true);
        });
    }
}