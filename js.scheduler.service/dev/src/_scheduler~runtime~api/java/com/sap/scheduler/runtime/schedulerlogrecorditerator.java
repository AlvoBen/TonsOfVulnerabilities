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
package com.sap.scheduler.runtime;

import java.io.Serializable;

/**
 * Iterator object used to retrieve large log files in chunks of LogRecordScheduler[]
 * 
 * @author d040939
 *
 */
public class SchedulerLogRecordIterator implements Serializable { 

    static final long serialVersionUID = -1166711865709322043L;
    
    public static final long NO_MORE_CHUNKS = -1;
    
    /**
     * Id of the next job to be retrieved
     */
    private long pos;

    private SchedulerLogRecord[] schedulerLogRecordsArray;
    
    public SchedulerLogRecordIterator() {
        pos = 0;
    }
    
    /**
     * This method returns true if there is more data available, e.g.
     * the end of the log has not been reached.
     * 
     * @return true if more data is available, false otherwise
     */
    public boolean hasMoreChunks() {
        return pos != NO_MORE_CHUNKS;
    }
        
    /**
     * This method returns the next chunk of data. This method can only
     * be invoked once on the log iterator.
     * 
     * @return the next chunk of data
     */
    public SchedulerLogRecord[] nextChunk() {
        SchedulerLogRecord[] tmpLogs = schedulerLogRecordsArray;
        schedulerLogRecordsArray = null;
        return tmpLogs;
    }
        
    /**
     * Method to be used by the scheduler runtime only!
     */
    public void setPos(long lval) {
        this.pos = lval;
    }
    
    /**
     * Method to be used by the scheduler runtime only!
     */
    public long getPos() {
        return pos;
    }
    
    /**
     * Method to be used by the scheduler runtime only!
     */
    public void setLogRecords(SchedulerLogRecord[] log) {
        schedulerLogRecordsArray = log;
    }
}
