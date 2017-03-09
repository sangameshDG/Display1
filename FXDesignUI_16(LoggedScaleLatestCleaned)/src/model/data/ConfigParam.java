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
//*H  Created: Mar 28, 2016
//*H
//*H  @author....: $Author: $Suraj
//*H  @date......: $Date: $
//*H  @version...: $Rev: $1.0
//*H  @path......: $URL: $
//*H
//*H============================================================================

package model.data;

public class ConfigParam {

	private String runwayNo;
	private String azAxis;
	private String rMin;
	private String elTilt;
	private String azTilt;
	private String xOffset;
	private String yOffset;
	private CR cr1;
	private CR cr2;
	private CR cr3;
	private CR cr4;
	private CR cr5;
	private CR cr6;
	
	public ConfigParam() {

	}

	public ConfigParam(String runwayNo, String azAxis, String rMin,
			String elTilt, String azTilt, String xOff, String yOff, CR cr1, CR cr2, CR cr3, CR cr4,
			CR cr5, CR cr6) {
		this.runwayNo = runwayNo;
		this.azAxis = azAxis;
		this.rMin = rMin;
		this.elTilt = elTilt;
		this.azTilt = azTilt;
		this.xOffset = xOff;
		this.yOffset = yOff;
		this.cr1 = cr1;
		this.cr2 = cr2;
		this.cr3 = cr3;
		this.cr4 = cr4;
		this.cr5 = cr5;
		this.cr6 = cr6;
	}

	public static class CR {
		private String Xoff;
		private String Yoff;
		private String Xoff1;
		private String Yoff1;
		
		public CR(String xoff, String yOff,String xoff1, String yOff1) {
			this.Xoff = xoff;
			this.Yoff = yOff;
			this.Xoff1 = xoff1;
			this.Yoff1 = yOff1;
			
		}

		public String getRange() {
			return Xoff;
		}

		public void setXoff(String xoff) {
			Xoff = xoff;
		}

		public String getAngle() {
			return Xoff1;
		}

		public void setXoff1(String xoff1) {
			Xoff1 = xoff1;
		}
		public String getRange1() {
			return Yoff;
		}

		public void setYoff(String yoff) {
			Yoff = yoff;
		}

		public String getAngle1() {
			return Yoff1;
		}

		public void setYoff1(String yoff1) {
			Yoff1 = yoff1;
		}

	}

	public String getAzAxis() {
		return azAxis;
	}

	public void setAzAxis(String azAxis) {
		this.azAxis = azAxis;
	}

	public String getRmin() {
		return rMin;
	}

	public void setrMin(String rMin) {
		this.rMin = rMin;
	}

	public String getElTilt() {
		return elTilt;
	}

	public void setElTilt(String elTilt) {
		this.elTilt = elTilt;
	}

	public String getAzTilt() {
		return azTilt;
	}

	public void setAzTilt(String azTilt) {
		this.azTilt = azTilt;
	}

	public String getRunwayNo() {
		return runwayNo;
	}

	public void setRunwayNo(String runwayNo) {
		this.runwayNo = runwayNo;
	}

	public CR getCr1() {
		return cr1;
	}

	public void setCr1(CR cr1) {
		this.cr1 = cr1;
	}

	public CR getCr2() {
		return cr2;
	}

	public void setCr2(CR cr2) {
		this.cr2 = cr2;
	}

	public CR getCr3() {
		return cr3;
	}

	public void setCr3(CR cr3) {
		this.cr3 = cr3;
	}

	public CR getCr4() {
		return cr4;
	}

	public void setCr4(CR cr4) {
		this.cr4 = cr4;
	}

	public CR getCr5() {
		return cr5;
	}

	public void setCr5(CR cr5) {
		this.cr5 = cr5;
	}

	public CR getCr6() {
		return cr6;
	}

	public void setCr6(CR cr6) {
		this.cr6 = cr6;
	}

	public String getXOffset() {
		return xOffset;
	}

	public void setXOffset(String xOffset) {
		this.xOffset = xOffset;
	}

	public String getYOffset() {
		return yOffset;
	}

	public void setYOffset(String yOffset) {
		this.yOffset = yOffset;
	}

	@Override
	public String toString() {
		return "Config: "+"\n"
				+azAxis+", "+xOffset;
	}
	
	

}
