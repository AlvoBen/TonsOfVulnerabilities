/*
 * Created on 07.12.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.scheduler.impl;

import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.TaskStatus;

public interface TaskExecutor  {
    
    public TaskStatus executeTask(SchedulerTask task, long currentTime) throws Exception;

    public void taskStateChanged(SchedulerTask task, TaskStatus newState) throws Exception;
}
