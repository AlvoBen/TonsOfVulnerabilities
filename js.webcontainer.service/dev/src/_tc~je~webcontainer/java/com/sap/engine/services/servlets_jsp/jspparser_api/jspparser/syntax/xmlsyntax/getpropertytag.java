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

import java.lang.reflect.Method;
import java.util.Arrays;

import com.sap.engine.services.servlets_jsp.jspparser_api.exception.JspParseException;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.Indentifier;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.JspElement;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.JspTag;

/*
 *
 * @author Galin Galchev,Ivo Simeonov
 * @version 4.0
 */
public class GetPropertyTag extends CustomJspTag {

  /*
   * all attributes that this element can
   * accept
   */
  private static final char[][] attributeNames = {"name".toCharArray(), "property".toCharArray(), };

  /**
   * Creates new GetPropertyTag
   *
   */
  public GetPropertyTag() {
    super();
    _name = "getProperty";
    START_TAG_INDENT = (_default_prefix_tag_start + _name).toCharArray();
    CMP_0 = (_default_prefix_tag_end + _name + END).toCharArray();
  }

  //  /**
  //   *
  //   *
  //   * @param   startTagIndent
  //   * @param   endTagIndent
  //   */
  //  public GetPropertyTag(char[] startTagIndent ,char[] endTagIndent) {
  //    this();
  //    START_TAG_INDENT = startTagIndent;
  //    CMP_0 = endTagIndent;
  //  }
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
        debugInfo.writeStart(buffer, false);
        String property = getAttributeValue("property");
        String name = getAttributeValue("name");
        Class beanClass;
        String beanClassName = "";
        try {
          beanClassName = (String) parser.getAllBeanNames().get(name);
          beanClass = parser.getApplicationClassLoader().loadClass(beanClassName);
        } catch (Exception ex) {
          buffer.append("\t\t\tout.print(com.sap.engine.services.servlets_jsp.lib.jspruntime.RunLibrary.getProp(_jspx_pageContext.findAttribute(\"").append(name).append("\"), \"").append(property).append("\"));\r\n");
          return;
        }
        try {
          Method m = beanClass.getDeclaredMethod("is" + parser.firstCap(property), new Class[0]);

          if (m.getReturnType().getName().equals("boolean")) {
            buffer.append("\t\t\tout.print(").append(name).append(".is").append(parser.firstCap(property)).append("());\r\n");
          } else {
            throw new JspParseException(JspParseException.METHOD_MUST_RETURN_BOOLEAN, new Object[]{"is" + parser.firstCap(property)}, parser.currentFileName(), debugInfo.start);
          }
        } catch (JspParseException ex) {
          throw ex;
        } catch (Exception nsfex) {
          buffer.append("\t\t\tout.print(").append(name).append(".get").append(parser.firstCap(property)).append("());\r\n");
        }
        debugInfo.writeEnd(buffer);
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
   * "<jsp:getProperty"
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
    return (this.copy) ? new GetPropertyTag() : this;
  }

  /**
   * Returnes element to witch will be associated
   * current parsing.
   *
   * @return object that is associated as current element
   * of parsing
   */
  protected JspElement createJspElement() {
    return new JspElement(JspElement.GET_PROPERTY);
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
    return (JspTag) (new GetPropertyTag().parse(this.parser));
  }

  /**
   * Verifies the attributes of this tag
   *
   * @exception   JspParseException  thrown if error occures
   * during verification
   */
  public void verifyAttributes() throws JspParseException {
    if (attributes == null || attributes.length != 2) {
      throw new JspParseException(JspParseException.ACTION_MUST_HAVE_EXACTLY_TWO_SPECIFIED_ATTRIBUTE, new Object[]{_name}, parser.currentFileName(), debugInfo.start);
    }

    boolean[] flags = new boolean[attributeNames.length];
    Arrays.fill(flags, true);
    Indentifier e = null;

    for (int i = 0; i < attributes.length; i++) {
      e = attributes[i].name;

      if (e.equals(attributeNames[0])) {
        if (flags[0]) {
          flags[0] = false;
        } else {
          throw new JspParseException(JspParseException.ATTRIBUTE_ALREADY_SPECIFED_IN_ACTION, new Object[]{"name", _name}, parser.currentFileName(), debugInfo.start);
        }
      } else if (e.equals(attributeNames[1])) {
        if (flags[1]) {
          flags[1] = false;
        } else {
          throw new JspParseException(JspParseException.ATTRIBUTE_ALREADY_SPECIFED_IN_ACTION, new Object[]{"property", _name}, parser.currentFileName(), debugInfo.start);
        }
      } else {
        throw new JspParseException(JspParseException.UNRECOGNIZED_ATTRIBUTE_IN_ACTION, new Object[]{e, _name}, parser.currentFileName(), debugInfo.start);
      }
    }
  }

}

