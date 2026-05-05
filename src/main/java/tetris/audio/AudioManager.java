package tetris.audio;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AudioManager {
    // SFX: Use AudioClip for low-latency, overlapping sounds
    private final Map<SoundType, AudioClip> sfxLibrary = new HashMap<>();
    // BGM: Use MediaPlayer for long tracks, looping, and volume control
    private final Map<SoundType, Media> bgmLibrary = new HashMap<>();
    private MediaPlayer musicPlayer;

    // Singleton Pattern
    private static AudioManager instance;

    private AudioManager() {
        loadSounds();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private void loadSounds() {
        for (SoundType type : SoundType.values()) {
            String fullPath = "/sounds/" + type.getFileName();
            URL resource = getClass().getResource(fullPath);

            if (resource == null) {
                System.err.println("Warning: Could not find " + fullPath);
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
            // separate thread for faster audio
            new Thread(() -> clip.play()).start();
        }
    }

    public void playRandomGameOverSound() {
        SoundType[] koSounds = {SoundType.KO1, SoundType.KO2, SoundType.WET_FART};

        Random random = new Random();
        SoundType randomType = koSounds[random.nextInt(koSounds.length)];

        playSfx(randomType);
    }

    /** Handles the Background Music */
    public void playBGM(SoundType soundType) {
        if (musicPlayer != null) {
            if (musicPlayer.getStatus() != MediaPlayer.Status.DISPOSED) {
                musicPlayer.stop();
            }
            musicPlayer.dispose();
        }

        Media media = bgmLibrary.get(soundType);
        musicPlayer = new MediaPlayer(media);
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop forever
        musicPlayer.play();
    }

    public void restartBGM() {
        if (musicPlayer != null) {
            // start at the beginning again
            musicPlayer.seek(musicPlayer.getStartTime());
            musicPlayer.play();
        }
    }

    public void pauseBGM() {
        if (musicPlayer != null) musicPlayer.pause();
    }

    public void resumeBGM() {
        if (musicPlayer != null) musicPlayer.play();
    }

    public void stopBGM() {
        if (musicPlayer != null) {
            if (musicPlayer.getStatus() != MediaPlayer.Status.DISPOSED) {
                musicPlayer.stop();
            }
        }
    }
}