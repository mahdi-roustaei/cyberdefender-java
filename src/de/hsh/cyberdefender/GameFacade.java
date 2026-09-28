package de.hsh.cyberdefender;

/**
 * GameFacade ist die Schnittstelle für die GUI.
 * Das Panel ruft nur Methoden dieser Klasse auf und kennt die Details innen nicht.
 */
public class GameFacade {

    private final GameController gameController;

    public GameFacade(GameController gameController) {
        this.gameController = gameController;
    }

    // --- Zugriff auf Session ---

    public GameSession getSession() {
        return gameController.getSession();
    }

    // --- Score / Leben ---

    public void addScore(int amount) {
        gameController.addScore(amount);
    }

    public void loseLife() {
        gameController.loseLife();
    }

    public void loseLife(int amount) {
        gameController.loseLife(amount);
    }

    public void addLife() {
        gameController.addLife();
    }

    public boolean isGameOver() {
        return gameController.isGameOver();
    }

    // --- Challenges ---

    public Challenge nextChallenge() {
        return gameController.nextChallenge();
    }

    public boolean submitChallengeAnswer(Challenge challenge, int chosenIndex) {
        return gameController.submitChallengeAnswer(challenge, chosenIndex);
    }
}
