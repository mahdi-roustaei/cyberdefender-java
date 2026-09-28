package de.hsh.cyberdefender;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PowerUp {

    /** Typen von PowerUps im Spiel. */
    public enum Type { LIFE, SCORE }

    /** Zwischengespeicherte Sprites (werden einmalig geladen). */
    private static BufferedImage spriteLife;
    private static BufferedImage spriteScore;

    static {
        // Sprites einmal beim Laden der Klasse initialisieren (spart Performance)
        spriteLife = loadSprite("powerup_life.png");
        spriteScore = loadSprite("powerup_score.png");
    }

    /**
     * Lädt ein Sprite zuerst als Resource (wichtig für JAR),
     * und nutzt ansonsten einen Fallback über den Projektpfad (für Entwicklung).
     *
     * @param fileName Dateiname des Sprites
     * @return geladenes Bild oder null, wenn nicht gefunden
     */
    private static BufferedImage loadSprite(String fileName) {
        BufferedImage img = null;

        // Erst versuchen, das Sprite als Resource zu laden
        try {
            var url = PowerUp.class.getResource(fileName);
            if (url != null) img = ImageIO.read(url);
        } catch (IOException ignored) { }

        // Fallback: direkt aus dem Dateisystem (praktisch während der Entwicklung)
        if (img == null) {
            try {
                File f = new File("src/de/hsh/cyberdefender/" + fileName);
                if (f.exists()) img = ImageIO.read(f);
                else System.err.println("PowerUp-Sprite nicht gefunden: " + f.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Fehler beim Laden von " + fileName + ": " + e.getMessage());
            }
        }
        return img;
    }

    /** Position des PowerUps im Spielfeld. */
    private int x, y;

    /** Feste Zeichen-Größe (unabhängig von der PNG-Größe). */
    private int width = 32;
    private int height = 32;

    /** Welcher Effekt ausgelöst wird (Extra-Leben oder Score). */
    private final Type type;

    /**
     * Erstellt ein PowerUp an einer Position.
     *
     * @param x X-Position
     * @param y Y-Position
     * @param type PowerUp-Typ
     */
    public PowerUp(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Zeichnet das PowerUp (Sprite oder Fallback-Shape).
     *
     * @param g Graphics-Kontext
     */
    public void draw(Graphics g) {
        BufferedImage img = (type == Type.LIFE) ? spriteLife : spriteScore;

        if (img != null) {
            g.drawImage(img, x, y, width, height, null);
        } else {
            // Fallback: falls keine Sprites vorhanden sind
            g.setColor(type == Type.LIFE ? Color.PINK : Color.YELLOW);
            g.fillOval(x, y, width, height);
        }
    }

    /**
     * Gibt eine kleinere Hitbox zurück, damit das Einsammeln "fair" wirkt
     * und nicht schon bei minimaler Berührung passiert.
     *
     * @return Hitbox-Rechteck
     */
    public Rectangle getBounds() {
        int hit = Math.max(12, width / 2);

        int hitX = x + (width - hit) / 2;
        int hitY = y + (height - hit) / 2;

        return new Rectangle(hitX, hitY, hit, hit);
    }

    /** @return PowerUp-Typ */
    public Type getType() { return type; }
}
