package nl.tudelft.mikeverhoeff.chromadepth.spectra;

import nl.tudelft.mikeverhoeff.chromadepth.Main;
import nl.tudelft.mikeverhoeff.chromadepth.util.Pair;

import java.io.InputStream;
import java.util.Scanner;

public class ColorMatchingFunctions {

    // 1 nm resolution
    private static int start1 = 380;
    private static int end1 = 780;

    private static float[] X1;
    private static float[] Y1;
    private static float[] Z1;

    // 5 nm resolution
    private static int start5 = 360;
    private static int end5 = 830;

    private static float[] X5;
    private static float[] Y5;
    private static float[] Z5;

    static {
        load1();
        load5();
        loadD10();
    }

    private static void load1() {

        InputStream stream = Main.class.getResourceAsStream("/res/ColorMatchingFunctions");
        //System.out.println(stream);

        Scanner scanner = new Scanner(stream);
        scanner.nextLine();
        X1 = new float[end1 - start1 +1];
        Y1 = new float[end1 - start1 +1];
        Z1 = new float[end1 - start1 +1];
        for(int i = start1; i<= end1; i++) {
            int wavelength = scanner.nextInt();
            float x = Float.parseFloat(scanner.next());
            float y = Float.parseFloat(scanner.next());
            float z = Float.parseFloat(scanner.next());
            if(wavelength != i) {
                System.out.println("mismatch");
            }
            X1[i- start1]=x;
            Y1[i- start1]=y;
            Z1[i- start1]=z;
        }
    }

    private static void load5() {
        InputStream stream = Main.class.getResourceAsStream("/res/ciexyz31.csv");

        Scanner scanner = new Scanner(stream);

        X5 = new float[(end5 - start5)/5+1];
        Y5 = new float[(end5 - start5)/5+1];
        Z5 = new float[(end5 - start5)/5+1];

        while(scanner.hasNextLine()) {
            String[] row = scanner.nextLine().split(",");
            if(row.length == 4) {
                int l = Integer.parseInt(row[0]);
                X5[(l-start5)/5] = Float.parseFloat(row[1]);
                Y5[(l-start5)/5] = Float.parseFloat(row[2]);
                Z5[(l-start5)/5] = Float.parseFloat(row[3]);
            } else {
                System.err.println("unexpected row length, skipping");
            }
        }



    }

    public static float X(int waveLength) {
        if(waveLength>= start1 && waveLength<= end1) {
            return X1[waveLength- start1];
            //return X5[(waveLength-start5)/5];
        }
        else {
            return 0.0f;
        }
    }

    public static float Y(int waveLength) {
        if(waveLength>= start1 && waveLength<= end1) {
            return Y1[waveLength- start1];
            //return Y5[(waveLength-start5)/5];
        }
        else {
            return 0.0f;
        }
    }

    public static float Z(int waveLength) {
        if(waveLength>= start1 && waveLength<= end1) {
            return Z1[waveLength- start1];
            //return Z5[(waveLength-start5)/5];
        }
        else {
            return 0.0f;
        }
    }

    public static int getColorForWavelength(int l) {
        float X = X(l);
        float Y = Y(l);
        float Z = Z(l);
        float r = +3.2406f*X -1.5372f*Y -0.4986f*Z;
        float g = -0.9689f*X +1.8758f*Y +0.0415f*Z;
        float b = +0.0557f*X -0.2040f*Y +1.0570f*Z;

        r = gammaCorrect(r)*255;
        g = gammaCorrect(g)*255;
        b = gammaCorrect(b)*255;

        if(r>255) {r=255;}
        if(r<0) {r=0;}
        if(g>255) {g=255;}
        if(g<0) {g=0;}
        if(b>255) {b=255;}
        if(b<0) {b=0;}


        //System.out.println("RGB: " + r + ", " + g + ", " + b);
        return (((int)r)&0xff)<<16 | (((int)g)&0xff)<<8 | (((int)b)&0xff);
    }

    private static float gammaCorrect(float c) {
        if(c<=0.0031308) {
            return Math.min(1.0f,12.92f*c);
        } else {
            return (float) Math.min(1.0,1.055 * Math.pow(c, 1/2.4) - 0.055);
        }
    }


    private static final int SStart = 300;
    private static final int SEnd = 830;
    private static final int SStep = 10;



    private static float[] S0;
    private static float[] S1;
    private static float[] S2;

    private static void loadD10() {
        InputStream stream = Main.class.getResourceAsStream("/res/DIlluminants.csv");

        Scanner scanner = new Scanner(stream);
        scanner.nextLine();

        S0 = new float[(SEnd - SStart)/SStep+1];
        S1 = new float[(SEnd - SStart)/SStep+1];
        S2 = new float[(SEnd - SStart)/SStep+1];

        while(scanner.hasNextLine()) {
            String[] row = scanner.nextLine().split(",");
            if(row.length == 4) {
                int l = Integer.parseInt(row[0]);
                S0[(l-SStart)/SStep] = Float.parseFloat(row[1]);
                S1[(l-SStart)/SStep] = Float.parseFloat(row[2]);
                S2[(l-SStart)/SStep] = Float.parseFloat(row[3]);
            } else {
                System.err.println("unexpected row length, skipping");
            }
        }


    }

    public static float S0(int waveLength) {
        if(waveLength>= SStart && waveLength<= SEnd) {
            return S0[(waveLength- SStart)/SStep];
        }
        else {
            return 0.0f;
        }
    }

    public static float S1(int waveLength) {
        if(waveLength>= SStart && waveLength<= SEnd) {
            return S1[(waveLength- SStart)/SStep];
        }
        else {
            return 0.0f;
        }
    }

    public static float S2(int waveLength) {
        if(waveLength>= SStart && waveLength<= SEnd) {
            return S2[(waveLength- SStart)/SStep];
        }
        else {
            return 0.0f;
        }
    }


    public static Pair<Double, Double> colorTempToxyColor(float T) {
        double xd = 0;
        if(T <= 7000) {
            xd = -4.6070e9 / (T*T*T) + 2.9678e6 / (T*T) + 0.09911e3 / T + 0.244063;
        } else {
            xd = -2.0064e9 / (T*T*T) + 1.9018e6 / (T*T) + 0.24748e3 / T + 0.237040;
        }
        double yd = -3.000*xd*xd + 2.870*xd - 0.275;
        return new Pair<Double, Double>(xd, yd);
    }

    public static double M(double x, double y) {
        return 0.0241 + 0.2562*x - 0.7341*y;
    }

    public static double M1(double x, double y, double M) {
        return (-1.3515 - 1.7703*x + 5.9114*y)/M;
    }

    public static double M2(double x, double y, double M) {
        return (0.0300 - 31.4424*x + 30.0717*y)/M;
    }

    public static double Sd(int wavelength, double M1, double M2) {
        return S0(wavelength) + M1*S1(wavelength) + M2*S2(wavelength);
    }

}
