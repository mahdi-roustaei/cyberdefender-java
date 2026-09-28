package de.hsh.cyberdefender;

import javax.swing.*;
import java.awt.*;

/**
 * Einstellungsbildschirm für das Spiel.
 * Hier kann der Spieler den Schwierigkeitsgrad auswählen und speichern.
 */
public class SettingsPanel extends JPanel {

    /** Referenz auf das Hauptfenster, um zwischen Screens zu wechseln. */
    private final MainFrame mainFrame;

    /**
     * Erstellt das Settings-Panel inklusive UI-Elementen und Actions.
     *
     * @param mainFrame Hauptfenster für die Navigation (Menu/Game/Highscore/Settings)
     */
    public SettingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setOpaque(true);

        // Kopfbereich mit Titel und Zurück-Button
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(16, 16, 10, 16));

        JLabel title = new JLabel("SETTINGS");
        title.setForeground(new Color(0, 220, 200));
        title.setFont(ArcadeTheme.font(26f));
        top.add(title, BorderLayout.WEST);

        JButton backTop = makeButtonSmall("BACK");
        backTop.addActionListener(e -> mainFrame.showScreen("menu"));
        top.add(backTop, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // Box in der Mitte (optisch wie Highscore)
        JPanel box = new JPanel();
        box.setOpaque(true);
        box.setBackground(new Color(0, 0, 0, 170));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 220, 200, 120), 2),
                BorderFactory.createEmptyBorder(24, 30, 24, 30)
        ));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel diffLabel = new JLabel("DIFFICULTY:");
        diffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        diffLabel.setForeground(Color.WHITE);
        diffLabel.setFont(ArcadeTheme.font(16f));
        box.add(diffLabel);

        box.add(Box.createVerticalStrut(14));

        // RadioButtons für Schwierigkeitsgrad
        JRadioButton easy = radio("EASY");
        JRadioButton med  = radio("MEDIUM");
        JRadioButton hard = radio("HARD");

        ButtonGroup group = new ButtonGroup();
        group.add(easy);
        group.add(med);
        group.add(hard);

        // Aktuelle Einstellung anzeigen
        Difficulty current = GameSettings.getDifficulty();
        if (current == Difficulty.EASY) {
            easy.setSelected(true);
        } else if (current == Difficulty.HARD) {
            hard.setSelected(true);
        } else {
            med.setSelected(true);
        }

        box.add(easy);
        box.add(Box.createVerticalStrut(8));
        box.add(med);
        box.add(Box.createVerticalStrut(8));
        box.add(hard);

        box.add(Box.createVerticalStrut(18));

        JButton save = makeButton("SAVE");
        JButton back = makeButton("BACK TO MENU");

        // Speichern übernimmt die Auswahl in die GameSettings
        save.addActionListener(e -> {
            if (easy.isSelected()) {
                GameSettings.setDifficulty(Difficulty.EASY);
            } else if (hard.isSelected()) {
                GameSettings.setDifficulty(Difficulty.HARD);
            } else {
                GameSettings.setDifficulty(Difficulty.MEDIUM);
            }

            // Rückmeldung an den Spieler
            JOptionPane.showMessageDialog(
                    this,
                    "Einstellungen gespeichert.",
                    "SAVED",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Zurück ins Menü
        back.addActionListener(e -> mainFrame.showScreen("menu"));

        box.add(save);
        box.add(Box.createVerticalStrut(12));
        box.add(back);

        // Wrapper, damit die Box mittig sitzt
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(box);

        add(center, BorderLayout.CENTER);
    }

    /**
     * Erstellt einen RadioButton im passenden Stil.
     *
     * @param text Anzeigetext
     * @return konfigurierter RadioButton
     */
    private JRadioButton radio(String text) {
        JRadioButton r = new JRadioButton(text);
        r.setOpaque(false);
        r.setForeground(Color.WHITE);
        r.setFont(ArcadeTheme.font(14f));
        r.setAlignmentX(Component.CENTER_ALIGNMENT);
        return r;
    }

    /**
     * Erstellt einen großen Button im Arcade-Design.
     *
     * @param text Button-Text
     * @return Button
     */
    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFont(ArcadeTheme.font(14f));
        b.setFocusPainted(false);
        b.setBackground(new Color(10, 30, 40));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 220, 200, 180), 2),
                BorderFactory.createEmptyBorder(10, 26, 10, 26)
        ));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(420, 46));
        return b;
    }

    /**
     * Erstellt einen kleineren Button für die Top-Leiste.
     *
     * @param text Button-Text
     * @return Button
     */
    private JButton makeButtonSmall(String text) {
        JButton b = new JButton(text);
        b.setFont(ArcadeTheme.font(14f));
        b.setFocusPainted(false);
        b.setBackground(new Color(10, 30, 40));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 220, 200, 180), 2),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        return b;
    }

    /**
     * Zeichnet den Hintergrund (Gradient + Grid + Rahmen), passend zum restlichen UI.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();

        // Farbverlauf
        g2.setPaint(new GradientPaint(0, 0, new Color(5, 10, 25), 0, h, new Color(0, 40, 50)));
        g2.fillRect(0, 0, w, h);

        // Grid-Linien
        g2.setColor(new Color(0, 220, 200, 20));
        for (int y = 0; y < h; y += 20) g2.drawLine(0, y, w, y);
        for (int x = 0; x < w; x += 20) g2.drawLine(x, 0, x, h);

        // Rahmen
        g2.setColor(new Color(0, 220, 200, 120));
        g2.drawRect(10, 10, w - 20, h - 20);
    }
}
