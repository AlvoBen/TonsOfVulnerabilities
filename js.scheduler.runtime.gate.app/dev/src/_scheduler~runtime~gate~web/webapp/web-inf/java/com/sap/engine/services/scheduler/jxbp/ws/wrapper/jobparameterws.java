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
package com.sap.engine.services.scheduler.jxbp.ws.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobParameterDefinition;
import com.sap.scheduler.runtime.JobParameterType;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


/**
 * This class represents a concrete job parameter to be used by the 
 * JXBP Web Service.
 * 
 * @author Thomas Mueller (d040939)
 */
public class JobParameterWS implements Serializable {

    static final long serialVersionUID = 1L;  
    
    private final static Location location = Location.getLocation(JobParameterWS.class); 
    private final static Category category = LoggingHelper.SYS_SERVER;

    private JobParameterDefinitionWS mDefinition = null;
        
    private String m_valueString = null;
    private Long m_valueLong = null;
    private Integer m_valueInteger = null;
    private Boolean m_valueBoolean = null;
    private Double m_valueDouble = null;
    private Float m_valueFloat = null;
    private String[][] m_valueProperties = null;
    private Date m_valueDate = null;
    
            
    
    // default constructor needed for WebService
    public JobParameterWS() {}
    
    
    public JobParameterWS(JobParameterDefinition def, String value) {
        mDefinition = new JobParameterDefinitionWS(def);
        m_valueString = value;
    }
    
    public JobParameterWS(JobParameterDefinition def, Long value) {
        mDefinition = new JobParameterDefinitionWS(def);
        m_valueLong = value;
    }

    public JobParameterWS(JobParameterDefinition def, Integer value) {
        mDefinition = new JobParameterDefinitionWS(def);
        m_valueInteger = value;
    }

    public JobParameterWS(JobParameterDefinition def, Boolean value) {
        mDefinition = new JobParameterDefinitionWS(def);
        m_valueBoolean = value;
    }

    public JobParameterWS(JobParameterDefinition def, Double value) {
        mDefinition = new JobParameterDefinitionWS(def);
        m_valueDouble = value;
    }

    public JobParameterWS(JobParameterDefinition def, Float value) {
        mDefinition = new JobParameterDefinitionWS(def);
        m_valueFloat = value;
    }

    public JobParameterWS(JobParameterDefinition def, String[][] value) {
        mDefinition = new JobParameterDefinitionWS(def);
        m_valueProperties = value;
    }
    
    public JobParameterWS(JobParameterDefinition def, Date value) {
        mDefinition = new JobParameterDefinitionWS(def);
        m_valueDate = value;
    }
    
    public JobParameterDefinitionWS getJobParameterDefinitionWS() {
        return mDefinition;
    }
    
    public void setJobParameterDefinitionWS(JobParameterDefinitionWS jobParamDef) {
        mDefinition = jobParamDef;
    }

    public String getName() {
        return mDefinition.getName();
    }
    
    // Values

    public String getStringValue() {
    	return m_valueString;
    }
  
    public void setStringValue(String value) {
    	m_valueString = value;
    }

    public Float getFloatValue() {
        return m_valueFloat;
    }
  
    public void setFloatValue(Float value) {
        m_valueFloat = value;
    }
  
    public Double getDoubleValue() {
        return m_valueDouble;
    }
  
    public void setDoubleValue(Double value) {
        m_valueDouble = value;
    }
  
    public Date getDateValue() {
        return m_valueDate;
    }
  
    public void setDateValue(Date dt) {
        m_valueDate = dt;
    }
  
    public Long getLongValue() {
        return m_valueLong;
    }
    
    public Boolean getBooleanValue() {
        return m_valueBoolean;
    }    
    
    public Integer getIntegerValue() {
        return m_valueInteger;
    }
    
    public void setIntegerValue(Integer value) {
        m_valueInteger = value;
    }
  
    public void setLongValue(Long value) {
        m_valueLong = value;
    }
    
    public void setBooleanValue(Boolean value) {
    	m_valueBoolean = value;
    }
    
    public void setPropertiesValue(String[][] props) {
    	m_valueProperties = props;
    }
    
    public String[][] getPropertiesValue() {
        return m_valueProperties;
    }
    
    
    public String toString() {
        if (m_valueString != null) {
            return m_valueString;
        } else if (m_valueFloat != null) {
            return m_valueFloat.toString();
        } else if (m_valueDouble != null) {
            return m_valueDouble.toString();
        } else if (m_valueLong != null) {
            return m_valueLong.toString();
        } else if (m_valueInteger != null) {
            return m_valueInteger.toString();
        } else if (m_valueDate != null) {
            return new SimpleDateFormat().format(m_valueDate);
        } else if (m_valueBoolean != null) {
            return m_valueBoolean.toString();
        } else if (m_valueProperties != null) {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                Properties tmpProperties = StringArrayToProperties(m_valueProperties);
                tmpProperties.store(bos, "");
                return bos.toString("ISO-8859-1");
            } catch (IOException io) {
                category.logThrowableT(Severity.ERROR, location, "Unexpected IOException from where no IOException can be thrown.", io);
                return "";
            }
        } else {
            throw new IllegalArgumentException("No value set. Value is null.");
        }
    }
    
    
    public JobParameter getJobParameter() {        
    	JobParameterType paramType = JobParameterType.valueOf(mDefinition.getType());
    	JobParameterDefinition jobParamDef = mDefinition.getJobParameterDefinition();
    	
        if ( paramType.equals(JobParameterType.STRING) ) {
        	return new JobParameter(jobParamDef, m_valueString);
        } else if ( paramType.equals(JobParameterType.LONG) ) {
        	return new JobParameter(jobParamDef, m_valueLong);
        } else if ( paramType.equals(JobParameterType.INTEGER) ) {
        	return new JobParameter(jobParamDef, m_valueInteger);
        } else if ( paramType.equals(JobParameterType.BOOLEAN) ) {
        	return new JobParameter(jobParamDef, m_valueBoolean);
        } else if ( paramType.equals(JobParameterType.DOUBLE) ) {
        	return new JobParameter(jobParamDef, m_valueDouble);
        } else if ( paramType.equals(JobParameterType.FLOAT) ) {
        	return new JobParameter(jobParamDef, m_valueFloat);
        } else if ( paramType.equals(JobParameterType.DATE) ) {
        	return new JobParameter(jobParamDef, m_valueDate);
        } else if ( paramType.equals(JobParameterType.PROPERTIES) ) {     
            if (m_valueProperties == null) {
                return new JobParameter(mDefinition.getJobParameterDefinition(), (Properties)null);
            } else {
                return new JobParameter(mDefinition.getJobParameterDefinition(), StringArrayToProperties((String[][])m_valueProperties));
            }
        } else {
        	throw new IllegalArgumentException("Data type '"+paramType.toString()+"' for parameter '"+jobParamDef.getName()+"' is not valid");
        }
    }
    
    public static JobParameterWS getJobParameterWS(JobParameter param) {
    	
        JobParameterDefinition jobDef = param.getJobParameterDefinition();
        JobParameterType paramType = jobDef.getType();
        
        if ( paramType.equals(JobParameterType.STRING) ) {
        	return new JobParameterWS(jobDef, param.getStringValue());
        } else if ( paramType.equals(JobParameterType.LONG) ) {
        	return new JobParameterWS(jobDef, param.getLongValue());
        } else if ( paramType.equals(JobParameterType.INTEGER) ) {
        	return new JobParameterWS(jobDef, param.getIntegerValue());
        } else if ( paramType.equals(JobParameterType.BOOLEAN) ) {
        	return new JobParameterWS(jobDef, param.getBooleanValue());
        } else if ( paramType.equals(JobParameterType.DOUBLE) ) {
        	return new JobParameterWS(jobDef, param.getDoubleValue());
        } else if ( paramType.equals(JobParameterType.FLOAT) ) {
        	return new JobParameterWS(jobDef, param.getFloatValue());
        } else if ( paramType.equals(JobParameterType.DATE) ) {
        	return new JobParameterWS(jobDef, param.getDateValue());
        } else if ( paramType.equals(JobParameterType.PROPERTIES) ) {
            if (param.getValue() == null) {
                return new JobParameterWS(jobDef, (String[][])null);
            } else {
                return new JobParameterWS(jobDef, propertiesToStringArray(param.getPropertiesValue()));
            }
        } else {
        	throw new IllegalArgumentException("Data type '"+paramType.toString()+"' for parameter '"+param.getName()+"' is not valid");
        }
    }
    
    private static String[][] propertiesToStringArray(Properties props) {
        
        Enumeration names = props.propertyNames();
        String[][] strProps = new String[props.size()][2];
        int i=0;
        
        while (names.hasMoreElements()) {
            
            String key = (String)names.nextElement();
            strProps[i][0] = key;
            strProps[i][1] = props.getProperty(key);
        }
        return strProps;
    }

    private static Properties StringArrayToProperties(String[][] strProps) {
        
        Properties props = new Properties();
        for (int i=0; i < strProps.length; i++) {
            props.setProperty(strProps[i][0], strProps[i][1]);
        }
        return props;
    }
    
}