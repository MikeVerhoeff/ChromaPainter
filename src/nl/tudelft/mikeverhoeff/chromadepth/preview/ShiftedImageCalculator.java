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

    public ShiftedImageCalculator(Painting painting) {
        this.painting = painting;
        spectralData = new float[painting.getWidth()][painting.getHeight()][/*(end-start)/step+1*/];
    }

    public void splitImage() {
        List<ColorChannel> colorChannels = painting.getChannels();
        List<Paint> paints = painting.getPaints();
        for(int x=0; x<painting.getWidth(); x++) {
            for(int y=0; y< painting.getHeight(); y++) {
                spectralData[x][y] = painting.getColorSpace().getSpectrumForValues(painting.getPaintMix(x, y).getValues()).getSamples();
                //spectralData[x][y] = painting.getPaintMix(x, y).getSpectrum().getSamples();
            }
        }
    }

    public Image simpleUniformShift(float pixelsPerSample) {
        int samplecount = (end-start)/step+1;
        int shiftedWidth = painting.getWidth()+(int)(samplecount*pixelsPerSample);
        shiftedImage = new float[shiftedWidth][painting.getHeight()][samplecount];

        // shift the spectra
        for (int x=0; x<painting.getWidth(); x++) {
            for (int y=0; y<painting.getHeight(); y++) {
                for(int s=0; s<samplecount; s++) {
                    shiftedImage[x+(int)(s*pixelsPerSample)][y][s] = spectralData[x][y][s];
                }
            }
        }

        WritableImage image = new WritableImage(shiftedWidth, painting.getHeight());
        PixelWriter writer = image.getPixelWriter();

        // calculate the screen color for the spectra
        for (int x = 0; x < shiftedWidth; x++) {
            for (int y = 0; y < painting.getHeight(); y++) {
                //int color = new Spectrum(start, end, step, shiftedImage[x][y]).getArgb();
                int color = new Spectrum(painting.getPaints().get(0).getSpectrum(), shiftedImage[x][y]).getArgb();
                writer.setArgb(x, y, color);
            }
        }

        return image;

    }

    public Image getImageAtDistance(double distance, double eye, int fixedwavelength, double pixelsize) {
        return getImageAtDistance(distance, eye, fixedwavelength, pixelsize, 4);
    }


    public Image getImageAtDistance(double distance, double eye, int fixedwavelength, double pixelsize, int subpixels) {
        //final int fixedwavelength = 500;
        double fixedoffset = (calculateShift(fixedwavelength, distance, eye)/pixelsize);
        //System.out.println("fixed offset: " + fixedoffset);

        double blueShift = calculateShift(start, distance, 1)/pixelsize;
        double redShift = calculateShift(end, distance, 1)/pixelsize;

        int minShift = (int)Math.floor(Math.min(blueShift, redShift));
        int maxShift = (int)Math.ceil(Math.max(blueShift, redShift));
        minShift = -maxShift;
        int extraSpace = maxShift-minShift;

        int samplecount = (end-start)/step+1;
        int shiftedWidth = painting.getWidth()+extraSpace;
        shiftedImage = new float[shiftedWidth*subpixels][painting.getHeight()][samplecount];


        //System.out.println("Min shift: "+minShift+", Max shift: "+maxShift);
        // shift the spectra
        System.out.println("calculating shifted image");
        for(int s=0; s<samplecount; s++) {
            double calculatedShift = calculateShift(start+s*step, distance, eye)/pixelsize;
            //System.out.println("sample layer "+s+" : "+calculatedShift);
            for (int x=0; x<painting.getWidth(); x++) {
                for (int y=0; y<painting.getHeight(); y++) {
                    for(int sp=0; sp<subpixels; sp++) {
                        shiftedImage[(int)( (x - minShift + calculatedShift - fixedoffset)*subpixels+sp)][y][s] = spectralData[x][y][s];
                    }
                }
            }
        }

        WritableImage image = new WritableImage(shiftedWidth*subpixels, painting.getHeight());
        PixelWriter writer = image.getPixelWriter();

        // calculate the screen color for the spectra
        for (int x = 0; x < shiftedWidth*subpixels; x++) {
            for (int y = 0; y < painting.getHeight(); y++) {
                //int color = new Spectrum(start, end, step, shiftedImage[x][y]).getArgb();
                int color = new Spectrum(painting.getPaints().get(0).getSpectrum(), shiftedImage[x][y]).getArgb();
                writer.setArgb(x, y, color);
            }
        }

        return image;
    }

    private double calculateShift(double wavelength /*nm*/, double distance, double eye) {
        double g = 32000/eye; //32 um
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
