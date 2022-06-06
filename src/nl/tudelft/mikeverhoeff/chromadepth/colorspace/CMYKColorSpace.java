package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import javafx.stage.Window;
import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrumIO;

import java.io.DataInputStream;
import java.io.File;
import java.util.function.Consumer;

public class CMYKColorSpace extends ColorSpace {

    private Spectrum backgroundColor;
    private Paint cyan;
    private Paint magenta;
    private Paint yellow;
    private Paint key;
    private float nPaper;

    Spectrum[] neugebauerPrimaries = null;

    public CMYKColorSpace() {
        cyan = Paint.getDefault();
        magenta = Paint.getDefault();
        yellow = Paint.getDefault();
        key = Paint.getDefault();
        nPaper = 1;
    }

    public Spectrum getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Spectrum backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.neugebauerPrimaries = null;
    }

    public Spectrum getCyan() {
        return cyan.getSpectrum();
    }

    public void setCyan(Spectrum cyan) {
        this.cyan.setSpectrum(cyan);
        this.neugebauerPrimaries = null;
    }

    public Spectrum getMagenta() {
        return magenta.getSpectrum();
    }

    public void setMagenta(Spectrum magenta) {
        this.magenta.setSpectrum(magenta);
        this.neugebauerPrimaries = null;
    }

    public Spectrum getYellow() {
        return yellow.getSpectrum();
    }

    public void setYellow(Spectrum yellow) {
        this.yellow.setSpectrum(yellow);
        this.neugebauerPrimaries = null;
    }

    public Spectrum getKey() {
        return key.getSpectrum();
    }

    public void setKey(Spectrum key) {
        this.key.setSpectrum(key);
        this.neugebauerPrimaries = null;
    }

    public void setN(float n) {
        nPaper = n;
    }

    @Override
    public int getNumberOfChannels() {
        return 4;
    }

    @Override
    public Paint getChanelColor(int channel) {
        if(channel==0) {
            return cyan;
        } else if (channel==1) {
            return magenta;
        } else if (channel==2) {
            return yellow;
        } else if (channel==3) {
            return key;
        } else {
            return Paint.getDefault();
        }
    }

    @Override
    public int getScreenColorForValues(byte[] values) {
        return getSpectrumForValues(values).getArgb();
    }

    @Override
    public Spectrum getSpectrumForValues(byte[] values) {
        float[] resultSamples = new float[backgroundColor.getSamples().length];
        float[] interp = new float[getNumberOfChannels()];
        interp[0] = Byte.toUnsignedInt(values[0])/255.0f;
        interp[1] = Byte.toUnsignedInt(values[1])/255.0f;
        interp[2] = Byte.toUnsignedInt(values[2])/255.0f;
        interp[3] = Byte.toUnsignedInt(values[3])/255.0f;

        if(neugebauerPrimaries == null) {
            System.out.println("Computing neugebauer");
            neugebauerPrimaries = MixHelper.createNeugebauerPrimaries(backgroundColor, new Spectrum[]{cyan.getSpectrum(), magenta.getSpectrum(), yellow.getSpectrum(), key.getSpectrum()});
        }
        float[] nbpMix = MixHelper.createNeugebauerMix(values);
        return  MixHelper.mixNeugebauerPrimaries(backgroundColor, neugebauerPrimaries, nbpMix, nPaper);

        //return MixHelper.mixYNSN(backgroundColor, new Spectrum[]{cyan, magenta, yellow, key}, values);
    }

    public float getMaxIntensity() {
        return Math.max(backgroundColor.getMaxSampleValue(),
                Math.max(cyan.getSpectrum().getMaxSampleValue(),
                        Math.max(yellow.getSpectrum().getMaxSampleValue(),
                                Math.max(cyan.getSpectrum().getMaxSampleValue(), key.getSpectrum().getMaxSampleValue()))));
    }

    @Override
    public void configureGUI(Window window, Consumer<ColorSpace> finish) {
        try {
            this.setBackgroundColor(SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\50_m1.txt")).get(0));
            this.setCyan(SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\06_m1.txt")).get(0));
            this.setYellow(SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\01_m1.txt")).get(0));
            this.setMagenta(SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\05_m1.txt")).get(0));
            this.setKey(SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\46_m1.txt")).get(0));
            finish.accept(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadFromReader(DataInputStream reader) {
        configureGUI(null, (s)->{});
    }

    @Override
    public void setBackground(Spectrum s) {
        backgroundColor = s;
    }

    @Override
    public void forceColorUpdate() {
        neugebauerPrimaries = null;
    }
}
