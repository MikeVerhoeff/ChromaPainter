package nl.tudelft.mikeverhoeff.chromadepth.ui.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.mikeverhoeff.chromadepth.Paint;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class PaintSlider extends AnchorPane {

    private Slider slider;
    private Spinner<Integer> numberSpinner;
    private Rectangle colorRect;

    private Paint paint;
    private byte value;

    private Consumer<Byte> changeHandler;
    private MainController mainController;

    public PaintSlider(Paint paint, byte value) {

        this.value = value;
        System.out.println("Creating slider whith value:" + Byte.toUnsignedInt(value));

        slider = new Slider(0, 255, Byte.toUnsignedInt(value));
        numberSpinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255));
        numberSpinner.getValueFactory().setValue(Byte.toUnsignedInt(value));
        numberSpinner.setEditable(true);
        colorRect = new Rectangle(20,20);
        this.paint=paint;
        if(paint != null) {
            int screenColor = paint.getScreenColor();
            colorRect.setFill(Color.rgb((screenColor>>16)&0xff, (screenColor>>8)&0xff, (screenColor)&0xff));
        }

        //slider.valueProperty().bind(numberSpinner.getValueFactory().valueProperty());
        // do binding the other way around to
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!oldValue.equals(newValue))
                    numberSpinner.getValueFactory().setValue(newValue.intValue());

            }
        });

        numberSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (!oldValue.equals(newValue)) {
                    slider.setValue(newValue);
                    handleChange(newValue);
                }
            }
        });

        colorRect.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            paint.displayEditDialog(mainController);
        });

        this.getChildren().add(slider);
        AnchorPane.setTopAnchor(slider,0.0);
        AnchorPane.setLeftAnchor(slider, 0.0);
        AnchorPane.setRightAnchor(slider, 90.0);

        this.getChildren().add(numberSpinner);
        AnchorPane.setRightAnchor(numberSpinner, 25.0);
        AnchorPane.setTopAnchor(numberSpinner, 0.0);
        numberSpinner.setPrefWidth(60);

        this.getChildren().add(colorRect);
        AnchorPane.setTopAnchor(colorRect, 0.0);
        AnchorPane.setRightAnchor(colorRect, 0.0);


    }

    private void handleChange(int value) {
        this.value = (byte)value;
        if(changeHandler != null)
            changeHandler.accept(this.value);
    }

    public byte getValue() {
        return value;
    }

    public void setChangeHandler(Consumer<Byte> changeHandler) {
        this.changeHandler = changeHandler;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void updateColorChange() {
        if(paint != null) {
            int screenColor = paint.getScreenColor();
            colorRect.setFill(Color.rgb((screenColor>>16)&0xff, (screenColor>>8)&0xff, (screenColor)&0xff));
        }
    }

    public void setValue(byte value) {
        numberSpinner.getValueFactory().setValue(10);
    }
}
