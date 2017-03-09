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
//*H  Created: 03-Jan-2017
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
import org.apache.log4j.Logger;
import admin.UserPreference;
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
import materialdesignbutton.MaterialDesignButtonWidget;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import model.ILayoutParam;
import utils.Constance;

/**
 * Defines the class AdvanceSettingsControllerForSPparameters.
 * @author SangappaG
 * @version $Revision: $.
 * @since 03-Jan-2017
 */
public class AdvanceSettingsControllerForSPparameters implements Initializable,ILayoutParam {

private static final Logger logger = Logger.getLogger(AdvanceSettingsControllerForSPparameters.class);
	
	@FXML AnchorPane AdvSettingSetup;
	
	
	@FXML MaterialDesignButtonWidget amtiOn;
	@FXML MaterialDesignButtonWidget amtiOff;
	@FXML MaterialDesignButtonWidget clutterMapOn;
	@FXML MaterialDesignButtonWidget clutterMapOff;
	@FXML MaterialDesignButtonWidget cfarTypeCA;
	@FXML MaterialDesignButtonWidget cfarTypeGO;
	@FXML MaterialDesignButtonWidget cfarTypeLO;

	@FXML ComboBox<String> mtiType;
	@FXML ComboBox<String> cfarThreshold;
		
	double initialX,initialY;

	
	private boolean amti = false;
	private boolean clutterMap = false;
	private String cfar;
	
	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Advanced Settings Dialog Opened");
		addDraggableNode(AdvSettingSetup);
		initDialog();
		loadUserPrefValues();
		
	}

	@FXML
	protected void updateClick(ActionEvent event) {
			saveUserPrefData();
			closeSettings(event);	
	}
	
	@FXML
	protected void cancelClick(ActionEvent event) {
		closeSettings(event);
	}
	

	@FXML
	protected void amtiOn(ActionEvent event) {
		amti = true;
		amtiOn.widgetSetVal(VAL.GREEN);
		amtiOff.widgetSetVal(VAL.RED);
	}
	
	@FXML
	protected void amtiOff(ActionEvent event) {
		amti = false;
		amtiOn.widgetSetVal(VAL.RED);
		amtiOff.widgetSetVal(VAL.GREEN);
	}
	
	@FXML
	protected void clutterMapOn(ActionEvent event) {
		clutterMap = true;
		clutterMapOn.widgetSetVal(VAL.GREEN);
		clutterMapOff.widgetSetVal(VAL.RED);
	}
	
	@FXML
	protected void clutterMapOff(ActionEvent event) {
		clutterMap = false;
		clutterMapOn.widgetSetVal(VAL.RED);
		clutterMapOff.widgetSetVal(VAL.GREEN);
	}
	
	@FXML
	protected void cfarCA(ActionEvent event) {
		cfar = "CA";
		cfarTypeCA.widgetSetVal(VAL.GREEN);
		cfarTypeGO.widgetSetVal(VAL.YELLOW);
		cfarTypeLO.widgetSetVal(VAL.YELLOW);
	}
	
	@FXML
	protected void cfarGO(ActionEvent event) {
		cfar = "GO";
		cfarTypeCA.widgetSetVal(VAL.YELLOW);
		cfarTypeGO.widgetSetVal(VAL.GREEN);
		cfarTypeLO.widgetSetVal(VAL.YELLOW);
	}
	
	@FXML
	protected void cfarLO(ActionEvent event) {
		cfar = "LO";
		cfarTypeCA.widgetSetVal(VAL.YELLOW);
		cfarTypeGO.widgetSetVal(VAL.YELLOW);
		cfarTypeLO.widgetSetVal(VAL.GREEN);
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
	    logger.info("Advanced Settings Dialog Closed");
	}
	
	private void loadUserPrefValues() {
		UserPreference uPreference = UserPreference.getInstance();
		
		int mType = Integer.parseInt(uPreference.getMTI_TYPE());
		mtiType.getSelectionModel().select(mType);
		
		if(uPreference.getAMTI().contains("ON")) {
			amtiOn.widgetSetVal(VAL.GREEN);
			amti = true;
		}else
			amtiOff.widgetSetVal(VAL.GREEN);
		
		if(uPreference.getCLUTTER_MAP().contains("ON")) {
			clutterMapOn.widgetSetVal(VAL.GREEN);
			clutterMap = true;
		} else
			clutterMapOff.widgetSetVal(VAL.GREEN);
		
		if(uPreference.getCFAR_TYPE().contains("CA")) {
			cfar = "CA";
			cfarTypeCA.widgetSetVal(VAL.GREEN);
		} else if(uPreference.getCFAR_TYPE().contains("GO")) {
			cfar = "GO";
			cfarTypeGO.widgetSetVal(VAL.GREEN);
		} else {
			cfar = "LO";
			cfarTypeLO.widgetSetVal(VAL.GREEN);
		}
		
		cfarThreshold.setValue(uPreference.getCFAR_THRESHOLD());			

	}
	
	private void saveUserPrefData() {
		UserPreference uPreference = UserPreference.getInstance();
		
		uPreference.setMTI_TYPE(String.valueOf(mtiType.getSelectionModel().getSelectedIndex()));
		
		if(amti)
			uPreference.setAMTI("ON");
		else
			uPreference.setAMTI("OFF");
		
		if(clutterMap)
			uPreference.setCLUTTER_MAP("ON");
		else
			uPreference.setCLUTTER_MAP("OFF");
		
		uPreference.setCFAR_TYPE(cfar);		
		uPreference.setCFAR_THRESHOLD(cfarThreshold.getValue());
		
		Constance.SPConfig.MTItype= (short) mtiType.getSelectionModel().getSelectedIndex();
		Constance.SPConfig.AMTi=(short) (amti? 1:0);
		Constance.SPConfig.ClutterMap=((byte) (clutterMap? 1:0));
		Constance.SPConfig.CFarType=cfar;
		Constance.SPConfig.CFarThreshold=(byte) Integer.parseInt(cfarThreshold.getValue());
			
	}
	
	private void initDialog() {
				
		mtiType.getItems().clear();
		mtiType.getItems().addAll("3 pulse","Wide notch");
		
		amtiOn.widgetSetVal(VAL.RED);
		amtiOff.widgetSetVal(VAL.RED);
		
		clutterMapOn.widgetSetVal(VAL.RED);
		clutterMapOff.widgetSetVal(VAL.RED);
		
		cfarTypeCA.widgetSetVal(VAL.YELLOW);
		cfarTypeGO.widgetSetVal(VAL.YELLOW);
		cfarTypeLO.widgetSetVal(VAL.YELLOW);
				
		cfarThreshold.getItems().clear();
		cfarThreshold.getItems().addAll("1","2", "3","4");
		
	}
	
}
