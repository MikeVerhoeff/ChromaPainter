package nl.tudelft.mikeverhoeff.chromadepth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ColorExperimentsMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("/res/UI/MainWindow.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/res/UI/ColorSpaceTest.fxml"));
        primaryStage.setTitle("Chroma Depth Painting");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
