package com.sap.engine.lib.xml.parser;

/**
 * Title:        XML
 *
 * Description:  Encapsulates the static constants from the XMLParser class.
 *               Does not have methods.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
import com.sap.engine.lib.xml.parser.helpers.CharArray;

public interface XMLParserConstants {

  // Markup defines
  public static final int M_COMMENT = 1;
  public static final int M_PI = 2;
  public static final int M_LT = 3;
  public static final int M_CHREFX = 4;
  public static final int M_CHREFD = 5;
  public static final int M_ENTREF = 6;
  public static final int M_PEREF = 7;
  public static final int M_CHDATA = 8;
  public static final int M_CDSECT = 9;
  public static final int M_ENDEL = 10;
  public static final int M_DTDELEMENT = 11;
  public static final int M_DTDATTLIST = 12;
  public static final int M_DTDENTITY = 13;
  public static final int M_DTDNOTATION = 14;
  public static final int M_OTHER = 15;
  public static final int M_CONTREF = 16;
  public static final int M_DOCTYPE = 17;
  public static final int M_XMLDECL = 18;
  public static final int M_CLEAR = 19;
  public static final int M_DTDINTEND = 20;
  // String constants
  public static final char caEmptyElement[] = {'/', '>', };
  public static final char caEndOfTag[] = {'<', '/', };
  public static final byte caStartPI[] = {(byte) '<', (byte) '?', };
  public static final char caEndPI[] = {'?', '>', };
  public static final char caEndCDATA[] = {']', ']', '>', };
  public static final char[] caA_CDATA = {'C', 'D', 'A', 'T', 'A', };
  public static final char[] caA_REF = {'R', 'E', 'F', };
  public static final char[] caA_ENTIT = {'E', 'N', 'T', 'I', 'T', };
  public static final char[] caA_MTOKEN = {'M', 'T', 'O', 'K', 'E', 'N', };
  public static final char[] caA_OTATION = {'O', 'T', 'A', 'T', 'I', 'O', 'N', };
  public static final char[] caA_REQUIRED = {'R', 'E', 'Q', 'U', 'I', 'R', 'E', 'D', };
  public static final char[] caA_IMPLIED = {'I', 'M', 'P', 'L', 'I', 'E', 'D', };
  public static final char[] caA_FIXED = {'F', 'I', 'X', 'E', 'D', };
  public static final char[] caPUBLIC = {'P', 'U', 'B', 'L', 'I', 'C', };
  public static final char[] caSYSTEM = {'S', 'Y', 'S', 'T', 'E', 'M', };
  public static final char[] caA_IES = {'I', 'E', 'S', };
  public static final char[] caE_NDATA = {'N', 'D', 'A', 'T', 'A', };
  public static final char[] caCommentStart = {'-', };
  public static final char[] caStartCDATA = {'[', 'C', 'D', 'A', 'T', 'A', '[', };
  public static final char[] caDTDElement = {'L', 'E', 'M', 'E', 'N', 'T', };
  public static final char[] caDTDEntity = {'N', 'T', 'I', 'T', 'Y', };
  public static final char[] caDTDAttList = {'A', 'T', 'T', 'L', 'I', 'S', 'T', };
  public static final char[] caDTDNotation = {'N', 'O', 'T', 'A', 'T', 'I', 'O', 'N', };
  public static final char[] caCommentEnd = {'-', '-', '>', };
  public static final char[] caComment2Dash = {'-', '-', };
  public static final char[] caComment = {'-', '-', };
  public static final char[] caXmlDeclFull = {'<', '?', 'x', 'm', 'l', };
  public static final char[] caXmlDeclRest = {'m', 'l', };
  public static final char[] caRefX = {'&', '#', 'x', };
  public static final char[] caRefD = {'&', '#', };
  public static final char[] caRefEnt = {'&', };
  public static final char[] caRefPE = {'%', };
  public static final char[] caDoctype = {'D', 'O', 'C', 'T', 'Y', 'P', 'E', };
  public static final char[] caGNORE = {'G', 'N', 'O', 'R', 'E'};
  public static final char[] caNCLUDE = {'N', 'C', 'L', 'U', 'D', 'E'};
  public static final String XMLDECL = "xml";
  public static final CharArray caXMLNS = new CharArray("xmlns").setStatic();
  public static final String sXMLNamespace = "http://www.w3.org/XML/1998/namespace";
  public static final CharArray caXMLNamespace = new CharArray("http://www.w3.org/XML/1998/namespace").setStatic();
  public static final CharArray crXMLBase = new CharArray("xml:base").setStatic();
  public static final CharArray crXMLLang = new CharArray("xml:lang").setStatic();
  public static final CharArray crLang = new CharArray("lang").setStatic();
  public static final CharArray crXML = new CharArray("xml").setStatic();
  public static final CharArray crXMLNSNamespace = new CharArray("http://www.w3.org/2000/xmlns/").setStatic();
  public static final String sXMLNSNamespace = "http://www.w3.org/2000/xmlns/";
  public static final CharArray crXMLSpace = new CharArray("xml:space").setStatic();
  public static final String sXMLBase = "xml:base";
  public static final String sXMLLang = "xml:lang";
  public static final String sXMLSpace = "xml:space";
  public static final String sSpecified = "SP";
  public static final String sNotSpecified = "NSP";

}

