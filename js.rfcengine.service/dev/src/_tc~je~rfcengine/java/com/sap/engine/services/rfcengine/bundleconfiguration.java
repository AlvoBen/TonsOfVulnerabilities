/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rfcengine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Stores the configuration settings for a bundle
 *
 * @author  Hristo Iliev, d035676
 * Sorry for inconsistent properties handling. This mess was a result of
 * multiple responsibles and rush in the development. If there will be 
 * free time, I'll make this handling homogenous, d035676
 * @version 4.4
 */
public class BundleConfiguration implements Serializable {

  /** Version UID for serialization */
  private static final long serialVersionUID = 1020062005L;
  public static final String APPLICATION_SERVER_HOST ="application.server.host";
  public static final String GATEWAY_HOST = "gateway.host";
  public static final String GATEWAY_SERVER = "gateway.server";
  public static final String LOCAL ="local";
  public static final String LOGON_CLIENT = "logon.client";
  public static final String LOGON_LANGUAGE ="logon.language";
  public static final String LOGON_PASSWORD = "logon.password";
  public static final String LOGON_USER  ="logon.user";
  public static final String MAX_CONNECTIONS ="max.connections.";
  //processes.number just for compatibility
  public static final String PROCESSES_NUMBER ="processes.number";
  //prifile.name just for compatibility
  public static final String PROFILE_NAME ="prifile.name";
  public static final String PROGRAM_ID ="program.id";
  public static final String RUNNING_STATE ="running.state";
  public static final String SYSTEM_NUMBER ="syste.number";
  //unicode just for compatibility
  public static final String UNICODE ="unicode";

  public static final String SNC_NAME = "jco.server.snc_myname";
  public static final String SNC_QOP = "jco.server.snc_qop";
  public static final String SNC_LIB = "jco.server.snc_lib";
  public static final String SNC_AUTH_PARTNER = "jco.server.auth_partner";
  public static final String USE_SNC = "use.snc";
  
  static final String [] PROPERTIES = {	
  		"ProgramId",
		"GatewayHost",	//1
		"GatewayService",
		"ServerCount",
		"ApplicationServerHost",
		"SystemNumber", //5
		"Client",
		"Language",
		"User",
		"Running",
		"Local",		//10
		"RfcTrace",
		"UseSnc",		// 12
		"SncName",	
		"SncQop",		
		"SncLib",		//15
		"SncAuthorizationPartner",
		"Password",
        "RepositoryDestination"};
  
  static final String REQUIRED_PROPERTIES = 
  	"ProgramId=,GatewayHost=,GatewayService=,RepositoryDestination=";

  /**
   * Maximum connections to the server
   */
  private int maxConnections = 20;

  /**
   * Array of strings. Inside has the following String fields :
   *   logonClient
   *   logonUser
   *   logonPassword
   *   logonLanguage
   *   applicationServerHost
   *   systemNumber
   *   gatewayHost
   *   gatewayServer
   *   programId
   */
  public String[] configuration = new String[10];

  /**
   * Determines if the configuration is local
   */
  private boolean local = false;

  /**
   * Stores the number of processes
   */
  private int processes = 1;

  /**
   * Running or stopped bundle ?
   */
  private boolean running = false;
  // middleware not needed. Just for compatibility
  private boolean middleware = false;
  //middleware not needed. Just for compatibility
  private boolean unicode = false;
  private Properties props = null;

  private String sncName = "";
  private int sncQop;
  private String sncLib = "";
  private boolean useSnc = false;
  private String authPartner = "";
  private boolean useOwnRepository = false;
  private String repositoryJNDIName;

  // trace variables. New variables at the end, so no serialization
  // problems should occur
  private boolean rfcTrace = false;
  private int jcoTraceLevel = 0;

  private boolean useJarm = false;
  private boolean globalJrfcTrace = false;
  private int cpicTraceLevel = 0;
  public static final String RFC_TRACE ="rfc_trace";
  
  public boolean useRepDest = false;
  private String repositoryDestination="";
  public static final String REPOSTORY_DESTINATION = "repositoryDestination";
  public static final String USE_REP_DEST = "useRepDestination";
  public static final String OWN_REPOSITORY = "$JAVA$";
  public static final String PASSWORD_STARS = "*****";

  /**
   * Constructor
   */
  public BundleConfiguration() {
    for (int i = 0; i < 10; i++) {
      this.configuration[i] = new String();
    }
  }

  /**
   * Copy constructor
   *
   * @param  bundleConfiguration   The BundleConfiguration to copy data from
   */
  public BundleConfiguration(BundleConfiguration bundleConfiguration) {
    this.setApplicationServerHost(bundleConfiguration.getApplicationServerHost());
    this.setGatewayHost(bundleConfiguration.getGatewayHost());
    this.setGatewayService(bundleConfiguration.getGatewayService());
    this.setLocal(bundleConfiguration.isLocal());
    this.setLogonClient(bundleConfiguration.getLogonClient());
    this.setLogonLanguage(bundleConfiguration.getLogonLanguage());
    this.setLogonPassword(bundleConfiguration.getLogonPassword());
    this.setLogonUser(bundleConfiguration.getLogonUser());
    this.setMaxConnections(bundleConfiguration.getMaxConnections());
    this.setProcessesNumber(bundleConfiguration.getProcessesNumber());
    this.setProgramId(bundleConfiguration.getProgramId());
    this.setRunningState(bundleConfiguration.isRunning());
    this.setSystemNumber(bundleConfiguration.getSystemNumber());
    this.setAuthPartner(bundleConfiguration.getAuthPartner());
    this.setSncLib(bundleConfiguration.getSncLib());
    this.setSncName(bundleConfiguration.getSncName());
    this.setSncQop(bundleConfiguration.getSncQop());
    this.setUseSnc(bundleConfiguration.getUseSnc());
    this.setRfcTrace(bundleConfiguration.getRfcTrace());
    this.setJcoTraceLevel(bundleConfiguration.getJcoTraceLevel());
    this.setJarm(bundleConfiguration.getJarm());
    this.setRepositoryDestination(bundleConfiguration.repositoryDestination);
    this.useRepDest = bundleConfiguration.useRepDest;
    
  }

  /**
   * Gets the maxConnections field
   *
   * @return  The requested property
   */
  public synchronized int getMaxConnections() {
    return this.maxConnections;
  }

  /**
   * Sets the maxConnections field
   */
  public synchronized void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  /**
   * Gets the logonClient field
   *
   * @return  The requested property
   */
  public synchronized String getLogonClient() {
    return this.configuration[Constants.LOGON_CLIENT];
  }

  /**
   * Sets the logonClient field
   *
   * @param  logonClient   Sets the client for logon
   */
  public synchronized void setLogonClient(String logonClient) {
    this.configuration[Constants.LOGON_CLIENT] = logonClient;
  }

  /**
   * Gets the logonUser field
   *
   * @return  The requested property
   */
  public synchronized String getLogonUser() {
    return this.configuration[Constants.LOGON_USER];
  }

  /**
   * Sets the logonUser field
   *
   * @param  logonUser   Sets the User for logon
   */
  public synchronized void setLogonUser(String logonUser) {
    this.configuration[Constants.LOGON_USER] = logonUser;
  }

  /**
   * Gets the logonPassword field
   *
   * @return  The requested property
   */
  public synchronized String getLogonPassword() {
    return this.configuration[Constants.LOGON_PASSWORD];
  }

  /**
   * Sets the logonPassword field
   *
   * @param  logonPassword   Sets the password for logon
   */
  public synchronized void setLogonPassword(String logonPassword) {
    this.configuration[Constants.LOGON_PASSWORD] = logonPassword;
  }

  /**
   * Gets the logonLanguage field
   *
   * @return  The requested property
   */
  public synchronized String getLogonLanguage() {
    return this.configuration[Constants.LOGON_LANGUAGE];
  }

  /**
   * Sets the logonLanguage field
   *
   * @param  logonLanguage   Sets the language for logon
   */
  public synchronized void setLogonLanguage(String logonLanguage) {
    this.configuration[Constants.LOGON_LANGUAGE] = logonLanguage;
  }

  /**
   * Gets the applicationServerHost field
   *
   * @return  The requested property
   */
  public synchronized String getApplicationServerHost() {
    return this.configuration[Constants.APPLICATION_SERVER_HOST];
  }

  /**
   * Sets the applicationServerHost field
   *
   * @param  applicationServerHost   Sets the Application Server's Host
   */
  public synchronized void setApplicationServerHost(String applicationServerHost) {
    this.configuration[Constants.APPLICATION_SERVER_HOST] = applicationServerHost;
  }

  /**
   * Gets the systemNumber field
   *
   * @return  The requested property
   */
  public synchronized String getSystemNumber() {
    return this.configuration[Constants.SYSTEM_NUMBER];
  }

  /**
   * Sets the systemNumber field
   */
  public synchronized void setSystemNumber(String systemNumber) {
    this.configuration[Constants.SYSTEM_NUMBER] = systemNumber;
  }

  /**
   * Gets the gatewayHost field
   *
   * @return  The requested property
   */
  public synchronized String getGatewayHost() {
    return this.configuration[Constants.GATEWAY_HOST];
  }

  /**
   * Sets the gatewayHost field
   *
   * @param  gatewayHost  Gateway Host property to set
   */
  public synchronized void setGatewayHost(String gatewayHost) {
    this.configuration[Constants.GATEWAY_HOST] = gatewayHost;
  }

  /**
   * Gets the gatewayServer field
   *
   * @return  The requested property
   */
  public synchronized String getGatewayService() {
    return this.configuration[Constants.GATEWAY_SERVER];
  }

  /**
   * Sets the gatewayServer field
   *
   * @param  gatewayServer  Gateway Server property to be set
   */
  public synchronized void setGatewayService(String gatewayServer) {
    this.configuration[Constants.GATEWAY_SERVER] = gatewayServer;
  }
  
  // just for compatibility
  public synchronized String getGatewayServer() {
      return getGatewayService();
  }

  public synchronized void setGatewayServer(String gatewayServer) {
      setGatewayService(gatewayServer);
  }

  /**
   * Gets the programId field
   *
   * @return   The requested property
   */
  public synchronized String getProgramId() {
      return this.configuration[Constants.PROGRAM_ID];
  }

  /**
   * Sets the programId field
   *
   * @param  programId  ProgramID to be set
   */
  public synchronized void setProgramId(String programId) {
    this.configuration[Constants.PROGRAM_ID] = programId;
  }

  /**
   * Gets the type of the configuration
   *
   * @return   TRUE if local
   */
  public synchronized boolean isLocal() {
    return this.local;
  }

  /**
   * Sets the local flag
   *
   * @param  localFlag   Sets the local flag
   */
  public synchronized void setLocal(boolean localFlag) {
    this.local = localFlag;
  }

  /**
   * Returns the number of processes
   *
   * @return  The number of paradigms (processes).
   */
  public synchronized int getProcessesNumber() {
    return this.processes;
  }

  /**
   * Sets the number of processes (paradigms)
   *
   * @param  num   The number of processes to be set
   */
  public synchronized void setProcessesNumber(int num) {
    this.processes = num;
  }

  /**
   * Returns the state of the bundle (running/stopped)
   *
   * @return   TRUE if the bundle is running
   */
  public synchronized boolean isRunning() {
    return this.running;
  }

  /**
   * Sets the state of the bundle (running/stopped)
   *
   * @param  state   TRUE for running, FALSE for stopped bundle
   */
  public synchronized void setRunningState(boolean state) {
    this.running = state;
  }

  /**
   *  Checks if this and passed configuration are equal
   *
   * @param  cfg   Configuration to check for equality
   * @return   TRUE if equal, FALSE if not equal
   */
  public synchronized boolean equals(BundleConfiguration cfg) {

    return (cfg.getApplicationServerHost().equals(this.configuration[Constants.APPLICATION_SERVER_HOST]) &&
            cfg.getGatewayHost().equals(this.configuration[Constants.GATEWAY_HOST]) &&
            cfg.getGatewayService().equals(this.configuration[Constants.GATEWAY_SERVER]) &&
            cfg.getLogonClient().equals(this.configuration[Constants.LOGON_CLIENT]) &&
            cfg.getLogonLanguage().equals(this.configuration[Constants.LOGON_LANGUAGE]) &&
            (cfg.getLogonPassword().equals(this.configuration[Constants.LOGON_PASSWORD])
            	|| BundleConfiguration.PASSWORD_STARS.equals(cfg.getLogonPassword())) &&
            cfg.getLogonUser().equals(this.configuration[Constants.LOGON_USER]) &&
            (cfg.getMaxConnections() == this.maxConnections) &&
            (cfg.getProcessesNumber() == this.processes) &&
            cfg.getProgramId().equals(this.configuration[Constants.PROGRAM_ID]) &&
            cfg.getSystemNumber().equals(this.configuration[Constants.SYSTEM_NUMBER]) &&
            (cfg.isLocal() == this.local) &&
            (cfg.getRfcTrace() == this.rfcTrace) &&
            (cfg.useSnc == this.useSnc) &&
            (cfg.authPartner.equals(this.authPartner)) &&
            (cfg.sncLib.equals(this.sncLib)) &&
            (cfg.sncName.equals(this.sncName)) &&
            (cfg.sncQop == this.sncQop)) &&
            (cfg.repositoryDestination.equals(this.repositoryDestination)) &&
            (cfg.useRepDest == this.useRepDest);
  }

  /**
   *  Checks if this and passed configuration are equal
   *  The number of processes is not checked.
   *
   * @param  cfg   Configuration to check for equality
   * @return   TRUE if equal, FALSE if not equal
   */
  public synchronized boolean equalsWithoutProcesses(BundleConfiguration cfg) {
    return (cfg.getApplicationServerHost().equals(this.configuration[Constants.APPLICATION_SERVER_HOST]) &&
            cfg.getGatewayHost().equals(this.configuration[Constants.GATEWAY_HOST]) &&
            cfg.getGatewayService().equals(this.configuration[Constants.GATEWAY_SERVER]) &&
            cfg.getLogonClient().equals(this.configuration[Constants.LOGON_CLIENT]) &&
            cfg.getLogonLanguage().equals(this.configuration[Constants.LOGON_LANGUAGE]) &&
            (cfg.getLogonPassword().equals(this.configuration[Constants.LOGON_PASSWORD])
                	|| BundleConfiguration.PASSWORD_STARS.equals(cfg.getLogonPassword())) &&
            cfg.getLogonUser().equals(this.configuration[Constants.LOGON_USER]) &&
            (cfg.getMaxConnections() == this.maxConnections) &&
            cfg.getProgramId().equals(this.configuration[Constants.PROGRAM_ID]) &&
            cfg.getSystemNumber().equals(this.configuration[Constants.SYSTEM_NUMBER]) &&
            (cfg.isLocal() == this.local) &&
            (cfg.getRfcTrace() == this.rfcTrace) &&
            (cfg.useSnc == this.useSnc) &&
            (cfg.authPartner.equals(this.authPartner)) &&
            (cfg.sncLib.equals(this.sncLib)) &&
            (cfg.sncName.equals(this.sncName)) &&
            (cfg.sncQop == this.sncQop)) &&
            (cfg.repositoryDestination.equals(this.repositoryDestination)) &&
            (cfg.useRepDest == this.useRepDest);
  }

  /**
   * @return
   */
  public String getSncLib() {
      return sncLib;
  }

  /**
   * @return
   */
  public String getSncName() {
      return sncName;
  }

  /**
   * @return
   */
  public int getSncQop() {
      return sncQop;
  }

  /**
   * @param string
   */
  public void setSncLib(String lib) {
      sncLib = lib;
  }

  /**
   * @param string
   */
  public void setSncName(String name) {
      sncName = name;
  }

  /**
   * @param i
   */
  public void setSncQop(int i) {
      sncQop = i;
  }

  /**
   * @return
   */
  public boolean getUseSnc() {
      return useSnc;
  }

  /**
   * @param b
   */
  public void setUseSnc(boolean b) {
      useSnc = b;
  }

  /**
   * @return
   */
  public String getAuthPartner() {
      return authPartner;
  }

  /**
   * @param string
   */
  public void setAuthPartner(String partner) {
      authPartner = partner;
  }

  private void writeObject(ObjectOutputStream oos)throws IOException {
    oos.defaultWriteObject();
  }

  private void readObject(ObjectInputStream ois)throws IOException , ClassNotFoundException{
      ois.defaultReadObject();
  }

  /**
   * @return
   */
  public boolean useOwnJCORepository() {
      return useOwnRepository;
  }

  /**
   * @param b
   */
  public void setUseOwnJCORepository(boolean b) {
      useOwnRepository = b;
  }

  /**
   * @param b
   */
  public void setJarm(boolean b) {
      useJarm = b;
  }

  /**
   * @return
   */
  public boolean getJarm() {
      return useJarm;
  }

  /**
   * @param b
   */
  public void setRfcTrace(boolean b) {
      rfcTrace = b;
  }

  /**
   * @return
   */
  public boolean getRfcTrace() {
      return rfcTrace;
  }
  
  /**
   * @param b
   */
  public void setGlobalJrfcTrace(boolean b) {
      globalJrfcTrace = b;
  }

  /**
   * @return
   */
  public boolean getGlobalJrfcTrace() {
      return globalJrfcTrace;
  }

  /**
   * @param b
   */
  public void setJcoTraceLevel(int i) {
      jcoTraceLevel = i;
  }

  /**
   * @return
   */
  public int getJcoTraceLevel() {
      return jcoTraceLevel;
  }
  
  /**
   * @param b
   */
  public void setCpicTraceLevel(int i) {
      cpicTraceLevel = i;
  }

  /**
   * @return
   */
  public int getCpicTraceLevel() {
      return cpicTraceLevel;
  }
  
  synchronized Properties getProperties()
  {
  	Properties props = new Properties();
  	props.put(BundleConfiguration.PROPERTIES[0],getProgramId());
  	props.put(BundleConfiguration.PROPERTIES[1],getGatewayHost());
  	props.put(BundleConfiguration.PROPERTIES[2],getGatewayService());
  	props.put(BundleConfiguration.PROPERTIES[3],String.valueOf(getProcessesNumber()));
    if (useRepDest)
    {
        props.put(BundleConfiguration.PROPERTIES[18],getRepositoryDestination());
    }
    else
    {
      	props.put(BundleConfiguration.PROPERTIES[4],getApplicationServerHost());
      	props.put(BundleConfiguration.PROPERTIES[5],getSystemNumber());
      	props.put(BundleConfiguration.PROPERTIES[6],getLogonClient());
      	props.put(BundleConfiguration.PROPERTIES[7],getLogonLanguage());
      	props.put(BundleConfiguration.PROPERTIES[8],getLogonUser());
    }
    
  	props.put(BundleConfiguration.PROPERTIES[9],String.valueOf(isRunning()));
  	props.put(BundleConfiguration.PROPERTIES[10],String.valueOf(isLocal()));
  	props.put(BundleConfiguration.PROPERTIES[11],String.valueOf(rfcTrace));
  	props.put(BundleConfiguration.PROPERTIES[12],String.valueOf(useSnc));
  	if (useSnc)
  	{
	  	props.put(BundleConfiguration.PROPERTIES[13],getSncName());
	  	props.put(BundleConfiguration.PROPERTIES[14],String.valueOf(getSncQop()));
	  	props.put(BundleConfiguration.PROPERTIES[15],getSncLib());
	  	props.put(BundleConfiguration.PROPERTIES[16],getAuthPartner());
  	}
  	props.put(BundleConfiguration.PROPERTIES[17],BundleConfiguration.PASSWORD_STARS);
  	return props;
  }
  
  public String toString() {
  	
  	/*
  	Vector v = new Vector();
  	StringBuffer sb = new StringBuffer();
  	sb.append(BundleConfiguration.PROPERTIES[0]);
  	sb.append('=');
  	sb.append(getProgramId());
  	sb.append(", ");
  	sb.append(BundleConfiguration.PROPERTIES[1]);
  	sb.append('=');
  	sb.append(getGatewayHost());
  	sb.append(", ");
  	sb.append(BundleConfiguration.PROPERTIES[2]);
  	sb.append('=');
  	sb.append(getGatewayServer());
  	sb.append(", ");
  	sb.append(BundleConfiguration.PROPERTIES[3]);
  	sb.append('=');
  	sb.append(getProcessesNumber());
  	sb.append(", ");
  	sb.append(BundleConfiguration.PROPERTIES[4]);
  	sb.append('=');
  	sb.append(getApplicationServerHost());
  	sb.append(", ");
  	sb.append(BundleConfiguration.PROPERTIES[5]);
  	sb.append('=');
  	sb.append(getSystemNumber());
  	sb.append(", ");
  	sb.append(BundleConfiguration.PROPERTIES[6]);
  	sb.append('=');
  	sb.append(getLogonClient());
  	sb.append(", ");
  	sb.append(BundleConfiguration.PROPERTIES[7]);
  	sb.append('=');
  	sb.append(getLogonLanguage());
  	sb.append(", ");
  	sb.append(BundleConfiguration.PROPERTIES[8]);
  	sb.append('=');
  	sb.append(getLogonUser());
  	sb.append(", ");
  	sb.append(BundleConfiguration.PROPERTIES[9]);
  	sb.append('=');
  	sb.append(String.valueOf(isRunning()));
  	sb.append(", ");
  	sb.append(BundleConfiguration.PROPERTIES[10]);
  	sb.append('=');
  	sb.append(String.valueOf(isLocal()));
  	if (getUseSnc())
  	{
  		sb.append(", ");
  		sb.append(BundleConfiguration.PROPERTIES[11]);
  	  	sb.append('=');
  		sb.append(getSncName());
  		sb.append(", ");
  		sb.append(BundleConfiguration.PROPERTIES[12]);
  	  	sb.append('=');
  		sb.append(String.valueOf(getSncQop()));
  		sb.append(", ");
  		sb.append(BundleConfiguration.PROPERTIES[13]);
  	  	sb.append('=');
  		sb.append(getSncLib());
  		sb.append(", ");
  		sb.append(BundleConfiguration.PROPERTIES[14]);
  	  	sb.append('=');
  		sb.append(getAuthPartner());
  	}
  	return sb.toString();
  	*/
	Properties props = getProperties();
  	return props.toString();
  }
  
  // these methods are only for compatibility support

  public synchronized boolean isUnicode() {
    return true;
  }


  public synchronized void setUnicodeState(boolean state) {
    
  }
  
  public synchronized void setProfileName(String profile) {
  }

  public synchronized String getProfileName() {
    return "";
  }
  public synchronized boolean isMiddleware() {
    return true;
  }
  
  /**
   * Gets the repository destination
   *
   * @return  The requested property
   */
  public synchronized String getRepositoryDestination() {
    return this.repositoryDestination;
  }

  /**
   * Sets the repository destination
   */
  public synchronized void setRepositoryDestination(String dest) {
    this.repositoryDestination = dest;
  }

}

