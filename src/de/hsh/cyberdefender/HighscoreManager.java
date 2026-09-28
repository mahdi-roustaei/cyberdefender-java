package de.hsh.cyberdefender;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class HighscoreManager {

    private static final String FILE = "highscores.txt";
    private static final int MAX = 10;

    private HighscoreManager() {}

    public static void addScore(String name, int score) {
        if (name == null || name.isBlank()) name = "Player";

        List<Entry> list = loadEntries();
        list.add(new Entry(name.trim(), score));

        list.sort(Comparator.comparingInt(Entry::score).reversed());

        // limit
        if (list.size() > MAX) {
            list = new ArrayList<>(list.subList(0, MAX));
        }

        saveEntries(list);
    }

    public static List<Entry> getHighscores() {
        List<Entry> list = loadEntries();
        list.sort(Comparator.comparingInt(Entry::score).reversed());
        if (list.size() > MAX) {
            list = new ArrayList<>(list.subList(0, MAX));
        }
        return list;
    }

    // ---------- intern ----------

    private static List<Entry> loadEntries() {
        List<Entry> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Format: name;score
                String[] parts = line.split(";");
                if (parts.length != 2) continue;
                String n = parts[0].trim();
                int s;
                try { s = Integer.parseInt(parts[1].trim()); }
                catch (NumberFormatException e) { continue; }
                list.add(new Entry(n, s));
            }
        } catch (IOException ignored) {}

        return list;
    }

    private static void saveEntries(List<Entry> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE, false))) {
            for (Entry e : list) {
                pw.println(e.name() + ";" + e.score());
            }
        } catch (IOException ignored) {}
    }

    public record Entry(String name, int score) {}
}
