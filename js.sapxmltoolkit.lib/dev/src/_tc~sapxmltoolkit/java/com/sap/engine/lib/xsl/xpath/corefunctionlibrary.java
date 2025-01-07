package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.functions.*;
import com.sap.engine.lib.xsl.xpath.xobjects.XBoolean;
import com.sap.engine.lib.xsl.xpath.xobjects.XNumber;
import com.sap.engine.lib.xsl.xpath.xobjects.XString;
import java.util.Hashtable;

/**
 * A collection of the core XPath functions.
 * These are the functions which do not need to be namespaced.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public class CoreFunctionLibrary implements FunctionLibrary {

  private static Hashtable functions = new Hashtable();
  public static final CharArray NAME = new CharArray("<CORE>");

  /**
   *   Maps in the Hashtable the name of a function to its instance.
   */
  protected static void p(XFunction xf) {
    functions.put(xf.getFunctionName(), xf);
  }

  static {
    p(new XBoolean());
    p(new XFCeiling());
    p(new XFConcat());
    p(new XFContains());
    p(new XFCount());
    p(new XFCurrent());
    p(new XFDetailedDTM());
    p(new XFDump());
    p(new XFDocument());
    p(new XFElementAvailable());
    p(new XFFalse());
    p(new XFFloor());
    p(new XFFormatNumber());
    p(new XFFunctionAvailable());
    p(new XFGenerateId());
    p(new XFId());
    //p(new XFKey());
    p(new XFLang());
    p(new XFLast());
    p(new XFLocalName());
    p(new XFName());
    p(new XFNamespaceUri());
    p(new XFNormalizeSpace());
    p(new XFNot());
    p(new XNumber());
    p(new XFPosition());
    p(new XFRound());
    p(new XFStartsWith());
    p(new XString());
    p(new XFStringLength());
    p(new XFSubstring());
    p(new XFSubstringAfter());
    p(new XFSubstringBefore());
    p(new XFSum());
    p(new XFSystemProperty());
    p(new XFTranslate());
    p(new XFTrue());
    //p(new XFUnparsedEntityUri());
    p(new XFOperatorPlus());
    p(new XFOperatorMinus());
    p(new XFOperatorAsterisk());
    p(new XFOperatorDiv());
    p(new XFOperatorMod());
    p(new XFOperatorAnd());
    p(new XFOperatorOr());
    p(new XFOperatorStroke());
    p(new XFOperatorEquals());
    p(new XFOperatorNotEquals());
    p(new XFOperatorGreaterThan());
    p(new XFOperatorGreaterThanOrEquals());
    p(new XFOperatorLessThan());
    p(new XFOperatorLessThanOrEquals());
    p(new XFUnparsedEntityUri());
    p(new XFKey());
    p(new XFIQNodeset());
  }
  
  public CoreFunctionLibrary() {
    super();
  }

  public XFunction getFunction(CharArray name) {
    return (XFunction) functions.get(name.toString());
  }

  public void init(String pack) {

  }

  public CharArray getName() {
    return NAME;
  }

}

