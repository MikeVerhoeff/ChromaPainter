package nl.tudelft.mikeverhoeff.chromadepth.ui.controller;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import nl.tudelft.mikeverhoeff.chromadepth.Painting;
import nl.tudelft.mikeverhoeff.chromadepth.painttools.PaintTool;

public class Canvas extends StackPane {

    private ImageView imageView;
    private Painting painting;
    private PaintTool tool;

    public Canvas() {
        imageView = new ImageView();
        imageView.addEventFilter(MouseEvent.MOUSE_CLICKED,
                (MouseEvent event) -> {
            System.out.println("Mouse Clicked");
            System.out.println("X: "+event.getX()+", Y:"+event.getY());
            if(tool != null && painting != null) {
                tool.paint(painting, (int)event.getX(), (int)event.getY());
            } else {
                System.out.println(tool);
                System.out.println(painting);
            }
            imageView.setImage(painting.getImage());
            event.consume();
        });
        imageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseEvent);
        this.getChildren().add(imageView);
    }

    private void handleMouseEvent(MouseEvent event) {
        System.out.println("DragEvent");
        if(tool != null && painting != null) {
            tool.paint(painting, (int)event.getX(), (int)event.getY());
        } else {
            System.out.println(tool);
            System.out.println(painting);
        }
        imageView.setImage(painting.getImage());
        event.consume();
    }

    public void setPainting(Painting painting) {
        this.painting = painting;
        setImage(painting.getImage());
    }

    public void setPaintTool(PaintTool tool) {
        this.tool = tool;
    }

    private void setImage(Image image) {
        imageView.setImage(image);
    }

    public void updateColorChange() {
        painting.calculateRGB();
    }

    public Painting getPainting() {
        return painting;
    }
}
