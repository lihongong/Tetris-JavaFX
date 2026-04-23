package tetris.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import tetris.Tetris;

import java.io.IOException;
import java.net.URL;

import static java.util.Objects.requireNonNull;

// READ ME: This code is copied from some part of cs2103t's seedu.addressbook

/**
 * Represents a distinct part of the UI. e.g. Windows, dialogs, panels, status bars, etc.
 * It contains a scene graph with a root node of type {@code T}.
 */
public abstract class UiPart<T extends Node> {
    /** Resource folder where FXML files are stored. */
    public static final String FXML_FILE_FOLDER = "/view/";
    private final FXMLLoader fxmlLoader = new FXMLLoader();

    private static boolean isUiEffectsOn = false;

    // Only calls loadFxmlFile(url, root) with the root if the root is a Stage. If not, just call loadFxmlFile(url, null);
    // loadFxmlFile will call setRoot(root)

    /**
     * Constructs a UiPart with the specified FXML file URL.
     * The FXML file must not specify the {@code fx:controller} attribute.
     */
    public UiPart(URL fxmlFileUrl) {
        loadFxmlFile(fxmlFileUrl, null);
    }

    /**
     * Constructs a UiPart using the specified FXML file within {@link #FXML_FILE_FOLDER}.
     * @see #UiPart(URL)
     */
    public UiPart(String fxmlFileName) {
        this(getFxmlFileUrl(fxmlFileName));
    }

    /**
     * Constructs a UiPart with the specified FXML file URL and root object.
     * The FXML file must not specify the {@code fx:controller} attribute.
     */
    public UiPart(URL fxmlFileUrl, T root) {
        loadFxmlFile(fxmlFileUrl, root);
    }

    /**
     * Constructs a UiPart with the specified FXML file within {@link #FXML_FILE_FOLDER} and root object.
     * @see #UiPart(URL, T)
     */
    public UiPart(String fxmlFileName, T root) {
        this(getFxmlFileUrl(fxmlFileName), root);
    }

    /**
     * Returns the root object of the scene graph of this UiPart.
     */
    public T getRoot() {
        return fxmlLoader.getRoot();
    }

    private void loadFxmlFile(URL location, T root) {
        requireNonNull(location);
        fxmlLoader.setLocation(location);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(root);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
    private static URL getFxmlFileUrl(String fxmlFileName) {
        requireNonNull(fxmlFileName);
        String fxmlFileNameWithFolder = FXML_FILE_FOLDER + fxmlFileName;
        URL fxmlFileUrl = Tetris.class.getResource(fxmlFileNameWithFolder);
        return requireNonNull(fxmlFileUrl);
    }

    // We don't do mainWindow.getRoot().add(Screens) because it is slow and laggy sometimes
    // Now, we add all the Screens in Mainwindow at launch time and hide the unused screens
    public static void hideNode(Node... nodes) {
        for (Node node : nodes) {
            node.setVisible(false);
        }
    }
    public void showNode(Node node) {
        node.toFront();
        node.setVisible(true);
    }

    public static boolean isUiEffectsOn() {
        return isUiEffectsOn;
    }
    public static void setUiEffectsOn() {
        isUiEffectsOn = true;
    }
    public static void setUiEffectsOff() {
        isUiEffectsOn = false;
    }


}