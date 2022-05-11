package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;

import java.util.Arrays;
import java.util.List;

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
}
