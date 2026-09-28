package de.hsh.cyberdefender;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class Enemy {

    /** Gegnertypen mit unterschiedlichen Werten (Schaden, Punkte, Geschwindigkeit). */
    public enum Type { HACKER, VIRUS, MALWARE }

    /** Aktuelles Ziel (Spielerposition) für alle Gegner. */
    private static int targetX = 0;
    private static int targetY = 0;

    /**
     * Setzt das aktuelle Ziel für alle Gegner (z.B. Mittelpunkt des Spielers).
     *
     * @param x Ziel-X
     * @param y Ziel-Y
     */
    public static void setTarget(int x, int y) {
        targetX = x;
        targetY = y;
    }

    private final Type type;
    private int x, y;

    /** Standardgröße; wird nach dem Laden des Sprites ggf. angepasst. */
    private int w = 48;
    private int h = 48;

    private int damage;
    private int points;
    private double speed;

    private Image sprite;

    /**
     * Erstellt einen Gegner an einer Startposition.
     * Je nach Typ werden Werte gesetzt und das passende Sprite geladen.
     *
     * @param type Gegnertyp
     * @param x Start-X
     * @param y Start-Y
     */
    public Enemy(Type type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;

        // Werte pro Gegnertyp setzen und Sprite laden
        switch (type) {
            case HACKER -> {
                damage = 1;
                points = 200;
                speed = 2.2;
                sprite = load("/de/hsh/cyberdefender/enemy_hacker.png");
            }
            case VIRUS -> {
                damage = 1;
                points = 150;
                speed = 2.8;
                sprite = load("/de/hsh/cyberdefender/enemy_virus.png");
            }
            case MALWARE -> {
                damage = 2;
                points = 250;
                speed = 2.0;
                sprite = load("/de/hsh/cyberdefender/enemy_malware.png");
            }
        }

        // Sprite-Größe übernehmen (oder auf sinnvolle Größe begrenzen)
        if (sprite != null) {
            w = sprite.getWidth(null);
            h = sprite.getHeight(null);

            // Falls Sprite zu groß ist, wird es auf 48x48 gezeichnet (Performance + Optik)
            if (w > 96 || h > 96) {
                w = 48;
                h = 48;
            }
        }
    }

    /**
     * Lädt ein Bild aus den Ressourcen.
     *
     * @param path Klassenpfad zur PNG-Datei
     * @return geladenes Image oder null, falls nicht gefunden/fehlerhaft
     */
    private Image load(String path) {
        try {
            var url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Enemy Sprite nicht gefunden: " + path);
                return null;
            }
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Bewegt den Gegner in Richtung des aktuellen Ziels (Spieler).
     * Danach wird die Position innerhalb der Welt begrenzt (clamp).
     *
     * @param worldW Breite der Welt (Panel)
     * @param worldH Höhe der Welt (Panel)
     */
    public void update(int worldW, int worldH) {
        if (worldW <= 0 || worldH <= 0) return;

        int cx = getCenterX();
        int cy = getCenterY();

        double dx = targetX - cx;
        double dy = targetY - cy;

        // Richtung normalisieren, damit speed konstant bleibt
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len > 0.0001) {
            dx /= len;
            dy /= len;
        }

        x += (int) Math.round(dx * speed);
        y += (int) Math.round(dy * speed);

        clampToWorld(worldW, worldH);
    }

    /**
     * Begrenzt die Gegnerposition so, dass er vollständig im sichtbaren Bereich bleibt.
     *
     * @param worldW Breite der Welt
     * @param worldH Höhe der Welt
     */
    private void clampToWorld(int worldW, int worldH) {
        int minX = 0;
        int minY = 0;
        int maxX = Math.max(0, worldW - w);
        int maxY = Math.max(0, worldH - h);

        if (x < minX) x = minX;
        if (y < minY) y = minY;
        if (x > maxX) x = maxX;
        if (y > maxY) y = maxY;
    }

    /**
     * Wird aufgerufen, wenn der Gegner getroffen wurde oder den Spieler berührt hat.
     * Setzt den Gegner an eine zufällige Kante (oben/rechts/unten/links) innerhalb der Welt.
     *
     * @param worldW Breite der Welt
     * @param worldH Höhe der Welt
     */
    public void hit(int worldW, int worldH) {
        if (worldW <= 0 || worldH <= 0) return;

        int side = (int) (Math.random() * 4); // 0 oben, 1 rechts, 2 unten, 3 links
        int margin = 10;

        int maxX = Math.max(0, worldW - w - margin);
        int maxY = Math.max(0, worldH - h - margin);

        switch (side) {
            case 0 -> { // oben
                x = margin + (int) (Math.random() * Math.max(1, maxX));
                y = margin;
            }
            case 1 -> { // rechts
                x = worldW - w - margin;
                y = margin + (int) (Math.random() * Math.max(1, maxY));
            }
            case 2 -> { // unten
                x = margin + (int) (Math.random() * Math.max(1, maxX));
                y = worldH - h - margin;
            }
            default -> { // links
                x = margin;
                y = margin + (int) (Math.random() * Math.max(1, maxY));
            }
        }

        clampToWorld(worldW, worldH);
    }

    /**
     * Zeichnet den Gegner als Sprite (oder als roten Platzhalter, falls Sprite fehlt).
     *
     * @param g2 Graphics2D Kontext
     */
    public void draw(Graphics2D g2) {
        if (sprite != null) {
            g2.drawImage(sprite, x, y, w, h, null);
        } else {
            g2.setColor(Color.RED);
            g2.fillOval(x, y, w, h);
        }
    }

    /**
     * @return Rechteck für Kollisionen
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }

    /** @return Mittelpunkt-X des Gegners */
    public int getCenterX() {
        return x + w / 2;
    }

    /** @return Mittelpunkt-Y des Gegners */
    public int getCenterY() {
        return y + h / 2;
    }

    /** @return Schaden, den dieser Gegner verursacht */
    public int getDamage() {
        return damage;
    }

    /** @return Punkte, die man beim Treffen/Erledigen bekommt */
    public int getPoints() {
        return points;
    }

    /** @return Gegnertyp */
    public Type getType() {
        return type;
    }
}
