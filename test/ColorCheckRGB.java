import nl.tudelft.mikeverhoeff.chromadepth.Main;
import nl.tudelft.mikeverhoeff.chromadepth.colorspace.MixHelper;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.ColorSpaceConverter;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrumIO;
import nl.tudelft.mikeverhoeff.chromadepth.util.Pair;
import org.junit.Test;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorCheckRGB {

    private static final File spectrumFolder = new File(Main.SpectrumDirectory+"\\");

    //private List<Pair<String, Spectrum>> colorSamples;
    private Map<String, Spectrum> colorSamples;

    ICC_ColorSpace colorConverterCMYK = new ICC_ColorSpace(ICC_Profile.getInstance("C:\\Users\\Mike\\Desktop\\Research Project\\Programming\\ChromaPainter\\src\\res\\SWOP2006_Coated5_GCR_bas.icc"));

    public ColorCheckRGB() throws IOException {
        //colorSamples = new ArrayList<>(50);
        colorSamples = new HashMap<>();

        load("FFFF00", "01");
        load("FF8800", "02");
        load("FF0000", "03");
        load("FF0088", "04");
        load("FF00FF", "05");

        load("00FFFF", "06");
        load("00FF88", "07");
        load("00FF00", "08");
        load("88FF00", "09");
        load("FFFF00", "10");

        load("00FFFF", "11");
        load("0088FF", "12");
        load("0000FF", "13");
        load("8800FF", "14");
        load("FF00FF", "15");



        load("555500", "16");
        load("AAAA00", "17");
        load("FFFF00", "18");
        load("FFFF55", "19");
        load("FFFFAA", "20");

        load("550000", "21");
        load("AA0000", "22");
        load("FF0000", "23");
        load("FF5555", "24");
        load("FFAAAA", "25");

        load("550055", "26");
        load("AA00AA", "27");
        load("FF00FF", "28");
        load("FF55FF", "29");
        load("FFAAFF", "30");

        load("000055", "31");
        load("0000AA", "32");
        load("0000FF", "33");
        load("5555FF", "34");
        load("AAAAFF", "35");

        load("005555", "36");
        load("00AAAA", "37");
        load("00FFFF", "38");
        load("55FFFF", "39");
        load("AAFFFF", "40");

        load("005500", "41");
        load("00AA00", "42");
        load("00FF00", "43");
        load("55FF55", "44");
        load("AAFFAA", "45");



        load("000000", "46");
        load("444444", "47");
        load("888888", "48");
        load("CCCCCC", "49");
        load("FFFFFF", "50");
    }

    private static Spectrum load(String sample) throws IOException {
        return SpectrumIO.loadCGATS17Spectrum(new File(spectrumFolder, sample+"_M1.txt")).get(0);
    }

    private void load(String hex, String sample) throws IOException {
        colorSamples.put(hex, load(sample));
        //colorSamples.add(new Pair<>(hex,  load(sample)));
    }


    @Test
    public void checkRGBAccuracy() {
        Spectrum background = colorSamples.get("FFFFFF");
        Spectrum cyan       = colorSamples.get("00FFFF");
        Spectrum magenta    = colorSamples.get("FF00FF");
        Spectrum yellow     = colorSamples.get("FFFF00");
        Spectrum key        = colorSamples.get("000000");

        Spectrum[] nbp = MixHelper.createNeugebauerPrimaries(background, cyan, magenta, yellow, key);

        compareSamples(nbp, 1, false);
        System.out.println("Average DeltaE (n=1): "+getAverageDeltaE(true));

        float bestN = tuneN(nbp, false);

        //doPrintCompare = true;
        //compareSamples(nbp, bestN, false);

        System.out.print("\n Using ICC profile");

        float bestN_ICC = tuneN(nbp, true);

        System.out.println("\nUsing more red, green and blue in Neurenbour primaries");
        //    KYMC
        nbp[0b0011] = colorSamples.get("0000FF");
        nbp[0b0101] = colorSamples.get("00FF00");
        nbp[0b0110] = colorSamples.get("FF0000");
//        nbp[0b1001] = colorSamples.get("005555");
//        nbp[0b1010] = colorSamples.get("550055");
//        nbp[0b1100] = colorSamples.get("555500");
//        nbp[0b1110] = colorSamples.get("550000");
//        nbp[0b1101] = colorSamples.get("005500");
//        nbp[0b1011] = colorSamples.get("000055");
        float bestN_RGB = tuneN(nbp, false);

        nbp = MixHelper.createNeugebauerPrimaries(background, cyan, magenta, yellow, key);
        findBestMix(nbp, colorSamples.get("0000FF"), bestN);
    }

    public float tuneN(Spectrum[] nbp, boolean useICC) {
        float bestN = 0;
        float bestDeltaE = Float.POSITIVE_INFINITY;
        for(float n=0.01f; n<10; n+=0.01f) {
            float resDe = compareSamples(nbp, n, useICC);
            if(resDe < bestDeltaE) {
                bestN = n;
                bestDeltaE = resDe;
            }
        }
        System.out.println("Best n="+bestN+" (DeltaE = "+bestDeltaE+")");
        return bestN;
    }

    public Spectrum mixColor(String hex, Spectrum[] nbp, float n, boolean useICC) {
        int rgb = Integer.parseInt(hex, 16);
        float r = ((rgb >> (2*8))&0xff) / 255.0f;
        float g = ((rgb >> (1*8))&0xff) / 255.0f;
        float b = ((rgb >> (0*8))&0xff) / 255.0f;

        float k = 1 - Math.max(r, Math.max(g, b));
        float c = (1 - r - k);
        float m = (1 - g - k);
        float y = (1 - b - k);

        if (colorConverterCMYK != null && useICC) {
            float[] cmyk = colorConverterCMYK.fromRGB(new float[] {r, g, b});
            c = cmyk[0];
            m = cmyk[1];
            y = cmyk[2];
            k = cmyk[3];
        }

        float[] nbpMix = MixHelper.createNeugebauerMix(new float[]{c, m, y, k});
        Spectrum result = MixHelper.mixNeugebauerPrimaries(nbp[0], nbp, nbpMix, n);
        return result;
    }

    private static float deltaESum = 0;
    private static int deltaECount = 0;

    private float compare(Spectrum reference, Spectrum result) {
        float deltaE = ColorSpaceConverter.deltaE_1976_FromLab(
                ColorSpaceConverter.XYZtoLab(reference.getXYZ()),
                ColorSpaceConverter.XYZtoLab(result.getXYZ())
        );
        String referenceHex = Integer.toHexString(reference.getArgb()).substring(2, 8);
        String resultHex = Integer.toHexString(result.getArgb()).substring(2, 8);
        printCompare(deltaE + " (DeltaE 1976)\t (reference: #"+referenceHex+", result: #"+resultHex+")");
        deltaESum += deltaE;
        deltaECount++;
        return deltaE;
    }

    private float getAverageDeltaE(boolean doReset) {
        float avg = deltaESum/deltaECount;
        if(doReset) {
            deltaECount = 0;
            deltaESum = 0;
        }
        return avg;
    }

    public boolean doPrintCompare = false;
    public void printComparePre(Object o) {
        if(doPrintCompare)
            System.out.print(o);
    }

    public void printCompare(Object o) {
        if(doPrintCompare)
            System.out.println(o);
    }

    public float compareSamples(Spectrum[] nbp, float n, boolean useICC) {
        getAverageDeltaE(true);

        for(Map.Entry<String, Spectrum> color : colorSamples.entrySet()) {

            Spectrum result = mixColor(color.getKey(), nbp, n, useICC);

            printComparePre("#"+color.getKey()+" : ");
            compare(color.getValue(), result);
        }

        return getAverageDeltaE(false);
    }

    public void findBestMix(Spectrum[] nbp, Spectrum target, float n) {
        float bestC = 0, bestM = 0, bestY = 0, bestK = 0;
        float bestDeltaE = Float.POSITIVE_INFINITY;
        int size = 64;
        for(int c=0; c<=size; c++) {
            for(int m=0; m<=size; m++) {
                for(int y=0; y<=size; y++) {
                    for(int k=0; k<=size; k++) {
                        float[] nbMix = MixHelper.createNeugebauerMix(new float[] {c/(float)size, m/(float)size, y/(float)size, k/(float)size});
                        Spectrum result = MixHelper.mixNeugebauerPrimaries(nbp[0], nbp, nbMix);
                        float de = compare(target, result);
                        if(de < bestDeltaE) {
                            bestDeltaE = de;
                            bestC = c;
                            bestM = m;
                            bestY = y;
                            bestK = k;
                        }
                        //System.out.println((k+255*y+255*255*m+255*255*255*c)/(float)(255*255*255*255) * 100);
                    }
                }
                //System.out.println("m"+m);
            }
            System.out.println("c"+c);
        }
        System.out.println(Integer.toHexString(target.getArgb())+" has CMYK mix: ("+(bestC*100)+", "+(bestM*100)+", "+(bestY*100)+", "+(bestK*100)+")");
    }
}
