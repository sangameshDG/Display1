package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light.Point;

import org.apache.log4j.Logger;

import utils.Constance;

public class MatrixRef implements ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(MatrixRef.class);
	private double actualXdimen;
	private double drawableXArea;
	
	private double ELactualYdimen;	
	private double ELdrawableYArea;
	
	private double AZactualYdimen;
	private double AZdrawableYArea;
	
	private double maxElevation;
	private double minElevation;
	
	private double maxRange;
	private double minRange;
	private double visibleRange;
	
	private double maxAzimuth;
	private double minAzimuth;
	
	private double startRange1;
	private double startRange;
	
	private static MatrixRef instance = null;
	
	protected MatrixRef() {
		// Exists only to defeat instantiation.
	}
	
	public static MatrixRef getInstance() {
	      if(instance == null) {
	         instance = new MatrixRef();
	         logger.info("Matrix Ref Instantiated");
	      }
	      return instance;
	}
	
	public void setActualAzimuthXY(double acutalX, double actualY) {
		actualXdimen = acutalX;
		AZactualYdimen = actualY;
		
		drawableXArea = actualXdimen - OFFSET;
		AZdrawableYArea = AZactualYdimen;
	}
	
	public void setActualElevationXY(double acutalX, double actualY) {
		actualXdimen = acutalX;
		ELactualYdimen = actualY;
		
		drawableXArea = actualXdimen - OFFSET;
		ELdrawableYArea = ELactualYdimen - TEXT_OFFSET;
	}
	
	public void setElevationVal(double max, double min) {
		maxElevation = max;
		minElevation = min;
	}
	
	public void setAzimuthVal(double max, double min) {
		maxAzimuth = max;
		minAzimuth = min;
	}
	
	public void setRangeVal(double max, double min) {
		maxRange = max;
		minRange = min;
	}
	
	public void setStartRange(double td) {
		this.startRange =(td);
		
	}
	
	public void setStartRange1(double min) {
		startRange1 = min;
	}
	
	// To get Pixels for the given Range.
	public double toRangePixels(double r) {
		if(Constance.UNITS.isLogged==true)
		    return ((((10*Math.log10((r+Constance.UNITS.LOG_OPT_VALUE+getStartRange())))- 10*Math.log10((Constance.UNITS.LOG_OPT_VALUE)))*drawableXArea)/((10*Math.log10((visibleRange+Constance.UNITS.LOG_OPT_VALUE+getStartRange())))-10*Math.log10(Constance.UNITS.LOG_OPT_VALUE)));
		else
			return ((r*drawableXArea)/(visibleRange));
	}
	
	
	// To get Pixels from the TDP for a given range.This is using only in Log scale in order to correct y axis. 
	public double toRangePixelsFromTDP(double r) {
		if(r >= 0)
		  return ((((10*Math.log10(((r)+Constance.UNITS.LOG_OPT_VALUE+getStartRange())))- 10*Math.log10((Constance.UNITS.LOG_OPT_VALUE)))*drawableXArea)/((10*Math.log10((visibleRange+Constance.UNITS.LOG_OPT_VALUE+getStartRange())))-10*Math.log10(Constance.UNITS.LOG_OPT_VALUE)) - (((10*Math.log10((Constance.UNITS.LOG_OPT_VALUE+getStartRange())))- 10*Math.log10((Constance.UNITS.LOG_OPT_VALUE)))*drawableXArea)/((10*Math.log10((visibleRange+Constance.UNITS.LOG_OPT_VALUE+getStartRange())))-10*Math.log10(Constance.UNITS.LOG_OPT_VALUE)));
		else
		 return ((((10*Math.log10((Constance.UNITS.LOG_OPT_VALUE+getStartRange())))- 10*Math.log10((Constance.UNITS.LOG_OPT_VALUE)))*drawableXArea)/((10*Math.log10((visibleRange+Constance.UNITS.LOG_OPT_VALUE+getStartRange())))-10*Math.log10(Constance.UNITS.LOG_OPT_VALUE)) -  (((10*Math.log10((r+Constance.UNITS.LOG_OPT_VALUE+getStartRange())))- 10*Math.log10((Constance.UNITS.LOG_OPT_VALUE)))*drawableXArea)/((10*Math.log10((visibleRange+Constance.UNITS.LOG_OPT_VALUE+getStartRange())))-10*Math.log10(Constance.UNITS.LOG_OPT_VALUE)) );
	 }
	
	// added for Raw video and Range scale purpose
	public double toRangePixels2(double r) {
		if(Constance.UNITS.isLogged==true)
			return ((((10*Math.log10((r+Constance.UNITS.LOG_OPT_VALUE)))- 10*Math.log10((Constance.UNITS.LOG_OPT_VALUE)))*drawableXArea)/((10*Math.log10((visibleRange+Constance.UNITS.LOG_OPT_VALUE)))-10*Math.log10(Constance.UNITS.LOG_OPT_VALUE)));
		else
		    return ((r*drawableXArea)/(visibleRange));
		}
	
	//Return range value in METERS
	public double fromRangePixels(double px) {
		if(Constance.UNITS.isLogged==true)
		   return ((px*((Math.log10(Constance.UNITS.LOG_OPT_VALUE+visibleRange+getStartRange()))-Math.log10((Constance.UNITS.LOG_OPT_VALUE))))/drawableXArea);
		else
			return (((px*visibleRange*1000)/drawableXArea));
	}
	
	public Point toElevationRangePixels(double el, double r) {
		Point p = new Point();
		double x = toRangePixels(r);
		double y = toElevationPixels(el);			
		p.setX(x);
		p.setY(y);
		return p;
	}
	
	public Point toElevationRangePixels2(double el, double r) {
		Point p = new Point();
		double x = toRangePixels2(r);
		double y = toElevationPixels(el);			
		p.setX(x);
		p.setY(y);
		return p;
	}
	
	
	public double toElevationPixels(double el) {
		return  ELdrawableYArea - ((el*ELdrawableYArea)/maxElevation);
	}
	
	public Point toAzimuthRangePixels(double az, double r) {
		Point p = new Point();
		double x = toRangePixels(r);
		double y = toAzimuthPixels(az);
		p.setX(x);
		p.setY(y);
		return p;
	}
	
	public Point toAzimuthRangePixels2(double az, double r) {
		Point p = new Point();
		double x = toRangePixels2(r);
		double y = toAzimuthPixels(az);
		p.setX(x);
		p.setY(y);
		return p;
	}
	
	
	public double toAzimuthPixels(double az) {
		double y;
		double addtionalYarea = AZdrawableYArea ;
		if((az > 0) && (az < maxAzimuth))
			y = addtionalYarea/2 - ((az*(addtionalYarea/2))/maxAzimuth);
		else if((minAzimuth <= az) && (az < 0))
			y = addtionalYarea/2 + ((az*(addtionalYarea/2))/minAzimuth);
		else if(az==maxAzimuth)
			y = 0;
		else if(az==minAzimuth)
			y = addtionalYarea;
		else
			y = addtionalYarea/2;
		return y;
	}
	
	public double fromAzimuthPixels(double px, double py) {
		double midAzimuth = (getMinAzimuth()+getMaxAzimuth())/2;
		double midAzimuthOffset = toRangePixels(Constance.AZIMUTH.RCLO/Constance.UNITS.getFACTOR_LENGTH());
        Point start = toAzimuthRangePixels(midAzimuth, getMinRange());
        double len = Math.abs(px-start.getX());
        double yAngle =Math.toDegrees( Math.asin(((py-start.getY()-midAzimuthOffset)/len)));
		return yAngle;
	}
	
	// For Co-ordinate points angle.
	public double fromAzimuthPixelsForCO(double px, double py) {     
		double midAzimuth = (getMinAzimuth()+getMaxAzimuth())/2;
		double midAzimuthOffset = toRangePixels(0+(Constance.UNITS.isLogged?(-getStartRange()):0));
        Point start = toAzimuthRangePixels(midAzimuth, getMinRange()+(Constance.UNITS.isLogged?(-getStartRange()):0));
        double len = Math.abs(px-start.getX());
        double yAngle = Math.toDegrees(Math.asin(((py-start.getY()-midAzimuthOffset)/len)));
		return yAngle;
	}
	
	// For converting pixels into Angle.
	public double fromElevationPixels(double px, double py) {
        Point start = toElevationRangePixels(getMinElevation(), getMinRange()+(Constance.UNITS.isLogged?(-getStartRange()):0));
        double len = Math.abs(px-start.getX());
        double yAngle =Math.toDegrees( Math.asin((py-start.getY())/len));
		return yAngle;
		
	}
	
	@Override
	public void draw(GraphicsContext gc) {
		
	}

	public double getActualXdimen() {
		return actualXdimen;
	}

	public double getELActualYdimen() {
		return ELactualYdimen;
	}

	public void setELActualYdimen(double actualYdimen) {
		this.ELactualYdimen = actualYdimen;
	}

	public double getDrawableXArea() {
		return drawableXArea;
	}

	public double getELDrawableYArea() {
		return ELdrawableYArea;
	}

	public void setELDrawableYArea(double drawableYArea) {
		this.ELdrawableYArea = drawableYArea;
	}

	public double getAZActualYdimen() {
		return AZactualYdimen;
	}

	public void setAZActualYdimen(double actualYdimen) {
		this.AZactualYdimen = actualYdimen;
	}

	public double getAZDrawableYArea() {
		return AZdrawableYArea;
	}

	public void setAZDrawableYArea(double drawableYArea) {
		this.AZdrawableYArea = drawableYArea;
	}
	
	public double getMaxElevation() {
		return maxElevation;
	}

	public void setMaxElevation(double maxElevation) {
		this.maxElevation = maxElevation;
	}

	public double getMinElevation() {
		return minElevation;
	}

	public void setMinElevation(double minElevation) {
		this.minElevation = minElevation;
	}

	public double getMaxRange() {
		return maxRange*Constance.UNITS.getFACTOR_LENGTH();
	}

	public void setMaxRange(double maxRange) {
		this.maxRange = maxRange;
	}

	public double getMinRange() {
		return minRange*Constance.UNITS.getFACTOR_LENGTH();
	}

	public void setMinRange(double minRange) {
		this.minRange = minRange;
	}
	
	public double getVisibleRange() {
		return visibleRange;
	}

	public void setVisibleRange(double visibleRange) {
		this.visibleRange =(visibleRange);
		
	}

	public double getMaxAzimuth() {
		return maxAzimuth;
	}

	public void setMaxAzimuth(double maxAzimuth) {
		this.maxAzimuth = maxAzimuth;
	}

	public double getMinAzimuth() {
		return minAzimuth;
	}

	public void setMinAzimuth(double minAzimuth) {
		this.minAzimuth = minAzimuth;
	}

	public double getStartRange1() {
		return startRange1*Constance.UNITS.getFACTOR_LENGTH();
	}

	public double getStartRange() {
		return startRange;
	}
}
