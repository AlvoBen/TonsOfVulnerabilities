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
 * Iterator object used to retrieve large log files in chunks.
 * 
 * @author Dirk Marwinski
 *
 */
public class LogIterator implements Serializable {

    static final long serialVersionUID = -1166711865709322043L;
    
    /**
     * Id of the next job to be retrieved
     */
    private long pos;

    private String msg;
    
    public LogIterator() {
        pos = 0;
    }
    
    /**
     * This method returns true if there is more data available, e.g.
     * the end of the log has not been reached.
     * 
     * @return true if more data is available, false otherwise
     */
    public boolean hasMoreChunks() {
        return pos != -1;
    }
        
    /**
     * This method returns the next chunk of data. This method can only
     * be invoked once on the log iterator.
     * 
     * @return the next chunk of data
     */
    public String nextChunk() {
        String tmp = msg;
        msg = null;
        return tmp;
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
    public void setLog(String log) {
        msg = log;
    }
}
