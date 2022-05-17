package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import nl.tudelft.mikeverhoeff.chromadepth.Painting;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.NewCanvasDialogController;

public class ImageCompare {

    public void compareSpectralAndScreen(Painting painting, Window window) {
        boolean storePreviewValue = painting.doesPreviewRealColors();

        painting.setPreviewRealColors(true);
        painting.calculateRGB();
        Image printColors = copyImage(painting.getImage());

        painting.setPreviewRealColors(false);
        painting.calculateRGB();
        Image rgbColors = copyImage(painting.getImage());

        painting.setPreviewRealColors(storePreviewValue);
        painting.calculateRGB();

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(window);


        ImageView printColorViewer = new ImageView(printColors);
        ImageView rgbColorViewer = new ImageView(rgbColors);
        FlowPane dialogUI = new FlowPane(printColorViewer, rgbColorViewer);


        Scene dialogScene = new Scene(dialogUI, printColors.getWidth()*3, printColors.getHeight()*2);
        dialog.setScene(dialogScene);
        dialog.show();

    }

    public Image copyImage(Image original) {
        PixelReader originalReader = original.getPixelReader();
        WritableImage clone = new WritableImage((int)original.getWidth(), (int)original.getHeight());
        PixelWriter cloneWriter = clone.getPixelWriter();
        for(int x=0; x<original.getWidth(); x++) {
            for(int y=0; y<original.getHeight(); y++) {
                cloneWriter.setArgb(x, y, originalReader.getArgb(x, y));
            }
        }
        return clone;
    }

}
