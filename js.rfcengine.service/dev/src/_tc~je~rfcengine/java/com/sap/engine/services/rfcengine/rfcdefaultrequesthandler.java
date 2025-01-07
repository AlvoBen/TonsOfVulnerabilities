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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.rfcengine.security.RFCCallbackHandler;
import com.sap.engine.services.rfcengine.security.impl.RFCCallbackHandlerImpl;
import com.sap.mw.jco.JCO;
import com.sap.tc.logging.Severity;


/**
 *  RFC Communication Service Frame. This handler is used to monitor a specific
 *  port where JCO request are assumed to come and handle them.
 *
 * @author  Petio Petev
 * @version 4.2
 */
public class RFCDefaultRequestHandler implements RFCRequestHandler {

  private Class[] sessionBeanParameterClasses = null;
  private RFCRuntimeInterfaceImpl runtimeInterface = null;
  private InitialContext ctx = null;

  private String ticket = null;
  private RFCJCOServer server = null;



  public RFCDefaultRequestHandler(RFCRuntimeInterface rintf, JCO.Server s) {
   runtimeInterface = (RFCRuntimeInterfaceImpl)rintf;
   server  = (RFCJCOServer)s;
   sessionBeanParameterClasses = new Class[] {JCO.Function.class};
  }

  public void handleRequest(JCO.Function function) throws Exception {

    String method = "DefaultRequestHandler.handleRequest(JCO.Function function)";
    LoginContext loginContext=null;
    Thread currentThread = Thread.currentThread();
    ClassLoader contextLoader = currentThread.getContextClassLoader() ;
    
    try
    {
	    if (ticket != null) {
            if (RFCApplicationFrame.isLogged(Severity.INFO))
                RFCApplicationFrame.logInfo("handleRequest", "SSO Ticket received from the R/3 side!", new String [] {});
	        RFCCallbackHandler handler = new RFCCallbackHandlerImpl(ticket);
	      try {
	      	loginContext = new LoginContext("evaluate_assertion_ticket", handler);
	      	loginContext.login();
	        } catch (LoginException le) {
	            //RFCApplicationFrame.logInfo("handleRequest", "SSO Ticket could not be authorized!", new String [] {});
	            StringBuffer text = new StringBuffer();
                text.append("call of FM ").append(function.getName());
                text.append(" to ProgId ").append(server.getProgID());
                try
                {
                    text.append(" on host ").append(java.net.InetAddress.getLocalHost().getHostName());
                } catch (java.net.UnknownHostException uhe)
                {
                    LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, "handleRequest", uhe);
                }
                
                text.append(" with SSO not authorized: ").append(le.getMessage()); 
                RuntimeException e = new RuntimeException(text.toString(),le);
                RFCApplicationFrame.logError(text.toString());
	        	LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, "handleRequest", e);
	            throw e;
	        }
            if (RFCApplicationFrame.isLogged(Severity.INFO))
                RFCApplicationFrame.logInfo("handleRequest", "R/3 call with SSO Ticket authorized!", new String [] {});
	    }

	    final String functionName = function.getName();
	    String beanName = (String) runtimeInterface.getFunctionNamesTable().get(functionName);
	
	    if(beanName == null) beanName = functionName; // allows invoking of beans not registered as functions
		Object home = null;

    	//loader service:rfcengine
		ClassLoader rfcLoader = this.getClass().getClassLoader() ;
		currentThread.setContextClassLoader(rfcLoader);
        
        if (ctx == null) ctx = new InitialContext();
	    try {
			if (beanName.startsWith("/")) beanName=beanName.substring(1);
	    	home = ctx.lookup("rfcaccessejb/" + beanName);
	    	if (home == null) 
	    	{
				throw new NamingException("home for bean "+beanName + " not found");
	    	}
		}catch(NamingException ne){
            StringBuffer text = new StringBuffer();
            text.append("Bean ").append(beanName);
            try
            {
                text.append(" not found on host ").append(java.net.InetAddress.getLocalHost().getHostName());
            } catch (java.net.UnknownHostException uhe)
            {
                LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, "handleRequest", uhe);
            }
            text.append(", ProgId =").append(server.getProgID());
            text.append(": ").append(ne.getMessage()); 
            RuntimeException e = new RuntimeException(text.toString(),ne);
            text.append("registered entries for FuctionName=JNDIName : ");
            text.append(runtimeInterface.getFunctionNamesTable().toString());
            RFCApplicationFrame.logError(text.toString());
            LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, "handleRequest", e);
			throw e;
	    }
	   
	    Method createMethod = null ;
	    Method processFunctionMethod = null ;
	    ClassLoader moduleLoader = home.getClass().getClassLoader();//application loader
	    currentThread.setContextClassLoader( moduleLoader ) ;
	    Object ejbobject = null ;
		try {
		  createMethod = home.getClass().getMethod("create", null);
		  
		} 
		catch (NoSuchMethodException nme) {                   
            StringBuffer text = new StringBuffer();
            text.append("No create method found in Bean ").append(beanName);
            try
            {
                text.append(" on host ").append(java.net.InetAddress.getLocalHost().getHostName());
            } catch (java.net.UnknownHostException uhe)
            {
                LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, "handleRequest", uhe);
            }
            text.append(", ProgId =").append(server.getProgID());
            text.append(": ").append(nme.getMessage()); 
            RuntimeException e = new RuntimeException(text.toString(),nme);
            RFCApplicationFrame.logError(text.toString());
            LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, "handleRequest", e);
		    throw e;
		}
		
		ejbobject = createMethod.invoke(home, null);
		
		try
		{
			processFunctionMethod = ejbobject.getClass().getMethod("processFunction", sessionBeanParameterClasses);
		}//try
		catch (NoSuchMethodException nme) {
            StringBuffer text = new StringBuffer();
            text.append("no processFunction method found in Bean ").append(beanName);
            try
            {
                text.append(" on host ").append(java.net.InetAddress.getLocalHost().getHostName());
            } catch (java.net.UnknownHostException uhe)
            {
                LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, "handleRequest", uhe);
            }
            text.append(", ProgId =").append(server.getProgID());
            text.append(": ").append(nme.getMessage()); 
            RuntimeException e = new RuntimeException(text.toString(),nme);
            RFCApplicationFrame.logError(text.toString());
            LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, "handleRequest", e);
            throw e;
		}//catch
		
		Object[] parameters = new Object[] {function};
		
		server.beginCall("NW:rfcengine:processFunction:"+beanName);
		processFunctionMethod.invoke(ejbobject, parameters);

	}//try
	catch (InvocationTargetException ite) {
		RFCApplicationFrame.logError(ite.toString());
		LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, ite);
		Throwable th = ite.getTargetException();
		Throwable cause = null;
		if (th instanceof Exception)
		{
			if (th instanceof JCO.AbapException)
				throw (JCO.AbapException)th;
			else if (th instanceof JCO.J2EEAbapException)
				throw (JCO.J2EEAbapException)th;
			cause = th.getCause();
			if (cause != null)
			{
				if (cause instanceof JCO.AbapException)
					throw (JCO.AbapException)cause;
				else if (cause instanceof JCO.J2EEAbapException)
					throw (JCO.J2EEAbapException)cause;
				
				cause = cause.getCause();
				if (cause != null)
				{
					if(cause instanceof JCO.AbapException)
						throw (JCO.AbapException)cause;
					else if (cause instanceof JCO.J2EEAbapException)
						throw (JCO.J2EEAbapException)cause;
				}
			}

			throw (Exception)th;
		}
		
		//else if (th instanceof Error) throw (Error)th;
		// if somebody extended Throwable by himself
		else throw new Exception(th);
	}
	catch (Exception ite) {
	    // this shouldn't happen
	    RFCApplicationFrame.logInfo("handleRequest", "Protocol encountered exception: ", new String [] {});
	    LoggingHelper.traceThrowable(Severity.INFO, RFCResourceAccessor.location, "handleRequest", ite);
	    throw ite;
	}
    finally{
        currentThread.setContextClassLoader( contextLoader ); //older loader
        if (loginContext!=null)
        {
            try
            {
                loginContext.logout();
            }
            catch (LoginException le)
            {
                LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, "handleRequest logout", le);
            }
        }
    }
  }
  
  private static String removeSign(char c, String s)
  {
	   int i;
	   while ((i = s.indexOf(c)) >= 0)
		  s = s.substring(0,i)+s.substring(i+1);
	   return s;
  }

  public void setTicket(String _ticket) {
    this.ticket = _ticket;
  }


}

