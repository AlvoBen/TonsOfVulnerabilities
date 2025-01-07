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
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.taglib.tagfiles.GenerateTagFileJavaFile;

import java.util.Arrays;

/*
 *
 * @author Bojidar Kadrev
 * @version 7.0
 */
public class DoBodyActionTag extends CustomJspTag {

   private static final String[] attributeNames = {"var", "varReader", "scope" };

  /**
   * Creates new ExpressionTag
   *
   */
  public DoBodyActionTag() {
    super();
    _name = "doBody";
    START_TAG_INDENT = (_default_prefix_tag_start + _name).toCharArray();
    CMP_0 = (_default_prefix_tag_end + _name + END).toCharArray();
  }


  /**
   * Takes specific action coresponding to this jsp element
   * logic
   *
   * @exception   JspParseException  thrown if error occures during
   * verification
   */
  public void action(StringBuffer buffer) throws JspParseException {
//    switch (tagType) {
//      case SINGLE_TAG: {
        if (!parser.isTagFile()) {
          throw new JspParseException(JspParseException.JSP_ACTION_CANNOT_BE_USED_IN_JSP, new Object[]{_name}, parser.currentFileName(), debugInfo.start);
        }
        buffer.append("\t\t((com.sap.engine.services.servlets_jsp.lib.jspruntime.JspContextWrapper)this.jspContext).syncBeforeInvoke();\r\n");
        String varReaderAttr = getAttributeValue("varReader");
        String varAttr = getAttributeValue("var");
        if (varReaderAttr != null || varAttr != null) {
          buffer.append("\t\t_jspx_sout = new java.io.StringWriter();\r\n");
        } else {
          buffer.append("\t\t_jspx_sout = null;\r\n");
        }

        // Invoke fragment, unless fragment is null
        buffer.append("\t\tif (getJspBody() != null){\r\n");
        buffer.append("\t\t\tgetJspBody().invoke(_jspx_sout);\r\n");
        buffer.append("\t\t}\r\n");

        // Store varReader in appropriate scope
        if (varReaderAttr != null || varAttr != null) {
          String scopeName = getAttributeValue("scope");
          buffer.append("\t\t_jspx_pageContext.setAttribute(");
          if (varReaderAttr != null) {
            buffer.append(GenerateTagFileJavaFile.quote(varReaderAttr));
            buffer.append(", new java.io.StringReader(_jspx_sout.toString())");
          } else {
            buffer.append(GenerateTagFileJavaFile.quote(varAttr));
            buffer.append(", _jspx_sout.toString()");
          }
          if (scopeName != null) {
            buffer.append(", ");
            buffer.append(GenerateTagFileJavaFile.getScope(scopeName));
          }
          buffer.append(");\r\n");
          }
//          break;
//        }
//    }
  }

  public void action() throws JspParseException {
    action(parser.getScriptletsCode());
  }

  /**
   * Retuns the indentification for this tag
   *
   * @return  array containing chars
   * "<jsp:doBody"
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
    return (this.copy) ? new DoBodyActionTag() : this;
  }

  /**
   * Returnes element to witch will be associated
   * current parsing.
   *
   * @return object that is associated as current element
   * of parsing
   */
  protected JspElement createJspElement() {
    return new JspElement(JspElement.DO_BODY);
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
    return (JspTag) (new DoBodyActionTag().parse(this.parser));
  }

  /**
   * Verifies the attributes of this tag
   *
   * @exception   JspParseException  thrown if error occures
   * during verification
   */
  public void verifyAttributes() throws JspParseException {
    if (!parser.isTagFile()) {
      throw new JspParseException(JspParseException.JSP_ACTION_CANNOT_BE_USED_IN_JSP, new Object[]{_name}, parser.currentFileName(), debugInfo.start);
    }

    boolean[] flags = new boolean[attributeNames.length];
    Arrays.fill(flags, true);
    String name = null;
    String value = null;
    if (attributes == null || attributes.length == 0) {
      return;
    }
    for (int i = 0; i < attributes.length; i++) {
      name = attributes[i].name.toString();
      value = attributes[i].value.toString();

      if (attributeNames[0].equals(name)) {
        if (flags[0]) {
          flags[0] = false;
        } else {
          throw new JspParseException(JspParseException.ATTRIBUTE_ALREADY_SPECIFED_IN_ACTION, new Object[]{"var", _name}, parser.currentFileName(), debugInfo.start);
        }
      } else if (attributeNames[1].equals(name)) {
        if (flags[1]) {
          flags[1] = false;
        } else {
          throw new JspParseException(JspParseException.ATTRIBUTE_ALREADY_SPECIFED_IN_ACTION, new Object[]{"varReader", _name}, parser.currentFileName(), debugInfo.start);
        }
      } else if (attributeNames[2].equals(name)) {
        if (flags[2]) {
          flags[2] = false;
        } else {
          throw new JspParseException(JspParseException.ATTRIBUTE_ALREADY_SPECIFED_IN_ACTION, new Object[]{"scope", _name}, parser.currentFileName(), debugInfo.start);
        }
        if (!("session".equals(value) || "page".equals(value) || "request".equals(value) || "application".equals(value))) {
          throw new JspParseException(JspParseException.INVALID_VALUE_FOR_ATTRIBUTE_IN_ACTION, new Object[]{value, "scope", _name}, parser.currentFileName(), debugInfo.start);
        }
      } else {
        throw new JspParseException(JspParseException.UNRECOGNIZED_ATTRIBUTE_IN_ACTION, new Object[]{name, _name}, parser.currentFileName(), debugInfo.start);
      }
    }

    if(!flags[0] && !flags[1]) {
      throw new JspParseException(JspParseException.JSP_INVOKE_CANNOT_CONTAIN, parser.currentFileName(), debugInfo.start);
    }

    if (!flags[2] && (flags[0] && flags[1])) {
      throw new JspParseException(JspParseException.JSP_INVOKE_SHOULD_CONTAIN, parser.currentFileName(), debugInfo.start);
    }
  }

}

