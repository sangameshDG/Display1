package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import admin.MasterSlavery;

public class Constance {
	
	public static String GPS_TIME = "?";
	public static String LATITUDE = "?";
	public static String LONGITUDE = "?";
	public static String WIND_SPEED = "?";
	public static String WIND_DIR = "?";
	public static String SCAN_EL = "?";
	public static String SCAN_AZ = "?";
	public static String getCONTROL(){ return MasterSlavery.getInstance().isMaster()? "MASTER": "SLAVE";}
	public static String SCALE = null;
	public static boolean isMSL = false;
	public static boolean isQFE = true;
	public static boolean isQNH = false;
	public static boolean isArea = false;
	public static boolean IS_ZOOM_DIALOG 	= true;
	
	
	public static final class UNITS {
		public static boolean isKM =false;
		public static boolean isLogged=true;
		public static final String DEGREES = "°";
		public static final double getFACTOR_LENGTH() {return (isKM ? 1:0.54);};//KM to NM
		public static final double getFACTOR_LENGTH2() {return (isKM ? 1:1);};
		public static final double getFACTOR_HEIGHT() {return (isKM ? 1:3.2808);};//MTS to FT
		public static final double getFACTOR_HEIGHTFIXED() {return (isKM ? 3.2808:3.2808);};//MTS to FT
		public static String getLENGTH() { return(isKM ? "KM":"NM");}
		public static String getHEIGHT() { return(isKM ? "m":"ft");}
		public static final double getSendInMeter(){return(isKM? 1000:2000);};
		public static final double getRecieveUnits(){return(isKM? 0.001:0.000536);};
		public static final double aZimuth = 0.001;
		public static final double SpeedUnit(){return(isKM? 3.5:1.9483);};
		public static final double getR_PX(){return(isKM? 1:1.852);};
		public static final double TDPzero(){return (isLogged ? -TOUCH_DOWN*getFACTOR_LENGTH():0);};
		public static final double FT_TO_KM = 0.000305;
		public static final double FT_TO_METER = 0.3048;
		public static final double NM_TO_KM = 1.852;
		public static final double METER_TO_NM = 0.000536;
		public static final int LOG_OPT_VALUE = 5;
		
	}

	public static boolean IS_LOGGING_SETUP 	= false;
	public static boolean IS_REFRESH_SETUP 	= false;
	public static boolean IS_RECORD_SETUP 	= false;
	public static boolean IS_REPLAY_SETUP 	= false;
	public static boolean IS_PAUSE_SETUP 	= false;
	public static int RECORD_WRITE_INTERVAL	= 10;// in minutes

	public static boolean IS_DISPLAY_SETUP 	= true;
	public static boolean IS_SYSTEM_SETUP 	= true;
	public static boolean IS_RADAR_SETUP 	= true;

	public static boolean SHOW_RAW 			= true;
	public static boolean SHOW_PLOT 		= true;
	public static boolean SHOW_PLOT_LABEL	= false;

	public static boolean SHOW_TRACK 		= true;
	public static int SHOW_TRACK_NO			= -1;
	public static boolean SHOW_TRACK_LABEL 	= true;
	public static boolean SHOW_TRACK_PATH	= false;
	public static final int SHOW_TRACK_LIMIT= 50;
	
	public static boolean SHOW_RANGE_MARKS	= true;
	

	public static double ELEVATION_MAX		= 10;//deg
	public static double ELEVATION_MIN		= 0;//ft or mts
	public static double AZIMUTH_MAX		= 10;//deg
	public static double AZIMUTH_MIN		= -AZIMUTH_MAX;//deg
	public static double RANGE_MAX			=(30);//km or NM
	public static double RANGE_DISP			= 1;//km or NM
	public static double RANGE_MIN			= 0;//km or NM
	public static double TOUCH_DOWN			= 1.120;//km or NM
	public static double START_RANGE		= 0;//km or NM
	public static int    ElEVATION_MUL      = 2;
	
	public static final int RAW_AZ_BEAMS				= 41;//20degress max
	public static final double RAW_AZ_BEAM_ANGLE_OFFSET	= 0.4878048780487805;//20/41
	public static final int RAW_EL_BEAMS				= 21;//10degrees max
	public static final double RAW_EL_BEAM_ANGLE_OFFSET	= 0.4761904761904762;//10/21
	public static final double RAW_EACH_RANGE_CELL		= 0.03;//In KM

	public static class ELEVATION {
		public static double GLIDE_ANGLE			= 3.0;//degrees
		public static double GLIDE_MAX_DIST			= 16;//NM
		public static double GLIDE_FLAT_START_DIST	= 15;//NM
		public static double USL_ANGLE				= 10;//degrees
		public static double LSL_ANGLE				= 0;//degrees
		public static double UAL_ANGLE				= GLIDE_ANGLE+1;//degrees
		public static double LAL_ANGLE				= GLIDE_ANGLE-1;//degrees
		public static double USaL_ANGLE				= GLIDE_ANGLE+2.5;//degrees
		public static double LSaL_ANGLE				= GLIDE_ANGLE-2.5;//degrees
		public static double DH						= 100; //KM
		public static double TILT;
		public static double GLIDE_ANGLE1            = 3.0;
	}

	public static class AZIMUTH {
		public static double LSL_ANGLE	= AZIMUTH_MAX;//degrees
		public static double RSL_ANGLE	= -AZIMUTH_MAX;//degrees
		public static double RCLO		= 0.153;//KM
		public static double LAL_ANGLE	= 4.5;//degrees
		public static double RAL_ANGLE	=-4.5;//degrees
		public static double LSaL		= 3;//degress
		public static double RSaL		=-3;//degress
		public static double AXIS;
		public static double TILT;
		public static double RCLO_PX;//pixels
	}

	public static class PREF {
		public static int NO_RUNWAY			= 4;//Number
		public static String SEL_RUNWAY		= " 1 ";//String Number
		public static String RANGE_UNITS	= " Kilometers (KM) ";
		public static String RANGE_UNITS_2	= " Logarithamic  ";
		public static String LEFT_RIGHT		= "Left";
	}

	public static class OPPMODEPARAM {
		public static byte OP_MODE=0;
		public static byte OP_FREQ=11;
		public static byte OP_POL=0;
	}
	
	public static class SPConfig {
		
		public static short MTItype=0 ;
		public static short AMTi=0;
		public static byte ClutterMap=0;
		public static String CFarType="CA" ;
		public static short CFarThreshold=2;	
		
	}
	
	
   public static class SafetyParam {
		
		public static double FirstLineEnd=3.704 ;  //KM
		public static double SecondLineEnd=9.26;   //KM
		public static double ThirdLineEnd=30.26;   //KM
		
		public static double DH1=1.852; // In KM
		public static double DH2=9.5378; // In KM
		public static double DH3=20.372; // In KM
		
		public static double FirstSetHeight=15.24; // In meters
		public static double SecondSetHeight=30.48; // In meters
		public static double ThirdSetHeight=152.4; // In meters
		
		public static double FirstSetLR=0.03;   // KM
		public static double SecondSetLR=0.152; //KM
		public static double ThirdSetLR=0.304;  // KM
		
		public static double QNH=0.152; //KM
		public static double QFE=0.304;  // KM
	
	}
	
	public static boolean IS_CLOSE			= false;
	public static boolean TCPIP 			= false;
	public static boolean UDPIP				= false;
	public static boolean MCUDP 			= true;
	public static boolean IS_CONNECTED		= false;
	public static boolean CornerSet=false;

	public static final int MY_TIMEOUT 		= 60*1000;
	public static final String SERVER_IP 	= "127.0.0.1";
	public static int PORT_AZ_PLOTS			= 7700;
	public static int PORT_EL_PLOTS			= 5500;
	public static int PORT_AZ_TRACKS		= 8800;
	public static int PORT_EL_TRACKS		= 6600;
	public static int PORT_VIDEO			= 9900;
	public static int PORT_STATUS			= 4400;
	public static int PORT_WRITE			= 8820; //13001;
	public static int PORT_DISPLAY_WRITE	= 14001;
	public static int PORT_RRM_WRITE		= 9876;
	public static int PORT_RC_WRITE			= 8765;
	public static String IP_RRM				= "192.168.1.110";
	public static String IP_RC_1			= SERVER_IP;//"192.168.1.8";
	public static String IP_RC_2			= "192.168.1.68";
    public static String GROUP_ADDR 		= "225.225.0.1";
    public static final int DGRAM_LEN 		= 2048;
	public static final int MILLI_SECOND 	= 1000;
	public static final int UPDATE_RATE 	= MILLI_SECOND;
	public static final String CURSOR 		= " > ";
	public static final String CURSOR_DUB	= " >> ";

	public static final String RECORD_DIR_LOC 	= "C:\\FX\\";
	public static final String RECORD_FILE_EXT	= ".bin";
	public static final int MAX_RECORD_TIME	= 60;//minutes
	public static final int NORMAL_SPEED	= 16000;//micro seconds
	public static int PLAY_SPEED			= NORMAL_SPEED;//micro seconds

	public static int VID_MAX_VAL_RX		= 1;
	public static int LOG_STRING_LENGTH		= 50;

	public static int CHANNEL_ID			= -1;
	public static byte DISPLAY_ID 			= 11;
	public static int USERS_LIMIT			= 15;
	public static String USER_MASTER		= "MASTER";
	public static String USER_TECHNICAL		= "Technical";
	public static String USER_CONTROLLER	= "Controller";
	
	public static String GENERAL_MSL		= "0";
	public static double RMIN;
	public static final String CONFIG_FILE_EXT	= ".cfg";
	public static final String REPORT_FILE_EXT	= ".report";
	public static final String REPORT_TEXT_EXT	= ".txt";
	
	
	public static byte InstallStatus=0;
	public static String Plane;
	public static String PlaneVerify;
	public static double CorrectedAzMax=191;
	public static double CorrectedElMax=181;
	public static byte DisplayScale;
	public static byte TailCounter=30;
	

	public static  int ANT_SPEAK_COUNTAZ = 2;
	public static  int ANT_SPEAK_COUNTEL = 2;
	public static int LAN_SPEAK_COUNT = 2;
	public static int EXRX_SPEAK_COUNT = 2;
	public static int SPEAK_TIMES = 4;
	public static int SDP_SPEAK_COUNT =2;
	
	public static class Config {
		public static byte Cr1ID=1;
		public static double CR1Range;
		public static double CR1MinRange;
		public static double CR1Angle;
		public static double CR1MinAngle;
		
		public static byte Cr2ID=2;
		public static double CR2Range;
		public static double CR2MinRange;
		public static double CR2Angle;
		public static double CR2MinAngle;
		
		public static byte Cr3ID=3;
		public static double CR3Range;
		public static double CR3MinRange;
		public static double CR3Angle;
		public static double CR3MinAngle;
		
		public static byte Cr4ID=4;
		public static double CR4Range;
		public static double CR4MinRange;
		public static double CR4Angle;
		public static double CR4MinAngle;
		
		public static byte Cr5ID=5;
		public static double CR5Range;
		public static double CR5MinRange;
		public static double CR5Angle;
		public static double CR5MinAngle;
		
		public static byte Cr6ID=6;
		public static double CR6Range;
		public static double CR6MinRange;
		public static double CR6Angle;
		public static double CR6MinAngle;
		
		public static double CRinMeters=1000;
		public static double CRinNM=0.000539;


	}
	
	public static class Zone{
		
		public static int ZoneID=1;
		public static int ZoneIDEl=1;
		public static int OpenDialog;
		public static double z1Rmin;
		public static double z1Rmax;
		public static double z1Azmin;
		public static double z1Azmax;
		
		public static double z2Rmin=0;
		public static double z2Rmax=0;
		public static double z2Azmin=0;
		public static double z2Azmax=0;
	
		public static double z3Rmin=0;
		public static double z3Rmax=0;
		public static double z3Azmin=0;
		public static double z3Azmax=0;
		
		public static double z4Rmin=0;
		public static double z4Rmax=0;
		public static double z4Azmin=0;
		public static double z4Azmax=0;
		
		public static double z5Rmin=0;
		public static double z5Rmax=0;
		public static double z5Azmin=0;
		public static double z5Azmax=0;
		
		
		public static int zon1Status=0;
		public static int zon2Status=0;
		public static int zon3Status=0;
		public static int zon4Status=0;
		public static int zon5Status=0;
		
		public static int zon1StatusEl=0;
		public static int zon2StatusEl=0;
		public static int zon3StatusEl=0;
		public static int zon4StatusEl=0;
		public static int zon5StatusEl=0;
		
		public static int zon1IDEL=1;
		public static int zon2IDEL=2;
		public static int zon3IDEL=3;
		public static int zon4IDEL=4;
		public static int zon5IDEL=5;
	}
	
        public static	double[] elZone1 = new double[20];
        public static  List<Integer> commonIDlist=new ArrayList<Integer>();
        public static  Map<Integer, String> azRenameListInTrack = new ConcurrentHashMap<Integer, String>();

	
}
