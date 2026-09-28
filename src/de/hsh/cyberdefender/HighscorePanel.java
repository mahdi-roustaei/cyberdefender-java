package de.hsh.cyberdefender;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HighscorePanel extends JPanel {

    private final MainFrame mainFrame;
    private final JTextArea textArea;

    public HighscorePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(16, 16, 10, 16));

        JLabel title = new JLabel("HIGHSCORES");
        title.setForeground(new Color(0, 220, 200));
        title.setFont(ArcadeTheme.font(26f));
        top.add(title, BorderLayout.WEST);

        JButton back = new JButton("BACK");
        back.setFont(ArcadeTheme.font(14f));
        back.setFocusPainted(false);
        back.setBackground(new Color(10, 30, 40));
        back.setForeground(Color.WHITE);
        back.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 220, 200, 180), 2),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        back.addActionListener(e -> mainFrame.showScreen("menu"));
        top.add(back, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(ArcadeTheme.font(14f));
        textArea.setForeground(Color.WHITE);
        textArea.setBackground(new Color(0, 0, 0, 170));
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 220, 200, 120), 2),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    /** Aktualisiert die Anzeige. */
    public void refresh() {
        var list = HighscoreManager.getHighscores();

        StringBuilder sb = new StringBuilder();
        sb.append("RANK  NAME               SCORE\n");
        sb.append("--------------------------------\n");

        if (list.isEmpty()) {
            sb.append("\nNo highscores yet.\nPlay a game first!\n");
        } else {
            int rank = 1;
            for (HighscoreManager.Entry e : list) {
                sb.append(String.format("%02d) %-18s %d\n", rank, e.name(), e.score()));
                rank++;
            }
        }

        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);
    }


    /**
     * Liest Highscores robust aus HighscoreManager, ohne dass du extra Methoden bauen musst.
     * Unterstützt:
     * - getHighscores(): List<String>
     * - getScores(): List<String>
     * - loadScores(): List<String>
     * - readScores(): List<String>
     * - oder als Fallback: toString() der Liste
     */
    @SuppressWarnings("unchecked")
    private List<String> tryReadHighscores() {
        List<String> result = new ArrayList<>();

        // Versuch: bekannte Methodennamen (statisch)
        String[] methods = {"getHighscores", "getScores", "loadScores", "readScores"};

        for (String m : methods) {
            try {
                Method method = HighscoreManager.class.getMethod(m);
                Object out = method.invoke(null); // static
                if (out instanceof List<?> list) {
                    for (Object o : list) {
                        if (o != null) result.add(o.toString());
                    }
                    return result;
                }
            } catch (Exception ignored) {
            }
        }

        // Fallback: wenigstens irgendwas zeigen
        result.add("(HighscoreManager: keine passende Methode gefunden)");
        result.add("Öffne HighscoreManager und sag mir wie die Methode heißt.");
        return result;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();

        g2.setPaint(new GradientPaint(0, 0, new Color(5, 10, 25), 0, h, new Color(0, 40, 50)));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(0, 220, 200, 20));
        for (int y = 0; y < h; y += 20) g2.drawLine(0, y, w, y);
        for (int x = 0; x < w; x += 20) g2.drawLine(x, 0, x, h);
    }
}
