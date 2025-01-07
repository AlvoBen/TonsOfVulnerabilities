/*
 *  last change 2003-11-10
 */

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

package com.sap.util.monitor.grmg;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.util.ArrayList;
import java.io.*;


/**
 * @author Miroslav Petrov
 * @author Georgi Mihailov
 * @version 6.30
 */
public class GrmgCustomizingXmlGenerator {
  public static final String DEFAULT_OUTPUT_DIRECTORY = "../../grmg";
  public static final String DEFAULT_FILE_NAME = "grmg-customizing.xml";
  public static final String X_SIGN_STRING = "X";
  public static final String EMPTY_STRING = "";
  public static final String SPACE_STRING = " ";
//  public static final String DTD =
//    "<!DOCTYPE customizing [\n" +
//      "<!ELEMENT customizing (control, scenarios)>\n" +
//      "<!ELEMENT control (grmgruns, runlog, errorlog)>\n" +
//      "<!ELEMENT grmgruns (#PCDATA)>\n" +
//      "<!ELEMENT runlog (#PCDATA)>\n" +
//      "<!ELEMENT errorlog (#PCDATA)>\n" +
//      "<!ELEMENT scenarios (scenario*)>\n" +
//      "<!ELEMENT scenario (scenname, scenversion, sceninst, scentype, scenstarturl, scenstartmod, scentexts, components)>\n" +
//      "<!ELEMENT scenname (#PCDATA)>\n" +
//      "<!ELEMENT scenversion (#PCDATA)>\n" +
//      "<!ELEMENT sceninst (#PCDATA)>\n" +
//      "<!ELEMENT scentype (#PCDATA)>\n" +
//      "<!ELEMENT scenstarturl (#PCDATA)>\n" +
//      "<!ELEMENT scenstartmod (#PCDATA)>\n" +
//      "<!ELEMENT scentexts (scentext)>\n" +
//      "<!ELEMENT scentext (scenlangu, scendesc)>\n" +
//      "<!ELEMENT scenlangu (#PCDATA)>\n" +
//      "<!ELEMENT scendesc (#PCDATA)>\n" +
//      "<!ELEMENT components (component*)>\n" +
//      "<!ELEMENT component (compname, compversion, comptype, comptexts, properties)>\n" +
//      "<!ELEMENT compname (#PCDATA)>\n" +
//      "<!ELEMENT compversion (#PCDATA)>\n" +
//      "<!ELEMENT comptype (#PCDATA)>\n" +
//      "<!ELEMENT comptexts (comptext)>\n" +
//      "<!ELEMENT comptext (complangu, compdesc)>\n" +
//      "<!ELEMENT complangu (#PCDATA)>\n" +
//      "<!ELEMENT compdesc (#PCDATA)>\n" +
//      "<!ELEMENT properties (property*)>\n" +
//      "<!ELEMENT property (propname, propvalue)>\n" +
//      "<!ELEMENT propname (#PCDATA)>\n" +
//      "<!ELEMENT propvalue (#PCDATA)>\n" +
//      "<!ELEMENT propversion (#PCDATA)>\n" +
//    "]>\n";

  public static Document createCustomizingXml(GrmgCustomizing customizingDescriptor) {
    return createCustomizingXml(DEFAULT_OUTPUT_DIRECTORY, DEFAULT_FILE_NAME, customizingDescriptor);
  }

  public static Document createCustomizingXml(String outputDirectory, String fileName, GrmgCustomizing customizingDescriptor) {
    try {
      Document doc = buildDocument(customizingDescriptor);

      File dir = new File(outputDirectory);
      dir.mkdirs();
      File file = new File(dir, fileName);
      FileOutputStream output = new FileOutputStream(file);

      printDocument(doc, output);

      output.flush();
      output.close();

      return doc;
    } catch (Exception exc) {
      return null;
      // TO DO : add logging
    }
  }


  protected static Document buildDocument(GrmgCustomizing customizingDescriptor) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();
    Element mainNode = doc.createElement("customizing");

    // {{ builds control element
    Element controlElement = doc.createElement("control");

    buildLeafTag(doc, controlElement, "grmgruns", customizingDescriptor.isGrmgRunsFlag() ? X_SIGN_STRING : EMPTY_STRING);
    buildLeafTag(doc, controlElement, "runlog", customizingDescriptor.isRunLogFlag() ? X_SIGN_STRING : SPACE_STRING);
    buildLeafTag(doc, controlElement, "errorlog", customizingDescriptor.isErrorLogFlag() ? X_SIGN_STRING : SPACE_STRING);

    mainNode.appendChild(controlElement);
    //}} builds control element


    //{{ builds scenarios element
    ArrayList grmgScenarios = customizingDescriptor.getScenarios();
    Element scenariosElement = doc.createElement("scenarios");

    for (int i = 0; i < grmgScenarios.size(); i++) {
      GrmgScenario scenario = (GrmgScenario) grmgScenarios.get(i);
      Element scenarioElement = doc.createElement("scenario");

      buildLeafTag(doc, scenarioElement, "scenname", scenario.getName());
      buildLeafTag(doc, scenarioElement, "scenversion", scenario.getVersion());
      buildLeafTag(doc, scenarioElement, "sceninst", scenario.getInstance());
      buildLeafTag(doc, scenarioElement, "scentype", scenario.getType());
      buildLeafTag(doc, scenarioElement, "scenstarturl", scenario.getStartUrl());
      buildLeafTag(doc, scenarioElement, "scenstartmode", scenario.getStartMode());

      //{{ i026851
      /*ArrayList scenarioTexts = scenario.getTexts();
      Element scenarioTextsElement = doc.createElement("scentexts");
      if (scenarioTexts != null) {
        for (int t = 0; t < scenarioTexts.size(); t++) {
          Element scenarioTextElement = doc.createElement("scentext");
          GrmgText text = (GrmgText) scenarioTexts.get(t);
          buildLeafTag(doc, scenarioTextElement, "scenlangu", text.getLanguage());
          buildLeafTag(doc, scenarioTextElement, "scendesc", text.getDescription());
          scenarioTextsElement.appendChild(scenarioTextElement);
        }
      }*/

      GrmgText scenarioText = scenario.getText();
      Element scenarioTextsElement = doc.createElement("scentexts");

      if (scenarioText != null) {
        Element scenarioTextElement = doc.createElement("scentext");
        buildLeafTag(doc, scenarioTextElement, "scenlangu", scenarioText.getLanguage());
        buildLeafTag(doc, scenarioTextElement, "scendesc", scenarioText.getDescription());
        scenarioTextsElement.appendChild(scenarioTextElement);
      }
      //}} i026851

      scenarioElement.appendChild(scenarioTextsElement);

      //{{ builds components element
      ArrayList components = scenario.getComponents();
      Element componentsElement = doc.createElement("components");
      for(int t = 0; t < components.size(); t ++) {
        GrmgComponent component = (GrmgComponent) components.get(t);
        Element componentElement = doc.createElement("component");

        buildLeafTag(doc, componentElement, "compname", component.getName());
        buildLeafTag(doc, componentElement, "compversion", component.getVersion());
        buildLeafTag(doc, componentElement, "comptype", component.getType());

        //{{ i026851
        /*ArrayList componentTexts = component.getTexts();
        Element componentTextsElement = doc.createElement("comptexts");
        if (componentTexts != null) {
          for (int s = 0; s < componentTexts.size(); s++) {
            Element componentTextElement = doc.createElement("comptext");
            GrmgText text = (GrmgText) componentTexts.get(s);
            buildLeafTag(doc, componentTextElement, "complangu", text.getLanguage());
            buildLeafTag(doc, componentTextElement, "compdesc", text.getDescription());
            componentTextsElement.appendChild(componentTextElement);
          }
        }*/

        GrmgText componentText = component.getText();
        Element componentTextsElement = doc.createElement("comptexts");

        if (componentText != null) {
          Element componentTextElement = doc.createElement("comptext");
          buildLeafTag(doc, componentTextElement, "complangu", componentText.getLanguage());
          buildLeafTag(doc, componentTextElement, "compdesc", componentText.getDescription());
          componentTextsElement.appendChild(componentTextElement);
        }
        //}} i026851

        componentElement.appendChild(componentTextsElement);

        ArrayList properties = component.getProperties();
        Element propertiesElement = doc.createElement("properties");
        for (int s = 0; s < properties.size(); s++) {
          Element propertyElement = doc.createElement("property");
          GrmgProperty property = (GrmgProperty) properties.get(s);
          buildLeafTag(doc, propertyElement, "propname", property.getName());
          buildLeafTag(doc, propertyElement, "propvallue", property.getValue());
          propertiesElement.appendChild(propertyElement);
        }
        componentElement.appendChild(propertiesElement);

        componentsElement.appendChild(componentElement);
      }
      scenarioElement.appendChild(componentsElement);
      //}} builds components element

      scenariosElement.appendChild(scenarioElement);
    }
    mainNode.appendChild(scenariosElement);
    //}} builds scenarios element

    doc.appendChild(mainNode);
    return doc;
  }

  protected static void buildLeafTag(Document doc, Element parentElement, String tagName, String tagValue) {
    Element subElement = doc.createElement(tagName);
    Text text = doc.createTextNode(tagValue);
    subElement.appendChild(text);
    parentElement.appendChild(subElement);
  }

  public static void printDocument(Document doc, OutputStream output) throws TransformerException{
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(output);
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.transform(source, result);
  }


  public final static void main(String[] args) throws Exception{
    GrmgCustomizing c = new GrmgCustomizing();
    c.setGrmgRunsFlag(true);

    GrmgScenario s = new GrmgScenario();
    s.setName("Some Scenario");
    s.setVersion("1");
    s.setInstance("2");
    s.setStartUrl("http://localhost:8080/myBeautifulTest");
    GrmgText text = new GrmgText();
    text.setLanguage("E");
    text.setDescription("the best run businesses run sap");

    //s.addText(text);
    s.setText(text);

    GrmgComponent comp = new GrmgComponent();
    comp.setName("Some Component");
    comp.setVersion("1");
    comp.setType("1");
    text = new GrmgText();
    text.setLanguage("D");
    text.setDescription("Deutsch macht Spass");
    //comp.addText(text);
    comp.setText(text);
    GrmgProperty prop = new GrmgProperty();
    prop.setName("propName");
    prop.setValue("propValue");
    comp.addProperty(prop);

    s.addComponent(comp);


    c.addScenario(s);

    printDocument(createCustomizingXml(".", "testGRMGCustomizing.xml", c), System.out);
  }
}