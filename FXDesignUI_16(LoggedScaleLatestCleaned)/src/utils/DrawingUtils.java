package utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light.Point;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class DrawingUtils {
	
	public enum FLIP { L2R, R2L };

	public static void drawLineAtAngle(GraphicsContext gc, double x1,double y1,double length,double angle) {
	    gc.strokeLine(x1,y1,x1 + length * Math.cos(Math.toRadians(angle)),y1 + length * Math.sin(Math.toRadians(angle)));
	}
	
	public static void drawLineAtAngle(Graphics2D g2d, double x1,double y1,double length,double angle) {
	    g2d.drawLine((int)x1,(int)y1,(int)(x1 + length * Math.cos(Math.toRadians(angle))),(int)(y1 + length * Math.sin(Math.toRadians(angle))));
	}
	
	public static Image printMirrorImage(String str, int size, Color color) {
    	BufferedImage bufferedImage = new BufferedImage(70,50, BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g2d = bufferedImage.createGraphics();
    	g2d.setFont(new java.awt.Font("Sans Serif",java.awt.Font.BOLD, size));
    	g2d.setColor(color);
    	g2d.drawString(str,10,10);
    	AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
    	tx.translate(-bufferedImage.getWidth(null), 0);
    	AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    	bufferedImage = op.filter(bufferedImage, null);
    	WritableImage wr = null;
		Image img = SwingFXUtils.toFXImage(bufferedImage, wr);
		return img;
	}
	
	public static void drawDashedLine(Graphics g, double x1, double y1, double x2, double y2) {

        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();

        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        g2d.drawLine((int)x1, (int)y1, (int)x2, (int)y2);

        //gets rid of the copy
        g2d.dispose();
	}
	
	// For setting the x and y pixel of plots and tracks.
	   public static Point getNextPointAtAngle(double x1, double y1, double lenPx, double angle){
		 Point point = new Point();
		 if(Constance.UNITS.isLogged==false){
			point.setX(x1+(lenPx*Math.cos(Math.toRadians(angle)))/Math.abs(Math.cos(Math.toRadians(angle))));
		    point.setY(y1+lenPx*Math.sin(Math.toRadians(angle)));
		 }else{
			point.setY(y1+lenPx*Math.sin(Math.toRadians(angle)));
			/* Here setX is not done because,
			after calculating the pixels according to the formula, its x-axis pixel value becoming greater than the 
			displays maximum pixel value. So that is why the pixels are directly calculated from the radar position itself for log display.*/
		 }
		
		  return point;
	   }
	
	    // Added in order to display the raw video from the radar.
		public static Point getNextPointAtAngleForRVideo(double x1, double y1, double lenPx, double angle){
			Point point = new Point();
			point.setX(x1+(lenPx*Math.cos(Math.toRadians(angle)))/Math.abs(Math.cos(Math.toRadians(angle))));
		    point.setY(y1+lenPx*Math.sin(Math.toRadians(angle)));
			return point;
		}
		
		// Added in order to make the track label in log version to display properly.
		public static Point getNextPointAtAngle2(double x1, double y1, double lenPx, double angle){
			Point point = new Point();
			point.setX(x1);
			point.setY(y1+lenPx*Math.sin(Math.toRadians(angle)));
			return point;
		}
		
	   // Added in order to make the landing strip alignment properly.
	   public static Point getNextPointAtAngle1(double x1, double y1, double lenPx, double angle){
		   Point point = new Point();
		   point.setX(x1+lenPx*Math.cos(Math.toRadians(angle)));
		   point.setY(y1+lenPx*Math.sin(Math.toRadians(angle)));
		   return point;
	    }
	
	  public static Point getRightAngled3Vertex(Point a, Point b, double angle){
		   double cx = a.getX() + Math.tan(angle)*(-1)*(b.getY()-a.getY());
		   double cy = a.getY() + Math.tan(angle)*(b.getX()-a.getX());
		   return new Point(cx, cy, 0, null);
	    }
	
	  public static void runSafe(final Runnable runnable) {
           Objects.requireNonNull(runnable, "runnable");
           if (Platform.isFxApplicationThread()) {
              runnable.run();
           }
           else {
              Platform.runLater(runnable);
           }
    }
	
      public static void flipCanvasDrawing(Canvas canvas, FLIP flip) {
		if(flip.equals(FLIP.R2L)) {
			canvas.getGraphicsContext2D().save();
//			canvas.setTranslateY(0);
			canvas.setScaleX(-1);
			canvas.setScaleY(1);
			canvas.getGraphicsContext2D().restore();
		} else {
			canvas.getGraphicsContext2D().save();
//			canvas.setTranslateY(1);
			canvas.setScaleX(1);
			canvas.setScaleY(1);
			canvas.getGraphicsContext2D().restore();
		}
	}
	
	public static BufferedImage overlapBufferedImage(BufferedImage baseImg,BufferedImage inImg) {
        Graphics2D g2 = baseImg.createGraphics();
        g2.drawImage(baseImg, null, 0, 0);
        g2.drawImage(inImg, null, 0, 0);
        g2.dispose();
        return baseImg;
    }
}
