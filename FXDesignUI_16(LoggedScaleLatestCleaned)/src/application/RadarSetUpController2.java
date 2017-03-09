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
//*H  Created: 02-Jan-2017
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
import org.apache.log4j.Logger;
import admin.UserPreference;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import model.AppConfig;
import model.ILayoutParam;
import utils.Constance;

/**
 * Defines the class RadarSetUpController2.
 * @author SangappaG
 * @version $Revision: $.
 * @since 02-Jan-2017
 */
public class RadarSetUpController2 implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(RadarSetUpController.class);

	@FXML AnchorPane RadarSetup;
	
	@FXML VBox radarSetUpAnchor;
	@FXML HBox opType_1;
	@FXML HBox scan_1;
	@FXML HBox byte_1;
	@FXML HBox northOff_1;
	@FXML HBox rmin_1;
	
	

	@FXML ComboBox<String> comboModes;
	@FXML ComboBox<String> comboScans;
	@FXML ComboBox<String> comboModeType;
	@FXML ComboBox<String> comboFreq;
	@FXML ComboBox<String> comboPol;

	@FXML MaterialDesignButtonWidget biteOn;
	@FXML MaterialDesignButtonWidget biteOff;
	@FXML MaterialDesignButtonWidget txOn;
	@FXML MaterialDesignButtonWidget txOff;

	@FXML TextField txtMinRng;
	@FXML TextField txtNorthOffsetBias;

	AppConfig appConfig = AppConfig.getInstance();

	double initialX,initialY;
	 
	@Override
	public void draw(GraphicsContext gc) {

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Radar Setup Dialog Opened");
		addDraggableNode(RadarSetup);
		initDialog();
		loadUserPrefValues();
	}
	
	

	@FXML
	protected void updateClick(ActionEvent event) {
		sendCommand();
		closeSettings(event);
	}

	@FXML
	protected void cancelClick(ActionEvent event) {
		closeSettings(event);
	}

	
	public void sendCommand() {
		
			if(appConfig.isDisplayHasPrevilege()) {
				saveUserPrefData();
				appConfig.openInformationDialog("Parameters saved");
			
		}
	}
	
	private void initDialog() {
		comboModes.getItems().clear();
		comboModes.getItems().addAll("Mode 1","Mode 2","Mode 3");

		
		String[] incVal = new String[22];
		int count = 0;
		DecimalFormat df = new DecimalFormat("0.##");
		for(double i=9;i<9.20;i=i+0.01)
				incVal[++count] = String.valueOf(df.format(i));
		incVal[count] = "agility";
		comboFreq.getItems().clear();
		comboFreq.getItems().addAll(incVal);

		comboPol.getItems().clear();
		comboPol.getItems().addAll("Vertical","Horizontal","L.H.C.P","R.H.C.P");
	
	}

	private void loadUserPrefValues() {
		UserPreference uPreference = UserPreference.getInstance();

		int modeIndex = Integer.parseInt(uPreference.getOP_MODE());
		System.out.println("Mode index is: "+modeIndex);
		comboModes.getSelectionModel().select(modeIndex);
		
		int freqIndex = Integer.parseInt(uPreference.getOP_FREQ());
		comboFreq.getSelectionModel().select(freqIndex);
		
		int pol = Integer.parseInt(uPreference.getOP_POLARIZATION());
		comboPol.getSelectionModel().select(pol);
	}
	
	private void saveUserPrefData() {
		UserPreference uPreference = UserPreference.getInstance();
		uPreference.setOP_MODE(String.valueOf(comboModes.getSelectionModel().getSelectedIndex()));
		uPreference.setOP_FREQ(String.valueOf(comboFreq.getSelectionModel().getSelectedIndex()));
		uPreference.setOP_POLARIZATION(String.valueOf(comboPol.getSelectionModel().getSelectedIndex()));
		
		Constance.OPPMODEPARAM.OP_MODE=(byte) comboModes.getSelectionModel().getSelectedIndex();
		Constance.OPPMODEPARAM.OP_FREQ=(byte) comboFreq.getSelectionModel().getSelectedIndex();
		Constance.OPPMODEPARAM.OP_POL=(byte) comboPol.getSelectionModel().getSelectedIndex();
			
	}

	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource();
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("Radar Setup Dialog Closed");
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

	
}
