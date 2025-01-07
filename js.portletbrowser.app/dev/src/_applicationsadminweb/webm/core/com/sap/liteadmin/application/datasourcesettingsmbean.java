package com.sap.liteadmin.application;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCCustomDataSourceSettings;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCCustomDataSourceWrapper;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeProperty;

public class DataSourceSettingsMBean {

  private String dsName;
  private String applicationName;
  
  private MBeanServerConnection mbs = null;
  private Logger log = Logger.getLogger(DataSourceSettingsMBean.class.getName());
  private int connectionLifetime;
  private String CPDSClassName;
  private String dataSourceName;
  private String deployer;
  private String description;
  private String driverClassName;
  private String driverName;
  private int initConnections;
  private int isolationLevel;
  private int maxConnections;
  private int maxTimeToWaitConnection;
  private String objectFactory;
  private String password;
  private int runCleanupThread;
  private String sqlEngine;
  private String url;
  private String username;
  private String XADSClassName;
  
  public DataSourceSettingsMBean() {
    try {
      InitialContext ctx = new InitialContext();
      mbs = (MBeanServerConnection) ctx.lookup("jmx");
    } catch (NamingException e) {
      e.printStackTrace();
    }
  }

  public void setDsName(String dsName) {
    this.dsName = dsName;    
  }
  
  public String getDsName() {
    return dsName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }
    
  public String getApplicationName() {
    return applicationName;
  }

  public void init() {
    try {
      ObjectName on = null;
       //ObjectName pattern = new ObjectName("*:cimclass=SAP_ITSAMJ2eeJDBCCustomDataSource,name=" + dsName + ",*");
       ObjectName pattern = new ObjectName("*:j2eeType=JDBCDataSource,name=" + dsName + ",*");
       Set result = mbs.queryNames(pattern, null); //THIS IS IMPORTANT - from JASEN
       
       if (result != null) {
         for (Iterator it = result.iterator(); it.hasNext();) {
           ObjectName currentON = (ObjectName) it.next();
           if (applicationName.equals(currentON.getKeyProperty("J2EEApplication"))) {
             on = currentON;
             break;
           }
         }
       }
       if (on == null) {
         log.info("Cannot find OjectName object for " + dsName);
         return;
       }
       
       CompositeData cd = (CompositeData)mbs.getAttribute(on, "Settings"); //THIS IS IMPORTANT - from JASEN
       SAP_ITSAMJ2eeJDBCCustomDataSourceSettings settings = SAP_ITSAMJ2eeJDBCCustomDataSourceWrapper.
         getSAP_ITSAMJ2eeJDBCCustomDataSourceSettingsForCData(cd);
       
       connectionLifetime = settings.getConnectionLifetime();
       CPDSClassName = settings.getCPDSClassName();
       dataSourceName = settings.getDataSourceName();
       deployer = settings.getDeployer();
       description = settings.getDescription();
       driverClassName = settings.getDriverClassName();
       driverName = settings.getDriverName();
       initConnections = settings.getInitConnections();
       isolationLevel = settings.getIsolationLevel();
       maxConnections = settings.getMaxConnections();
       maxTimeToWaitConnection = settings.getMaxTimeToWaitConnection();
       objectFactory = settings.getObjectFactory();
       password = settings.getPassword();
       SAP_ITSAMJ2eeProperty[] properties1x = settings.getProperties1x();
       SAP_ITSAMJ2eeProperty[] properties20 = settings.getProperties20();
       runCleanupThread = settings.getRunCleanupThread();
       sqlEngine = settings.getSQLEngine();
       url = settings.getURL();
       username = settings.getUsername();
       XADSClassName = settings.getXADSClassName();       
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public int getConnectionLifetime() {
    return connectionLifetime;
  }

  public String getCPDSClassName() {
    return CPDSClassName;
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  public String getDeployer() {
    return deployer;
  }

  public String getDescription() {
    return description;
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public String getDriverName() {
    return driverName;
  }

  public int getInitConnections() {
    return initConnections;
  }

  public int getIsolationLevel() {
    return isolationLevel;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public int getMaxTimeToWaitConnection() {
    return maxTimeToWaitConnection;
  }

  public String getObjectFactory() {
    return objectFactory;
  }

  public int getRunCleanupThread() {
    return runCleanupThread;
  }

  public String getSqlEngine() {
    return sqlEngine;
  }
}
