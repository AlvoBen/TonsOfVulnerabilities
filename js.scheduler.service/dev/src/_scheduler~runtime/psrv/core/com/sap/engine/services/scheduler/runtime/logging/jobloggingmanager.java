/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.runtime.logging;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Logger;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduler.runtime.db.LogHandler;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.SchedulerLogRecordIterator;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This class handles the initialization for Job logging
 * 
 *  @author Dirk Marwinski
 */
public class JobLoggingManager {
    
    private final static Location location = Location.getLocation(JobLoggingManager.class);
    private final static Category category = LoggingHelper.SYS_SERVER;

    private ArrayList mCurrentLoggers = new ArrayList();
    private Environment mEnvironment;
    
    private ConcurrentLinkedQueue<Logger> mJDKLoggerPool = new ConcurrentLinkedQueue<Logger>();
    private AtomicInteger mJDKLoggerNextId = new AtomicInteger(0);
    
    public JobLoggingManager(Environment env) {
    	mEnvironment = env;
    }

    
    public Logger getLogger(Category jobLogCategory, Location jobLogLocation) {
        
        Logger jdkLogger = mJDKLoggerPool.poll();
        
        if (jdkLogger == null) {

            String name = "NWScheduler" + mJDKLoggerNextId.getAndIncrement();
            jdkLogger = Logger.getLogger(name);
        }

        // remove all handlers that may already have been defined
        Handler [] hdlrs = jdkLogger.getHandlers();
        for (Handler hdlr : hdlrs) {
            jdkLogger.removeHandler(hdlr);
        }

        SAPLoggingWrapperHandler sapHdlr = new SAPLoggingWrapperHandler(jobLogCategory, jobLogLocation);
        jdkLogger.addHandler(sapHdlr);
        jdkLogger.setUseParentHandlers(false);

        return jdkLogger;
    }
    
    public void returnLogger(Logger jdkLogger) {

        // remove all handlers
        Handler [] hdlrs = jdkLogger.getHandlers();
        for (Handler hdlr : hdlrs) {
            jdkLogger.removeHandler(hdlr);
        }
        
        mJDKLoggerPool.add(jdkLogger);
        
        if (location.beDebug()) {
            location.debugT("Current size of logger pool: " + mJDKLoggerPool.size());
        }
    }

    public synchronized Category initializeCategory(JobID jobid) {
        
        Category logct = Category.getCategory(Category.APPLICATIONS, "Jobs/GUID-" + jobid.toString());
        logct.removeLogs();
        if (location.beDebug()) {
        	location.debugT("Created new category \"" + logct.getName() + "\" for job \"" + jobid.toString() + "\".");
        }

        // Usage is ok here and agreed with LM
        // $JL-LOG_CONFIG$
    	logct.addLog(new DatabaseLogger(mEnvironment.getLogHandler(), jobid));
        // $JL-LOG_CONFIG$
        
        // usage is ok here and agreed with LM
    	// $JL-LOG_CONFIG$
        logct.setEffectiveSeverity(Severity.INFO);
        // $JL-LOG_CONFIG$
        
        return logct;
    }

    public synchronized Location initializeLocation(JobID logid) {

        // usage here has been agreed with Robert Boban
        
        // TODO
        // no matter what we do here, everything will be written to the 
        // application log. This is a bug in the logging and will be fixed
        // according to Robert Boban
        
    	// create new empty location (no logs will be written to 
    	// that location)
        Location loc = Location.getLocation("empty");

        // never write anything to this location
    	// usage is ok here and agreed with LM
    	
        // $JL-LOG_CONFIG$
        loc.setEffectiveSeverity(Severity.NONE);
        // $JL-LOG_CONFIG$
        loc.removeLogs();
        // $JL-LOG_CONFIG$
        
        return loc;
    }
    
    public void closeCategory(Category jobLogger) {
        
        // explicitly close this category
        //
        jobLogger.release();
    }
    
    public void closeLocation(Location jobLogger) {
        jobLogger.release();
    }
        
    public LogIterator getLog(JobID jobid, LogIterator iter, int size) 
                                                   throws NoSuchJobException,
                                                          SQLException {

    	LogHandler hdlr = mEnvironment.getLogHandler();

    	iter = hdlr.getLog(jobid, iter, size);
        return iter;
    }
    
    
    public SchedulerLogRecordIterator getLog(JobID jobid, SchedulerLogRecordIterator iter, int size) 
                                                   throws NoSuchJobException,
                                                          SQLException {

        LogHandler hdlr = mEnvironment.getLogHandler();

        iter = hdlr.getLogRecords(jobid, iter, size);
        return iter;
    }
    
    
    public synchronized void deleteLog(JobID jobid) throws NoSuchJobException,
                                                           SQLException {
        
        
        // try to delete log from db
        //
        LogHandler hdlr = mEnvironment.getLogHandler();

    	hdlr.deleteLog(jobid);
    }
}
