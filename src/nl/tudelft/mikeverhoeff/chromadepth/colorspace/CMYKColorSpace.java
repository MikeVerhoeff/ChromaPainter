package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;

import java.util.ArrayList;
import java.util.List;

public class CMYKColorSpace extends ColorSpace {

    private Spectrum backgroundColor;
    private Spectrum cyan;
    private Spectrum magenta;
    private Spectrum yellow;
    private Spectrum key;

    public CMYKColorSpace() {

    }

    public Spectrum getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Spectrum backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Spectrum getCyan() {
        return cyan;
    }

    public void setCyan(Spectrum cyan) {
        this.cyan = cyan;
    }

    public Spectrum getMagenta() {
        return magenta;
    }

    public void setMagenta(Spectrum magenta) {
        this.magenta = magenta;
    }

    public Spectrum getYellow() {
        return yellow;
    }

    public void setYellow(Spectrum yellow) {
        this.yellow = yellow;
    }

    public Spectrum getKey() {
        return key;
    }

    public void setKey(Spectrum key) {
        this.key = key;
    }

    @Override
    public int getNumberOfChannels() {
        return 4;
    }

    @Override
    public int getScreenColorForValues(byte[] values) {
        return getSpectrumForValues(values).getArgb();
    }

    @Override
    public Spectrum getSpectrumForValues(byte[] values) {
        float[] resultSamples = new float[backgroundColor.getSamples().length];
        float[] interp = new float[getNumberOfChannels()];
        interp[0] = Byte.toUnsignedInt(values[0])/255.0f;
        interp[1] = Byte.toUnsignedInt(values[1])/255.0f;
        interp[2] = Byte.toUnsignedInt(values[2])/255.0f;
        interp[3] = Byte.toUnsignedInt(values[3])/255.0f;

        for (int i=0; i<resultSamples.length; i++) {
            float c = (1-interp[0]) * backgroundColor.getSamples()[i] + (interp[0]) * cyan.getSamples()[i];
            float m = (1-interp[1]) * backgroundColor.getSamples()[i] + (interp[1]) * magenta.getSamples()[i];
            float y = (1-interp[2]) * backgroundColor.getSamples()[i] + (interp[2]) * yellow.getSamples()[i];
            float k = (1-interp[3]) * backgroundColor.getSamples()[i] + (interp[3]) * key.getSamples()[i];

            resultSamples[i] = Math.min(c, Math.min(m, Math.min(y, k)));

            float background = backgroundColor.getSamples()[i];
            c = cyan.getSamples()[i]/background * interp[0] + (1-interp[0]);
            m = magenta.getSamples()[i]/background * interp[1] + (1-interp[1]);
            y = yellow.getSamples()[i]/background * interp[2] + (1-interp[2]);
            k = key.getSamples()[i]/background * interp[3] + (1-interp[3]);

            resultSamples[i] = background * c * m * y * k;
        }

        return new Spectrum(
                backgroundColor.getStart(), backgroundColor.getStop(), backgroundColor.getStep(),
                resultSamples, backgroundColor.getIlluminant()
        );
    }

    public float getMaxIntensity() {
        return Math.max(backgroundColor.getMaxSampleValue(),
                Math.max(cyan.getMaxSampleValue(),
                        Math.max(yellow.getMaxSampleValue(),
                                Math.max(cyan.getMaxSampleValue(), key.getMaxSampleValue()))));
    }
}
