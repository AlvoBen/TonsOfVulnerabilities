/*
 * Created on 08.01.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.scheduler.impl.parameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.sap.engine.services.scheduler.impl.TaskPersistor;
import com.sap.engine.services.scheduler.util.DB;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobParameterDefinition;
import com.sap.scheduler.runtime.SchedulerRuntimeException;

/*
 * Also persists the type of the param, although this is not necessary for reading back the parameter.
 */
public class ParameterPersistor {

    /**
     * @param taskId
     * @param jobParameters
     * @param c
     */
    public void persist(SchedulerTaskID taskId, JobParameter[] jobParameters, Connection c) throws SQLException  {
        PreparedStatement ps = null;
        try {
            if (jobParameters.length == 0) 
                return;
            String stmt = "insert into BC_JOB_TASK_PARAM (TASK_ID, ARG_NAME, ARG_TYPE, ARG_VALUE, ARG_LONG_VALUE) values(?, ?, ?, ?, ?)";
            ps = c.prepareStatement(stmt);
            for (int i = 0; i < jobParameters.length; i++) {
                ps.setBytes(1, taskId.getBytes());
                ps.setString(2, jobParameters[i].getName());
                ps.setString(3, jobParameters[i].getJobParameterDefinition().getType().toString());
                String pValue = jobParameters[i].toString();
                if (pValue == null || (pValue != null && pValue.length() > 200) ){
                    ps.setString(5, pValue);
                    ps.setString(4, null);
                } else {
                    ps.setString(4, pValue);
                    ps.setString(5, null);
                }
                ps.addBatch();
            }
            int insertCount = DB.countSingleRowBatchInserts(ps.executeBatch()); 
            if (insertCount != jobParameters.length)
                throw DB.createAndLogBadInsertCountExcetpion(jobParameters.length, insertCount, TaskPersistor.location);
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
    public JobParameter[] read(SchedulerTaskID id, JobParameterDefinition[] paramDefs, Connection c) throws SQLException {
        PreparedStatement ps = null;
        ResultSet currentRow = null;
        try {
            ps = c.prepareStatement("select ARG_NAME, ARG_VALUE, ARG_LONG_VALUE from BC_JOB_TASK_PARAM where TASK_ID = ?");
            ps.setBytes(1, id.getBytes());
            currentRow = ps.executeQuery();
            final ArrayList<JobParameter> params = new ArrayList<JobParameter>();
            while (currentRow.next()) {
                final String name = currentRow.getString(1);
                final String shortValue = currentRow.getString(2);
                final String longValue = currentRow.getString(3);
                final JobParameterDefinition paramDef = extractNecessaryJobDefinition(name, paramDefs);
                if (paramDef == null) throw new SchedulerRuntimeException("Parameter with name " + name + "was not found in job" +
                        " definition for task " + id + ". This is a serious error as the parameters" +
                        " have been verified against the job definition when the task has been scheduled");
                final JobParameter param = new JobParameter(paramDef, longValue == null ? shortValue : longValue );
                params.add(param);
            }
            return (JobParameter[])params.toArray(new JobParameter[params.size()]);
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
     * Removes Parameters for a given SchedulerTaskID[].
     * 
     * @param taskIds the SchedulerTaskID[] which contains the taskIds
     * @param c the Connection
     * 
     * @throws SQLException if any error occurs
     */
    public void remove(SchedulerTaskID[] taskIds, Connection c) throws SQLException {        
        PreparedStatement ps = null;
        
        try {
            ps = c.prepareStatement("DELETE FROM BC_JOB_TASK_PARAM where TASK_ID = ?");    
            
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
    
    
    private  JobParameterDefinition extractNecessaryJobDefinition(String argName, JobParameterDefinition[] paramDefs) {
        for (int i = 0; i < paramDefs.length; i++) {
            if (paramDefs[i].getName().equals(argName)) 
                return paramDefs[i];
        }
        return null;
    }
}
