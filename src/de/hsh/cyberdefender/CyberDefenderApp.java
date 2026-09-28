package de.hsh.cyberdefender;

import javax.swing.SwingUtilities;

/**
 * Einstiegspunkt der Anwendung.
 * Startet das UI im Event-Dispatch-Thread (Swing-Thread).
 */
public final class CyberDefenderApp {


    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("Uncaught exception in thread: " + t.getName());
            e.printStackTrace();
        });

        try {
            ArcadeTheme.install();
        } catch (Exception ex) {
            System.err.println("Theme init failed, continuing with default UI.");
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(MainFrame::new);
    }
}
