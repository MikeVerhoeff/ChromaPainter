import nl.tudelft.mikeverhoeff.chromadepth.spectra.ColorMatchingFunctions;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrumIO;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class QuickTryOut {


    @Test
    public void quickTest() throws IOException {
        Spectrum yellow = SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\01_M1.txt")).get(0);
        int argb = yellow.getArgb();
        System.out.println("(yello) RGB: " + ((argb>>16)&0xff) + ", " + ((argb>>8)&0xff) + ", " + ((argb)&0xff));

        System.out.println("\n------\n");

        Spectrum white = SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\50_M1.txt")).get(0);
        argb = white.getArgb();
        System.out.println("(white) RGB: " + ((argb>>16)&0xff) + ", " + ((argb>>8)&0xff) + ", " + ((argb)&0xff));

        System.out.println("\n------\n");

        Spectrum black = SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\46_M1.txt")).get(0);
        argb = black.getArgb();
        System.out.println("(black) RGB: " + ((argb>>16)&0xff) + ", " + ((argb>>8)&0xff) + ", " + ((argb)&0xff));
    }

    @Test
    public void wavelengthColorTest() {
        ColorMatchingFunctions.getColorForWavelength(700);
    }

    @Test
    public void gammaCorrectTest() {
        float original = 0.5f;
        float corrected = ColorMatchingFunctions.gammaCorrect(original);
        float inverted = ColorMatchingFunctions.inverseGammaCorrect(corrected);
        System.out.println(original + " -> " + corrected + " -> " + inverted);
    }

}
