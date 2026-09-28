# CyberDefender

A 2D arcade survival game built with Java and Swing, combining real-time combat with cybersecurity quizzes.

Dodge enemies, collect power-ups, and answer security questions to increase your score. Developed as an individual university programming project, CyberDefender brings together desktop UI development, game state management, collision detection, audio, and local persistence.

## Features

- Three enemy types: Hacker, Virus, and Malware, with different movement speeds, damage, and score values.
- Keyboard movement and single-shot firing with automatic targeting of the nearest enemy.
- Easy, Medium, and Hard modes, with enemy numbers increasing as your score grows.
- Health and score power-ups.
- Twelve German-language multiple-choice questions covering topics such as phishing, passwords, two-factor authentication, updates, and VPNs.
- Pause/resume, a settings screen, and a local top-10 leaderboard.
- Custom sprites, background music, sound effects, and an arcade-style interface.

The interface combines English menu labels with German gameplay text and questions. This is an educational game, not a security scanning or protection tool.

## Technology

| Area | Implementation |
| --- | --- |
| Language | Java |
| Desktop UI | Swing and AWT |
| Rendering | Java2D / Graphics2D and ImageIO |
| Game loop | Swing Timer with a 16 ms interval |
| Audio | Java Sound (`javax.sound.sampled`) |
| Persistence | Local text file (`highscores.txt`) |
| Dependencies | Java standard library only |

## Run from source

Use a **JDK 17** installation with `java` and `javac` available in your terminal. A graphical desktop is required. No Maven, Gradle, database, or external Java libraries are needed.

From the repository root in **Windows PowerShell**:

```powershell
New-Item -ItemType Directory -Force build/classes | Out-Null
javac -encoding UTF-8 --release 17 -d build/classes src/de/hsh/cyberdefender/*.java
java -cp "build/classes;src" de.hsh.cyberdefender.CyberDefenderApp
```

On **macOS or Linux**:

```sh
mkdir -p build/classes
javac -encoding UTF-8 --release 17 -d build/classes src/de/hsh/cyberdefender/*.java
java -cp "build/classes:src" de.hsh.cyberdefender.CyberDefenderApp
```

Keep `src` on the runtime classpath so the game can locate its images, font, and audio. The source and resources intentionally retain their original package layout.

Compilation with JDK 17 was verified during preparation of this README. Interactive gameplay and audio were not tested in that review; the macOS/Linux commands were not executed.

## Controls

| Key | Action |
| --- | --- |
| W / A / S / D | Move |
| Space | Fire a single shot, subject to the firing cooldown |
| P | Pause or resume |
| Esc | Return to the menu while unpaused |
| 1 / 2 / 3 | Answer a quiz question |

During a quiz, the game pauses. A correct answer awards points; an incorrect answer or dismissing the quiz costs one life.

## Project design

- `CyberDefenderApp` initializes the theme and opens the main window on Swing's event dispatch thread.
- `MainFrame` uses `CardLayout` to switch between the menu, game, settings, and leaderboard.
- `GamePanel` handles the timer, rendering, keyboard input, entities, collision checks, and quiz flow.
- `GameSession`, `GameController`, and `GameFacade` organize score, lives, and access to challenge logic.
- `ChallengeController` stores the question bank and evaluates answers.
- `Player`, `Enemy`, `Projectile`, and `PowerUp` represent game entities.
- `HighscoreManager` reads and writes the local leaderboard; `SoundManager` handles music and effects.

## Local data

The game stores player-entered names and scores in `highscores.txt` in the working directory. This file is excluded from version control. Difficulty settings apply to the current application session.

## Project context

CyberDefender was developed by Mahdi Roustaei as an individual project for the university course **Programmierprojekt** and is shared with permission.

## Current limitations and next steps

- Add automated tests for game state, quiz scoring, and leaderboard persistence.
- Validate leaderboard names and surface file read/write errors to users.
- Cache projectile/enemy image assets to reduce repeated loading during gameplay.
- Review quiz wording and add explanations for answers.
- Add gameplay screenshots and a short demonstration after a desktop play-through.

## Assets and licensing

The project uses third-party images, audio, and a font reported by the project contributor as open source. Source links, creator credits, and specific license details are being documented. No repository-wide open-source license has been assigned in this draft.
