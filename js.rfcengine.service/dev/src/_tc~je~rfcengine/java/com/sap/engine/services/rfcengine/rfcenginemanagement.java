/*
 * Created on 17.05.2005
 */
package com.sap.engine.services.rfcengine;

import com.sap.mw.jco.*;
import com.sap.tc.logging.Severity;

import java.util.*;
import java.rmi.RemoteException;

/**
 * @author d035676
 *
 * JMX MBean implementation for configuration of the JCo RFC Provider Service
 */
public class RFCEngineManagement implements RFCEngineManagementMBean{
	private RFCRuntimeInterfaceImpl interfaceImpl = null;
	
	/**
	  * Constructor
	  *
	  * @param msgContext Message context to use
	  */
	public RFCEngineManagement(RFCRuntimeInterfaceImpl interf) 
	{
	  	 this.interfaceImpl = interf;
	}
	
	public int getMaxConnections()
	{
		return interfaceImpl.getMaxConnections();
	}
	  
	// management operations
	public String printInfo()
	{
		
		StringBuffer sb = new StringBuffer();
		sb.append("JCoRfcProvider Version ");
		sb.append(RFCRuntimeInterfaceImpl.Version);
		sb.append("<br>JCo API Version ");
		sb.append(JCO.getVersion());
		sb.append("<br>JavaRfc JCo middleware Version ");
		sb.append(JCO.getMiddlewareVersion());
		return sb.toString();
	}
	
	public String [] getDestinationNames()
	{
		return interfaceImpl.getDestinationNames();
	}
	
	public String getDestinationPropertiesTemplate()
	{
		return interfaceImpl.getDestinationPropertiesTemplate();
	}
	
	public Properties getDestinationProperties(String progId)
	{
		return interfaceImpl.getDestinationProperties(progId);
	}
	
	/**
	   * Sets Properties, that complete list and current values 
	   * could be seen by calling getDestinationProperties(String progId)
	   * @param  prgId   	Program Id of the bundle
	   * @param  key   		key of the property 
	   * @param  value   	value of the property 
	   */
	public boolean setDestinationProperties(Properties props)  throws RemoteException
	{
		interfaceImpl.setDestinationProperties(props);
		return true;
	}
	
	public boolean startDestination(String progId) throws RemoteException
	{
		interfaceImpl.startBundle(progId);
		return true;
	}
	
	public boolean stopDestination(String progId)  throws RemoteException
	{
		interfaceImpl.stopBundle(progId);
		return true;
	}
	
	public boolean removeDestination(String progId) throws RemoteException
	{
		interfaceImpl.removeBundle(progId);
		return true;
	}
	
	/**
	 * @param b
	 */
	public void setJrfcTrace(boolean on) {

		String method = "RFCEngineManagement.setGlobalJrfcTrace";
	  	String trace = JCO.getMiddlewareProperty("jrfc.trace");
	    if (trace != null)
	    {
	    	if (trace.equals("1") && (!on))
	    		JCO.setMiddlewareProperty("jrfc.trace","0");
	    	else if (trace.equals("0") && on)
	    	{
	    		interfaceImpl.resetTracePath();
	    		JCO.setMiddlewareProperty("jrfc.trace","1");
	    		RFCApplicationFrame.logInfo(method, "Global JRFC Trace activated ", null);
	    	}
	    }
	    else if (on) 
	    {
	    	interfaceImpl.resetTracePath();
	    	JCO.setMiddlewareProperty("jrfc.trace","1");
            if (RFCApplicationFrame.isLogged(Severity.INFO))
                RFCApplicationFrame.logInfo(method, "Global JRFC Trace activated ", null);
	    }  
	  }
	
	  /**
	   * @return
	   */
	  public boolean getJrfcTrace() {
	  	String trace = JCO.getMiddlewareProperty("jrfc.trace");
	  	if (trace == null || "0".equals(trace)) return false;
	  	return true;
	  }

	  /**
	   * @param b
	   */
	  public void setJcoTraceLevel(int newLevel) {
      
	  	String method = "RFCEngineManagement.setJcoTraceLevel";
	  	if (newLevel != JCO.getTraceLevel())
	    {
	  	  if (newLevel > 0)
	      {
	      	// set trace to 0 to close file writer
	  	  	if (JCO.getTraceLevel() != newLevel) JCO.setTraceLevel(0);
	  	  	interfaceImpl.resetTracePath();
	      	JCO.setTraceLevel(newLevel);
            if (RFCApplicationFrame.isLogged(Severity.INFO))
                RFCApplicationFrame.logInfo(method, "JCO Trace activated ", null);
	      }
	      else
	      {
	        JCO.setTraceLevel(0);
            if (RFCApplicationFrame.isLogged(Severity.INFO))
                RFCApplicationFrame.logInfo(method, "JCO Trace deactivated ", null);
	      }
	    }
	  }

	  /**
	   * @return
	   */
	  public int getJcoTraceLevel() {
	      return JCO.getTraceLevel();
	  }
  
	  /**
	   * @param b
	   */
	  public void setCpicTraceLevel(int newLevel) {
	  	// set cpic.trace property only if it is different from the existent
	  	String method = "RFCEngineManagement.setCpicTraceLevel";
	  	String trace = JCO.getMiddlewareProperty("cpic.trace");
	    if (trace != null)
	    {
	    	try {
	    		int level = Integer.parseInt(trace);
	    		if (level != newLevel)
	    		{
	    			JCO.setMiddlewareProperty("cpic.trace",String.valueOf(newLevel));
                    if (RFCApplicationFrame.isLogged(Severity.INFO))
                        RFCApplicationFrame.logInfo(method, "CPIC Trace changed to "+newLevel+"from "+level, null);
	    		}
			} catch (Exception e) {
				// $JL-EXC$ , works as designed
				RFCApplicationFrame.logError(method, "Error setting cpic level to "+newLevel,  null);
			}
	    	
	    }
	    else if (newLevel != 0) 
	    {
	    	JCO.setMiddlewareProperty("cpic.trace",String.valueOf(newLevel));
            if (RFCApplicationFrame.isLogged(Severity.INFO))
                RFCApplicationFrame.logInfo(method, "CPIC Trace set to "+newLevel, null);
	    }  
	  }
	
	  /**
	   * @return
	   */
	  public int getCpicTraceLevel() {
	  	String trace = JCO.getMiddlewareProperty("cpic.trace");
	  	if (trace != null)
	  	{
	  		try {
	    		int level = Integer.parseInt(trace);
	    		return level;
			} catch (Exception e) {
				// $JL-EXC$ , works as designed
			}
	  	}//if
	  	return 0;
	  }
	  
	  /**
       *  Returns Max server number.
       *  @return Max server number
       */
	  public int getMaxServerNumber()
	  {
		  return interfaceImpl.getMaxProcesses();
	  }
}
