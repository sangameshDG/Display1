package model.drawable;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light.Point;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.ILayoutParam;
import model.MatrixRef;
import model.OverlayItem;
import utils.Constance;
import utils.DrawingUtils;

public class Plot extends OverlayItem implements Cloneable,ILayoutParam{

	private static final int PLOT_SIZE = 2;
	private static final int PLOT_SIZE1 = 7;  // Corner reflector size.

	private String Title = "";
	private int plotNumber;

	private double elevation;//degress
	private double azimuth;//degress
	private double range;//Meters
	private int Crstate;
	public double fromTDP;
	
	private boolean isAz = false;
	private boolean isEl = false;
	private Color color;

	public Plot() {
		super(null,null,null);
	}

	public void setTitle(String title) {
		this.Title = title;
	}

	public void setPlotNumber(int number) {
		plotNumber = number;
	}

	public String getPlotNumber() {
		return String.valueOf(plotNumber);
	}

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double elevation) {
		isEl = true;
		this.elevation = elevation;
	}

	public double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(double azimuth) {
		isAz = true;
		this.azimuth = azimuth;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range =(range);
	}

	public void setAz(boolean b) {
		isAz = b;
	}

	public void setEl(boolean b) {
		isEl = b;
	}
	
	public int getCrstate() {
		return Crstate;
	}

	public void setCrstate(int crstate) {
		Crstate = crstate;
	}


	public void extractGraphAER() {
		
		  double x = 0;
		  double y = range*Math.cos(Math.toRadians(azimuth));
		  double z = range*Math.cos(Math.toRadians(elevation));
		  y = y - (Constance.TOUCH_DOWN*1000 * Math.cos(Math.toRadians(Constance.AZIMUTH_MAX)) - (Constance.AZIMUTH.RCLO*1000*Math.sin(Math.toRadians(Constance.AZIMUTH_MAX))));
		  z = z - (Constance.TOUCH_DOWN*1000);
		
		if(isAz) {
			
			x = range*Math.sin(Math.toRadians(azimuth));
			x = x - (Constance.TOUCH_DOWN*1000 * Math.sin(Math.toRadians(Constance.AZIMUTH_MAX)) + (Constance.AZIMUTH.RCLO*1000*Math.cos(Math.toRadians(Constance.AZIMUTH_MAX))));
			range = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
			azimuth = (Math.toDegrees(Math.atan2(x,y)));
	        if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")) ) {
			  azimuth = Constance.AZIMUTH_MAX-azimuth;//Changing from 0->20degrees to +10->-10degrees
		    } else {
			  azimuth = Constance.AZIMUTH_MIN+azimuth;//Changing from 0->20degrees to -10->+10degrees
		    }
	        if(Constance.UNITS.isLogged==true){
			     if( azimuth< -90 || azimuth > 90 )
			    	  range=-range;
	        }else{
	        	if(x<0 && y<0 )
	        		range=-range;
	        }
		}
		if(isEl) {
			
			x = range*Math.sin(Math.toRadians(elevation));	
			range = Math.sqrt(Math.pow(x, 2)+Math.pow(z, 2));
			if(z<0){
				range=-range;
			 }
		}

		MatrixRef matrixRef = MatrixRef.getInstance();
		//Range
		if(Constance.UNITS.isKM==true){
			
			if(Constance.UNITS.isLogged==true){
			   setX(matrixRef.toRangePixels((range/1000)));
			   fromTDP=(matrixRef.toRangePixelsFromTDP(((range/1000))));
			}
		    else{
		    	 setX(matrixRef.toRangePixels(Math.abs(range/1000)));
		    }
		
		}
		else{
			
			if(Constance.UNITS.isLogged==true){
				 setX(matrixRef.toRangePixels((range*Constance.UNITS.METER_TO_NM)));
				 fromTDP=(matrixRef.toRangePixelsFromTDP(((range*Constance.UNITS.METER_TO_NM))));
				}
			else{
			   setX(matrixRef.toRangePixels(Math.abs(range*Constance.UNITS.METER_TO_NM)));
			}
		
		}

		//Azimuth. Here we will set the Az X and Y pixels
		if(isAz){
			if(Constance.UNITS.isLogged){
			double midAzimuth = (matrixRef.getMinAzimuth()+matrixRef.getMaxAzimuth())/2;
	        Point start = matrixRef.toAzimuthRangePixels(midAzimuth, matrixRef.getMinRange());
			Point p = DrawingUtils.getNextPointAtAngle(start.getX(), start.getY(), fromTDP, -azimuth);
			setY(p.getY());
			}
			else {
				double midAzimuth = (matrixRef.getMinAzimuth()+matrixRef.getMaxAzimuth())/2;
		        Point start = matrixRef.toAzimuthRangePixels(midAzimuth, matrixRef.getStartRange());
				Point p = DrawingUtils.getNextPointAtAngle(start.getX(), start.getY(), getX(), -azimuth);
				setX(p.getX());
				setY(p.getY());
			}
		}
		
		//Elevation. Here we will set the El X and Y pixels
		if(isEl && x >=0){
			if(Constance.UNITS.isLogged){
			elevation = (Math.toDegrees(Math.atan2(x,z)));
				elevation = Constance.ELEVATION_MIN+elevation;//Changing from 0->20degrees to 0->+10degrees
	        Point start = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getMinRange());
			Point p = DrawingUtils.getNextPointAtAngle(start.getX(), start.getY(), fromTDP, -(elevation));
			setZ(2*p.getY()-start.getY());	// This is added in order to double the y pixel in el.
			}
			else{
			elevation = (Math.toDegrees(Math.atan2(x,z)));
				elevation = Constance.ELEVATION_MIN+elevation;//Changing from 0->20degrees to 0->+10degrees
	        Point start = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getStartRange());
			Point p = DrawingUtils.getNextPointAtAngle(start.getX(), start.getY(), getX(), -(elevation));
			setX(p.getX());
			setZ(2*p.getY()-start.getY());  // This is added in order to double the y pixel in el.
			}
		  }
	   }
		
	@Override
	public void draw(GraphicsContext gc) {
		MatrixRef matrixRef = MatrixRef.getInstance();
	//if(Constance.UNITS.isKM==true){
		if(((range/1000) <= (matrixRef.getVisibleRange()) && Constance.SHOW_PLOT)||((range*Constance.UNITS.METER_TO_NM) <= (matrixRef.getVisibleRange()) && Constance.SHOW_PLOT)) {
			double x=getX();
			double y = 0;
			color = null;
			if(isAz) {
				y = getY();
				if(azimuth <= (-Constance.CorrectedAzMax) || azimuth >=(Constance.CorrectedAzMax) ) // This condition is Added inorder to display plots before TDP.
					color = Color.TRANSPARENT;
			} else if(isEl) {
				y = getZ();
				if(elevation > Constance.CorrectedElMax  || elevation <0 ) 
					color = Color.TRANSPARENT;
			  }

			if(y > OFFSET) {
				if(color!=null)
		    		gc.setFill(color);
				else if(getCrstate()==1){
					gc.setFill(Color.CORNFLOWERBLUE);
				    gc.fillOval(x-PLOT_SIZE1, y-PLOT_SIZE1, PLOT_SIZE1, PLOT_SIZE1);
				}
		    	else{
		    		gc.setFill(Color.RED);
		    	    gc.fillOval(x-PLOT_SIZE, y-PLOT_SIZE, PLOT_SIZE, PLOT_SIZE);
		    	}
		    	if(color!=null)
		    		gc.setStroke(color);
		    	else
		    		gc.setStroke(Color.WHITE);
		    	gc.setLineWidth(0.5);
		    	gc.strokeOval(x-PLOT_SIZE, y-PLOT_SIZE, PLOT_SIZE, PLOT_SIZE);

		    	if(Constance.SHOW_PLOT_LABEL) {
		    		gc.setStroke(Color.CHOCOLATE);
		        	DrawingUtils.drawLineAtAngle(gc, x, y, 2*PLOT_SIZE, -45);
		        	Point p = DrawingUtils.getNextPointAtAngle(x, y, 2*PLOT_SIZE, -45);
		        	gc.strokeLine(p.getX(), p.getY(), p.getX()+6*PLOT_SIZE, p.getY());
		        	gc.setFont(new Font("Arial", 2*PLOT_SIZE));
		        	gc.setStroke(Color.WHITE);
		        	gc.strokeText(Title, p.getX()+PLOT_SIZE, p.getY()-PLOT_SIZE);
		        	gc.setStroke(Color.YELLOW);
		        	gc.strokeText(getPlotNumber(), p.getX()+PLOT_SIZE, p.getY()+2*PLOT_SIZE);
		    	}
			}
		}
	}
	
	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	@Override
	public String toString() {
		return "onPlot : "+"\n"
				+"Range: "+range+"\n"
				+"Az: "+ azimuth+"\n"
				+"El: "+ elevation;
	}	
}
