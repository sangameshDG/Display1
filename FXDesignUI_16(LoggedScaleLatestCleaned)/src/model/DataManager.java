package model;

import java.util.ArrayList;

import java.util.List;

import java.util.Map;

import java.util.Map.Entry;

import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.canvas.GraphicsContext;

import javafx.scene.effect.Light.Point;

import javafx.scene.paint.Color;

import javax.swing.SwingWorker;

import messages.radar.AzimuthPlaneDetectionPlotMsg;

import messages.radar.AzimuthPlanePlotsPerCPIMsg;

import messages.radar.AzimuthPlaneTrackMsg;

import messages.radar.ElevationPlaneDetectionPlotMsg;

import messages.radar.ElevationPlanePlotsPerCPIMsg;

import messages.radar.ElevationPlaneTrackMsg;

import messages.radar.PlaneRAWVideoMsg;
import model.drawable.Plot;

import model.drawable.Track;

import model.drawable.Video;

import org.apache.log4j.Logger;

import utils.Constance;

import application.TrackOptionController;
import application.TrackOptionControllerEl;

public class DataManager {

        private static final Logger logger = Logger.getLogger(DataManager.class);

        private static DataManager instance = null;
        

        private SketchItemizedOverlay mAzTrackList;

        private Map<Integer, TrackAttr> mAzTrackSetMap;
        
        private int azTrackCounter;

        private List<SketchPathOverlay> mAzSketchPathOverlayList;

        private SketchItemizedOverlay mAzPlotList;
        
        private List<Integer> mAzPrevScanTrackIdList;
        
        

        private SketchItemizedOverlay mElTrackList;

        private Map<Integer, TrackAttr> mElTrackSetMap;

        private int elTrackCounter;

        private List<SketchPathOverlayEl> mElSketchPathOverlayList;

        private SketchItemizedOverlay mElPlotList;
        
        private List<Integer> mElPrevScanTrackIdList;
        
        

        private List<SketchItemizedOverlay> mAzFullVideoList;

        private List<SketchItemizedOverlay> mElFullVideoList;
        

        private SketchItemizedOverlay mZoomTrackList;

        private SketchItemizedOverlay mZoomPlotList;

        private SketchItemizedOverlay mZoomVideoList;
        
        

        private boolean isAzTrackAdded = false;

        private boolean isAzPlotAdded = false;

        private boolean isElTrackAdded = false;

        private boolean isElPlotAdded = false;

        private boolean isAzVideoAdded = false;

        private boolean isElVideoAdded = false;
        
        

        public synchronized static DataManager getInstance() {

                if (instance == null) {

                        instance = new DataManager();

                        logger.info("Data Observer Instantiated");

                }

                return instance;

        }

        public static DataManager newInstance() {

                instance = new DataManager();

                logger.info("Data Observer Instantiated");

                return instance;

        }

        public static class TrackAttr {

                int trackIndex;

                Point labelPoint;

                public TrackAttr(final int tNo) {

                        this.trackIndex = tNo;

                }

        }

        protected DataManager() {

                mAzTrackList = new SketchItemizedOverlay(SketchItemizedOverlay.TRACK);

                mAzTrackSetMap = new ConcurrentHashMap<Integer, TrackAttr>();
                
                mAzPlotList = new SketchItemizedOverlay(SketchItemizedOverlay.PLOTS);

                mAzSketchPathOverlayList = new ArrayList<SketchPathOverlay>();
                
                mAzPrevScanTrackIdList = new ArrayList<Integer>();

                mElTrackList = new SketchItemizedOverlay(SketchItemizedOverlay.TRACK);

                mElTrackSetMap = new ConcurrentHashMap<Integer, TrackAttr>();

                mElPlotList = new SketchItemizedOverlay(SketchItemizedOverlay.PLOTS);

                mElSketchPathOverlayList = new ArrayList<SketchPathOverlayEl>();
                
                mElPrevScanTrackIdList = new ArrayList<Integer>();

                mAzFullVideoList = new ArrayList<SketchItemizedOverlay>();

                mElFullVideoList = new ArrayList<SketchItemizedOverlay>();

                for(int i = 0;i<Constance.RAW_AZ_BEAMS;i++)

                        mAzFullVideoList.add(i,new SketchItemizedOverlay(SketchItemizedOverlay.VIDEO));

                for(int i = 0;i<Constance.RAW_EL_BEAMS;i++)

                        mElFullVideoList.add(i,new SketchItemizedOverlay(SketchItemizedOverlay.VIDEO));

                mZoomTrackList = new SketchItemizedOverlay(SketchItemizedOverlay.TRACK);

                mZoomPlotList = new SketchItemizedOverlay(SketchItemizedOverlay.PLOTS);

                mZoomVideoList = new SketchItemizedOverlay(SketchItemizedOverlay.VIDEO);

        }
        
        public void copyAzPrevScanTrackIdList() {

            mAzPrevScanTrackIdList.clear();

            for(int i=0;i<mAzTrackList.getOverlayItemList().size();i++){

                        mAzPrevScanTrackIdList.add(((Track)mAzTrackList.getOverlayItemList(i)).getTrackNumber());

            }

            logger.info("copyAzPrevScanTrackIdList Current:"+mAzPrevScanTrackIdList.size());

        }



         public void updateAzNewTracksList() {

            if(mAzPrevScanTrackIdList!=null && mAzPrevScanTrackIdList.size()>0)

                 for(int i=0;i<mAzTrackList.getOverlayItemList().size();i++) {

                         final int trackNo = ((Track)mAzTrackList.getOverlayItemList(i)).getTrackNumber();

                                if(mAzPrevScanTrackIdList.contains(trackNo)) {

                                          mAzTrackList.removeOverlayItemList(i);

                                             --azTrackCounter;

                                           if(mAzTrackSetMap.containsKey(trackNo))

                                                mAzTrackSetMap.remove(trackNo);
                                    }
                            }

            logger.info("updateAzNewTracksList Remaining:"+azTrackCounter);

           

            if(mAzPrevScanTrackIdList.size() ==0 && mAzTrackList.size() == 0) {

                        mAzTrackSetMap.clear();

                        azTrackCounter = 0;

            }
}



            public void copyElPrevScanTrackIdList() {

                   mElPrevScanTrackIdList.clear();

                   for(int i=0;i<mElTrackList.getOverlayItemList().size();i++){

                        mElPrevScanTrackIdList.add(((Track)mElTrackList.getOverlayItemList(i)).getTrackNumber());

                  }
           }



           public void updateElNewTracksList() {

               if(mElPrevScanTrackIdList!=null && mElPrevScanTrackIdList.size()>0)

                     for(int i=0;i<mElTrackList.getOverlayItemList().size();i++) {

                              final int trackNo = ((Track)mElTrackList.getOverlayItemList(i)).getTrackNumber();

                                  if(mElPrevScanTrackIdList.contains(trackNo)) {

                                                mElTrackList.removeOverlayItemList(i);

                                                --elTrackCounter;

                                                if(mElTrackSetMap.containsKey(trackNo))

                                                            mElTrackSetMap.remove(trackNo);

                                     }
                              }

           

            if(mElPrevScanTrackIdList.size() ==0 && mElTrackList.size() == 0) {

                        mElTrackSetMap.clear();

                        elTrackCounter = 0;

            }
        }



        public SketchItemizedOverlay getAzTrackDataList() {

                return mAzTrackList;

        }
        
      

        public SketchItemizedOverlay getAzPlotDataList() {

                return mAzPlotList;

        }

        public SketchItemizedOverlay getElTrackDataList() {

                return mElTrackList;

        }

        public SketchItemizedOverlay getElPlotDataList() {

                return mElPlotList;

        }

        public SketchItemizedOverlay getZoomTrackList() {

                return mZoomTrackList;

        }

        public List<SketchItemizedOverlay> getAzFullVideoList() {

                return mAzFullVideoList;

        }

        public List<SketchItemizedOverlay> getElFullVideoList() {

                return mElFullVideoList;

        }

        public List<SketchPathOverlay> getAzSketchPathOverlayList() {

                return mAzSketchPathOverlayList;

        }

        public List<SketchPathOverlayEl> getElSketchPathOverlayList() {

                return mElSketchPathOverlayList;

        }

        public SketchItemizedOverlay getZoomPlotList() {

                return mZoomPlotList;

        }

        public SketchItemizedOverlay getZoomVideoList() {

                return mZoomVideoList;

        }

        public boolean isAzTrackAdded() {

                return isAzTrackAdded;

        }

        public void setAzTrackAdded(boolean isAzTrackAdded) {

                this.isAzTrackAdded = isAzTrackAdded;

        }

        public boolean isAzPlotAdded() {

                return isAzPlotAdded;

        }

        public void setAzPlotAdded(boolean isAzPlotAdded) {

                this.isAzPlotAdded = isAzPlotAdded;

        }

        public boolean isElTrackAdded() {

                return isElTrackAdded;

        }

        public void setElTrackAdded(boolean isElTrackAdded) {

                this.isElTrackAdded = isElTrackAdded;

        }

        public boolean isElPlotAdded() {

                return isElPlotAdded;

        }

        public void setElPlotAdded(boolean isElPlotAdded) {

                this.isElPlotAdded = isElPlotAdded;

        }

        public boolean isAzVideoAdded() {

                return isAzVideoAdded;

        }

        public void setAzVideoAdded(boolean isAzVideoAdded) {

                this.isAzVideoAdded = isAzVideoAdded;

        }

        public boolean isElVideoAdded() {

                return isElVideoAdded;

        }

        public void setElVideoAdded(boolean isElVideoAdded) {

                this.isElVideoAdded = isElVideoAdded;

        }

        public void clearAzTrackData() {

                mAzTrackSetMap.clear();

                mAzTrackList.clear();

                mAzSketchPathOverlayList.clear();

                azTrackCounter = 0;

                mAzPlotList.clear();

                if(AppConfig.getInstance().getTrackOptionController()!=null)

                        AppConfig.getInstance().getTrackOptionController().clearTrackLabels();

        }

        public void clearElTrackData() {

                mElTrackSetMap.clear();

                mElTrackList.clear();

                mElSketchPathOverlayList.clear();

                elTrackCounter = 0;

                mElPlotList.clear();

                if(AppConfig.getInstance().getTrackOptionController()!=null)

                        AppConfig.getInstance().getTrackOptionController().clearTrackLabels();

        }
        
             public void clearObseleteAzPlots() {

                    mAzPlotList.clear();

               }



             public void clearObseleteElPlots() {

                    mElPlotList.clear();
               }
             
             
             public void clearObseleteElPathList() {

            	  mElSketchPathOverlayList.clear();
              }
             
             public void clearObseleteAzPathList() {
            	 mAzSketchPathOverlayList.clear();
             }

          
             
             public void addAzTracks(AzimuthPlaneTrackMsg aTrackMsg) {

                 // creating track

                 final int trackNo = aTrackMsg.getTrackName();

                 Track track = new Track();

                 if(!mAzTrackSetMap.containsKey(trackNo)) {

                             TrackAttr tAttr = new TrackAttr(azTrackCounter);

                             mAzTrackSetMap.put(trackNo, tAttr);

                             track.setTrackNumber(trackNo);

                             track.setY(aTrackMsg.getY());

                             track.setX(aTrackMsg.getX());

                             track.setxVel(aTrackMsg.getxVelocity());

                             track.setyVel(aTrackMsg.getyVelocity());

                             track.setSpeed(Math.sqrt(Math.pow(aTrackMsg.getxVelocity(), 2)+Math.pow(aTrackMsg.getyVelocity(), 2)));

                             track.setEvaluated(false);

                             track.setAz(true);



                             //for path

                             SketchPathOverlay sPathOverlay = new SketchPathOverlay();

                             sPathOverlay.setTrackNo(trackNo);



                             mAzTrackList.addOverlayItemList(tAttr.trackIndex, track);

                             mAzSketchPathOverlayList.add(tAttr.trackIndex, sPathOverlay);

                             ++azTrackCounter;

                 } else {

                             //removing from prev scan

                             Integer trackId = new Integer(trackNo);

                             if(mAzPrevScanTrackIdList!=null && mAzPrevScanTrackIdList.size()>0 && mAzPrevScanTrackIdList.contains(trackId)){

                                         mAzPrevScanTrackIdList.remove(trackId);

                             }

                            

                             //setting already available track

                             List<OverlayItem> azList = mAzTrackList.getOverlayItemList();

                             int index = 0;

                             for(int i=0;i<azList.size();i++) {

                                         if(trackNo == ((Track)azList.get(i)).getTrackNumber()) {

                                                     index = i;

                                                     track = (Track)azList.get(i);

                                                     break;

                                         }

                             }

                             track.setTrackNumber(aTrackMsg.getTrackName());

                             track.setY(aTrackMsg.getY());

                             track.setX(aTrackMsg.getX());

                             track.setxVel(aTrackMsg.getxVelocity());

                             track.setyVel(aTrackMsg.getyVelocity());

                             track.setSpeed(Math.sqrt(Math.pow(aTrackMsg.getxVelocity(), 2)+Math.pow(aTrackMsg.getyVelocity(), 2)));

                             track.setEvaluated(false);

                             track.setAz(true);

                             if(index!=0)

                                         mAzTrackList.setOverlayItemList(index,track);

                            }

                 isAzTrackAdded = true;

             }
             
             public void addElTracks(final ElevationPlaneTrackMsg eTrackMsg) {

                 // creating track

                 final int trackNo = eTrackMsg.getTrackName();

                 Track track = new Track();

                 if(!mElTrackSetMap.containsKey(trackNo)) {

                             mElTrackSetMap.put(trackNo, new TrackAttr(elTrackCounter));

                             track.setTrackNumber(trackNo);
                             
                             track.setState(eTrackMsg.getTrackStatus());
                             
                 			 track.setCorrName(eTrackMsg.getReserved());
                 			 
                 			 track.setDesentRate(eTrackMsg.getXposVariance());

                             track.setZ(eTrackMsg.getZ());

                             track.setX(eTrackMsg.getX());

                             track.setxVel(eTrackMsg.getxVelocity());

                             track.setyVel(eTrackMsg.getzVelocity());

                             track.setSpeed(Math.sqrt(Math.pow(eTrackMsg.getxVelocity(), 2)+Math.pow(eTrackMsg.getzVelocity(), 2)));

                             track.setEvaluated(false);

                             track.setEl(true);



                             //for path

                            SketchPathOverlayEl sPathOverlayEl = new SketchPathOverlayEl();

                        
                            sPathOverlayEl.setTrackNo(trackNo);



                             mElTrackList.addOverlayItemList(elTrackCounter, track);

                             mElSketchPathOverlayList.add(elTrackCounter, sPathOverlayEl);

                             ++elTrackCounter;

                 } else {

                             //removing from prev scan

                             Integer trackId = new Integer(trackNo);

                             if(mElPrevScanTrackIdList!=null && mElPrevScanTrackIdList.size()>0 && mElPrevScanTrackIdList.contains(trackId)){

                                         mElPrevScanTrackIdList.remove(trackId);

                             }

                            

                             //setting already available track

                             List<OverlayItem> elList = mElTrackList.getOverlayItemList();

                             int index = 0;

                             for(int i=0;i<elList.size();i++) {

                                         if(trackNo == ((Track)elList.get(i)).getTrackNumber()) {

                                                     index = i;

                                                     track = (Track)elList.get(i);

                                                     break;

                                         }

                             }

                             track.setTrackNumber(eTrackMsg.getTrackName());
                             
                             track.setState(eTrackMsg.getTrackStatus());
                             
                 			 track.setCorrName(eTrackMsg.getReserved());
                 			 
                 			 track.setDesentRate(eTrackMsg.getXposVariance());

                             track.setZ(eTrackMsg.getZ());

                             track.setX(eTrackMsg.getX());

                             track.setxVel(eTrackMsg.getxVelocity());

                             track.setyVel(eTrackMsg.getzVelocity());

                             track.setSpeed(Math.sqrt(Math.pow(eTrackMsg.getxVelocity(), 2)+Math.pow(eTrackMsg.getzVelocity(), 2)));

                             track.setEvaluated(false);

                             track.setEl(true);

                             if(index!=0)

                                         mElTrackList.setOverlayItemList(index,track);

                 }

                 isElTrackAdded = true;
            }

             public void addAzPlots(AzimuthPlanePlotsPerCPIMsg aPlotsPerCPIMsg) {

                 for (AzimuthPlaneDetectionPlotMsg aPlotMsg : aPlotsPerCPIMsg

                                 .getAzimuthPlaneDetectionPlotMsg()) {

                         Plot plot = new Plot();

                         plot.setAzimuth(aPlotMsg.getAzimuth());

                         plot.setRange(aPlotMsg.getRange());
                         
                         plot.setCrstate(aPlotMsg.getReservedInt());

                         plot.setAz(true);

                         mAzPlotList.addOverlayItem(plot);

                 }

                 isAzPlotAdded = true;

         }
             
        public void addElPlots(ElevationPlanePlotsPerCPIMsg ePlotsPerCPIMsg) {

                for (ElevationPlaneDetectionPlotMsg aPlotMsg : ePlotsPerCPIMsg

                                .getElevationPlaneDetectionPlotMsg()) {

                        Plot plot = new Plot();

                        plot.setElevation(aPlotMsg.getElevation());

                        plot.setRange(aPlotMsg.getRange());

                        plot.setEl(true);

                        mElPlotList.addOverlayItem(plot);

                }

                isElPlotAdded = true;

        }

        public void addRAWVideo(final PlaneRAWVideoMsg vidMsg) {

          SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override

            protected Void doInBackground() throws Exception {

                  if (vidMsg.isAz()) {

                        if(vidMsg.getCpiNo()>0 && vidMsg.getCpiNo()<=Constance.RAW_AZ_BEAMS) {

                                double azAngle = 0;

          if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")) ) {

                azAngle = (Constance.AZIMUTH_MAX - (vidMsg.getCpiNo()*Constance.RAW_AZ_BEAM_ANGLE_OFFSET));// In degrees

            } else {

                 azAngle = (Constance.AZIMUTH_MIN + (vidMsg.getCpiNo()*Constance.RAW_AZ_BEAM_ANGLE_OFFSET));// In degrees

                }

          
               double range = (Constance.RAW_EACH_RANGE_CELL)*Constance.UNITS.getFACTOR_LENGTH();// In KM/NM  .
             

              SketchItemizedOverlay mAzEachBeamVideoList = mAzFullVideoList.get(vidMsg.getCpiNo()-1);

                mAzEachBeamVideoList.clear();

                mAzEachBeamVideoList.setCpiNo(vidMsg.getCpiNo());
               if(azAngle > Constance.AZIMUTH_MIN || azAngle <= Constance.AZIMUTH_MAX) {
            
                    for (int i = 0; i < vidMsg.getNoRangeCells(); i++) {

                            for(double j = azAngle-Constance.RAW_AZ_BEAM_ANGLE_OFFSET*0.5;j<azAngle+Constance.RAW_AZ_BEAM_ANGLE_OFFSET*0.5;j+=0.1) {
                            	
                                    Video video = new Video();

                                    video.setVal(vidMsg.getRangeCellList((short) i));

                                    video.setAz(true);

                                    video.setAzimuth(Math.toRadians(j));

                                    video.setRange(range * 1000);// In meters

                                    mAzEachBeamVideoList.addOverlayItem(video);

                            }
                            
                       range += Constance.RAW_EACH_RANGE_CELL;

                                  }

                            }

                isAzVideoAdded = true;

        }

}

          if(vidMsg.isEl()) {

               if (vidMsg.getCpiNo()>0 && vidMsg.getCpiNo() <= Constance.RAW_EL_BEAMS) {

                double elAngle = 0;
                         
                elAngle = (Constance.ELEVATION_MIN + vidMsg.getCpiNo()*Constance.RAW_EL_BEAM_ANGLE_OFFSET);// In degrees

                double range = (Constance.RAW_EACH_RANGE_CELL)*Constance.UNITS.getFACTOR_LENGTH();// In KM/NM    .

                SketchItemizedOverlay mElEachBeamVideoList = mElFullVideoList.get(vidMsg.getCpiNo()-1);

                mElEachBeamVideoList.clear();

                mElEachBeamVideoList.setCpiNo(vidMsg.getCpiNo());

                if(elAngle > Constance.ELEVATION_MIN && elAngle <= Constance.ELEVATION_MAX-1.5) {
             

                        for (int i = 0; i < vidMsg.getNoRangeCells(); i++) {

                                for(double j = elAngle-Constance.RAW_EL_BEAM_ANGLE_OFFSET*0.5;j<elAngle+Constance.RAW_EL_BEAM_ANGLE_OFFSET*0.5;j+=0.1) {

                                        Video video = new Video();

                                        video.setVal(vidMsg.getRangeCellList((short) i));

                                        video.setEl(true);

                                        video.setElevation(Math.toRadians(j));

                                        video.setRange(range * 1000);// In Meters

                                        mElEachBeamVideoList.addOverlayItem(video);

                                }

                                range += Constance.RAW_EACH_RANGE_CELL;

                             }

                        }
                
					   isElVideoAdded = true;
					}
				}
          
				return null;
			}



			@Override
			protected void process(List<Void> chunks) {
				super.process(chunks);
			}



			@Override
			protected void done() {

				super.done();
			}

		};
		worker.execute();

	}

	public void addTrackPathPoint(String name, Track track, int trackNo) {
		Track cloned = null;
		try {
			cloned = (Track) track.clone();
		} catch (CloneNotSupportedException e) {
			logger.error(e);
		}

		List<SketchPathOverlay> sPathOverlayList = null;
		List<SketchPathOverlayEl> sPathOverlayListEl = null;
		switch (name) {
		case "AZ":
			sPathOverlayList = mAzSketchPathOverlayList;
			break;

		case "EL":
			sPathOverlayListEl = mElSketchPathOverlayList;
			break;

		default:
			break;
		}

		if(name.equals("AZ")){
		if(sPathOverlayList!=null && sPathOverlayList.size() > 0 && cloned != null) {
			//get path index
			for(int i=0;i<sPathOverlayList.size();i++) {
				SketchPathOverlay sOverlay = sPathOverlayList.get(i);
				if(trackNo == sOverlay.getTrackNo()) {
					sOverlay.addTrackPath(cloned);
					//if(name.equals("AZ"))
						sOverlay.setAzEl(true);
					break;
				}
			}
		}
	  }
		else{
			if(sPathOverlayListEl!=null && sPathOverlayListEl.size() > 0 && cloned != null) {
				//get path index
				for(int i=0;i<sPathOverlayListEl.size();i++) {
					SketchPathOverlayEl sOverlayEl = sPathOverlayListEl.get(i);
					if(trackNo == sOverlayEl.getTrackNo()) {
						sOverlayEl.addTrackPathEl(cloned);
							sOverlayEl.setAzEl(true);
						break;
					}
				}
			}
		}
	}

	
	 public void drawAzSketchPathOverlay(GraphicsContext gc, TrackOptionController tController) {

         if(mAzSketchPathOverlayList.size() > 0 ) {

                int pathTrackNoIndex = 0;

                     for(int i=0;i<mAzSketchPathOverlayList.size();i++) {

                             SketchPathOverlay sOverlay = mAzSketchPathOverlayList.get(i);

                                if(tController.getTrackNo() == sOverlay.getTrackNo()) {

                                           pathTrackNoIndex = i;

                                             break;
                                 }
                         }

      if(mAzTrackSetMap.containsKey(tController.getTrackNo())) {

             TrackAttr tAttr = mAzTrackSetMap.get(tController.getTrackNo());

                 Track track = null;

                       for(int i=0;i<mAzTrackList.getOverlayItemList().size();i++)

                            if(tController.getTrackNo() == ((Track)mAzTrackList.getOverlayItemList(i)).getTrackNumber()) {

                                         track = (Track) mAzTrackList.getOverlayItemList(i);

                                        }                                             

                        if(track !=null) {

                                 boolean isLabel = tController.isShowLabel();

                                 Color col = tController.getColor();

                                 track.setShowLabel(isLabel);

                                 track.setColor(col);

                                 tAttr.labelPoint = track.getLablePoint();

                                 if(tController.isShowPath()) {

                                     SketchPathOverlay sOverlay = mAzSketchPathOverlayList.get(pathTrackNoIndex);

                                     sOverlay.setColor(tController.getColor());

                                     sOverlay.drawCompletePath(gc);

                                         }

                                      }
                              }
                      }
                }



	  public void drawElSketchPathOverlay(GraphicsContext gc, TrackOptionControllerEl tControllerEl) {

          if(mElSketchPathOverlayList.size() > 0 ) {

                      int pathTrackNoIndex = 0;

                      for(int i=0;i<mElSketchPathOverlayList.size();i++) {

                                  SketchPathOverlayEl sOverlayEl = mElSketchPathOverlayList.get(i);

                                  if(tControllerEl.getTrackNo() == sOverlayEl.getTrackNo()) {

                                              pathTrackNoIndex = i;

                                              break;

                                  }

                      }

                     

                      if(mElTrackSetMap.containsKey(tControllerEl.getTrackNo())) {

                                  TrackAttr tAttr = mElTrackSetMap.get(tControllerEl.getTrackNo());

                                  Track track = null;

                                  for(int i=0;i<mElTrackList.getOverlayItemList().size();i++)

                                              if(tControllerEl.getTrackNo() == ((Track)mElTrackList.getOverlayItemList(i)).getTrackNumber()) {

                                                          track = (Track) mElTrackList.getOverlayItemList(i);

                                                     }

                                  if(track != null) {

                                              track.setShowLabel(tControllerEl.isShowLabel());

                                              track.setColor(tControllerEl.getColor());

                                              tAttr.labelPoint = track.getLablePoint();



                                              if(tControllerEl.isShowPath()) {

                                                          SketchPathOverlayEl sOverlayEl = mElSketchPathOverlayList.get(pathTrackNoIndex);

                                                          sOverlayEl.setColor(tControllerEl.getColor());

                                                          sOverlayEl.drawCompletePathEl(gc);

                                          }
                                  }
                           }
                    }
              }


	 
	
	  
	  public Point getAzTrackLabelRegion(Point initPoint) {

          for (Entry<Integer, TrackAttr> entry : mAzTrackSetMap.entrySet()){

                      TrackAttr tAttr = entry.getValue();

                      Point actPoint = new Point();

                      if(tAttr!=null && tAttr.labelPoint!=null) {

                                  //setting region

                                  actPoint.setX(tAttr.labelPoint.getX()+Track.REGION_WIDTH);

                                  actPoint.setY(tAttr.labelPoint.getY()+Track.RECT_HEIGHT);


                                  //detecting

                                  if((tAttr.labelPoint.getX() < initPoint.getX()) &&  (actPoint.getX() > initPoint.getX()))

                                              if((tAttr.labelPoint.getY() < initPoint.getX()) && (actPoint.getY() > initPoint.getY()))

                                                          return tAttr.labelPoint;

                                      }
                              }
          return null;
     }



 public Point getElTrackLabelRegion(Point initPoint) {

          for (Entry<Integer, TrackAttr> entry : mElTrackSetMap.entrySet()){

                      TrackAttr tAttr = entry.getValue();

                      Point actPoint = new Point();

                      if(tAttr!=null && tAttr.labelPoint!=null) {

                                  //setting region

                                  actPoint.setX(tAttr.labelPoint.getX()+Track.REGION_WIDTH);

                                  actPoint.setY(tAttr.labelPoint.getY()+Track.RECT_HEIGHT1);

                                  if((tAttr.labelPoint.getX() < initPoint.getX()) &&  (actPoint.getX() > initPoint.getX()))

                                              if((tAttr.labelPoint.getY() < initPoint.getX()) && (actPoint.getY() > initPoint.getY()))

                                                          return tAttr.labelPoint;
                                 }
                      }
          return null;
         }

  }