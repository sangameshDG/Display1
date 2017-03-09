package admin;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import utils.Constance;

public class UserPreference {

	private static final Logger logger = Logger.getLogger(UserPreference.class);
	private static UserPreference instance = null;
	
	private Preferences prefs;
	
	protected UserPreference() {
		prefs = Preferences.userRoot().node(this.getClass().getName());
	}
	
	public static UserPreference getInstance() {
	      if(instance == null) {
	         instance = new UserPreference();
	         logger.info("UserPreference Instantiated");
	      }
	      return instance;
	}
	
	private static final String PASS_CODE="PASS_CODE";
	
	private static final String DISPLAY_SCALE="DISPLAY_SCALE";
	
	private static final String TOUCH_DOWN="TOUCH_DOWN";
	
	private static final String EL_GLIDE_ANGLE="EL_GLIDE_ANGLE";
	private static final String EL_GLIDE_MAX_DIST="EL_GLIDE_MAX_DIST";
	private static final String EL_GLIDE_FLAT_START_DIST="EL_GLIDE_FLAT_START_DIST";
	private static final String EL_USL_ANGLE="EL_USL_ANGLE";
	private static final String EL_LSL_ANGLE="EL_LSL_ANGLE";
	private static final String EL_UAL_ANGLE="EL_UAL_ANGLE";
	private static final String EL_LAL_ANGLE="EL_LAL_ANGLE";
	private static final String EL_USaL_ANGLE="EL_USaL_ANGLE";
	private static final String EL_LSaL_ANGLE="EL_LSaL_ANGLE";
	private static final String EL_DH="EL_DH";
	
	private static final String AZ_LSL_ANGLE="AZ_LSL_ANGLE";
	private static final String AZ_RSL_ANGLE="AZ_RSL_ANGLE";
	private static final String AZ_RCLO="AZ_RCLO";
	private static final String AZ_LAL_ANGLE="AZ_LAL_ANGLE";
	private static final String AZ_RAL_ANGLE="AZ_RAL_ANGLE";
	private static final String AZ_LSaL="AZ_LSaL";
	private static final String AZ_RSaL="AZ_RSaL";
	
	private static final String DH_HEIGHT1="DH_HEIGHT1";
	

	public void setDH_HEIGHT1(double val) {
		prefs.putDouble(DH_HEIGHT1,val );
	}

	public double getDhHeight1() {
		return prefs.getDouble(DH_HEIGHT1, Constance.SafetyParam.DH1);
	}
	
	public void setDH_HEIGHT2(double val) {
		prefs.putDouble(DH_HEIGHT2, val);
	}
	
	public double getDhHeight2() {
		return prefs.getDouble(DH_HEIGHT2, Constance.SafetyParam.DH2);
	}

	public void setDH_HEIGHT3(double val) {
		prefs.putDouble(DH_HEIGHT3, val);
	}
	
	public double getDhHeight3() {
		return prefs.getDouble(DH_HEIGHT3, Constance.SafetyParam.DH3);
	}
	
	public void setEL_SAFETY_HEIGHT1(double val) {
		prefs.putDouble(EL_SAFETY_HEIGHT1, val);
	}
	
	public double getElSafetyHeight1() {
		return prefs.getDouble(EL_SAFETY_HEIGHT1, Constance.SafetyParam.FirstSetHeight);
	}

	public void setEL_SAFETY_HEIGHT2(double val) {
		prefs.putDouble(EL_SAFETY_HEIGHT2, val);
	}
	
	public double getElSafetyHeight2() {
		return prefs.getDouble(EL_SAFETY_HEIGHT2, Constance.SafetyParam.SecondSetHeight);
	}

	public void setEL_SAFETY_HEIGHT3(double val) {
		prefs.putDouble(EL_SAFETY_HEIGHT3, val);
	}
	
	public double getElSafetyHeight3() {
		return prefs.getDouble(EL_SAFETY_HEIGHT3, Constance.SafetyParam.ThirdSetHeight);
	}

	public void setAZ_SAFETY_HEIGHT1(double val) {
		prefs.putDouble(AZ_SAFETY_HEIGHT1, val);
	}
	
	public double getAzSafetyHeight1() {
		return prefs.getDouble(AZ_SAFETY_HEIGHT1, Constance.SafetyParam.FirstSetLR);
	}
	
	public void setAZ_SAFETY_HEIGHT2(double val) {
		prefs.putDouble(AZ_SAFETY_HEIGHT2, val);
	}

	public double getAzSafetyHeight2() {
		return prefs.getDouble(AZ_SAFETY_HEIGHT2, Constance.SafetyParam.SecondSetLR);
	}

	public void setAZ_SAFETY_HEIGHT3(double val) {
		prefs.putDouble(AZ_SAFETY_HEIGHT3, val);
	}
	
	public double getAzSafetyHeight3() {
		return prefs.getDouble(AZ_SAFETY_HEIGHT3, Constance.SafetyParam.ThirdSetLR);
	}

	public void setQNH(double val) {
		prefs.putDouble(QNH, val);
	}
	
	public double getQnh() {
		return prefs.getDouble(QNH, Constance.SafetyParam.QNH);
	}

	public void setQFE(double val) {
		prefs.putDouble(QFE, val);
	}
	
	public double getQfe() {
		return prefs.getDouble(QFE, Constance.SafetyParam.QFE);
	}

	private static final String DH_HEIGHT2="DH_HEIGHT2";
	private static final String DH_HEIGHT3="DH_HEIGHT3";
	private static final String EL_SAFETY_HEIGHT1="EL_SAFETY_HEIGHT1";
	private static final String EL_SAFETY_HEIGHT2="EL_SAFETY_HEIGHT2";
	private static final String EL_SAFETY_HEIGHT3="EL_SAFETY_HEIGHT3";
	private static final String AZ_SAFETY_HEIGHT1="AZ_SAFETY_HEIGHT1";
	private static final String AZ_SAFETY_HEIGHT2="AZ_SAFETY_HEIGHT2";
	private static final String AZ_SAFETY_HEIGHT3="AZ_SAFETY_HEIGHT3";
	private static final String QNH="QNH";
	private static final String QFE="QFE";
	
	private static final String PREF_RANGE_UNITS="PREF_RANGE_UNITS";
	private static final String PREF_RANGE_UNITS_2="PREF_RANGE_UNITS_2";
	private static final String DISPLAY_ID="DISPLAY_ID";
	private static final String IP_RRM="IP_RRM";
	private static final String IP_RC_1="IP_RC_1";
	private static final String IP_RC_2="IP_RC_2";
	private static final String GROUP_ADDR="GROUP_ADDR";
	private static final String PORT_AZ_PLOTS="PORT_AZ_PLOTS";
	private static final String PORT_AZ_TRACKS="PORT_AZ_TRACKS";
	private static final String PORT_EL_PLOTS="PORT_EL_PLOTS";
	private static final String PORT_EL_TRACKS="PORT_EL_TRACKS";
	private static final String PORT_VIDEO="PORT_VIDEO";
	private static final String PORT_RC_WRITE="PORT_RC_WRITE";
	private static final String PORT_RRM_WRITE="PORT_RRM_WRITE";
	
	private static final String RECORD_LOC="RECORD_LOC";
	
	private static final String MTI = "MTI";
	private static final String MTI_TYPE = "MTI_TYPE";
	private static final String AMTI = "AMTI";
	private static final String CLUTTER_MAP = "CLUTTER_MAP";
	private static final String CFAR_TYPE = "CFAR_TYPE";
	private static final String CFAR_THRESHOLD = "CFAR_THRESHOLD";
	private static final String CAL_AZ = "CAL_AZ";	
	private static final String CAL_EL = "CAL_EL";	
	
	private static final String BITE = "BITE";
	private static final String AZ_DOP = "AZ_DOP";
	private static final String AZ_ATT = "AZ_ATT";
	private static final String EL_DOP = "EL_DOP";
	private static final String EL_ATT = "EL_ATT";
	private static final String ST_TARGET = "ST_TARGET";	
	private static final String MOV_START_RNG = "MOV_START_RNG";
	private static final String MOV_STOP_RNG = "MOV_STOP_RNG";
	private static final String MOV_STEP_RNG = "MOV_STEP_RNG";
	
	private static final String AZ_MOTOR = "AZ_MOTOR";
	private static final String HOR_MOTOR = "HOR_MOTOR";
	private static final String VER_MOTOR = "VER_MOTOR";
	private static final String AZ_TILT = "AZ_TILT";
	private static final String EL_TILT = "EL_TILT";
	private static final String AZ_ACT_POS = "AZ_ACT_POS";
	private static final String HOR_ACT_POS = "HOR_ACT_POS";
	private static final String VER_ACT_POS = "VER_ACT_POS";
	
	private static final String MSTC_LAW_AZ = "MSTC_LAW_AZ";
	private static final String MSTC_LAW_EL = "MSTC_LAW_EL";
	private static final String AGC_LAW = "AGC_LAW";
	private static final String MAINTAIN_FREQ = "MAINTAIN_FREQ";
	private static final String AZ_AXIS = "AZ_AXIS";
	private static final String MIN_RNG = "MIN_RNG";
	private static final String NORTH_OFF_BIAS = "NORTH_OFF_BIAS";
	private static final String TAILSCAN = "TAIL_SCAN";
	
	
	private static final String OP_MODE = "OP_MODE";
	private static final String OP_MODE_TYPE = "OP_MODE_TYPE";
	private static final String OP_FREQ = "OP_FREQ";
	private static final String OP_SCAN = "OP_SCAN";
	private static final String OP_POLARIZATION = "OP_POLARIZATION";
	private static final String OP_TX = "OP_TX";
	private static final String OP_BITE	= "OP_BITE";
	private static final String OP_RUNWAY = "OP_RUNWAY";
	
	private static final String GENERAL_MSL	= "GENERAL_MSL";

	private static final String RUNWAY_1 = "RUNWAY_1";
	private static final String RUNWAY_2 = "RUNWAY_2";
	private static final String RUNWAY_3 = "RUNWAY_3";
	private static final String RUNWAY_4 = "RUNWAY_4";
	
	public void clearUserPrefs() {
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	public void setPASS_CODE(String val) {
		prefs.put(PASS_CODE, val);
	}
	
	public String getPASS_CODE() {
		return prefs.get(PASS_CODE, "0000");
	}
	
	public void setDISPLAY_SCALE(double val) {
		prefs.putDouble(DISPLAY_SCALE, val);
	}
	
	public double getDISPLAY_SCALE() {
		return prefs.getDouble(DISPLAY_SCALE, 10);
	}
	
	public void setEL_GLIDE_ANGLE(double val) {
		prefs.putDouble(EL_GLIDE_ANGLE, val);
	}
	
	public double getEL_GLIDE_ANGLE() {
		return prefs.getDouble(EL_GLIDE_ANGLE, Constance.ELEVATION.GLIDE_ANGLE);
	}
	
	public void setEL_GLIDE_MAX_DIST(double val) {
		prefs.putDouble(EL_GLIDE_MAX_DIST, val);
	}
	
	public double getEL_GLIDE_MAX_DIST() {
		return prefs.getDouble(EL_GLIDE_MAX_DIST, Constance.ELEVATION.GLIDE_MAX_DIST);
	}
	
	public void setEL_GLIDE_FLAT_START_DIST(double val) {
		prefs.putDouble(EL_GLIDE_FLAT_START_DIST, val);
	}
	
	public double getEL_GLIDE_FLAT_START_DIST() {
		return prefs.getDouble(EL_GLIDE_FLAT_START_DIST, Constance.ELEVATION.GLIDE_FLAT_START_DIST);
	}
	
	public void setEL_USL_ANGLE(double val) {
		prefs.putDouble(EL_USL_ANGLE, val);
	}
	
	public double getEL_USL_ANGLE() {
		return prefs.getDouble(EL_USL_ANGLE, Constance.ELEVATION.USL_ANGLE);
	}
	
	public void setTOUCH_DOWN(double val) {
		prefs.putDouble(TOUCH_DOWN, val);
	}
	
	public double getTOUCH_DOWN() {
		return prefs.getDouble(TOUCH_DOWN, Constance.TOUCH_DOWN);
	}
	
	public void setEL_LSL_ANGLE(double val) {
		prefs.putDouble(EL_LSL_ANGLE, val);
	}
	
	public double getEL_LSL_ANGLE() {
		return prefs.getDouble(EL_LSL_ANGLE, Constance.ELEVATION.LSL_ANGLE);
	}
	
	public void setEL_UAL_ANGLE(double val) {
		prefs.putDouble(EL_UAL_ANGLE, val);
	}
	
	public double getEL_UAL_ANGLE() {
		return prefs.getDouble(EL_UAL_ANGLE, Constance.ELEVATION.UAL_ANGLE);
	}
	
	public void setEL_LAL_ANGLE(double val) {
		prefs.putDouble(EL_LAL_ANGLE, val);
	}
	
	public double getEL_LAL_ANGLE() {
		return prefs.getDouble(EL_LAL_ANGLE, Constance.ELEVATION.LAL_ANGLE);
	}
	
	public void setEL_USaL_ANGLE(double val) {
		prefs.putDouble(EL_USaL_ANGLE, val);
	}
	
	public double getEL_USaL_ANGLE() {
		return prefs.getDouble(EL_USaL_ANGLE, Constance.ELEVATION.USaL_ANGLE);
	}
	
	public void setEL_LSaL_ANGLE(double val) {
		prefs.putDouble(EL_LSaL_ANGLE, val);
	}
	
	public double getEL_LSaL_ANGLE() {
		return prefs.getDouble(EL_LSaL_ANGLE, Constance.ELEVATION.LSaL_ANGLE);
	}
	
	public void setEL_DH(double val) {
		prefs.putDouble(EL_DH, val);
	}
	
	public double getEL_DH() {
		return prefs.getDouble(EL_DH, Constance.ELEVATION.DH);
	}
	
	public void setAZ_LSL_ANGLE(double val) {
		prefs.putDouble(AZ_LSL_ANGLE, val);
	}
	
	public double getAZ_LSL_ANGLE() {
		return prefs.getDouble(AZ_LSL_ANGLE, Constance.AZIMUTH.LSL_ANGLE);
	}
	
	public void setAZ_RSL_ANGLE(double val) {
		prefs.putDouble(AZ_RSL_ANGLE, val);
	}
	
	public double getAZ_RSL_ANGLE() {
		return prefs.getDouble(AZ_RSL_ANGLE, Constance.AZIMUTH.RSL_ANGLE);
	}
	
	public void setAZ_RCLO(double val) {
		prefs.putDouble(AZ_RCLO, val);
	}
	
	public double getAZ_RCLO() {
		return prefs.getDouble(AZ_RCLO, Constance.AZIMUTH.RCLO);
	}
	
	public void setAZ_LAL_ANGLE(double val) {
		prefs.putDouble(AZ_LAL_ANGLE, val);
	}
	
	public double getAZ_LAL_ANGLE() {
		return prefs.getDouble(AZ_LAL_ANGLE, Constance.AZIMUTH.LAL_ANGLE);
	}
	
	public void setAZ_RAL_ANGLE(double val) {
		prefs.putDouble(AZ_RAL_ANGLE, val);
	}
	
	public double getAZ_RAL_ANGLE() {
		return prefs.getDouble(AZ_RAL_ANGLE, Constance.AZIMUTH.RAL_ANGLE);
	}
	
	public void setAZ_LSaL(double val) {
		prefs.putDouble(AZ_LSaL, val);
	}
	
	public double getAZ_LSaL() {
		return prefs.getDouble(AZ_LSaL, Constance.AZIMUTH.LSaL);
	}
	
	public void setAZ_RSaL(double val) {
		prefs.putDouble(AZ_RSaL, val);
	}
	
	public double getAZ_RSaL() {
		return prefs.getDouble(AZ_RSaL, Constance.AZIMUTH.RSaL);
	}
	
	public void setPREF_RANGE_UNITS(String val) {
		prefs.put(PREF_RANGE_UNITS, val);
	}
	
	public String getPREF_RANGE_UNITS() {
		return prefs.get(PREF_RANGE_UNITS, Constance.PREF.RANGE_UNITS);
	}
	
	public void setPREF_RANGE_UNITS_2(String val) {
		prefs.put(PREF_RANGE_UNITS_2, val);
	}
	
	public String getPREF_RANGE_UNITS_2() {
		return prefs.get(PREF_RANGE_UNITS_2, Constance.PREF.RANGE_UNITS_2);
	}
	
	public void setIP_RRM(String val) {
		prefs.put(IP_RRM, val);
	}
	
	public String getIP_RRM() {
		return prefs.get(IP_RRM, Constance.IP_RRM);
	}
	
	public void setIP_RC_1(String val) {
		prefs.put(IP_RC_1, val);
	}
	
	public String getIP_RC_1() {
		return prefs.get(IP_RC_1, Constance.IP_RC_1);
	}
	
	public void setIP_RC_2(String val) {
		prefs.put(IP_RC_2, val);
	}
	
	public String getIP_RC_2() {
		return prefs.get(IP_RC_2, Constance.IP_RC_2);
	}
	
	public void setGENERAL_MSL(String val) {
		prefs.put(GENERAL_MSL, val);
	}
	
	public String getGENERAL_MSL() {
		return prefs.get(GENERAL_MSL, Constance.GENERAL_MSL);
	}
	
	public void setGROUP_ADDR(String val) {
		prefs.put(GROUP_ADDR, val);
	}
	
	public String getGROUP_ADDR() {
		return prefs.get(GROUP_ADDR, Constance.GROUP_ADDR);
	}
	
	public void setDISPLAY_ID(byte val) {
		prefs.putInt(DISPLAY_ID, val);
	}
	
	public int getDISPLAY_ID() {
		return prefs.getInt(DISPLAY_ID, Constance.DISPLAY_ID);
	}
	
	public void setPORT_AZ_PLOTS(int val) {
		prefs.putInt(PORT_AZ_PLOTS, val);
	}
	
	public int getPORT_AZ_PLOTS() {
		return prefs.getInt(PORT_AZ_PLOTS, Constance.PORT_AZ_PLOTS);
	}
	
	public void setPORT_AZ_TRACKS(int val) {
		prefs.putInt(PORT_AZ_TRACKS, val);
	}
	
	public int getPORT_AZ_TRACKS() {
		return prefs.getInt(PORT_AZ_TRACKS, Constance.PORT_AZ_TRACKS);
	}
	
	public void setPORT_EL_PLOTS(int val) {
		prefs.putInt(PORT_EL_PLOTS, val);
	}
	
	public int getPORT_EL_PLOTS() {
		return prefs.getInt(PORT_EL_PLOTS, Constance.PORT_EL_PLOTS);
	}
	
	public void setPORT_EL_TRACKS(int val) {
		prefs.putInt(PORT_EL_TRACKS, val);
	}
	
	public int getPORT_EL_TRACKS() {
		return prefs.getInt(PORT_EL_TRACKS, Constance.PORT_EL_TRACKS);
	}
	
	public void setPORT_VIDEO(int val) {
		prefs.putInt(PORT_VIDEO, val);
	}
	
	public int getPORT_VIDEO() {
		return prefs.getInt(PORT_VIDEO, Constance.PORT_VIDEO);
	}
	
	public void setPORT_RRM_WRITE(int val) {
		prefs.putInt(PORT_RRM_WRITE, val);
	}
	
	public int getPORT_RRM_WRITE() {
		return prefs.getInt(PORT_RRM_WRITE, Constance.PORT_RRM_WRITE);
	}
	
	public void setPORT_RC_WRITE(int val) {
		prefs.putInt(PORT_RC_WRITE, val);
	}
	
	public int getPORT_RC_WRITE() {
		return prefs.getInt(PORT_RC_WRITE, Constance.PORT_RC_WRITE);
	}
	
	public void setRECORD_LOC(String val) {
		prefs.put(RECORD_LOC, val);
	}
	
	public String getRECORD_LOC() {
		return prefs.get(RECORD_LOC, Constance.RECORD_DIR_LOC);
	}
	
	public void setMTI(String val) {
		prefs.put(MTI, val);
	}
	
	public String getMTI() {
		return prefs.get(MTI, "ON");
	}
	
	public void setMTI_TYPE(String val) {
		prefs.put(MTI_TYPE, val);
	}
	
	public String getMTI_TYPE() {
		return prefs.get(MTI_TYPE, "0");
	}
	
	public void setAMTI(String val) {
		prefs.put(AMTI, val);
	}
	
	public String getAMTI() {
		return prefs.get(AMTI, "ON");
	}
	
	public void setCLUTTER_MAP(String val) {
		prefs.put(CLUTTER_MAP, val);
	}
	
	public String getCLUTTER_MAP() {
		return prefs.get(CLUTTER_MAP, "ON");
	}
	
	public void setCFAR_TYPE(String val) {
		prefs.put(CFAR_TYPE, val);
	}
	
	public String getCFAR_TYPE() {
		return prefs.get(CFAR_TYPE, "CA");
	}
	
	public void setCFAR_THRESHOLD(String val) {
		prefs.put(CFAR_THRESHOLD, val);
	}
	
	public String getCFAR_THRESHOLD() {
		return prefs.get(CFAR_THRESHOLD, "1");
	}
	
	public void setCAL_AZ(String val) {
		prefs.put(CAL_AZ, val);
	}
	
	public String getCAL_AZ() {
		return prefs.get(CAL_AZ, "TX");
	}
	
	public void setCAL_EL(String val) {
		prefs.put(CAL_EL, val);
	}
	
	public String getCAL_EL() {
		return prefs.get(CAL_EL, "TX");
	}	
	
	public void setBITE(String val) {
		prefs.put(BITE, val);
	}
	
	public String getBITE() {
		return prefs.get(BITE, "ON");
	}
	
	public void setAZ_DOP(String val) {
		prefs.put(AZ_DOP, val);
	}
	
	public String getAZ_DOP() {
		return prefs.get(AZ_DOP, "0");
	}
	
	public void setAZ_ATT(String val) {
		prefs.put(AZ_ATT, val);
	}
	
	public String getAZ_ATT() {
		return prefs.get(AZ_ATT, "0");
	}
	
	public void setEL_DOP(String val) {
		prefs.put(EL_DOP, val);
	}
	
	public String getEL_DOP() {
		return prefs.get(EL_DOP, "0");
	}
	
	public void setEL_ATT(String val) {
		prefs.put(EL_ATT, val);
	}
	
	public String getEL_ATT() {
		return prefs.get(EL_ATT, "0");
	}
	
	public void setST_TARGET(String val) {
		prefs.put(ST_TARGET, val);
	}
	
	public String getST_TARGET() {
		return prefs.get(ST_TARGET, "0");
	}
	
	public void setMOV_START_RNG(String val) {
		prefs.put(MOV_START_RNG, val);
	}
	
	public String getMOV_START_RNG() {
		return prefs.get(MOV_START_RNG, "0");
	}
	
	public void setMOV_STOP_RNG(String val) {
		prefs.put(MOV_STOP_RNG, val);
	}
	
	public String getMOV_STOP_RNG() {
		return prefs.get(MOV_STOP_RNG, "0");
	}
	
	public void setMOV_STEP_RNG(String val) {
		prefs.put(MOV_STEP_RNG, val);
	}
	
	public String getMOV_STEP_RNG() {
		return prefs.get(MOV_STEP_RNG, "0");
	}
	
	public void setAZ_MOTOR(String val) {
		prefs.put(AZ_MOTOR, val);
	}
	
	public String getAZ_MOTOR() {
		return prefs.get(AZ_MOTOR, "true");
	}
	
	public void setHOR_MOTOR(String val) {
		prefs.put(HOR_MOTOR, val);
	}
	
	public String getHOR_MOTOR() {
		return prefs.get(HOR_MOTOR, "true");
	}
	
	public void setVER_MOTOR(String val) {
		prefs.put(VER_MOTOR, val);
	}
	
	public String getVER_MOTOR() {
		return prefs.get(VER_MOTOR, "true");
	}
	
	public void setAZ_TILT(String val) {
		prefs.put(AZ_TILT, val);
	}
	
	public String getAZ_TILT() {
		return prefs.get(AZ_TILT, String.valueOf(Constance.AZIMUTH.TILT));
	}
	
	public void setEL_TILT(String val) {
		prefs.put(EL_TILT, val);
	}
	
	public String getEL_TILT() {
		return prefs.get(EL_TILT, String.valueOf(Constance.ELEVATION.TILT));
	}
	
	public void setAZ_ACT_POS(String val) {
		prefs.put(AZ_ACT_POS, val);
	}
	
	public String getAZ_ACT_POS() {
		return prefs.get(AZ_ACT_POS, "0");
	}
	
	public void setHOR_ACT_POS(String val) {
		prefs.put(HOR_ACT_POS, val);
	}
	
	public String getHOR_ACT_POS() {
		return prefs.get(HOR_ACT_POS, "0");
	}
	
	public void setVER_ACT_POS(String val) {
		prefs.put(VER_ACT_POS, val);
	}
	
	public String getVER_ACT_POS() {
		return prefs.get(VER_ACT_POS, "0");
	}
	
	public void setMSTC_LAW_AZ(String val) {
		prefs.put(MSTC_LAW_AZ , val);
	}
	
	public String getMSTC_LAW_AZ() {
		return prefs.get(MSTC_LAW_AZ , "0");
	}
	
	public void setMSTC_LAW_EL(String val) {
		prefs.put(MSTC_LAW_EL , val);
	}
	
	public String getMSTC_LAW_EL() {
		return prefs.get(MSTC_LAW_EL , "0");
	}
	
	public void setAGC_LAW(String val) {
		prefs.put(AGC_LAW , val);
	}
	
	public String getAGC_LAW() {
		return prefs.get(AGC_LAW , "0");
	}
	
	public void setTail(String val) {
		prefs.put(TAILSCAN , val);
	}
	
	public String getTailscan() {
		return prefs.get(TAILSCAN,"0");
	}
	
	public void setAZ_AXIS(String val) {
		prefs.put(AZ_AXIS , val);
	}
	
	public String getAZ_AXIS() {
		return prefs.get(AZ_AXIS , "0");
	}
	
	public void setMIN_RNG(String val) {
		prefs.put(MIN_RNG , val);
	}
	
	public String getMIN_RNG() {
		return prefs.get(MIN_RNG , "0");
	}
	
	public void setNORTH_OFF_BIAS(String val) {
		prefs.put(NORTH_OFF_BIAS , val);
	}
	
	public String getNORTH_OFF_BIAS() {
		return prefs.get(NORTH_OFF_BIAS , "0");
	}
	
	public void setOP_MODE(String val) {
		prefs.put(OP_MODE  , val);
	}
	
	public String getOP_MODE () {
		return prefs.get(OP_MODE , "0");
	}
	
	public void setOP_MODE_TYPE(String val) {
		prefs.put(OP_MODE_TYPE , val);
	}
	
	public String getOP_MODE_TYPE() {
		return prefs.get(OP_MODE_TYPE , "0");
	}
	
	public void setOP_FREQ(String val) {
		prefs.put(OP_FREQ , val);
	}
	
	public String getOP_FREQ() {
		return prefs.get(OP_FREQ , "0");
	}
	
	public void setMAINTAIN_FREQ(String val) {
		prefs.put(MAINTAIN_FREQ , val);
	}
	
	public String getMAINTAIN_FREQ() {
		return prefs.get(MAINTAIN_FREQ , "0");
	}
	
	public void setOP_SCAN(String val) {
		prefs.put(OP_SCAN  , val);
	}
	
	public String getOP_SCAN() {
		return prefs.get(OP_SCAN , "0");
	}
	
	public void setOP_POLARIZATION(String val) {
		prefs.put(OP_POLARIZATION  , val);
	}
	
	public String getOP_POLARIZATION() {
		return prefs.get(OP_POLARIZATION , "0");
	}
	
	public void setOP_TX(String val) {
		prefs.put(OP_TX  , val);
	}
	
	public String getOP_TX() {
		return prefs.get(OP_TX , "ON");
	}
	
	public void setOP_BITE(String val) {
		prefs.put(OP_BITE , val);
	}
	
	public String getOP_BITE() {
		return prefs.get(OP_BITE , "ON");
	}
	
	public void setOP_RUNWAY(String val) {
		prefs.put(OP_RUNWAY , val);
	}
	
	public String getOP_RUNWAY() {
		return prefs.get(OP_RUNWAY , "0");
	}

	public void setRUNWAY_1(String val) {
		prefs.put(RUNWAY_1 , val);
	}
	
	public String getRUNWAY_1() {
		return prefs.get(RUNWAY_1 , "");
	}
	
	public void setRUNWAY_2(String val) {
		prefs.put(RUNWAY_2 , val);
	}
	
	public String getRUNWAY_2() {
		return prefs.get(RUNWAY_2 , "");
	}
	
	public void setRUNWAY_3(String val) {
		prefs.put(RUNWAY_3 , val);
	}
	
	public String getRUNWAY_3() {
		return prefs.get(RUNWAY_3 , "");
	}
	
	public void setRUNWAY_4(String val) {
		prefs.put(RUNWAY_4 , val);
	}
	
	public String getRUNWAY_4() {
		return prefs.get(RUNWAY_4 , "");
	}

}

