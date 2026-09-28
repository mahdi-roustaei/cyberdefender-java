package de.hsh.cyberdefender;

/**
 * Repräsentiert eine einzelne IT-Security-Challenge.
 * Eine Challenge besteht aus:
 * - einer Frage
 * - mehreren Antwortoptionen
 * - dem Index der richtigen Antwort
 * - einer Belohnung in Punkten bei richtiger Lösung
 */
public class Challenge {

    private final String question;
    private final String[] answers;
    private final int correctIndex;
    private final int rewardScore;

    /**
     * Erstellt eine neue Challenge.
     *
     * @param question      die Frage, die angezeigt wird
     * @param answers       die Antwortoptionen (z.B. 3 Stück)
     * @param correctIndex  Index der richtigen Antwort im answers-Array (0-basiert)
     * @param rewardScore   Punkte, die man bei richtiger Antwort erhält
     */
    public Challenge(String question, String[] answers, int correctIndex, int rewardScore) {
        this.question = question;
        this.answers = answers;
        this.correctIndex = correctIndex;
        this.rewardScore = rewardScore;
    }

    /** @return die Frage der Challenge */
    public String getQuestion() {
        return question;
    }

    /** @return die Antwortoptionen */
    public String[] getAnswers() {
        return answers;
    }

    /** @return die Punkte-Belohnung bei richtiger Antwort */
    public int getRewardScore() {
        return rewardScore;
    }

    /** @return Index der richtigen Antwort (0-basiert) */
    public int getCorrectIndex() {
        return correctIndex;
    }

    /**
     * Prüft, ob der gegebene Index die richtige Antwort ist.
     *
     * @param index gewählter Antwortindex (0-basiert)
     * @return true, wenn richtig; sonst false
     */
    public boolean isCorrect(int index) {
        return index == correctIndex;
    }
}
