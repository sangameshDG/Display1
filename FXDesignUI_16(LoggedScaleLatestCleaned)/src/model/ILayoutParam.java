package model;

import javafx.scene.canvas.GraphicsContext;

public interface ILayoutParam {
	
	int OFFSET 			= 10;//px
	int HGAP 			= 2*OFFSET;
	int TEXT_OFFSET 	= 3*OFFSET;
	
	
	public void draw(GraphicsContext gc);

}
