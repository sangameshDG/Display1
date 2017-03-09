package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.AppConfig;

import org.apache.log4j.Logger;

public class Master extends Application {
	
	private static final Logger logger = Logger.getLogger(Master.class);
	FXMLController mFXMLController;
	
	@Override
	public void start(Stage primaryStage) {
		initStage(primaryStage);
	}
	
	public static void main(String[] args) {
		loadLoggerSettings();
		launch(args);
	}
	
	private static void loadLoggerSettings() {
		logger.info("FX LAUNCH");
	}
	
	private void initStage(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.load(getClass().getResourceAsStream("Activity.fxml"));
			Parent root = (Parent) fxmlLoader.getRoot();
			mFXMLController = (FXMLController) fxmlLoader.getController();
			AppConfig.getInstance().setFxmlController(mFXMLController);
			Scene scene = new Scene(root);
			primaryStage.initStyle(StageStyle.UNDECORATED);
	        primaryStage.setMaximized(false);
	        primaryStage.setResizable(false);
	        primaryStage.setScene(scene);
	        primaryStage.show();   
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
}
