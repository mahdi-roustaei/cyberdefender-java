package de.hsh.cyberdefender;

import javax.swing.*;
import java.awt.*;

/**
 * Hauptmenü des Spiels.
 * Von hier aus kann ein neues Spiel gestartet werden oder zu Highscore/Settings gewechselt werden.
 */
public class MainMenuPanel extends JPanel {

    private final MainFrame mainFrame;

    /**
     * Erstellt das Hauptmenü.
     *
     * @param mainFrame Referenz auf das Hauptfenster (zum Screen-Wechsel)
     */
    public MainMenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setOpaque(true);

        // Kopfbereich mit Titel
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(16, 16, 10, 16));

        JLabel title = new JLabel("MAIN MENU");
        title.setForeground(new Color(0, 220, 200));
        title.setFont(ArcadeTheme.font(26f));
        top.add(title, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);

        // Inhalt (Box in der Mitte)
        JPanel content = new JPanel();
        content.setOpaque(true);
        content.setBackground(new Color(0, 0, 0, 170));
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 220, 200, 120), 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JButton playBtn = makeButton("SPIELEN");
        JButton highBtn = makeButton("HIGH SCORE TABELLE");
        JButton setBtn  = makeButton("EINSTELLUNGEN");
        JButton howBtn  = makeButton("HOW TO PLAY");
        JButton exitBtn = makeButton("BEENDEN");

        // Neues Spiel: garantiert frischer Run (Score/Lives/etc. zurückgesetzt)
        playBtn.addActionListener(e -> mainFrame.startNewGame());

        highBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.SCREEN_HIGHSCORE));
        setBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.SCREEN_SETTINGS));

        // Erklärung der Steuerung im Theme-Dialog
        howBtn.addActionListener(e -> UiDialogs.showInfo(
                this,
                "HOW TO PLAY",
                "STEUERUNG\n\n" +
                        "W / A / S / D  = Bewegen\n" +
                        "LEERTASTE      = Schießen\n" +
                        "P              = Pause\n" +
                        "1 / 2 / 3      = Antwort bei Fragen\n" +
                        "ESC            = Zurück ins Menü\n\n" +
                        "ZIEL\n" +
                        "Besiege Gegner, sammle Punkte\n" +
                        "und löse IT-Security Fragen."
        ));

        // Beenden ohne Absturz
        exitBtn.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) {
                w.dispose();
            } else {
                System.exit(0);
            }
        });

        content.add(playBtn);
        content.add(Box.createVerticalStrut(14));
        content.add(highBtn);
        content.add(Box.createVerticalStrut(14));
        content.add(setBtn);
        content.add(Box.createVerticalStrut(14));
        content.add(howBtn);
        content.add(Box.createVerticalStrut(14));
        content.add(exitBtn);

        // Wrapper, damit die Box mittig sitzt
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(content);

        add(center, BorderLayout.CENTER);
    }

    /**
     * Erstellt einen Button im Arcade-Stil.
     *
     * @param text Button-Text
     * @return fertig gestylter Button
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Gleicher Hintergrundstil wie die anderen UI-Panels
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();

        g2.setPaint(new GradientPaint(
                0, 0, new Color(5, 10, 25),
                0, h, new Color(0, 40, 50)
        ));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(0, 220, 200, 20));
        for (int y = 0; y < h; y += 20) g2.drawLine(0, y, w, y);
        for (int x = 0; x < w; x += 20) g2.drawLine(x, 0, x, h);

        g2.setColor(new Color(0, 220, 200, 120));
        g2.drawRect(10, 10, w - 20, h - 20);
    }
}
