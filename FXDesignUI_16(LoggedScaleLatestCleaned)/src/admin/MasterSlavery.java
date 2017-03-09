package admin;

import messages.app.RequestLoReMsg;
import messages.utils.IByteSum;

import org.apache.log4j.Logger;

import utils.AppUtils;

public class MasterSlavery implements IByteSum {

	private static final Logger logger = Logger.getLogger(MasterSlavery.class);
	private static MasterSlavery instance = null;
	private static final int PID = AppUtils.getPID();
	private boolean toggleState = false;
	private boolean isMaster = false;
	private boolean isRemote = false;

	protected MasterSlavery() {
		logger.info("PID: " + PID);
	}

	public static MasterSlavery getInstance() {
		if (instance == null) {
			instance = new MasterSlavery();
			logger.info("MasterSlavery Instantiated");
		}
		return instance;
	}

	public void setMaster(boolean b) {
		isMaster = b;
	}

	public synchronized boolean isMaster() {
		return isMaster;
	}

	public void setMode(byte remote) {
		isRemote = false;
		if (remote == RequestLoReMsg.REMOTE)
			isRemote = true;
	}

	public boolean isRemote() {
		return isRemote;
	}

	public boolean isToggleState() {
		return toggleState;
	}

	public void setToggleState(boolean toggleState) {
		this.toggleState = toggleState;
	}

}
