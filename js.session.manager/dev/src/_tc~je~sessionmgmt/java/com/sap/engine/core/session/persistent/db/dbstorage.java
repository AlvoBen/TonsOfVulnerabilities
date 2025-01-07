package com.sap.engine.core.session.persistent.db;

import com.sap.engine.session.spi.persistent.Storage;
import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.session.telnet.DBMonitoring;
import com.sap.engine.session.SessionDomain;

import com.sap.engine.core.database.DatabaseManager;
import com.sap.engine.frame.core.database.DatabaseDataSource;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.SimpleLogger;


import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DBStorage implements Storage {
  private static DatabaseDataSource dbds = null;

  private static Location loc = Location.getLocation(DBStorage.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  static {
    initConnection();
  }

  private static void initConnection() {
    DatabaseManager dbm = (DatabaseManager) Framework.getManager(Names.DATABASE_MANAGER);
    try {
      dbds = dbm.getSystemDataSource();
      DBMonitoring.dbds = dbds;
    } catch (Exception ex) {
      SimpleLogger.log(Severity.FATAL, Category.SYS_SERVER, PersistentDomainModel.LOC, "ASJ.ses.ps0001", 
	  "System Data Source is missing so failover will not work. Check the data source status.");
      log(ex);
    }
  }

  private Connection getConnection() throws SQLException {
    Connection con = dbds.getConnection();
    con.setAutoCommit(false);

    return con;
  }


  public PersistentDomainModel getDomainModel(String context, String path) throws PersistentStorageException {
    path = context + SessionDomain.SEPARATOR + path;

    return createDomainModel(context, path, -1, -1);
  }

  public PersistentDomainModel createDomainModel(String context, String path, int parentHash, int parentCounter) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;

    int hash = path.hashCode();
    int counter = -1;
    try {
      con = getConnection();
      PersistentDomainModel model = null;

      while (model == null) {
        try {
          counter++;
          insertDomain(context, path, hash, counter, parentHash, parentCounter, con);
          con.commit();
          return new DBPersistentDomainModel(context, path, hash, counter, parentHash, parentCounter, this);
        } catch (SQLException e) {
          model = selectDomain(path, con);
        }
      }

      return model;
    } catch (Exception e) {
      try {
        con.rollback();
      } catch (Exception e2) {
        log(e2);
      }
      log(e);
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }


  private void insertDomain(String context, String path, int hash, int counter, int parentHash, int parentCounter, Connection con) throws SQLException {
    PreparedStatement ps = con.prepareStatement(
            " INSERT INTO T_DOMAIN (ID_HASH, ID_COUNTER, PARENT_ID_HASH, PARENT_ID_COUNTER, CONTEXT_NAME, DOMAIN_PATH) " +
                    " VALUES (?, ?, ?, ?, ?, ?) ");
    try {
      ps.setInt(1, hash);
      ps.setInt(2, counter);

      if (parentHash == -1) {
        ps.setNull(3, Types.INTEGER);
        ps.setNull(4, Types.INTEGER);
      } else {
        ps.setInt(3, parentHash);
        ps.setInt(4, parentCounter);
      }

      ps.setString(5, context);
      ps.setString(6, path);


      ps.executeUpdate();
    } finally {
      ps.close();
    }
  }

  private DBPersistentDomainModel selectDomain(String path, Connection con) throws SQLException {
    DBPersistentDomainModel model = null;

    PreparedStatement ps = con.prepareStatement(" SELECT ID_HASH, ID_COUNTER, PARENT_ID_HASH, PARENT_ID_COUNTER, CONTEXT_NAME " +
            " FROM T_DOMAIN " +
            " WHERE DOMAIN_PATH = ? ");

    try {
      ps.setString(1, path);

      ResultSet set = ps.executeQuery();
      if (set.next()) {
        int hash = set.getInt(1);
        int counter = set.getInt(2);

        int parentHash = set.getInt(3);
        if (set.wasNull()) {
          parentHash = -1;
        }

        int parentCounter = set.getInt(4);
        if (set.wasNull()) {
          parentCounter = -1;
        }

        String context = set.getString(5);

        model = new DBPersistentDomainModel(context, path, hash, counter, parentHash, parentCounter, this);
      }
      set.close();
    } finally {
      ps.close();
    }

    return model;
  }


  public ArrayList<DBPersistentDomainModel> getSubdomains(int parentHash, int parentCounter) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;

    try {
      con = getConnection();

      return selectSubdomains(parentHash, parentCounter, con);
    } catch (Exception e) {
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }


  private ArrayList<DBPersistentDomainModel> selectSubdomains(int parentHash, int parentCounter, Connection con) throws SQLException {
    ArrayList<DBPersistentDomainModel> subdomains = new ArrayList<DBPersistentDomainModel>();

    PreparedStatement ps = null;
    String SQL;
    try {
    if (parentCounter == -1) {
      SQL = " SELECT ID_HASH, ID_COUNTER, CONTEXT_NAME, DOMAIN_PATH " +
              " FROM T_DOMAIN " +
              " WHERE PARENT_ID_HASH IS NULL AND " +
              "       PARENT_ID_COUNTER IS NULL ";

      ps = con.prepareStatement(SQL);
    } else {
      SQL = " SELECT ID_HASH, ID_COUNTER, CONTEXT_NAME, DOMAIN_PATH " +
            " FROM T_DOMAIN " +
            " WHERE PARENT_ID_HASH = ? AND " +
            "       PARENT_ID_COUNTER = ? ";

      ps = con.prepareStatement(SQL);
      ps.setInt(1, parentHash);
      ps.setInt(2, parentCounter);
    }

    ResultSet set = ps.executeQuery();
    while (set.next()) {
      int hash = set.getInt(1);
      int counter = set.getInt(2);
      String context = set.getString(3);
      String path = set.getString(4);

      DBPersistentDomainModel model = new DBPersistentDomainModel(context, path, hash, counter, parentHash, parentCounter, this);

      subdomains.add(model);
    }

    set.close();
    } finally {
      if (ps != null)
        ps.close();
    }

    return subdomains;
  }

  public void removeDomainModel(int hash, int counter) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;

    try {
      con = getConnection();

      this.deleteDomain(hash, counter, con);
      con.commit();
    } catch (Exception e) {
      try {
        if (con != null) {
          con.rollback();
        }
      } catch (Exception e2) {
        log(e2);
      }
      log(e);
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }

  private void deleteDomain(int hash, int counter, Connection con) throws SQLException {
    PreparedStatement deleteDomain = con.prepareStatement(" DELETE FROM T_DOMAIN " +
            " WHERE ID_HASH = ? AND " +
            "       ID_COUNTER = ? ");

    PreparedStatement selectSubdomains = con.prepareStatement(" SELECT ID_HASH, ID_COUNTER " +
            " FROM T_DOMAIN " +
            " WHERE PARENT_ID_HASH = ? AND " +
            "       PARENT_ID_COUNTER = ? ");

    try {
      Stack<Integer> stackHash = new Stack<Integer>();
      stackHash.push(hash);

      Stack<Integer> stackCounter = new Stack<Integer>();
      stackCounter.push(counter);

      while (!stackHash.isEmpty()) {
        hash = stackHash.pop();
        counter = stackCounter.pop();

        deleteSessionsForDomain(hash, counter, con);

        deleteDomain.clearParameters();
        deleteDomain.setInt(1, hash);
        deleteDomain.setInt(2, counter);
        deleteDomain.executeUpdate();

        selectSubdomains.clearParameters();
        selectSubdomains.setInt(1, hash);
        selectSubdomains.setInt(2, counter);
        ResultSet res = selectSubdomains.executeQuery();
        while (res.next()) {
          hash = res.getInt(1);
          stackHash.push(hash);
          counter = res.getInt(1);
          stackCounter.push(counter);
        }
        res.close();
      }
    } finally {
      selectSubdomains.close();
      deleteDomain.close();
    }
  }


  public static void log(Throwable t) {
    loc.logT(Severity.ERROR, t.getMessage());
  }


  private void deleteSessionsForDomain(int domainHash, int domainCounter, Connection con) throws SQLException {
    PreparedStatement deleteSessionInfo = con.prepareStatement(" DELETE " +
            " FROM T_SESSION " +
            " WHERE DOMAIN_ID_HASH = ? AND " +
            "       DOMAIN_ID_COUNTER = ? ");

    PreparedStatement deleteSessionChunks = con.prepareStatement(" DELETE " +
            " FROM T_CHUNK " +
            " WHERE DOMAIN_HASH = ? AND " +
            "       DOMAIN_COUNTER = ? ");

    try {
      //delete all the sessions for the given domain
      deleteSessionInfo.setInt(1, domainHash);
      deleteSessionInfo.setInt(2, domainCounter);
      deleteSessionInfo.executeUpdate();
    } finally {
      deleteSessionInfo.close();
    }

     try {
      //delete all the chunks for the given domain
      deleteSessionChunks.setInt(1, domainHash);
      deleteSessionChunks.setInt(2, domainCounter);
      deleteSessionChunks.executeUpdate();
     } finally {
       deleteSessionChunks.close();
     }
  }

  public DBPersistentSessionModel createSessionModel(int domainHash, int domainCounter, String sessionID) throws PersistentStorageException {
    if (dbds == null) {
      SimpleLogger.log(Severity.FATAL, Category.SYS_SERVER, PersistentDomainModel.LOC, "ASJ.ses.ps0002", 
	  "System Data Source is missing so failover will not work. Check the data source status.");
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;

    int sessionHash = sessionID.hashCode();
    int sessionCounter = -1;
    try {
      con = getConnection();
      DBPersistentSessionModel model = null;

      while (model == null) {
        try {
          sessionCounter++;
          insertSession(domainHash, domainCounter, sessionHash, sessionCounter, sessionID, con);
          con.commit();
          return new DBPersistentSessionModel(domainHash, domainCounter, sessionHash, sessionCounter, sessionID, this);
        } catch (SQLException e) {
          model = selectSession(domainHash, domainCounter, sessionID, con);
        }
      }

      return model;
    } catch (Exception e) {
      SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, PersistentDomainModel.LOC, "ASJ.ses.ps0003", 
	  "Problem during DB connection creation, failover will not work. Check the DB state.");
      try {
        con.rollback();
      } catch (Exception e2) {
        log(e2);
      }
      log(e);
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }


  public DBPersistentSessionModel selectSessionModel(int domainHash, int domainCounter, String sessionID) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;
    try {
      con = getConnection();
      return selectSession(domainHash, domainCounter, sessionID, con);
    } catch (SQLException e) {
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }

  private DBPersistentSessionModel selectSession(int domainHash, int domainCounter, String sessionID, Connection con) throws SQLException {

    PreparedStatement pselect = con.prepareStatement(
            " SELECT SESSION_ID_HASH, SESSION_ID_COUNTER, EXPIRATION_TIME, CREATION_TIME, LOCK_INFO  " +
                    " FROM T_SESSION " +
                    " WHERE DOMAIN_ID_HASH = ? AND " +
                    "       DOMAIN_ID_COUNTER = ? AND " +
                    "       SESSION_ID = ? ");


    DBPersistentSessionModel model = null;

    int idHash;
    int idCounter;
    Timestamp expTime;
    Timestamp creationTime;
    String lockInfo;
    try {
      pselect.setInt(1, domainHash);
      pselect.setInt(2, domainCounter);
      pselect.setString(3, sessionID);

      ResultSet res = pselect.executeQuery();
      if (res.next()) {
        idHash = res.getInt(1);
        idCounter = res.getInt(2);
        expTime = res.getTimestamp(3);
        creationTime = res.getTimestamp(4);
        lockInfo = res.getString(5);

        model = new DBPersistentSessionModel(domainHash, domainCounter, idHash, idCounter, sessionID, lockInfo, creationTime, expTime, this);
      }
    } finally {
      pselect.close();
    }

    return model;
  }

  private void insertSession(int domainHash, int domainCounter, int sessionHash, int sessionCounter,
                             String sessionID, Connection con) throws SQLException {

    PreparedStatement pinsert = con.prepareStatement(
            " INSERT INTO T_SESSION (DOMAIN_ID_HASH, DOMAIN_ID_COUNTER, SESSION_ID_HASH, SESSION_ID_COUNTER, SESSION_ID ) " +
            " VALUES (?, ?, ?, ?, ?) ");

    try {
      pinsert.setInt(1, domainHash);
      pinsert.setInt(2, domainCounter);
      pinsert.setInt(3, sessionHash);
      pinsert.setInt(4, sessionCounter);
      pinsert.setString(5, sessionID);
      pinsert.executeUpdate();
    } finally {
      pinsert.close();
    }
  }


  public Iterator<DBPersistentSessionModel> selectSessionModels(int domainHash, int domainCounter) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;
    try {
      con = getConnection();
      return selectSessions(domainHash, domainCounter, con);
    } catch (SQLException e) {
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }

  private Iterator<DBPersistentSessionModel> selectSessions(int domainHash, int domainCounter, Connection con) throws SQLException {

    PreparedStatement pselect = con.prepareStatement(
            " SELECT SESSION_ID_HASH, SESSION_ID_COUNTER, EXPIRATION_TIME, CREATION_TIME, LOCK_INFO, SESSION_ID  " +
            " FROM T_SESSION " +
            " WHERE DOMAIN_ID_HASH = ? AND " +
            "       DOMAIN_ID_COUNTER = ? ");


    ArrayList<DBPersistentSessionModel> list = new ArrayList<DBPersistentSessionModel>();

    DBPersistentSessionModel model;

    int idHash;
    int idCounter;
    Timestamp expTime;
    Timestamp creationTime;
    String lockInfo;
    String sessionID;
    try {
      pselect.setInt(1, domainHash);
      pselect.setInt(2, domainCounter);

      ResultSet res = pselect.executeQuery();
      while (res.next()) {
        idHash = res.getInt(1);
        idCounter = res.getInt(2);
        expTime = res.getTimestamp(3);
        creationTime = res.getTimestamp(4);
        lockInfo = res.getString(5);
        sessionID = res.getString(6);

        model = new DBPersistentSessionModel(domainHash, domainCounter, idHash, idCounter, sessionID, lockInfo, creationTime, expTime, this);
        list.add(model);
      }
    } finally {
      pselect.close();
    }

    return list.iterator();
  }

  public Iterator<DBPersistentSessionModel> getExpiredSessions(int domainHash, int domainCounter) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;
    try {
      con = getConnection();
      return selectExpiredSessions(domainHash, domainCounter, con);
    } catch (SQLException e) {
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }


  private Iterator<DBPersistentSessionModel> selectExpiredSessions(int domainHash, int domainCounter, Connection con) throws SQLException {

    PreparedStatement pselect = con.prepareStatement(
            " SELECT SESSION_ID_HASH, SESSION_ID_COUNTER, EXPIRATION_TIME, CREATION_TIME, LOCK_INFO, SESSION_ID " +
                    " FROM T_SESSION " +
                    " WHERE DOMAIN_ID_HASH = ? AND " +
                    "       DOMAIN_ID_COUNTER = ? AND " +
                    "       EXPIRATION_TIME <= ?");


    ArrayList<DBPersistentSessionModel> list = new ArrayList<DBPersistentSessionModel>();

    DBPersistentSessionModel model;

    int idHash;
    int idCounter;
    Timestamp expTime;
    Timestamp creationTime;
    String lockInfo;
    String sessionID;
    try {
      pselect.setInt(1, domainHash);
      pselect.setInt(2, domainCounter);
      pselect.setDate(3, new Date(System.currentTimeMillis()));

      ResultSet res = pselect.executeQuery();
      while (res.next()) {
        idHash = res.getInt(1);
        idCounter = res.getInt(2);
        expTime = res.getTimestamp(3);
        creationTime = res.getTimestamp(4);
        lockInfo = res.getString(5);
        sessionID = res.getString(6);

        model = new DBPersistentSessionModel(domainHash, domainCounter, idHash, idCounter, sessionID, lockInfo, creationTime, expTime, this);
        list.add(model);
      }
    } finally {
      pselect.close();
    }

    return list.iterator();
  }


  public void removeExpiredSessions(int domainHash, int domainCounter) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;
    try {
      con = getConnection();
      deleteExpiredSessions(domainHash, domainCounter, con);
    } catch (Exception e) {
      try {
        if (con != null) {
          con.rollback();
        }
      } catch (Exception e2) {
        log(e2);
      }
      log(e);
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }


  private void deleteExpiredSessions(int domainHash, int domainCounter, Connection con) throws SQLException {
    PreparedStatement deleteSessionInfo = con.prepareStatement(" DELETE " +
            " FROM T_SESSION " +
            " WHERE DOMAIN_ID_HASH = ? AND " +
            "       DOMAIN_ID_COUNTER = ? AND " +
            "       SESSION_ID_HASH = ? AND " +
            "       SESSION_ID_COUNTER = ? ");

    PreparedStatement deleteSessionChunks = con.prepareStatement(" DELETE " +
            " FROM T_CHUNK " +
            " WHERE DOMAIN_HASH = ? AND " +
            "       DOMAIN_COUNTER = ? AND " +
            "       SESSION_HASH = ? AND " +
            "       SESSION_COUNTER = ? ");

    try {
      Iterator<DBPersistentSessionModel> iterat = selectExpiredSessions(domainHash, domainCounter, con);
      while (iterat.hasNext()) {
        DBPersistentSessionModel model = iterat.next();

        deleteSessionInfo.setInt(1, domainHash);
        deleteSessionInfo.setInt(2, domainCounter);
        deleteSessionInfo.setInt(3, model.getSessionHash());
        deleteSessionInfo.setInt(4, model.getSessionCounter());
        deleteSessionInfo.executeUpdate();
        deleteSessionInfo.clearParameters();

        deleteSessionChunks.setInt(1, domainHash);
        deleteSessionChunks.setInt(2, domainCounter);
        deleteSessionChunks.setInt(3, model.getSessionHash());
        deleteSessionChunks.setInt(4, model.getSessionCounter());
        deleteSessionChunks.executeUpdate();
        deleteSessionChunks.clearParameters();
      }
    } finally {
      deleteSessionInfo.close();
      deleteSessionChunks.close();
    }
  }


  public void updateSessionModel(int domainHash, int domainCounter, int sessionHash, int sessionCounter,
                                 Timestamp creationTime, Timestamp expTime, String lockInfo) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;
    try {
      con = getConnection();
      updateSession(domainHash, domainCounter, sessionHash, sessionCounter,
              creationTime, expTime, lockInfo, con);
      con.commit();
    } catch (Exception e) {
      try {
        if (con != null) {
          con.rollback();
        }
      } catch (Exception e2) {
        log(e2);
      }
      log(e);
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }

  private void updateSession(int domainHash, int domainCounter, int sessionHash, int sessionCounter,
                             Timestamp creationTime, Timestamp expTime, String lockInfo, Connection con) throws SQLException {

    PreparedStatement pupdate = con.prepareStatement(
            " UPDATE T_SESSION " +
                    " SET CREATION_TIME = ?, " +
                    "     EXPIRATION_TIME = ?, " +
                    "     LOCK_INFO = ? " +
                    " WHERE SESSION_ID_HASH = ? AND  " +
                    "       SESSION_ID_COUNTER = ? AND " +
                    "       DOMAIN_ID_HASH = ? AND " +
                    "       DOMAIN_ID_COUNTER = ? ");

    try {
      pupdate.setTimestamp(1, creationTime);
      pupdate.setTimestamp(2, expTime);
      pupdate.setString(3, lockInfo);

      pupdate.setInt(4, sessionHash);
      pupdate.setInt(5, sessionCounter);
      pupdate.setInt(6, domainHash);
      pupdate.setInt(7, domainCounter);

      pupdate.executeUpdate();
    } finally {
      pupdate.close();
    }
  }

  public String getLockInfo(int domainHash, int domainCounter, int sessionHash, int sessionCounter) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;
    try {
      con = getConnection();
      return selectLockInfo(domainHash, domainCounter, sessionHash, sessionCounter, con);
    } catch (SQLException e) {
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }

  private String selectLockInfo(int domainHash, int domainCounter, int sessionHash, int sessionCounter, Connection con) throws SQLException {
    PreparedStatement pselect = con.prepareStatement(
            " SELECT LOCK_INFO " +
                    " FROM T_SESSION " +
                    " WHERE DOMAIN_ID_HASH = ? AND " +
                    "       DOMAIN_ID_COUNTER = ? AND " +
                    "       SESSION_ID_HASH = ? AND " +
                    "       SESSION_ID_COUNTER = ?");


    String lockInfo = null;
    try {
      pselect.setInt(1, domainHash);
      pselect.setInt(2, domainCounter);
      pselect.setInt(3, sessionHash);
      pselect.setInt(4, sessionCounter);

      ResultSet res = pselect.executeQuery();
      if (res.next()) {
        lockInfo = res.getString(1);
      }
    } finally {
      pselect.close();
    }

    return lockInfo;
  }


  public void removeSession(int domainHash, int domainCounter, int sessionHash, int sessionCounter) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;
    try {
      con = getConnection();
      deleteSession(domainHash, domainCounter, sessionHash, sessionCounter, con);
			con.commit();
		} catch (Exception e) {
      try {
        if (con != null) {
          con.rollback();
        }
      } catch (Exception e2) {
        log(e2);
      }
      log(e);
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
				try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }

  private void deleteSession(int domainHash, int domainCounter, int sessionHash, int sessionCounter, Connection con) throws SQLException {
    PreparedStatement deleteSessionInfo = con.prepareStatement(" DELETE " +
            " FROM T_SESSION " +
            " WHERE DOMAIN_ID_HASH = ? AND " +
            "       DOMAIN_ID_COUNTER = ? AND " +
            "       SESSION_ID_HASH = ? AND " +
            "       SESSION_ID_COUNTER = ? ");

    PreparedStatement deleteSessionChunks = con.prepareStatement(" DELETE " +
            " FROM T_CHUNK " +
            " WHERE DOMAIN_HASH = ? AND " +
            "       DOMAIN_COUNTER = ? AND " +
            "       SESSION_HASH = ? AND " +
            "       SESSION_COUNTER = ? ");

    try {
      deleteSessionInfo.setInt(1, domainHash);
      deleteSessionInfo.setInt(2, domainCounter);
      deleteSessionInfo.setInt(3, sessionHash);
      deleteSessionInfo.setInt(4, sessionCounter);
      deleteSessionInfo.executeUpdate();
      deleteSessionInfo.clearParameters();
    } finally {
      deleteSessionInfo.close();
    }

    try {
      deleteSessionChunks.setInt(1, domainHash);
      deleteSessionChunks.setInt(2, domainCounter);
      deleteSessionChunks.setInt(3, sessionHash);
      deleteSessionChunks.setInt(4, sessionCounter);
      deleteSessionChunks.executeUpdate();
      deleteSessionChunks.clearParameters();
    } finally {
      deleteSessionChunks.close();
    }

  }


  public byte[] getChunk(int domainHash, int domainCounter, int sessionHash, int sessionCounter, String chunkName) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;

    try {
      con = getConnection();

      return selectSessionChunk(domainHash, domainCounter, sessionHash, sessionCounter, chunkName, con);
    } catch (Exception e) {
      log(e);
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }

  private byte[] selectSessionChunk(int domainHash, int domainCounter, int sessionHash, int sessionCounter,
                                    String chunkName, Connection con) throws SQLException {
    byte[] chunkData = null;

    PreparedStatement ps = con.prepareStatement(" SELECT CHUNK_DATA " +
            " FROM T_CHUNK " +
            " WHERE DOMAIN_HASH = ? AND " +
            "       DOMAIN_COUNTER = ? AND " +
            "       SESSION_HASH = ? AND " +
            "       SESSION_COUNTER = ? AND " +
            "       CHUNK_NAME = ? ");
    try {
      ps.setInt(1, domainHash);
      ps.setInt(2, domainCounter);
      ps.setInt(3, sessionHash);
      ps.setInt(4, sessionCounter);
      ps.setString(5, chunkName);

      ResultSet set = ps.executeQuery();
      if (set.next()) {
        chunkData = set.getBytes(1);
      }
      set.close();
    } finally {
      ps.close();
    }

    return chunkData;
  }


  public Map<String, byte[]> listChunks(int domainHash, int domainCounter, int sessionHash, int sessionCounter) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;

    try {
      con = getConnection();

      return selectSessionChunks(domainHash, domainCounter, sessionHash, sessionCounter, con);
    } catch (Exception e) {
      log(e);
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }

  private Map<String, byte[]> selectSessionChunks(int domainHash, int domainCounter, int sessionHash, int sessionCounter, Connection con) throws SQLException {
    HashMap<String, byte[]> map = new HashMap<String, byte[]>();

    PreparedStatement ps = con.prepareStatement(" SELECT CHUNK_NAME, CHUNK_DATA " +
            " FROM T_CHUNK " +
            " WHERE DOMAIN_HASH = ? AND " +
            "       DOMAIN_COUNTER = ? AND " +
            "       SESSION_HASH = ? AND " +
            "       SESSION_COUNTER = ? ");
    try {
      ps.setInt(1, domainHash);
      ps.setInt(2, domainCounter);
      ps.setInt(3, sessionHash);
      ps.setInt(4, sessionCounter);

      ResultSet set = ps.executeQuery();
      while (set.next()) {
        String chunkName = set.getString(1);
        byte[] chunkData = set.getBytes(2);

        map.put(chunkName, chunkData);
      }
      set.close();
    } finally {
      ps.close();
    }
    return map;
  }


  public void removeChunk(int domainHash, int domainCounter, int sessionHash, int sessionCounter, String chunkName) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;

    try {
      con = getConnection();
      deleteSessionChunk(domainHash, domainCounter, sessionHash, sessionCounter, chunkName, con);
      con.commit();
    } catch (Exception e) {
      try {
        if (con != null) {
          con.rollback();
        }
      } catch (Exception e2) {
        log(e2);
      }
      log(e);
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }

  private void deleteSessionChunk(int domainHash, int domainCounter, int sessionHash, int sessionCounter, String chunkName, Connection con) throws SQLException {
    PreparedStatement ps = con.prepareStatement(
            " DELETE FROM T_CHUNK " +
            " WHERE DOMAIN_HASH = ? AND " +
            "       DOMAIN_COUNTER = ? AND " +
            "       SESSION_HASH = ? AND " +
            "       SESSION_COUNTER = ? AND " +
            "       CHUNK_NAME = ? ");
    try {
      ps.setInt(1, domainHash);
      ps.setInt(2, domainCounter);
      ps.setInt(3, sessionHash);
      ps.setInt(4, sessionCounter);
      ps.setString(5, chunkName);

      ps.executeUpdate();
    } finally {
      ps.close();
    }
  }


  public void setChunk(int domainHash, int domainCounter, int sessionHash, int sessionCounter,
                       String chunkName, byte[] chunkData) throws PersistentStorageException {
    if (dbds == null) {
      throw new PersistentStorageException("System Data Source is NULL!");
    }

    Connection con = null;

    try {
      con = getConnection();
      int result = updateSessionChunk(domainHash, domainCounter, sessionHash, sessionCounter, chunkName, chunkData, con);
      if(result == 0){
        insertSessionChunk(domainHash, domainCounter, sessionHash, sessionCounter, chunkName, chunkData, con);
      }
      con.commit();
    } catch (Exception e) {
      try {
        if (con != null) {
          con.rollback();
        }
      } catch (Exception e2) {
        log(e2);
      }
      log(e);
      throw new PersistentStorageException(e);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          log(e);
        }
      }
    }
  }


  private void insertSessionChunk(int domainHash, int domainCounter, int sessionHash, int sessionCounter,
                                  String chunkName, byte[] chunkData, Connection con) throws SQLException {
    PreparedStatement pinsert = con.prepareStatement(
            " INSERT INTO T_CHUNK (DOMAIN_HASH, DOMAIN_COUNTER, SESSION_HASH, SESSION_COUNTER, CHUNK_NAME, CHUNK_DATA) " +
            " VALUES (?, ?, ?, ?, ?, ?) ");
    try {
      pinsert.setInt(1, domainHash);
      pinsert.setInt(2, domainCounter);
      pinsert.setInt(3, sessionHash);
      pinsert.setInt(4, sessionCounter);
      pinsert.setString(5, chunkName);
      pinsert.setBytes(6, chunkData);
      pinsert.executeUpdate();
    } finally {
      pinsert.close();
    }
  }

  private int updateSessionChunk(int domainHash, int domainCounter, int sessionHash, int sessionCounter,
                                  String chunkName, byte[] chunkData, Connection con) throws SQLException {

    int result = -1;
    PreparedStatement ps = con.prepareStatement(
            " UPDATE T_CHUNK " +
            " SET CHUNK_DATA = ? " +
            " WHERE DOMAIN_HASH = ? AND " +
            "       DOMAIN_COUNTER = ? AND " +
            "       SESSION_HASH = ? AND " +
            "       SESSION_COUNTER = ? AND " +
            "       CHUNK_NAME = ? ");

    try {
      ps.setBytes(1, chunkData);
      ps.setInt(2, domainHash);
      ps.setInt(3, domainCounter);
      ps.setInt(4, sessionHash);
      ps.setInt(5, sessionCounter);
      ps.setString(6, chunkName);
      result = ps.executeUpdate();
    } finally {
      ps.close();
    }
    return result;
  }
}

