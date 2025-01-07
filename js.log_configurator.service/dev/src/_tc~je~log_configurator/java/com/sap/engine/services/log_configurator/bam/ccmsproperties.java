/*
 * Created on 2006-1-13
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.log_configurator.bam;

import com.sap.bc.proj.jstartup.JStartupFramework;
import com.sap.engine.frame.cluster.ClusterElement;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import com.sap.tc.logging.Location;

/**
 * @author IVAN-MI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CCMSProperties {

  private static final String GENERATION_FLAG_KEY = "CCMSTemplateGenerationFlag";
  private static final String GENERATE_FOR_FILE_KEY = "CCMSGenerateTemplateForFilename";
  private static final String DO_NOT_GENERATE_FOR_FILE_KEY = "CCMSDoNotGenerateTemplateForFilename";

  private static final Location logger = Location.getLocation(CCMSProperties.class);

  private static CCMSProperties instance = null;

  private String templateOutputDir = null;
  private String instanceNumber = "";
  private String systemName = "";
  private int clusterNodeID = 0;
  private String clusterNodeName = "";

  private boolean generationOn = false;
  private List listCCMSIncludeFileName = null;
  private List listCCMSExcludeFileName = null;


  private CCMSProperties(ClusterElement currNode, Properties properties) {
    clusterNodeID = currNode.getClusterId();
    clusterNodeName = currNode.getName();
    systemName = System.getProperty("SAPSYSTEMNAME", "Unknown");
    instanceNumber = System.getProperty("SAPSYSTEM", "Unknown");

    if(properties != null) {
      String CCMSTemplateGenerationFlag = (String) properties.get(GENERATION_FLAG_KEY);
      generationOn = (CCMSTemplateGenerationFlag != null && CCMSTemplateGenerationFlag.equals("1"));

      String includeStr = (String) properties.get(GENERATE_FOR_FILE_KEY);
      if(includeStr != null && includeStr.length() > 0) {
        listCCMSIncludeFileName = parseProperties(includeStr);
      } else {
        listCCMSIncludeFileName = null;
      }

      String excludeStr = (String) properties.get(DO_NOT_GENERATE_FOR_FILE_KEY);
      if(excludeStr != null && excludeStr.length() > 0) {
        listCCMSExcludeFileName = parseProperties(excludeStr);
      } else {
        listCCMSExcludeFileName = null;
      }
    } else {
      generationOn = false;
      logger.warningT("Properties for Log Configurator service not found!");
    }
  }


  public static void init(ClusterElement currNode, Properties properties) {
    instance = new CCMSProperties(currNode, properties);
  }


  public static CCMSProperties getProperties() {
    return instance;
  }


  public String getTemplateDestination() {
    if(templateOutputDir == null) {
      String dsrRoot = null;
      try {
        dsrRoot = JStartupFramework.getParam("DIR_CCMS"); 
        //dsrRoot = "C:\\usr\\sap\\CCMS";
      } catch(Exception ex) {
        logger.warningT("There was a linking problem in the JStartupFrame.getParam().");
      }

      if(dsrRoot != null && !(dsrRoot.equals(""))) {
        templateOutputDir = dsrRoot;
      } else {
        templateOutputDir = ".";
      }
      if(!templateOutputDir.endsWith(File.separator)) {
        templateOutputDir += File.separator;
      }
      templateOutputDir += systemName + "_" + instanceNumber + File.separator + "logmon";

      File destination = new File(templateOutputDir);

      // if the destination does not exist, create it:
      if(!destination.exists()) {
        destination.mkdir();
      }
    }
    return templateOutputDir;
  }


  public boolean isGenerationON() {
    return generationOn;
  }


  /**
   *                                               ExcludeList has any values?
   *                                                          |
   *                     _________________YES_________________|________________NO_______________________
   *       part of logfilename present?                                                          include list has any props?
   *                 |                                                                                       |
   *      __YES______|__NO__                                                          _________YES___________|______NO_____
   *  1. return          2.return                                      part of logfilename present?                   3. return
   *   false               true                                                     |                                    true
   *                                                                _____YES________|______NO___
   *                                                           4.return                     5.return
   *                                                             true                         false
   */
  public boolean shouldGenerateCCMSTemplate(String absolutePath) {
    if(isGenerationON()) {
      if(listCCMSExcludeFileName != null && listCCMSExcludeFileName.size() > 0) {
        for(Iterator Excludeiter = listCCMSExcludeFileName.iterator(); Excludeiter.hasNext();) {
          String property = (String) Excludeiter.next();
          if(absolutePath.indexOf(property) != -1) {
            return false;
          }
        }
      }

      if(listCCMSIncludeFileName != null && listCCMSIncludeFileName.size() > 0) {
        for(Iterator Includeiter = listCCMSIncludeFileName.iterator(); Includeiter.hasNext();) {
          String property = (String) Includeiter.next();
          if(absolutePath.indexOf(property) != -1) {
            return true;
          }
        }
        return false;
      } else {
        return true;
      }
    } else {
      return false;
    }
  }


  public String getNodeInstance() {
    return instanceNumber;
  }


  public String getSystemName() {
    return systemName;
  }


  public int getClusterNodeID() {
    return clusterNodeID;
  }


  public String getClusterNodeName() {
    return clusterNodeName;
  }


  private List parseProperties(String props) {
    final String tokenSeparator = ";";
    int i = 0;
    StringTokenizer st = new StringTokenizer(props, tokenSeparator);
    List properties = new ArrayList();
    while(st.hasMoreTokens()) {
      properties.add(st.nextToken());
    }
    return properties;
  }
}
