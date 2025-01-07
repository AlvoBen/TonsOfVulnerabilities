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
package com.sap.engine.services.scheduleradapter.repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.scheduler.runtime.util.LocalizationHelper;
import com.sap.engine.services.scheduleradapter.jobdeploy.ConfigurationParserException;
import com.sap.engine.services.scheduleradapter.jobdeploy.UpdateJobDefinition;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionName;
import com.sap.scheduler.runtime.JobParameterDefinition;
import com.sap.scheduler.runtime.MDBJobDefinition;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * @author Dirk Marwinski
 */
public class JobRepository {

    private final static Location location = Location.getLocation(JobRepository.class);
    private final static Category category = LoggingHelper.SYS_SERVER;

	private Environment mEnvironment;
	
	private HashMap mDeployedJobs = new HashMap();
	
	public JobRepository(Environment env) {
		mEnvironment = env;
	}
	
    /**
     * Updates the repository. It returns those entries which have been updated
     * or added.
     * 
     * @param jobs
     * @return
     */
	public synchronized ArrayList<UpdateJobDefinition> updateRepository(String applicationName,
	                                                                    HashMap<JobDefinitionName,JobDefinition> jobs) 
                                                                 throws ConfigurationParserException,
                                                                        DuplicateJobDefinitionException,
                                                                        SQLException {
		
        // ArrayList containing the jobs to add, change, remove or leave 
	    // untouched. 
	    //
        ArrayList<UpdateJobDefinition> changeJobs = new ArrayList<UpdateJobDefinition>();
        
        // Get all existing job definitions which were deployed before in this
        // application
        //
        ArrayList<JobDefinition> oldJobDefs = mEnvironment.getJobDefinitionHandler().getJobDefinitionsByApplication(applicationName);
        // store it into a map
        HashMap<String, JobDefinition> mapOldJobDefs = new HashMap<String, JobDefinition>();
        for (JobDefinition tmp : oldJobDefs) {
            mapOldJobDefs.put(tmp.getJobDefinitionName().getName(), tmp);               
        }

        for (JobDefinitionName newJobName : jobs.keySet()) {
			
            JobDefinition newJob = jobs.get(newJobName);
            
            // remove from "old" map to indicate that we have covered this one
            //
            mapOldJobDefs.remove(newJob.getJobDefinitionName().getName());
            
            // name object with job name only
            JobDefinitionName tmpName = new JobDefinitionName(newJob.getJobDefinitionName().getName());
            ArrayList<JobDefinition> existingJobs = mEnvironment.getJobDefinitionHandler().getJobDefinitionsByName(tmpName);
            
            if (existingJobs.size() == 0) {
                // job did not exist, just add it
                //
                if (location.beDebug()) {
                    location.debugT("Job definition with name \"" + newJob.getJobDefinitionName() + "\" did not exist. Adding new job definition.");   
                }
                
                changeJobs.add(UpdateJobDefinition.newJobDefinition(newJob));
                continue;
            }
            
            // --------------------------------------------------------------
            // Job exists, we need to figure out whether this deployment is
            // legal
            // --------------------------------------------------------------
            
            MDBJobDefinition mjd = new MDBJobDefinition(newJob);
            
            // --------------------------------------------------------------
            // check restrictions depending on the version
            // --------------------------------------------------------------
            
            if (mjd.getVersion() == MDBJobDefinition.JobVersion.v1) {
                
                // ----------------------------------------------------------
                // There must be at most one exsiting job and it must live
                // in the same (this) application
                // ----------------------------------------------------------

                if (existingJobs.size() > 1) {
                    throw new DuplicateJobDefinitionException("Job \"" + newJob.getJobDefinitionName() + 
                            "\" cannot be deployed. There is at least another job with the same name in another application");
                }
                
                JobDefinition existing = existingJobs.get(0);
                if (!newJob.getJobDefinitionName().getApplicationName().equals(existing.getJobDefinitionName().getApplicationName())) {

                    throw new DuplicateJobDefinitionException("Job \"" + newJob.getJobDefinitionName() + 
                                  "\" cannot be deployed. There is at least another job with the same name in another application");
                }

                if (!compareTo(newJob, existing)) {
                    // There is an existing job which differs from the new 
                    // job. De-activate the existing job and add the new one
                    //
                    if (location.beDebug()) {
                        location.debugT("Job definition with name \"" + newJob.getJobDefinitionName() + "\" did exist but was outdated. Invalidating old definition and adding new one.");
                    }
                    changeJobs.add(UpdateJobDefinition.changedJobDefinition(newJob, existing));
                } else {
                    if (location.beDebug()) {
                        location.debugT("Job definition with name \"" + newJob.getJobDefinitionName() + "\" exists and is up-to-date.");
                    }
                    changeJobs.add(UpdateJobDefinition.existingUnchangedJobDefinition(newJob));
                }
                continue;
            } else {
                
                // version is v2
                
                // check wheter there is the same job definition in this 
                // application

                // check whether another v1 job exists in a different application
                // in this case it is a deploy failure. An existing v1-job in this 
                // application is not a failure
                for (int i = 0; i < existingJobs.size(); i++) {
                    MDBJobDefinition existingMDB = new MDBJobDefinition(existingJobs.get(i));
                    if (existingMDB.getVersion() == MDBJobDefinition.JobVersion.v1) {
                        if ( !mjd.getApplicationName().equals(existingMDB.getApplicationName()) ) {
                            throw new DuplicateJobDefinitionException("Job \"" + newJob.getJobDefinitionName() + 
                            "\" cannot be deployed. There is at least another job with the same name in another application");
                        }
                    } 
                }                
                
                JobDefinition existing = mEnvironment.getJobDefinitionHandler().getJobDefinitionByName(newJob.getJobDefinitionName());
                
                if (existing == null) {
                    changeJobs.add(UpdateJobDefinition.newJobDefinition(newJob));
                    continue;
                }
                
                if (!compareTo(newJob, existing)) {
                    // There is an existing job which differs from the new 
                    // job. De-activate the existing job and add the new one
                    //
                    if (location.beDebug()) {
                        location.debugT("Job definition with name \"" + newJob.getJobDefinitionName() + "\" did exist but was outdated. Invalidating old definition and adding new one.");
                    }
                    changeJobs.add(UpdateJobDefinition.changedJobDefinition(newJob, existing));
                } else {
                    if (location.beDebug()) {
                        location.debugT("Job definition with name \"" + newJob.getJobDefinitionName() + "\" exists and is up-to-date.");
                    }
                    changeJobs.add(UpdateJobDefinition.existingUnchangedJobDefinition(newJob));
                }
                continue;
            }
		}
        
        // make sure we correctly remove those jobs which did exist in the old
        // but not in the new archive
        //
        Collection<JobDefinition> coll = mapOldJobDefs.values();
        for (Iterator<JobDefinition> iter = coll.iterator(); iter.hasNext();) {
            JobDefinition jobDef = iter.next();
            changeJobs.add(UpdateJobDefinition.removedJobDefinition(jobDef));
        }
        
        // For the localization files we do not need a special comparing routine, 
        // because while we are running in a re-deployment we delete always first 
        // old rows
        
        if (changeJobs.size() != 0)
        	mEnvironment.getJobDefinitionHandler().updateJobDefinitionsForApplication(changeJobs);
        
        if (location.beDebug()) {
            location.debugT("Job repository update updated " + changeJobs.size() + " jobs.");
        }
        
        return changeJobs;
	}
    
    private boolean compareTo(JobDefinition newDef, JobDefinition existingDef) {

        // check job definition
        //
        if (newDef.getDescription() == null) {
            if (existingDef.getDescription() != null) {
                return false;
            }
        } else if (!newDef.getDescription().equals(existingDef.getDescription())) {
            return false;
        }
        if (newDef.getRetentionPeriod() != existingDef.getRetentionPeriod()) {
            return false;
        }
        if (newDef.getJobType() != existingDef.getJobType()) {
            return false;
        }
        
        if (newDef.getApplication() == null) {
            if (existingDef.getApplication() != null) {
                return false;
            }
        } else if (!newDef.getApplication().equals(existingDef.getApplication())) {
            return false;
        }
        
        // compare localization properties of JobDefinition
        HashMap<String, HashMap<String, String>> localeMap = newDef.getLocalizationInfoMap();        
        if ( !LocalizationHelper.compareTo(localeMap, existingDef.getLocalizationInfoMap()) ) {
            return false;
        } 
        
        // check properties 
        //
        HashMap existingProps = getProperties(existingDef.getProperties());
        String[][] newProps = newDef.getProperties();
        for (int i=0; i < newProps.length; i++) {
            String name = newProps[i][0];
            String value = newProps[i][1];
            
            String existingValue = (String)existingProps.remove(name);
            
            if (value == null) {
                if (existingValue != null) {
                    return false;
                }
            } else {
                if (!value.equals(existingValue)) {
                    return false;
                }
            }
        }
        
        // check parameters
        //
        HashMap existingParameters = getParameters(existingDef.getParameters());
        
        JobParameterDefinition[] params = newDef.getParameters();
        for (int i=0; i < params.length; i++) {
            JobParameterDefinition p = params[i];
            JobParameterDefinition ep = (JobParameterDefinition)existingParameters.remove(p.getName());
            
            if (ep == null) {
                // parameter added, new job definition not equal
                return false;
            }
            if (!p.getDirection().equals(ep.getDirection())) {
                // direction has been changed
                return false;
            }
            if (p.isNullable() != ep.isNullable()) {
                // nullable value changed
                return false;
            }
            if (!p.getType().equals(ep.getType())) {
                // type changed
                return false;
            }

            if (p.getDefaultData() == null) {
                if (ep.getDefaultData() != null) {
                    // default set on existing parameter, but not on new one
                    return false;
                }
            } else if (!p.getDefaultData().equals(ep.getDefaultData())) {
                return false;
            }

            if (p.getGroup() == null) {
                if (ep.getGroup() != null) {
                    return false;
                }
            } else if (!p.getGroup().equals(ep.getGroup())) {
                return false;
            }
            
            if (p.getDescription() == null) {
                if (ep.getDescription() != null) {
                    return false;
                }
            } else if (!p.getDescription().equals(ep.getDescription())) {
                return false;
            }
            
            if (p.isDisplay() != ep.isDisplay()) {
                return false;
            }
            
            // compare localization properties of JobParameterDefinition
            HashMap<String, HashMap<String, String>> localeMapParam = p.getLocalizationInfoMap();          
            if ( !LocalizationHelper.compareTo(localeMapParam, ep.getLocalizationInfoMap()) ) {
                return false;
            }
        }
        return true;
        

    }
    
    
    private HashMap getParameters(JobParameterDefinition[] defs) {
        
        HashMap params = new HashMap();
        for (int i=0; i < defs.length; i++) {
            params.put(defs[i].getName(), defs[i]);
        }
        return params;
    }

    private HashMap getProperties(String[][] props) {
        
        HashMap params = new HashMap();
        for (int i=0; i < props.length; i++) {
            params.put(props[i][0], props[i][1]);
        }
        return params;
    }

    public ArrayList<JobDefinition> deactivateJobDefinitions(String applicationName)
                                                                   throws SQLException {

        return mEnvironment.getJobDefinitionHandler().deactivateJobDefinitions(applicationName);
        
        // Note: 
        // The localization info will be removed from while with removing the
        // JobDefinitions while running the CleanJob from NWScheduler
        
    }
    
	
	/**
	 * Returns a job definition of a deployed job
	 * 
	 * @param jobName name of the job
	 * @return the job definition or null if there is no such job
	 */
	public JobDefinition getJobDefinition(String jobName) {
		return (JobDefinition)mDeployedJobs.get(jobName);
	}
	
	/**
	 * Retrieves job definitions currently in the repository.
	 * @return array ofjob definitions currently 
	 */
	public JobDefinition[] getAllJobDefinitions() {

		return (JobDefinition[])mDeployedJobs.values().toArray(new JobDefinition[mDeployedJobs.size()]);
	}
}
