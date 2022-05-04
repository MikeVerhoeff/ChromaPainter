package nl.tudelft.mikeverhoeff.chromadepth.colorspace;

import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrogramChart;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.Spectrum;
import nl.tudelft.mikeverhoeff.chromadepth.spectra.SpectrumIO;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.SelectSpectrumController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ColorSpaceTesterUI implements Initializable {

    @FXML
    private VBox primariesBox;

    @FXML
    private Pane referenceColorSpace;

    @FXML
    private StackPane referenceSpectrumSpace;

    @FXML
    private Pane resultcolorSpace;

    @FXML
    private StackPane resultspectrumSpace;

    @FXML
    private Slider sliderC;

    @FXML
    private Slider sliderM;

    @FXML
    private Slider sliderY;

    @FXML
    private Slider sliderK;

    @FXML
    private Spinner<Double> spinnerC;

    @FXML
    private Spinner<Double> spinnerM;

    @FXML
    private Spinner<Double> spinnerY;

    @FXML
    private Spinner<Double> spinnerK;

    @FXML
    void selectReferenceAction(ActionEvent event) {
        SelectSpectrumController.display(primariesBox.getScene().getWindow(), (spectrum -> {
            setPaneColor(referenceColorSpace, spectrum.getArgb());
            referenceSpectrum.displayColorSpectrum(spectrum);
        }));
    }
    private SpectrogramChart resultSpectrum;
    private SpectrogramChart referenceSpectrum;

    private CMYKColorSpace colorSpace;

    private void setPaneColor(Pane pane, int screencolor) {
        pane.setStyle("-fx-background-color: rgb("+((screencolor>>16) & 0xff)+","+((screencolor>>8) & 0xff)+","+((screencolor) & 0xff)+")");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultSpectrum = new SpectrogramChart();
        resultspectrumSpace.getChildren().add(resultSpectrum);

        referenceSpectrum = new SpectrogramChart();
        referenceSpectrumSpace.getChildren().add(referenceSpectrum);

        colorSpace = new CMYKColorSpace();
        try {
            colorSpace.setBackgroundColor(SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\50_m1.txt")).get(0));
            colorSpace.setCyan(SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\06_m1.txt")).get(0));
            colorSpace.setYellow(SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\01_m1.txt")).get(0));
            colorSpace.setMagenta(SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\05_m1.txt")).get(0));
            colorSpace.setKey(SpectrumIO.loadCGATS17Spectrum(new File("C:\\Users\\Mike\\Pictures\\ChromaPaint\\Spectra\\46_m1.txt")).get(0));
            resultSpectrum.setMaxValue(colorSpace.getMaxIntensity());
            referenceSpectrum.setMaxValue(colorSpace.getMaxIntensity());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        SpectrogramChart wchart = new SpectrogramChart();
        wchart.displayColorSpectrum(colorSpace.getBackgroundColor());
        Pane wpane = new Pane();
        setPaneColor(wpane, colorSpace.getBackgroundColor().getArgb());
        wpane.setPrefHeight(200);
        wpane.setPrefWidth(30);
        HBox wbox = new HBox(wchart, wpane);
        primariesBox.getChildren().add(wbox);


        SpectrogramChart cchart = new SpectrogramChart();
        cchart.displayColorSpectrum(colorSpace.getCyan());
        Pane cpane = new Pane();
        setPaneColor(cpane, colorSpace.getCyan().getArgb());
        cpane.setPrefHeight(200);
        cpane.setPrefWidth(30);
        HBox cbox = new HBox(cchart, cpane);
        primariesBox.getChildren().add(cbox);


        SpectrogramChart mchart = new SpectrogramChart();
        mchart.displayColorSpectrum(colorSpace.getMagenta());
        Pane mpane = new Pane();
        setPaneColor(mpane, colorSpace.getMagenta().getArgb());
        mpane.setPrefHeight(200);
        mpane.setPrefWidth(30);
        HBox mbox = new HBox(mchart, mpane);
        primariesBox.getChildren().add(mbox);


        SpectrogramChart ychart = new SpectrogramChart();
        ychart.displayColorSpectrum(colorSpace.getYellow());
        Pane ypane = new Pane();
        setPaneColor(ypane, colorSpace.getYellow().getArgb());
        ypane.setPrefHeight(200);
        ypane.setPrefWidth(30);
        HBox ybox = new HBox(ychart, ypane);
        primariesBox.getChildren().add(ybox);

        SpectrogramChart kchart = new SpectrogramChart();
        kchart.displayColorSpectrum(colorSpace.getKey());
        Pane kpane = new Pane();
        setPaneColor(kpane, colorSpace.getKey().getArgb());
        kpane.setPrefHeight(200);
        kpane.setPrefWidth(30);
        HBox kbox = new HBox(kchart, kpane);
        primariesBox.getChildren().add(kbox);


        spinnerC.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 255));
        spinnerM.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 255));
        spinnerY.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 255));
        spinnerK.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 255));

        sliderC.valueProperty().addListener(this::onSliderChange);
        sliderM.valueProperty().addListener(this::onSliderChange);
        sliderY.valueProperty().addListener(this::onSliderChange);
        sliderK.valueProperty().addListener(this::onSliderChange);

        onSliderChange(null);
    }

    private void onSliderChange(Observable observable) {
        byte[] values = new byte[4];
        values[0] = (byte)(int)sliderC.getValue();
        values[1] = (byte)(int)sliderM.getValue();
        values[2] = (byte)(int)sliderY.getValue();
        values[3] = (byte)(int)sliderK.getValue();
        spinnerC.getValueFactory().setValue(sliderC.getValue());
        spinnerM.getValueFactory().setValue(sliderM.getValue());
        spinnerY.getValueFactory().setValue(sliderY.getValue());
        spinnerK.getValueFactory().setValue(sliderK.getValue());

        Spectrum result = colorSpace.getSpectrumForValues(values);

        resultSpectrum.displayColorSpectrum(result);
        setPaneColor(resultcolorSpace, result.getArgb());
    }
}