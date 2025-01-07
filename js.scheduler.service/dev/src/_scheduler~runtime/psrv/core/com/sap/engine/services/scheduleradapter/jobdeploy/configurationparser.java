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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.scheduler.runtime.util.LocalizationHelper;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionName;
import com.sap.scheduler.runtime.JobParameterDefinition;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Dirk Marwinski
 *
 * This class contains methods to search and process ejb jars in the 
 * configuration manager
 */
public class ConfigurationParser {

    private final static Location location = Location.getLocation(ConfigurationParser.class);
    private final static Category category = LoggingHelper.SYS_SERVER;
    
    private static final String PROPERTY_FILE_PREFIX = "meta-inf/job-definition";
    private static final String PROPERTY_FILE_POSTFIX = ".properties";
    
    private static final Pattern patternLanguage = Pattern.compile("_([a-zA-Z][a-zA-Z])");
    private static final Pattern patternLanguageCountry = Pattern.compile("_([a-zA-Z][a-zA-Z])_([a-zA-Z][a-zA-Z])");
    
    private Environment mEnvironment;

    
    public ConfigurationParser(Environment env) {
        mEnvironment = env;
    }

    public HashMap<JobDefinitionName,JobDefinition> readJobDefinitions(
                                File[] files, String applicationName)
                                throws ConfigurationParserException, IOException,
                                IllegalArgumentException, PropertyFileValidationException  {

        HashMap<JobDefinitionName, JobDefinition> jobDefinitions = new HashMap<JobDefinitionName, JobDefinition>();
        HashMap<Locale, Properties> languageFiles = new  HashMap<Locale, Properties>();
        
        ConfigurationParser parser = mEnvironment.getConfigurationParser();

        // initialize SAX parser
        //
        SAXParser saxParser = parser.initParser();
        if (saxParser == null) {
            throw new ConfigurationParserException(
                    "Unable to create SAX parser in order to read descriptors.");
        }

        for (File f : files) {
            InputStream is = new FileInputStream(f);
            ZipInputStream zi = new ZipInputStream(is);

            try {

                // read whole zip file (note: we don't care if the file
                // is empty)
                //
                ZipEntry entry = null;
                while ((entry = zi.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        continue;
                    } else if ("META-INF/job-definition.xml".equalsIgnoreCase(entry
                            .getName())) {

                        // read and parse job-defintion file (file is
                        // required for a job)
                        //
                        if (location.beLogged(Severity.INFO)) {
                            location.infoT("job-definition.xml file.");
                        }

                        byte[] parameterFile = parser.readFileFromStream(zi);
                        zi.closeEntry();

                        HashMap<JobDefinitionName, JobDefinition> tmpJobDefs = parser
                                .parseJobDefintion(saxParser, parameterFile,
                                        applicationName);

                        addAll(jobDefinitions, tmpJobDefs);

                        if (location.beDebug()) {
                            location.debugT("Found " + tmpJobDefs.size()
                                    + " jobs in jar job-definition.xml file of application\""
                                    + applicationName + "\".");
                        }

                    } else {
                        String entryName = entry.getName().toLowerCase();
                        if (entryName.startsWith(PROPERTY_FILE_PREFIX) &&
                                entryName.endsWith(PROPERTY_FILE_POSTFIX)) {
                            
                            Locale l = getLocale(entryName);
                            if (l == null) {
                                // TODO
                                throw new IllegalArgumentException("Localtization file \"" + entry.getName()
                                        + "\" of application \"" + applicationName
                                        + "\" does not conform to file name rules.");
                            }
                            Properties languageFile = new Properties();
                            languageFile.load(zi);
                            languageFiles.put(l, languageFile);
                        }
                    }
                }
            } finally {
                // make sure they are closed
                zi.close();
                is.close();
            }
        }
                
        // add the localization info to JobDefinition and JobParameterDefinition and validate the content
        // 
        addLocalizationFilesToDefinitions(jobDefinitions, languageFiles);
        
        return jobDefinitions;
    }
    
    
    private void addLocalizationFilesToDefinitions(HashMap<JobDefinitionName, JobDefinition> jobDefs, HashMap<Locale, Properties> propMap) throws PropertyFileValidationException {
        // if we are here we know the language files are valid (no invalid keys and 
        // all keys are related to jobs which are deployed in this deployment)
        
        // convert jobDefs to a map
        Map<String, JobDefinition> jobDefsMap = new HashMap<String, JobDefinition>();
        for (Map.Entry<JobDefinitionName,JobDefinition> entry : jobDefs.entrySet()) {
            jobDefsMap.put(entry.getKey().getName(), entry.getValue()); 
        }
        
        // iterate over propMap
        for (Map.Entry<Locale, Properties> langEntry : propMap.entrySet()) {
            // validate if the job exists in this deployment
            
            Locale locale = langEntry.getKey();
            Properties props = langEntry.getValue();            
            
            for (Iterator iter = props.entrySet().iterator(); iter.hasNext();) {
                Map.Entry propEntry = (Map.Entry) iter.next();
                String propKey = ((String)propEntry.getKey()).trim();
                String propValue = ((String)propEntry.getValue()).trim();
                
                
                // validate the property-name
                // names[0] = <jobName>
                // names[1] = <paramName>
                String[] names = PropertyFileValidator.validateLocalizationPropertyName(propKey, locale, jobDefsMap);
                
                // validate the property-key
                if (propValue == null || propValue.equals("")) {
                    throw new PropertyFileValidationException("Property-value '"+propValue+"' defined in file '"+PROPERTY_FILE_PREFIX+locale.toString()+"' is invalid (it is empty)");
                }   
                
                // store the key, value pair according to the info of the names[]
                JobDefinition jobDef = jobDefsMap.get(names[0]);
                if (names[1] == null) {
                    // no parameter property --> we have a property for the job itself
                    HashMap<String, HashMap<String, String>> localeMap = jobDef.getLocalizationInfoMap();
                    if (localeMap == null) {
                        localeMap = new HashMap<String, HashMap<String, String>>();
                        jobDef.setLocalizationInfoMap(localeMap);
                    }
                    LocalizationHelper.addPropertyToLocale(localeMap, locale, propKey, propValue);
                } else {
                    // we have a parameter key, value pair
                    JobParameterDefinition jobParamDef = jobDef.getParameter(names[1]);
                    HashMap<String, HashMap<String, String>> localeMap = jobParamDef.getLocalizationInfoMap();
                    if (localeMap == null) {
                        localeMap = new HashMap<String, HashMap<String, String>>();
                        jobParamDef.setLocalizationInfoMap(localeMap);
                    }
                    LocalizationHelper.addPropertyToLocale(localeMap, locale, propKey, propValue);
                }                
            } // inner for           
        } // outer for
    }
        
    
    /**
     * Read file from InputStream
     * 
     */
    private byte[] readFileFromStream(InputStream is) throws IOException {

        byte[] tmpBuf = new byte[2048];
        byte[] fileContents = null;
        int length = 0;
        
        while((length = is.read(tmpBuf, 0, tmpBuf.length)) != -1) {
            
            if (fileContents == null) {
                fileContents = new byte[length];
                System.arraycopy(tmpBuf, 0, fileContents, 0, length);
            } else {
                byte[] oldContents = fileContents;
                int oldlength = oldContents.length;
                fileContents = new byte[oldlength+length];
                System.arraycopy(oldContents, 0, fileContents, 0, oldlength);
                System.arraycopy(tmpBuf, 0, fileContents, oldlength, length);
            }
        }
        return fileContents;
    }
    
    
    HashMap<JobDefinitionName, JobDefinition> parseJobDefintion(SAXParser parser, byte[] jobDefintion, String applicationName) {
        ByteArrayInputStream is = new ByteArrayInputStream(jobDefintion);
        JobParameterDefinitionContentHandler jdch = new JobParameterDefinitionContentHandler();
        jdch.setApplicationName(applicationName);
        
        try {
            parser.parse(is, jdch);
        } catch (IOException io) {
            category.logThrowableT(Severity.ERROR, location, "Error parsing job-definition.xml file.", io);
            throw new IllegalArgumentException("Error parsing job-definition.xml file.\r\n" + io.getMessage());
        } catch (SAXException se) {
            category.logThrowableT(Severity.ERROR, location, "Error parsing job-definition.xml file.", se);
			      throw new IllegalArgumentException("Error parsing job-definition.xml file.\r\n" + se.getMessage());
        }
        return jdch.jobDefinitions;
    }
    
    public SAXParser initParser() {

        try {
            SAXParserFactory factory;

            factory = SAXParserFactory.newInstance();
            factory.setValidating(false);            
            // must be supported
            factory.setFeature("http://xml.org/sax/features/namespaces", true);
            
            SAXParser parser = factory.newSAXParser();
            
            return parser;

        } catch (SAXNotSupportedException sne) {
            category.logThrowableT(Severity.ERROR, location, "SAX parser does not support feature: \"" + "http://xml.org/sax/features/namespaces" + "\".", sne);            
            return null;
        } catch (SAXNotRecognizedException sne) {
            category.logThrowableT(Severity.ERROR, location, "SAX parser does not recognize feature: \"" + "http://xml.org/sax/features/namespaces" + "\".", sne);
            return null;
        } catch (ParserConfigurationException pe) {
            category.logThrowableT(Severity.ERROR, location, "Error configuring SAX parser.", pe);
            return null;
        } catch (SAXException se)  {
            category.logThrowableT(Severity.ERROR, location, "Error configuring SAX parser.", se);
            return null;            
        }
    }
    
        
    private void addAll(HashMap<JobDefinitionName,JobDefinition> target, 
            HashMap<JobDefinitionName,JobDefinition> source)
                                                 throws ConfigurationParserException {
        
        for(JobDefinitionName name : source.keySet()) {
            if (target.containsKey(name)) {
                throw new ConfigurationParserException("Job with definition name \"" + name + "\" defined more than once in application.");
            }
            target.put(name, source.get(name));
        }
    }    
    
    private Locale getLocale(String entryName) {
        
        // entry name if of the form
        //
        //           meta-inf/job-definition[...].properties 
        //
        // which has already been validated
        
        String localeInformation = entryName.substring(PROPERTY_FILE_PREFIX.length());
        localeInformation = localeInformation.substring(0, localeInformation.length() - PROPERTY_FILE_POSTFIX.length());
        
        if (location.beDebug()) {
            location.debugT("Locale information in propety file name is: " + localeInformation);
        }

        // try to match language
        //
        Matcher mt = patternLanguage.matcher(localeInformation);
        if (mt.matches()) {
            
            return new Locale(mt.group(1));
        }

        // try to match language and country
        //
        mt = patternLanguageCountry.matcher(localeInformation);
        if (mt.matches()) {

            return new Locale(mt.group(1), mt.group(2));
        }
        
        // unable to match anything
        //
        return null;
    }
}
