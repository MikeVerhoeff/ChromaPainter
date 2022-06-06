package nl.tudelft.mikeverhoeff.chromadepth.util;

import javafx.stage.Window;
import nl.tudelft.mikeverhoeff.chromadepth.ui.controller.MainController;

public class MainControllerWindowWrapper extends MainController {

    private Window window;
    private Runnable updateColor;

    public MainControllerWindowWrapper(Window window, Runnable updateColor) {
        this.window=window;
        this.updateColor = updateColor;
    }


    @Override
    public void updateColorChange() {
        updateColor.run();
    }

    @Override
    public Window getWindow() {
        return window;
    }
}
