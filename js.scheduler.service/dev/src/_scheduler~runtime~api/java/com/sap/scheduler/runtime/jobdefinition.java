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
package com.sap.scheduler.runtime;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.sap.scheduler.runtime.util.LocalizationHelper;

/**
 * This abstract class represents the job meta information for jobs which
 * have been deployed. Information represented by instances of this class
 * are taken from the deployment descriptor and the 
 * <code>JobDefinition.xml</code> file. Instances of this class are provided
 * by the scheduler. Applications cannot change this object in the scheduler.
 * <p>
 * <b>Note</b>: Although there are a constructor with no arguments and public set methods,
 * these methods should not be used. This class is designed to be
 * XML serializable/deserializable. Instances of these class are asumed to be 
 * immutable and once a field is initialized it cannot be changed. An attempt 
 * to invoke the setter for a given attribute would result in an
 * <code>IllegalStateException</code>. 
 * 
 * @author Dirk Marwinski
 * @author Hristo Sabev
 */
public final class JobDefinition implements Serializable { 

    private static final long serialVersionUID = 4199080844227259137L;

    private JobDefinitionID          id;
    private JobDefinitionName        name;
    private String                   description;
    private JobParameterDefinition[] parameters;
    private int                      retentionPeriod;
    private int                      jobDefinitionType;
    private Date                     removeDate;
    private String[][]               properties = new String[0][0];

    /** 
     * The Map for the localization information. If no localization is 
     * available the localized text might be null.
     */
    private HashMap<String, HashMap<String, String>> m_localizedTextMap = null;

    private static final String notSetMessage = "A value for {0} has not been set yet.";
    private static final String setMessage = "A value for {0} is already set for this instance. This object is immutable." +
            " Once a value is set it cannot be changed";

    /**
     * This constant represents the value for an infinite retention period.
     */
    public static final int INFINITE_RETENTION_PERIOD = -1;
    

    /**
     * This constant represents the default retention period if no value is 
     * specified. It means the jobs are kept persistent for 4 weekends and the 
     * 3 weeks in between (23 days).
     */
    public static final int DEFAULT_RETENTION_PERIOD = 23;
    
    
    /**
     * @deprecated Please use {@link #JobDefinition(JobDefinitionID, JobDefinitionName, String, JobParameterDefinition[], int, int, Date, String[][], Map)}
     */
    public JobDefinition(JobDefinitionID id,
                         String name,
                         String description,
                         JobParameterDefinition[] parameters,
                         int retentionPeriod,
                         int jobDefinitionType,
                         Date removeDate,
                         String[][] properties,
                         String application,
                         HashMap<String, HashMap<String, String>> localizedTextMap) {
        
        this(id, 
             new JobDefinitionName(application, name),
             description, 
             parameters, 
             retentionPeriod, 
             jobDefinitionType, 
             removeDate, 
             properties,
             localizedTextMap);
    }

    /**
     * Creates a new instance of JobDefinition. An object created with this 
     * constructor is considered to be completely initialized and values of 
     * its properties cannot be changed with call to any of the set methods.
     * 
     * @param id  the id of this job definition object. This parameter cannot be null.
     * @param name  the name of this job definition. It must not be null.
     * @param description  description of the job. This could be also null. If <code>description</code>
     * is null then the job definition is considered to have no description.
     * @param parameters an array of job parameter definitions. This parameter must not be null. If a instances
     * of this job definition takes no parameters than an empty array must be passed.
     * @param retentionPeriod the period for which the logs of individual job instance are kept. Afte this period
     * has expired the logs are removed. Its values are in days measurement units.
     * @param jobDefinitionType an integer signifying the job defintion type. 
     * It's values are enumerated by <code>JobDefinitionTyp</code> class.
     * @param removeDate the date when the job which is represented by this
     * metadata has been removed. This can be <code>null</code> if the job is
     * still deployed.
     * @param properties a list of additional properties for this job.
     * @param localizedTextMap localization information for this job.
     * @throws NullPointerException thrown if <code>id</code>, 
     * <code>name</code>, or <code>parameters</code> is null.
     * @throws IllegalArgumentException thrown if the value of
     * <code>jobDefinitionType</code> does not corresponds to a valid job 
     * definition as defined in {@link com.sap.scheduler.runtime.JobDefinitionType}
     */
    public JobDefinition(JobDefinitionID id,
            JobDefinitionName name,
            String description,
            JobParameterDefinition[] parameters,
            int retentionPeriod,
            int jobDefinitionType,
            Date removeDate,
            String[][] properties,
            HashMap<String, HashMap<String, String>> localizedTextMap) {
        
        if ((this.id = id) == null)
            throw new NullPointerException("id");
        if ((this.name = name) == null)
            throw new NullPointerException("name");
        if ((this.parameters = parameters) == null)
            throw new NullPointerException("parameters");
        if (!JobDefinitionType.isValidJobDefintionType(this.jobDefinitionType = jobDefinitionType))
            throw new IllegalArgumentException("the supplied job definition type with value " + jobDefinitionType
               + " is not a valid job definition type. For values of correct job defintion types please refer to" +
               " the documentation of " + JobDefinitionType.class);
        if ((this.properties = properties) == null) {
            throw new IllegalArgumentException("properties variable must not be null");
        }

        //no restrictions for retention period currently.
        this.retentionPeriod = retentionPeriod;
        this.description = description;
        this.removeDate = removeDate;
        this.m_localizedTextMap = localizedTextMap;        
    }
    
    
    /**
     * @deprecated Please use {@link #JobDefinition(JobDefinitionID, JobDefinitionName, String, JobParameterDefinition[], int, int, Date, String[][], Map)}
     */
    public JobDefinition() {
        jobDefinitionType = JobDefinitionType.INVALID_JOB_DEFINITION;
    }
    
    
    /**
     * Sets the localization info fpr this parameter
     * 
	 * @param the localization info
     * @deprecated use constructor instead
	 */
    public void setLocalizationInfoMap(HashMap<String, HashMap<String, String>> localizedTextMap) {
    	m_localizedTextMap = localizedTextMap;
    }
    
    /**
     * Returns the localization info for this parameter. 
     * <p>
     * Note: This method should be used only by the Scheduler runtime.
     * 
	 * @return the localization info
	 */
    public HashMap<String, HashMap<String, String>> getLocalizationInfoMap() {
    	return m_localizedTextMap;
    }
    

    /**
     * Queries a property from the current instance. The property name search is done with linear complexity. The
     * search is not casensitive so quering for property with name "EXAMPLENAME" and "exampblename" would yield
     * the same result.
     * @param name - a property name whose value should be found. This parameter can be null.
     * @return the value of the property <code>name</code>. Null if no property with this name is found.
     */
    public String readProperty(String name) {
        for (int i = 0; i < properties.length; i++) {
            if (name.equalsIgnoreCase(properties[i][0]))
                return properties[i][1];
        }
        return null;
    }

    /**
     * Puts a property in the list of properties belonging to this instance. If a property with the same
     * name already exists it is overriden. Letter capitalization is not taken into care so if a <code>name</code>
     * equals "EXAMPLENAME" but a property with name "examplename" exists the value will be overriden.
     * @param name name of the property to be put. This cannot be null nor it could be the empty string
     * @param value the value of the property.
     * @return the old value associated with this name. Null if no property with this name is found.
     */
    public String putProperty(String name, String value) {
        if (name == null)
            throw new NullPointerException("name");
        if (name.equalsIgnoreCase(""))
            throw new IllegalArgumentException("The supplied name is an empty string");
        for (int i = 0; i < properties.length; i++) {
            if (name.equalsIgnoreCase(properties[i][0])) {
                String tmp = properties[i][1];  // stores the new value under the same name
                properties[i][1] = value;       // and return the found value
                return tmp;
            } else if (properties[i][0] == null) {
                properties[i][0] = name;  // if no existing property with this name is found
                properties[i][1] = value; // write this property in the first free slot
                return null;
            }
        }
        //if no free slot is found resize the array and write the new property at the end.
        String[][] resized = new String[properties.length + 5][2]; //fixed resizing with 5 more elements
        System.arraycopy(properties, 0, resized, 0, properties.length);
        resized[properties.length][0] = name;
        resized[properties.length][1] = value;
        properties = resized;
        return null;
    }

    /**
     * Obtains all properties currently stored in this instance. Each row of the returned array represents
     * a single property. The first column is the property's name and the second is the property's value. I.e.
     * <code>getProperties[2][0]</code> would yield the name of the 3 property and the expression <code>
     * getProperties[2][1]</code> would yield the value of the 3 property. The lenght of the returned array
     * could not be used to accurately determine the number of properties currently stored in this instance.
     * @return The list of properties stored in this instance. The return value cannot not be null.
     */
    public String[][] getProperties() {
        assertSet(properties, "properties");
        return properties;
    }

    /**
     * @deprecated - intended to be used only by XML Seriliazliation/deserialization framework. Instead of this
     * method use the {@link #putProperty(String, String)} method
     * @throws IllegalStateException - thrown if properties field is already initialized.
     */
    public void setProperties(String[][] properties) {
        assertNotSet(this.properties, "properties");
        this.properties = properties;
    }


    /**
     * Obtains the id of this job definition.
     * @return the id of this job definition
     * @throws IllegalStateException - if job id is not initialized
     */
    public JobDefinitionID getJobDefinitionId() {
        assertSet(id, "job id");
        return id;
    }

    /**
     * @deprecated - used only for XML Serialization/Deserialization. Instead of using this method
     * use a constructor acceptiong JobDefintionID argument.
     * @param id - the id of this job definition
     */
    public void setJobDefinitionId(JobDefinitionID id) {
        assertNotSet(this.id, "job id");
        this.id = id;
    }

    /**
     * Obtains a text description of this job definition
     * @return the description of this job the description. Null if the job definition has no description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @deprecated - used only for XML Serialization/Deserialization. Instead of using this method
     * use a constructor accipting a description argument.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @deprecated Use <code>getJobDefinitionName.getName()</code> instead
     * 
     * @return the name of this job definition. The return value cannot be null.
     * @throws IllegalStateException - if this instance has not yet been initialized
     */
    public String getName() {
        assertSet(name, "name");
        return name.getName();
    }
    
    /**
     * This method returns the {@link JobDefinitionName} object which represents
     * the name of this job definition.
     * 
     * @return the name object
     */
    public JobDefinitionName getJobDefinitionName() {
        return name;
    }

    /**
     * @deprecated - used only for XML Serialization/Deserialization. Instead of using this method
     * use a constructor accepting a {@link JobDefinitionName} argument.
     * 
     * @param name - the name to set.
     */
    public void setName(String name) {
        assertNotSet(this.name, "name");
        this.name = new JobDefinitionName(name);
    }

    /**
     * This method returns the array of parameter definitions for
     * this job definition.
     * 
     * @return possibly empty array of parameters
     * @throws IllegalStateException - if this instance has not been initialized yet.
     */
    public JobParameterDefinition[] getParameters() {
        assertSet(parameters, "parameters");
        return parameters;
    }
    
    /**
     * This method returns the job parameter definition for the specified
     * parameter. 
     * 
     * @param name name of the parameter
     * @return a JobParameterDefinition object or null if there is no such
     * parameter
     * @throws NullPointerException if name is null
     */
    public JobParameterDefinition getParameter(String name) {
        for (JobParameterDefinition def : parameters) {
            if (name.equals(def.getName())) {
                return def;
            }
        }
        return null;
    }

    /**
     * Sets the list of parameter definitions for this job.
     * @deprecated - used only for XML Serialization/Deserialization. Instead of using this method
     * use a constructor accepting a parameters argument.
     * 
     * @param parameters The parameters to set.
     */
    public void setParameters(JobParameterDefinition[] parameters) {
        assertNotSet(this.parameters, "parameters");
        this.parameters = parameters;
    }

    /**
     * Set the retention period in days
     * @deprecated - used only for XML Serialization/Deserialization. Instead of using this method
     * use a constructor accepting a parameters argument.
     * @param days retention period for this job
     */
    public void setRetentionPeriod(int days) {
        retentionPeriod = days;
    }

    /**
     * Obtains the retention period in days.
     * 
     * @return the retention period in days. The value 
     * {@link #INFINITE_RETENTION_PERIOD} means that instances of this job 
     * definition will not deleted. 
     */
    public int getRetentionPeriod() {
        return retentionPeriod;
    }

    /**
     * This method returns an int representing the type of this job.
     * 
     * @return an int representing the type of this job
     * @throws IllegalStateException - if this instance has not been initialized yet.
     */
    public int getJobType() {
        if (jobDefinitionType == JobDefinitionType.INVALID_JOB_DEFINITION) {
            throw new IllegalStateException(MessageFormat.format(notSetMessage, new Object[]{"job type"}));
        }
        return jobDefinitionType;

    }
    
    public String getApplication() {
        return name.getApplicationName();
    }
    
    public Date getRemoveDate() {
        return removeDate;
    }

    /**
     * @deprecated - used only for XML Serialization/Deserialization. Instead of using this method
     * use a constructor accipting a parameters argument.
     */
    public void setJobType(int jobDefinitionType) {
        if (!JobDefinitionType.isValidJobDefintionType(jobDefinitionType)) {
            throw new IllegalStateException(MessageFormat.format(setMessage, new Object[]{"job type"}));
        }
        this.jobDefinitionType = jobDefinitionType;
    }

    private void assertNotSet(Object field, String fieldName) {
        if (field != null)
            throw new IllegalStateException(MessageFormat.format(setMessage, new Object[]{fieldName}));
    }

    private void assertSet(Object field, String fieldName) {
        if (field == null)
            throw new IllegalStateException(MessageFormat.format(notSetMessage, new Object[]{fieldName}));
    }

    /**
     * Obtains a string representation of this instance. The string representation contains all attributes including
     * the properties. The returned string delimits each attribute by a new line character. Each property is also represented
     * as a name=value string, where the seprate properties are again delimited by new line character. Each parameter is
     * delimited from the rest again with a new line character.
     * @return the string representation of this object.
     */
    public String toString() {
        StringBuffer strBuff = new StringBuffer();
        String LINE_WRAP = System.getProperty("line.separator");
        
        strBuff.append("Job Definition ID: ").append(id).append(LINE_WRAP);
        strBuff.append("Job Name:          ").append(name.getName()).append(LINE_WRAP);
        strBuff.append("Application Name:  ").append(name.getApplicationName()).append(LINE_WRAP);
        strBuff.append("Job Type:          ").append(JobDefinitionType.toString(jobDefinitionType)).append(LINE_WRAP);
        strBuff.append("Job Description:   ").append(description).append(LINE_WRAP);
        strBuff.append("Retention Period:  ").append(retentionPeriod).append(LINE_WRAP);
        strBuff.append("Remove Date:       ").append(removeDate).append(LINE_WRAP);
        strBuff.append("Parameters:        ").append(LINE_WRAP);
        for (int i = 0; i < parameters.length; i++) {
            strBuff.append(parameters[i].toFormattedString()).append(LINE_WRAP);
        }
        strBuff.append("Properties:        ").append(LINE_WRAP);
        for (int i = 0; i < properties.length; i++) {
            strBuff.append(properties[i][0]).append('=').append(properties[i][1]).append(LINE_WRAP);
        }
        
        return strBuff.toString();
    }
    
    
    /**
     * Returns the localized name depending on the current system locale. 
     * If not available, the default name deployed with this job will be 
     * returned.
     * 
     * @return the localized name
     */
    public String getLocalizedName() {
        return getLocalizedString(null, false);
    }

    
    /**
     * Returns the localized name depending on the provided locale 
     * if available, otherwise the default name deployed with this job will be 
     * returned.
     * 
     * @param l the locale
     * 
     * @return the localized String if available, otherwise the default name
     */
    public String getLocalizedName(Locale l) {
        return getLocalizedString(l, false);
    }
    

    /**
     * Returns the localized description depending on the current system locale, 
     * if not available, the default description deployed with this job will be 
     * returned.
     * 
     * @return the localized description
     */
    public String getLocalizedDescription() {
        return getLocalizedString(null, true);
    }
    

    /**
     * Returns the localized description depending on the provided locale 
     * if available, otherwise the default description deployed with this job 
     * will be returned.
     * 
     * @param l the locale
     * 
     * @return the localized String if available, otherwise the default description
     */
    public String getLocalizedDescription(Locale l) {
        return getLocalizedString(l, true);
    }
    
    
    /**
     * Helper-method which perform the calculating of the correct String to
     * return.
     * 
     * @param l the Locale which might be null, what means using the default 
     *          locale
     * @param isDescription should the localization String for the description
     *                      returned or for the name
     * @return the localized value
     */
    private String getLocalizedString(Locale l, boolean isDescription) {
        String defaultValue = null;
        if (isDescription) { 
            defaultValue = description;
            if (defaultValue == null) {
                defaultValue = "";
            }
        } else {
            defaultValue = name.getName();
        }
        
        if (m_localizedTextMap == null) {
            // no localization available, return the default
            return defaultValue;
        }
        if (l == null) {
            l = Locale.getDefault(); // default locale
        }        

        HashMap<String, String> props = m_localizedTextMap.get(LocalizationHelper.getStringFromLocale(l));
        if (props == null) {
            // no localization available for the given locale, return the default
            return defaultValue;
        } else {
            String searchKey = null;
            if (isDescription) {
                searchKey = "job."+name.getName()+".Description";
            } else {
                searchKey = "job."+name.getName();
            }
            
            String value = props.get(searchKey);
            if (value != null) {
                return value; // localized String found
            }
            
            // localized value not found, return the default
            return defaultValue;
        }                    
    }
}
