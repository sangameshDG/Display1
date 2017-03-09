package application;

import java.net.URL;
import java.util.ResourceBundle;
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
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import model.AppConfig;
import model.ILayoutParam;

import org.apache.log4j.Logger;

import utils.AppUtils;
import utils.Constance;
import admin.UserPreference;

public class SystemSetUpController implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(DisplaySetUpController.class);
	
	@FXML AnchorPane SystemSetup;
	
	@FXML ComboBox<String> comboUnits;
	@FXML ComboBox<String> comboUnits_2;
	
	
	@FXML TextField dispId;
	@FXML TextField rrmIP;
	@FXML TextField rrmPort;
	@FXML TextField rc1Ip;
	@FXML TextField rc2Ip;
	@FXML TextField portSend;
	
	@FXML TextField groupIP;
	@FXML TextField portAzPlot;
	@FXML TextField portAzTrack;
	@FXML TextField portElPlot;
	@FXML TextField portElTrack;
	@FXML TextField portVideo;
	
	@FXML MaterialDesignButtonWidget restart;
	
	@FXML Label statusPort;
	
//	-------------For disabling the Display Scale and units values-----------------
	@FXML VBox sys_setup;
	@FXML HBox rangeUnits;
	
	AppConfig appConfig = AppConfig.getInstance();
	
	double initialX,initialY;
    private StringProperty restrict = new SimpleStringProperty(this, "restrict");

	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("System Setup Dialog Opened");
		addDraggableNode(SystemSetup);
		loadUserPrefValues();
		initDialog();
		loadDefault();
		showingSelected();
		
	}	

	private void showingSelected() {
		sys_setup.getChildren().remove(rangeUnits);	
	}

	@FXML
	protected void okClick(ActionEvent event) {
		if(appConfig.isUserHasPrevilege()) {
			if(Constance.IS_CONNECTED) {
				if(appConfig.openConfirmationDialog("Do you want to stop the network? (Warning: Data lose may occur)" )) {
					appConfig.getFxmlController().stopNetworkTask();
					initDialog();
					loadDefault();
					
					
				}
			} else {
				appConfig.validateSystemSetupData();
				if(appConfig.isSystemSetupValid() && isValidData()) {
					if(validateLocalSaving()) {
						saveUserPrefData();//Must be after above call
						appConfig.getFxmlController().updateUnits();
						appConfig.getFxmlController().handle40ButtonAction(null);
						appConfig.getFxmlController().startNetworkTask();
						appConfig.getFxmlController().notifyChanges();
						appConfig.getFxmlController().startRcNet();
						closeSettings(event);
						
					}
				}
			}
		}
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
		
		comboUnits.setDisable(Constance.IS_CONNECTED);
		dispId.setEditable(!Constance.IS_CONNECTED);
		rrmIP.setEditable(!Constance.IS_CONNECTED);
		rrmPort.setEditable(!Constance.IS_CONNECTED);
		rc1Ip.setEditable(!Constance.IS_CONNECTED);
		rc2Ip.setEditable(!Constance.IS_CONNECTED);
		groupIP.setEditable(!Constance.IS_CONNECTED);
		portAzPlot.setEditable(!Constance.IS_CONNECTED);
		portAzTrack.setEditable(!Constance.IS_CONNECTED);
		portElPlot.setEditable(!Constance.IS_CONNECTED);
		portElTrack.setEditable(!Constance.IS_CONNECTED);
		portVideo.setEditable(!Constance.IS_CONNECTED);
		portSend.setEditable(!Constance.IS_CONNECTED);
		
		restrict.set("[0-9]");
		styleTextValue(portAzPlot);
		styleTextValue(portAzTrack);
		styleTextValue(portElPlot);
		styleTextValue(portElTrack);
		styleTextValue(portVideo);
		styleTextValue(portSend);
		
		statusPort.setText("Port Number Changes Disabled: Connection Active");
		restart.setText("Stop");
		restart.widgetSetVal(VAL.RED);
		if(!Constance.IS_CONNECTED) {
			statusPort.setText("Port Number Changes Enabled: Connection Inactive");
			restart.setText("Start");
			restart.widgetSetVal(VAL.GREEN);
		}		
	}

	private void loadDefault() {
		
		dispId.setText(String.valueOf(Constance.DISPLAY_ID));
		rrmIP.setText(Constance.IP_RRM);
		rrmPort.setText(String.valueOf(Constance.PORT_RRM_WRITE));
		rc1Ip.setText(Constance.IP_RC_1);
		rc2Ip.setText(Constance.IP_RC_2);
		portSend.setText(String.valueOf(Constance.PORT_RC_WRITE));
		
		groupIP.setText(Constance.GROUP_ADDR);
		portAzPlot.setText(String.valueOf(Constance.PORT_AZ_PLOTS));
		portAzTrack.setText(String.valueOf(Constance.PORT_AZ_TRACKS));
		portElPlot.setText(String.valueOf(Constance.PORT_EL_PLOTS));
		portElTrack.setText(String.valueOf(Constance.PORT_EL_TRACKS));
		portVideo.setText(String.valueOf(Constance.PORT_VIDEO));
	}

	private boolean validateLocalSaving() {

		Constance.DISPLAY_ID = (byte) appConfig.validateIntegerData(Constance.DISPLAY_ID, dispId.getText(), 0, 255, "DISPLAY_ID", "");
		
		if(AppUtils.checkIPv4(rrmIP.getText()))
			Constance.IP_RRM = rrmIP.getText();
		else {
			appConfig.openErrorDialog("RRM IP: Invalid");
			return false;
		}
		Constance.PORT_RRM_WRITE = appConfig.validateIntegerData(Constance.PORT_RRM_WRITE, rrmPort.getText(), 2000, 10000, "PORT_RRM_WRITE", "");
		
		if(AppUtils.checkIPv4(rc1Ip.getText()))
			Constance.IP_RC_1 = rc1Ip.getText();
		else {
			appConfig.openErrorDialog("RC 1 IP: Invalid");
			return false;
		}
		if(AppUtils.checkIPv4(rc2Ip.getText()))
			Constance.IP_RC_2 = rc2Ip.getText();
		else {
			appConfig.openErrorDialog("RC 2 IP: Invalid");
			return false;
		}
		Constance.PORT_RC_WRITE = appConfig.validateIntegerData(Constance.PORT_RC_WRITE, portSend.getText(), 2000, 15000, "PORT_RC_WRITE", "");
		
		if(AppUtils.checkIPv4(groupIP.getText()))
			Constance.GROUP_ADDR = groupIP.getText();
		else {
			appConfig.openErrorDialog("GROUP_ADDR: Invalid");
			return false;
		}
		Constance.PORT_AZ_PLOTS = appConfig.validateIntegerData(Constance.PORT_AZ_PLOTS, portAzPlot.getText(), 2000, 10000, "PORT_AZ_PLOTS", "");
		Constance.PORT_AZ_TRACKS = appConfig.validateIntegerData(Constance.PORT_AZ_TRACKS, portAzTrack.getText(), 2000, 10000, "PORT_AZ_TRACKS", "");
		Constance.PORT_EL_PLOTS = appConfig.validateIntegerData(Constance.PORT_EL_PLOTS, portElPlot.getText(), 2000, 10000, "PORT_EL_PLOTS", "");
		Constance.PORT_EL_TRACKS = appConfig.validateIntegerData(Constance.PORT_EL_TRACKS, portElTrack.getText(), 2000, 10000, "PORT_EL_TRACKS", "");
		Constance.PORT_VIDEO = appConfig.validateIntegerData(Constance.PORT_VIDEO, portVideo.getText(), 2000, 10000, "PORT_VIDEO", "");
	
		return true;
	}

	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("System Setup Dialog Closed");
	}
	
	private void loadUserPrefValues() {
		UserPreference uPreference = UserPreference.getInstance();
		Constance.DISPLAY_ID = (byte) uPreference.getDISPLAY_ID();
		Constance.IP_RRM = uPreference.getIP_RRM();
		Constance.PORT_RRM_WRITE = uPreference.getPORT_RRM_WRITE();
		Constance.IP_RC_1 = uPreference.getIP_RC_1();
		Constance.IP_RC_2 = uPreference.getIP_RC_2();
		Constance.PORT_RC_WRITE = uPreference.getPORT_RC_WRITE();
		
		Constance.GROUP_ADDR = uPreference.getGROUP_ADDR();
		Constance.PORT_AZ_PLOTS = uPreference.getPORT_AZ_PLOTS();
		Constance.PORT_AZ_TRACKS = uPreference.getPORT_AZ_TRACKS();
		Constance.PORT_EL_PLOTS = uPreference.getPORT_EL_PLOTS();
		Constance.PORT_EL_TRACKS = uPreference.getPORT_EL_TRACKS();
		Constance.PORT_VIDEO = uPreference.getPORT_VIDEO();
		
	}
	
	private void saveUserPrefData() {
		
		UserPreference uPreference = UserPreference.getInstance();
		uPreference.setDISPLAY_ID(Constance.DISPLAY_ID);
		uPreference.setIP_RRM(Constance.IP_RRM);
		uPreference.setPORT_RRM_WRITE(Constance.PORT_RRM_WRITE);
		uPreference.setIP_RC_1(Constance.IP_RC_1);
		uPreference.setIP_RC_2(Constance.IP_RC_2);
		uPreference.setPORT_RC_WRITE(Constance.PORT_RC_WRITE);
		
		uPreference.setGROUP_ADDR(Constance.GROUP_ADDR);
		uPreference.setPORT_AZ_PLOTS(Constance.PORT_AZ_PLOTS);
		uPreference.setPORT_AZ_TRACKS(Constance.PORT_AZ_TRACKS);
		uPreference.setPORT_EL_PLOTS(Constance.PORT_EL_PLOTS);
		uPreference.setPORT_EL_TRACKS(Constance.PORT_EL_TRACKS);
		uPreference.setPORT_VIDEO(Constance.PORT_VIDEO);
	}
	
	private void styleTextValue(final TextField textField) {
		textField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				
				if (restrict.get() != null && !restrict.get().equals("") && !newValue.matches(restrict.get() + "*")) {
					textField.setText(oldValue);
                }
				
			}
		});
	}
	
	private boolean isValidData() {
		if((portAzPlot.getText().length() > 4) 
				||(portElPlot.getText().length() > 4)
				||(portAzTrack.getText().length() > 4)
				||(portElTrack.getText().length() > 4)
				||(portVideo.getText().length() > 4)
				||(portSend.getText().length() > 5)) {
			appConfig.openErrorDialog("Length can't exceed more than 4 digits");
			return false;
		}
		return true;
	}

}