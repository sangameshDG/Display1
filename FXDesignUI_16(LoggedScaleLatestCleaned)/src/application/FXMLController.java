package application;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import admin.MasterSlavery;
import admin.User;
import admin.UserPreference;
import db.DBRecord;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Light.Point;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import materialdesignbutton.MaterialDesignButtonWidget;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import messages.app.IbitRequestMsg;
import messages.app.OperationModeMsg;
import messages.app.ZoneSuppression;
import messages.radar.AntHealthStatusMsg;
import messages.radar.AzimuthScanStartMessage;
import messages.radar.ElAntHealthStatusMsg;
import messages.radar.ElevationScanStartMessage;
import messages.radar.ExRxHealthStatusMsg;
import messages.radar.GpsMsg;
import messages.radar.HealthStatusMsg;
import messages.radar.LANHealthStatusMsg;
import messages.radar.NoiseFigureStatusMsg;
import messages.radar.PCHealthStatusMsg;
import messages.radar.PwrHealthStatusMsg;
import messages.radar.SDPHealthStatusMsg;
import messages.radar.TempHumHealthStatusMsg;
import messages.radar.VSWRHealthStatusMsg;
import messages.radar.WindSensorMsg;
import messages.radar.ZoneUpdateMsg;
import messages.radar.ZoneValuesUpdateMsg;
import model.AppConfig;
import model.DataManager;
import model.ILayoutParam;
import model.MatrixRef;
import model.SketchItemizedOverlay;
import model.drawable.Track;
import model.graph.AzimuthChart;
import model.graph.ElevationChart;
import network.RcNet;
import network.TaskObserver;
import textpanel.TextPanelWidget;
import utils.Constance;
import utils.DrawingUtils;
import utils.DrawingUtils.FLIP;
import views.ResizableCanvas;

public class FXMLController implements Initializable, ILayoutParam {

	public static final Logger logger = Logger.getLogger(FXMLController.class);

	
	@FXML
	private MenuBar fxMenuBar;

	@FXML
	private BorderPane MainView;
	@FXML
	private ScrollPane UIGraph;
	@FXML
	private ScrollPane UIControls;
	@FXML
	private AnchorPane UIGraphAnchor;
	@FXML
	private GridPane GraphGridPane;
	
	@FXML
	TextPanelWidget userProfileBox;
	@FXML
	MaterialDesignButtonWidget btn_linear;
	@FXML
	MaterialDesignButtonWidget btn_logged;
	@FXML
	MaterialDesignButtonWidget btn_km;
	@FXML
	MaterialDesignButtonWidget btn_nm;

	@FXML
	private TextPanelWidget RadarControl;
	@FXML
	private MaterialDesignButtonWidget btn_localRemote;
	@FXML
	private MaterialDesignButtonWidget btn_Ibit;

	@FXML
	private TextPanelWidget DisplayControl;
	@FXML
	private MaterialDesignButtonWidget btn_trackLabel;
	@FXML
	private MaterialDesignButtonWidget btn_track;
	@FXML
	private MaterialDesignButtonWidget btn_raw;
	@FXML
	private MaterialDesignButtonWidget btn_plot;
	@FXML
	private MaterialDesignButtonWidget btn_checkmarks;
	@FXML
	private MaterialDesignButtonWidget btn_unitsdisplay;

	@FXML
	private TextPanelWidget DisplayScale;
	@FXML
	private MaterialDesignButtonWidget btn_display5;
	@FXML
	private MaterialDesignButtonWidget btn_display10;
	@FXML
	private MaterialDesignButtonWidget btn_display20;
	@FXML
	private MaterialDesignButtonWidget btn_display40;
	@FXML
	private Slider displayScaleSlider;
	
	@FXML
	private TextPanelWidget QNH_QFE_Param;
	@FXML 
	TextField QNH;
	@FXML 
	TextField QFE;
	@FXML
	private MaterialDesignButtonWidget save_QFE_QNH;
	
	@FXML
	private TextPanelWidget renameTrack;
	@FXML 
	TextField existing_ID;
	@FXML 
	TextField required_ID;
	@FXML
	private MaterialDesignButtonWidget save_Track_Name;

	@FXML
	private VBox zoomTopControl;
	@FXML
	private MaterialDesignButtonWidget topUp;
	@FXML
	private MaterialDesignButtonWidget topDown;
	@FXML
	private MaterialDesignButtonWidget topLeft;
	@FXML
	private MaterialDesignButtonWidget topRight;
	@FXML
	private MaterialDesignButtonWidget topCenter;
	@FXML
	private Slider zoomSliderTop;

	@FXML
	private VBox zoomBtmControl;
	@FXML
	private MaterialDesignButtonWidget BtmUp;
	@FXML
	private MaterialDesignButtonWidget BtmDown;
	@FXML
	private MaterialDesignButtonWidget BtmLeft;
	@FXML
	private MaterialDesignButtonWidget BtmRight;
	@FXML
	private MaterialDesignButtonWidget BtmCenter;
	@FXML
	private Slider zoomSliderBtm;

	@FXML
	private Label actiontarget;
	@FXML
	private TextField textTime;
	@FXML
	private TextField textDate;

	@FXML
	private MaterialDesignButtonWidget btn_showLoggingSetting;
	@FXML
	private MaterialDesignButtonWidget btn_showUserSetting;
	@FXML
	private MaterialDesignButtonWidget btn_showMasterSetting;
	@FXML
	private MaterialDesignButtonWidget btn_showSystemSetting;
	@FXML
	private MaterialDesignButtonWidget btn_showDisplaySetting;
	@FXML
	private MaterialDesignButtonWidget btn_showInstallation;
	@FXML
	private MaterialDesignButtonWidget btn_showBite;
	@FXML
	private MaterialDesignButtonWidget btn_showAdvSettings;
	@FXML
	private MaterialDesignButtonWidget btn_refresh;
	@FXML
	private MaterialDesignButtonWidget btn_zoom;
	@FXML
	private MaterialDesignButtonWidget btn_zoom1;
	@FXML
	private MaterialDesignButtonWidget btn_record;
	@FXML
	private MaterialDesignButtonWidget btn_showRunway;
	@FXML
	private MaterialDesignButtonWidget btn_showDiamond;
	@FXML
	private MaterialDesignButtonWidget btn_startDisplay;
	@FXML
	private MaterialDesignButtonWidget tdp_msl;
	@FXML
	private MaterialDesignButtonWidget selectPoint;
	@FXML
	private MaterialDesignButtonWidget btn_Zone;
	@FXML
	private MaterialDesignButtonWidget btn_showStatus;

	@FXML
	private ScrollPane chartTopScroll;
	@FXML
	private ScrollPane chartBottomScroll;
	@FXML
	private Pane chartTop;
	@FXML
	private Pane chartBottom;

	@FXML
	private AnchorPane UIRightAnchor;
	@FXML
	private VBox controlBox;
	@FXML
	private VBox statusBox;
	@FXML
	private VBox statusBox1;

	@FXML
	ResizableCanvas cTopL0;// Text
	@FXML
	ResizableCanvas cTopL1;// Graph
	@FXML
	ResizableCanvas cTopL11;// Diamonds
	@FXML
	ResizableCanvas cTopL2;// Video
	@FXML
	ResizableCanvas cTopL31;// Tracks (TopMost Layer)
	@FXML
	ResizableCanvas cTopL32;// Plots
	@FXML
	ResizableCanvas cTopL4;// Track path
	@FXML
	ResizableCanvas cTopL5;// Handle Input Events

	@FXML
	ResizableCanvas cBtmL0;// Text
	@FXML
	ResizableCanvas cBtmL1;// Graph
	@FXML
	ResizableCanvas cBtmL11;// Diamonds
	@FXML
	ResizableCanvas cBtmL2;// Video
	@FXML
	ResizableCanvas cBtmL31;// Tracks (TopMost Layer)
	@FXML
	ResizableCanvas cBtmL32;// Plots
	@FXML
	ResizableCanvas cBtmL4;// Track path
	@FXML
	ResizableCanvas cBtmL5;// Handle Input Events

	@FXML
	TextPanelWidget radarSetupBox;
	@FXML
	VBox radarSetUpAnchor;
	@FXML
	ComboBox<String> comboModes;
	@FXML
	ComboBox<String> comboScans;
	@FXML
	ComboBox<String> comboModeType;
	@FXML
	ComboBox<String> comboFreq;
	@FXML
	ComboBox<String> comboPol;
	@FXML
	ComboBox<String> comboRunway;
	@FXML
	MaterialDesignButtonWidget biteOn;
	@FXML
	MaterialDesignButtonWidget biteOff;
	@FXML
	MaterialDesignButtonWidget txOn;
	@FXML
	MaterialDesignButtonWidget txOff;
	@FXML
	TextField txtMinRng;
	@FXML
	TextField txtNorthOffsetBias;
	@FXML
	MaterialDesignButtonWidget btn_radarCmdSend;
	
	@FXML
	MaterialDesignButtonWidget btn_Update_omp;
	

	@FXML
	TextPanelWidget HealthStatus;
	@FXML
	TextField txtField_Sp;
	@FXML
	TextField txtField_exrx;
	@FXML
	TextField txtField_Ac1;
	@FXML
	TextField txtField_Pc;
	@FXML
	TextField txtField_Ac2;
	@FXML
	TextField txtField_Ch1;
	@FXML
	TextField txtField_Ch2;
	@FXML
	TextField txtField_lan;
	@FXML
	TextField txtField_AzPwr;
	@FXML
	TextField txtField_ElPwr;
	
	@FXML HBox opType_1;
	@FXML HBox scan_1;
	@FXML HBox byte_1;
	@FXML HBox northOff_1;
	@FXML HBox rmin_1;
	
	@FXML VBox Display_Control;
	@FXML HBox trackInfo_Units;
	@FXML HBox allButtonsBelow;
	
	@FXML
	MaterialDesignButtonWidget btn_MTI_ON;
	@FXML
	MaterialDesignButtonWidget btn_MTI_OFF;

	static String textFieldBg = "-fx-background-insets: 0;"
			+ "-fx-text-inner-color: brown;"
			+ "-fx-padding: 6 12;"
			+ "-fx-background-radius: 2px;"
			+ "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.25), 4, 0.0, 0, 1);";

	enum DATA {
		DEFAULT, RAW, PLOT, TRACK, LABEL, TRACKOPT, TRACK_LABEL
	};
	private boolean isDisplayingStatus = false;
	private boolean isLocked = false;
	private boolean toggleUnits = true;
	private boolean isRangeMarks = true;
	private boolean isAccumulating = false;
	private boolean isAppRunning = false;
	private boolean plotElRefresh = false;
	private boolean plotAzRefresh = false;
	private boolean trackElRefresh = false;
	private boolean trackAzRefresh = false;
	private boolean enableZoomDialog = false;
	private boolean enableZoomDialog1 = false;  // For Selecting the zone region.
	private boolean enableSelectLocation = false;
	private boolean isZoomSelectable = false;

	Point initialXY = new Point();
	Point releasedXY;
	double initialX,initialY,endX,endY;
	double minRange;
	double minAngle;
	double maxRange;
	double maxAngle;
	
	int ZID;
    int indexCount;
    double zRminEl;
    double eLzmin;
    double zRmaxEl;
    double eLzmax;
    
    
    int lPoint=0;

	Timer rxBroadcastRadarInfo;
	Timer slaveListener;
//	Timer refreshAnim;
	AnimationTimer plotElAnimTimer;
	AnimationTimer plotAzAnimTimer;
	AnimationTimer trackElAnimTimer;
	AnimationTimer trackAzAnimTimer;
	AnimationTimer rawElAnimTimer;
	AnimationTimer rawAzAnimTimer;

	int iconCnt = 0;
	String masterIconName = "";
	Timeline iconMasterTimeline = null;
	Timeline iconRecordTimeline = null;
	Timeline iconReplayTimeline = null;

	Timer myHealth;
	MatrixRef matrixRef = MatrixRef.getInstance();
	TaskObserver tTask;
	RcNet rcNet;
	DataManager dataObserver = DataManager.getInstance();
	DBRecord dbRecord = DBRecord.getInstance();
	ElevationChart mElevationChart;
	AzimuthChart mAzimuthChart;
	RadarSetUpController radarSetUpController;
	AdvanceSettingsController advanceSetUp;
	InstallationController incontoller=new InstallationController();
	
	ZoomController zC=new ZoomController();

	DecimalFormat df = new DecimalFormat("0.000");
	
	ArrayList<Integer> trackLabelBuffer=new ArrayList<Integer>();
	
    Map<Integer, String> azRenameList = new ConcurrentHashMap<Integer, String>();
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initLayout();
		initControls();
		initAdmin();
		actiontarget.setText("Start the System....");

	}
	
	// For Co-ordinate point selection.
	@FXML 
    protected void onSelectArea(ActionEvent event) {
		if(isAppRunning) {
			
		if(Constance.isArea==false){
			enableSelectLocation = true;
			Constance.isArea=true;
			selectPoint.setStyle("-fx-background-image: url(\"assets/img/Co-PointsON.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		     }else{
		    	enableSelectLocation = false;
				Constance.isArea=false;
				selectPoint.setStyle("-fx-background-image: url(\"assets/img/Co-Points.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
				
		     }
		  
		} else {
			AppConfig.getInstance().openErrorDialog("Application not running");
		}
    }
	
	
	 private void addSelectablePoint(Node node,String name) {
		 
		 node.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (event.getButton() != MouseButton.MIDDLE) {
		                initialX = event.getX();
		                initialY = event.getY();
		                Constance.Plane=name;
		                MatrixRef matrixRef = MatrixRef.getInstance();
						double ang=0;
						double rang=0;
						DecimalFormat df1 = new DecimalFormat("#.0");
						double TDPpixels = matrixRef.toRangePixels(matrixRef.getStartRange());
						if(Constance.Plane=="EL")
						ang=-(((Math.toRadians(matrixRef.fromElevationPixels((initialX-TDPpixels), initialY))))/Constance.ElEVATION_MUL);
						else
						ang=-(Math.toRadians(matrixRef.fromAzimuthPixelsForCO((initialX-TDPpixels), initialY)));
						
						if(Constance.UNITS.isLogged){
						   rang=(matrixRef.fromRangePixels(initialX));
						   rang=Math.pow(10, rang);
						   rang=(5*rang)-(5+matrixRef.getStartRange());
						}
						else
					       rang=((matrixRef.fromRangePixels(initialX))-(matrixRef.getStartRange()*1000))/1000;
					   
					    int height= (int) (1000*((rang)*Constance.UNITS.getR_PX())*Math.tan(ang));
					    if(Constance.isQNH==true){
					    	if(Constance.Plane=="EL")
					    	  height=height+Integer.parseInt(Constance.GENERAL_MSL);
					     }
					 
					    	
					      height=(int) (height*3.2808);
					      java.awt.Color yel = new java.awt.Color(255, 255, 0, 255);
		                  String Point="("+df1.format(rang)+" ,"+height+")";
		               //   String Point="("+df1.format(rang)+" ,"+Math.toDegrees(ang)+")";
		                // String Point="("+df1.format(initialX)+" ,"+initialY+")";
					       GraphicsContext gc = ((Canvas) node).getGraphicsContext2D();
					       gc.setStroke(Color.YELLOW);
		                 if(enableSelectLocation==true){
		                   if(Constance.UNITS.isLogged==false){
		                	 if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4")))
		                		 gc.drawImage(DrawingUtils.printMirrorImage(Point, 12,yel), initialX, initialY);
		                		 else
		                           gc.strokeText(Point,initialX, initialY);
		                      }   
		                 }else{
		                	// AppConfig.getInstance().openErrorDialog("Please check the CO points in Linear Scale");
		                 }
		            }
					
				}
			});
			
	    	
	    	
	    	node.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (event.getButton() != MouseButton.MIDDLE &&  isAppRunning && enableSelectLocation) {
						//Window POP UP
						endX = event.getX();
						endY = event.getY();
						
						 try {
								Thread.sleep(1000);
								((ResizableCanvas)node).clear();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		            }				
				}
			});
	    	
	    	
	    }
	
	
	    

	@FXML
	public void onShowInstallation(ActionEvent event) {
		showInstallation();
	}

	@FXML
	protected void onShowBite(ActionEvent event) {
		showBite();
	}

	@FXML
	protected void onShowAdvSettings(ActionEvent event) {
		showAdvSettings();
	}
	
	@FXML 
    protected void onShowStatusAction(ActionEvent event) {
		showStatus();
    }

	@FXML
	protected void onRefreshAction(ActionEvent event) {
		refreshAccumulate();
	}

	@FXML
	protected void onZoomAction(ActionEvent event) {
		if (isAppRunning) {
			toggleZoom();
		} else {
			AppConfig.getInstance().openErrorDialog("Start Application");
		}
	}
	
	

	@FXML
	protected void topUp(ActionEvent event) {
		logger.info("Move Top Up: " + chartTop.getTranslateY());
		double moveTop = chartTop.getTranslateY() - 25
				* zoomSliderTop.getValue();
		if (chartTop.getTranslateY() > -100 * zoomSliderTop.getValue()) {
			chartTop.setTranslateY(moveTop);
		}
	}

	@FXML
	protected void topDown(ActionEvent event) {
		logger.info("Move Top Down: " + chartTop.getTranslateY());
		double moveDown = chartTop.getTranslateY() + 25
				* zoomSliderTop.getValue();
		if (chartTop.getTranslateY() < 100 * zoomSliderTop.getValue()) {
			chartTop.setTranslateY(moveDown);
		}
	}

	@FXML
	protected void topLeft(ActionEvent event) {
		logger.info("Move Top Left: " + chartTop.getTranslateX());
		double moveLeft = chartTop.getTranslateX() - 25
				* zoomSliderTop.getValue();
		if (chartTop.getTranslateX() > -250 * zoomSliderTop.getValue()) {
			chartTop.setTranslateX(moveLeft);
		}
	}

	@FXML
	protected void topRight(ActionEvent event) {
		logger.info("Move Top Right: " + chartTop.getTranslateX());
		double moveRight = chartTop.getTranslateX() + 25
				* zoomSliderTop.getValue();
		if (chartTop.getTranslateX() < 250 * zoomSliderTop.getValue()) {
			chartTop.setTranslateX(moveRight);
		}
	}

	@FXML
	protected void topCenter(ActionEvent event) {
		logger.info("Move Top Center: ");
		zoomSliderTop.setValue(1);
		chartTop.setTranslateX(0);
		chartTop.setTranslateY(0);
	}

	@FXML
	protected void BtmUp(ActionEvent event) {
		logger.info("Move Btm Up: " + chartBottom.getTranslateY());
		double moveTop = chartBottom.getTranslateY() - 25
				* zoomSliderBtm.getValue();
		if (chartBottom.getTranslateY() > -100 * zoomSliderBtm.getValue()) {
			chartBottom.setTranslateY(moveTop);
		}
	}

	@FXML
	protected void BtmDown(ActionEvent event) {
		logger.info("Move Btm Down: " + chartBottom.getTranslateY());
		double moveDown = chartBottom.getTranslateY() + 25
				* zoomSliderBtm.getValue();
		if (chartBottom.getTranslateY() < 100 * zoomSliderBtm.getValue()) {
			chartBottom.setTranslateY(moveDown);
		}
	}

	@FXML
	protected void BtmLeft(ActionEvent event) {
		logger.info("Move Btm Left: " + chartBottom.getTranslateX());
		double moveLeft = chartBottom.getTranslateX() - 25
				* zoomSliderBtm.getValue();
		if (chartBottom.getTranslateX() > -250 * zoomSliderBtm.getValue()) {
			chartBottom.setTranslateX(moveLeft);
		}
	}

	@FXML
	protected void BtmRight(ActionEvent event) {
		logger.info("Move Btm Right: " + chartBottom.getTranslateX());
		double moveRight = chartBottom.getTranslateX() + 25
				* zoomSliderBtm.getValue();
		if (chartBottom.getTranslateX() < 250 * zoomSliderBtm.getValue()) {
			chartBottom.setTranslateX(moveRight);
		}
	}

	@FXML
	protected void BtmCenter(ActionEvent event) {
		logger.info("Move Btm Reset: ");
		zoomSliderBtm.setValue(1);
		chartBottom.setTranslateX(0);
		chartBottom.setTranslateY(0);

	}

	@FXML
	protected void onRecordAction(ActionEvent event) {
		startRecordAction();
	}

	@FXML
	protected void onStartAction(ActionEvent event) {
		if (!isAppRunning) {
			initLogin();
		} else
			startDisplayAction();
	}

	@FXML
	protected void changeToKM(ActionEvent event) {
		Constance.ELEVATION.GLIDE_MAX_DIST = Constance.ELEVATION.GLIDE_MAX_DIST * Constance.UNITS.getR_PX();
		Constance.ELEVATION.GLIDE_FLAT_START_DIST = Constance.ELEVATION.GLIDE_FLAT_START_DIST * Constance.UNITS.getR_PX();
		Constance.UNITS.isKM=true; 
		updateUnits();
		invalidate();
		handle40ButtonAction(null);
		btn_km.widgetSetVal(VAL.GREEN);
		btn_nm.widgetSetVal(VAL.DEFAULT);	
	}
	
	@FXML
	protected void changeToNM(ActionEvent event) {
		Constance.ELEVATION.GLIDE_MAX_DIST = Constance.ELEVATION.GLIDE_MAX_DIST * (Constance.UNITS.isKM?0.54:1);
		Constance.ELEVATION.GLIDE_FLAT_START_DIST = Constance.ELEVATION.GLIDE_FLAT_START_DIST * (Constance.UNITS.isKM?0.54:1);
		Constance.UNITS.isKM=false; 
		updateUnits();
		invalidate();
		handle40ButtonAction(null);
		btn_nm.widgetSetVal(VAL.GREEN);
		btn_km.widgetSetVal(VAL.DEFAULT);
	}
	
	@FXML
	protected void changeToLinear(ActionEvent event) {
		Constance.UNITS.isLogged=false;
		updateUnits();
		invalidate();
		handle40ButtonAction(null);
		btn_linear.widgetSetVal(VAL.GREEN);
		btn_logged.widgetSetVal(VAL.DEFAULT);
		
	}
	
	@FXML
	protected void changeToLogged(ActionEvent event) {
		Constance.UNITS.isLogged=true;
		updateUnits();
		invalidate();
		handle40ButtonAction(null);
		btn_logged.widgetSetVal(VAL.GREEN);
		btn_linear.widgetSetVal(VAL.DEFAULT);
		
	}
	
	@FXML
	protected void txOn(ActionEvent event) {
		radarSetUpController.txOn(event);  
	}

	@FXML
	protected void txOff(ActionEvent event) {
		radarSetUpController.txOff(event);
	}

	@FXML
	public void radarUpdateClick(ActionEvent event) {
		radarSetUpController.sendCommand();
	}
	
	@FXML
	protected void mTIOn(ActionEvent event) {
		advanceSetUp.mtiOn(event);  
	}

	@FXML
	protected void mTIOff(ActionEvent event) {
		advanceSetUp.mtiOff(event);
	}

	@FXML
	public void mtiUpdateClick(ActionEvent event) {
		advanceSetUp.sendCommand();
	}
	
	@FXML
	protected void checkLocalRemote(ActionEvent event) {
		requestLocalMode();
	}

	@FXML
	protected void sendIbit(ActionEvent event) {
		requestIbit();
	}
	
	@FXML
	protected void handleRangeMarks(ActionEvent event) {
		isRangeMarks = !isRangeMarks;
		Constance.SHOW_RANGE_MARKS = isRangeMarks;
		btn_checkmarks.widgetSetVal(VAL.DEFAULT);
		if (isRangeMarks)
			btn_checkmarks.widgetSetVal(VAL.GREEN);
		reDrawGraphs();
	}
	
	@FXML
	protected void onUnitsDisplay(ActionEvent event) {
		toggleUnits = !toggleUnits;
		btn_unitsdisplay.widgetSetVal(toggleUnits ? VAL.GREEN : VAL.DEFAULT);
		reDrawGraphs();
	}
	
	@FXML
	protected void showRAW(ActionEvent event) {
		Constance.SHOW_RAW = !Constance.SHOW_RAW;
		setDataDisplay(DATA.RAW);
		cTopL2.getGraphicsContext2D().clearRect(0, 0, cTopL2.getWidth(),
				cTopL2.getHeight());
		cBtmL2.getGraphicsContext2D().clearRect(0, 0, cBtmL2.getWidth(),
				cBtmL2.getHeight());
	}

	@FXML
	protected void showTRACK_LABEL(ActionEvent event) {
		Constance.SHOW_TRACK_LABEL = !Constance.SHOW_TRACK_LABEL;
		setDataDisplay(DATA.TRACK_LABEL);
	}

	@FXML
	protected void showPLOT(ActionEvent event) {
		Constance.SHOW_PLOT = !Constance.SHOW_PLOT;
		setDataDisplay(DATA.PLOT);
	}
	
	

	@FXML
	protected void showTRACK(ActionEvent event) {
		Constance.SHOW_TRACK = !Constance.SHOW_TRACK;
		setDataDisplay(DATA.TRACK);
	}
	
	@FXML
	protected void handle5ButtonAction(ActionEvent event) {
		setDisplayScale(5 * Constance.UNITS.getFACTOR_LENGTH());
		resetDisplayScaleButtonColors();
		btn_display5.widgetSetVal(VAL.GREEN);
		Constance.DisplayScale=5;
		//displayScaleSlider.setValue(5 * Constance.UNITS.getFACTOR_LENGTH());
		invalidate();
		refreshAccumulate();
		refreshAccumulate();
		actiontarget.setText("Display Scale set to " + Constance.SCALE);
	}

	@FXML
	protected void handle10ButtonAction(ActionEvent event) {
		setDisplayScale(10 * Constance.UNITS.getFACTOR_LENGTH());
		resetDisplayScaleButtonColors();
		btn_display10.widgetSetVal(VAL.GREEN);
		Constance.DisplayScale=10;
		//displayScaleSlider.setValue(10 * Constance.UNITS.getFACTOR_LENGTH());
		invalidate();
		refreshAccumulate();
		refreshAccumulate();
		actiontarget.setText("Display Scale set to " + Constance.SCALE);
	}

	@FXML
	protected void handle20ButtonAction(ActionEvent event) {
		setDisplayScale(20 * Constance.UNITS.getFACTOR_LENGTH());
		resetDisplayScaleButtonColors();
		btn_display20.widgetSetVal(VAL.GREEN);
		Constance.DisplayScale=20;
		//displayScaleSlider.setValue(20 * Constance.UNITS.getFACTOR_LENGTH());
		invalidate();
		refreshAccumulate();
		refreshAccumulate();
		actiontarget.setText("Display Scale set to " + Constance.SCALE);
	}

	@FXML
	protected void handle40ButtonAction(ActionEvent event) {
		setDisplayScale(30 * Constance.UNITS.getFACTOR_LENGTH());
		Constance.DisplayScale=30;
		//displayScaleSlider.setValue(30 * Constance.UNITS.getFACTOR_LENGTH());
		invalidate();
		refreshAccumulate();
		refreshAccumulate();
		resetDisplayScaleButtonColors();
		btn_display40.widgetSetVal(VAL.GREEN);
		actiontarget.setText("Display Scale set to " + Constance.SCALE);
	}
	
	@FXML
	protected void saveQFE(ActionEvent event) {
		radarSetUpController.saveUserPrefDataQNH_QFE();
	}
	
	@FXML
	protected void onClickRename(ActionEvent event) {
		int id = (int) Double.parseDouble(existing_ID.getText());
		String requiredName = required_ID.getText();
		Constance.azRenameListInTrack.put(id, requiredName);	
	}
	
	@FXML
	protected void handleMasterSlaveAction(ActionEvent event) {
		// setMasterSlave();
		if (rcNet != null && rcNet.isOnline() && rcNet.isMasterOpen())
			claimMaster();
		else if (MasterSlavery.getInstance().isMaster())
			claimSlave();
	}

	@FXML
	protected void menuCloseStage() {
		if (AppConfig.getInstance().openConfirmationDialog(
				"Do you wish to shutdown?")) {
			shutDown();
			Stage stage = (Stage) fxMenuBar.getScene().getWindow();
			stage.close();
			logger.info("APPLICATION CLOSED");
		}
	}

	@FXML
	protected void onUserSetup(ActionEvent event) {
		//displayUserManager();
		safetyParamSetup();
	}

	
	@FXML
	protected void onLoggingSetup(ActionEvent event) {
		loggingSetup();
	}

	@FXML
	protected void onDisplaySetup(ActionEvent event) {
		displaySetup();
	}

	@FXML
	protected void onSystemSetup(ActionEvent event) {
		systemSetup();
	}

	@FXML
	protected void onRadarSetup(ActionEvent event) {
		radarSetup();
	}

	@FXML
	protected void onDiamondSetup(ActionEvent event) {
		diamondSetup();
	}

	

	@FXML
	protected void onRunwaySetup(ActionEvent event) {
		runwaySetup();
	}
	
	@FXML
	protected void onTDP_MSL(ActionEvent event) {
		elHeightSetup();
	}
	@FXML
	protected void onZoneAction(ActionEvent event) {
		zoneSetup();
	}
	
	@FXML
	protected void om_UpdateClick(ActionEvent event) {
		opModemParamSetup();
	}
	
	// This is for selecting the Zone region on display
	@FXML 
    protected void onZoomAction1(ActionEvent event) {     
	if(isAppRunning) {
		if(AppConfig.getInstance().isDisplayHasPrevilege()){
			if(enableZoomDialog1==false){
			enableZoomDialog1 = true;
			btn_zoom1.widgetSetVal(VAL.GREEN);
			}
		    else
		    {
		    	enableZoomDialog1 = false;
				btn_zoom1.widgetSetVal(VAL.DEFAULT);
		    }
			actiontarget.setText("Now select a particular region on graph");
		  }
	  } else {
			AppConfig.getInstance().openErrorDialog("Application not running");
		}
    }
	

	@FXML
	protected void onLockMe(ActionEvent event) {
		if (!isLocked && isAppRunning) {
			// unlockMe();
			unlockMeLogin();
		} else
			AppConfig.getInstance().openErrorDialog("Start Application");
	}
	
	

	@Override
	public void draw(GraphicsContext gc) {

	}

	public static void setNodeStyle(Node node) {
		node.setStyle("-fx-background-color: rgba(250, 250, 250, 1);"
				+ textFieldBg);
	}
	
	private void showStatus() {
	  	if(isAppRunning) {
	  		
			UIRightAnchor.getChildren().removeAll(controlBox,statusBox,statusBox1);
			if(!isDisplayingStatus) {
				btn_showStatus.setStyle("-fx-background-image: url(\"assets/img/status_on.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
				UIRightAnchor.getChildren().add(statusBox1);
				
			} else {
				btn_showStatus.setStyle("-fx-background-image: url(\"assets/img/status_off.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
				UIRightAnchor.getChildren().add(statusBox);	
			}
			isDisplayingStatus = !isDisplayingStatus;
		} else {
			AppConfig.getInstance().openErrorDialog("Application not running");
		}
		
 	}
  

	public void elHeightSetup(){
		if (isAppRunning) {
		if(Constance.isMSL==false){
		Constance.isMSL=true;
		tdp_msl.setText("QNH");
		Constance.isQNH=true;
		cTopL1.clear();
		tdp_msl.setStyle("-fx-background-image: url(\"assets/img/MSL.jpg\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		drawGraphTop(cTopL1); // To redraw the elevation units after changing it to MSL.		
		}
		else{
			Constance.isMSL=false;
			tdp_msl.setText("QFE");
			Constance.isQNH=false;
			cTopL1.clear();
			tdp_msl.setStyle("-fx-background-image: url(\"assets/img/TDP.jpg\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
			drawGraphTop(cTopL1);
		}
	   }else{
		   AppConfig.getInstance().openErrorDialog("Start the application");
	   }
	}
	
	private void opModemParamSetup() {
		if (isAppRunning) {
			if (AppConfig.getInstance().isUserHasPrevilege()) {
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader fxmlLoader = new FXMLLoader();
				try {
					fxmlLoader.load(getClass().getResourceAsStream(
							"/application/RadarSetUp.fxml"));
				} catch (IOException e) {
					logger.error(e);
				}
				Parent root = (Parent) fxmlLoader.getRoot();
				Scene dialogPreferences = new Scene(root);
				dialog.setScene(dialogPreferences);
				dialog.setResizable(false);
				dialog.initStyle(StageStyle.UNDECORATED);
				dialog.centerOnScreen();
				dialog.show();
			}
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}
	
	private void zoneSetup() {
		if (isAppRunning) {
			btn_Zone.setStyle("-fx-background-image: url(\"assets/img/ZoneUpdates.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			Constance.Zone.OpenDialog=1;
			FXMLLoader fxmlLoader = new FXMLLoader();
			try {
				fxmlLoader.load(getClass().getResourceAsStream(
						"/application/ZoomActivity.fxml"));
				ZoomController zController = (ZoomController)fxmlLoader.getController();
				zController.updateUI();
			} catch (IOException e) {
				logger.error(e);
			}
			Parent root = (Parent) fxmlLoader.getRoot();
			Scene dialogSettings = new Scene(root);
			dialog.setScene(dialogSettings);
			dialog.setResizable(false);
			dialog.initStyle(StageStyle.UNDECORATED);
			dialog.centerOnScreen();
			dialog.show();
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	
	
	 private void openZoomDialog() {
		
		 
		    enableZoomDialog1 = false;
			btn_zoom1.widgetSetVal(VAL.DEFAULT);
		
		    MatrixRef matrixRef = MatrixRef.getInstance();
		    ZoneSuppression zoneSup=new ZoneSuppression();
		    
		    double zRmin=(matrixRef.fromRangePixels(initialX)*Constance.UNITS.aZimuth);
		    double zAzmin=matrixRef.fromAzimuthPixels(endX, endY);
		    double zRmax=(matrixRef.fromRangePixels(endX)*Constance.UNITS.aZimuth);
		    double zAzmax=matrixRef.fromAzimuthPixels(initialX, initialY);
				    
		  if(Constance.Zone.ZoneID<=5){
			    zoneSup.setRmin((int)(zRmin*Constance.UNITS.getSendInMeter()));
				zoneSup.setAzmin((int)(((-zAzmin)+10)*Constance.Config.CRinMeters));
				zoneSup.setRmax((int)(zRmax*Constance.UNITS.getSendInMeter()));
				zoneSup.setAzmax((int)(((-zAzmax)+10)*Constance.Config.CRinMeters));
				zoneSup.setMsgId(0x3001);  
			  
			    if(Constance.Zone.zon1Status==0){	
			    zoneSup.setZoneID(1);
				sendBytes(zoneSup.getByteBuffer().array());
				
				Constance.Zone.z1Rmin=zRmin;
				Constance.Zone.z1Azmin=zAzmin;
				Constance.Zone.z1Rmax=zRmax;
				Constance.Zone.z1Azmax=zAzmax;
				
				Constance.Zone.zon1Status=1;
			   }else if(Constance.Zone.zon2Status==0){
				zoneSup.setZoneID(2);
				sendBytes(zoneSup.getByteBuffer().array());	
				
				Constance.Zone.z2Rmin=zRmin;
				Constance.Zone.z2Azmin=zAzmin;
				Constance.Zone.z2Rmax=zRmax;
				Constance.Zone.z2Azmax=zAzmax;
				
				Constance.Zone.zon2Status=1;
				
			   }else if(Constance.Zone.zon3Status==0){
				zoneSup.setZoneID(3);
				sendBytes(zoneSup.getByteBuffer().array());
				
				Constance.Zone.z3Rmin=zRmin;
				Constance.Zone.z3Azmin=zAzmin;
				Constance.Zone.z3Rmax=zRmax;
				Constance.Zone.z3Azmax=zAzmax;
				
				Constance.Zone.zon3Status=1;
				
			   }else if(Constance.Zone.zon4Status==0){
				zoneSup.setZoneID(4);
				sendBytes(zoneSup.getByteBuffer().array());
				
				Constance.Zone.z4Rmin=zRmin;
				Constance.Zone.z4Azmin=zAzmin;
				Constance.Zone.z4Rmax=zRmax;
				Constance.Zone.z4Azmax=zAzmax;
				
				Constance.Zone.zon4Status=1;
				
			   }else if(Constance.Zone.zon5Status==0){
				zoneSup.setZoneID(5);
				sendBytes(zoneSup.getByteBuffer().array());
				
				Constance.Zone.z5Rmin=zRmin;
				Constance.Zone.z5Azmin=zAzmin;
				Constance.Zone.z5Rmax=zRmax;
				Constance.Zone.z5Azmax=zAzmax;
				
				Constance.Zone.zon5Status=1;
			  }
			
		 }
			
			if(Constance.Zone.ZoneID>5){
					AppConfig.getInstance().openErrorDialog("Zone creation is not possible");	
				}
			
			if(Constance.Zone.ZoneID<=5){
				Constance.Zone.ZoneID= (Constance.Zone.ZoneID+1);
				System.out.println("Zone Id is: "+Constance.Zone.ZoneID);
			}    
	   }
	 
	 private void openZoomDialogEl(){
		 
		     enableZoomDialog1 = false;
		     btn_zoom1.widgetSetVal(VAL.DEFAULT);
		    
		     MatrixRef matrixRef = MatrixRef.getInstance();

		     zRminEl=(matrixRef.fromRangePixels(initialX)*Constance.UNITS.aZimuth);
		     eLzmin=-(matrixRef.fromElevationPixels(endX, endY))/2;
		     zRmaxEl=(matrixRef.fromRangePixels(endX)*Constance.UNITS.aZimuth);
		     eLzmax=-(matrixRef.fromElevationPixels(initialX, initialY))/2;
		    if(Constance.Zone.ZoneIDEl<=5){
			  
		    	if(Constance.Zone.zon1StatusEl==0){
		    		indexCount=0;
					ZID=Constance.Zone.zon1IDEL;
					Constance.Zone.zon1StatusEl=1;
					
				}else if(Constance.Zone.zon2StatusEl==0){
		    		indexCount=4;
					ZID=Constance.Zone.zon2IDEL;
					Constance.Zone.zon2StatusEl=1;
					
				}else if(Constance.Zone.zon3StatusEl==0){
		    		indexCount=8;
					ZID=Constance.Zone.zon3IDEL;
					Constance.Zone.zon3StatusEl=1;
					
				}else if(Constance.Zone.zon4StatusEl==0){
		    		indexCount=12;
					ZID=Constance.Zone.zon4IDEL;
					Constance.Zone.zon4StatusEl=1;
					
				}else if(Constance.Zone.zon5StatusEl==0){
		    		indexCount=16;
					ZID=Constance.Zone.zon5IDEL;
					Constance.Zone.zon5StatusEl=1;
					
				}
		    	
		    	sendValuesToCreateZone();
		   }
		    
		    if(Constance.Zone.ZoneIDEl>5){
				AppConfig.getInstance().openErrorDialog("Zone creation is not possible");	
			}
		
		if(Constance.Zone.ZoneIDEl<=5){
			Constance.Zone.ZoneIDEl= (Constance.Zone.ZoneIDEl+1);
			
		  }
	 }

 
	private void sendValuesToCreateZone() {
		
		ZoneSuppression zoneSup=new ZoneSuppression();
	    zoneSup.setZoneID(ZID);
		zoneSup.setRmin((int)(zRminEl*Constance.UNITS.getSendInMeter()));
		zoneSup.setAzmin((int)((eLzmin)*Constance.Config.CRinMeters));
		zoneSup.setRmax((int)(zRmaxEl*Constance.UNITS.getSendInMeter()));
		zoneSup.setAzmax((int)((eLzmax)*Constance.Config.CRinMeters));
		zoneSup.setMsgId(0x3011);
		sendBytes(zoneSup.getByteBuffer().array());
		
		Constance.elZone1[indexCount]=zRminEl;indexCount++;
		Constance.elZone1[indexCount]=zRmaxEl;indexCount++;
		Constance.elZone1[indexCount]=eLzmin;indexCount++;
		Constance.elZone1[indexCount]=eLzmax;
	}

	private void unlockMeLogin() {
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			fxmlLoader.load(getClass().getResourceAsStream(
					"/application/Login.fxml"));
			LoginController loginController = (LoginController) fxmlLoader
					.getController();
			loginController.setShowDisplay(true);
		} catch (IOException e) {
			logger.error(e);
		}
		Parent root = (Parent) fxmlLoader.getRoot();
		Scene dialogSettings = new Scene(root);
		dialog.setScene(dialogSettings);
		dialog.setResizable(false);
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.centerOnScreen();
		dialog.show();
	}

	private void initLogin() {
		isLocked = true;// LOCK the sytem
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			fxmlLoader.load(getClass().getResourceAsStream(
					"/application/Login.fxml"));
			LoginController loginController = (LoginController) fxmlLoader
					.getController();
			loginController.setStartDisplay(true);
		} catch (IOException e) {
			logger.error(e);
		}
		Parent root = (Parent) fxmlLoader.getRoot();
		Scene dialogSettings = new Scene(root);
		dialog.setScene(dialogSettings);
		dialog.setResizable(false);
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.centerOnScreen();
		dialog.show();
	}

	/*private void displayUserManager() {
		if (isAppRunning) {
			if (AppConfig.getInstance().isUserHasPrevilege()) {
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader fxmlLoader = new FXMLLoader();
				try {
					fxmlLoader.load(getClass().getResourceAsStream(
							"/application/Login.fxml"));
					LoginController loginController = (LoginController) fxmlLoader
							.getController();
					loginController.setShowUserMgmt(true);
				} catch (IOException e) {
					logger.error(e);
				}
				Parent root = (Parent) fxmlLoader.getRoot();
				Scene dialogSettings = new Scene(root);
				dialog.setScene(dialogSettings);
				dialog.setResizable(false);
				dialog.initStyle(StageStyle.UNDECORATED);
				dialog.centerOnScreen();
				dialog.show();
			}
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}*/

	private void sdpSetup() {
		if (isAppRunning) {
			if (AppConfig.getInstance().isUserHasPrevilege()) {
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader fxmlLoader = new FXMLLoader();
				try {
					fxmlLoader.load(getClass().getResourceAsStream(
							"/application/SdpSetUp.fxml"));
					SdpSetUpController sdpSetUpController = (SdpSetUpController) fxmlLoader.getController();
					sdpSetUpController.updateData();
					AppConfig.getInstance().setSdpSetUpController(sdpSetUpController);
				} catch (IOException e) {
					logger.error(e);
				}
				Parent root = (Parent) fxmlLoader.getRoot();
				Scene dialogPreferences = new Scene(root);
				dialog.setScene(dialogPreferences);
				dialog.setResizable(false);
				dialog.initStyle(StageStyle.UNDECORATED);
				dialog.centerOnScreen();
				dialog.show();
			}
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	private void exrxSetup() {
		if (isAppRunning) {
			if (AppConfig.getInstance().isUserHasPrevilege()) {
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader fxmlLoader = new FXMLLoader();
				try {
					fxmlLoader.load(getClass().getResourceAsStream(
							"/application/ExRxSetUp.fxml"));
					ExRxSetUpController exRxSetUpController = (ExRxSetUpController) fxmlLoader.getController();
					exRxSetUpController.updateData();
					AppConfig.getInstance().setExRxSetUpController(exRxSetUpController);
				} catch (IOException e) {
					logger.error(e);
				}
				Parent root = (Parent) fxmlLoader.getRoot();
				Scene dialogPreferences = new Scene(root);
				dialog.setScene(dialogPreferences);
				dialog.setResizable(false);
				dialog.initStyle(StageStyle.UNDECORATED);
				dialog.centerOnScreen();
				dialog.show();
			}
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	private void antennaSetup(String str) {
		if (isAppRunning) {
			if (AppConfig.getInstance().isUserHasPrevilege()) {
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader fxmlLoader = new FXMLLoader();
				try {
					fxmlLoader.load(getClass().getResourceAsStream("/application/AntennaSetUp.fxml"));
					AntennaSetUpController antennaSetUpController = (AntennaSetUpController) fxmlLoader.getController();
					antennaSetUpController.setType(str);
					antennaSetUpController.updateData();
					antennaSetUpController.updateTempHum();
					antennaSetUpController.updateVswr();
					AppConfig.getInstance().setAntSetUpController(antennaSetUpController);
				} catch (IOException e) {
					logger.error(e);
				}
				Parent root = (Parent) fxmlLoader.getRoot();
				Scene dialogPreferences = new Scene(root);
				dialog.setScene(dialogPreferences);
				dialog.setResizable(false);
				dialog.initStyle(StageStyle.UNDECORATED);
				dialog.centerOnScreen();
				dialog.show();
			}
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	private void loggingSetup() {
		if (isAppRunning) {
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader fxmlLoader = new FXMLLoader();
			try {
				fxmlLoader.load(getClass().getResourceAsStream(
						"/application/LoggingSetUp.fxml"));
				LoggingSetUpController loggingSetUpController = (LoggingSetUpController)fxmlLoader.getController();
				AppConfig.getInstance().setLoggingSetUpController(loggingSetUpController);
			} catch (IOException e) {
				logger.error(e);
			}
			Parent root = (Parent) fxmlLoader.getRoot();
			Scene dialogSettings = new Scene(root);
			dialog.setScene(dialogSettings);
			dialog.setResizable(false);
			dialog.initStyle(StageStyle.UNDECORATED);
			dialog.centerOnScreen();
			dialog.show();
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	private void displaySetup() {
		if (isAppRunning) {
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader fxmlLoader = new FXMLLoader();
			try {
				fxmlLoader.load(getClass().getResourceAsStream(
						"/application/DisplaySetUp.fxml"));
			} catch (IOException e) {
				logger.error(e);
			}
			Parent root = (Parent) fxmlLoader.getRoot();
			Scene dialogSettings = new Scene(root);
			dialog.setScene(dialogSettings);
			dialog.setResizable(false);
			dialog.initStyle(StageStyle.UNDECORATED);
			dialog.centerOnScreen();
			dialog.show();
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}
	
	private void safetyParamSetup() {
		if (isAppRunning) {
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader fxmlLoader = new FXMLLoader();
			try {
				fxmlLoader.load(getClass().getResourceAsStream(
						"/application/SafetyLineSetUp.fxml"));
			} catch (IOException e) {
				logger.error(e);
			}
			Parent root = (Parent) fxmlLoader.getRoot();
			Scene dialogSettings = new Scene(root);
			dialog.setScene(dialogSettings);
			dialog.setResizable(false);
			dialog.initStyle(StageStyle.UNDECORATED);
			dialog.centerOnScreen();
			dialog.show();
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	private void systemSetup() {
		if (isAppRunning) {
			if (AppConfig.getInstance().isUserHasPrevilege()) {
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader fxmlLoader = new FXMLLoader();
				try {
					fxmlLoader.load(getClass().getResourceAsStream(
							"/application/SystemSetUp.fxml"));
				} catch (IOException e) {
					logger.error(e);
				}
				Parent root = (Parent) fxmlLoader.getRoot();
				Scene dialogPreferences = new Scene(root);
				dialog.setScene(dialogPreferences);
				dialog.setResizable(false);
				dialog.initStyle(StageStyle.UNDECORATED);
				dialog.centerOnScreen();
				dialog.show();
			}
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	private void showInstallation() {
		if (isAppRunning) {
			if (AppConfig.getInstance().isUserHasPrevilege()) {
				Constance.InstallStatus=1;
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader fxmlLoader = new FXMLLoader();
				try {
					fxmlLoader.load(getClass().getResourceAsStream(
							"/application/Installation.fxml"));
					AppConfig.getInstance()
							.setInstallationController(
									(InstallationController) fxmlLoader
											.getController());
				} catch (IOException e) {
					logger.error(e);
				}
				Parent root = (Parent) fxmlLoader.getRoot();
				Scene dialogPreferences = new Scene(root);
				dialog.setScene(dialogPreferences);
				dialog.setResizable(false);
				dialog.initStyle(StageStyle.UNDECORATED);
				dialog.centerOnScreen();
				dialog.show();
				
			}
		  
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
		
	}

	private void showBite() {
		if (isAppRunning) {
			if (AppConfig.getInstance().isUserHasPrevilege()) {
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader fxmlLoader = new FXMLLoader();
				try {
					fxmlLoader.load(getClass().getResourceAsStream(
							"/application/BiteSetUp.fxml"));
				} catch (IOException e) {
					logger.error(e);
				}
				Parent root = (Parent) fxmlLoader.getRoot();
				Scene dialogPreferences = new Scene(root);
				dialog.setScene(dialogPreferences);
				dialog.setResizable(false);
				dialog.initStyle(StageStyle.UNDECORATED);
				dialog.centerOnScreen();
				dialog.show();
			}
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	private void showAdvSettings() {
		if (isAppRunning) {
			if (AppConfig.getInstance().isUserHasPrevilege()) {
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader fxmlLoader = new FXMLLoader();
				try {
					fxmlLoader.load(getClass().getResourceAsStream(
							"/application/AdvanceSettings.fxml"));
				} catch (IOException e) {
					logger.error(e);
				}
				Parent root = (Parent) fxmlLoader.getRoot();
				Scene dialogPreferences = new Scene(root);
				dialog.setScene(dialogPreferences);
				dialog.setResizable(false);
				dialog.initStyle(StageStyle.UNDECORATED);
				dialog.centerOnScreen();
				dialog.show();
			}
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	private void radarSetup() {
		if (isAppRunning) {
			if (AppConfig.getInstance().isUserHasPrevilege()) {
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader fxmlLoader = new FXMLLoader();
				try {
					fxmlLoader.load(getClass().getResourceAsStream(
							"RadarSetUp.fxml"));
				} catch (IOException e) {
					logger.error(e);
				}
				Parent root = (Parent) fxmlLoader.getRoot();
				Scene dialogPreferences = new Scene(root);
				dialog.setScene(dialogPreferences);
				dialog.setResizable(false);
				dialog.initStyle(StageStyle.UNDECORATED);
				dialog.centerOnScreen();
				dialog.show();
			}
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	private void diamondSetup() {
		if (isAppRunning) {
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader fxmlLoader = new FXMLLoader();
			try {
				fxmlLoader.load(getClass().getResourceAsStream(
						"/application/DiamondOption.fxml"));
				DiamondOptionController diamondOptionController = (DiamondOptionController) fxmlLoader
						.getController();
				diamondOptionController.setElCanvas(cTopL11);
				diamondOptionController.setAzCanvas(cBtmL11);
				AppConfig.getInstance().setDiamondController(
						diamondOptionController);
			} catch (IOException e) {
				logger.error(e);
			}
			Parent root = (Parent) fxmlLoader.getRoot();
			Scene dialogPreferences = new Scene(root);
			dialog.setScene(dialogPreferences);
			dialog.setResizable(false);
			dialog.initStyle(StageStyle.UNDECORATED);
			dialog.centerOnScreen();
			dialog.show();
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	private void runwaySetup() {
		if (isAppRunning) {
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader fxmlLoader = new FXMLLoader();
			try {
				fxmlLoader.load(getClass().getResourceAsStream(
						"/application/RunwaySetUp.fxml"));
			} catch (IOException e) {
				logger.error(e);
			}
			Parent root = (Parent) fxmlLoader.getRoot();
			Scene dialogPreferences = new Scene(root);
			dialog.setScene(dialogPreferences);
			dialog.setResizable(false);
			dialog.initStyle(StageStyle.UNDECORATED);
			dialog.centerOnScreen();
			dialog.show();
		} else {
			AppConfig.getInstance().openErrorDialog("Start the application");
		}
	}

	public void openUserMgmt() {
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			fxmlLoader.load(getClass().getResourceAsStream(
					"/application/MasterSetUp.fxml"));
		} catch (IOException e) {
			logger.error(e);
		}
		Parent root = (Parent) fxmlLoader.getRoot();
		Scene dialogSettings = new Scene(root);
		dialog.setScene(dialogSettings);
		dialog.setResizable(false);
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.centerOnScreen();
		dialog.show();
	}

	private void openTrackOptionsDialog(double x, double y, int trackNo) {

		//if (AppConfig.getInstance().getTrackOptionController() != null)
			//AppConfig.getInstance().getTrackOptionController().getStage()
					//.close();
		
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			fxmlLoader.load(getClass().getResourceAsStream(
					"/application/TrackOption.fxml"));
			TrackOptionController tController = (TrackOptionController) fxmlLoader
					.getController();
			
			tController.setTrackNo(trackNo);
			AppConfig.getInstance().setTrackOptionController(tController);
			
		
			if(trackLabelBuffer.contains(trackNo)){
				tController.loadPref1();
				Iterator<Integer> it = trackLabelBuffer.iterator();
				while (it.hasNext()) {
				    Integer integer = it.next();
				    if (integer == trackNo) {
				        it.remove();
				    }
				}
			}else{
				trackLabelBuffer.add(trackNo);
				
			}
			
			lPoint=trackLabelBuffer.size();	
			if(lPoint>20000){
				trackLabelBuffer.clear();	
			}
				
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}
		Parent root = (Parent) fxmlLoader.getRoot();
		Scene dialogTrack = new Scene(root);
		dialog.setScene(dialogTrack);
		dialog.setResizable(false);
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.setX(OFFSET);
		if ((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY
				.contains("4")))
			dialog.setX(matrixRef.getActualXdimen() - 2 * TEXT_OFFSET);
		dialog.setY(TEXT_OFFSET);
		//dialog.show();
	}

	private void openTrackOptionsDialogEl(double x, double y, int trackNo) {

		//if (AppConfig.getInstance().getTrackOptionController() != null)
			//AppConfig.getInstance().getTrackOptionController().getStage()
					//.close();
		
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			fxmlLoader.load(getClass().getResourceAsStream(
					"/application/TrackOptionEl.fxml"));
			TrackOptionControllerEl tControllerEl = (TrackOptionControllerEl) fxmlLoader
					.getController();
			
			tControllerEl.setTrackNo(trackNo);
			AppConfig.getInstance().setTrackOptionControllerEl(tControllerEl);
			
		
			if(trackLabelBuffer.contains(trackNo)){
				tControllerEl.loadPref1();
				Iterator<Integer> it = trackLabelBuffer.iterator();
				while (it.hasNext()) {
				    Integer integer = it.next();
				    if (integer == trackNo) {
				        it.remove();
				    }
				}
			}else{
				trackLabelBuffer.add(trackNo);
				
			}
			
			lPoint=trackLabelBuffer.size();	
			if(lPoint>20000){
				trackLabelBuffer.clear();	
			}
				
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}
		Parent root = (Parent) fxmlLoader.getRoot();
		Scene dialogTrack = new Scene(root);
		dialog.setScene(dialogTrack);
		dialog.setResizable(false);
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.setX(OFFSET);
		if ((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY
				.contains("4")))
			dialog.setX(matrixRef.getActualXdimen() - 2 * TEXT_OFFSET);
		dialog.setY(TEXT_OFFSET);
		//dialog.show();
	}
	
	
	private void startShowingDisplay() {

		if (!isAppRunning) {
			initSystem();
			initTopChart();
			initBottomChart();
			//startNetworkTask();
   		   // startRcNet();
			startNetworkSettings();
			startDisplayHealthStatus();
			initRunway();
			isAppRunning = true;
			btn_startDisplay
					.setStyle("-fx-background-image: url(\"assets/img/stop.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
			actiontarget.setText("System Loaded!");
			logger.info("System Loaded");
			
		} else {
			if (AppConfig.getInstance().openConfirmationDialog(
					"Do you wish to quite? (warning: display will reset)")) {
				cTopL1.draw();
				cTopL11.draw();
				cTopL2.draw();
				cTopL31.draw();
				cTopL32.draw();
				cTopL4.draw();
				cTopL5.draw();

				cBtmL1.draw();
				cBtmL11.draw();
				cBtmL2.draw();
				cBtmL31.draw();
				cBtmL32.draw();
				cBtmL4.draw();
				cBtmL5.draw();

				resetSystem();
				stopNetworkTask();
				stopRCNet();
				// unlockMe();
				isAppRunning = false;
				btn_startDisplay
						.setStyle("-fx-background-image: url(\"assets/img/start.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
				actiontarget.setText("System Stopped");
				logger.info("System Stopped");
			}
		}
	}
	
	

	public void startNetworkTask() {
		tTask = new TaskObserver();
		tTask.start();
	}

	public void stopNetworkTask() {
		if (Constance.IS_CONNECTED) {
			Constance.IS_CONNECTED = false;
			if (Constance.UDPIP)
				tTask.InterruptableUDPThread();
			tTask.closeActiveConnection();
			tTask.interrupt();
			try {
				tTask.join();
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}

	public TaskObserver getTaskObserver() {
		return tTask;
	}

	private void resetSystem() {
		if (slaveListener != null)
			slaveListener.cancel();

		initDataObserver();
		initControls();
		if (enableZoomDialog)
			toggleZoom();
		logger.info("Resetting System");
	}

	private void shutDown() {
//		if (refreshAnim != null)
//			refreshAnim.cancel();
		if(rxBroadcastRadarInfo != null)
			rxBroadcastRadarInfo.cancel();
		if (myHealth != null)
			myHealth.cancel();
		if (plotElAnimTimer != null)
			plotElAnimTimer.stop();
		if (plotAzAnimTimer != null)
			plotAzAnimTimer.stop();
		if (trackElAnimTimer != null)
			trackElAnimTimer.stop();
		if (trackAzAnimTimer != null)
			trackAzAnimTimer.stop();
		if (rawElAnimTimer != null)
			rawElAnimTimer.stop();
		if (rawAzAnimTimer != null)
			rawAzAnimTimer.stop();
		logger.info("Ended General/Animation Timers");
		Constance.IS_CLOSE = true;
		stopNetworkTask();
	}

	private void initLayout() {

		cTopL0.widthProperty().bind(chartTop.widthProperty());
		cTopL0.heightProperty().bind(chartTop.heightProperty());

		cTopL1.widthProperty().bind(chartTop.widthProperty());
		cTopL1.heightProperty().bind(chartTop.heightProperty());

		cTopL11.widthProperty().bind(chartTop.widthProperty());
		cTopL11.heightProperty().bind(chartTop.heightProperty());

		cTopL2.widthProperty().bind(chartTop.widthProperty());
		cTopL2.heightProperty().bind(chartTop.heightProperty());

		cTopL31.widthProperty().bind(chartTop.widthProperty());
		cTopL31.heightProperty().bind(chartTop.heightProperty());

		cTopL32.widthProperty().bind(chartTop.widthProperty());
		cTopL32.heightProperty().bind(chartTop.heightProperty());

		cTopL4.widthProperty().bind(chartTop.widthProperty());
		cTopL4.heightProperty().bind(chartTop.heightProperty());

		cTopL5.widthProperty().bind(chartTop.widthProperty());
		cTopL5.heightProperty().bind(chartTop.heightProperty());
		cTopL5.setDisable(false);

		cBtmL0.widthProperty().bind(chartBottom.widthProperty());
		cBtmL0.heightProperty().bind(chartBottom.heightProperty());

		cBtmL1.widthProperty().bind(chartBottom.widthProperty());
		cBtmL1.heightProperty().bind(chartBottom.heightProperty());

		cBtmL11.widthProperty().bind(chartBottom.widthProperty());
		cBtmL11.heightProperty().bind(chartBottom.heightProperty());

		cBtmL2.widthProperty().bind(chartBottom.widthProperty());
		cBtmL2.heightProperty().bind(chartBottom.heightProperty());

		cBtmL31.widthProperty().bind(chartBottom.widthProperty());
		cBtmL31.heightProperty().bind(chartBottom.heightProperty());

		cBtmL32.widthProperty().bind(chartBottom.widthProperty());
		cBtmL32.heightProperty().bind(chartBottom.heightProperty());

		cBtmL4.widthProperty().bind(chartBottom.widthProperty());
		cBtmL4.heightProperty().bind(chartBottom.heightProperty());

		cBtmL5.widthProperty().bind(chartBottom.widthProperty());
		cBtmL5.heightProperty().bind(chartBottom.heightProperty());
		cBtmL5.setDisable(false);

		RadarControl.widgetSetPanelTitle("Radar Control");
		DisplayControl.widgetSetPanelTitle("Display Control");
		DisplayScale.widgetSetPanelTitle("Display Scale");
		QNH_QFE_Param.widgetSetPanelTitle("QNH and QFE ");
		renameTrack.widgetSetPanelTitle("Track ID Rename"); 
		userProfileBox.widgetSetPanelTitle("Scale And Units");
		radarSetupBox.widgetSetPanelTitle("Radar Operation ");
		HealthStatus.widgetSetPanelTitle("Health Status");
		
		// For reduced buttons version
		/*RadarControl.widgetSetPanelTitle("RC");
		DisplayControl.widgetSetPanelTitle("DC");
		DisplayScale.widgetSetPanelTitle("DS");
		QNH_QFE_Param.widgetSetPanelTitle("QNH/QFE ");
		renameTrack.widgetSetPanelTitle("TIDR");
		radarSetupBox.widgetSetPanelTitle("ROC");
		userProfileBox.widgetSetPanelTitle("SAU");
		HealthStatus.widgetSetPanelTitle("HS");*/

		userProfileBox.setTitleVisible(true);
		HealthStatus.setDisable(true);
		radarSetUpAnchor.setDisable(true);

		// addPannablePane(chartTop);
		// addPannablePane(chartBottom);

		fxMenuBar.setVisible(false);

	}
	
	 private void addSelectableZoom(Node node,String name) {

	    	node.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (event.getButton() != MouseButton.MIDDLE) {
		                initialX = event.getX();
		                initialY = event.getY();
		                isZoomSelectable = false;
		               
		            }
					
				}
			});
			
	    	node.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (event.getButton() != MouseButton.MIDDLE && isAppRunning && enableZoomDialog1) {
						((ResizableCanvas)node).drawRect(initialX, initialY, Math.abs(event.getX() - initialX), Math.abs(event.getY() - initialY));
						isZoomSelectable = true;
						
		            }			
				}
			});
	    	
	    	node.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (event.getButton() != MouseButton.MIDDLE && isZoomSelectable && isAppRunning && enableZoomDialog1) {
						((ResizableCanvas)node).clear();
						//Window POP UP
						endX = event.getX();
						endY = event.getY();
						
						if(name.equals("AZ"))
						   openZoomDialog();
						if(name.equals("EL"))
						   openZoomDialogEl();
						isZoomSelectable = false;
						
		            }				
				}
			});	
	   }

	private void addTrackSelectable(Node node, String name) {

		node.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						if (event.getButton() != MouseButton.MIDDLE) {
							if (Constance.SHOW_TRACK_LABEL) {
								initialXY.setX(event.getX());
								initialXY.setY(event.getY());
								if (name.equals("AZ")){
									releasedXY = dataObserver.getAzTrackLabelRegion(initialXY);	
								}
								if (name.equals("EL"))
									releasedXY = dataObserver.getElTrackLabelRegion(initialXY);	
							}
						}
					}
				});

		node.addEventHandler(MouseEvent.MOUSE_RELEASED,
				new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						if (event.getButton() != MouseButton.MIDDLE
								&& isAppRunning) {
							((ResizableCanvas) node).clear();
							// Window POP UP
							if (!isAccumulating)
								showTrackPathOverlay(name, event.getX(),
										event.getY());

							if (Constance.SHOW_TRACK_LABEL
									&& releasedXY != null) {
								releasedXY.setX(event.getX());
								releasedXY.setY(event.getY());
		
							   //This is added for track label drag and drop purpose
								/*releasedXY.setX(event.getX()-initialXY.getX());
								releasedXY.setY(event.getY()-initialXY.getY());*/
							}
						}
					}
				});
	}

	private void showTrackPathOverlay(String name, double x, double y) {
		final Point clicked = new Point(x, y, 0, null);
		SketchItemizedOverlay trackList = null;
		double Yoffset = y;
		switch (name) {
		case "AZ":
			trackList = dataObserver.getAzTrackDataList();
			Yoffset = cBtmL5.getLayoutY() + y;
			break;

		case "EL":
			trackList = dataObserver.getElTrackDataList();
			break;

		default:
			break;
		}

		boolean xyBox = false;
		for (int i = 0; i < trackList.size(); i++) {
			Track tk = (Track) trackList.getOverlayItemList(i);
			Point p = tk.getPoint();

			// click within pixel range
			if (Math.abs(clicked.getX() - p.getX()) <= 2*Track.TRACK_SIZE)
				if (Math.abs(clicked.getY() - p.getY()) <= 2*Track.TRACK_SIZE)
					xyBox = true;

			if (xyBox) {
				
				int pathTrackNumber = tk.getTrackNumber();
				logger.info("Path Track No: " + pathTrackNumber + ", XY: " + x
						+ "," + Yoffset);
				if(name.equals("AZ"))
				  openTrackOptionsDialog(x, Yoffset, pathTrackNumber);
				else
				  openTrackOptionsDialogEl(x, Yoffset, pathTrackNumber);

				break;
			}
		}
	}

	

	private void initSystem() {
		initTimeDate();
		// initConsole();
		initMatrixRef();
		initAnimTimers();
		initBroadcastTimer();

		initPoint();
	    initZoom();
		initTrackPath();
		initRadarSetUp();
		initAdvanceSetUp();
		initMasterSetUp();

		setControls();
		setDisplayScale(UserPreference.getInstance().getDISPLAY_SCALE());
		setDataDisplay(DATA.DEFAULT);
		
	}

	private void startDisplayHealthStatus() {
		myHealth = new Timer();
		myHealth.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// send health status
				if (rcNet != null && rcNet.isOnline()) {
					rcNet.sendHealthMsg();
				}
			}
		}, Constance.MILLI_SECOND, 1 * Constance.MILLI_SECOND);
	}

	public void startNetworkSettings() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (AppConfig
						.getInstance()
						.openConfirmationDialog(
								"Do you wish to connect RRM/RC? (Or configure network settings?)")) {
					systemSetup();
				}
			}
		});
	}

	public void startRcNet() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (AppConfig
						.getInstance()
						.openConfirmationDialog(
								"Do you wish to connect and request information from RRM/RC?")) {
					// TODO
					new Thread(new Runnable() {

						@Override
						public void run() {
							int rrmCount = 0;
							InetAddress iPAddress = null;
							DatagramSocket mSocketWriteRRM = null;
							while (true) {
								// response connection status after some attempts
								if (rrmCount++ > RcNet.TIME_REPETITION) {
									Platform.runLater(new Runnable() {

										@Override
										public void run() {
											if(AppConfig
													.getInstance()
													.openConfirmationDialog(
															"No response from RRM. Do you wish to try RC?")){
												tryConnectingRc();
											}
										}
									});
									break;
								}

								try {
									iPAddress = InetAddress
											.getByName(Constance.IP_RRM);
									mSocketWriteRRM = new DatagramSocket(Constance.PORT_RRM_WRITE);
									logger.info("RRM talk: initialized at IP:"
											+ mSocketWriteRRM.getLocalAddress()
											+ " PORT: "
											+ mSocketWriteRRM.getLocalPort());
									break;
								} catch (UnknownHostException e) {
									logger.error(e);
								} catch (SocketException e) {
									logger.error(e);
									
								}

								try {
									Thread.sleep(Constance.MILLI_SECOND);
								} catch (InterruptedException e) {
									logger.error(e);
								}
							}

							// if connected, send msg
							if (mSocketWriteRRM != null) {
								byte[] sendData = new String("100").getBytes();
								final DatagramPacket sendPacket = new DatagramPacket(
										sendData, sendData.length, iPAddress,
										Constance.PORT_RRM_WRITE);

								try {
									mSocketWriteRRM.send(sendPacket);
									logger.info("RRM talk: pkt sent "
											+ sendData.length);
								} catch (IOException e) {
									logger.error(e);
								}

								byte[] receiveData = new byte[5];//length has to be 5
								try {
									mSocketWriteRRM.setSoTimeout(RcNet.TIME_REPETITION*Constance.MILLI_SECOND);
									logger.info("RRM talk: listening...."+RcNet.TIME_REPETITION*Constance.MILLI_SECOND+" ms");
									final DatagramPacket receivePacket = new DatagramPacket(
											receiveData, receiveData.length);
									mSocketWriteRRM.receive(receivePacket);

									//read data
									String rxData = new String(receivePacket.getData());
									logger.info("RRM talk: "+rxData);
									if (rxData.equals("100 0")) {
										// both offline
										AppConfig
												.getInstance()
												.openInformationDialog(
														"Both RC1 and RC2 are unavailable.");
									} else if (rxData.equals("100 1")) {
										// connect to RC1
										radarControlConnection(1);
									} else if (rxData.equals("100 2")) {
										// connect to RC2
										radarControlConnection(2);
									} else {
										Platform.runLater(new Runnable() {

											@Override
											public void run() {
												if(AppConfig
														.getInstance()
														.openConfirmationDialog(
																"Invalid response or Nothing received. Do you wish to try RC?")){
													tryConnectingRc();
												}
											}
										});
									}
								} catch (SocketException e) {
									logger.error(e);
									tryConnectingRc();
								} catch (IOException e) {
									logger.error(e);
									rcNet = null;
									System.gc();
									tryConnectingRc();
								}

								if(mSocketWriteRRM!=null)
									mSocketWriteRRM.close();
							}
						}
					}).start();
				}
			}
		});
	}

	public void stopRCNet() {
		
		if (rcNet != null && rcNet.isOnline()) {
			rcNet.closeActiveConnection();
			
		}
	}

	private void tryConnectingRc() {
		new Thread(new Runnable() {

			int rcNo = 1;

			@Override
			public void run() {
				logger.info("RC reconnecting Thread: Start");
				if(rcNet == null) {
					logger.info("Inside Trying RC ...");
					//trying RC1
					radarControlConnection(rcNo);
					while (rcNet.isRunning()) {
						if(rcNet.isOnline())
							break;
						try {
							Thread.sleep(Constance.MILLI_SECOND);
						} catch (InterruptedException e) {
							logger.error(e);
						}
					}

					//wait sometime
					try {
						Thread.sleep(10*Constance.MILLI_SECOND);
					} catch (InterruptedException e) {
						logger.error(e);
					}

					//trying RC2, if not online, then RC1 then repeat
					rcNo = 2;
					while (!rcNet.isOnline()) {
						logger.info("Toggling RC ...");
						radarControlConnection(rcNo);
						while (rcNet.isRunning()) {
							try {
								Thread.sleep(Constance.MILLI_SECOND);
							} catch (InterruptedException e) {
								logger.error(e);
							}
						}

						//try connecting to different RC
						if(rcNet.isOnline())
							break;
						try {
							Thread.sleep(10*RcNet.TIME_REPETITION * Constance.MILLI_SECOND);
						} catch (InterruptedException e) {
							logger.error(e);
						}

						//toggling between RCs
						if(rcNo == 2)
							rcNo = 1;
						else
							rcNo = 2;
					}
				}
				logger.info("RC reconnecting Thread: End");
			}
		}).start();
	}

	private void radarControlConnection(int key) {
		switch (key) {
		case 1:
			connectRC("1", Constance.IP_RC_1, Constance.PORT_RC_WRITE);
			break;

		case 2:
			connectRC("2", Constance.IP_RC_2, Constance.PORT_RC_WRITE);
			break;
		}
	}

	private void connectRC(final String name, final String ip, final int port) {
		rcNet = new RcNet(name, ip, port);
		Thread rcClient = new Thread(rcNet);
		rcClient.start();
	}

	private void requestLocalMode() {
		if (btn_localRemote.getText().equals("REMOTE")) {
			if (rcNet != null && rcNet.isOnline()) {
				if (AppConfig.getInstance().isDisplayHasPrevilege()) {
					if (AppConfig.getInstance().openConfirmationDialog(
							"Do you wish to control radar in LOCAL mode?")) {
						rcNet.sendLocalMsg();
						logger.info("LR talk: pkt sent");
					}
				}
			} else
				AppConfig.getInstance().openErrorDialog(
						"Network communication error");
		} else {
			AppConfig.getInstance().openErrorDialog(
					"You are in LOCAL mode.");
		}
	}

	private void claimMaster() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (AppConfig.getInstance().openConfirmationDialog(
						"Do you wish to become MASTER?")) {
					rcNet.sendRequestMasterMsg();
				}
			}
		});
	}

	private void claimSlave() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (AppConfig.getInstance().openConfirmationDialog(
						"Do you wish to become SLAVE?")) {
					rcNet.sendRequestSlaveMsg();
				}
			}
		});
	}
	
	public void claimInstallation() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				showInstallation();
				AppConfig.getInstance().getInstallationController().updateClick(null);
				AppConfig.getInstance().getInstallationController().writePcSetting(null);
			}
		});
	}

	private void initTopChart() {
		cTopL5.clear();
		cTopL4.clear();
		cTopL31.clear();
		cTopL32.clear();
		cTopL2.clear();
		cTopL1.clear();
		cTopL1.clear();
		cTopL0.clear();
		drawGraphTop(cTopL1);
		drawTextTop(cTopL0);
//		drawDiamondTop(cTopL11);
		logger.info("Top Chart initialization");
	}

	private void initBottomChart() {
		cBtmL5.clear();
		cBtmL4.clear();
		cBtmL31.clear();
		cBtmL32.clear();
		cBtmL2.clear();
		cBtmL11.clear();
		cBtmL1.clear();
		cBtmL0.clear();
		drawGraphBottom(cBtmL1);
		drawTextBottom(cBtmL0);
		drawDiamondBottom(cBtmL11);
		logger.info("Bottom Chart initialization");
	}

	private void initAdmin() {
		// Create Admin if doesn't exist
		if (!dbRecord.isUserTableExist()) {
			createAdminUser();
		} else if (!dbRecord.isUserExist("admin")) {
			createAdminUser();
		} else {
			logger.info("ADMIN exists");
		}
	}

	private void createAdminUser() {
		User admin = new User("0", "ADMIMISTRATOR", "admin", "admin",
				Constance.USER_MASTER);
		if (dbRecord.createUserDataToDb(admin))
			logger.info("Successfully created ADMIN");
	}

	private void initControls() {

		txtField_Sp.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

		     @Override
		     public void handle(MouseEvent event) {
		         sdpSetup();
		     }
		});

		txtField_exrx.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

		     @Override
		     public void handle(MouseEvent event) {
		         exrxSetup();
		     }
		});

		txtField_Ac1.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

		     @Override
		     public void handle(MouseEvent event) {
		         antennaSetup("AZ");
		     }
		});

		txtField_Ac2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

		     @Override
		     public void handle(MouseEvent event) {
		         antennaSetup("EL");
		     }
		});

		btn_showUserSetting.setTooltip(new Tooltip("Logging Management"));
		btn_showUserSetting.setTooltip(new Tooltip("Safety Line Settings"));
		btn_showMasterSetting.setTooltip(new Tooltip("Master/Slave"));
		btn_showSystemSetting.setTooltip(new Tooltip("System Settings"));
		btn_showDisplaySetting.setTooltip(new Tooltip("Display Management"));
		btn_showInstallation.setTooltip(new Tooltip("Installation Settings"));
		btn_showBite.setTooltip(new Tooltip("BITE Settings"));
		btn_showAdvSettings.setTooltip(new Tooltip("Advanced Settings"));
		btn_refresh.setTooltip(new Tooltip("Refresh/Accumulate"));
		btn_zoom.setTooltip(new Tooltip("Zoom"));
		btn_record.setTooltip(new Tooltip("Record/Replay"));
		btn_showRunway.setTooltip(new Tooltip("Runway Management"));
		btn_showDiamond.setTooltip(new Tooltip("Configuration"));
		btn_startDisplay.setTooltip(new Tooltip("Start/Stop"));
		btn_zoom1.setTooltip(new Tooltip("Zone Creation"));
		selectPoint.setTooltip(new Tooltip("Co-ordinate points"));
		tdp_msl.setTooltip(new Tooltip("Height wrt TDP/MSL"));
		btn_Zone.setTooltip(new Tooltip("Zone Updates"));
		btn_showStatus.setTooltip(new Tooltip("Health Status"));
		
		zoomSliderTop.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {

				double scaleXVal = new_val.doubleValue();
				String str = Constance.PREF.SEL_RUNWAY;
				if (str.contains("3") || str.contains("4")) {
					scaleXVal = -new_val.doubleValue();
				}

				cTopL5.setScaleX(scaleXVal);
				cTopL5.setScaleY(new_val.doubleValue());
				cTopL4.setScaleX(scaleXVal);
				cTopL4.setScaleY(new_val.doubleValue());
				cTopL31.setScaleX(scaleXVal);
				cTopL31.setScaleY(new_val.doubleValue());
				cTopL32.setScaleX(scaleXVal);
				cTopL32.setScaleY(new_val.doubleValue());
				cTopL2.setScaleX(scaleXVal);
				cTopL2.setScaleY(new_val.doubleValue());
				cTopL11.setScaleX(scaleXVal);
				cTopL11.setScaleY(new_val.doubleValue());
				cTopL1.setScaleX(scaleXVal);
				cTopL1.setScaleY(new_val.doubleValue());
				// cTopL0.setScaleX(scaleXVal);
				// cTopL0.setScaleY(new_val.doubleValue());

				if (new_val.intValue() == 1) {
					chartTop.setTranslateX(0);
					chartTop.setTranslateY(0);
				}
			}
		});

		zoomSliderBtm.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {

				double scaleXVal = new_val.doubleValue();
				String str = Constance.PREF.SEL_RUNWAY;
				if (str.contains("3") || str.contains("4")) {
					scaleXVal = -new_val.doubleValue();
				}

				cBtmL5.setScaleX(scaleXVal);
				cBtmL5.setScaleY(new_val.doubleValue());
				cBtmL4.setScaleX(scaleXVal);
				cBtmL4.setScaleY(new_val.doubleValue());
				cBtmL31.setScaleX(scaleXVal);
				cBtmL31.setScaleY(new_val.doubleValue());
				cBtmL32.setScaleX(scaleXVal);
				cBtmL32.setScaleY(new_val.doubleValue());
				cBtmL2.setScaleX(scaleXVal);
				cBtmL2.setScaleY(new_val.doubleValue());
				cBtmL11.setScaleX(scaleXVal);
				cBtmL11.setScaleY(new_val.doubleValue());
				cBtmL1.setScaleX(scaleXVal);
				cBtmL1.setScaleY(new_val.doubleValue());
				// cBtmL0.setScaleX(scaleXVal);
				// cBtmL0.setScaleY(new_val.doubleValue());

				if (new_val.intValue() == 1) {
					chartBottom.setTranslateX(0);
					chartBottom.setTranslateY(0);
				}
			}
		});

		setNodeStyle(textDate);
		setNodeStyle(textTime);

		btn_localRemote.widgetSetVal(VAL.DEFAULT);
		btn_Ibit.widgetSetVal(VAL.DEFAULT);
	
		btn_track.widgetSetVal(VAL.DEFAULT);
		btn_trackLabel.widgetSetVal(VAL.DEFAULT);
		btn_raw.widgetSetVal(VAL.DEFAULT);
		btn_plot.widgetSetVal(VAL.DEFAULT);
		btn_checkmarks.widgetSetVal(VAL.DEFAULT);
		btn_unitsdisplay.widgetSetVal(VAL.DEFAULT);

		resetDisplayScaleButtonColors();
		updateUnits();
		resetScaleAndUnitColors();
		//updateScaleAndUnitNames();

		setTextField(VAL.DEFAULT, txtField_Sp);
		setTextField(VAL.DEFAULT, txtField_exrx);
		setTextField(VAL.DEFAULT, txtField_Ac1);
		setTextField(VAL.DEFAULT, txtField_Pc);
		setTextField(VAL.DEFAULT, txtField_Ac2);
		setTextField(VAL.DEFAULT, txtField_Ch1);
		setTextField(VAL.DEFAULT, txtField_Ch2);
		setTextField(VAL.DEFAULT, txtField_lan);
		setTextField(VAL.DEFAULT, txtField_AzPwr);
		setTextField(VAL.DEFAULT, txtField_ElPwr);

		UIRightAnchor.getChildren().removeAll(controlBox, statusBox,statusBox1);
		UIRightAnchor.getChildren().addAll(statusBox);
		Display_Control.getChildren().remove(trackInfo_Units);
		allButtonsBelow.getChildren().remove(btn_showDisplaySetting);
		
		txOn.widgetSetVal(VAL.DEFAULT);
		txOff.widgetSetVal(VAL.DEFAULT);
		btn_Update_omp.widgetSetVal(VAL.DEFAULT);

		RadarControl.setDisable(true);
		DisplayControl.setDisable(true);
		HealthStatus.setDisable(false);
		radarSetUpAnchor.setDisable(true);
		DisplayScale.setDisable(true);
		QNH_QFE_Param.setDisable(true);
		renameTrack.setDisable(true);

		btn_showMasterSetting
				.setStyle("-fx-background-image: url(\"assets/img/master_settings.png\"); -fx-background-size: 60 60; -fx-background-repeat: no-repeat; -fx-background-position: center;");

	}

	private void resetDisplayScaleButtonColors() {
		btn_display5.widgetSetVal(VAL.DEFAULT);
		btn_display10.widgetSetVal(VAL.DEFAULT);
		btn_display20.widgetSetVal(VAL.DEFAULT);
		btn_display40.widgetSetVal(VAL.DEFAULT);
	}
	
	private void resetScaleAndUnitColors() {
		btn_linear.widgetSetVal(VAL.DEFAULT);
		btn_logged.widgetSetVal(VAL.GREEN);
		btn_km.widgetSetVal(VAL.DEFAULT);
		btn_nm.widgetSetVal(VAL.GREEN);
	}
	
	private void initDataObserver() {
		dataObserver = DataManager.newInstance();
	}

	public void initUIComponents(String str) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				MainView.setLeft(null);
				MainView.setRight(null);
				if (str.contains("Left")) {
					MainView.setRight(UIControls);
				} else if (str.contains("Right")) {
					MainView.setLeft(UIControls);
				}
			}
		});
	}
	
	

	public void flipGraphOnRunwaySel(String str) {
		mElevationChart.drawUnitsText(toggleUnits);
		mAzimuthChart.drawUnitsText(toggleUnits);
		if (str.contains("3") || str.contains("4")) {
			DrawingUtils.flipCanvasDrawing(cTopL1, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cTopL11, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cTopL31, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cTopL32, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cTopL2, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cTopL4, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cTopL5, FLIP.R2L);

			DrawingUtils.flipCanvasDrawing(cBtmL1, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cBtmL11, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cBtmL31, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cBtmL32, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cBtmL2, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cBtmL4, FLIP.R2L);
			DrawingUtils.flipCanvasDrawing(cBtmL5, FLIP.R2L);

			// zoom controls
			AnchorPane.setLeftAnchor(zoomTopControl,
					matrixRef.getDrawableXArea() - 5 * TEXT_OFFSET);
			AnchorPane.setLeftAnchor(zoomBtmControl,
					matrixRef.getDrawableXArea() - 5 * TEXT_OFFSET);

		} else if (str.contains("1") || str.contains("2")) {
			DrawingUtils.flipCanvasDrawing(cTopL1, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cTopL11, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cTopL31, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cTopL32, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cTopL2, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cTopL4, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cTopL5, FLIP.L2R);

			DrawingUtils.flipCanvasDrawing(cBtmL1, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cBtmL11, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cBtmL31, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cBtmL32, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cBtmL2, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cBtmL4, FLIP.L2R);
			DrawingUtils.flipCanvasDrawing(cBtmL5, FLIP.L2R);

			AnchorPane.setLeftAnchor(zoomTopControl, (double) (5 * OFFSET));
			AnchorPane.setLeftAnchor(zoomBtmControl, (double) (5 * OFFSET));
		}
	}

	public void updateUnits() {
		
		Constance.SCALE = (int) (matrixRef.getVisibleRange()-Math.abs(Constance.TOUCH_DOWN)*Constance.UNITS.getFACTOR_LENGTH()+(Constance.UNITS.isLogged?1:0)) + " "
				+ Constance.UNITS.getLENGTH();
		
		int five = (int)(5 * Constance.UNITS.getFACTOR_LENGTH()+(Constance.UNITS.isLogged?1:0));
		btn_display5.setText(" "+five);
		
		int ten = (int)(10 * Constance.UNITS.getFACTOR_LENGTH()+(Constance.UNITS.isLogged?1:0));
		btn_display10.setText(" "+ten);
		
		int twenty = (int)(20 * Constance.UNITS.getFACTOR_LENGTH()+(Constance.UNITS.isLogged?1:0));
		btn_display20.setText(" "+twenty);
		
		int thirty = (int)(30 * Constance.UNITS.getFACTOR_LENGTH()+(Constance.UNITS.isLogged?1:0));
		btn_display40.setText(" "+thirty);
		
		/*btn_display40.setText(" "
		+ Math.floor(30 * Constance.UNITS.getFACTOR_LENGTH()+(Constance.UNITS.isLogged?1:0)) + " "
		);*/
	}

	private void initTimeDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

		Task<Void> timedate = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				while (!Constance.IS_CLOSE) {
					Date date = new Date();
					updateTitle(dateFormat.format(date));
					updateMessage(timeFormat.format(date));
					TimeUnit.MICROSECONDS.sleep(Constance.MILLI_SECOND);
				}
				return null;
			}
		};
		textDate.textProperty().bind(timedate.titleProperty());
		textTime.textProperty().bind(timedate.messageProperty());
		new Thread(timedate).start();

	}

	private void initMatrixRef() {
		matrixRef.setActualElevationXY(chartTop.getWidth(),
				chartTop.getHeight());
		matrixRef.setActualAzimuthXY(chartBottom.getWidth(),
				chartBottom.getHeight());

		matrixRef.setElevationVal(Constance.ELEVATION_MAX,
				Constance.ELEVATION_MIN);
		matrixRef.setAzimuthVal(Constance.AZIMUTH_MAX, Constance.AZIMUTH_MIN);
//		if((Constance.PREF.SEL_RUNWAY.contains("3") || Constance.PREF.SEL_RUNWAY.contains("4"))){
//			matrixRef.setElevationVal(Constance.ELEVATION_MIN,
//					Constance.ELEVATION_MAX);
//			matrixRef.setAzimuthVal(Constance.AZIMUTH_MIN, Constance.AZIMUTH_MAX);
//		}
		matrixRef.setRangeVal(Constance.RANGE_MAX, Constance.RANGE_MIN);
		matrixRef.setStartRange(Constance.TOUCH_DOWN*Constance.UNITS.getFACTOR_LENGTH());
		matrixRef.setStartRange1(Constance.RANGE_MIN);
	}

	private void broadcastRadarInfoSignal() {
		//broadcast signal
		OperationModeMsg opMsg = radarSetUpController.getOpMsg();
		sendBytes(opMsg.encode().array());
	}

	private void initBroadcastTimer() {
		rxBroadcastRadarInfo = new Timer();
		rxBroadcastRadarInfo.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (MasterSlavery.getInstance().isToggleState()) {

					if(MasterSlavery.getInstance().isMaster())
						broadcastRadarInfoSignal();

					//receive signal
					OperationModeMsg opMsg2 = new OperationModeMsg();
					opMsg2.decode(receiveBytes());
					radarSetUpController.setUpdatedOpModeMsg(opMsg2);
				}
			}
		}, Constance.MILLI_SECOND, Constance.MILLI_SECOND);
	}

	private void initAnimTimers() {

		// THIS is needed to clear all tracks if no msg is received
//		refreshAnim = new Timer();
//		refreshAnim.scheduleAtFixedRate(new TimerTask() {
//
//			@Override
//			public void run() {
//				// clear obslete tracks
//				if (!isAccumulating) {
//					dataObserver.clearObseleteElTracks();
//					dataObserver.clearObseleteAzTracks();
//				}
//			}
//		}, Constance.MILLI_SECOND, (long) (1 * Constance.MILLI_SECOND));

		// graph refresher
		plotElAnimTimer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				if (Constance.IS_CONNECTED && plotElRefresh
						&& !Constance.IS_PAUSE_SETUP) {
					// Plot
					GraphicsContext gcEl = cTopL32.getGraphicsContext2D();
					if (!isAccumulating) {
						gcEl.clearRect(0, 0, cTopL32.getWidth(),
								cTopL32.getHeight());
					}

					if (dataObserver.isElPlotAdded()) {
						dataObserver.getElPlotDataList().drawPlots(gcEl);
						dataObserver.setElPlotAdded(false);
						dataObserver.clearObseleteElPlots();
					}

					plotElRefresh = false;
					// logger.info("Plot EL Objects Refreshed");
				}
			}
		};

		plotAzAnimTimer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				if (Constance.IS_CONNECTED && plotAzRefresh
						&& !Constance.IS_PAUSE_SETUP) {
					// Plot
					GraphicsContext gcAz = cBtmL32.getGraphicsContext2D();
					if (!isAccumulating) {
						gcAz.clearRect(0, 0, cBtmL32.getWidth(),
								cBtmL32.getHeight());
					}

					if (dataObserver.isAzPlotAdded()) {
						dataObserver.getAzPlotDataList().drawPlots(gcAz);
						dataObserver.setAzPlotAdded(false);
						dataObserver.clearObseleteAzPlots();
					}

					plotAzRefresh = false;
					// logger.info("Plot AZ Objects Refreshed");
				}
			}
		};

		trackElAnimTimer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				if (Constance.IS_CONNECTED && trackElRefresh) {
					// Tracks
					GraphicsContext gcEl = cTopL31.getGraphicsContext2D();
					if (!isAccumulating) {
						gcEl.clearRect(0, 0, cTopL31.getWidth(),
								cTopL31.getHeight());
					}

					// Need to draw path history & label here
					TrackOptionControllerEl tControllerEl = AppConfig.getInstance().getTrackOptionControllerEl();
					if (tControllerEl != null) {
						GraphicsContext gcPathEl = cTopL4.getGraphicsContext2D();
						gcPathEl.clearRect(0, 0, cTopL4.getWidth(),cTopL4.getHeight());
						dataObserver.drawElSketchPathOverlay(gcPathEl,tControllerEl);
					}
					
					if (dataObserver.isElTrackAdded()) {
						dataObserver.getElTrackDataList().drawTracks(gcEl);
						dataObserver.setElTrackAdded(false);
					}

					trackElRefresh = false;
					// logger.info("EL Track Objects Refreshed");
				}
			}
		};

		trackAzAnimTimer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				if (Constance.IS_CONNECTED && trackAzRefresh) {
					// Tracks
					GraphicsContext gcAz = cBtmL31.getGraphicsContext2D();
					if (!isAccumulating) {
						gcAz.clearRect(0, 0, cBtmL31.getWidth(),
								cBtmL31.getHeight());
					}
					
					// Need to draw path & label history here
					TrackOptionController tController = AppConfig.getInstance()
							.getTrackOptionController();
					if (tController != null) {
						GraphicsContext gcPathAz = cBtmL4
								.getGraphicsContext2D();
						gcPathAz.clearRect(0, 0, cBtmL4.getWidth(),
								cBtmL4.getHeight());
						dataObserver.drawAzSketchPathOverlay(gcPathAz,
								tController);
					}

					
					if (dataObserver.isAzTrackAdded()) {
						dataObserver.getAzTrackDataList().drawTracks(gcAz);
						dataObserver.setAzTrackAdded(false);
						
					}
					
					trackAzRefresh = false;
					// logger.info("AZ Track Objects Refreshed");
				}
			}
		};

		rawElAnimTimer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				if (Constance.IS_CONNECTED && Constance.SHOW_RAW) {
					// EL Video
					GraphicsContext tgc = cTopL2.getGraphicsContext2D();

					if (dataObserver.isElVideoAdded()) {
						tgc.clearRect(0, 0, cTopL2.getWidth(),
								cTopL2.getHeight());
						for (SketchItemizedOverlay sOverlay : dataObserver
								.getElFullVideoList())
							sOverlay.drawVideos(tgc);
						dataObserver.setElVideoAdded(false);
					}
				}
			}
		};

		rawAzAnimTimer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				if (Constance.IS_CONNECTED && Constance.SHOW_RAW) {
					// AZ Video
					GraphicsContext bgc = cBtmL2.getGraphicsContext2D();

					if (dataObserver.isAzVideoAdded()) {
						bgc.clearRect(0, 0, cBtmL2.getWidth(),
								cBtmL2.getHeight());
						for (SketchItemizedOverlay sOverlay : dataObserver
								.getAzFullVideoList())
							sOverlay.drawVideos(bgc);
						dataObserver.setAzVideoAdded(false);
					}
					// logger.info("AZ Video Objects Refreshed");
				}
			}
		};

		// start animation timers
		plotElAnimTimer.start();
		plotAzAnimTimer.start();
		trackElAnimTimer.start();
		trackAzAnimTimer.start();
		rawElAnimTimer.start();
		rawAzAnimTimer.start();
	}
	
	 private void initZoom() {
			addSelectableZoom(cTopL5,"EL");
			addSelectableZoom(cBtmL5,"AZ");
	    }
	    
	private void initTrackPath() {
		addTrackSelectable(cTopL5, "EL");
		addTrackSelectable(cBtmL5, "AZ");
	}
	
	private void initPoint() {
		addSelectablePoint(cTopL5,"EL");
		addSelectablePoint(cBtmL5,"AZ");	
    }
	
	private void initRadarSetUp() {
		radarSetUpController = new RadarSetUpController( txOn, txOff, QNH, QFE);
		radarSetUpController.initRadarSetUp();
		
	}
	
	private void initAdvanceSetUp() {
		advanceSetUp = new AdvanceSettingsController( btn_MTI_ON, btn_MTI_OFF);
		advanceSetUp.initAdvanceSetUp();
		
	}

	private void initRunway() {
		int runwayIndex = Integer.parseInt(UserPreference.getInstance()
				.getOP_RUNWAY());
		Constance.PREF.SEL_RUNWAY = String.valueOf(runwayIndex + 1);
		Constance.PREF.LEFT_RIGHT = "Right";
		if (Constance.PREF.SEL_RUNWAY.equals("1")
				|| Constance.PREF.SEL_RUNWAY.equals("2"))
			Constance.PREF.LEFT_RIGHT = "Left";
		flipGraphOnRunwaySel(Constance.PREF.SEL_RUNWAY);
		initUIComponents(Constance.PREF.LEFT_RIGHT);

		reDrawGraphs();
	}

	public void reDrawGraphs() {
		cTopL11.clear();
		cTopL1.clear();
		cTopL0.clear();
		drawGraphTop(cTopL1);
		drawTextTop(cTopL0);
		//drawDiamondTop(cTopL11);
		logger.info("Top Chart Updated!");

		cBtmL11.clear();
		cBtmL1.clear();
		cBtmL0.clear();
		drawGraphBottom(cBtmL1);
		drawTextBottom(cBtmL0);
		drawDiamondBottom(cBtmL11);
		logger.info("Bottom Chart Updated!");

	}

	public void invalidate() {
		initMatrixRef();

		cTopL5.clear();
		cTopL4.clear();
		cTopL31.clear();
		cTopL32.clear();
		cTopL2.clear();
		cTopL11.clear();
		cTopL1.clear();
		cTopL0.clear();
		drawGraphTop(cTopL1);
		drawTextTop(cTopL0);
//		drawDiamondTop(cTopL11);
		logger.info("Top Chart Invalidated!");

		cBtmL5.clear();
		cBtmL4.clear();
		cBtmL31.clear();
		cBtmL32.clear();
		cBtmL2.clear();
		cBtmL11.clear();
		cBtmL1.clear();
		cBtmL0.clear();
		drawGraphBottom(cBtmL1);
		drawTextBottom(cBtmL0);
		drawDiamondBottom(cBtmL11);
		logger.info("Bottom Chart Invalidated!");
			
	}

	private void setControls() {
		RadarControl.setDisable(false);
		DisplayControl.setDisable(false);
		radarSetUpAnchor.setDisable(false);
		DisplayScale.setDisable(false);
		QNH_QFE_Param.setDisable(false);
		renameTrack.setDisable(false);

		btn_Ibit.widgetSetVal(VAL.YELLOW);
		btn_unitsdisplay.widgetSetVal(VAL.GREEN);
		btn_checkmarks.widgetSetVal(VAL.GREEN);

		displayScaleSlider.valueProperty().addListener(
				new ChangeListener<Number>() {

					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, Number newValue) {
						int val = newValue.intValue();
						logger.info("Display Scale: " + val);
						setDisplayScale(val);
						invalidate();
						refreshAccumulate();
						refreshAccumulate();
					}
				});
	}

	private void setDisplayScale(double v) {
		double val = v+Math.abs(Constance.TOUCH_DOWN)*Constance.UNITS.getFACTOR_LENGTH();
		UserPreference.getInstance().setDISPLAY_SCALE(val);
		matrixRef.setVisibleRange(val);
		displayScaleSlider.setMax(matrixRef.getMaxRange());
		updateUnits();
	}

	private void initMasterSetUp() {
		MasterSlavery.getInstance().setMaster(false);
		btn_localRemote.setText("LOCAL");
		btn_localRemote.widgetSetVal(VAL.RED);
		if (MasterSlavery.getInstance().isMaster()) {
			btn_showMasterSetting
					.setStyle("-fx-background-image: url(\"assets/img/master_settings_on.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		} else {
			btn_showMasterSetting
					.setStyle("-fx-background-image: url(\"assets/img/master_settings_off.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		}

//		// Listen for Broadcast
//		if (slaveListener == null) {
//			slaveListener = new Timer();
//			slaveListener.schedule(new TimerTask() {
//
//				@Override
//				public void run() {
//					logger.info("MaSl: Started listener");
//					while (isAppRunning) {
//						try {
//							masterSlavery.receiveBroadcast(tTask.receiveBytes());
//						} catch (IOException e) {
//							logger.error(e);
//							break;
//						}
//					}
//					slaveListener.cancel();
//					slaveListener = null;
//				}
//			}, Constance.MILLI_SECOND / 5);
//		}
	}

	private void requestIbit() {
		if (rcNet.isOnline()) {
			if (AppConfig.getInstance().openConfirmationDialog(
					"Do you wish to request radar health status?")) {
				IbitRequestMsg ibitRequestMsg = new IbitRequestMsg();
				ibitRequestMsg.setIbit((short) 1);
				sendRCBytes(ibitRequestMsg.encode().array());
				btn_Ibit.widgetSetVal(VAL.GREEN);
			}
		} else
			AppConfig.getInstance().openErrorDialog(
					"RC Network communication error");
	}

	public void masterNotify() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// update UI
				cTopL0.clear();
				drawTextTop(cTopL0);
				if (MasterSlavery.getInstance().isMaster()) {
					AppConfig.getInstance().openInformationDialog(
							"You are MASTER!");
					btn_showMasterSetting
							.setStyle("-fx-background-image: url(\"assets/img/master_settings_on.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
				} else {
					AppConfig.getInstance().openInformationDialog(
							"You are SLAVE!");
					btn_showMasterSetting
							.setStyle("-fx-background-image: url(\"assets/img/master_settings_off.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
				}
			}
		});
	}

	private void setDataDisplay(DATA data) {
		switch (data) {
		case RAW:
			btn_raw.widgetSetVal(VAL.DEFAULT);
			if (Constance.SHOW_RAW)
				btn_raw.widgetSetVal(VAL.GREEN);
			break;

		case PLOT:
			btn_plot.widgetSetVal(VAL.DEFAULT);
			if (Constance.SHOW_PLOT)
				btn_plot.widgetSetVal(VAL.GREEN);
			break;

		case TRACK:
			btn_track.widgetSetVal(VAL.DEFAULT);
			if (Constance.SHOW_TRACK)
				btn_track.widgetSetVal(VAL.GREEN);
			break;

		case TRACK_LABEL:
			btn_trackLabel.widgetSetVal(VAL.DEFAULT);
			if (Constance.SHOW_TRACK_LABEL)
				btn_trackLabel.widgetSetVal(VAL.GREEN);
			break;

		default:
			btn_raw.widgetSetVal(VAL.DEFAULT);
			if (Constance.SHOW_RAW)
				btn_raw.widgetSetVal(VAL.GREEN);

			btn_plot.widgetSetVal(VAL.DEFAULT);
			if (Constance.SHOW_PLOT)
				btn_plot.widgetSetVal(VAL.GREEN);

			btn_track.widgetSetVal(VAL.DEFAULT);
			if (Constance.SHOW_TRACK)
				btn_track.widgetSetVal(VAL.GREEN);

			btn_trackLabel.widgetSetVal(VAL.DEFAULT);
			if (Constance.SHOW_TRACK_LABEL)
				btn_trackLabel.widgetSetVal(VAL.GREEN);
			break;
		}
	}

	private void drawGraphTop(ResizableCanvas canvas) {
		mElevationChart = new ElevationChart(canvas);
		mElevationChart.drawRadarLocation();
		mElevationChart.drawElevationLine();
		mElevationChart.drawRedDistanceLine();
		mElevationChart.drawLandingStrip();
		//if (isRangeMarks)
			mElevationChart.drawDistanceGrid();
		mElevationChart.drawUnitsText(toggleUnits);
	}

	private void drawTextTop(ResizableCanvas canvas) {
		mElevationChart.drawText(canvas.getGraphicsContext2D());
	}

	private void drawGraphBottom(ResizableCanvas canvas) {
		mAzimuthChart = new AzimuthChart(canvas);
		mAzimuthChart.drawRadarLocation();
		mAzimuthChart.drawAzimuthLine();
		mAzimuthChart.drawRedDistanceLine();
		mAzimuthChart.drawLandingStrip();
		//if (isRangeMarks)
			mAzimuthChart.drawDistanceGrid();
		mAzimuthChart.drawUnitsText(toggleUnits);
	}

	private void drawTextBottom(ResizableCanvas canvas) {
		mAzimuthChart.drawText(canvas.getGraphicsContext2D());
	}

//	private void drawDiamondTop(ResizableCanvas canvas) {
//		if (AppConfig.getInstance().getDiamondController() != null)
//			AppConfig.getInstance().getDiamondController()
//					.drawElDiamonds(canvas);
//	}

	 void drawDiamondBottom(ResizableCanvas canvas) {
		if (AppConfig.getInstance().getDiamondController() != null)
			AppConfig.getInstance().getDiamondController()
					.drawAzDiamonds(canvas);
	}

	public void startDisplayAction() {
		if (!Constance.IS_DISPLAY_SETUP) {
			Alert alert = new Alert(AlertType.INFORMATION,
					"Do you like to configure Display Setup for the radar now "
							+ "?", ButtonType.YES, ButtonType.NO);
			alert.setTitle("Alert");
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES) {
				displaySetup();
				actiontarget.setTextFill(Color.RED);
				actiontarget
						.setText("Please perform the Display setup accordingly!");
				actiontarget.setTextFill(Color.AQUAMARINE);
			} else {
				
				startShowingDisplay();
			}
		} else if (!Constance.IS_SYSTEM_SETUP) {
			Alert alert = new Alert(AlertType.INFORMATION,
					"Choose your System Setup. Do you want to change now ? ",
					ButtonType.YES, ButtonType.NO);
			alert.setTitle("Alert");
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES) {
				systemSetup();
				actiontarget.setTextFill(Color.RED);
				actiontarget
						.setText("Please perform the System setup accordingly!");
				actiontarget.setTextFill(Color.AQUAMARINE);
			} else {
				startShowingDisplay();
			}
		} else if (!Constance.IS_RADAR_SETUP) {
			Alert alert = new Alert(AlertType.INFORMATION,
					"Choose your Radar Setup. Do you want to change now ? ",
					ButtonType.YES, ButtonType.NO);
			alert.setTitle("Alert");
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES) {
				systemSetup();
				actiontarget.setTextFill(Color.RED);
				actiontarget
						.setText("Please perform the Radar setup accordingly!");
				actiontarget.setTextFill(Color.AQUAMARINE);
			} else {
				startShowingDisplay();
			}
		} else {
			startShowingDisplay();
			
		}
	}

	private void toggleZoom() {
		if (!enableZoomDialog) {
			zoomTopControl.setVisible(true);
			zoomBtmControl.setVisible(true);
			btn_zoom.setStyle("-fx-background-image: url(\"assets/img/zoom_on.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		} else {
			chartTop.setTranslateX(0);
			chartTop.setTranslateY(0);
			chartBottom.setTranslateX(0);
			chartBottom.setTranslateY(0);
			zoomTopControl.setVisible(false);
			zoomBtmControl.setVisible(false);
			btn_zoom.setStyle("-fx-background-image: url(\"assets/img/zoom_off.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		}
		enableZoomDialog = !enableZoomDialog;
	}

	private void refreshAccumulate() {
		if (isAppRunning) {
			if (!isAccumulating) {
				invalidate();
				if (dataObserver != null) {
					dataObserver.clearAzTrackData();
					dataObserver.clearElTrackData();
				}
				btn_refresh
						.setStyle("-fx-background-image: url(\"assets/img/refresh_off.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
			} else
				btn_refresh
						.setStyle("-fx-background-image: url(\"assets/img/refresh_on.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
			isAccumulating = !isAccumulating;
		} else {
			AppConfig.getInstance().openErrorDialog("Start Application");
		}
	}

	private void startRecordAction() {
		if (isAppRunning) {
			if (AppConfig.getInstance().getReplayController() == null) {
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
				FXMLLoader fxmlLoader = new FXMLLoader();
				try {
					fxmlLoader.load(getClass().getResourceAsStream(
							"ReplayActivity.fxml"));
					ReplayController replayController = (ReplayController) fxmlLoader
							.getController();
					replayController.setReplayActivity(dialog);
					AppConfig.getInstance().setReplayController(
							replayController);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error(e);
				}
				Parent root = (Parent) fxmlLoader.getRoot();
				Scene scene = new Scene(root);
				dialog.setScene(scene);
				dialog.setResizable(false);
				dialog.initStyle(StageStyle.UNDECORATED);
				dialog.centerOnScreen();
				dialog.show();
			} else {
				AppConfig.getInstance().getReplayController()
						.getReplayActivity().show();
			}
		} else {
			AppConfig.getInstance().openErrorDialog(
					"Application is not running");
		}
	}

	public void notifyChanges() {
		if (!isAppRunning)
			startDisplayAction();
		else
			invalidate();
	}

	public String getTime() {
		return textTime.getText();
	}

	public String getDate() {
		return textDate.getText();
	}

	public void setZoomDialogEnable(boolean bool) {
		enableZoomDialog = bool;
	}

	public boolean isAppRunning() {
		return isAppRunning;
	}

	public boolean isAccumulating() {
		return isAccumulating;
	}

	public void replayELPData() {
		if(tTask!=null)
			tTask.replayELPData();
		else
			AppConfig.getInstance().openErrorDialog("Network connection needed.");
	}

	public void replayELTData() {
		if(tTask!=null)
			tTask.replayELTData();
		else
			AppConfig.getInstance().openErrorDialog("Network connection needed.");
	}

	public void replayAZPData() {
		if(tTask!=null)
			tTask.replayAZPData();
		else
			AppConfig.getInstance().openErrorDialog("Network connection needed.");
	}

	public void replayAZTData() {
		if(tTask!=null)
			tTask.replayAZTData();
		else
			AppConfig.getInstance().openErrorDialog("Network connection needed.");
	}

	public void setRecording(boolean bool) {
		iconCnt = 0;
		if (bool) {
			iconRecordTimeline = new Timeline(new KeyFrame(Duration.seconds(1),
					new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent t) {
							btn_record
									.setStyle("-fx-background-image: url(\"assets/img/rec_recording_"
											+ iconCnt
											+ ".png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
							iconCnt = (++iconCnt) % 3;
						}
					}));
			iconRecordTimeline.setCycleCount(Timeline.INDEFINITE);
			iconRecordTimeline.play();
		} else {
			if (iconRecordTimeline != null)
				iconRecordTimeline.stop();
			btn_record
					.setStyle("-fx-background-image: url(\"assets/img/rec.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		}
	}

	public void setReplaying(boolean bool) {
		iconCnt = 0;
		if (bool) {
			iconReplayTimeline = new Timeline(new KeyFrame(Duration.seconds(1),
					new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent t) {
							btn_record
									.setStyle("-fx-background-image: url(\"assets/img/rec_replaying_"
											+ iconCnt
											+ ".png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
							iconCnt = (++iconCnt) % 3;
						}
					}));
			iconReplayTimeline.setCycleCount(Timeline.INDEFINITE);
			iconReplayTimeline.play();
		} else {
			if (iconReplayTimeline != null)
				iconReplayTimeline.stop();
			btn_record
					.setStyle("-fx-background-image: url(\"assets/img/rec.png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		}
	}

	public void clearHistory() {
		cTopL4.clear();
		cBtmL4.clear();
	}

	public static void setTextField(VAL val, TextField textField) {
		String str = textField.getText();
		switch (val) {
		case RED:
			textField.setStyle("-fx-background-color:rgba(231, 68, 35, 1);"
					+ "-fx-text-fill: #fff;" + textFieldBg);
			break;

		case GREEN:
			textField.setStyle("-fx-background-color:rgba(36, 229, 81, 1);"
					+ "-fx-text-fill: #000;" + textFieldBg);
			break;

		case YELLOW:
			textField.setStyle("-fx-background-color:rgba(247, 251, 22, 1);"
					+ "-fx-text-fill: #000;" + textFieldBg);
			break;

		case DEFAULT:
			textField.clear();
			setNodeStyle(textField);
			break;

		default:
			break;
		}
		textField.setText(str);
		
	}

	public boolean isElTrackRefresh() {
		return trackElRefresh;
	}

	public boolean isAzTrackRefresh() {
		return trackAzRefresh;
	}

	public void setElTrackRefresh(boolean val) {
		trackElRefresh = val;
	}

	public void setElPlotRefresh(boolean val) {
		plotElRefresh = val;
	}

	public void setAzTrackRefresh(boolean val) {
		trackAzRefresh = val;
	}

	public void setAzPlotRefresh(boolean val) {
		plotAzRefresh = val;
	}

	public void sendRCBytes(byte[] bArr) {
		if (rcNet != null && rcNet.isOnline()) {
			try {
				rcNet.sendBytes(bArr);
				logger.info("Sent RC Msg");
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	public void sendBytes(byte[] bArr) {
		if (tTask != null && Constance.IS_CONNECTED) {
			try {
				tTask.sendBytes(bArr);
				logger.info("Sent Msg");
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	public byte[] receiveBytes() {
		if (tTask != null && Constance.IS_CONNECTED) {
			try {
				return tTask.receiveBytes();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return null;
	}

	private VAL getColor(byte val) {
		switch (val) {
		case 0:
			return VAL.RED;

		case 1:
			return VAL.YELLOW;

		case 2:
			return VAL.GREEN;

		default:
			return VAL.DEFAULT;
		}
	}

	public void updateElScanStatus(ElevationScanStartMessage pMsg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Constance.SCAN_EL = String.valueOf((pMsg.getScanNo()>0)?pMsg.getScanNo():"?");
				cTopL0.clear();
				drawTextTop(cTopL0);
			}
		});
	}

	public void updateAzScanStatus(AzimuthScanStartMessage pMsg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Constance.SCAN_AZ = String.valueOf((pMsg.getScanNo()>0)?pMsg.getScanNo():"?");
				cBtmL0.clear();
				drawTextBottom(cBtmL0);
			}
		});
	}

	public void updateWindStatus(WindSensorMsg pMsg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Constance.WIND_SPEED = pMsg.getWindSpeed() + " m/s";
				Constance.WIND_DIR = pMsg.getWindDirection() +" "+ Constance.UNITS.DEGREES;
				cTopL0.clear();
				drawTextTop(cTopL0);
			}
		});
	}

	public void updateGpsStatus(GpsMsg pMsg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Constance.GPS_TIME = pMsg.getTimestamp();
				Constance.LATITUDE = pMsg.getLatitude();
				Constance.LONGITUDE = pMsg.getLongitude();
				cTopL0.clear();
				drawTextTop(cTopL0);
			}
		});
	}

	public void updateStatus(HealthStatusMsg hMsg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				setTextField(getColor(hMsg.getSp()), txtField_Sp);
				setTextField(getColor(hMsg.getTcc()), txtField_exrx);
				setTextField(getColor(hMsg.getAc1()), txtField_Ac1);
				setTextField(getColor(hMsg.getAc2()), txtField_Ac2);
				setTextField(getColor(hMsg.getCh1()), txtField_Ch1);
				setTextField(getColor(hMsg.getCh2()), txtField_Ch2);
			}
		});
	}

	public void updatePwrStatus(PwrHealthStatusMsg hMsg) {
		
		Platform.runLater(new Runnable() {
       
			@Override
			public void run() {
				
				txtField_AzPwr.setText("Az Pwr: "+(hMsg.getAzPwr()>0?df.format(hMsg.getAzPwr()*0.001):"?")+" W");
				txtField_ElPwr.setText("El Pwr: "+(hMsg.getElPwr()>0?df.format(hMsg.getElPwr()*0.001):"?")+" W");
				setTextField(VAL.DEFAULT, txtField_AzPwr);
				setTextField(VAL.DEFAULT, txtField_ElPwr);
				
			}
		});
	}

	public void updatePcStatus(PCHealthStatusMsg hMsg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				setTextField(getColor(hMsg.getPc()), txtField_Pc);
			}
		});
	}

	 public void updateLanStatus(LANHealthStatusMsg hMsg) {

	        Platform.runLater(new Runnable() {



	                    @Override

	                    public void run() {

	                                if(hMsg!=null) {

	                                            if(!(hMsg.getLan()==2)) {

	                                                        if(Constance.LAN_SPEAK_COUNT < Constance.SPEAK_TIMES) {

	                                                                    AppConfig.getInstance().speakVoice("LAN system has failed");

	                                                                    ++Constance.LAN_SPEAK_COUNT;

	                                                        }

	                                            } else

	                                                        Constance.LAN_SPEAK_COUNT = 0;

	                                }

	                                setTextField(getColor(hMsg.getLan()), txtField_lan);

	                    }

	        });

	}

	   public void updateSdpStatus(SDPHealthStatusMsg hMsg) {

           Platform.runLater(new Runnable() {



                       @Override

                       public void run() {

                                   AppConfig.getInstance().setSdp(hMsg);

                                   SdpSetUpController sdpSetUpController = AppConfig.getInstance().getSdpSetUpController();

                                   if(hMsg!=null) {

                                               if((!(hMsg.getBoard1()==1) || !(hMsg.getBoard2()==1) || !(hMsg.getBoard3()==1))) {

                                                           if(Constance.SDP_SPEAK_COUNT < Constance.SPEAK_TIMES) {

                                                                       AppConfig.getInstance().speakVoice("SDP system board has failed");

                                                                       ++Constance.SDP_SPEAK_COUNT;

                                                           }

                                               } else

                                                           Constance.SDP_SPEAK_COUNT = 0;

                                   }

                                   if(sdpSetUpController!=null) {                                                         

                                               sdpSetUpController.updateData();//invalidate

                                   }

                       }

           });

}

	  public void updateExRxStatus(ExRxHealthStatusMsg hMsg) {

          Platform.runLater(new Runnable() {



                      @Override

                      public void run() {

                                  AppConfig.getInstance().setExrx(hMsg);

                                  ExRxSetUpController exRxSetUpController = AppConfig.getInstance().getExRxSetUpController();

                                  if(hMsg!=null) {

                                              if(!hMsg.isIs_ps_n5v() || !hMsg.isIs_ps_p5v() || !hMsg.isIs_ps_n12v() || !hMsg.isIs_ps_p12v()

                                                                      || !hMsg.isIs_rx_if_az() || !hMsg.isIs_rx_if_el() || !hMsg.isIs_rx_if_cal() || !hMsg.isIs_rx_rf_az() || !hMsg.isIs_rx_rf_el() || !hMsg.isIs_rx_rf_cal()

                                                                      || !hMsg.isIs_ex_az_lo2() || !hMsg.isIs_ex_az_adc() || !hMsg.isIs_ex_az_wfg() || !hMsg.isIs_ex_az_lo1() || !hMsg.isIs_ex_az_tx()

                                                                      || !hMsg.isIs_ex_el_lo2() || !hMsg.isIs_ex_el_adc() || !hMsg.isIs_ex_el_wfg() || !hMsg.isIs_ex_el_lo1() || !hMsg.isIs_ex_el_tx()) {

                                                          if(Constance.EXRX_SPEAK_COUNT < Constance.SPEAK_TIMES) {

                                                                      AppConfig.getInstance().speakVoice("EX RX system has failed");

                                                                      ++Constance.EXRX_SPEAK_COUNT;

                                                          }

                                              } else

                                                          Constance.EXRX_SPEAK_COUNT = 0;

                                  }

                                  if(exRxSetUpController!=null) {

                                              exRxSetUpController.updateData();//invalidate

                                  }

                      }

          });

}

	public void updateNoiseFigStatus(NoiseFigureStatusMsg hMsg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				AppConfig.getInstance().setNf(hMsg);
				ExRxSetUpController exRxSetUpController = AppConfig.getInstance().getExRxSetUpController();
				if(exRxSetUpController!=null) {
					exRxSetUpController.updateData();//invalidate
				}
			}
		});
	}

	public void updateVswrStatus(VSWRHealthStatusMsg hMsg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				AntennaSetUpController antSetUpController = AppConfig.getInstance().getAntSetUpController();
				if(antSetUpController!=null) {
					antSetUpController.setVswrAz(hMsg.getAz());
					antSetUpController.setVswrEl(hMsg.getEl());
					antSetUpController.updateVswr();//invalidate
				}
			}
		});
	}

	public void updateTempHumStatus(TempHumHealthStatusMsg hMsg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				AntennaSetUpController antSetUpController = AppConfig.getInstance().getAntSetUpController();
				if(antSetUpController!=null) {
					antSetUpController.setTempVal(hMsg.getTemp());
					antSetUpController.setHumidityVal(hMsg.getHumidity());
					antSetUpController.updateTempHum();//invalidate
				}
			}
		});
	}

	public void updateAntennaStatus(AntHealthStatusMsg hMsg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				AntennaSetUpController antSetUpController = AppConfig.getInstance().getAntSetUpController();
				if(antSetUpController!=null) {
					if(hMsg instanceof ElAntHealthStatusMsg)
						AppConfig.getInstance().setElAntHealthStatusMsg(hMsg);
					else
						AppConfig.getInstance().setAzAntHealthStatusMsg(hMsg);
					antSetUpController.updateData();//invalidate
				}
			}
		});
	}
	
	public void updateStatusVoice(HealthStatusMsg hMsg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
			if(hMsg!=null){
				if(!(hMsg.getAc1()==2)){
					if(Constance.ANT_SPEAK_COUNTAZ < Constance.SPEAK_TIMES) {
						  AppConfig.getInstance().speakVoice("azimuth antenna system has failed");
						  ++Constance.ANT_SPEAK_COUNTAZ;
					}else{
						Constance.ANT_SPEAK_COUNTAZ = 0;
					}	
				}
				if(!(hMsg.getAc2()==2)){
					if(Constance.ANT_SPEAK_COUNTEL < Constance.SPEAK_TIMES) {
						  AppConfig.getInstance().speakVoice("elevation antenna system has failed");
						  ++Constance.ANT_SPEAK_COUNTEL;
					}else{
						Constance.ANT_SPEAK_COUNTEL = 0;
					}
				}
			}
		}});
	}

	public void updateUserProfile() {
		//lbl_displayUserName.setText(AppConfig.getInstance().getLoggedUser()
				//.getFullName());
		//lbl_displayUserType.setText(AppConfig.getInstance().getLoggedUser()
				//.getType());
	}
	

	public Point getReleaseXY() {
		return releasedXY;
	}
	
	public void toggleMasterButton(boolean run) {
		if (run) {
			iconMasterTimeline = new Timeline(new KeyFrame(
					Duration.millis(Constance.MILLI_SECOND/2), new EventHandler<ActionEvent>() {

						int breakDownCount = -1;
						@Override
						public void handle(ActionEvent t) {
							btn_showMasterSetting
									.setStyle("-fx-background-image: url(\"assets/img/master_settings"
											+ masterIconName
											+ ".png\"); -fx-background-size: 30 30; -fx-background-repeat: no-repeat; -fx-background-position: center;");

							if (masterIconName.equals(""))
								masterIconName = "_off";
							else if (masterIconName.equals("_off"))
								masterIconName = "";

							if(++breakDownCount > Constance.MILLI_SECOND/20) {
								iconMasterTimeline.stop();
							}
						}
					}));
			iconMasterTimeline.setCycleCount(Timeline.INDEFINITE);
			iconMasterTimeline.play();
		} else {
			if (iconMasterTimeline != null)
				iconMasterTimeline.stop();
			masterNotify();
		}
	}

	public void changeLocalRemote(String name) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (name.equals("REMOTE")) {
					btn_localRemote.setText("REMOTE");
					btn_localRemote.widgetSetVal(VAL.GREEN);
				} else {
					btn_localRemote.setText("LOCAL");
					btn_localRemote.widgetSetVal(VAL.RED);
				}
			}
		});
		AppConfig.getInstance().openInformationDialog(
				"You are now in " + name + " mode");
	}

	public void sendConfigUpdateData() {
		if (rcNet != null && rcNet.isOnline()) {
			rcNet.sendConfigUpdateMsg();
			logger.info("Sent configuration to RC");
			AppConfig.getInstance().openInformationDialog("Sent configuration to RC");
		} else {
			logger.info("Unable to send update to RC");
			AppConfig.getInstance().openInformationDialog("Unable to send update to RC");
		}
	}

	public void changeLogging(boolean bool) {
		if (bool) {
			btn_showLoggingSetting
					.setStyle("-fx-background-image: url(\"assets/img/log_settings_on.png\"); -fx-background-size: 60 60; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		} else {
			btn_showLoggingSetting
					.setStyle("-fx-background-image: url(\"assets/img/log_settings_off.png\"); -fx-background-size: 60 60; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		}
	}

	
	public void updateZoneActivity(ZoneSuppression stateMsg) {
		int zoneNum=stateMsg.getZoneID();
		if(zoneNum==1){	
			
			Constance.Zone.ZoneID=Constance.Zone.ZoneID-1;
			Constance.Zone.zon1Status=stateMsg.getStatus();
		
		}else if(zoneNum==2){
							
			 Constance.Zone.ZoneID=Constance.Zone.ZoneID-1;
			 Constance.Zone.zon2Status=stateMsg.getStatus();
		}else if(zoneNum==3){
			
			Constance.Zone.ZoneID=Constance.Zone.ZoneID-1;
			Constance.Zone.zon3Status=stateMsg.getStatus();
		}else if(zoneNum==4){
						
		    Constance.Zone.ZoneID=Constance.Zone.ZoneID-1;
		    Constance.Zone.zon4Status=stateMsg.getStatus();
		}else if(zoneNum==5){
			
			Constance.Zone.ZoneID=Constance.Zone.ZoneID-1;
			Constance.Zone.zon5Status=stateMsg.getStatus();
		}
				
  }

	public void addZoneValues(ZoneUpdateMsg zUpdate){
		for(ZoneValuesUpdateMsg zValues : zUpdate.getZoneValues()){
			if(zValues.getZoneID()==1){
				Constance.Zone.z1Rmin=zValues.getRmin()*Constance.UNITS.getRecieveUnits();  
				Constance.Zone.z1Azmin=-((zValues.getAzmin()*Constance.UNITS.aZimuth)-10); 
				Constance.Zone.z1Rmax=zValues.getRmax()*Constance.UNITS.getRecieveUnits();   
				Constance.Zone.z1Azmax=-((zValues.getAzmax()*Constance.UNITS.aZimuth)-10);
				Constance.Zone.zon1Status=zValues.getStatus();
				if(zValues.getStatus()==1)
					Constance.Zone.ZoneID=Constance.Zone.ZoneID+1;
			}else if(zValues.getZoneID()==2){
				Constance.Zone.z2Rmin=zValues.getRmin()*Constance.UNITS.getRecieveUnits();  
				Constance.Zone.z2Azmin=-((zValues.getAzmin()*Constance.UNITS.aZimuth)-10); 
				Constance.Zone.z2Rmax=zValues.getRmax()*Constance.UNITS.getRecieveUnits();   
				Constance.Zone.z2Azmax=-((zValues.getAzmax()*Constance.UNITS.aZimuth)-10);
				Constance.Zone.zon2Status=zValues.getStatus();
				if(zValues.getStatus()==1)
					Constance.Zone.ZoneID=Constance.Zone.ZoneID+1;
			}else if(zValues.getZoneID()==3){
				Constance.Zone.z3Rmin=zValues.getRmin()*Constance.UNITS.getRecieveUnits();  
				Constance.Zone.z3Azmin=-((zValues.getAzmin()*Constance.UNITS.aZimuth)-10); 
				Constance.Zone.z3Rmax=zValues.getRmax()*Constance.UNITS.getRecieveUnits();   
				Constance.Zone.z3Azmax=-((zValues.getAzmax()*Constance.UNITS.aZimuth)-10);
				Constance.Zone.zon3Status=zValues.getStatus();
				if(zValues.getStatus()==1)
					Constance.Zone.ZoneID=Constance.Zone.ZoneID+1;
			}else if(zValues.getZoneID()==4){
				Constance.Zone.z4Rmin=zValues.getRmin()*Constance.UNITS.getRecieveUnits();  
				Constance.Zone.z4Azmin=-((zValues.getAzmin()*Constance.UNITS.aZimuth)-10); 
				Constance.Zone.z4Rmax=zValues.getRmax()*Constance.UNITS.getRecieveUnits();   
				Constance.Zone.z4Azmax=-((zValues.getAzmax()*Constance.UNITS.aZimuth)-10);
				Constance.Zone.zon4Status=zValues.getStatus();
				if(zValues.getStatus()==1)
					Constance.Zone.ZoneID=Constance.Zone.ZoneID+1;
			}else if(zValues.getZoneID()==5){
				Constance.Zone.z5Rmin=zValues.getRmin()*Constance.UNITS.getRecieveUnits();  
				Constance.Zone.z5Azmin=-((zValues.getAzmin()*Constance.UNITS.aZimuth)-10); 
				Constance.Zone.z5Rmax=zValues.getRmax()*Constance.UNITS.getRecieveUnits();   
				Constance.Zone.z5Azmax=-((zValues.getAzmax()*Constance.UNITS.aZimuth)-10);
				Constance.Zone.zon5Status=zValues.getStatus();
				logger.info("Zone5Status is:"+Constance.Zone.zon5Status);
				if(zValues.getStatus()==1)
					Constance.Zone.ZoneID=Constance.Zone.ZoneID+1;
			}
		}
	}

	public void updateZoneActivityEL(ZoneSuppression stateMsg) {
		int zoneNumEl=stateMsg.getZoneID();
        if(zoneNumEl==Constance.Zone.zon1IDEL){	
			
			Constance.Zone.ZoneIDEl=Constance.Zone.ZoneIDEl-1;
			Constance.Zone.zon1StatusEl=stateMsg.getStatus();
		
		}else if(zoneNumEl==Constance.Zone.zon2IDEL){
							
			Constance.Zone.ZoneIDEl=Constance.Zone.ZoneIDEl-1;
			 Constance.Zone.zon2StatusEl=stateMsg.getStatus();
		}else if(zoneNumEl==Constance.Zone.zon3IDEL){
			
			Constance.Zone.ZoneIDEl=Constance.Zone.ZoneIDEl-1;
			Constance.Zone.zon3StatusEl=stateMsg.getStatus();
		}else if(zoneNumEl==Constance.Zone.zon4IDEL){
						
			Constance.Zone.ZoneIDEl=Constance.Zone.ZoneIDEl-1;
		    Constance.Zone.zon4StatusEl=stateMsg.getStatus();
		}else if(zoneNumEl==Constance.Zone.zon5IDEL){
			
			Constance.Zone.ZoneIDEl=Constance.Zone.ZoneIDEl-1;
			Constance.Zone.zon5StatusEl=stateMsg.getStatus();
		}
		
	 }	
	public void addZoneValuesEL(ZoneUpdateMsg zUpdate) {
		for(ZoneValuesUpdateMsg zValues : zUpdate.getZoneValues()){
		int	indexCount2;
			if(zValues.getZoneID()==Constance.Zone.zon1IDEL){
				indexCount2=0;
				Constance.elZone1[indexCount2]=zValues.getRmin()*Constance.UNITS.getRecieveUnits();indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getRmax()*Constance.UNITS.getRecieveUnits();indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getAzmin()*Constance.UNITS.aZimuth;indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getAzmax()*Constance.UNITS.aZimuth;
				Constance.Zone.zon1StatusEl=zValues.getStatus();
				if(zValues.getStatus()==1)
					Constance.Zone.ZoneIDEl=Constance.Zone.ZoneIDEl+1;
			}else if(zValues.getZoneID()==Constance.Zone.zon2IDEL){
				indexCount2=4;
				Constance.elZone1[indexCount2]=zValues.getRmin()*Constance.UNITS.getRecieveUnits();indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getRmax()*Constance.UNITS.getRecieveUnits();indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getAzmin()*Constance.UNITS.aZimuth;indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getAzmax()*Constance.UNITS.aZimuth;
				Constance.Zone.zon2StatusEl=zValues.getStatus();
				if(zValues.getStatus()==1)
					Constance.Zone.ZoneIDEl=Constance.Zone.ZoneIDEl+1;
			}else if(zValues.getZoneID()==Constance.Zone.zon3IDEL){
				indexCount2=8;
				Constance.elZone1[indexCount2]=zValues.getRmin()*Constance.UNITS.getRecieveUnits();indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getRmax()*Constance.UNITS.getRecieveUnits();indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getAzmin()*Constance.UNITS.aZimuth;indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getAzmax()*Constance.UNITS.aZimuth;
				Constance.Zone.zon3StatusEl=zValues.getStatus();
				if(zValues.getStatus()==1)
					Constance.Zone.ZoneIDEl=Constance.Zone.ZoneIDEl+1;
			}else if(zValues.getZoneID()==Constance.Zone.zon4IDEL){
				indexCount2=12;
				Constance.elZone1[indexCount2]=zValues.getRmin()*Constance.UNITS.getRecieveUnits();indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getRmax()*Constance.UNITS.getRecieveUnits();indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getAzmin()*Constance.UNITS.aZimuth;indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getAzmax()*Constance.UNITS.aZimuth;
				Constance.Zone.zon4StatusEl=zValues.getStatus();
				if(zValues.getStatus()==1)
					Constance.Zone.ZoneIDEl=Constance.Zone.ZoneIDEl+1;
			}else if(zValues.getZoneID()==Constance.Zone.zon5IDEL){
				indexCount2=16;
				Constance.elZone1[indexCount2]=zValues.getRmin()*Constance.UNITS.getRecieveUnits();indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getRmax()*Constance.UNITS.getRecieveUnits();indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getAzmin()*Constance.UNITS.aZimuth;indexCount2++;
				Constance.elZone1[indexCount2]=zValues.getAzmax()*Constance.UNITS.aZimuth;
				Constance.Zone.zon5StatusEl=zValues.getStatus();
				if(zValues.getStatus()==1)
					Constance.Zone.ZoneIDEl=Constance.Zone.ZoneIDEl+1;
			}
	     }
     }	
  }