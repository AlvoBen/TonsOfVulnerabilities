/*
 * WebModulesInfo.java
 *
 * Created on June 29, 2006, 4:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sap.liteadmin.application;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.InitialContext;

/**
 *
 * @author nikolai-do
 */
public class ModulesInfo {
    private String type;
    private String name;
    private String applicationName;
    
    private Logger log = Logger.getLogger(ModulesInfo.class.getName());
    
    /** Creates a new instance of WebModulesInfo */
    public ModulesInfo() {
    }
    
     public ModulesInfo(String name, String type) {
       this.name = name;
       this.type = type;
       checkType();  
    }
    
    public String getType() {      
        return type;
    }
    
    public void setType(String type) {
      this.type = type;
      checkType();
    }

    public boolean isUnknownType() {
      return !type.equalsIgnoreCase("web") && !type.equalsIgnoreCase("DataSource") && !type.equalsIgnoreCase("DataSourceAlias");     
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name) {
      this.name = name;
    }
    
    public void setApplicationName(String applicationName) {
      this.applicationName = applicationName;
    }
    
    public String getApplicationName() {
      return applicationName;
    }    
    
    private void checkType() {
      if (!type.equals("JDBCConnector")) {
        return;
      }
      
      try {
        InitialContext ctx = new InitialContext();
        MBeanServerConnection mbs = (MBeanServerConnection) ctx.lookup("jmx");
        
        ObjectName pattern = new ObjectName("*:j2eeType=JDBCDataSource,name=" + name + ",*");
        Set result = mbs.queryNames(pattern, null); //THIS IS IMPORTANT - from JASEN
        
        if (result != null) {
          for (Iterator it = result.iterator(); it.hasNext();) {
            ObjectName currentON = (ObjectName) it.next();
            if (applicationName.equals(currentON.getKeyProperty("J2EEApplication"))) {
              type = "DataSource";          
              return;
            }
          }
        }
        
        pattern = new ObjectName("*:j2eeType=JDBCDataSourceAlias,name=" + name + ",*");
        result = mbs.queryNames(pattern, null); //THIS IS IMPORTANT - from JASEN
        
        if (result != null) {
          for (Iterator it = result.iterator(); it.hasNext();) {
            ObjectName currentON = (ObjectName) it.next();
            if (applicationName.equals(currentON.getKeyProperty("J2EEApplication"))) {
              type = "DataSourceAlias";          
              return;
            }
          } 
        }
        
        log.info("Although type of the " + name + " is JDBCConnector, it belongs to resources of none of the types JDBCDataSource or JDBCDataSourceAlias");
      } catch (Exception e) { 
        log.info("Error in retrieving the type of the " + name);
        e.printStackTrace();        
      }      
    }
}
