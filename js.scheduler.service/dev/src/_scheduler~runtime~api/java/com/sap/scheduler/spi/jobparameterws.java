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
package com.sap.scheduler.spi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.scheduler.runtime.JobDefinition;
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
 * @author Dirk Marinski
 */
public class JobParameterWS implements Serializable {

    static final long serialVersionUID = -7096506475866802404L;  
    
    private final static Location location = Location.getLocation(JobParameterWS.class);
    private final static Category category = LoggingHelper.SYS_SERVER;

    private JobParameterDefinition mDefinition;
    private Object mValue;
            
    public JobParameterWS(JobParameterDefinition def, Object value) {
        mDefinition = def;
        mValue = value;
    }
    
    public JobParameterWS(JobParameterDefinition def, String value) {
        mDefinition = def;
        mValue = valueOf(value);
    }
    
    public JobParameterWS(JobParameterDefinition def, Long value) {
        mDefinition = def;
        mValue = value;
    }

    public JobParameterWS(JobParameterDefinition def, Integer value) {
        mDefinition = def;
        mValue = value;
    }

    public JobParameterWS(JobParameterDefinition def, Boolean value) {
        mDefinition = def;
        mValue = value;
    }

    public JobParameterWS(JobParameterDefinition def, Double value) {
        mDefinition = def;
        mValue = value;
    }

    public JobParameterWS(JobParameterDefinition def, Float value) {
        mDefinition = def;
        mValue = value;
    }

    public JobParameterWS(JobParameterDefinition def, String[][] value) {
        mDefinition = def;
        mValue = value;
    }
    
    public JobParameterWS(JobParameterDefinition def, Date value) {
        mDefinition = def;
        mValue = value;
    }
    
    public JobParameterDefinition getJobParameterDefinition() {
        return mDefinition;
    }

    public String getName() {
        return mDefinition.getName();
    }
    
    // Values

    public String getStringValue() {
    	return (String)mValue;
    }
  
    public void setStringValue(String value) {
        mValue = value;
    }

    public Float getFloatValue() {
        return (Float)mValue;
    }
  
    public void setFloatValue(Float value) {
        mValue = value;
    }
  
    public Double getDoubleValue() {
        return (Double)mValue;
    }
  
    public void setDoubleValue(Double value) {
        mValue = value;
    }
  
    public Date getDateValue() {
        return (Date)mValue;
    }
  
    public void setDateValue(Date dt) {
        mValue = dt;
    }
  
    public Long getLongValue() {
        return (Long)mValue;
    }
    
    public Boolean getBooleanValue() {
        return (Boolean)mValue;
    }    
    
    public Integer getIntegerValue() {
        return (Integer)mValue;
    }
    
    public void setIntegerValue(int value) {
        mValue = new Integer(value);
    }
  
    public void setLongValue(Long value) {
        mValue = value;
    }

    public void setLongValue(long value) {
        mValue = new Long(value);
    }
    
    public void setBoloeanValue(Boolean value) {
        mValue = value;
    }

    public void setBooleanValue(boolean value) {
        mValue = new Boolean(value);
    }
    
    public void setPropertiesValue(String[][] props) {
        mValue = props;
    }
    
    public String[][] getPropertiesValue() {
        return (String[][])mValue;
    }
    
    public void setValue(Object value) {
        mValue = value;
    }
    
    public Object getValue() {
        return mValue;
    }
    
    public String toString() {
        
        if (mValue == null) {
            return null;
        } else if (mValue instanceof String) {
            return (String)mValue;
        } else if (mValue instanceof Float) {
            return ((Float)mValue).toString();
        } else if (mValue instanceof Double) {
            return ((Double)mValue).toString();
        } else if (mValue instanceof Long) {
            return ((Long)mValue).toString();
        } else if (mValue instanceof Integer) {
            return ((Integer)mValue).toString();
        } else if (mValue instanceof Date) {
            return new SimpleDateFormat().format((Date)mValue);
        } else if (mValue instanceof Boolean) {
            return ((Boolean)mValue).toString();
        } else if (mValue instanceof String[][]) {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                Properties tmpProperties = StringArrayToProperties((String[][])mValue);
                tmpProperties.store(bos, "");
                return bos.toString("ISO-8859-1");
            } catch (IOException io) {
                category.logThrowableT(Severity.ERROR, location, "Unexpected IOException from where no IOException can be thrown.", io);
                return "";
            }
        } else {
            throw new IllegalArgumentException("Illegal value type " + mValue.getClass().getName());
        }
    }
    
    public Object valueOf(String value) 
                                throws IllegalArgumentException {
        JobParameterType t = mDefinition.getType();
        
        if (value == null) {
            // value not set
            return null;
        }
        
        if (JobParameterType.INTEGER.equals(t)) {
            return Integer.valueOf(value);
        } else if (JobParameterType.LONG.equals(t)) {
            return Long.valueOf(value);
        } else if (JobParameterType.FLOAT.equals(t)) {
        	return Float.valueOf(value);
        } else if (JobParameterType.DOUBLE.equals(t)) {
        	return Double.valueOf(value);
        } else if (JobParameterType.STRING.equals(t)){
            return value;
        } else if (JobParameterType.DATE.equals(t)){
            try {
                return new SimpleDateFormat().parse(value);
            } catch (ParseException pe) {
                throw new IllegalArgumentException("Illegal date format: " + pe.getMessage());
            }
        } else if (JobParameterType.BOOLEAN.equals(t)) {
            return Boolean.valueOf(value);
        } else if (JobParameterType.PROPERTIES.equals(t)) {
            
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PrintStream pst = new PrintStream(bos, true, "ISO-8859-1");
                pst.print(value);
                pst.close();

                ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());
                Properties props = new Properties();
                props.load(in);
                return propertiesToStringArray(props);
            } catch (IOException io) {
                category.logThrowableT(Severity.ERROR, location, "Unexpected IOException from where no IOException can be thrown.", io);
                return "";
            }

        } else {
            throw new IllegalArgumentException("Type " + t.toString() + " nyi.");
        }
    }
    
    public JobParameter getJobParameter() {
        
        if (!mDefinition.getType().equals(JobParameterType.PROPERTIES)) {
            // no change necessary
            return new JobParameter(mDefinition, mValue);
        } else {
            if (mValue == null) {
                return new JobParameter(mDefinition, (Properties)null);
            } else {
                return new JobParameter(mDefinition, StringArrayToProperties((String[][])mValue));
            }
        }
    }
    
    public static JobParameterWS getJobParameterWS(JobParameter param) {
        
        JobParameterDefinition paramType = param.getJobParameterDefinition();
        if (!param.getJobParameterDefinition().getType().equals(JobParameterType.PROPERTIES)) {
            return new JobParameterWS(paramType, param.getValue());
        } else {
            if (param.getValue() == null) {
                return new JobParameterWS(paramType, (String[][])null);
            } else {
                return new JobParameterWS(paramType, propertiesToStringArray(param.getPropertiesValue()));
            }
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