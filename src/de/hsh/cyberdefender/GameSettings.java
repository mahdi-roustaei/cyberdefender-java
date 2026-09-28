package de.hsh.cyberdefender;

/** Globale Spieleinstellungen (z.B. aktueller Schwierigkeitsgrad). */
public class GameSettings {

    private static Difficulty currentDifficulty = Difficulty.MEDIUM;

    private GameSettings() {
        // Utility-Klasse
    }

    public static Difficulty getDifficulty() {
        return currentDifficulty;
    }

    public static void setDifficulty(Difficulty difficulty) {
        if (difficulty != null) {
            currentDifficulty = difficulty;
        }
    }
}
