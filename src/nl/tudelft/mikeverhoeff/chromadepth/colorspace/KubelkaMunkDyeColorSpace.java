package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrumIO;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.NewCanvasDialogController;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.PaintSlider;
import nl.tudelft.mikeverhoeff.chromadepth.util.MainControllerWindowWrapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KubelkaMunkDyeColorSpace extends ColorSpace {

    private List<Paint> dyes;
    private Paint background;

    public KubelkaMunkDyeColorSpace() {
        background = Paint.getDefault();
    }

    public KubelkaMunkDyeColorSpace(Paint background, List<Paint> dyes) {
        this.background = background;
        this.dyes = dyes;
    }

    public List<Paint> getDyes() {
        return dyes;
    }

    public void setDyes(List<Paint> dyes) {
        this.dyes = dyes;
    }

    public Paint getBackground() {
        return background;
    }

    public void setBackground(Paint background) {
        this.background = background;
    }

    @Override
    public int getNumberOfChannels() {
        return dyes.size();
    }

    @Override
    public Paint getChanelColor(int channel) {
        return dyes.get(channel);
    }

    @Override
    public int getScreenColorForValues(byte[] values) {
        return getSpectrumForValues(values).getArgb();
    }

    @Override
    public Spectrum getSpectrumForValues(byte[] values) {
        Spectrum[] spectrums = new Spectrum[dyes.size()];
        for(int i=0; i<spectrums.length; i++) {
            spectrums[i] = dyes.get(i).getSpectrum();
        }
        return MixHelper.mixKubelkaMunkDyes(background.getSpectrum(), spectrums, values);
    }

    @Override
    public void configureGUI(Window window, Consumer<ColorSpace> finish) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(window);

        Label numPaintsText = new Label("Number of dyes:");
        Spinner<Integer> numPaintsInput = new Spinner<Integer>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,Integer.MAX_VALUE,6));
        HBox numPaintsRow = new HBox(numPaintsText, numPaintsInput);

        Label backgroundText = new Label("Background Color:");
        PaintSlider backgroundPaint = new PaintSlider(background, (byte)0);
        backgroundPaint.setMainController(new MainControllerWindowWrapper(window, backgroundPaint::updateColorChange));
        HBox backgroundRow = new HBox(backgroundText, backgroundPaint);

        Button acceptButton = new Button("Okay");
        acceptButton.setOnAction(e -> {
            int n = numPaintsInput.getValue();
            dyes = new ArrayList<>(n);
            for(int i=0; i<n; i++) {
                dyes.add(Paint.getDefault());
            }
            finish.accept(this);
        });
        VBox dialogUI = new VBox(numPaintsRow, backgroundRow, acceptButton);

        Scene dialogScene = new Scene(dialogUI, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @Override
    public void setBackground(Spectrum s) {
        background.setSpectrum(s);
    }


    @Override
    public void saveToWriter(DataOutputStream writer) {
        try {
            SpectrumIO.saveToWriter(background.getSpectrum(), writer);
            writer.writeInt(dyes.size());
            for(Paint dye : dyes) {
                SpectrumIO.saveToWriter(dye.getSpectrum(), writer);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void loadFromReader(DataInputStream reader) {
        try {
            background = new Paint(SpectrumIO.loadFromReader(reader));
            int count = reader.readInt();
            dyes = new ArrayList<Paint>(count);
            for(int i=0; i<count; i++) {
                Paint dye = new Paint(SpectrumIO.loadFromReader(reader));
                dyes.add(dye);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
