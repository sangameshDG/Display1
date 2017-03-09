package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import messages.radar.AntHealthStatusMsg;
import messages.radar.ExRxHealthStatusMsg;
import messages.radar.NoiseFigureStatusMsg;
import messages.radar.SDPHealthStatusMsg;

import org.apache.log4j.Logger;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import utils.Constance;
import admin.MasterSlavery;
import admin.User;
import application.AntennaSetUpController;
import application.DiamondOptionController;
import application.ExRxSetUpController;
import application.FXMLController;
import application.InstallationController;
import application.LoggingSetUpController;
import application.NumPadPanel;
import application.ReplayController;
import application.RunwaySetUpController;
import application.SdpSetUpController;
import application.TrackOptionController;
import application.TrackOptionControllerEl;
import application.ZoomController;

public class AppConfig {

	private static final Logger logger = Logger.getLogger(AppConfig.class);
	private static AppConfig instance = null;
	private static final String VOICE_NAME = "kevin16";
	private Voice voice;

	  protected AppConfig() {
	        try {
	            this.voice = VoiceManager.getInstance().getVoice(VOICE_NAME);
	            this.voice.allocate();
	        }
	        catch (Exception e) {
	            logger.error((Object)e);
	        }
	    }

	public static AppConfig getInstance() {
		if (instance == null) {
			instance = new AppConfig();
			logger.info("App Config Instantiated");
		}
		return instance;
	}

	FXMLController fxmlController;
	ReplayController replayController;
	TrackOptionController tController;
	TrackOptionControllerEl tControllerEl;
	DiamondOptionController dController;
	InstallationController iController;
	ExRxSetUpController exRxSetUpController;
	SdpSetUpController sdpSetUpController;
	LoggingSetUpController loggingSetUpController;
	AntennaSetUpController antSetUpController;
	NumPadPanel numPadPanel;
	User loggedUser;
	ZoomController zoomControl;
	RunwaySetUpController runwayController;
	

	ExRxHealthStatusMsg exrx;
	NoiseFigureStatusMsg nf;
	SDPHealthStatusMsg sdp;
	AntHealthStatusMsg azAntHealthStatusMsg;
	AntHealthStatusMsg elAntHealthStatusMsg;

	boolean isValidDisplaySetup = false;
	boolean isValidSystemSetup = false;
	boolean isValidRadarSetup = false;

	List<HBox> itemDisplaySetupList = new ArrayList<HBox>();
	double[] itemDisplaySetupVal = new double[] { Constance.ELEVATION_MAX,
			Constance.AZIMUTH_MAX, Constance.TOUCH_DOWN,

			Constance.ELEVATION.USL_ANGLE, Constance.ELEVATION.GLIDE_ANGLE,
			Constance.ELEVATION.GLIDE_MAX_DIST,
			Constance.ELEVATION.GLIDE_FLAT_START_DIST,
			Constance.ELEVATION.LSL_ANGLE, Constance.ELEVATION.UAL_ANGLE,
			Constance.ELEVATION.LAL_ANGLE, Constance.ELEVATION.USaL_ANGLE,
			Constance.ELEVATION.LSaL_ANGLE, Constance.ELEVATION.DH,

			Constance.AZIMUTH.LSL_ANGLE, Constance.AZIMUTH.RSL_ANGLE,
			Constance.AZIMUTH.RCLO, Constance.AZIMUTH.LAL_ANGLE,
			Constance.AZIMUTH.RAL_ANGLE, Constance.AZIMUTH.LSaL,
			Constance.AZIMUTH.RSaL };

	String[] itemSystemSetupVal = new String[] { Constance.PREF.RANGE_UNITS,
			Constance.GROUP_ADDR, String.valueOf(Constance.PORT_AZ_PLOTS),
			String.valueOf(Constance.PORT_AZ_TRACKS),
			String.valueOf(Constance.PORT_EL_PLOTS),
			String.valueOf(Constance.PORT_EL_TRACKS),
			String.valueOf(Constance.PORT_VIDEO),
			String.valueOf(Constance.PORT_WRITE) };
	


	public ExRxHealthStatusMsg getExrx() {
		return exrx;
	}

	public void setExrx(ExRxHealthStatusMsg exrx) {
		this.exrx = exrx;
	}
	
	public NoiseFigureStatusMsg getnf() {
		return nf;
	}

	public void setNf(NoiseFigureStatusMsg nf) {
		this.nf = nf;
	}

	public SDPHealthStatusMsg getSdp() {
		return sdp;
	}

	public void setSdp(SDPHealthStatusMsg sdp) {
		this.sdp = sdp;
	}

	public AntHealthStatusMsg getAzAntHealthStatusMsg() {
		return azAntHealthStatusMsg;
	}

	public void setAzAntHealthStatusMsg(AntHealthStatusMsg azAntHealthStatusMsg) {
		this.azAntHealthStatusMsg = azAntHealthStatusMsg;
	}

	public AntHealthStatusMsg getElAntHealthStatusMsg() {
		return elAntHealthStatusMsg;
	}

	public void setElAntHealthStatusMsg(AntHealthStatusMsg elAntHealthStatusMsg) {
		this.elAntHealthStatusMsg = elAntHealthStatusMsg;
	}

	public User getLoggedUser() {
		return loggedUser;
	}
	
	public void setZoomController(ZoomController zoomSetup){
		this.zoomControl=zoomSetup;
	}
	
	public ZoomController getZoomController(){
		return zoomControl;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
		if (fxmlController != null)
			fxmlController.updateUserProfile();
	}

	public FXMLController getFxmlController() {
		return fxmlController;
	}

	public void setFxmlController(FXMLController fxmlController) {
		this.fxmlController = fxmlController;
	}


	public RunwaySetUpController getRunwayController() {
		return runwayController;
	}


	public void setRunwayController(RunwaySetUpController runwayController) {
		this.runwayController = runwayController;
	}

	public ReplayController getReplayController() {
		return replayController;
	}

	public void setReplayController(ReplayController replayController) {
		this.replayController = replayController;
	}

	public TrackOptionController getTrackOptionController() {
		return tController;
	}
	
	public TrackOptionControllerEl getTrackOptionControllerEl() {
		return tControllerEl;
	}

	public void setDiamondController(DiamondOptionController dOptionController) {
		this.dController = dOptionController;
	}

	public DiamondOptionController getDiamondController() {
		return dController;
	}
	
	public void setSdpSetUpController(SdpSetUpController controller) {
		this.sdpSetUpController = controller;
	}

	public SdpSetUpController getSdpSetUpController() {
		return sdpSetUpController;
	}
	
	public void setLoggingSetUpController(LoggingSetUpController controller) {
		this.loggingSetUpController = controller;
	}

	public LoggingSetUpController getLoggingSetUpController() {
		return loggingSetUpController;
	}

	public void setExRxSetUpController(ExRxSetUpController controller) {
		this.exRxSetUpController = controller;
	}

	public ExRxSetUpController getExRxSetUpController() {
		return exRxSetUpController;
	}

	public AntennaSetUpController getAntSetUpController() {
		return antSetUpController;
	}

	public void setAntSetUpController(
			AntennaSetUpController azAntSetUpController) {
		this.antSetUpController = azAntSetUpController;
	}

	public void setInstallationController(
			InstallationController iOptionController) {
		this.iController = iOptionController;
	}

	public InstallationController getInstallationController() {
		return iController;
	}

	public NumPadPanel getNumPadPanel() {
		return numPadPanel;
	}

	public void setTrackOptionController(TrackOptionController tOptionController) {
		this.tController = tOptionController;
	}
	
	public void setTrackOptionControllerEl(TrackOptionControllerEl tOptionControllerEl) {
		this.tControllerEl = tOptionControllerEl;
	}

	public void addDisplaySetup(HBox hbox) {
		itemDisplaySetupList.add(hbox);
	}

	public int getDisplaySetupListSize() {
		return itemDisplaySetupList.size();
	}

	public List<HBox> getDisplaySetupList() {
		return itemDisplaySetupList;
	}

	public double getDisplaySetupListValue(int index) {
		return itemDisplaySetupVal[index];
	}

	public void setDisplaySetupListValue(int index, double value) {
		itemDisplaySetupVal[index] = value;
	}

	public void setSystemSetupListValue(int index, String value) {
		itemSystemSetupVal[index] = value;
	}

	public boolean isDisplaySetupValid() {
		return isValidDisplaySetup;
	}

	public boolean isSystemSetupValid() {
		return isValidSystemSetup;
	}

	public boolean isRadarSetupValid() {
		return isValidRadarSetup;
	}
	
	public void speakVoice(final String text) {
        new Thread(new Runnable(){
			@Override
        	public void run(){
				AppConfig.this.voice.speak(text);
 	        }
        }).start();     
    }
	
	public void locallySaveDisplaySetupData() {

		if (checkNullData()) {
			int i = 0;
			isValidDisplaySetup = true;
			Constance.ELEVATION_MAX = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 5, 10, Constance.UNITS.DEGREES);
			i++;
			Constance.AZIMUTH_MAX = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 5, 20, Constance.UNITS.DEGREES);
			i++;
			Constance.TOUCH_DOWN = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 0, 2, Constance.UNITS.getLENGTH());
			i++;

			Constance.ELEVATION.USL_ANGLE = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 5, 10, Constance.UNITS.DEGREES);
			i++;
			Constance.ELEVATION.GLIDE_ANGLE = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 1, 7.5, Constance.UNITS.DEGREES);
			i++;
			Constance.ELEVATION.GLIDE_MAX_DIST = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 5, 30, Constance.UNITS.getLENGTH());
			i++;
			Constance.ELEVATION.GLIDE_FLAT_START_DIST = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 3, 29, Constance.UNITS.getLENGTH());
			i++;

			if (Constance.ELEVATION.GLIDE_FLAT_START_DIST > Constance.ELEVATION.GLIDE_MAX_DIST) {
				isValidDisplaySetup = false;
				openErrorDialog("Glide path maximum distance value should be greater than the Glide path Flattering start distance");
			}

			Constance.ELEVATION.LSL_ANGLE = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 0, 90, Constance.UNITS.DEGREES);
			i++;
			Constance.ELEVATION.UAL_ANGLE = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), Constance.ELEVATION.GLIDE_ANGLE + 1,
					20, Constance.UNITS.DEGREES);
			i++;
			Constance.ELEVATION.LAL_ANGLE = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), Constance.ELEVATION.GLIDE_ANGLE - 1,
					20, Constance.UNITS.DEGREES);
			i++;
			Constance.ELEVATION.USaL_ANGLE = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), Constance.ELEVATION.GLIDE_ANGLE + 2.5,
					20, Constance.UNITS.DEGREES);
			i++;
			Constance.ELEVATION.LSaL_ANGLE = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), Constance.ELEVATION.GLIDE_ANGLE - 2.5,
					20, Constance.UNITS.DEGREES);
			i++;
			Constance.ELEVATION.DH = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 0.03*Constance.UNITS.getFACTOR_HEIGHT(), 300*Constance.UNITS.getFACTOR_HEIGHT(), Constance.UNITS.getHEIGHT());
			i++;

			Constance.AZIMUTH.LSL_ANGLE = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 3, 10, Constance.UNITS.DEGREES);
			i++;
			Constance.AZIMUTH.RSL_ANGLE = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), -10, -3, Constance.UNITS.DEGREES);
			i++;
			Constance.AZIMUTH.RCLO = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), -0.5, 0.5, Constance.UNITS.getLENGTH());
			i++;
			Constance.AZIMUTH.LAL_ANGLE = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 1, 15, Constance.UNITS.DEGREES);
			i++;
			Constance.AZIMUTH.RAL_ANGLE = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), -15, -1, Constance.UNITS.DEGREES);
			i++;
			Constance.AZIMUTH.LSaL = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), 3, 15, Constance.UNITS.DEGREES);
			i++;
			Constance.AZIMUTH.RSaL = itemDisplaySetupVal[i] = validateDoubleData(
					itemDisplaySetupVal[i],
					((TextField) (itemDisplaySetupList.get(i).getChildren()
							.get(1))).getText(),
					((Label) (itemDisplaySetupList.get(i).getChildren().get(0)))
							.getText(), -15, -3, Constance.UNITS.DEGREES);

		} else
			openErrorDialog("Value can't be empty!");

		if (isValidDisplaySetup) {
			Constance.IS_DISPLAY_SETUP = true;
			logger.info("DisplaySetup Data Saved");
		}
	}

	public void saveSystemSetupData() {
		if (isValidSystemSetup) {
			Constance.IS_SYSTEM_SETUP = true;
			logger.info("SystemSetup Data Saved");
		}
	}

	public void saveRadarSetupData() {
		if (isValidRadarSetup) {
			Constance.IS_RADAR_SETUP = true;
			logger.info("RadarSetup Data Saved");
		}
	}

	public void openInformationDialog(String msg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert alert = new Alert(AlertType.INFORMATION, msg,
						ButtonType.OK);
				alert.setTitle("Information");
				alert.showAndWait();
			}
		});
	}

	public boolean openConfirmationDialog(String msg) {
		Alert alert = new Alert(AlertType.CONFIRMATION, msg, ButtonType.NO,
				ButtonType.YES);
		alert.setTitle("Confirmation");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get().equals(ButtonType.YES))
			return true;
		return false;
	}

	public void openErrorDialog(String msg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
				alert.setTitle("Error");
				alert.showAndWait();
			}
		});
	}

	public void validateSystemSetupData() {
		for (int i = 0; i < itemSystemSetupVal.length; i++) {
			if (itemSystemSetupVal[i].isEmpty()) {
				isValidSystemSetup = false;
				break;
			}
			isValidSystemSetup = true;
		}
		if (!isValidSystemSetup) {
			openErrorDialog("Value can't be empty!");
		}
		logger.info("SystemSetup Data Validated");

	}

	private boolean checkNullData() {
		for (int i = 0; i < itemDisplaySetupList.size(); i++) {
			TextField tField = ((TextField) (itemDisplaySetupList.get(i)
					.getChildren().get(1)));
			if (tField.getText().isEmpty()) {
				((Label) (itemDisplaySetupList.get(i).getChildren().get(3)))
						.setTextFill(Color.RED);
				((Label) (itemDisplaySetupList.get(i).getChildren().get(3)))
						.setText("*");
				return false;
			}
		}
		logger.info("DisplaySetup Data Null Check");
		return true;
	}

	public double validateDoubleData(double actualVAl, String str,
			String title, double min, double max, String unit) {
		double val = actualVAl;
		if (!str.isEmpty()) {
			double check = Double.valueOf(str);
			if ((check <= max) && (check >= min)) {
				val = check;
				return val;
			}
		}
		isValidDisplaySetup = false;
		openErrorDialog(title + " value ranges from: " + min + " " + unit
				+ " to " + max + " " + unit
				+ ". \nPlease make suitable corrections");
		return val;
	}

	public int validateIntegerData(int actualVAl, String str, double min,
			double max, String title, String unit) {
		int val = actualVAl;
		if (!str.isEmpty()) {
			int check = Integer.valueOf(str);
			if ((check <= max) && (check >= min)) {
				val = check;
				return val;
			}
		}
		isValidSystemSetup = false;
		openErrorDialog(title + " value ranges from: " + min + " " + unit
				+ " to " + max + " " + unit
				+ ". \nPlease make suitable corrections");
		return val;
	}

	public void openNumPadPanel(String title) {
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			fxmlLoader.load(getClass().getResourceAsStream(
					"/application/NumPadPanel.fxml"));
			numPadPanel = (NumPadPanel) fxmlLoader.getController();
			numPadPanel.setTitle(title);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Parent root = (Parent) fxmlLoader.getRoot();
		Scene dialogNumPad = new Scene(root);
		dialog.setScene(dialogNumPad);
		dialog.setResizable(false);
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.centerOnScreen();
		dialog.showAndWait();
	}

	public boolean isDisplayHasPrevilege() {
		if (MasterSlavery.getInstance().isRemote()
				&& MasterSlavery.getInstance().isMaster())
			return true;
		else if (!MasterSlavery.getInstance().isRemote())
			openErrorDialog("You are in LOCAL mode");
		else if (!MasterSlavery.getInstance().isMaster())
			openErrorDialog("You are a SLAVE");
		return false;
	}

	public boolean isUserHasPrevilege() {
		if (getLoggedUser().getType().equals(Constance.USER_TECHNICAL)
				|| getLoggedUser().getType().equals(Constance.USER_MASTER))
			return true;
		openErrorDialog("You don't have enough privileges");
		return false;
	}
}
