import nl.tudelft.mikeverhoeff.chromadepth.Painting;
import nl.tudelft.mikeverhoeff.chromadepth.colorspace.ColorSpace;
import nl.tudelft.mikeverhoeff.chromadepth.colorspace.MyPrinterSimulator;
import nl.tudelft.mikeverhoeff.chromadepth.preview.ShiftedImageCalculator;
import org.junit.Test;

public class SimulationSpeedTest {

    @Test
    public void speedTest1() {
        int size = 0;
        long startTime = 0;
        long endTime = 0;

        ColorSpace colorSpace = new MyPrinterSimulator();

        for(int i=1; i< 20; i++) {
            size = i*50;
            Painting painting = new Painting(size, size, colorSpace);
            ShiftedImageCalculator calculator = new ShiftedImageCalculator(painting);

            startTime = System.nanoTime();
            calculator.splitImage();
            calculator.getImageAtDistance(0.4, 1, 530, 0.2645e-3);
            endTime = System.nanoTime();

            long runtime = endTime - startTime;
            //System.out.println("Size: " + size + " : " + runtime + "ns");
            System.out.println(size+", "+runtime);
        }
    }

}
