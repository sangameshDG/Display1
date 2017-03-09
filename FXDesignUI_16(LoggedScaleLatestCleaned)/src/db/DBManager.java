package db;

import java.sql.SQLException;

import org.h2.tools.Server;

public class DBManager {

	public static void startDB() throws SQLException {
		Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
	}

	public static void stopDB() throws SQLException {
		Server.shutdownTcpServer("tcp://localhost:9092", "", true, true);
	}
}