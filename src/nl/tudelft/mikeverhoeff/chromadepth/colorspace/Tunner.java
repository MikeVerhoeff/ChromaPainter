package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrumIO;
import nl.tudelft.mikeverhoeff.chromadepth.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tunner {

    MyPrinterSimulator colorMixer;

    List<Pair<Spectrum, Integer>> referencePoints;
    List<String> filenumbers;
    List<Integer> originalColors;

    File basefolder = new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra");

    public void loadSampleList() {
        filenumbers = new ArrayList<>(Arrays.asList(
                "01", "02", "03", "04", "05",
                "06", "07", "08", "09", "10",
                "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25",
                "26", "27", "28", "29", "30",
                "31", "32", "33", "34", "35",
                "36", "37", "38", "39", "40",
                "41", "42", "43", "44", "45",
                "46", "47", "48", "49", "50"
        ));
        originalColors = new ArrayList<>(Arrays.asList(
                0xffff00, 0xff8800, 0xff0000, 0xff0088, 0xff00ff,
                0x00ffff, 0x00ff88, 0x00ff00, 0x88ff00, 0xffff00,
                0x00ffff, 0x0088ff, 0x0000ff, 0x8800ff, 0xff00ff,
                0x555500, 0xaaaa00, 0xffff00, 0xffff55, 0xffffaa,
                0x550000, 0xaa0000, 0xff0000, 0xff5555, 0xffaaaa,
                0x550055, 0xaa00aa, 0xff00ff, 0xff55ff, 0xffaaff,
                0x000055, 0x0000aa, 0x0000ff, 0x5555ff, 0xaaaaff,
                0x005500, 0x00aa00, 0x00ff00, 0x55ff55, 0xaaffaa,
                0x000000, 0x444444, 0x888888, 0xcccccc, 0xffffff
        ));
        referencePoints = new ArrayList<>(filenumbers.size());
        try {
            for (int i=0; i<filenumbers.size(); i++) {
                Spectrum spectrum = SpectrumIO.loadCGATS17Spectrum(new File(basefolder, filenumbers.get(i)+"_M1.txt")).get(0);
                referencePoints.add(new Pair<Spectrum, Integer>(spectrum, originalColors.get(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
