package mailapp;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Program entry point. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ex) { System.err.println("Using default Swing look and feel."); }
            MailService service = new MailService();
            new LoginFrame(service).setVisible(true);
        });
    }
}
