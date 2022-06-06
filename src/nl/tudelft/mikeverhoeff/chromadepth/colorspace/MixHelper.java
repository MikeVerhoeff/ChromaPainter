package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;

import java.util.List;

public class MixHelper {

    private static float ks(float r) {
        return (1-r)*(1-r) / (2*r);
    }

    public static float invks(float ks) {
        return (1 + ks - (float) Math.sqrt(ks * ks + 2 * ks));
    }

    public static Spectrum mixKubelkaMunkDyes(Spectrum background, Spectrum[] dyes, byte[] values) {
        float[] resultSamples = new float[background.getSamples().length];
        float[] interp = new float[dyes.length];
        for(int i=0; i<values.length; i++) {
            interp[i] = Byte.toUnsignedInt(values[i]) / 255.0f;
        }

        for (int i=0; i<resultSamples.length; i++) {

            // implementation: kubelka-munk
            float scale = 1;

            float r_background = background.getSamples()[i] * scale;
            float ks_background = ks(r_background);

            float ks_mix = ks_background;

            for(int d = 0; d<dyes.length; d++) {
                float r_dye = dyes[d].getSamples()[i]*scale;
                if(r_dye==0) {r_dye=0.0000001f;}
                float ks_dye = ks(r_dye) - ks_background;
                ks_mix += interp[d]*ks_dye;
            }

            float r_mix = invks(ks_mix);

            resultSamples[i] = r_mix / scale;
        }

        return new Spectrum(background, resultSamples);
    }

    public static Spectrum mixYNSN(Spectrum background, Spectrum[] dyes, byte[] values) {
        double n=4;
        float scale = 1;

        if(dyes.length != 4) {
            throw new RuntimeException("YNSN currently only supports mixing 4 dyes (CMYK)");
        }
        float[] resultSamples = new float[background.getSamples().length];
        float[] interp = new float[dyes.length];
        for(int i=0; i<values.length; i++) {
            interp[i] = Byte.toUnsignedInt(values[i]) / 255.0f;
        }
        // implementation: Yule-Nielson modified Spectral Neugebauer

        float aw = (1-interp[0]) * (1-interp[1]) * (1-interp[2]) * (1-interp[3]); // white

        float ac = ( interp[0] ) * (1-interp[1]) * (1-interp[2]) * (1-interp[3]); // cyan
        float am = (1-interp[0]) * ( interp[1] ) * (1-interp[2]) * (1-interp[3]); // magenta
        float ay = (1-interp[0]) * (1-interp[1]) * ( interp[2] ) * (1-interp[3]); // yellow
        float ak = (1-interp[0]) * (1-interp[1]) * (1-interp[2]) * ( interp[3] ); // black


        float amy = (1-interp[0]) * ( interp[1] ) * ( interp[2] ) * (1-interp[3]); // red
        float acy = ( interp[0] ) * (1-interp[1]) * ( interp[2] ) * (1-interp[3]); // green
        float acm = ( interp[0] ) * ( interp[1] ) * (1-interp[2]) * (1-interp[3]); // blue
        float ack = ( interp[0] ) * (1-interp[1]) * (1-interp[2]) * ( interp[3] ); // dark cyan
        float amk = (1-interp[0]) * ( interp[1] ) * (1-interp[2]) * ( interp[3] ); // dark magenta
        float ayk = (1-interp[0]) * (1-interp[1]) * ( interp[2] ) * ( interp[3] ); // dark yellow

        float acmy = ( interp[0] ) * ( interp[1] ) * ( interp[2] ) * (1-interp[3]); // mixed black
        float acmk = ( interp[0] ) * ( interp[1] ) * (1-interp[2]) * ( interp[3] ); // dark blue
        float acyk = ( interp[0] ) * (1-interp[1]) * ( interp[2] ) * ( interp[3] ); // dark green
        float amyk = (1-interp[0]) * ( interp[1] ) * ( interp[2] ) * ( interp[3] ); // dark red

        float acmyk = ( interp[0] ) * ( interp[1] ) * ( interp[2] ) * ( interp[3] ); // pure black

        Spectrum w = background;

        Spectrum c = dyes[0];
        Spectrum m = dyes[1];
        Spectrum y = dyes[2];
        Spectrum k = dyes[3];

        Spectrum my = mixKubelkaMunkDyes(background, new Spectrum[]{m, y}, new byte[] {(byte)255, (byte)255});
        Spectrum cy = mixKubelkaMunkDyes(background, new Spectrum[]{c, y}, new byte[] {(byte)255, (byte)255});
        Spectrum cm = mixKubelkaMunkDyes(background, new Spectrum[]{c, m}, new byte[] {(byte)255, (byte)255});
        Spectrum ck = mixKubelkaMunkDyes(background, new Spectrum[]{c, k}, new byte[] {(byte)255, (byte)255});
        Spectrum mk = mixKubelkaMunkDyes(background, new Spectrum[]{m, k}, new byte[] {(byte)255, (byte)255});
        Spectrum yk = mixKubelkaMunkDyes(background, new Spectrum[]{y, k}, new byte[] {(byte)255, (byte)255});

        Spectrum cmy = mixKubelkaMunkDyes(background, new Spectrum[]{c, m, y}, new byte[] {(byte)255, (byte)255, (byte)255});
        Spectrum cmk = mixKubelkaMunkDyes(background, new Spectrum[]{c, m, k}, new byte[] {(byte)255, (byte)255, (byte)255});
        Spectrum cyk = mixKubelkaMunkDyes(background, new Spectrum[]{c, y, k}, new byte[] {(byte)255, (byte)255, (byte)255});
        Spectrum myk = mixKubelkaMunkDyes(background, new Spectrum[]{m, y, k}, new byte[] {(byte)255, (byte)255, (byte)255});

        Spectrum cmyk = mixKubelkaMunkDyes(background, new Spectrum[]{c, m, y, k}, new byte[] {(byte)255, (byte)255, (byte)255, (byte)255});

        for (int i=0; i<resultSamples.length; i++) {
            resultSamples[i] = (float) Math.pow((
                      aw*Math.pow(w.getSamples()[i]*scale, 1/n)
                    + ac*Math.pow(c.getSamples()[i]*scale, 1/n)
                    + am*Math.pow(m.getSamples()[i]*scale, 1/n)
                    + ay*Math.pow(y.getSamples()[i]*scale, 1/n)
                    + ak*Math.pow(k.getSamples()[i]*scale, 1/n)

                    + amy*Math.pow(my.getSamples()[i]*scale, 1/n)
                    + acy*Math.pow(cy.getSamples()[i]*scale, 1/n)
                    + acm*Math.pow(cm.getSamples()[i]*scale, 1/n)
                    + ack*Math.pow(ck.getSamples()[i]*scale, 1/n)
                    + amk*Math.pow(mk.getSamples()[i]*scale, 1/n)
                    + ayk*Math.pow(yk.getSamples()[i]*scale, 1/n)

                    + acmy*Math.pow(cmy.getSamples()[i]*scale, 1/n)
                    + acmk*Math.pow(cmk.getSamples()[i]*scale, 1/n)
                    + acyk*Math.pow(cyk.getSamples()[i]*scale, 1/n)
                    + amyk*Math.pow(myk.getSamples()[i]*scale, 1/n)

                    + acmyk*Math.pow(cmyk.getSamples()[i]*scale, 1/n)
            ),n)/scale;
        }
        return new Spectrum(background, resultSamples);
    }

    public static Spectrum[] createNeugebauerPrimaries(Spectrum background, Spectrum ... primaries) {
        int nbpCount = (int) Math.pow(2, primaries.length);
        Spectrum[] nbp = new Spectrum[nbpCount];

        byte[] mix = new byte[primaries.length];
        for(int i=0; i<nbpCount; i++) {

            for(int j=0; j<primaries.length; j++) {
                mix[j] = ((i&(1<<j)) != 0) ? (byte)255 : (byte)0;
            }

            nbp[i] = mixKubelkaMunkDyes(background, primaries, mix);
        }

        return nbp;
    }


    public static float[] createNeugebauerMix(byte[] baseMix) {
        float[] interp = new float[baseMix.length];
        for(int i=0; i<baseMix.length; i++) {
            interp[i] = Byte.toUnsignedInt(baseMix[i]) / 255.0f;
        }

        return createNeugebauerMix(interp);
    }
    public static float[] createNeugebauerMix(float[] baseMix) {
        int nbpCount = (int) Math.pow(2, baseMix.length);
        float[] nbpMix = new float[nbpCount];

        /*float[] interp = new float[baseMix.length];
        for(int i=0; i<baseMix.length; i++) {
            interp[i] = Byte.toUnsignedInt(baseMix[i]) / 255.0f;
        }*/

        for(int i=0; i<nbpCount; i++) {

            nbpMix[i] = 1;
            for (int j = 0; j < baseMix.length; j++) {
                nbpMix[i] *= ((i&(1<<j)) != 0) ? baseMix[j] : (1-baseMix[j]);
            }
        }
        return nbpMix;
    }

    public static Spectrum mixNeugebauerPrimaries(Spectrum background, Spectrum[] primaries, float[] mix) {
        return mixNeugebauerPrimaries(background, primaries, mix, 1);
    }

    public static Spectrum mixNeugebauerPrimaries(Spectrum background, Spectrum[] primaries, float[] mix, float n) {
        float[] resultSamples = new float[background.getSamples().length];
        for (int i=0; i<resultSamples.length; i++) {

            resultSamples[i] = 0;
            for(int j=0; j< primaries.length; j++) {
                resultSamples[i] += Math.pow(primaries[j].getSamples()[i], 1.0/n) * mix[j];
            }
            resultSamples[i] = (float)Math.pow(resultSamples[i], n);
        }
        return new Spectrum(background, resultSamples);
    }

}
