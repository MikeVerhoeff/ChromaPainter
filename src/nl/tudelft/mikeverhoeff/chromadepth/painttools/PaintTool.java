package nl.tudelft.mikeverhoeff.chromadepth.painttools;

import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.PaintMix;
import nl.tudelft.mikeverhoeff.chromadepth.Painting;

import java.util.Arrays;
import java.util.List;

public class PaintTool {

    public boolean safePaint(Painting painting, int x, int y, byte[] color) {
        if (x>=0 && y>=0 && x<painting.getWidth() && y<painting.getHeight()) {
            painting.setPixel(x, y, color);
            return true;
        } else {
            return false;
        }
    }

    public void paint(Painting painting, int x, int y) {
        List<Paint> paints = painting.getPaints();
        byte[] values = new byte[paints.size()];
        Arrays.fill(values, (byte)255);
        PaintMix color = new PaintMix(paints, values);
        painting.setPixel(x, y, color);
    }

}
