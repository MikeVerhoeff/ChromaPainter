package nl.tudelft.mikeverhoeff.chromadepth.spectra;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SpectrogramChart extends AnchorPane {

    private Canvas chartCanvas;
    private Button exportButton;

    float maxValue = -1;

    public SpectrogramChart() {
        this.setWidth(200);
        this.setHeight(200);
        chartCanvas = new Canvas(200, 200);
        //System.out.println("chart test");
        this.getChildren().add(chartCanvas);
        AnchorPane.setTopAnchor(chartCanvas, 0.0);
        AnchorPane.setLeftAnchor(chartCanvas, 0.0);

        this.exportButton = new Button("export");
        this.getChildren().add(exportButton);
        AnchorPane.setTopAnchor(exportButton, 5.0);
        AnchorPane.setLeftAnchor(exportButton, 5.0);
        exportButton.setVisible(false);
        exportButton.setOnAction(this::exportButtonAction);

        this.setOnMouseEntered(e->exportButton.setVisible(true));
        this.setOnMouseExited(e->exportButton.setVisible(false));

    }

    private void exportButtonAction(ActionEvent e) {
        exportButton.setVisible(false);
        WritableImage snapshot = chartCanvas.snapshot(null, null);
        exportButton.setVisible(true);
        FileChooser exportLocationChooser = new FileChooser();
        File exportLocation = exportLocationChooser.showSaveDialog(exportButton.getScene().getWindow());
        if(exportLocation != null) {
            try {
                System.out.println("Exporting to: "+exportLocation.getAbsolutePath());
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", exportLocation);
            } catch (IOException exception) {
                System.err.println("Could not save chart to: "+exportLocation.getAbsolutePath()+" ("+exception.getLocalizedMessage()+")");
            }
        }
    }

    public void setMaxValue(float value) {
        maxValue = value;
    }

    public void displayColorSpectrum(Spectrum spectrum) {
        double width = chartCanvas.getWidth();
        double height = chartCanvas.getHeight();

        float maxIntensity = 0;
        if(maxValue>0) {
            maxIntensity = maxValue;
        } else {
            for (int i = 0; i < spectrum.getSamples().length; i++) {
                if (spectrum.getSamples()[i] > maxIntensity) {
                    maxIntensity = spectrum.getSamples()[i];
                }
            }
        }

        if(maxIntensity == 0) {
            chartCanvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            return;
        }

        double widthStep = width/spectrum.getSamples().length;
        double heightStep = height/maxIntensity;

        GraphicsContext draw = chartCanvas.getGraphicsContext2D();
        draw.clearRect(0, 0, width, height);
        for(int x=0; x<width; x++) {
            int wavelength = (int)(spectrum.getStart() + x/widthStep * spectrum.getStep());
            double pos = x/widthStep;
            int i = (int)pos;
            pos = pos-i;
            float y = spectrum.getSamples()[i];
            if(spectrum.getSamples().length>i+1) {
                y = (float) ((1-pos)*y + pos*spectrum.getSamples()[i+1]);
            }

            int argb = ColorMatchingFunctions.getColorForWavelength(wavelength);
            draw.setFill(Color.rgb(argb>>16 & 0xff, argb>>8 & 0xff, argb & 0xff));
            draw.fillRect(x, height-y*heightStep, 1, height);
        }
        draw.setFill(Color.BLACK);
        draw.fillOval(5,5,5,5);
        for (int i = 0; i < spectrum.getSamples().length; i++) {
            draw.fillOval(i*widthStep, height-spectrum.getSamples()[i]*heightStep, 3, 3);
        }
    }

}
