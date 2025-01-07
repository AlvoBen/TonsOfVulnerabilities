/*
 *  last change 2004-03-19
 */

/**
 * @author Bernhard Drabant
 * 
 */
package com.sap.util.monitor.grmg;

import com.sapportals.utilities.analyzer.interfaces.IPluginAnalyzer;
import com.sapportals.utilities.analyzer.interfaces.IPluginAction;
import com.sapportals.utilities.analyzer.interfaces.IResult;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.util.*;
import java.io.*;

// dynamic object adapter from adaptee "ScenarioPanel" to target "IPluginAnalyzer"
// each adapter has to be extended from PluginAdapter to a class 
// PluginAdapter<name_of_scenarioPanel>, and be put to the same package

public class PluginAdapter implements IPluginAnalyzer {

  private ScenarioPanel m_panel = null;
  private Document m_doc = null;
  private Properties m_XMLProperties;
  private ScenarioDataLoader dataLoader = null;

  public PluginAdapter() {

    String testName = "";
    String deviceName = "";
    String adapterName = this.getClass().getName();

    if (adapterName.length() - "PluginAdapter".length() > 0) {
      testName = adapterName.substring(adapterName.length() - "PluginAdapter".length());
      deviceName = adapterName.substring(0, adapterName.length() - "PluginAdapter".length());
    }

    if (testName.equals("PluginAdapter") & !deviceName.endsWith(".")) {
      try {
        dataLoader = new ScenarioDataLoader(deviceName);
        m_XMLProperties = dataLoader.getGrmgProperties();
        m_doc = dataLoader.getGrmgXmlDocument();

        /*
         if(com.sap.util.monitor.grmg.ScenarioDevice.class.isAssignableFrom(dataContainer.getClass())){            
          m_device = dataContainer.getScenarioDevice();
         }           	
         if(com.sap.util.monitor.grmg.ScenarioPanel.class.isAssignableFrom(dataContainer.getClass())){      	
          m_panel = dataContainer.getScenarioPanel();
         }
        */

        // all scenario devices are wrapped into a panel
        m_panel = dataLoader.getScenarioPanel();
      } catch (IOException e) {
        debug(e.getMessage());
      } catch (ClassNotFoundException e) {
        debug(e.getMessage());
      } catch (ParserConfigurationException e) {
        debug(e.getMessage());
      } catch (SAXException e) {
        debug(e.getMessage());
      } catch (InstantiationException e) {
        debug(e.getMessage());
      } catch (IllegalAccessException e) {
        debug(e.getMessage());
      } catch (ScenarioPanelException e) {
        debug(e.getMessage());
      }
    }
  }

  public void analyze(IPluginAction e) {
    if (dataLoader != null) {
      // tbd !!!!!!!!!!!!!!!!!  	
    }
  }

  public void fix(IResult res) {
  }

  public Vector getColumnNames() {
    return null;
  }

  public String getDescription() {
    String scenarioDescription = m_XMLProperties.getProperty("scendesc");
    NodeList nlist = m_doc.getElementsByTagName("compdesc");

    for (int j = 0; j < nlist.getLength(); j += 1) {
      scenarioDescription += " | " + nlist.item(j).getNodeValue();
    }
    
    return scenarioDescription;
  }

  private void debug(String s) {
    //add logging here
  }

  private void log(String s) {
    //add logging here
  }

  private void log(String s, Exception e) {
    //add logging here
  }

}
