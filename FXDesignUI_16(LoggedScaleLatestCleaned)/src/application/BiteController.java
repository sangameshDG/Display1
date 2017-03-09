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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import messages.app.BiteModeMsg;
import messages.app.CalibrationModeMsg;
import model.AppConfig;
import model.ILayoutParam;

import org.apache.log4j.Logger;

import admin.UserPreference;

/**
 * Defines the class BiteController.
 * @author SUKA150
 * @version $Revision: $.
 * @since Dec 18, 2015
 */
public class BiteController implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(BiteController.class);
	
	@FXML AnchorPane BiteSetup;
	
	@FXML MaterialDesignButtonWidget biteOn;
	@FXML MaterialDesignButtonWidget biteOff;	
	@FXML ComboBox<String> comboAzDop;
	@FXML TextField txtAzAtt;
	@FXML ComboBox<String> comboElDop;
	@FXML TextField txtElAtt;
	
	@FXML ComboBox<String> comboStTarget;
	@FXML ComboBox<String> comboMovStartRng;
	@FXML ComboBox<String> comboMovStopRng;
	@FXML ComboBox<String> comboMovStepRng;
	
	@FXML Label lbl_biteMsg;
	@FXML Label lbl_calibMsg;
	@FXML MaterialDesignButtonWidget updateBite;
	@FXML MaterialDesignButtonWidget updateCalib;
	
	@FXML CheckBox AzTxCal;
	@FXML CheckBox AzRxCal;
	@FXML CheckBox ElTxCal;
	@FXML CheckBox ElRxCal;
	
	double initialX,initialY;
	
	private BiteModeMsg biteModeMsg;
	private static final int ATTEN = 127;
	private boolean bite = false;
	
	private CalibrationModeMsg calibModeMsg;
	private boolean azTx = false;
	private boolean elTx = false;
	
	
	private IntegerProperty maxLength = new SimpleIntegerProperty(this, "maxLength", 5);
    private StringProperty restrict = new SimpleStringProperty(this, "restrict");
	
	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info(" BITE SetUp Dialog Opened");
		addDraggableNode(BiteSetup);
		initObj();
		initDialog();
		loadUserPrefValues();
		
	}

	@FXML
	protected void updateBiteClick(ActionEvent event) {
		if(verifyData()) {
			makeBiteMsg();
			saveBiteUserPrefData();
			sendBiteUpdate();
			lbl_biteMsg.setText("Command Sent");
		}
	}
	
	@FXML
	protected void cancelClick(ActionEvent event) {
		closeSettings(event);
	}
	
	@FXML
	protected void biteOn(ActionEvent event) {
		bite = true;
		biteOn.widgetSetVal(VAL.GREEN);
		biteOff.widgetSetVal(VAL.RED);
	}
	
	@FXML
	protected void biteOff(ActionEvent event) {
		bite = false;
		biteOn.widgetSetVal(VAL.RED);
		biteOff.widgetSetVal(VAL.GREEN);
	}
	
	@FXML
	protected void azTxCalib(ActionEvent event) {
		azTx = true;
		AzTxCal.setSelected(azTx);
		AzRxCal.setSelected(!azTx);
	}
	
	@FXML
	protected void azRxCalib(ActionEvent event) {
		azTx = false;
		AzTxCal.setSelected(azTx);
		AzRxCal.setSelected(!azTx);
	}
	
	@FXML
	protected void elTxCalib(ActionEvent event) {
		elTx = true;
		ElTxCal.setSelected(elTx);
		ElRxCal.setSelected(!elTx);
	}
	
	@FXML
	protected void elRxCalib(ActionEvent event) {
		elTx = false;
		ElTxCal.setSelected(elTx);
		ElRxCal.setSelected(!elTx);
	}
	
	@FXML
	protected void updateCalibClick(ActionEvent event) {
		if(verifyData()) {
			makeCalibMsg();
			saveCalibUserPrefData();
			sendCalibUpdate();
			lbl_calibMsg.setText("Command Sent");
		}
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
	    logger.info("BITE Setup Dialog Closed");
	}

	private void initObj() {
		biteModeMsg = new BiteModeMsg();
		calibModeMsg = new CalibrationModeMsg();
	}
	
	private void loadUserPrefValues() {
		UserPreference uPreference = UserPreference.getInstance();
		if(uPreference.getBITE().contains("ON")) {
			biteOn.widgetSetVal(VAL.GREEN);
			bite = true;
		}else
			biteOff.widgetSetVal(VAL.GREEN);
		
		int stTarget = Integer.parseInt(uPreference.getST_TARGET());
		comboStTarget.getSelectionModel().select(stTarget);
		
		int azDop = Integer.parseInt(uPreference.getAZ_DOP());
		comboAzDop.getSelectionModel().select(azDop);
		txtAzAtt.setText(uPreference.getAZ_ATT());
		int elDop = Integer.parseInt(uPreference.getEL_DOP());
		comboElDop.getSelectionModel().select(elDop);
		txtElAtt.setText(uPreference.getEL_ATT());
		
		int movStartRng = Integer.parseInt(uPreference.getMOV_START_RNG());
		comboMovStartRng.getSelectionModel().select(movStartRng);
		int movStopRng = Integer.parseInt(uPreference.getMOV_STOP_RNG());
		comboMovStopRng.getSelectionModel().select(movStopRng);
		int movStepRng = Integer.parseInt(uPreference.getMOV_STEP_RNG());
		comboMovStepRng.getSelectionModel().select(movStepRng);
		
		if(uPreference.getCAL_AZ().contains("TX"))
			AzTxCal.setSelected(true);
		else if(uPreference.getCAL_AZ().contains("RX"))
			AzRxCal.setSelected(true);
		else {
			AzTxCal.setSelected(false);
			AzRxCal.setSelected(false);
		}
		
		if(uPreference.getCAL_EL().contains("TX"))
			ElTxCal.setSelected(true);
		else if(uPreference.getCAL_EL().contains("RX"))
			ElRxCal.setSelected(true);
		else {
			ElTxCal.setSelected(false);
			ElRxCal.setSelected(false);
		}
	}
	
	private void saveBiteUserPrefData() {
		UserPreference uPreference = UserPreference.getInstance();
		if(bite)
			uPreference.setBITE("ON");
		else
			uPreference.setBITE("OFF");
		
		uPreference.setST_TARGET(String.valueOf(comboStTarget.getSelectionModel().getSelectedIndex()));
		
		uPreference.setAZ_DOP(String.valueOf(comboAzDop.getSelectionModel().getSelectedIndex()));
		uPreference.setAZ_ATT(txtAzAtt.getText());
		uPreference.setEL_DOP(String.valueOf(comboElDop.getSelectionModel().getSelectedIndex()));
		uPreference.setEL_ATT(txtElAtt.getText());
		
		uPreference.setMOV_START_RNG(String.valueOf(comboMovStartRng.getSelectionModel().getSelectedIndex()));
		uPreference.setMOV_STOP_RNG(String.valueOf(comboMovStopRng.getSelectionModel().getSelectedIndex()));
		uPreference.setMOV_STEP_RNG(String.valueOf(comboMovStepRng.getSelectionModel().getSelectedIndex()));
	}
	
	private void saveCalibUserPrefData() {
		UserPreference uPreference = UserPreference.getInstance();
		if(AzTxCal.isSelected())
			uPreference.setCAL_AZ("TX");
		else if(AzRxCal.isSelected())
			uPreference.setCAL_AZ("RX");
		else 
			uPreference.setCAL_AZ("NULL");
		
		if(ElTxCal.isSelected())
			uPreference.setCAL_EL("RX");
		else if(ElRxCal.isSelected())
			uPreference.setCAL_EL("RX");
		else 
			uPreference.setCAL_EL("NULL");
	}
	
	private void initDialog() {
		biteOn.widgetSetVal(VAL.RED);
		biteOff.widgetSetVal(VAL.RED);
		
		comboAzDop.getItems().clear();
		comboAzDop.getItems().addAll("Freq","Freq/2","Freq/4");
		
		comboElDop.getItems().clear();
		comboElDop.getItems().addAll("Freq","Freq/2","Freq/4");
		
		String[] incVal = new String[61];
		for(double i=0;i<=30;i=i+0.5) {
			incVal[(int) (2*i)] = String.valueOf(i);
		}
		
		comboStTarget.getItems().clear();
		comboStTarget.getItems().addAll(incVal);
		
		comboMovStartRng.getItems().clear();
		comboMovStartRng.getItems().addAll(incVal);
		
		comboMovStopRng.getItems().clear();
		comboMovStopRng.getItems().addAll(incVal);
		
		comboMovStepRng.getItems().clear();
		comboMovStepRng.getItems().addAll("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15");
				
	    restrict.set("[0-9]");
	    txtAzAtt.setPromptText("(max "+ATTEN+" )");
	    txtAzAtt.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (restrict.get() != null && !restrict.get().equals("") && !newValue.matches(restrict.get() + "*")) {
					txtAzAtt.setText(newValue);
                }

			}
		});
	    txtElAtt.setPromptText("(max "+ATTEN+" )");
	    txtElAtt.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (restrict.get() != null && !restrict.get().equals("") && !newValue.matches(restrict.get() + "*")) {
					txtElAtt.setText(newValue);
                }

			}
		});
	    
		
		AzTxCal.setSelected(false);
		AzRxCal.setSelected(false);
		ElTxCal.setSelected(false);
		ElRxCal.setSelected(false);
	}
	
	private boolean verifyData() {
		if(txtAzAtt.getText().isEmpty() || txtElAtt.getText().isEmpty()) {
			AppConfig.getInstance().openErrorDialog("Nothing entered in Azimuth/Elevation attentuation");
			return false;
		} else if(txtAzAtt.getText().length() > maxLength.intValue() || txtElAtt.getText().length()  > maxLength.intValue()) {
			AppConfig.getInstance().openErrorDialog("Invalid data entered in Azimuth/Elevation attenuation");
			return false;
		}
		
		int azAtt = Integer.parseInt(txtAzAtt.getText());
		int elAtt = Integer.parseInt(txtElAtt.getText());
		
		if(azAtt < 0 || azAtt > ATTEN) {
			AppConfig.getInstance().openErrorDialog("Azimuth attentuation value cannot exceed max limit (MAX: "+ATTEN+" )");
			return false;
		} 
		
		if(elAtt < 0 || elAtt > ATTEN) {
			AppConfig.getInstance().openErrorDialog("Elevation attentuation value cannot exceed max limit (MAX: "+ATTEN+" )");
			return false;
		}
		
		return true;
	}
	
	private void makeBiteMsg() {
		
		//BITE Msg
		if(bite)
			biteModeMsg.setBiteOnOff((byte) (comboStTarget.getValue().equals("0.0") ? 2:3));
		else
			biteModeMsg.setBiteOnOff((byte) 1);
		biteModeMsg.setStationaryTargetFlag((byte) comboStTarget.getSelectionModel().getSelectedIndex());
		
		biteModeMsg.setAzDoppler((byte) comboAzDop.getSelectionModel().getSelectedIndex());
		biteModeMsg.setAzBiteAttentuation((byte) Integer.parseInt(txtAzAtt.getText()));
		biteModeMsg.setElDoppler((byte) comboElDop.getSelectionModel().getSelectedIndex());
		biteModeMsg.setElBiteAttentuation((byte) Integer.parseInt(txtElAtt.getText()));
		
		biteModeMsg.setMovingTargetStartRange((byte) comboMovStartRng.getSelectionModel().getSelectedIndex());
		biteModeMsg.setMovingTargetStopRange((byte) comboMovStopRng.getSelectionModel().getSelectedIndex());
		biteModeMsg.setMovingTargetStepSize((byte) comboMovStepRng.getSelectionModel().getSelectedIndex());
	}
	
	private void makeCalibMsg() {
		
		//Calib Msg
		if(AzTxCal.isSelected())
			calibModeMsg.setAzAntenna((byte) 1);
		else if(AzRxCal.isSelected())
			calibModeMsg.setAzAntenna((byte) 2);
		else 
			calibModeMsg.setAzAntenna((byte) 0);
		
		if(ElTxCal.isSelected())
			calibModeMsg.setElAntenna((byte) 1);
		else if(ElRxCal.isSelected())
			calibModeMsg.setElAntenna((byte) 2);
		else 
			calibModeMsg.setElAntenna((byte) 0);
	}
	
	private void sendBiteUpdate() {
		//send BITE Msg
		AppConfig.getInstance().getFxmlController().sendRCBytes(biteModeMsg.encode().array());
	}
	
	private void sendCalibUpdate() {
		//send BITE Msg
		AppConfig.getInstance().getFxmlController().sendRCBytes(calibModeMsg.encode().array());
	}
}
