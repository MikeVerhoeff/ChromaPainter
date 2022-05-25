package nl.tudelft.mikeverhoeff.chromadepth.spectra;

public class ColorSpaceConverter {

    public static float[] XYZtoLab(float[] XYZ) {

        // D65 white point
        float Xr = 0.9504f;
        float Yr = 1.0000f;
        float Zr = 1.0888f;

        // standard values
        float e = 0.00865f; // 216/24389
        float k = 903.3f; // 24389/27

        float xr = XYZ[0]/Xr;
        float yr = XYZ[1]/Yr;
        float zr = XYZ[2]/Zr;

        float fx;
        if(xr>e) {
            fx = (float)Math.pow(xr, 1/3.0);
        } else {
            fx = (k*xr+16)/116;
        }

        float fy;
        if(yr>e) {
            fy = (float)Math.pow(yr, 1/3.0);
        } else {
            fy = (k*yr+16)/116;
        }

        float fz;
        if(zr>e) {
            fz = (float)Math.pow(zr, 1/3.0);
        } else {
            fz = (k*zr+16)/116;
        }

        float L = 116*fy-16;
        float a = 500*(fx-fy);
        float b = 200*(fy-fz);

        return new float[]{L, a, b};
    }

    public static float deltaE_CMC_FromLab(float[] Lab1, float[] Lab2) {

        // CMC(l:c)
        // CMC(2:1) for acceptability
        // CMC(1:1) for percetibility
        double l=2;
        double c=1;

        double C1 = Math.sqrt(Lab1[1]*Lab1[1]+Lab1[2]*Lab1[2]);
        double C2 = Math.sqrt(Lab2[1]*Lab2[1]+Lab2[2]*Lab2[2]);
        double dC = C1-C2;

        double dH = Math.sqrt(Math.pow((Lab1[1]-Lab2[1]), 2) + Math.pow((Lab1[2]-Lab2[2]), 2) - dC*dC);

        double dL = Lab1[0]-Lab2[0];

        double H = (Math.atan(Lab1[2]/Lab1[1])*180/Math.PI);

        double H1;
        if(H>=0) {
            H1 = H;
        } else {
            H1 = H+360;
        }

        double T;
        if (164 <= H1 && H1 <= 345) {
            T = 0.56 + Math.abs(0.2*Math.cos((H1+168)/180*Math.PI));
        } else {
            T = 0.36 + Math.abs(0.4*Math.cos((H1+35)/180*Math.PI));
        }

        double F = Math.sqrt(Math.pow(C1, 4)/(Math.pow(C1, 4)+1900));

        double SL;
        if(Lab1[0]<16) {
            SL = 0.511;
        } else {
            SL = (0.040975*Lab1[0])/(1+0.01765*Lab1[0]);
        }

        double SC = (0.0638*C1)/(1+0.0131*C1) + 0.638;
        double SH = SC*(F*T+1-F);

        return (float)Math.sqrt(Math.pow(dL/(l*SL), 2) + Math.pow(dC/(c*SC), 2) + Math.pow(dH/(SH), 2));
    }

    public static float deltaE_1976_FromLab(float[] Lab1, float[] Lab2) {
        return (float)Math.sqrt( Math.pow(Lab1[0]-Lab2[0], 2) + Math.pow(Lab1[1]-Lab2[1], 2) + Math.pow(Lab1[2]-Lab2[2], 2) );
    }

}
