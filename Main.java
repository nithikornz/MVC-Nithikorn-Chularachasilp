import Controller.RegistrationController;
import Model.RegistrationService;
import View.AppFrame;

import javax.swing.*;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        Path dataDir = Path.of("data"); 
        RegistrationService service = new RegistrationService(dataDir);
        RegistrationController controller = new RegistrationController(service);
        controller.loadData();

        SwingUtilities.invokeLater(() -> {
            AppFrame f = new AppFrame(controller);
            f.setVisible(true);
            f.showLogin(); 
        });
    }
}
