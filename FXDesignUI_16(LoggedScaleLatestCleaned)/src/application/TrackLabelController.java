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
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.Light.Point;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.ILayoutParam;

import org.apache.log4j.Logger;

public class TrackLabelController implements Initializable,ILayoutParam {
	
	private static final Logger logger = Logger.getLogger(TrackLabelController.class);
	
	Stage stage;
	
	@FXML AnchorPane trackLabelDialog;
	
	@FXML Label lbl_trackNo;
	@FXML Label lbl_trackRng;
	@FXML Label lbl_trackSpeed;
	@FXML Label lbl_trackOffset;
	
	double initialX,initialY;
	Point end = new Point();

	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Track Label Dialog Opened");
		addDraggableNode(trackLabelDialog);
		initDialog();
	}

	public Stage getStage() {
		return stage;
	}
	
	public void setTrackNo(double trackNo) {
		lbl_trackNo.setText(String.valueOf(trackNo));
	}
	
	public void setTrackRng(double trackRng) {
		lbl_trackRng.setText(String.valueOf(trackRng));
	}
	
	public void setTrackSpeed(double trackSp) {
		lbl_trackSpeed.setText(String.valueOf(trackSp));
	}
	
	public void setTrackOffset(double trackOff) {
		lbl_trackOffset.setText(String.valueOf(trackOff));
	}
	
	public Point getEndPoint() {
		return end;
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
	    
	    node.setOnMouseReleased(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent me) {
	            if (me.getButton() != MouseButton.MIDDLE) {
	                end.setX(me.getScreenX() - initialX);
	                end.setY(me.getScreenY() - initialY);
	            }
	        }
	    });
	}
	
	private void initDialog() {
//		trackLabelDialog.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
	}
	
}
