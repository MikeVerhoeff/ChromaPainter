package nl.tudelft.mikeverhoeff.chromadepth.ui.controller;

import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import nl.tudelft.mikeverhoeff.chromadepth.PaintMix;
import nl.tudelft.mikeverhoeff.chromadepth.colorspace.ColorSpace;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrogramChart;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;

public class ColorMixer extends AnchorPane {


    //private PaintMix paintMix;
    private VBox paintSlides;
    private Pane mixColor;
    private SpectrogramChart spectrogramChart;
    private MainController mainController;

    private ColorSpace colorSpace;
    private byte[] values;

    public ColorMixer() {
        mixColor = new Pane();
        this.getChildren().add(mixColor);
        AnchorPane.setTopAnchor(mixColor, 10.0);
        AnchorPane.setLeftAnchor(mixColor, 10.0);
        AnchorPane.setRightAnchor(mixColor, 10.0);
        mixColor.setPrefHeight(50);
        mixColor.setOnMouseClicked(e->{
            if(this.colorSpace != null) {
                SelectSpectrumController.display(this.getScene().getWindow(), s -> colorSpace.setBackground(s));
                mainController.updateColorChange();
            }
        });

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

    public void setColorSpace(ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
        AnchorPane.setTopAnchor(spectrogramChart, 100.0+30*colorSpace.getNumberOfChannels());
    }

    public void setPaintMix(byte[] values) {
        this.values = values;
        paintSlides.getChildren().clear();
        for(int i=0; i<values.length; i++) {
            PaintSlider slider = new PaintSlider(colorSpace.getChanelColor(i),values[i]);
            int finalI = i;
            slider.setChangeHandler(value -> {
                values[finalI]=value;
                updateMixColor();
            });
            slider.setMainController(mainController);
            paintSlides.getChildren().add(slider);
        }
        updateMixColor();
    }

    private void updateMixColor() {
        Spectrum spectrum = colorSpace.getSpectrumForValues(values);
        //int screencolor = colorSpace.getScreenColorForValues(values);
        int screencolor = spectrum.getArgb();
        mixColor.setStyle("-fx-background-color: rgb("+((screencolor>>16) & 0xff)+","+((screencolor>>8) & 0xff)+","+((screencolor) & 0xff)+")");
        spectrogramChart.displayColorSpectrum(spectrum);
    }

    public byte[] getPaintMix() {
        return values;
    }

    public void updateColorChange() {
        colorSpace.forceColorUpdate();
        updateMixColor();
        for(Node node:paintSlides.getChildren()) {
            if(node instanceof PaintSlider) {
                ((PaintSlider)node).updateColorChange();
            }
        }
    }
}
