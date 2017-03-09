package application;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;
import admin.UserPreference;
import db.DBRecord;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import materialdesignbutton.MaterialDesignButtonWidget;
import messages.radar.AzimuthPlaneDetectionPlotMsg;
import messages.radar.AzimuthPlanePlotsPerCPIMsg;
import messages.radar.AzimuthPlaneTrackMsg;
import messages.radar.ElevationPlaneDetectionPlotMsg;
import messages.radar.ElevationPlanePlotsPerCPIMsg;
import messages.radar.ElevationPlaneTrackMsg;
import messages.utils.IByteSum;
import model.AppConfig;
import model.ILayoutParam;
import network.LiquidStream;
import utils.AppUtils;
import utils.Constance;

public class ReplayController implements Initializable,ILayoutParam, IByteSum {

	private static final Logger logger = Logger.getLogger(ReplayController.class);

	Stage replayActivity;

	@FXML AnchorPane ReplayDialog;
	@FXML AnchorPane replayParentView;
	@FXML TextField directoryPath;
	@FXML TextField loadFilePath;
	@FXML CheckBox recInf;
	@FXML HBox minSecBox;
	@FXML TextField recordDurationMin;
	@FXML TextField recordDurationSec;
	@FXML MaterialDesignButtonWidget btn_record;
	@FXML MaterialDesignButtonWidget btn_replayfwd;
	@FXML MaterialDesignButtonWidget btn_replay;
	@FXML MaterialDesignButtonWidget btn_replaybwd;
	@FXML MaterialDesignButtonWidget btn_stop;
	@FXML Label lbl_time;
	@FXML Label lbl_speed;
	@FXML CheckBox ck_elevation;
	@FXML CheckBox ck_azimuth;
	@FXML CheckBox ck_plots;
	@FXML CheckBox ck_tracks;
	@FXML ProgressIndicator pi_loading;

	double displayingSpeedText = 1;
	double initialX,initialY;
	int hourTime = 0;
	int minTime = 0;
	double secTime = 0;
	double inputMinutes,inputSeconds;// THIS IS WITH CONTROLS ENABLED MINS & SECS

	String recordLoc;
	boolean isPause = false;

	//private IntegerProperty maxLength = new SimpleIntegerProperty(this, "maxLength", 3);
    private StringProperty restrict = new SimpleStringProperty(this, "restrict");

	private Timeline timerTimeline;
	private StringProperty timeRecord = new SimpleStringProperty(this, "0.0");

	File elPlotFile;
	File elTrackFile;
	File azPlotFile;
	File azTrackFile;
	File selectedFile = null;

	private static final String ELT = "EL-TRACK-";
	private static final String ELP = "EL-PLOT-";
	private static final String AZT = "AZ-TRACK-";
	private static final String AZP = "AZ-PLOT-";

	public Stage getReplayActivity() {
		return replayActivity;
	}

	public void setReplayActivity(Stage replayController) {
		this.replayActivity = replayController;
	}

	@Override
	public void draw(GraphicsContext gc) {

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Record Replay Dialog Opened");
		addDraggableNode(ReplayDialog);
		loadPref();
		initDialog();
	}

	@FXML
	protected void cancelClick(ActionEvent event) {
		savePref();
		closeRecordActivity(event);
	}

	@FXML
	protected void recordClick(ActionEvent event) {
		recordAction();
	}

	@FXML
	protected void replayClick(ActionEvent event) {
		if(!Constance.IS_RECORD_SETUP) {
			replayAction();
			if(!Constance.IS_REPLAY_SETUP) {
				Constance.PLAY_SPEED = Constance.NORMAL_SPEED;
				displayingSpeedText = 1;
				lbl_speed.setText("Speed: "+ new DecimalFormat("#.#").format(displayingSpeedText)+"X");
			}
		} else {
			AppConfig.getInstance().openErrorDialog("Recording in progress");
		}
	}

	@FXML
	protected void replayFwdClick(ActionEvent event) {
		if(Constance.IS_REPLAY_SETUP) {
			Constance.PLAY_SPEED = (int) (Constance.PLAY_SPEED*0.5);
			displayingSpeedText *= 2;
			lbl_speed.setText("Speed: "+ new DecimalFormat("#.#").format(displayingSpeedText)+"X");
		}
	}

	@FXML
	protected void replayBwdClick(ActionEvent event) {
		if(Constance.IS_REPLAY_SETUP && displayingSpeedText > 0.25){
			Constance.PLAY_SPEED = (int) (Constance.PLAY_SPEED*2);
			displayingSpeedText *= 0.5;
			lbl_speed.setText("Speed: "+ new DecimalFormat("#.#####").format(displayingSpeedText)+"X");
		}
	}

	@FXML
	protected void changeClick(ActionEvent event) {
		changeDirectory(event);
	}

	@FXML
	protected void browseClick(ActionEvent event) {
		loadFile(event);
	}

	@FXML
	protected void stopClick(ActionEvent event) {
		stopAction();
		timeRecord.set("Time » 0:0:0");
		logger.info("Recording stopped");
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

	private void loadPref() {
		recordLoc = UserPreference.getInstance().getRECORD_LOC();
	}

	private void initDialog() {
		logger.info("Previous Stored Record Location: "+recordLoc);

		recInf.setSelected(true);
		minSecBox.setVisible(false);
		
		directoryPath.setText(recordLoc);
		lbl_speed.setVisible(false);
		btn_stop.setText("");
		btn_record.setText("");
		btn_replay.setText("");
		btn_replayfwd.setText("");
		btn_replaybwd.setText("");
		btn_stop.setStyle("-fx-background-image: url(\"assets/img/stopped.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		btn_record.setStyle("-fx-background-image: url(\"assets/img/record.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		btn_replay.setStyle("-fx-background-image: url(\"assets/img/replay.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		btn_replayfwd.setStyle("-fx-background-image: url(\"assets/img/forward.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		btn_replaybwd.setStyle("-fx-background-image: url(\"assets/img/backward.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");

		recInf.selectedProperty().addListener(new ChangeListener<Boolean>() {
			
			@Override
	        public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				minSecBox.setVisible(old_val);
	        }
	    });
		
		timeRecord.set("Time » "+hourTime+":"+minTime+":"+secTime);
		lbl_time.textProperty().bind(timeRecord);
	    restrict.set("[0-9]");
	    recordDurationMin.setPromptText("(max "+Constance.MAX_RECORD_TIME+" )");
	    recordDurationMin.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (restrict.get() != null && !restrict.get().equals("") && !newValue.matches(restrict.get() + "*")) {
					recordDurationMin.setText(newValue);
                }

			}
		});

	    recordDurationSec.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				if (restrict.get() != null && !restrict.get().equals("") && !newValue.matches(restrict.get() + "*")) {
					recordDurationSec.setText(newValue);
                }

			}
		});

		timerTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                new EventHandler<ActionEvent>() {

                	@Override
                    public void handle(ActionEvent t) {
                		
						if(!Constance.IS_PAUSE_SETUP){                		
	                        secTime += displayingSpeedText;
	                        if(secTime>=60){
	                        	secTime=0;
	                        	++minTime;
	                        	
	                        	//every defined interval write files
		                        if(recInf.isSelected() && (minTime%Constance.RECORD_WRITE_INTERVAL == 0) && Constance.IS_RECORD_SETUP) {
		                        	createFiles();
		                        	writeFiles();
		                        }
	                        }	                        
	                        if(minTime>=60){
	                        	minTime=0;
	                        	++hourTime;
	                        }
	                        
	                        //set time correctly
	                        timeRecord.set("Time » "+hourTime+":"+minTime+":"+(int)secTime);
	                        if (minTime==inputMinutes && secTime==inputSeconds && !recInf.isSelected()) {
								stopAction();
	                        }                        
	
	                        //stop if application exits
	                        if(!AppConfig.getInstance().getFxmlController().isAppRunning())
	                        	stopAction();
						}
                    }
                })
            );
		timerTimeline.setCycleCount(Timeline.INDEFINITE);

	}

	private void savePref() {
		UserPreference.getInstance().setRECORD_LOC(directoryPath.getText());
	}

	private void closeRecordActivity(ActionEvent event) {
		replayActivity.close();
	    logger.info("Record Replay Dialog Closed");
	}

	private void recordAction() {
		lbl_speed.setVisible(false);
		if((ck_elevation.isSelected() || ck_azimuth.isSelected()) && (ck_plots.isSelected() || ck_tracks.isSelected())) {
			
			if(recInf.isSelected()) {
				//With Inf
				startRecording();
			} else {
				//With Mins & Seconds
				inputSeconds = Double.parseDouble(recordDurationSec.getText());
				if(inputSeconds >= 60)
					AppConfig.getInstance().openErrorDialog("enter valid seconds");
				else if(!recordDurationMin.getText().isEmpty()) {
					inputMinutes = Double.parseDouble(recordDurationMin.getText());
					if(inputMinutes == 0 && inputSeconds == 0)
						AppConfig.getInstance().openErrorDialog("enter valid minutes/seconds");
					else if(inputMinutes >= 0 && inputMinutes <= Constance.MAX_RECORD_TIME && DBRecord.getInstance().clearRecordDB()) {
						startRecording();
					} else {
						AppConfig.getInstance().openErrorDialog("Max duration is "+Constance.MAX_RECORD_TIME+" minutes");
					}
				} else {
					AppConfig.getInstance().openErrorDialog("Enter valid Minutes/Seconds");
				}
			}
		} else {
			AppConfig.getInstance().openErrorDialog("No data selected to record. Please select Elevation/Azimuth Plots/Track");
		}
	}
	
	private void startRecording() {
		timerTimeline.playFromStart();
		btn_record.setDisable(true);
		replayParentView.setDisable(true);
		Constance.IS_RECORD_SETUP = true;
		btn_record.setStyle("-fx-background-image: url(\"assets/img/recording.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		AppConfig.getInstance().getFxmlController().setRecording(true);
		logger.info("Recording....");
	}

	private void replayAction() {
		if(isPause && Constance.IS_REPLAY_SETUP){
			isPause = false;
			btn_replay.setStyle("-fx-background-image: url(\"assets/img/replaying.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");
			Constance.IS_PAUSE_SETUP = true;
			logger.info("Replaying is Paused");
		} else if (!isPause && Constance.IS_REPLAY_SETUP) {
			isPause = true;
			btn_replay.setStyle("-fx-background-image: url(\"assets/img/pause.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");
			Constance.IS_PAUSE_SETUP = false;
			logger.info("Replaying is Running");
		} else {
			if(!loadFilePath.getText().isEmpty()) {
				if(loadFilePath.getText().contains(Constance.RECORD_FILE_EXT)) {
					btn_replay.setStyle("-fx-background-image: url(\"assets/img/pause.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");
					isPause = true;
					lbl_speed.setVisible(true);
					replayParentView.setDisable(true);
					AppConfig.getInstance().getFxmlController().setReplaying(true);
					AppConfig.getInstance().getFxmlController().invalidate();//clear screen
					if(loadFilePath.getText().contains(ELP)) {
						readToDB(ELP);
					} else if(loadFilePath.getText().contains(ELT)) {
						readToDB(ELT);
					} else if(loadFilePath.getText().contains(AZP)) {
						readToDB(AZP);
					} else if(loadFilePath.getText().contains(AZT)) {
						readToDB(AZT);
					} else {
						btn_replay.setStyle("-fx-background-image: url(\"assets/img/replay.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");
						isPause = false;
						Constance.IS_REPLAY_SETUP = false;
						AppConfig.getInstance().openErrorDialog("Nothing to re-play");
					}
				} else {
					AppConfig.getInstance().openErrorDialog("Invalid file format");
				}
			} else {
				AppConfig.getInstance().openErrorDialog("Please select a recorded file");
			}
		}
	}

	public void stopAction() {
		//When stopped Write left overs
		if(Constance.IS_RECORD_SETUP) {
			createFiles();
			writeFiles();
		}
		
		btn_record.setStyle("-fx-background-image: url(\"assets/img/record.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		btn_replay.setStyle("-fx-background-image: url(\"assets/img/replay.png\"); -fx-background-size: 50 50; -fx-background-repeat: no-repeat; -fx-background-position: center;");
		lbl_speed.setVisible(false);

		timerTimeline.stop();
		hourTime = 0;
		secTime = 0;
		minTime = 0;
		btn_record.setDisable(false);
		btn_replay.setDisable(false);
		replayParentView.setDisable(false);

		isPause = false;
		Constance.IS_PAUSE_SETUP = false;
		Constance.IS_RECORD_SETUP = false;
		Constance.IS_REPLAY_SETUP = false;
		AppConfig.getInstance().getFxmlController().setRecording(false);
		AppConfig.getInstance().getFxmlController().setReplaying(false);
		AppConfig.getInstance().getFxmlController().invalidate();
	}

	private void changeDirectory(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Selct Folder Directory");
		try {
			File saveFile = directoryChooser.showDialog(replayActivity);
			if(saveFile !=null) {
				directoryPath.setText(saveFile.getAbsolutePath()+"\\");
				logger.info("Saving Directory: "+directoryPath.getText());
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void loadFile(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("BIN Files", "*"+Constance.RECORD_FILE_EXT));

		try {
			selectedFile = fileChooser.showOpenDialog(replayActivity);
			if(selectedFile !=null) {
				loadFilePath.setText(selectedFile.getPath());
				logger.info("Opening File: "+selectedFile.getName());
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private synchronized void createFiles() {

		String date = AppConfig.getInstance().getFxmlController().getDate();
		String time = AppConfig.getInstance().getFxmlController().getTime();
		Date datetime;
		String dt = "NULL";
		try {
			datetime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(date+" "+time);
			dt = new SimpleDateFormat("dd-MM-yyyy-HH-mm").format(datetime);
		} catch (ParseException e1) {
			logger.error(e1);
		}

		final String filePath = directoryPath.getText() +"\\"+ dt+"\\";
		final File dirFile = new File(filePath);
		try {
			if(!dirFile.exists())
				dirFile.mkdir();
			logger.info("Dir available: "+dirFile.getAbsolutePath());
		} catch (Exception e) {
			logger.error(e);
		}

		if(ck_elevation.isSelected()) {
			if(ck_plots.isSelected()) {
				try {
					elPlotFile = new File(filePath
						+ELP
						+dt
						+Constance.RECORD_FILE_EXT);
				} catch (Exception e) {
					logger.error(e);
				}
			}
			if(ck_tracks.isSelected()) {
				try {
					elTrackFile = new File(filePath
						+ELT
						+dt
						+Constance.RECORD_FILE_EXT);
				} catch (Exception e) {
					logger.error(e);
				}

			}
		}
		if(ck_azimuth.isSelected()) {
			if(ck_plots.isSelected()) {
				try {
					azPlotFile = new File(filePath
						+AZP
						+dt
						+Constance.RECORD_FILE_EXT);
				} catch (Exception e) {
					logger.error(e);
				}
			}
			if(ck_tracks.isSelected()) {
				try {
				azTrackFile = new File(filePath
						+AZT
						+dt
						+Constance.RECORD_FILE_EXT);
				} catch (Exception e) {
					logger.error(e);
				}

			}
		}
	}

	private void writeFiles() {

		Thread writeThread = new Thread(new Runnable() {

			@Override
			public void run() {
//				showProgressLoading(true);
				
				//Write Record duration onto File
				int recordTimeSeconds = 0;
				if(recInf.isSelected()) {
					recordTimeSeconds = Constance.RECORD_WRITE_INTERVAL*60;// (int) (((hourTime * 60 + minTime) * 60) + secTime);
				} else {
					recordTimeSeconds = (int) (inputMinutes *60 + inputSeconds);
				}
				ByteBuffer b = ByteBuffer.allocate(4);
				b.putInt(recordTimeSeconds);
				final byte[] recordTimeByte = b.array();

				//Write to DB
				if(ck_azimuth.isSelected() && ck_tracks.isSelected()) {
//					
					BlockingQueue<LiquidStream> consumeAzTrack = AppConfig.getInstance().getFxmlController().getTaskObserver().getAzTrackRecordQ();
					if(consumeAzTrack!=null) {
						try {
							int until = consumeAzTrack.size();
							logger.info("Start Writing Az Track: "+until);
							final FileOutputStream azTrackWriter = new FileOutputStream(azTrackFile,true);
							final FileChannel azTrackch = azTrackWriter.getChannel();
							azTrackch.write(ByteBuffer.wrap(recordTimeByte));
							for(int i=0;i<until;i++) {
								LiquidStream lStream = consumeAzTrack.take();
								byte[] mData = lStream.getAsterixBuffer().array();
								azTrackch.write(ByteBuffer.wrap(AppUtils.subBytes(mData, 0, AzimuthPlaneTrackMsg.MSG_SIZE)));
							}
							azTrackch.close();
							azTrackWriter.close();
							logger.info("Finished Writing Az Tracks: "+until);
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}

				if(ck_azimuth.isSelected() && ck_plots.isSelected()) {
					
					BlockingQueue<LiquidStream> consumeAzPlot = AppConfig.getInstance().getFxmlController().getTaskObserver().getAzPlotRecordQ();
					if(consumeAzPlot!=null) {
						try {
							int until = consumeAzPlot.size();
							logger.info("Start Writing Az Plots: "+until);
							final FileOutputStream azPlotWriter = new FileOutputStream(azPlotFile,true);
							final FileChannel azPlotch = azPlotWriter.getChannel();
							azPlotch.write(ByteBuffer.wrap(recordTimeByte));
							for(int i=0;i<until;i++) {
								LiquidStream lStream = consumeAzPlot.take();
								byte[] mData = lStream.getAsterixBuffer().array();
								ByteBuffer bb = ByteBuffer.wrap(mData);
								bb.order(ByteOrder.LITTLE_ENDIAN);
								int index = 0;
								//int messageHeader = (int)bb.getInt(index);index += BYTES_PER_INT;
								int plotCount = (short)bb.getShort(index);
								azPlotch.write(ByteBuffer.wrap(AppUtils.subBytes(mData, 0, AzimuthPlanePlotsPerCPIMsg.MSG_SIZE+plotCount*AzimuthPlaneDetectionPlotMsg.MSG_SIZE)));
								bb.clear();
							}
							azPlotch.close();
							azPlotWriter.close();
							logger.info("Finished Writing Az Plots: "+until);
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}

				if(ck_elevation.isSelected() && ck_tracks.isSelected()) {
					
					BlockingQueue<LiquidStream> consumeElTrack = AppConfig.getInstance().getFxmlController().getTaskObserver().getElTrackRecordQ();
					if(consumeElTrack!=null) {
						try {
							int until = consumeElTrack.size();
							logger.info("Start Writing El Track: "+until);
							final FileOutputStream elTrackWriter = new FileOutputStream(elTrackFile,true);
							final FileChannel elTrackch = elTrackWriter.getChannel();
							elTrackch.write(ByteBuffer.wrap(recordTimeByte));
							for(int i=0;i<until;i++) {
								LiquidStream lStream = consumeElTrack.take();
								byte[] mData = lStream.getAsterixBuffer().array();
								elTrackch.write(ByteBuffer.wrap(AppUtils.subBytes(mData, 0, ElevationPlaneTrackMsg.MSG_SIZE)));
							}
							elTrackch.close();
							elTrackWriter.close();
							logger.info("Finished Writing El Tracks: "+until);
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}

				if(ck_elevation.isSelected() && ck_plots.isSelected()) {
					BlockingQueue<LiquidStream> consumeElPlot = AppConfig.getInstance().getFxmlController().getTaskObserver().getElPlotRecordQ();
					if(consumeElPlot!=null) {
						try {
							int until = consumeElPlot.size();
							logger.info("Start Writing El Plot: "+until);
							final FileOutputStream elPlotWriter = new FileOutputStream(elPlotFile,true);
							final FileChannel elPlotch = elPlotWriter.getChannel();
							elPlotch.write(ByteBuffer.wrap(recordTimeByte));
							for(int i=0;i<until;i++) {
								LiquidStream lStream = consumeElPlot.take();
								byte[] mData = lStream.getAsterixBuffer().array();
								ByteBuffer bb = ByteBuffer.wrap(mData);
								bb.order(ByteOrder.LITTLE_ENDIAN);
								int index = 0;
								//int messageHeader = (int)bb.getInt(index);index += BYTES_PER_INT;
								int plotCount = (short)bb.getShort(index);
								elPlotch.write(ByteBuffer.wrap(AppUtils.subBytes(mData, 0, AzimuthPlanePlotsPerCPIMsg.MSG_SIZE+plotCount*AzimuthPlaneDetectionPlotMsg.MSG_SIZE)));
								bb.clear();
							}
							elPlotch.close();
							elPlotWriter.close();
							logger.info("Finished Writing El Plot: "+until);
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}

				//clean DB
				if(DBRecord.getInstance().clearRecordDB())
					logger.info("DB Cleared");

			}
		});
		writeThread.start();
	}

	private void readToDB(String str) {
		Thread loadDB = new Thread(new Runnable() {

			@Override
			public void run() {
				//show progress
				showProgressLoading(true);
				
				//clean DB
				if(DBRecord.getInstance().clearRecordDB())
					logger.info("DB Cleared");
				Constance.IS_REPLAY_SETUP = true;
				
				//Read recorded time
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(selectedFile);
				} catch (FileNotFoundException e) {
					logger.error(e);
				}
				int itemCounter = 0;
				int recordTimeRead = 0;
				byte[] recordTime = new byte[4];
				try {
					if(fis.read(recordTime)>0) {
						ByteBuffer bb = ByteBuffer.wrap(recordTime);
						recordTimeRead = bb.getInt(0);
						logger.info("File Replay Time: "+recordTimeRead+" (s)");
					}
				} catch (IOException e1) {
					logger.error(e1);
				}

				switch (str) {
				case ELP:
					while(true) {
						try {
							byte[] subArray = new byte[ElevationPlanePlotsPerCPIMsg.MSG_SIZE];
							if(fis.read(subArray)>0) {
								++itemCounter;
								ByteBuffer bb = ByteBuffer.wrap(subArray);
								bb.order(ByteOrder.LITTLE_ENDIAN);

								int index = 0;
								bb.getInt(index);index += 4;
								short plotCount = (short)bb.getShort(index);

								byte[] subPlotArray = new byte[ElevationPlaneDetectionPlotMsg.MSG_SIZE*plotCount];
								fis.read(subPlotArray);

								ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
								outputStream.write( subArray );
								outputStream.write( subPlotArray );

								byte outPlotArray[] = outputStream.toByteArray( );

								DBRecord.getInstance().writeElPlotToDB(outPlotArray);
							} else
								break;
						} catch (IOException e) {
							logger.error(e);
						}
					}

					//set play speed
					if(itemCounter > 0) {
						Constance.PLAY_SPEED = ((recordTimeRead*1000000)/itemCounter);
						AppConfig.getInstance().getFxmlController().replayELPData();
						logger.info("Replaying...."+ELP+", @: "+Constance.PLAY_SPEED+", Items: "+itemCounter);
					}
					break;

				case ELT:
					while(true) {
						try {
							byte[] subArray = new byte[ElevationPlaneTrackMsg.MSG_SIZE];
							if(fis.read(subArray)>0) {
								++itemCounter;
								DBRecord.getInstance().writeElTrackToDB(subArray);
							} else
								break;
						} catch (IOException e) {
							logger.error(e);
						}
					}

					//set play speed
					if(itemCounter > 0) {
						Constance.PLAY_SPEED = ((recordTimeRead*1000000)/itemCounter);
						AppConfig.getInstance().getFxmlController().replayELTData();
						logger.info("Replaying...."+ELT+", @: "+Constance.PLAY_SPEED+", Items: "+itemCounter);
					}
					break;

				case AZP:
					while(true) {
						try {
							byte[] subArray = new byte[AzimuthPlanePlotsPerCPIMsg.MSG_SIZE];
							if(fis.read(subArray)>0) {
								++itemCounter;
								ByteBuffer bb = ByteBuffer.wrap(subArray);
								bb.order(ByteOrder.LITTLE_ENDIAN);

								int index = 0;
								bb.getInt(index);index += 4;
								short plotCount = (short)bb.getShort(index);

								byte[] subPlotArray = new byte[AzimuthPlaneDetectionPlotMsg.MSG_SIZE*plotCount];
								fis.read(subPlotArray);

								ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
								outputStream.write( subArray );
								outputStream.write( subPlotArray );

								byte outPlotArray[] = outputStream.toByteArray( );

								DBRecord.getInstance().writeAzPlotToDB(outPlotArray);
							} else
								break;
						} catch (IOException e) {
							logger.error(e);
						}
					}
					
					//set play speed
					if(itemCounter > 0) {
						Constance.PLAY_SPEED = ((recordTimeRead*1000000)/itemCounter);
						AppConfig.getInstance().getFxmlController().replayAZPData();
						logger.info("Replaying...."+AZP+", @: "+Constance.PLAY_SPEED+", Items: "+itemCounter);
					}
					break;

				case AZT:
					while(true) {
						try {
							byte[] subArray = new byte[AzimuthPlaneTrackMsg.MSG_SIZE];
							if(fis.read(subArray)>0) {
								++itemCounter;
								DBRecord.getInstance().writeAzTrackToDB(subArray);
							} else
								break;
						} catch (IOException e) {
							logger.error(e);
						}
					}

					//set play speed
					if(itemCounter > 0) {
						Constance.PLAY_SPEED = ((recordTimeRead*1000000)/itemCounter);
						AppConfig.getInstance().getFxmlController().replayAZTData();
						logger.info("Replaying...."+AZT +", @: "+Constance.PLAY_SPEED+", Items: "+itemCounter);
					}
					break;

				default:
					break;
				}

				//close system resources
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(e);
				}
				
				showProgressLoading(false);
			}
		});
		loadDB.start();
	}

	private void showProgressLoading(boolean show) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				pi_loading.setVisible(show);
				if(Constance.IS_REPLAY_SETUP && !show)
					timerTimeline.playFromStart();
			}
		});
	}
	
	public boolean isRecordAzTracks() {
		return ((ck_azimuth.isSelected() && ck_tracks.isSelected()));
	}
	
	public boolean isRecordAzPlots() {
		return ((ck_azimuth.isSelected() && ck_plots.isSelected()));
	}
	
	public boolean isRecordElTracks() {
		return ((ck_elevation.isSelected() && ck_tracks.isSelected()));
	}
	
	public boolean isRecordElPlots() {
		return ((ck_elevation.isSelected() && ck_plots.isSelected()));
	}

}
