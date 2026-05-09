package tetris.audio;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import tetris.Bag;
import tetris.util.GameMode;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.util.Arrays.stream;

public class AudioManager {
    // SFX: Use AudioClip for low-latency, overlapping sounds
    private final Map<SoundType, AudioClip> sfxLibrary = new HashMap<>();
    // BGM: Use MediaPlayer for long tracks, looping, and volume control
    private final Map<SoundType, Media> bgmLibrary = new HashMap<>();
    /** Contain all bgm sound type except for SoundType.BLITZ */
    private final Bag<SoundType> bgmSoundType = new Bag<>();
    private MediaPlayer musicPlayer;

    private Timeline musicPlayerTimeline;

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

                // exclude SoundType.BLITZ
                if (type != SoundType.BLITZ_OST) {
                    bgmSoundType.add(type);
                }
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
    public void playLoopingBGM(SoundType soundType) {
        if (musicPlayerTimeline != null) {
            musicPlayerTimeline.stop();
        }

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
        if (musicPlayerTimeline != null) {
            musicPlayerTimeline.stop();
        }

        if (musicPlayer != null) {
            // start at the beginning again
            musicPlayer.setVolume(1.0); // pausing the game will set volume to 0
            musicPlayer.seek(musicPlayer.getStartTime());
            musicPlayer.play();
        }
    }

    public void playRandomBGM() {
        if (musicPlayerTimeline != null) {
            musicPlayerTimeline.stop();
        }

        if (musicPlayer != null) {
            if (musicPlayer.getStatus() != MediaPlayer.Status.DISPOSED) {
                musicPlayer.stop();
            }
            musicPlayer.dispose();
        }

        SoundType randomBGMType = bgmSoundType.pickRandomly();
        Media media = bgmLibrary.get(randomBGMType);
        musicPlayer = new MediaPlayer(media);
        // play a new bgm once the current one finishes
        musicPlayer.setOnEndOfMedia(this::playRandomBGM);

        musicPlayer.play();
    }

    /**
     * Pauses the bgm smoothly using {@code Timeline}
     */
    public void pauseBGM() {
        if (musicPlayer != null) {
            if (musicPlayerTimeline != null) {
                musicPlayerTimeline.stop();
            }
            musicPlayerTimeline = new Timeline(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(musicPlayer.volumeProperty(), 0))
            );

            musicPlayerTimeline.setOnFinished(e -> {
                musicPlayer.pause();
            });

            musicPlayerTimeline.play();
        }
    }
    /**
     * Resumes the bgm smoothly using {@code Timeline}
     */
    public void resumeBGM() {
        if (musicPlayer != null) {
            if (musicPlayerTimeline != null) {
                musicPlayerTimeline.stop();
            }
            musicPlayer.play();
            musicPlayerTimeline = new Timeline(
                    new KeyFrame(Duration.millis(150), // keep it at the same prev volume in the first 150ms for smoothness
                            new KeyValue(musicPlayer.volumeProperty(), musicPlayer.getVolume())),
                    new KeyFrame(Duration.millis(350), // Very fast fade (50ms)
                            new KeyValue(musicPlayer.volumeProperty(), 1.0))
            );

            musicPlayerTimeline.play();
            //musicPlayer.play();
        }
    }

    public void stopBGM() {
        if (musicPlayerTimeline != null) {
            musicPlayerTimeline.stop();
        }

        if (musicPlayer != null) {
            if (musicPlayer.getStatus() != MediaPlayer.Status.DISPOSED) {
                musicPlayer.stop();
            }
        }
    }
}