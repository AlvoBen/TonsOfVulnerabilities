/*
 * Created on 08.01.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.scheduler.impl.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.TimeZone;

import com.sap.engine.services.scheduler.impl.TaskPersistor;
import com.sap.engine.services.scheduler.util.DB;
import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;

public class FilterPersistor {

    /**
     * @param taskId
     * @param filters
     * @param c
     */
    public void persist(SchedulerTaskID taskId, Filter[] filters, Connection c) throws SQLException {
        PreparedStatement deletePS = null;
        PreparedStatement insertPS = null;
        try {
            if (filters.length == 0) return;
            deletePS = c.prepareStatement("delete from BC_JOB_E_FLTS where TASK_ID = ?");
            insertPS = c.prepareStatement("insert into BC_JOB_E_FLTS (TASK_ID, START_TIME, END_TIME, TIME_ZONE) values (?, ? , ?, ?)");
            deletePS.setBytes(1, taskId.getBytes());
            deletePS.executeUpdate();
            
            for (int i = 0; i < filters.length; i++) {
                insertPS.setBytes(1, taskId.getBytes());
                insertPS.setTimestamp(2, new Timestamp(filters[i].getStartTime().timeMillis()));
                insertPS.setTimestamp(3, new Timestamp(filters[i].getEndTime().timeMillis()));
                insertPS.setString(4, filters[i].getStartTime().getTimeZone().getID());
                insertPS.addBatch();
            }
            final int insertCount = DB.countSingleRowBatchInserts(insertPS.executeBatch());
            if (insertCount != filters.length)
                throw DB.createAndLogBadInsertCountExcetpion(filters.length, insertCount, TaskPersistor.location);
        } finally {
            if (deletePS != null) {
                deletePS.close();
            }
            if (insertPS != null) {
                insertPS.close();
            }
        }
    }

    /**
     * @param id
     * @return
     */
    public Filter[] read(SchedulerTaskID id, Connection c) throws SQLException {
        PreparedStatement ps = null;
        ResultSet currentRow = null;
        try {
            ps = c.prepareStatement("Select START_TIME, END_TIME, TIME_ZONE from BC_JOB_E_FLTS where TASK_ID = ?");
            ps.setBytes(1, id.getBytes());
            currentRow = ps.executeQuery();
            ArrayList<Filter> allFilters = new ArrayList<Filter>();
            while (currentRow.next()) {
                TimeZone tz = TimeZone.getTimeZone(currentRow.getString(3));
                final SchedulerTime startTime = new SchedulerTime(currentRow.getTimestamp(1).getTime(), tz);
                final SchedulerTime endTime = new SchedulerTime(currentRow.getTimestamp(2).getTime(), tz);
                allFilters.add(new Filter(startTime, endTime));
            }
            return (Filter[])allFilters.toArray(new Filter[allFilters.size()]);
        } finally {
            if (currentRow != null) {
                currentRow.close();
            } 
            if (ps != null) {
                ps.close();
            }
        }
    }
    
    
    /**
     * Removes Filters for a given SchedulerTaskID[].
     * 
     * @param taskIds the SchedulerTaskID[] which contains the taskIds
     * @param c the Connection
     * 
     * @throws SQLException if any error occurs
     */
    public void remove(SchedulerTaskID[] taskIds, Connection c) throws SQLException {        
        PreparedStatement ps = null;
        
        try {
            ps = c.prepareStatement("DELETE FROM BC_JOB_E_FLTS where TASK_ID = ?");    
            
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
