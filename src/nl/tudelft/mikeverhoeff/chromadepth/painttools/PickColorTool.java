package nl.tudelft.mikeverhoeff.chromadepth.painttools;

import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.PaintMix;
import nl.tudelft.mikeverhoeff.chromadepth.Painting;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.MainController;

import java.util.List;

public class PickColorTool extends PaintTool {

    private MainController mainController;

    public PickColorTool(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void paint(Painting painting, int x, int y) {
        List<Paint> paints =  painting.getPaints();
        byte[] values = new byte[paints.size()];
        for(int i=0; i<paints.size(); i++) {
            values[i] = painting.getChannels().get(i).getPixel(x, y);
        }
        mainController.getColorMixer().setPaintMix(values);
    }
}
