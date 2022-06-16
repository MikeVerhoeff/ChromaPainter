import nl.tudelft.mikeverhoeff.chromadepth.Main;
import nl.tudelft.mikeverhoeff.chromadepth.colorspace.MixHelper;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.ColorSpaceConverter;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrumIO;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ColorCheckCMYK {

    private static final File spectrumFolder = new File(Main.SpectrumDirectory+"\\CMYK\\");

    public ColorCheckCMYK() throws IOException {
    }

    private static Spectrum load(String sample) throws IOException {
        return SpectrumIO.loadCGATS17Spectrum(new File(spectrumFolder, sample+"_M1.txt")).get(0);
    }

    // load all the samples
    Spectrum background = load("01");

    Spectrum C100 = load("02");
    Spectrum M100 = load("04");
    Spectrum Y100 = load("03");
    Spectrum K100 = load("05");

    Spectrum C100K100 = load("06");
    Spectrum Y100K100 = load("07");
    Spectrum M100K100 = load("08");

    Spectrum C100Y100 = load("09");
    Spectrum Y100M100 = load("10");
    Spectrum C100M100 = load("11");

    Spectrum C100M100Y100 = load("12");

    Spectrum C70 = load("13");
    Spectrum C30 = load("14");

    Spectrum Y70 = load("15");
    Spectrum Y30 = load("16");

    Spectrum M70 = load("17");
    Spectrum M30 = load("18");

    Spectrum K70 = load("19");
    Spectrum K30 = load("20");

    Spectrum C50K30 = load("21");
    Spectrum C80K50 = load("22");

    Spectrum Y50K30 = load("23");
    Spectrum Y80K50 = load("24");

    Spectrum M50K30 = load("25");
    Spectrum M80K50 = load("26");

    Spectrum C50Y100Error = load("27"); // probably bad, identical to next one.
    Spectrum C50Y50  = load("28");
    Spectrum C100Y50 = load("29");

    Spectrum Y50M100 = load("30");
    Spectrum Y50M50  = load("31");
    Spectrum Y100M50 = load("32");

    Spectrum C50M100 = load("33");
    Spectrum C50M50  = load("34");
    Spectrum C100M50 = load("35");

    Spectrum C80M50Y70 = load("36"); // CYM values could be off I was not careful enough with writing this one down
    Spectrum C70M60Y80 = load("37");
    Spectrum C30M50Y50 = load("38");
    Spectrum C60M30Y40 = load("39");
    Spectrum C50M90Y60 = load("40");

    @Test
    public void checkSimulationAccuracy() throws IOException {

        // initialize the simulation
        Spectrum[] nbp = MixHelper.createNeugebauerPrimaries(background, C100, M100, Y100, K100);

        // compare simulation to measurements

        printResultln("should be almost zero : to check implementation and see rounding erros");
        compare(background, nbp, 0,0,0,0);
        compare(C100, nbp, 1,0,0,0);
        compare(M100, nbp, 0,1,0,0);
        compare(Y100, nbp, 0,0,1,0);
        compare(K100, nbp, 0,0,0,1);

        printResultln("\nCheck the Neugebauer primaries");
        compare(background, nbp[0]);
        compare(C100, nbp[1]);
        compare(M100, nbp[2]);
        compare(C100M100, nbp[3]);
        compare(Y100, nbp[4]);
        compare(C100Y100, nbp[5]);
        compare(Y100M100, nbp[6]);
        compare(C100M100Y100, nbp[7]);
        compare(K100, nbp[8]);
        compare(C100K100, nbp[9]);
        compare(M100K100, nbp[10]);
        compare(Y100K100, nbp[12]);

        getAverageDeltaE(true);

        compareSamples(nbp, 1);
        System.out.println("\nAverage DeltaE with simulated nbp: "+getAverageDeltaE(true));

        tuneN(nbp);

        System.out.println("\n----------------------- Start Simulations With Measured Neurenbour Primaries -----------------------------");

        // use medusred nbp
        nbp[0] = background;
        nbp[1] = C100;
        nbp[2] = M100;
        nbp[3] = C100M100;
        nbp[4] = Y100;
        nbp[5] = C100Y100;
        nbp[6] = Y100M100;
        nbp[7] = C100M100Y100;
        nbp[8] = K100;
        nbp[9] = C100K100;
        nbp[10] = M100K100;
        nbp[12] = Y100K100;

        compareSamples(nbp, 1);
        System.out.println("\nAverage DeltaE with real nbp: "+getAverageDeltaE(true));

        float n = tuneN(nbp);

        printCompareResult = true;
        compareSamples(nbp, 2.15f);

    }

    private static boolean printCompareResult = false;

    private static void printResultln(Object o) {
        if(printCompareResult)
            System.out.println(o);
    }

    private static void printResult(Object o) {
        if(printCompareResult)
            System.out.print(o);
    }

    private float compare(Spectrum reference, Spectrum[] nbp, double C, double M, double Y, double K) {
        return compare(reference, nbp, C, M, Y, K, 1);
    }

    private float compare(Spectrum reference, Spectrum[] nbp, double C, double M, double Y, double K, float n) {
        float[] nbpMix = MixHelper.createNeugebauerMix(new float[]{(float)C, (float)M, (float)Y, (float)K}); // C, M, Y, K
        Spectrum result = MixHelper.mixNeugebauerPrimaries(nbp[0], nbp, nbpMix, n);
        printResult("C"+(int)(C*100)+" M"+(int)(M*100)+" Y"+(int)(Y*100)+" K"+(int)(K*100)+" : ");
        return compare(reference, result);
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
        printResultln(deltaE + " (DeltaE 1976)\t (reference: #"+referenceHex+", result: #"+resultHex+")");
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

    private float compareSamples(Spectrum[] nbp, float n) {
        getAverageDeltaE(true);

        printResultln("\nThe 100 mixes, Simulated using kuberka munk, no tuning yet, could be replaced with real values");
        compare(C100K100, nbp, 1,0,0,1, n);
        compare(M100K100, nbp, 0,1,0,1, n);
        compare(Y100K100, nbp, 0,0,1,1, n);

        compare(C100Y100, nbp, 1, 0, 1, 0, n);
        compare(Y100M100, nbp, 0, 1, 1, 0, n);
        compare(C100M100, nbp, 1, 1, 0, 0, n);

        compare(C100M100Y100, nbp, 1, 1, 1, 0, n);

        printResultln("\nTest intermediate Values (less bright colors)");
        compare(C70, nbp, 0.7, 0, 0, 0, n);
        compare(C30, nbp, 0.3, 0, 0, 0, n);
        compare(M70, nbp, 0, 0.7, 0, 0, n);
        compare(M30, nbp, 0, 0.3, 0, 0, n);
        compare(Y70, nbp, 0, 0, 0.7, 0, n);
        compare(Y30, nbp, 0, 0, 0.3, 0, n);
        compare(K70, nbp, 0, 0, 0, 0.7, n);
        compare(K30, nbp, 0, 0, 0, 0.3, n);


        printResultln("\nTest intermediate Values (mix with color and key)");
        compare(C50K30, nbp, 0.5, 0, 0, 0.3, n);
        compare(C80K50, nbp, 0.8, 0, 0, 0.5, n);
        compare(M50K30, nbp, 0, 0.5, 0, 0.3, n);
        compare(M80K50, nbp, 0, 0.8, 0, 0.5, n);
        compare(Y50K30, nbp, 0, 0, 0.5, 0.3, n);
        compare(Y80K50, nbp, 0, 0, 0.8, 0.5, n);

        printResultln("\nTest intermediate Values (mix 2 colors)");
        //compare(C50Y100Error, nbp, 0.5, 0, 1  , 0, n);
        compare(C50Y50 , nbp, 0.5, 0, 0.5, 0, n);
        compare(C100Y50, nbp, 1  , 0, 0.5, 0, n);

        compare(Y50M100, nbp, 0, 1  , 0.5, 0, n);
        compare(Y50M50 , nbp, 0, 0.5, 0.5, 0, n);
        compare(Y100M50, nbp, 0, 0.5, 1  , 0, n);

        compare(C50M100, nbp, 0.5, 1  , 0, 0, n);
        compare(C50M50 , nbp, 0.5, 0.5, 0, 0, n);
        compare(C100M50, nbp, 1  , 0.5, 0, 0, n);


        printResultln("\nTest intermediate Values (mix 3 colors)");
        compare(C80M50Y70, nbp, 0.8, 0.5, 0.7, 0, n);
        compare(C70M60Y80, nbp, 0.7, 0.6, 0.8, 0, n);
        compare(C30M50Y50, nbp, 0.3, 0.5, 0.5, 0, n);
        compare(C60M30Y40, nbp, 0.6, 0.3, 0.4, 0, n);
        compare(C50M90Y60, nbp, 0.5, 0.9, 0.6, 0, n);

        return getAverageDeltaE(false);
    }

    public float tuneN(Spectrum[] nbp) {
        float bestN = 0;
        float bestDeltaE = Float.POSITIVE_INFINITY;
        for(float n=0.01f; n<10; n+=0.01f) {
            float resDe = compareSamples(nbp, n);
            if(resDe < bestDeltaE) {
                bestN = n;
                bestDeltaE = resDe;
            }
        }
        System.out.println("Best n="+bestN+" (DeltaE = "+bestDeltaE+")");
        return bestN;
    }
}
