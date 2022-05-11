package nl.tudelft.mikeverhoeff.chromadepth.ui.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import nl.tudelft.mikeverhoeff.chromadepth.PaintMix;
import nl.tudelft.mikeverhoeff.chromadepth.painttools.BrushTool;
import nl.tudelft.mikeverhoeff.chromadepth.painttools.PaintTool;
import nl.tudelft.mikeverhoeff.chromadepth.painttools.PickColorTool;

public class ToolSelector extends AnchorPane {

    private MainController mainController;
    private Slider slider;
    private Spinner<Integer> spinner;
    private Button colorPickButton;

    private static final int MAX_SIZE = 100;

    private PaintTool tool;

    public ToolSelector() {
        slider = new Slider(0, MAX_SIZE, 10);
        spinner = new Spinner(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, MAX_SIZE));
        spinner.getValueFactory().setValue(10);

        this.getChildren().add(slider);
        AnchorPane.setTopAnchor(slider,0.0);
        AnchorPane.setLeftAnchor(slider, 0.0);
        AnchorPane.setRightAnchor(slider, 50.0);

        this.getChildren().add(spinner);
        AnchorPane.setRightAnchor(spinner, 0.0);
        AnchorPane.setTopAnchor(spinner, 0.0);
        spinner.setPrefWidth(60);

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!oldValue.equals(newValue))
                    spinner.getValueFactory().setValue(newValue.intValue());

            }
        });

        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (!oldValue.equals(newValue)) {
                    slider.setValue(newValue);
                    handleChange(newValue);
                }
            }
        });

        colorPickButton = new Button("pick color");
        colorPickButton.setOnAction(this::selectColorPick);
        this.getChildren().add(colorPickButton);
        AnchorPane.setLeftAnchor(colorPickButton, 0.0);
        AnchorPane.setTopAnchor(colorPickButton, 20.0);

    }

    private void selectColorPick(ActionEvent event) {
        colorPickButton.setText("pick color (selected)");
        colorPickButton.setOnAction(this::deselectColorPick);
        mainController.setTool(new PickColorTool(mainController));
    }

    private void deselectColorPick(ActionEvent event) {
        colorPickButton.setText("pick color");
        colorPickButton.setOnAction(this::selectColorPick);
        mainController.setTool(new BrushTool(spinner.getValue(), mainController.getColorMixer().getPaintMix()));
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void handleChange(int value) {
        if(mainController != null) {
            tool = new BrushTool(value, mainController.getColorMixer().getPaintMix());
            colorPickButton.setOnAction(this::selectColorPick);
            mainController.setTool(tool);
        }
    }

    public void handlePaintChange(byte[] paintMix) {
        this.tool = new BrushTool(spinner.getValue(), paintMix);
    }

    public PaintTool getTool() {
        return tool;
    }

}
