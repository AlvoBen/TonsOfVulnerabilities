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

import com.sap.engine.services.servlets_jsp.jspparser_api.exception.JspParseException;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.JspElement;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.JspTag;
import com.sap.engine.services.servlets_jsp.jspparser_api.JspPageInterface;

/*
 *
 * @author Bojidar Kadrev
 * @version 7.0
 */
public class OutputActionTag extends CustomJspTag {

  private static final String xmlBegin = "<?xml version=\\\"1.0\\\" encoding=\\\"";
  private static final String xmlEnd = "\\\" ?>\\r\\n";
  private static final String doctypeBegin = "<!DOCTYPE ";
  private static final String doctypeEnd = ">\\r\\n";
  private static final String doctype_public = " PUBLIC ";
  private static final String doctype_system = " SYSTEM ";
  private static final String[] attributeNames = {"omit-xml-declaration", "doctype-root-element", "doctype-system", "doctype-public" };
  /**
   * Creates new OutputActionTag
   *
   */
  public OutputActionTag() {
    super();
    _name = "output";
    START_TAG_INDENT = (_default_prefix_tag_start + _name).toCharArray();
    CMP_0 = (SLASH_END).toCharArray();
  }


  /**
   * Takes specific action coresponding to this jsp element
   * logic
   *
   * @exception   JspParseException  thrown if error occures during
   * verification
   */
  public void action(StringBuffer buffer) throws JspParseException {
    switch (tagType) {
      case SINGLE_TAG: {
        if (!parser.isXml()) {
          throw new JspParseException(JspParseException.CANNOT_BE_USED_IN_JSP_SYNTAX, new Object[]{_name}, parser.currentFileName(), debugInfo.start);
        }


        // The default value for a JSP document that has a jsp:root element is "yes".
        // The default value for JSP documents without a jsp:root element is "no".
        // The default value for a tag file in XML syntax is always "yes".
        boolean omitXml = parser.isTagFile() ? true : parser.hasJspRoot();

        String temp = getAttributeValue("omit-xml-declaration");
        if (temp != null){
          if (temp.equals("no") || temp.equals("false")) {
            omitXml = false;
          } else if (temp.equals("yes") || temp.equals("true")){
            omitXml = true;
          }
        }
        parser.setOmitXMLDeclaration(omitXml);
        break;
      }
      case START_TAG:
      case END_TAG:
      default:
    }
  }

  public void action() throws JspParseException {
    action(parser.getScriptletsCode());
  }

  /**
   * Retuns the indentification for this tag
   *
   * @return  array containing chars
   * "<jsp:output"
   */
  public char[] indent() {
    return START_TAG_INDENT;
  }

  /**
   * Returnes element to witch will be associated
   * current parsing.
   *
   * @return object that is associated as current element
   * of parsing
   */
  protected CustomJspTag createThis() {
    return (this.copy) ? new OutputActionTag() : this;
  }

  /**
   * Returnes element to witch will be associated
   * current parsing.
   *
   * @return object that is associated as current element
   * of parsing
   */
  protected JspElement createJspElement() {
    return new JspElement(JspElement.OUTPUT);
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
    return (JspTag) (new OutputActionTag().parse(this.parser));
  }

  /**
   * Verifies the attributes of this tag
   *
   * @exception   JspParseException  thrown if error occures
   * during verification
   */
  public void verifyAttributes() throws JspParseException {
    //verified by SAXparser.
    //if it is used in JSP synrax - throw an exc
    if (!parser.isXml()) {
      throw new JspParseException(JspParseException.CANNOT_BE_USED_IN_JSP_SYNTAX, new Object[]{_name}, parser.currentFileName(), debugInfo.start);
    }
    boolean[] appeared = new boolean[attributeNames.length];
    if (attributes == null || attributes.length == 0) {
      return;
    }
    String currAttributeName = null;
    String currAttributeValue = null;
    for (int i = 0; i < attributes.length; i++) {
      currAttributeName = attributes[i].name.toString();
      currAttributeValue = attributes[i].value.toString();
      
      int attributeIndex = -1;
      for (int j = 0; j < attributeNames.length; j++) {
        if( attributeNames[j].equals(currAttributeName) ){
          attributeIndex = j;
          break;
        }
      }
      if( attributeIndex < 0 ){
        throw new JspParseException(JspParseException.UNRECOGNIZED_ATTRIBUTE_IN_ACTION, new Object[]{currAttributeName, _name}, parser.currentFileName(), debugInfo.start);
      }
      if (!appeared[attributeIndex]) {
        appeared[attributeIndex] = true;
      } else {
        throw new JspParseException(JspParseException.ATTRIBUTE_ALREADY_SPECIFED_IN_ACTION, new Object[]{currAttributeName, _name}, parser.currentFileName(), debugInfo.start);
      }
      
      switch (attributeIndex) {
	      case 0: //omit-xml-declaration
	        if (!( "true".equals(currAttributeValue) 
	            || "false".equals(currAttributeValue) 
	            || "yes".equals(currAttributeValue) 
	            || "no".equals(currAttributeValue))) {
	          throw new JspParseException(JspParseException.INVALID_VALUE_FOR_ATTRIBUTE_IN_ACTION, new Object[]{currAttributeValue, currAttributeName, _name}, parser.currentFileName(), debugInfo.start);
	        }
          break;
	      case 1: //"doctype-root-element",
          //JSP2.1 5.16 : Multiple occurrences of the doctype-root-element, doctype-system
          //or doctype-public properties will cause a translation error if the values for the
          //properties differ from the previous occurrence.
          if (parser.getDoctypeRootElement() != null && !currAttributeValue.equals(parser.getDoctypeRootElement())) {
            throw new JspParseException(JspParseException.DUPLICATED_JSP_OUTPUT_WITH_DIFFERENT_ATTRIBUTE_VALUE,
              new Object[]{_default_prefix.peek() + ":"+_name, parser.getDoctypeRootElement(), currAttributeValue, currAttributeName}, parser.currentFileName(), debugInfo.start);
          }
          parser.setDoctypeRootElement(currAttributeValue);
          break;
        case 2: //"doctype-system",
          if (parser.getDoctypeSystem() != null && !currAttributeValue.equals(parser.getDoctypeSystem())) {
            throw new JspParseException(JspParseException.DUPLICATED_JSP_OUTPUT_WITH_DIFFERENT_ATTRIBUTE_VALUE,
              new Object[]{_default_prefix.peek() + ":"+_name, parser.getDoctypeSystem(), currAttributeValue, currAttributeName}, parser.currentFileName(), debugInfo.start);
          }
          parser.setDoctypeSystem(currAttributeValue);
          break;
        case 3: //"doctype-public"
          if (parser.getDoctypePublic() != null && !currAttributeValue.equals(parser.getDoctypePublic())) {
            throw new JspParseException(JspParseException.DUPLICATED_JSP_OUTPUT_WITH_DIFFERENT_ATTRIBUTE_VALUE,
              new Object[]{_default_prefix.peek() + ":"+_name, parser.getDoctypePublic(), currAttributeValue, currAttributeName}, parser.currentFileName(), debugInfo.start);
          }
          parser.setDoctypePublic(currAttributeValue);
          break;
	      default: // impossible
	        throw new JspParseException(JspParseException.UNRECOGNIZED_ATTRIBUTE_IN_ACTION, new Object[]{currAttributeName, _name}, parser.currentFileName(), debugInfo.start);
      }//end switch
    }// end for
    
    // {"omit-xml-declaration", "doctype-root-element", "doctype-system", "doctype-public" };
    /*
     * JSP 5.16
     * <jsp:output ( omit-xml-declaration="yes|no|true|false" ) { doctypeDecl } />
		 * 	doctypeDecl ::= ( doctype-root-element="rootElement"
		 *										doctype-public="PubidLiteral"
		 *										doctype-system="SystemLiteral" )
		 *								| ( doctype-root-element="rootElement"
		 *										doctype-system="SystemLiteral" )
     */
    
    // doctype-root-element and doctype-system always go together
    if( (appeared[1] && !appeared[2]) || (!appeared[1] && appeared[2])) {
      throw new JspParseException(JspParseException.MISSING_ATTRIBUTE_OR_ATTRIBUTE_IN_ACTION, new Object[]{attributeNames[1], attributeNames[2], _name}, parser.currentFileName(), debugInfo.start);
    }
    
    //doctype-public - (optional) -  Must not be specified unless doctype-system is specified. 
    if ( appeared[3] && !appeared[2] ) {
      throw new JspParseException(JspParseException.MISSING_ATTRIBUTE_IN_ACTION, new Object[]{attributeNames[2], _name}, parser.currentFileName(), debugInfo.start);
    }

  }

  public static StringBuffer getXMLDeclaration(JspPageInterface parser) {
    StringBuffer code = new StringBuffer();
    String charset = getEncodingForXMLDeclaration(parser);
    if (parser.isOmitXMLDeclarationSet()) { // here is a jsp:output declaration
      if (!parser.isOmitXMLDeclaration()) {
        code.append(xmlBegin).append(charset).append(xmlEnd);
      }
    } else { // default values
      if (parser.isXml() && !parser.hasJspRoot() && !parser.isTagFile()) {
        // The default value for JSP documents without a jsp:root element is "no".
        // The default value for a tag file in XML syntax is always "yes". -> No xml declaration in tag files by default.
        code.append(xmlBegin).append(charset).append(xmlEnd);
      }
    }
    if (parser.getDoctypeSystem() != null) {
      code.append(doctypeBegin).append(parser.getDoctypeRootElement()).append((parser.getDoctypePublic() == null ? doctype_system : doctype_public + " \\\"" + parser.getDoctypePublic() + "\\\" "))
          .append("\\\"" + parser.getDoctypeSystem() + "\\\"").append(doctypeEnd);
    }
    return code.length() > 0 ? code : null;
  }
  
  /**
   * Gets charset per JSP.4.2 Response Character Encoding
   * The initial response character encoding is set to the CHARSET value of the 
   * contentType attribute of the page directive. 
   * If the page doesn’t provide this attribute or 
   * the attribute doesn’t have a CHARSET value, 
   * the initial response character encoding is determined as follows: 
   * • For documents in XML syntax, it is UTF-8.
   * @param parser
   * @return
   */
  private static String getEncodingForXMLDeclaration(JspPageInterface parser){
    String encoding = null;
    String contentType = parser.getContentType();

    if (contentType == null || contentType.indexOf("charset") < 0) {
        encoding = "UTF-8";
    } else{
      encoding = contentType.substring(contentType.indexOf("charset=") + "charset=".length());
    }
    return encoding;
  }
}

