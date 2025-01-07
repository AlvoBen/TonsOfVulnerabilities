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

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.mw.jco.JCO;
import com.sap.tc.logging.Severity;
import java.io.*;

/**
 * @author  d035676
 * @version 1.0
 */
class JCOListener implements  JCO.ServerExceptionListener, JCO.ServerErrorListener,
                              JCO.ServerStateChangedListener
{

    private boolean isShown = false;
    /**
     * Constructor
     */
    public JCOListener() {
    }

    public void serverExceptionOccurred(JCO.Server srv, Exception ex) {
    	handleThrowable(srv, ex);
    }

    public void serverErrorOccurred(JCO.Server srv, Error err) {
    	handleThrowable(srv, err);
    }

    // currently do not differ between Exception and Error
    private void handleThrowable(JCO.Server srv, Throwable t) {

      if (!(srv instanceof RFCJCOServer)) return;
      final String method = "handleThrowable(Throwable t)";
      String programId = srv.getProgID();
      
      String errorString = "Server startup error on : host="+
            srv.getGWHost()+", service="+srv.getGWServ()+
            ", programid="+programId+ ". Error: "+t.getMessage();
      
      if (t instanceof JCO.Exception && ((JCO.Exception) t).getGroup() == JCO.Exception.JCO_ERROR_SERVER_STARTUP) 
      {
				
    	if (!(srv instanceof RFCJCOServer))
    	{
    		RFCApplicationFrame.logError(method, "Non-RFC Engine Server encounted",  null);
    		return;
    	}
    	Bundle bundle = ((RFCJCOServer)srv).m_bundle;
    	
    	if (bundle.onStartupError) //it is not the first time
    	{
    		if (RFCApplicationFrame.isLogged(Severity.INFO))
                RFCApplicationFrame.logInfo(method, errorString, null);
    	}
    	else
    	{
    		bundle.onStartupError = true;
    		bundle.getConfiguration().setRunningState(false);
    		// we trace it one time as error
    		RFCApplicationFrame.logError(method, errorString, null);
    	}
                
      }
      else
      {
    	  LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, t);
      }
    }

    /**
       *  Simply prints server state changes
    */
    public void serverStateChangeOccurred(JCO.Server server, int old_state, int new_state)
    {
    	if (!(server instanceof RFCJCOServer)) return;
    	RFCJCOServer srv = (RFCJCOServer)server;
    	
    	if ((new_state & JCO.STATE_LISTENING)!=0)
    	{
    		srv.m_bundle.onStartupError = false;
    		srv.m_bundle.getConfiguration().setRunningState(true);
    	}
    	if (RFCApplicationFrame.isLogged(Severity.INFO))
    	{
	        StringBuffer sb = new StringBuffer("Server ");
	        sb.append(server.getProgID()).append(" changed state from [");
	
	        if ((old_state & JCO.STATE_STOPPED    ) != 0) sb.append(" STOPPED ");
	        if ((old_state & JCO.STATE_STARTED    ) != 0) sb.append(" STARTED ");
	        if ((old_state & JCO.STATE_LISTENING  ) != 0) sb.append(" LISTENING ");
	        if ((old_state & JCO.STATE_TRANSACTION) != 0) sb.append(" TRANSACTION ");
	        if ((old_state & JCO.STATE_BUSY       ) != 0) sb.append(" BUSY ");
	
	        sb.append("] to [");
	        if ((new_state & JCO.STATE_STOPPED    ) != 0) sb.append(" STOPPED ");
	        if ((new_state & JCO.STATE_STARTED    ) != 0) sb.append(" STARTED ");
	        if ((new_state & JCO.STATE_LISTENING  ) != 0) sb.append(" LISTENING ");
	        if ((new_state & JCO.STATE_TRANSACTION) != 0) sb.append(" TRANSACTION ");
	        if ((new_state & JCO.STATE_BUSY       ) != 0) sb.append(" BUSY ");
	        sb.append("]");
        
        	RFCApplicationFrame.logInfo("", sb.toString(), null);
    	}
    }

    public void trace(int trace_level, java.lang.String message)
    {
        showAbout();
        if (RFCApplicationFrame.isLogged(Severity.INFO))
        	RFCApplicationFrame.logInfo("JCOListener.trace", message,  null);
    }

    private void showAbout()
    {
        if (isShown) return;
        try
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println("\n********************  "+new java.util.Date()+"   ***************************");
            new com.sap.mw.jco.About().printTo(pw);
            pw.flush();
            RFCApplicationFrame.logInfo("", sw.toString(),  null);
            pw.close();
            sw.close();
        }//try
        catch (java.lang.Exception ex)
        {
            RFCApplicationFrame.logError("", ex.getMessage(),  null);
        }//catch
        isShown = true;
    }
}

