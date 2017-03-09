package model;

import java.util.Iterator;
import javafx.scene.canvas.GraphicsContext;
import model.drawable.Plot;
import model.drawable.Track;
import model.drawable.Video;

import org.apache.log4j.Logger;

public class SketchItemizedOverlay extends ItemizedOverlay<OverlayItem> implements ILayoutParam {

	static final Logger logger = Logger.getLogger(SketchItemizedOverlay.class);
	
	private int cpiNo;

	public SketchItemizedOverlay(String name) {
		super(name);
	}

	public int getCpiNo() {
		return cpiNo;
	}

	public void setCpiNo(int cpiNo) {
		this.cpiNo = cpiNo;
	}

	public void drawTracks(GraphicsContext gc) {
		if (size() > 0) {
			for (int i = 0; i < size(); i++) {
				Track track = (Track) getOverlayItemList(i);
				if (track != null) {
					if (!track.isEvaluated())
						track.extractGraphAER();
					track.draw(gc);
				}
			}
		}
	}

	public void drawPlots(GraphicsContext gc) {
		if (size() > 0) {
			for (int i = 0; i < size(); i++) {
				Plot plot = (Plot) getOverlayItemList(i);//removeOverlayItem();
				if (plot != null) {
					plot.extractGraphAER();
					if (gc != null)
						plot.draw(gc);
				}
			}
		}
	}

	public void drawVideos(GraphicsContext gc) {
		if (size() > 0) {
			Iterator<OverlayItem> item = getOverlayIterator();
			while (item.hasNext()) {
				Video video = (Video) item.next();
				if (video != null) {
					video.evaluateXYZ();
					video.draw(gc);
				}
			}
		}
	}

	@Override
	public void draw(GraphicsContext gc) {

	}

}
