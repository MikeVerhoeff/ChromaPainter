package nl.tudelft.mikeverhoeff.chromadepth.preview;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.MainController;

public class EyeShiftUI extends VBox {

    private ImageView leftImage;
    private ImageView rightImage;
    private ImageView animatedImage;
    private Label debugTextLabel;

    private boolean doAnimate = false;
    private Image[] frames;

    private Spinner<Double> distanceSelector;

    private MainController mainController;

    public EyeShiftUI() {
        this.setAlignment(Pos.TOP_CENTER);
        leftImage = new ImageView();
        rightImage = new ImageView();
        animatedImage = new ImageView();
        distanceSelector = new Spinner<Double>(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0.4, 0.1));
        distanceSelector.setEditable(true);
        distanceSelector.setMinWidth(60);

        HBox box = new HBox();
        box.getChildren().add(new Label("Distance to painting (m):"));
        box.getChildren().add(distanceSelector);

        Button calcButton = new Button("Calculate result");
        calcButton.setOnAction(event -> {
            System.out.println("Starting calculation");
            doAnimate = false;
            ShiftedImageCalculator calculator = new ShiftedImageCalculator(mainController.getCanvas().getPainting());
            calculator.splitImage();
            //Image image = calculator.simpleUniformShift((float)(double)distanceSelector.getValue());
            Image image = calculator.getImageAtDistance((float)(double)distanceSelector.getValue(), 1, 0.26e-3);
            leftImage.setImage(image);

            Image imageRight = calculator.getImageAtDistance((float)(double)distanceSelector.getValue(), -1, 0.26e-3);
            this.rightImage.setImage(imageRight);


            frames = new Image[9];
            for(int i=0; i<frames.length; i++) {
                float eye = (i/ (float)(frames.length-1))*2-1;
                System.out.println(eye);
                frames[i] = calculator.getImageAtDistance((float)(double)distanceSelector.getValue(), eye, 0.26e-3);
            }

            doAnimate = true;
        });

        this.getChildren().add(box);
        this.getChildren().add(calcButton);
        this.getChildren().add(leftImage);
        this.getChildren().add(rightImage);
        this.getChildren().add(animatedImage);

        debugTextLabel = new Label();
        this.getChildren().add(debugTextLabel);

        // animation
        AnimationTimer animationTimer = new AnimationTimer() {
            int frame=0;
            int spacing = 10;
            @Override
            public void handle(long now) {
                if(doAnimate) {
                    int i = frame / spacing;
                    if(i<frames.length) {
                        animatedImage.setImage(frames[i]);
                        debugTextLabel.setText("Frame: "+i+", Eye: "+((i/ (float)(frames.length-1))*2-1));
                        frame++;
                    } else if(-i+2*frames.length-2>0) {
                        animatedImage.setImage(frames[-i+2*frames.length-2]);
                        debugTextLabel.setText("Frame: "+i+", Eye: "+(((-i+2*frames.length-2)/ (float)(frames.length-1))*2-1));
                        frame++;
                    } else {
                        frame=0;
                    }

                } else {
                    frame=0;
                }
            }
        };
        animationTimer.start();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
