package application;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import model.AppConfig;
import model.ILayoutParam;

import org.apache.log4j.Logger;

import utils.Constance;
import admin.User;
import db.DBRecord;

public class MasterSetUp implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(MasterSetUp.class);	
	
	@FXML AnchorPane MasterDialog;
	@FXML AnchorPane MainView;
	
	@FXML TabPane mainTab;
	
	@FXML Tab tabUserCreation;
	@FXML TextField fullname;
	@FXML TextField username;
	@FXML PasswordField password;
	@FXML ComboBox<String> comboRole;
	@FXML Label lbl_fullName;
	@FXML Label lbl_username;
	@FXML Label lbl_password;
	@FXML Label lbl_createStatus;
	@FXML TextField uniqueID;
	@FXML MaterialDesignButtonWidget createUser;
	
	@FXML Tab tabUserManagement;
	@FXML TableView<User> userTable;

	double initialX,initialY;
	private static final int CHAR_LENGTH = 5;
	
	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Master SetUp Dialog Opened");
		addDraggableNode(MasterDialog);
		initDialog();
	}	
	
	@FXML
	protected void cancelClick(ActionEvent event) {
		cleanUp();
		closeSettings(event);
	}
	
	@FXML
	protected void createUserClick(ActionEvent event) {
		if(validInfo()) {
			User newUser = new User(uniqueID.getText(),fullname.getText(),username.getText(),password.getText(),comboRole.getValue());
			if(DBRecord.getInstance().createUserDataToDb(newUser)) {
				lbl_createStatus.setText("Successfully created user: "+newUser.getFullName());
				logger.info(lbl_createStatus.getText());
			}
		}		
	}
	
	@FXML
	protected void resetClick(ActionEvent event) {
		fullname.setText("");
		username.setText("");
		password.setText("");
		comboRole.getSelectionModel().select(1);
		lbl_createStatus.setText("");
	}
	
	@FXML
	protected void refreshUserTable(ActionEvent event) {
		refreshTable();
	}
	
	@FXML
	protected void deleteUsers(ActionEvent event) {
		deleteSelUsers();
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
		
		tabUserCreation.setDisable(true);
		tabUserManagement.setDisable(true);
		User user = AppConfig.getInstance().getLoggedUser();
		logger.info(user.toString());
		if(user.getType().equals(Constance.USER_MASTER) || user.getType().equals(Constance.USER_TECHNICAL)){
			tabUserCreation.setDisable(false);
			tabUserManagement.setDisable(false);
		}
		
		comboRole.getItems().clear();
		comboRole.getItems().addAll(Constance.USER_TECHNICAL,Constance.USER_CONTROLLER);
		comboRole.getSelectionModel().select(1);
		
		uniqueID.setDisable(true);
		String date = AppConfig.getInstance().getFxmlController().getDate();
		String time = AppConfig.getInstance().getFxmlController().getTime();
		Date datetime;
		String dt = "NULL";
		try {
			datetime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(date+" "+time);
			dt = new SimpleDateFormat("ddMMyyyyHHmm").format(datetime);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		uniqueID.setText("IN_"+dt);		
		
		createUser.widgetSetVal(VAL.DEFAULT);
		
		//load user table
		userTable.setEditable(true);
		TableColumn<User, String> colId = new TableColumn<User, String>("Id");
		TableColumn<User, String> colFname = new TableColumn<User, String>("Full Name");
		TableColumn<User, String> colUname = new TableColumn<User, String>("Username");
		TableColumn<User, String> colType = new TableColumn<User, String>("Type");

		//userTable.getColumns().addAll(colId,colFname,colUname,colType);
		colId.setMinWidth(50);
		colId.setCellValueFactory(
                new PropertyValueFactory<User, String>("id"));
		colFname.setMinWidth(100);
		colFname.setCellValueFactory(
                new PropertyValueFactory<User, String>("fullName"));
		colUname.setMinWidth(100);
		colUname.setCellValueFactory(
                new PropertyValueFactory<User, String>("username"));
		colType.setMinWidth(100);
		colType.setCellValueFactory(
                new PropertyValueFactory<User, String>("type"));

		tabUserManagement.setOnSelectionChanged(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				refreshTable();
			}
		});
	}
		
	private void cleanUp() {

	}

	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("Master SetUp Dialog Closed");	
	}
	
	private boolean validInfo() {
		if(fullname.getText().length() < CHAR_LENGTH) {
			AppConfig.getInstance().openErrorDialog("Full Name must be more than "+CHAR_LENGTH+" characters");
			return false;
		}
		
		if(username.getText().length() < CHAR_LENGTH) {
			AppConfig.getInstance().openErrorDialog("User Name must be more than "+CHAR_LENGTH+" characters");
			return false;
		}
		
		if(password.getText().length() > 3*CHAR_LENGTH) {
			AppConfig.getInstance().openErrorDialog("Password should not exceed more than "+3*CHAR_LENGTH+" characters");
			return false;
		}
		
		
		if(DBRecord.getInstance().getTotalUsers() > Constance.USERS_LIMIT) {
			AppConfig.getInstance().openErrorDialog("Cannot create more users. Limit reached.");
			return false;
		}
		
		if(DBRecord.getInstance().isUserExist(username.getText())) {
			AppConfig.getInstance().openErrorDialog("Username already exists.");
			return false;
		}
				
		return true;
	}
	
	private void refreshTable() { 
		userTable.setItems(DBRecord.getInstance().readUsersTable());
	}
	
	private void deleteSelUsers() {
		User deleteUser = userTable.getSelectionModel().getSelectedItem();
		if(deleteUser!=null) {
			if (AppConfig.getInstance().openConfirmationDialog(
					"Do you wish to be delete this user?")) {
				if(DBRecord.getInstance().deleteUserName(deleteUser.getUsername()))
					logger.info("Deleted user: "+deleteUser.toString());
				
				refreshTable();
			}
		}
	}

}