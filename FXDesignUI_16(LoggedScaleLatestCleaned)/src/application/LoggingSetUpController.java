package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import model.AppConfig;
import model.ILayoutParam;
import model.data.security.TrippleDes;

import org.apache.log4j.Logger;

import utils.Constance;

public class LoggingSetUpController implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(LoggingSetUpController.class);
	
	@FXML AnchorPane LoggingSetup;	
	
	@FXML MaterialDesignButtonWidget startBtn;
	@FXML MaterialDesignButtonWidget exportBtn;
	@FXML MaterialDesignButtonWidget loadBtn;
	@FXML MaterialDesignButtonWidget privilegedExportBtn;
	@FXML ProgressIndicator reportProgress;
	
	@FXML ListView<String> azStringLogList;
	@FXML ListView<String> elStringLogList;
	
	double initialX,initialY;
	File selectedFile = null;
	StringBuilder sbAz = new StringBuilder();
	StringBuilder sbEl = new StringBuilder();
	boolean isClosed;
		
	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Logging Setup Dialog Opened");
		addDraggableNode(LoggingSetup);
		initDialog();
	}
	
	@FXML
	protected void loadLog(ActionEvent event) {
		if(!Constance.IS_LOGGING_SETUP) {
			initObjs();
			loadReport(event);
			new Thread(new Runnable() {
				@Override
				public void run() {
					decodeReportData();
					if(selectedFile!=null)
						AppConfig.getInstance().openInformationDialog("Loaded Reports Successfully");
				}
			}).start();
		} else {
			AppConfig.getInstance().openErrorDialog("Logging is in progress. Please stop to load.");
		}
	}
	
	@FXML
	protected void exportLog(ActionEvent event) {
		if(!Constance.IS_LOGGING_SETUP) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					exportReportData();
					AppConfig.getInstance().openInformationDialog("Exported Current Reports Successfully");
				}
			}).start();
		} else {
			AppConfig.getInstance().openErrorDialog("Logging is in progress. Please stop to export.");
		}
	}
	
	@FXML
	protected void priviExportlog(ActionEvent event) {
		if(AppConfig.getInstance().isUserHasPrevilege()) {
			if(!Constance.IS_LOGGING_SETUP) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						priviExportReportData();
						AppConfig.getInstance().openInformationDialog("Exported Current Reports Successfully");
					}
				}).start();
			} else {
				AppConfig.getInstance().openErrorDialog("Logging is in progress. Please stop to export.");
			}
		}
	}
	
	@FXML
	protected void startLog(ActionEvent event) {
		if(!Constance.IS_LOGGING_SETUP) {
			startBtn.setText("Stop Logging");
			startBtn.widgetSetVal(VAL.RED);
			initObjs();
			Constance.IS_LOGGING_SETUP = true;
		} else {
			if(AppConfig.getInstance().openConfirmationDialog("Do you wish to stop logging ?")) {
				startBtn.setText("Start Logging");
				startBtn.widgetSetVal(VAL.GREEN);
				Constance.IS_LOGGING_SETUP = false;
				sbAz.append("\nENDED HERE");
				sbEl.append("\nENDED HERE");
			}
		}
		AppConfig.getInstance().getFxmlController().changeLogging(Constance.IS_LOGGING_SETUP);
	}

	@FXML
	protected void cancelClick(ActionEvent event) {
		closeSettings(event);
	}
	
	public void notifyAzListData(String text) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				azStringLogList.getItems().add(text);
			}
		});
	}

	public void notifyElListData(String text) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				elStringLogList.getItems().add(text);
			}
		});
	}

	public void appendAzLine(final String data) {
		sbAz.append(data+"\n");		
		if(!isClosed)
			notifyAzListData(data+"\n");
	}
	
	public void appendElLine(final String data) {
		sbEl.append(data+"\n");
		if(!isClosed)
			notifyElListData(data+"\n");
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
	
	private void initObjs() {
		sbAz = new StringBuilder();
		sbEl = new StringBuilder();
		sbAz.append("STARTED HERE\n");
		sbEl.append("STARTED HERE\n");
		azStringLogList.getItems().clear();
		elStringLogList.getItems().clear();
	}
	
	private void initDialog() {
		exportBtn.widgetSetVal(VAL.DEFAULT);
		loadBtn.widgetSetVal(VAL.DEFAULT);
		startBtn.widgetSetVal(VAL.GREEN);
		if(Constance.IS_LOGGING_SETUP) {
			startBtn.setText("Stop Logging");
			startBtn.widgetSetVal(VAL.RED);
		}
		
		if(AppConfig.getInstance().getLoggingSetUpController()!=null) {
			sbAz.append(AppConfig.getInstance().getLoggingSetUpController().sbAz);
			sbEl.append(AppConfig.getInstance().getLoggingSetUpController().sbEl);
		}
		
	}

	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("Logging Setup Dialog Closed");
	}
	
	private void priviExportReportData() {
		showProgressLoading(true);		
		// writing txt data
		try {
			String date = AppConfig.getInstance().getFxmlController().getDate();
			String time = AppConfig.getInstance().getFxmlController().getTime();
			Date datetime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
					.parse(date + " " + time);
			String dt = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss")
					.format(datetime);
			Files.write(Paths.get("radar-az-report-" + dt + Constance.REPORT_TEXT_EXT),
					sbAz.toString().getBytes(), StandardOpenOption.CREATE);
			Files.write(Paths.get("radar-el-report-" + dt + Constance.REPORT_TEXT_EXT),
					sbEl.toString().getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		showProgressLoading(false);
	}
	
	private void exportReportData() {
		showProgressLoading(true);
		//combining AZ EL
		StringBuilder sb = new StringBuilder();
		sb.append(sbAz.toString());
		sb.append("\n"+DiamondOptionController.PATTERN_SEP+"\n");
		sb.append(sbEl.toString());
		
		// encrypting data
		try {
			String encrytped = new String(new TrippleDes().encrypt(sb.toString()));
			logger.info("ENCRYPTED REPORT");

			String date = AppConfig.getInstance().getFxmlController().getDate();
			String time = AppConfig.getInstance().getFxmlController().getTime();
			Date datetime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
					.parse(date + " " + time);
			String dt = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss")
					.format(datetime);
			Files.write(Paths.get("radar-report-" + dt + Constance.REPORT_FILE_EXT),
					encrytped.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		showProgressLoading(false);
	}
	
	private void loadReport(ActionEvent event) {
		// browse file
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("REPORT Files", "*"
						+ Constance.REPORT_FILE_EXT));

		// select file
		try {
			Node source = (Node) event.getSource();
			Stage stage = (Stage) source.getScene().getWindow();
			selectedFile = fileChooser.showOpenDialog(stage);
			if (selectedFile != null) {
				logger.info("Opening File: " + selectedFile.getName());
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	private void decodeReportData() {
		// read file
		showProgressLoading(true);
		if (selectedFile != null) {
			StringBuilder content = new StringBuilder();
			try {
				@SuppressWarnings("resource")
				BufferedReader br = new BufferedReader(new FileReader(selectedFile));
				String line;
				while ((line = br.readLine()) != null) {
					content.append(line);
					logger.info("REPORT file read");
				}
			} catch (IOException e) {
				logger.error(e);
			}

			if (content != null) {
				// Decrypt file
				try {
					String decrypted = new String(new TrippleDes().decrypt(content.toString()));
					logger.info("DECRYPTED REPORT: ");

					// extract from REPORT to data
					String[] reportSplit = decrypted.split("\n",-1);
					if (reportSplit.length > 0) {
						boolean isEl = false;
						List<String> collecTextEl = new ArrayList<String>();
						List<String> collecTextAz = new ArrayList<String>();
						for(String str: reportSplit) {
							if(str.contains(DiamondOptionController.PATTERN_SEP)) {
								isEl = true;
								continue;
							}
							if(isEl) {
								collecTextEl.add(str);
							} else {
								collecTextAz.add(str);
							}
						}
						
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								azStringLogList.getItems().addAll(collecTextAz);
								elStringLogList.getItems().addAll(collecTextEl);								
							}
						});
						
						logger.info("Published report: "+reportSplit);
					}
				} catch (Exception e) {
					logger.error(e);
					AppConfig.getInstance().openErrorDialog("Corrupt File");
				}
			}
			showProgressLoading(false);
		}
	}
	
	private void showProgressLoading(boolean show) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				reportProgress.setVisible(show);
			}
		});
	}
}