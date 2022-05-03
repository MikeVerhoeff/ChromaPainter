package nl.tudelft.mikeverhoeff.chromadepth.preview;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import nl.tudelft.mikeverhoeff.chromadepth.ColorChannel;
import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.Painting;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;
import nl.tudelft.mikeverhoeff.chromadepth.util.Pair;
import nl.tudelft.mikeverhoeff.chromadepth.util.WaveLengthColorChanel;

import java.util.ArrayList;
import java.util.List;

public class ShiftedImageCalculator {

    private Painting painting;

    private int start = 380;
    private int end = 730;
    private int step = 10;

    private float[][][] spectralData; // [x][y][wavelength] = intensity
    private float[][][] shiftedImage;

    private double minWavelength = 300;
    private double maxWavelength = 900;

    public ShiftedImageCalculator(Painting painting) {
        this.painting = painting;
        spectralData = new float[painting.getWidth()][painting.getHeight()][/*(end-start)/step+1*/];
    }

    public void splitImage() {
        List<ColorChannel> colorChannels = painting.getChannels();
        List<Paint> paints = painting.getPaints();
        for(int x=0; x<painting.getWidth(); x++) {
            for(int y=0; y< painting.getHeight(); y++) {
                spectralData[x][y] = painting.getPaintMix(x, y).getSpectrum().getSamples();
            }
        }
    }

    public Image simpleUniformShift() {
        int samplecount = (end-start)/step+1;
        shiftedImage = new float[painting.getWidth()+samplecount][painting.getHeight()][samplecount];

        // shift the spectra
        for (int x=0; x<painting.getWidth(); x++) {
            for (int y=0; y<painting.getHeight(); y++) {
                for(int s=0; s<samplecount; s++) {
                    shiftedImage[x+s][y][s] = spectralData[x][y][s];
                }
            }
        }

        WritableImage image = new WritableImage(painting.getWidth()+samplecount, painting.getHeight());
        PixelWriter writer = image.getPixelWriter();

        // calculate the screen color for the spectra
        for (int x = 0; x < painting.getWidth()+samplecount; x++) {
            for (int y = 0; y < painting.getHeight(); y++) {
                int color = new Spectrum(start, end, step, shiftedImage[x][y]).getArgb();
                writer.setArgb(x, y, color);
            }
        }

        return image;

    }

    public Image getImageAtDistance(double distance, double eye, double pixelsize) {
        double blueShift = calculateShift(minWavelength, distance)*pixelsize;
        double redShift = calculateShift(maxWavelength, distance)*pixelsize;

        int minShift = (int)Math.floor(Math.min(blueShift, redShift));
        int maxShift = (int)Math.ceil(Math.max(blueShift, redShift));
        int extraSpace = maxShift-minShift;
        int start = -minShift;

        byte[][][] collectImage = new byte[painting.getWidth()][painting.getHeight()][3];


        WritableImage image = new WritableImage(painting.getWidth()+extraSpace, painting.getHeight());
        PixelWriter writer = image.getPixelWriter();

        return null;
    }

    private double calculateShift(double wavelength /*nm*/, double distance) {
        double g = 32000; //32 um
        // sin b = l/g
        double b = Math.asin(wavelength/g);
        /*
        |\
        |b\
        |  \
        |L  \
        -----
        tan b = shift / distance
         */
        return Math.tan(b)*distance;
    }

}
