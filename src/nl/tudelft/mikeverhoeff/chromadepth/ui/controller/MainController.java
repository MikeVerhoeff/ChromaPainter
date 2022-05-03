package nl.tudelft.mikeverhoeff.chromadepth.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.*;
import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.PaintMix;
import nl.tudelft.mikeverhoeff.chromadepth.Painting;
import nl.tudelft.mikeverhoeff.chromadepth.PaintingIO;
import nl.tudelft.mikeverhoeff.chromadepth.painttools.PaintTool;
import nl.tudelft.mikeverhoeff.chromadepth.preview.EyeShiftUI;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane CanvasSpace;
    private Canvas canvas;

    @FXML
    private AnchorPane PaintSelectSpace;
    private ColorMixer colorMixer;
    private ToolSelector toolSelector;

    @FXML
    private AnchorPane PreviewSpace;
    private EyeShiftUI singleEyePreview;

    @FXML
    private MenuBar menuBar;

    private PaintTool tool;

    @FXML
    public void TestAction(ActionEvent event) {

        List<Paint> paints = new ArrayList<>(3);
        paints.add(new Paint(Paint.RGBColor.RED));
        paints.add(new Paint(Paint.RGBColor.GREEN));
        paints.add(new Paint(Paint.RGBColor.BLUE));

        PaintMix red    = new PaintMix(paints, (byte)255, (byte)0, (byte)0);
        PaintMix yellow = new PaintMix(paints, (byte)255, (byte)255, (byte)0);
        PaintMix green  = new PaintMix(paints, (byte)0, (byte)255, (byte)0);
        PaintMix cyan   = new PaintMix(paints, (byte)0, (byte)255, (byte)255);
        PaintMix blue   = new PaintMix(paints, (byte)0, (byte)0, (byte)255);
        PaintMix magenta= new PaintMix(paints, (byte)255, (byte)0, (byte)255);

        Painting painting = new Painting(50, 50, paints);

        painting.setSquare(10,10, 10, 10, red);
        painting.setSquare(10,20, 10, 10, yellow);
        painting.setSquare(20,10, 10, 10, green);
        painting.setSquare(20,20, 10, 10, cyan);
        painting.setSquare(30,10, 10, 10, blue);
        painting.setSquare(30,20, 10, 10, magenta);

        //CanvasImage.setImage(painting.getImage());
        displayPainting(painting);
        canvas.setPaintTool(new PaintTool());
    }

    @FXML
    public void NewAction(ActionEvent event) throws IOException {
        System.out.println("Creating new painting");
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(menuBar.getScene().getWindow());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/UI/newCanvasDialog.fxml"));
        Parent dialogUI = loader.load();
        NewCanvasDialogController newCanvasDialogController = (NewCanvasDialogController)loader.getController();
        newCanvasDialogController.setMainController(this);

        Scene dialogScene = new Scene(dialogUI, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML
    public void saveAsAction(ActionEvent event) {
        System.out.println("Saving as...");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("All files", "*.*");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setTitle("Save Painting");
        File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
        if (file==null) {
            System.out.println("No File Selected");
        } else {
            System.out.print("File: ");
            System.out.println(file);
            PaintingIO.save(canvas.getPainting(), file);
        }
    }

    @FXML
    public void openAction(ActionEvent event) {
        System.out.println("Opening");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Spectral Image Metadata", "*.spim");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setTitle("Opening Painting");
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        Painting newPainting = PaintingIO.load(file);
        if(newPainting != null) {
            displayPainting(newPainting);
            updateColorChange();
        }
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        canvas = new Canvas();
        CanvasSpace.getChildren().add(canvas);

        colorMixer = new ColorMixer();
        colorMixer.setMainController(this);
        AnchorPane.setLeftAnchor(colorMixer, 0.0);
        AnchorPane.setRightAnchor(colorMixer, 0.0);
        AnchorPane.setTopAnchor(colorMixer, 150.0);
        PaintSelectSpace.getChildren().add(colorMixer);

        toolSelector = new ToolSelector();
        toolSelector.setMainController(this);
        AnchorPane.setLeftAnchor(toolSelector, 0.0);
        AnchorPane.setRightAnchor(toolSelector, 0.0);
        AnchorPane.setTopAnchor(toolSelector, 100.0);
        PaintSelectSpace.getChildren().add(toolSelector);

        singleEyePreview = new EyeShiftUI();
        singleEyePreview.setMainController(this);
        AnchorPane.setTopAnchor(singleEyePreview, 50.0);
        AnchorPane.setLeftAnchor(singleEyePreview, 0.0);
        AnchorPane.setRightAnchor(singleEyePreview, 0.0);
        AnchorPane.setBottomAnchor(singleEyePreview, 0.0);
        PreviewSpace.getChildren().add(singleEyePreview);
    }

    public void displayPainting(Painting painting) {
        List<Paint> paints = painting.getPaints();
        byte[] vales = new byte[paints.size()];
        PaintMix paintMix = new PaintMix(paints, vales);
        colorMixer.setPaintMix(paintMix);
        canvas.setPainting(painting);
        toolSelector.handlePaintChange(paintMix);
        if(tool != null) {
            canvas.setPaintTool(tool);
        } else {
            tool = toolSelector.getTool();
            if(tool != null) {
                canvas.setPaintTool(tool);
            }
        }
    }

    public void setTool(PaintTool tool) {
        this.tool = tool;
        if(canvas != null) {
            canvas.setPaintTool(tool);
        }
    }

    public ColorMixer getColorMixer() {
        return colorMixer;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void updateColorChange() {
        colorMixer.updateColorChange();
        canvas.updateColorChange();
    }

    public Window getWindow() {
        return menuBar.getScene().getWindow();
    }
}
