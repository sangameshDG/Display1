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
//*H  Created: 16-Jul-2016
//*H
//*H  @author....: $Author: $Suraj
//*H  @date......: $Date: $
//*H  @version...: $Rev: $1.0
//*H  @path......: $URL: $
//*H
//*H============================================================================

package application;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import messages.app.ZoneSuppression;
import model.AppConfig;
import model.DataManager;
import model.ILayoutParam;
import utils.Constance;

public class ZoomController implements Initializable,ILayoutParam{
	
	private static final Logger logger = Logger.getLogger(ZoomController.class);
	
	
	
	@FXML AnchorPane ZoomDialog;
	
	@FXML private MaterialDesignButtonWidget btn_delete;
	@FXML
	CheckBox ch_zone1;
	@FXML
	CheckBox ch_zone2;
	@FXML
	CheckBox ch_zone3;
	@FXML
	CheckBox ch_zone4;
	@FXML
	CheckBox ch_zone5;
	@FXML
	CheckBox ch_zone11;
	@FXML
	CheckBox ch_zone21;
	@FXML
	CheckBox ch_zone31;
	@FXML
	CheckBox ch_zone41;
	@FXML
	CheckBox ch_zone51;
	
	

	
	@FXML TextField z1_Rmin;
	@FXML TextField z1_Rmax;
	@FXML TextField z1_Azmin;
	@FXML TextField z1_Azmax;
	
	@FXML TextField z1_Rmin1;
	@FXML TextField z1_Rmax1;
	@FXML TextField z1_Azmin1;
	@FXML TextField z1_Azmax1;
	
	@FXML TextField z2_Rmin;
	@FXML TextField z2_Rmax;
	@FXML TextField z2_Azmin;
	@FXML TextField z2_Azmax;
	
	@FXML TextField z2_Rmin1;
	@FXML TextField z2_Rmax1;
	@FXML TextField z2_Azmin1;
	@FXML TextField z2_Azmax1;
	
	@FXML TextField z3_Rmin;
	@FXML TextField z3_Rmax;
	@FXML TextField z3_Azmin;
	@FXML TextField z3_Azmax;
	
	@FXML TextField z3_Rmin1;
	@FXML TextField z3_Rmax1;
	@FXML TextField z3_Azmin1;
	@FXML TextField z3_Azmax1;
	
	@FXML TextField z4_Rmin;
	@FXML TextField z4_Rmax;
	@FXML TextField z4_Azmin;
	@FXML TextField z4_Azmax;
	
	@FXML TextField z4_Rmin1;
	@FXML TextField z4_Rmax1;
	@FXML TextField z4_Azmin1;
	@FXML TextField z4_Azmax1;
	
	@FXML TextField z5_Rmin;
	@FXML TextField z5_Rmax;
	@FXML TextField z5_Azmin;
	@FXML TextField z5_Azmax;
	
	@FXML TextField z5_Rmin1;
	@FXML TextField z5_Rmax1;
	@FXML TextField z5_Azmin1;
	@FXML TextField z5_Azmax1;
	
	
	
	
	
	List<String> al=new ArrayList<String>();
	DecimalFormat df = new DecimalFormat("#.000");
	int indexDelete;
	int iD;
		
	AppConfig appConfig = AppConfig.getInstance();
	DataManager dataObserver = DataManager.getInstance();
	
	double initialX,initialY;
	boolean refresh = false;

	@Override
	public void draw(GraphicsContext gc) {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		logger.info("Zoom Dialog Opened");
		addDraggableNode(ZoomDialog);
		initDialog();
		System.out.println("Level 1");
		updateUI();
	}	
	
	@FXML
	protected void cancelClick(ActionEvent event) {
		//cleanUp();
		closeSettings(event);
		
	}
	
	@FXML
	protected void onDeleteZone(ActionEvent event) {
		if(AppConfig.getInstance().isDisplayHasPrevilege()){
		sendDeletZones();
		sendDeleteZoneEl();
		}
		closeSettings(event);
	}
	
	
		private void sendDeletZones() {
		   ZoneSuppression zoneSup1=new ZoneSuppression();
		   //ch_zone1.
		    if((ch_zone1.isSelected())&&(Constance.Zone.zon1Status==1)) {
			    zoneSup1.setMsgId(0x3002);
			    zoneSup1.setZoneID( 1);
				zoneSup1.setRmin((int)(Constance.Zone.z1Rmin*Constance.UNITS.getSendInMeter()));
				zoneSup1.setAzmin((int)(((-Constance.Zone.z1Azmin)+10)*Constance.Config.CRinMeters));
				zoneSup1.setRmax((int)(Constance.Zone.z1Rmax*Constance.UNITS.getSendInMeter()));
				zoneSup1.setAzmax((int)(((-Constance.Zone.z1Azmax)+10)*Constance.Config.CRinMeters));
				
			   AppConfig.getInstance().getFxmlController().sendBytes(zoneSup1.getByteBuffer().array());
			   
			 
		   }
		   
		   if((ch_zone2.isSelected())&&(Constance.Zone.zon2Status==1)) {
			    zoneSup1.setMsgId(0x3002);
			    zoneSup1.setZoneID(2);
			    zoneSup1.setRmin((int)(Constance.Zone.z2Rmin*Constance.UNITS.getSendInMeter()));
				zoneSup1.setAzmin((int)(((-Constance.Zone.z2Azmin)+10)*Constance.Config.CRinMeters));
				zoneSup1.setRmax((int)(Constance.Zone.z2Rmax*Constance.UNITS.getSendInMeter()));
				zoneSup1.setAzmax((int)(((-Constance.Zone.z2Azmax)+10)*Constance.Config.CRinMeters));
				
			    AppConfig.getInstance().getFxmlController().sendBytes(zoneSup1.getByteBuffer().array());
			   
			  
		   }
		   if((ch_zone3.isSelected())&&(Constance.Zone.zon3Status==1)) {
			    zoneSup1.setMsgId(0x3002);
			    zoneSup1.setZoneID(3);		   
			    zoneSup1.setRmin((int)(Constance.Zone.z3Rmin*Constance.UNITS.getSendInMeter()));
				zoneSup1.setAzmin((int)(((-Constance.Zone.z3Azmin)+10)*Constance.Config.CRinMeters));
				zoneSup1.setRmax((int)(Constance.Zone.z3Rmax*Constance.UNITS.getSendInMeter()));
				zoneSup1.setAzmax((int)(((-Constance.Zone.z3Azmax)+10)*Constance.Config.CRinMeters));

			    AppConfig.getInstance().getFxmlController().sendBytes(zoneSup1.getByteBuffer().array());
			   
			  
			   
			   } 
		   
		   if((ch_zone4.isSelected())&&(Constance.Zone.zon4Status==1)) {
			    zoneSup1.setMsgId(0x3002);
			    zoneSup1.setZoneID(4);		   
			    zoneSup1.setRmin((int)(Constance.Zone.z4Rmin*Constance.UNITS.getSendInMeter()));
				zoneSup1.setAzmin((int)(((-Constance.Zone.z4Azmin)+10)*Constance.Config.CRinMeters));
				zoneSup1.setRmax((int)(Constance.Zone.z4Rmax*Constance.UNITS.getSendInMeter()));
				zoneSup1.setAzmax((int)(((-Constance.Zone.z4Azmax)+10)*Constance.Config.CRinMeters));
			   
			    AppConfig.getInstance().getFxmlController().sendBytes(zoneSup1.getByteBuffer().array()); 
		   }
		   if((ch_zone5.isSelected())&&(Constance.Zone.zon5Status==1)) {
			    zoneSup1.setMsgId(0x3002);
			    zoneSup1.setZoneID(5);
			    zoneSup1.setRmin((int)(Constance.Zone.z5Rmin*Constance.UNITS.getSendInMeter()));
				zoneSup1.setAzmin((int)(((-Constance.Zone.z5Azmin)+10)*Constance.Config.CRinMeters));
				zoneSup1.setRmax((int)(Constance.Zone.z5Rmax*Constance.UNITS.getSendInMeter()));
				zoneSup1.setAzmax((int)(((-Constance.Zone.z5Azmax)+10)*Constance.Config.CRinMeters));

			    AppConfig.getInstance().getFxmlController().sendBytes(zoneSup1.getByteBuffer().array());
			     
		   }
		   
		   
		   btn_delete.widgetSetVal(VAL.GREEN);
		  
		
	 }

		private void sendDeleteZoneEl() {
			//System.out.println("------Entered into elevation delete zone------");
			if((ch_zone11.isSelected())&&(Constance.Zone.zon1StatusEl==1)){
				indexDelete=0;
				iD=Constance.Zone.zon1IDEL;
				sendValuesToDelete();
			}
			if((ch_zone21.isSelected())&&(Constance.Zone.zon2StatusEl==1)){
				indexDelete=4;
				iD=Constance.Zone.zon2IDEL;
				sendValuesToDelete();
			}
			if((ch_zone31.isSelected())&&(Constance.Zone.zon3StatusEl==1)){
				indexDelete=8;
				iD=Constance.Zone.zon3IDEL;
				sendValuesToDelete();
			}
			if((ch_zone41.isSelected())&&(Constance.Zone.zon4StatusEl==1)){
				indexDelete=12;
				iD=Constance.Zone.zon4IDEL;
				sendValuesToDelete();
			}
			if((ch_zone51.isSelected())&&(Constance.Zone.zon5StatusEl==1)){
				indexDelete=16;
				iD=Constance.Zone.zon5IDEL;
				sendValuesToDelete();
			}   
		}
	
		public void sendValuesToDelete() {
			ZoneSuppression zoneSup1=new ZoneSuppression();
			    zoneSup1.setMsgId(0x3012);
			    zoneSup1.setZoneID(iD);
			    zoneSup1.setRmin((int)(Constance.elZone1[indexDelete]*Constance.UNITS.getSendInMeter()));indexDelete=indexDelete+2;
				zoneSup1.setAzmin((int)(Constance.elZone1[indexDelete]*Constance.Config.CRinMeters));indexDelete=indexDelete-1;
				zoneSup1.setRmax((int)(Constance.elZone1[indexDelete]*Constance.UNITS.getSendInMeter()));indexDelete=indexDelete+2;
				zoneSup1.setAzmax((int)(Constance.elZone1[indexDelete]*Constance.Config.CRinMeters));
			 /*   System.out.println("----ZoneID deleted is--------"+iD);
			    zoneSup1.setRmin((int)(Constance.elZone1[indexDelete]*Constance.UNITS.getSendInMeter()));
			    System.out.println("-----RminValue Deleted is-------"+Constance.elZone1[indexDelete]);
			    indexDelete=indexDelete+2;
				zoneSup1.setAzmin((int)(Constance.elZone1[indexDelete]*Constance.Config.CRinMeters));
				System.out.println("-----AzminValue Deleted is-------"+Constance.elZone1[indexDelete]);
				indexDelete=indexDelete-1;
				zoneSup1.setRmax((int)(Constance.elZone1[indexDelete]*Constance.UNITS.getSendInMeter()));
				System.out.println("-----RmaxValue Deleted is-------"+Constance.elZone1[indexDelete]);
				indexDelete=indexDelete+2;
				zoneSup1.setAzmax((int)(Constance.elZone1[indexDelete]*Constance.Config.CRinMeters));
				System.out.println("-----AzmaxValue Deleted is-------"+Constance.elZone1[indexDelete]);*/

			    AppConfig.getInstance().getFxmlController().sendBytes(zoneSup1.getByteBuffer().array());	
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
		
		Constance.IS_ZOOM_DIALOG = true;
		
		
		ch_zone1.setSelected(false);
		ch_zone2.setSelected(false);
		ch_zone3.setSelected(false);
		ch_zone4.setSelected(false);
		ch_zone5.setSelected(false);
		
	}
	private void closeSettings(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
	    logger.info("Zoom Dialog Closed");	
	}

	
    public void updateUI() {
		
	    System.out.println("Entered");	
	    	
			int i;	
		    for( i=1;i<=5;i=i+1){
			
		       if(i==1){
		    
		    	String zRmin=df.format(Constance.Zone.z1Rmin);
				String zRmax=df.format(Constance.Zone.z1Rmax);
				String zAzmin=df.format(-Constance.Zone.z1Azmin);
				String zAzmax=df.format(-Constance.Zone.z1Azmax);
				
		    	    if(Constance.Zone.zon1Status==1){
		                z1_Rmin.setText(zRmin);
		                z1_Rmax.setText(zRmax);
		                z1_Azmin.setText(zAzmin);
		                z1_Azmax.setText(zAzmax);
		    	      }
		    	      else
		    	     {
		    	        z1_Rmin.setText("");
				        z1_Rmax.setText("");
				        z1_Azmin.setText("");
				        z1_Azmax.setText("");
		    	     }
		    	    if(Constance.Zone.zon1StatusEl==1){
		                z1_Rmin1.setText(String.valueOf(df.format(Constance.elZone1[0])));
		                z1_Rmax1.setText(String.valueOf(df.format(Constance.elZone1[1])));
		                z1_Azmin1.setText(String.valueOf(df.format(Constance.elZone1[2])));
		                z1_Azmax1.setText(String.valueOf(df.format(Constance.elZone1[3])));
		    	    	
		    	    	
		    	      }
		    	      else
		    	     {
		    	        z1_Rmin1.setText("");
				        z1_Rmax1.setText("");
				        z1_Azmin1.setText("");
				        z1_Azmax1.setText("");
		    	     }
		    	    
		    	
		      }
		       else if(i==2){
		    	
		    	String zRmin=df.format(Constance.Zone.z2Rmin);
				String zRmax=df.format(Constance.Zone.z2Rmax);
				String zAzmin=df.format(-Constance.Zone.z2Azmin);
				String zAzmax=df.format(-Constance.Zone.z2Azmax);
				
			/*	String zRmin1=df.format(Constance.elZone1[4]);
				String zRmax1=df.format(Constance.elZone1[5]);
				String zElmin=df.format(Constance.elZone1[6]);
				String zElmax=df.format(Constance.elZone1[7]);*/
				 if(Constance.Zone.zon2Status==1){
		                z2_Rmin.setText(zRmin);
		                z2_Rmax.setText(zRmax);
		                z2_Azmin.setText(zAzmin);
		                z2_Azmax.setText(zAzmax);
		    	      }
		    	      else
		    	     {
		    	        z2_Rmin.setText("");
				        z2_Rmax.setText("");
				        z2_Azmin.setText("");
				        z2_Azmax.setText("");
		    	     }
				 if(Constance.Zone.zon2StatusEl==1){
					    z2_Rmin1.setText(String.valueOf(df.format(Constance.elZone1[4])));
		                z2_Rmax1.setText(String.valueOf(df.format(Constance.elZone1[5])));
		                z2_Azmin1.setText(String.valueOf(df.format(Constance.elZone1[6])));
		                z2_Azmax1.setText(String.valueOf(df.format(Constance.elZone1[7])));
		    	      }
		    	      else
		    	     {
		    	        z2_Rmin1.setText("");
				        z2_Rmax1.setText("");
				        z2_Azmin1.setText("");
				        z2_Azmax1.setText("");
		    	     }
		    } 
		       else if(i==3){
		    	
		    	String zRmin=df.format(Constance.Zone.z3Rmin);
				String zRmax=df.format(Constance.Zone.z3Rmax);
				String zAzmin=df.format(-Constance.Zone.z3Azmin);
				String zAzmax=df.format(-Constance.Zone.z3Azmax);
				
				/*String zRmin1=df.format(Constance.elZone1[8]);
				String zRmax1=df.format(Constance.elZone1[9]);
				String zElmin=df.format(Constance.elZone1[10]);
				String zElmax=df.format(Constance.elZone1[11]);*/
				
				 if(Constance.Zone.zon3Status==1){
		                z3_Rmin.setText(zRmin);
		                z3_Rmax.setText(zRmax);
		                z3_Azmin.setText(zAzmin);
		                z3_Azmax.setText(zAzmax);
		    	      }
		    	      else
		    	     {
		    	        z3_Rmin.setText("");
				        z3_Rmax.setText("");
				        z3_Azmin.setText("");
				        z3_Azmax.setText("");
		    	     }
				 if(Constance.Zone.zon3StatusEl==1){
					    z3_Rmin1.setText(String.valueOf(df.format(Constance.elZone1[8])));
		                z3_Rmax1.setText(String.valueOf(df.format(Constance.elZone1[9])));
		                z3_Azmin1.setText(String.valueOf(df.format(Constance.elZone1[10])));
		                z3_Azmax1.setText(String.valueOf(df.format(Constance.elZone1[11])));
		    	      }
		    	      else
		    	     {
		    	        z3_Rmin1.setText("");
				        z3_Rmax1.setText("");
				        z3_Azmin1.setText("");
				        z3_Azmax1.setText("");
		    	     }
		    }
		    else if(i==4){
		    	
		    	String zRmin=df.format(Constance.Zone.z4Rmin);
				String zRmax=df.format(Constance.Zone.z4Rmax);
				String zAzmin=df.format(-Constance.Zone.z4Azmin);
				String zAzmax=df.format(-Constance.Zone.z4Azmax);
				
				 if(Constance.Zone.zon4Status==1){
		                z4_Rmin.setText(zRmin);
		                z4_Rmax.setText(zRmax);
		                z4_Azmin.setText(zAzmin);
		                z4_Azmax.setText(zAzmax);
		    	      }
		    	      else
		    	     {
		    	        z4_Rmin.setText("");
				        z4_Rmax.setText("");
				        z4_Azmin.setText("");
				        z4_Azmax.setText("");
		    	     }
				 if(Constance.Zone.zon4StatusEl==1){
					    z4_Rmin1.setText(String.valueOf(df.format(Constance.elZone1[12])));
		                z4_Rmax1.setText(String.valueOf(df.format(Constance.elZone1[13])));
		                z4_Azmin1.setText(String.valueOf(df.format(Constance.elZone1[14])));
		                z4_Azmax1.setText(String.valueOf(df.format(Constance.elZone1[15])));
		    	      }
		    	      else
		    	     {
		    	        z4_Rmin1.setText("");
				        z4_Rmax1.setText("");
				        z4_Azmin1.setText("");
				        z4_Azmax1.setText("");
		    	     }
		    } else if(i==5){
		    	
		    	
		    	String zRmin=df.format(Constance.Zone.z5Rmin);
				String zRmax=df.format(Constance.Zone.z5Rmax);
				String zAzmin=df.format(-Constance.Zone.z5Azmin);
				String zAzmax=df.format(-Constance.Zone.z5Azmax);
				
				
				 if(Constance.Zone.zon5Status==1){
		                z5_Rmin.setText(zRmin);
		                z5_Rmax.setText(zRmax);
		                z5_Azmin.setText(zAzmin);
		                z5_Azmax.setText(zAzmax);
		    	      }
		    	      else
		    	     {
		    	        z5_Rmin.setText("");
				        z5_Rmax.setText("");
				        z5_Azmin.setText("");
				        z5_Azmax.setText("");
		    	     }
				 if(Constance.Zone.zon5StatusEl==1){
					    z5_Rmin1.setText(String.valueOf(df.format(Constance.elZone1[16])));
		                z5_Rmax1.setText(String.valueOf(df.format(Constance.elZone1[17])));
		                z5_Azmin1.setText(String.valueOf(df.format(Constance.elZone1[18])));
		                z5_Azmax1.setText(String.valueOf(df.format(Constance.elZone1[19])));
		    	      }
		    	      else
		    	     {
		    	        z5_Rmin1.setText("");
				        z5_Rmax1.setText("");
				        z5_Azmin1.setText("");
				        z5_Azmax1.setText("");
		    	     }
		    }
		}
	    	
    }
    
}
    
   /* public void updateZoneActivity(ZoneSuppression zoneSup1){
    	if(zoneSup1!=null){
    	int setVal=zoneSup1.getZoneID();
    	float mul = 0.0f;
    	if(setVal==1){
    		z1_Rmin.setText(String.valueOf(zoneSup1.getRmin()*mul));
		    z1_Rmax.setText(String.valueOf(zoneSup1.getRmax()*mul));
		    z1_Azmin.setText(String.valueOf(zoneSup1.getAzmin()*mul));
		    z1_Azmax.setText(String.valueOf(zoneSup1.getAzmax()*mul));	
    	}else if(setVal==2){
    		
    		Constance.Zone.z2Rmin=zoneSup1.getRmin();
    		Constance.Zone.z2Rmax=zoneSup1.getRmax();
    		String zRmin=df.format(Constance.Zone.z2Rmin);
			String zRmax=df.format(Constance.Zone.z2Rmax);
			
			z2_Rmin.setText(zRmin);
		    z2_Rmax.setText(zRmax);
    		/*z2_Rmin.setText(String.valueOf(zoneSup1.getRmin()*mul));
		    z2_Rmax.setText(String.valueOf(zoneSup1.getRmax()*mul));
		    z2_Azmin.setText(String.valueOf(zoneSup1.getAzmin()*mul));
		    z2_Azmax.setText(String.valueOf(zoneSup1.getAzmax()*mul));*/
    		// }  
       // }
    //}*/
  	
