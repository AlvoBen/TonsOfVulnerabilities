package com.sap.liteadmin.application;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCDataSourceAliasSettings;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCDataSourceAliasWrapper;

public class DataSourceAliasSettingsMBean {

  private MBeanServerConnection mbs;
  private String dsAliasName;
  private String applicationName;
  private Logger log = Logger.getLogger(DataSourceAliasSettingsMBean.class.getName());
  private String name;
  private String dataSourceName;
  private String description;
  private String deployer;

  public DataSourceAliasSettingsMBean() {
    try {
      InitialContext ctx = new InitialContext();
      mbs = (MBeanServerConnection) ctx.lookup("jmx");
    } catch (NamingException e) {
      e.printStackTrace();
    }
  }
  
  public void setDsName(String dsName) {
    this.dsAliasName = dsName;    
  }
  
  public String getDsAliasName() {
    return dsAliasName;
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
       ObjectName pattern = new ObjectName("*:j2eeType=JDBCDataSourceAlias,name=" + dsAliasName + ",*");
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
         log.info("Cannot find OjectName object for " + dsAliasName);
         return;
       }
       
       CompositeData cd = (CompositeData)mbs.getAttribute(on, "Settings"); //THIS IS IMPORTANT - from JASEN
       SAP_ITSAMJ2eeJDBCDataSourceAliasSettings settings = SAP_ITSAMJ2eeJDBCDataSourceAliasWrapper.
         getSAP_ITSAMJ2eeJDBCDataSourceAliasSettingsForCData(cd);
       
       name = settings.getName();
       dataSourceName = settings.getDataSourceName();
       deployer = settings.getDeployer();
       description = settings.getDescription();       
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public String getDeployer() {
    return deployer;
  }
}
