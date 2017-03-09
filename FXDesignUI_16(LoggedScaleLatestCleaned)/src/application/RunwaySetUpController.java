package application;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import messages.app.CornerReflectorMsg;
import model.AppConfig;
import model.ILayoutParam;
import model.data.ConfigParam;

import org.apache.log4j.Logger;

import utils.Constance;
import admin.UserPreference;

public class RunwaySetUpController implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(RunwaySetUpController.class);
	
	@FXML AnchorPane RunwaySetup;
	
	@FXML ComboBox<String> comboRunway;
	@FXML ComboBox<String> comboLR;
	
	AppConfig appConfig = AppConfig.getInstance();
	
	double initialX,initialY;
	
	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Runway Setup Dialog Opened");
		addDraggableNode(RunwaySetup);
		initDialog();
		loadDefault();
	}	

	@FXML
	protected void okClick(ActionEvent event) {
		 
        // if(AppConfig.getInstance().isDisplayHasPrevilege()){
		//AppConfig.getInstance().openErrorDialog("Runway change is in progress");
		saveData();
		
		if(DiamondOptionController.configList!=null)
			updateRcConfig();
		    updateCrConfig();
		    sendUpdate();
		   
		closeSettings(event);
		AppConfig.getInstance().getFxmlController().flipGraphOnRunwaySel(Constance.PREF.SEL_RUNWAY);
		AppConfig.getInstance().getFxmlController().initUIComponents(Constance.PREF.LEFT_RIGHT);
		AppConfig.getInstance().getFxmlController().notifyChanges();
		
		 AppConfig.getInstance().openErrorDialog("Runway change done successfully");
		//}
	}

	

	@FXML
	protected void cancelClick(ActionEvent event) {
		loadDefault();
		saveData();
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
		comboRunway.getItems().clear();
		comboRunway.getItems().addAll(" 09 "," 09  "," 27 "," 27  ");
		
		comboLR.getItems().clear();
		comboLR.getItems().addAll("Left","Right");
//		comboLR.setDisable(true);
		
		comboRunway.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if(newValue.contains(" 09 ") || newValue.contains(" 09  "))
					comboLR.setValue("Left");
				else if(newValue.contains(" 27 ") || newValue.contains(" 27  "))
					comboLR.setValue("Right");						
			}
		});
		
	}

	private void loadDefault() {
		int runwayIndex = Integer.parseInt(UserPreference.getInstance().getOP_RUNWAY());
		comboRunway.getSelectionModel().select(runwayIndex);
		comboLR.setValue(Constance.PREF.LEFT_RIGHT);
	}

	private void saveData() {
		
		UserPreference.getInstance().setOP_RUNWAY(String.valueOf(comboRunway.getSelectionModel().getSelectedIndex()));
		Constance.PREF.SEL_RUNWAY = adjustRunayVal(comboRunway.getValue());  //added in-order to change the label from 1 to 09 and 3 to 27.
		Constance.PREF.LEFT_RIGHT = comboLR.getValue();
	}

	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("Runway Setup Dialog Closed");
	}	
	
	protected void updateRcConfig() {
		if(DiamondOptionController.configList.size() > 3) {
			ConfigParam cParam = null;
			switch (Constance.PREF.SEL_RUNWAY) {
			case " 1 ":
				cParam = DiamondOptionController.configList.get(0);
				break;
				
			case " 2 ":
				cParam = DiamondOptionController.configList.get(1);		
				break;
						
			case " 3 ":
				cParam = DiamondOptionController.configList.get(2);
				break;
				
			case " 4 ":
				cParam = DiamondOptionController.configList.get(3);
				break;
	
			default:
				break;
			}
			
			//load internal
			try {
				if(cParam!=null) {
					Constance.AZIMUTH.AXIS = Double.parseDouble(cParam.getAzAxis()); 
					Constance.RMIN = Double.parseDouble(cParam.getRmin());
					Constance.AZIMUTH.TILT = Double.parseDouble(cParam.getAzTilt());
					Constance.ELEVATION.TILT = Double.parseDouble(cParam.getElTilt());		
					Constance.AZIMUTH.RCLO = Double.parseDouble(cParam.getYOffset())*0.001;
					Constance.TOUCH_DOWN = Double.parseDouble(cParam.getXOffset())*0.001;
					
					/*if(AppConfig.getInstance().isDisplayHasPrevilege())
					   AppConfig.getInstance().getFxmlController().sendConfigUpdateData();
					 logger.info("-----------------Sent those tilt values to RC--------------------");*/
				}
			} catch (Exception e) {
				logger.error(e);
				AppConfig.getInstance().openErrorDialog("Load Configuration parameters. (Not all Runway parameters are loaded)");
			}	
		} else {
			AppConfig.getInstance().openErrorDialog("Load Configuration parameters. (Not all Runway parameters are loaded)");
		}
	}
	private void updateCrConfig(){
		if(DiamondOptionController.configList.size() > 3) {
			ConfigParam cParam = null;
			switch (Constance.PREF.SEL_RUNWAY) {
			case " 1 ":
				cParam = DiamondOptionController.configList.get(0);
				break;
				
			case " 2 ":
				cParam = DiamondOptionController.configList.get(1);		
				break;
						
			case " 3 ":
				cParam = DiamondOptionController.configList.get(2);
				break;
				
			case " 4 ":
				cParam = DiamondOptionController.configList.get(3);
				break;
	
			default:
				break;
			}
			
			
			try {
				
				if(cParam!=null) {

					Constance.Config.CR1Range=Double.parseDouble((cParam.getCr1().getRange()));
					Constance.Config.CR1MinRange=Double.parseDouble((cParam.getCr1().getRange1()));
					Constance.Config.CR1Angle=Double.parseDouble((cParam.getCr1().getAngle()));
					Constance.Config.CR1MinAngle=Double.parseDouble((cParam.getCr1().getAngle1()));
					
					Constance.Config.CR2MinRange=Double.parseDouble((cParam.getCr2().getRange1()));
					System.out.println("cr2 Range:"+Constance.Config.CR2MinRange);
					Constance.Config.CR2Range=Double.parseDouble((cParam.getCr2().getRange()));
					System.out.println("cr2 Range:"+Constance.Config.CR2Range);
					Constance.Config.CR2MinAngle=Double.parseDouble((cParam.getCr2().getAngle1()));
					System.out.println("cr2 Range:"+Constance.Config.CR2MinAngle);
					Constance.Config.CR2Angle=Double.parseDouble((cParam.getCr2().getAngle()));
					System.out.println("cr2 Range:"+Constance.Config.CR2Angle);
					
					
					Constance.Config.CR3Range=Double.parseDouble((cParam.getCr3().getRange()));
					Constance.Config.CR3MinRange=Double.parseDouble((cParam.getCr3().getRange1()));
					Constance.Config.CR3Angle=Double.parseDouble((cParam.getCr3().getAngle()));
					Constance.Config.CR3MinAngle=Double.parseDouble((cParam.getCr3().getAngle1()));
					
					Constance.Config.CR4Range=Double.parseDouble((cParam.getCr4().getRange()));
					Constance.Config.CR4MinRange=Double.parseDouble((cParam.getCr4().getRange1()));
					Constance.Config.CR4Angle=Double.parseDouble((cParam.getCr4().getAngle()));
					Constance.Config.CR4MinAngle=Double.parseDouble((cParam.getCr4().getAngle1()));
					
					Constance.Config.CR5Range=Double.parseDouble((cParam.getCr5().getRange()));
					Constance.Config.CR5MinRange=Double.parseDouble((cParam.getCr5().getRange1()));
					Constance.Config.CR5Angle=Double.parseDouble((cParam.getCr5().getAngle()));
					Constance.Config.CR5MinAngle=Double.parseDouble((cParam.getCr5().getAngle1()));
					
					Constance.Config.CR6Range=Double.parseDouble((cParam.getCr6().getRange()));
					Constance.Config.CR6MinRange=Double.parseDouble((cParam.getCr6().getRange1()));
					Constance.Config.CR6Angle=Double.parseDouble((cParam.getCr6().getAngle()));
					Constance.Config.CR6MinAngle=Double.parseDouble((cParam.getCr6().getAngle1()));
					
					}
								
			} catch (Exception e) {
				logger.error(e);
				
			}	
		} else {
			AppConfig.getInstance().openErrorDialog("Load Configuration parameters. (Not all Runway parameters are loaded)");
		}
		
		
	}
	
	private String adjustRunayVal(String runNum){
		String run="1";
		if(runNum==" 09 "){
			run=" 1 ";
		}
		else if(runNum==" 09  "){
			run=" 2 ";
		}
		else if(runNum==" 27 "){
			run=" 3 ";
		}
		else if(runNum==" 27  "){
			run=" 4 ";
		}
		
		return run;
	}
	/** 
	 * Method sendUpdate ...
	 */
	private void sendUpdate() {
		CornerReflectorMsg crMsg=new CornerReflectorMsg();
		if(Constance.UNITS.isKM==true){
			crMsg.setCr1ID((int)(Constance.Config.Cr1ID));
			crMsg.setCr1range((int)(Constance.Config.CR1Range*Constance.Config.CRinMeters));
			crMsg.setCr1Minrange((int)(Constance.Config.CR1MinRange*Constance.Config.CRinMeters));
			crMsg.setCr1angle((int)((Constance.Config.CR1Angle+10)*Constance.Config.CRinMeters));
			crMsg.setCr1Minangle((int)((Constance.Config.CR1MinAngle+10)*Constance.Config.CRinMeters));
			
			crMsg.setCr2ID((int)(Constance.Config.Cr2ID));
			crMsg.setCr2range((int)(Constance.Config.CR2Range*Constance.Config.CRinMeters));
			crMsg.setCr2Minrange((int)(Constance.Config.CR2MinRange*Constance.Config.CRinMeters));
			crMsg.setCr2angle((int)((Constance.Config.CR2Angle+10)*Constance.Config.CRinMeters));
			crMsg.setCr2Minangle((int)((Constance.Config.CR2MinAngle+10)*Constance.Config.CRinMeters));
			
			crMsg.setCr3ID((int)(Constance.Config.Cr3ID));
			crMsg.setCr3range((int)(Constance.Config.CR3Range*Constance.Config.CRinMeters));
			crMsg.setCr3Minrange((int)(Constance.Config.CR3MinRange*Constance.Config.CRinMeters));
			crMsg.setCr3angle((int)((Constance.Config.CR3Angle+10)*Constance.Config.CRinMeters));
			crMsg.setCr3Minangle((int)((Constance.Config.CR3MinAngle+10)*Constance.Config.CRinMeters));
			
			crMsg.setCr4ID((int)(Constance.Config.Cr4ID));
			crMsg.setCr4range((int)(Constance.Config.CR4Range*Constance.Config.CRinMeters));
			crMsg.setCr4Minrange((int)(Constance.Config.CR4MinRange*Constance.Config.CRinMeters));
			crMsg.setCr4angle((int)((Constance.Config.CR4Angle+10)*Constance.Config.CRinMeters));
			crMsg.setCr4Minangle((int)((Constance.Config.CR4MinAngle+10)*Constance.Config.CRinMeters));
			
			crMsg.setCr5ID((int)(Constance.Config.Cr5ID));
			crMsg.setCr5range((int)(Constance.Config.CR5Range*Constance.Config.CRinMeters));
			crMsg.setCr5Minrange((int)(Constance.Config.CR5MinRange*Constance.Config.CRinMeters));
			crMsg.setCr5angle((int)((Constance.Config.CR5Angle+10)*Constance.Config.CRinMeters));
			crMsg.setCr5Minangle((int)((Constance.Config.CR5MinAngle+10)*Constance.Config.CRinMeters));
			
			crMsg.setCr6ID((int)(Constance.Config.Cr6ID));
			crMsg.setCr6range((int)(Constance.Config.CR6Range*Constance.Config.CRinMeters));
			crMsg.setCr6Minrange((int)(Constance.Config.CR6MinRange*Constance.Config.CRinMeters));
			crMsg.setCr6angle((int)((Constance.Config.CR6Angle+10)*Constance.Config.CRinMeters));
			crMsg.setCr6Minangle((int)((Constance.Config.CR6MinAngle+10)*Constance.Config.CRinMeters));
			if(AppConfig.getInstance().isDisplayHasPrevilege())
	    	 appConfig.getFxmlController().sendBytes(crMsg.encode().array());
			
		}
		else{
			crMsg.setCr1ID((int)(Constance.Config.Cr1ID));
			crMsg.setCr1range((int)(Constance.Config.CR1Range/Constance.Config.CRinNM));
			crMsg.setCr1Minrange((int)(Constance.Config.CR1MinRange/Constance.Config.CRinNM));
			crMsg.setCr1angle((int)((Constance.Config.CR1Angle+10)/Constance.Config.CRinNM));
			crMsg.setCr1Minangle((int)((Constance.Config.CR1MinAngle+10)/Constance.Config.CRinNM));
			
			crMsg.setCr2ID((int)(Constance.Config.Cr2ID));
			crMsg.setCr2range((int)(Constance.Config.CR2Range/Constance.Config.CRinNM));
			crMsg.setCr2Minrange((int)(Constance.Config.CR2MinRange/Constance.Config.CRinNM));
			crMsg.setCr2angle((int)((Constance.Config.CR2Angle+10)/Constance.Config.CRinNM));
			crMsg.setCr2Minangle((int)((Constance.Config.CR2MinAngle+10)/Constance.Config.CRinNM));
			
			crMsg.setCr3ID((int)(Constance.Config.Cr3ID));
			crMsg.setCr3range((int)(Constance.Config.CR3Range/Constance.Config.CRinNM));
			crMsg.setCr3Minrange((int)(Constance.Config.CR3MinRange/Constance.Config.CRinNM));
			crMsg.setCr3angle((int)((Constance.Config.CR3Angle+10)/Constance.Config.CRinNM));
			crMsg.setCr3Minangle((int)((Constance.Config.CR3MinAngle+10)/Constance.Config.CRinNM));
			
			crMsg.setCr4ID((int)(Constance.Config.Cr4ID));
			crMsg.setCr4range((int)(Constance.Config.CR4Range/Constance.Config.CRinNM));
			crMsg.setCr4Minrange((int)(Constance.Config.CR4MinRange/Constance.Config.CRinNM));
			crMsg.setCr4angle((int)((Constance.Config.CR4Angle+10)/Constance.Config.CRinNM));
			crMsg.setCr4Minangle((int)((Constance.Config.CR4MinAngle+10)/Constance.Config.CRinNM));
			
			crMsg.setCr5ID((int)(Constance.Config.Cr5ID));
			crMsg.setCr5range((int)(Constance.Config.CR5Range/Constance.Config.CRinNM));
			crMsg.setCr5Minrange((int)(Constance.Config.CR5MinRange/Constance.Config.CRinNM));
			crMsg.setCr5angle((int)((Constance.Config.CR5Angle+10)/Constance.Config.CRinNM));
			crMsg.setCr5Minangle((int)((Constance.Config.CR5MinAngle+10)/Constance.Config.CRinNM));
			
			crMsg.setCr6ID((int)(Constance.Config.Cr6ID));
			crMsg.setCr6range((int)(Constance.Config.CR6Range/Constance.Config.CRinNM));
			crMsg.setCr6Minrange((int)(Constance.Config.CR6MinRange/Constance.Config.CRinNM));
			crMsg.setCr6angle((int)((Constance.Config.CR6Angle+10)/Constance.Config.CRinNM));
			crMsg.setCr6Minangle((int)((Constance.Config.CR6MinAngle+10)/Constance.Config.CRinNM));			
			if(AppConfig.getInstance().isDisplayHasPrevilege())
			  appConfig.getFxmlController().sendBytes(crMsg.encode().array());
		}
	}
	
}