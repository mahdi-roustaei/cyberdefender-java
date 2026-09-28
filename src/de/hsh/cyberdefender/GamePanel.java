package de.hsh.cyberdefender;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Zentrales Spielfeld:
 * - Spiel-Loop (Timer)
 * - Zeichnen (Rendering)
 * - Eingaben (Tastatur)
 * - Gegner/Projektile/PowerUps
 * - Challenges und Game-Over
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    /** Wenn aktiv, wird automatisch auf den nächsten Gegner gezielt. */
    private boolean autoAim = true;

    /** SPACE: Ein Schuss pro Tastendruck (kein Dauerfeuer durch Halten). */
    private boolean spaceDown = false;

    /** Soundverwaltung für Musik und Soundeffekte. */
    private final SoundManager sound = new SoundManager();

    /** Zeitsteuerung für Challenges. */
    private long lastChallengeMs = 0L;
    private long minChallengeIntervalMs = 18_000L;
    private int killsToNextChallenge = 8;
    private int enemiesHitCounter = 0;

    /** Kurze Einblendung (Toast) unten im Bild. */
    private String toastText = null;
    private long toastUntilMs = 0L;

    /** Referenz auf das Hauptfenster, um zwischen Screens zu wechseln. */
    private final MainFrame mainFrame;

    /** Timer für den Spiel-Loop (ca. 60 FPS). */
    private final Timer timer;

    /** Laufstatus des Spiels. */
    private boolean running = false;

    /** Pausenstatus. */
    private boolean paused = false;

    /** Wenn true, ist gerade ein Challenge-Dialog aktiv. */
    private boolean challengeActive = false;

    /** Spielfigur. */
    private Player player;

    /** Projektile, Gegner und PowerUps. */
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();

    /** Fassade/Session für Score/Lives/Challenges. */
    private GameFacade gameFacade;
    private GameSession session;

    /** Hintergrundbild. */
    private Image backgroundImage;

    private long lastShotMs = 0L;
    private long shotCooldownMs = 250L; // 120ms

    private long lastDamageMs = 0L;
    private long damageCooldownMs = 600L;



    public GamePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setBackground(Color.BLACK);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        loadBackground();

        rebuildSessionAndFacade();
        player = new Player(100, 300);
        initEnemies();

        // Challenge-Steuerung initialisieren
        scheduleNextChallenge();
        lastChallengeMs = 0L;
        enemiesHitCounter = 0;

        timer = new Timer(16, this); // ca. 60 FPS
        // Timer wird NICHT im Konstruktor gestartet.
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
            grabFocus();
        });
    }

    // -------------------- Start / Stop --------------------

    /** Startet das Spiel (Timer + Musik). */
    public void startGame() {
        if (running) return;

        running = true;
        paused = false;

        timer.start();
        sound.playMusicLoop("/de/hsh/cyberdefender/sounds/bg_loop.wav", -18f);

        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
            grabFocus();
        });
    }

    /** Stoppt das Spiel (Timer + Musik) und setzt Eingaben zurück. */
    public void stopGame() {
        running = false;
        timer.stop();
        sound.stopMusic();

        paused = false;

        // Eingaben zurücksetzen, damit keine Taste "hängen bleibt"
        spaceDown = false;
        resetMovementKeys();
    }

    // -------------------- Reset / Neues Spiel --------------------

    /** Setzt den kompletten Spielzustand zurück. */
    public void resetGame() {
        stopGame();

        rebuildSessionAndFacade();

        projectiles.clear();
        enemies.clear();
        powerUps.clear();

        player = new Player(100, 300);
        initEnemies();

        paused = false;
        challengeActive = false;

        // Challenge-Steuerung zurücksetzen
        lastChallengeMs = 0L;
        enemiesHitCounter = 0;
        scheduleNextChallenge();

        spaceDown = false;
        resetMovementKeys();

        repaint();
    }

    /** Erstellt Session und Fassade neu (Score/Lives/Challenges). */
    private void rebuildSessionAndFacade() {
        session = new GameSession();
        ChallengeController cc = new ChallengeController(session);
        GameController gc = new GameController(session, cc);
        gameFacade = new GameFacade(gc);
    }

    // -------------------- Challenge-Steuerung --------------------

    /** Plant die nächste Challenge abhängig von der Schwierigkeit. */
    private void scheduleNextChallenge() {
        Difficulty diff = GameSettings.getDifficulty();

        int minKills = switch (diff) {
            case EASY -> 10;
            case MEDIUM -> 8;
            case HARD -> 6;
        };
        int maxKills = switch (diff) {
            case EASY -> 14;
            case MEDIUM -> 12;
            case HARD -> 10;
        };

        killsToNextChallenge = minKills + (int) (Math.random() * (maxKills - minKills + 1));

        minChallengeIntervalMs = switch (diff) {
            case EASY -> 22_000L;
            case MEDIUM -> 18_000L;
            case HARD -> 14_000L;
        };
    }

    /** Prüft, ob seit der letzten Challenge genug Zeit vergangen ist. */
    private boolean canTriggerChallengeNow() {
        long now = System.currentTimeMillis();
        return (now - lastChallengeMs) >= minChallengeIntervalMs;
    }

    // -------------------- Hintergrund --------------------

    /** Lädt den Hintergrund aus den Ressourcen. */
    private void loadBackground() {
        try {
            var url = getClass().getResource("/de/hsh/cyberdefender/background.png");
            if (url != null) {
                backgroundImage = ImageIO.read(url);
            } else {
                backgroundImage = null;
                System.err.println("Hintergrund nicht gefunden: /de/hsh/cyberdefender/background.png");
            }
        } catch (IOException e) {
            backgroundImage = null;
            e.printStackTrace();
        }
    }

    // -------------------- Gegner --------------------

    /** Erstellt eine Grundmenge an Gegnern je nach Schwierigkeit. */
    private void initEnemies() {
        Difficulty diff = GameSettings.getDifficulty();
        int w = getWidth() > 0 ? getWidth() : 1200;

        enemies.clear();

        switch (diff) {
            case EASY -> {
                enemies.add(new Enemy(Enemy.Type.VIRUS, w - 120, 200));
                enemies.add(new Enemy(Enemy.Type.HACKER, w - 120, 320));
                enemies.add(new Enemy(Enemy.Type.MALWARE, w - 120, 120));
            }
            case MEDIUM -> {
                enemies.add(new Enemy(Enemy.Type.HACKER, w - 120, 200));
                enemies.add(new Enemy(Enemy.Type.VIRUS, w - 120, 100));
                enemies.add(new Enemy(Enemy.Type.MALWARE, w - 120, 300));
                enemies.add(new Enemy(Enemy.Type.VIRUS, w - 120, 180));
            }
            case HARD -> {
                enemies.add(new Enemy(Enemy.Type.HACKER, w - 120, 150));
                enemies.add(new Enemy(Enemy.Type.HACKER, w - 120, 250));
                enemies.add(new Enemy(Enemy.Type.VIRUS, w - 120, 100));
                enemies.add(new Enemy(Enemy.Type.MALWARE, w - 120, 300));
                enemies.add(new Enemy(Enemy.Type.MALWARE, w - 120, 200));
            }
        }
    }

    /**
     * Erhöht die Gegneranzahl mit steigendem Score.
     * Pro 150 Punkte kommt maximal ein zusätzlicher Gegner dazu (bis zu einem Limit).
     */
    private void adjustEnemyCountBasedOnScore(Difficulty diff) {
        int baseCount = switch (diff) {
            case EASY -> 3;
            case MEDIUM -> 4;
            case HARD -> 5;
        };

        int score = session.getScore();
        int extra = score / 150;
        int target = Math.min(40, baseCount + extra);

        int missing = target - enemies.size();

        // Begrenzung pro Tick, damit keine Ruckler entstehen
        int maxSpawnPerTick = 2;
        int spawnNow = Math.min(missing, maxSpawnPerTick);

        for (int i = 0; i < spawnNow; i++) {
            spawnExtraEnemyInsideEdge();
        }
    }

    /** Spawnt einen zusätzlichen Gegner am rechten Rand innerhalb des Spielfelds. */
    private void spawnExtraEnemyInsideEdge() {
        int pw = getWidth() > 0 ? getWidth() : 1200;
        int ph = getHeight() > 0 ? getHeight() : 700;

        Enemy.Type type = Enemy.Type.values()[(int) (Math.random() * Enemy.Type.values().length)];

        int margin = 20;
        int x = pw - 80;
        int y = margin + (int) (Math.random() * Math.max(1, ph - 2 * margin - 80));

        enemies.add(new Enemy(type, x, y));
    }

    // -------------------- Zeichnen --------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();

        // Hintergrund zeichnen
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, w, h, null);
        } else {
            g2.setColor(new Color(5, 10, 25));
            g2.fillRect(0, 0, w, h);
        }

        // Entities zeichnen
        player.draw(g2);
        for (Projectile p : projectiles) p.draw(g2);
        for (Enemy e : enemies) e.draw(g2);
        for (PowerUp p : powerUps) p.draw(g2);

        // HUD
        g2.setFont(ArcadeTheme.font(14f));
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(10, 10, 300, 55, 10, 10);
        g2.setColor(new Color(0, 220, 200, 180));
        g2.drawRoundRect(10, 10, 300, 55, 10, 10);

        g2.setColor(Color.WHITE);
        g2.drawString("Leben: " + session.getLives(), 20, 32);
        g2.drawString("Punkte: " + session.getScore(), 20, 52);

        // Toast
        if (toastText != null && System.currentTimeMillis() < toastUntilMs) {
            g2.setFont(ArcadeTheme.font(16f));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(toastText);

            int boxW = Math.min(w - 40, tw + 40);
            int boxH = 46;
            int x = (w - boxW) / 2;
            int y = h - 90;

            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRoundRect(x, y, boxW, boxH, 12, 12);

            g2.setColor(new Color(0, 220, 200, 180));
            g2.drawRoundRect(x, y, boxW, boxH, 12, 12);

            g2.setColor(Color.WHITE);
            g2.drawString(toastText, x + (boxW - tw) / 2, y + 30);
        } else if (toastText != null) {
            toastText = null;
        }

        // Pausen-Overlay
        if (paused) {
            g2.setFont(ArcadeTheme.font(24f));
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(w / 2 - 160, h / 2 - 60, 320, 120, 12, 12);

            g2.setColor(new Color(0, 220, 200));
            g2.drawRoundRect(w / 2 - 160, h / 2 - 60, 320, 120, 12, 12);

            g2.setColor(Color.WHITE);
            g2.drawString("PAUSE", w / 2 - 70, h / 2 + 10);

            g2.setFont(ArcadeTheme.font(12f));
            g2.drawString("Drücke P zum Fortsetzen", w / 2 - 115, h / 2 + 35);
        }
    }

    // -------------------- Spiel-Loop --------------------

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running) return;
        if (challengeActive) return;
        if (paused) return;

        Difficulty diff = GameSettings.getDifficulty();

        player.update();
        player.clampToBounds(0, 0, getWidth(), getHeight());

        // Zielpunkt für Gegner, die dem Spieler folgen
        Rectangle pb = player.getBounds();
        Enemy.setTarget(pb.x + pb.width / 2, pb.y + pb.height / 2);

        // Kollision Spieler <-> Gegner
        int pcx = player.getBounds().x + player.getBounds().width / 2;
        int pcy = player.getBounds().y + player.getBounds().height / 2;
        int pr  = Math.max(10, Math.min(player.getBounds().width, player.getBounds().height) / 2);

        long nowDmg = System.currentTimeMillis();

        for (Enemy enemy : enemies) {
            int ecx = enemy.getCenterX();
            int ecy = enemy.getCenterY();
            int er  = 14;

            if (circleHit(pcx, pcy, pr, ecx, ecy, er)) {

                // Schaden-Cooldown
                if (nowDmg - lastDamageMs < damageCooldownMs) {
                    continue;
                }
                lastDamageMs = nowDmg;

                int damage = enemy.getDamage();
                if (diff == Difficulty.EASY) damage = Math.max(1, damage - 1);
                if (diff == Difficulty.HARD) damage += 1;

                gameFacade.loseLife(damage);
                enemy.hit(getWidth(), getHeight());

                if (gameFacade.isGameOver()) {
                    handleGameOver();
                    return;
                }
            }
        }


        // Projektile bewegen und Treffer prüfen
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update();

            if (!p.isAlive()) {
                it.remove();
                continue;
            }

            boolean hit = false;
            for (Enemy enemy : enemies) {
                if (p.collidesWith(enemy)) {

                    int points = enemy.getPoints();
                    if (diff == Difficulty.EASY) points = (int) (points * 0.8);
                    if (diff == Difficulty.HARD) points = (int) (points * 1.2);

                    gameFacade.addScore(points);

                    enemiesHitCounter++;

                    int cx = enemy.getCenterX();
                    int cy = enemy.getCenterY();

                    enemy.hit(getWidth(), getHeight());
                    spawnPowerUp(cx, cy, diff);

                    // Challenge: abhängig von Kills + Zeitabstand
                    if (enemiesHitCounter >= killsToNextChallenge && canTriggerChallengeNow()) {
                        lastChallengeMs = System.currentTimeMillis();
                        enemiesHitCounter = 0;
                        scheduleNextChallenge();
                        triggerChallenge();
                    }

                    hit = true;
                    break;
                }
            }
            if (hit) it.remove();
        }

        // Gegner bewegen
        for (Enemy enemy : enemies) enemy.update(getWidth(), getHeight());

        // Mit steigendem Score mehr Gegner
        adjustEnemyCountBasedOnScore(diff);

        // PowerUps einsammeln
        Iterator<PowerUp> puIt = powerUps.iterator();
        while (puIt.hasNext()) {
            PowerUp pu = puIt.next();
            if (player.getBounds().intersects(pu.getBounds())) {
                applyPowerUp(pu);
                puIt.remove();
            }
        }

        repaint();
    }

    // -------------------- Schießen / Auto-Ziel --------------------

    /** Erzeugt ein Projektil (optional mit Auto-Ziel). */
    private void shoot() {
        Rectangle pb = player.getBounds();
        int startX = pb.x + pb.width / 2;
        int startY = pb.y + pb.height / 2;

        double bulletSpeed = 10.0;
        double dx = 1, dy = 0;

        sound.playSfx("/de/hsh/cyberdefender/sounds/shot.wav", -8f);

        if (autoAim) {
            Enemy target = getNearestEnemy();
            if (target != null) {
                double tx = target.getCenterX();
                double ty = target.getCenterY();

                dx = tx - startX;
                dy = ty - startY;

                double len = Math.sqrt(dx * dx + dy * dy);
                if (len > 0.0001) {
                    dx /= len;
                    dy /= len;
                }
            }
        }

        double vx = dx * bulletSpeed;
        double vy = dy * bulletSpeed;

        projectiles.add(new Projectile(startX, startY, vx, vy));
    }

    /** Liefert den nächstgelegenen Gegner zum Spieler (oder null). */
    private Enemy getNearestEnemy() {
        if (enemies.isEmpty()) return null;

        Rectangle pb = player.getBounds();
        int px = pb.x + pb.width / 2;
        int py = pb.y + pb.height / 2;

        Enemy best = null;
        double bestDist2 = Double.MAX_VALUE;

        for (Enemy e : enemies) {
            int ex = e.getCenterX();
            int ey = e.getCenterY();

            double dx = ex - px;
            double dy = ey - py;
            double d2 = dx * dx + dy * dy;

            if (d2 < bestDist2) {
                bestDist2 = d2;
                best = e;
            }
        }
        return best;
    }

    // -------------------- PowerUps --------------------

    /** Spawnt mit einer Wahrscheinlichkeit ein PowerUp am Trefferpunkt. */
    private void spawnPowerUp(int x, int y, Difficulty diff) {
        double chance = 0.30;
        if (diff == Difficulty.EASY) chance += 0.15;
        if (diff == Difficulty.HARD) chance -= 0.15;

        if (Math.random() < chance) {
            PowerUp.Type type = Math.random() < 0.5 ? PowerUp.Type.LIFE : PowerUp.Type.SCORE;
            powerUps.add(new PowerUp(x - 10, y - 10, type));
        }
    }

    /** Wendet ein PowerUp an und zeigt eine kurze Meldung. */
    private void applyPowerUp(PowerUp powerUp) {
        if (powerUp.getType() == PowerUp.Type.LIFE) {
            gameFacade.addLife();
            showToast("Extra-Leben!");
        } else {
            gameFacade.addScore(300);
            showToast("+300 Punkte!");
        }

        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
            grabFocus();
        });
    }

    /** Zeigt einen kurzen Hinweis unten im Bild. */
    private void showToast(String text) {
        toastText = text;
        toastUntilMs = System.currentTimeMillis() + 1200;
    }

    // -------------------- Challenge --------------------

    /** Setzt alle Bewegungs-Tasten zurück. */
    private void resetMovementKeys() {
        player.setUp(false);
        player.setDown(false);
        player.setLeft(false);
        player.setRight(false);
    }

    /** Startet einen Challenge-Dialog (Spiel stoppt währenddessen). */
    private void triggerChallenge() {
        challengeActive = true;

        stopGame();

        Challenge challenge = gameFacade.nextChallenge();
        String[] options = challenge.getAnswers();

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "IT-Security-Challenge",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0, 210));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 220, 200, 180), 2),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel q = new JLabel("<html>" + challenge.getQuestion() + "</html>");
        q.setForeground(new Color(0, 220, 200));
        q.setFont(ArcadeTheme.font(16f));
        q.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(q);

        panel.add(Box.createVerticalStrut(10));

        JLabel hint = new JLabel("Drücke 1 / 2 / 3 für die Antwort");
        hint.setForeground(Color.WHITE);
        hint.setFont(ArcadeTheme.font(12f));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(hint);

        panel.add(Box.createVerticalStrut(10));

        for (int i = 0; i < options.length; i++) {
            JLabel opt = new JLabel((i + 1) + ": " + options[i]);
            opt.setForeground(Color.WHITE);
            opt.setFont(ArcadeTheme.font(14f));
            opt.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(opt);
        }

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        final int[] choiceHolder = new int[]{-1};

        JRootPane root = dialog.getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        for (int i = 0; i < options.length && i < 9; i++) {
            final int idx = i;
            String action = "ans" + i;

            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_1 + i, 0), action);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1 + i, 0), action);

            am.put(action, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    choiceHolder[0] = idx;
                    dialog.dispose();
                }
            });
        }

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        am.put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                choiceHolder[0] = -1;
                dialog.dispose();
            }
        });

        dialog.setVisible(true);

        int choice = choiceHolder[0];
        boolean correct = gameFacade.submitChallengeAnswer(challenge, choice);

        if (correct) {
            showToast("Richtig! +" + challenge.getRewardScore() + " Punkte");
        } else {
            showToast("Falsch! -1 Leben");
            gameFacade.loseLife();
        }

        if (gameFacade.isGameOver()) {
            handleGameOver();
            return;
        }

        challengeActive = false;

        resetMovementKeys();
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
            grabFocus();
        });

        startGame();
    }

    private boolean circleHit(int ax, int ay, int ar, int bx, int by, int br) {
        int dx = ax - bx;
        int dy = ay - by;
        int r = ar + br;
        return (dx * dx + dy * dy) <= (r * r);
    }


    // -------------------- Game Over --------------------

    /** Beendet das Spiel, speichert Highscore und springt zurück ins Menü. */
    private void handleGameOver() {
        stopGame();

        sound.playSfx("/de/hsh/cyberdefender/sounds/game_over.wav", -6f);

        int finalScore = session.getScore();
        String name = JOptionPane.showInputDialog(
                this,
                "Spiel vorbei!\nPunkte: " + finalScore + "\nName für Highscore:",
                "Game Over",
                JOptionPane.PLAIN_MESSAGE
        );

        if (name == null) name = "";
        name = name.trim();
        if (name.isEmpty()) name = "Player";

        HighscoreManager.addScore(name, finalScore);
        mainFrame.showScreen("menu");
    }

    // -------------------- Tastatur --------------------

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();

        // Pause umschalten
        if (k == KeyEvent.VK_P) {
            paused = !paused;
            resetMovementKeys();
            repaint();
            return;
        }
        if (paused) return;

        // Bewegung
        if (k == KeyEvent.VK_W) player.setUp(true);
        if (k == KeyEvent.VK_S) player.setDown(true);
        if (k == KeyEvent.VK_A) player.setLeft(true);
        if (k == KeyEvent.VK_D) player.setRight(true);

        // Ein Schuss pro Tastendruck
        if (k == KeyEvent.VK_SPACE) {
            long now = System.currentTimeMillis();
            if (!spaceDown && (now - lastShotMs) >= shotCooldownMs) {
                spaceDown = true;
                lastShotMs = now;
                shoot();
            }
        }


        // Zurück ins Menü
        if (k == KeyEvent.VK_ESCAPE) {
            mainFrame.showScreen("menu");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();

        if (k == KeyEvent.VK_W) player.setUp(false);
        if (k == KeyEvent.VK_S) player.setDown(false);
        if (k == KeyEvent.VK_A) player.setLeft(false);
        if (k == KeyEvent.VK_D) player.setRight(false);

        // Danach ist der nächste Schuss wieder erlaubt
        if (k == KeyEvent.VK_SPACE) {
            spaceDown = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Nicht benötigt
    }
}
