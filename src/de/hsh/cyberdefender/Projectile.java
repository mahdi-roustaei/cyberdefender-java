package de.hsh.cyberdefender;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Projektil, das vom Spieler abgefeuert wird.
 * Bewegt sich mit fester Geschwindigkeit und verschwindet, wenn es das Spielfeld verlässt.
 */
public class Projectile {

    /** Aktuelle Position (double für flüssigere Bewegung). */
    private double x, y;

    /** Geschwindigkeit pro Tick. */
    private final double vx, vy;

    /** Zeichen-/Hitbox-Größe des Projektils. */
    private final int size = 10;

    /** Wenn false, wird das Projektil nicht mehr gezeichnet/berücksichtigt. */
    private boolean alive = true;

    /** Optionales Sprite für das Projektil (kann auch null sein). */
    private Image sprite;

    /**
     * Erstellt ein Projektil an Startposition mit Richtung/Geschwindigkeit.
     *
     * @param startX Start-X
     * @param startY Start-Y
     * @param vx Geschwindigkeit in X-Richtung
     * @param vy Geschwindigkeit in Y-Richtung
     */
    public Projectile(int startX, int startY, double vx, double vy) {
        this.x = startX;
        this.y = startY;
        this.vx = vx;
        this.vy = vy;

        // Sprite als Resource laden (wenn vorhanden)
        sprite = load("/de/hsh/cyberdefender/projectile.png");
    }

    /**
     * Lädt ein Bild aus den Ressourcen (funktioniert auch im JAR).
     *
     * @param path Resource-Pfad
     * @return Bild oder null, wenn nicht gefunden/Fehler
     */
    private Image load(String path) {
        try {
            var url = getClass().getResource(path);
            if (url == null) return null;
            return ImageIO.read(url);
        } catch (IOException e) {
            return null;
        }
    }

    /** Aktualisiert die Position und prüft, ob das Projektil noch im Spielfeld ist. */
    public void update() {
        x += vx;
        y += vy;

        // Wenn das Projektil sehr weit außerhalb ist, wird es deaktiviert (Performance)
        if (x < -50 || y < -50 || x > 5000 || y > 5000) {
            alive = false;
        }
    }

    /**
     * Zeichnet das Projektil (Sprite oder Fallback).
     *
     * @param g2 Zeichenkontext
     */
    public void draw(Graphics2D g2) {
        if (!alive) return;

        int ix = (int) Math.round(x);
        int iy = (int) Math.round(y);

        if (sprite != null) {
            g2.drawImage(sprite, ix, iy, size, size, null);
        } else {
            // Fallback: einfacher Kreis, falls kein Sprite existiert
            g2.setColor(Color.YELLOW);
            g2.fillOval(ix, iy, size, size);
        }
    }

    /** @return true, wenn das Projektil aktiv ist */
    public boolean isAlive() {
        return alive;
    }

    /** @return Rechteck für Kollisionserkennung */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, size, size);
    }

    /**
     * Prüft Kollision mit einem Enemy über die Hitboxen.
     *
     * @param enemy Gegner-Objekt
     * @return true, wenn sich die Rechtecke schneiden
     */
    public boolean collidesWith(Enemy enemy) {
        return getBounds().intersects(enemy.getBounds());
    }
}
