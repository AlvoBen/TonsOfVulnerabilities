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
package com.sap.engine.services.scheduler.impl.cron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimeZone;

import com.sap.engine.services.scheduler.impl.TaskPersistor;
import com.sap.engine.services.scheduler.util.DB;
import com.sap.scheduler.api.CronEntry;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.fields.CronDOMField;
import com.sap.scheduler.api.fields.CronDOWField;
import com.sap.scheduler.api.fields.CronHourField;
import com.sap.scheduler.api.fields.CronMinuteField;
import com.sap.scheduler.api.fields.CronMonthField;
import com.sap.scheduler.api.fields.CronYearField;

/**
 * @author Hristo Sabev (i027642)
 */
public class CronPersistor {

	/**
	 * @param taskId
	 * @param cronEntries
	 * @param c
	 */
	public void persist(SchedulerTaskID taskId, CronEntry[] cronEntries, Connection c) throws SQLException {
		if (cronEntries.length == 0) 
            return; 
        
        StringBuilder buf = new StringBuilder();
        buf.append("insert into BC_JOB_CRONS (TASK_ID, MINUTES, HOURS, DAY_OF_MONTH, MONTHS, DAY_OF_WEEK, YEARS, TIME_ZONE) ");
        buf.append("values (?, ?, ?, ?, ?, ?, ?, ?)");
        
        PreparedStatement ps = null;
        
        try {
    		ps = c.prepareStatement(buf.toString());
            
    		for (int i = 0; i < cronEntries.length; i++) {
    			ps.setBytes(1, taskId.getBytes());
    			ps.setString(2, cronEntries[i].getMinutes().persistableValue());
    			ps.setString(3, cronEntries[i].getHours().persistableValue());
    			ps.setString(4, cronEntries[i].getDays_of_month().persistableValue());
    			ps.setString(5, cronEntries[i].getMonths().persistableValue());
    			ps.setString(6, cronEntries[i].getDays_of_week().persistableValue());
    			ps.setString(7, cronEntries[i].getYears().persistableValue());
    			ps.setString(8, cronEntries[i].getTimeZone().getID());
    			ps.addBatch();
    		}
    		final int insertCount = DB.countSingleRowBatchInserts(ps.executeBatch());
    		if (insertCount != cronEntries.length)
    			throw DB.createAndLogBadInsertCountExcetpion(cronEntries.length, insertCount, TaskPersistor.location);
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
	public CronEntry[] read(SchedulerTaskID id, Connection c) throws SQLException {
        StringBuilder buf = new StringBuilder();
        buf.append("select MINUTES, HOURS, DAY_OF_MONTH, MONTHS, DAY_OF_WEEK, YEARS, TIME_ZONE ");
        buf.append("from BC_JOB_CRONS where TASK_ID = ?");
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
		try {
            ps = c.prepareStatement(buf.toString());
            
            ps.setBytes(1, id.getBytes());
            rs = ps.executeQuery();
            final ArrayList<CronEntry> read = new ArrayList<CronEntry>();
            while (rs.next()) {
            	final CronMinuteField mins = new CronMinuteField(rs.getString(1));
            	final CronHourField hours = new CronHourField(rs.getString(2));
            	final CronDOMField doms = new CronDOMField(rs.getString(3));
            	final CronMonthField months = new CronMonthField(rs.getString(4));
            	final CronDOWField dows = new CronDOWField(rs.getString(5));
            	final CronYearField years = new CronYearField(rs.getString(6));
            	final TimeZone tz = TimeZone.getTimeZone(rs.getString(7));
            	final CronEntry thisRow = new CronEntry(years, months, doms, dows, hours, mins, tz);
            	read.add(thisRow);
            }
            return (CronEntry[])read.toArray(new CronEntry[read.size()]);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
	}
    
        
    /**
     * Removes CronEntries for a given SchedulerTaskID[].
     * 
     * @param taskIds the SchedulerTaskID[] which contains the taskIds
     * @param c the Connection
     * 
     * @throws SQLException if any error occurs
     */
    public void remove(SchedulerTaskID[] taskIds, Connection c) throws SQLException {        
        PreparedStatement ps = null;
        
        try {
            ps = c.prepareStatement("DELETE FROM BC_JOB_CRONS where TASK_ID = ?");    
            
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
