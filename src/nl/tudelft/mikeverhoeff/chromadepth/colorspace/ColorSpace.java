package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;

public abstract class ColorSpace {

    public abstract int getNumberOfChannels();
    public abstract Paint getChanelColor(int channel);

    public abstract int getScreenColorForValues(byte[] values);

    public abstract Spectrum getSpectrumForValues(byte[] values);

}
