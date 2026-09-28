package de.hsh.cyberdefender;

/** Hält den Zustand eines Runs: Score und Leben. */
public class GameSession {

    private int score;
    private int lives;

    public GameSession() {
        this.score = 0;
        this.lives = 3; // Start-Leben
    }

    public int getScore() {
        return score;
    }

    public void addScore(int amount) {
        score += amount;
        if (score < 0) {
            score = 0;
        }
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        loseLife(1);
    }

    /** Zieht eine bestimmte Anzahl von Leben ab. */
    public void loseLife(int amount) {
        lives -= amount;
    }

    /** Ein Leben dazu (z.B. durch PowerUp). */
    public void addLife() {
        lives++;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    public void reset() {
        score = 0;
        lives = 3;
    }
}
