package nl.tudelft.mikeverhoeff.chromadepth;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.List;

public class Painting {

    private List<Paint> paints;
    private List<ColorChannel> channels;
    private int width;
    private int height;
    private WritableImage image;

    public Painting(int width, int height, List<Paint> paints) {
        this.width = width;
        this.height = height;
        this.paints = new ArrayList<>(paints);
        this.channels = new ArrayList<>(paints.size());
        for(Paint paint : paints) {
            channels.add(new ColorChannel(width, height, paint));
        }
        initializeImage();
    }

    private void setPixelUnchecked(int x, int y, PaintMix color) {
        for(int i=0; i< paints.size(); i++) {
            channels.get(i).setPixel(x, y, color.getValues()[i]);
        }
        image.getPixelWriter().setArgb(x, y, color.getScreenColor());
    }

    public void setPixel(int x, int y, PaintMix color) {
        if(color.getPaints().size() != paints.size())
            throw new RuntimeException("Color dimention mismatch");
        setPixelUnchecked(x, y, color);
    }

    public void setPixel(int x, int y, byte[] color) {
        setPixel(x, y, new PaintMix(paints, color));
    }

    public void setSquare(int x, int y, int w, int h, PaintMix color) {
        if(color.getPaints().size() != paints.size())
            throw new RuntimeException("Color dimention mismatch");
        /*for(int i=0; i< paints.size(); i++) { // better for cash hits
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

    public PaintMix getPaintMix(int x, int y) {
        byte values[] = new byte[paints.size()];
        for(int i=0; i<paints.size(); i++) {
            values[i] = channels.get(i).getPixel(x, y);
        }
        return new PaintMix(paints, values);
    }

    public void calculateRGB() {
        PixelWriter writer = image.getPixelWriter();
        byte values[] = new byte[paints.size()];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int i = 0; i < paints.size(); i++) {
                    values[i] = channels.get(i).getPixel(x, y);
                }
                writer.setArgb(x, y, new PaintMix(paints, values).getScreenColor());
            }
        }
    }
}
