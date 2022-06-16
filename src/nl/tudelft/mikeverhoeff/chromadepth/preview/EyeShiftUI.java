package nl.tudelft.mikeverhoeff.chromadepth.preview;

import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.stage.DirectoryChooser;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.MainController;
import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoSGBM;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class EyeShiftUI extends VBox {

    private ImageView leftImage;
    private ImageView rightImage;
    private ImageView animatedImage;
    private Label debugTextLabel;
    private ImageView depthMapImage;
    private Button exportButton;

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
        exportButton = new Button("Export");
        exportButton.setVisible(false);

        HBox box = new HBox();
        box.getChildren().add(new Label("Distance to painting (m):"));
        box.getChildren().add(distanceSelector);

        Spinner<Integer> numDisparSelector = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 16));
        numDisparSelector.setEditable(true);
        Spinner<Integer> blockSizeSelector = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200, 15));
        blockSizeSelector.setEditable(true);
        Spinner<Integer> subPixelSelector = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 16, 1));
        subPixelSelector.setEditable(true);

        Button calcButton = new Button("Calculate result");
        calcButton.setOnAction(event -> {
            System.out.println("Starting calculation");
            doAnimate = false;
            ShiftedImageCalculator calculator = new ShiftedImageCalculator(mainController.getCanvas().getPainting());
            calculator.splitImage();
            //Image image = calculator.simpleUniformShift((float)(double)distanceSelector.getValue());
            Image imageLeft = calculator.getImageAtDistance((float)(double)distanceSelector.getValue(), 1, fixedWavelengthSelector.getValue(), 0.26e-3, subPixelSelector.getValue());
            leftImage.setImage(imageLeft);

            Image imageRight = calculator.getImageAtDistance((float)(double)distanceSelector.getValue(), -1, fixedWavelengthSelector.getValue(), 0.26e-3, subPixelSelector.getValue());
            this.rightImage.setImage(imageRight);


            frames = new Image[9];
            for(int i=0; i<frames.length; i++) {
                float eye = (i/ (float)(frames.length-1))*2-1;
                System.out.println(eye);
                frames[i] = calculator.getImageAtDistance((float)(double)distanceSelector.getValue(), eye, fixedWavelengthSelector.getValue(), 0.26e-3, subPixelSelector.getValue());
            }

            StereoBM stereoBM = StereoBM.create(numDisparSelector.getValue(), blockSizeSelector.getValue());
            StereoSGBM stereoSGBM = StereoSGBM.create(numDisparSelector.getValue(), blockSizeSelector.getValue());
            Image disparityMap = StereoDepthMap.OpenCVStereoDepthEstimationBlockMatching(imageLeft, imageRight, stereoBM);
            depthMapImage.setImage(disparityMap);

            exportButton.setVisible(true);
            exportButton.setOnAction(e -> {
                DirectoryChooser exportLocationChooser = new DirectoryChooser();
                File saveFolder = exportLocationChooser.showDialog(this.getScene().getWindow());
                if(saveFolder != null) {
                    try {
                        saveImage(imageLeft, saveFolder, "left.png");
                        saveImage(imageRight, saveFolder, "right.png");
                        saveImage(disparityMap, saveFolder, "disparity.png");
                        saveImage(calculator.getImageAtDistance(distanceSelector.getValue(), 0, 530, 0.26e-3, subPixelSelector.getValue()), saveFolder, "normal.png");
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            });

            doAnimate = true;
        });

        this.getChildren().add(box);
        this.getChildren().add(new HBox(new Label("Static wavelength:"), fixedWavelengthSelector));
        this.getChildren().addAll(numDisparSelector, blockSizeSelector);
        this.getChildren().add(new HBox(new Label("Shifting subpixels:"), subPixelSelector));
        this.getChildren().add(calcButton);
        this.getChildren().add(new FlowPane(leftImage, rightImage));
        this.getChildren().add(animatedImage);
        this.getChildren().add(depthMapImage);
        this.getChildren().add(exportButton);

        debugTextLabel = new Label();
        this.getChildren().add(debugTextLabel);

        // animation
        AnimationTimer animationTimer = new AnimationTimer() {
            int frame=0;
            int spacing = 4;
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

    private void saveImage(Image image, File folder, String filename) throws IOException {
        File imageFile = new File(folder, filename);
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imageFile);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
