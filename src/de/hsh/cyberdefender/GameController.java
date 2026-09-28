package de.hsh.cyberdefender;

/**
 * GameController steuert die Spiellogik.
 * Er arbeitet mit GameSession (Score, Leben) und ChallengeController (Fragen).
 */
public class GameController {

    private final GameSession session;
    private final ChallengeController challengeController;

    public GameController(GameSession session, ChallengeController challengeController) {
        this.session = session;
        this.challengeController = challengeController;
    }

    // --- Zugriff auf Session / Challenge ---

    public GameSession getSession() {
        return session;
    }

    public ChallengeController getChallengeController() {
        return challengeController;
    }

    // --- Score und Leben steuern ---

    public void addScore(int amount) {
        session.addScore(amount);
    }

    public void loseLife() {
        session.loseLife();
    }

    public void loseLife(int amount) {
        session.loseLife(amount);
    }

    public void addLife() {
        session.addLife();
    }

    public boolean isGameOver() {
        return session.isGameOver();
    }

    // --- Challenges ---

    /** Gibt die nächste IT-Security-Challenge zurück. */
    public Challenge nextChallenge() {
        return challengeController.nextChallenge();
    }

    /**
     * Antwort auswerten und Session updaten.
     * @return true, wenn Antwort richtig war.
     */
    public boolean submitChallengeAnswer(Challenge challenge, int chosenIndex) {
        return challengeController.submitAnswer(challenge, chosenIndex);
    }
}
