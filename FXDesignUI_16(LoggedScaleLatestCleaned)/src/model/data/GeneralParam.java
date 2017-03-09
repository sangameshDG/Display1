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

public class GeneralParam {

	private String rc1Ip;
	private String rc2Ip;
	private String rcPort;
	private String msl;

	private DisplayParam displayParam;

	public GeneralParam(DisplayParam displayParam, String rc1Ip, String rc2Ip,
			String rcPort, String msl) {
		this.displayParam = displayParam;
		this.rc1Ip = rc1Ip;
		this.rc2Ip = rc2Ip;
		this.rcPort = rcPort;
		this.msl = msl;
	}

	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public String getRc1Ip() {
		return rc1Ip;
	}

	public void setRc1Ip(String rc1Ip) {
		this.rc1Ip = rc1Ip;
	}

	public String getRc2Ip() {
		return rc2Ip;
	}

	public void setRc2Ip(String rc2Ip) {
		this.rc2Ip = rc2Ip;
	}

	public String getRcPort() {
		return rcPort;
	}

	public void setRcPort(String rcPort) {
		this.rcPort = rcPort;
	}

	public String getMsl() {
		return msl;
	}

	public void setMsl(String msl) {
		this.msl = msl;
	}

}
