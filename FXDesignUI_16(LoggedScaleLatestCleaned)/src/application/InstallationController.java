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
//*H  Created: Dec 19, 2015
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import messages.app.MaintenanceModeMsg;
import messages.app.PcReadMsg;
import messages.app.PcWriteMsg;
import messages.radar.PcReadStatusMsg;
import model.AppConfig;
import model.ILayoutParam;
import utils.Constance;

import org.apache.log4j.Logger;

import admin.UserPreference;


public class InstallationController implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(InstallationController.class);
	
	@FXML AnchorPane InstallationSetup;
	
//	-------------For disabling the tilt values-----------------
	@FXML VBox installationBox;
	@FXML HBox rangeUnits;
	@FXML HBox elevationUnits;
	@FXML HBox rangeUnits1;
	@FXML HBox rangeUnits11;
	@FXML HBox updateUnit;
//-------------------------------------------------------------
	
	@FXML CheckBox azMotor;
	@FXML CheckBox horMotor;
	@FXML CheckBox verMotor;	
	@FXML TextField txtAzTilt;
	@FXML TextField txtElTilt;
	@FXML TextField txtAzAxis;
	@FXML Label lbl_PcWrite;
	@FXML MaterialDesignButtonWidget writePcSetting;
	
	@FXML TextField txtAzActPos;
	@FXML TextField txtHorActPos;
	@FXML TextField txtVerActPos;	
	@FXML Label lbl_PcRead;
	@FXML MaterialDesignButtonWidget readPcSetting;
	
	@FXML TextField txtMstcAz;
	@FXML TextField txtMstcEl;
	@FXML TextField txtAgc;
	@FXML ComboBox<String> comboFreq;
	
	@FXML Label lbl_MainMsg;
	
	@FXML TextField tailCount;
	
	double initialX,initialY;
	double azTilt = 0;
	double elTilt = 0;
	double azAxis = 0;
	double tailScan=0;
	
	private StringProperty pcReadText = new SimpleStringProperty(this, "");
	
	private MaintenanceModeMsg maintenanceModeMsg;
	private static final int AGC_VAL = 63;
	private static final int MSTC_VAL = 127;
	private static final int MSTC_VAL_TAIL =35;
	
	
	private IntegerProperty maxLength = new SimpleIntegerProperty(this, "maxLength", 5);
	
	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info(" Installation SetUp Dialog Opened");
		addDraggableNode(InstallationSetup);
		initObj();
		initDialog();
		loadUserPrefValues();
		//showingSelected();
		
	}

	/*private void showingSelected() {
		installationBox.getChildren().remove(rangeUnits);
		installationBox.getChildren().remove(elevationUnits);
		installationBox.getChildren().remove(rangeUnits1);
		installationBox.getChildren().remove(rangeUnits11);
		installationBox.getChildren().remove(updateUnit);	
	}*/
	

	@FXML
	public void updateClick(ActionEvent event) {
	
		if(verifyData()) {
			if(AppConfig.getInstance().isDisplayHasPrevilege()) {
				makeMsg();
				saveUserPrefData();
		        sendUpdate();
				lbl_MainMsg.setText("Command Sent");
			}
		}
		
	}
	
	@FXML
	public void updateClick2(ActionEvent event) {
	
		if(verifyDataTail()) {
				saveUserPrefDataTail();
				int count2 = Constance.TailCounter;
				Constance.TailCounter = (byte) tailScan;
				if(Constance.TailCounter < count2){
					AppConfig.getInstance().getFxmlController().onRefreshAction(null);
					AppConfig.getInstance().getFxmlController().onRefreshAction(null);
				}else{
					Constance.TailCounter =(byte) tailScan;
				}
		        
			}	
	  }
	
	
	private void saveUserPrefDataTail() {
        UserPreference uPreference = UserPreference.getInstance();
		uPreference.setTail(tailCount.getText());		
	}

	private boolean verifyDataTail() {
		if(tailCount.getText().isEmpty()) {
			AppConfig.getInstance().openErrorDialog("Nothing Entered in Tail History");
			return false;
		} else if(tailCount.getText().length() > maxLength.intValue()) {
			AppConfig.getInstance().openErrorDialog("Invalid data entered in Tail History");
			return false;
		}
		
		try {
			tailScan = Double.parseDouble(tailCount.getText());
		} catch (Exception e) {
			logger.error(e);
		}		
		if(tailScan <3 || tailScan >35) {
			AppConfig.getInstance().openErrorDialog("Tail History value should be between ( +3 to +35");
			return false;
		}	
		
		
		return true;
	}

	@FXML
	protected void cancelClick(ActionEvent event) {
		Constance.InstallStatus=0;
		closeSettings(event);
	}
	
	@FXML
	public void writePcSetting(ActionEvent event) {
		if(verifyPcWrite()) {
			if(AppConfig.getInstance().isDisplayHasPrevilege()) {
				//Send PC Write Msg
				PcWriteMsg pcWriteMsg = new PcWriteMsg();
				pcWriteMsg.setAzMotor((byte)(azMotor.isSelected() ? 1:0));
				pcWriteMsg.setHorMotor((byte)(horMotor.isSelected() ? 1:0));
				pcWriteMsg.setVerMotor((byte)(verMotor.isSelected() ? 1:0));
				pcWriteMsg.setAzTilt(azTilt);
				pcWriteMsg.setElTilt(elTilt);
				pcWriteMsg.setAzAxis((int) azAxis);
				AppConfig.getInstance().getFxmlController().sendRCBytes(pcWriteMsg.encode().array());
				lbl_PcWrite.setText("Command Sent");
				
				//save
				UserPreference uPreference = UserPreference.getInstance();
				uPreference.setAZ_MOTOR(azMotor.isSelected() ? "true": "false");
				uPreference.setHOR_MOTOR(horMotor.isSelected() ? "true": "false");
				uPreference.setVER_MOTOR(verMotor.isSelected() ? "true": "false");
				uPreference.setAZ_TILT(txtAzTilt.getText());
				uPreference.setEL_TILT(txtElTilt.getText());
				uPreference.setAZ_AXIS(txtAzAxis.getText());
				
				//notify UI
				AppConfig.getInstance().getFxmlController().invalidate();
			}
		}
	}
	
	@FXML
	protected void readPcSetting(ActionEvent event) {
		if(AppConfig.getInstance().isDisplayHasPrevilege()) {
			//send PC read msg
			PcReadMsg pcReadMsg = new PcReadMsg();
			pcReadMsg.setReadPc(1);
			AppConfig.getInstance().getFxmlController().sendRCBytes(pcReadMsg.encode().array());
			pcReadText.set("Command Sent");
			logger.info("Command Sent: "+pcReadMsg.getMsgId());
			
			//Listen to N/W
			pcReadText.set("Listening.... ");
		}
	}
	
	public void updateReadPc(PcReadStatusMsg pcReadStatusMsg) {
		if(pcReadStatusMsg!=null) {
			float div = 0.001f;
			txtAzActPos.setText(String.valueOf(pcReadStatusMsg.getAzActualPos()*div));
			txtHorActPos.setText(String.valueOf(pcReadStatusMsg.getHorActualPos()*div));
			txtVerActPos.setText(String.valueOf(pcReadStatusMsg.getVerActualPos()*div));
			pcReadText.set("Updated!");
		} else {
			pcReadText.set("No response");
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
	    logger.info("Installation Setup Dialog Closed");
	}

	private void initObj() {
		maintenanceModeMsg = new MaintenanceModeMsg();
	}
	
	private void loadUserPrefValues() {
		UserPreference uPreference = UserPreference.getInstance();
		if(uPreference.getAZ_MOTOR().contains("true"))
			azMotor.setSelected(true);

		if(uPreference.getHOR_MOTOR().contains("true"))
			horMotor.setSelected(true);

		if(uPreference.getVER_MOTOR().contains("true"))
			verMotor.setSelected(true);
		
		txtAzTilt.setText(uPreference.getAZ_TILT());
		txtElTilt.setText(uPreference.getEL_TILT());
		txtAzAxis.setText(uPreference.getAZ_AXIS());
		
		txtAzActPos.setText(uPreference.getAZ_ACT_POS());
		txtHorActPos.setText(uPreference.getHOR_ACT_POS());		
		txtVerActPos.setText(uPreference.getVER_ACT_POS());
		
		txtMstcAz.setText(uPreference.getMSTC_LAW_AZ());
		txtMstcEl.setText(uPreference.getMSTC_LAW_EL());
		txtAgc.setText(uPreference.getAGC_LAW());
		
		tailCount.setText(uPreference.getTailscan());
		
		int freqIndex = Integer.parseInt(uPreference.getMAINTAIN_FREQ());
		comboFreq.getSelectionModel().select(freqIndex);
	}
	
	private void saveUserPrefData() {
		UserPreference uPreference = UserPreference.getInstance();
		
		uPreference.setMSTC_LAW_AZ(txtMstcAz.getText());
		uPreference.setMSTC_LAW_EL(txtMstcEl.getText());
		uPreference.setAGC_LAW(txtAgc.getText());
		uPreference.setMAINTAIN_FREQ(String.valueOf(comboFreq.getSelectionModel().getSelectedIndex()));
		
	}
	
	private void initDialog() {
		lbl_PcWrite.setText("");
		pcReadText.set("");
		lbl_PcRead.textProperty().bind(pcReadText);
		
		FXMLController.setNodeStyle(txtAzActPos);
		FXMLController.setNodeStyle(txtHorActPos);
		FXMLController.setNodeStyle(txtVerActPos);
		
		StringProperty doubleRestrict = new SimpleStringProperty(this, "restrict");
		doubleRestrict.set("[0-9]+-");
	    txtAzTilt.setPromptText("(-5 to 10)");
	    txtAzTilt.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (doubleRestrict.get() != null && !doubleRestrict.get().equals("") && !newValue.matches(doubleRestrict.get() + "*")) {
					txtAzTilt.setText(newValue);
                }

			}
		});
	    txtElTilt.setPromptText("(-5 to 10)");
	    txtElTilt.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (doubleRestrict.get() != null && !doubleRestrict.get().equals("") && !newValue.matches(doubleRestrict.get() + "*")) {
					txtElTilt.setText(newValue);
                }

			}
		});
	    
	    txtAzAxis.setPromptText("(0 to 290)");
	    txtAzAxis.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (doubleRestrict.get() != null && !doubleRestrict.get().equals("") && !newValue.matches(doubleRestrict.get() + "*")) {
					txtAzAxis.setText(newValue);
                }

			}
		});

	    
	    StringProperty restrict = new SimpleStringProperty(this, "restrict");
	    restrict.set("[0-9]");
	    txtAgc.setPromptText("(max "+AGC_VAL+" )");
	    txtAgc.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (restrict.get() != null && !restrict.get().equals("") && !newValue.matches(restrict.get() + "*")) {
					txtAgc.setText(newValue);
                }

			}
		});
	    txtMstcAz.setPromptText("(max "+MSTC_VAL+" )");
	    txtMstcAz.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (restrict.get() != null && !restrict.get().equals("") && !newValue.matches(restrict.get() + "*")) {
					txtMstcAz.setText(newValue);
                }

			}
		});
	    txtMstcEl.setPromptText("(max "+MSTC_VAL+" )");
	    txtMstcEl.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (restrict.get() != null && !restrict.get().equals("") && !newValue.matches(restrict.get() + "*")) {
					txtMstcEl.setText(newValue);
                }

			}
		});
	    
	   tailCount.setPromptText("(max "+MSTC_VAL_TAIL+" )");
	   tailCount.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (restrict.get() != null && !restrict.get().equals("") && !newValue.matches(restrict.get() + "*")) {
					tailCount.setText(newValue);
                }

			}
		});
		
		String[] incVal = new String[22];
		int count = 0;
		DecimalFormat df = new DecimalFormat("0.##");
		for(double i=9;i<=9.20;i=i+0.01)
			incVal[++count] = String.valueOf(df.format(i));		
		comboFreq.getItems().clear();
		comboFreq.getItems().addAll(incVal);
	}
	
	private boolean verifyPcWrite() {
		if(txtAzTilt.getText().isEmpty() || txtElTilt.getText().isEmpty() || txtAzAxis.getText().isEmpty()) {
			AppConfig.getInstance().openErrorDialog("Nothing Entered in either Azimuth/Elevation Tilt/Axis");
			return false;
		} else if(txtAzTilt.getText().length() > maxLength.intValue() || txtElTilt.getText().length()  > maxLength.intValue() || txtAzAxis.getText().length()  > maxLength.intValue()) {
			AppConfig.getInstance().openErrorDialog("Invalid data entered in either Azimuth/Elevation Tilt");
			return false;
		}
		
		try {
			azTilt = Double.parseDouble(txtAzTilt.getText());
			elTilt = Double.parseDouble(txtElTilt.getText());
			azAxis = Double.parseDouble(txtAzAxis.getText());
		} catch (Exception e) {
			logger.error(e);
		}		
		if(azTilt < -5 || azTilt > 5) {
			AppConfig.getInstance().openErrorDialog("Azimuth Tilt value cannot exceed limit ( -5 to +5 )");
			return false;
		}		
		if(elTilt < -5 || elTilt > 5) {
			AppConfig.getInstance().openErrorDialog("Elevation Tilt value cannot exceed limit ( -5 to +5 )");
			return false;
		}
		if(azAxis < 0 || azAxis > 290) {
			AppConfig.getInstance().openErrorDialog("Azimuth Axis value cannot exceed limit ( 0 to +290 )");
			return false;
		}
		
		return true;
	}
	
	private boolean verifyData() {
		
		if(txtAgc.getText().isEmpty() || txtMstcAz.getText().isEmpty() || txtMstcEl.getText().isEmpty()) {
			AppConfig.getInstance().openErrorDialog("Nothing Entered in either AGC, MSTC LAW");
			return false;
		} else if(txtAgc.getText().length() > maxLength.intValue() || txtMstcAz.getText().length()  > maxLength.intValue()
				|| txtMstcEl.getText().length()  > maxLength.intValue()) {
			AppConfig.getInstance().openErrorDialog("Invalid data entered in either AGC, MSTC LAW");
			return false;
		}
		
		int agc = Integer.parseInt(txtAgc.getText());
		int mstcAz = Integer.parseInt(txtMstcAz.getText());
		int mstcEl = Integer.parseInt(txtMstcEl.getText());		
		if(agc < 0 || agc > AGC_VAL) {
			AppConfig.getInstance().openErrorDialog("AGC value cannot exceed max limit (MAX: "+AGC_VAL+" )");
			return false;
		}		
		if(mstcAz < 0 || mstcAz > MSTC_VAL) {
			AppConfig.getInstance().openErrorDialog("MSTC Law Azimuth value cannot exceed max limit (MAX: "+MSTC_VAL+" )");
			return false;
		}		
		if(mstcEl < 0 || mstcEl > MSTC_VAL) {
			AppConfig.getInstance().openErrorDialog("MSTC Law Elevation value cannot exceed max limit (MAX: "+MSTC_VAL+" )");
			return false;
		}
		
		return true;
	}
	
	private void makeMsg() {
		
		//Maintenance Msg
		maintenanceModeMsg.setAgc((byte)Integer.parseInt(txtAgc.getText()));
		maintenanceModeMsg.setMstcAz((byte)Integer.parseInt(txtMstcAz.getText()));
		maintenanceModeMsg.setMstcEl((byte)Integer.parseInt(txtMstcEl.getText()));
		maintenanceModeMsg.setFreq((byte)comboFreq.getSelectionModel().getSelectedIndex());
		
	}
	
	private void sendUpdate() {
		//send Maintenance Msg
		AppConfig.getInstance().getFxmlController().sendRCBytes(maintenanceModeMsg.encode().array());		
	}

}
