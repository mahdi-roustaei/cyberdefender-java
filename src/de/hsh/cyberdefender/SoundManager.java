package de.hsh.cyberdefender;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Zentrale Klasse für Audio:
 * - Hintergrundmusik (Endlosschleife)
 * - Soundeffekte (einmalig)
 *
 * Hinweis zur Kompatibilität:
 * Am stabilsten funktionieren WAV-Dateien im Format PCM_SIGNED (16-bit, 44.1 kHz).
 */
public class SoundManager {

    private Clip musicClip;
    private String currentMusicPath;

    private final Map<String, Clip[]> sfxPools = new HashMap<>();
    private final Map<String, Integer> sfxPoolIndex = new HashMap<>();

    public void playMusicLoop(String resourcePath, float volumeDb) {
        if (musicClip != null && musicClip.isRunning() && resourcePath.equals(currentMusicPath)) return;

        stopMusic();

        try {
            Clip clip = loadClip(resourcePath);
            if (clip == null) return;

            setVolume(clip, volumeDb);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            musicClip = clip;
            currentMusicPath = resourcePath;

        } catch (Exception ex) {
            System.err.println("Music error: " + resourcePath);
            ex.printStackTrace();
        }
    }

    public void stopMusic() {
        if (musicClip != null) {
            try {
                musicClip.stop();
                musicClip.close();
            } catch (Exception ignored) {}
            musicClip = null;
            currentMusicPath = null;
        }
    }

    public void playSfx(String resourcePath, float volumeDb) {
        try {
            Clip clip = nextSfxClip(resourcePath, volumeDb);
            if (clip == null) return;

            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();

        } catch (Exception ex) {
            System.err.println("SFX error: " + resourcePath);
            ex.printStackTrace();
        }
    }

    private Clip nextSfxClip(String resourcePath, float volumeDb) throws Exception {
        Clip[] pool = sfxPools.get(resourcePath);

        if (pool == null) {
            int poolSize = resourcePath.contains("shot") ? 8 : 3; // Mehr Overlap für Schüsse
            pool = new Clip[poolSize];

            for (int i = 0; i < poolSize; i++) {
                Clip c = loadClip(resourcePath);
                if (c == null) return null;
                setVolume(c, volumeDb);
                pool[i] = c;
            }

            sfxPools.put(resourcePath, pool);
            sfxPoolIndex.put(resourcePath, 0);
        }

        int idx = sfxPoolIndex.get(resourcePath);
        Clip result = pool[idx];

        idx = (idx + 1) % pool.length;
        sfxPoolIndex.put(resourcePath, idx);

        return result;
    }

    private Clip loadClip(String resourcePath) throws Exception {
        URL url = getClass().getResource(resourcePath);
        if (url == null) {
            System.err.println("Sound not found: " + resourcePath);
            return null;
        }

        try (BufferedInputStream bis = new BufferedInputStream(url.openStream());
             AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {

            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        }
    }

    private void setVolume(Clip clip, float volumeDb) {
        try {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float clamped = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), volumeDb));
            gain.setValue(clamped);
        } catch (Exception ignored) {
        }
    }
}
