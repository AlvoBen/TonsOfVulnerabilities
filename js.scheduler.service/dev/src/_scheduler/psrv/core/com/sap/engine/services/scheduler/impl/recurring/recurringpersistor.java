/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.impl.recurring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.TimeZone;

import com.sap.engine.services.scheduler.impl.TaskPersistor;
import com.sap.engine.services.scheduler.util.DB;
import com.sap.scheduler.api.RecurringEntry;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;

/**
 * @author Hristo Sabev (i027642)
 */
public class RecurringPersistor {

	/**
	 * @param taskId
	 * @param recurringEntries
	 * @param c
	 */
	public void persist(SchedulerTaskID taskId, RecurringEntry[] recurringEntries, Connection c) throws SQLException {
		if (recurringEntries.length == 0)
			return;
        StringBuilder buf = new StringBuilder();
        buf.append("insert into BC_JOB_RECURRINGS (TASK_ID, TIME_ZONE, START_TIME, END_TIME, WAIT_PERIOD) ");
        buf.append("values (?, ?, ?, ? ,?)");
        
        PreparedStatement ps = null; 
        
		try {
            ps = c.prepareStatement(buf.toString());
            
            for (int i = 0; i < recurringEntries.length; i++) {
            	ps.setBytes(1, taskId.getBytes());
            	ps.setString(2, recurringEntries[i].getStartTime().getTimeZone().getID());
            	ps.setTimestamp(3, new Timestamp(recurringEntries[i].getStartTime().timeMillis()));
            	SchedulerTime endTime = recurringEntries[i].getEndTime();
            	if (endTime == null) {
            		ps.setTimestamp(4, null);
            	} else {
            		ps.setTimestamp(4, new Timestamp(recurringEntries[i].getEndTime().timeMillis()));
            	}
            	ps.setLong(5, recurringEntries[i].getPeriod());
            	ps.addBatch();
            }
            int insertCount = DB.countSingleRowBatchInserts(ps.executeBatch());
            if (insertCount != recurringEntries.length)
            	throw DB.createAndLogBadInsertCountExcetpion(recurringEntries.length, insertCount, TaskPersistor.location);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

	}

	/**
	 * @param id
	 * @return
	 */
	public RecurringEntry[] read(SchedulerTaskID id, Connection c) throws SQLException {
        String stmt = "select TIME_ZONE, START_TIME, END_TIME, WAIT_PERIOD from BC_JOB_RECURRINGS where TASK_ID = ?";
        PreparedStatement ps = null;
        ResultSet current = null;

        try {
            ps = c.prepareStatement(stmt);
            ps.setBytes(1, id.getBytes());
            current = ps.executeQuery();
            ArrayList<RecurringEntry> allEntries = new ArrayList<RecurringEntry>();
            while (current.next()) {
                final TimeZone tz = TimeZone.getTimeZone(current.getString(1));
                final SchedulerTime startTime = new SchedulerTime(current.getTimestamp(2).getTime(), tz);
                final Timestamp endTimestamp = current.getTimestamp(3);
                final SchedulerTime endTime = endTimestamp == null ? null : new SchedulerTime(endTimestamp, tz);
                final long waitPeriod = current.getLong(4);
                if (waitPeriod == 0) { // only one iteration
                    allEntries.add(new RecurringEntry(startTime, endTime, 1)); // one iteration entry
                } else {
                    allEntries.add(new RecurringEntry(startTime, endTime, waitPeriod));
                }
            }
    
            return (RecurringEntry[]) allEntries.toArray(new RecurringEntry[allEntries.size()]);
        } finally {
            if (current != null) {
                current.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }
    
    
    /**
     * Removes RecurringEntries for a given SchedulerTaskID[].
     * 
     * @param taskIds the SchedulerTaskID[] which contains the taskIds
     * @param c the Connection
     * 
     * @throws SQLException if any error occurs
     */
    public void remove(SchedulerTaskID[] taskIds, Connection c) throws SQLException {        
        PreparedStatement ps = null;
        
        try {
            ps = c.prepareStatement("DELETE FROM BC_JOB_RECURRINGS where TASK_ID = ?");    
            
            for (int i = 0; i < taskIds.length; i++) {
                ps.setBytes(1, taskIds[i].getBytes());
                ps.addBatch();
            }
            
            // flush the changes
            ps.executeBatch();
            
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

}
