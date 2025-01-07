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
package com.sap.engine.services.scheduler.runtime.mdb;

import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.scheduler.runtime.JobExecutionRuntime;
import com.sap.scheduler.runtime.mdb.MDBJobDelegateReferenceHolder;


/**
 * @author Dirk Marwinski
 *
 */
public class MDBJobRuntimeManager {


    public MDBJobRuntimeManager(Environment env, JobExecutionRuntime jert) {
        MDBJobDelegateReferenceHolder.delegate = new MDBJobDelegateImpl(env, jert);
    }

}
