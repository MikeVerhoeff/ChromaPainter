package nl.tudelft.mikeverhoeff.chromadepth.painttools;

import nl.tudelft.mikeverhoeff.chromadepth.PaintMix;
import nl.tudelft.mikeverhoeff.chromadepth.Painting;

public class BrushTool extends PaintTool {

    private int radius;
    private byte[] color;

    public BrushTool(int radius, byte[] color) {
        this.radius = radius;
        this.color = color;
    }

    public void paint(Painting painting, int x, int y) {
        System.out.println("Using Brush");
        for(int i=0; i<=radius; i++) {
            for(int j=0; j<=i; j++) {
                if(i*i + j*j <= radius*radius) {
                    safePaint(painting, x+i, y+j, color);
                    safePaint(painting, x+i, y-j, color);
                    safePaint(painting, x-i, y+j, color);
                    safePaint(painting, x-i, y-j, color);
                    safePaint(painting, x+j, y+i, color);
                    safePaint(painting, x+j, y-i, color);
                    safePaint(painting, x-j, y+i, color);
                    safePaint(painting, x-j, y-i, color);
                }
            }
        }
    }

}
