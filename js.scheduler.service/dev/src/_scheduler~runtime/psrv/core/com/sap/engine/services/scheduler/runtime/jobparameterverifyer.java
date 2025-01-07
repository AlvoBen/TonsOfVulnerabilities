/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.runtime;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobExecutorException;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobParameterDefinition;
import com.sap.scheduler.runtime.MDBJobDefinition;
import com.sap.scheduler.runtime.NoSuchJobDefinitionException;
import com.sap.scheduler.runtime.ParameterValidationException;
import com.sap.tc.logging.Location;
import com.sap.engine.services.scheduler.runtime.mdb.MDBJobExecutor;

public class JobParameterVerifyer {

    private final static Location location = Location.getLocation(JobParameterVerifyer.class);

    
    public static void verifyParameters(
                       JobDefinitionID jobDefinitionId, 
                       JobParameter[] jobParameters,
                       Environment env)
                                    throws ParameterValidationException, 
                                           NoSuchJobDefinitionException,
                                           SQLException {
        
        //----------------------------------------------------
        // Get job definition from database and check that 
        // job definition and job implementation exist
        //----------------------------------------------------

        JobDefinition jd = null;
        jd = env.getJobDefinitionHandler().getJobDefinitionById(jobDefinitionId);
        if (jd==null) {
            throw new NoSuchJobDefinitionException("There is no job definition with id \"" + jobDefinitionId.toString() + "\".");
        }
        if (jd.getRemoveDate() != null) {
            throw new NoSuchJobDefinitionException("The job implementation for job \"" + jd.getJobDefinitionName() + "\" has been undeployed and is no longer available.");
        }

        if (location.beDebug()) {
            location.debugT("Job definition for job \"" + jd.getJobDefinitionName() + "\" found.");
        }
        
        MDBJobDefinition mjd = new MDBJobDefinition(jd);
        
        //----------------------------------------------------
        // Get job parameter definition from database and 
        // check that all required parameters are provided. 
        // Also check that there are no additional parameters
        //----------------------------------------------------

        JobParameterDefinition[] defs = jd.getParameters();

        Map<String,JobParameterDefinition> paramDefsMap = new HashMap<String,JobParameterDefinition>();
        for (int i=0; i < defs.length; i++) {
            paramDefsMap.put(defs[i].getName(), defs[i]);
        }
        
        for (int i=0; i < jobParameters.length; i++) {
            JobParameter param = jobParameters[i];
            JobParameterDefinition paramDef = param.getJobParameterDefinition();
            if (paramDef == null) {
                throw new ParameterValidationException("Job parameter number \"" + i + 
                                                       "\" for job \"" + jd.getJobDefinitionName() + 
                                                       "\" has no associated parameter definition.");
            }
            
            JobParameterDefinition def = (JobParameterDefinition)paramDefsMap.remove(paramDef.getName());
            if (def == null) {
                throw new ParameterValidationException("Job parameter number \"" + i + 
                        "\" for job \"" + jd.getJobDefinitionName() + 
                        "\" has unknown associated type \"" + paramDef.getName() + "\".");
            }
            
            if (!paramDef.equals(def)) {
                throw new ParameterValidationException("Job parameter number \"" + i + 
                        "\" for job \"" + jd.getJobDefinitionName() + 
                        "\" with name \"" + paramDef.getName() + 
                        "\" has invalid job parameter definition. It does not " +
                        "match the required job parameter definition.");
            }
            
            if (paramDef.getDirection().equalsIgnoreCase("out")) {
                throw new ParameterValidationException("Job parameter number \"" + i +
                               "\" for job \"" + jd.getJobDefinitionName() + 
                               "\" with name \"" + paramDef.getName() + 
                               "\" is an OUT parameter and must not be specified " +
                               "when executing a job.");
            }
        }
        
        // make sure no required parameters are omitted
        //
        Iterator it = paramDefsMap.values().iterator();
        while (it.hasNext()) {
            JobParameterDefinition def = (JobParameterDefinition)it.next();
            if ((def.isIn() || def.isInOut())
                    && !def.isNullable()) {
                throw new ParameterValidationException("Required job parameter \"" +
                        def.getName() + "\" for job \"" + jd.getJobDefinitionName() + 
                        "\" not specified.");
            }
        }
        
        // parameters are ok

    }
}
