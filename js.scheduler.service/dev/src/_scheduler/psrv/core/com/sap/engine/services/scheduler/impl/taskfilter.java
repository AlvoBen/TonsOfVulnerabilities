package com.sap.engine.services.scheduler.impl;

import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.TaskStatus;

public class TaskFilter {
    
    private SchedulerTaskID m_taskID = null;
    private TaskStatus m_taskStatus = null;
    private Short m_taskSource = null;
    private String m_taskOwnerUser = null;
    
    
    /**
     * @return Returns the m_taskID.
     */
    public SchedulerTaskID getTaskID() {
        return m_taskID;
    }
    /**
     * @param taskid The m_taskID to set.
     */
    public void setTaskID(SchedulerTaskID taskid) {
        m_taskID = taskid;
    }
    /**
     * @return Returns the m_taskOwnerUser.
     */
    public String getTaskOwnerUser() {
        return m_taskOwnerUser;
    }
    /**
     * @param owner The taskOwnerUser to set.
     */
    public void setTaskOwnerUser(String taskOwner) {
        m_taskOwnerUser = taskOwner;
    }
    /**
     * @return Returns the m_taskSource.
     */
    public Short getTaskSource() {
        return m_taskSource;
    }
    /**
     * @param source The taskSource to set.
     */
    public void setTaskSource(Short source) {
        m_taskSource = source;
    }
    /**
     * @return Returns the m_taskStatus.
     */
    public TaskStatus getTaskStatus() {
        return m_taskStatus;
    }
    /**
     * @param status The taskStatus to set.
     */
    public void setTaskStatus(TaskStatus status) {
        m_taskStatus = status;
    }
    
    

}
