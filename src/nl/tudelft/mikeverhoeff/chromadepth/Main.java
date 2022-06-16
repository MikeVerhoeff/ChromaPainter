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

import java.nio.file.Paths;

public class Main extends Application {

    public static String SpectrumDirectory = "C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra";

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
        SpectrumDirectory = Paths.get("").toAbsolutePath().toString()+"\\src\\res\\Spectra";
        System.out.println(SpectrumDirectory);
        launch(args);
    }
}
