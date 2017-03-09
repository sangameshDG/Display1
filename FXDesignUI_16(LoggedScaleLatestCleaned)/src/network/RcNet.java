package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import messages.app.AckMsg;
import messages.app.ConfigUpdateMsg;
import messages.app.MyHealthMsg;
import messages.app.RequestLoReMsg;
import messages.app.RequestMaSlMsg;
import messages.radar.MasterMsg;
import messages.radar.MasterOpenMsg;
import messages.radar.RemoteMsg;
import messages.utils.DataIdentifier;
import messages.utils.DataSupervisor;
import model.AppConfig;

import org.apache.log4j.Logger;

import utils.Constance;
import admin.MasterSlavery;
import admin.UserPreference;

public class RcNet implements Runnable {

	static final Logger logger = Logger.getLogger(RcNet.class);

	public static final int TIME_REPETITION = 1;

	private static final int BUF_SIZE = 20;

	private boolean isRunning = false;
	private boolean isRConline = false;

	private boolean isMasterOpen = false;

	private String rcName;
	private int port;
	private Socket mSocketWrite = null;
	InetAddress serverAddr;

	DataSupervisor mDataSupervisor;

	public RcNet(String name, String ip, int port) {
		this.rcName = name;
		this.port = port;
		this.mDataSupervisor = new DataSupervisor();

		try {
			serverAddr = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			logger.error(e);
		}
	}

	@Override
	public void run() {
		isRunning = true;
		runRCTask();
		isRunning = false;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean isOnline() {
		return isRConline;
	}

	public boolean isMasterOpen() {
		return isMasterOpen;
	}

	private void runRCTask() {
		if (!connect()) {
			AppConfig.getInstance()
					.openErrorDialog("RC " + rcName + " " + "is Offline.");
		} else {
			AppConfig.getInstance().openInformationDialog("RC " + rcName + " "
					+ "is Online. Connection established.");

			parseData();
		}
	}

	private boolean connect() {
		int count = TIME_REPETITION;
		while (count > 0) {
			try {
				mSocketWrite = new Socket(serverAddr, port);
				isRConline = true;
				Constance.CHANNEL_ID = Integer.valueOf(rcName);
				logger.info("RC TCP (mSocketWrite)socket created at IP:"
						+ mSocketWrite.getInetAddress() + " PORT: "
						+ mSocketWrite.getPort());
				break;
			} catch (IOException e) {
				logger.error(e);
				isRConline = false;
			}
			--count;
		}
		return isRConline;
	}

	private void parseData() {
	//	setStdWaitingPeriod();
		while (isRConline && mSocketWrite.isConnected()) {
			try {
				makeData(receiveBytes());
			} catch (IOException e) {
				logger.error(e);
				isRConline = false;
				setAsSlave();
				AppConfig.getInstance().openErrorDialog("RC "+Constance.CHANNEL_ID+" went Offline.");
				AppConfig.getInstance().getFxmlController().startRcNet();
				break;
			}
		}
	}

	private void makeData(byte[] mData) {
		// logger.info("Incoming: "+AppUtils.bytesToHex(mData));

		// identify data
		final String msgName = DataIdentifier.getMessageType(mData);
		logger.info("RC TCP Data Identified: " + msgName);

		// decode data
		if (msgName != null) {
			Object object = mDataSupervisor.decodeMsg(msgName, mData);

			if (object instanceof MasterMsg) {
				MasterMsg masterMsg = (MasterMsg) object;
				logger.info(
						"RC TCP Data MasterMsg : " + masterMsg.toString());

				// check display ID & become master
				checkMaster(masterMsg);

			} else if (object instanceof MasterOpenMsg) {
				MasterOpenMsg masterOpenMsg = (MasterOpenMsg) object;
				logger.info("RC TCP Data MasterOpenMsg : "
						+ masterOpenMsg.toString());

				// check with user to become master
				claimMaster(masterOpenMsg);

			} else if (object instanceof RemoteMsg) {
				RemoteMsg remoteMsg = (RemoteMsg) object;
				logger.info(
						"RC TCP Data RemoteMsg : " + remoteMsg.toString());

				// check with user to become remote
				claimRemote(remoteMsg);

			}
		}
	}

	private void checkMaster(MasterMsg masterMsg) {
		if (masterMsg.getDisplayId() == Constance.DISPLAY_ID) {
			if (masterMsg.getMaster() == MasterMsg.MASTER) {
	
				MasterSlavery.getInstance().setMaster(true);
				MasterSlavery.getInstance().setToggleState(false);
				AppConfig.getInstance().getFxmlController().toggleMasterButton(false);
				AppConfig.getInstance().getFxmlController().radarUpdateClick(null);//send cmd to RC
				AppConfig.getInstance().getFxmlController().claimInstallation(); 
				
				// send ack
				sendAckMsg(AckMsg.ACK_MASTER);
				logger.info("------------------Sent the ACK messageg to RC--------------------");
			}else {
				setAsSlave();
			}
		} else {
			setAsSlave();
		}
	}

	private void claimMaster(MasterOpenMsg masterOpenMsg) {
		if (masterOpenMsg.getMaster() == MasterOpenMsg.CLAIM) {
			// check with user by toggling master icon
			isMasterOpen = true;
			MasterSlavery.getInstance().setToggleState(true);
			AppConfig.getInstance().getFxmlController().toggleMasterButton(isMasterOpen);
		}
	}

	private void claimRemote(RemoteMsg remoteMsg) {
//		if (remoteMsg.getDisplayId() == Constance.DISPLAY_ID) {
			if (remoteMsg.getRemote() == RequestLoReMsg.REMOTE) {
//				if (MasterSlavery.getInstance().isMaster()) {
					AppConfig.getInstance().getFxmlController().changeLocalRemote("REMOTE");

					// send Ack
					sendAckMsg(AckMsg.ACK_REMOTE);
//				}
			} else {
				// stay in local
				AppConfig.getInstance().getFxmlController().changeLocalRemote("LOCAL");
			}
			MasterSlavery.getInstance().setMode(remoteMsg.getRemote());
//		}
	}

	private void setAsSlave() {
		MasterSlavery.getInstance().setMaster(false);
//		AppConfig.getInstance().getFxmlController().changeLocalRemote("LOCAL");
		AppConfig.getInstance().getFxmlController().toggleMasterButton(false);
	}

	public void sendRequestSlaveMsg() {
		RequestMaSlMsg rMaSlMsg = new RequestMaSlMsg();
		rMaSlMsg.setChId((byte) Constance.CHANNEL_ID);
		rMaSlMsg.setDisplayId(Constance.DISPLAY_ID);
		rMaSlMsg.setRequest(RequestMaSlMsg.SLAVE);

		try {
			sendBytes(rMaSlMsg.getByteBuffer().array());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void sendRequestMasterMsg() {
		RequestMaSlMsg rMaSlMsg = new RequestMaSlMsg();
		rMaSlMsg.setChId((byte) Constance.CHANNEL_ID);
		rMaSlMsg.setDisplayId(Constance.DISPLAY_ID);
		rMaSlMsg.setRequest(RequestMaSlMsg.MASTER);

		try {
			isMasterOpen = false;
			sendBytes(rMaSlMsg.getByteBuffer().array());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void sendAckMsg(final byte val) {
		AckMsg ackMsg = new AckMsg();
		ackMsg.setChId((byte) Constance.CHANNEL_ID);
		ackMsg.setDisplayId(Constance.DISPLAY_ID);
		ackMsg.setAck(val);

		try {
			sendBytes(ackMsg.getByteBuffer().array());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void sendLocalMsg() {
		RequestLoReMsg localMsg = new RequestLoReMsg();
		localMsg.setChId((byte) Constance.CHANNEL_ID);
		localMsg.setDisplayId(Constance.DISPLAY_ID);
		localMsg.setLocalRemote(RequestLoReMsg.LOCAL);

		try {
			sendBytes(localMsg.getByteBuffer().array());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void sendRemoteMsg() {
		RequestLoReMsg localMsg = new RequestLoReMsg();
		localMsg.setChId((byte) Constance.CHANNEL_ID);
		localMsg.setDisplayId(Constance.DISPLAY_ID);
		localMsg.setLocalRemote(RequestLoReMsg.REMOTE);

		try {
			sendBytes(localMsg.getByteBuffer().array());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void sendHealthMsg() {
		MyHealthMsg healthMsg = new MyHealthMsg();
		healthMsg.setChId((byte) Constance.CHANNEL_ID);
		healthMsg.setDisplayId(Constance.DISPLAY_ID);
		healthMsg.setHealth(MyHealthMsg.GOOD);

		try {
			sendBytes(healthMsg.getByteBuffer().array());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void sendConfigUpdateMsg() {
		ConfigUpdateMsg cUpdateMsg = new ConfigUpdateMsg();
		cUpdateMsg.setRunwayNo(Integer.parseInt(UserPreference.getInstance().getOP_RUNWAY())+1);//index plus one
		cUpdateMsg.setAzAxis((int) Constance.AZIMUTH.AXIS);
		cUpdateMsg.setrMin((int) Constance.RMIN);
		cUpdateMsg.setAzTilt(Constance.AZIMUTH.TILT);
		cUpdateMsg.setElTilt(Constance.ELEVATION.TILT);

		try {
			sendBytes(cUpdateMsg.getByteBuffer().array());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void sendBytes(byte[] myByteArray) throws IOException {
		sendBytes(myByteArray, 0, myByteArray.length);
	}

	public void sendBytes(byte[] data, int start, int len) throws IOException {
		if (len < 0)
			throw new IllegalArgumentException("Negative length not allowed");
		if (start < 0 || start >= data.length)
			throw new IndexOutOfBoundsException("Out of bounds: " + start);

		DataOutputStream mServerSocketOutPacket = new DataOutputStream(
				mSocketWrite.getOutputStream());
//		mServerSocketOutPacket.writeInt(len);
		if (len > 0)
			mServerSocketOutPacket.write(data);
		logger.info("RC TCP Socket Data Sent: " + len);
	}

	public void setStdWaitingPeriod() {
		try {
			mSocketWrite.setSoTimeout(TIME_REPETITION*Constance.MILLI_SECOND);
			logger.info("RC TCP Socket Data Rx: Waiting..." + TIME_REPETITION);
		} catch (SocketException e) {
			logger.error(e);
		}
	}

	public byte[] receiveBytes() throws IOException {
		logger.info("Listening RC Server data... ");
		DataInputStream mServerSocketInPacket = new DataInputStream(
				mSocketWrite.getInputStream());
//		int len = mServerSocketInPacket.readInt();
		byte[] tcpdata = new byte[BUF_SIZE];
		// Wait to receive a socket data
//		if (len > 0) {
			mServerSocketInPacket.read(tcpdata);
			logger.info("RC TCP Socket Data Rx: " + tcpdata.length);
			return tcpdata;
//		}
//		return null;
	}

	public void closeActiveConnection() {
		try {
			mSocketWrite.shutdownInput();
			mSocketWrite.shutdownOutput();
			mSocketWrite.close();
			logger.info("RC TCP socket Closed/Ended");
		} catch (IOException e) {
			logger.error("RC TCP socket close failed", e);
		}
	}

}
