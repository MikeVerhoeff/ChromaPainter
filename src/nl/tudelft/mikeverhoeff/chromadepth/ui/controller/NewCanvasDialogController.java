package nl.tudelft.mikeverhoeff.chromadepth.ui.controller;

import com.sun.javafx.collections.ImmutableObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.Painting;
import nl.tudelft.mikeverhoeff.chromadepth.colorspace.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NewCanvasDialogController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Spinner<Integer> heightField;

    @FXML
    private Spinner<Integer> widthField;

    //@FXML
    //private Spinner<Integer> numPaintsField;

    @FXML
    private ChoiceBox<String> colorSpaceChoice;

    private MainController mainController;

    public static final String CMYK_NAME = "CMYK";
    public static final String PRINTER_NAME = "Printer Simulator";
    public static final String ADDITIVE_NAME = "Additive";
    public static final String DYE_MIXER_NAME = "Dye Mixer";

    public NewCanvasDialogController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        heightField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        heightField.setEditable(true);
        heightField.getValueFactory().setValue(200);
        widthField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        widthField.setEditable(true);
        widthField.getValueFactory().setValue(200);
        //numPaintsField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        //numPaintsField.setEditable(true);
        //numPaintsField.getValueFactory().setValue(3);
        colorSpaceChoice.setItems(FXCollections.observableArrayList(CMYK_NAME, PRINTER_NAME, ADDITIVE_NAME, DYE_MIXER_NAME));
        colorSpaceChoice.setValue(CMYK_NAME);
    }

    @FXML
    void cancleAction(ActionEvent event) {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void createAction(ActionEvent event) {
        /*List<Paint> paints = new ArrayList<>(numPaintsField.getValue());
        for(int i=0; i<numPaintsField.getValue(); i++) {
            paints.add(Paint.getDefault());
        }*/

        ColorSpace colorSpace = null;
        switch (colorSpaceChoice.getValue()) {
            case CMYK_NAME:
                colorSpace = new CMYKColorSpace();
                break;
            case PRINTER_NAME:
                colorSpace = new MyPrinterSimulator();
                break;
            case ADDITIVE_NAME:
                colorSpace = new AdditiveColorSpace();
                break;
            case DYE_MIXER_NAME:
                colorSpace = new KubelkaMunkDyeColorSpace();
                break;
        }
        colorSpace.configureGUI(rootPane.getScene().getWindow(), (configuredColorSpace)-> {

            Painting painting = new Painting(widthField.getValue(), heightField.getValue(), configuredColorSpace);

            mainController.displayPainting(painting);

            Stage stage = (Stage)rootPane.getScene().getWindow();
            stage.close();
        });
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
