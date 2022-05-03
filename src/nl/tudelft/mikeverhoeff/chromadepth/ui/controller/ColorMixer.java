package nl.tudelft.mikeverhoeff.chromadepth.ui.controller;

import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import nl.tudelft.mikeverhoeff.chromadepth.PaintMix;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrogramChart;

public class ColorMixer extends AnchorPane {


    private PaintMix paintMix;
    private VBox paintSlides;
    private Pane mixColor;
    private SpectrogramChart spectrogramChart;
    private MainController mainController;

    public ColorMixer() {
        mixColor = new Pane();
        this.getChildren().add(mixColor);
        AnchorPane.setTopAnchor(mixColor, 10.0);
        AnchorPane.setLeftAnchor(mixColor, 10.0);
        AnchorPane.setRightAnchor(mixColor, 10.0);
        mixColor.setPrefHeight(50);

        paintSlides = new VBox();
        this.getChildren().add(paintSlides);
        AnchorPane.setTopAnchor(paintSlides, 70.0);
        AnchorPane.setLeftAnchor(paintSlides, 0.0);
        AnchorPane.setRightAnchor(paintSlides, 0.0);

        spectrogramChart = new SpectrogramChart();
        this.getChildren().add(spectrogramChart);
        AnchorPane.setTopAnchor(spectrogramChart, 200.0);
        AnchorPane.setLeftAnchor(spectrogramChart, 0.0);
        AnchorPane.setRightAnchor(spectrogramChart, 0.0);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPaintMix(PaintMix paintMix) {
        this.paintMix = paintMix;
        paintSlides.getChildren().clear();
        for(int i=0; i<paintMix.getPaints().size(); i++) {
            PaintSlider slider = new PaintSlider(paintMix.getPaints().get(i),paintMix.getValues()[i]);
            int finalI = i;
            slider.setChangeHandler(value -> {
                paintMix.getValues()[finalI]=value;
                updateMixColor();
            });
            slider.setMainController(mainController);
            paintSlides.getChildren().add(slider);
        }
        updateMixColor();
    }

    private void updateMixColor() {
        int screencolor = paintMix.getScreenColor();
        mixColor.setStyle("-fx-background-color: rgb("+((screencolor>>16) & 0xff)+","+((screencolor>>8) & 0xff)+","+((screencolor) & 0xff)+")");
        spectrogramChart.displayColorSpectrum(paintMix.getSpectrum());
    }

    public PaintMix getPaintMix() {
        return paintMix;
    }

    public void updateColorChange() {
        updateMixColor();
        for(Node node:paintSlides.getChildren()) {
            if(node instanceof PaintSlider) {
                ((PaintSlider)node).updateColorChange();
            }
        }
    }
}
