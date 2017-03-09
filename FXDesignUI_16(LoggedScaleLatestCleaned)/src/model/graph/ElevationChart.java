package model.graph;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import admin.UserPreference;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light.Point;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.GraphChart;
import model.MatrixRef;
import utils.Constance;
import utils.DrawingUtils;
import views.ResizableCanvas;

public class ElevationChart extends GraphChart{

	static final Logger logger = Logger.getLogger(ElevationChart.class);
	MatrixRef matrixRef = MatrixRef.getInstance();
	class myPoint {
		int X;
		int Y;
	}
	List<myPoint> crossPoints = new ArrayList<myPoint>();
	public ElevationChart(ResizableCanvas canvas) {
		super(canvas);
	}

	public void drawRadarLocation() {
		Point startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getMinRange()+Constance.UNITS.TDPzero());
		gc.setFill(Color.CORNFLOWERBLUE);
		gc.fillRoundRect(startPoint.getX(), startPoint.getY()-OFFSET/6, OFFSET, OFFSET, 2, 2);
	
	}

	public void drawElevationLine() {
        gc.setStroke(Color.CADETBLUE);
        gc.setLineWidth(1.0);
      
        Point  startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getMinRange()+Constance.UNITS.TDPzero());  //// 1 is added in place of matrixRef.getMinRange() because log(0) value is infinity,so in order to get value as 0 we are passing the parameter 1.i.e log(1)=0.  
        Point pointCross = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getDrawableXArea()+TEXT_OFFSET, -((Constance.ELEVATION.USL_ANGLE*Constance.ElEVATION_MUL)-2*Constance.ElEVATION_MUL));//cross line at 10 degrees
        Point pointFlat = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getDrawableXArea()+TEXT_OFFSET, -Constance.ELEVATION.LSL_ANGLE);//flat line
        gc.strokePolyline(new double[]{pointCross.getX(), startPoint.getX(), pointFlat.getX()},
                new double[]{pointCross.getY(), startPoint.getY(), pointFlat.getY()}, 3);

        //calculation done for finding the X,Y point for range markers
        for(double i= matrixRef.getMinRange();i<(matrixRef.getVisibleRange()+Constance.RANGE_DISP+1);i+=Constance.RANGE_DISP){
        	Point point = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), i+Constance.UNITS.TDPzero()); //Constance.UNITS.TDPzero() is added in order to make the Range scale line alignment properly in NM.
        	myPoint p = new myPoint();
        	p.X = (int) point.getX();
        	crossPoints.add(p);
        }
        //finding Y co-ordinates
        for(int i=0;i<matrixRef.getDrawableXArea()+TEXT_OFFSET;i++) {
        	pointCross = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),i, -((Constance.ELEVATION.USL_ANGLE*Constance.ElEVATION_MUL)-2*Constance.ElEVATION_MUL));//cross line at 20 degrees
        	int raX = (int) pointCross.getX();
        	for(int j=0;j<crossPoints.size();j++) {
        		if(raX == crossPoints.get(j).X){
        			int yVal = (int) pointCross.getY();
        			if(!Constance.UNITS.isKM)
        				yVal -= (TEXT_OFFSET+HGAP)/(matrixRef.getVisibleRange());
        			crossPoints.get(j).Y = yVal;
        			break;
        		}
        	}
        }

	}

	public void drawRedDistanceLine() {
		// For drawing decision height line
		Point startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getStartRange()+Constance.UNITS.TDPzero());
		// For drawing first line.
		double TdpRange = matrixRef.toRangePixels(0);
		double endRange = matrixRef.toRangePixels(Constance.RANGE_MAX*Constance.UNITS.getFACTOR_LENGTH()+ matrixRef.getStartRange());
		double range = matrixRef.toRangePixels((Constance.SafetyParam.DH1*Constance.UNITS.getFACTOR_LENGTH()));
		double rangeActual = range - TdpRange;
	    startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getStartRange()+Constance.UNITS.TDPzero());
		Point p = DrawingUtils.getNextPointAtAngle1(startPoint.getX(),startPoint.getY(),rangeActual, -Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL);
	
		// For drawing second line.
		double range2 = matrixRef.toRangePixels((Constance.SafetyParam.DH2*Constance.UNITS.getFACTOR_LENGTH()));
		rangeActual = range2-TdpRange;
	    startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getStartRange()+Constance.UNITS.TDPzero());
		Point p2 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(),startPoint.getY(),rangeActual, -Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL);
		
		// For drawing third line.
		double range3 = matrixRef.toRangePixels((Constance.SafetyParam.DH3*Constance.UNITS.getFACTOR_LENGTH()));
		rangeActual = range3 - TdpRange;
	    startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getStartRange()+Constance.UNITS.TDPzero());
		Point p3 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(),startPoint.getY(),rangeActual, -Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL);
	
		
	   for(int i=0;i<3;i++){
	    int height=0;
		if(i==0)	
		   height = (int) (((Constance.SafetyParam.DH1)*(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))*1000);
		else if(i==1)
		   height = (int) (((Constance.SafetyParam.DH2)*(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))*1000);
		else
			 height = (int) (((Constance.SafetyParam.DH3)*(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))*1000);
		height = (int)(height*Constance.UNITS.getFACTOR_HEIGHT());
		String height1 = " "+height;
		gc.setFont(new Font("Sans Serif", 10));
        gc.setLineWidth(1);
		java.awt.Color blue = new java.awt.Color(0, 225, 225, 255);
		if(i==0){
			 if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")))
        		 gc.drawImage(DrawingUtils.printMirrorImage(height1, 10,blue), 3*OFFSET, p.getY()-OFFSET);
			 else
		         gc.strokeText(height1, 3*OFFSET, p.getY());
		}
		else if(i==1){
			 if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")))
        		 gc.drawImage(DrawingUtils.printMirrorImage(height1, 10,blue), 5*OFFSET, p2.getY()-OFFSET);
			 else
		         gc.strokeText(height1, 5*OFFSET, p2.getY());
		}
		else{
			 if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")))
        		 gc.drawImage(DrawingUtils.printMirrorImage(height1, 10,blue), 7*OFFSET, p3.getY()-OFFSET);
			 else
		         gc.strokeText(height1, 7*OFFSET, p3.getY());
	        }
	  }
	    gc.setLineWidth(0.5);
		gc.setLineDashes(OFFSET/3);
	    gc.setStroke(Color.RED);
		gc.strokeLine(0,p.getY(),endRange,p.getY());
		gc.setStroke(Color.GREENYELLOW);
		gc.strokeLine(0,p2.getY(),endRange,p2.getY());
		gc.setStroke(Color.LIGHTPINK);
		gc.strokeLine(0,p3.getY(),endRange,p3.getY());

		gc.setLineDashes(0);

		/* if(Constance.UNITS.isLogged==false){
				// For Drawing 50ft below the glide line.
		        double d1=((Constance.SafetyParam.FirstSetHeight)/(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))/1000;
		        double R1,R2,dRange;
		        Point d2;
		        double SatartRangeX1=(matrixRef.getStartRange()+(d1*Constance.UNITS.getFACTOR_LENGTH()));
		        Point startpointx1=matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), SatartRangeX1+Constance.UNITS.TDPzero());
		        if(Constance.UNITS.isLogged){
		        	R1 = matrixRef.toRangePixels(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH());
		        	R2 = matrixRef.toRangePixels(d1*Constance.UNITS.getFACTOR_LENGTH());
		           dRange=(R1-R2);
		           
		        }else{
		            d2=matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), (((Constance.SafetyParam.FirstLineEnd-d1)*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero()));
		            dRange=d2.getX();
		        }
		        Point endPointx2=DrawingUtils.getNextPointAtAngle1(startpointx1.getX(),startpointx1.getY(),dRange, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));
		        gc.setStroke(Color.RED);
				gc.setLineWidth(0.7);
				gc.strokeLine(startpointx1.getX(),startpointx1.getY(),endPointx2.getX(),endPointx2.getY()); 
				
				
				 // For Drawing 50ft above the glide line.
				 double d_1=matrixRef.getStartRange()-d1*Constance.UNITS.getFACTOR_LENGTH();
				 double sp_1=matrixRef.toRangePixels(d_1+Constance.UNITS.TDPzero());
				 double ep2 = matrixRef.toRangePixels(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH()+matrixRef.getStartRange()+Constance.UNITS.TDPzero());
				 double hCalPix = ep2 - sp_1;
			     Point endPointx3=DrawingUtils.getNextPointAtAngle1(sp_1,startPoint.getY(),hCalPix, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));  
			     gc.setStroke(Color.RED);
				 gc.strokeLine(sp_1,startPoint.getY(),endPointx3.getX(),endPointx3.getY());
			 
			// For drawing 100ft below the glide line	 
		 double y1=(Constance.SafetyParam.FirstLineEnd*(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))*1000;
		 double y = y1-Constance.SafetyParam.SecondSetHeight;
		 double q = (y/Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE)))/1000;
		 double q_1 = Constance.SafetyParam.FirstLineEnd-q;
		 double startPoint_1 = matrixRef.toRangePixels((q_1*Constance.UNITS.getFACTOR_LENGTH()));
		 double startPoint_2 = matrixRef.toRangePixels((Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH()));
		 double r1pix = startPoint_2 - startPoint_1;
		 Point h1Pixel=DrawingUtils.getNextPointAtAngle1(startPoint_1,startPoint.getY(),r1pix, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL)); // added in order to find out the y1 value in pixel. 
		 double R1Pixel=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		 double z_1 = matrixRef.toRangePixels(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())-startPoint_1;
		 Point h2Pixel=DrawingUtils.getNextPointAtAngle1(startPoint_1,startPoint.getY(),z_1, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));  // added in order to find out the y2 value in pixel. 
		 double R2Pixel=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		 gc.setStroke(Color.SPRINGGREEN);
		 gc.strokeLine(R1Pixel,h1Pixel.getY(),R2Pixel,h2Pixel.getY());
		 
		 // For drawing 100ft above the glide line
		 double ya=y1+Constance.SafetyParam.SecondSetHeight;
		 double qa=(ya/Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE)))/1000;
		 double qa_1 = (Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH())-(qa*Constance.UNITS.getFACTOR_LENGTH());
		 double qa_1pix = matrixRef.toRangePixels((qa_1));
		 double r1pix_1=matrixRef.toRangePixels(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH())-qa_1pix;
		 Point h1Pixela=DrawingUtils.getNextPointAtAngle1(qa_1pix,startPoint.getY(),r1pix_1, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL)); // added in order to find out the y1 value in pixel. 
		 double R1Pixela=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		 double y2=((Constance.SafetyParam.SecondLineEnd)*(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))*1000;
		 double y2a=y2+Constance.SafetyParam.SecondSetHeight;
		 double q2a=(y2a/Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE)))/1000;
		 qa_1 = (Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())-(q2a*Constance.UNITS.getFACTOR_LENGTH());
		 r1pix_1=matrixRef.toRangePixels(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())-matrixRef.toRangePixels((qa_1));
		 Point h2Pixela=DrawingUtils.getNextPointAtAngle1(startPoint.getX(),startPoint.getY(),r1pix_1, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));  // added in order to find out the y2 value in pixel. 
		 double R2Pixela=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		 gc.setStroke(Color.SPRINGGREEN);
		 gc.strokeLine(R1Pixela,h1Pixela.getY(),R2Pixela,h2Pixela.getY());
	
		 // For drawing the line 500ft below from the glide line.
		 double y1fiveh = ((Constance.SafetyParam.SecondLineEnd)*(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))*1000;
		 double yfiveh = y1fiveh-Constance.SafetyParam.ThirdSetHeight;
		 double qfiveh = (yfiveh/Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE)))/1000;
		 double qfiveh_1 = (Constance.SafetyParam.SecondLineEnd) - qfiveh;
		 double startPoint_3 = matrixRef.toRangePixels((qfiveh_1*Constance.UNITS.getFACTOR_LENGTH()));
		 double startPoint_4 = matrixRef.toRangePixels((Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH()));
		 r1pix = startPoint_4 - startPoint_3;
		 Point h1Pixelfiveh = DrawingUtils.getNextPointAtAngle1(startPoint_3,startPoint.getY(),r1pix, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL)); // added in order to find out the y1 value in pixel. 
		 double R1Pixelfiveh = matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		 double  z_2 = matrixRef.toRangePixels(Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH())-startPoint_3;
		 Point h2Pixelfiveh = DrawingUtils.getNextPointAtAngle1(startPoint_3,startPoint.getY(),z_2, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));  // added in order to find out the y2 value in pixel. 
		 double R2Pixelfiveh = matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		 gc.setStroke(Color.FUCHSIA);
		 gc.strokeLine(R1Pixelfiveh,h1Pixelfiveh.getY(),R2Pixelfiveh,h2Pixelfiveh.getY());
		 
		 // For drawing 500ft above the glide line
		 double yafiveh=y1fiveh+Constance.SafetyParam.ThirdSetHeight;
		 double qafiveh=(yafiveh/Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE)))/1000;
		 double r1pixafiveh=matrixRef.toRangePixels((qafiveh*Constance.UNITS.getFACTOR_LENGTH()+Constance.UNITS.TDPzero()));
		 Point h1Pixelafiveh=DrawingUtils.getNextPointAtAngle1(startPoint.getX(),startPoint.getY(),r1pixafiveh, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL)); // added in order to find out the y1 value in pixel. 
		 double R1Pixelafiveh=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		 double y2fiveh=((Constance.SafetyParam.ThirdLineEnd)*(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))*1000;
		 double y2afiveh=y2fiveh+Constance.SafetyParam.ThirdSetHeight;
		 double q2afiveh=(y2afiveh/Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE)))/1000;
		 double r2pixelafiveh=matrixRef.toRangePixels((q2afiveh*Constance.UNITS.getFACTOR_LENGTH()));
		 Point h2Pixelafiveh=DrawingUtils.getNextPointAtAngle1(startPoint.getX(),startPoint.getY(),r2pixelafiveh, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));  // added in order to find out the y2 value in pixel. 
		 double R2Pixelafive=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		 gc.setStroke(Color.FUCHSIA);
		 gc.strokeLine(R1Pixelafiveh,h1Pixelafiveh.getY(),R2Pixelafive,h2Pixelafiveh.getY());
		 }else{*/
			 
		  // For Drawing 50ft below the glide line.
			 Point startPointFix = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getStartRange()+Constance.UNITS.TDPzero());
		     double d1=((Constance.SafetyParam.FirstSetHeight)/(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))/1000;
		     double SatartRangeX1=(matrixRef.getStartRange()+(d1*Constance.UNITS.getFACTOR_LENGTH()));
		     Point startpointx1=matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), SatartRangeX1+Constance.UNITS.TDPzero());
		     double rRd = ((Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH())+matrixRef.getStartRange()+Constance.UNITS.TDPzero());
		     double rRdPix = matrixRef.toRangePixels(rRd);
		     double ractPix = rRdPix - startpointx1.getX();
		     Point h_0pixY = DrawingUtils.getNextPointAtAngle1(startPointFix.getX(),startPointFix.getY(),ractPix, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));
		     double endPointx = matrixRef.toRangePixels(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH()+matrixRef.getStartRange()+Constance.UNITS.TDPzero());
			 gc.setLineWidth(0.7);
			 gc.setStroke(Color.RED);
			 gc.strokeLine(startpointx1.getX(),startpointx1.getY(),endPointx,h_0pixY.getY()); 
			
				 // For Drawing 50ft above the glide line.
			 double d_1=matrixRef.getStartRange()-d1*Constance.UNITS.getFACTOR_LENGTH();
			 double sp_1=matrixRef.toRangePixels(d_1+Constance.UNITS.TDPzero());
			 double rdPix = matrixRef.toRangePixels(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH());
			 double tdpPix = matrixRef.toRangePixels(0);
			 double Pix = rdPix-tdpPix;
			 Point pixThree = DrawingUtils.getNextPointAtAngle1(startPoint.getX(),startPoint.getY(),Pix, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));
			 double increasePix = pixThree.getY()-h_0pixY.getY();
			 gc.setStroke(Color.RED);
			 gc.strokeLine(sp_1,startPoint.getY(),endPointx,pixThree.getY()+increasePix);
			   
			// For drawing 100ft below the glide line
			 double y1=(Constance.SafetyParam.FirstLineEnd*(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))*1000;
			 double y = y1-Constance.SafetyParam.SecondSetHeight;
			 double q = (y/Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE)))/1000;
			 double q_1 = Constance.SafetyParam.FirstLineEnd-q;
			 double startPoint_1 = matrixRef.toRangePixels((q_1*Constance.UNITS.getFACTOR_LENGTH()));
			 double startPoint_2 = matrixRef.toRangePixels((Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH()));
			 double r1pix = startPoint_2 - startPoint_1;
			 Point h1Pixel=DrawingUtils.getNextPointAtAngle1(startPoint_1,startPoint.getY(),r1pix, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL)); // added in order to find out the y1 value in pixel. 
			 double R1Pixel=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
			 double z_1 = matrixRef.toRangePixels(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())-startPoint_1;
			 Point h2Pixel=DrawingUtils.getNextPointAtAngle1(startPoint_1,startPoint.getY(),z_1, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));  // added in order to find out the y2 value in pixel. 
			 double R2Pixel=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
			 gc.setStroke(Color.SPRINGGREEN);
			 gc.strokeLine(R1Pixel,h1Pixel.getY(),R2Pixel,h2Pixel.getY());
			 
			 // For drawing 100ft above the glide line
			 rdPix = matrixRef.toRangePixels(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH());
			 tdpPix = matrixRef.toRangePixels(0);
			 Pix = rdPix-tdpPix;
			 pixThree = DrawingUtils.getNextPointAtAngle1(startPoint_1,startPoint.getY(),Pix, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));
			 increasePix = pixThree.getY()-h1Pixel.getY();
			 rdPix = matrixRef.toRangePixels(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH());
			 tdpPix = matrixRef.toRangePixels(0);
			 Pix = rdPix-tdpPix;
			 Point pixThree2 = DrawingUtils.getNextPointAtAngle1(startPoint_1,startPoint.getY(),Pix, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));
			 double increasePix2 = pixThree2.getY() - h2Pixel.getY();
			 double R1Pixela=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
			 double R2Pixela=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
			 gc.setStroke(Color.SPRINGGREEN);
			 gc.strokeLine(R1Pixela,pixThree.getY()+increasePix,R2Pixela,pixThree2.getY()+increasePix2);
			 
			 // For drawing the line 500ft below from the glide line.
			 double y1fiveh = ((Constance.SafetyParam.SecondLineEnd)*(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))*1000;
			 double yfiveh = y1fiveh-Constance.SafetyParam.ThirdSetHeight;
			 double qfiveh = (yfiveh/Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE)))/1000;
			 double qfiveh_1 = (Constance.SafetyParam.SecondLineEnd) - qfiveh;
			 double startPoint_3 = matrixRef.toRangePixels((qfiveh_1*Constance.UNITS.getFACTOR_LENGTH()));
			 double startPoint_4 = matrixRef.toRangePixels((Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH()));
			 r1pix = startPoint_4 - startPoint_3;
			 Point h1Pixelfiveh = DrawingUtils.getNextPointAtAngle1(startPoint_3,startPoint.getY(),r1pix, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL)); // added in order to find out the y1 value in pixel. 
			 double R1Pixelfiveh = matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
			 double  z_2 = matrixRef.toRangePixels(Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH())-startPoint_3;
			 Point h2Pixelfiveh = DrawingUtils.getNextPointAtAngle1(startPoint_3,startPoint.getY(),z_2, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));  // added in order to find out the y2 value in pixel. 
			 double R2Pixelfiveh = matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
			 gc.setStroke(Color.FUCHSIA);
			 gc.strokeLine(R1Pixelfiveh,h1Pixelfiveh.getY(),R2Pixelfiveh,h2Pixelfiveh.getY());
			 
			 
			// For drawing the line 500ft above from the glide line.
			 rdPix = matrixRef.toRangePixels(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH());
			 tdpPix = matrixRef.toRangePixels(0);
			 Pix = rdPix-tdpPix;
			 pixThree = DrawingUtils.getNextPointAtAngle1(startPoint_1,startPoint.getY(),Pix, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));
			 increasePix = pixThree.getY()-h1Pixelfiveh.getY();
			 rdPix = matrixRef.toRangePixels(Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH());
			 Pix = rdPix-tdpPix;
			 pixThree2 = DrawingUtils.getNextPointAtAngle1(startPoint_1,startPoint.getY(),Pix, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));
			 increasePix2 = pixThree2.getY() - h2Pixelfiveh.getY();
			 R1Pixela=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.FirstLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		     R2Pixela=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		     double R1Pixelafiveh=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		     double R2Pixelafive=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
			 gc.setStroke(Color.FUCHSIA);
			 gc.strokeLine(R1Pixelafiveh,pixThree.getY()+increasePix,R2Pixelafive,pixThree2.getY()+increasePix2);
		// }
		 
		 // Accurate measurment
		/* double yafiveh=y1fiveh+Constance.SafetyParam.ThirdSetHeight;
		 double qafiveh=(yafiveh/Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE)))/1000;
		 qa_1 = (Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())-(qafiveh*Constance.UNITS.getFACTOR_LENGTH());
		 qa_1pix = matrixRef.toRangePixels((qa_1));
		 r1pix_1=matrixRef.toRangePixels(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())-qa_1pix;
		 h1Pixela=DrawingUtils.getNextPointAtAngle1(qa_1pix,startPoint.getY(),r1pix_1, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL)); // added in order to find out the y1 value in pixel. 
		 double R1Pixelafiveh=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.SecondLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		 y2=((Constance.SafetyParam.ThirdLineEnd)*(Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE))))*1000;
		 y2a=y2+Constance.SafetyParam.ThirdSetHeight;
		 q2a=(y2a/Math.tan(Math.toRadians(Constance.ELEVATION.GLIDE_ANGLE)))/1000;
		 qa_1 = (Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH())-(q2a*Constance.UNITS.getFACTOR_LENGTH());
		 r1pix_1=matrixRef.toRangePixels(Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH())-matrixRef.toRangePixels((qa_1));
		 h2Pixela=DrawingUtils.getNextPointAtAngle1(startPoint.getX(),startPoint.getY(),r1pix_1, -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));  // added in order to find out the y2 value in pixel. 
		 double R2Pixelafive=matrixRef.toRangePixels((matrixRef.getStartRange()+(Constance.SafetyParam.ThirdLineEnd*Constance.UNITS.getFACTOR_LENGTH())+Constance.UNITS.TDPzero())); // added in order to find out the x1 pixels.
		 gc.setStroke(Color.FUCHSIA);
		 gc.strokeLine(R1Pixelafiveh,h1Pixela.getY(),R2Pixelafive,h2Pixela.getY());*/
		 
		 	 
		//write text TD
		gc.setStroke(Color.YELLOW);
		gc.setLineWidth(1.5);
		if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")))
    		gc.drawImage(DrawingUtils.printMirrorImage(" TDP ",12,java.awt.Color.YELLOW), startPoint.getX()-HGAP, startPoint.getY()+OFFSET);
		else
    		gc.strokeText(" TDP ", startPoint.getX()-OFFSET, startPoint.getY()+HGAP);

	}

	public void drawDistanceGrid() {

		gc.setLineWidth(1.5);
        //draw remaining lines
		Point endPoint = new Point();
        for(double i=matrixRef.getStartRange();i<matrixRef.getVisibleRange()+Constance.RANGE_DISP+1;i+=Constance.RANGE_DISP){	
        	Point startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), i+Constance.UNITS.TDPzero()); //Constance.UNITS.TDPzero() is added in order to make the Range scale line alignment properly in NM.
        	
        	endPoint.setX(crossPoints.get((int) i).X);
        	endPoint.setY(crossPoints.get((int) i).Y);
        	
        	int val = (int) i;
        	if(val>matrixRef.getStartRange())
        		val = (int) (i - matrixRef.getStartRange());	
        	if (i==matrixRef.getStartRange()) 
        		gc.setStroke(Color.YELLOW);
        	else if((((val%5)==0)&&(val!=0)) || (val==2)) {
        		//draw yellow lines
        		gc.setLineWidth(0.5);
        		gc.setStroke(Color.YELLOW);
        		if(Constance.UNITS.isKM)
        		  gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endPoint.getY());
        		else{
        			if((Constance.DisplayScale==20)||(Constance.DisplayScale==30))
        		       gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endPoint.getY()-10);
        			else if((Constance.DisplayScale==10))
        			   gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endPoint.getY()-34);
        			else if((Constance.DisplayScale==5))
        			   gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endPoint.getY()-54);
        			else
        				 gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endPoint.getY()-10);
        		     }
        	 } else if(Constance.SHOW_RANGE_MARKS){
        		gc.setLineWidth(0.5);
            	gc.setStroke(Color.GREEN);
            	if(Constance.UNITS.isKM)
          		  gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endPoint.getY());
          		else{
          			if((Constance.DisplayScale==20)||(Constance.DisplayScale==30))
          		       gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endPoint.getY()-10);
          			else if((Constance.DisplayScale==10))
         			   gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endPoint.getY()-34);
         			else if((Constance.DisplayScale==5))
         			   gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endPoint.getY()-54);
         			else
         				gc.strokeLine(startPoint.getX(),startPoint.getY(),startPoint.getX(),endPoint.getY()-10);
         			
          		}
        	}
        }
	}

	public void drawLandingStrip() {
		 //center line or glide line
		double centerDist =(Constance.ELEVATION.GLIDE_FLAT_START_DIST);   //-(5*Constance.TDPzero);
		double maxDist = Constance.ELEVATION.GLIDE_MAX_DIST+Constance.RANGE_DISP+Constance.UNITS.TDPzero();
		gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        Point startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getStartRange()+Constance.UNITS.TDPzero());
        Point endPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), centerDist);
        double endActal = endPoint.getX() - startPoint.getX();
        DrawingUtils.drawLineAtAngle(gc, startPoint.getX(), startPoint.getY(), (Constance.UNITS.isLogged?endActal:endPoint.getX()), -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));//center red line
        Point point = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(), (Constance.UNITS.isLogged?endActal:endPoint.getX()), -(Constance.ELEVATION.GLIDE_ANGLE*Constance.ElEVATION_MUL));
        gc.strokeLine(point.getX(),point.getY(),matrixRef.toRangePixels(maxDist),point.getY());//center flat line  
	}

	public void drawUnitsText(boolean bool) {

		//Unit check
		gc.setStroke(Color.TRANSPARENT);
		java.awt.Color colorMirror = new java.awt.Color(0f,0f,0f,1f );

		if(bool) {
			gc.setStroke(Color.YELLOW);
			colorMirror = java.awt.Color.YELLOW;
		}
		gc.setFont(new Font("Sans Serif", 12));
        gc.setLineWidth(1);

        //X - AXIS UNITS
        int rangeCounter =0; 
    	String val = null;
        for(double i= matrixRef.getStartRange();i<matrixRef.getVisibleRange()+Constance.RANGE_DISP+1;i+=Constance.RANGE_DISP){

        	Point startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), i+Constance.UNITS.TDPzero());
        	val = (rangeCounter++)+"";
        	if(i==matrixRef.getVisibleRange())
        		val = val +" _";

        	int rngVal = rangeCounter;
        	if(rngVal>1)
        		rngVal -= (int)Constance.RANGE_DISP;
        	if(matrixRef.getVisibleRange()<=20*Constance.RANGE_DISP*Constance.UNITS.getFACTOR_LENGTH()) {
        		//write text Range
        		if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")) )
	        		gc.drawImage(DrawingUtils.printMirrorImage(val,12,colorMirror), startPoint.getX()-3*HGAP, startPoint.getY()+OFFSET);
        		else
            		gc.strokeText(val, startPoint.getX()-OFFSET, startPoint.getY()+HGAP);
        	} else if(((rngVal%5)==0)||(rngVal==2)) {
        		//write text Range
        		if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")) )
	        		gc.drawImage(DrawingUtils.printMirrorImage(val,12,colorMirror), startPoint.getX()-3*HGAP, startPoint.getY()+OFFSET);
        		else
            		gc.strokeText(val, startPoint.getX()-OFFSET, startPoint.getY()+HGAP);
        	}
        }

        //Y - AXIS UNITS
     /*   gc.setFont(new Font("Sans Serif", 10));
        double tHeight = ((matrixRef.getVisibleRange()*Constance.UNITS.getR_PX()) * Math.tan(Math.toRadians(Constance.ELEVATION_MAX-Constance.ElEVATION_MUL)));
        int totalHeight;
        if(Constance.isMSL==true){
        	totalHeight = (int) ((Math.ceil(tHeight * 1000 * Constance.UNITS.getFACTOR_HEIGHT()) + Integer.parseInt(Constance.GENERAL_MSL)));
        }
        else{
        	totalHeight = (int) ((Math.ceil(tHeight * 1000 * Constance.UNITS.getFACTOR_HEIGHT())));
        }
        String unit = Constance.UNITS.getHEIGHT();
        Point startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getMinRange());
        Point pointCross = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*1.45);//cross line at 10 degrees
        Point pointCross8 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*1.15);
        Point pointCross6 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*0.85);
        Point pointCross4 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*0.55);
        Point pointCross2 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*0.25);
        Point pointCross01 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*0);
        if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")) ) {
        	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf(totalHeight)+unit,10,colorMirror), OFFSET/2, pointCross.getY());
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalHeight*0.8))+unit,10,colorMirror), OFFSET/2, pointCross8.getY());
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalHeight*0.6))+unit,10,colorMirror), OFFSET/2, pointCross6.getY());
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalHeight*0.4))+unit,10,colorMirror), OFFSET/2, pointCross4.getY());
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalHeight*0.2))+unit,10,colorMirror), OFFSET/2, pointCross2.getY());
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalHeight*0))+unit,10,colorMirror), OFFSET/2, pointCross01.getY());
        } else {
	        gc.strokeText(String.valueOf(totalHeight*1)+unit, OFFSET/2, pointCross.getY());
	        gc.strokeText(String.valueOf((int)(totalHeight*0.8))+unit, OFFSET/2, pointCross8.getY());
	        gc.strokeText(String.valueOf((int)(totalHeight*0.6))+unit, OFFSET/2, pointCross6.getY());
	        gc.strokeText(String.valueOf((int)(totalHeight*0.4))+unit, OFFSET/2, pointCross4.getY());
	        gc.strokeText(String.valueOf((int)(totalHeight*0.2))+unit, OFFSET/2, pointCross2.getY());
	        gc.strokeText(String.valueOf((int)(totalHeight*0))+unit, OFFSET/2, pointCross01.getY()+(OFFSET/2));//Zero value
	        
        }*/
     /*   gc.setFont(new Font("Sans Serif", 10));
        if(Constance.UNITS.isLogged==false){
        double tHeight = ((matrixRef.getVisibleRange()*Constance.UNITS.getR_PX()) * Math.tan(Math.toRadians(Constance.ELEVATION_MAX-Constance.ElEVATION_MUL)));
       // int totalHeight = (int) ((Math.ceil(tHeight * 1000 * Constance.UNITS.getFACTOR_HEIGHT()) + Integer.parseInt(Constance.GENERAL_MSL)));
        int totalHeight;
        if(Constance.isMSL==true){
        	totalHeight = (int) ((Math.ceil(tHeight * 1000 * Constance.UNITS.getFACTOR_HEIGHT()) + Integer.parseInt(Constance.GENERAL_MSL)));
        }
        else{
        	totalHeight = (int) ((Math.ceil(tHeight * 1000 * Constance.UNITS.getFACTOR_HEIGHT())));
        }
        String unit = Constance.UNITS.getHEIGHT();
        Point startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getMinRange());
        Point pointCross = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*1.45);//cross line at 10 degrees
        Point pointCross8 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*1.15);
        Point pointCross6 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*0.85);
        Point pointCross4 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*0.55);
        Point pointCross2 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*0.25);
        Point pointCross01 = DrawingUtils.getNextPointAtAngle1(startPoint.getX(), startPoint.getY(),matrixRef.getActualXdimen(), -Constance.ELEVATION.USL_ANGLE*0);
        if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")) ) {
        	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf(totalHeight)+unit,10,colorMirror), OFFSET/2, pointCross.getY());
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalHeight*0.8))+unit,10,colorMirror), OFFSET/2, pointCross8.getY());
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalHeight*0.6))+unit,10,colorMirror), OFFSET/2, pointCross6.getY());
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalHeight*0.4))+unit,10,colorMirror), OFFSET/2, pointCross4.getY());
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalHeight*0.2))+unit,10,colorMirror), OFFSET/2, pointCross2.getY());
	    	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf((int)(totalHeight*0))+unit,10,colorMirror), OFFSET/2, pointCross01.getY());
        } else {
	        gc.strokeText(String.valueOf(totalHeight*1)+unit, OFFSET/2, pointCross.getY());
	        gc.strokeText(String.valueOf((int)(totalHeight*0.8))+unit, OFFSET/2, pointCross8.getY());
	        gc.strokeText(String.valueOf((int)(totalHeight*0.6))+unit, OFFSET/2, pointCross6.getY());
	        gc.strokeText(String.valueOf((int)(totalHeight*0.4))+unit, OFFSET/2, pointCross4.getY());
	        gc.strokeText(String.valueOf((int)(totalHeight*0.2))+unit, OFFSET/2, pointCross2.getY());
	        gc.strokeText(String.valueOf((int)(totalHeight*0))+unit, OFFSET/2, pointCross01.getY()+(OFFSET/2));//Zero value
       
        }
     }else{
        String unit = Constance.UNITS.getHEIGHT();
        Point startPoint = matrixRef.toElevationRangePixels(matrixRef.getMinElevation(), matrixRef.getMinRange());
        double rangePix;
        if(Constance.UNITS.isKM){
        for(int i =1; i<5; i++){
        	rangePix = matrixRef.toRangePixels(i);
        	Point yPix = DrawingUtils.getNextPointAtAngle(startPoint.getX(), startPoint.getY(),rangePix,-(16));
        	int height=(int) (((((i*Constance.UNITS.getR_PX())+Constance.TOUCH_DOWN)*(Math.tan(Math.toRadians((Constance.ELEVATION.USL_ANGLE-Constance.ElEVATION_MUL)))))*1000)*Constance.UNITS.getFACTOR_HEIGHT());
        	 if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")) ) 
             	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf(height)+unit,10,colorMirror), OFFSET/2, yPix.getY());
             else
             	gc.strokeText(String.valueOf(height)+unit, OFFSET/2, yPix.getY());
        }
        int j=0;
        while(j<30){
        	j=j+5;
        	rangePix = matrixRef.toRangePixels(j);
        	Point yPix = DrawingUtils.getNextPointAtAngle(startPoint.getX(), startPoint.getY(),rangePix,-(16));
        	int height=(int) (((((j*Constance.UNITS.getR_PX())+Constance.TOUCH_DOWN)*(Math.tan(Math.toRadians((Constance.ELEVATION.USL_ANGLE-Constance.ElEVATION_MUL)))))*1000)*Constance.UNITS.getFACTOR_HEIGHT());
        	 if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")) ) 
              	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf(height)+unit,10,colorMirror), OFFSET/2, yPix.getY());
              else
              	gc.strokeText(String.valueOf(height)+unit, OFFSET/2, yPix.getY());
          }
       } else{
    	   for(int i =1; i<16; i++){
           	rangePix = matrixRef.toRangePixels(i);
           	Point yPix = DrawingUtils.getNextPointAtAngle(startPoint.getX(), startPoint.getY(),rangePix,-(16));
           	int height=(int) (((((i*Constance.UNITS.getR_PX())+Constance.TOUCH_DOWN)*(Math.tan(Math.toRadians((Constance.ELEVATION.USL_ANGLE-Constance.ElEVATION_MUL)))))*1000)*Constance.UNITS.getFACTOR_HEIGHT());
           	 if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")) ) 
                	gc.drawImage(DrawingUtils.printMirrorImage(String.valueOf(height)+unit,10,colorMirror), OFFSET/2, yPix.getY());
             else
                	gc.strokeText(String.valueOf(height)+unit, OFFSET/2, yPix.getY());
           }
        }
      }*/

	}

	public void drawText(GraphicsContext gc) {
		int count = 0;
		int OFFSET = 70;

		if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))) //{
			 OFFSET = (int) matrixRef.getDrawableXArea() - 3*OFFSET;
		
			    gc.setFont(new Font("Sans Serif", 14));
		        gc.setStroke(Color.GREENYELLOW);

		        gc.setFont(new Font("Sans Serif", 14));
		        gc.setStroke(Color.RED);
		        gc.strokeText("EL Tilt       : "+UserPreference.getInstance().getEL_TILT()+"º", OFFSET, TEXT_OFFSET+HGAP*count);
		        count++;
		        gc.setStroke(Color.YELLOW);
		        gc.strokeText("El Angle    : +"+UserPreference.getInstance().getEL_USL_ANGLE()+"º", OFFSET, TEXT_OFFSET+HGAP*count);
		        count++;

		        gc.setFont(new Font("Sans Serif", 14));
		        gc.setStroke(Color.ALICEBLUE);
		        gc.strokeText("Glide Slope      : "+Constance.ELEVATION.GLIDE_ANGLE+"º", OFFSET, TEXT_OFFSET+HGAP*count);
		       
		        gc.setFont(new Font("Sans Serif", 14));
		        gc.setStroke(Color.CADETBLUE);
		        count = 0;
		        OFFSET = 15;
		        String chId = "?";
		        if(Constance.CHANNEL_ID > 0)
		        	chId = ""+ Constance.CHANNEL_ID;
		        gc.strokeText("Channel    : "+chId,((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))?2.7:0.75)*OFFSET*HGAP, TEXT_OFFSET+HGAP*count);
		        count++;
		        gc.strokeText("Control    : "+Constance.getCONTROL(), ((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))?2.7:0.75)*OFFSET*HGAP, TEXT_OFFSET+HGAP*count);
		        count++;
		        gc.strokeText("RWY        : "+adjustRunayVal(), ((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))?2.7:0.75)*OFFSET*HGAP, TEXT_OFFSET+HGAP*count);
		        count++;
		        gc.strokeText("Scale      : "+(Constance.SCALE), ((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))?2.7:0.75)*OFFSET*HGAP, TEXT_OFFSET+HGAP*count);
		        count++;
		        
		        gc.setFont(new Font("Sans Serif", 14));
		        gc.setStroke(Color.GREENYELLOW);
		        gc.strokeText("Wind Speed        : "+Constance.WIND_SPEED, ((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))?2.7:0.75)*OFFSET*HGAP, TEXT_OFFSET+HGAP*count);
		        count++;
		        gc.strokeText("Wind Direction    : "+Constance.WIND_DIR, ((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))?2.7:0.75)*OFFSET*HGAP, TEXT_OFFSET+HGAP*count);
		        count = 0;
		        gc.strokeText("Latitude        : "+Constance.LATITUDE, ((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))?2.2:1.2)*OFFSET*HGAP, TEXT_OFFSET+HGAP*count);
		        count++;
		        gc.strokeText("Longitude     : "+Constance.LONGITUDE, ((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))?2.2:1.2)*OFFSET*HGAP, TEXT_OFFSET+HGAP*count);
		        count++;
		        gc.strokeText("GPS Time     : "+Constance.GPS_TIME, ((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))?2.2:1.2)*OFFSET*HGAP, TEXT_OFFSET+HGAP*count);

		        gc.setStroke(Color.ALICEBLUE);
		        count++;
		        gc.strokeText("Scan       : "+Constance.SCAN_EL, ((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))?2.2:1.2)*OFFSET*HGAP, TEXT_OFFSET+HGAP*count);

		        gc.setFont(new Font("Sans Serif", 14));
		        gc.setStroke(Color.GREENYELLOW);
		        gc.strokeText("Elevation Plane",((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))?(matrixRef.getDrawableXArea()/(2.0)):matrixRef.getDrawableXArea()/(2.0)) , TEXT_OFFSET);
	
		}
	
	// For displaying actual Run way numbers instead of 1,2,3,4.
	private String adjustRunayVal(){
		String run="1";
		if(Constance.PREF.SEL_RUNWAY==" 1 "){
			run=" 09 ";
		}
		else if(Constance.PREF.SEL_RUNWAY==" 2 "){
			run=" 09 ";
		}
		else if(Constance.PREF.SEL_RUNWAY==" 3 "){
			run=" 27 ";
		}
		else if(Constance.PREF.SEL_RUNWAY==" 4 "){
			run=" 27 ";
		}
		else{
			run=" 09 ";
		}
		return run;
	}
}