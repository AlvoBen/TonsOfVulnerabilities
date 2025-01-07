/*
 * Copyright (c) 2005-2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduleradapter.jobdeploy;

import com.sap.engine.services.scheduler.runtime.db.JobDefinitionHandler;
import com.sap.scheduler.runtime.JobDefinition;

/**
 * This class helps to represent different scenarios and states during
 * update deployment.
 * 
 * @author Dirk Marwinski
 *
 */
public class UpdateJobDefinition {

    public static enum UpdateType {
        NEW_JOB_DEFINITION,
        UNCHANGED_JOB_DEFINITION,
        CHANGED_JOB_DEFINITION,
        REMOVED_JOB_DEFINITION
    }
    
    public static UpdateJobDefinition newJobDefinition(JobDefinition newDef) {
        return new UpdateJobDefinition(newDef, UpdateType.NEW_JOB_DEFINITION);
    }
    
    public static UpdateJobDefinition existingUnchangedJobDefinition(JobDefinition existing) {
        return new UpdateJobDefinition(existing, UpdateType.UNCHANGED_JOB_DEFINITION);
    }
    
    public static UpdateJobDefinition changedJobDefinition(JobDefinition newDef, JobDefinition oldDef) {
        return new UpdateJobDefinition(newDef, oldDef, UpdateType.CHANGED_JOB_DEFINITION);
    }
    
    public static UpdateJobDefinition removedJobDefinition(JobDefinition removed) {
        return new UpdateJobDefinition(removed, UpdateType.REMOVED_JOB_DEFINITION);
    }
    
    private UpdateType mUpdateType;
    private JobDefinition mNewJobDefinition;
    private JobDefinition mOldJodDefinition = null;
    
    private UpdateJobDefinition(JobDefinition newDef, UpdateType t) {
        mUpdateType = t;
        mNewJobDefinition = newDef;
    }
    
    private UpdateJobDefinition(JobDefinition newDef, JobDefinition oldDef, UpdateType t) {
        mUpdateType = t;
        mNewJobDefinition = newDef;
        mOldJodDefinition = oldDef;
    }
    
    public JobDefinition getJobDefinition() {
        return mNewJobDefinition;
    }
    
    public JobDefinition getOldJobDefinition() {
        return mOldJodDefinition;
    }
    
    public JobDefinition getRemovedJobDefinition() {
        if (mUpdateType != UpdateType.REMOVED_JOB_DEFINITION) {
            throw new IllegalArgumentException("No removed job definition in this update container.");
        }
        return mNewJobDefinition;
    }
    
    public UpdateType getUpdateType() {
        return mUpdateType;
    }
    
}

