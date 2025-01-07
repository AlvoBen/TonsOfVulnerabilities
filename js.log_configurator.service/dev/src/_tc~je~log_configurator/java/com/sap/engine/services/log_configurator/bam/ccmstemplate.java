package com.sap.engine.services.log_configurator.bam;

import java.io.*;
import java.net.*;
import java.util.*;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.lib.logging.descriptors.LogDestinationDescriptor;
import com.sap.tc.logging.FileLog;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.interfaces.IFileLog;

/**
 * @author Manish Garg (I803727)
 * @created Apr 18, 2003
 * 
 * This file represents the CCMS template, which is picked up by the CCMS Agent. These template file contains the definition of the
 * Log Files that will be picked up by the agent for monitoring.
 */
public class CCMSTemplate {

  private static final Location logger = Location.getLocation(CCMSTemplate.class);
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private static CCMSTemplate instance = null;

  private String hostname;
  private String mNodeType;
  private String dirForTemplates;


  public static void init(ClusterElement currentNode, Properties properties) {
    if(currentNode != null) {
      instance = new CCMSTemplate(currentNode, properties);
    }
  }


  public static CCMSTemplate getCCMSTemplate() {
    return instance;
  }


  private CCMSTemplate(ClusterElement currentNode, Properties properties) {
    CCMSProperties.init(currentNode, properties);
    CCMSProperties ccmsProperties = CCMSProperties.getProperties();

    if(hostname == null) {
      InetAddress localAddress = null;
      try {
        localAddress = InetAddress.getLocalHost();
        hostname = localAddress.getHostName();
      } catch(UnknownHostException e) {
        logger.traceThrowableT(Severity.ERROR, "CCMSTemplate creation: cannot get host name", e);
        hostname = "unknown";
      }
    }
    dirForTemplates = ccmsProperties.getTemplateDestination();
  }


  public void writeTemplate(LogDestinationDescriptor log) {
    if(log == null) {
      return;
    }
    CCMSProperties ccmsProperties = CCMSProperties.getProperties();

    boolean isMultipleFiles = false;
    boolean generateTemplate = false;
    String offset = "";
    File logFile = null;

    String logPattern = log.getPattern();
    if(logPattern == null) {
      logger.errorT("CCMSTemplate generation for log failed: pattern is NULL!");
      return;
    }

    String absolutePath = null;

    IFileLog logFileLog = new FileLog(logPattern);
    String logDirName = logFileLog.getParent();

    // The pattern returned by logging contains / (hard coded in logging). There fore / and file separator are both checked
    // and trated as potential file path separator,
    String logName = logPattern.substring(logPattern.lastIndexOf(File.separator) + 1);
    logName = logName.substring(logName.lastIndexOf("/") + 1);

    // replace the %g, %t, etc with *
    char[] newChars = replacePatternsWithwildChars(logName);
    logName = new String(newChars).trim();

    isMultipleFiles = (log.getCount() > 1);

    absolutePath = (new File(logDirName, logName)).getAbsolutePath();
    logFile = new File(absolutePath);

    offset = getOffset(logDirName);
    
    if(ccmsProperties.isGenerationON()) {
//    If the flag is set to generate the ccms files, then generate ccmstemplate.
      generateTemplate = ccmsProperties.shouldGenerateCCMSTemplate(absolutePath);      
    }

    String monitorContext = getMonitorContext(offset);

    String tempFileName = logFile.getName();
    String filenameWithoutExtension = tempFileName.lastIndexOf(".") != -1 ? tempFileName.substring(0, tempFileName.lastIndexOf(".")) : tempFileName;
    String filename = dirForTemplates + File.separator + hostname + "_" + ccmsProperties.getSystemName() + "_" + ccmsProperties.getNodeInstance() + "_" + mNodeType + "_" + (offset.length() > 0 ? offset + "_" : "") + filenameWithoutExtension + "_" + "logmon" + ".ini";

    File templateFile = new File(filename);

    //Delete the template file, when it is exists and the template generation flag is set to false (0)
    if(templateFile.exists()) {
      if(!generateTemplate) {
        templateFile.delete();
      }
    } else if(generateTemplate && logFile != null) {
      OutputStream outStream = null;

      Properties templateData = new Properties();
      templateData.put("MONITOR_CONTEXT", "\"" + monitorContext + "\"");

      String templateFileName = logFile.getName();

      // the format of multiple files is logname.0.trc, logname.1.trc. Therefore the 0, 1, should be replaced by *
      if(isMultipleFiles) {
        StringBuffer sbFileName = new StringBuffer(templateFileName);
        int lastDotIndex = templateFileName.lastIndexOf('.');
        if(lastDotIndex > 0) {
          sbFileName.insert(lastDotIndex, ".*"); // insert a * before the last .
          templateFileName = sbFileName.toString();
        }
      }

      templateData.put("FILENAME", "\"" + templateFileName + "\"");
      templateData.put("DIRECTORY", "\"" + logFile.getParent() + "\"");

      String mteClass = "Logs";
      if(offset != null && !offset.equals("")) {
        mteClass = mteClass + "_" + offset;
      }
      templateData.put("MTE_CLASS", "\"" + mteClass + "\"");

      // e.g. of multiple files: Rotating files in Logging. Put additional information for the rotating files.
      if(isMultipleFiles) {
        templateData.put("MONITORNEWESTFILE", new String("1"));
        templateData.put("MONITOR_LAST_FILEMODIF", new String("1"));
      }

      try {
        //create the folder if does not exist.
        File ff = templateFile.getParentFile();
        if(!ff.exists()) {
          List lstF = new ArrayList();
          while(ff != null && !ff.exists()) {
            lstF.add(ff);
            ff = ff.getParentFile();
          }
          for(int f = lstF.size() - 1; f >= 0; f--) {
            ff = (File) lstF.get(f);
            ff.mkdir();
          }
        }
        outStream = new FileOutputStream(templateFile);

      } catch(FileNotFoundException flEx) {
        logger.warningT("INVALID_FILE" + flEx.getMessage());
        throw new RuntimeException("File not found");
      }

      Writer out = new BufferedWriter(new OutputStreamWriter(outStream));
      try {
        out.write("LOGFILE_TEMPLATE" + LINE_SEPARATOR);
        // Write out the properties.
        Iterator keyItr = templateData.keySet().iterator();
        while(keyItr.hasNext()) {
          String key = (String) keyItr.next();
          String value = (String) templateData.get(key);

          out.write(key + "=" + value + LINE_SEPARATOR);
          out.flush();
        }

        // Write out the pattern - value pairs.				
        out.write(LINE_SEPARATOR);

        out.write("PATTERN_0=\"Fatal\"");
        out.write(LINE_SEPARATOR);
        out.write("VALUE_0=\"RED\"");
        out.write(LINE_SEPARATOR);
        out.write("PATTERN_1=\"Error\"");
        out.write(LINE_SEPARATOR);
        out.write("VALUE_1=\"RED\"");
        out.write(LINE_SEPARATOR);
        out.write("PATTERN_2=\"Warning\"");
        out.write(LINE_SEPARATOR);
        out.write("VALUE_2=\"YELLOW\"");
        out.write(LINE_SEPARATOR);
        out.write("PATTERN_3=\"Info\"");
        out.write(LINE_SEPARATOR);
        out.write("VALUE_3=\"GREEN\"");
        out.write(LINE_SEPARATOR);
        out.write("PATTERN_4=\"Debug\"");
        out.write(LINE_SEPARATOR);
        out.write("VALUE_4=\"GREEN\"");
        out.write(LINE_SEPARATOR);
        out.write("." + LINE_SEPARATOR);
        out.flush();

      } catch(IOException ioEx) {
        logger.warningT("INVALID_FILE[" + filename + "]");
        throw new RuntimeException("Invalid file");
      } finally {
        try {
          out.close();
        } catch(IOException ioEx) {
          logger.warningT("INVALID_FILE");
          throw new RuntimeException("Invalid file");
        }
      }
    }
  }


  private char[] replacePatternsWithwildChars(String filename) {
    if(filename.indexOf('%') != -1) {
      char replacedChars[] = new char[filename.length()];
      for(int i = 0, newCharCounter = 0; i < filename.length(); i++) {
        // Replace patterns (%g, %t, etc witha  a wild character *)
        if(filename.charAt(i) == '%') {
          i++; // ignore the next char. 
        } else {
          replacedChars[newCharCounter++] = filename.charAt(i);
        }
      }
      return replacedChars;
    } else {
      return filename.toCharArray();
    }
  }


  /**
   * This method accesses the IDs by collecting them from the ConfigurationHandler.
   */
  private String getMonitorContext(String offset) {
    CCMSProperties ccmsProperties = CCMSProperties.getProperties();

    int clusterNodeID = ccmsProperties.getClusterNodeID();
    int nodeID = (clusterNodeID % 100);

    String clusterNodeName = ccmsProperties.getClusterNodeName();
    mNodeType = getResolvedNodeType(clusterNodeName, nodeID);

    //		MONITOR_CONTEXT = =<SID>_<hostname>_<NR>_<Sxx> <all_dirs_after_log_fill_to_39_Chars>
    String intermediatoryMonitorContext = ccmsProperties.getSystemName() + "_" + hostname + "_" + ccmsProperties.getNodeInstance() + "_" + mNodeType;
    int intermediatoryLength = intermediatoryMonitorContext.length();
    String usefulOffset = "";
    // 38 because currently (May 12, 2004 ) due to a  bug, it supports 39 chars and not 40. and we are appending a space too. (38 + 1 char)
    if(offset.length() > (38 - intermediatoryLength)) { // if appending the offset will make the length more than 40 chars.
    //					 take out the a chunk that will fill up the remaining space of 40 chars.
      usefulOffset = " " + offset.substring(0, 38 - intermediatoryLength);
      // take out, 39 - length of intermediatory from the offset   
    } else {
      usefulOffset = offset.length() > 0 ? " " + offset : "";
    }
    String monitorContext = intermediatoryMonitorContext + usefulOffset;

    // The MONITOR_CONTEXT cannot be more than 40 chars. In case it did happen to be more than 40 chars, reduce it to 40.
    if(monitorContext.length() > 40) {
      monitorContext = monitorContext.substring(monitorContext.length() - 40, monitorContext.length());
    }

    return monitorContext;
  }


  /**
   *   The nodeName contains a string like server0, dispatcher0, etc.. I have to pass back srv0, dsp0, etc..
   */
  private String getResolvedNodeType(String nodeName, int nodeID) {
    String ints = "";
    String resolvedType = "";
    if(nodeName.trim().toLowerCase().startsWith("server")) {
      ints = new Integer(nodeID - 50).toString(); // server is 6 chars long
      if(ints.length() == 1)
        resolvedType = ("S0" + ints);
      else
        resolvedType = ("S" + ints);
    } else {
      ints = new Integer(nodeID).toString();
      if(ints.equals("0")) {
        resolvedType = ("dsp");
      } else {
        resolvedType = ("dsp" + ints);
      }
    }
    return resolvedType;
  }


  /**
   * This method parses the filename that is passed to it and returns everything that is after the directory log.
   * This forms the offset part of the filename and the MONITOR_CONTEXT.
   * e.g. file passed in = /usr/sap/log/services/logviewer/default.trc
   * What is returned is /services/logviewer
   * 
   * @param fileName  filename to parsed. - usually the directory of the log.
   * @return everything after the directory /log
   * If /log not present, returns "" - Empty string.
   *  
   */
  private String getOffset(String dirname) {
    String offset = "";
    String newOffset = "";

    // go over the string log and the following file separator.
    if(dirname.indexOf("log" + File.separator) != -1) {
      offset = dirname.substring(dirname.lastIndexOf("log" + File.separator) + 4);
      newOffset = offset.replace(File.separatorChar, '_');
    }
    return newOffset;
  }
}
