package model.drawable;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light.Point;
import model.ILayoutParam;
import model.MatrixRef;
import model.OverlayItem;
import utils.Constance;
import utils.DrawingUtils;
import utils.RawColor;

public class Video extends OverlayItem implements Cloneable,ILayoutParam{

	private static final int SHORT_MAX = 65535;

	private static final int COLOR_PALLETE = RawColor.COLOR_ARRAY.length-1;//5;// changed from 10
	
	private int val;
	private double range;//meters
	private double az;//radians
	private double el;//radians
	
	private boolean isAz = false;
	private boolean isEl = false;
	
	public Video() {
		super(null,null,null);
	}
	
	public int getVal() {
		return val;
	}

	public void setVal(int valParam) {
		this.val = (valParam);
		if(val < 0)
			this.val += SHORT_MAX ;
		
		if(val > Constance.VID_MAX_VAL_RX)
			Constance.VID_MAX_VAL_RX = val;
	}
	
	public void degradeVal(double degradeVal) {
		val *= degradeVal;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range =(range);
	}

	public double getAz() {
		return az;
	}

	public void setAzimuth(double az) {
		this.az = az;
	}

	public double getEl() {
		return el;
	}

	public void setElevation(double el) {
		this.el = el;
	}
	
	public void setAz(boolean isAz) {
		this.isAz = isAz;
	}

	public void setEl(boolean isEl) {
		this.isEl = isEl;
	}

	public void evaluateXYZ() {
		MatrixRef matrixRef = MatrixRef.getInstance();
		if(Constance.UNITS.isKM)
		    setX(matrixRef.toRangePixels((range/1000)+Constance.UNITS.TDPzero()));
		else
			setX(matrixRef.toRangePixels((range*0.000536)+Constance.UNITS.TDPzero()));
		
		// For getting Az X and Y Pixel values.
		if(isAz) {
	    	double midAzimuth = (matrixRef.getMinAzimuth()+matrixRef.getMaxAzimuth())/2;
	    	Point start = matrixRef.toAzimuthRangePixels(midAzimuth,matrixRef.getMinRange()+Constance.UNITS.TDPzero() );	
	    	Point point = DrawingUtils.getNextPointAtAngleForRVideo(start.getX(), start.getY(), getX(), -Math.toDegrees(az));
	    	setX(point.getX());
	      if(Constance.UNITS.isKM){
	    	if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))) 
	    	    setY(point.getY()-(Constance.AZIMUTH.RCLO_PX+7)); // 7, 5, 11, 14 ARE ADDED IN ORDER TO MAKE THE RAW VIDEO ALIGNMENT FOR THE PARTICULAR X AND Y OFFSETS.
	    	else
	    		setY(point.getY()+(Constance.AZIMUTH.RCLO_PX+5));
	      }else {
	    	  if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))) 
		    	    setY(point.getY()-(Constance.AZIMUTH.RCLO_PX+8));
		    	else
		    		setY(point.getY()+(Constance.AZIMUTH.RCLO_PX+6));
	       }
		
		}
		// For getting El X and Y Pixel values.
		if(isEl) {
			Point start = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getMinRange()+Constance.UNITS.TDPzero());  
	    	Point point = DrawingUtils.getNextPointAtAngleForRVideo(start.getX(), start.getY(), getX(),-Math.toDegrees(el));
	    	setX(point.getX());
	    	setZ(2*point.getY()-start.getY()); 	
		 }
	 }

	// Drawing RAW video image.
	@Override
	public void draw(GraphicsContext gc) {
		MatrixRef matrixRef = MatrixRef.getInstance();
		
		if(((range > 0) && (range/1000) <= (matrixRef.getVisibleRange()+(Constance.UNITS.isLogged?matrixRef.getStartRange():0)) && Constance.SHOW_RAW) || ((range > 0) && (range*0.000536) <= (matrixRef.getVisibleRange()+(Constance.UNITS.isLogged?matrixRef.getStartRange():0)) && Constance.SHOW_RAW)) {
			double x = getX();
			double y = 0;
			if(isAz) {
				y = getY();
			} else if(isEl) {
				y = getZ();
			}
			
			int colorIndex = (int) Math.ceil((val*COLOR_PALLETE)/Constance.VID_MAX_VAL_RX);
			if(colorIndex > COLOR_PALLETE || colorIndex < 0)
				colorIndex = 0;	
		//	gc.setFill(RawColor.COLOR_ARRAY[COLOR_PALLETE - colorIndex]);
			gc.setFill(RawColor.COLOR_ARRAY[COLOR_PALLETE - (COLOR_PALLETE - colorIndex)]); // For lower strength higher the color Intensity and for higher the strength lower the color intensity.
			double size = matrixRef.getMaxRange()/matrixRef.getVisibleRange();
			gc.fillRect(x, y, size, size);
			gc.setGlobalAlpha(0.5);
		}

	}
	
	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
