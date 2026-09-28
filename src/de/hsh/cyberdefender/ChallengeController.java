package de.hsh.cyberdefender;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Verwaltet die IT-Security-Challenges (Fragen + Antwortoptionen).
 * Liefert zufällige Challenges und wertet Antworten aus.
 * Bei richtiger Antwort werden Punkte zur aktuellen Session addiert.
 */
public class ChallengeController {

    private final GameSession session;
    private final List<Challenge> challenges = new ArrayList<>();
    private final Random random = new Random();

    /**
     * Erstellt den ChallengeController für eine Spiel-Session und lädt alle Fragen.
     *
     * @param session aktuelle GameSession, in die Punkte geschrieben werden
     */
    public ChallengeController(GameSession session) {
        this.session = session;
        initChallenges();
    }

    /**
     * Legt alle verfügbaren Fragen/Challenges an.
     * Diese Liste wird später zufällig für das Spiel ausgewählt.
     */
    private void initChallenges() {

        // 1 – sicheres Passwort
        challenges.add(new Challenge(
                "Welches Passwort ist am sichersten?",
                new String[]{
                        "123456",
                        "Passwort2024",
                        "Kf7!pZ9@qL"
                },
                2,
                300
        ));

        // 2 – Passwort-Länge
        challenges.add(new Challenge(
                "Welche Aussage zu Passwörtern ist am besten?",
                new String[]{
                        "Kurz und leicht zu merken ist besser.",
                        "Lange Passwörter mit Sonderzeichen sind sicherer.",
                        "Passwort und Benutzername sollten gleich sein."
                },
                1,
                200
        ));

        // 3 – Zwei-Faktor-Authentifizierung
        challenges.add(new Challenge(
                "Was ist Zwei-Faktor-Authentifizierung (2FA)?",
                new String[]{
                        "Zwei Leute kennen das Passwort.",
                        "Man braucht Passwort und einen zweiten Faktor, z.B. SMS-Code.",
                        "Das Passwort wird zweimal eingegeben."
                },
                1,
                250
        ));

        // 4 – Phishing
        challenges.add(new Challenge(
                "Was ist typisch für eine Phishing-E-Mail?",
                new String[]{
                        "Sie kommt von einer Adresse der eigenen Firma.",
                        "Sie enthält immer einen Anhang mit Fotos.",
                        "Sie fordert dich auf, dringend auf einen Link zu klicken und Daten einzugeben."
                },
                2,
                250
        ));

        // 5 – Öffentliches WLAN
        challenges.add(new Challenge(
                "Was ist im offenen WLAN eines Cafés riskant?",
                new String[]{
                        "Ein Online-Spiel ohne Account spielen.",
                        "Online-Banking mit Benutzername und Passwort.",
                        "Musik über Kopfhörer hören."
                },
                1,
                250
        ));

        // 6 – Updates
        challenges.add(new Challenge(
                "Warum sind Software-Updates wichtig?",
                new String[]{
                        "Nur wegen neuen Farben im Design.",
                        "Sie schließen Sicherheitslücken und verbessern Schutz.",
                        "Sie machen den PC immer langsamer."
                },
                1,
                200
        ));

        // 7 – Anhang
        challenges.add(new Challenge(
                "Was machst du mit einem Anhang von einer unbekannten Person?",
                new String[]{
                        "Direkt öffnen, vielleicht ist es wichtig.",
                        "Zuerst den Virenscanner ausmachen.",
                        "Nicht öffnen und im Zweifel löschen bzw. nachfragen."
                },
                2,
                250
        ));

        // 8 – Passwort-Speicherung
        challenges.add(new Challenge(
                "Wie solltest du Passwörter NICHT speichern?",
                new String[]{
                        "In einem seriösen Passwort-Manager.",
                        "Auf einem Zettel am Bildschirm.",
                        "In einem verschlüsselten Tresor."
                },
                1,
                200
        ));

        // 9 – https
        challenges.add(new Challenge(
                "Woran erkennst du eine sicherere Webseite beim Login?",
                new String[]{
                        "Am Schloss-Symbol und \"https\" in der Adresszeile.",
                        "Sie hat bunte Bilder und Animationen.",
                        "Die Seite lädt sehr schnell."
                },
                0,
                200
        ));

        // 10 – VPN
        challenges.add(new Challenge(
                "Was macht ein VPN im Normalfall?",
                new String[]{
                        "Es erhöht die Bildschirmhelligkeit.",
                        "Es verschlüsselt den Netzwerkverkehr zwischen dir und dem VPN-Server.",
                        "Es löscht automatisch alle Viren."
                },
                1,
                250
        ));

        // 11 – USB-Stick
        challenges.add(new Challenge(
                "Was ist bei gefundenen USB-Sticks sinnvoll?",
                new String[]{
                        "Sofort einstecken und schauen, was drauf ist.",
                        "Nicht verwenden und ggf. an IT/Verluststelle abgeben.",
                        "Jemandem schicken, den man nicht mag."
                },
                1,
                250
        ));

        // 12 – Firmen-Account
        challenges.add(new Challenge(
                "Was ist bei Firmen-Accounts wichtig?",
                new String[]{
                        "Passwort auch Freunden geben, damit sie helfen können.",
                        "Gleiche Passwörter privat und beruflich benutzen.",
                        "Passwort niemandem verraten und nicht wiederverwenden."
                },
                2,
                300
        ));
    }

    /**
     * Liefert eine zufällige Challenge aus der Liste zurück.
     * Falls keine Fragen vorhanden sind, wird eine Dummy-Challenge zurückgegeben.
     *
     * @return zufällige Challenge
     */
    public Challenge nextChallenge() {
        if (challenges.isEmpty()) {
            return new Challenge(
                    "Keine Challenge definiert.",
                    new String[]{"OK"},
                    0,
                    0
            );
        }
        int index = random.nextInt(challenges.size());
        return challenges.get(index);
    }

    /**
     * Wertet eine Antwort aus und vergibt bei richtiger Antwort Punkte.
     *
     * @param challenge    die aktuelle Challenge
     * @param chosenIndex  Index der gewählten Antwort (0-basiert)
     * @return true, wenn die Antwort richtig war, sonst false
     */
    public boolean submitAnswer(Challenge challenge, int chosenIndex) {
        if (challenge.isCorrect(chosenIndex)) {
            session.addScore(challenge.getRewardScore());
            return true;
        }
        return false;
    }
}
