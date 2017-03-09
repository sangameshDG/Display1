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
//*H  Created: Jan 31, 2016
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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import model.AppConfig;
import model.ILayoutParam;

import org.apache.log4j.Logger;

import db.DBRecord;

public class LoginController implements Initializable, ILayoutParam {

	private static final Logger logger = Logger
			.getLogger(LoginController.class);

	@FXML
	AnchorPane LoginDialog;

	@FXML
	TextField username;
	@FXML
	PasswordField password;

	@FXML MaterialDesignButtonWidget close;
	@FXML
	MaterialDesignButtonWidget login;

	double initialX, initialY;

	boolean showUserMgmt;
	boolean startDisplay;
	boolean showDisplay;

	@Override
	public void draw(GraphicsContext gc) {

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Login Dialog Opened");
		addDraggableNode(LoginDialog);
		initDialog();
	}

	@FXML
	protected void cancelClick(ActionEvent event) {
		cleanUp();
		closeSettings(event);
	}

	@FXML
	protected void SignInClick(ActionEvent event) {
		if (authUser()) {
			if (startDisplay) {
				closeSettings(event);
//				unlock();
				AppConfig.getInstance().getFxmlController()
						.startDisplayAction();
			} else if (showUserMgmt) {
				closeSettings(event);
				AppConfig.getInstance().getFxmlController().openUserMgmt();
			} else if (showDisplay) {
				closeSettings(event);
//				unlock();
			}
		} else {
			AppConfig.getInstance().openErrorDialog(
					"Incorrect username or password");
		}
	}

	public boolean isShowUserMgmt() {
		return showUserMgmt;
	}

	public void setShowUserMgmt(boolean showUserMgmt) {
		this.showUserMgmt = showUserMgmt;
	}

	public boolean isStartDisplay() {
		return startDisplay;
	}

	public void setStartDisplay(boolean startDisplay) {
		this.startDisplay = startDisplay;
	}

	public boolean isShowDisplay() {
		return showDisplay;
	}

	public void setShowDisplay(boolean showDisplay) {
		this.showDisplay = showDisplay;
		close.setVisible(false);
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
		login.widgetSetVal(VAL.DEFAULT);
	}

	private void cleanUp() {

	}

	private void closeSettings(ActionEvent event) {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
		logger.info("Login Dialog Closed");
	}

	private boolean authUser() {
		String user = username.getText();
		String pass = password.getText();
		return DBRecord.getInstance().authUser(user, pass);
	}
}