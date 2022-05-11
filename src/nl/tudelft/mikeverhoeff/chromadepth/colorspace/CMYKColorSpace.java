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
    public Paint getChanelColor(int channel) {
        if(channel==0) {
            return new Paint(cyan);
        } else if (channel==1) {
            return new Paint(magenta);
        } else if (channel==2) {
            return new Paint(yellow);
        } else if (channel==3) {
            return new Paint(key);
        } else {
            return Paint.getDefault();
        }
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

            // implementation: kubelka-munk
            float scale = 10000;

            float r_p = backgroundColor.getSamples()[i] * scale;
            float r_c = cyan.getSamples()[i] * scale;
            float r_m = magenta.getSamples()[i] * scale;
            float r_y = yellow.getSamples()[i] * scale;
            float r_k = key.getSamples()[i] * scale;

            float ks_p = (1-r_p)*(1-r_p)/(2*r_p);
            float ks_c = (1-r_c)*(1-r_c)/(2*r_c) - ks_p;
            float ks_m = (1-r_m)*(1-r_m)/(2*r_m) - ks_p;
            float ks_y = (1-r_y)*(1-r_y)/(2*r_y) - ks_p;
            float ks_k = (1-r_k)*(1-r_k)/(2*r_k) - ks_p;

            float ks_mix = ks_p + (interp[0]*ks_c + interp[1]*ks_m + interp[2]*ks_y + interp[3]*ks_k);

            float r_mix = (1 + ks_mix - (float)Math.sqrt(ks_mix*ks_mix + 2*ks_mix)) / scale;

            resultSamples[i] = r_mix;

            // implementation: Yule-Nielson modified Spectral Neugebauer

            float aw = (1-interp[0]) * (1-interp[1]) * (1-interp[2]) * (1-interp[3]); // white

            float ac = ( interp[0] ) * (1-interp[1]) * (1-interp[2]) * (1-interp[2]); // cyan
            float am = (1-interp[0]) * ( interp[1] ) * (1-interp[2]) * (1-interp[2]); // magenta
            float ay = (1-interp[0]) * (1-interp[1]) * ( interp[2] ) * (1-interp[2]); // yellow
            float ak = (1-interp[0]) * (1-interp[1]) * (1-interp[2]) * ( interp[3] ); // black


            float amy = (1-interp[0]) * ( interp[1] ) * ( interp[2] ) * (1-interp[2]); // red
            float acy = ( interp[0] ) * (1-interp[1]) * ( interp[2] ) * (1-interp[2]); // green
            float acm = ( interp[0] ) * ( interp[1] ) * (1-interp[2]) * (1-interp[2]); // blue
            float ack = ( interp[0] ) * (1-interp[1]) * (1-interp[2]) * ( interp[3] ); // dark cyan
            float amk = (1-interp[0]) * ( interp[1] ) * (1-interp[2]) * ( interp[3] ); // dark magenta
            float ayk = (1-interp[0]) * (1-interp[1]) * ( interp[2] ) * ( interp[3] ); // dark yellow

            float acmy = ( interp[0] ) * ( interp[1] ) * ( interp[2] ) * (1-interp[3]); // mixed black
            float acmk = ( interp[0] ) * ( interp[1] ) * (1-interp[2]) * ( interp[3] ); // dark blue
            float acyk = ( interp[0] ) * (1-interp[1]) * ( interp[2] ) * ( interp[3] ); // dark green
            float amyk = (1-interp[0]) * ( interp[1] ) * ( interp[2] ) * ( interp[3] ); // dark red

            float acmyk = ( interp[0] ) * ( interp[1] ) * ( interp[2] ) * ( interp[3] ); // pure black


            // implementation: minimun (decent color, bad spectrum)
            float c = (1-interp[0]) * backgroundColor.getSamples()[i] + (interp[0]) * cyan.getSamples()[i];
            float m = (1-interp[1]) * backgroundColor.getSamples()[i] + (interp[1]) * magenta.getSamples()[i];
            float y = (1-interp[2]) * backgroundColor.getSamples()[i] + (interp[2]) * yellow.getSamples()[i];
            float k = (1-interp[3]) * backgroundColor.getSamples()[i] + (interp[3]) * key.getSamples()[i];

            //resultSamples[i] = Math.min(c, Math.min(m, Math.min(y, k)));

            // implementation: scale (bad color, decent spectrum)
            float background = backgroundColor.getSamples()[i];
            c = cyan.getSamples()[i]/background * interp[0] + (1-interp[0]);
            m = magenta.getSamples()[i]/background * interp[1] + (1-interp[1]);
            y = yellow.getSamples()[i]/background * interp[2] + (1-interp[2]);
            k = key.getSamples()[i]/background * interp[3] + (1-interp[3]);

            //resultSamples[i] = background * c * m * y * k;
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
