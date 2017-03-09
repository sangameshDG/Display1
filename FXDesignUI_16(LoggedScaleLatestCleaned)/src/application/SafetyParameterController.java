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
//*H  Created: 12-Jan-2017
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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.AppConfig;
import model.ILayoutParam;
import utils.Constance;

/**
 * Defines the class SafetyParameterController.
 * @author SangappaG
 * @version $Revision: $.
 * @since 12-Jan-2017
 */
public class SafetyParameterController implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(SafetyParameterController.class);
	
	AppConfig appConfig = AppConfig.getInstance();

	double initialX,initialY;
	@FXML AnchorPane Settings;
	
	@FXML TextField DH1;
	@FXML TextField DH2;
	@FXML TextField DH3;
	
	@FXML TextField EL_SH1;
	@FXML TextField EL_SH2;
	@FXML TextField EL_SH3;
	
	@FXML TextField AZ_SH1;
	@FXML TextField AZ_SH2;
	@FXML TextField AZ_SH3;
	
	
	@Override
	public void draw(GraphicsContext gc) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		logger.info("SafetyParam SetUp Dialog Opened");
		addDraggableNode(Settings);
		loadUserPrefValues();
		initDialog();	
	}
	
	@FXML
	protected void okClick(ActionEvent event) {
		    locallySaveDisplaySetupData();
			saveUserPrefData();//Must be after above call
			AppConfig.getInstance().getFxmlController().invalidate();
			closeSettings(event);			
	}
	
	
	private void loadUserPrefValues() {
		UserPreference uPreference = UserPreference.getInstance();

		DH1.setText(String.valueOf(uPreference.getDhHeight1()));
		DH2.setText(String.valueOf(uPreference.getDhHeight2()));
		DH3.setText(String.valueOf(uPreference.getDhHeight3()));
		
		EL_SH1.setText(String.valueOf(uPreference.getElSafetyHeight1()));
		EL_SH2.setText(String.valueOf(uPreference.getElSafetyHeight2()));
		EL_SH3.setText(String.valueOf(uPreference.getElSafetyHeight3()));
		
		AZ_SH1.setText(String.valueOf(uPreference.getAzSafetyHeight1()));
		AZ_SH2.setText(String.valueOf(uPreference.getAzSafetyHeight2()));
		AZ_SH3.setText(String.valueOf(uPreference.getAzSafetyHeight3()));
		
		/*DH1.setText(String.valueOf(Constance.SafetyParam.DH1*0.54));
		DH2.setText(String.valueOf(Constance.SafetyParam.DH2*0.54));
		DH3.setText(String.valueOf(Constance.SafetyParam.DH3*0.54));*/
		
		
	}
	
     private void initDialog() {
    	 StringProperty doubleRestrict = new SimpleStringProperty(this, "restrict");
    	 doubleRestrict.set("[0-9]+-");
    	 
    	 DH1.textProperty().addListener(new ChangeListener<String>() {

 			@Override
 			public void changed(ObservableValue<? extends String> observable,
 					String oldValue, String newValue) {

 				if (doubleRestrict.get() != null && !doubleRestrict.get().equals("") && !newValue.matches(doubleRestrict.get() + "*")) {
 					DH1.setText(newValue);
                 }

 			}
 		});
    	 
    	 DH2.textProperty().addListener(new ChangeListener<String>() {

  			@Override
  			public void changed(ObservableValue<? extends String> observable,
  					String oldValue, String newValue) {

  				if (doubleRestrict.get() != null && !doubleRestrict.get().equals("") && !newValue.matches(doubleRestrict.get() + "*")) {
  					DH2.setText(newValue);
                  }

  			}
  		});
    	 
    	 DH3.textProperty().addListener(new ChangeListener<String>() {

  			@Override
  			public void changed(ObservableValue<? extends String> observable,
  					String oldValue, String newValue) {

  				if (doubleRestrict.get() != null && !doubleRestrict.get().equals("") && !newValue.matches(doubleRestrict.get() + "*")) {
  					DH3.setText(newValue);
                  }

  			}
  		});
		
	 }
     
     private void locallySaveDisplaySetupData() {
 		
    	Constance.SafetyParam.DH1 =Double.valueOf(DH1.getText())*Constance.UNITS.NM_TO_KM;	
    	Constance.SafetyParam.DH2 =Double.valueOf(DH2.getText())*Constance.UNITS.NM_TO_KM;	
    	Constance.SafetyParam.DH3 =Double.valueOf(DH3.getText())*Constance.UNITS.NM_TO_KM;		
 		
 		Constance.SafetyParam.FirstSetHeight = Double.valueOf(EL_SH1.getText())*Constance.UNITS.FT_TO_METER;
 		Constance.SafetyParam.SecondSetHeight = Double.valueOf(EL_SH2.getText())*Constance.UNITS.FT_TO_METER;
 		Constance.SafetyParam.ThirdSetHeight = Double.valueOf(EL_SH3.getText())*Constance.UNITS.FT_TO_METER;
 		
 		Constance.SafetyParam.FirstSetLR = Double.valueOf(AZ_SH1.getText())*Constance.UNITS.FT_TO_KM;
 		Constance.SafetyParam.SecondSetLR =Double.valueOf(AZ_SH2.getText())*Constance.UNITS.FT_TO_KM;
 		Constance.SafetyParam.ThirdSetLR = Double.valueOf(AZ_SH3.getText())*Constance.UNITS.FT_TO_KM;
 	
 	}
     
     private void saveUserPrefData() {
    	 UserPreference uPreference = UserPreference.getInstance();
    	 
 		uPreference.setDH_HEIGHT1(Double.valueOf(DH1.getText()));
 		uPreference.setDH_HEIGHT2(Double.valueOf(DH2.getText()));
 		uPreference.setDH_HEIGHT3(Double.valueOf(DH3.getText()));
 		
 		uPreference.setEL_SAFETY_HEIGHT1(Double.valueOf(EL_SH1.getText()));
 		uPreference.setEL_SAFETY_HEIGHT2(Double.valueOf(EL_SH2.getText()) );
 		uPreference.setEL_SAFETY_HEIGHT3(Double.valueOf(EL_SH3.getText()));
 		
 		uPreference.setAZ_SAFETY_HEIGHT1( Double.valueOf(AZ_SH1.getText()));
 		uPreference.setAZ_SAFETY_HEIGHT2( Double.valueOf(AZ_SH2.getText()));
 		uPreference.setAZ_SAFETY_HEIGHT3( Double.valueOf(AZ_SH3.getText()));
 			
 	}
	
     @FXML
     protected void cancelClick(ActionEvent event) {
	    closeSettings(event);
    }

	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("SafetyParam Setup Dialog Closed");
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

}
