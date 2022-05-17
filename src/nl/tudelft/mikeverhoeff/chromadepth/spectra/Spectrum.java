package nl.tudelft.mikeverhoeff.chromadepth.spectra;

import nl.tudelft.mikeverhoeff.chromadepth.util.Pair;

import java.util.Arrays;

import static nl.tudelft.mikeverhoeff.chromadepth.spectra.ColorMatchingFunctions.*;

public class Spectrum {

    private int start;
    private int stop;
    private int step;

    private float[] samples;

    private int argb = 0;

    private float[] refXYZ;
    private float[] XYZ;
    private String illuminant;

    public Spectrum(int start, int stop, int step, float[] samples) {
        init(start, stop, step, samples);
    }

    public Spectrum(int start, int stop, int step, float[] samples, String illuminant) {
        this.illuminant = illuminant;
        init(start, stop, step, samples);
    }

    public Spectrum(int start, int stop, int step, float[] samples, float Xref, float Yref, float Zref) {
        setRefXYZ(Xref, Yref, Zref);
        init(start, stop, step, samples);
    }

    public Spectrum(int start, int stop, int step, float[] samples, float Xref, float Yref, float Zref, String illuminant) {
        this.illuminant = illuminant;
        setRefXYZ(Xref, Yref, Zref);
        init(start, stop, step, samples);
    }

    public Spectrum(Spectrum metareference, float[] samples) {
        this.start = metareference.getStart();
        this.stop = metareference.getStop();
        this.step = metareference.getStep();
        this.illuminant = metareference.getIlluminant();
        init(start, stop, step, samples);
    }

    private void init(int start, int stop, int step, float[] samples) {
        this.start = start;
        this.stop = stop;
        this.step = step;
        this.samples = samples;
        argb = toRGB();
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

    public int getStep() {
        return step;
    }

    public float[] getSamples() {
        return samples;
    }

    public float getMaxSampleValue() {
        float max = 0;
        for(float value:samples) {
            max = Math.max(max, value);
        }
        return max;
    }

    public int getArgb() {
        return argb;
    }

    public void setRefXYZ(float X, float Y, float Z) {
        this.refXYZ = new float[]{X, Y, Z};
    }

    @Override
    public String toString() {
        return "Spectrum: "+start+"nm to "+stop+"nm "+Arrays.toString(samples);
    }

    private int toRGB() {

        // calculate XYZ
        float X=0;
        float Y=0;
        float Z=0;
        float N=0;

        if (illuminant != null) {
            // calculate white point : my samples were reflective measured under D50
            Pair<Double, Double> xyD = colorTempToxyColor(5000);
            double xd = xyD.getA();
            double yd = xyD.getB();
            double M = M(xd, yd);
            double M1 = M1(xd, yd, M);
            double M2 = M2(xd, yd, M);

            for (int j = 0; j < samples.length; j++) {
                float sample = samples[j];
                int sampleWavelength = start + j * step;
                float sd = (float) Sd(sampleWavelength, M1, M2);
                X += X(sampleWavelength) * sd * sample * step;
                Y += Y(sampleWavelength) * sd * sample * step;
                Z += Z(sampleWavelength) * sd * sample * step;
                N += Y(sampleWavelength) * sd * step;
            }
            X = 1/N * X;
            Y = 1/N * Y;
            Z = 1/N * Z;
        } else {
            for (int j = 0; j < samples.length; j++) {
                float sample = samples[j];
                int sampleWavelength = start + j * step;
                X += X(sampleWavelength) * sample * step;
                Y += Y(sampleWavelength) * sample * step;
                Z += Z(sampleWavelength) * sample * step;
            }
        }
        float mag = 1;//(float)Math.sqrt(X*X+Y*Y+Z*Z);
        /*if(refXYZ != null) {
            mag = Y/refXYZ[1];
        }
        System.out.println(mag);*/
        X=X/mag;
        Y=Y/mag;
        Z=Z/mag;
        XYZ = new float[] {X, Y, Z};

        // update samles to reflect correct magnetude
        if(mag != 1) {
            for(int i=0; i<samples.length; i++) {
                samples[i] = samples[i]/mag;
            }
        }

        if(refXYZ != null && refXYZ.length>=3) {
            System.out.println("XYZ (cal): "+X+", "+Y+", "+Z);
            System.out.println("XYZ (ref): " + refXYZ[0] + ", " + refXYZ[1] + ", " + refXYZ[2]);
            System.out.println("XYZ (delta): " + (X - refXYZ[0]) + ", " + (Y - refXYZ[1]) + ", " + (Z - refXYZ[2]));
        }

        // D65 conversion
        float r = +3.2406f*X -1.5372f*Y -0.4986f*Z;
        float g = -0.9689f*X +1.8758f*Y +0.0415f*Z;
        float b = +0.0557f*X -0.2040f*Y +1.0570f*Z;
        r = gammaCorrect(r)*255;
        g = gammaCorrect(g)*255;
        b = gammaCorrect(b)*255;

        // calmp the values
        if(r>255) {r=255;}
        if(r<0) {r=0;}
        if(g>255) {g=255;}
        if(g<0) {g=0;}
        if(b>255) {b=255;}
        if(b<0) {b=0;}

        //System.out.println("RGB: ("+(int)r+", "+(int)g+", "+(int)b+")");

        return 0xff<<24 | (((int)r)&0xff)<<16 | (((int)g)&0xff)<<8 | ((int)b)&0xff;
    }

    private float gammaCorrect(float c) {
        if(c<=0.0031308) {
            return Math.min(1.0f,12.92f*c);
        } else {
            return (float) Math.min(1.0,1.055 * Math.pow(c, 1/2.4) - 0.055);
        }
    }

    public String getIlluminant() {
        return illuminant;
    }

    public float[] getXYZ() {
        return XYZ;
    }
}
