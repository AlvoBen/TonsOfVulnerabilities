package com.sap.engine.session.telnet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.sap.engine.frame.core.database.DatabaseDataSource;
import com.sap.engine.lib.util.concurrent.QueuedSemaphore;

public class DBMonitoring {

  public static Connection con;
  public static DatabaseDataSource dbds;

  public static Set<String> listDbDomains() throws SQLException {
    Set<String> domains = new TreeSet<String>();
    Connection con = dbds.getConnection();
    con.setAutoCommit(false);
    PreparedStatement pselect = con.prepareStatement("SELECT DOMAIN_PATH FROM T_DOMAIN");
    try {
      ResultSet res = pselect.executeQuery();
      String domain;
      while (res.next()) {
        domain = res.getString(1);
        domains.add(domain);
      }
    } finally {
      pselect.close();
      con.close();
    }
    return domains;
  }

  private static int findDomainTableID(String path) throws SQLException {
    int result = -1;
    Connection con = dbds.getConnection();
    con.setAutoCommit(false);
    PreparedStatement pselect = con.prepareStatement("SELECT ID_HASH FROM T_DOMAIN WHERE DOMAIN_PATH = ?");
    try {
      pselect.setString(1, path);
      ResultSet res = pselect.executeQuery();
      if (res.next()) {
        result = res.getInt(1);
      }else{
        throw new IllegalArgumentException("Domain does not exist");
      }
    } finally {
      pselect.close();
      con.close();
    }
    return result;
  }

  public static Set<String> listDbSessions(String path) throws SQLException {
    Set<String> sessions = new TreeSet<String>();
    int domainId = DBMonitoring.findDomainTableID(path);
    Connection con = dbds.getConnection();
    con.setAutoCommit(false);
    PreparedStatement pselect = con.prepareStatement("SELECT SESSION_ID FROM T_SESSION WHERE DOMAIN_ID_HASH = ?");
    try {
      pselect.setInt(1, domainId);
      ResultSet res = pselect.executeQuery();
      String session;
      while (res.next()) {
        session = res.getString(1);
        sessions.add(session);
      }
    } finally {
      pselect.close();
      con.close();
    }
    return sessions;
  }
  
  public static Map<String, Integer> domainsWithSessions() throws SQLException{
    Map<String, Integer> domains = new HashMap<String, Integer>();
    int sessionsNumber;
    for(String domain : listDbDomains()){
      sessionsNumber = numberOfSessions(domain);
      if(sessionsNumber > 0){
        domains.put(domain, sessionsNumber);
      }
    }
    return domains;
  }
  
  public static int numberOfSessions(String path) throws SQLException{
    int sessions = 0;
    int domainId = DBMonitoring.findDomainTableID(path);
    Connection con = dbds.getConnection();
    con.setAutoCommit(false);
    PreparedStatement pselect = con.prepareStatement("SELECT SESSION_ID FROM T_SESSION WHERE DOMAIN_ID_HASH = ?");
    try {
      pselect.setInt(1, domainId);
      ResultSet res = pselect.executeQuery();
      while (res.next()) {
        sessions++;
      }
    } finally {
      pselect.close();
      con.close();
    }
    return sessions;
  }

  public static void removeDBAll() throws SQLException{
    Connection con = dbds.getConnection();
    con.setAutoCommit(false);
    PreparedStatement pdelete = con.prepareStatement("DELETE FROM T_SESSION");
    try {
      pdelete.executeUpdate();
      pdelete = con.prepareStatement("DELETE FROM T_CHUNK");
      pdelete.executeUpdate();
      con.commit();
    }catch (SQLException e) {
      con.rollback();
    } finally {
      pdelete.close();
      con.close();
    }    
  }
  
  public static void removeDBDomain(String path) throws SQLException{
    int domainId = DBMonitoring.findDomainTableID(path);
    Connection con = dbds.getConnection();
    con.setAutoCommit(false);
    PreparedStatement pdelete = con.prepareStatement("DELETE FROM T_SESSION WHERE DOMAIN_ID_HASH = ?");
    try {
      pdelete.setInt(1, domainId);
      pdelete.executeUpdate();
      pdelete = con.prepareStatement("DELETE FROM T_CHUNK WHERE DOMAIN_HASH = ?");
      pdelete.setInt(1, domainId);
      pdelete.executeUpdate();
      con.commit();
    }catch (SQLException e) {
      con.rollback();
    } finally {
      pdelete.close();
      con.close();
    }
  }
}
