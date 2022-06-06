package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import javafx.stage.Window;
import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.function.Consumer;

public abstract class ColorSpace {

    public abstract int getNumberOfChannels();
    public abstract Paint getChanelColor(int channel);

    public abstract int getScreenColorForValues(byte[] values);

    public abstract Spectrum getSpectrumForValues(byte[] values);

    public abstract void configureGUI(Window window, Consumer<ColorSpace> afterFinish);

    public void saveToWriter(DataOutputStream writer) {};

    public void loadFromReader(DataInputStream reader) {};

    public void setBackground(Spectrum s) {}

    public void forceColorUpdate() {}
}
