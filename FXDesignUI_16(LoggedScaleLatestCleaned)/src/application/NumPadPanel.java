package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import model.AppConfig;

import org.apache.log4j.Logger;

import admin.UserPreference;

public class NumPadPanel implements Initializable{

	static final Logger logger = Logger.getLogger(NumPadPanel.class);
	
	@FXML AnchorPane NumPad;
	
	@FXML Label NumTitle;
	@FXML Label lblName;
	@FXML TextField NumValue;
	@FXML CheckBox showPass;
	
	@FXML MaterialDesignButtonWidget Num1;
	@FXML MaterialDesignButtonWidget Num2;
	@FXML MaterialDesignButtonWidget Num3;
	@FXML MaterialDesignButtonWidget Num4;
	@FXML MaterialDesignButtonWidget Num5;
	@FXML MaterialDesignButtonWidget Num6;
	@FXML MaterialDesignButtonWidget Num7;
	@FXML MaterialDesignButtonWidget Num8;
	@FXML MaterialDesignButtonWidget Num9;
	@FXML MaterialDesignButtonWidget Num0;
	
	@FXML MaterialDesignButtonWidget NewPass;
	@FXML MaterialDesignButtonWidget NumEsc;
	@FXML MaterialDesignButtonWidget NumReturn;
	
	List<String> Value = new ArrayList<String>();
	double initialX,initialY;
	boolean isShow = false;
	boolean isOk = false;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addDraggableNode(NumPad);
		initComponents();		
		logger.info("Num Pad Dialog Initialized");
	}
	
	@FXML
	protected void onNum1(ActionEvent event) {
		if(isShow)
			NumValue.appendText("1");
		else
			NumValue.appendText("*");
		Value.add("1");
	}
	
	@FXML
	protected void onNum2(ActionEvent event) {
		if(isShow)
			NumValue.appendText("2");
		else
			NumValue.appendText("*");
		Value.add("2");
	}
	
	@FXML
	protected void onNum3(ActionEvent event) {
		if(isShow)
			NumValue.appendText("3");
		else
			NumValue.appendText("*");
		Value.add("3");
	}
	
	@FXML
	protected void onNum4(ActionEvent event) {
		if(isShow)
			NumValue.appendText("4");
		else
			NumValue.appendText("*");
		Value.add("4");
	}
	
	@FXML
	protected void onNum5(ActionEvent event) {
		if(isShow)
			NumValue.appendText("5");
		else
			NumValue.appendText("*");
		Value.add("5");
	}
	
	@FXML
	protected void onNum6(ActionEvent event) {
		if(isShow)
			NumValue.appendText("6");
		else
			NumValue.appendText("*");
		Value.add("6");
	}
	
	@FXML
	protected void onNum7(ActionEvent event) {
		if(isShow)
			NumValue.appendText("7");
		else
			NumValue.appendText("*");
		Value.add("7");
	}
	
	@FXML
	protected void onNum8(ActionEvent event) {
		if(isShow)
			NumValue.appendText("8");
		else
			NumValue.appendText("*");
		Value.add("8");
	}
	
	@FXML
	protected void onNum9(ActionEvent event) {
		if(isShow)
			NumValue.appendText("9");
		else
			NumValue.appendText("*");
		Value.add("9");
	}
	
	@FXML
	protected void onNum0(ActionEvent event) {
		if(isShow)
			NumValue.appendText("0");
		else
			NumValue.appendText("*");
		Value.add("0");
	}
	
	@FXML
	protected void onEsc(ActionEvent event) {
		NumValue.setText("");
		Value.clear();
	}
	
	@FXML
	protected void closeClick(ActionEvent event) {
		closeDialog(event);
	}
	
	@FXML
	protected void onNewPassword(ActionEvent event) {
		setNewPassCodeButton(false);
		NumTitle.setStyle("-fx-background-color: #9966ff;");
		NumTitle.setText("Enter Current Passcode: ");
		NumValue.setText("");
		Value.clear();
	}
	
	@FXML
	protected void onReturn(ActionEvent event) {
		isOk = true;
		if(NumTitle.getText().contains("Current")) {
			String passcode = getValue();
			if(UserPreference.getInstance().getPASS_CODE().equals(passcode)) {
				NumTitle.setStyle("-fx-background-color: rgba(30, 104, 35, 1);");
				NumTitle.setText("Enter New Passcode: ");
			} else {
				AppConfig.getInstance().openErrorDialog("Incorrect Passcode!");
				setNewPassCodeButton(true);
				NumTitle.setText("Enter Passcode: ");
				NumTitle.setStyle("-fx-background-color: #2398d3;");
			}
			NumValue.setText("");
			Value.clear();
		} else if(NumTitle.getText().contains("New")) {
			String passcode = getValue();
			if(passcode.length() > 4) {
				UserPreference.getInstance().setPASS_CODE(passcode);
				AppConfig.getInstance().openInformationDialog("You Passcode is Changed!");
			} else {
				AppConfig.getInstance().openErrorDialog("Passcode must be of minimum length 5 or more.</br>Exiting New Passcode dialog.");
				setNewPassCodeButton(true);
			}
			NumValue.setText("");
			Value.clear();
			NumTitle.setText("Enter Passcode: ");
			NumTitle.setStyle("-fx-background-color: #2398d3;");
		} else {
			closeDialog(event);
		}
	}

	private void initComponents() {
		FXMLController.setNodeStyle(NumValue);
		NewPass.widgetSetVal(VAL.YELLOW);
		NumEsc.widgetSetVal(VAL.DEFAULT);
		NumReturn.widgetSetVal(VAL.GREEN);
		
		showPass.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
	        public void changed(ObservableValue<? extends Boolean> ov,
	                Boolean old_val, Boolean new_val) {
	                   if(showPass.isSelected()) {
	                	   showNumActualVal();
	                	   isShow = true;
	                   } else {
	                	   setNumValStars();
	                	   isShow = false;
	                   }
	            }
	        });
		
	}
	
	private void showNumActualVal() {
		StringBuilder sb = new StringBuilder();
 	   for (String s : Value)
 	       sb.append(s);
 	   NumValue.setText(sb.toString());
	}
	
	private void setNumValStars() {
		NumValue.setText("");
 	   for(int i=0;i<Value.size();i++)
 		NumValue.appendText("*"); 
	}
	
	private void closeDialog(ActionEvent event) {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
		logger.info("Num Pad Dialog Closed");
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
	
	public String getValue() {
		if(isOk) {
			StringBuilder sb = new StringBuilder();
	 	   	for (String s : Value)
	 	       sb.append(s);
			return sb.toString();
		}
		return null;
	}

	public void setTitle(final String title) {
		NumTitle.setText(title);
	}
	
	public void setNewPassCodeButton(boolean bool) {
		NewPass.setVisible(bool);
	}

}
