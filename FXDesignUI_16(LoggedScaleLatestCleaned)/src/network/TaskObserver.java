package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import messages.app.CornerReflectorMsg;
import messages.app.ZoneQueryMsg;
import messages.app.ZoneSuppression;
import messages.radar.AntHealthStatusMsg;
import messages.radar.AzimuthPlanePlotsPerCPIMsg;
import messages.radar.AzimuthPlaneTrackMsg;
import messages.radar.AzimuthScanStartMessage;
import messages.radar.ElAntHealthStatusMsg;
import messages.radar.ElevationPlanePlotsPerCPIMsg;
import messages.radar.ElevationPlaneTrackMsg;
import messages.radar.ElevationScanStartMessage;
import messages.radar.ExRxHealthStatusMsg;
import messages.radar.GpsMsg;
import messages.radar.HealthStatusMsg;
import messages.radar.LANHealthStatusMsg;
import messages.radar.NoiseFigureStatusMsg;
import messages.radar.PCHealthStatusMsg;
import messages.radar.PcReadStatusMsg;
import messages.radar.PlaneRAWVideoMsg;
import messages.radar.PwrHealthStatusMsg;
import messages.radar.RRMHealthStatusMsg;
import messages.radar.RunwayMsg;
import messages.radar.SDPHealthStatusMsg;
import messages.radar.TempHumHealthStatusMsg;
import messages.radar.VSWRHealthStatusMsg;
import messages.radar.WindSensorMsg;
import messages.radar.ZoneUpdateMsg;
import messages.utils.DataIdentifier;
import messages.utils.DataSupervisor;
import messages.utils.IByteSum;
import model.AppConfig;
import model.DataManager;
import model.drawable.Track;
import org.apache.log4j.Logger;
import utils.Constance;
import admin.UserPreference;
import db.DBRecord;

public class TaskObserver extends Thread implements IByteSum {

	static final Logger logger = Logger.getLogger(TaskObserver.class);

	private Socket mSocketAzPlot = null;
	private Socket mSocketElPlot = null;
	private Socket mSocketAzTrack = null;
	private Socket mSocketElTrack = null;
	private Socket mSocketVideo = null;
	private Socket mSocketStatus = null;
	private Socket mSocketWrite = null;

	private DatagramSocket mDatagramSocketAzPlot = null;
	private DatagramSocket mDatagramSocketElPlot = null;
	private DatagramSocket mDatagramSocketAzTrack = null;
	private DatagramSocket mDatagramSocketElTrack = null;
	private DatagramSocket mDatagramSocketVideo = null;
	private DatagramSocket mDatagramSocketStatus = null;
	private DatagramSocket mDatagramSocketWrite = null;

	private InetAddress groupAddr;
	private MulticastSocket mMCSocketAzPlot = null;
	private MulticastSocket mMCSocketElPlot = null;
	private MulticastSocket mMCSocketAzTrack = null;
	private MulticastSocket mMCSocketElTrack = null;
	private MulticastSocket mMCSocketVideo = null;
	private MulticastSocket mMCSocketStatus = null;
	private MulticastSocket mMCSocketWrite = null;

	List<Thread> TaskManager = new ArrayList<Thread>();
	DataManager mDataManager = DataManager.getInstance();
	List<Integer> elLatestTrackNoList = new ArrayList<Integer>();
	List<Integer> azLatestTrackNoList = new ArrayList<Integer>();
	DataSupervisor mDataSupervisor;
	
	int elClearing = 0;

	BlockingQueue<LiquidStream> mElTrackRecordQ;
	BlockingQueue<LiquidStream> mElPlotRecordQ;
	BlockingQueue<LiquidStream> mAzTrackRecordQ;
	BlockingQueue<LiquidStream> mAzPlotRecordQ;

	int azTrack = 0;
	int azPlot = 0;
	int elTrack = 0;
	int elPlot = 0;
	int azClearing=0;

	public TaskObserver() {
		mDataSupervisor = new DataSupervisor();
				
		mElTrackRecordQ = new LinkedBlockingQueue<LiquidStream>();
		mElPlotRecordQ = new LinkedBlockingQueue<LiquidStream>();
		mAzTrackRecordQ = new LinkedBlockingQueue<LiquidStream>();
		mAzPlotRecordQ = new LinkedBlockingQueue<LiquidStream>();
	}

	@Override
	public void run() {
		runNetworkTask();
		sendZonequery();
		logger.info("Network Task Launched");
	}
	private void sendZonequery() {
		 ZoneQueryMsg zMsg=new ZoneQueryMsg();
		 try {
			sendBytes(zMsg.getByteBuffer().array());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.println("Message sent for zone query");
	}

	public BlockingQueue<LiquidStream> getElTrackRecordQ() {
		return mElTrackRecordQ;
	}

	public BlockingQueue<LiquidStream> getAzTrackRecordQ() {
		return mAzTrackRecordQ;
	}

	public BlockingQueue<LiquidStream> getElPlotRecordQ() {
		return mElPlotRecordQ;
	}

	public BlockingQueue<LiquidStream> getAzPlotRecordQ() {
		return mAzPlotRecordQ;
	}

	private void runNetworkTask() {

		// create sockets
		createSockets();
		Constance.IS_CONNECTED = true;

		// Creating Individual Threads for simultaneous read of data
		addThreadReadAzPlots();
		addThreadReadElPlots();
		addThreadReadAzTracks();
		addThreadReadElTracks();
		addThreadVideo();
		addThreadStatus();
		addThreadReadZone();

		// Starting all threads
		logger.info("Starting All Threads");
		for (Thread thread : TaskManager)
			thread.start();

	}

	private void addThreadReadAzPlots() {
		// Now loop forever, waiting to receive packets and printing them.
		Thread AzPlot = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("Waiting to Read Thread Az Plots Looper");
				while (Constance.IS_CONNECTED) {
					// Get Data
					byte[] mData = null;
					try {
						if (Constance.TCPIP)
							mData = parseTCPData(mSocketAzPlot);
						else if (Constance.UDPIP)
							mData = parseUDPData(mDatagramSocketAzPlot);
						else if (Constance.MCUDP)
							mData = parseMCUDPData(mMCSocketAzPlot);
					} catch (IOException e) {
						Constance.IS_CONNECTED = false;
						logger.error(e);
						break;
					}

					if (!Constance.IS_REPLAY_SETUP) {
						// Make Packet Object
						makeData(mData);
					}

				}
				logger.info("Ending Network Read Thread Az Plots Looper");
			}
		});
		TaskManager.add(AzPlot);
	}

	private void addThreadReadElPlots() {
		// Now loop forever, waiting to receive packets and printing them.
		Thread ElPlot = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("Waiting to Read Thread El Plots Looper");
				while (Constance.IS_CONNECTED) {
					// Get Data
					byte[] mData = null;
					try {
						if (Constance.TCPIP)
							mData = parseTCPData(mSocketElPlot);
						else if (Constance.UDPIP)
							mData = parseUDPData(mDatagramSocketElPlot);
						else if (Constance.MCUDP)
							mData = parseMCUDPData(mMCSocketElPlot);
					} catch (IOException e) {
						Constance.IS_CONNECTED = false;
						logger.error(e);
						break;
					}

					if (!Constance.IS_REPLAY_SETUP) {
						// Make Packet Object
						makeData(mData);
					}

				}
				logger.info("Ending Network Read Thread El Plots Looper");
			}
		});
		TaskManager.add(ElPlot);
	}

	private void addThreadReadAzTracks() {
		// Now loop forever, waiting to receive packets and printing them.
		Thread AzTrack = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("Waiting to Read Thread Az Tracks Looper");
				while (Constance.IS_CONNECTED) {
					// Get Data
					byte[] mData = null;
					try {
						if (Constance.TCPIP)
							mData = parseTCPData(mSocketAzTrack);
						else if (Constance.UDPIP)
							mData = parseUDPData(mDatagramSocketAzTrack);
						else if (Constance.MCUDP)
							mData = parseMCUDPData(mMCSocketAzTrack);
					} catch (IOException e) {
						Constance.IS_CONNECTED = false;
						logger.error(e);
						break;
					}

					if (!Constance.IS_REPLAY_SETUP) {
						// Make Packet Object
						makeData(mData);
					}

				}
				logger.info("Ending Network Read Thread Az Tracks Looper");
			}
		});
		TaskManager.add(AzTrack);
	}

	private void addThreadReadElTracks() {
		// Now loop forever, waiting to receive packets and printing them.
		Thread ElTrack = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("Waiting to Read Thread El Tracks Looper");
				while (Constance.IS_CONNECTED) {
					// Get Data
					byte[] mData = null;
					try {
						if (Constance.TCPIP)
							mData = parseTCPData(mSocketElTrack);
						else if (Constance.UDPIP)
							mData = parseUDPData(mDatagramSocketElTrack);
						else if (Constance.MCUDP)
							mData = parseMCUDPData(mMCSocketElTrack);
					} catch (IOException e) {
						Constance.IS_CONNECTED = false;
						logger.error(e);
						break;
					}

					if (!Constance.IS_REPLAY_SETUP) {
						// Make Packet Object
						makeData(mData);
					}

				}
				logger.info("Ending Network Read Thread El Tracks Looper");
			}
		});
		TaskManager.add(ElTrack);
	}

	private void addThreadVideo() {
		// Now loop forever, waiting to receive packets and printing them.
		Thread Video = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("Waiting to Read Thread Video Looper");
				while (Constance.IS_CONNECTED) {
					// Get Data
					byte[] mData = null;
					try {
						if (Constance.TCPIP)
							mData = parseTCPData(mSocketVideo);
						else if (Constance.UDPIP)
							mData = parseUDPData(mDatagramSocketVideo);
						else if (Constance.MCUDP)
							mData = parseMCUDPData(mMCSocketVideo);
					} catch (IOException e) {
						Constance.IS_CONNECTED = false;
						logger.error(e);
						break;
					}

					if (!Constance.IS_REPLAY_SETUP) {
						// Make Packet Object
						makeData(mData);
					}

				}
				logger.info("Ending Network Read Thread Video Looper");
			}
		});
		TaskManager.add(Video);
	}

	private void addThreadReadZone() {
		// Now loop forever, waiting to receive packets and printing them.
		Thread Zone = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("Waiting to Read Thread Zone Looper");
				while (Constance.IS_CONNECTED) {
					// Get Data
					byte[] mData = null;
					try {
						if (Constance.TCPIP)
							mData = parseTCPData(mSocketWrite);
						else if (Constance.UDPIP)
							mData = parseUDPData(mDatagramSocketWrite);
						else if (Constance.MCUDP)
							mData = parseMCUDPData(mMCSocketWrite);
					} catch (IOException e) {
						Constance.IS_CONNECTED = false;
						logger.error(e);
						break;
					}

					if (!Constance.IS_REPLAY_SETUP) {
						// Make Packet Object
						makeData(mData);
					}

				}
				logger.info("Ending Network Read Thread Status Looper");
			}
		});
		TaskManager.add(Zone);
	}
	
	private void addThreadStatus() {
		// Now loop forever, waiting to receive packets and printing them.
		Thread Status = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("Waiting to Read Thread Status Looper");
				while (Constance.IS_CONNECTED) {
					// Get Data
					byte[] mData = null;
					try {
						if (Constance.TCPIP)
							mData = parseTCPData(mSocketStatus);
						else if (Constance.UDPIP)
							mData = parseUDPData(mDatagramSocketStatus);
						else if (Constance.MCUDP)
							mData = parseMCUDPData(mMCSocketStatus);
					} catch (IOException e) {
						Constance.IS_CONNECTED = false;
						logger.error(e);
						break;
					}

					if (!Constance.IS_REPLAY_SETUP) {
						// Make Packet Object
						makeData(mData);
					}

				}
				logger.info("Ending Network Read Thread Status Looper");
			}
		});
		TaskManager.add(Status);
	}
	
	

	private void createSockets() {

		try {
			if (Constance.TCPIP) {
				InetAddress serverAddr = InetAddress
						.getByName(Constance.SERVER_IP);
				mSocketAzPlot = new Socket(serverAddr, Constance.PORT_AZ_PLOTS);
				mSocketElPlot = new Socket(serverAddr, Constance.PORT_EL_PLOTS);
				mSocketAzTrack = new Socket(serverAddr,
						Constance.PORT_AZ_TRACKS);
				mSocketElTrack = new Socket(serverAddr,
						Constance.PORT_EL_TRACKS);
				mSocketVideo = new Socket(serverAddr, Constance.PORT_VIDEO);
				mSocketStatus = new Socket(serverAddr, Constance.PORT_STATUS);
				mSocketWrite = new Socket(serverAddr, Constance.PORT_WRITE);

				logger.info("TCP (mSocketAzPlot)socket created at IP:"
						+ mSocketAzPlot.getInetAddress() + " PORT: "
						+ mSocketAzPlot.getPort());
				logger.info("TCP (mSocketElPlot)socket created at IP:"
						+ mSocketElPlot.getInetAddress() + " PORT: "
						+ mSocketElPlot.getPort());
				logger.info("TCP (mSocketAzTrack)socket created at IP:"
						+ mSocketAzTrack.getInetAddress() + " PORT: "
						+ mSocketAzTrack.getPort());
				logger.info("TCP (mSocketElTrack)socket created at IP:"
						+ mSocketElTrack.getInetAddress() + " PORT: "
						+ mSocketElTrack.getPort());
				logger.info("TCP (mSocketVideo)socket created at IP:"
						+ mSocketVideo.getInetAddress() + " PORT: "
						+ mSocketVideo.getPort());
				logger.info("TCP (mSocketStatus)socket created at IP:"
						+ mSocketStatus.getInetAddress() + " PORT: "
						+ mSocketStatus.getPort());
				logger.info("TCP (mSocketWrite)socket created at IP:"
						+ mSocketWrite.getInetAddress() + " PORT: "
						+ mSocketWrite.getPort());
			} else if (Constance.UDPIP) {
				mDatagramSocketAzPlot = new DatagramSocket(
						Constance.PORT_AZ_PLOTS);
				mDatagramSocketElPlot = new DatagramSocket(
						Constance.PORT_EL_PLOTS);
				mDatagramSocketAzTrack = new DatagramSocket(
						Constance.PORT_AZ_TRACKS);
				mDatagramSocketElTrack = new DatagramSocket(
						Constance.PORT_EL_TRACKS);
				mDatagramSocketVideo = new DatagramSocket(Constance.PORT_VIDEO);
				mDatagramSocketStatus = new DatagramSocket(
						Constance.PORT_STATUS);
				mDatagramSocketWrite = new DatagramSocket(
						Constance.PORT_WRITE);

				logger.info("UDP (mDatagramSocketAzPlot)socket created at IP:"
						+ mDatagramSocketAzPlot.getLocalAddress() + " PORT: "
						+ mDatagramSocketAzPlot.getLocalPort());
				logger.info("UDP (mDatagramSocketElPlot)socket created at IP:"
						+ mDatagramSocketElPlot.getLocalAddress() + " PORT: "
						+ mDatagramSocketElPlot.getLocalPort());
				logger.info("UDP (mDatagramSocketAzTrack)socket created at IP:"
						+ mDatagramSocketAzTrack.getLocalAddress() + " PORT: "
						+ mDatagramSocketAzTrack.getLocalPort());
				logger.info("UDP (mDatagramSocketElTrack)socket created at IP:"
						+ mDatagramSocketElTrack.getLocalAddress() + " PORT: "
						+ mDatagramSocketElTrack.getLocalPort());
				logger.info("UDP (mDatagramSocketVideo)socket created at IP:"
						+ mDatagramSocketVideo.getLocalAddress() + " PORT: "
						+ mDatagramSocketVideo.getLocalPort());
				logger.info("UDP (mDatagramSocketStatus)socket created at IP:"
						+ mDatagramSocketStatus.getLocalAddress() + " PORT: "
						+ mDatagramSocketStatus.getLocalPort());
				logger.info("UDP (mDatagramSocketWrite)socket created at IP:"
						+ mDatagramSocketWrite.getLocalAddress() + " PORT: "
						+ mDatagramSocketWrite.getLocalPort());
			} else if (Constance.MCUDP) {
				mMCSocketAzPlot = new MulticastSocket(Constance.PORT_AZ_PLOTS);
				mMCSocketElPlot = new MulticastSocket(Constance.PORT_EL_PLOTS);
				mMCSocketAzTrack = new MulticastSocket(Constance.PORT_AZ_TRACKS);
				mMCSocketElTrack = new MulticastSocket(Constance.PORT_EL_TRACKS);
				mMCSocketVideo = new MulticastSocket(Constance.PORT_VIDEO);
				mMCSocketStatus = new MulticastSocket(Constance.PORT_STATUS);
				mMCSocketWrite = new MulticastSocket(Constance.PORT_WRITE);

				groupAddr = InetAddress.getByName(Constance.GROUP_ADDR);
				mMCSocketAzPlot.joinGroup(groupAddr);
				mMCSocketElPlot.joinGroup(groupAddr);
				mMCSocketAzTrack.joinGroup(groupAddr);
				mMCSocketElTrack.joinGroup(groupAddr);
				mMCSocketVideo.joinGroup(groupAddr);
				mMCSocketStatus.joinGroup(groupAddr);
				mMCSocketWrite.joinGroup(groupAddr);

				mMCSocketAzPlot.setInterface(InetAddress.getLocalHost());
				mMCSocketAzTrack.setInterface(InetAddress.getLocalHost());
				mMCSocketElPlot.setInterface(InetAddress.getLocalHost());
				mMCSocketElTrack.setInterface(InetAddress.getLocalHost());
				mMCSocketVideo.setInterface(InetAddress.getLocalHost());
				mMCSocketStatus.setInterface(InetAddress.getLocalHost());

				logger.info("MC-UDP (mMCSocketAzPlot)socket created at IP:"
						+ mMCSocketAzPlot.getInterface() + " PORT: "
						+ mMCSocketAzPlot.getLocalPort());
				logger.info("MC-UDP (mMCSocketElPlot)socket created at IP:"
						+ mMCSocketElPlot.getInterface() + " PORT: "
						+ mMCSocketElPlot.getLocalPort());
				logger.info("MC-UDP (mMCSocketAzTrack)socket created at IP:"
						+ mMCSocketAzTrack.getInterface() + " PORT: "
						+ mMCSocketAzTrack.getLocalPort());
				logger.info("MC-UDP (mMCSocketElTrack)socket created at IP:"
						+ mMCSocketElTrack.getInterface() + " PORT: "
						+ mMCSocketElTrack.getLocalPort());
				logger.info("MC-UDP (mMCSocketVideo)socket created at IP:"
						+ mMCSocketVideo.getInterface() + " PORT: "
						+ mMCSocketVideo.getLocalPort());
				logger.info("MC-UDP (mMCSocketStatus)socket created at IP:"
						+ mMCSocketStatus.getInterface() + " PORT: "
						+ mMCSocketStatus.getLocalPort());
				logger.info("MC-UDP (mMCSocketWrite)socket created at IP:"
						+ mMCSocketWrite.getInterface() + " PORT: "
						+ mMCSocketWrite.getLocalPort());
			}

		} catch (IOException e) {
			logger.error("Socket creation failed", e);
		}
	}

	public void closeActiveConnection() {
		if (Constance.TCPIP) {
			try {
				mSocketAzPlot.close();
				mSocketElPlot.close();
				mSocketAzTrack.close();
				mSocketElTrack.close();
				mSocketVideo.close();
				mSocketStatus.close();
				mSocketWrite.close();
				logger.info("Network Task TCP socket Closed/Ended");
			} catch (IOException e) {
				logger.error("TCP socket close failed", e);
			}
		} else if (Constance.UDPIP) {
			try {
				mDatagramSocketAzPlot.close();
				mDatagramSocketElPlot.close();
				mDatagramSocketAzTrack.close();
				mDatagramSocketElTrack.close();
				mDatagramSocketVideo.close();
				mDatagramSocketStatus.close();
				mDatagramSocketWrite.close();
				logger.info("Network Task UDP socket Closed/Ended");
			} catch (Exception e) {
				logger.error("UDP socket close failed", e);
			}
		} else if (Constance.MCUDP) {
			try {
				mMCSocketAzPlot.close();
				mMCSocketElPlot.close();
				mMCSocketAzTrack.close();
				mMCSocketElTrack.close();
				mMCSocketVideo.close();
				mMCSocketStatus.close();
				mMCSocketWrite.close();
				logger.info("Network Task MC-UDP socket Closed/Ended");
			} catch (Exception e) {
				logger.error("MC-UDP socket close failed", e);
			}
		}
	}

	public void InterruptableUDPThread() {
		try {
			this.mDatagramSocketAzPlot = new DatagramSocket();
			this.mDatagramSocketElPlot = new DatagramSocket();
			this.mDatagramSocketAzTrack = new DatagramSocket();
			this.mDatagramSocketElTrack = new DatagramSocket();
			this.mDatagramSocketVideo = new DatagramSocket();
			this.mDatagramSocketStatus = new DatagramSocket();
			this.mDatagramSocketWrite = new DatagramSocket();
		} catch (SocketException e) {
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

		if (Constance.TCPIP) {
			DataOutputStream mServerSocketOutPacket = new DataOutputStream(
					mSocketWrite.getOutputStream());
			mServerSocketOutPacket.writeInt(len);
			if (len > 0)
				mServerSocketOutPacket.write(data);
			logger.info("TCP Broadcast Socket Data Sent: " + len);
		} else if (Constance.UDPIP) {
			DatagramPacket mDatagramOutPacket = new DatagramPacket(data, len);
			mDatagramOutPacket.setLength(len);
			if (len > 0)
				mDatagramSocketWrite.send(mDatagramOutPacket);
			logger.info("UDP Broadcast Socket Data sent: " + len);
		} else if (Constance.MCUDP) {
			DatagramPacket mDatagramOutPacket = new DatagramPacket(data, len,
					groupAddr, Constance.PORT_WRITE);
			mDatagramOutPacket.setLength(len);
			if (len > 0)
				mMCSocketWrite.send(mDatagramOutPacket);
			logger.info("MC-UDP Broadcast Socket Data sent: " + len);
				
		}
	}

	
	public byte[] receiveBytes() throws IOException {
		logger.info("Listening Peer data... ");
		if (Constance.TCPIP) {
			DataInputStream mServerSocketInPacket = new DataInputStream(
					mSocketWrite.getInputStream());
			int len = mServerSocketInPacket.readInt();
			byte[] tcpdata = new byte[len];
			// Wait to receive a socket data
			if (len > 0) {
				mServerSocketInPacket.readFully(tcpdata);
				return tcpdata;
			}
			logger.info("TCP Server Data Rx: " + len);
		} else if (Constance.UDPIP) {
			byte[] mUDPSocketbuffer = new byte[20];
			int len = mUDPSocketbuffer.length;
			DatagramPacket mDatagramInPacket = new DatagramPacket(
					mUDPSocketbuffer, len);
			// Wait to receive a datagram
			// mDatagramSocketWrite.setSoTimeout(3*Constance.MILLI_SECOND);
			mDatagramSocketWrite.receive(mDatagramInPacket);
			logger.info("UDP Server Data Rx: " + len);
			return mUDPSocketbuffer;
		} else if (Constance.MCUDP) {
			byte[] mMCUDPSocketbuffer = new byte[28];
			int len = mMCUDPSocketbuffer.length;
			DatagramPacket mDatagramInPacket = new DatagramPacket(
					mMCUDPSocketbuffer, len);
			// Wait to receive a datagram
			// mMCSocketWrite.setSoTimeout(10*Constance.MILLI_SECOND);
			mMCSocketWrite.receive(mDatagramInPacket);
			logger.info("MC-UDP Server Data Rx: " + len);
			for(int i=0;i<mMCUDPSocketbuffer.length;i++){
			System.out.println("Recieved data:"+mMCUDPSocketbuffer[i]);	
		     }
			return mMCUDPSocketbuffer;
		}
		return null;
	}

	private byte[] parseTCPData(Socket socket) throws IOException {

		DataInputStream mServerSocketInPacket = new DataInputStream(
				socket.getInputStream());
		int len = mServerSocketInPacket.readInt();
		byte[] tcpdata = new byte[len];
		// Wait to receive a socket data
		if (len > 0) {
			mServerSocketInPacket.readFully(tcpdata);
			// logger.info("TCP Server Data received: "+len);
			return tcpdata;
		}
		return null;
	}

	private byte[] parseUDPData(DatagramSocket datagramSocket)
			throws IOException {

		byte[] mUDPSocketbuffer = new byte[Constance.DGRAM_LEN];
		DatagramPacket mDatagramInPacket = new DatagramPacket(mUDPSocketbuffer,
				mUDPSocketbuffer.length);
		// Wait to receive a datagram
		datagramSocket.receive(mDatagramInPacket);
		// logger.info("UDP Server Data received: "+len);
		return mUDPSocketbuffer;
	}

	private byte[] parseMCUDPData(MulticastSocket multicastSocket)
			throws IOException {

		byte[] mMCUDPSocketbuffer = new byte[4 * Constance.DGRAM_LEN];
		int len = mMCUDPSocketbuffer.length;
		DatagramPacket mDatagramInPacket = new DatagramPacket(
				mMCUDPSocketbuffer, len);
		// Wait to receive a datagram
		multicastSocket.receive(mDatagramInPacket);
		// logger.info("MC-UDP Server Data received");
		return mMCUDPSocketbuffer;
	}

	private void makeData(byte[] mData) {
		// logger.info("Incoming: "+Utils.bytesToHex(mData));

		// identify data
		final String msgName = DataIdentifier.getMessageType(mData);
		logger.info("Server Data Identified: " + msgName);

		// decode data
		if (msgName != null) {
			Object object = mDataSupervisor.decodeMsg(msgName, mData);
			// logger.info("Server Data Decoded");
			if (object instanceof AzimuthPlanePlotsPerCPIMsg) {
				AzimuthPlanePlotsPerCPIMsg aPlotsPerCPIMsg = (AzimuthPlanePlotsPerCPIMsg) object;
				//logger.info("Server Data AzimuthPlanePlotsPerCPIMsg added: "
				//		+ aPlotsPerCPIMsg.toString());
				// add data
				mDataManager.addAzPlots(aPlotsPerCPIMsg);

				// record data
				if (Constance.IS_RECORD_SETUP
						&& AppConfig.getInstance().getReplayController()
								.isRecordAzPlots()) {

					try {
						mAzPlotRecordQ.put(new LiquidStream(mData));
						++azPlot;
						logger.info("Az Plot Data recorded: " + azPlot);
					} catch (InterruptedException e) {
						logger.error(e);
					}
					// if (DBRecord.getInstance().writeAzPlotToDB(mData)) {
					// ++azPlot;
					// logger.info("Az Plot Data recorded: " + azPlot);
					// }
				}
			} else if (object instanceof AzimuthPlaneTrackMsg) {
				AzimuthPlaneTrackMsg aTrackMsg = (AzimuthPlaneTrackMsg) object;
				//logger.info("Server Data AzimuthPlaneTrackMsg added: "
						//+ aTrackMsg.toString());
				// add data
				mDataManager.addAzTracks(aTrackMsg);
				azLatestTrackNoList.add(aTrackMsg.getTrackName());

				// record data
				if (Constance.IS_RECORD_SETUP
						&& AppConfig.getInstance().getReplayController()
								.isRecordAzTracks()) {
					try {
						mAzTrackRecordQ.put(new LiquidStream(mData));
						++azTrack;
						logger.info("Az Track Data recorded: " + azTrack);
					} catch (InterruptedException e) {
						logger.error(e);
					}

					// if (DBRecord.getInstance().writeAzTrackToDB(mData)) {
					// ++azTrack;
					// logger.info("Az Track Data recorded: " + azTrack);
					// }
				}
			} else if (object instanceof ElevationPlanePlotsPerCPIMsg) {
				ElevationPlanePlotsPerCPIMsg ePlotsPerCPIMsg = (ElevationPlanePlotsPerCPIMsg) object;
				//logger.info("Server Data ElevationPlanePlotsPerCPIMsg added: "
						//+ ePlotsPerCPIMsg.toString());
				// add data
				mDataManager.addElPlots(ePlotsPerCPIMsg);

				// record data
				if (Constance.IS_RECORD_SETUP
						&& AppConfig.getInstance().getReplayController()
								.isRecordElPlots()) {
					try {
						mElPlotRecordQ.put(new LiquidStream(mData));
						++elPlot;
						logger.info("El Plot Data recorded: " + elPlot);
					} catch (InterruptedException e) {
						logger.error(e);
					}
					// if (DBRecord.getInstance().writeElPlotToDB(mData)) {
					// ++elPlot;
					// logger.info("El Plot Data recorded: " + elPlot);
					// }
				}
			} else if (object instanceof ElevationPlaneTrackMsg) {
				ElevationPlaneTrackMsg eTrackMsg = (ElevationPlaneTrackMsg) object;
				//logger.info("Server Data ElevationPlaneTrackMsg added: "
						//+ eTrackMsg.toString());
				// add data
				mDataManager.addElTracks(eTrackMsg);
				if(eTrackMsg.getTrackStatus()==4)
				    elLatestTrackNoList.add(eTrackMsg.getReserved());

				// record data
				if (Constance.IS_RECORD_SETUP
						&& AppConfig.getInstance().getReplayController()
								.isRecordElTracks()) {
					try {
						mElTrackRecordQ.put(new LiquidStream(mData));
						++elTrack;
						logger.info("El Track Data recorded: " + elTrack);
					} catch (InterruptedException e) {
						logger.error(e);
					}
					// if(DBRecord.getInstance().writeElTrackToDB(mData)) {
					// ++elTrack;
					// logger.info("El Track Data recorded: "+elTrack);
					// }
				}
			} else if (object instanceof PlaneRAWVideoMsg) {
				PlaneRAWVideoMsg vidMsg = (PlaneRAWVideoMsg) object;
				// logger.info("Server Data PlaneRAWVideoMsg added: "+vidMsg.toString());

				// add data video
				mDataManager.addRAWVideo(vidMsg);

			} else if (object instanceof GpsMsg) {
				GpsMsg stateMsg = (GpsMsg) object;
				logger.info("Server Data GPSMsg added: " + stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController()
						.updateGpsStatus(stateMsg);
				if(Constance.IS_LOGGING_SETUP) {
					AppConfig.getInstance().getLoggingSetUpController().appendAzLine(stateMsg.toString());
					AppConfig.getInstance().getLoggingSetUpController().appendElLine(stateMsg.toString());
				}

			} else if (object instanceof WindSensorMsg) {
				WindSensorMsg stateMsg = (WindSensorMsg) object;
				logger.info("Server Data WindSensorMsg added: "
						+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController()
						.updateWindStatus(stateMsg);

			} else if (object instanceof RunwayMsg) {
				RunwayMsg stateMsg = (RunwayMsg) object;
				logger.info("Server Data RunwayMsg added: "
						+ stateMsg.toString());

				// notify UI directly
				Constance.PREF.SEL_RUNWAY = String.valueOf(stateMsg.getRunwayNo());
				UserPreference.getInstance().setOP_RUNWAY(Constance.PREF.SEL_RUNWAY);
				AppConfig.getInstance().getFxmlController().flipGraphOnRunwaySel(Constance.PREF.SEL_RUNWAY);
				AppConfig.getInstance().getFxmlController().initUIComponents(Constance.PREF.LEFT_RIGHT);
				AppConfig.getInstance().getFxmlController().notifyChanges();
				AppConfig.getInstance().openInformationDialog("Runway Changed to: "+Constance.PREF.SEL_RUNWAY);

			} else if (object instanceof PcReadStatusMsg) {
				PcReadStatusMsg stateMsg = (PcReadStatusMsg) object;
				logger.info("Server Data PcReadStatusMsg added: "
						+ stateMsg.toString());

				// notify UI directly
				if(Constance.InstallStatus==1){
				AppConfig.getInstance().getInstallationController()
						.updateReadPc(stateMsg);
				}
			}else if (object instanceof CornerReflectorMsg) {
				CornerReflectorMsg stateMsg = (CornerReflectorMsg) object;
				logger.info("Server CornerReflectorStatus added: "+ stateMsg.toString());
				
			}else if (object instanceof ZoneSuppression) {
				ZoneSuppression stateMsg = (ZoneSuppression) object;
				logger.info("Server ZoneDelet added: "+ stateMsg.toString());
				
				// notify UI directly
			  if(msgName.equals("ZoneSuppressionMsgClass"))
				AppConfig.getInstance().getFxmlController().updateZoneActivity(stateMsg);
			  else if(msgName.equals("ZoneSuppressionMsgClassEL"))
			    AppConfig.getInstance().getFxmlController().updateZoneActivityEL(stateMsg); 
				
	    	}else if(object instanceof ZoneUpdateMsg){
				ZoneUpdateMsg stateMsg= (ZoneUpdateMsg)object;
				logger.info("Zone update Msg Added: "+ stateMsg.toString());
				if(msgName.equals("ZoneUpdateMsg"))
				    AppConfig.getInstance().getFxmlController().addZoneValues(stateMsg);
				else if(msgName.equals("ZoneUpdateMsgEL"))
					AppConfig.getInstance().getFxmlController().addZoneValuesEL(stateMsg);
			}
			else if (object instanceof HealthStatusMsg) {
				HealthStatusMsg stateMsg = (HealthStatusMsg) object;
				logger.info("Server Data HealthStatusMsg added: "
						+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController()
						.updateStatus(stateMsg);
				
				AppConfig.getInstance().getFxmlController()
				.updateStatusVoice(stateMsg);

			} else if (object instanceof PwrHealthStatusMsg) {
				PwrHealthStatusMsg stateMsg = (PwrHealthStatusMsg) object;
				logger.info("Server Data PwrHealthStatusMsg added: "
						+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController()
						.updatePwrStatus(stateMsg);

			} else if (object instanceof NoiseFigureStatusMsg) {
				NoiseFigureStatusMsg stateMsg = (NoiseFigureStatusMsg) object;
				logger.info("Server Data NoiseFigureStatusMsg added: "
						+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController()
						.updateNoiseFigStatus(stateMsg);

			} else if (object instanceof VSWRHealthStatusMsg) {
				VSWRHealthStatusMsg stateMsg = (VSWRHealthStatusMsg) object;
				logger.info("Server Data VSWRHealthStatusMsg added: "+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController().updateVswrStatus(stateMsg);

			} else if (object instanceof TempHumHealthStatusMsg) {
				TempHumHealthStatusMsg stateMsg = (TempHumHealthStatusMsg) object;
				logger.info("Server Data TempHumHealthStatusMsg added: "+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController().updateTempHumStatus(stateMsg);
				
			} else if (object instanceof ExRxHealthStatusMsg) {
				ExRxHealthStatusMsg stateMsg = (ExRxHealthStatusMsg) object;
				logger.info("Server Data ExRxHealthStatusMsg added: "+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController().updateExRxStatus(stateMsg);

			} else if (object instanceof AntHealthStatusMsg) {
				AntHealthStatusMsg stateMsg = (AntHealthStatusMsg) object;
				logger.info("Server Data AzAntHealthStatusMsg added: "+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController().updateAntennaStatus(stateMsg);

			} else if (object instanceof ElAntHealthStatusMsg) {
				ElAntHealthStatusMsg stateMsg = (ElAntHealthStatusMsg) object;
				logger.info("Server Data ElAntHealthStatusMsg added: "+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController().updateAntennaStatus(stateMsg);

			} else if (object instanceof SDPHealthStatusMsg) {
				SDPHealthStatusMsg stateMsg = (SDPHealthStatusMsg) object;
				logger.info("Server Data SdpHealthStatusMsg added: "+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController().updateSdpStatus(stateMsg);

			} else if (object instanceof RRMHealthStatusMsg) {
				RRMHealthStatusMsg stateMsg = (RRMHealthStatusMsg) object;
				logger.info("Server Data RRMHealthStatusMsg added: "+ stateMsg.toString());

				// notify UI directly


			} else if (object instanceof PCHealthStatusMsg) {
				PCHealthStatusMsg stateMsg = (PCHealthStatusMsg) object;
				logger.info("Server Data PCHealthStatusMsg added: "+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController().updatePcStatus(stateMsg);

			} else if (object instanceof LANHealthStatusMsg) {
				LANHealthStatusMsg stateMsg = (LANHealthStatusMsg) object;
				logger.info("Server Data LANHealthStatusMsg added: "+ stateMsg.toString());

				// notify UI directly
				AppConfig.getInstance().getFxmlController().updateLanStatus(stateMsg);

			} else if (object instanceof ElevationScanStartMessage) {
				ElevationScanStartMessage eScanStartMessage = (ElevationScanStartMessage) object;
				logger.info("Server Data ElevationScanStartMessage rx: "
						+ eScanStartMessage.toString());
				
				// clear el plane tracks				
				AppConfig.getInstance().getFxmlController()
						.setElTrackRefresh(true);// notify UI to draw
				AppConfig.getInstance().getFxmlController()
						.setElPlotRefresh(true);// notify UI to draw
				AppConfig.getInstance().getFxmlController().updateElScanStatus(eScanStartMessage);
				
				if(Constance.IS_LOGGING_SETUP)
					AppConfig.getInstance().getLoggingSetUpController().appendElLine(eScanStartMessage.toString());
				
				Track trackEl=new Track();
				trackEl.updateElcommonIDListWithTracks(elLatestTrackNoList);
		        elLatestTrackNoList.clear();
				 //update tracks to recents ones
             
	               if(!AppConfig.getInstance().getFxmlController().isAccumulating()) {

	                                mDataManager.updateElNewTracksList();

	                                mDataManager.copyElPrevScanTrackIdList();
	               }

			} else if (object instanceof AzimuthScanStartMessage) {
				AzimuthScanStartMessage aScanStartMessage = (AzimuthScanStartMessage) object;
				logger.info("Server Data AzimuthScanStartMessage rx: "
						+ aScanStartMessage.toString());
				
				// clear az plane tracks
				AppConfig.getInstance().getFxmlController()
						.setAzTrackRefresh(true);// notify UI to draw
				AppConfig.getInstance().getFxmlController()
						.setAzPlotRefresh(true);// notify UI to draw
				AppConfig.getInstance().getFxmlController().updateAzScanStatus(aScanStartMessage);

				if(Constance.IS_LOGGING_SETUP)
					AppConfig.getInstance().getLoggingSetUpController().appendAzLine(aScanStartMessage.toString());
				
			
				  //update tracks to recents ones

                if(!AppConfig.getInstance().getFxmlController().isAccumulating()) {

                                mDataManager.updateAzNewTracksList();

                                mDataManager.copyAzPrevScanTrackIdList();

                }
				    
			}
		}
	}

	public void replayELPData() {

		if (DBRecord.getInstance().isElPlotTableExist()) {
			Thread replayELPData = new Thread(new Runnable() {

				@Override
				public void run() {

					logger.info("Starting Replay Thread Looper");
					ResultSet resultSet = DBRecord.getInstance()
							.readElPlotTable();
					try {
						int count = 0;
						while (resultSet.next()) {

							while (Constance.IS_PAUSE_SETUP) {
								TimeUnit.MILLISECONDS.sleep(100);
							}
							;

							byte[] mData = resultSet.getBytes("OBJECT");
							makeData(mData);
							TimeUnit.MICROSECONDS.sleep(Constance.PLAY_SPEED);
							if (!Constance.IS_REPLAY_SETUP)
								break;
							++count;
							AppConfig.getInstance().getFxmlController()
									.setElPlotRefresh(true);// notify UI to draw
						}
						logger.info("El Plot Data Replayed: " + count);

						AppConfig.getInstance().getReplayController()
								.stopAction();
					} catch (SQLException e) {
						logger.error(e);
					} catch (InterruptedException e) {
						logger.error(e);
					}
					logger.info("Ending Replay Thread Looper");
				}
			});
			replayELPData.start();
		}
	}

	public void replayELTData() {

		if (DBRecord.getInstance().isElTrackTableExist()) {
			Thread replayELTData = new Thread(new Runnable() {

				@Override
				public void run() {

					logger.info("Starting Replay Thread Looper");
					ResultSet resultSet = DBRecord.getInstance()
							.readElTrackTable();
					try {
						int count = 0;
						while (resultSet.next()) {
							mDataManager.clearElTrackData();//imp to clear everytime, before loading
							while (Constance.IS_PAUSE_SETUP) {
								TimeUnit.MILLISECONDS.sleep(100);
							}
							;

							byte[] mData = resultSet.getBytes("OBJECT");
							makeData(mData);
							TimeUnit.MICROSECONDS.sleep(Constance.PLAY_SPEED);
							if (!Constance.IS_REPLAY_SETUP)
								break;
							++count;
							AppConfig.getInstance().getFxmlController()
									.setElTrackRefresh(true);// notify UI to
																// draw
						}
						logger.info("El Track Data Replayed: " + count);

						AppConfig.getInstance().getReplayController()
								.stopAction();
					} catch (SQLException e) {
						logger.error(e);
					} catch (InterruptedException e) {
						logger.error(e);
					}
					logger.info("Ending Replay Thread Looper");
				}
			});
			replayELTData.start();
		}
	}

	public void replayAZPData() {

		if (DBRecord.getInstance().isAzPlotTableExist()) {
			Thread replayAZPData = new Thread(new Runnable() {

				@Override
				public void run() {

					logger.info("Starting Replay Thread Looper");
					ResultSet resultSet = DBRecord.getInstance()
							.readAzPlotTable();
					try {
						int count = 0;
						while (resultSet.next()) {

							while (Constance.IS_PAUSE_SETUP) {
								TimeUnit.MILLISECONDS.sleep(100);
							}
							;

							byte[] mData = resultSet.getBytes("OBJECT");
							makeData(mData);
							TimeUnit.MICROSECONDS.sleep(Constance.PLAY_SPEED);
							if (!Constance.IS_REPLAY_SETUP)
								break;
							++count;
							AppConfig.getInstance().getFxmlController()
									.setAzPlotRefresh(true);// notify UI to draw
						}
						logger.info("Az Plot Data Replayed: " + count);

						AppConfig.getInstance().getReplayController()
								.stopAction();
					} catch (SQLException e) {
						logger.error(e);
					} catch (InterruptedException e) {
						logger.error(e);
					}
					logger.info("Ending Replay Thread Looper");
				}
			});
			replayAZPData.start();
		}
	}

	public void replayAZTData() {

		if (DBRecord.getInstance().isAzTrackTableExist()) {
			Thread replayAZTData = new Thread(new Runnable() {

				@Override
				public void run() {

					logger.info("Starting Replay Thread Looper");
					ResultSet resultSet = DBRecord.getInstance()
							.readAzTrackTable();
					try {
						int count = 0;
						while (resultSet.next()) {
							mDataManager.clearAzTrackData();//imp to clear everytime, before loading
							while (Constance.IS_PAUSE_SETUP) {
								TimeUnit.MILLISECONDS.sleep(100);
							}
							;

							byte[] mData = resultSet.getBytes("OBJECT");
							makeData(mData);
							TimeUnit.MICROSECONDS.sleep(Constance.PLAY_SPEED);
							if (!Constance.IS_REPLAY_SETUP)
								break;
							++count;
							AppConfig.getInstance().getFxmlController()
									.setAzTrackRefresh(true);// notify UI to
																// draw
						}
						logger.info("Az Track Data Replayed: " + count);

						AppConfig.getInstance().getReplayController()
								.stopAction();
					} catch (SQLException e) {
						logger.error(e);
					} catch (InterruptedException e) {
						logger.error(e);
					}
					logger.info("Ending Replay Thread Looper");
				}
			});
			replayAZTData.start();
		}
	}

}