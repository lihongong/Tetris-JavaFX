package tetris.ui;

import javafx.scene.Node;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
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
        try {
            URL dirUrl = getClass().getResource(imageDirectoryPath);
            String decodedPath = URLDecoder.decode(dirUrl.getFile(), "UTF-8");
            File directory = new File(decodedPath);

            // Note: listFiles applies filter (the lambda block) and lists all the files in the directory
            File[] filesInDirectory = directory.listFiles((dir, fileName) -> {
                String lowerCaseFileName = fileName.toLowerCase();
                return lowerCaseFileName.endsWith(".png") ||
                        lowerCaseFileName.endsWith(".jpg") ||
                        lowerCaseFileName.endsWith(".jpeg") ||
                        lowerCaseFileName.endsWith(".gif") ||
                        lowerCaseFileName.endsWith(".bmp");
            });

            if (filesInDirectory == null || filesInDirectory.length == 0) {
                System.err.println("No image files found in directory: " + imageDirectoryPath);
                return;
            }

            // Add the file names in files into the array list
            for (File file : filesInDirectory) {
                backgroundImagesFileNames.add(file.getName());
                backgroundImagesFileNamesCopy.add(file.getName());
            }
        } catch (Exception e) {
            System.err.println("Error loading image names: " + e.getMessage());
        }
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
