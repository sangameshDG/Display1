package views;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ResizableCanvas extends Canvas {

    public ResizableCanvas() {
        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
        setDisable(true);
    }
    
    public void clear() {
    	getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
    }

    public void draw() {
    	double width = getWidth();
        double height = getHeight();
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,width, height);
        gc.setStroke(Color.RED);
        gc.strokeLine(0, 0, width, height);
        gc.strokeLine(0, height, width, 0);
    }
    
    public void drawRect(double x, double y, double w, double h) {
    	clear();
    	getGraphicsContext2D().setStroke(Color.WHITESMOKE);
		getGraphicsContext2D().setLineWidth(2);
		getGraphicsContext2D().strokeRect(x, y, w, h);
    }
    
    public void drawText(String rg,double x, double y) {
    	clear();
    	getGraphicsContext2D().setStroke(Color.GOLD);
		//getGraphicsContext2D().setLineWidth(2);
		getGraphicsContext2D().strokeText(rg,x+10, y+10);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
}