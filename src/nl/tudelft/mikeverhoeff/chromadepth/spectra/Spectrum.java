package nl.tudelft.mikeverhoeff.chromadepth.spectra;

import java.util.Arrays;

public class Spectrum {

    private int start;
    private int stop;
    private int step;

    private float[] samples;

    private int argb = 0;

    private float[] refXYZ;

    public Spectrum(int start, int stop, int step, float[] samples) {
        this.start = start;
        this.stop = stop;
        this.step = step;
        this.samples = samples;
        argb = toRGB();
    }

    public Spectrum(int start, int stop, int step, float[] samples, float Xref, float Yref, float Zref) {
        this.start = start;
        this.stop = stop;
        this.step = step;
        this.samples = samples;
        setRefXYZ(Xref, Yref, Zref);
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
        for(int j=0; j<samples.length; j++) {
            float sample = samples[j];
            int sampleWavelength = start+j*step;
            /*for(int i=0; i<step; i++) { // could improve interpolation
                X += ColorMatchingFunctions.X(sampleWavelength+i-step/2)*sample;
                Y += ColorMatchingFunctions.Y(sampleWavelength+i-step/2)*sample;
                Z += ColorMatchingFunctions.Z(sampleWavelength+i-step/2)*sample;
            }*/
            X += ColorMatchingFunctions.X(sampleWavelength)*sample*step;
            Y += ColorMatchingFunctions.Y(sampleWavelength)*sample*step;
            Z += ColorMatchingFunctions.Z(sampleWavelength)*sample*step;
        }
        float mag = 1;//(float)Math.sqrt(X*X+Y*Y+Z*Z);
        if(refXYZ != null) {
            mag = Y/refXYZ[1];
        }
        X=X/mag;
        Y=Y/mag;
        Z=Z/mag;

        // update samles to reflect correct magnetude
        if(mag != 1) {
            for(int i=0; i<samples.length; i++) {
                samples[i] = samples[i]/mag;
            }
        }

        //System.out.println("XYZ (cal): "+X+", "+Y+", "+Z);
        //if(refXYZ != null && refXYZ.length>=3)
        //    System.out.println("XYZ (ref): "+refXYZ[0]+", "+refXYZ[1]+", "+refXYZ[2]);

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
}