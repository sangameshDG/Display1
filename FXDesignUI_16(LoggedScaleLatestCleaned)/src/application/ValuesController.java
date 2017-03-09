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
//*H  Created: 08-Jul-2016
//*H
//*H  @author....: $Author: $Suraj
//*H  @date......: $Date: $
//*H  @version...: $Rev: $1.0
//*H  @path......: $URL: $
//*H
//*H============================================================================

package application;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.Timer;
import org.apache.log4j.Logger;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.AppConfig;
import model.DataManager;
import model.ILayoutParam;
import utils.Constance;
import views.ResizableCanvas;


public class ValuesController implements Initializable,ILayoutParam {

private static final Logger logger = Logger.getLogger(ZoomController.class);
	
	@FXML AnchorPane ZoomDialog;
	
	@FXML Pane chartZoom;
	@FXML ResizableCanvas cZoomL0;//Graph
	
	
	private int startRange;//meters
	private int endRange;//meters
	private double startY;
	private double endY;

		
	AppConfig appConfig = AppConfig.getInstance();
	Timer zoomRefreshAnim;
	AnimationTimer zoomAnimTimer;
	DataManager dataObserver = DataManager.getInstance();
	
	double initialX,initialY;
	boolean refresh = false;

	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Zoom Dialog Opened");
		addDraggableNode(ZoomDialog);
		initDialog();
	}	
	
	@FXML
	protected void cancelClick(ActionEvent event) {
		closeSettings(event);
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
		cZoomL0.widthProperty().bind(chartZoom.widthProperty());
		cZoomL0.heightProperty().bind(chartZoom.heightProperty());
				
		Constance.IS_ZOOM_DIALOG = true;
		//Wait for Canvas to bloat the zoom dialog
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
				//start drawing
				initAnimators();				
			}
		}).start();
		
	}
	
	private void initAnimators() {
		
		cZoomL0.clear();
		GraphicsContext gc = cZoomL0.getGraphicsContext2D();
		gc.setFont(new Font("Arial", 12));
		gc.setStroke(Color.WHITE);
		DecimalFormat df = new DecimalFormat("#.000");
		if(Constance.Plane=="EL"){
		gc.strokeText("Rang :  " +df.format(getStartRange()*0.001)+" "+Constance.UNITS.getLENGTH(), OFFSET, TEXT_OFFSET);
		if(Constance.UNITS.isKM==true)
		    gc.strokeText("Height:  "+df.format(getStartY())+" m", OFFSET, 2*TEXT_OFFSET);
		else
			gc.strokeText("Height:  "+df.format(getStartY()*3.2808)+" ft", OFFSET, 2*TEXT_OFFSET);
	    }
		else{
			gc.strokeText("Rang :  " +df.format(getStartRange()*0.001)+" "+Constance.UNITS.getLENGTH(), OFFSET, TEXT_OFFSET);
			if(Constance.UNITS.isKM==true){
			gc.strokeText(" LO :  "+df.format(getStartY())+" m", OFFSET, 2*TEXT_OFFSET);
			}
			else
				gc.strokeText(" LO :  "+df.format(getStartY()*3.2808)+" ft", OFFSET, 2*TEXT_OFFSET);	
		}
		
 }
	

	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("Co-points Dialog Closed");	
	}

	public int getStartRange() {
		return startRange;
	}

	public void setStartRange(int startRange) {
		this.startRange = startRange;
	}

	public int getEndRange() {
		return endRange;
	}

	public void setEndRange(int endRange) {
		this.endRange = endRange;
	}
	
	public double getStartY() {
		return startY;
	}

	public void setStartY(double starty) {
		this.startY = starty;
	}
	
	public double getEndY() {
		return endY;
	}

	public void setEndY(double endy) {
		this.endY = endy;
	}
	
}
