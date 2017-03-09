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
//*H  Created: Dec 13, 2015
//*H
//*H  @author....: $Author: $Suraj
//*H  @date......: $Date: $
//*H  @version...: $Rev: $1.0
//*H  @path......: $URL: $
//*H
//*H============================================================================

package application;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import model.ILayoutParam;

import org.apache.log4j.Logger;

import utils.Constance;

public class TrackOptionController implements Initializable,ILayoutParam {
	
	private static final Logger logger = Logger.getLogger(TrackOptionController.class);
	private static final String WHITE = "white";
	private static final String YELLOW = "yellow";
	private static final String TEAL = "teal";
	private static final String BLUE = "blue";
	private static final String PURPLE = "purple";
	private static final String BROWN = "brown";
	private static final String GREENYELLOW = "greenyellow";
	
	Stage stage;
	
	@FXML AnchorPane trackOptionDialog;
	@FXML MaterialDesignButtonWidget closeX;
	
	@FXML Label lbl_trackNo;
	@FXML CheckBox showPath;
	@FXML CheckBox showLabel;
	@FXML ComboBox<String> colorBox;
	
	double initialX,initialY;
	int trackNo;
	boolean delTrack = false;
	Map<Integer, Items> trackNoLabel = new HashMap<Integer,Items> ();

	public class Items {
		boolean label;
		Color color;

		public Items(boolean showPath, Color color2) {
			this.label = showPath;
			this.color = color2;
		}

		public boolean isLabel() {
			return label;
		}

		public Color getColor() {
			return color;
		}

		public void setLabel(boolean b) {
			this.label = b;
		}
		
	}
	
	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Track Options Dialog Opened");
		addDraggableNode(trackOptionDialog);
		initDialog();
		loadPref();
		
	  }
	
	@FXML
	protected void closeClick(ActionEvent event) {
		addTrackNoLabel(trackNo, new Items(isShowPath(),getColor()));
		savePref();
		closeActivity(event);
	}
	
	@FXML
	protected void deleteTrackClick(ActionEvent event) {
		delTrack = true;
	}
	
	public Map<Integer,Items> getTrackOptionMap() {
		return trackNoLabel;
	}
	
	public void clearTrackLabels() {
		trackNoLabel.clear();
	}
	
	public void addTrackNoLabel(int key, Items value) {
		trackNoLabel.put(key, value);
	}
	
	public boolean isShowPath() {
		return showPath.isSelected();
	}
	
	public boolean isShowLabel() {
		return showLabel.isSelected();
	}
	
	public int getTrackNo() {
		return trackNo;
	}

	public Stage getStage() {
		return stage;
	}
	
	public void setTrackNo(int tNo) {
		this.trackNo = tNo;
		loadPref();
	}
	
	public Color getColor() {
		switch (colorBox.getValue()) {
		case WHITE:
			return Color.valueOf("#FFFFFF");
			
		case YELLOW:
			return Color.valueOf("#FFFF00");
			
		case TEAL:
			return Color.valueOf("#548687");
			
		case BLUE:
			return Color.valueOf("#b3d9ff");
			
		case PURPLE:
			return Color.valueOf("#832161");
			
		case BROWN:
			return Color.valueOf("#E07A5F");
			
		case GREENYELLOW:
			//return Color.valueOf("#FF1493");
			return Color.valueOf("#FF1493");
			
		default:
			return Color.valueOf("#FFFFFF");
		}
	}

	private void closeActivity(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("Track Options Dialog Closed");	
	}
	
	private void addDraggableNode(final Node node) {

	    node.setOnMousePressed(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent me) {
	            if (me.getButton() != MouseButton.MIDDLE) {
	                initialX = me.getSceneX();
	                initialY = me.getSceneY();
	            }
	        }
	    });

	    node.setOnMouseDragged(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent me) {
	            if (me.getButton() != MouseButton.MIDDLE) {
	                node.getScene().getWindow().setX(me.getScreenX() - initialX);
	                node.getScene().getWindow().setY(me.getScreenY() - initialY);
	            }
	        }
	    });
	}
	
	private void initDialog() {
		
		colorBox.getItems().clear();
		colorBox.getItems().addAll(WHITE,YELLOW,TEAL,BLUE,PURPLE,BROWN,GREENYELLOW);
		colorBox.setValue(YELLOW);
		
		showLabel.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if(newValue.equals(Boolean.FALSE)){
					showPath.setSelected(false);
				}				
			}
			
		});
	}
	
	private void loadPref() {
		lbl_trackNo.setText(trackNo+"");
		if(trackNo == Constance.SHOW_TRACK_NO) {
			showLabel.setSelected(Constance.SHOW_TRACK_LABEL);
			showPath.setSelected(Constance.SHOW_TRACK_PATH);
		} else {
			showLabel.setSelected(true);
			showPath.setSelected(true);
		}
	}	
		public void loadPref1() {
			lbl_trackNo.setText(trackNo+"");
			if(trackNo == Constance.SHOW_TRACK_NO) {
				showLabel.setSelected(Constance.SHOW_TRACK_LABEL);
				showPath.setSelected(Constance.SHOW_TRACK_PATH);
			} else {
				showLabel.setSelected(false);
				showPath.setSelected(false);
			}
	}
	
	private void savePref() {
		//save till application lasts
		Constance.SHOW_TRACK_NO = trackNo;
		Constance.SHOW_TRACK_LABEL = showLabel.isSelected();
		Constance.SHOW_TRACK_PATH = showPath.isSelected();
		
	}
	
}
