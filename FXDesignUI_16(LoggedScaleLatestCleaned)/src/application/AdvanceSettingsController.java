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
//*H  Created: Dec 18, 2015
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
import org.apache.log4j.Logger;
import admin.UserPreference;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import messages.app.SpConfigMsg;
import model.AppConfig;
import model.ILayoutParam;
import utils.Constance;

public class AdvanceSettingsController implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(AdvanceSettingsController.class);
	
	@FXML AnchorPane AdvSettingSetup;
	
	@FXML MaterialDesignButtonWidget mtiOn;
	@FXML MaterialDesignButtonWidget mtiOff;
		
	double initialX,initialY;

	private SpConfigMsg spConfigMsg;
	private boolean mti = false;
	
	AppConfig appConfig = AppConfig.getInstance();
	
	public AdvanceSettingsController(MaterialDesignButtonWidget btn_MTI_ON,
			MaterialDesignButtonWidget btn_MTI_OFF) {
		this.mtiOn = btn_MTI_ON;
		this.mtiOff = btn_MTI_OFF;
	}

	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Advanced Settings Dialog Opened");
		addDraggableNode(AdvSettingSetup);
		initAdvanceSetUp();
		loadUserPrefValues();
		
	}

	public void initAdvanceSetUp() {
		initObj();
		loadUserPrefValues();
	}
	
	@FXML
	protected void updateClick(ActionEvent event) {
		if(verifyPassword()) {
			makeMsg();
			saveUserPrefData();
			sendUpdate();
			closeSettings(event);
		}
	}
	
	public void sendCommand() {
		if(appConfig.isDisplayHasPrevilege()) {
			makeMsg();
			saveUserPrefData();
			sendUpdate();
			appConfig.openInformationDialog("Command Sent");
	    }
    }
	
	@FXML
	protected void cancelClick(ActionEvent event) {
		closeSettings(event);
	}
	
	@FXML
	protected void mtiOn(ActionEvent event) {
		mti = true;
		mtiOn.widgetSetVal(VAL.GREEN);
		mtiOff.widgetSetVal(VAL.RED);
	}
	
	@FXML
	protected void mtiOff(ActionEvent event) {
		mti = false;
		mtiOn.widgetSetVal(VAL.RED);
		mtiOff.widgetSetVal(VAL.GREEN);
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
	
	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("Advanced Settings Dialog Closed");
	}

	private void initObj() {
		spConfigMsg = new SpConfigMsg();
	}
	
	private void loadUserPrefValues() {
		UserPreference uPreference = UserPreference.getInstance();
		if(uPreference.getMTI().contains("ON")) {
			mtiOn.widgetSetVal(VAL.GREEN);
			mtiOff.widgetSetVal(VAL.RED);
			mti = true;
		}else{
			mtiOff.widgetSetVal(VAL.GREEN);
			mtiOn.widgetSetVal(VAL.RED);
		}
	}
	
	private void saveUserPrefData() {
		UserPreference uPreference = UserPreference.getInstance();
		if(mti)
			uPreference.setMTI("ON");
		else
			uPreference.setMTI("OFF");		
	}
	
	private boolean verifyPassword() {
		AppConfig.getInstance().openNumPadPanel("Enter Passcode: ");
		NumPadPanel numPadPanel = AppConfig.getInstance().getNumPadPanel();
		if(numPadPanel!=null&& !numPadPanel.getValue().isEmpty()) {
			String passcode = numPadPanel.getValue();
			if(UserPreference.getInstance().getPASS_CODE().equals(passcode))
				return true;
			else {
				AppConfig.getInstance().openErrorDialog("Incorrect Passcode!");
				return false;
			}
		}
		return false;
	}
	
   private void makeMsg() {
		
		//SP Config Msg
		spConfigMsg.setMtiOnOff((byte) (mti? 1:0));
		spConfigMsg.setMtiType(Constance.SPConfig.MTItype);
		spConfigMsg.setaMtiOnOff(Constance.SPConfig.AMTi);
		spConfigMsg.setClutterMap(Constance.SPConfig.ClutterMap);
		switch (Constance.SPConfig.CFarType) {
		case "CA":
			spConfigMsg.setCfarSel((short) 1);
			break;

		case "GO":
			spConfigMsg.setCfarSel((short) 2);
			break;

		case "LO":
			spConfigMsg.setCfarSel((short) 3);
			break;
		}
		spConfigMsg.setCfarThreshold((byte)Constance.SPConfig.CFarThreshold);		
	}
	
	private void sendUpdate() {
		//send SP Config Msg
		AppConfig.getInstance().getFxmlController().sendRCBytes(spConfigMsg.encode().array());
	}

}
