//*H============================================================================
//*M                      ATHINDRIYA SYSTEMS RESTRICTED
//*H============================================================================
//*H
//*S  $Id: $
//*H
//*C  COPYRIGHT
//*C  This software is copyrighted. It is the property of Athindriya Systems 
//*C  India which reserves all right and title to it. It must not be
//*C  reproduced, copied, published or released to third parties nor may the
//*C  content be disclosed to third parties without the prior written consent
//*C  of Athindriya Systems India. Offenders are liable to the payment of
//*C  damages. All rights reserved in the event of the granting of a patent or
//*C  the registration of a utility model or design.
//*C  (c) Athindriya Systems India 2015
//*H
//*H  Created: 24-Sep-2016
//*H
//*H  @author....: $Author: $Suraj
//*H  @date......: $Date: $
//*H  @version...: $Rev: $1.0
//*H  @path......: $URL: $
//*H
//*H============================================================================

package model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.drawable.Track;
import utils.Constance;

public class SketchPathOverlayEl {
	
	private static final Logger logger = Logger.getLogger(SketchPathOverlay.class);
	
	private int trackNo;
	private List<Track> pathListEl;
	private Color color;
	private int counter = 0;
	private boolean isAz = false;
	DataManager dataObserver = DataManager.getInstance();

	MatrixRef matrixRef;
	public SketchPathOverlayEl() {
		pathListEl = new ArrayList<Track>();
		matrixRef = MatrixRef.getInstance();
	}
	
	public void addTrackPathEl(Track track) {
		circualrAddTrackPath(counter++,track);
		if(counter == Constance.TailCounter)
			counter = 0;
	}
	
	private void circualrAddTrackPath(int location, Track track) {
		
		try{
		if(pathListEl.size() < Constance.TailCounter)
			pathListEl.add(location, track);
		else
			pathListEl.set(location, track);
		} catch (Exception e) {
			dataObserver.clearObseleteElPathList();
			logger.error(e);
		}
		
	}
	
	public void removeTrackPath(int location) {
		pathListEl.remove(location);
	}

	public int getTrackNo() {
		return trackNo;
	}

	public void setTrackNo(int trackNo) {
		this.trackNo = trackNo;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void clearPathHistory() {
		pathListEl.clear();
	}

	public boolean isAzEl() {
		return isAz;
	}

	public void setAzEl(boolean isAzEl) {
		this.isAz = isAzEl;
	}

	public void drawCompletePathEl(GraphicsContext gc) {
	
		if(counter >= 0) {
			int startIndexEl = counter;
			for(int i=0;i<pathListEl.size();i++) {
				Track tkPrevEl = null;
				if(i>0 && startIndexEl !=0 )
					tkPrevEl = pathListEl.get((startIndexEl-1)%pathListEl.size());
				Track tkCurrEl = pathListEl.get(startIndexEl%pathListEl.size());
				++startIndexEl;
				
		  if(Constance.UNITS.isKM==true){
					
			 if ((tkCurrEl.getRange() / 1000) <= MatrixRef.getInstance().getVisibleRange() && tkPrevEl !=null) {	
					
			    double y1 = 0;
			    y1 = tkPrevEl.getZ();
				if(color!=null)
					gc.setStroke(color);
				else
					gc.setStroke(Color.WHITE);
			    gc.strokeOval(tkPrevEl.getX(),y1-3,6,6);  
			    gc.setFill(Color.valueOf("#ff80b3"));   // Lower intensity red color
			    gc.fillOval(tkPrevEl.getX(),y1-3,5,5);
			    
				}
			}else
				{
					if ((tkCurrEl.getRange()*0.000536) <= MatrixRef.getInstance().getVisibleRange() && tkPrevEl !=null) {					
						
						double y1 = 0;
						y1 = tkPrevEl.getZ();
					
					    if(color!=null)
						   gc.setStroke(color);
					    else
						   gc.setStroke(Color.WHITE);
					
					gc.strokeOval(tkPrevEl.getX(),y1-3,6,6);
				    gc.setFill(Color.valueOf("#ff80b3"));
				    gc.fillOval(tkPrevEl.getX(),y1-3,5,5);
				}
			}
		 }
	  }
   }	
}
