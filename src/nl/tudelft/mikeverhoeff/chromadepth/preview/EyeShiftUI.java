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
import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoSGBM;

public class EyeShiftUI extends VBox {

    private ImageView leftImage;
    private ImageView rightImage;
    private ImageView animatedImage;
    private Label debugTextLabel;
    private ImageView depthMapImage;

    private boolean doAnimate = false;
    private Image[] frames;

    private Spinner<Double> distanceSelector;
    private Spinner<Integer> fixedWavelengthSelector;

    private MainController mainController;

    public EyeShiftUI() {
        this.setAlignment(Pos.TOP_CENTER);
        leftImage = new ImageView();
        rightImage = new ImageView();
        animatedImage = new ImageView();
        depthMapImage = new ImageView();
        distanceSelector = new Spinner<Double>(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0.4, 0.05));
        distanceSelector.setEditable(true);
        distanceSelector.setMinWidth(60);
        fixedWavelengthSelector = new Spinner<Integer>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 460, 10));
        fixedWavelengthSelector.setEditable(true);

        HBox box = new HBox();
        box.getChildren().add(new Label("Distance to painting (m):"));
        box.getChildren().add(distanceSelector);

        Spinner<Integer> numDisparSelector = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 16));
        numDisparSelector.setEditable(true);
        Spinner<Integer> blockSizeSelector = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 15));
        blockSizeSelector.setEditable(true);

        Button calcButton = new Button("Calculate result");
        calcButton.setOnAction(event -> {
            System.out.println("Starting calculation");
            doAnimate = false;
            ShiftedImageCalculator calculator = new ShiftedImageCalculator(mainController.getCanvas().getPainting());
            calculator.splitImage();
            //Image image = calculator.simpleUniformShift((float)(double)distanceSelector.getValue());
            Image imageLeft = calculator.getImageAtDistance((float)(double)distanceSelector.getValue(), 1, fixedWavelengthSelector.getValue(), 0.26e-3);
            leftImage.setImage(imageLeft);

            Image imageRight = calculator.getImageAtDistance((float)(double)distanceSelector.getValue(), -1, fixedWavelengthSelector.getValue(), 0.26e-3);
            this.rightImage.setImage(imageRight);


            frames = new Image[9];
            for(int i=0; i<frames.length; i++) {
                float eye = (i/ (float)(frames.length-1))*2-1;
                System.out.println(eye);
                frames[i] = calculator.getImageAtDistance((float)(double)distanceSelector.getValue(), eye, fixedWavelengthSelector.getValue(), 0.26e-3);
            }

            StereoBM stereoBM = StereoBM.create(numDisparSelector.getValue(), blockSizeSelector.getValue());
            StereoSGBM stereoSGBM = StereoSGBM.create(numDisparSelector.getValue(), blockSizeSelector.getValue());
            depthMapImage.setImage(StereoDepthMap.OpenCVStereoDepthEstimationBlockMatching(imageLeft, imageRight, stereoBM));

            doAnimate = true;
        });

        this.getChildren().add(box);
        this.getChildren().add(new HBox(new Label("Static wavelength:"), fixedWavelengthSelector));
        this.getChildren().addAll(numDisparSelector, blockSizeSelector);
        this.getChildren().add(calcButton);
        this.getChildren().add(new FlowPane(leftImage, rightImage));
        this.getChildren().add(animatedImage);
        this.getChildren().add(depthMapImage);

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
