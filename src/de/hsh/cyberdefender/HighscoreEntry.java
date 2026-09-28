package de.hsh.cyberdefender;

/** Ein Eintrag in der Highscore-Tabelle. */
public class HighscoreEntry {

    private final String name;
    private final int score;

    public HighscoreEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
