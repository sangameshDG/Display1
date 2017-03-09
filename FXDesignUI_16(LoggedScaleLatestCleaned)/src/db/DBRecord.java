package db;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.AppConfig;

import org.apache.log4j.Logger;

import utils.AppUtils;
import utils.Constance;
import admin.User;

public class DBRecord {
	
	private static final Logger logger = Logger.getLogger(DBRecord.class);
	private static DBRecord instance = null;
	
	public static DBRecord getInstance() {
	      if(instance == null) {
	         instance = new DBRecord();
	         logger.info("DBRecord Instantiated");
	      }
	      return instance;
	}
	
	public static final String TBL_AZ_T = "FXDB_AZ_TRACK";
	public static final String TBL_AZ_P = "FXDB_AZ_PLOT";
	public static final String TBL_EL_T = "FXDB_EL_TRACK";
	public static final String TBL_EL_P = "FXDB_EL_PLOT";
	
	public static final String TBL_ADMIN = "FXDB_ADMIN";
	public static final String TBL_USERS = "FXDB_USERS";
	
	private Connection connection;
	private Statement statement;
	
	private Connection connectionServer;
	private Statement statementServer;

	File db;
	BufferedOutputStream bos = null;
	BufferedReader br = null;
		
	public DBRecord() {
		//Write to H2 DB
		String dbUrlLocal = null;
		try {
			dbUrlLocal = AppUtils.getProgramPath()+File.separator+"fxdbLocal";
			logger.info("DB Program Path: "+dbUrlLocal);
		} catch (UnsupportedEncodingException e1) {
			logger.error(e1);
		}
       	dbUrlLocal = "jdbc:h2:" + dbUrlLocal;
        
        String dbDriverClass = "org.h2.Driver";
        String userName = "root";
        String password = "root";

        try {
			Class.forName(dbDriverClass);
	        connection = DriverManager.getConnection(dbUrlLocal, userName, password);
	        statement = connection.createStatement();
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
	}
	
	private void initServerDb() {
		//Write to H2 DB
		String dbUrlServer = null;
		try {
			dbUrlServer = AppUtils.getProgramPath()+File.separator+"fxdbServer";
			logger.info("DB Program Path: "+dbUrlServer);
		} catch (UnsupportedEncodingException e1) {
			logger.error(e1);
		}
       	dbUrlServer = "jdbc:h2:" + dbUrlServer;
        
        String dbDriverClass = "org.h2.Driver";
        String userName = "root";
        String password = "root";

        try {
			Class.forName(dbDriverClass);
	        connectionServer = DriverManager.getConnection(dbUrlServer, userName, password);
	        statementServer = connectionServer.createStatement();
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
	}
	
    private void closeServerDB() {
        try {
        	if(statementServer!=null)
        		statementServer.close();
        	if(connectionServer!=null)
        		connectionServer.close();
		} catch (SQLException e) {
			logger.error(e);
		}
    }
	
	public boolean clearUsersDB() {
		initServerDb();
    	try {
			statementServer.execute("drop table if exists "+TBL_USERS);
	    	closeServerDB();
			return true;
		} catch (SQLException e) {
			logger.error(e);
		}
    	return false;
	}
	
	public boolean isUserTableExist() {
		initServerDb();
		try {
			ResultSet resultSet;
			resultSet = connectionServer.getMetaData().getTables(null, null, TBL_USERS, null);
			if(resultSet.next()) {
				closeServerDB();
				return true;
			}
		} catch (SQLException e) {
			logger.error(e);
		}
		return false;
	}
	
	public boolean createUserDataToDb(User user) {
		initServerDb();
		try {
            statementServer.execute("create table if not exists "+TBL_USERS+"(ID varchar(255), NAME varchar(255), USERNAME varchar(255), PASSWORD varchar(255), TYPE varchar(255) )");
            PreparedStatement prep = connectionServer.prepareStatement("insert into "+TBL_USERS+" (ID, NAME, USERNAME, PASSWORD, TYPE) values (?,?,?,?,?)");
            prep.setString(1, user.getId());
            prep.setString(2, user.getFullName());
            prep.setString(3, user.getUsername());
            prep.setString(4, user.getPassword());
            prep.setString(5, user.getType());
            prep.execute();
            closeServerDB();
            return true;
        } catch (Exception e) {
        	logger.error(e);
        }
        return false;
	}	
	
	public ObservableList<User> readUsersTable() {
		initServerDb();
    	try {
            ResultSet results = statementServer.executeQuery("select * from "+TBL_USERS);
            final ObservableList<User> userData = FXCollections.observableArrayList();
			while(results.next()) {
				if(!results.getString(5).equals(Constance.USER_MASTER))
					userData.add(new User(results.getString(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5)));
			}
            closeServerDB();
            return userData;
        } catch (Exception e) {
        	logger.error(e);
        }
    	return null;
    }
	
	public boolean authUser(String username, String pass) {
		initServerDb();
		try {
            ResultSet results = statementServer.executeQuery("select * from "+TBL_USERS+" where USERNAME like '%"+username+"%'");
            if(results.next()) {
            	String user = new String(results.getString(3));
            	if(user.equals(username)) {
            		String password = new String(results.getString(4));
                    if(password.equals(pass)) {
                    	User loggedUser = new User();
                    	loggedUser.setFullName(new String(results.getString(2)));
                    	loggedUser.setUsername(user);
                    	loggedUser.setPassword(password);
                    	loggedUser.setType(new String(results.getString(5)));
                    	AppConfig.getInstance().setLoggedUser(loggedUser);
                    	closeServerDB();
                    	return true;
                    } 
                    closeServerDB();
            	}
            	closeServerDB();
            }
        } catch (Exception e) {
        	logger.error(e);
        }
    	return false;
	}
	
	public boolean isUserExist(String username) {
		initServerDb();
		try {
            ResultSet results = statementServer.executeQuery("select * from "+TBL_USERS+" where USERNAME like '%"+username+"%'");
            if(results.next()) {
            	String user = new String(results.getString(3));
        		closeServerDB();
            	if(user.equals(username)) {
            		return true;
            	}
            }
        } catch (Exception e) {
        	logger.error(e);
        }
    	return false;
	}
	
	public String nextUserId() {
		initServerDb();
		try {
            ResultSet results = statementServer.executeQuery("select * from "+TBL_USERS);
            if(results.last()) {
            	String str = new String(results.getString(1));
                closeServerDB();
            	return str;
            }
        } catch (Exception e) {
        	logger.error(e);
        }
		return null;
	}
	
	public int getTotalUsers() {
		initServerDb();
		try {
            ResultSet results = statementServer.executeQuery("select * from "+TBL_USERS);
            int count = 0;
            while(results.next())
            	++count;
            closeServerDB();
            return count;
        } catch (Exception e) {
        	logger.error(e);
        }
		return -1;
	}
	
	public boolean deleteUserName(String username) {
		initServerDb();
		try {
            statementServer.execute("delete from "+TBL_USERS+" where USERNAME ='"+username+"'");
            closeServerDB();
            return true;
        } catch (Exception e) {
        	logger.error(e);
        }
		return false;
	}
	
	public boolean writeAzTrackToDB(byte[] object) {
        try {
            statement.execute("create table if not exists "+TBL_AZ_T+"(OBJECT BINARY)");
            PreparedStatement prep = connection.prepareStatement("insert into "+TBL_AZ_T+" (OBJECT) values (?)");
            prep.setBytes(1, object);
            prep.execute();
            return true;
        } catch (Exception e) {        	logger.error(e);
        }
        return false;
    }
	
	public boolean writeAzPlotToDB(byte[] object) {
        try {
            statement.execute("create table if not exists "+TBL_AZ_P+"(OBJECT BINARY)");
            PreparedStatement prep = connection.prepareStatement("insert into "+TBL_AZ_P+" (OBJECT) values (?)");
            prep.setBytes(1, object);
            prep.execute();
            return true;
        } catch (Exception e) {
        	logger.error(e);
        }
        return false;
    }
	
	public boolean writeElTrackToDB(byte[] object) {
        try {
            statement.execute("create table if not exists "+TBL_EL_T+"(OBJECT BINARY)");
            PreparedStatement prep = connection.prepareStatement("insert into "+TBL_EL_T+" (OBJECT) values (?)");
            prep.setBytes(1, object);
            prep.execute();
            return true;
        } catch (Exception e) {
        	logger.error(e);
        }
        return false;
    }
	
	public boolean writeElPlotToDB(byte[] object) {
        try {
            statement.execute("create table if not exists "+TBL_EL_P+"(OBJECT BINARY)");
            PreparedStatement prep = connection.prepareStatement("insert into "+TBL_EL_P+" (OBJECT) values (?)");
            prep.setBytes(1, object);
            prep.execute();
            return true;
        } catch (Exception e) {
        	logger.error(e);
        }
        return false;
    }
    
    public ResultSet readAzTrackTable() {
    	try {
            ResultSet results = statement.executeQuery("select * from "+TBL_AZ_T);
            return results;
        } catch (Exception e) {
        	logger.error(e);
        }
    	return null;
    }
    
    public ResultSet readAzPlotTable() {
    	try {
            ResultSet results = statement.executeQuery("select * from "+TBL_AZ_P);
            return results;
        } catch (Exception e) {
        	logger.error(e);
        }
    	return null;
    }
    
    public ResultSet readElTrackTable() {
    	try {
            ResultSet results = statement.executeQuery("select * from "+TBL_EL_T);
            return results;
        } catch (Exception e) {
        	logger.error(e);
        }
    	return null;
    }
    
    public ResultSet readElPlotTable() {
    	try {
            ResultSet results = statement.executeQuery("select * from "+TBL_EL_P);
            return results;
        } catch (Exception e) {
        	logger.error(e);
        }
    	return null;
    }

	public boolean isAzTrackTableExist() {
		DatabaseMetaData metadata;
		try {
			metadata = connection.getMetaData();
			ResultSet resultSet;
			resultSet = metadata.getTables(null, null, TBL_AZ_T, null);
			if(resultSet.next())
				return true;
		} catch (SQLException e) {
			logger.error(e);
		}
		return false;
	}
	
	public boolean isAzPlotTableExist() {
		DatabaseMetaData metadata;
		try {
			metadata = connection.getMetaData();
			ResultSet resultSet;
			resultSet = metadata.getTables(null, null, TBL_AZ_P, null);
			if(resultSet.next())
				return true;
		} catch (SQLException e) {
			logger.error(e);
		}
		return false;
	}
	
	public boolean isElTrackTableExist() {
		DatabaseMetaData metadata;
		try {
			metadata = connection.getMetaData();
			ResultSet resultSet;
			resultSet = metadata.getTables(null, null, TBL_EL_T, null);
			if(resultSet.next())
				return true;
		} catch (SQLException e) {
			logger.error(e);
		}
		return false;
	}
	
	public boolean isElPlotTableExist() {
		DatabaseMetaData metadata;
		try {
			metadata = connection.getMetaData();
			ResultSet resultSet;
			resultSet = metadata.getTables(null, null, TBL_EL_P, null);
			if(resultSet.next())
				return true;
		} catch (SQLException e) {
			logger.error(e);
		}
		return false;
	}
	
	public boolean clearRecordDB() {
    	try {
			statement.execute("drop table if exists "+TBL_AZ_T);
			statement.execute("drop table if exists "+TBL_AZ_P);
			statement.execute("drop table if exists "+TBL_EL_T);
			statement.execute("drop table if exists "+TBL_EL_P);
			return true;
		} catch (SQLException e) {
			logger.error(e);
			return false;
		}
	}
    
    public void closeLocalDB() {
        try {
	        statement.close();
			connection.close();
		} catch (SQLException e) {
			logger.error(e);
		}
    }
    
}