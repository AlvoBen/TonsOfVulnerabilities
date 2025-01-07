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
package com.sap.engine.services.scheduleradapter.jobdeploy;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import com.sap.scheduler.runtime.JobDefinition;

/**
 * Helper class for validating property files. Currently only localization 
 * property files are validated (see method validateLocalizationPropertyName(...))
 * 
 * @author Thomas Mueller (d040939)
 */

public class PropertyFileValidator {
    
    private static final String PROPERTY_FILE_PREFIX = "job-definition_";
    private static final String DESCRIPTION = "Description";
    private static final String PARAMETER = "Parameter";
    private static final String JOB_PREFIX = "job";
    
    
    /**
     * Method verifies the incoming key and value for a translation info. The key
     * will be verified against the format of localization property files. The key
     * format looks like:
     * 
     * job.<JobName> = SchlafJob
     * e.g.: job.SleepJob = SchlafJob
     * 
     * job.<JobName>.Description = Job der schlaeft
     * e.g.: job.SleepJob.Description = Job der schlaeft
     * 
     * job.<JobName>.Parameter.<JobParameterName> = Schlafzeit
     * e.g.: job.SleepJob.Parameter.sleepTime = Schlafzeit
     * 
     * job.<JobName>.Parameter.<JobParameterName>.Description = Zeit die der Job schlafen soll
     * e.g.: job.SleepJob.Parameter.sleepTime.Description = Zeit die der Job schlafen soll
     * 
     * The keys will also be checked against the deployed JobDefinition-names and
     * The JobParamDefinition-names deployed with this application.
     *  
     * @param propKey the key to validate
     * @param locale the Locale to which the property belong
     * @param jobDefsMap the Map with the JobDefinitions to check if the property
     *                   belongs to this deployment
     * @return String[] with String[0]=<JobDefinitionName> and 
     *                       String[1]=<JobParameterName> or null the property 
     *                                                    belongs to the JobDefinition
     * 
     * @throws PropertyFileValidationException if the property is not valid in sense
     *                                         descriptions above
     */
    protected static String[] validateLocalizationPropertyName(String propKey, Locale locale, Map<String, JobDefinition> jobDefsMap) throws PropertyFileValidationException {

        
        // names[0] = <jobName>
        // names[1] = <paramName>
        String[] names = new String[2];
        
        if (propKey.equals("")) {
            throw new PropertyFileValidationException("Property-key '"+propKey+"' defined in file '"+PROPERTY_FILE_PREFIX+locale.toString()+"' is invalid");
        }
                
        StringTokenizer st = new StringTokenizer(propKey, ".");
        ArrayList<String> list = new ArrayList<String>();
        while(st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        
        String[] keyLevels = list.toArray(new String[list.size()]);
        if (keyLevels.length < 2 || keyLevels.length > 5 ) {
            throw new PropertyFileValidationException("Property-key '"+propKey+"' defined in file '"+PROPERTY_FILE_PREFIX+locale.toString()+"' is invalid");
        } 
        
        if ( keyLevels.length >= 2 ) { // --> 2
            if ( !keyLevels[0].equalsIgnoreCase(JOB_PREFIX) ) {
                throw new PropertyFileValidationException("Property-key '"+propKey+"' defined in file '"+PROPERTY_FILE_PREFIX+locale.toString()+"' is invalid (starts not with 'job')");    
            }
            if ( !jobDefsMap.containsKey(keyLevels[1]) ) {
                throw new PropertyFileValidationException("There's no job available in this deployment which fits to the property-key '"+propKey+"' defined in file '"+PROPERTY_FILE_PREFIX+locale.toString()+"'");    
            } else {
                // store the jobName
                names[0] = keyLevels[1];
            }
        } 
        
        if ( keyLevels.length == 3 ) { // --> exact 3
            if ( !keyLevels[2].equalsIgnoreCase(DESCRIPTION) ) {
                throw new PropertyFileValidationException("Property-key '"+propKey+"' defined in file '"+PROPERTY_FILE_PREFIX+locale.toString()+"' is invalid.");  
            }
        } 
        
        if ( keyLevels.length >= 4 ) { // --> 4
            JobDefinition jobDef = jobDefsMap.get(keyLevels[1]);
            
            if ( !keyLevels[2].equalsIgnoreCase(PARAMETER) ) {
                throw new PropertyFileValidationException("Property-key '"+propKey+"' defined in file '"+PROPERTY_FILE_PREFIX+locale.toString()+"' is invalid (third position not 'Parameter').");  
            }
            if ( jobDef.getParameter(keyLevels[3]) == null ) {
                throw new PropertyFileValidationException("There's no parameter available in this deployment which fits to the property-key '"+propKey+"' defined in file '"+PROPERTY_FILE_PREFIX+locale.toString()+"'");  
            } else {
                // store the paramName
                names[1] = keyLevels[3];
            }
        }
        
        if ( keyLevels.length >= 5 ) { // --> 5
            if ( !keyLevels[4].equalsIgnoreCase(DESCRIPTION) ) {
                throw new PropertyFileValidationException("Property-key '"+propKey+"' defined in file '"+PROPERTY_FILE_PREFIX+locale.toString()+"' is invalid.");  
            }
        }        
        
        return names;
    } // validateLocalizationPropertyName     

}
