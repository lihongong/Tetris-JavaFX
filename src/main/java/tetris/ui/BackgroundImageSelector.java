package tetris.ui;

import javafx.scene.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BackgroundImageSelector {
    private final List<String> backgroundImagesFileNames = new ArrayList<>(); // stores all the file names in the directory
    private final List<String> backgroundImagesFileNamesCopy = new ArrayList<>();
    private final String backgroundImagesDirectory;

    public BackgroundImageSelector(String imageDirectoryPath) {
        backgroundImagesDirectory = imageDirectoryPath;
        scanForImagesInDirectory(backgroundImagesDirectory);
    }

    /**
     * Scans for files with extensions (.png, .jpg, .jpeg, .gif, .bmp) and add the file names into
     * {@code backgroundImagesFileNames} list.
     * @param imageDirectoryPath the directory where background images are stored.
     */
    private void scanForImagesInDirectory(String imageDirectoryPath) {
        String imageListFileName = "image_list.txt";
        String imageListPath = imageDirectoryPath + imageListFileName;

        try (InputStream inputStream = getClass().getResourceAsStream(imageListPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String fileName;
            while ((fileName = reader.readLine()) != null) {
                fileName = fileName.trim();
                if (!fileName.isEmpty() && isImageFileName(fileName)) {
                    backgroundImagesFileNames.add(fileName);
                    backgroundImagesFileNamesCopy.add(fileName);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read " + imageListFileName + " : " + e.getMessage());
        }
    }
    private void lolscanForImagesInDirectory(String imageDirectoryPath) {
        try {
            URI uri = getClass().getResource(imageDirectoryPath).toURI();

            if (uri.getScheme().equals("jar")) {
                // --- CASE 1: RUNNING FROM JAR ---
                // We "mount" the JAR as a filesystem so we can walk through it
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                    Path myPath = fileSystem.getPath(imageDirectoryPath);
                    Files.walk(myPath, 1).forEach(p -> {
                        String name = p.getFileName().toString();
                        if (isImageFileName(name)) {
                            backgroundImagesFileNames.add(name);
                            backgroundImagesFileNamesCopy.add(name);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // --- CASE 2: RUNNING FROM IDE ---
                File directory = new File(uri);
                File[] files = directory.listFiles((dir, name) -> isImageFileName(name)); // list files that are images (image format)
                if (files != null) {
                    for (File f : files) {
                        backgroundImagesFileNames.add(f.getName());
                        backgroundImagesFileNamesCopy.add(f.getName());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Scanning failed: " + e.getMessage());
        }
    }
    // Helper to keep code clean
    private boolean isImageFileName(String name) {
        String lower = name.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".bmp") || lower.endsWith(".gif");
    }

    /**
     * Choose a random image file name in {@code backgroundImagesFileNames} and set the background for the given node
     * via css set style method.
     * @param mainNode is the node to which the background will be applied
     */
    public void setRandomBackgroundImage(Node mainNode) {

        // Ensures no image will be selected twice in a row
        String imagePath = chooseDifferentBackgroundImage();
        // set the background in the node (For GameScreen, it is "mainLayout")
        mainNode.setStyle(
                "-fx-background-image: url('" + imagePath + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center;" +
                        "-fx-background-repeat: no-repeat;"
        );
    }

    private String chooseDifferentBackgroundImage() {
        if (backgroundImagesFileNames.isEmpty()) {
            backgroundImagesFileNames.addAll(backgroundImagesFileNamesCopy);
        }

        Random random = new Random();
        int randomImageIndex = random.nextInt(backgroundImagesFileNames.size());
        String chosenImageFileName = backgroundImagesFileNames.remove(randomImageIndex);

        // Set the background will the chosen image
        String imagePath = backgroundImagesDirectory + chosenImageFileName;
        return imagePath;
    }
}
