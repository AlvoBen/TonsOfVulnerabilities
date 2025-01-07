/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduleradapter.jobdeploy;

import java.util.ArrayList;
import java.util.HashMap;

import com.sap.tc.logging.Location;

/**
 * Helper class for parsing job scheduler message selectory
 * 
 * @author Dirk Marwinski
 *
 */
public class MessageSelectorParser {

    private final static Location location = Location
                      .getLocation(MessageSelectorParser.class);

    public static final String JOB_DEFINITION = "JobDefinition";
    public static final String APPLICATION_NAME = "ApplicationName";
    public static final String JOB_ID = "job-id";
    

    /**
     * Returns the parsed contents of the message selector. It has been
     * validated.
     * @param messageSelector 
     * @return
     */
    static HashMap<String,String> parseMessageSelector(String messageSelector, String ejbName)
                                                                      throws IllegalMessageSelector {

        // the format of message selectors is specified as follows:
        //
        //   JobDefinition = 'XXX' [ AND ApplicationName = 'YYY' ]
        // 
        // where XXX is [A-Z][A-Z0-9_]*
        //

        ArrayList<String[]> selectors = new ArrayList<String[]>();
        String selector = messageSelector;

        selector = selector.trim();
        selector = readNextSelectorPart(selector, messageSelector, selectors);

        while (selector.length() > 0) {

            selector = selector.trim();
            // tailing spaces
            //
            if (selector.length() == 0) {
                break;
            }

            if (selector.length() > 1
                    && "AND".equalsIgnoreCase(selector.substring(0, 3))) {
                // there is another entry
                //
                selector = selector.substring(3);
                selector = selector.trim();
                selector = readNextSelectorPart(selector, messageSelector,
                        selectors);
            } else {
                throw new IllegalMessageSelector(
                        "Error parsing message selector \"" + messageSelector
                                + "\". \"OR\" expected.");
            }
        }
        
        // validate that every entry occurs at least once
        //
        HashMap<String,String> val = new HashMap<String,String>();
        for (String[] sel : selectors) {
            if (val.containsKey(sel[0])) {
                throw new IllegalMessageSelector("Messge selector for ejb \"" + ejbName 
                        + "\" does not conform to the requirements for jobs: \"" + messageSelector + "\".");
            }
            val.put(sel[0], sel[1]);
        } 
        
        // validate that there is at least one "JobDefinition" part
        //
        if (!val.containsKey(JOB_DEFINITION)) {
            throw new IllegalMessageSelector("Messge selector for ejb \"" 
                    + ejbName + "\" does not conform to the requirements for jobs (it does not contain a 'JobDefinition =' clause): \"" + messageSelector + "\".");
        }        
        return val;
    }

    private static String readNextSelectorPart(String selector, String origSelector,
                                      ArrayList<String[]> selectors)
                                              throws IllegalMessageSelector {

        if (location.beDebug()) {
            location.debugT("readNextSelectorPart: parsing selector \"" +
                    selector + "\".");
        }
        
        String selectorName;
        String selectorValue;
        
        if (selector.startsWith(JOB_DEFINITION)) {

            selector = selector.substring(JOB_DEFINITION.length());
            selector = selector.trim();
            selectorName = JOB_DEFINITION;
            
        } else if (selector.startsWith(APPLICATION_NAME)) {
            
            selector = selector.substring(APPLICATION_NAME.length());
            selector = selector.trim();
            selectorName = APPLICATION_NAME;
            
        } else {
            throw new IllegalMessageSelector("Message selector \""
                    + origSelector
                    + "\" does not start with \""+JOB_DEFINITION+"\" or \""
                    + APPLICATION_NAME + "\".");
        }
        
        // "=" expected
        if (selector.charAt(0) != '=') {
            throw new IllegalMessageSelector("Message selector \""
                    + origSelector
                    + "\" does not start with \""+JOB_DEFINITION+"=\" or "
                    + "\"" + APPLICATION_NAME + "=\".");
        }
        
        selector = selector.substring(1);
        selector = selector.trim();
        
        // "'" expected
        if (selector.charAt(0) != '\'') {
            throw new IllegalMessageSelector("Message selector \""
                    + origSelector
                    + "\" does not start with \""+JOB_DEFINITION+"='\" or "
                    + "\"" + APPLICATION_NAME + "='\".");
        }
        selector = selector.substring(1);
        selector = selector.trim();
        
        selectorValue = readJobName(selector);
        // name validation - alowed length is 230 chars
        String result;
        if (JOB_DEFINITION.equals(selectorName) 
                &&  !(result = Validator.validateName(selectorValue, 230)).equals(Validator.VALID) ) {
            throw new IllegalMessageSelector("Message selector \""
                     + origSelector
                     + "\" has illegal name : " + result + ")");
        }
        
        selectors.add(new String[] {selectorName, selectorValue});

        selector = selector.substring(selectorValue.length());

        if (selector.length() > 0 && selector.charAt(0) == '\'') {
            selector = selector.substring(1);
        } else {
            throw new IllegalMessageSelector(
                    "Job Definition name in message selector \"" + origSelector
                            + "\" not terminated by \"'\".");
        }

        return selector;
    }

    private static String readJobName(String name) {

        int pos = 0;
        String result = "";
        while (pos < name.length() && name.charAt(pos) != '\'') {
            result += name.charAt(pos);
            pos++;
        }
        return result;
    }    
}
