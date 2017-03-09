package application;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

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
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import messages.radar.ExRxHealthStatusMsg;
import messages.radar.NoiseFigureStatusMsg;
import model.AppConfig;
import model.ILayoutParam;

import org.apache.log4j.Logger;

public class ExRxSetUpController implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(ExRxSetUpController.class);
	
	@FXML AnchorPane ExRxSetup;	

	@FXML TextField ps_n5v;
	@FXML TextField ps_p5v;
	@FXML TextField ps_n12v;
	@FXML TextField ps_p12v;
	
	@FXML TextField ex_az_lo1;
	@FXML TextField ex_az_lo2;
	@FXML TextField ex_az_adc;
	@FXML TextField ex_az_wfg;
	@FXML TextField ex_az_tx;
	
	@FXML TextField ex_el_lo1;
	@FXML TextField ex_el_lo2;
	@FXML TextField ex_el_adc;
	@FXML TextField ex_el_wfg;
	@FXML TextField ex_el_tx;
	
	@FXML TextField rx_if_az;
	@FXML TextField rx_if_el;
	@FXML TextField rx_if_cal;
	
	@FXML TextField rx_rf_az;
	@FXML TextField rx_rf_el;
	@FXML TextField rx_rf_cal;
	
	@FXML TextField nf_db;
	
	double initialX,initialY;
	
	public void updateData() {
		ExRxHealthStatusMsg exrx = AppConfig.getInstance().getExrx();
		if(exrx!=null) {
			FXMLController.setTextField(getColor(exrx.isIs_ps_n5v()), ps_n5v);
			FXMLController.setTextField(getColor(exrx.isIs_ps_p5v()), ps_p5v);
			FXMLController.setTextField(getColor(exrx.isIs_ps_n12v()), ps_n12v);
			FXMLController.setTextField(getColor(exrx.isIs_ps_p12v()), ps_p12v);
			
			FXMLController.setTextField(getColor(exrx.isIs_rx_if_az()), rx_if_az);
			FXMLController.setTextField(getColor(exrx.isIs_rx_if_el()), rx_if_el);
			FXMLController.setTextField(getColor(exrx.isIs_rx_if_cal()), rx_if_cal);
			FXMLController.setTextField(getColor(exrx.isIs_rx_rf_az()), rx_rf_az);
			FXMLController.setTextField(getColor(exrx.isIs_rx_rf_el()), rx_rf_el);
			FXMLController.setTextField(getColor(exrx.isIs_rx_rf_cal()), rx_rf_cal);
			
			FXMLController.setTextField(getColor(exrx.isIs_ex_az_lo2()), ex_az_lo2);
			FXMLController.setTextField(getColor(exrx.isIs_ex_az_adc()), ex_az_adc);
			FXMLController.setTextField(getColor(exrx.isIs_ex_az_wfg()), ex_az_wfg);
			FXMLController.setTextField(getColor(exrx.isIs_ex_az_lo1()), ex_az_lo1);
			FXMLController.setTextField(getColor(exrx.isIs_ex_az_tx()), ex_az_tx);
			
			FXMLController.setTextField(getColor(exrx.isIs_ex_el_lo2()), ex_el_lo2);
			FXMLController.setTextField(getColor(exrx.isIs_ex_el_adc()), ex_el_adc);
			FXMLController.setTextField(getColor(exrx.isIs_ex_el_wfg()), ex_el_wfg);
			FXMLController.setTextField(getColor(exrx.isIs_ex_el_lo1()), ex_el_lo1);
			FXMLController.setTextField(getColor(exrx.isIs_ex_el_tx()), ex_el_tx);
		}
		

		NoiseFigureStatusMsg nf = AppConfig.getInstance().getnf();
		if(nf!=null) {
			DecimalFormat df = new DecimalFormat("0.000");
			nf_db.setText(df.format(nf.getNoiseFig()*0.001)+" db");
		}
	}

	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("ExRx Setup Dialog Opened");
		addDraggableNode(ExRxSetup);
		initDialog();
	}	

	@FXML
	protected void cancelClick(ActionEvent event) {
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
		VAL val = VAL.DEFAULT;
		FXMLController.setTextField(val, ps_n5v);
		FXMLController.setTextField(val, ps_p5v);
		FXMLController.setTextField(val, ps_n12v);
		FXMLController.setTextField(val, ps_p12v);
		
		FXMLController.setTextField(val, rx_if_az);
		FXMLController.setTextField(val, rx_if_el);
		FXMLController.setTextField(val, rx_if_cal);
		FXMLController.setTextField(val, rx_rf_az);
		FXMLController.setTextField(val, rx_rf_el);
		FXMLController.setTextField(val, rx_rf_cal);
		
		FXMLController.setTextField(val, ex_az_lo2);
		FXMLController.setTextField(val, ex_az_adc);
		FXMLController.setTextField(val, ex_az_wfg);
		FXMLController.setTextField(val, ex_az_lo1);
		FXMLController.setTextField(val, ex_az_tx);
		
		FXMLController.setTextField(val, ex_el_lo2);
		FXMLController.setTextField(val, ex_el_adc);
		FXMLController.setTextField(val, ex_el_wfg);
		FXMLController.setTextField(val, ex_el_lo1);
		FXMLController.setTextField(val, ex_el_tx);
		
		FXMLController.setTextField(val, nf_db);
	}

	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("ExRx Setup Dialog Closed");
	}
	
	private VAL getColor(boolean boo) {
		return (boo?VAL.GREEN:VAL.RED);
	}

}