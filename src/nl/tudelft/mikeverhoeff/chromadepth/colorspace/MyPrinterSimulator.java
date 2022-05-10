package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;

import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;

public class MyPrinterSimulator extends ColorSpace {

    private CMYKColorSpace printerSpace;
    private java.awt.color.ColorSpace colorConverterCMYK;

    public MyPrinterSimulator() {
        printerSpace = new CMYKColorSpace();
        /*try {
            colorConverterCMYK = new ICC_ColorSpace(ICC_Profile.getInstance("C:\\Users\\Mike\\Desktop\\Research Project\\Programming\\ChromaPainter\\src\\res\\SWOP2006_Coated5_GCR_bas.icc"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }*/
    }

    @Override
    public int getNumberOfChannels() {
        return 3;
    }

    @Override
    public int getScreenColorForValues(byte[] values) {
        return 0xff<<24 | values[0]&0xff << 16 | values[1]&0xff << 8 | values[2]&0xff;
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

        if (colorConverterCMYK != null) {
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
}
