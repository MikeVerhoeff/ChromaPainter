package nl.tudelft.mikeverhoeff.chromadepth;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrumIO;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.MainController;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.NewCanvasDialogController;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.SelectSpectrumController;

import java.io.File;
import java.io.IOException;

public class Paint {

    public enum RGBColor {
        RED, GREEN, BLUE, WHITE
    }

    private Spectrum spectrum;

    public Paint(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public Paint(RGBColor color) {
        try {
            switch (color) {
                case RED:
                    spectrum = SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\03_M1.txt")).get(0);
                    break;
                case GREEN:
                    spectrum = SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\08_M1.txt")).get(0);
                    break;
                case BLUE:
                    spectrum = SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\13_M1.txt")).get(0);
                    break;
                case WHITE:
                    spectrum = SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\50_M1.txt")).get(0);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public int getScreenColor() {
        return spectrum.getArgb();
    }

    public static Paint getDefault() {
        return new Paint(RGBColor.WHITE);
    }

    public void displayEditDialog(MainController mainController) {


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/UI/SelectSpectrum.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(mainController.getWindow());
            SelectSpectrumController ssc = (SelectSpectrumController)loader.getController();
            ssc.setOnAccept((Spectrum spectrum) -> {this.spectrum = spectrum; mainController.updateColorChange();});

            Scene dialogScene = new Scene(root, 500, 400);
            dialog.setScene(dialogScene);
            dialog.setOnCloseRequest(event -> ssc.onClose());
            dialog.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        /*FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(mainController.getWindow());
        if(file != null) {
            try {
                spectrum = SpectrumIO.loadCGATS17Spectrum(file).get(0);
            } catch (IOException ex) {
                System.err.println("Failed to load spectrum");
            }
        } else {
            System.out.println("No file selected");
        }
        mainController.updateColorChange();*/
    }

}
