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
package com.sap.engine.services.scheduler.utils;

import java.util.ArrayList;
import java.util.Date;

import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.SchedulerID;


public class JobFilterHelper {

    static class StringHolder {
        
        private char[] characterString;
        private int position;
        
        StringHolder(String value, int position) {
            characterString = value.toCharArray();
            this.position = position;
        }
        
        void trim() {
            
            while (characterString.length > position && Character.isWhitespace(characterString[position])) {
                position++;
            }
        }
        
        boolean isEmpty() {
            return position == characterString.length;
        }

        char nextCharacter() {
            
            return characterString[position++];
        }

        char currentCharacter() {
            
            return characterString[position];
        }

        boolean isLetter() {
            return Character.isLetter(characterString[position]);
        }
    }
    
    private static final String STD_ERROR_STRING = "Job filter string must be of format 'name=\"value\", name1=\"value1\"'.";
    
    public static JobFilter parseString(String originalFilterString) 
                                                 throws IllegalArgumentException {
        if (originalFilterString == null || "".equals(originalFilterString)) {
            return new JobFilter();
        }
        
        ArrayList<String[]> values = new ArrayList<String[]>();
        
        StringHolder filterString = new StringHolder(originalFilterString, 0);

        readNextPart(filterString, values, originalFilterString);

        while (!filterString.isEmpty()) {
            
            if (filterString.nextCharacter() != ',') {
                throw new IllegalArgumentException(STD_ERROR_STRING);
            }
            readNextPart(filterString, values, originalFilterString);
        }
        
        return getJobFilter(values);
    }

    
    private static JobFilter getJobFilter(ArrayList<String[]> values)  
                                                  throws IllegalArgumentException {
        
        JobFilter f = new JobFilter();
        
        for (String[] nameValue : values) {
            String name = nameValue[0];
            String value = nameValue[1];
            
            if ("StartedFrom".equals(name)) {
                
                f.setStartedFrom(new Date(Long.parseLong(value)));
            } else if ("StartedTo".equals(name)) {
                
                f.setStartedTo(new Date(Long.parseLong(value)));
            } else if ("EndedFrom".equals(name)) {

                f.setEndedFrom(new Date(Long.parseLong(value)));
            } else if ("EndedTo".equals(name)) {

                f.setEndedTo(new Date(Long.parseLong(value)));
            } else if ("JobStatus".equals(name)) {
                
                f.setJobStatus(JobStatus.valueOf(value));
            } else if ("VendorData".equals(name)) {

                f.setVendorData(value);
            } else if ("JobDefinitionID".equals(name)) {

                f.setJobDefinitionId(JobDefinitionID.parseID(value));
            } else if ("ParentID".equals(name)) {

                f.setParentId(JobID.parseID(value));
            } else if ("JobID".equals(name)) {

                f.setJobId(JobID.parseID(value));
            } else if ("SchedulerID".equals(name)) {

                f.setScheduler(SchedulerID.parseID(value));
            } else if ("ReturnCode".equals(name)) {

                f.setReturnCode(Short.parseShort(value));
            } else if ("Node".equals(name)) {

                f.setNode(value);
            } else if ("UserID".equals(name)) {

                f.setUserId(value);
            } else if ("Name".equals(name)) {

                f.setName(value);
            } else if ("SchedulerTaskID".equals(name)) {

                f.setSchedulerTaskId(SchedulerTaskID.parseID(value));
            } else {
                throw new IllegalArgumentException("Name \"" + name + "\" not supported in job filter string.");
            }
        }
        
        return f;
    }
    
    private static void readNextPart(StringHolder filterString, ArrayList<String[]> values, String originalFilterString) throws IllegalArgumentException {
        
        filterString.trim();
        String name = getName(filterString);
        
        if ("".equals(name)) {
            throw new IllegalArgumentException(STD_ERROR_STRING);
        }

        filterString.trim();
        
        emptyCheck(filterString);
        
        if (filterString.nextCharacter() != '=') {
            throw new IllegalArgumentException(STD_ERROR_STRING);
        }
        
        filterString.trim();
        
        String value = getValue(filterString);
        values.add(new String[] {name, value});
        filterString.trim();
    }
    
    private static String getName(StringHolder tmpFilter) {
        
        StringBuffer name = new StringBuffer();

        while(!tmpFilter.isEmpty() && tmpFilter.isLetter()) {
            name.append(tmpFilter.nextCharacter());
        }
        return name.toString();
    }
    
    private static String getValue(StringHolder tmpFilter)  
                                    throws IllegalArgumentException {
        
        StringBuffer valueb = new StringBuffer();
        emptyCheck(tmpFilter);
        if (tmpFilter.nextCharacter() != '"') {
            throw new IllegalArgumentException("Job filter string must be of format 'name=\"value\", name1=\"value1\"'.");
        }

        char c = 'a'; // dummy initialization
        while (!tmpFilter.isEmpty() && ((c = tmpFilter.nextCharacter()) != '"')) {
            valueb.append(c);
        }

        if (c != '"') {
            throw new IllegalArgumentException("Job filter string must be of format 'name=\"value\", name1=\"value1\"'.");
        }
        return valueb.toString();
    }
    
    private static void emptyCheck(StringHolder str)
                               throws IllegalArgumentException {
        if (str.isEmpty()) {
            throw new IllegalArgumentException("Job filter string must be of format 'name=\"value\", name1=\"value1\"'.");
        }
    }
    
    
    public static String toString(JobFilter filter) {
        StringBuffer buf = new StringBuffer();
        boolean separator = false;
                
        if (filter.getStartedFrom() != null) {
            appendSeparator(buf, separator);
            buf.append("StartedFrom=\"").append(filter.getStartedFrom().getTime()).append("\"");
            separator = true;
        }
        
        if (filter.getStartedTo() != null) {
            appendSeparator(buf, separator);
            buf.append("StartedTo=\"").append(filter.getStartedTo().getTime()).append("\"");
            separator = true;
        }

        if (filter.getEndedFrom() != null) {
            appendSeparator(buf, separator);
            buf.append("EndedFrom=\"").append(filter.getEndedFrom().getTime()).append("\"");
            separator = true;
        }

        if (filter.getEndedTo() != null) {
            appendSeparator(buf, separator);
            buf.append("EndedTo=\"").append(filter.getEndedTo().getTime()).append("\"");
            separator = true;
        }
        
        if (filter.getJobStatus() != null) {
            appendSeparator(buf, separator);
            buf.append("JobStatus=\"").append(filter.getJobStatus().toString()).append("\"");
            separator = true;
        }

        if (filter.getVendorData() != null) {
            appendSeparator(buf, separator);
            buf.append("VendorData=\"").append(filter.getVendorData()).append("\"");
            separator = true;
        }

        if (filter.getJobDefinitionId() != null) {
            appendSeparator(buf, separator);
            buf.append("JobDefinitionID=\"").append(filter.getJobDefinitionId()).append("\"");
            separator = true;
        }

        if (filter.getParentId() != null) {
            appendSeparator(buf, separator);
            buf.append("ParentID=\"").append(filter.getParentId()).append("\"");
            separator = true;
        }

        if (filter.getJobId() != null) {
            appendSeparator(buf, separator);
            buf.append("JobID=\"").append(filter.getJobId()).append("\"");
            separator = true;
        }

        if (filter.getScheduler() != null) {
            appendSeparator(buf, separator);
            buf.append("SchedulerID=\"").append(filter.getScheduler()).append("\"");
            separator = true;
        }

        if (filter.getReturnCode() != null) {
            appendSeparator(buf, separator);
            buf.append("ReturnCode=\"").append(filter.getReturnCode()).append("\"");
            separator = true;
        }
        
        if (filter.getNode() != null) {
            appendSeparator(buf, separator);
            buf.append("Node=\"").append(filter.getNode()).append("\"");
            separator = true;
        }

        if (filter.getUserId() != null) {
            appendSeparator(buf, separator);
            buf.append("UserID=\"").append(filter.getUserId()).append("\"");
            separator = true;
        }
        
        if (filter.getSchedulerTaskId() != null) {
            appendSeparator(buf, separator);
            buf.append("SchedulerTaskID=\"").append(filter.getSchedulerTaskId().toString()).append("\"");
            separator = true;
        }
        
        return buf.toString();
    }
    
    private static void appendSeparator(StringBuffer buf, boolean append) {
        if (append) {
            buf.append(",");
        }
    }
}
