package nl.tudelft.mikeverhoeff.chromadepth.spectra;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpectrumIO {

    public static List<Spectrum> loadCGATS17Spectrum(File file) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(file));
        String line = input.readLine().trim();
        if(!line.equalsIgnoreCase("CGATS.17")) {
            System.err.println("File not CGATS.17");
            return null;
        }
        Map context = new HashMap<String, Object>();
        while ( (line = input.readLine()) != null) {
            String[] keyvalue = line.split("\\s", 2);

            if (keyvalue.length == 0 || keyvalue.length==1 && keyvalue[0].equals("")) {
                //System.out.println("Empty line");
            } else if (keyvalue.length == 1) {
                //System.out.println("Statement: `"+keyvalue[0]+"`");
                if (keyvalue[0].equalsIgnoreCase("BEGIN_DATA_FORMAT")) {
                    parseDataFormat(input, context);
                }
                else if (keyvalue[0].equalsIgnoreCase("BEGIN_DATA")) {
                    parseData(input, context);
                }
                else {
                    System.err.println("Unexpected statement: `"+keyvalue[0]+"`");
                }
            } else {
                context.put(keyvalue[0], keyvalue[1]);
                //System.out.println("Key: `"+keyvalue[0]+"` Value: `"+keyvalue[1]+"`");
            }
        }
        List<Spectrum> spectra = createSpectraFromContext(context);
        //System.out.println("XYZ (ref): "+((List<String[]>)context.get("DATA")).get(0)[2]+", "+((List<String[]>)context.get("DATA")).get(0)[3]+", "+((List<String[]>)context.get("DATA")).get(0)[4]);
        return spectra;
    }

    private static List<Spectrum> createSpectraFromContext(Map<String, Object> context) {
        int numSets = (int)context.get("NUMBER_OF_SETS");
        String[] fields = (String[])context.get("DATA_FORMAT");
        List<String[]> sets = (List<String[]>)context.get("DATA");
        Pattern p = Pattern.compile("SPECTRAL_NM([0-9]+)");
        int xi=-1, yi=-1, zi=-1;

        String iluminant = (String)context.get("ILLUMINATION_NAME");

        int minwave = Integer.MAX_VALUE;
        int maxwave = 0;
        int samplecount = 0;
        // get wavelength ranges
        Map<Integer, Integer> waveIndex = new HashMap<>(fields.length);
        for(int i=0; i<fields.length; i++) {
            Matcher m = p.matcher(fields[i]);
            if(m.matches()) {
                int wavelength = Integer.parseInt(m.group(1));
                waveIndex.put(wavelength, i);
                minwave = Math.min(minwave, wavelength);
                maxwave = Math.max(maxwave, wavelength);
                samplecount++;
            }
            if(fields[i].equalsIgnoreCase("XYZ_X")) {
                xi=i;
            }
            if(fields[i].equalsIgnoreCase("XYZ_Y")) {
                yi=i;
            }
            if(fields[i].equalsIgnoreCase("XYZ_Z")) {
                zi=i;
            }
        }
        int stepsize = (maxwave-minwave)/(samplecount-1);

        //System.out.println("Wavelength range: "+minwave+" - "+maxwave+", Samples: "+samplecount+", Step:"+stepsize);

        List<Spectrum> spectra = new ArrayList<>(sets.size());
        for (String[] set : sets) {
            float[] samples = new float[samplecount];
            int j=0;
            for(int i=minwave; i<=maxwave; i+=stepsize) {
                String v = set[waveIndex.get(i)];
                samples[j] = Float.parseFloat(v);
                j++;
            }
            Spectrum spectrum;
            if(xi!=-1 && yi!=-1 && zi!=-1) {
                spectrum = new Spectrum(minwave, maxwave, stepsize, samples, Float.parseFloat(set[xi]), Float.parseFloat(set[yi]), Float.parseFloat(set[zi]), iluminant);
            } else {
                spectrum = new Spectrum(minwave, maxwave, stepsize, samples, iluminant);
            }
            spectra.add(spectrum);
        }
        //System.out.println(spectra);
        return spectra;
    }

    private static void parseDataFormat(BufferedReader reader, Map<String, Object> context) throws IOException {
        String line = reader.readLine();
        String[] labels = line.split("\\s+");
        context.put("DATA_FORMAT", labels);

        // check number of field
        Object numfield = context.get("NUMBER_OF_FIELDS");
        if(numfield instanceof String && ((String) numfield).matches("[0-9]+")) {
            int n = Integer.parseInt((String)numfield);
            if (n != labels.length) {
                System.err.println("Expected number of format field did not match real number of format fields");
            }
        }

        // check end of data
        if( !reader.readLine().trim().equalsIgnoreCase("END_DATA_FORMAT")) {
            System.err.println("Data format segment ended unexpectedly");
        }
        context.put("NUMBER_OF_FIELDS", labels.length);
    }

    private static void parseData(BufferedReader reader, Map<String, Object> context) throws IOException {
        Object numSets = context.get("NUMBER_OF_SETS");

        List<String[]> entries = new ArrayList<>(1);
        String line = null;
        while ( !(line = reader.readLine()).equalsIgnoreCase("END_DATA")) {
            String[] entry = line.split("\\s+");
            entries.add(entry);
        }

        context.put("DATA", entries);

        if(numSets instanceof String && ((String)numSets).matches("[0-9]+")) {
            int n = Integer.parseInt((String)numSets);
            if (n != entries.size()) {
                System.err.println("Expected number of values("+numSets+") did not match real number of values("+entries.size()+")");
            }
        }
        context.put("NUMBER_OF_SETS", entries.size());
    }

}
