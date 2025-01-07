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

import java.util.Arrays;

import com.sap.engine.services.servlets_jsp.jspparser_api.exception.JspParseException;
import com.sap.engine.services.servlets_jsp.lib.jspruntime.FunctionMapperImpl;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.Indentifier;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.InnerExpression;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.JspElement;
import com.sap.engine.services.servlets_jsp.jspparser_api.jspparser.syntax.JspTag;

/*
 *
 * @author Galin Galchev,Ivo Simeonov
 * @version 4.0
 */
public class SetPropertyTag extends CustomJspTag {

  /*
   * all attributes that this element can
   * accept
   */
  private static final char[][] attributeNames = {"name".toCharArray(), "property".toCharArray(), "param".toCharArray(), "value".toCharArray(), };

  /**
   * Creates new SetPropertyTag
   *
   */
  public SetPropertyTag() {
    super();
    _name = "setProperty";
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
    switch (tagType) {
      case SINGLE_TAG: {
        String property = getAttributeValue("property");
        String name = getAttributeValue("name");

        if (property.equalsIgnoreCase("*")) {
          if (parser.isInner()) {
            debugInfo.writeStart(buffer, false);
            buffer.append("\t\t\ttry{\r\n");
            buffer.append("\t\t\t\tcom.sap.engine.services.servlets_jsp.lib.jspruntime.RunLibrary.introspect(_jspx_pageContext.findAttribute(\"").append(name).append("\"),request);\r\n");
            buffer.append("\t\t\t} catch (OutOfMemoryError o) {\r\n");
            buffer.append("\t\t\t\tthrow o;\r\n");
            buffer.append("\t\t\t} catch (ThreadDeath tde) { throw tde;\r\n");
            buffer.append("\t\t\t} catch (Throwable t) {\r\n");
            buffer.append("\t\t\t\ttry {\r\n");
            buffer.append("\t\t\t\t\tout.clear();\r\n");
            buffer.append("\t\t\t\t} catch(java.io.IOException _jspioex) {}\r\n");
            buffer.append("\t\t\t\trequest.setAttribute(\"javax.servlet.jsp.jspException\",t );\r\n");
            buffer.append("\t\t\t\tthrow new ServletException(\"ID018218: Unknown exception: \"+t.toString());\r\n");
            buffer.append("\t\t\t}\r\n");
          } else {
            debugInfo.writeStart(buffer, false);
            buffer.append("\t\t\t\tcom.sap.engine.services.servlets_jsp.lib.jspruntime.RunLibrary.introspect(_jspx_pageContext.findAttribute(\"").append(name).append("\"),request);\r\n");
          }
        } else {
          String param = getAttributeValue("param");
          String value;

          if (!(param == null || param.equals("") || param.equalsIgnoreCase("null"))) {

          } else {
            param = property;
          }

          value = getAttributeValue("value");
          StringBuffer temp = new StringBuffer();

          if (param != property) {
            temp.append("\t\t\t\t");
            temp.append("com.sap.engine.services.servlets_jsp.lib.jspruntime.RunLibrary.introspecthelper(_jspx_pageContext." +
              "findAttribute(\"" + name + "\"), \"" + property +
              "\", request.getParameterValues(\"" + param + "\"));");
            temp.append("\r\n");
          } else if (value != null) {
            if (!isRuntimeExpr(value)) {
              buffer.append("\t\t\tcom.sap.engine.services.servlets_jsp.lib.jspruntime.RunLibrary.setProp(").append(name).append(", \"").append(param).append("\", \"").append(value).append("\");\r\n");
            } else {
              if (value.startsWith("${")) {
                String funcMapper = FunctionMapperImpl.getFunctionMapper(value, parser);
                if (funcMapper == null) {
                  funcMapper = "null";
                }
                buffer.append("\t\t\tcom.sap.engine.services.servlets_jsp.lib.jspruntime.RunLibrary.setELProp(")
                .append(name).append(", \"").append(param).append("\", ").append( InnerExpression.quote(value) ).append(", _jspx_pageContext, ").append(funcMapper).append(");\r\n");
              } else {
                temp.append("\t\t").append(name).append(".set").append(parser.firstCap(property)).append("(");
                temp.append(evaluateExpression(value));
                temp.append("\t\t").append(");\r\n");
              }
            }
          } else {
            temp.append("\t\t\t\t");
            temp.append("com.sap.engine.services.servlets_jsp.lib.jspruntime.RunLibrary.introspecthelper(_jspx_pageContext." +
              "findAttribute(\"" + name + "\"), \"" + property +
              "\", request.getParameterValues(\"" + param + "\"));");
            temp.append("\r\n");
          }

          if (parser.isInner()) {
            buffer.append("\t\t\ttry{\r\n");
            buffer.append(temp);
            buffer.append("\t\t\t} catch (ThreadDeath tde) { throw tde;\r\n");
            buffer.append("\t\t\t} catch (OutOfMemoryError o) {\r\n");
            buffer.append("\t\t\t\tthrow o;\r\n");
            buffer.append("\t\t\t} catch (Throwable t) {\r\n");
            buffer.append("\t\t\t\ttry {\r\n");
            buffer.append("\t\t\t\t\tout.clear();\r\n");
            buffer.append("\t\t\t\t} catch(java.io.IOException _jspioex) {}\r\n");
            buffer.append("\t\t\t\trequest.setAttribute(\"javax.servlet.jsp.jspException\",t );\r\n");
            buffer.append("\t\t\t\tthrow new ServletException(\"ID018220: Unknown exception: \"+t);\r\n");
            buffer.append("\t\t\t}\r\n");
            debugInfo.writeEnd(buffer);
          } else {
            buffer.append(temp);
            debugInfo.writeEnd(buffer);
          }
        }

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
   * "<jsp:setProperty"
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
    return (this.copy) ? new SetPropertyTag() : this;
  }

  /**
   * Returnes element to witch will be associated
   * current parsing.
   *
   * @return object that is associated as current element
   * of parsing
   */
  protected JspElement createJspElement() {
    return new JspElement(JspElement.SET_PROPERTY);
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
    return (JspTag) (new SetPropertyTag().parse(this.parser));
  }

  /**
   * Verifies the attributes of this tag
   *
   * @exception   JspParseException  thrown if error occures
   * during verification
   */
  public void verifyAttributes() throws JspParseException {
    if (attributes == null || attributes.length < 2) {
      throw new JspParseException(JspParseException.ACTION_MUST_HAVE_AT_LEAST_TWO_SPECIFIED_ATTRIBUTES, new Object[]{_name},
              parser.currentFileName(), debugInfo.start);
    }

    boolean[] flags = new boolean[attributeNames.length];
    Arrays.fill(flags, true);
    Indentifier e = null;
    Indentifier value = null;

    for (int i = 0; i < attributes.length; i++) {
      e = attributes[i].name;
      if (e.equals(attributeNames[0])) {
        if (flags[0]) {
          flags[0] = false;
        } else {
          throw new JspParseException(JspParseException.ATTRIBUTE_ALREADY_SPECIFED_IN_ACTION, new Object[]{"name", _name},
                  parser.currentFileName(), debugInfo.start);
        }
      } else if (e.equals(attributeNames[1])) {
        if (flags[1]) {
          flags[1] = false;
        } else {
          throw new JspParseException(JspParseException.ATTRIBUTE_ALREADY_SPECIFED_IN_ACTION, new Object[]{"property", _name},
                  parser.currentFileName(), debugInfo.start);
        }
      } else if (e.equals(attributeNames[2])) {
        if (flags[2]) {
          flags[2] = false;
        } else {
          throw new JspParseException(JspParseException.ATTRIBUTE_ALREADY_SPECIFED_IN_ACTION, new Object[]{"param", _name},
                  parser.currentFileName(), debugInfo.start);
        }
      } else if (e.equals(attributeNames[3])) {
        if (flags[3]) {
          flags[3] = false;
        } else {
          throw new JspParseException(JspParseException.ATTRIBUTE_ALREADY_SPECIFED_IN_ACTION, new Object[]{"value", _name}, parser.currentFileName(), debugInfo.start);
        }
      } else {
        throw new JspParseException(JspParseException.UNRECOGNIZED_ATTRIBUTE_IN_ACTION, new Object[]{e, _name}, parser.currentFileName(), debugInfo.start);
      }
    }

    if (flags[0] || flags[1]) {
      throw new JspParseException(JspParseException.MISSING_ATTRIBUTE_OR_ATTRIBUTE_IN_ACTION, new Object[]{"name", "property", _name}, parser.currentFileName(), debugInfo.start);
    }

    value = attributes[1].value;

    if (value.equals(new char[] {'*'})) {
      if (!flags[2] || !flags[3]) {
        throw new JspParseException(JspParseException.ATTRIBUTE_OR_ATTRIBUTE_SHOULD_NOT_BE_SPECIFED_IN_ACTION_WHEN_THE_VALUE_OF_ATTRIBUTE_IS_STAR, new Object[]{"param", "value", _name, "property"}, parser.currentFileName(), debugInfo.start);
      }
    } else {
      if (!flags[2] && !flags[3]) {
        throw new JspParseException(JspParseException.ATTRIBUTE_AND_ATTRIBUTE_CANNOT_BE_SPECIFED_TOGETHER_IN_ACTION, new Object[]{"param", "value", _name}, parser.currentFileName(), debugInfo.start);
      }
    }
  }

}

