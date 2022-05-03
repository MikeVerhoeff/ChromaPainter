package nl.tudelft.mikeverhoeff.chromadepth.preview;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.MainController;

public class EyeShiftUI extends FlowPane {

    private ImageView leftImage;
    private ImageView rightImage;

    private Spinner<Double> distanceSelector;

    private MainController mainController;

    public EyeShiftUI() {
        this.setAlignment(Pos.TOP_CENTER);
        leftImage = new ImageView();
        rightImage = new ImageView();
        distanceSelector = new Spinner<Double>(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0.4, 0.1));
        distanceSelector.setEditable(true);
        distanceSelector.setMinWidth(60);

        HBox box = new HBox();
        box.getChildren().add(new Label("Distance to painting (m):"));
        box.getChildren().add(distanceSelector);

        Button calcButton = new Button("Calculate result");
        calcButton.setOnAction(event -> {
            ShiftedImageCalculator calculator = new ShiftedImageCalculator(mainController.getCanvas().getPainting());
            calculator.splitImage();
            Image image = calculator.simpleUniformShift();
            leftImage.setImage(image);
        });

        this.getChildren().add(box);
        this.getChildren().add(calcButton);
        this.getChildren().add(leftImage);
        this.getChildren().add(rightImage);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
