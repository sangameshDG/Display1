package model.drawable;

import java.text.DecimalFormat;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light.Point;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.AppConfig;
import model.DataManager;
import model.ILayoutParam;
import model.MatrixRef;
import model.OverlayItem;
import org.apache.log4j.Logger;
import utils.AppUtils;
import utils.Constance;
import utils.DrawingUtils;

public class Track extends OverlayItem implements Cloneable,ILayoutParam{

	public static final Logger logger = Logger.getLogger(Track.class);

	public static final int REGION_WIDTH = 0; //15
	public static final int RECT_WIDTH = 70; //90 
	public static final int RECT_HEIGHT = 60; //85
	public static final int RECT_HEIGHT1 = 60; //100
	public static final int TRACK_SIZE = 3;
	private static final int LABEL_SIZE = 5;
	public static final int TRACK_SIZE1 = 5;
	public static final int LBEL_REMOVE_WHITE =0;

	private int trackNumber;
	private double elevation;//radians
	private double elevationRadar;
	private double azimuth;//radians
	private double azimuthRadar;//radians
	private double range;//meters
	private short state;
	private int corrName;
	private int desentRate;
	private double actualHeight;
	private double offLoVd;
	private int xVel;
	private int yVel;
	private int speed;

	private boolean isEval = false;
	private boolean isEl = false;
	private boolean isAz = false;
	private boolean showLabel = false;
	
	String offLoVd1;
	String acutualHeight1;
    String Rdescent1;
    
	private Point trackXY;
	private Point labelXY;
	private Color color;

	DecimalFormat df = new DecimalFormat("0.0");

	public Track() {
		super(null,null,null);
		trackXY = new Point();
	}

	public Track(Track t) {
		this();
		trackNumber = t.getTrackNumber();
		elevation = t.getElevation();
		azimuth = t.getAzimuth();
		range = t.getRange();
		trackXY = t.getPoint();
		state=t.getState();
		corrName=t.getCorrName();

		isEval = t.isEvaluated();
		isEl = t.isEl();
		isAz = t.isAz();
	}

	public void setTrackNumber(int number) {
		trackNumber = number;
	}

	public int getTrackNumber() {
		
		return trackNumber;
	}

	public String getTrackNumberString() {
		
		return String.valueOf(trackNumber);
	}

	public double getElevation() {
		return elevation;
	}

	public double getAzimuth() {
		return azimuth;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		
		this.range =(range);
	}

	public int getxVel() {
		return xVel;
	}

	public void setxVel(int xVel) {
		this.xVel = xVel;
	}

	public int getyVel() {
		return yVel;
	}

	public void setyVel(int yVel) {
		this.yVel = yVel;
	}

	public boolean isAz() {
		return isAz;
	}

	public boolean isEl() {
		return isEl;
	}

	public boolean isEvaluated() {
		return isEval;
	}

	public void setEvaluated(boolean b) {
		isEval = b;
	}

	public void setAz(boolean b) {
		isAz = b;
	}

	public void setEl(boolean b) {
		isEl = b;
	}

	public Point getPoint() {
		return trackXY;
	}

	public Point getLablePoint() {
		return labelXY;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setPoint(Point point) {
		this.trackXY = point;
	}

	public void setLabelPoint(Point point) {
		this.labelXY = point;
	}

	public boolean isShowLabel() {
		return showLabel;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	public void setSpeed(double s) {
		speed = (int) s;
	}
	
	public short getState() {
		return state;
	}

	public void setState(short state) {
		this.state = state;
	}

	public int getCorrName() {
		return corrName;
	}

	public void setCorrName(int corrName) {
		this.corrName = corrName;
	}
	
	public int getDesentRate() {
		return desentRate;
	}

	public void setDesentRate(int desentRate) {
		this.desentRate = desentRate;
	}
	
	public void updateElcommonIDListWithTracks(
			List<Integer> elLatestTrackNoList) {
	    Constance.commonIDlist.clear();
	    Constance.commonIDlist=elLatestTrackNoList;	
	}

	 public void extractGraphAER() {
		 
		 double x1=getX();
		 double y1=getY();
		 double z1=getZ();
		 
		double x = getX();
		if(isAz) {
			x = x - (Constance.TOUCH_DOWN*1000 * Math.sin(Math.toRadians(Constance.AZIMUTH_MAX)) + (Constance.AZIMUTH.RCLO*1000*Math.cos(Math.toRadians(Constance.AZIMUTH_MAX))));
		}
		double y = getY() - (Constance.TOUCH_DOWN*1000 * Math.cos(Math.toRadians(Constance.AZIMUTH_MAX)) - (Constance.AZIMUTH.RCLO*1000*Math.sin(Math.toRadians(Constance.AZIMUTH_MAX))));
		double z = getZ() - (Constance.TOUCH_DOWN*1000);
		
		MatrixRef matrixRef = MatrixRef.getInstance();
		//Azimuth
        if(isAz) {
		
		   range = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
		   if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))){ 
			   azimuth = Math.toRadians(Constance.AZIMUTH_MAX) - Math.atan2(x,y);   
			   azimuthRadar=Math.toRadians(Constance.AZIMUTH_MAX) - Math.atan2(x1,y1); 
		   }                                                                                           //Changing from 0->20degrees to +10->-10degrees	          
		   else {
		       azimuth = Math.toRadians(Constance.AZIMUTH_MIN) + Math.atan2(x,y);
		       azimuthRadar=Math.toRadians(Constance.AZIMUTH_MAX) - Math.atan2(x1,y1); 
		   }                                                                                             //Changing from 0->20degrees to -10->+10degrees
			          
		   if(Constance.UNITS.isLogged){
		      if(Math.toDegrees(azimuth) < -90 || Math.toDegrees(azimuth)> 90)
		          range=-range;
		      }
		   else{
			  if(x<0 && y<0 )
				 range=-range;
		    }
		   
				if(Constance.UNITS.isLogged){
					double fromTDP;
				    if(Constance.UNITS.isKM){
					   setX(matrixRef.toRangePixels(((range/1000))));
					   fromTDP = (matrixRef.toRangePixelsFromTDP(((range/1000))));
				    }else{
				       setX(matrixRef.toRangePixels(((range*Constance.UNITS.METER_TO_NM))));
					   fromTDP=(matrixRef.toRangePixelsFromTDP(((range*Constance.UNITS.METER_TO_NM))));
				    }
					double midAzimuth = (matrixRef.getMinAzimuth()+matrixRef.getMaxAzimuth())/2;
			        Point start = matrixRef.toAzimuthRangePixels(midAzimuth, matrixRef.getMinRange());
					Point p = DrawingUtils.getNextPointAtAngle(start.getX(), start.getY(), fromTDP, (Math.toDegrees(-azimuth)));
					setY(p.getY());	
				  }
				else{
					if(Constance.UNITS.isKM)
					    setX(matrixRef.toRangePixels((Math.abs((range/1000)))));
					else
						setX(matrixRef.toRangePixels((Math.abs((range*Constance.UNITS.METER_TO_NM)))));
					double midAzimuth = (matrixRef.getMinAzimuth()+matrixRef.getMaxAzimuth())/2;
				    Point start = matrixRef.toAzimuthRangePixels(midAzimuth, matrixRef.getStartRange());
					Point p = DrawingUtils.getNextPointAtAngle(start.getX(), start.getY(), getX(), (Math.toDegrees(-azimuth)));
					setY(p.getY());
					setX(p.getX());
				  }  
			       offLoVd = (range*Math.tan(azimuth))*Constance.UNITS.getFACTOR_HEIGHTFIXED();//LO
		         //offLoVd = (range+Constance.TOUCH_DOWN*1000)*Math.tan(azimuthRadar)*Constance.UNITS.getFACTOR_HEIGHTFIXED();//LO   in order to show the offset always in Feets.
			       speed=(int) (speed*(Constance.UNITS.isKM?3.5:1.984));  // 3.5 is multiplied in order to convert the speed from m/s to Kmph.Similarly 1.984 is multiplied in order to convert the speed from m/s to knots.
			       offLoVd1=""+(int)offLoVd+(offLoVd<0?" ft R":" ft L");
			       acutualHeight1=" ";
			       Rdescent1=" ";
		  }
  //Elevation
  if(isEl && x >=0 ) {
	  range = Math.sqrt(Math.pow(x, 2)+Math.pow(z, 2));
		if(z<0)
			range=-range;
		
			if(Constance.UNITS.isLogged){
				double fromTDP;
				if(Constance.UNITS.isKM){	
				   setX(matrixRef.toRangePixels((range/1000)));
				   fromTDP=(matrixRef.toRangePixelsFromTDP(((range/1000))));
				}else{
				   setX(matrixRef.toRangePixels((range*Constance.UNITS.METER_TO_NM)));
				   fromTDP=(matrixRef.toRangePixelsFromTDP(((range*Constance.UNITS.METER_TO_NM))));
				}
				elevation = Math.toRadians(Constance.ELEVATION_MIN) + Math.atan2(x,z);
				elevationRadar=Math.toRadians(Constance.ELEVATION_MIN) + Math.atan2(x1,z1); //added in order to find the angle wrt radar.
		        Point start = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getMinRange());
				Point p = DrawingUtils.getNextPointAtAngle(start.getX(), start.getY(), fromTDP, (Math.toDegrees(-elevation)));
				setZ(2*p.getY()-start.getY());
				
			}
			else{
				if(Constance.UNITS.isKM)
				   setX(matrixRef.toRangePixels(Math.abs((range/1000))));
				else
				   setX(matrixRef.toRangePixels(Math.abs((range*Constance.UNITS.METER_TO_NM))));
				elevation = Math.toRadians(Constance.ELEVATION_MIN) + Math.atan2(x,z);
				elevationRadar=Math.toRadians(Constance.ELEVATION_MIN) + Math.atan2(x1,z1); //added in order to find the angle wrt radar.
			    Point start = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getStartRange());
				Point p = DrawingUtils.getNextPointAtAngle(start.getX(), start.getY(),getX(), (Math.toDegrees(-elevation)));
				setX(p.getX());
				setZ(2*p.getY()-start.getY());
			}
			
			    double aglForVD=Math.toDegrees(elevation);
			    //double aglForVD=Math.toDegrees(elevationRadar);
			    double glideHeight = range*Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))*Constance.UNITS.getFACTOR_HEIGHTFIXED();
			    //actualHeight = range*Math.tan(elevation);
			    actualHeight = (range+Constance.TOUCH_DOWN*1000)*Math.tan(elevationRadar)*Constance.UNITS.getFACTOR_HEIGHTFIXED();
			    offLoVd = Math.abs(actualHeight-glideHeight);//VD
			    speed=(int) (speed*(Constance.UNITS.isKM?3.5:1.984));
			    if(Constance.isQNH==true){
		        offLoVd=offLoVd+(Integer.parseInt(Constance.GENERAL_MSL)*Constance.UNITS.getFACTOR_HEIGHTFIXED());
		        actualHeight=actualHeight+(Integer.parseInt(Constance.GENERAL_MSL)*Constance.UNITS.getFACTOR_HEIGHTFIXED());
			    }
			    offLoVd1=""+(int)offLoVd+(aglForVD<Constance.ELEVATION.GLIDE_ANGLE?" ft D":" ft U");  //In order to display the height always in feet	
			    acutualHeight1=""+(int)actualHeight;
			    Rdescent1=""+getDesentRate()+"m/s";
	   }
		isEval = true;

		//LOGGING
		if(Constance.IS_LOGGING_SETUP) {
			String str_report = (isAz? AppUtils.fixedLengthString("Azimuth Track Report:", Constance.LOG_STRING_LENGTH):AppUtils.fixedLengthString("Elevation Track Report:", Constance.LOG_STRING_LENGTH));
			String str_xydash = (isAz? (AppUtils.fixedLengthString("X':", Constance.LOG_STRING_LENGTH)+(int)x+"\n"+AppUtils.fixedLengthString("Y':", Constance.LOG_STRING_LENGTH)+(int)y)
					:(AppUtils.fixedLengthString("X':", Constance.LOG_STRING_LENGTH)+(int)x+"\n"+AppUtils.fixedLengthString("Y':", Constance.LOG_STRING_LENGTH)+(int)z));
	    	String str_id = AppUtils.fixedLengthString("ID:", Constance.LOG_STRING_LENGTH)+getTrackNumberString();
	    	String str_range = AppUtils.fixedLengthString("Range:", Constance.LOG_STRING_LENGTH-4)+(int)range+" m";
	    	String str_angle = (isAz?AppUtils.fixedLengthString("Azimuth Angle:", Constance.LOG_STRING_LENGTH-10)
	    			+df.format(Math.toDegrees(azimuth))+Constance.UNITS.DEGREES
	    			:AppUtils.fixedLengthString("Elevation Angle:", Constance.LOG_STRING_LENGTH-10)
	    			+df.format(Math.toDegrees(elevation))+Constance.UNITS.DEGREES);
	    	String str_xyvel = AppUtils.fixedLengthString("X Velocity:", Constance.LOG_STRING_LENGTH-5)+xVel
	    			+"\n"+ AppUtils.fixedLengthString("Y Velocity:", Constance.LOG_STRING_LENGTH-5)+yVel;
	    	String str_speed = AppUtils.fixedLengthString("Speed:", Constance.LOG_STRING_LENGTH-3)+speed+" Kmph";
	    	String str_off = (isAz? AppUtils.fixedLengthString("Lateral Offset:  ", Constance.LOG_STRING_LENGTH-7):
	    		AppUtils.fixedLengthString("Vertical Deviation:", Constance.LOG_STRING_LENGTH-7))
	    			+(int)offLoVd+" m";

	    	if(isAz) {
	    		AppConfig.getInstance().getLoggingSetUpController().appendAzLine(str_report);
	    		AppConfig.getInstance().getLoggingSetUpController().appendAzLine(str_xydash);
	    		AppConfig.getInstance().getLoggingSetUpController().appendAzLine(str_id);
	    		AppConfig.getInstance().getLoggingSetUpController().appendAzLine(str_range);
	    		AppConfig.getInstance().getLoggingSetUpController().appendAzLine(str_angle);
	    		AppConfig.getInstance().getLoggingSetUpController().appendAzLine(str_xyvel);
	    		AppConfig.getInstance().getLoggingSetUpController().appendAzLine(str_speed);
	    		AppConfig.getInstance().getLoggingSetUpController().appendAzLine(str_off);

		    	AppConfig.getInstance().getLoggingSetUpController().appendAzLine("*********************************************************************************");
	    	} else {
	    		AppConfig.getInstance().getLoggingSetUpController().appendElLine(str_report);
	    		AppConfig.getInstance().getLoggingSetUpController().appendElLine(str_xydash);
	    		AppConfig.getInstance().getLoggingSetUpController().appendElLine(str_id);
	    		AppConfig.getInstance().getLoggingSetUpController().appendElLine(str_range);
	    		AppConfig.getInstance().getLoggingSetUpController().appendElLine(str_angle);
	    		AppConfig.getInstance().getLoggingSetUpController().appendElLine(str_xyvel);
	    		AppConfig.getInstance().getLoggingSetUpController().appendElLine(str_speed);
	    		AppConfig.getInstance().getLoggingSetUpController().appendElLine(str_off);

	    		AppConfig.getInstance().getLoggingSetUpController().appendElLine("*********************************************************************************");
	    	}
		}
	}

 @Override
 public void draw(GraphicsContext gc) {
	MatrixRef matrixRef = MatrixRef.getInstance();
	 	
		 if((((range/1000)) <=(matrixRef.getVisibleRange()) && Constance.SHOW_TRACK)||((range*Constance.UNITS.METER_TO_NM) <= matrixRef.getVisibleRange() && Constance.SHOW_TRACK)) {
			double x = getX();
			double y = 0;
			double agl = 0;
			double anglRD = 0;
			color = null;
			if(isAz) {
				y = getY();
				agl = Math.toDegrees(azimuth);
				anglRD = Math.toDegrees(azimuthRadar);
				if(agl <=(-Constance.CorrectedAzMax) || agl >=(Constance.CorrectedAzMax) ) {
					color = Color.TRANSPARENT;
					showLabel = false;
					return;
				}
			}else if(isEl) {
				y = getZ();
				agl = Math.toDegrees(elevation);
				anglRD = Math.toDegrees(elevationRadar);
				if(agl >Constance.CorrectedElMax  || agl<0 ) {
					//working
					color = Color.TRANSPARENT;
					showLabel = false;
					return;
				}
			}

			//set point
			trackXY.setX(x);
			trackXY.setY(y);
	    	if(color!=null){
	    		         gc.setFill(color);
	    		         gc.setStroke(color);
	    	 }
	    	// For Creating Track symbol.
	        else if (isEl==true){	
	      
	    	         // If target matches, Track symbol should be plus with circle and shown in white color
	    	         if((getState()==4)){
		    		     gc.setFill(Color.WHITE);
		    		     gc.setStroke(Color.WHITE);
		    	         gc.strokeOval(x-TRACK_SIZE1, y-TRACK_SIZE1, 2*TRACK_SIZE1, 2*TRACK_SIZE1);
		    	         gc.setLineWidth(2.0);
			             gc.strokeLine(x,y+TRACK_SIZE1,x,y-TRACK_SIZE1);
			    	     gc.strokeLine(x+TRACK_SIZE1, y, x-TRACK_SIZE1, y);	
		    	     }
	    	         //  If target does not match and outside tolerance line, Track symbol should be plus  and shown in white color	
	    	        else{
	    		        gc.setFill(Color.WHITE);
	            	    gc.setStroke(Color.WHITE);
	            	    gc.fillOval(x-TRACK_SIZE, y-TRACK_SIZE, 2*TRACK_SIZE, 2*TRACK_SIZE);
	            	    gc.setLineWidth(2.0);
	            	    gc.strokeLine(x,y+2*TRACK_SIZE,x,y-2*TRACK_SIZE);
	 		    	    gc.strokeLine(x+2*TRACK_SIZE, y, x-2*TRACK_SIZE, y); 	
	    	       }
	         }
	    	 
	         else if(isAz==true){
		
		                //If target matches, Track symbol should be plus with circle and shown in white color
		              if(Constance.commonIDlist.contains(trackNumber)){
	    		        gc.setFill(Color.WHITE);
	    		        gc.setStroke(Color.WHITE);
	    	            gc.strokeOval(x-TRACK_SIZE1, y-TRACK_SIZE1, 2*TRACK_SIZE1, 2*TRACK_SIZE1);
	    	            gc.setLineWidth(2.0);
		                gc.strokeLine(x,y+TRACK_SIZE1,x,y-TRACK_SIZE1);
		    	        gc.strokeLine(x+TRACK_SIZE1, y, x-TRACK_SIZE1, y);
                       }
		               // If target does not match, Track symbol should be plus  and shown in white color	
                      else{
                        gc.setFill(Color.WHITE);
        	            gc.setStroke(Color.WHITE);
        	            gc.fillOval(x-TRACK_SIZE, y-TRACK_SIZE, 2*TRACK_SIZE, 2*TRACK_SIZE);
        	            gc.setLineWidth(2.0);
        	            gc.strokeLine(x,y+2*TRACK_SIZE,x,y-2*TRACK_SIZE);
		    	        gc.strokeLine(x+2*TRACK_SIZE, y, x-2*TRACK_SIZE, y);	
                      }  
		 
		              /* gc.setFill(Color.CYAN);
		                 gc.fillPolygon(new double[] { x, x - 10, x,       //Added in order to change the track symbol.
				    	x+10 }, new double[] {y - 5, y,
							y + 5, y }, 4);
		                gc.strokePolygon(new double[] { x, x - 10, x,
					        x+10 }, new double[] {y - 5, y,
							y + 5, y }, 4);*/  
	      }
	    	
	      // Track data block generation.
	      if (showLabel && Constance.SHOW_TRACK_LABEL) {
	    		if(color!=null)
		    		gc.setStroke(color);
		    	else
		    		gc.setStroke(Color.CHOCOLATE);
	        	float dScale = 2.5f;
	        	gc.setLineWidth(1.0);
	        	Point p;
	        	if(labelXY == null) {
		        	DrawingUtils.drawLineAtAngle(gc, x, y, dScale*LABEL_SIZE, -45);
		        	if(Constance.UNITS.isLogged==true)
		        	   p = DrawingUtils.getNextPointAtAngle2(x, y, dScale*LABEL_SIZE, -45);  // In order to make changes to the label for the logged scale version.
		        	else
		        	   p = DrawingUtils.getNextPointAtAngle(x, y, dScale*LABEL_SIZE, -45);
		        	labelXY = p;
	        	}else {
	        		
	        	   if(isAz){
	        		     if(Constance.UNITS.isLogged){
		        	        p = DrawingUtils.getNextPointAtAngle2(x+30, TEXT_OFFSET+5, dScale*LABEL_SIZE, -45);   // In order to make changes to the label for the logged scale version.
		        	        labelXY = p;  // Added in order to move the track label along with track.
	        		      }else{
	        		    	 p = DrawingUtils.getNextPointAtAngle(x+30, TEXT_OFFSET+5, dScale*LABEL_SIZE, -45);
			        	     labelXY = p;  // Added in order to move the track label along with track.
	        		       }
	        		}else{
	        			 if(Constance.UNITS.isLogged){
	        			    p = DrawingUtils.getNextPointAtAngle2(x+30, matrixRef.getELActualYdimen()-8, dScale*LABEL_SIZE, -45);   // In order to make changes to the label for the logged scale version.
			        	    labelXY = p;  // Added in order to move the track label along with track.
	        			 }else{
	        				 p = DrawingUtils.getNextPointAtAngle(x+30, matrixRef.getELActualYdimen()-8, dScale*LABEL_SIZE, -45);
				        	 labelXY = p;  // Added in order to move the track label along with track.
	        			 }
	        		 }
		        	  p = labelXY;
	        		  gc.strokeLine(x, y, p.getX(), p.getY());		
	        	}

	        if(isAz){
		          gc.strokeRect(p.getX(), p.getY()-RECT_HEIGHT/2, RECT_WIDTH, RECT_HEIGHT);
		          gc.fillRect(p.getX(), p.getY()-RECT_HEIGHT/2, REGION_WIDTH, RECT_HEIGHT);//region	
		         }
		    else {
		    	   gc.strokeRect(p.getX(), p.getY()-RECT_HEIGHT1/2, RECT_WIDTH, RECT_HEIGHT1);
			       gc.fillRect(p.getX(), p.getY()-RECT_HEIGHT1/2, REGION_WIDTH, RECT_HEIGHT1);//region	 
		        }
	         
	        	double rng =(Constance.UNITS.isKM?(range/1000):(range*Constance.UNITS.METER_TO_NM));
	        	//double rngRD = (Constance.UNITS.isKM?((range/1000)+matrixRef.getStartRange()):((range*Constance.UNITS.METER_TO_NM)+matrixRef.getStartRange()));
                String id = null;
                if(getState()==4){
                	if(Constance. azRenameListInTrack.containsKey(corrName)){
                 		id =Constance.azRenameListInTrack.get(corrName);
                 		id = "   ID: "+id;
                 	} else{
                 		 id = "   ID: "+corrName;
                 	} 
	        	    
                }else{
                	if(Constance. azRenameListInTrack.containsKey(trackNumber)){
                 		id =Constance.azRenameListInTrack.get(trackNumber);
                 		id = "   ID: "+id;
                 	} else{
                 		id = "   ID: "+getTrackNumberString();
                 	}       	    	
                }
                
	        	String range = "  R: "+df.format((rng))+(Constance.UNITS.isKM?" Km":" NM");
              //  String range = "  R: "+df.format((isAz?rng:rngRD))+(Constance.UNITS.isKM?" Km":" NM");
	          //  String angle = (isAz?"  A: ":"  E: ")+df.format(isAz?agl:agl)+Constance.UNITS.DEGREES;
	        	String vel = "  S: "+speed+(Constance.UNITS.isKM?" KmPh":" Knots");
	        	String off = (isAz? "  ":"  ")+offLoVd1;
	        	String height=(isAz?"  ":"  H: ")+acutualHeight1+(isAz?"  ":" ft");
	        	//String descent=(isAz?"  ":" RD: ")+Rdescent1;
	        	
	           if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))) {
	        		double loff = 1.5*dScale;
	        		java.awt.Color yel = new java.awt.Color(255, 255, 0, 255);
	        		java.awt.Color whit = new java.awt.Color(255, 255, 255, 255);
	        		if(color!=null) {
	        			yel = new java.awt.Color(255, 0, 0, 0);
	        			whit = new java.awt.Color(255, 255, 255, 0);
	        		}
	        	
	        		if(isAz){
	        			gc.drawImage(DrawingUtils.printMirrorImage(id, 12,yel), p.getX()+loff*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()-5*LABEL_SIZE);
		        		gc.drawImage(DrawingUtils.printMirrorImage(range, 12,whit), p.getX()+loff*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()-2*LABEL_SIZE);
		        		gc.drawImage(DrawingUtils.printMirrorImage(vel, 12,whit), p.getX()+loff*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()+LABEL_SIZE);
		        		gc.drawImage(DrawingUtils.printMirrorImage(off, 12,whit), p.getX()+loff*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()+4*LABEL_SIZE);
		        		
	        		}else{
	        			gc.drawImage(DrawingUtils.printMirrorImage(id, 12,yel), p.getX()+loff*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()-5*LABEL_SIZE);
		        		gc.drawImage(DrawingUtils.printMirrorImage(off, 12,whit), p.getX()+loff*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()-2*LABEL_SIZE);
		        		gc.drawImage(DrawingUtils.printMirrorImage(height, 12,whit), p.getX()+loff*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()+LABEL_SIZE);
	        		}
	        		
	        	} else {
	        		gc.setLineWidth(1);
		        	gc.setFont(new Font("Serif", dScale*LABEL_SIZE));
		        	if(color!=null)
			    		gc.setStroke(color);
			    	else
			    		gc.setStroke(Color.YELLOW);
		        	
		        	 gc.strokeText(id, p.getX()+(dScale*LABEL_SIZE*LBEL_REMOVE_WHITE), p.getY()-3*LABEL_SIZE);
		        	if(color!=null)
			    		gc.setStroke(color);
			    	else
			    		gc.setStroke(Color.WHITE);
		        	
		           if(isAz){
		        	    gc.strokeText(range, p.getX()+dScale*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()-0.3*LABEL_SIZE);
		        	    gc.strokeText(vel, p.getX()+dScale*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()+3*LABEL_SIZE);
		        	    gc.strokeText(off, p.getX()+dScale*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()+6*LABEL_SIZE);
		        	   // gc.strokeText(angle, p.getX()+dScale*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()+9*LABEL_SIZE); //This is addede just to display angle from radar.
		           }
		           else{
		        	   // gc.strokeText(range, p.getX()+dScale*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()-8*LABEL_SIZE);  //This is added just to display angle from radar.
			          //  gc.strokeText(angle, p.getX()+dScale*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()-6*LABEL_SIZE); 
		        	    gc.strokeText(off, p.getX()+dScale*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()-0.3*LABEL_SIZE);
		        	    gc.strokeText(height, p.getX()+dScale*LABEL_SIZE*LBEL_REMOVE_WHITE, p.getY()+3*LABEL_SIZE);
		           }
	          }   	
	      }	
	  }
		//add for path history
		String name = "";
		if(isAz)
			name = "AZ";
		if(isEl)
			name = "EL";
		DataManager.getInstance().addTrackPathPoint(name, this, trackNumber);	
	}

	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	@Override
	public String toString() {
		return "onTrack : "+"\n"
				+"Range: "+range+"\n"
				+"Az: "+ Math.toDegrees(azimuth)+"\n"
				+"El: "+ Math.toDegrees(elevation);
	}
	
	
}
