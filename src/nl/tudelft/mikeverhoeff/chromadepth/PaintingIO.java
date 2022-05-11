package nl.tudelft.mikeverhoeff.chromadepth;

import javafx.scene.control.Alert;
import nl.tudelft.mikeverhoeff.chromadepth.colorspace.MyPrinterSimulator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaintingIO {

    public static void save(Painting painting, File file) {
        try {
            File metaDataFile;
            // make sure a folder exists for this file
            if (file.exists()) {
                if (!file.isDirectory()) {
                    metaDataFile = file;
                    file = file.getParentFile();
                } else {
                    metaDataFile = new File(file, "meta.spim");
                }
            } else {
                file.mkdirs();
                metaDataFile = new File(file, "meta.spim");
            }


            DataOutputStream metaWriter = new DataOutputStream(new FileOutputStream(metaDataFile));
            metaWriter.writeInt(painting.getWidth());
            metaWriter.writeInt(painting.getHeight());
            metaWriter.writeInt(painting.getChannels().size());
            metaWriter.close();

            for(int i=0; i<painting.getChannels().size(); i++) {
                File channelFile = new File(file, "Chanel_"+i+".bmp");
                ColorChannel colorChannel = painting.getChannels().get(i);
                BufferedImage bufferedImage = new BufferedImage(painting.getWidth(), painting.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                WritableRaster raster = bufferedImage.getRaster();
                for(int x=0; x<painting.getWidth(); x++) {
                    for(int y=0; y< painting.getHeight(); y++) {
                        raster.setPixel(x, y, new int[] {colorChannel.getPixel(x, y)});
                    }
                }
                ImageIO.write(bufferedImage, "bmp", channelFile);
            }

        } catch (IOException exception) {
            exception.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Save Error");
            alert.setHeaderText("An error occurred while trying to save your image");
            alert.setContentText(exception.getLocalizedMessage());
            alert.showAndWait();
        }
    }

    public static Painting load(File file) {
        // the file is the meta data file
        try {
            DataInputStream reader = new DataInputStream(new FileInputStream(file));
            int width = reader.readInt();
            int height = reader.readInt();
            int numChannels = reader.readInt();

            File parent = file.getParentFile();

            List<Paint> paints = new ArrayList<>(numChannels);
            for(int i=0; i<numChannels; i++) {
                paints.add(Paint.getDefault());
            }

            Painting painting = new Painting(width, height, paints);

            for(int i=0; i<numChannels; i++) {
                File channelFile = new File(parent, "Chanel_"+i+".bmp");
                BufferedImage bufferedImage = ImageIO.read(channelFile);
                WritableRaster raster = bufferedImage.getRaster();

                ColorChannel colorChannel = painting.getChannels().get(i);

                for(int x=0; x<width; x++) {
                    for(int y=0; y< height; y++) {
                        byte sample = (byte)raster.getSample(x, y, 0);
                        colorChannel.setPixel(x, y, sample);
                    }
                }
            }
            return painting;

        } catch (IOException exception) {
            exception.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Load Error");
            alert.setHeaderText("An error occurred while trying to load your image");
            alert.setContentText(exception.getLocalizedMessage());
            alert.showAndWait();

            return null;
        }
    }

    public static Painting loadImage(File file) {

        try {
            BufferedImage image = ImageIO.read(file);
            int width = image.getWidth();
            int height = image.getHeight();
            int chanels = image.getColorModel().getNumComponents();
            List<Paint> paints = new ArrayList<>(chanels);
            if(chanels == 3 || true) {
                paints.add(new Paint(Paint.RGBColor.RED));
                paints.add(new Paint(Paint.RGBColor.GREEN));
                paints.add(new Paint(Paint.RGBColor.BLUE));
            } else {
                for (int i = 0; i < chanels; i++) {
                    paints.add(Paint.getDefault());
                }
            }
            Painting painting = new Painting(width, height, new MyPrinterSimulator());

            for(int x=0; x<width; x++) {
                for(int y=0; y<height; y++) {
                    int color = image.getRGB(x, y);
                    painting.setPixel(x, y, new byte[]{(byte)((color>>16)&0xff), (byte)((color>>8)&0xff), (byte)((color)&0xff)});
                }
            }

            return painting;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
