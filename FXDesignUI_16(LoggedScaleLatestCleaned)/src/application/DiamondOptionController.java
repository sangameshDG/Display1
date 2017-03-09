

package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Light.Point;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import model.AppConfig;
import model.ILayoutParam;
import model.MatrixRef;
import model.data.ConfigParam;
import model.data.ConfigParam.CR;
import model.data.DisplayParam;
import model.data.GeneralParam;
import model.data.security.TrippleDes;

import org.apache.log4j.Logger;

import utils.AppUtils;
import utils.Constance;
import utils.DrawingUtils;
import views.ResizableCanvas;
import admin.UserPreference;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class DiamondOptionController implements Initializable, ILayoutParam {

	private static final Logger logger = Logger
			.getLogger(DiamondOptionController.class);
	public static final String PATTERN_SEP = "|-|-|-|-|";

	@FXML
	AnchorPane diamondOptionDialog;
	@FXML
	MaterialDesignButtonWidget closeX;
	@FXML
	MaterialDesignButtonWidget btn_save;
	@FXML
	MaterialDesignButtonWidget btn_load;
	@FXML
	MaterialDesignButtonWidget btn_export;

	@FXML
	TextField rc1Ip;
	@FXML
	TextField rc2Ip;
	@FXML
	TextField portSend;
	@FXML
	TextField txtMslOff;
	
	@FXML
	TextField txtAzRsl;
	@FXML
	TextField txtAzLsl;
	@FXML
	TextField txtAzRal;
	@FXML
	TextField txtAzLal;
	@FXML
	ComboBox<String> comboGlideAngle;
	@FXML
	TextField txtElUsl;
	@FXML
	TextField txtElLsl;
	@FXML
	TextField txtElRal;
	@FXML
	TextField txtElLal;

	@FXML
	TextField txtGlideDistFixedHeight;
	@FXML
	TextField txtGlideAngleMaxDist;
	@FXML
	CheckBox displayParamCheck;

	@FXML
	ComboBox<String> comboRunway;
	@FXML
	TextField txtAzAxis;
	@FXML
	TextField txtMinRng;
	@FXML
	TextField txtAzTilt;
	@FXML
	TextField txtElTilt;
	@FXML
	TextField txtXoffset;
	@FXML
	TextField txtYoffset;

	// These fields are used to get Min range.
	@FXML
	TextField azSpotRng1;
	@FXML
	TextField azSpotRng2;
	@FXML
	TextField azSpotRng3;
	@FXML
	TextField azSpotRng4;
	@FXML
	TextField azSpotRng5;
	@FXML
	TextField azSpotRng6;
	
	// These fields are used to get MAx range.
	@FXML
	TextField azSpotRng1_1;
	@FXML
	TextField azSpotRng2_1;
	@FXML
	TextField azSpotRng3_1;
	@FXML
	TextField azSpotRng4_1;
	@FXML
	TextField azSpotRng5_1;
	@FXML
	TextField azSpotRng6_1;

	// These fields are used to get Min angle.
	@FXML
	TextField azSpotAng1;
	@FXML
	TextField azSpotAng2;
	@FXML
	TextField azSpotAng3;
	@FXML
	TextField azSpotAng4;
	@FXML
	TextField azSpotAng5;
	@FXML
	TextField azSpotAng6;
	
	// These fields are used to get Max angle.
	@FXML
	TextField azSpotAng1_1;
	@FXML
	TextField azSpotAng2_1;
	@FXML
	TextField azSpotAng3_1;
	@FXML
	TextField azSpotAng4_1;
	@FXML
	TextField azSpotAng5_1;
	@FXML
	TextField azSpotAng6_1;

	@FXML
	CheckBox azSpot1;
	@FXML
	CheckBox azSpot2;
	@FXML
	CheckBox azSpot3;
	@FXML
	CheckBox azSpot4;
	@FXML
	CheckBox azSpot5;
	@FXML
	CheckBox azSpot6;
	
	@FXML VBox disp_Paramtr;
	@FXML HBox aZRsafety;
	@FXML HBox azLsafety;
	@FXML HBox elUsafety;
	@FXML HBox elLsafety;
	@FXML HBox elLerror;
	@FXML HBox elUerror;
	@FXML HBox aZRAL;
	@FXML HBox aZLAL;
	@FXML Label errorMargin;
	@FXML Label errorMarginEL;
	@FXML Label aZLabel;
	@FXML HBox includeDispParam;
	
	double initialX, initialY;
	ResizableCanvas elCanvas;
	ResizableCanvas azCanvas;

	List<Double> azRng;
	List<Double> azAng;
    List<Double> AzCornerRange=new ArrayList<Double>();
    List<Double> AzCornerAngle=new ArrayList<Double>();
	public static List<ConfigParam> configList = new ArrayList<ConfigParam>();

	boolean isValid;

	private StringProperty restrict = new SimpleStringProperty(this, "restrict");

	// for JSON
	GeneralParam genParam;

	File selectedFile = null;

	@Override
	public void draw(GraphicsContext gc) {

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Config Options Dialog Opened");
		addDraggableNode(diamondOptionDialog);
		loadPref();
		initDialog();
		populateGenParam();
		showingSelected();
	}
	
	private void showingSelected() {
		disp_Paramtr.getChildren().remove(aZRsafety);
		disp_Paramtr.getChildren().remove(azLsafety);
		disp_Paramtr.getChildren().remove(elUsafety);
		disp_Paramtr.getChildren().remove(elLsafety);
		disp_Paramtr.getChildren().remove(elLerror);
		disp_Paramtr.getChildren().remove(elUerror);
		disp_Paramtr.getChildren().remove(aZRAL);
		disp_Paramtr.getChildren().remove(aZLAL);
		disp_Paramtr.getChildren().remove(errorMargin);
		disp_Paramtr.getChildren().remove(errorMarginEL);
		disp_Paramtr.getChildren().remove(aZLabel);
		disp_Paramtr.getChildren().remove(includeDispParam);
	}
	

	@FXML
	protected void closeClick(ActionEvent event) {
		closeActivity(event);
	}

	@FXML
	protected void exportClick(ActionEvent event) {
		verifyAllItems();
		if (isValid) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(!exportData());
					AppConfig.getInstance().openInformationDialog("Radar Config File exported");

				}
			}).start();
		}
	}

	@FXML
	protected void loadClick(ActionEvent event) {
		clearGenPa(null);
		clearConfigPa(null);
		readData(event);
		if (selectedFile != null) {
			loadGenParamData();
			saveGenParamPref();
			loadConfigParamData();

			// ui update
			populateGenParam();
			loadValues(adjustRunayVal(comboRunway.getValue()));
			selectedFile = null;
		}
	}

	@FXML
	protected void saveClick(ActionEvent event) {
		isValid = true;
		verifyGeneralItems();
		verifyEachRunwayItems(adjustRunayVal(comboRunway.getSelectionModel().getSelectedItem()));
		if (isValid) {
			saveGenParamPref();
			savePrefEachRunway(adjustRunayVal(comboRunway.getSelectionModel()
					.getSelectedItem()));
			AppConfig.getInstance().openInformationDialog(
					"Configuration successfully saved.");
			//AppConfig.getInstance().getFxmlController().notifyChanges(); //Added to make the changes on display once user click on the save button.
		}
	}
	
	private String adjustRunayVal(String rNum){
		String run="NO ";
		if(rNum==" 09 "){
			run=" 1 ";
		}
		else if(rNum==" 09  "){
			run=" 2 ";
		}
		else if(rNum==" 27 "){
			run=" 3 ";
		}
		else if(rNum==" 27  "){
			run=" 4 ";
		}
		else{
			logger.info("Not selected any Runway");
		}
		return run;
	}
	
	
	
	@FXML
	protected void clearGenPa(ActionEvent event) {
		rc1Ip.setText("");
		rc2Ip.setText("");
		portSend.setText("");
		txtMslOff.setText("");

		txtAzRsl.setText("");
		txtAzLsl.setText("");
		txtAzRal.setText("");
		txtAzLal.setText("");

		txtElUsl.setText("");
		txtElLsl.setText("");
		txtElRal.setText("");
		txtElLal.setText("");

		txtGlideDistFixedHeight.setText("");
		txtGlideAngleMaxDist.setText("");
	}

	@FXML
	protected void clearConfigPa(ActionEvent event) {
		txtAzAxis.setText("");
		txtMinRng.setText("");
		txtElTilt.setText("");
		txtAzTilt.setText("");
		txtXoffset.setText("");
		txtYoffset.setText("");

		azSpotRng1.setText("");
		azSpotRng2.setText("");
		azSpotRng3.setText("");
		azSpotRng4.setText("");
		azSpotRng5.setText("");
		azSpotRng6.setText("");

		azSpotAng1.setText("");
		azSpotAng2.setText("");
		azSpotAng3.setText("");
		azSpotAng4.setText("");
		azSpotAng5.setText("");
		azSpotAng6.setText("");
		
		azSpotRng1_1.setText("");
		azSpotRng2_1.setText("");
		azSpotRng3_1.setText("");
		azSpotRng4_1.setText("");
		azSpotRng5_1.setText("");
		azSpotRng6_1.setText("");

		azSpotAng1_1.setText("");
		azSpotAng2_1.setText("");
		azSpotAng3_1.setText("");
		azSpotAng4_1.setText("");
		azSpotAng5_1.setText("");
		azSpotAng6_1.setText("");
		
	}

	public void setElCanvas(ResizableCanvas elCanvas) {
		this.elCanvas = elCanvas;
	}

	public void setAzCanvas(ResizableCanvas azCanvas) {
		this.azCanvas = azCanvas;
	}

	private void closeActivity(ActionEvent event) {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
		logger.info("Config Options Dialog Closed");
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
					node.getScene().getWindow()
							.setX(me.getScreenX() - initialX);
					node.getScene().getWindow()
							.setY(me.getScreenY() - initialY);
				}
			}
		});
	}

	private void initDialog() {
		
		String[] incVal = new String[16];
		for(double i=0;i<=7.5;i=i+0.5)
				incVal[(int) (2*i)] = String.valueOf(i);
		comboGlideAngle.getItems().clear();
		comboGlideAngle.getItems().addAll(incVal);
		
		comboGlideAngle.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				double agl = Double.parseDouble(newValue);
				txtElUsl.setText(""+(agl + 2.5));
				txtElLsl.setText(""+(agl - 2.5));
				txtElRal.setText(""+(agl + 2));
				txtElLal.setText(""+(agl - 2));
			}
		});
		
		
		comboRunway.getItems().clear();
		comboRunway.getItems().addAll(" 09 ", " 09  ", " 27 ", " 27  ");
		comboRunway.getSelectionModel().select(0);

		//loadValues(comboRunway.getValue());
		loadValues(adjustRunayVal(comboRunway.getValue()));
		comboRunway.valueProperty().addListener(new ChangeListener<String>() {

			//@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				isValid = true;
				if (isValid) {
					savePrefEachRunway(adjustRunayVal(oldValue));
					clearConfigPa(null);
					loadValues(adjustRunayVal(newValue));
					
				}
			}
		});

		azSpot1.setSelected(true);
		azSpot2.setSelected(true);
		azSpot3.setSelected(true);
		azSpot4.setSelected(true);
		azSpot5.setSelected(true);
		azSpot6.setSelected(true);

		for (int i = 0; i < 4; i++)
			configList.add(i, new ConfigParam());

		restrict.set("[0-9+-]");
		
	}

	private void verifyAllItems() {
		logger.info("verifying all");
		isValid = true;
		verifyGeneralItems();
		
		for(int i=1;i<=4;i++) {
			isValid = true;
			loadValues(" "+i+" ");
			verifyEachRunwayItems(" "+i+" ");
			if (isValid) {
				savePrefEachRunway(" "+i+" ");
			}
			logger.info("Loaded: RW"+i);
		}
	}

	private void populateGenParam() {
		rc1Ip.setText(Constance.IP_RC_1);
		rc2Ip.setText(Constance.IP_RC_2);
		portSend.setText(String.valueOf(Constance.PORT_RC_WRITE));
		txtMslOff.setText(Constance.GENERAL_MSL);

		txtAzRsl.setText(String.valueOf(Constance.AZIMUTH.RSaL));
		txtAzLsl.setText(String.valueOf(Constance.AZIMUTH.LSaL));
		txtAzRal.setText(String.valueOf(Constance.AZIMUTH.RAL_ANGLE));
		txtAzLal.setText(String.valueOf(Constance.AZIMUTH.LAL_ANGLE));

		int index = comboGlideAngle.getItems().indexOf(String.valueOf(Constance.ELEVATION.GLIDE_ANGLE));
		comboGlideAngle.getSelectionModel().select(index);
		
		txtElUsl.setText(String.valueOf(Constance.ELEVATION.USaL_ANGLE));
		txtElLsl.setText(String.valueOf(Constance.ELEVATION.LSaL_ANGLE));
		txtElRal.setText(String.valueOf(Constance.ELEVATION.UAL_ANGLE));
		txtElLal.setText(String.valueOf(Constance.ELEVATION.LAL_ANGLE));

		txtGlideDistFixedHeight.setText(String
				.valueOf(Constance.ELEVATION.GLIDE_FLAT_START_DIST));
		txtGlideAngleMaxDist.setText(String
				.valueOf(Constance.ELEVATION.GLIDE_MAX_DIST));
	}

	private void loadPref() {
		UserPreference uPreference = UserPreference.getInstance();

		Constance.IP_RC_1 = uPreference.getIP_RC_1();
		Constance.IP_RC_2 = uPreference.getIP_RC_2();
		Constance.PORT_RC_WRITE = uPreference.getPORT_RC_WRITE();
		Constance.GENERAL_MSL = uPreference.getGENERAL_MSL();

		Constance.AZIMUTH.RSaL = uPreference.getAZ_RSaL();
		Constance.AZIMUTH.LSaL = uPreference.getAZ_LSaL();
		Constance.AZIMUTH.RAL_ANGLE = uPreference.getAZ_RAL_ANGLE();
		Constance.AZIMUTH.LAL_ANGLE = uPreference.getAZ_LAL_ANGLE();

		Constance.ELEVATION.GLIDE_ANGLE = uPreference.getEL_GLIDE_ANGLE();
		Constance.ELEVATION.USaL_ANGLE = uPreference.getEL_USaL_ANGLE();
		Constance.ELEVATION.LSaL_ANGLE = uPreference.getEL_LSaL_ANGLE();
		Constance.ELEVATION.UAL_ANGLE = uPreference.getEL_UAL_ANGLE();
		Constance.ELEVATION.LAL_ANGLE = uPreference.getEL_LAL_ANGLE();

		Constance.ELEVATION.GLIDE_FLAT_START_DIST = uPreference
				.getEL_GLIDE_FLAT_START_DIST();
		Constance.ELEVATION.GLIDE_MAX_DIST = uPreference.getEL_GLIDE_MAX_DIST();
	}

	private void saveGenParamPref() {
		UserPreference uPreference = UserPreference.getInstance();

		uPreference.setIP_RC_1(Constance.IP_RC_1);
		uPreference.setIP_RC_2(Constance.IP_RC_2);
		uPreference.setPORT_RC_WRITE(Constance.PORT_RC_WRITE);
		uPreference.setGENERAL_MSL(Constance.GENERAL_MSL);

		uPreference.setAZ_RSaL(Constance.AZIMUTH.RSaL);
		uPreference.setAZ_LSaL(Constance.AZIMUTH.LSaL);
		uPreference.setAZ_RAL_ANGLE(Constance.AZIMUTH.RAL_ANGLE);
		uPreference.setAZ_LAL_ANGLE(Constance.AZIMUTH.LAL_ANGLE);

		uPreference.setEL_GLIDE_ANGLE(Constance.ELEVATION.GLIDE_ANGLE);
		uPreference.setEL_USaL_ANGLE(Constance.ELEVATION.USaL_ANGLE);
		uPreference.setEL_LSaL_ANGLE(Constance.ELEVATION.LSaL_ANGLE);
		uPreference.setEL_UAL_ANGLE(Constance.ELEVATION.UAL_ANGLE);
		uPreference.setEL_LAL_ANGLE(Constance.ELEVATION.LAL_ANGLE);

		uPreference.setEL_GLIDE_FLAT_START_DIST(Constance.ELEVATION.GLIDE_FLAT_START_DIST);		
		uPreference.setEL_GLIDE_MAX_DIST(Constance.ELEVATION.GLIDE_MAX_DIST);

	}

	private void savePrefEachRunway(String key) {
		UserPreference uPreference = UserPreference.getInstance();

		StringBuilder sb = new StringBuilder();
		sb.append(txtAzAxis.getText() + ",");
		sb.append(txtMinRng.getText() + ",");
		sb.append(txtElTilt.getText() + ",");
		sb.append(txtAzTilt.getText() + ",");
		sb.append(txtXoffset.getText() + ",");
		sb.append(txtYoffset.getText() + ",");

		sb.append(azSpotRng1.getText() + ",");
		sb.append(azSpotAng1.getText() + ",");
		sb.append(azSpotRng1_1.getText() + ",");
		sb.append(azSpotAng1_1.getText() + ",");
		
		sb.append(azSpotRng2.getText() + ",");
		sb.append(azSpotAng2.getText() + ",");
		sb.append(azSpotRng2_1.getText() + ",");
		sb.append(azSpotAng2_1.getText() + ",");
		
		sb.append(azSpotRng3.getText() + ",");
		sb.append(azSpotAng3.getText() + ",");
		sb.append(azSpotRng3_1.getText() + ",");
		sb.append(azSpotAng3_1.getText() + ",");
		
		sb.append(azSpotRng4.getText() + ",");
		sb.append(azSpotAng4.getText() + ",");
		sb.append(azSpotRng4_1.getText() + ",");
		sb.append(azSpotAng4_1.getText() + ",");
		
		sb.append(azSpotRng5.getText() + ",");
		sb.append(azSpotAng5.getText() + ",");
		sb.append(azSpotRng5_1.getText() + ",");
		sb.append(azSpotAng5_1.getText() + ",");
		
		sb.append(azSpotRng6.getText() + ",");
		sb.append(azSpotAng6.getText() + ",");
		sb.append(azSpotRng6_1.getText() + ",");
		sb.append(azSpotAng6_1.getText() + ",");

		switch (key) {
		case " 1 ":
			uPreference.setRUNWAY_1(sb.toString());
			break;

		case " 2 ":
			uPreference.setRUNWAY_2(sb.toString());
			break;

		case " 3 ":
			uPreference.setRUNWAY_3(sb.toString());
			break;

		case " 4 ":
			uPreference.setRUNWAY_4(sb.toString());
			break;

		default:
			break;
		}
	}

	private void loadValues(String key) {
		UserPreference uPreference = UserPreference.getInstance();
		String loadVal = null;
		switch (key) {
		case " 1 ":
			loadVal = uPreference.getRUNWAY_1();
			break;

		case " 2 ":
			loadVal = uPreference.getRUNWAY_2();
			break;

		case " 3 ":
			loadVal = uPreference.getRUNWAY_3();
			break;

		case " 4 ":
			loadVal = uPreference.getRUNWAY_4();
			break;

		default:
			break;
		}

		if (loadVal != null && !loadVal.isEmpty()) {
			String[] runwayVals = loadVal.split(",");
			int cnt = 0;
			if (runwayVals.length > cnt) {
				txtAzAxis.setText(runwayVals[cnt++]);
				uPreference.setAZ_AXIS(txtAzAxis.getText());
			}
			if (runwayVals.length > cnt) {
				txtMinRng.setText(runwayVals[cnt++]);
				uPreference.setMIN_RNG(txtMinRng.getText());
			}
			if (runwayVals.length > cnt) {
				txtElTilt.setText(runwayVals[cnt++]);
				uPreference.setEL_TILT(txtElTilt.getText());
			}
			if (runwayVals.length > cnt) {
				txtAzTilt.setText(runwayVals[cnt++]);
				uPreference.setAZ_TILT(txtAzTilt.getText());
			}
			if (runwayVals.length > cnt)
				txtXoffset.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				txtYoffset.setText(runwayVals[cnt++]);

			if (runwayVals.length > cnt)
				azSpotRng1.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng1.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotRng1_1.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng1_1.setText(runwayVals[cnt++]);
			
			if (runwayVals.length > cnt)
				azSpotRng2.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng2.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotRng2_1.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng2_1.setText(runwayVals[cnt++]);
			
			if (runwayVals.length > cnt)
				azSpotRng3.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng3.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotRng3_1.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng3_1.setText(runwayVals[cnt++]);
			
			if (runwayVals.length > cnt)
				azSpotRng4.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng4.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotRng4_1.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng4_1.setText(runwayVals[cnt++]);
			
			if (runwayVals.length > cnt)
				azSpotRng5.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng5.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotRng5_1.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng5_1.setText(runwayVals[cnt++]);
			
			if (runwayVals.length > cnt)
				azSpotRng6.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng6.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotRng6_1.setText(runwayVals[cnt++]);
			if (runwayVals.length > cnt)
				azSpotAng6_1.setText(runwayVals[cnt++]);
		}
	}

	private void verifyGeneralItems() {
		if (AppUtils.checkIPv4(rc1Ip.getText()))
			Constance.IP_RC_1 = rc1Ip.getText();
		else {
			AppConfig.getInstance().openErrorDialog("RC 1 IP: Invalid");
			isValid = false;
		}
		if (AppUtils.checkIPv4(rc2Ip.getText()))
			Constance.IP_RC_2 = rc2Ip.getText();
		else {
			AppConfig.getInstance().openErrorDialog("RC 2 IP: Invalid");
			isValid = false;
		}
		int portRcWrite = (int) validateDoubleData("Radar write port ",
				portSend, 2000, 15000, "");
		if (portRcWrite != 0)
			Constance.PORT_RC_WRITE = portRcWrite;

		int msl = (int) validateDoubleData("MSL offset ", txtMslOff, 0, 10000,
				"");
		if (msl >= 0)
			Constance.GENERAL_MSL = String.valueOf(msl);

		Constance.AZIMUTH.RAL_ANGLE = validateDoubleData(
				"Azimuth Right Approach limit ", txtAzRal, -15, -1,
				Constance.UNITS.DEGREES);
		Constance.AZIMUTH.LAL_ANGLE = validateDoubleData(
				"Azimuth Left Approach limit ", txtAzLal, 1, 15,
				Constance.UNITS.DEGREES);
		Constance.AZIMUTH.RSaL = validateDoubleData(
				"Azimuth Right Safety limit ", txtAzRsl, -15, -3,
				Constance.UNITS.DEGREES);
		Constance.AZIMUTH.LSaL = validateDoubleData(
				"Azimuth Left Safety limit ", txtAzLsl, 3, 15,
				Constance.UNITS.DEGREES);

		Constance.ELEVATION.GLIDE_ANGLE = comboGlideAngle.getSelectionModel().getSelectedIndex()*0.5;
		
		Constance.ELEVATION.UAL_ANGLE = Constance.ELEVATION.GLIDE_ANGLE + 1;
		Constance.ELEVATION.LAL_ANGLE = Constance.ELEVATION.GLIDE_ANGLE - 1;
		Constance.ELEVATION.USaL_ANGLE = Constance.ELEVATION.GLIDE_ANGLE + 2.5;
		Constance.ELEVATION.LSaL_ANGLE = Constance.ELEVATION.GLIDE_ANGLE - 2.5;

		int gAng = (int) validateDoubleData(
				"Glide angle distance for fixed height ",
				txtGlideDistFixedHeight,
				3 * Constance.UNITS.getFACTOR_LENGTH(),
				29 * Constance.UNITS.getFACTOR_LENGTH(),
				Constance.UNITS.getLENGTH());
		if (gAng >= 0)
			Constance.ELEVATION.GLIDE_FLAT_START_DIST = gAng;
		int gMax = (int) validateDoubleData("Glide angle maximum distance ",
				txtGlideAngleMaxDist, 5 * Constance.UNITS.getFACTOR_LENGTH(),
				30 * Constance.UNITS.getFACTOR_LENGTH(),
				Constance.UNITS.getLENGTH());
		if (gMax >= 0)
			Constance.ELEVATION.GLIDE_MAX_DIST = gMax;

		if (Constance.ELEVATION.GLIDE_FLAT_START_DIST > Constance.ELEVATION.GLIDE_MAX_DIST) {
			AppConfig
					.getInstance()
					.openErrorDialog(
							"Glide path maximum distance value should be greater than the Glide path Flattering start distance");
		}

		// export display data
		saveGenParamPref();
		DisplayParam displayParam = null;
		if (displayParamCheck.isSelected()) {
			UserPreference uPreference = UserPreference.getInstance();
			displayParam = new DisplayParam();
			displayParam.setTouchDown(uPreference.getTOUCH_DOWN());
			displayParam.setElUslAngle(uPreference.getEL_USL_ANGLE());
			displayParam.setElGlideAngle(uPreference.getEL_GLIDE_ANGLE());
			displayParam.setElGlideMaxDist(uPreference.getEL_GLIDE_MAX_DIST());
			displayParam.setElGlideFlatDist(uPreference
					.getEL_GLIDE_FLAT_START_DIST());
			displayParam.setElLslAngle(uPreference.getEL_LSL_ANGLE());
			displayParam.setElUalAngle(uPreference.getEL_UAL_ANGLE());
			displayParam.setElLalAngle(uPreference.getEL_LAL_ANGLE());
			displayParam.setElUsalAngle(uPreference.getEL_USaL_ANGLE());
			displayParam.setElLsaLAngle(uPreference.getEL_LSaL_ANGLE());
			displayParam.setElDh(uPreference.getEL_DH());

			displayParam.setAzLslAngle(uPreference.getAZ_LSL_ANGLE());
			displayParam.setAzRslAngle(uPreference.getAZ_RSL_ANGLE());
			displayParam.setAzRclo(uPreference.getAZ_RCLO());
			displayParam.setAzLalAngle(uPreference.getAZ_LAL_ANGLE());
			displayParam.setAzRalAngle(uPreference.getAZ_RAL_ANGLE());
			displayParam.setAzLsalAngle(uPreference.getAZ_LSaL());
			displayParam.setAzRsalAngle(uPreference.getAZ_RSaL());
		}

		genParam = new GeneralParam(displayParam, Constance.IP_RC_1,
				Constance.IP_RC_2, String.valueOf(Constance.PORT_RC_WRITE),
				Constance.GENERAL_MSL);
	}

	private void verifyEachRunwayItems(String key) {
		double azAxis = validateDoubleData("Azimuth axis ", txtAzAxis, 0, 290,
				"");
		double azTilt = 0;
		double elTilt = 0;
		int raMin = 0;
		double xOff = 0;
		double yOff = 0;
		try {
			azTilt = Double.parseDouble(txtAzTilt.getText());
			elTilt = Double.parseDouble(txtElTilt.getText());
			raMin = Integer.parseInt(txtMinRng.getText());
			xOff = Double.parseDouble(txtXoffset.getText());
			yOff = Double.parseDouble(txtYoffset.getText());
		} catch (Exception e) {
			logger.error(e);
		}
		if (azTilt < -5 || azTilt > 5) {
			AppConfig.getInstance().openErrorDialog(
					"Azimuth Tilt value cannot exceed limit ( -5 to +5 )");
			isValid = false;
		}
		if (elTilt < -5 || elTilt > 5) {
			AppConfig.getInstance().openErrorDialog(
					"Elevation Tilt value cannot exceed limit ( -5 to +5 )");
			isValid = false;
		}
		if (raMin < 0 || raMin > 30000) {
			AppConfig
					.getInstance()
					.openErrorDialog(
							"R min value cannot exceed range limit ( 0 to 30000 meters )");
			isValid = false;
		}
		if (xOff < 0 || xOff > 2000) {
			AppConfig
					.getInstance()
					.openErrorDialog(
							"X offset value cannot exceed range limit ( 0 to 2000 meters )");
			isValid = false;
		}
		if (yOff < -500 || yOff > 500) {
			AppConfig
					.getInstance()
					.openErrorDialog(
							"Y Offset value cannot exceed range limit ( -500 to 500 meters )");
			isValid = false;
		}

		azRng = new ArrayList<Double>();
		azAng = new ArrayList<Double>();

		final double rMin = Constance.RANGE_MIN
				* Constance.UNITS.getFACTOR_LENGTH();
		final double rMax = 5 * Constance.UNITS.getFACTOR_LENGTH();

		double range;
		double angle;
		double range1;
		double angle1;
		
		CR cr1 = null, cr2 = null, cr3 = null, cr4 = null, cr5 = null, cr6 = null;
		if (azSpot1.isSelected()) {
			range = validateDoubleData(azSpot1.getText(), azSpotRng1, rMin,
					rMax, Constance.UNITS.getLENGTH());
			range1 =validateDoubleData1(azSpot1.getText(),azSpotRng1, azSpotAng1,
					 Constance.UNITS.getLENGTH());	
			angle = validateDoubleData(azSpot1.getText(), azSpotRng1_1, Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			angle1 = validateDoubleData(azSpot1.getText(), azSpotAng1_1,
					Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			
			cr1 = new CR(String.valueOf(range), String.valueOf(range1),String.valueOf(angle), String.valueOf(angle1));
			azRng.add(range);
			azAng.add(angle);
			azRng.add(range1);
			azAng.add(angle1);
		}

		if (azSpot2.isSelected()) {
			range = validateDoubleData(azSpot2.getText(), azSpotRng2, rMin,
					rMax, Constance.UNITS.getLENGTH());
			range1 =validateDoubleData1(azSpot2.getText(),azSpotRng2, azSpotAng2,
					 Constance.UNITS.getLENGTH());
			angle= validateDoubleData(azSpot2.getText(), azSpotRng2_1,
					Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			angle1 = validateDoubleData(azSpot2.getText(), azSpotAng2_1,
					Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			
			cr2 = new CR(String.valueOf(range), String.valueOf(range1),String.valueOf(angle), String.valueOf(angle1));
			azRng.add(range);
			azAng.add(angle);
			azRng.add(range1);
			azAng.add(angle1);
		}

		if (azSpot3.isSelected()) {
			range = validateDoubleData(azSpot3.getText(), azSpotRng3, rMin,
					rMax, Constance.UNITS.getLENGTH());
			range1 =validateDoubleData1(azSpot3.getText(),azSpotRng3, azSpotAng3,
					 Constance.UNITS.getLENGTH());
			angle = validateDoubleData(azSpot3.getText(), azSpotRng3_1, Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			angle1 = validateDoubleData(azSpot3.getText(), azSpotAng3_1,
					Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			
			cr3 = new CR(String.valueOf(range), String.valueOf(range1),String.valueOf(angle), String.valueOf(angle1));
			azRng.add(range);
			azAng.add(angle);
			azRng.add(range1);
			azAng.add(angle1);
		}

		if (azSpot4.isSelected()) {
			range = validateDoubleData(azSpot4.getText(), azSpotRng4, rMin,
					rMax, Constance.UNITS.getLENGTH());
			range1 =validateDoubleData1(azSpot4.getText(),azSpotRng4, azSpotAng4,
					 Constance.UNITS.getLENGTH());
			
			angle = validateDoubleData(azSpot4.getText(), azSpotRng4_1, Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			angle1 = validateDoubleData(azSpot4.getText(), azSpotAng4_1,
					Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			
			cr4 = new CR(String.valueOf(range), String.valueOf(range1),String.valueOf(angle), String.valueOf(angle1));
			azRng.add(range);
			azAng.add(angle);
			azRng.add(range1);
			azAng.add(angle1);
		}

		if (azSpot5.isSelected()) {
			range = validateDoubleData(azSpot5.getText(), azSpotRng5, rMin,
					rMax, Constance.UNITS.getLENGTH());
			range1 =validateDoubleData1(azSpot5.getText(),azSpotRng5, azSpotAng5,
					 Constance.UNITS.getLENGTH());
			angle = validateDoubleData(azSpot5.getText(), azSpotRng5_1, Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			angle1 = validateDoubleData(azSpot5.getText(), azSpotAng5_1,
					Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			
			
			cr5 = new CR(String.valueOf(range), String.valueOf(range1),String.valueOf(angle), String.valueOf(angle1));
			azRng.add(range);
			azAng.add(angle);
			azRng.add(range1);
			azAng.add(angle1);
		}

		if (azSpot6.isSelected()) {
			range = validateDoubleData(azSpot6.getText(), azSpotRng6, rMin,
					rMax, Constance.UNITS.getLENGTH());
			range1 =validateDoubleData1(azSpot6.getText(),azSpotRng6, azSpotAng6,
					 Constance.UNITS.getLENGTH());
			angle = validateDoubleData(azSpot5.getText(), azSpotRng5_1, Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			angle1 = validateDoubleData(azSpot5.getText(), azSpotAng5_1,
					Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX,
					Constance.UNITS.DEGREES);
			
			cr6 = new CR(String.valueOf(range), String.valueOf(range1),String.valueOf(angle), String.valueOf(angle1));
			azRng.add(range);
			azAng.add(angle);
			azRng.add(range1);
			azAng.add(angle1);
		}

		// make data model for export
		ConfigParam cParam = new ConfigParam(key, String.valueOf(azAxis),
				String.valueOf(raMin), String.valueOf(elTilt),
				String.valueOf(azTilt), String.valueOf(xOff),
				String.valueOf(yOff), cr1, cr2, cr3, cr4, cr5, cr6);
		
		switch (key) {
		case " 1 ":
			configList.set(0,cParam);
			break;
			
		case " 2 ":
			configList.set(1,cParam);
			break;
			
		case " 3 ":
			configList.set(2,cParam);
			break;
			
		case " 4 ":
			configList.set(3,cParam);
			break;

		default:
			break;
		}
	}

	private double validateDoubleData(String title, TextField textField,
			double min, double max, String unit) {
		double val = 0;
		if (textField.getText() != null && !textField.getText().isEmpty()) {
			try {
				double check = Double.valueOf(textField.getText());
				if ((check <= max) && (check >= min)) {
					val = check;
					return val;
				}
			} catch (Exception e) {
				AppConfig.getInstance().openErrorDialog("Not a Number");
				logger.error(e);
			}
		}
		isValid = false;
		AppConfig.getInstance().openErrorDialog(
				title + " value ranges from: " + min + " " + unit + " to "
						+ max + " " + unit
						+ ". \nPlease make suitable corrections");
		return val;
	}
	
	private double validateDoubleData1(String title, TextField textField,
			TextField textField1,  String unit) {
		       double val = 0;
				double check1 = Double.valueOf(textField.getText());
				double check2 = Double.valueOf(textField1.getText());
				//double check =check2-check1;
				double MaxRange=check1+0.1;
				try{	
				if ((check2 <=check1+0.1)) {
					val = check2;
					return val;
				}
			} catch (Exception e) {
				AppConfig.getInstance().openErrorDialog("Not a Number");
				logger.error(e);
			}
		
		isValid = false;
		AppConfig.getInstance().openErrorDialog(
				title + " value ranges from: " + check1 + " " + unit + " to "
						+ MaxRange + " " + unit
						+ ". \nPlease make suitable corrections");
		return val;
	}

	private boolean exportData() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		// from genParam to json
		String jsonGenPara = gson.toJson(genParam);
		// from configParamList to json
		Type type = new TypeToken<List<ConfigParam>>() {}.getType();
		String jsonConfigList = gson.toJson(configList, type);

		StringBuilder sb = new StringBuilder();
		sb.append(jsonGenPara);
		// logger.info("JSON GenParam: "+jsonGenPara);
		sb.append("\n" + PATTERN_SEP + "\n");
		sb.append(jsonConfigList);
//		 logger.info("JSON ConfigParam : "+jsonConfigList);
//		 logger.info("JSON: "+sb.toString());

		// encrypting data
		try {
			String encrytped = new TrippleDes().encrypt(sb.toString());
			logger.info("ENCRYPTED CFG ");
//			logger.info(": "+encrytped);

			String date = AppConfig.getInstance().getFxmlController().getDate();
			String time = AppConfig.getInstance().getFxmlController().getTime();
			Date datetime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
					.parse(date + " " + time);
			String dt = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss")
					.format(datetime);
			Files.write(Paths.get("radar-" + dt + Constance.CONFIG_FILE_EXT),
					encrytped.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		return true;
	}

	private void readData(ActionEvent event) {
		// browse file
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("CFG Files", "*"
						+ Constance.CONFIG_FILE_EXT));

		// select file
		try {
			Node source = (Node) event.getSource();
			Stage stage = (Stage) source.getScene().getWindow();
			selectedFile = fileChooser.showOpenDialog(stage);
			if (selectedFile != null) {
				logger.info("Opening File: " + selectedFile.getName());
			}
		} catch (Exception e) {
			logger.error(e);
		}

		// read file
		if (selectedFile != null) {
			StringBuilder content = new StringBuilder();
			try {
				@SuppressWarnings("resource")
				BufferedReader br = new BufferedReader(new FileReader(selectedFile));		
				String line;
				while ((line = br.readLine()) != null) {
					content.append(line);
					logger.info("CFG file read");
				}
			} catch (IOException e) {
				logger.error(e);
			}

			if (content != null) {
				// Decrypt file
				try {
					String decrypted = new TrippleDes().decrypt(content.toString());
					logger.info("DECRYPTED CFG");
					//logger.info(": "+decrypted);

					// extract from JSON to data
					configList.clear();
					Gson gson = new Gson();
					String[] jsonSplit = decrypted.split("\n", -1);
					StringBuilder sb = new StringBuilder();
					for (String str : jsonSplit) {
						if (str.contains(DiamondOptionController.PATTERN_SEP)) {
							// logger.info("JSON GenParam: "+ sb.toString());
							genParam = gson.fromJson(sb.toString(),
									GeneralParam.class);
							sb = new StringBuilder();
							continue;
						}
						sb.append(str);
					}
					// logger.info("JSON ConfigParam: "+ sb.toString());
					Type type = new TypeToken<List<ConfigParam>>() {}.getType();
					configList = gson.fromJson(sb.toString(), type);
				} catch (Exception e) {
					logger.error(e);
					AppConfig.getInstance().openErrorDialog("Corrupt File");
				}
			}
		}
	}

	private void loadGenParamData() {
		if (genParam != null) {
			Constance.IP_RC_1 = genParam.getRc1Ip();
			Constance.IP_RC_2 = genParam.getRc2Ip();
			Constance.PORT_RC_WRITE = Integer.parseInt(genParam.getRcPort());
			Constance.GENERAL_MSL = genParam.getMsl();

			loadDisplayParamData(genParam.getDisplayParam());
		}
	}

	private void loadDisplayParamData(DisplayParam displayParam) {
		if (displayParam != null) {
			UserPreference uPreference = UserPreference.getInstance();
			uPreference.setTOUCH_DOWN(displayParam.getTouchDown());
			uPreference.setEL_USL_ANGLE(displayParam.getElUslAngle());
			uPreference.setEL_GLIDE_ANGLE(displayParam.getElGlideAngle());
			uPreference.setEL_GLIDE_MAX_DIST(displayParam.getElGlideMaxDist());
			uPreference.setEL_GLIDE_FLAT_START_DIST(displayParam
					.getElGlideFlatDist());
			uPreference.setEL_LSL_ANGLE(displayParam.getElLslAngle());
			uPreference.setEL_UAL_ANGLE(displayParam.getElUalAngle());
			uPreference.setEL_LAL_ANGLE(displayParam.getElLalAngle());
			uPreference.setEL_USaL_ANGLE(displayParam.getElUsalAngle());
			uPreference.setEL_LSaL_ANGLE(displayParam.getElLsaLAngle());
			uPreference.setEL_DH(displayParam.getElDh());

			uPreference.setAZ_LSL_ANGLE(displayParam.getAzLslAngle());
			uPreference.setAZ_RSL_ANGLE(displayParam.getAzRslAngle());
			uPreference.setAZ_RCLO(displayParam.getAzRclo());
			uPreference.setAZ_LAL_ANGLE(displayParam.getAzLalAngle());
			uPreference.setAZ_RAL_ANGLE(displayParam.getAzRalAngle());
			uPreference.setAZ_LSaL(displayParam.getAzLsalAngle());
			uPreference.setAZ_RSaL(displayParam.getAzRsalAngle());
		}
	}

	private void loadConfigParamData() {
		UserPreference uPreference = UserPreference.getInstance();
		if (configList != null)
			for (ConfigParam cParam : configList) {
				StringBuilder sb = new StringBuilder();
				sb.append(cParam.getAzAxis() + ",");
				sb.append(cParam.getRmin() + ",");
				sb.append(cParam.getElTilt() + ",");
				sb.append(cParam.getAzTilt() + ",");
				sb.append(cParam.getXOffset() + ",");
				sb.append(cParam.getYOffset() + ",");

				sb.append(cParam.getCr1().getRange() + ",");
				sb.append(cParam.getCr1().getRange1() + ",");
				sb.append(cParam.getCr1().getAngle() + ",");
				sb.append(cParam.getCr1().getAngle1() + ",");
				
				sb.append(cParam.getCr2().getRange() + ",");
				sb.append(cParam.getCr2().getRange1() + ",");
				sb.append(cParam.getCr2().getAngle() + ",");
				sb.append(cParam.getCr2().getAngle1() + ",");
				
				sb.append(cParam.getCr3().getRange() + ",");
				sb.append(cParam.getCr3().getRange1() + ",");
				sb.append(cParam.getCr3().getAngle() + ",");
				sb.append(cParam.getCr3().getAngle1() + ",");
				
				sb.append(cParam.getCr4().getRange() + ",");
				sb.append(cParam.getCr4().getRange1() + ",");
				sb.append(cParam.getCr4().getAngle() + ",");
				sb.append(cParam.getCr4().getAngle1() + ",");
				
				sb.append(cParam.getCr5().getRange() + ",");
				sb.append(cParam.getCr5().getRange1() + ",");
				sb.append(cParam.getCr5().getAngle() + ",");
				sb.append(cParam.getCr5().getAngle1() + ",");
				
				sb.append(cParam.getCr6().getRange() + ",");
				sb.append(cParam.getCr6().getRange1() + ",");
				sb.append(cParam.getCr6().getAngle() + ",");
				sb.append(cParam.getCr6().getAngle1() + ",");
				
				
				switch (cParam.getRunwayNo()) {
				case " 1 ":
					uPreference.setRUNWAY_1(sb.toString());
					break;

				case " 2 ":
					uPreference.setRUNWAY_2(sb.toString());
					break;

				case " 3 ":
					uPreference.setRUNWAY_3(sb.toString());
					break;

				case " 4 ":
					uPreference.setRUNWAY_4(sb.toString());
					break;

				default:
					break;
				}

			}
	  }

	// This method is not using for the drawing diamonds now because its happening in the plot class.
		public void drawAzDiamonds(ResizableCanvas resizableCanvas) {
		
		GraphicsContext gc = resizableCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, resizableCanvas.getWidth(),
				resizableCanvas.getHeight());
        
		int OFFSET = 5;
		gc.setFill(Color.BEIGE);
		gc.setStroke(Color.ORCHID);
		gc.setLineWidth(2);
        
		AzCornerRange.clear();
		AzCornerAngle.clear();
		
		AzCornerRange.add(Constance.Config.CR1Range);
		AzCornerRange.add(Constance.Config.CR2Range);
		AzCornerRange.add(Constance.Config.CR3Range);
		AzCornerRange.add(Constance.Config.CR4Range);
		AzCornerRange.add(Constance.Config.CR5Range);
		AzCornerRange.add(Constance.Config.CR6Range);
		
		AzCornerAngle.add(Constance.Config.CR1Angle);
		AzCornerAngle.add(Constance.Config.CR2Angle);
		AzCornerAngle.add(Constance.Config.CR3Angle);
		AzCornerAngle.add(Constance.Config.CR4Angle);
		AzCornerAngle.add(Constance.Config.CR5Angle);
		AzCornerAngle.add(Constance.Config.CR6Angle);
		
		if(Constance.CornerSet==true){
		if (AzCornerRange != null) {
			logger.info("Az Diamond Points: " + AzCornerRange.size());
			for (int i = 0; i < AzCornerRange.size(); i++) {

				MatrixRef matrixRef = MatrixRef.getInstance();
				double rngX = matrixRef.toRangePixels(AzCornerRange.get(i));

				double midAzimuth = (matrixRef.getMinAzimuth() + matrixRef
						.getMaxAzimuth()) / 2;
				Point start = matrixRef.toAzimuthRangePixels(midAzimuth,
						matrixRef.getStartRange());
				Point p = DrawingUtils.getNextPointAtAngle(start.getX(),
						start.getY(), rngX, -(AzCornerAngle.get(i)));
				rngX = p.getX();
				double azY = p.getY();

				gc.fillPolygon(new double[] { rngX, rngX - OFFSET, rngX,
						rngX + OFFSET }, new double[] { azY - OFFSET, azY,
						azY + OFFSET, azY }, 4);
			}
			logger.info("Az Diamonds drawn");
		}
		
	  }
		
    }
		
}
