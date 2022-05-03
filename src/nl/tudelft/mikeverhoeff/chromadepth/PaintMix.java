package nl.tudelft.mikeverhoeff.chromadepth;

import javafx.scene.paint.Color;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static nl.tudelft.mikeverhoeff.chromadepth.Paint.RGBColor.RED;

public class PaintMix {

    private List<Paint> paints;
    private byte[] values;

    public PaintMix(List<Paint> paints, byte... values) {
        this.paints = paints;
        this.values = values;
        if ( paints.size() != values.length )
            throw new RuntimeException("Number of paints should match number of values");
    }

    public List<Paint> getPaints() {
        return paints;
    }

    public byte[] getValues() {
        return values;
    }

    public int getScreenColor() {
        return getSpectrum().getArgb();
        /*int r = 0;
        int g = 0;
        int b = 0;
        float R = 0;
        float G = 0;
        float B = 0;
        for(int i=0; i<paints.size(); i++) {
            int paintColor = paints.get(i).getScreenColor();
            R += ((paintColor>>16)&0xff)/255.0f * Byte.toUnsignedInt(values[i])/255.0f;
            G += ((paintColor>>8 )&0xff)/255.0f * Byte.toUnsignedInt(values[i])/255.0f;
            B += ((paintColor    )&0xff)/255.0f * Byte.toUnsignedInt(values[i])/255.0f;
            //switch (paints.get(i).color) {
            //    case RED:
            //        r += Byte.toUnsignedInt(values[i]);
            //        break;
            //    case BLUE:
            //        b += Byte.toUnsignedInt(values[i]);
            //        break;
            //    case GREEN:
            //        g += Byte.toUnsignedInt(values[i]);
            //        break;
            //    case WHITE:
            //        r += Byte.toUnsignedInt(values[i]);
            //        g += Byte.toUnsignedInt(values[i]);
            //        b += Byte.toUnsignedInt(values[i]);
            //        break;
            //}
        }
        r=(int)(R*255);
        g=(int)(G*255);
        b=(int)(B*255);
        int a = 0xff;
        if (r>255) {
            r=255;
        }
        if (g>255) {
            g=255;
        }
        if (b>255) {
            b=255;
        }
        int result = 0xFF000000| (r&0xFF)<<16 | (g&0xFF)<<8 | (b&0xFF);
        return result;/**/
    }

    public Spectrum getSpectrum() {
        //TODO: correct mixing

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
        return new Spectrum(samplestart, samplestop, samplestep, mixresults);
    }
}
