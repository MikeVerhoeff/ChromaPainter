package nl.tudelft.mikeverhoeff.chromadepth;

public class ColorChannel {

    private Paint paint;
    private byte[][] pixels;

    public ColorChannel(int width, int height, Paint paint) {
        this.paint = paint;
        pixels = new byte[width][height];
    }

    public void setPixel(int x, int y, byte value) {
        pixels[x][y] = value;
    }

    public byte getPixel(int x, int y) {
        return pixels[x][y];
    }
}
