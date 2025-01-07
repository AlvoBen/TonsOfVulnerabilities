/*
 * Created on 27.12.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.scheduler.impl;

import java.util.Map;
import com.sap.scheduler.api.TaskStatus;


public class TaskStatusInternal extends TaskStatus {    
    
    protected static final TaskStatus activeInitialZeroAdmin     = new TaskStatusInternal(STATUS_ACTIVE, STATUS_ACT_INITIAL_ZEROADMIN.shortValue());
    protected static final TaskStatus activeInitialAPI           = new TaskStatusInternal(STATUS_ACTIVE, STATUS_ACT_INITIAL_API.shortValue());
    protected static final TaskStatus activeReleased             = new TaskStatusInternal(STATUS_ACTIVE, STATUS_HOLD_TO_ACT_API.shortValue());
    protected static final TaskStatus finishedFinished           = new TaskStatusInternal(STATUS_FINISHED, STATUS_ACT_TO_FIN_FINISHED.shortValue());
    protected static final TaskStatus finishedCancelledZeroAdmin = new TaskStatusInternal(STATUS_FINISHED, STATUS_ACT_TO_FIN_CANCELLED_ZEROADMIN.shortValue());
    protected static final TaskStatus finishedCancelledAPI       = new TaskStatusInternal(STATUS_FINISHED, STATUS_ACT_TO_FIN_CANCELLED_API.shortValue());
    protected static final TaskStatus finishedUndeployed         = new TaskStatusInternal(STATUS_FINISHED, STATUS_ACT_TO_FIN_UNDEPLOYED.shortValue());
    protected static final TaskStatus holdApi                    = new TaskStatusInternal(STATUS_HOLD, STATUS_ACT_TO_HOLD_API.shortValue());
    protected static final TaskStatus holdError                  = new TaskStatusInternal(STATUS_HOLD, STATUS_ACT_TO_HOLD_ERROR.shortValue());
    
    protected static final short DESCRIPTION_UNDEFINED = TaskStatus.DESCRIPTION_UNDEFINED;
    protected static final Map<Short, String> STATUS_MAP = TaskStatus.STATUS_MAP;
    protected static final Map<Short, String> STATUS_CHANGED_MAP = TaskStatus.STATUS_CHANGED_MAP;
    
    private TaskStatusInternal(short value, short descVal) {
        super(value, descVal);
    }
    
    
    protected static TaskStatus getTaskStatus(short status, short desc) {
        switch (status) {
            case STATUS_ACTIVE: {
                // for backward compatibility
                if (desc == DESCRIPTION_UNDEFINED) {
                    return active;
                } else if (desc == STATUS_ACT_INITIAL_ZEROADMIN.shortValue()) {
                    return activeInitialZeroAdmin;
                } else if (desc == STATUS_ACT_INITIAL_API.shortValue()) {
                    return activeInitialAPI;
                } else if (desc == STATUS_HOLD_TO_ACT_API.shortValue()) {
                    return activeReleased;
                } else {
                    throw new IllegalArgumentException("Description is not valid in combination with status. Status: "+status+", Desc: "+desc);
                }
            }
            case STATUS_FINISHED: {
                if (desc == DESCRIPTION_UNDEFINED) {
                    return finished;
                } else if (desc == STATUS_ACT_TO_FIN_FINISHED.shortValue()) {
                    return finishedFinished;
                } else if (desc == STATUS_ACT_TO_FIN_UNDEPLOYED.shortValue()) {
                    return finishedUndeployed;
                } else if (desc == STATUS_ACT_TO_FIN_CANCELLED_ZEROADMIN.shortValue()) {
                    return finishedCancelledZeroAdmin;
                } else if (desc == STATUS_ACT_TO_FIN_CANCELLED_API.shortValue()) {
                    return finishedCancelledAPI;
                } else {
                    throw new IllegalArgumentException("Description is not valid in combination with statue. Staus: "+status+"Desc: "+desc);
                }
            }
            case STATUS_HOLD: {
                if (desc == DESCRIPTION_UNDEFINED) {
                    return hold;
                } else if (desc == STATUS_ACT_TO_HOLD_API.shortValue()) {
                    return holdApi;
                } else if (desc == STATUS_ACT_TO_HOLD_ERROR.shortValue()) {
                    return holdError;
                } else {
                    throw new IllegalArgumentException("Description is not valid in combination with status. Status: "+status+", Desc: "+desc);
                }
            } default: {
                throw new IllegalArgumentException("Status is not valid. Status: "+status);
            }
        }

    }


}
