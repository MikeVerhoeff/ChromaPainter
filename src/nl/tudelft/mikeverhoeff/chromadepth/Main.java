package nl.tudelft.mikeverhoeff.chromadepth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.xyYChart;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/res/UI/MainWindow.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/res/UI/ColorSpaceTest.fxml"));
        //root = new StackPane(new ImageView(xyYChart.renderChart()));
        primaryStage.setTitle("Chroma Depth Painting");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat mat = Mat.eye(3,3, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());
        launch(args);
    }
}
