package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import messages.radar.SDPHealthStatusMsg;
import model.AppConfig;
import model.ILayoutParam;

import org.apache.log4j.Logger;

public class SdpSetUpController implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(SdpSetUpController.class);
	
	@FXML AnchorPane SdpSetup;	

	@FXML TextField board1;
	@FXML TextField board2;
	@FXML TextField board3;
	
	double initialX,initialY;
	
	public void updateData() {
		SDPHealthStatusMsg sdp = AppConfig.getInstance().getSdp();
		if(sdp!=null) {
			FXMLController.setTextField(getColor((sdp.getBoard1()==1)?true:false), board1);
			FXMLController.setTextField(getColor((sdp.getBoard2()==1)?true:false), board2);
			FXMLController.setTextField(getColor((sdp.getBoard3()==1)?true:false), board3);
		}
	}

	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("SDP Setup Dialog Opened");
		addDraggableNode(SdpSetup);
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
		VAL val = VAL.DEFAULT;
		FXMLController.setTextField(val, board1);
		FXMLController.setTextField(val, board2);
		FXMLController.setTextField(val, board3);
	}

	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("SDP Setup Dialog Closed");
	}
	
	private VAL getColor(boolean boo) {
		return (boo?VAL.GREEN:VAL.RED);
	}

}