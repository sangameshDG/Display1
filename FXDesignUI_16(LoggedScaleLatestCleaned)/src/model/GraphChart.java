package model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class GraphChart implements ILayoutParam{
	
	protected double actualWidth;
	protected double actualHeight;
	protected static double HEIGHT_OFF;
	protected static double WIDTH_OFF;
	
	Canvas mCanvas;
	protected GraphicsContext gc;

	public GraphChart(Canvas canvas) {
		mCanvas = canvas;
    	gc = canvas.getGraphicsContext2D(); 
    	initBounds();
	}
	
	private void initBounds() {
		actualWidth = mCanvas.getWidth();
    	actualHeight = mCanvas.getHeight();
    	HEIGHT_OFF = actualHeight-TEXT_OFFSET;
    	WIDTH_OFF = actualWidth-OFFSET;
	}
	
	@Override
	public void draw(GraphicsContext gc) {
		
	}
	
}
