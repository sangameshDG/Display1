package model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.drawable.Track;
import utils.Constance;

import org.apache.log4j.Logger;

public class SketchPathOverlay {

	private static final Logger logger = Logger.getLogger(SketchPathOverlay.class);
	
	private int trackNo;
	private List<Track> pathList;
	private Color color;
	private int counter = 0;
	private boolean isAz = false;
	DataManager dataObserver = DataManager.getInstance();

	MatrixRef matrixRef;
	public SketchPathOverlay() {
		pathList = new ArrayList<Track>();
		matrixRef = MatrixRef.getInstance();
	}
	
	public void addTrackPath(Track track) {
		circualrAddTrackPath(counter++,track);
		if(counter == Constance.TailCounter)
			counter = 0;
	   }
	
	private void circualrAddTrackPath(int location, Track track) {
		try{
			if(pathList.size() < Constance.TailCounter)
				pathList.add(location, track);
			else
				pathList.set(location, track);
			} catch (Exception e) {
				dataObserver.clearObseleteAzPathList();
				pathList.clear();
				logger.error(e);
			}
	}
	
	public void removeTrackPath(int location) {
		pathList.remove(location);
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
		pathList.clear();
	}

	public boolean isAzEl() {
		return isAz;
	}

	public void setAzEl(boolean isAzEl) {
		this.isAz = isAzEl;
	}

	public void drawCompletePath(GraphicsContext gc) {

		if(counter  >= 0) {                            // Add (counter >= 0) in order to not refresh tail for every 30 scans
			int startIndex = counter;
			for(int i=0;i<pathList.size();i++) {
				Track tkPrev = null;
				if(i>0 && startIndex !=0 )
					tkPrev = pathList.get((startIndex-1)%pathList.size());
				Track tkCurr = pathList.get(startIndex%pathList.size());
				++startIndex;
				if(Constance.UNITS.isKM==true){
				if ((tkCurr.getRange() / 1000) <= MatrixRef.getInstance().getVisibleRange() && tkPrev !=null) {					
					double y1 = 0;
						y1 = tkPrev.getY();
					if(color!=null)
						gc.setStroke(color);
					else
						gc.setStroke(Color.WHITE);
					
				    gc.strokeOval(tkPrev.getX(),y1-3,6,6);
				    gc.setFill(Color.valueOf("#ff80b3"));
				    gc.fillOval(tkPrev.getX(),y1-3,5,5);
				}
			}
				else
				{
					if ((tkCurr.getRange()*0.000536) <= MatrixRef.getInstance().getVisibleRange() && tkPrev !=null) {					
					
					double y1 = 0;
						y1 = tkPrev.getY();
					
					if(color!=null)
						gc.setStroke(color);
					else
						gc.setStroke(Color.WHITE);
					
					gc.strokeOval(tkPrev.getX(),y1-3,6,6);
				    gc.setFill(Color.valueOf("#ff80b3"));
				    gc.fillOval(tkPrev.getX(),y1-3,5,5);

				 }
			}
		}
	}
  }
}
