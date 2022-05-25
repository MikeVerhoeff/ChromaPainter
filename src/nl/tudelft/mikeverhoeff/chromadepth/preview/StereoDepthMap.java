package nl.tudelft.mikeverhoeff.chromadepth.preview;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoMatcher;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StereoDepthMap {

    public static Mat imageToMat(Image image) {
        ByteArrayOutputStream bytedata = new ByteArrayOutputStream();
        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            System.out.println("buffredImage: "+bufferedImage);
            boolean writerSuccess = ImageIO.write(bufferedImage, "png", bytedata);
            System.out.println("Writer result: "+writerSuccess);
            System.out.println(bytedata.size());
            bytedata.flush();
            Mat leftmat = Imgcodecs.imdecode(new MatOfByte(bytedata.toByteArray()), Imgcodecs.IMREAD_COLOR);
            return leftmat;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static Image matToImage(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".bmp", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    public static Image OpenCVStereoDepthEstimationBlockMatching(Image left, Image right, StereoMatcher matcher) {
        Mat leftMat = new Mat();
        Imgproc.cvtColor(imageToMat(left), leftMat, Imgproc.COLOR_RGB2GRAY);
        Mat rightMat = new Mat();
        Imgproc.cvtColor(imageToMat(right), rightMat, Imgproc.COLOR_RGB2GRAY);


        HighGui.imshow("left", leftMat);
        //HighGui.waitKey();
        HighGui.imshow("right", rightMat);
        //HighGui.waitKey();


        //StereoBM stereoEstimator = StereoBM.create(16, 15);
        Mat disparity = new Mat();
        matcher.compute(leftMat, rightMat, disparity);

        System.out.println("Disparity: "+disparity);

        Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(disparity);
        System.out.println("Max Value: "+minMaxLocResult.maxVal+", Min Value: "+minMaxLocResult.minVal);


        Mat grayResult = new Mat();
        disparity.convertTo(grayResult, CvType.CV_8U, 255.0/minMaxLocResult.maxVal, -minMaxLocResult.minVal);

        Mat colormaped = new Mat();
        Imgproc.applyColorMap(grayResult, colormaped, Imgproc.COLORMAP_JET);

        HighGui.imshow("disp", colormaped);
        HighGui.waitKey(1);

        return matToImage(disparity);
    }

}
