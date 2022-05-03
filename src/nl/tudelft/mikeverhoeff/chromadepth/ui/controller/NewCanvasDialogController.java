package nl.tudelft.mikeverhoeff.chromadepth.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.tudelft.mikeverhoeff.chromadepth.Paint;
import nl.tudelft.mikeverhoeff.chromadepth.Painting;

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

    @FXML
    private Spinner<Integer> numPaintsField;

    private MainController mainController;

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
        numPaintsField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        numPaintsField.setEditable(true);
        numPaintsField.getValueFactory().setValue(3);
    }

    @FXML
    void cancleAction(ActionEvent event) {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void createAction(ActionEvent event) {
        List<Paint> paints = new ArrayList<>(numPaintsField.getValue());
        for(int i=0; i<numPaintsField.getValue(); i++) {
            paints.add(Paint.getDefault());
        }
        Painting painting = new Painting(widthField.getValue(), heightField.getValue(), paints);

        mainController.displayPainting(painting);

        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
