package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.PaintSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AdditiveColorSpace extends ColorSpace {

    private List<Paint> lights;

    public AdditiveColorSpace() {

    }

    public AdditiveColorSpace(List<Paint> lights) {
        this.lights = lights;
    }

    public List<Paint> getLights() {
        return lights;
    }

    public void setLights(List<Paint> lights) {
        this.lights = lights;
    }

    @Override
    public int getNumberOfChannels() {
        return lights.size();
    }

    @Override
    public Paint getChanelColor(int channel) {
        return lights.get(channel);
    }

    @Override
    public int getScreenColorForValues(byte[] values) {
        return getSpectrumForValues(values).getArgb();
    }

    @Override
    public Spectrum getSpectrumForValues(byte[] values) {
        float[] resultSamples = new float[lights.get(0).getSpectrum().getSamples().length];
        float[] interp = new float[lights.size()];
        for(int i=0; i<values.length; i++) {
            interp[i] = Byte.toUnsignedInt(values[i]) / 255.0f;
        }

        for (int i=0; i<resultSamples.length; i++) {
            for(int j=0; i<values.length; i++) {
                resultSamples[i] += interp[j] * lights.get(j).getSpectrum().getSamples()[i];
            }
        }

        return new Spectrum(lights.get(0).getSpectrum(), resultSamples);
    }

    @Override
    public void configureGUI(Window window, Consumer<ColorSpace> finish) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(window);

        Label numPaintsText = new Label("Number of dyes:");
        Spinner<Integer> numPaintsInput = new Spinner<Integer>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,Integer.MAX_VALUE,6));
        HBox numPaintsRow = new HBox(numPaintsText, numPaintsInput);


        Button acceptButton = new Button("Okay");
        acceptButton.setOnAction(e -> {
            int n = numPaintsInput.getValue();
            lights = new ArrayList<>(n);
            for(int i=0; i<n; i++) {
                lights.add(Paint.getDefault());
            }
            ((Stage)window).close();
            finish.accept(this);
        });
        VBox dialogUI = new VBox(numPaintsRow, acceptButton);

        Scene dialogScene = new Scene(dialogUI, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}
