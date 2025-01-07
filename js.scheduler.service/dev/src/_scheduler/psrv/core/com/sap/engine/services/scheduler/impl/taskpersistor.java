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
package com.sap.engine.services.scheduler.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sap.engine.services.scheduler.impl.cron.CronPersistor;
import com.sap.engine.services.scheduler.impl.filter.FilterPersistor;
import com.sap.engine.services.scheduler.impl.parameter.ParameterPersistor;
import com.sap.engine.services.scheduler.impl.recurring.RecurringPersistor;
import com.sap.scheduler.api.CronEntry;
import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.RecurringEntry;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.TaskDoesNotExistException;
import com.sap.scheduler.api.TaskStatus;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.SchedulerRuntimeException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Hristo Sabev (i027642)
 *
 */
public class TaskPersistor {
	public final static Location location = Location.getLocation(TaskPersistor.class);
	private static final Category category = Category.SYS_SERVER;
	
	private final CronPersistor m_cp = new CronPersistor();
	private final RecurringPersistor m_rp = new RecurringPersistor();
	private final FilterPersistor m_fp = new FilterPersistor();
	private final ParameterPersistor m_pp = new ParameterPersistor();
	private final ServiceFrame m_service;
	
    protected TaskPersistor(ServiceFrame theService) {
		this.m_service = theService;
	}
	
	
    /**
     * Method persists a task with the given fields. The owner and runAsUser must
     * not be null.
     * 
     * @param task the SchedulerTask 
     * @param owner the owner String
     * @param runAsUser the runAsUser String
     * @param c the SQLConnection
     * 
     * @throws SQLException if any DB-error occurs
     */
    protected void persist(SchedulerTask task, String owner, String runAsUser, Connection c) throws SQLException {
        PreparedStatement ps = null;
        try {
            StringBuilder stmt = new StringBuilder();
            stmt.append("insert into BC_JOB_TASKS (TASK_ID, JOB_DEFINITION_ID, RUN_AS_USER, SCHEDULING_USER, ");
            stmt.append("TIME_SUBMITTED, RETENTION_PERIOD, STATUS, NAME, DESCRIPTION, CUSTOM_DATA, TASK_SOURCE, STATUS_DESC) ");
            stmt.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    
    		ps = c.prepareStatement(stmt.toString());
            
    		ps.setBytes(1, task.getTaskId().getBytes());
    		ps.setBytes(2, task.getJobDefinitionId().getBytes());
    		ps.setString(3, runAsUser);
    		ps.setString(4, owner);
    		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    		ps.setTimestamp(5, currentTime);
    		ps.setInt(6, task.getRetentionPeriod());
    		ps.setShort(7, task.getTaskStatus().getValue());
            
            String name = task.getName();
            if (name == null) {
                // take the name from job definition
                name = m_service.theRuntime().getJobDefinitionById(task.getJobDefinitionId()).getJobDefinitionName().getName();
            }
            ps.setString(8, name);
            
            // in case we got an empty String for the description, we need to set null
            String taskDesc = null;
            if ( task.getDescription() != null && !task.getDescription().trim().equals("") ) {
                taskDesc = task.getDescription();
            }
            ps.setString(9, taskDesc);
            
            // in case we got an empty String for the customData, we need to set null
            String custData = null;
            if ( task.getCustomData() != null && !task.getCustomData().trim().equals("") ) {
                custData = task.getCustomData();
            }
            ps.setString(10, custData);
            
            ps.setShort(11, task.getTaskSource()); 
            if (task.getTaskStatus().getDescriptionValue() != TaskStatusInternal.DESCRIPTION_UNDEFINED) {
                ps.setShort(12, task.getTaskStatus().getDescriptionValue());
            } else {
                ps.setShort(12, TaskStatusInternal.activeInitialAPI.getDescriptionValue()); // Task comes from an API-call
            }
            
    		int updatesCount = ps.executeUpdate();
    		if (updatesCount != 1) throw new SchedulerRuntimeException("One new task with id "
    				+ task.getTaskId()+ " was inserted, however " + updatesCount + " rows were reported as updated." +
    						" This is a severe problem as the jdbc driver didn't throw SQLException");
    		m_cp.persist(task.getTaskId(), task.getCronEntries(), c);
    		m_rp.persist(task.getTaskId(), task.getRecurringEntries(), c);
    		m_fp.persist(task.getTaskId(), task.getFilters(), c);
    		m_pp.persist(task.getTaskId(), task.getJobParameters(), c);
            
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
	}
    
    
    /**
     * Removes all tasks which are not in status active or hold and older than 
     * a specific timestamp.
     * 
     * @param ts - the timestamp which indicates whether the task should be 
     *             removed or not
     * @param c - the connection
     *             
     * @return the amount of removed SchedulerTask 
     * 
     * @throws SQLException if any DB-error occurs
     */
    protected int removeTasks(Timestamp ts, Connection c) throws SQLException {
        PreparedStatement psSelect = null;
        PreparedStatement psDelete = null;
        ResultSet rsSelect = null;
        ArrayList<SchedulerTaskID> taskIdsToDelete = new ArrayList<SchedulerTaskID>();
        
        try {
            // first access all Tasks for which the delete-criteria match
            String selectStmt  = "SELECT TASK_ID FROM BC_JOB_TASKS WHERE NOT (STATUS = ?) AND NOT (STATUS = ?) AND TIME_FINISHED < ?";
            psSelect = c.prepareStatement(selectStmt.toString());
            psSelect.setShort(1, TaskStatus.active.getValue());
            psSelect.setShort(2, TaskStatus.hold.getValue());
            psSelect.setTimestamp(3, ts);
            rsSelect = psSelect.executeQuery();
            
            while(rsSelect.next()) {
                taskIdsToDelete.add(SchedulerTaskID.parseID(rsSelect.getBytes("TASK_ID")));
            }
            
            // delete now this tasks in BC_JOB_TASKS
            psDelete = c.prepareStatement("DELETE FROM BC_JOB_TASKS WHERE TASK_ID = ?");            
            for (int i = 0; i < taskIdsToDelete.size(); i++) {
                psDelete.setBytes(1, taskIdsToDelete.get(i).getBytes());
                psDelete.addBatch();
            }
            // flush the changes
            psDelete.executeBatch();
            
            SchedulerTaskID[] taskIds = (SchedulerTaskID[])taskIdsToDelete.toArray(new SchedulerTaskID[taskIdsToDelete.size()]);
            // delete the entries in CronEntry-table
            m_cp.remove(taskIds, c);
            
            // delete the entries in RecurringEntry-table
            m_rp.remove(taskIds, c);
            
            // delete the entries in Filter-table
            m_fp.remove(taskIds, c);
            
            // delete the entries in JobParameter-table
            m_pp.remove(taskIds, c);
            
            return taskIds.length;
        } finally {
            if (rsSelect != null) {
                rsSelect.close();
            }
            if (psSelect != null) {
                psSelect.close();
            }
            if (psDelete != null) {
                psDelete.close();
            }
        }
    }
	
    
    /**
     * Cancels a SchedulerTask where the state is ACTIVE or HOLD for a given user.
     * If the given user is null the owner will be ignored because this method 
     * is called by an administrator.
     * 
     * @param SchedulerTaskID the SchedulerTaskID 
     * @param String the owner string (scheduling- or runAs-user). If the ownerId 
     *               is null we handle it as the Administrator-user
     * @param Connection the incoming SQLConnection
     * @param TaskStatus the status to set
     * 
     * @exception SQLException if any DB-error occurrs
     * @exception TaskDoesNotExistException if the Task does not more exist
     */
    protected void markCancelled(SchedulerTaskID id, String ownerId, Connection c, TaskStatus status) throws SQLException, TaskDoesNotExistException {
        updateStatusAndFinishedTime(id, null, status, ownerId, new Timestamp(System.currentTimeMillis()), c);
	}
    
    
    /**
     * Sets a SchedulerTask to state HOLD where the state is ACTIVE for 
     * a given user. 
     * If the given user is null the owner will be ignored because this method 
     * is called by an administrator.
     * 
     * @param SchedulerTaskID the SchedulerTaskID 
     * @param String the owner string (scheduling- or runAs-user). If the ownerId 
     *               is null we handle it as the Administrator-user
     * @param Connection the incoming SQLConnection
     * @param TaskStatus the status to set
     * 
     * @exception SQLException if any DB-error occurrs
     * @exception TaskDoesNotExistException if the Task does not more exist
     */
    protected void markHold(SchedulerTaskID id, String ownerId, Connection c, TaskStatus status) throws SQLException, TaskDoesNotExistException {
        updateStatusAndFinishedTime(id, TaskStatus.active, status, ownerId, new Timestamp(System.currentTimeMillis()), c);
    }
    
    
    /**
     * Sets a SchedulerTask to state ACTIVE where the state is HOLD for 
     * a given user. 
     * If the given user is null the owner will be ignored because this method 
     * is called by an administrator.
     * 
     * @param SchedulerTaskID the SchedulerTaskID 
     * @param String the owner string (scheduling- or runAs-user). If the ownerId 
     *               is null we handle it as the Administrator-user
     * @param Connection the incoming SQLConnection
     * 
     * @exception SQLException if any DB-error occurrs
     * @exception TaskDoesNotExistException if the Task with the SchedulerTaskID 
     *                                      and set to state HOLD does not more exist
     */
    protected void markReleased(SchedulerTaskID id, String ownerId, Connection c) throws SQLException, TaskDoesNotExistException {
        // set null for Timestamp, because we want to reset it
        updateStatusAndFinishedTime(id, TaskStatus.hold, TaskStatusInternal.activeReleased, null, null, c);
    }
    
	
    /**
     * Method sets all Tasks to cancelled which were deployed from the ZeroAdminTemplate 
     * (user independent). The commit will be performed by the caller.
     * 
     * @param c the connection
     * @throws SQLException if any DB-error occurs
     */
    protected void markCancelledForZeroAdminTasks(Connection c) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement("update BC_JOB_TASKS set TIME_FINISHED = ?, STATUS = ?, STATUS_DESC = ? where TASK_SOURCE = ? and (STATUS = ? or STATUS = ?)");
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(1, currentTime);
            ps.setShort(2, TaskStatus.finished.getValue());
            ps.setShort(3, TaskStatusInternal.finishedCancelledZeroAdmin.getDescriptionValue());
            ps.setShort(4, SchedulerTask.TASK_SOURCE_ZERO_ADMIN);
            ps.setShort(5, TaskStatus.active.getValue());
            ps.setShort(6, TaskStatus.hold.getValue());
            // flush the changes
            int updatesCount = ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }
    
	
    /**
     * Sets a SchedulerTask to a given state where the state is ACTIVE (user-
     * independent). The finish-time will also be set.
     * 
     * @param owner the scheduling- or runAs-user - may be null
     * @param SchedulerTaskID the SchedulerTaskID 
     * @param Connection the incoming SQLConnection
     * 
     * @exception SQLException if any DB-error occurrs
     * @exception TaskDoesNotExistException if the Task with the SchedulerTaskID 
     *                                      does not more exist
     */
    protected void markFinished(String owner, SchedulerTaskID id, Connection c) throws SQLException, TaskDoesNotExistException {
        updateStatusAndFinishedTime(id, TaskStatus.active, TaskStatusInternal.finishedFinished, owner, new Timestamp(System.currentTimeMillis()), c);
	}
    
    
    /**
     * Private helper method which persists the new task state and the finished time.
     * 
     * @param id the SchedulerTaskID
     * @param oldStatusForSelect the old state which will be used for the select 
     *                           in the where clause. It it is null the select will
     *                           be performed with TaskStatus.active & TaskStatus.hold
     * @param owner the scheduling- or runAs-user - may be null
     * @param statusToSet the new state to set
     * @param timestampToSet the timestamp to set as time finished
     * @param c the SQLConnection
     * 
     * @throws SQLException if any db-error occurs
     * @throws TaskDoesNotExistException if the task with the SchedulerTaskID and 
     *                                   the oldStatusForSelect does not exist
     */
    private int updateStatusAndFinishedTime(SchedulerTaskID id, TaskStatus oldStatusForSelect, TaskStatus statusToSet, String owner, Timestamp timestampToSet, Connection c) throws SQLException, TaskDoesNotExistException {
        PreparedStatement ps = null;
        try {
            String stmt = "update BC_JOB_TASKS set TIME_FINISHED = ?, STATUS = ?, STATUS_DESC = ? where ";
            
            TaskFilter filter = new TaskFilter();
            filter.setTaskID(id);
            filter.setTaskStatus(oldStatusForSelect);
            filter.setTaskOwnerUser(owner);
            
            ps = createStatement(stmt, c, filter, 3);
            
            ps.setTimestamp(1, timestampToSet); 
            ps.setShort(2, statusToSet.getValue());
            ps.setShort(3, statusToSet.getDescriptionValue()); 

            final int updatesCount = ps.executeUpdate();
            if (updatesCount == 0) throwTaskDoesNotExistException(id, owner);
            if (updatesCount > 1) throwIncosistentDBException(id); 
            
            return updatesCount;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }
	
    
    /**
     * Method returns a SchedulerTask which are in state TaskStatus.active or
     * TaskStatus.hold and a given user.
     * If there's a JobDefinition not more available (in case the JobDefinition has
     * been removed meanwhile) to a given task, we will throw a TaskDoesNotExistException.
     * 
     * @param SchedulerTaskID the SchedulerTaskID 
     * @param String the owner string (scheduling- or runAs-user). If the ownerId 
     *               is null we handle it as the Administrator-user
     * @param Connection the incoming SQLConnection
     *  
     * @return SchedulerTask the SchedulerTask 
     * 
     * @exception SQLException if any DB-error occurrs
     * @exception TaskDoesNotExistException if the JobDefinition does not more exist
     */
    protected SchedulerTask readTask(SchedulerTaskID id, String ownerId, Connection c) throws SQLException, TaskDoesNotExistException {
        PreparedStatement ps = null;
        ResultSet readResult = null;
        
        try {
            StringBuilder stmt = new StringBuilder();
            stmt.append("select JOB_DEFINITION_ID, RETENTION_PERIOD, NAME, DESCRIPTION, CUSTOM_DATA, ");
            stmt.append("TASK_SOURCE, STATUS, STATUS_DESC, SCHEDULING_USER, RUN_AS_USER ");
            stmt.append("from BC_JOB_TASKS where ");
            
            TaskFilter filter = new TaskFilter();
            filter.setTaskID(id);
            filter.setTaskOwnerUser(ownerId);
            filter.setTaskStatus(null); // means all active or hold tasks
    
            ps = createStatement(stmt.toString(), c, filter, 0);
    		readResult = ps.executeQuery();
    		if (!readResult.next()) throwTaskDoesNotExistException(id, ownerId);
    		final JobDefinitionID jobDefinitionId = JobDefinitionID.parseID(readResult.getBytes("JOB_DEFINITION_ID"));
    		final int retentionPeriod = readResult.getInt("RETENTION_PERIOD");
            final String name = readResult.getString("NAME"); // name cannot be null there, because we stored in this case the jobDefinitionName
            final String description = readResult.getString("DESCRIPTION");
            final String customData = readResult.getString("CUSTOM_DATA");
            final short taskSource = readResult.getShort("TASK_SOURCE");
            final TaskStatus status = TaskStatusInternal.getTaskStatus(readResult.getShort("STATUS"), readResult.getShort("STATUS_DESC"));
            final String schedulingUser = readResult.getString("SCHEDULING_USER");
            final String runAsUser = readResult.getString("RUN_AS_USER");
            
    		if (readResult.next()) throwIncosistentDBException(id);
    		readResult.close();
    		Filter[] filters = m_fp.read(id, c);
    		final JobDefinition jobDefinition = m_service.theRuntime().getJobDefinitionById(jobDefinitionId);
    		if (jobDefinition == null) throw new TaskDoesNotExistException("Job definition with id "
    				+ jobDefinitionId + " was not found. Null was returned by the Job Execution Runtime" +
    				" when this defintion was requested. Probably this job definition has been removed" +
    				" since the last execution of this task.");
    		JobParameter[] parameters = m_pp.read(id, jobDefinition.getParameters(), c);
    		CronEntry[] crons = m_cp.read(id, c);
    		RecurringEntry[] recurrings = m_rp.read(id, c);
            
            SchedulerTask task = new SchedulerTaskExtension(id, jobDefinitionId, parameters, recurrings, crons, filters, retentionPeriod, name, description, customData, taskSource, status, schedulingUser, runAsUser);
            
    		return task; 
        } finally {
            if (readResult != null) {
                readResult.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
	}
	    
    
    /**
     * Method returns Tasks for a given status and/or taskSource(independent from 
     * any user) (@see SchedulerTask, TaskStatus). 
     * 
     * Note: If taskSourceParam is null, it will be ignored by the where clause. 
     *       If status is null both states (TaskStatus.active and TaskStatus.active)
     *       will be considered in the where-clause.
     * 
     * If there's a JobDefinition not more available (in case the JobDefinition has
     * been removed meanwhile) to a given task, we will log an error an continue with 
     * execution.
     * 
     * @param Connection the incoming SQLConnection 
     * @param Short taskSourceParam in SchedulerTask
     * @param TaskStatus the statue 
     * @return List<SchedulerTask> the list of the tasks 
     * @exception SQLException if any DB-error occurrs
     */
    protected List<SchedulerTask> getAllTasks(Connection c, Short taskSourceParam, TaskStatus status) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<SchedulerTask> allTasks = null;
        
        try {
            StringBuilder stmtStr = new StringBuilder();
            stmtStr.append("select TASK_ID, JOB_DEFINITION_ID, RETENTION_PERIOD, NAME, DESCRIPTION, CUSTOM_DATA, ");
            stmtStr.append("STATUS, TASK_SOURCE, STATUS_DESC, SCHEDULING_USER, RUN_AS_USER ");
            stmtStr.append("from BC_JOB_TASKS where ");
            
            TaskFilter filter = new TaskFilter();
            filter.setTaskSource(taskSourceParam);
            filter.setTaskStatus(status);
            
            ps = createStatement(stmtStr.toString(), c, filter, 0);
            rs = ps.executeQuery();
            allTasks = new ArrayList<SchedulerTask>(100);
            
            while (rs.next()) {
                SchedulerTaskID taskId = SchedulerTaskID.parseID(rs.getBytes("TASK_ID"));
                JobDefinitionID jobDefinitionId = JobDefinitionID.parseID(rs.getBytes("JOB_DEFINITION_ID"));
                int retentionPeriod = rs.getInt("RETENTION_PERIOD");
                String name = rs.getString("NAME"); // name cannot be null there, because we stored in this case the jobDefinitionName
                String description = rs.getString("DESCRIPTION");
                String customData = rs.getString("CUSTOM_DATA");
                TaskStatus taskStatus = TaskStatusInternal.getTaskStatus(rs.getShort("STATUS"), rs.getShort("STATUS_DESC"));
                short taskSource = rs.getShort("TASK_SOURCE");
                final String schedulingUser = rs.getString("SCHEDULING_USER");
                final String runAsUser = rs.getString("RUN_AS_USER");
    
                JobDefinition jobDefinition = m_service.theRuntime().getJobDefinitionById(jobDefinitionId);
                
                if (jobDefinition == null) {
                    // log an error but continue with execution
                    
                    String errMsg = "Job definition with id '" + jobDefinitionId + "' was not found for TaskID '"+
                    taskId+"' . Null was returned by the Job Execution Runtime when this defintion was requested."+
                    " Probably this job definition has been removed since the last execution of this task.";
                    
                    Category.SYS_SERVER.logT(Severity.ERROR, location, errMsg);
                } else {     
                    Filter[] filters = m_fp.read(taskId, c);
                    JobParameter[] parameters = m_pp.read(taskId, jobDefinition.getParameters(), c);
                    CronEntry[] crons = m_cp.read(taskId, c);
                    RecurringEntry[] recurrings = m_rp.read(taskId, c);
                    
                    SchedulerTask task = new SchedulerTaskExtension(taskId, jobDefinitionId, parameters, recurrings, crons, filters, retentionPeriod, name, description, customData, taskSource, taskStatus, schedulingUser, runAsUser);

                    allTasks.add(task);
                }
            } 
            return allTasks; 
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
     * Cleans the BC_JOB_TASKS table from stale entries
     * 
     * @param Connection
     *            the incoming SQLConnection
     * @param Short
     *            taskSourceParam in SchedulerTask
     * @param TaskStatus
     *            the statue
     * @return List<SchedulerTask> the list of the tasks
     * @exception SQLException
     *                if any DB-error occurrs
     */
    protected void removeStaleTasks(Connection c) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            StringBuilder stmtStr = new StringBuilder();
            stmtStr.append("select TASK_ID, JOB_DEFINITION_ID, NAME, ");
            stmtStr.append("STATUS, TASK_SOURCE, STATUS_DESC ");
            stmtStr.append("from BC_JOB_TASKS");

            ps = c.prepareStatement(stmtStr.toString());           
            rs = ps.executeQuery();

            while (rs.next()) {
                SchedulerTaskID taskId = SchedulerTaskID.parseID(rs
                        .getBytes("TASK_ID"));
                JobDefinitionID jobDefinitionId = JobDefinitionID.parseID(rs
                        .getBytes("JOB_DEFINITION_ID"));
                String name = rs.getString("NAME"); // name cannot be null
                                                    // there, because we stored
                                                    // in this case the
                                                    // jobDefinitionName
                TaskStatus taskStatus = TaskStatusInternal.getTaskStatus(rs
                        .getShort("STATUS"), rs.getShort("STATUS_DESC"));
                short taskSource = rs.getShort("TASK_SOURCE");

                if (taskSource == SchedulerTask.TASK_SOURCE_ZERO_ADMIN
                        || taskStatus.isFinished()) {
                    // not interested in finished tasks, zero admin is handled
                    // elsewhere
                    continue;
                }

                JobDefinition jobDefinition = m_service.theRuntime()
                        .getJobDefinitionById(jobDefinitionId);

                if (jobDefinition == null
                        || jobDefinition.getRemoveDate() != null) {
                    // job has been undeployed, need to get rid of it here as
                    // well
                    //
                    try {
                        // cannot invoke cancelTask() because the priority queue
                        // does not exist yet.
                        markCancelled(taskId, null, c, TaskStatus.finished);
                        category
                                .logT(
                                        Severity.WARNING,
                                        location,
                                        "Task '"
                                                + taskId
                                                + "' has been removed because the corresponding application has been undeployed.");
                    } catch (TaskDoesNotExistException tde) {
                        // wrong movie? We just got it from there.
                        location
                                .traceThrowableT(
                                        Severity.ERROR,
                                        "Unable to cancel task '"
                                                + taskId
                                                + "' although it did exist shortly before.",
                                        tde);
                    }
                }
            }
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
     * Method returns all Tasks which are in state TaskStatus.active or
     * TaskStatus.hold (independent from any user).
     * If there's a JobDefinition not more available (in case the JobDefinition has
     * been removed meanwhile) to a given task, we will log an error an continue with 
     * execution.
     * 
     * @param Connection the incoming SQLConnection 
     * @return List<SchedulerTask> the list of the tasks 
     * @exception SQLException if any DB-error occurrs
     */
    protected List<SchedulerTask> getAllTasks(Connection c) throws SQLException {
        return getAllTasks(c, null, null);
    }
    
    /**
     * Method returns all SchedulerTaskIDs which are in state TaskStatus.active or
     * TaskStatus.hold and a given owner. If the owner is null all SchedulerTaskIDs 
     * will be returned, because an Administartor is executing that method.
     * 
     * @param String the owner string (scheduling- or runAs-user). If the ownerId 
     *               is null we handle it as the Administrator-user
     * @param Connection the incoming SQLConnection
     *  
     * @return List list with SchedulerTaskIDs 
     * 
     * @exception SQLException if any DB-error occurrs
     */
    protected List<SchedulerTaskID> getAllTaskIds(String owner, Connection c) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String stmt = "select TASK_ID from BC_JOB_TASKS where ";
            TaskFilter filter = new TaskFilter();
            filter.setTaskOwnerUser(owner);
            filter.setTaskStatus(null); // means all active or hold task
            ps = createStatement(stmt, c, filter, 0);
            
    		rs = ps.executeQuery();
    		ArrayList<SchedulerTaskID> allTaskIds = new ArrayList<SchedulerTaskID>();
    		while (rs.next()) {
    			allTaskIds.add(SchedulerTaskID.parseID(rs.getBytes(1)));
    		}
    		return allTaskIds;
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
     * Sets filters for a given task.
     * 
     * @param taskId the SchedulerTaskID
     * @param filters the Filter[]
     * @param owner the owner of the task. If null, the select will be performed without owner (Administrator-mode)
     * @param c the SQLConnection
     * 
     * @throws TaskDoesNotExistException if the task with SchedulerTaskID taskId does not exist
     * @throws SQLException if any DB-error occurrs
     */
	protected void setFilters(SchedulerTaskID taskId, Filter[] filters, String owner, Connection c) throws TaskDoesNotExistException, SQLException {
		if (!taskExist(taskId, owner, c)) throwTaskDoesNotExistException(taskId, owner);
		m_fp.persist(taskId, filters, c);
	}
	
    
    /**
     * Returns the TaskSecurity data if the task with the taskId is in TaskStatus
     * TaskStatus.active or TaskStatus.hold.
     * 
     * @param taskId the SchedulerTaskID
     * @param c the SQLConnection
     * @return the TaskSecuritsData
     * @throws SQLException if any error occurs
     * @throws TaskDoesNotExistException if the task with the given taskId does not exist
     */
    protected TaskSecurityData getTaskSecurityData(SchedulerTaskID taskId, Connection c) throws SQLException, TaskDoesNotExistException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String stmt = "select SCHEDULING_USER, RUN_AS_USER from BC_JOB_TASKS where ";
            TaskFilter filter = new TaskFilter();
            filter.setTaskID(taskId);
            filter.setTaskStatus(null); // means all active or hold task

            ps = createStatement(stmt, c, filter, 0);
    		rs = ps.executeQuery();
    		if (!rs.next()) throwTaskDoesNotExistException(taskId, null);
    		final String owner = rs.getString(1);
    		final String runAsUser = rs.getString(2);
    		if (rs.next()) throwIncosistentDBException(taskId);
    		
    		return new TaskSecurityData(owner, runAsUser);
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
     * Checks if for the given user the task which is in state TaskStatus.active or 
     * TaskStatus.hold with SchedulerTaskID taskId, exists. If the
     * owner is null the select will be performed without user. (Administrator-mode)
     * 
     * @param taskId the given SchedulerTaskID
     * @param owner the owner. If null, the select will be performed without owner (Administrator-mode)
     * @param c the SQLConnection
     * 
     * @return true if the task exists, false otherwise
     * 
     * @throws SQLException if any db error occurs
     */
	private boolean taskExist(SchedulerTaskID taskId, String owner, Connection c) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String stmt = "select count(*) from BC_JOB_TASKS where ";
            TaskFilter filter = new TaskFilter();
            filter.setTaskID(taskId);
            filter.setTaskOwnerUser(owner);
            filter.setTaskStatus(null); // means all active or hold tasks
            
            ps = createStatement(stmt, c, filter, 0);
    
    		rs = ps.executeQuery();
    		rs.next();
    		long tasksCount = rs.getLong(1);
    		if (tasksCount > 1) 
                throwIncosistentDBException(taskId);
    		if (rs.next()) 
                throw new SchedulerRuntimeException("A single aggregate function was invoked, however more than one rows were returned by the SQL query");
            
    		return tasksCount != 0;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
	}
    
    
    private PreparedStatement createStatement(String stmtPrefix, Connection c, TaskFilter filter, int countOfModifiedColumns) throws SQLException {
        StringBuilder query = new StringBuilder(stmtPrefix);
        
        boolean addAnd = false;
        ArrayList values = new ArrayList();
        
        // SchedulerTaskID
        if (filter.getTaskID() != null) {
            query.append("TASK_ID = ? ");
            values.add(filter.getTaskID());
            addAnd = true;
        }
        
        // TaskStatus
        // if TaskStatus is set to null, set TaskStatus.active and TaskStatus.hold
        if (addAnd) {
            query.append(" and ");
        }
        query.append("(STATUS = ? or STATUS = ?) "); 
        if (filter.getTaskStatus() == null) {   
            values.add(TaskStatus.active);
            values.add(TaskStatus.hold);            
        } else {
            values.add(filter.getTaskStatus());
            values.add(filter.getTaskStatus());
        }
        addAnd = true;

        // TaskSource
        if (filter.getTaskSource() != null) {
            if (addAnd) {
                query.append(" and ");
            }
            query.append("TASK_SOURCE = ? ");
            values.add(filter.getTaskSource());
            addAnd = true;
        }
        
        // Owner
        // if owner is set, set it also as scheduling user
        if (filter.getTaskOwnerUser() != null) {
            if (addAnd) {
                query.append(" and ");
            }
            query.append("(RUN_AS_USER = ? or SCHEDULING_USER = ?) ");
            values.add(filter.getTaskOwnerUser());
            addAnd = true;
        }       
        
        if (location.beDebug()) {
            location.debugT("About to execute query: " + query.toString());
        }  
        
        // Create prepared statement and add parameters to query
        
        PreparedStatement ps = c.prepareStatement(query.toString());
        
        int size = values.size();
        for (int i=1; i <= size; i++) {            
            Object value = values.remove(0);
            if (value instanceof SchedulerTaskID) {
                ps.setBytes(i+countOfModifiedColumns, ((SchedulerTaskID)value).getBytes());
            } 
            else if (value instanceof TaskStatus) {
                 ps.setShort(i+countOfModifiedColumns, ((TaskStatus)value).getValue());
            } 
            else if (value instanceof Short) {
                Short source = (Short)value;
                ps.setShort(i+countOfModifiedColumns, source.shortValue());
            } 
            else if (value instanceof String) {
                ps.setString(i+countOfModifiedColumns, (String)value);
                ps.setString(i+countOfModifiedColumns+1, (String)value);
            } 
            else {
                throw new IllegalArgumentException("Illegal type for task filter: " + value.getClass().getName());
            }
        }        
        
        return ps;
    }


    
    private void throwTaskDoesNotExistException(SchedulerTaskID id, String owner) throws TaskDoesNotExistException {
        String errMsg = null;
        if (owner == null) {
            errMsg = "Task with id '" + id + "' does not exist";
        } else {
            errMsg = "Task with id '" + id + "' was not found among the tasks of user '"+owner+"'.";
        }
        throw new TaskDoesNotExistException(errMsg);
    }
    
	private void throwIncosistentDBException(SchedulerTaskID id) {
		throw new SchedulerRuntimeException("More than 1 task with id " + id +" were found in the database." +
				" Probably this means that the database is not in cosistent state." +
				" Please analyse the database and fix the problem.");
	}	

}
