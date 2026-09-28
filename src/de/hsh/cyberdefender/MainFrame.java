package de.hsh.cyberdefender;

import javax.swing.*;
import java.awt.*;

/**
 * Hauptfenster der Anwendung.
 * Verwendet ein {@link CardLayout}, um zwischen Menü, Spiel, Highscore und Settings zu wechseln.
 */
public class MainFrame extends JFrame {

    // Screen-Keys (keine Magic-Strings im Code verteilen)
    public static final String SCREEN_MENU = "menu";
    public static final String SCREEN_GAME = "game";
    public static final String SCREEN_HIGHSCORE = "highscore";
    public static final String SCREEN_SETTINGS = "settings";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final MainMenuPanel menuPanel;
    private final GamePanel gamePanel;
    private final HighscorePanel highscorePanel;
    private final SettingsPanel settingsPanel;

    private String currentScreen = null;

    public MainFrame() {
        super("CyberDefender");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Fenster-Einstellungen
        setSize(1200, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setResizable(true);

        // Panels erzeugen (einmalig)
        menuPanel = new MainMenuPanel(this);
        gamePanel = new GamePanel(this);
        highscorePanel = new HighscorePanel(this);
        settingsPanel = new SettingsPanel(this);

        // Cards registrieren
        cardPanel.add(menuPanel, SCREEN_MENU);
        cardPanel.add(gamePanel, SCREEN_GAME);
        cardPanel.add(highscorePanel, SCREEN_HIGHSCORE);
        cardPanel.add(settingsPanel, SCREEN_SETTINGS);

        setContentPane(cardPanel);

        // Start im Menü (Spiel startet NICHT automatisch)
        showScreen(SCREEN_MENU);

        setVisible(true);
    }

    /**
     * Zeigt einen Screen anhand seines Keys an.
     * Achtung: Diese Methode setzt das Spiel NICHT zurück.
     * Für einen frischen Run bitte {@link #startNewGame()} verwenden.
     */
    public void showScreen(String name) {
        if (name == null || name.isBlank()) return;
        if (name.equals(currentScreen)) return;

        // Wenn wir das Spiel verlassen: Timer stoppen (wichtig für Stabilität)
        if (SCREEN_GAME.equals(currentScreen)) {
            gamePanel.stopGame();
        }

        // Highscore vor dem Anzeigen aktualisieren
        if (SCREEN_HIGHSCORE.equals(name)) {
            highscorePanel.refresh();
        }

        // Screen anzeigen
        cardLayout.show(cardPanel, name);
        currentScreen = name;

        // Wenn wir ins Spiel wechseln: Timer starten und Fokus setzen
        if (SCREEN_GAME.equals(name)) {
            gamePanel.startGame();
            SwingUtilities.invokeLater(() -> {
                gamePanel.requestFocusInWindow();
                gamePanel.grabFocus();
            });
        }
    }

    /**
     * Startet ein komplett neues Spiel (Score/Lives/Gegner etc. werden zurückgesetzt)
     * und wechselt danach in den Game-Screen.
     * Diese Methode sollte vom "Spielen"-Button genutzt werden.
     */
    public void startNewGame() {
        gamePanel.resetGame();
        showScreen(SCREEN_GAME);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
