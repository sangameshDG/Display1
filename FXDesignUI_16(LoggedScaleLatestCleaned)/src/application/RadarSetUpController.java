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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import messages.app.OperationModeMsg;
import model.AppConfig;
import model.ILayoutParam;
import utils.Constance;

public class RadarSetUpController implements Initializable,ILayoutParam{

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
	
	@FXML TextField QNH;
	@FXML TextField QFE;

	private boolean tx = false;

	AppConfig appConfig = AppConfig.getInstance();

	double initialX,initialY;
	private OperationModeMsg opModeMsg;
	
	

    public RadarSetUpController( MaterialDesignButtonWidget txOn, MaterialDesignButtonWidget txOff, TextField QNH, TextField QFE) {
		this.txOn = txOn;
		this.txOff = txOff;
		this.QNH = QNH;
		this.QFE = QFE;
		
	}
    
	@Override
	public void draw(GraphicsContext gc) {

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Radar Setup Dialog Opened");
		addDraggableNode(RadarSetup);
		initRadarSetUp();
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

	@FXML
	protected void txOn(ActionEvent event) {
		tx = true;
		txOn.widgetSetVal(VAL.GREEN);
		txOff.widgetSetVal(VAL.RED);
	}
	
	@FXML
	protected void txOff(ActionEvent event) {
		tx = false;
		txOn.widgetSetVal(VAL.RED);
		txOff.widgetSetVal(VAL.GREEN);
	}


	public void initRadarSetUp() {
		initObj();
		loadUserPrefValues();
	}

	public void sendCommand() {
		
			if(appConfig.isDisplayHasPrevilege()) {
				makeMsg();
				saveUserPrefData();
				sendUpdate();
				appConfig.openInformationDialog("Command Sent");
			
		}
	}

	public OperationModeMsg getOpMsg() {
		initObj();
		makeMsg();
		return opModeMsg;
	}

	public void setUpdatedOpModeMsg(OperationModeMsg opMsg) {
		//save preferences
		UserPreference uPreference = UserPreference.getInstance();
		uPreference.setOP_MODE(String.valueOf(opMsg.getOpMode()));
		uPreference.setOP_FREQ(String.valueOf(opMsg.getFreqCtrl()));
		uPreference.setOP_POLARIZATION(String.valueOf(opMsg.getPolarization()));

		if(opMsg.getTxOnOff()==1)
			uPreference.setOP_TX("ON");
		else
			uPreference.setOP_TX("OFF");

		//update UI
		loadUserPrefValues();
	}

	private void loadUserPrefValues() {
		UserPreference uPreference = UserPreference.getInstance();
		
		if(uPreference.getOP_TX().contains("ON")) {
			txOn.widgetSetVal(VAL.GREEN);
			txOff.widgetSetVal(VAL.RED);	
			tx = true;
		} else{
			txOff.widgetSetVal(VAL.GREEN);	
		    txOn.widgetSetVal(VAL.RED);
		 }
		
		QNH.setText(String.valueOf(uPreference.getQnh()));
		QFE.setText(String.valueOf(uPreference.getQfe()));
	  }
	
	private void saveUserPrefData() {
		UserPreference uPreference = UserPreference.getInstance();
		
		if(tx)
			uPreference.setOP_TX("ON");
		else
			uPreference.setOP_TX("OFF");
	}
	
	public void saveUserPrefDataQNH_QFE() {
		UserPreference uPreference = UserPreference.getInstance();
		
		uPreference.setQNH(Double.valueOf(QNH.getText()));
 		uPreference.setQFE(Double.valueOf(QFE.getText()));
			
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

	private void initObj() {
		opModeMsg = new OperationModeMsg();
	}

	private void makeMsg() {

		//Operation Mode Msg
		opModeMsg.setOpMode(Constance.OPPMODEPARAM.OP_MODE);
		opModeMsg.setOpModeType((byte)0);
		opModeMsg.setFreqCtrl(Constance.OPPMODEPARAM.OP_FREQ);

		opModeMsg.setScanOnOff((byte) (tx? 3:0));
		opModeMsg.setPolarization(Constance.OPPMODEPARAM.OP_POL);
		opModeMsg.setTxOnOff((byte) (tx? 1:0));
		opModeMsg.setBiteOnOff((byte) (tx? 0:1));

		opModeMsg.setrMin((byte)0);
		opModeMsg.setNorthOffset((byte)0);
		
	}

	private void sendUpdate() {
		//send BITE Msg
		appConfig.getFxmlController().sendRCBytes(opModeMsg.encode().array());
	}

}