package model.graph;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light.Point;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.GraphChart;
import model.MatrixRef;
import org.apache.log4j.Logger;
import utils.Constance;
import utils.DrawingUtils;
import views.ResizableCanvas;
import admin.UserPreference;

public class AzimuthChart extends GraphChart {
	
	static final Logger logger = Logger.getLogger(AzimuthChart.class);
	
	double midAzimuth;	
	double cutXup,cutXdown;
	MatrixRef matrixRef = MatrixRef.getInstance();
	double touchDownOffset;
	int totalVisibleHeight;
	
	class myPoint {
		int X;
		int leftY;
		int rightY;		
	}	
	List<myPoint> crossPoints = new ArrayList<myPoint>();
	 
	public AzimuthChart(ResizableCanvas canvas) {
		super(canvas);
		touchDownOffset = matrixRef.toRangePixels(matrixRef.getStartRange());
		midAzimuth = (matrixRef.getMinAzimuth()+matrixRef.getMaxAzimuth())/2;

        double tHeight = (((matrixRef.getVisibleRange()-matrixRef.getStartRange())*Constance.UNITS.getR_PX()) * Math.tan(Math.toRadians(Constance.AZIMUTH_MAX)));
        totalVisibleHeight = (int) Math.ceil(tHeight * 1000*Constance.UNITS.getFACTOR_HEIGHT());//in Meters/Feet
        
	}
	
	public void drawRadarLocation() {
		Point startPoint = matrixRef.toAzimuthRangePixels(midAzimuth,matrixRef.getMinRange()+Constance.UNITS.TDPzero() );
		Constance.AZIMUTH.RCLO_PX = (matrixRef.getAZActualYdimen() * 250 * Constance.AZIMUTH.RCLO)/totalVisibleHeight;
		gc.setFill(Color.CORNFLOWERBLUE);
		if(Constance.AZIMUTH.RCLO_PX<0)
				Constance.AZIMUTH.RCLO_PX=Constance.AZIMUTH.RCLO_PX-OFFSET;  //Added in order to change the radar symbol position to correct place. 
	    if(Constance.UNITS.isKM){
		    if(Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))
		        gc.fillRoundRect(startPoint.getX(), startPoint.getY()-(Constance.AZIMUTH.RCLO_PX)-OFFSET, OFFSET, OFFSET, 2, 2);
		    else
			    gc.fillRoundRect(startPoint.getX(), startPoint.getY()+((Constance.AZIMUTH.RCLO_PX)), OFFSET, OFFSET, 2, 2);
		}else{
			if(Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))
			    gc.fillRoundRect(startPoint.getX(), startPoint.getY()-(Constance.AZIMUTH.RCLO_PX)-(OFFSET+5), OFFSET, OFFSET, 2, 2);
			else
				gc.fillRoundRect(startPoint.getX(), startPoint.getY()+((Constance.AZIMUTH.RCLO_PX)+5), OFFSET, OFFSET, 2, 2);
		}
		
	}
	
	public void drawAzimuthLine() {

		Point startPoint = matrixRef.toAzimuthRangePixels(midAzimuth,matrixRef.getMinRange()+Constance.UNITS.TDPzero()); //because log(0) value is infinity
		Point endPoint = matrixRef.toAzimuthRangePixels(midAzimuth, matrixRef.getVisibleRange()+Constance.UNITS.TDPzero());     	
                 
        //calculation done for finding the X,Y point for range markers
        for(double i= matrixRef.getMinRange();i<(matrixRef.getVisibleRange()+Constance.RANGE_DISP+1);i+=(Constance.RANGE_DISP)){
        	Point point = matrixRef.toAzimuthRangePixels(matrixRef.getMinElevation(), i+Constance.UNITS.TDPzero());  // Constance.UNITS.TDPzero() is added in order to make the Range scale line alignment properly in NM.
        	myPoint p = new myPoint();
        	p.X = (int) point.getX();
        	crossPoints.add(p);
        }
                
        //finding Y (L & R) co-ordinates
        cutXup = endPoint.getX()+TEXT_OFFSET;
        cutXdown = endPoint.getX()+TEXT_OFFSET;
        boolean stopCutXup = false;
        boolean stopCutXdown = false;
        for(int i=0;i<matrixRef.getDrawableXArea()+TEXT_OFFSET;i++) {
        Point	pointLeft = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),i, -Constance.AZIMUTH.LSL_ANGLE);//cross line at 20 degrees
        Point	pointRight = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),i, -Constance.AZIMUTH.RSL_ANGLE);//cross line at 20 degrees
        	int raX = (int) pointLeft.getX();
        	for(int j=0;j<crossPoints.size();j++) {
        		if(raX == crossPoints.get(j).X){
        			int yLeftVal = (int) pointLeft.getY();
        			int yRightVal = (int) pointRight.getY();
        			if(!Constance.UNITS.isKM) {
     
        				if(Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")){
        					yLeftVal -= ((TEXT_OFFSET+HGAP)/matrixRef.getVisibleRange())-OFFSET/2;
        					yRightVal += ((TEXT_OFFSET+HGAP)/matrixRef.getVisibleRange()); // (OFFSET/2) is added in order to make the Top range mark lines alignment properly,this is done after changing the radar point to show the center line inside the scan line. 
        				}else{
        					yLeftVal -= ((TEXT_OFFSET+HGAP)/matrixRef.getVisibleRange());
        					yRightVal += ((TEXT_OFFSET+HGAP)/matrixRef.getVisibleRange())-OFFSET/2; // similarly like above comment
        				}
        			 }
        
        			crossPoints.get(j).leftY = yLeftVal;
        			crossPoints.get(j).rightY = yRightVal;
        			break;
        		}
        	}
        	//getting the cutting edge
    		if(pointRight.getY() > (matrixRef.getAZDrawableYArea()-OFFSET/5) && !stopCutXdown) {
    			cutXdown = pointRight.getX()+OFFSET/5;
    			stopCutXdown = true;
			}
    		
    		if(pointLeft.getY() < OFFSET/5 && !stopCutXup) {
    			cutXup = pointLeft.getX()+OFFSET/5;
    			stopCutXup = true;
			}
    		
    		
        }
        
        //Drawing angled Line
        gc.setStroke(Color.CADETBLUE);
        gc.setLineWidth(1.0);
          
      if(Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")){
          if(Constance.UNITS.isKM){
                DrawingUtils.drawLineAtAngle(gc, startPoint.getX(),startPoint.getY()-((Constance.AZIMUTH.RCLO_PX)+5),endPoint.getX()+TEXT_OFFSET, -Constance.AZIMUTH.LSL_ANGLE);//cross line at top az degrees
                DrawingUtils.drawLineAtAngle(gc, startPoint.getX(),startPoint.getY()-((Constance.AZIMUTH.RCLO_PX)+5),endPoint.getX()+TEXT_OFFSET, -(Constance.AZIMUTH.RSL_ANGLE));//cross line at bottom az degrees
        
        // These lines are added in order to maintain  the symmetry of azimuth graph for the default offsets. 
               if(Constance.DisplayScale==5)	
                      gc.strokeLine(cutXup-(TEXT_OFFSET/15-(OFFSET/15)+158), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
               else if(Constance.DisplayScale==10)	
                     gc.strokeLine(cutXup-(TEXT_OFFSET/15-(OFFSET/15)+93), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
               else if(Constance.DisplayScale==20)	
                    gc.strokeLine(cutXup-(TEXT_OFFSET/15-(OFFSET/15)+65), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
               else if(Constance.DisplayScale==30)	
                   gc.strokeLine(cutXup-(TEXT_OFFSET/15-(OFFSET/15)+50), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
        						
        	
              if(Constance.DisplayScale==5)
                  gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET-145), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);//flat bottom line
              else if(Constance.DisplayScale==10)
                  gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET-95), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);
             else if(Constance.DisplayScale==20)
                 gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET-68), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);
             else if(Constance.DisplayScale==30)
                 gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET-58), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);
           } else{
        	  
              DrawingUtils.drawLineAtAngle(gc, startPoint.getX(),startPoint.getY()-((Constance.AZIMUTH.RCLO_PX)+5),endPoint.getX()+TEXT_OFFSET, -Constance.AZIMUTH.LSL_ANGLE);//cross line at top az degrees
              DrawingUtils.drawLineAtAngle(gc, startPoint.getX(),startPoint.getY()-((Constance.AZIMUTH.RCLO_PX)+5),endPoint.getX()+TEXT_OFFSET, -(Constance.AZIMUTH.RSL_ANGLE));//cross line at bottom az degrees
              
              if(Constance.DisplayScale==5)	
                  gc.strokeLine(cutXup-(TEXT_OFFSET/15-(OFFSET/15)+158), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
              else if(Constance.DisplayScale==10)	
                  gc.strokeLine(cutXup-(TEXT_OFFSET/15-(OFFSET/15)+93), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
              else if(Constance.DisplayScale==20)	
                  gc.strokeLine(cutXup-(TEXT_OFFSET/15-(OFFSET/15)+65), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
              else if(Constance.DisplayScale==30)	
                  gc.strokeLine(cutXup-(TEXT_OFFSET/15-(OFFSET/15)+50), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
              						
              	
              if(Constance.DisplayScale==5)
                gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET-145), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);//flat bottom line
              else if(Constance.DisplayScale==10)
                gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET-115), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);
              else if(Constance.DisplayScale==20)
                gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET-88), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);
              else if(Constance.DisplayScale==30)
                gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET-78), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);     
          }
       }  else{
        	if(Constance.UNITS.isKM){
        		// For drawing upper angled lines based on the scale selected.
        	     DrawingUtils.drawLineAtAngle(gc, startPoint.getX(),startPoint.getY()+((Constance.AZIMUTH.RCLO_PX)+5),endPoint.getX()+TEXT_OFFSET, -Constance.AZIMUTH.LSL_ANGLE);//cross line at top az degrees
        		 DrawingUtils.drawLineAtAngle(gc, startPoint.getX(),startPoint.getY()+((Constance.AZIMUTH.RCLO_PX)+5),endPoint.getX()+TEXT_OFFSET, -Constance.AZIMUTH.RSL_ANGLE);//cross line at bottom az degrees
        		
            
        		 //for drawing  Upper flat line
                 if(Constance.DisplayScale==5)	
                     gc.strokeLine(cutXup-(TEXT_OFFSET/15+(OFFSET/15)-158), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
                 else if(Constance.DisplayScale==10)	
                     gc.strokeLine(cutXup-(TEXT_OFFSET/15+(OFFSET/15)-93), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
                 else if(Constance.DisplayScale==20)	
                     gc.strokeLine(cutXup-(TEXT_OFFSET/15+(OFFSET/15)-65), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
                 else if(Constance.DisplayScale==30)	
                     gc.strokeLine(cutXup-(TEXT_OFFSET/15+(OFFSET/15)-50), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
             						
                 //for drawing  Lower flat line based on the display scale selected    
                 if(Constance.DisplayScale==5)
                     gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET+115), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);//flat bottom line
                 else if(Constance.DisplayScale==10)
                     gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET+45), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);
                 else if(Constance.DisplayScale==20)
                     gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET+18), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);
                 else if(Constance.DisplayScale==30)
                     gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET+8), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);
        	}
        	else {
        		DrawingUtils.drawLineAtAngle(gc, startPoint.getX(),startPoint.getY()+((Constance.AZIMUTH.RCLO_PX)+5),endPoint.getX()+TEXT_OFFSET, -(Constance.AZIMUTH.LSL_ANGLE));//cross line at top az degrees
                DrawingUtils.drawLineAtAngle(gc, startPoint.getX(),startPoint.getY()+((Constance.AZIMUTH.RCLO_PX)+5),endPoint.getX()+TEXT_OFFSET, -Constance.AZIMUTH.RSL_ANGLE);//cross line at bottom az degrees
                
                //for drawing  Upper flat line
                if(Constance.DisplayScale==5)	
                    gc.strokeLine(cutXup-(TEXT_OFFSET/15+(OFFSET/15)-158), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
                else if(Constance.DisplayScale==10)	
                    gc.strokeLine(cutXup-(TEXT_OFFSET/15+(OFFSET/15)-93), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
                else if(Constance.DisplayScale==20)	
                    gc.strokeLine(cutXup-(TEXT_OFFSET/15+(OFFSET/15)-65), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
                else if(Constance.DisplayScale==30)	
                    gc.strokeLine(cutXup-(TEXT_OFFSET/15+(OFFSET/15)-50), OFFSET/5, matrixRef.getDrawableXArea()+TEXT_OFFSET,(OFFSET/2)-5);//flat top line
                						
              //for drawing  Lower flat line
                if(Constance.DisplayScale==5)
                  gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET+95), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);//flat bottom line
                else if(Constance.DisplayScale==10)
                  gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET+68), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);
                else if(Constance.DisplayScale==20)
                  gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET+51), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);
                else if(Constance.DisplayScale==30)
                  gc.strokeLine(cutXdown-(TEXT_OFFSET+OFFSET+44), matrixRef.getAZDrawableYArea()-(OFFSET/5)+2, matrixRef.getDrawableXArea()+TEXT_OFFSET,matrixRef.getAZDrawableYArea()-(OFFSET/2)+3);
        	}
        	
        }
      
	}

	public void drawLandingStrip() {
		//for runway landing strip offset
		
		// For Drawing center line.
		double centerDist =(Constance.ELEVATION.GLIDE_MAX_DIST)+Constance.UNITS.TDPzero();
		Point startPoint = matrixRef.toAzimuthRangePixels(midAzimuth,matrixRef.getStartRange()+Constance.UNITS.TDPzero());
		Point endPoint = matrixRef.toAzimuthRangePixels(midAzimuth, centerDist+Constance.RANGE_DISP);
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(1);        
        gc.setStroke(Color.BLUE);
        gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(),endPoint.getY());//center line
        
    
        // These lines are drawn based on angle calculation.  
        // First set of Safety lines
        double angle = Math.asin((Constance.SafetyParam.FirstSetLR)/(Constance.TOUCH_DOWN));
        System.out.println("angle for the first set is: "+angle);
        Point RangePix = matrixRef.toAzimuthRangePixels(midAzimuth,matrixRef.getStartRange()+Constance.UNITS.TDPzero());
        Point startPoint1 = DrawingUtils.getNextPointAtAngle1(0, RangePix.getY(), RangePix.getX(), -(Math.toDegrees(angle)));
        angle = Math.asin((Constance.SafetyParam.FirstSetLR)/(Constance.SafetyParam.FirstLineEnd+(Constance.TOUCH_DOWN)));
        double RangeEnd= matrixRef.toRangePixels((Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH()+(Constance.UNITS.isLogged?0:matrixRef.getStartRange())));
        gc.setStroke(Color.RED);
        gc.setLineWidth(0.5);    
        gc.strokeLine(startPoint1.getX(), startPoint1.getY(), RangeEnd,startPoint1.getY());//above center line
        
        angle = Math.asin((Constance.SafetyParam.FirstSetLR)/(Constance.TOUCH_DOWN));
        Point RangePix2 = matrixRef.toAzimuthRangePixels(midAzimuth,matrixRef.getStartRange()+Constance.UNITS.TDPzero());
        Point startPoint9 = DrawingUtils.getNextPointAtAngle1(0, RangePix.getY(), RangePix2.getX(), (Math.toDegrees(angle)));
        angle = Math.asin((Constance.SafetyParam.FirstSetLR)/(Constance.SafetyParam.FirstLineEnd+(Constance.TOUCH_DOWN)));
        RangeEnd= matrixRef.toRangePixels((Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH()+(Constance.UNITS.isLogged?0:matrixRef.getStartRange())));
        gc.setStroke(Color.RED); 
        gc.strokeLine(startPoint9.getX(), startPoint9.getY(), RangeEnd,startPoint9.getY());//below center line 
        
      //  Second set of Safety lines
        angle = Math.asin((Constance.SafetyParam.SecondSetLR)/(Constance.SafetyParam.FirstLineEnd+Constance.TOUCH_DOWN));
        System.out.println("angle for the second set is: "+angle);
        RangePix = matrixRef.toAzimuthRangePixels(midAzimuth,(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH()+matrixRef.getStartRange()+Constance.UNITS.TDPzero()));
        startPoint1 = DrawingUtils.getNextPointAtAngle1(0, RangePix.getY(), RangePix.getX(), -(Math.toDegrees(angle)));
        double RangeEnd2=startPoint1.getX();
        double RangeEnd3=matrixRef.toRangePixels((Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH()+matrixRef.getStartRange()+Constance.UNITS.TDPzero()));
        gc.setStroke(Color.SPRINGGREEN);
        gc.strokeLine(RangeEnd2, startPoint1.getY(), RangeEnd3,startPoint1.getY());//above center line
       
        angle = Math.asin((Constance.SafetyParam.SecondSetLR)/(Constance.SafetyParam.FirstLineEnd+Constance.TOUCH_DOWN));
        RangePix = matrixRef.toAzimuthRangePixels(midAzimuth,(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH()+matrixRef.getStartRange()+Constance.UNITS.TDPzero()));
        startPoint1 = DrawingUtils.getNextPointAtAngle1(0, RangePix.getY(), RangePix.getX(), (Math.toDegrees(angle)));
        RangeEnd2=startPoint1.getX();
        RangeEnd3=matrixRef.toRangePixels((Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH()+matrixRef.getStartRange()+Constance.UNITS.TDPzero()));
        gc.setStroke(Color.SPRINGGREEN);
        gc.strokeLine(RangeEnd2, startPoint1.getY(), RangeEnd3,startPoint1.getY());//below center line
    
       // Third set of Safety lines
        angle = Math.asin((Constance.SafetyParam.ThirdSetLR)/(Constance.SafetyParam.SecondLineEnd+(Constance.TOUCH_DOWN)));
        RangePix = matrixRef.toAzimuthRangePixels(midAzimuth,(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH()+matrixRef.getStartRange()+Constance.UNITS.TDPzero()));
        startPoint1 = DrawingUtils.getNextPointAtAngle1(0, RangePix.getY(), RangePix.getX(), -(Math.toDegrees(angle)));
        RangeEnd2=startPoint1.getX();
        RangeEnd3=matrixRef.toRangePixels((Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH()+matrixRef.getStartRange()+Constance.UNITS.TDPzero()));
        gc.setStroke(Color.FUCHSIA);
        gc.strokeLine(RangeEnd2, startPoint1.getY(), RangeEnd3,startPoint1.getY());//above center line
        
        angle = Math.asin((Constance.SafetyParam.ThirdSetLR)/(Constance.SafetyParam.SecondLineEnd+Constance.TOUCH_DOWN));
        RangePix = matrixRef.toAzimuthRangePixels(midAzimuth,(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH()+matrixRef.getStartRange()+Constance.UNITS.TDPzero()));
        startPoint1 = DrawingUtils.getNextPointAtAngle1(0, RangePix.getY(), RangePix.getX(), (Math.toDegrees(angle)));
        RangeEnd2=startPoint1.getX(); 
        RangeEnd3=matrixRef.toRangePixels((Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH()+matrixRef.getStartRange()+Constance.UNITS.TDPzero()));
        gc.setStroke(Color.FUCHSIA);
        gc.strokeLine(RangeEnd2, startPoint1.getY(), RangeEnd3,startPoint1.getY());//below center line
        
        
	}
	
	public void drawRedDistanceLine() {		
	
		//draw line TD
	    double range = matrixRef.toRangePixels(matrixRef.getStartRange()+Constance.UNITS.TDPzero());
	    Point	startPoint = matrixRef.toAzimuthRangePixels(midAzimuth, matrixRef.getStartRange()+Constance.UNITS.TDPzero());
	    Point	pL = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(), range,- Constance.AZIMUTH.LSL_ANGLE);
	    Point	pR = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(), range, - Constance.AZIMUTH.RSL_ANGLE);
		gc.setStroke(Color.YELLOW);
		gc.setLineWidth(1.5);
		gc.setLineDashes(OFFSET/3);
	    if(Constance.UNITS.isKM){
		  if(Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")){
		       gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),pL.getY()-(Constance.AZIMUTH.RCLO_PX+5));
		       gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),pR.getY()-(Constance.AZIMUTH.RCLO_PX+5));
		   } else{
			   gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),pL.getY()+(Constance.AZIMUTH.RCLO_PX+5));
			   gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),pR.getY()+(Constance.AZIMUTH.RCLO_PX+10)); 	 
		  }
	    }else{
		  if(Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")){
			   gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),pL.getY()-(Constance.AZIMUTH.RCLO_PX+5));
			   gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),pR.getY()-(Constance.AZIMUTH.RCLO_PX+5));
		   } else{
			   gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),pL.getY()+(Constance.AZIMUTH.RCLO_PX+5));
			   gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),pR.getY()+(Constance.AZIMUTH.RCLO_PX+10)); 	 
		   }   
	  }
	
        //write text TD
		gc.setLineDashes(0);
		gc.setStroke(Color.YELLOW);
		gc.setLineWidth(1.5);
		if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")))
    		gc.drawImage(DrawingUtils.printMirrorImage(" TDP ", 12, java.awt.Color.YELLOW), startPoint.getX()-2*HGAP, matrixRef.getAZDrawableYArea()/2+OFFSET);
		else
    		gc.strokeText(" TDP ", startPoint.getX()-TEXT_OFFSET, matrixRef.getAZDrawableYArea()/2+OFFSET);
		
        //Drawing +/-
		Point sPoint = matrixRef.toAzimuthRangePixels2(midAzimuth, matrixRef.getMinRange());
		gc.setFont(new Font("Sans Serif", 18));
        gc.setStroke(Color.YELLOW);
        if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))){
             gc.strokeText("-", 7*OFFSET, sPoint.getY()+HGAP+TEXT_OFFSET-Constance.AZIMUTH.RCLO_PX);
             gc.strokeText("+", 7*OFFSET, sPoint.getY()-TEXT_OFFSET-Constance.AZIMUTH.RCLO_PX);  
        }else{
        	gc.strokeText("-", 7*OFFSET, sPoint.getY()+HGAP+TEXT_OFFSET+Constance.AZIMUTH.RCLO_PX);
            gc.strokeText("+", 7*OFFSET, sPoint.getY()-TEXT_OFFSET+Constance.AZIMUTH.RCLO_PX);  
        }
      
	}

	public void drawDistanceGrid() {
		
		Point startPoint = matrixRef.toAzimuthRangePixels(midAzimuth,matrixRef.getStartRange()+Constance.UNITS.TDPzero());
		gc.setLineWidth(1.5);

		//remaining Lines		
        for(double i=matrixRef.getStartRange();i<matrixRef.getVisibleRange()+Constance.RANGE_DISP+1;i+=Constance.RANGE_DISP){
        	
        	startPoint = matrixRef.toAzimuthRangePixels(midAzimuth, i+Constance.UNITS.TDPzero()); //Constance.UNITS.TDPzero() is added in order to make the Range scale line alignment properly in NM.
        	
        	myPoint ePoint = crossPoints.get((int) i);
        	if(ePoint.leftY==ePoint.rightY)
        		ePoint.rightY=(int) matrixRef.getAZActualYdimen();
    		Point endTop = new Point();
    		endTop.setX(ePoint.X);
    		if((Constance.PREF.SEL_RUNWAY.contains("1") || Constance.PREF.SEL_RUNWAY.contains("2") || (Constance.UNITS.isKM==true)))
    		  endTop.setY(ePoint.leftY);
    		else
    		  endTop.setY(ePoint.leftY-OFFSET);
        	Point endBttm = new Point();
        	endBttm.setX(ePoint.X);
        	endBttm.setY(ePoint.rightY);

    		if(ePoint.X > (cutXup) || ePoint.X > cutXdown) {
    			endTop.setY((OFFSET/2-20));  // -20 is substracted in order to align the top vertical grid lines in azimuth.
    			if((Constance.PREF.SEL_RUNWAY.contains("1") || Constance.PREF.SEL_RUNWAY.contains("2")))
    			  endBttm.setY(matrixRef.getAZDrawableYArea()-OFFSET/2);
    			else
    			  endBttm.setY(matrixRef.getAZDrawableYArea()-OFFSET/2+20);  // 20 is added in order to align the bottom vertical grid lines in azimuth.
    		}
    		
        	int val = (int)( i);
        	if(val>(int)matrixRef.getStartRange())
        		val = (int) (i - matrixRef.getStartRange());	
        	if (i==matrixRef.getStartRange()) {          
      		   //gc.setStroke(Color.YELLOW);
        	} else if((((val%5)==0)&&(val!=0)) || (val==2)) {
        		gc.setLineWidth(0.5);
        		gc.setStroke(Color.YELLOW);
        	    if(Constance.UNITS.isKM){
        		     if(Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")){
        		         gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endTop.getY()-(Constance.AZIMUTH.RCLO_PX+5));
        		         gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endBttm.getY()-(Constance.AZIMUTH.RCLO_PX+5));
        		      }else{
        		         gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endTop.getY()+(Constance.AZIMUTH.RCLO_PX+5));
             		     gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endBttm.getY()+(Constance.AZIMUTH.RCLO_PX+5));
        		      }
        	     }else{
        		     if(Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")){
          		         gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endTop.getY()-(Constance.AZIMUTH.RCLO_PX+5));
          		         gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endBttm.getY()-(Constance.AZIMUTH.RCLO_PX+5));
          		     }else{
          		         gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endTop.getY()+(Constance.AZIMUTH.RCLO_PX+5));
               		     gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endBttm.getY()+(Constance.AZIMUTH.RCLO_PX+20));
          		     } 
        	     }
        	} else if(Constance.SHOW_RANGE_MARKS){
        		 gc.setLineWidth(0.5);
        		 gc.setStroke(Color.GREEN);
        		 if(Constance.UNITS.isKM){
        		      if(Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")){
        		         gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endTop.getY()-(Constance.AZIMUTH.RCLO_PX+5));
        		         gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endBttm.getY()-(Constance.AZIMUTH.RCLO_PX+5));
        		      }else{
        			     gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endTop.getY()+(Constance.AZIMUTH.RCLO_PX+5));
         		         gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endBttm.getY()+(Constance.AZIMUTH.RCLO_PX+5)); 
        		      }
        	     }else{
        		      if(Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")){
           		         gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endTop.getY()-(Constance.AZIMUTH.RCLO_PX+5));
           		         gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endBttm.getY()-(Constance.AZIMUTH.RCLO_PX+5));
           		      }else{
           			     gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endTop.getY()+(Constance.AZIMUTH.RCLO_PX+5));
            		     gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endBttm.getY()+(Constance.AZIMUTH.RCLO_PX+20)); 
           		      }
        	     }
        	}
        }
	}
	
	public void drawUnitsText(boolean bool) {

		gc.setStroke(Color.TRANSPARENT);
		java.awt.Color colorMirror = new java.awt.Color(0f,0f,0f,1f );
		
		if(bool) {
			gc.setStroke(Color.YELLOW);
			colorMirror = java.awt.Color.YELLOW;
		}
		gc.setFont(new Font("Sans Serif", 12));
        gc.setLineWidth(1);
		
        // X - AXIS UNITS
        int rangeCounter = 0;
    	String val = null;
        for(double i= matrixRef.getStartRange();i<matrixRef.getVisibleRange()+Constance.RANGE_DISP+1;i+=Constance.RANGE_DISP){
        	
        	Point startPoint = matrixRef.toAzimuthRangePixels(matrixRef.getMinAzimuth(), i+Constance.UNITS.TDPzero());
        	val = (rangeCounter++)+"";
        	if(i==matrixRef.getVisibleRange())
        		val = val +" _";
        	int rngVal = rangeCounter;
        	myPoint ePoint = crossPoints.get((int) i);
        	if(rngVal>1)
        		rngVal -= (int)Constance.RANGE_DISP;
        	if(matrixRef.getVisibleRange()<=(20*Constance.RANGE_DISP*Constance.UNITS.getFACTOR_LENGTH())) {
        		//write text Range
        		if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))){
        			if(((startPoint.getX()) > cutXdown) || (startPoint.getX() > cutXup))
        				gc.drawImage(DrawingUtils.printMirrorImage(val,12, colorMirror), startPoint.getX()-3*HGAP, startPoint.getY()-(2*OFFSET));	
        			else{
        				if((Constance.UNITS.isKM==false) && (Constance.DisplayScale==5))
        					gc.drawImage(DrawingUtils.printMirrorImage(val,12, colorMirror), startPoint.getX()-3*HGAP, ePoint.rightY+(2*OFFSET));
        					 // this condition is added in order to bring the units in 5km scale to little bit down.
        				else
        					gc.drawImage(DrawingUtils.printMirrorImage(val,12, colorMirror), startPoint.getX()-3*HGAP, ePoint.rightY);			
        			}
        		 }	
        		 else{
        		    if(((startPoint.getX()+2*OFFSET) > cutXdown) || (startPoint.getX() > cutXup))
        				gc.strokeText(val, startPoint.getX()-OFFSET, startPoint.getY()-OFFSET/2);
        			else{
        				if((Constance.UNITS.isKM==false) && (Constance.DisplayScale==5))
        				   gc.strokeText(val, startPoint.getX(), ePoint.rightY+(3*OFFSET)); // this condition is added in order to bring the units in 5km scale to little bit down.
        				else
        				   gc.strokeText(val, startPoint.getX(), ePoint.rightY+(2*OFFSET));
        			    }
        		     }
        	 } else if(((rngVal%5)==0)||(rngVal==2)) {
        		
        		   if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))){
        			     if( ((startPoint.getX()) > cutXdown) || (startPoint.getX() > cutXup))
	        		           gc.drawImage(DrawingUtils.printMirrorImage(val,12, colorMirror), startPoint.getX()-3*HGAP, startPoint.getY()-OFFSET);
        		         else
        			           gc.drawImage(DrawingUtils.printMirrorImage(val,12, colorMirror), startPoint.getX()-3*HGAP, ePoint.rightY+(1*OFFSET));
        		   }
        		   else{
        			if( ((startPoint.getX()+2*OFFSET) > cutXdown) || (startPoint.getX() > cutXup))
        				gc.strokeText(val, startPoint.getX()-OFFSET, startPoint.getY()-OFFSET/2);
        			else
        				gc.strokeText(val, startPoint.getX(), ePoint.rightY+(2*OFFSET));	
        		   }
        	  }
         }
        
        //Y - AXIS UNITS
     /*   gc.setFont(new Font("Sans Serif", 10));
	    String unit = Constance.UNITS.getHEIGHT();
	    if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))) {
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf(totalVisibleHeight)+unit,10,colorMirror), OFFSET/2, matrixRef.getAZDrawableYArea()*0.05);
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalVisibleHeight*0.5))+unit,10,colorMirror), OFFSET/2, matrixRef.getAZDrawableYArea()*0.29);
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalVisibleHeight*0))+unit,10,colorMirror), OFFSET/2, matrixRef.getAZDrawableYArea()*0.49);
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(-totalVisibleHeight*0.5))+unit,10,colorMirror), OFFSET/2, matrixRef.getAZDrawableYArea()*0.76);
	    	if(matrixRef.getVisibleRange() <= (Constance.RANGE_MAX+matrixRef.getStartRange())*Constance.RANGE_DISP){
	    		gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(-totalVisibleHeight))+unit,10,colorMirror), OFFSET/2, matrixRef.getAZDrawableYArea()-OFFSET);
	    	}
	    	} else {
		    gc.strokeText(String.valueOf(totalVisibleHeight)+unit, OFFSET/2, matrixRef.getAZDrawableYArea()*0.05);
		    gc.strokeText(String.valueOf((int)(totalVisibleHeight*0.5))+unit, OFFSET/2, matrixRef.getAZDrawableYArea()*0.29);
		    gc.strokeText(String.valueOf((int)(totalVisibleHeight*0))+unit, OFFSET/2, matrixRef.getAZDrawableYArea()*0.49);//Zero value
		    gc.strokeText(String.valueOf((int)(-totalVisibleHeight*0.5))+unit, OFFSET/2, matrixRef.getAZDrawableYArea()*0.76);
		    if(matrixRef.getVisibleRange() <= (Constance.RANGE_MAX+matrixRef.getStartRange())*Constance.RANGE_DISP)
		    	gc.strokeText(String.valueOf((int)(-totalVisibleHeight))+unit, OFFSET/2, matrixRef.getAZDrawableYArea());
	    }  */
        
      /*  gc.setFont(new Font("Sans Serif", 10));
        if(Constance.UNITS.isLogged==false){
	    String unit = Constance.UNITS.getHEIGHT();
	    if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))) {
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf(totalVisibleHeight)+unit,10,colorMirror), OFFSET/2, matrixRef.getAZDrawableYArea()*0.05);
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalVisibleHeight*0.5))+unit,10,colorMirror), OFFSET/2, matrixRef.getAZDrawableYArea()*0.29);
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalVisibleHeight*0))+unit,10,colorMirror), OFFSET/2, matrixRef.getAZDrawableYArea()*0.49);
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(-totalVisibleHeight*0.5))+unit,10,colorMirror), OFFSET/2, matrixRef.getAZDrawableYArea()*0.76);
	    	if(matrixRef.getVisibleRange() <= (Constance.RANGE_MAX+matrixRef.getStartRange())*Constance.RANGE_DISP)
	    		gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(-totalVisibleHeight))+unit,10,colorMirror), OFFSET/2, matrixRef.getAZDrawableYArea()-OFFSET);
	    	
	    	} else {
		    gc.strokeText(String.valueOf(totalVisibleHeight)+unit, OFFSET/2, matrixRef.getAZDrawableYArea()*0.05);
		    gc.strokeText(String.valueOf((int)(totalVisibleHeight*0.5))+unit, OFFSET/2, matrixRef.getAZDrawableYArea()*0.29);
		    gc.strokeText(String.valueOf((int)(totalVisibleHeight*0))+unit, OFFSET/2, matrixRef.getAZDrawableYArea()*0.49);//Zero value
		    gc.strokeText(String.valueOf((int)(-totalVisibleHeight*0.5))+unit, OFFSET/2, matrixRef.getAZDrawableYArea()*0.76);
		    if(matrixRef.getVisibleRange() <= (Constance.RANGE_MAX+matrixRef.getStartRange())*Constance.RANGE_DISP)
		    	gc.strokeText(String.valueOf((int)(-totalVisibleHeight))+unit, OFFSET/2, matrixRef.getAZDrawableYArea());
	    }  
	}else{
		 String unit = Constance.UNITS.getHEIGHT();
	     Point startPoint = matrixRef.toAzimuthRangePixels(midAzimuth, matrixRef.getMinRange());
	     double rangePix;
		  int j=0;
	        while(j<30){
	        	j=j+5;
	        	rangePix = matrixRef.toRangePixels(j);
	        	System.out.println("");
	        	Point yPixL = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),rangePix,-(10));
	        	Point yPixR = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),rangePix,(10));
	        	int height=(int) (((((j*Constance.UNITS.getR_PX()))*(Math.tan(Math.toRadians((Constance.AZIMUTH_MAX)))))*1000)*Constance.UNITS.getFACTOR_HEIGHT());
	        	 if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")) ) 
	              	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf(height)+unit,10,colorMirror), OFFSET/2, yPixL.getY());
	             else
	              	gc.strokeText(String.valueOf(height)+unit, OFFSET/2, yPixL.getY());
	        	 
	        	 if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))) 
		              	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf(-height)+unit,10,colorMirror), OFFSET/2, yPixR.getY());
		         else
		              	gc.strokeText(String.valueOf(-height)+unit, OFFSET/2, yPixR.getY());
	          }
	       } 	*/    
	}

	public void drawText(GraphicsContext gc) {
		int DPIX = 10;
		int OFFSET = 70;
		int scanOff = 4*OFFSET;
		if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))) {
			OFFSET = (int) matrixRef.getDrawableXArea() - 3*OFFSET;
			scanOff = (int) (OFFSET - scanOff*0.5);
		}
        gc.setFont(new Font("Sans Serif", 14));
        gc.setStroke(Color.GREENYELLOW);
        gc.strokeText("Azimuth Plane", matrixRef.getDrawableXArea()/2, matrixRef.getAZDrawableYArea()-10);

        gc.setFont(new Font("Sans Serif", 14));
        gc.setStroke(Color.RED);
        gc.setStroke(Color.YELLOW);
        gc.strokeText("AZ Angle    : "+(UserPreference.getInstance().getAZ_LSL_ANGLE()-UserPreference.getInstance().getAZ_RSL_ANGLE())+"º", OFFSET, matrixRef.getAZDrawableYArea()-DPIX);

        gc.setFont(new Font("Sans Serif", 14));
        gc.setStroke(Color.ALICEBLUE);
        DecimalFormat df = new DecimalFormat("0.000");
        gc.strokeText("Offset   : "+df.format(Constance.AZIMUTH.RCLO*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.getLENGTH(), scanOff, matrixRef.getAZDrawableYArea()-3*DPIX);
        gc.strokeText("Scan     : "+Constance.SCAN_AZ, scanOff, matrixRef.getAZDrawableYArea()-DPIX);
	}

}
