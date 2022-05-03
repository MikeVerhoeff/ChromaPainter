package nl.tudelft.mikeverhoeff.chromadepth.ui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrogramChart;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrumIO;
import nl.tudelft.mikeverhoeff.chromadepth.util.Pair;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SelectSpectrumController implements Initializable {

    private static Map<File, Spectrum> loadedSpectrum;
    static {
        loadedSpectrum = new HashMap<>();
    }
    private static File lastDirectory;

    private ExecutorService spectraLoader;

    @FXML
    private StackPane chartField;
    private SpectrogramChart chart;

    @FXML
    private Pane colorPane;

    @FXML
    private TextField directoryField;

    @FXML
    private VBox filesField;

    private Consumer<Spectrum> onAccept;
    private File directory;
    private Spectrum selectedSpectrum;

    public SelectSpectrumController() {
        spectraLoader = Executors.newSingleThreadExecutor();
    }

    @FXML
    void brouseAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(directoryField.getScene().getWindow());
        if(dir != null && dir.isDirectory()) {
            directory = dir;
            directoryField.setText(directory.getAbsolutePath());
            showFiles();
        }
        event.consume();
    }

    @FXML
    void onDirectoryChange(ActionEvent event) {
        File dir = new File(directoryField.getText());
        if(dir.exists() && dir.isDirectory()) {
            directory = dir;
            showFiles();
        } else {
            directoryField.setText(directory.getAbsolutePath());
        }
        event.consume();
    }

    private void setDirectory(File file) {
        if(file != null && file.exists() && file.isDirectory()) {
            directoryField.setText(file.getAbsolutePath());
            showFiles();
        }
    }

    private void showFiles() {
        if(directory != null && directory.isDirectory()) {
            filesField.getChildren().clear();
            File[] files = directory.listFiles();
            for(File file : files) {
                if (file.isFile()) {
                    System.out.println("Added file: "+file.getName());
                    filesField.getChildren().add(lineForFile(file));
                } else {
                    System.out.println("Skipped directory: "+file.getName());
                }
            }
        }
    }

    private Node lineForFile(File file) {
        Pane pane = new Pane();
        pane.setPrefWidth(20);
        pane.setPrefHeight(20);

        Label label = new Label(file.getName());

        HBox box = new HBox(label, pane);
        box.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> onSelectFile(event, file, pane));

        if (loadedSpectrum.containsKey(file)) {
            setPaneColor(pane, loadedSpectrum.get(file).getArgb());
        } else {
            spectraLoader.execute(() -> {

                try {
                    Spectrum spectrum = SpectrumIO.loadCGATS17Spectrum(file).get(0);
                    chart.displayColorSpectrum(spectrum);
                    int screencolor = spectrum.getArgb();
                    Platform.runLater(() -> {
                        loadedSpectrum.put(file, spectrum);
                        pane.setStyle("-fx-background-color: rgb(" + ((screencolor >> 16) & 0xff) + "," + ((screencolor >> 8) & 0xff) + "," + ((screencolor) & 0xff) + ")");
                    });
                } catch (Exception exception) {
                }

            });
        }

        return box;
    }

    void setPaneColor(Pane pane, int screencolor) {
        pane.setStyle("-fx-background-color: rgb("+((screencolor>>16) & 0xff)+","+((screencolor>>8) & 0xff)+","+((screencolor) & 0xff)+")");
    }

    void onSelectFile(MouseEvent event, File file, Pane filePane) {
        System.out.println(file.getAbsoluteFile());
        try {
            Spectrum spectrum = SpectrumIO.loadCGATS17Spectrum(file).get(0);
            chart.displayColorSpectrum(spectrum);
            int screencolor = spectrum.getArgb();
            colorPane.setStyle("-fx-background-color: rgb("+((screencolor>>16) & 0xff)+","+((screencolor>>8) & 0xff)+","+((screencolor) & 0xff)+")");
            filePane.setStyle("-fx-background-color: rgb("+((screencolor>>16) & 0xff)+","+((screencolor>>8) & 0xff)+","+((screencolor) & 0xff)+")");
            this.selectedSpectrum = spectrum;
            loadedSpectrum.put(file, spectrum);
            event.consume();
        } catch (Exception exception) {
            System.out.println("Not a spectrum ("+exception.getLocalizedMessage()+")");
        }
    }

    @FXML
    void cancleAction(ActionEvent event) {
        lastDirectory = directory;
        spectraLoader.shutdown();
        ((Stage)filesField.getScene().getWindow()).close();
    }

    @FXML
    void useColorAction(ActionEvent event) {
        lastDirectory = directory;
        spectraLoader.shutdown();
        if (onAccept != null) {
            onAccept.accept(selectedSpectrum);
        }
        ((Stage)filesField.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chart = new SpectrogramChart();
        chartField.getChildren().add(chart);
        directory = lastDirectory;
        setDirectory(directory);
    }

    public void onClose() {
        lastDirectory = directory;
        spectraLoader.shutdown();
    }

    public void setOnAccept(Consumer<Spectrum> onAccept) {
        this.onAccept = onAccept;
    }
}
