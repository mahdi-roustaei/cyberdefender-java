package de.hsh.cyberdefender;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public final class ArcadeTheme {

    private static Font baseFont;

    private ArcadeTheme() {}

    /** Einmal am Anfang aufrufen (bevor MainFrame erstellt wird). */
    public static void install() {
        baseFont = loadBaseFont();

        // Einheitliche Standardschrift für Swing-Komponenten setzen
        setUIDefaultFont("Label.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("Button.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("ToggleButton.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("RadioButton.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("CheckBox.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("Menu.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("MenuItem.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("PopupMenu.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("ToolTip.font", baseFont.deriveFont(Font.PLAIN, 12f));
        setUIDefaultFont("TextField.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("TextArea.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("TextPane.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("ComboBox.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("List.font", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("Table.font", baseFont.deriveFont(Font.PLAIN, 14f));

        // OptionPane (Popups)
        setUIDefaultFont("OptionPane.messageFont", baseFont.deriveFont(Font.PLAIN, 14f));
        setUIDefaultFont("OptionPane.buttonFont", baseFont.deriveFont(Font.PLAIN, 14f));

        refreshAllWindows();
    }

    /** Für Graphics2D (HUD/Text im Spiel). */
    public static Font font(float size) {
        if (baseFont == null) baseFont = loadBaseFont();
        return baseFont.deriveFont(Font.PLAIN, size);
    }

    private static Font loadBaseFont() {
        String path = "/de/hsh/cyberdefender/font/PressStart2P.ttf";

        try (InputStream in = ArcadeTheme.class.getResourceAsStream(path)) {
            if (in == null) {
                System.err.println("Font nicht gefunden: " + path);
                return fallbackFont();
            }

            Font f = Font.createFont(Font.TRUETYPE_FONT, in);
            // Font registrieren (hilft bei manchen Systemen)
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
            return f;

        } catch (Exception e) {
            System.err.println("Font konnte nicht geladen werden: " + path);
            return fallbackFont();
        }
    }

    private static Font fallbackFont() {
        // Solider Fallback, der auf Windows meist gut aussieht
        Font f = new Font("Consolas", Font.PLAIN, 14);
        if (f.getFamily().equalsIgnoreCase("Dialog")) {
            f = new Font("Monospaced", Font.PLAIN, 14);
        }
        return f;
    }

    private static void setUIDefaultFont(String key, Font font) {
        UIManager.put(key, font);
    }

    private static void refreshAllWindows() {
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
        }
    }
}
