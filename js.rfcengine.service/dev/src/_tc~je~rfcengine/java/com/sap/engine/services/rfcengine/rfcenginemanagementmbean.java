/*
 * Created on 17.05.2005
 */
package com.sap.engine.services.rfcengine;

import java.rmi.RemoteException;
import java.util.Properties;

/**
 * @author d035676
 *
 * JMX MBean interface for configuration of the JCo RFC Provider Service
 */
public interface RFCEngineManagementMBean {
  
	  /**
	   * Returns an info about JCo RFC Provider service
	   * @return  an info about service
	   */
	  public String printInfo();
	  
	  /**
	   * Returns a String array representation of all configured RFC Destinations
	   * @return  a String array representation of all configured RFC Destinations
	   */
	  public String [] getDestinationNames();
	  
	  /**
	   * Returns a String template with the smallest required set of
	   * configuration properties that could be used (by cut and paste) to
	   * configure a new RFC Destinations
	   * @return  a String template
	   */
	  public String getDestinationPropertiesTemplate();
	  
	  
	  /**
	   * Returns a Properties representing Destination configuration
	   * and their values. See description of the properties at 
	   * @see   com.sap.engine.services.rfcengine.RFCEngineManagementMBean#setDestinationProperties
	   * @param  prgId   Program Id of the RFC Destination
	   * @return  Properties representing Destination configuration
	   * and their values. Returns null, if the Destination doesn't exist
	   */
	  public Properties getDestinationProperties(String progId);
	  
	  /**
	   * If the Destination with configured ProgramId doesn't exist, then
	   * a new Destination is created. If it exists already, then configured
	   * properties will be updated.
	   * Property ProgramId should be set in any cases, otherwise Exception 
	   * is thrown.<br>
	   * List of possible properties: <br>
	   * <code>ProgramId</code> - ProgramId of the Destination as configured in sm59 <br>
	   * <code>GatewayHost</code> - gateway host as configured in sm59. On default same as ApplicationServerHost<br>
	   * <code>GatewayService</code> - gateway service as configured in sm59. On default it is the SystemNumber of the configured ApplicationServerHost<br>
	   * <code>ServerCount</code> - number of simultaneously running servers <br>
	   * <code>ApplicationServerHost</code> - Application Server from which a MetaData information of called FunctionModules is taken<br>
	   * <code>SystemNumber</code> - SystemNumber (port) over which the local host communicates with ApplicationServerHost<br>
	   * <code>Client</code> - logon client for ApplicationServerHost<br>
	   * <code>User</code> - logon user for ApplicationServerHost<br>
	   * <code>Password</code> - logon user for ApplicationServerHost<br>
	   * <code>Language</code> - logon language for ApplicationServerHost. On default is English<br>
	   * <code>Local</code> - true to run servers only on one J2EE Server instance, false to run servers on each Server instance<br>
	   * <code>RfcTrace</code> - true to activate RfcTrance for actual Destination<br>
	   * <code>UseSnc</code> - true to activate SNC. If activated at least configuration of property SncAuthorizationPartner is required<br>
	   * <code>SncLib</code> - path to SncLib. On default the system path<br>
	   * <code>SncName</code> - SNC MyName. On default the SNC MyName of the actual user<br>
	   * <code>SncQop</code> - level of the SNC encryption. Values 1..9<br>
	   * <code>SncAuthorizationPartner</code> - SNC Partner name<br>
       * <code>RepositoryDestination</code> - Repository Destination name<br>
	   * <br>
	   * List and current values of already configured properties for 
	   * given Destination please see by calling
	   * @see   com.sap.engine.services.rfcengine.RFCEngineManagementMBean#getDestinationProperties
	   * @param  props configuration properties
	   * @exception  RemoteException   Thrown if a problem occurs.
	   */
	  public boolean setDestinationProperties(Properties props)  throws RemoteException;
	  
	  /**
	   * Starts servers for Destination with progId
	   *
	   * @param  prgId   Program Id of the bundle
	   * @return  TRUE if the bundle exists and started successfully
	   * If the method is called for already started Destination, return false.
	   * @exception  RemoteException thrown if a problem occurs.
	   */
	  public boolean startDestination(String progId) throws RemoteException;

	  /**
	   * Stops servers for Destination with progId
	   *
	   * @param  prgId   Program Id of the bundle
	   * @return  TRUE if the bundle exists and stopped successfully
	   * If the method is called for already stopped Destination, return false.
	   * @exception  RemoteException thrown if a problem occurs.
	   */
	  public boolean stopDestination(String progId)  throws RemoteException;
		
	  /**
	   * Removes Destination with progId
	   *
	   * @param  prgId   Program Id of the bundle
	   * @return  TRUE if the bundle exists and stopped successfully
	   * If the method is called for non-existent, return false.
	   * @exception  RemoteException thrown if a problem occurs.
	   */
	  public boolean removeDestination(String progId) throws RemoteException;

	  /**
	   * Sets JrfcTrace for all RFC connections created after trace activation
	   * in the actual process
	   * @param  boolean true to turn on the trace, false to turn off it.
	   */
	  public void setJrfcTrace(boolean on);
	
	  /**
	   * Returns boolean. True for activated traces, false for deactivated traces.
	   * @return whether traces are activated or not
	   */
	  public boolean getJrfcTrace();

	  /**
	   * Sets JCo trace level (0..10) for all existing and new RFC connections
	   * in the actual process. 
	   * 0  - trace is deactivated. 
	   * 1  - lowest level
	   * 10 - highest level
	   * @param  int trace level
	   */
	  public void setJcoTraceLevel(int newLevel);

	  /**
       *  Returns JCo trace level.
       *  @see   com.sap.engine.services.rfcengine.RFCEngineManagementMBean#setJcoTraceLevel
       *  @return JCo trace level
       */
	  public int getJcoTraceLevel();
  
	  /**
	   * Sets CPIC trace level (0..3) for all existing and new RFC connections
	   * in the actual process. 
	   * 0  - trace is deactivated. 
	   * 1  - lowest level
	   * 3  - highest level
	   * @param  int trace level
	   */
	  public void setCpicTraceLevel(int newLevel);
	
	  /**
       *  Returns CPIC trace level.
       *  @see   com.sap.engine.services.rfcengine.RFCEngineManagementMBean#setCpicTraceLevel
       *  @return CPIC trace level
       */
	  public int getCpicTraceLevel();
	  
	  /**
       *  Returns Max server number.
       *  @return Max server number
       */
	  public int getMaxServerNumber();
}
