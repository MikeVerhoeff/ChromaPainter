package nl.tudelft.mikeverhoeff.chromadepth.spectra;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class SpectrogramChart extends Canvas {

    public SpectrogramChart() {
        super(200, 200);
        System.out.println("chart test");
    }

    public void displayColorSpectrum(Spectrum spectrum) {
        double width = this.getWidth();
        double height = this.getHeight();

        float maxIntensity = 0;
        for (int i = 0; i < spectrum.getSamples().length; i++) {
            if (spectrum.getSamples()[i] > maxIntensity) {
                maxIntensity = spectrum.getSamples()[i];
            }
        }

        if(maxIntensity == 0) {
            this.getGraphicsContext2D().clearRect(0, 0, width, height);
            return;
        }

        double widthStep = width/spectrum.getSamples().length;
        double heightStep = height/maxIntensity;

        GraphicsContext draw = this.getGraphicsContext2D();
        draw.clearRect(0, 0, width, height);
        for(int x=0; x<width; x++) {
            int wavelength = (int)(spectrum.getStart() + x/widthStep * spectrum.getStep());
            double pos = x/widthStep;
            int i = (int)pos;
            pos = pos-i;
            float y = spectrum.getSamples()[i];
            if(spectrum.getSamples().length>i+1) {
                y = (float) ((1-pos)*y + pos*spectrum.getSamples()[i+1]);
            }

            int argb = ColorMatchingFunctions.getColorForWavelength(wavelength);
            draw.setFill(Color.rgb(argb>>16 & 0xff, argb>>8 & 0xff, argb & 0xff));
            draw.fillRect(x, height-y*heightStep, 1, height);
        }
        draw.setFill(Color.BLACK);
        draw.fillOval(5,5,5,5);
        for (int i = 0; i < spectrum.getSamples().length; i++) {
            draw.fillOval(i*widthStep, height-spectrum.getSamples()[i]*heightStep, 3, 3);
        }
    }

}
