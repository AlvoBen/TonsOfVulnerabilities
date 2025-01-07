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

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.scheduler.runtime.*;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * Content handler to parse job-definition XML file
 *
 * @author Dirk Marwinski
 */
public class JobParameterDefinitionContentHandler extends DefaultHandler {
        
    private final static Location location = Location.getLocation(JobParameterDefinitionContentHandler.class);
    private final static Category category = LoggingHelper.SYS_SERVER;
    
    public HashMap<JobDefinitionName, JobDefinition> jobDefinitions = new HashMap<JobDefinitionName, JobDefinition>();

    private int currentJDRetentionPeriod;
    private String currentJDName;
    private String currentJDDescription;
    private List currentJDParams;
    private String applicationName;


    private String currentPath;
    
    public void setApplicationName(String name) {
        applicationName = name;
    }
    
    public void startDocument() {
        currentPath = "";
        currentJDRetentionPeriod = JobDefinition.DEFAULT_RETENTION_PERIOD;
        currentJDName = null;
        currentJDDescription = null;
        currentJDParams = new ArrayList();
    }
    
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        
        currentPath += ("/" + localName);
        
        if ("/job-definitions/job-definition".equals(currentPath)) {
            // new job begins --> reset the values
            currentJDRetentionPeriod = JobDefinition.DEFAULT_RETENTION_PERIOD;
            currentJDDescription = null;
            
            for (int i=0; i < atts.getLength(); i++) {
                String name = atts.getLocalName(i);
                String value = atts.getValue(i);
                if ("name".equals(name)) {
                    String result;
                    if ( !(result = Validator.validateName(value, 230, 1)).equals(Validator.VALID) ) {
                      throw new SAXException("'" + value + "' is illegal job-definition name : " + result);
                    }
                    currentJDName = value;
                } else if ("description".equals(name)) {
                    if ( !Validator.validateLength(value, 1024) ) {
                      throw new SAXException("'" + value + "' is too long description, alowed is 1024 chars");
                    }
                    currentJDDescription = value;
                } else if ("retention-period".equals(name)) {
                    int retentionPeriod;
                    try {
                        retentionPeriod = Integer.valueOf(value).intValue();
                    } catch (NumberFormatException nfe) {
                        throw new SAXException("Retention period parameter is not an integer. Provided value is \"" + value + "\".");
                    }
                    if (retentionPeriod < -1) {
                        throw new SAXException("Retention period parameter must be in range from -1 to 2147483647. Provided value is \"" + value + "\".");
                    }
                    currentJDRetentionPeriod = retentionPeriod;
                } else {
                    throw new SAXException("Attribute \"" + name + "\" not recognized for element \"" + currentPath + "\".");
                }
            }
            if (currentJDName == null) {
                throw new SAXException("\"job-definition\" element must have a name atribute.");
            }
            if (jobDefinitions.containsKey(currentJDName)) {
                throw new SAXException("Job with name \"" + currentJDName + "\" specified more than once.");
            }
        } else if ("/job-definitions/job-definition/job-definition-parameter".equals(currentPath) ||
                       "/job-definitions/job-definition/job-parameter".equals(currentPath)) {
            
            String defName = null;
            JobParameterType type = null;
            boolean nullable = false;
            String description = null;
            String dataDefault = null;
            boolean display = true;
            String direction = "IN";
            String group = null;
            
            for (int i=0; i < atts.getLength(); i++) {

//              name        CDATA   #REQUIRED
//              data_type   (VARCHAR|NUMBER|TIMESTAMP)  #REQUIRED
//              nullable    (Y|N)   #IMPLIED
//              description CDATA   #IMPLIED
//              data_default    CDATA   #IMPLIED
//              display     (Y|N)   #IMPLIED
//              direction       (IN|OUT|INOUT) #IMPLIED
//              group       CDATA   #IMPLIED

                String name = atts.getLocalName(i);
                String value = atts.getValue(i);
                
                if ("name".equals(name)) {
                    String result;
                    if ( !(result = Validator.validateName(value, 255, 1)).equals(Validator.VALID) ) {
                      throw new SAXException("'" + value + "' is illegal job-parameter name : " + result);
                    }
                    defName = value;
                } else if ( "data_type".equals(name) || "data-type".equals(name) ) {
                    try {
                        type = JobParameterType.valueOf(value);
                    } catch (IllegalArgumentException ia) {
                        throw new SAXException("Parameter type \"" + value + "\" not supported.");
                    }
                } else if ("nullable".equals(name)) {
                    if ("y".equalsIgnoreCase(value) ||
                            "yes".equalsIgnoreCase(value) ||
                            "true".equalsIgnoreCase(value)) {
                        nullable = true;
                    } else {
                        nullable = false;
                    }                        
                } else if ("description".equals(name)) {
                    if ( !Validator.validateLength(value, 200) ) {
                      throw new SAXException("'" + value + "' is too long description, allowed is 200 chars");
                    }
                    description = value;
                } else if ( "data_default".equals(name) || "data-default".equals(name)) {
                    dataDefault = value;
                } else if ("display".equals(name)) {
                    if ("y".equalsIgnoreCase(value) ||
                            "yes".equalsIgnoreCase(value) ||
                            "true".equalsIgnoreCase(value)) {
                        display = true;
                    } else {
                        display = false;
                    }
                } else if ("direction".equals(name)) {
                    if ("in".equalsIgnoreCase(value) || 
                            "inout".equalsIgnoreCase(value) || 
                            "out".equalsIgnoreCase(value)) {
                    	direction = value;
                    } else {
                        throw new SAXException("Value for attribute \"direction\" must either be \"in\", \"out\", or \"inout\" but is \"" + value + "\".");   
                    }
                } else if ("group".equals(name)) {
                    if (!Validator.validateLength(value, 255)) {
                        throw new SAXException("Group name '" + value + "' is too long. The maximum length is 255 characters.");
                    }
                    group = value;
                } else {
                    throw new SAXException("Attribute \"" + name + "\" not recognized for element \"" + currentPath + "\".");
                }
            }
            
            JobParameterDefinition param = new JobParameterDefinition(defName, type, nullable, description, dataDefault, display, direction, group, null); // null for LocalizedObject --> will be filled later 
            
            if (currentJDParams.contains(param)) {
                throw new SAXException("Job definition \"" + currentJDName + "\" has two parameters with the same name: " + param.getName());
            }
            currentJDParams.add(param);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

        if ("/job-definitions/job-definition".equals(currentPath)) {
            JobDefinitionName jdn = new JobDefinitionName(applicationName, currentJDName);
            jobDefinitions.put(jdn,
                    new JobDefinition(JobDefinitionID.newID(),
                    jdn,
                    currentJDDescription,
                    (JobParameterDefinition[])currentJDParams.toArray(new JobParameterDefinition[currentJDParams.size()]),
                    currentJDRetentionPeriod,
                    JobDefinitionType.MDB_JOB_DEFINITION,
                    null, new String[0][0],
                    null) // null for LocalizedObject --> will be filled later 
                    );

            currentJDParams.clear();
        }
        
        int last = currentPath.lastIndexOf('/');
        currentPath = currentPath.substring(0, last);
    }

    public void characters(char[] ch, int start, int length) 
                                                 throws SAXException {

    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {

        // The SAX parser seems to insist on it, so we return an 
        // empty DTD
        return new InputSource(new StringReader("<!-- empty DTD -->"));

    }
}
