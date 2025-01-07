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

import com.sap.engine.services.scheduler.runtime.db.LogHandler;
import com.sap.scheduler.runtime.JobID;
import com.sap.tc.logging.Log;
import com.sap.tc.logging.LogRecord;

/**
 * This class receives log requests from SAP logging and writes them 
 * to the special job scheduler database log tables.
 * 
 * @author Dirk Marwinski
 */
public class DatabaseLogger extends  Log {

	private String mEncoding;
	private long position = -1;
    private JobID mJobId;
	
	private LogHandler mLogHandler;
	
	DatabaseLogger(LogHandler hdlr, JobID jobid) {
		mLogHandler = hdlr;
        mJobId = jobid;
	}
	
	public void setEncoding(String enc) {
		mEncoding = enc;
	}
	
	public String getEncoding() {
		return mEncoding;
	}
	
	protected String writeInt(LogRecord rec) {
		position++;
		mLogHandler.addEntry(rec, position, mJobId);
		return "";
	}
}
