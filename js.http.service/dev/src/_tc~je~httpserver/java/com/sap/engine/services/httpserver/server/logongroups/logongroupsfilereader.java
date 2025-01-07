/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server.logongroups;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.StringTokenizer;
import java.util.Vector;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import java.text.ParseException;
import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.server.Log;
import static com.sap.engine.services.httpserver.server.properties.HttpPropertiesImpl.GROUP_INFO_LOCATION;
import static com.sap.engine.services.httpserver.server.properties.HttpPropertiesImpl.URL_MAP_LOCATION;

/**
 * @author Violeta Uzunova
 *
 * Used to read the logon groups configuration from files. The location of
 * the files are specified by "GroupInfoLocation" and "UrlMapLocation"
 * service properties.
 * This configuration is stored in the DB after it is read.
 *
 * For the syntax of the both configuration files see
 * http://aiokeh.wdf.sap.corp:1080/SAPIKS2/contentShow.sap?_CLASS=IWB_EXTHLP&_LOIO=87252C4142AEF623E10000000A155106&&TMP_IWB_TASK=PREVIEW2&RELEASE=700&LANGUAGE=EN
 */
public class LogonGroupsFileReader {
  public static final double VERSION = 1.0;
  public static final String SEPARATOR = "&";
  public static final String PREFIX = "PREFIX";
  public static final String GROUP = "GROUP";
  public static final String CASE = "CASE";
  public static final String VHOST = "VHOST";
  public static final String STACK = "STACK";
  public static final String J2EE = "J2EE";

  private double version;
  // contains all the instances, defined in instances section
  // in icrgroups.txt file; used for checking of the correct syntax
  private Vector<String> allInstances;
  private LogonGroupsManager logonGroupsManager;
  private String logonGroupSeparator = "~";

  /**
   * Constructor
   *
   * @param groupInfoLocation   location for icrgroup.txt file
   * @param urlMapLocation      location for urlinfo.txt file
   */
  public LogonGroupsFileReader(LogonGroupsManager logonGroupsManager) {
    this.logonGroupsManager = logonGroupsManager;
    this.allInstances = new Vector<String>();
  }

  /**
   * Reads, parse and stores data from icrgroups.txt and urlInfo.txt files
   *
   * @param groupInfoLocation     icrgroups.txt
   * @param urlMapLocation        urlInfo.txt
   * @param logonGroupSeparator   ZoneSeparator
   * @throws Exception
   */
  public void parse(String groupInfoLocation, String urlMapLocation, String logonGroupSeparator) throws Exception {
    if ((groupInfoLocation == null || groupInfoLocation.equals(""))
        && (urlMapLocation == null || urlMapLocation.equals(""))) {
      // nothing set - OK
      return;
    }
    if (groupInfoLocation == null || groupInfoLocation.equals("")
        || urlMapLocation == null || urlMapLocation.equals("")) {
      Log.logWarning("ASJ.http.000060", 
    		        "Incomplete configuration for logon groups. One of the " +
    		        "configuration properties [{0}] or [{1}] " +
    		        "is not set. Please specify both of them.", 
    		        new Object[]{GROUP_INFO_LOCATION, URL_MAP_LOCATION}, null, null, null);
      return;
    }
    this.logonGroupSeparator = logonGroupSeparator;
    loadGroupInfoFromFile(groupInfoLocation);
    loadUrlMapFromFile(urlMapLocation);
  }

  // ======================== PRIVATE ========================================
  // methods for parsing the file icrgroups.txt

  /**
   * Reads and parse the icrgroups.txt file
   *
   * @param fileLocation              a file location
   * @throws IOException              on problems during reading the file from file system
   * @throws ParseException           on problems during parsing the file
   * @throws ConfigurationException   on problems during writing the data in the DB
   */
  private void loadGroupInfoFromFile(String fileLocation) throws IOException, ParseException, ConfigurationException {
    if (fileLocation == null) {
      throw new IOException("File location for logon groups config file, corresponding to \"" + GROUP_INFO_LOCATION + "\" service property.");
    }
    BufferedReader bufferedReader = null;
    try {
      bufferedReader = new BufferedReader(new FileReader(fileLocation));
      parseVersionLine(bufferedReader.readLine(), fileLocation);
      String nextLine = bufferedReader.readLine();

      while (parseInstance(nextLine, fileLocation)) {
        nextLine = bufferedReader.readLine();
      }

      if (!parseEmptyLine(nextLine, fileLocation)) {
        throw new ParseException("Illegal file format for logon groups config file [" + fileLocation + "]. " +
            "Expected empty line at the end, found [" + nextLine + "].", -1);
      }

      nextLine = bufferedReader.readLine();
      while (parseLogonGroup(nextLine, fileLocation)) {
        nextLine = bufferedReader.readLine();
      }
      nextLine = bufferedReader.readLine();
      if (nextLine != null) {
        throw new ParseException("Illegal file format for logon groups config file [" + fileLocation + "]. " +
            "Expected empty line at the end, found [" + nextLine + "].", -1);
      }
    } finally {
      bufferedReader.close();
    }
  }

  /**
   * Parse the current line from instances section form icrgroups.txt file. It should have syntax
   * &lt;instanceName&gt;\r\n
   *
   * @param nextLine          current line
   * @param fileLocation      configuration file (used for log only)
   * @return true if the file is correctly parsed, false if the end of the instance section is reached
   */
  private boolean parseInstance(String nextLine, String fileLocation) {
    if (nextLine == null) {
      return false;
    }
    nextLine = nextLine.trim();
    if (nextLine.length() == 0) {
      return false;
    }

    if (allInstances.contains(nextLine)) {
      if (LOCATION_HTTP.beInfo()) {
				LOCATION_HTTP.infoT("LogonGroupsFileReader.parseInstance(): Illegal file format for logon groups config file [" + fileLocation + "]. " +
						"Duplication of the instance [" + nextLine + "] ");
			}
    } else {
      allInstances.add(nextLine);
    }
    return true;
  }

  /**
   * Parse a line with mapping between logonGroup to instances from icrgroups.txt file. It should have syntax
   * &lt;groupName&gt; : &lt;instanceName1&gt;, &lt;instanceName2&gt; ... &lt;instanceNameN&gt;\r\n
   *
   * InstanceName = J2EE + DispatcherId
   * Zone in the Zone interface for instance should be added a groupId of this dispatcher
   *
   * @param nextLine                 current line
   * @param fileLocation             configuration file (used for log only)
   * @return true if the file is correctly parsed, false if the end of the instance section is reached
   * @throws ParseException          if the syntax is not correct
   * @throws ConfigurationException  on problems during writing the data in DB
   * @throws RemoteException
   */
  private boolean parseLogonGroup(String nextLine, String fileLocation) throws ParseException, ConfigurationException, RemoteException {
    if (nextLine == null) {
      return false;
    }
    nextLine = nextLine.trim();
    if (nextLine.length() == 0) {
      return false;
    }
    int sep = nextLine.indexOf(':');
    if (sep == -1) {
      throw new ParseException("Illegal file format for logon groups config file [" + fileLocation + "]. " +
              "Separator [:] not found on line [" + nextLine + "].", -1);
    }
    String logonGroupName = nextLine.substring(0, sep);
    logonGroupName = logonGroupName.trim();

    //definition of one logonGroup can be on several lines
    LogonGroup logonGroup = logonGroupsManager.getLogonGroup(logonGroupName);
    if (logonGroup == null) {
      logonGroup = ((LogonGroupsManager)logonGroupsManager).newLogonGroupRegistered(logonGroupName);
    }

    nextLine = nextLine.substring(sep + 1);
    StringTokenizer token = new StringTokenizer(nextLine, ",");
    while (token.hasMoreTokens()) {
      String nextInstance = token.nextToken().trim();
      if (!allInstances.contains(nextInstance)) {
        throw new ParseException("Illegal file format for logon groups config file [" + fileLocation + "]. " +
                "Instance [" + nextInstance + "] from logon group [" + logonGroupName + "] is not declared in instances section.", -1);
      }

      logonGroup.addInstance(nextInstance);
//      try {
//        nextInstance = nextInstance.substring("J2EE".length());  // only dispatcher id should be left
//        //nextInstance = nextInstance.substring(0, nextInstance.length() - 2); //groupId is the dispatcherId without last two digits
//        int groupId = getGroupId(new Integer(nextInstance).intValue()); // only group id should be left
//
//        if (groupId == -1) {
//          Log.logWarning("Illegal logon groups file format. Cannot find the group id for" +
//              " dispatcher [" + nextInstance + "] in instance line [J2EE" + nextInstance + "], file [" + fileLocation + "]", null);
//          continue;
//        }
//        zone.addInstance(groupId);
//      } catch (NumberFormatException e) {
//        Log.logWarning("Illegal logon groups file format. " +
//            "Instance [" + nextInstance + "] is not valid instance name. Instance name should be J2EE + DispatcherId(int value)" +
//            "File is: [" + fileLocation + "]", e, null);
//        continue;
//      } catch (IndexOutOfBoundsException e) {
//        Log.logWarning("Illegal logon groups file format. " +
//            "Instance [" + nextInstance + "] is not valid instance name. Instance name should be J2EE + DispatcherId(int value)" +
//            "File is: [" + fileLocation + "]", e, null);
//        continue;
//      }
    }
    return true;
  }

//  /**
//   * returns the instance name for the web dispatcher config response
//   * @param groupId
//   * @return J2EE + DispatcherId[0] for the groupId
//   */
//  private int getGroupId(int dispatcherId) {
//    return zoneManagement.getGroupId(dispatcherId);
//  }


  // ======================== PRIVATE ========================================
  // methods for parsing the file urlInfo.txt

  /**
   * Reads and parse the urlInfo.txt file
   *
   * @param fileLocation              a file location
   * @throws IOException              on problems during reading the file from file system
   * @throws ParseException           on problems during parsing the file
   * @throws ConfigurationException   on problems during writing the data in the DB
   */
  private void loadUrlMapFromFile(String fileLocation) throws IOException, ParseException, ConfigurationException {
    //todo - check that the same alias does not belong to two different groups
    if (fileLocation == null) {
      throw new IOException("File location for logon groups config file, corresponding to \"" + URL_MAP_LOCATION + "\" service property.");
    }
    BufferedReader bufferedReader = null;
    try {
      bufferedReader = new BufferedReader(new FileReader(fileLocation));
      parseVersionLine(bufferedReader.readLine(), fileLocation);
      String nextLine = bufferedReader.readLine();
      try {
        while (parseUrlInfo(nextLine, fileLocation)) {
          nextLine = bufferedReader.readLine();
        }
      } catch (ConfigurationException e) {
        Log.logError("ASJ.http.000127",  
          "Error during adding an alias to the logon group.", e, null, null, null);
      }
      nextLine = bufferedReader.readLine();
      if (nextLine != null) {
        throw new ParseException("Illegal file format for logon groups config file [" + fileLocation + "]. " +
            "Expected empty line at the end, found [" + nextLine + "].", -1);
      }
    } finally {
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    }
  }

  /**
   * Stores the data for the current line in the DB
   *
   * @param nextLine           current line
   * @param fileLocation       location of the configuration file (used for log only)
   * @return true if the file is correctly parsed, false if the end of the instance section is reached
   * @throws ParseException    on problems during parsing
   */
  private boolean parseUrlInfo(String nextLine, String fileLocation) throws ConfigurationException, ParseException {
    if (nextLine == null) {
      return false;
    }
    nextLine = nextLine.trim();
    if (nextLine.length() == 0) {
      return false;
    }

    AliasInfo aliasInfo = new AliasInfo();
    parseAliasInfo(nextLine, aliasInfo, fileLocation);
    LogonGroup logonGroup = logonGroupsManager.getLogonGroup(aliasInfo.getGroup());
    if (logonGroup == null) {
      throw new ParseException("Illegal alias info at line [" + nextLine + "] in file [" + fileLocation + "]. " +
          "Logon Group [" + aliasInfo.getGroup() + "] not specified.", -1);
    }
    if (aliasInfo.isExactAlias()) {
      logonGroup.addExactAlias(aliasInfo.getAliasName());
    } else {
      logonGroup.addAlias(aliasInfo.getAliasName());
    }
    return true;
  }

  /**
   * Parse a line describing the urls. It should have syntax
   * PREFIX=/isolde~za/&GROUP=za&CASE=&VHOST=*.*;&STACK=J2EE
   *
   * @param line              current line
   * @param aliasInfo         object representing the data from this line
   * @param fileLocation      location of the configuration file (used for log only)
   * @throws ParseException   if wrong syntax
   */
  public void parseAliasInfo(String line, AliasInfo aliasInfo, String fileLocation) throws ParseException {
    if (line == null || line.trim().length() == 0) {
      throw new ParseException("Illegal alias info line [" + line + "] in logon groups config file [" + fileLocation + "].", -1);
    }
    line = line.trim();
    StringTokenizer tokenizer = new StringTokenizer(line, SEPARATOR);
    if (!tokenizer.hasMoreTokens()) {
      throw new ParseException("Illegal alias info line [" + line + "] in logon " +
          "groups config file [" + fileLocation + "]. Alias name not found.", -1);
    }
    parsePrefix(tokenizer.nextToken(), aliasInfo, fileLocation);
    if (!tokenizer.hasMoreTokens()) {
      return;
    }
    String next = tokenizer.nextToken();
    if (parseGroup(next, aliasInfo)) {
      next = tokenizer.nextToken();
    }
    if (!tokenizer.hasMoreTokens()) {
      return;
    }
    if (parseCase(next)) {
      next = tokenizer.nextToken();
    }
    if (!tokenizer.hasMoreTokens()) {
      return;
    }
    if (parseVHost(next)) {
      next = tokenizer.nextToken();
    }
    if (!tokenizer.hasMoreTokens()) {
      return;
    }
    if (parseStack(next, aliasInfo, fileLocation)) {
      next = tokenizer.nextToken();
    }
    if (tokenizer.hasMoreTokens()) {
      throw new ParseException("Illegal alias info line [" + line + "] in logon groups config file [" + fileLocation + "]. " +
          "Unknown attribute [" + tokenizer.nextToken() + "]", -1);
    }
  }

  /**
   * Parse a section PREFIX=/isolde~zb/ from line describing the urls
   *
   * @param lineSection       line section
   * @param aliasInfo         object representing the data from this line
   * @throws ParseException   if wrong syntax
   */
  private void parsePrefix(String lineSection, AliasInfo aliasInfo, String fileLocation) throws ParseException {
    if (lineSection == null || lineSection.trim().length() == 0) {
      throw new ParseException("Illegal alias info prefix [" + lineSection + "] in logon groups config file [" + fileLocation + "].", -1);
    }
    lineSection = lineSection.trim();
    int sep = lineSection.indexOf('=');
    if (sep == -1) {
      throw new ParseException("Illegal alias info syntax [" + lineSection + "] in logon groups config file [" + fileLocation + "]," +
          " missing [=].", -1);
    }
    if (!PREFIX.equals(lineSection.substring(0, sep).trim())) {
      throw new ParseException("Illegal alias info syntax [" + lineSection + "]in logon groups config file [" + fileLocation + "], " +
          "missing [" + PREFIX + "].", -1);
    }

    String aliasName = lineSection.substring(sep + 1);
    aliasName = aliasName.trim();

    int logonGroupSeparatorIndex = aliasName.indexOf(logonGroupSeparator);
    if (logonGroupSeparatorIndex == -1) {
      aliasInfo.setIsExactAlias(true);
    } else {
      aliasName = aliasName.substring(0, logonGroupSeparatorIndex).trim();
      aliasInfo.setIsExactAlias(false);
    }
    aliasName = ParseUtils.convertAlias(aliasName);
    aliasName = ParseUtils.canonicalize(aliasName);
    if (aliasName.startsWith(ParseUtils.separator)) {
      aliasName = aliasName.substring(1);
    }
    if (aliasName.endsWith(ParseUtils.separator)) {
      aliasName = aliasName.substring(0, aliasName.length() - 2);
    }
    if (aliasName.equals("")) {
      aliasName = "/";
    }
    aliasInfo.setAliasName(aliasName);
  }

  /**
   * Parse a section GROUP=za from line describing the urls
   *
   * @param lineSection       line section
   * @param aliasInfo         object representing the data from this line
   */
  private boolean parseGroup(String lineSection, AliasInfo aliasInfo) {
    if (lineSection == null || lineSection.trim().length() == 0) {
      //todo - warning
      return true;
    }
    lineSection = lineSection.trim();
    int sep = lineSection.indexOf('=');
    if (sep == -1) {
      return lineSection.startsWith(GROUP);
    }
    String group = lineSection.substring(sep + 1);
    group = group.trim();
    lineSection = lineSection.substring(0, sep);
    lineSection = lineSection.trim();
    if (!GROUP.equals(lineSection)) {
      group = null;
      return false;
    }
    aliasInfo.setGroup(group);
    return true;
  }

  /**
   * Parse a section CASE= from line describing the urls. This parameter
   * is deprecated; The method checks only for correct syntax
   *
   * @param lineSection       line section
   */
  private boolean parseCase(String lineSection) {
    if (lineSection == null || lineSection.trim().length() == 0) {
      //todo - warning
      return true;
    }
    lineSection = lineSection.trim();
    int sep = lineSection.indexOf('=');
    if (sep == -1) {
      return lineSection.startsWith(CASE);
    }
    lineSection = lineSection.substring(0, sep);
    lineSection = lineSection.trim();
    if (!CASE.equals(lineSection)) {
      return false;
    }
    return true;
  }

  /**
   * Parse a section VHOST=*.* from line describing the urls. This parameter
   * is deprecated; The method checks only for correct syntax
   *
   * @param lineSection       line section
   */
  private boolean parseVHost(String lineSection) {
    if (lineSection == null || lineSection.trim().length() == 0) {
      //todo - warning
      return true;
    }
    lineSection = lineSection.trim();
    int sep = lineSection.indexOf('=');
    if (sep == -1) {
      return lineSection.startsWith(VHOST);
    }
    lineSection = lineSection.substring(0, sep);
    lineSection = lineSection.trim();
    if (!VHOST.equals(lineSection)) {
      return false;
    }
    return true;
  }

  /**
   * Parse a section TACK=J2EE from line describing the urls
   *
   * @param lineSection       line section
   * @param aliasInfo         object representing the data from this line
   */
  private boolean parseStack(String lineSection, AliasInfo aliasInfo, String fileLocation) throws ParseException {
    if (lineSection == null || lineSection.trim().length() == 0) {
      throw new ParseException("Illegal alias info syntax [" + lineSection + "]in logon groups config file [" + fileLocation + "], " +
          "missing [" + J2EE + "].", -1);
    }
    lineSection = lineSection.trim();
    int sep = lineSection.indexOf('=');
    if (sep == -1) {
      return false;
    }
    String stack = lineSection.substring(sep + 1);
    stack = stack.trim();
    // the default value is ABAP
    if (stack.length() == 0 || !stack.equals(J2EE)) {
      throw new ParseException("Illegal alias info syntax [" + lineSection + "]in logon groups config file [" + fileLocation + "], " +
          "missing [" + J2EE + "].", -1);
    }
    lineSection = lineSection.substring(0, sep);
    lineSection = lineSection.trim();
    if (!STACK.equals(lineSection)) {
      stack = J2EE;
      aliasInfo.setStack(stack);
      return false;
    }
    aliasInfo.setStack(stack);
    return true;
  }

  // ======================== PRIVATE ========================================
  // methods for parsing the file icrgroups.txt and urlInfo.txt

  /**
   * Parse the first line of the icrgroups.txt and urlinfo.txt files
   *
   * @param versionLine
   * @param fileLocation
   * @throws ParseException
   */
  private void parseVersionLine(String versionLine, String fileLocation)
      throws ParseException {
    if (versionLine == null) {
      throw new ParseException("Illegal file format for logon groups config file [" + fileLocation + "]. " +
          " Leading line [" + LogonGroupsManager.VERSION_LINE + "] not found, first line [null]." , -1);
    }
    versionLine = versionLine.trim();
    if (!LogonGroupsManager.VERSION_LINE.equals(versionLine)) {
      throw new ParseException("Illegal file format for logon groups config file [" + fileLocation + "]. " +
          "Leading line [" + LogonGroupsManager.VERSION_LINE + "] expected, found [" + versionLine + "].", -1);
    }
    version = VERSION;
  }

  /**
   * Parses the empty lines in icrgroups.txt and urlinfo.txt configuration files
   *
   * @return true if empty line is found, false if EOF is reached
   */
  private boolean parseEmptyLine(String emptyLine, String fileLocation) throws ParseException {
    if (emptyLine == null) {
      return false;
    }
    emptyLine = emptyLine.trim();
    if (emptyLine.length() > 0) {
      throw new ParseException("Illegal file format for logon groups config file [" + fileLocation + "]. " +
          "Empty line expected, found [" + emptyLine + "].", -1);
    }
    return true;
  }
}
