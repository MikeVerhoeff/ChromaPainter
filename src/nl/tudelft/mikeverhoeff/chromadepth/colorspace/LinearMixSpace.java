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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class LinearMixSpace extends ColorSpace {

    private List<Paint> paints;

    public LinearMixSpace(List<Paint> paints) {
        this.paints = paints;
    }


    @Override
    public int getNumberOfChannels() {
        return paints.size();
    }

    @Override
    public Paint getChanelColor(int channel) {
        return paints.get(channel);
    }

    @Override
    public int getScreenColorForValues(byte[] values) {
        return getSpectrumForValues(values).getArgb();
    }

    @Override
    public Spectrum getSpectrumForValues(byte[] values) {
        int samplesize = paints.get(0).getSpectrum().getSamples().length;
        int samplestart = paints.get(0).getSpectrum().getStart();
        int samplestop = paints.get(0).getSpectrum().getStop();
        int samplestep = paints.get(0).getSpectrum().getStep();

        float[] mixresults = new float[samplesize];
        Arrays.fill(mixresults, 0.0f);

        for(int i=0; i<paints.size(); i++) {
            for(int j=0; j<mixresults.length; j++) {
                mixresults[j] += paints.get(i).getSpectrum().getSamples()[j] * Byte.toUnsignedInt(values[i]) / 255;
            }
        }
        return new Spectrum(samplestart, samplestop, samplestep, mixresults, paints.get(0).getSpectrum().getIlluminant());
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
            paints = new ArrayList<>(n);
            for(int i=0; i<n; i++) {
                paints.add(Paint.getDefault());
            }
            finish.accept(this);
        });
        VBox dialogUI = new VBox(numPaintsRow, acceptButton);

        Scene dialogScene = new Scene(dialogUI, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}
