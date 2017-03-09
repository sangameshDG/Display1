package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.AppConfig;
import model.ILayoutParam;

import org.apache.log4j.Logger;

import utils.Constance;
import admin.UserPreference;

public class DisplaySetUpController implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(DisplaySetUpController.class);

	@FXML AnchorPane Settings;
	
	@FXML VBox displayBox;
	
	@FXML HBox MaximumElevation;
	@FXML HBox MaximumAzimuth;
	@FXML HBox TouchDown;
	
	@FXML HBox ElevationUSL;
	@FXML HBox ElevationGlideAngle;
	@FXML HBox ElevationGlideMaxDist;
	@FXML HBox ElevationGlideFlatStartDist;
	@FXML HBox ElevationLSL;
	@FXML HBox ElevationUAL;
	@FXML HBox ElevationLAL;
	@FXML HBox ElevationUSaL;
	@FXML HBox ElevationLSaL;
	@FXML HBox ElevationDH;
	
	@FXML HBox AzimuthLSL;
	@FXML HBox AzimuthRSL;
	@FXML HBox AzimuthRCLO;
	@FXML HBox AzimuthLAL;
	@FXML HBox AzimuthRAL;
	@FXML HBox AzimuthLSaL;
	@FXML HBox AzimuthRSaL;

	AppConfig appConfig = AppConfig.getInstance();
	
	double initialX,initialY;
	
	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle rBundle) {
		logger.info("Display SetUp Dialog Opened");
		addDraggableNode(Settings);
		loadUserPrefValues();
		loadToList();
		initDefaultValues();
		initUnits();
		
		disableControls(false);
		showingSelected();
		
		Constance.IS_DISPLAY_SETUP = true;
	}

	@FXML
	protected void okClick(ActionEvent event) {
		appConfig.locallySaveDisplaySetupData();
		if(appConfig.isDisplaySetupValid()) {
			saveUserPrefData();//Must be after above call
			appConfig.getFxmlController().notifyChanges();//Must be 2 times
			appConfig.getFxmlController().notifyChanges();
			closeSettings(event);
		}		
	}

	@FXML
	protected void cancelClick(ActionEvent event) {
		closeSettings(event);
	}

	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("Display Setup Dialog Closed");
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
	
	private void disableControls(boolean master) {
		MaximumElevation.setDisable(master);
		MaximumAzimuth.setDisable(master);
		TouchDown.setDisable(master);
		ElevationGlideAngle.setDisable(master);
		ElevationGlideMaxDist.setDisable(master);
		ElevationGlideFlatStartDist.setDisable(master);
		ElevationUSL.setDisable(master);
		ElevationLSL.setDisable(master);
		ElevationUAL.setDisable(master);
		ElevationLAL.setDisable(master);
		ElevationUSaL.setDisable(master);
		ElevationLSaL.setDisable(master);
		ElevationDH.setDisable(master);
		AzimuthLSL.setDisable(master);
		AzimuthRSL.setDisable(master);
		AzimuthRCLO.setDisable(master);
		AzimuthLAL.setDisable(master);
		AzimuthRAL.setDisable(master);
		AzimuthLSaL.setDisable(master);
		AzimuthRSaL.setDisable(master);
	}
	
	private void showingSelected() {
		displayBox.getChildren().remove(TouchDown);
		displayBox.getChildren().remove(ElevationGlideAngle);
		displayBox.getChildren().remove(ElevationGlideMaxDist);
		displayBox.getChildren().remove(ElevationGlideFlatStartDist);
		displayBox.getChildren().remove(ElevationUAL);
		displayBox.getChildren().remove(ElevationLAL);
		displayBox.getChildren().remove(ElevationUSaL);
		displayBox.getChildren().remove(ElevationLSaL);
		
		displayBox.getChildren().remove(AzimuthRCLO);		
		displayBox.getChildren().remove(AzimuthLAL);
		displayBox.getChildren().remove(AzimuthRAL);
		displayBox.getChildren().remove(AzimuthLSaL);
		displayBox.getChildren().remove(AzimuthRSaL);
	}
	
	private void loadToList() {
		appConfig.getDisplaySetupList().clear();
		appConfig.addDisplaySetup(MaximumElevation);
		appConfig.addDisplaySetup(MaximumAzimuth);
		appConfig.addDisplaySetup(TouchDown);
		appConfig.addDisplaySetup(ElevationUSL);
		appConfig.addDisplaySetup(ElevationGlideAngle);
		appConfig.addDisplaySetup(ElevationGlideMaxDist);
		appConfig.addDisplaySetup(ElevationGlideFlatStartDist);
		appConfig.addDisplaySetup(ElevationLSL);
		appConfig.addDisplaySetup(ElevationUAL);
		appConfig.addDisplaySetup(ElevationLAL);
		appConfig.addDisplaySetup(ElevationUSaL);
		appConfig.addDisplaySetup(ElevationLSaL);
		appConfig.addDisplaySetup(ElevationDH);
		appConfig.addDisplaySetup(AzimuthLSL);
		appConfig.addDisplaySetup(AzimuthRSL);
		appConfig.addDisplaySetup(AzimuthRCLO);
		appConfig.addDisplaySetup(AzimuthLAL);
		appConfig.addDisplaySetup(AzimuthRAL);
		appConfig.addDisplaySetup(AzimuthLSaL);
		appConfig.addDisplaySetup(AzimuthRSaL);		
	}

	private void initDefaultValues() {
		
		//set Default label to NOTHING
		for(int i=0;i<appConfig.getDisplaySetupListSize();i++) {
			((Label)(appConfig.getDisplaySetupList().get(i).getChildren().get(3))).setText("");
		}
		
		//set TextField to their values
		for(int i=0;i<appConfig.getDisplaySetupListSize();i++) {
			((TextField)(appConfig.getDisplaySetupList().get(i).getChildren().get(1))).setText(String.valueOf(appConfig.getDisplaySetupListValue(i)));
		}
	}

	private void initUnits() {
		((Label) (TouchDown.getChildren().get(2))).setText(Constance.UNITS.getLENGTH());
		((Label) (ElevationDH.getChildren().get(2))).setText("meters");
		((Label) (ElevationGlideMaxDist.getChildren().get(2))).setText(Constance.UNITS.getLENGTH());
		((Label) (ElevationGlideFlatStartDist.getChildren().get(2))).setText(Constance.UNITS.getLENGTH());
		
		((Label) (AzimuthRCLO.getChildren().get(2))).setText(Constance.UNITS.getLENGTH());
		
	}	
	
	private void loadUserPrefValues() {
		UserPreference uPreference = UserPreference.getInstance();
		Constance.TOUCH_DOWN = uPreference.getTOUCH_DOWN();		
		Constance.ELEVATION.USL_ANGLE = uPreference.getEL_USL_ANGLE();
		Constance.ELEVATION.GLIDE_ANGLE = uPreference.getEL_GLIDE_ANGLE();
		Constance.ELEVATION.GLIDE_MAX_DIST = uPreference.getEL_GLIDE_MAX_DIST();
		Constance.ELEVATION.GLIDE_FLAT_START_DIST = uPreference.getEL_GLIDE_FLAT_START_DIST();
		Constance.ELEVATION.LSL_ANGLE = uPreference.getEL_LSL_ANGLE();
		Constance.ELEVATION.UAL_ANGLE = uPreference.getEL_UAL_ANGLE();
		Constance.ELEVATION.LAL_ANGLE = uPreference.getEL_LAL_ANGLE();
		Constance.ELEVATION.USaL_ANGLE = uPreference.getEL_USaL_ANGLE();
		Constance.ELEVATION.LSaL_ANGLE = uPreference.getEL_LSaL_ANGLE();
		Constance.ELEVATION.DH = uPreference.getEL_DH();
		
		Constance.AZIMUTH.LSL_ANGLE = uPreference.getAZ_LSL_ANGLE();
		Constance.AZIMUTH.RSL_ANGLE = uPreference.getAZ_RSL_ANGLE();
		Constance.AZIMUTH.RCLO = uPreference.getAZ_RCLO();
		Constance.AZIMUTH.LAL_ANGLE = uPreference.getAZ_LAL_ANGLE();
		Constance.AZIMUTH.RAL_ANGLE = uPreference.getAZ_RAL_ANGLE();
		Constance.AZIMUTH.LSaL = uPreference.getAZ_LSaL();
		Constance.AZIMUTH.RSaL = uPreference.getAZ_RSaL();
	}
	
	private void saveUserPrefData() {
		UserPreference uPreference = UserPreference.getInstance();
		uPreference.setTOUCH_DOWN(Constance.TOUCH_DOWN);
		uPreference.setEL_USL_ANGLE(Constance.ELEVATION.USL_ANGLE);
		uPreference.setEL_GLIDE_ANGLE(Constance.ELEVATION.GLIDE_ANGLE);
		uPreference.setEL_GLIDE_MAX_DIST(Constance.ELEVATION.GLIDE_MAX_DIST);
		uPreference.setEL_GLIDE_FLAT_START_DIST(Constance.ELEVATION.GLIDE_FLAT_START_DIST);
		uPreference.setEL_LSL_ANGLE(Constance.ELEVATION.LSL_ANGLE);
		uPreference.setEL_UAL_ANGLE(Constance.ELEVATION.UAL_ANGLE);
		uPreference.setEL_LAL_ANGLE(Constance.ELEVATION.LAL_ANGLE);
		uPreference.setEL_USaL_ANGLE(Constance.ELEVATION.USaL_ANGLE);
		uPreference.setEL_LSaL_ANGLE(Constance.ELEVATION.LSaL_ANGLE);
		uPreference.setEL_DH(Constance.ELEVATION.DH);
		
		uPreference.setAZ_LSL_ANGLE(Constance.AZIMUTH.LSL_ANGLE);
		uPreference.setAZ_RSL_ANGLE(Constance.AZIMUTH.RSL_ANGLE);
		uPreference.setAZ_RCLO(Constance.AZIMUTH.RCLO);
		uPreference.setAZ_LAL_ANGLE(Constance.AZIMUTH.LAL_ANGLE);
		uPreference.setAZ_RAL_ANGLE(Constance.AZIMUTH.RAL_ANGLE);
		uPreference.setAZ_LSaL(Constance.AZIMUTH.LSaL);
		uPreference.setAZ_RSaL(Constance.AZIMUTH.RSaL);
	}
}
