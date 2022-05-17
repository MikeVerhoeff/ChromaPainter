package nl.tudelft.mikeverhoeff.chromadepth;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import nl.tudelft.mikeverhoeff.chromadepth.colorspace.ColorSpace;
import nl.tudelft.mikeverhoeff.chromadepth.colorspace.LinearMixSpace;

import java.util.ArrayList;
import java.util.List;

public class Painting {

    private List<Paint> paints;
    private List<ColorChannel> channels;
    private int numberOfChannels;
    private ColorSpace colorSpace;
    private int width;
    private int height;
    private WritableImage image;

    private boolean previewRealColors = true;

    public Painting(int width, int height, List<Paint> paints) {
        this.width = width;
        this.height = height;
        this.paints = new ArrayList<>(paints);
        numberOfChannels = paints.size();
        this.channels = new ArrayList<>(paints.size());
        for(Paint paint : paints) {
            channels.add(new ColorChannel(width, height, paint));
        }
        this.colorSpace = new LinearMixSpace(paints);
        initializeImage();
    }

    public Painting(int width, int height, ColorSpace colorSpace) {
        this.width = width;
        this.height = height;
        this.paints = new ArrayList<>(colorSpace.getNumberOfChannels());
        numberOfChannels = colorSpace.getNumberOfChannels();
        this.channels = new ArrayList<>(colorSpace.getNumberOfChannels());
        for(int i=0; i<numberOfChannels; i++) {
            Paint paint = colorSpace.getChanelColor(i);
            paints.add(paint);
            channels.add(new ColorChannel(width, height, paint));
        }
        this.colorSpace = colorSpace;
        initializeImage();
    }

    public boolean doesPreviewRealColors() {
        return previewRealColors;
    }

    public void setPreviewRealColors(boolean previewRealColors) {
        this.previewRealColors = previewRealColors;
    }

    private int getDisplayColor(byte[] values) {
        if(previewRealColors) {
            return colorSpace.getSpectrumForValues(values).getArgb();
        } else {
            return colorSpace.getScreenColorForValues(values);
        }
    }

    private void setPixelUnchecked(int x, int y, PaintMix color) {
        for(int i=0; i< numberOfChannels; i++) {
            channels.get(i).setPixel(x, y, color.getValues()[i]);
        }
        image.getPixelWriter().setArgb(x, y, getDisplayColor(color.getValues()));
    }

    private void setPixelUnchecked(int x, int y, byte[] color) {
        for(int i=0; i< numberOfChannels; i++) {
            channels.get(i).setPixel(x, y, color[i]);
        }
        image.getPixelWriter().setArgb(x, y, getDisplayColor(color));
    }

    public void setPixel(int x, int y, PaintMix color) {
        if(color.getPaints().size() != numberOfChannels)
            throw new RuntimeException("Color dimention mismatch");
        setPixelUnchecked(x, y, color);
    }

    public void setPixel(int x, int y, byte[] color) {
        if(color.length != numberOfChannels)
            throw new RuntimeException("Color dimention mismatch");
        setPixelUnchecked(x, y, color);
    }

    public void setSquare(int x, int y, int w, int h, byte[] color) {
        if(color.length != numberOfChannels)
            throw new RuntimeException("Color dimention mismatch");
        /*for(int i=0; i< numberOfChannels; i++) { // better for cash hits
            for(int j=0; j<w; j++) {
                for (int k = 0; k < h; k++) {
                    channels.get(i).setPixel(x+j, y+k, color.getValues()[i]);
                }
            }
        }*/
        for(int j=0; j<w; j++) {
            for (int k = 0; k < h; k++) {
                setPixelUnchecked(x+j, y+k, color);
            }
        }
    }

    private void initializeImage() {
        image = new WritableImage(width, height);
        calculateRGB();
    }

    public Image getImage() {
        return image;
    }

    public List<Paint> getPaints() {
        return paints;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<ColorChannel> getChannels() {
        return channels;
    }

    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    public PaintMix getPaintMix(int x, int y) {
        byte values[] = new byte[numberOfChannels];
        for(int i=0; i<numberOfChannels; i++) {
            values[i] = channels.get(i).getPixel(x, y);
        }
        return new PaintMix(paints, values);
    }

    public void calculateRGB() {
        PixelWriter writer = image.getPixelWriter();
        byte values[] = new byte[numberOfChannels];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int i = 0; i < numberOfChannels; i++) {
                    values[i] = channels.get(i).getPixel(x, y);
                }
                writer.setArgb(x, y, getDisplayColor(values));
            }
        }
    }

}
