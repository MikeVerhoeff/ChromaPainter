package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import javafx.stage.Window;
import nl.tudelft.mikeverhoeff.chromadepth.Main;
import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrumIO;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class MyPrinterSimulator extends ColorSpace {

    private CMYKColorSpace printerSpace;
    private java.awt.color.ColorSpace colorConverterCMYK;

    public MyPrinterSimulator() {
        printerSpace = new CMYKColorSpace();
        try {
            colorConverterCMYK = new ICC_ColorSpace(ICC_Profile.getInstance("C:\\Users\\Mike\\Desktop\\Research Project\\Programming\\ChromaPainter\\src\\res\\SWOP2006_Coated5_GCR_bas.icc"));
            //colorConverterCMYK = new ICC_ColorSpace(ICC_Profile.getInstance(ICC_Profile.icSigCmykData));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            printerSpace.setBackgroundColor(SpectrumIO.loadCGATS17Spectrum(new File(Main.SpectrumDirectory+"\\50_m1.txt")).get(0));
            printerSpace.setCyan(SpectrumIO.loadCGATS17Spectrum(new File(Main.SpectrumDirectory+"\\06_m1.txt")).get(0));
            printerSpace.setYellow(SpectrumIO.loadCGATS17Spectrum(new File(Main.SpectrumDirectory+"\\01_m1.txt")).get(0));
            printerSpace.setMagenta(SpectrumIO.loadCGATS17Spectrum(new File(Main.SpectrumDirectory+"\\05_m1.txt")).get(0));
            printerSpace.setKey(SpectrumIO.loadCGATS17Spectrum(new File(Main.SpectrumDirectory+"\\46_m1.txt")).get(0));
            printerSpace.setN(5.22f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNumberOfChannels() {
        return 3;
    }

    @Override
    public Paint getChanelColor(int channel) {
        if(channel==0) {
            return new Paint(Paint.RGBColor.RED);
        } else if (channel==1) {
            return new Paint(Paint.RGBColor.GREEN);
        } else if (channel==2) {
            return new Paint(Paint.RGBColor.BLUE);
        } else {
            return Paint.getDefault();
        }
    }

    @Override
    public int getScreenColorForValues(byte[] values) {
        return 0xff<<24 | (values[0]&0xff) << 16 | (values[1]&0xff) << 8 | (values[2]&0xff);
    }

    @Override
    public Spectrum getSpectrumForValues(byte[] values) {
        int r = Byte.toUnsignedInt(values[0]);
        int g = Byte.toUnsignedInt(values[1]);
        int b = Byte.toUnsignedInt(values[2]);

        int k = 255 - Math.max(r, Math.max( g, b));
        int c = 255 - r - k;
        int m = 255 - g - k;
        int y = 255 - b - k;

        if (colorConverterCMYK != null && false) {
            float[] cymk = colorConverterCMYK.fromRGB(new float[] {r/255.0f, g/255.0f, b/255.0f});
            c = ((int)(cymk[0]*255))&0xff;
            m = ((int)(cymk[1]*255))&0xff;
            y = ((int)(cymk[2]*255))&0xff;
            k = ((int)(cymk[3]*255))&0xff;
        }

        return printerSpace.getSpectrumForValues(new byte[] {(byte)c, (byte)m, (byte)y, (byte)k});
    }

    public CMYKColorSpace getPrinterSpace() {
        return printerSpace;
    }

    public void setPrinterSpace(CMYKColorSpace printerSpace) {
        this.printerSpace = printerSpace;
    }

    @Override
    public void configureGUI(Window window, Consumer<ColorSpace> finish) {
        finish.accept(this);
    }

    @Override
    public void setBackground(Spectrum s) {
        printerSpace.setBackgroundColor(s);
    }
}
