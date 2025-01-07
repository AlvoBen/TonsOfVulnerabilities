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
package com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.xmlsyntax;

import java.util.Enumeration;
import java.util.StringTokenizer;

import com.sap.engine.services.servlets_jsp.jspparser_api.exception.JspParseException;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.Attribute;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.IDCounter;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.JspElement;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.JspTag;
import com.sap.engine.services.servlets_jsp.server.deploy.descriptor.TagInfoImpl;
import com.sap.engine.services.servlets_jsp.server.deploy.descriptor.TagLibDescriptor;
import com.sap.engine.services.servlets_jsp.server.lib.StringUtils;

/**
 * Represents tag directive.
 *
 * @author Bojidar Kadrev
 * @author Mladen Markov
 *
 * @version 7.0
 */
public class TagDirectiveTag extends CustomJspTag {


  /*
   * all attributes that this element can
   * accept
   */
  private static final String[] attributeNames = {"display-name", "body-content",
                                                  "dynamic-attributes", "small-icon",
                                                  "large-icon", "description", "example",
                                                  "language", "import", "pageEncoding",
                                                  "isELIgnored", "deferredSyntaxAllowedAsLiteral", "trimDirectiveWhitespaces"};

  /*
   * Must save attributes for this directive, to use in XML view of jsp.
   */
  private Attribute[] attributes0 = null;

  /**
   * Constructs new JspPageDirective
   *
   */
  public TagDirectiveTag() {
    super();
    _name = "directive.tag";
    START_TAG_INDENT = (_default_prefix_tag_start + _name).toCharArray();
    CMP_0 = (_default_prefix_tag_end + _name + END).toCharArray();
    setCopy(true);
  }

  /**
   * Takes specific action coresponding to this jsp element
   * logic
   *
   * @exception   JspParseException  thrown if error occures during
   * verification
   */
  public void action(StringBuffer buffer) throws JspParseException {
// extends
    if (parser.getPageActionTaken()) {
      return;
    } else {
      parser.setPageActionTaken(true);
      attributes = new Attribute[parser.getPageAttributes().size()];
      Enumeration e = parser.getPageAttributes().elements();
      int i = 0;

      while (e.hasMoreElements()) {
        attributes[i] = (Attribute) e.nextElement();
        i++;
      }
    }

    TagInfoImpl tagInfo = parser.getTagInfo();
    if (tagInfo == null) {
      tagInfo = new TagInfoImpl();
      parser.setTagInfo(tagInfo);
    }

    // example
    String attributeValue = getAttributeValue("example");
    if (attributeValue != null) {
      // Table JSP.8-2 ...presents an informal description of an example of a use of this action.
    }

    // description
    attributeValue = getAttributeValue("description");
    if (attributeValue != null) {
      tagInfo.setInfoString(attributeValue);
    }

    // large-icon
    attributeValue = getAttributeValue("large-icon");
    if (attributeValue != null) {
      tagInfo.setLargeIcon(attributeValue);
    }

    // small-icon
    attributeValue = getAttributeValue("small-icon");
    if (attributeValue != null) {
      tagInfo.setSmallIcon(attributeValue);
    }

    // dynamic-attributes
    attributeValue = getAttributeValue("dynamic-attributes");
    if (attributeValue != null) {
      tagInfo.setDynamicAttrsMapName(attributeValue);
      tagInfo.setDynamicAttributes(true);
    }

    // display-name
    attributeValue = getAttributeValue("display-name");
    if (attributeValue != null) {
      tagInfo.setDisplayName(attributeValue);
    }

    // body-content
    attributeValue = getAttributeValue("body-content");
    if (attributeValue != null) {
      tagInfo.setBodyContent(attributeValue);
    } else {
      tagInfo.setBodyContent("scriptless");
    }

    // import
    attributeValue = parser.getPageImportAttribute();

    if (attributeValue != null) {
      StringTokenizer toker = new StringTokenizer(attributeValue, ",");

      while (toker.hasMoreTokens()) {
        parser.getImportDirective().append("import ").append(toker.nextToken().trim()).append(';').append("\r\n");
      }
    }

    // language
    // attributeValue = getAttributeValue("language");
    // The value should be "java". It is laready chcked in verifyAttributes(). Nothing to do here.

    // pageEncoding
    attributeValue = getAttributeValue("pageEncoding");
    if (attributeValue != null) {
      // JSP2.0 Part 1, JSP.8.5.1 : The pageEncoding attribute cannot be used in tag files in XML syntax.
      // JSP8.6 Using the pageEncoding attribute shall result in a translation-time error.
      throw new JspParseException(JspParseException.PAGEENCODING_CANNOT_BE_USED_IN_TAG_FILES_XWM_SYNTAX);
    }

    attributeValue = getAttributeValue("isELIgnored");
    if (attributeValue != null) {
      parser.setIsELIgnored(new Boolean(attributeValue).booleanValue());
    }

    attributeValue = getAttributeValue("deferredSyntaxAllowedAsLiteral");
    if (attributeValue != null) {
      parser.setDeferredSyntaxAllowedAsLiteral(new Boolean(attributeValue).booleanValue());
    }

    attributeValue = getAttributeValue("trimDirectiveWhitespaces");
    if (attributeValue != null) {
      parser.setTrimDirectiveWhitespaces(new Boolean(attributeValue).booleanValue());
    }
  }

  public void action() throws JspParseException {
    action(parser.getScriptletsCode());
  }

  /**
   * Verifies the attributes of this tag
   *
   * @exception   JspParseException  thrown if error occures
   * during verification
   */
  public void verifyAttributes() throws JspParseException {
    if (attributes == null) {
      throw new JspParseException(JspParseException.DIRECTIVE_MUST_HAVE_AT_LEAST_ONE_SPECIFIED_ATTRIBUTE, new Object[]{_name}, parser.currentFileName(), debugInfo.start);
    }
    Object o = null;
    String attributeName0;
    //save attributes
    attributes0 = attributes;

    for (int i = 0; i < attributes.length; i++) {
      attributeName0 = attributes[i].name.toString();
      if (attributeName0.equals("import")) {
        if (parser.getPageImportAttribute() == null) {
          parser.setPageImportAttribute(attributes[i].value.toString());
        } else {
          parser.setPageImportAttribute(parser.getPageImportAttribute() + "," + attributes[i].value.toString());
        }

        continue;
      }
      o = parser.getPageAttributes().get(attributeName0);
      if (o == null) {
        parser.getPageAttributes().put(attributeName0, attributes[i]);
      } else {
        throw new JspParseException(JspParseException.ATTRIBUTE_ALREADY_SPECIFED_IN_DIRECTIVE, new Object[]{attributes[i].name, _name}, parser.currentFileName(), debugInfo.start);
      }
    }

    for (int i = 0; i < attributes.length; i++) {
      String attrName = attributes[i].name.toString();
      String value = attributes[i].value.toString();
      boolean valid = true;
      if (attributeNames[0].equals(attrName)) { // display-name
      } else if (attributeNames[1].equals(attrName)) { // body-content
        valid = !(value.equalsIgnoreCase("scriptless") || value.equalsIgnoreCase("tagdependent") || value.equalsIgnoreCase("empty"));
      } else if (attributeNames[2].equals(attrName)) { // dynamic-attributes
        //Check value for a valid java identifier
        //It is not obligatory by the spec, but if the identifier is not valid, it is better to determine the error
        // in transalation time instead of compile time
        valid = StringUtils.isValidJavaIdentifier(value);
      } else if (attributeNames[3].equals(attrName)) { // small-icon
      } else if (attributeNames[4].equals(attrName)) { // large-icon
      } else if (attributeNames[5].equals(attrName)) { // description
      } else if (attributeNames[6].equals(attrName)) { // example
      } else if (attributeNames[7].equals(attrName)) { // language
        //JSP.8.5.1
        //Carries the same syntax and semantics of thelanguage attribute of the page directive.
        valid = "java".equals(value); 
      } else if (attributeNames[8].equals(attrName)) { // import
      } else if (attributeNames[9].equals(attrName)) { // pageEncoding
      } else if (attributeNames[10].equals(attrName)) { // isELIgnored
        valid = "true".equals(value) || "false".equals(value);
      } else if (attributeNames[11].equals(attrName)) { // deferredSyntaxAllowedAsLiteral
        valid = "true".equals(value) || "false".equals(value);
         if ( StringUtils.lessThan(parser.getTagFileTLD().getRequiredVersion(), TagLibDescriptor.JSP_VERSION_21_DOUBLE) ) {
          throw new JspParseException(JspParseException.UNRECOGNIZED_ATTRIBUTE_IN_DIRECTIVE, new Object[]{attrName, _name}, parser.currentFileName(), debugInfo.start);
        }
      } else if (attributeNames[12].equals(attrName)) { // trimDirectiveWhitespaces
        valid = "true".equals(value) || "false".equals(value);
      } else {
        throw new JspParseException(JspParseException.UNRECOGNIZED_ATTRIBUTE_IN_DIRECTIVE, new Object[]{attrName, _name}, parser.currentFileName(), debugInfo.start);
      }
      if (!valid) {
        throw new JspParseException(JspParseException.INVALID_VALUE_FOR_ATTRIBUTE_IN_DIRECTIVE, new Object[]{value, attrName, _name}, parser.currentFileName(), debugInfo.start);
      }
    }
  }

  public String getString(IDCounter id) {
    String s = _default_prefix_tag_start + "directive." + _name;
    String name = null;
    String value = null;

    if (attributes0 != null) {
      for (int i = 0; i < attributes0.length; i++) {
        s = s + "\n\t";
        name = attributes0[i].name.toString();
        value = attributes0[i].value.toString();
        s = s + name + "=\"" + value + "\"";
      }
    }
    s = s + getId(id);
    return s.concat("\n/>");
  }

  /**
   * Returnes element to witch will be associated
   * current parsing.
   *
   * @return object that is associated as current element
   * of parsing
   */
  protected CustomJspTag createThis() {
    return (this.copy) ? new TagDirectiveTag() : this;
  }

  /**
   * Returnes element to witch will be associated
   * current parsing.
   *
   * @return object that is associated as current element
   * of parsing
   */
  protected JspElement createJspElement() {
    return new JspElement(JspElement.DIRECTIVE_TAG); 
  }

  /**
   * Returnes element to witch will be associated
   * current parsing.
   *
   * @return object that is associated as current element
   * of parsing
   * @exception   JspParseException  thrown if error occures
   * during verification or parsing
   */
  protected JspTag createEndTag() throws JspParseException {
    return (JspTag) (new TagDirectiveTag().parse(this.parser));
  }
}

