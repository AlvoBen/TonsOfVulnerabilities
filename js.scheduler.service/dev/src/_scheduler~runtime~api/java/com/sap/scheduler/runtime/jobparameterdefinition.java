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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.sap.scheduler.runtime.util.LocalizationHelper;

/**
 * This class represents the definition for a single job definition 
 * parameter.
 *
 * @author Dirk Marwinski
 */
public class JobParameterDefinition implements Serializable {

    static final long serialVersionUID = 5154254481473659240L;
    
    // The following DTD is used to define the XML from which this object 
    // is created.
    //    
    //    <!ATTLIST job_definition_parameter
    //        name        CDATA   #REQUIRED
    //        data_type   (STRING|BIGDECIMAL|TIMESTAMP)  #REQUIRED
    //        nullable    (Y|N)   #IMPLIED
    //        description CDATA   #IMPLIED
    //        dataDefault    CDATA   #IMPLIED
    //        display     (Y|N)   #IMPLIED
    //        direction       (IN|OUT|INOUT) #IMPLIED
    //        group       CDATA   #IMPLIED
    //    >
    //    <!ELEMENT job_definition_parameter EMPTY>

    private String m_name;
    private JobParameterType m_type;
    private boolean m_nullable;
    private String m_description;
    private String m_dataDefault;
    private boolean m_display;
    private String m_direction;
    private String m_group;

    /** 
     * The Map for the localization information. If no localization is available 
     * the localized text might be null.
     */
    private HashMap<String, HashMap<String, String>> m_localizedTextMap = null;
    
    
    /** 
     * Default constructor
     * 
     * @deprecated Needed for jver tests. Use only the non-empty constructor! 
     */
    public JobParameterDefinition() {}
    
    public JobParameterDefinition(String name, JobParameterType type, boolean nullable, String description, String dataDefault, boolean display, String direction, String group, HashMap<String, HashMap<String, String>> localizedTextMap) {
       this.m_name = name;
       this.m_type = type;
       this.m_nullable = nullable;
       this.m_description = description;
       this.m_dataDefault = dataDefault;
       this.m_display = display;
       this.m_direction = direction;
       this.m_group = group;   
       this.m_localizedTextMap = localizedTextMap;
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
	 * @return Returns the default value for this parameter (or null if there
     * is no default)
	 */
	public String getDefaultData() {
		return m_dataDefault;
	}

    /**
	 * @param data_default the default value for this parameter
     * @deprecated use constructor instead
	 */
	public void setDefaultData(String data_default) {
		this.m_dataDefault = data_default;
	}

    /**
	 * @return Returns the description of this parameter to be displayed 
     * in a dialog.
	 */
	public String getDescription() {
		return m_description;
	}
	/**
     * Set the description for a parameter
     * 
	 * @param description The description to set.
     * @deprecated use constructor instead
	 */
	public void setDescription(String description) {
		this.m_description = description;
	}
	/**
	 * @return Returns the direction. It can either be in, out, or inout
	 */
	public String getDirection() {
		return m_direction;
	}
    
    public boolean isIn() {
        return "in".equalsIgnoreCase(m_direction);
    }

    public boolean isOut() {
        return "out".equalsIgnoreCase(m_direction);
    }
    
    public boolean isInOut() {
        return "inout".equalsIgnoreCase(m_direction);
    }
    
	/**
	 * @param direction The direction to set. It can either be in, out, or 
     * inout
     * @deprecated use constructor instead
     * 
     * @throws IllegalArgumentException if the string is not in, out, or inout
	 */
	public void setDirection(String direction) {
        if ("in".equalsIgnoreCase(direction) ||
                "inout".equalsIgnoreCase(direction) ||
                "out".equalsIgnoreCase(direction)) {
        	this.m_direction = direction;
        } else {
            throw new IllegalArgumentException("Direction must either be \"in\", \"out\", or \"inout\" but value is \"" + direction + "\".");
        }
	}
	/**
	 * @return Returns true if this parameter should be displayed in a dialog
     * box, false otherwise.
	 */
	public boolean isDisplay() {
		return m_display;
	}
	/**
	 * @param display The display to set.
     * @deprecated use constructor instead
	 */
	public void setDisplay(boolean display) {
		this.m_display = display;
	}
    
	/**
	 * @return Returns the group.
	 */
	public String getGroup() {
		return m_group;
	}
	/**
	 * @param group The group to set.
     * @deprecated use constructor instead
	 */
	public void setGroup(String group) {
		this.m_group = group;
	}

    /**
	 * @return Returns the name of the parameter.
	 */
	public String getName() {
		return m_name;
	}
	/**
	 * @param name Set the name of this parameter.
     * @deprecated use constructor instead
	 */
	public void setName(String name) {
		this.m_name = name;
	}
	/**
	 * @return Returns true if this parameter may be null, false otherwise
	 */
	public boolean isNullable() {
		return m_nullable;
	}
	/**
	 * @param nullable Set this to true if the parameter may be null, false 
     * otherwise
     * @deprecated use constructor instead
	 */
	public void setNullable(boolean nullable) {
		this.m_nullable = nullable;
	}
	/**
     * This method returns the type of the parameter. The following types
     * are supported:
     * <ul>
     * <li>String
     * <li>Integer
     * <li>Long
     * <li>Date
     * <li>TBD
     * </ul>
     * 
	 * @return Returns the type.
	 */
	public JobParameterType getType() {
		return m_type;
	}
	/**
     * Sets the type of this parameter.
     * 
	 * @param type The type to set. See description of {@link #getType()}
     * @deprecated use constructor instead
	 */
	public void setType(JobParameterType type) {
		this.m_type = type;
	}
    
    public int hashCode() {
        StringBuffer value = new StringBuffer();
        value.append(m_name)
             .append(m_type.toString())
             .append(new Boolean(m_nullable).toString())
             .append(m_description)
             .append(m_dataDefault)
             .append(new Boolean(m_display).toString())
             .append(m_group);
        return value.toString().hashCode();
    }
    
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof JobParameterDefinition)) {
            return false;
        }
        JobParameterDefinition def = (JobParameterDefinition) obj;
        return m_name.equals(def.m_name) 
               && m_type.equals(def.m_type)
               && m_nullable == def.m_nullable
               && (m_description != null ? m_description.equals(def.m_description) : def.m_description == null)
               && (m_dataDefault != null ? m_dataDefault.equals(def.m_dataDefault) : def.m_dataDefault == null)
               && m_display == def.m_display
               && (m_group != null ? m_group.equals(def.m_group) : def.m_group == null);
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
            defaultValue = m_description;
            if (defaultValue == null) {
                defaultValue = "";
            }
        } else {
            defaultValue = m_name;
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
            // no localization available for the default locale, return the default
            return defaultValue;
        } else {
            // we do not have the jobName here to which this parameter belongs,
            // so iterating is necessary
            String searchKey = null;
            if (isDescription) {
                searchKey = ".Parameter."+m_name+".Description";
            } else {
                searchKey = ".Parameter."+m_name;
            }
            
            for (Iterator<Map.Entry<String, String>> iter = props.entrySet().iterator(); iter.hasNext();) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next(); 
                String key = entry.getKey();
                if (key.startsWith("job.") && key.endsWith(searchKey)) {
                    return entry.getValue();
                }                    
            }
            // localized value not found, return the default
            return defaultValue;
        }                    
    }
        
    
    /**
     * Returns this JobParameterDefinition in a formatted way
     * 
     * @return JobParameterDefinition in formatted way
     */
    public String toFormattedString() {
        String LINE_WRAP = System.getProperty("line.separator");
        StringBuilder buf = new StringBuilder();   
        
        buf.append("Name:        ").append(m_name);  
        buf.append(LINE_WRAP);
        buf.append("Type:        ").append(m_type.toString());
        buf.append(LINE_WRAP);
        buf.append("Description: ").append(m_description);  
        buf.append(LINE_WRAP);
        buf.append("DefaultData: ").append(m_dataDefault);
        buf.append(LINE_WRAP);
        buf.append("Direction:   ").append(m_direction);  
        buf.append(LINE_WRAP);
        buf.append("Display:     ").append(m_display);
        buf.append(LINE_WRAP);
        buf.append("Group:       ").append(m_group);  
        buf.append(LINE_WRAP);
        buf.append("Nullable:    ").append(m_nullable);
        buf.append(LINE_WRAP);
        
        return buf.toString();
    }
    
}
