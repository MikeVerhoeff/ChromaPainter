package nl.tudelft.mikeverhoeff.chromadepth.spectra;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import nl.tudelft.mikeverhoeff.chromadepth.util.Pair;

public class xyYChart {

    public static Image renderChart() {
        int size = 400;
        WritableImage image = new WritableImage(size, size);

        Pair<Double, Double> xyD = ColorMatchingFunctions.colorTempToxyColor(6500);
        int whiteX = (int)(xyD.getA()*size);
        int whiteY = (int)(xyD.getB()*size);
        float[] whitexyY = new float[] {(float)(double)xyD.getA(), (float)(double)xyD.getB(), 0.8f};

        int whiteRGB = ColorSpaceConverter.XYZtoRGB(ColorSpaceConverter.xyYtoXYZ(whitexyY));
        image.getPixelWriter().setArgb(whiteX, whiteY, 0xff000000);
        image.getPixelWriter().setArgb(whiteX+1, whiteY, 0xff000000);
        image.getPixelWriter().setArgb(whiteX-1, whiteY, 0xff000000);
        image.getPixelWriter().setArgb(whiteX, whiteY+1, 0xff000000);
        image.getPixelWriter().setArgb(whiteX, whiteY-1, 0xff000000);

        //for(int x=0; x<size; x++) {
        //    for(int y=0; y<size; y++) {
        //        image.getPixelWriter().setArgb(x, y, ColorSpaceConverter.XYZtoRGB(ColorSpaceConverter.xyYtoXYZ(new float[]{x/(float)size, y/(float)size, 0.5f})));
        //    }
        //}
        for(int i=380 ; i<780; i++) {
            float[] XYZ = ColorSpaceConverter.WavelengthToXYZ(i);
            float[] xyY = ColorSpaceConverter.XYZtoxyY(XYZ);
            int argb = ColorSpaceConverter.XYZtoRGB(XYZ);
            int x = (int)(xyY[0]*size);
            int y = size-(int)(xyY[1]*size);
            if(x>0&&x<size & y>0&&y<size) {
                image.getPixelWriter().setArgb(x, y, argb);

                for(int s=0; s<80; s++) {
                    float[] xyY2 = interp(whitexyY, xyY, s/80.0f);
                    int x2 = (int)(xyY2[0]*size);
                    int y2 = size-(int)(xyY2[1]*size);
                    if(x2>0&&x2<size & y2>0&&y2<size) {
                        image.getPixelWriter().setArgb(x2, y2, ColorSpaceConverter.XYZtoRGB(ColorSpaceConverter.xyYtoXYZ(xyY2)));
                    }
                }

            }
        }
        return image;
    }

    private static float[] interp(float[] a, float[] b, float i) {
        return new float[]{a[0]*i+b[0]*(1-i), a[1]*i+b[1]*(1-i), a[2]*i+b[2]*(1-i)};
    }

}
