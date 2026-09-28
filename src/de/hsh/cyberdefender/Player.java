package de.hsh.cyberdefender;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Player {

    private enum Direction { UP, DOWN, LEFT, RIGHT }

    private int x, y;
    private int width = 64;
    private int height = 64;
    private int speed = 8;

    private boolean up, down, left, right;
    private Direction facing = Direction.UP;

    private BufferedImage sprite;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        loadSprite();
    }

    private void loadSprite() {
        BufferedImage img = null;

        try {
            var url = getClass().getResource("survivor_shotgun.png");
            if (url != null) img = ImageIO.read(url);
        } catch (IOException ignored) { }

        if (img == null) {
            try {
                File f = new File("src/de/hsh/cyberdefender/survivor_shotgun.png");
                if (f.exists()) img = ImageIO.read(f);
                else System.err.println("Sprite-Datei nicht gefunden: " + f.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Fehler beim Laden von survivor_shotgun.png: " + e.getMessage());
            }
        }

        if (img != null) {
            sprite = img;
            width = img.getWidth();
            height = img.getHeight();
        } else {
            System.err.println("Konnte survivor_shotgun.png nicht laden, benutze Fallback.");
        }
    }

    public void update() {
        if (up) {
            y -= speed;
            facing = Direction.UP;
        }
        if (down) {
            y += speed;
            facing = Direction.DOWN;
        }
        if (left) {
            x -= speed;
            facing = Direction.LEFT;
        }
        if (right) {
            x += speed;
            facing = Direction.RIGHT;
        }
    }

    public void clampToBounds(int minX, int minY, int maxX, int maxY) {
        if (x < minX) x = minX;
        if (x + width > maxX) x = maxX - width;
        if (y < minY) y = minY;
        if (y + height > maxY) y = maxY - height;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (sprite == null) {
            g2.setColor(new Color(40, 200, 230));
            g2.fillRect(x, y, width, height);
            return;
        }

        double angle = switch (facing) {
            case UP -> 0.0;
            case RIGHT -> Math.PI / 2;
            case DOWN -> Math.PI;
            case LEFT -> -Math.PI / 2;
        };

        int cx = x + width / 2;
        int cy = y + height / 2;

        AffineTransform old = g2.getTransform();
        g2.translate(cx, cy);
        g2.rotate(angle);
        g2.drawImage(sprite, -width / 2, -height / 2, width, height, null);
        g2.setTransform(old);
    }

    // Schussrichtung NUR nach Blickrichtung
    public Projectile shoot(double ignoredX, double ignoredY) {
        double shotDirX = 0;
        double shotDirY = 0;

        switch (facing) {
            case UP -> shotDirY = -1;
            case DOWN -> shotDirY = 1;
            case LEFT -> shotDirX = -1;
            case RIGHT -> shotDirX = 1;
        }

        int centerX = x + width / 2;
        int centerY = y + height / 2;
        return new Projectile(centerX, centerY, shotDirX, shotDirY);
    }

    //  HITBOX (kleiner als Sprite, damit kein "Ghost Collision")
    public Rectangle getBounds() {
        int hitboxW = width / 2;
        int hitboxH = height / 2;

        int hitboxX = x + (width - hitboxW) / 2;
        int hitboxY = y + (height - hitboxH) / 2;

        return new Rectangle(hitboxX, hitboxY, hitboxW, hitboxH);
    }

    // Steuerung
    public void setUp(boolean up) { this.up = up; }
    public void setDown(boolean down) { this.down = down; }
    public void setLeft(boolean left) { this.left = left; }
    public void setRight(boolean right) { this.right = right; }

    public void setShooting(boolean shooting) { /* nicht nötig */ }
}
