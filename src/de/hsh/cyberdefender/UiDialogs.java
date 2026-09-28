package de.hsh.cyberdefender;

import javax.swing.*;
import java.awt.*;

/**
 * Hilfsklasse für einheitliche Dialoge im Arcade-Design.
 * Nutzt ein eigenes JDialog-Fenster statt Standard-JOptionPane.
 */
public final class UiDialogs {

    private UiDialogs() {
        // Utility-Klasse: keine Instanzen erlauben
    }

    /**
     * Zeigt einen Info-Dialog (modal) mit Titel, Text und OK-Button an.
     *
     * @param parent Parent-Komponente (für Positionierung/Owner), kann null sein
     * @param title  Titel des Dialogs
     * @param text   Inhaltstext des Dialogs
     */
    public static void showInfo(Component parent, String title, String text) {
        // Owner-Fenster ermitteln, damit der Dialog korrekt zentriert wird
        Window owner = (parent == null) ? null : SwingUtilities.getWindowAncestor(parent);

        // Modaler Dialog blockiert Eingaben, bis er geschlossen wird
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        // Root-Panel mit Rahmen und Hintergrund
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(new Color(0, 0, 0, 210));
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 220, 200, 180), 2),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        // Titel oben
        JLabel t = new JLabel(title);
        t.setForeground(new Color(0, 220, 200));
        t.setFont(ArcadeTheme.font(18f));
        root.add(t, BorderLayout.NORTH);

        // Textbereich (read-only), transparent, damit der Hintergrund sichtbar bleibt
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setOpaque(false);
        area.setForeground(Color.WHITE);
        area.setFont(ArcadeTheme.font(14f));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        root.add(area, BorderLayout.CENTER);

        // OK-Button schließt den Dialog
        JButton ok = new JButton("OK");
        ok.setFont(ArcadeTheme.font(14f));
        ok.setFocusPainted(false);
        ok.setBackground(new Color(10, 30, 40));
        ok.setForeground(Color.WHITE);
        ok.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 220, 200, 180), 2),
                BorderFactory.createEmptyBorder(10, 26, 10, 26)
        ));
        ok.addActionListener(e -> dialog.dispose());

        // Button unten mittig
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottom.setOpaque(false);
        bottom.add(ok);
        root.add(bottom, BorderLayout.SOUTH);

        // Dialog anzeigen
        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }
}
