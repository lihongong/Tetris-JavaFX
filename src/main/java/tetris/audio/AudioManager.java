package tetris.audio;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    // SFX: Use AudioClip for low-latency, overlapping sounds
    private final Map<SoundType, AudioClip> sfxLibrary = new HashMap<>();
    // BGM: Use MediaPlayer for long tracks, looping, and volume control
    private final Map<SoundType, Media> bgmLibrary = new HashMap<>();
    private MediaPlayer musicPlayer;
    /*private final String[] sfxFileNames = {
            "Combo1.wav", "Combo2.wav", "Combo3.wav", "Combo4.wav", "Combo5.wav", "Combo6.wav", "Combo7.wav",
            "Remove1Line.wav", "Remove2Line.wav", "Remove3Line.wav", "Remove4Line_TSpin.wav",
            "Hold.wav", "Space.wav", "KO1.wav", "KO2.wav", ""// ... add all your files
    };
    private final String[] bgmFileNames = {
            "OSTShorten.wav"
    };*/

    // Singleton Pattern
    private static AudioManager instance;

    public AudioManager() {
        loadSounds();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private void loadSounds() {
        // load sfx sound

        /*
        for (int i = 0; i < sfxFileNames.length; i++) {
            try {
                // IMPORTANT: Use getResource so it works inside a JAR file later
                URL resource = getClass().getResource("/sounds/" + sfxFileNames[i]);
                if (resource != null) {
                    sfxLibrary.put(i, new AudioClip(resource.toExternalForm()));
                }
            } catch (Exception e) {
                System.err.println("Could not load sound: " + sfxFileNames[i]);
            }
        }
        // load ost / bgm
        for (int i = 0; i < bgmFileNames.length; i++) {
            try {
                // IMPORTANT: Use getResource so it works inside a JAR file later
                URL resource = getClass().getResource("/sounds/" + bgmFileNames[i]);
                if (resource != null) {
                    bgmLibrary.put(i, new Media(resource.toExternalForm()));
                }
            } catch (Exception e) {
                System.err.println("Could not load sound: " + bgmFileNames[i]);
            }
        }*/

        for (SoundType type : SoundType.values()) {
            String path = type.getFileName();
            URL resource = getClass().getResource(path);

            if (resource == null) {
                continue;
            }

            String externalForm = resource.toExternalForm();
            if (type.isBgm()) {
                bgmLibrary.put(type, new Media(externalForm));
            } else {
                sfxLibrary.put(type, new AudioClip(externalForm));
            }
        }
    }

    /** Plays a short sound effect (can overlap) */
    public void playSfx(SoundType soundType) {
        AudioClip clip = sfxLibrary.get(soundType);
        if (clip != null) {
            clip.play();
        }
    }

    /** Handles the Background Music */
    public void playBGM(SoundType soundType) {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.dispose();
        }

        Media media = bgmLibrary.get(soundType);
        musicPlayer = new MediaPlayer(media);
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop forever
        musicPlayer.play();
    }

    public void pauseBGM() {
        if (musicPlayer != null) musicPlayer.pause();
    }

    public void resumeBGM() {
        if (musicPlayer != null) musicPlayer.play();
    }

    public void stopBGM() {
        if (musicPlayer != null) musicPlayer.stop();
    }
}