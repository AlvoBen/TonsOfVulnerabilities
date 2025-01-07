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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


/**
 * This interface represents a concrete job parameter. It also contains
 * a reference to the JobParameterDefinition object in order to describe
 * the type of this parameter.
 * 
 * @author Dirk Marinski
 */
public class JobParameter implements Serializable {

    static final long serialVersionUID = -7096506475866802404L;  
    
    private final static Location location = Location.getLocation(JobParameter.class);
    private final static Category category = LoggingHelper.SYS_SERVER;

    private JobParameterDefinition mDefinition;
    private Object mValue;
    
    /**
     * Constructs a job parameter object from the given value. An 
     * IllegalArgumentException is thrown if the type does not match 
     * the type specified in the JobParameterDefinition object.
     * 
     * @param def the JobParameterDefinition object
     * @param value the value
     * @throws IllegalArgumentException if the type of the value does not match
     * the type specified in the JobParameterDefinition object
     */
    public JobParameter(JobParameterDefinition def, Object value) 
                                            throws IllegalArgumentException {
        
        JobParameterType type = def.getType();
        
        if (value == null) {
            // only do type checking below if value is provided
            mDefinition = def;
            return;
        }
        
        if (JobParameterType.BOOLEAN.equals(type)) {
            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException("Expected type must be Boolean but is " + value.getClass().getName());
            }
        } else if (JobParameterType.DATE.equals(type)) {
            if (!(value instanceof Date)) {
                throw new IllegalArgumentException("Expected type must be Date but is " + value.getClass().getName());
            }
        } else if (JobParameterType.DOUBLE.equals(type)) {
            if (!(value instanceof Double)) {
                throw new IllegalArgumentException("Expected type must be Double but is " + value.getClass().getName());
            }
        } else if (JobParameterType.FLOAT.equals(type)) {
            if (!(value instanceof Float)) {
                throw new IllegalArgumentException("Expected type must be Float but is " + value.getClass().getName());
            }
        } else if (JobParameterType.INTEGER.equals(type)) {
            if (!(value instanceof Integer)) {
                throw new IllegalArgumentException("Expected type must be Integer but is " + value.getClass().getName());
            }
        } else if (JobParameterType.LONG.equals(type)) {
            if (!(value instanceof Long)) {
                throw new IllegalArgumentException("Expected type must be Long but is " + value.getClass().getName());
            }
        } else if (JobParameterType.PROPERTIES.equals(type)) {
            if (!(value instanceof Properties)) {
                throw new IllegalArgumentException("Expected type must be Properties but is " + value.getClass().getName());
            }
        } else if (JobParameterType.STRING.equals(type)) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException("Expected type must be String but is " + value.getClass().getName());
            }
        }
        
        mDefinition = def;
        mValue = value;
    }
    
    /**
     * Constructs a job parameter object from the given string value. If the
     * expected value type is not String it is created from the String.
     * 
     * @param def the JobParameterDefinition object
     * @param value the string value
     * @throws IllegalArgumentException if the provided type does not match
     * the type of the JobParameterDefinition
     */
    public JobParameter(JobParameterDefinition def, String value) 
                                               throws IllegalArgumentException {
        mDefinition = def;
        mValue = valueOf(value);
    }
    
    public JobParameter(JobParameterDefinition def, Long value) {
        mDefinition = def;
        mValue = value;
    }

    public JobParameter(JobParameterDefinition def, Integer value) {
        mDefinition = def;
        mValue = value;
    }

    public JobParameter(JobParameterDefinition def, Boolean value) {

        mDefinition = def;
        mValue = value;
    }

    public JobParameter(JobParameterDefinition def, Double value) {
        mDefinition = def;
        mValue = value;
    }

    public JobParameter(JobParameterDefinition def, Float value) {
        mDefinition = def;
        mValue = value;
    }

    public JobParameter(JobParameterDefinition def, Properties value) {
        mDefinition = def;
        mValue = value;
    }
    
    public JobParameter(JobParameterDefinition def, Date value) {
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
    
    public void setPropertiesValue(Properties props) {
        mValue = props;
    }
    
    public Properties getPropertiesValue() {
        return (Properties)mValue;
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
            if ("".equals(mValue)) {
                return null;
            } else {            
                return (String)mValue;
            }
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
        } else if (mValue instanceof Properties) {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                ((Properties)mValue).store(bos, "");
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
                return props;
            } catch (IOException io) {
                category.logThrowableT(Severity.ERROR, location, "Unexpected IOException from where no IOException can be thrown.", io);
                return "";
            }

        } else {
            throw new IllegalArgumentException("Type " + t.toString() + " nyi.");
        }
    }
    
    
    /**
     * Compares a JobParameter with this.
     * 
     * @param entry the JobParameter to compare
     * @return true if the JobParameter are equals in case of its members otherwise false.
     */
    public boolean compareJobParameter(JobParameter entry) {
        if ( !toString().equals(entry.toString()) || !mDefinition.equals(entry.mDefinition) ) {
            return false;
        }
        return true;
    }
}