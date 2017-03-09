package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import messages.radar.AntHealthStatusMsg;
import messages.radar.AntHealthStatusMsg.Dtrm;
import model.AppConfig;
import model.ILayoutParam;

import org.apache.log4j.Logger;

import status.three.Status3Widget;
import utils.Constance;

public class AntennaSetUpController implements Initializable, ILayoutParam {

	private static final Logger logger = Logger
			.getLogger(AntennaSetUpController.class);

	@FXML
	AnchorPane AntennaSetup;

	@FXML
	Label title;

	@FXML
	TextField rf_if_ch1;
	@FXML
	TextField rf_if_ch2;
	@FXML
	TextField rf_if_duty;

	@FXML
	TextField humidity;
	@FXML
	TextField temp;
	@FXML
	TextField vswr;

	@FXML
	MaterialDesignButtonWidget plank1;
	@FXML
	MaterialDesignButtonWidget plank2;
	@FXML
	MaterialDesignButtonWidget plank3;
	@FXML
	MaterialDesignButtonWidget plank4;
	@FXML
	MaterialDesignButtonWidget plank5;
	@FXML
	MaterialDesignButtonWidget plank6;
	@FXML
	MaterialDesignButtonWidget plank7;
	@FXML
	MaterialDesignButtonWidget plank8;

	@FXML
	Status3Widget dtrm1;
	@FXML
	Status3Widget dtrm2;
	@FXML
	Status3Widget dtrm3;
	@FXML
	Status3Widget dtrm4;
	@FXML
	Status3Widget dtrm5;
	@FXML
	Status3Widget dtrm6;
	@FXML
	Status3Widget dtrm7;
	@FXML
	Status3Widget dtrm8;
	@FXML
	Status3Widget dtrm9;
	@FXML
	Status3Widget dtrm10;
	@FXML
	Status3Widget dtrm11;
	@FXML
	Status3Widget dtrm12;
	@FXML
	Status3Widget dtrm13;
	@FXML
	Status3Widget dtrm14;
	@FXML
	Status3Widget dtrm15;
	@FXML
	Status3Widget dtrm16;

	double initialX, initialY;
	List<Status3Widget> dtrmList;
	String type;
	int vswrAz;
	int vswrEl;
	int tempVal;
	int humidityVal;
	int plankNo = -1;

	VAL val = VAL.DEFAULT;

	public void setType(String str) {
		this.type = str;
		switch (type) {
		case "AZ":
			title.setText("Azimuth Antenna Subsytem status");
			break;

		case "EL":
			title.setText("Elevation Antenna Subsytem status");
			break;

		default:
			break;
		}
	}

	public void setVswrAz(int vswrAz) {
		this.vswrAz = vswrAz;
	}

	public void setVswrEl(int vswrEl) {
		this.vswrEl = vswrEl;
	}

	public void setTempVal(int tempVal) {
		this.tempVal = tempVal;
	}

	public void setHumidityVal(int humidityVal) {
		this.humidityVal = humidityVal;
	}

	public void updateVswr() {
		if(type!=null) {
			int vswrVal = 0;
			switch (type) {
			case "AZ":
				vswrVal = vswrAz;
				break;

			case "EL":
				vswrVal = vswrEl;
				break;

			default:
				break;
			}

			String vswrName = "";
			switch (vswrVal) {
			case 1:
				vswrName = "1:12";
				break;

			case 2:
				vswrName = "1:5";
				break;

			case 3:
				vswrName = "1:3.5";
				break;

			case 4:
				vswrName = "1:2.3";
				break;

			case 5:
				vswrName = "1:1.76";
				break;

			case 6:
				vswrName = "1:1.46";
				break;

			default:
				vswrName = "unknown";
				break;
			}
			vswr.setText(vswrName);
		}
	}

	public void updateTempHum() {
		//text updates
		temp.setText("");
		humidity.setText("");
		temp.setText(tempVal+Constance.UNITS.DEGREES);
		humidity.setText(humidityVal+"%");
		 System.out.println("---------Entered into Temp Humdity inside dialog and updated---------------");
	}

	public void updateData() {
		if(type!=null) {
			AntHealthStatusMsg antHealthStatusMsg = null;
			switch (type) {
			case "AZ":
				antHealthStatusMsg = AppConfig.getInstance().getAzAntHealthStatusMsg();
				break;

			case "EL":
				antHealthStatusMsg = AppConfig.getInstance().getElAntHealthStatusMsg();
				break;

			default:
				break;
			}

			// invalidate
			if (antHealthStatusMsg != null && plankNo != -1) {

				FXMLController.setTextField(getColor(antHealthStatusMsg.isCh1()),rf_if_ch1);
				FXMLController.setTextField(getColor(antHealthStatusMsg.isCh2()),rf_if_ch2);
				FXMLController.setTextField(getColor(antHealthStatusMsg.isDuty()),rf_if_duty);

				List<Dtrm> showDtrm = getChosenDtrm(plankNo, antHealthStatusMsg);
				if (showDtrm != null) {
					for (int i = 0; i < showDtrm.size(); i++) {
						Status3Widget sWidget = dtrmList.get(i);
						Dtrm dtrm = showDtrm.get(i);

						sWidget.widgetSetVal_1(getColor(dtrm.isCh2()));
						sWidget.widgetSetVal_2(getColor(dtrm.isCh1()));
						sWidget.widgetSetVal_3(getColor(dtrm.isTemp()));
						sWidget.widgetSetVal_4(getColor(dtrm.isP28v()));
						sWidget.widgetSetVal_5(getColor(dtrm.isN5v()));
						sWidget.widgetSetVal_6(getColor(dtrm.isP8v()));
						sWidget.widgetSetVal_7(getColor(dtrm.isP5v()));
					}
				}
			}
		}
	}

	private List<Dtrm> getChosenDtrm(int plankNo2,
			AntHealthStatusMsg antHealthStatusMsg) {
		switch (plankNo2) {
		case 1:
			return antHealthStatusMsg.getPlank0_dtrm();

		case 2:
			return antHealthStatusMsg.getPlank1_dtrm();

		case 3:
			return antHealthStatusMsg.getPlank2_dtrm();

		case 4:
			return antHealthStatusMsg.getPlank3_dtrm();

		case 5:
			return antHealthStatusMsg.getPlank4_dtrm();

		case 6:
			return antHealthStatusMsg.getPlank5_dtrm();

		case 7:
			return antHealthStatusMsg.getPlank6_dtrm();

		case 8:
			return antHealthStatusMsg.getPlank7_dtrm();

		default:
			break;
		}
		return null;
	}

	@Override
	public void draw(GraphicsContext gc) {

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Antenna Setup Dialog Opened");
		addDraggableNode(AntennaSetup);
		initDialog();
		updateData();
		updateVswr();
		updateTempHum();
	}

	@FXML
	protected void cancelClick(ActionEvent event) {
		closeSettings(event);
	}

	@FXML
	protected void plank1Click(ActionEvent event) {
		plankNo = 1;
		initPlanks(val);
		plank1.widgetSetVal(VAL.GREEN);
		updateData();
	}

	@FXML
	protected void plank2Click(ActionEvent event) {
		plankNo = 2;
		initPlanks(val);
		plank2.widgetSetVal(VAL.GREEN);
		updateData();
	}

	@FXML
	protected void plank3Click(ActionEvent event) {
		plankNo = 3;
		initPlanks(val);
		plank3.widgetSetVal(VAL.GREEN);
		updateData();
	}

	@FXML
	protected void plank4Click(ActionEvent event) {
		plankNo = 4;
		initPlanks(val);
		plank4.widgetSetVal(VAL.GREEN);
		updateData();
	}

	@FXML
	protected void plank5Click(ActionEvent event) {
		plankNo = 5;
		initPlanks(val);
		plank5.widgetSetVal(VAL.GREEN);
		updateData();
	}

	@FXML
	protected void plank6Click(ActionEvent event) {
		plankNo = 6;
		initPlanks(val);
		plank6.widgetSetVal(VAL.GREEN);
		updateData();
	}

	@FXML
	protected void plank7Click(ActionEvent event) {
		plankNo = 7;
		initPlanks(val);
		plank7.widgetSetVal(VAL.GREEN);
		updateData();
	}

	@FXML
	protected void plank8Click(ActionEvent event) {
		plankNo = 8;
		initPlanks(val);
		plank8.widgetSetVal(VAL.GREEN);
		updateData();
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
		FXMLController.setTextField(val, rf_if_ch1);
		FXMLController.setTextField(val, rf_if_ch2);
		FXMLController.setTextField(val, rf_if_duty);

		FXMLController.setTextField(val, humidity);
		FXMLController.setTextField(val, temp);
		FXMLController.setTextField(val, vswr);

		initPlanks(val);

		dtrmList = new ArrayList<Status3Widget>();
		dtrmList.add(dtrm1);
		dtrmList.add(dtrm2);
		dtrmList.add(dtrm3);
		dtrmList.add(dtrm4);
		dtrmList.add(dtrm5);
		dtrmList.add(dtrm6);
		dtrmList.add(dtrm7);
		dtrmList.add(dtrm8);
		dtrmList.add(dtrm9);
		dtrmList.add(dtrm10);
		dtrmList.add(dtrm11);
		dtrmList.add(dtrm12);
		dtrmList.add(dtrm13);
		dtrmList.add(dtrm14);
		dtrmList.add(dtrm15);
		dtrmList.add(dtrm16);

		for (int i = 0; i < dtrmList.size(); i++) {
			Status3Widget sWidget = dtrmList.get(i);
			sWidget.widgetSetName("" + (i+1));
			sWidget.widgetSetVal_All(val);
		}
	}

	private void initPlanks(VAL val) {
		plank1.widgetSetVal(val);
		plank2.widgetSetVal(val);
		plank3.widgetSetVal(val);
		plank4.widgetSetVal(val);
		plank5.widgetSetVal(val);
		plank6.widgetSetVal(val);
		plank7.widgetSetVal(val);
		plank8.widgetSetVal(val);
	}

	private void closeSettings(ActionEvent event) {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
		logger.info("Antenna Setup Dialog Closed");
	}

	private VAL getColor(boolean boo) {
		return (boo ? VAL.GREEN : VAL.RED);
	}

}