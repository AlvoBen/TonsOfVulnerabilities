package com.sap.engine.lib.xml.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import org.xml.sax.EntityResolver;

import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.SystemProperties;
import com.sap.engine.lib.xml.dom.DOMDocHandler1;
import com.sap.engine.lib.xml.parser.dtd.ElementContentValidator;
import com.sap.engine.lib.xml.parser.dtd.ValidationException;
import com.sap.engine.lib.xml.parser.handlers.INamespaceHandler;
import com.sap.engine.lib.xml.parser.handlers.NamespaceHandler;
import com.sap.engine.lib.xml.parser.helpers.AdvancedXMLStreamReader;
import com.sap.engine.lib.xml.parser.helpers.Attribute;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.CharArrayStack;
import com.sap.engine.lib.xml.parser.helpers.DefaultAttributesHandler;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.helpers.Reference;
import com.sap.engine.lib.xml.parser.helpers.UTF8Encoding;
import com.sap.engine.lib.xml.parser.pool.AttributePool;
import com.sap.engine.lib.xml.parser.pool.CharArrayPool;
import com.sap.engine.lib.xml.parser.pool.ReferencePool;
import com.sap.engine.lib.xml.parser.readers.EncodedDataReader;
import com.sap.engine.lib.xml.parser.readers.SOAPXMLStream;
import com.sap.engine.lib.xml.util.ReaderInputStream;

/**
 * @author  Vladimir Savtchenko
 *
 * @version Apr 2001
 */
public final class XMLParser extends AbstractXMLParser implements XMLParserConstants {

  // Reusable data
  private Vector vScanElementAttList = new Vector();
  private CharArray carr = new CharArray(100);
//  private CharArray carr1 = new CharArray(100);
  private CharArray carr2 = new CharArray(100);
//  private CharArray carr3 = new CharArray(100);
  private CharArray carrNS[] = {new CharArray(20), new CharArray(20), new CharArray(20), };
  private CharArray carrNS0 = carrNS[0];
  private CharArray carrNS1 = carrNS[1];
  private CharArray carrNS2 = carrNS[2];
//  private CharArray ct = null; //used in scanQName
  private CharArray carrNSAtt[] = {new CharArray(20), new CharArray(20), new CharArray(20), };
//  private CharArray carrAttValue = new CharArray(100);
  public DocHandler docHandler = null;
  // Handlers & Readers
  private Hashtable pentities = new Hashtable();
  private Hashtable gentities = new Hashtable();
  private CharArrayStack caTagStack = new CharArrayStack();
  private DefaultAttributesHandler defaultAttributesHandler = new DefaultAttributesHandler();
  private EntityResolver entityResolver = urlLoader;
  // Pools
  private AttributePool fAttributePool = new AttributePool(15, 10);
  private CharArrayPool fCharArrayPool = new CharArrayPool(15, 10);
  private ReferencePool fReferencePool = new ReferencePool(15, 10);
  // Location data
  private boolean hasDTD = false;
  private boolean inDTD = false;
  private boolean bEndDoc = false;
  private boolean bExtSubset = false;
  private boolean bDocFinished = false;
  // Wellformed data
  private Vector vTagList = new Vector();
//  private boolean gotXMLDecl = false;
//  private boolean gotXMLDeclEx = false;
  private boolean xmlDeclAllowed = false;
//  private boolean bAllowISChange = true;
  private ElementContentValidator elementContentValidator = new ElementContentValidator();
  public Hashtable attributeTypes = new Hashtable();
  int ch_scans;
  public final static String AT_NMTOKEN = "N";
  public final static String AT_CDATA = "C";

  private int maximumReferencesCount = 10000;
  private int referencesProcessed = 0;

  public XMLParser() throws ParserException {
  	caXMLNS.bufferHash();
    init();
  }

  public void init() throws ParserException {
    needXMLDeclVersion = true;
    hasDTD = false;
    inDTD = false;
    bEndDoc = false;
    bExtSubset = false;
    bDocFinished = false;
    referencesProcessed = 0;
    vScanElementAttList.clear();
    pentities.clear();
    gentities.clear();

    if (isInitializeNamespaceHandler()) {
      getNamespaceHandler().reuse(this);
    }

    //    namespaceHandler.add("xml:lang", "utf-8");
    vTagList.clear();
    caTagStack.reuse();

    //    xmlValidator = new XMLValidator(this);
    if (defaultAttributesHandler == null) {
      defaultAttributesHandler = new DefaultAttributesHandler();
    } else {
      defaultAttributesHandler.reuse();
    }

    xmlValidator.reuse();
    //urlLoader.init();
    attributeTypes.clear();
    //bScanContentOnly = false;
    carrNS[0] = carrNS0;
//    carrNS[1] = carrNS1;
//    carrNS[2] = carrNS2;
  }

  private static int id = 10000;
  public String filename = null;
  public InputStream original_is = null;
  public InputStream fs_is = null;
  /**
   * All parse methods are directed to this one.
   */
  protected void parse0(InputStream is, String sourceID, DocHandler docHandler) throws Exception {
    try {
      {
        if (!"".equals(SystemProperties.getProperty("xmltoolkit.dump_parsing", ""))) {
          int myid;
          synchronized (XMLParser.class) {
            myid = id++;
  
          }
          filename = new File("").getAbsolutePath() + "\\xml\\xml"  + ",id=" + myid + ",inst=" + this.hashCode() + ",thread=" + Thread.currentThread().hashCode() + ".xml";
          new File(filename).getParentFile().mkdirs();
          //LogWriter.getSystemLogWriter().println("filename=" + filename);
          FileOutputStream ftrace = new FileOutputStream(filename + ".trace");
          PrintStream ps = new PrintStream(ftrace);
          FileOutputStream fo = new FileOutputStream(filename);
          int len = 0;
          if (is instanceof ReaderInputStream) {
            ps.println("Reading from reader");
            ReaderInputStream r = (ReaderInputStream) is;
            char buf[] = new char[5000];
            while ((len = r.read(buf, 0, buf.length)) != -1) {
              ps.println("Read " + len + " bytes");
              fo.write(new String(buf, 0, len).getBytes("UTF-8"));
            }
          } else {
            byte buf[] = new byte[5000];
            ps.println("Reading from InputStream");
            while ((len = is.read(buf)) != -1) {
              ps.println("Read " + len + " bytes");
              fo.write(buf, 0, len);
            }
          }
          fo.close();
          ps.println("File parsed at: " + new GregorianCalendar().getTime());
          ps.println("Default Encoding is: " + SystemProperties.getProperty("file.encoding"));
          ps.println("Location Parsed at: " );
          try {
            throw new Exception("ignore");
          } catch (Exception e) {
            e.printStackTrace(ps);
            ps.close();
            ftrace.close();
          }
          original_is = is;
          if (new File(filename).length() < 60000 ) {
            byte[] membuf = new byte[60000];
            InputStream i2 = new FileInputStream(filename);
            int l = i2.read(membuf);
            i2.close();
            is = new ByteArrayInputStream(membuf, 0, l);
          } else {
            is = new FileInputStream(filename);
          }
          fs_is = is;
        }
      }
      init();
      docHandler.onCustomEvent(DocHandler.NAMESPACE_AWARENESS, getNamespaces() ? Boolean.TRUE : Boolean.FALSE);
  
      if (getSoapProcessing()) {
        if (this.is != null && this.is instanceof SOAPXMLStream) {
          ((SOAPXMLStream) this.is).reuse();
        } else {
          this.is = new SOAPXMLStream(this);
        }
      } else {
        //this.is = new AdvancedXMLStreamReader(this);
        if (this.is != null && this.is instanceof AdvancedXMLStreamReader) {
          ((AdvancedXMLStreamReader) this.is).reuse(this);
        } else {
          this.is = new AdvancedXMLStreamReader(this);
        }
  
        xmlValidator.setXMLReader((AdvancedXMLStreamReader) this.is);
      }
  
      if (docHandler instanceof DOMDocHandler1) {
        ((DOMDocHandler1) docHandler).setDOMTrimWhiteSpaces(getDOMTrimWhiteSpaces());
      }
  
      this.is.addInputFromInputStream(is, new CharArray(sourceID));
      this.docHandler = docHandler;
  
      // if this is set to true then the parser supposes that the xml consits only of the content
      // and someone has called the proper StartDocument, etc methods of the DocHandler
      // and he will call also the EndDocument method
      if (getScanContentOnly()) {
        scanContent();
      } else {
        scanDocument();
      }
  
      if (bActiveParse == false) {
        if (getUseCaches()) {
          urlLoader.storeIndex();
        }
      }
    } finally {
      getNamespaceHandler().reuse(this);
      setAdditionalDTDLocation(null);
    }
  }

  public int checkMarkup() throws Exception {
    try {
      int ch;
      ch = is.getLastChar();

      //      LogWriter.getSystemLogWriter().println("XP.checkMarkup 1: " + Integer.toHexString(ch));
      switch (ch) { //$JL-SWITCH$
        case '<': { //$JL-SWITCH$
          ch = is.read();

          switch (ch) { //$JL-SWITCH$
            case '/': { //$JL-SWITCH$
              return M_ENDEL;
            }
            case '!': { //$JL-SWITCH$
              ch = is.read();

              switch (ch) { //$JL-SWITCH$
                case '-': { //$JL-SWITCH$ 
                  return M_COMMENT;
                }
                case 'E': { //$JL-SWITCH$
                  ch = is.read();

                  switch (ch) { //$JL-SWITCH$
                    case 'L': { //$JL-SWITCH$
                      return M_DTDELEMENT;
                    }
                    case 'N': { //$JL-SWITCH$
                      return M_DTDENTITY;
                    }
                    default: { //$JL-SWITCH$
                      return M_OTHER;
                    }
                  }
                }
                case 'A': { //$JL-SWITCH$
                  return M_DTDATTLIST;
                }
                case 'N': { //$JL-SWITCH$
                  return M_DTDNOTATION;
                }
                case 'D': { //$JL-SWITCH$
                  return M_DOCTYPE;
                }
                case '[': { //$JL-SWITCH$
                  return M_CDSECT;
                }
                default: { //$JL-SWITCH$
                  return M_OTHER;
                }
              }
            }
            case '?': { //$JL-SWITCH$
              is.read();
              return M_PI;
            }
            default: { //$JL-SWITCH$
              return M_LT;
            }
          }
        }
        case '&': { //$JL-SWITCH$
          return M_CONTREF;
        }
        case '%': { //$JL-SWITCH$
          return (inDTD) ? M_PEREF : M_CHDATA;
        }
        case ']': { //$JL-SWITCH$
          return (inDTD) ? M_DTDINTEND : M_CHDATA;
        }
        default: { //$JL-SWITCH$
          return M_CHDATA;
        }
      }
    } catch (Exception e) {
      ParserEOFException parserEOFException = new ParserEOFException(e.getMessage());
      parserEOFException.setStackTrace(e.getStackTrace());
      throw parserEOFException;
    }
  }

  private boolean readText(CharArray lcarr) throws Exception {
    char cs = (char) is.getLastChar();
    char aa;
    lcarr.clear();
    if (cs != '\'' && cs != '\"') {
      throw new ParserException("Expected ' or \".", is.getID(), is.getRow(), is.getCol());
    }
    while (((aa = is.read()) != cs)) {
      lcarr.append(aa);
      if (bDocFinished == true) {
        throw new ParserException("Expected ' or \".", is.getID(), is.getRow(), is.getCol());
      }
    }
    is.read();
    return true;
  }

  private int colonChar = -1; // used by scanName to indicate where is the : - optimization purpose

  public void scanQName(CharArray qname, CharArray prefix, CharArray localname, boolean doClear) throws Exception {
    colonChar = -1;
    scanName(qname, doClear);

    if (colonChar > -1) {
      prefix.substring(qname, 0, colonChar);
      localname.substring(qname, colonChar + 1);
    } else {
      prefix.clear();
      localname.substring(qname, 0);
    }
  }
  
  public void scanName(CharArray chr, boolean doClear) throws Exception {
    scanName(chr, doClear, true);
  }

  public void scanName(CharArray chr, boolean doClear, boolean allowReferences) throws Exception {
    colonChar = -1;
    if (doClear) {
      chr.clear();   
    }
    if (bSoapProcessing) {
   	  colonChar = ((SOAPXMLStream)is).scanName(chr);
      return;
    }
    
    char ch = (char) is.getLastChar();

    if (ch == '%') {
      if (!allowReferences) {
        throw new ParserException("It is not allowed to use the character '%' inside a name.", is.getID(), is.getRow(), is.getCol());
      }
      handleContentReference(false);
      is.scanS();
      ch = is.getLastChar();
    }

    if (Character.isLetter(ch) || ch == '_' || ch == ':') {
      chr.append(ch);

        boolean notdone = true;
        while (notdone) {  //(Symbols.isLetterOrDigit((ch = is.read())) || ch == '_' || ch == ':' || ch == '.' || ch == '-' || ch == '|')) {
        	ch = is.read();
      		if ((ch >= 'a' && ch <='z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')) {
  			   chr.append(ch);
      			continue;
      		}
        	switch (ch) { //$JL-SWITCH$
            case ' ': //$JL-SWITCH$
            case '>': //$JL-SWITCH$
            case '=': //$JL-SWITCH$
  			    case '\"': //$JL-SWITCH$
        		case '\'': //$JL-SWITCH$
              notdone = false; continue; 
  			    case ':': //$JL-SWITCH$
              colonChar = chr.length(); 
        		case '_': //$JL-SWITCH$
        		case '.': //$JL-SWITCH$
        		case '-': //$JL-SWITCH$
        		case '|': //$JL-SWITCH$ 
              chr.append(ch);  
              continue; 
        	}

      		if (Character.isLetterOrDigit(ch)) {
      			chr.append(ch);
      			continue;
      		}
      		break;
        }
      
    } else {
      throw new ParserException("Name expected: 0x" + Integer.toHexString(ch), is.getID(), is.getRow(), is.getCol());
    }
  }

/*  public void scanName(CharArray chr) throws Exception {
    chr.clear();
    char ch = (char) is.getLastChar();

    if (ch == '%') {
      scanReference();
      is.scanS();
      ch = is.getLastChar();
    }

    if (Character.isLetter(ch) || ch == '_' || ch == ':') {
      chr.append(ch);
      while ((Symbols.isLetterOrDigit((ch = is.read())) || ch == '_' || ch == ':' || ch == '.' || ch == '-' || ch == '|')) {
        chr.append(ch);
      }
    } else {
      throw new ParserException("Name expected ", is.getID(), is.getRow(), is.getCol());
    }
  }*/

  private void scanDTDElement() throws Exception {
    if (!is.scanChars(caDTDElement)) {
      throw new ParserException("XMLParser: Bad DTD Element (space required)", is.getID(), is.getRow(), is.getCol());
    }

    if (!is.scanS()) {
      throw new ParserException("XMLParser: Bad DTD Element (space required)", is.getID(), is.getRow(), is.getCol());
    }

    scanName(carrNS[0], true);

    if (!is.scanS()) {
      throw new ParserException("XMLParser: Bad DTD Element Definition. Space required after name", is.getID(), is.getRow(), is.getCol());
    }

    char cs;
    carr2.clear();
    try {
      while (true) {
        cs = (char) is.getLastChar();

        if (cs == '>') {
          is.read();
          docHandler.onDTDElement(carrNS[0], carr2);

          if (!elementContentValidator.check(carr2)) {
            throw new ParserException(elementContentValidator.toString(), is.getID(), is.getRow(), is.getCol());
          }

          if (bValidation) {
            getXMLValidator().onDTDElement(carrNS[0], carr2);
          }

          return;
        } else if (cs == '%') {
          handleContentReference(false);
          cs = (char) is.getLastChar();
        }

        carr2.append(cs);
        cs = is.read();
      }
    } catch (ParserValidationException e) {
      throw new ParserException(e.getMessage(), is.getID(), is.getRow(), is.getCol());
    } catch (ParserEOFException e) {
      throw new ParserException("XMLParser: EOF occured while parsing DTD Element", is.getID(), is.getRow(), is.getCol());
    }
  }

  private char checkPE(char ch) throws Exception {
    if (ch == '%' && inDTD) {
      handleDTDReference(true);
      ch = is.getLastChar();
    }

    return ch;
  }

//  private boolean checkPESpace() throws Exception {
//    char ch = is.getLastChar();
//
//    if (ch == '%' && inDTD) {
//      handleDTDReference(true);
//      return true;
//    }
//
//    return false;
//  }

  private void scanDTDAttList() throws Exception {
    String sType = null;
    String sNote;
    String sDefDecl = null;
//    String sAttValue;
//    Vector[] vAttValue = new Vector[1];
    if (!is.scanChars(caDTDAttList)) {
      return;
    }
    checkPE(is.getLastChar());
    if (!is.scanS()) {
      throw new ParserException("XMLParser: Bad DTD Attribute list (no space)", is.getID(), is.getRow(), is.getCol());
    }
    checkPE(is.getLastChar());
    scanName(carrNS[0], true);
    is.scanS();
    checkPE(is.getLastChar());
    docHandler.onDTDAttListStart(carrNS[0]);

    if (bValidation) {
      getXMLValidator().onDTDAttListStart(carrNS[0]);
    }

    is.scanS();

    if (is.scanByte('>')) {
      docHandler.onDTDAttListEnd();

      if (bValidation) {
        getXMLValidator().onDTDAttListEnd();
      }

      return;
    }

    while (true) {
      is.scanS();
      sNote = "";
      handleContentReference(false);
      is.scanS();

      if (is.scanByte('>')) {
        docHandler.onDTDAttListEnd();

        if (bValidation) {
          getXMLValidator().onDTDAttListEnd();
        }

        return;
      }

      char cs;
      //LogWriter.getSystemLogWriter().println("scanDTDAttlist. lastchar=" + is.getLastChar());
      handleContentReference(false);
      scanQName(carrNSAtt[0], carrNSAtt[1], carrNSAtt[2], true);
      if (!is.scanS()) {
        throw new ParserException("XMLParser: Bad DTD Attribute list (no space after name)", is.getID(), is.getRow(), is.getCol());
      }
      handleContentReference(false);
      char ch = is.getLastChar();

      if (ch == 'C') {
        if (!is.scanChars(caA_CDATA)) {
          throw new ParserException("XMLParser: CDATA expected", is.getID(), is.getRow(), is.getCol());
        }

        sType = "CDATA";
      } else if (ch == 'I') {
        is.read();
        if (!is.scanByte('D')) {
          throw new ParserException("XMLParser: ID, IDREF, IDREFS expected", is.getID(), is.getRow(), is.getCol());
        }
        ch = is.getLastChar();

        if (ch != 'R') {
          sType = "ID";
        } else {
          if (!is.scanChars(caA_REF)) {
            throw new ParserException("XMLParser: IDREF, IDREFS expected", is.getID(), is.getRow(), is.getCol());
          }

          if (is.scanByte('S')) {
            sType = "IDREFS";
          } else {
            sType = "IDREF";
          }
        }
      } else if (ch == 'E') {
        if (!is.scanChars(caA_ENTIT)) {
          throw new ParserException("XMLParser: ENTITY, ENTITIES expected", is.getID(), is.getRow(), is.getCol());
        }

        if (is.scanByte('Y')) {
          sType = "ENTITY";
        } else {
          if (!is.scanChars(caA_IES)) {
            throw new ParserException("XMLParser: ENTITIES expected", is.getID(), is.getRow(), is.getCol());
          }
          sType = "ENTITIES";
        }
      } else if (ch == 'N') {
        ch = is.read();

        if (ch == 'M') {
          if (!is.scanChars(caA_MTOKEN)) {
            throw new ParserException("XMLParser: NMTOKEN/S expected", is.getID(), is.getRow(), is.getCol());
          }

          if (is.scanByte('S')) {
            sType = "NMTOKENS";
          } else {
            sType = "NMTOKEN";
          }
        } else if (ch == 'O') {
          if (!is.scanChars(caA_OTATION)) {
            throw new ParserException("XMLParser: NOTATION/S expected", is.getID(), is.getRow(), is.getCol());
          }
          sType = "NOTATION";
          sNote = "";
          if (is.scanS() == false) {
            throw new ParserException("bad DTD Attribute list 2", is.getID(), is.getRow(), is.getCol());
          }
          cs = is.getLastChar();
          cs = checkPE(cs);
          if (cs != '(') {
            throw new ParserException("bad DTD Attribute list 3", is.getID(), is.getRow(), is.getCol());
          }
          cs = checkPE(cs);
          sNote += cs;

          while ((cs = is.read()) != ')') {
            cs = checkPE(cs);
            sNote += cs;
          }

          sNote += ')';
          is.read();
        }
      } else if (ch == '(') {
        sType = "ENUMERATION";
        sNote = "";
        sNote += '(';
        cs = is.getLastChar();
        cs = checkPE(cs);

        while ((cs = is.read()) != ')') {
          cs = checkPE(cs);
          sNote += cs;
        }

        sNote += cs;
        sType = sNote;
        is.read();
      } else {
        throw new ParserException("XMLParser: Bad ATTLIST Type", is.getID(), is.getRow(), is.getCol());
      }

      boolean sp = is.scanS();
      ch = checkPE(is.getLastChar());
      sp = is.scanS() || sp;
      if (sp == false) {
        throw new ParserException("XMLParser: Bad ATTLIST, space required after type", is.getID(), is.getRow(), is.getCol());
      }
      CharArray crav = fCharArrayPool.getObject().reuse();
      ch = is.getLastChar();

      if ((ch = is.getLastChar()) == '#') {
        ch = is.read();

        if (ch == 'R') {
          if (!is.scanChars(caA_REQUIRED)) {
            throw new ParserException("XMLParser: Bad ATTLIST, #REQUIRED expected", is.getID(), is.getRow(), is.getCol());
          }
          sDefDecl = "#REQUIRED";
        } else if (ch == 'I') {
          if (!is.scanChars(caA_IMPLIED)) {
            throw new ParserException("XMLParser: Bad ATTLIST, #IMPLIED expected", is.getID(), is.getRow(), is.getCol());
          }
          sDefDecl = "#IMPLIED";
        } else if (ch == 'F') {
          if (!is.scanChars(caA_FIXED)) {
            throw new ParserException("XMLParser: Bad ATTLIST, #FIXED expected", is.getID(), is.getRow(), is.getCol());
          }
          sDefDecl = "#FIXED";
          if (!is.scanS()) {
            throw new ParserException("XMLParser: Bad ATTLIST, space required after type", is.getID(), is.getRow(), is.getCol());
          }
          scanAttValue(crav, true);
          defaultAttributesHandler.add(carrNS[0], getCharArrayRawName(carrNSAtt), getCharArrayPrefix(carrNSAtt), getCharArrayLocalName(carrNSAtt), crav);
        }
      } else {
        sDefDecl = "";
        scanAttValue(crav, true);

        defaultAttributesHandler.add(carrNS[0], getCharArrayRawName(carrNSAtt), getCharArrayPrefix(carrNSAtt), getCharArrayLocalName(carrNSAtt), crav);
      }

      Hashtable h1 = (Hashtable) attributeTypes.get(carrNS[0]);

      if (h1 == null) {
        h1 = new Hashtable();
        attributeTypes.put(carrNS[0].copy(), h1);
      }

      h1.put(carrNSAtt[0].copy(), sType);
      //      if (sType.equals("CDATA")) {
      //  //        LogWriter.getSystemLogWriter().println("XMLParser.scanDTDAttlist: name=" + carrNS[0] + ", isCDATA = true");
      //        attributeTypes.put(carrNSAtt[0].copy(), AT_CDATA);
      //      } else {
      //  //        LogWriter.getSystemLogWriter().println("XMLParser.scanDTDAttlist: name=" + carrNS[0] + ", isCDATA = false");
      //        attributeTypes.put(carrNSAtt[0].copy(), AT_NMTOKEN);
      //      }
      docHandler.onDTDAttListItem(carrNS[0], carrNSAtt[0], sType, sDefDecl, crav, sNote);

      if (bValidation) {
        getXMLValidator().onDTDAttListItem(carrNS[0], carrNSAtt[0], sType, sDefDecl, crav, sNote);
      }

      fCharArrayPool.releaseObject(crav);
    }
  }

  public void scanEntityValue(CharArray value) throws Exception {
    char cs = is.getLastChar();
    char aa;

    if ((cs == '\"') || (cs == '\'')) {
      aa = is.read();

      while (is.getLiteral() || (aa != cs)) {
        if ((aa == '&') || (aa == '%')) {
          Reference ref = null;
          ref = scanReference();

          if (ref.getType() == Reference.RX) {
            value.append(ref.getChar());
          } else if (ref.getType() == Reference.RD) {
            value.append(ref.getChar());
          } else if (ref.getType() == Reference.RENT) {
            value.append('&');
            value.append(ref.getName());
            value.append(';');
          } else if (ref.getType() == Reference.RPE) {
            Entity pent = getPEntity(ref.getName());

            if (pent.getValue() != null && pent.getValue().length() > 0) {
              makePEReplacement(ref);
              is.setLiteral(true);
            }
          }

          if (ref.getType() != Reference.RX && ref.getType() != Reference.RD) {
            fCharArrayPool.releaseObject(ref.getName());
          }

          fReferencePool.releaseObject(ref);
        } else if (bDocFinished == true) {
          throw new ParserException("Entity value is not closed. End Of File reached.", is.getID(), is.getRow(), is.getCol());
        } else {
          value.append(aa);
          is.read();
        }

        aa = is.getLastChar();
      }

      aa = is.read();
    }
  }

  private void scanDTDEntity() throws Exception {
//    Vector vEntityValue = new Vector();
    boolean pe = false;

    if (!is.scanChars(caDTDEntity)) {
      throw new ParserException("XML Error: <!ENTITY expected", is.getID(), is.getRow(), is.getCol());
    }

    CharArray caEntityValue = new CharArray();
    CharArray caName = new CharArray();
    CharArray caPub = new CharArray();
    CharArray caSys = new CharArray();
    CharArray caNoteName = new CharArray();
//    int caNameHash = 0;

    if (!is.scanS()) {
      throw new ParserException("XML Error: Bad DTD Entity - no space after \'<!ENTITY\'", is.getID(), is.getRow(), is.getCol());
    }

    if (is.scanByte('%')) {
      pe = true;
      if (!is.scanS()) {
        throw new ParserException("XML Error: Bad DTD PE-Entity - no space after \'%\'", is.getID(), is.getRow(), is.getCol());
      }
    }

    scanName(caName, true);
    caName.bufferHash();
    if (!is.scanS()) {
      throw new ParserException("XML Error: Bad DTD Entity - no space after name", is.getID(), is.getRow(), is.getCol());
    }
    // Check if this is an internal entity, that is - the entity definition
    // is a EntityValue (begins with ' or ")
    char cs = is.getLastChar();

    if ((cs == '\"') || (cs == '\'')) {
      scanEntityValue(caEntityValue);

      if (caEntityValue.length() == 1 && caEntityValue.charAt(0) == '<' && !caName.equals("lt")) {
        throw new ParserException("'<' encounted in entity value. According to XMLSpec 1.0 it is not allowed to exist there, due to wellformednedd constraints", is.getID(), is.getRow(), is.getCol());
      }

      is.scanS();

      if (!is.scanByte('>')) {
        throw new ParserException("bad DTD Entity " + caEntityValue.getString() + " --- " + is.read() + is.read() + is.read() + is.read() + is.read() + is.read() + is.read() + is.read(), is.getID(), is.getRow(), is.getCol());
      }

      Entity ent = new Entity(caName, caEntityValue, pe, urlLoader);
      fCharArrayPool.releaseObject(caEntityValue);
      fCharArrayPool.releaseObject(caName);
      fCharArrayPool.releaseObject(caPub);
      fCharArrayPool.releaseObject(caSys);
      fCharArrayPool.releaseObject(caNoteName);
      ent.getName().bufferHash();

      if (pe) {
        if (pentities.get(ent.getName()) == null) {
          pentities.put(ent.getName(), ent);
        }
      } else {
        if (gentities.get(ent.getName()) == null) {
          gentities.put(ent.getName(), ent);
        }
      }

      docHandler.onDTDEntity(ent);

      if (bValidation) {
        getXMLValidator().onDTDEntity(ent);
      }
    } else {
      scanPublicExternalID(caPub, caSys, bHTMLMode);
      boolean hadSpace = is.scanS();

      if (is.getLastChar() == '>') {
        is.read();
        Entity ent = new Entity(caName, caEntityValue, pe, caPub, caSys, caNoteName, urlLoader.peek(), urlLoader);

        if (pe) {
          if (pentities.get(ent.getName()) == null) {
            pentities.put(ent.getName(), ent);
          }
        } else {
          if (gentities.get(ent.getName()) == null) {
            gentities.put(ent.getName(), ent);
          } else {

          }
        }

        docHandler.onDTDEntity(ent);

        if (bValidation) {
          getXMLValidator().onDTDEntity(ent);
        }

        fCharArrayPool.releaseObject(caEntityValue);
        fCharArrayPool.releaseObject(caName);
        fCharArrayPool.releaseObject(caNoteName);
        fCharArrayPool.releaseObject(caPub);
        fCharArrayPool.releaseObject(caSys);
        return;
      } else if (!is.scanChars(caE_NDATA) || !hadSpace) {
        throw new ParserException("XMLParser: Bad DTDEntity - bad NDATA declaration", is.getID(), is.getRow(), is.getCol());
      }

      if (!is.scanS()) {
        throw new ParserException("bad DTD Entity", is.getID(), is.getRow(), is.getCol());
      }

      if (pe) {
        throw new ParserException("Cannot use the NDATA keyword in a parameter entity declaration", is.getID(), is.getRow(), is.getCol());
      }

      scanName(caNoteName, true);
      //LogWriter.getSystemLogWriter().println("XMLParser.scanDTDEntity: notename = " +caNoteName);
      is.scanS();
      if (is.getLastChar() != '>') {
        throw new ParserException("bad DTD Entity", is.getID(), is.getRow(), is.getCol());
      }
      is.read();
      Entity ent = new Entity(caName, caEntityValue, pe, caPub, caSys, caNoteName, urlLoader.peek(), urlLoader);

      if (pe) {
        if (pentities.get(caName) == null) {
          pentities.put(caName, ent);
        }
      } else {
        if (gentities.get(caName) == null) {
          gentities.put(ent.getName(), ent);
        }
      }

      docHandler.onDTDEntity(ent);

      if (bValidation) {
        getXMLValidator().onDTDEntity(ent);
      }

      fCharArrayPool.releaseObject(caEntityValue);
      fCharArrayPool.releaseObject(caName);
      fCharArrayPool.releaseObject(caNoteName);
      fCharArrayPool.releaseObject(caPub);
      fCharArrayPool.releaseObject(caSys);
    }
  }

  private void scanDTDNotation() throws Exception {
    CharArray crPub = fCharArrayPool.getObject().reuse();
    CharArray crSys = fCharArrayPool.getObject().reuse();
    if (!is.scanChars(caDTDNotation)) {
      throw new ParserException("XMLParser: <!NOTATION expected", is.getID(), is.getRow(), is.getCol());
    }
    if (!is.scanS()) {
      throw new ParserException("bad notation, no space after <!NOTATION", is.getID(), is.getRow(), is.getCol());
    }
    scanName(carrNS[0], true);
    if (!is.scanS()) {
      throw new ParserException("bad notation, no space after name", is.getID(), is.getRow(), is.getCol());
    }
    scanPublicExternalID(crPub, crSys, true);
    is.scanS();
    if (!is.scanByte('>')) {
      throw new ParserException("bad notation, > expected", is.getID(), is.getRow(), is.getCol());
    }
    docHandler.onDTDNotation(carrNS[0], crPub, crSys);

    if (bValidation) {
      getXMLValidator().onDTDNotation(carrNS[0], crPub, crSys);
    }

    fCharArrayPool.releaseObject(crPub);
    fCharArrayPool.releaseObject(crSys);
  }

  public void scanPublicExternalID(CharArray sPub, CharArray sSys, boolean bAllowJustPubid) throws Exception {
    sPub.clear();
    sSys.clear();
    char cs = is.getLastChar(), aa;

    if (cs == 'S') {
      if (!is.scanChars(caSYSTEM)) {
        throw new ParserException("SYSTEM excpected", is.getID(), is.getRow(), is.getCol());
      }
      if (!is.scanS()) {
        throw new ParserException("Bad SYSTEM declaration", is.getID(), is.getRow(), is.getCol());
      }
      cs = is.getLastChar();
      if (cs != '\"' && cs != '\'') {
        throw new ParserException("Bad SYSTEM declaration", is.getID(), is.getRow(), is.getCol());
      }
      while ((aa = is.read()) != cs) {
        sSys.append(aa);
      }
      is.read();
    } else if (cs == 'P') {
      if (!is.scanChars(caPUBLIC)) {
        throw new ParserException("PUBLIC excpected", is.getID(), is.getRow(), is.getCol());
      }
      if (!is.scanS()) {
        throw new ParserException("Bad PUBLIC declaration", is.getID(), is.getRow(), is.getCol());
      }
      cs = is.getLastChar();
      if (cs != '\"' && cs != '\'') {
        throw new ParserException("Bad PUBLIC declaration", is.getID(), is.getRow(), is.getCol());
      }
      while ((aa = is.read()) != cs) {
        if (aa == '&' || aa == '<' || aa == '[' || aa == '{' ) {
          throw new ParserException("Wrong symbol in PUBLIC Declaration: " + aa, is.getID(), is.getRow(), is.getCol());
        }
        sPub.append(aa);
      }
      is.read();
      if (!is.scanS() && !bAllowJustPubid) {
        throw new ParserException("Bad PUBLIC declaration", is.getID(), is.getRow(), is.getCol());
      }
      cs = is.getLastChar();

      if (cs != '\"' && cs != '\'') {
        if (!bAllowJustPubid) {
          throw new ParserException("Bad PUBLIC declaration", is.getID(), is.getRow(), is.getCol());
        } else {
          return;
        }
      }

      while ((aa = is.read()) != cs) {
        sSys.append(aa);
      }
      is.read();
    }
  }

  public void scanMarkupDecl(int type) throws Exception {
    switch (type) {
      case M_DTDELEMENT: {
        scanDTDElement();
        break;
      }
      case M_DTDATTLIST: {
        scanDTDAttList();
        break;
      }
      case M_DTDENTITY: {
        scanDTDEntity();
        break;
      }
      case M_DTDNOTATION: {
        scanDTDNotation();
        break;
      }
      case M_COMMENT: {
        scanComment();
        break;
      }
    }
  }

  public int scanIntSubset() throws Exception {
    int a;
    int sourceID = is.getSourceID();

    while (true) {
      switch (a = checkMarkup()) {
        case M_PI: {
          scanPI(false);
          break;
        }
        case M_DTDELEMENT:
        case M_DTDATTLIST:
        case M_DTDENTITY:
        case M_DTDNOTATION:
        case M_COMMENT: {
          scanMarkupDecl(a);
          break;
        }
        case M_PEREF: {
          handleDTDReference(false);
          break;
        }
        case M_CHDATA: {
          if (is.scanS() == true) {
            break;
          } else {
            throw new ParserException("Character data not allowed between DTD Markup declaraions", is.getID(), is.getRow(), is.getCol());
          }
        }
        default: {
          return a;
        }
      }

      if (is.isFinished(sourceID)) {
        is.clearFinished(sourceID);
        return M_CLEAR;
      }
    }
  }

  private void scanConditionalSectionStart() throws Exception {
    is.read();
    checkPE(is.getLastChar());
    is.scanS();

    if (is.getLastChar() != 'I') {
      throw new ParserException("XMLParser: Conditional section in external DTD must begin with INCLUDE or IGNORE.", is.getID(), is.getRow(), is.getCol());
    }

    char ch = is.read();

    if (ch == 'G') { //check if rest chars are NORE
      if (!is.scanChars(caGNORE)) {
        throw new ParserException("XMLParser: Conditional section in external DTD must begin with INCLUDE or IGNORE.", is.getID(), is.getRow(), is.getCol());
      }

      is.scanS();

      if (is.getLastChar() != '[') {
        throw new ParserException("XMLParser: Conditional section in external DTD: [ expected after IGNORE.", is.getID(), is.getRow(), is.getCol());
      }

      is.read();
      int level = 0;

      while (true) {
        ch = is.getLastChar();

        if (ch == '<') {
          if (is.read() == '!') {
            if (is.read() == '[') {
              level++;
              continue;
            }
          }
        } else if (ch == ']') {
          boolean b = false;

          while ((ch = is.read()) == ']') {
            b = true;
          }

          if (b == true && ch == '>') {
            level--;

            if (level == -1) {
              is.read();
              break;
            }
          }
        }

        is.read();
      }
    } else if (ch == 'N') {
      if (!is.scanChars(caNCLUDE)) {
        throw new ParserException("XMLParser: Conditional section in external DTD must begin with INCLUDE or IGNORE.", is.getID(), is.getRow(), is.getCol());
      }

      is.scanS();

      if (is.getLastChar() != '[') {
        throw new ParserException("XMLParser: Conditional section in external DTD: [ expected after IGNORE.", is.getID(), is.getRow(), is.getCol());
      }

      is.read();
      scanExtSubset(false);
    } else {
      throw new ParserException("XMLParser: Conditional section in external DTD must begin with INCLUDE or IGNORE.", is.getID(), is.getRow(), is.getCol());
    }
  }

  private void scanConditionalSectionEnd() throws Exception {
    if (is.getLastChar() == ']' && is.read() == ']' && is.read() == '>') {
      is.read();
      return;
    } else {
      throw new ParserException("XMLParser: Conditional Section must end on ]]>.", is.getID(), is.getRow(), is.getCol());
    }
  }

  private int scanExtSubset(boolean textdeclallowed) throws Exception {
    int a;
    int sourceID = is.getSourceID();

    while (true) {
      switch (a = checkMarkup()) {
        case M_PI: {
          scanPI(textdeclallowed);
          break;
        }
        case M_DTDELEMENT:
        case M_DTDATTLIST:
        case M_DTDENTITY:
        case M_DTDNOTATION:
        case M_COMMENT: {
          scanMarkupDecl(a);
          break;
        }
        case M_PEREF: {
          handleDTDReference(false);
          break;
        }
        case M_CDSECT: {
          scanConditionalSectionStart();
          break; //when <![ is reached - start of conditional section in dtd

        }
        case M_DTDINTEND: {
          scanConditionalSectionEnd();

          if (sourceID != is.getSourceID()) {
            throw new ParserException("XMLParser: Condtional section must begin and end in the same entity.", is.getID(), is.getRow(), is.getCol());
          }

          return M_CLEAR; //when ] is reached .. if next chars are ]]> end of conditional section in external dtd
        }
        case M_CHDATA: {
          {
            if (is.scanS() == false) {
              throw new ParserException("Character data not allowed between DTD Markup declaraions" + ":" + is.getLastChar() + is.read(), is.getID(), is.getRow(), is.getCol());
            }
          }

          break;
        }
        default: {
          return a;
        }
      }

      textdeclallowed = false;

      if (is.isFinished(sourceID)) {
        is.clearFinished(sourceID);
        return M_CLEAR;
      }
    }
  }

  public void scanDTD() throws Exception {
    needXMLDeclVersion = false;
    CharArray crPub = fCharArrayPool.getObject().reuse();
    CharArray crSys = fCharArrayPool.getObject().reuse();
    if (!is.scanChars(caDoctype)) {
      return;
    }
    
    
    if (bSoapProcessing) {
      throw new ParserException("DOCTYPE declaration is not allowed in SOAP Processing mode", is.getRow(), is.getCol());
    }
    
    hasDTD = true;
    inDTD = true;
    is.scanS();
    scanName(carrNS[0], true);
    is.scanS();
    scanPublicExternalID(crPub, crSys, bHTMLMode);
    docHandler.startDTD(carrNS[0], crPub, crSys);

    if (bValidation) {
      getXMLValidator().startDTD(carrNS[0], crPub, crSys);
    }

    is.scanS();

    if (is.scanByte('[')) {
      int res = scanIntSubset();

      if (res == M_CHDATA & is.scanS() == false) {
        throw new ParserException("Character data is not allowed inside DTD", is.getID(), is.getRow(), is.getCol());
      } else if (res == M_OTHER) {
        throw new ParserException("This kind of data is not allowed here", is.getID(), is.getRow(), is.getCol());
      }

      if (!is.scanByte(']')) {
        throw new ParserException(" ] expected to close internal DTD", is.getID(), is.getRow(), is.getCol());
      }
    }

    //    LogWriter.getSystemLogWriter().println(bValidation);
    //do not scan ext subset!
    //if in non-validation mode - do not scan external subset (problems with url loading)

    //if (bValidation) {
    if (bReadDTD) {
      if (crSys.getSize() > 0) {
        org.xml.sax.InputSource isource = null;

        //TODO tova raboti.. a dolnoto ne ravoti s b2b.ear.. ne se deployva na 4.2.1
        if (getEntityResolver() != null) {
          //urlLoader.push(new URL(crSys.toString()));
          isource = getEntityResolver().resolveEntity(crPub.toString(), crSys.toString());
          //urlLoader.push(new URL(isource.getSystemId()));
        } else {
          //URL external = xmlParser.urlLoader.loadAndPush(ent.getSys().toString());
          isource = urlLoader.resolveEntity(crPub.toString(), crSys.toString());
        }

        //xmlParser.((getEntityResolver()==null)?urlLoader:getEntityResolver).resolveEntity();
        if (isource != null) {
          if (isource.getSystemId() != null) {
            urlLoader.pushTheSame();
          }
            
          is.addInputSource(isource);
          scanExtSubset(true);
        } else {
          isource = urlLoader.resolveEntity(crPub.toString(), crSys.toString());

          if (isource == null) {
            throw new ParserException("After resolving entity: " + crSys + " inputSource is null! Maybe you are parsing throug SAX and extending DefaultHandler, which has an empty implementation for EntityResolver.resolveEntity()", is.getID(), is.getRow(), is.getCol());
          }

          if (isource.getSystemId() != null) {
            urlLoader.pushTheSame();
          }

          is.addInputSource(isource);
          scanExtSubset(true);
        }
      }
    }
    //}
    fCharArrayPool.releaseObject(crPub);
    fCharArrayPool.releaseObject(crSys);
    is.scanS();
    if (!is.scanByte('>')) {
      throw new ParserException("> expected to close DTD: ", is.getID(), is.getRow(), is.getCol());
    }
    is.scanS();
    inDTD = false;
    docHandler.endDTD();

    if (bValidation) {
      getXMLValidator().endDTD();
    }
  }

  /**
   * Scans any reference into the Reference array, the 0th elemet is the
   * scanned reference
   */
  public Reference scanReference() throws Exception {
    char a;
    char ch = is.getLastChar();

    if (ch != '%' && ch != '&') {
      return null;
    }

    Reference ref = null;
    CharArray cr = fCharArrayPool.getObject().reuse();

    if (ch == '&') {
      ch = is.read();

      if (ch == '#') {
        ch = is.read();

        if (ch == 'x') {
          a = is.read();

          while (a != ';' && ((a >= '0' && a <= '9') || ((a >= 'a' && a <= 'f') || (a >= 'A' && a <= 'F')))) {
            cr.append(a);
            a = is.read();
          }

          if (a != ';') {
            throw new ParserException("XMLParser: Character Reference must end on ;", is.getID(), is.getRow(), is.getCol());
          }
          is.read();
          ref = fReferencePool.getObject().reuse(Reference.RX, cr);
          fCharArrayPool.releaseObject(cr);
        } else {
          a = is.getLastChar();

          while (a != ';' && a >= '0' && a <= '9') {
            cr.append(a);
            a = is.read();
          }

          if (a != ';') {
            throw new ParserException("XMLParser: Character Reference must end on ;", is.getID(), is.getRow(), is.getCol());
          }
          is.read();
          ref = fReferencePool.getObject().reuse(Reference.RD, cr);
          fCharArrayPool.releaseObject(cr);
        }
      } else {
        scanName(cr, true, false);
        if (!is.scanByte(';')) {
          throw new ParserException("XMLParser: Entity Reference must end on ;", is.getID(), is.getRow(), is.getCol());
        }
        ref = fReferencePool.getObject().reuse(Reference.RENT, cr);

      }

    } else if (ch == '%') {
      is.read();
      scanName(cr, true, false);
      if (!is.scanByte(';')) {
        throw new ParserException("XMLParser: PEReference must end on ;", is.getID(), is.getRow(), is.getCol());
      }
      ref = fReferencePool.getObject().reuse(Reference.RPE, cr);
      fCharArrayPool.releaseObject(cr);
    } else {
      throw new ParserException("XMLParser: % or & expected", is.getID(), is.getRow(), is.getCol());
    }

    return ref;
  }

  //private boolean[] had20Holder = new boolean[1];

  private void scanAttValue(CharArray crVal, boolean isCDATA) throws Exception {
    //SOAPXMLStream is = (SOAPXMLStream)this.is;
    char cs = is.getLastChar();
    boolean had0D = false;

    if (cs != '\"' && cs != '\'') {
      if (!bHTMLMode) {
        throw new ParserException("XMLParser: Bad Attribute value: \' or \" expected!", is.getID(), is.getRow(), is.getCol());
      } else {
        crVal.clear();
        char aa = cs;

        while (Symbols.isLetterOrDigit(aa) || aa == '%' || aa == '-' || aa == '.' || aa == ',' || aa == '_' || aa == '#') {
          crVal.append(aa);
          aa = is.read();
        }

        return;
      }
    }

    char aa = is.read();
    crVal.clear();
    int had20Holder = 0;

    while ( aa != cs && aa > 0) {

      switch (aa) {
      	case 0xD : {
            aa = 0x20;
            had0D = true;
          } break;
        case 0xA : {
          if (had0D) {
            had0D = false;
            aa = is.read();
            continue;
          }
          aa = 0x20;
      	} break;
      	case 9 :
          aa = 0x20;
          had0D = false;
          break;
        case '&' :
          had20Holder = handleAttributeReference(crVal, had20Holder, isCDATA);
          aa = is.getLastChar();
//          if (!Symbols.isChar(aa)) {
//            throw new ParserException("Wrong Replacement text.", is.getID(), is.getRow(), is.getCol());
//          }
          had0D = false;
          continue;
        case '<' :
          throw new ParserException("XMLParser: You cannot use < directly in attribute value, it should be escaped", is.getID(), is.getRow(), is.getCol());
      }
      
        	
      if (!isCDATA) {
        if (aa == 0x20) {
          if (crVal.length() > 0) {
           	had20Holder = 1;
          }
        } else {
          if (had20Holder == 1) {
            crVal.append(' ');
            had20Holder = 0;
          }

          crVal.append(aa);
        }
      } else {
        crVal.append(aa);
      }
      aa = is.read();
    }

    if (aa == cs) {
      aa = is.read();
    } else {
      throw new ParserException("XMLParser: Bad Attribute value", is.getID(), is.getRow(), is.getCol());
    }
  }

  private void scanAttListHandleAttribute(Vector AttList, Attribute at) throws Exception {
    if (bNamespaces) {
      //String res = crav.toString();
      if (at.crRawName.equals(caXMLNS)) {
        
        if (isNamespaceReplacing && namespaceReplacements.get(at.crValue) != null) {
          at.crValue.set((CharArray)namespaceReplacements.get(at.crValue));
        } 
        
        
        namespaceHandler.addDefault(at.crValue);
        docHandler.startPrefixMapping(CharArray.EMPTY, at.crValue);

        if (getNamespacePrefixes()) {
          AttList.add(at);
        }
      } else if (at.crPrefix.equals(caXMLNS)) {
        if (at.crValue.length() == 0) {
          throw new ParserException("The empty string is not acceptable for a value of an xmlns:* attribute.", is.getID(), is.getRow(), is.getCol());
        }

        if (isNamespaceReplacing && namespaceReplacements.get(at.crValue) != null) {
          at.crValue.set((CharArray)namespaceReplacements.get(at.crValue));
        } 
        
        namespaceHandler.add(at.crLocalName, at.crValue);
        docHandler.startPrefixMapping(at.crLocalName, at.crValue);

        if (getNamespacePrefixes()) {
          //LogWriter.getSystemLogWriter().println("XMLParser.scanAttListHandlerAttr: addin xmlns:::" + getCharArrayLocalName(carrNSAtt));
          AttList.add(at);
        }
      } else {
        //here the attribute uri is set to null because it is later checked in handleDefaultAttributes so the propert namespace is assigned to each attribute
        AttList.add(at);
      }
    } else {
      AttList.add(at);
    }
  }

  private boolean bSATGotXMLLang = false;
  private boolean bSATGotXMLBase = false;
  private boolean bSATGotXMLSpace = false;
  private HashSet hs = new HashSet();

  private void scanAttList(CharArray elementName, Vector AttList) throws Exception {
    //SOAPXMLStream is = (SOAPXMLStream)this.is;
    is.scanS();
    char ch = is.getLastChar();

    if ((ch == '>') || (ch == '/')) {
      if (!bSoapProcessing) {
        handleDefaultAttributes(elementName, AttList);
      }

      return;
    }
    hs.clear();

    CharArray crav = null;

    while ((ch != '>') && (ch != '/')) {
      Attribute at = fAttributePool.getObject();

      if (bNamespaces) {
        scanQName(at.crRawName, at.crPrefix, at.crLocalName, true);
      } else {
        scanName(at.crRawName, true);
        at.crPrefix.clear();
        at.crLocalName.substring(at.crRawName, 0);
      }
      
      at.crRawName.bufferHash();
      if (!hs.add(at.crRawName)) {
      	throw new Exception("XMLParser attribute:" + at.crRawName + ", already exists");
      }
      if (!bSoapProcessing) {
        at.sType = getAttributeType(elementName, at.crRawName);
      }
      

      crav = at.crValue;//fCharArrayPool.getObject().reuse();
      crav.clear();
      is.scanS();
      boolean hasEq = is.scanByte('=');

      if (!hasEq && !bHTMLMode) {
        throw new ParserException(" = expected in attlist", is.getID(), is.getRow(), is.getCol());
      } else if (hasEq) {
        is.scanS();
        boolean isCDATA = false;
        if (at.sType.equals("CDATA")) {
          isCDATA = true;
        } else {
          isCDATA = false;
        }

        scanAttValue(crav, isCDATA);

      } else {
        crav.clear();
        crav.append("#default");
      }

      /*for (int i = 0; i < AttList.size(); i++) {
        if (((Attribute) AttList.get(i)).crRawName.equals(at.crRawName)) {
          throw new ParserException("Duplicate attribute value: " + getCharArrayRawName(carrNSAtt) + " = \'" + crav + "\'", is.getID(), is.getRow(), is.getCol());
        }
      }
      */

      if ((is.scanS() == false && !bHTMLMode) && ((ch=is.getLastChar()) != '/') && (ch != '>')) {
        throw new ParserException("XMLParser: Bad attribute list. Expected WhiteSpace, / or >:", is.getID(), is.getRow(), is.getCol());
      }
      ch = is.getLastChar();
      if (!bSoapProcessing) {
        defaultAttributesHandler.setDefCheck(elementName, at.crRawName);
        at.isSpecified = true;
      }
      scanAttListHandleAttribute(AttList, at);
      //fCharArrayPool.releaseObject(crav); //SASHO
    }

    if (!bSoapProcessing) {
      handleDefaultAttributes(elementName, AttList);
    } else {
      for (int i = 0; i < AttList.size(); i++) {
        Attribute at = (Attribute) AttList.get(i);
        try {
          if (XMLParserConstants.caXMLNS.equals(at.crRawName)){
            at.crUri.substring(namespaceHandler.isMappedAttr(at.crRawName), 0);
          } else {
            at.crUri.substring(namespaceHandler.isMappedAttr(at.crPrefix), 0);
          }
        } catch (Exception e) {
          throw new ParserException("XMLParser: Attribute prefix \'" + at.crPrefix + "\' is not mapped to a namespace", is.getID(), is.getRow(), is.getCol());
        }
      }
    }
  }

  public void handleDefaultAttributes(CharArray elementName, Vector AttList) throws Exception {
    Vector v = defaultAttributesHandler.getVect(elementName);

    if (v != null) {
      //LogWriter.getSystemLogWriter().println("XMLParser.handleDefAttr:1");
      Attribute attr = null;

      for (int i = 0; i < v.size(); i++) {
        attr = (Attribute) v.get(i);

        if (attr.getDefCheck() == false) {
          carrNSAtt[0].set(attr.crRawName);

          if (attr.crPrefix.length() > 0) {
            carrNSAtt[1].set(attr.crPrefix);
            carrNSAtt[2].set(attr.crLocalName);
          } else {
            carrNSAtt[1].clear();
            carrNSAtt[2].set(attr.crLocalName);
          }

          scanAttListHandleAttribute(AttList, fAttributePool.getObject().reuse(carrNSAtt[0], carrNSAtt[1], carrNSAtt[2], null, attr.crValue, false));
        }
      }

      defaultAttributesHandler.clearDefCheck(elementName);
    }

    bSATGotXMLLang = false;
    bSATGotXMLBase = false;
    bSATGotXMLSpace = false;

    for (int i = 0; i < AttList.size(); i++) {
      Attribute at = (Attribute) AttList.get(i);
      if (bNamespaces) {
        try {
         // LogWriter.getSystemLogWriter().println(at.crRawName+ " " + at.crPrefix+ " "+ at.crLocalName+" "+XMLParserConstants.caXMLNS.equals(at.crRawName));
          if (XMLParserConstants.caXMLNS.equals(at.crRawName)){
            at.crUri.substring(namespaceHandler.isMappedAttr(at.crRawName), 0);
          } else {
            at.crUri.substring(namespaceHandler.isMappedAttr(at.crPrefix), 0);
          }
        } catch (Exception e) {
          throw new ParserException("XMLParser: Attribute prefix \'" + at.crPrefix + "\' is not mapped to a namespace", is.getID(), is.getRow(), is.getCol());
        }
      }

      String value = null;

      if (at.crRawName.equals(crXMLBase)) {
        value = at.crValue.toString();
        namespaceHandler.add(crXMLBase, at.crValue);
        urlLoader.loadAndPush(value);
        bSATGotXMLBase = true;
      } else if (at.crRawName.equals(crXMLLang)) {
        bSATGotXMLLang = true;
        value = at.crValue.toString();
        namespaceHandler.add(crXMLLang, at.crValue);
      } else if (at.crRawName.equals(crXMLSpace)) {
        bSATGotXMLSpace = true;
        value = at.crValue.toString();
        namespaceHandler.add(crXMLSpace, at.crValue);
      }
    }

  }

  int v1s = 0;
  public boolean scanElement() throws Exception {
    try  {
  
      //v1 = carrNS[0];
      //carrNS[0] = caTagStack.getStorage();
      v1s = caTagStack.getStorage().length();
    
      //doScanNameClear = false;
//      if (bNamespaces) {
//        scanQName(carrNS);
//      } else {
//        scanName(carrNS);
//      }
      scanName(caTagStack.getStorage(), false);
      caTagStack.appendSize(caTagStack.getStorage().length() - v1s);
      caTagStack.substringTop(carrNS[0]);
      colonChar -= v1s;
      if (bNamespaces && colonChar > -1) {
        carrNS[1].substring(carrNS[0], 0, colonChar);
        carrNS[2].substring(carrNS[0], colonChar + 1);
      } else {
        carrNS[1].clear();
        carrNS[2].substring(carrNS[0], 0);
      }
//      LogWriter.getSystemLogWriter().println("XMLParser.scanElement carrNS0=" + carrNS[0] + ", carrNS1=" + carrNS[1] + ", carrNS2=" + carrNS[2]);

      //    if (is.getRow() > 4700) {
      //      LogWriter.getSystemLogWriter().println("Starting Element:" + carrNS[0]);
      //    }
      //
      vScanElementAttList.removeAllElements();
      scanAttList(carrNS[0], vScanElementAttList);
      //
      CharArray uri = null;
      if (bNamespaces) {
        //String prefixString = getCarrNSPrefix().getString();
        try {
          uri = namespaceHandler.isMapped(carrNS[1]);
        } catch (Exception e) {
          throw new ParserException("XMLParser: Prefix \'" + carrNS[1] + "\' is not mapped to a namespace", is.getID(), is.getRow(), is.getCol());
        }

  //      uri = namespaceHandler.get(carrNS[1]);
  //	    uri = namespaceHandler.get(carrNS[1]);
  //
  //      if (uri == null || uri.equals(CharArray.EMPTY)) { //(!namespaceHandler.isMapped(carrNS[1])) {
  //        throw new ParserException("XMLParser: Prefix \'" + carrNS[1] + "\' is not mapped to a namespace", is.getID(), is.getRow(), is.getCol());
  //      }
      }

      if (uri == null) {
        uri = CharArray.EMPTY;
      }

      //String uri = (bNamespaces)? namespaceHandler.get(getCarrNSPrefix().getString()) : null;
      CharArray qname = carrNS[0];
      CharArray localName = carrNS[(bNamespaces) ? 2 : 0];

      if (bHTMLMode) {
        qname.toUpperCase();
        localName.toUpperCase();
      }
      
      docHandler.startElementStart(uri, localName, qname);      

      for (int i = 0; i < vScanElementAttList.size(); i++) {
        Attribute at = (Attribute) vScanElementAttList.get(i);

        if (!at.isSpecified) {
          docHandler.onCustomEvent(DocHandler.ATTRIBUTE_IS_NOT_SPECIFIED, null);
        }

        //      docHandler.addAttribute(at.crUri.getString(), at.crPrefix, at.crLocalName, at.crRawName, at.isSpecified?sSpecified:sNotSpecified, at.crValue);
        //      if (at.isSpecified) {
        //        docHandler
        //      LogWriter.getSystemLogWriter().println("XMLParser.scanElement name=" + qname + ",sendAttribute name=" + at.crRawName + ", value=" + at.crValue + ", uri= " + at.crUri + ", is spec=" +at.isSpecified);
        docHandler.addAttribute(at.crUri, at.crPrefix, at.crLocalName, at.crRawName, at.sType, at.crValue);
      }

      is.scanS();
      namespaceHandler.levelUp();
      if (namespaceHandler.getLevel() > MAX_DEPTH) {
        throw new ParserException("XML Document is too deeply nested. Max allowed depth is: " + MAX_DEPTH + ".", is.getID(), is.getRow(), is.getCol());
      }

      if (is.scanByte('/')) {
        if (!is.scanByte('>')) {
          throw new ParserException("XMLParser: > expected to close empty element", is.getID(), is.getRow(), is.getCol());
        }
        docHandler.startElementEnd(true);
        docHandler.endElement(uri, localName, qname, true);

        if (bValidation) {
          try {
            getXMLValidator().startElement(uri.toString(), localName, qname, vScanElementAttList);
            getXMLValidator().endElement(uri.toString(), localName, qname);
          } catch (ValidationException e) {
            docHandler.onCustomEvent(DocHandler.XML_SPEC_ERROR, e);
          }
        }

        namespaceHandler.levelDown();

        for (int i = 0; i < vScanElementAttList.size(); i++) {
          fAttributePool.releaseObject((Attribute) vScanElementAttList.get(i));
        }

        vScanElementAttList.removeAllElements();
        fCharArrayPool.releasePool();
        //      if (is.getRow() > 4700) {
        //        LogWriter.getSystemLogWriter().println("Ending Element  :" + carrNS[0] + " empty");
        //      }
        caTagStack.consumeTop(carrNS[0].length());
        return true;
      }

  //    if (bWellformed) {
  //      caTagStack.put(carrNS[0]);
  //    }

      if (is.scanByte('>')) {
        if (bValidation) {
          if (uri == null) {
            uri = CharArray.EMPTY;
          }
          try {
            getXMLValidator().startElement(uri.toString(), localName, qname, vScanElementAttList);
          } catch (ValidationException e) {
            docHandler.onCustomEvent(DocHandler.XML_SPEC_ERROR, e);
          }

        }

        docHandler.startElementEnd(false);
      } else {
        throw new ParserException(" > expected to close start-element-tag", is.getID(), is.getRow(), is.getCol());
      }

      for (int i = 0; i < vScanElementAttList.size(); i++) {
        fAttributePool.releaseObject((Attribute) vScanElementAttList.get(i));
      }

      vScanElementAttList.removeAllElements();

      if (bHTMLMode && HTML_OPEN_TAGS.contains(qname)) {
        fCharArrayPool.releasePool();
        docHandler.endElement(uri, localName, qname, false); //*//getCarrNSQName(), getCarrNSPrefix(), getCarrNSLocalname(), namespaceHandler.get(getCarrNSPrefix().getString()));
        if (bValidation) {
          try{
            getXMLValidator().endElement(uri.toString(), localName, qname); //(getCarrNSQName(), getCarrNSPrefix(), getCarrNSLocalname(), namespaceHandler.get(getCarrNSPrefix().getString()));
          } catch (ValidationException e) {
            docHandler.onCustomEvent(DocHandler.XML_SPEC_ERROR, e);
          }

        }
        namespaceHandler.levelDown();
        return true;
      }

      if (bActiveParse == false) {
        scanContent();

        if (!bHTMLMode) {
          scanEndTag();
        }
      }

      return true;
    } finally  {
     // carrNS[0] = carrNS0;
     // carrNS[1] = carrNS1;
      //carrNS[2] = carrNS2;
    }
  }

  //  public boolean scanStartOfTag(Vector vAttList) throws Exception {
  //    if (bNamespaces) {
  //      scanQName(carrNS);
  //    } else {
  //      scanName(carrNS);
  //    }
  //    scanAttList(carrNS[0], vAttList);
  //    is.scanS();
  //    return true;
  //  }
  //
  public void scanEndTag() throws Exception {
//    int err;
    if (!is.scanByte('/')) {
      throw new ParserException("</ expected", is.getID(), is.getRow(), is.getCol());
    }
    
    if (bSoapProcessing)  {
      caTagStack.substringTop(carrNS[0]);
      ((SOAPXMLStream)is).checkName(carrNS[0]);
    } else  {
      if (bNamespaces) {
        scanQName(carrNS[0], carrNS[1], carrNS[2], true);
      } else {
        scanName(carrNS[0], true);
      }
    }

    CharArray uri = CharArray.EMPTY;

//    if (bNamespaces) {
//      //String prefixString = getCarrNSPrefix().getString();
//	    uri = namespaceHandler.get(carrNS[1]);
//
//      if (uri == null || uri.equals(CharArray.EMPTY)) {// (!namespaceHandler.isMapped(carrNS[1])) {
//        throw new ParserException("XMLParser: Prefix \'" + carrNS[1] + "\' is not mapped to a namespace", is.getID(), is.getRow(), is.getCol());
//      }
//    }

    if (!bSoapProcessing)  {
      if (bNamespaces) {
        //String prefixString = getCarrNSPrefix().getString();
        try {
          uri = namespaceHandler.isMapped(carrNS[1]);
        } catch (Exception e) {
          e.printStackTrace();
          throw new ParserException("XMLParser: Prefix \'" + carrNS[1] + "\' is not mapped to a namespace", is.getID(), is.getRow(), is.getCol());
        }
      }


      if (uri == null) {
        uri = CharArray.EMPTY;
      }
    }

    CharArray qname = carrNS[0];
    CharArray localName = carrNS[(bNamespaces) ? 2 : 0];

    if (bHTMLMode) {
      qname.toUpperCase();
      localName.toUpperCase();

      if (HTML_OPEN_TAGS.contains(qname)) {
        is.scanS();
        if (!is.scanByte('>')) {
          throw new ParserException("> expected", is.getID(), is.getRow(), is.getCol());
        }
        return;
      }
    }   

    is.scanS();
    if (!is.scanByte('>')) {
      throw new ParserException("> expected", is.getID(), is.getRow(), is.getCol());
    }

    //    if (is.getRow() > 4700) {
    //      LogWriter.getSystemLogWriter().println("Ending Element  :" + carrNS[0] + " empty");
    //    }
    
    if (bWellformed && !bSoapProcessing) {
      if (!caTagStack.matchTop(carrNS[0])) {
        char[] aaa = caTagStack.getTop();
        throw new ParserException("Document is not well-formed: Start-tag \'" + new String(aaa) + "\' is different from end-tag \'" + carrNS[0].getString() + "\'", is.getID(), is.getRow(), is.getCol());
      }
    } 

    if (bValidation) {
      try {
        getXMLValidator().endElement(null, null, null);
      } catch (ValidationException e) {
        docHandler.onCustomEvent(DocHandler.XML_SPEC_ERROR, e);
      }
    }
    
    docHandler.endElement(uri, localName, qname, false); //*//getCarrNSQName(), getCarrNSPrefix(), getCarrNSLocalname(), namespaceHandler.get(getCarrNSPrefix().getString()));
    
    if (bSoapProcessing) {
      caTagStack.consumeTop(carrNS[0].length());
    }
    //LogWriter.getSystemLogWriter().println("Top before consume:" + caTagStack.getTopCharArray() + ", length =" + carrNS[0].length());
//    LogWriter.getSystemLogWriter().println("Top after consume:" + caTagStack.getTopCharArray());
    namespaceHandler.levelDown();
  }

  int cds1;

  public void scanCharData() throws Exception {
    if (bSoapProcessing) {
      ((SOAPXMLStream) is).scanCharData(carr);
      //LogWriter.getSystemLogWriter().println("XMLParser.scanned:" + carr);
    } else {
      char cs = is.getLastChar();
      boolean had0D = false;
      carr.clear();
      cds1 = 0;
      int startedSurrogatePair = -2;

      switch (cs) {
        case '<':
        case '&': {
          return;
        }
        case ']': {
          cds1++;
          carr.append(cs);
          break;
        }
        case 0xD: {
          carr.append('\n');
          had0D = true;
          break;
        }
        default: {
          if (Symbols.isValidChar(cs)) {
            carr.append(cs);
          } else  if (cs >= 0xD800 && cs <= 0xDBFF) {
            startedSurrogatePair = is.getCol();
            carr.append(cs);
          } else {
            throw new ParserException("Invalid char #0x" + Integer.toString((cs), 16), is.getID(), is.getRow(), is.getCol());
          }
        }
      }

      while (((cs = is.read()) != '<') && (cs != '&')) {
        if (startedSurrogatePair != -2) {
          if (!(cs >= 0xDC00 && cs <= 0xDFFF)) {
            int row = is.getRow();
            if (is.getCol() <= 1) {
              row -= 1;
            }
//            int col = startedSurrogatePair;
            throw new ParserException("Invalid char #0x" + Integer.toString((cs), 16), is.getID(), is.getRow(), is.getCol());
          }
          cds1 = 0;
          startedSurrogatePair = -2;
        } else if (cs == ']') {
          cds1++;
        } else if (cs == 0xD) {
          carr.append('\n');
          had0D = true;
          continue;
        } else if (cs == 0xA) {
          if (!had0D) {
            carr.append('\n');
          }
          had0D = false;
          continue;

        } else if (cds1 >= 2 && cs == '>') {
          throw new ParserException("XMLParser: ]]> is not allowed in Character data sections", is.getID(), is.getRow(), is.getCol());
        } else if (!Symbols.isValidChar(cs)) {
          if (cs >= 0xD800 && cs <= 0xDBFF) {
            startedSurrogatePair = is.getCol();
          } else {
            throw new ParserException("Invalid char #0x" + Integer.toString((cs), 16), is.getID(), is.getRow(), is.getCol());
          }
        } else {
          cds1 = 0;
        }


        had0D = false;
        carr.append(cs);

        if (bDocFinished == true && getScanContentOnly() == false && !bHTMLMode) {
          throw new ParserException("Element <" + new String(caTagStack.getTop()) + "> is not closed. End Of File reached.", is.getID(), is.getRow(), is.getCol());
        } else if (bDocFinished == true && getScanContentOnly() == true) {
          break;
        } else if (bDocFinished == true && bHTMLMode) {
          break;
        }
      }
    }

    callCharData(carr);
  }

  private void callCharData(CharArray carr) throws Exception {
    if (bValidation) {
      try {
        docHandler.onCustomEvent(DocHandler.IGNORABLE_WHITESPACE, getXMLValidator().charData(carr));
      } catch (ValidationException e) {
        docHandler.onCustomEvent(DocHandler.XML_SPEC_ERROR, e);
      }
    } else {
      docHandler.onCustomEvent(DocHandler.IGNORABLE_WHITESPACE, Boolean.FALSE);
    }
    
    if (getDOMTrimWhiteSpaces()) {
      carr.trim();
      if(carr.length() == 0) {
        return;
      }
    }
      docHandler.charData(carr, true);
 
  }

  public void scanCDSect() throws Exception {
    //LogWriter.getSystemLogWriter().println("XMLPatsetr.scanCDSect: " + is.getLastChar() + is.read() + is.read());
    if (!is.scanChars(caStartCDATA)) {
      throw new ParserException("XMLParser: Bad CDSECT begin markup", is.getID(), is.getRow(), is.getCol());
    }

    //is.setReadRaw(true);
    carr.clear();
    char cs;
    int cds1 = 0;
    cs = is.getLastChar();

    while (true) {
      if (cs == ']') {
        if (cds1 < 2) {
          cds1 = cds1 + 1;
        }
      } else if (cds1 == 2 && cs == '>') {
        is.read();
        carr.setSize(carr.getSize() - 2);
        docHandler.onCDSect(carr);

        if (bValidation) {
          getXMLValidator().onCDSect(carr);
        }

        //is.setReadRaw(false);
        return;
      } else if (!Symbols.isValidChar(cs)) {
        throw new ParserException("Invalid char #0x" + Integer.toString((cs), 16), is.getID(), is.getRow(), is.getCol());
      } else {
        cds1 = 0;
      }

      carr.append(cs);
      cs = is.read();

      if (bDocFinished == true) {
        throw new ParserException("CDATA Section is not closed. End Of File reached.", is.getID(), is.getRow(), is.getCol());
      }
    }
  }

  public void scanPI(boolean textdeclallowed) throws Exception {
    scanName(carrNS[0], true);

    if (carrNS[0].getString().compareTo(XMLDECL) == 0) {
      if (textdeclallowed == true || xmlDeclAllowed == true || isBackwardsCompatibilityMode()) {
        scanXMLDeclEx();
        return;
      } else {
        throw new ParserException("XML Declaration not allowed here.", is.getID(), is.getRow(), is.getCol());
      }
    } else if (carrNS[0].getString().compareToIgnoreCase(XMLDECL) == 0) {
      throw new ParserException("XML declaration must be in lower case.", is.getID(), is.getRow(), is.getCol());
    }

    xmlDeclAllowed = false;
    if (!is.scanS() && !Symbols.isNameChar(is.getLastChar())) {
      if (is.getLastChar() != '?') {
        throw new ParserException("Unexpected character in PITarget content: 0x" + Integer.toHexString(is.getLastChar()), is.getID(), is.getRow(), is.getCol());
      }
    }
    cds1 = 0;
    char cs = is.getLastChar();
    carr.clear();

    while (true) {
      if (cs == '?') {
        cds1 = 1;
      } else if (cds1 == 1 && cs == '>') {
        is.read();
        carr.setSize(carr.getSize() - 1);
        docHandler.onPI(carrNS[0], carr);
        return;
      } else if (!Symbols.isChar(cs)) {
        throw new ParserException("XMLParser : #" + (int) cs + " not allowed in PI data", is.getID(), is.getRow(), is.getCol());
      } else {
        cds1 = 0;
      }
      
      if (!Symbols.isChar(cs)) {
        throw new ParserException("Unexpected character in PI: 0x" + Integer.toHexString(cs), is.getID(), is.getRow(), is.getCol());
      }
      carr.append(cs);
      cs = is.read();
    }
  }

  public void scanComment() throws Exception {
    if (!is.scanChars(caComment)) {
      throw new ParserException("XMLParser: Bad Comment begin markup", is.getID(), is.getRow(), is.getCol());
    }

    char cs = is.getLastChar();
    cds1 = 0;
    carr.clear();

    while (true) {
      if (cs == '-') {
        cds1++;

        if (cds1 == 3) {
          throw new ParserException("Bad comment content", is.getID(), is.getRow(), is.getCol());
        }
      } else if (cds1 == 2 && cs == '>') {
        is.read();
        carr.setSize(carr.getSize() - 2);
        docHandler.onComment(carr);
        return;
      } else if (cds1 == 2) {
        throw new ParserException("Bad comment content", is.getID(), is.getRow(), is.getCol());
      } else if (bDocFinished == true) {
        throw new ParserEOFException("XMLParser: End of file, while reading comment: <!--" + carr);
      } else {
        cds1 = 0;
      }

      if (!Symbols.isChar(cs) && cs != 38) {
        throw new ParserException("Unexpected character in comment content: 0x" + Integer.toHexString(cs), is.getID(), is.getRow(), is.getCol());
      }
      carr.append(cs);
      cs = is.read();
    }
  }

  private int handleAttributeReference(CharArray c, int had20Holder, boolean isCDATA) throws Exception {
    Reference ref = null; // = fReferencePool.getObject();
    ref = scanReference();

    if (ref.getType() == Reference.RX) {
      if (!isCDATA && ref.getChar() != 0x20 && had20Holder==1 && c.length() > 0) {
        c.append(' ');
        had20Holder = 0;
      } else if (ref.getChar() == 0x20) {
        had20Holder = 0;
      }

      c.append(ref.getChar());
    } else if (ref.getType() == Reference.RD) {
      if (!isCDATA && ref.getChar() != 0x20 && had20Holder==1 && c.length() > 0) {
        c.append(' ');
        had20Holder = 0;
      } else if (ref.getChar() == 0x20) {
        had20Holder = 0;
      }

      c.append(ref.getChar());
    } else if (ref.getType() == Reference.RENT) {
      if (!isCDATA && had20Holder==1 && c.length() > 0) {
        c.append(' ');
        had20Holder = 0;
      }

      if (ref.isPredefined()) {
        c.append(ref.getChar());
      } else {
        makeGEReplacement(ref);
        is.setLiteral(true);
      }
    }

    if (ref.getType() == Reference.RENT) {
      fCharArrayPool.releaseObject(ref.getName());
    }

    fReferencePool.releaseObject(ref);
    return had20Holder;
  }

  private void handleDTDReference(boolean inMarkup) throws Exception {
//    boolean gotent = false;
//    int r = 0;
//    boolean internal = false;
    Reference ref = null; // = fReferencePool.getObject();
    ref = scanReference();

    if (ref == null) {
      return;
    } else if (ref.getType() != Reference.RPE) {
      throw new ParserException("Parameter Reference expected at this point", is.getID(), is.getRow(), is.getCol());
    }

    Entity ent = getPEntity(ref.getName());

    if (ent == null) {
      throw new ParserException("No such entity \'" + ref.getName() + "\' was declared", is.getID(), is.getRow(), is.getCol());
    }

    if (inMarkup) {
      is.addInputFromEntity(ent);
    } else {
      is.addInputFromEntity(ent);

      if (ent.isInternal()) {
        scanExtSubset(false);
      } else {
        scanExtSubset(true);
      }
    }

    if (ref.getType() == Reference.RENT || ref.getType() == Reference.RPE) {
      fCharArrayPool.releaseObject(ref.getName());
    }

    fReferencePool.releaseObject(ref);
  }

  public void handleContentReference(boolean literal) throws Exception {
//    boolean gotent = false;
    int r = 0;
    boolean internal = false;
    Reference ref = null; // = fReferencePool.getObject();
    ref = scanReference();

    if (ref == null) {
      return;
    }

    CharArray c = fCharArrayPool.getObject().reuse();

    if (ref.getType() == Reference.RX) {
      int ch = ref.getCharAsInt();

      if (ch >= 0x10000 && ch <= 0x10FFFF) { //SASHO Unicode surrogates
        int high = (ch - 0x10000) / 0x400 + 0xd800;
        int low = (ch - 0x10000) % 0x400 + 0xdc00;
        c.append((char) high);
        c.append((char) low);
      } else {
        c.append((char) ch);

        if (c.charAt(0) != 38 && !Symbols.isChar(c.charAt(0))) {
          throw new ParserException("XMLParser : #" + (int) c.charAt(0) + " not allowed in Character data sections", is.getID(), is.getRow(), is.getCol());
        }
      }

      callCharData(c);
    } else if (ref.getType() == Reference.RD) {
      int ch = ref.getCharAsInt();

      if (ch >= 0x10000 && ch <= 0x10FFFF) { //SASHO Unicode surrogates
        int high = (ch - 0x10000) / 0x400 + 0xd800;
        int low = (ch - 0x10000) % 0x400 + 0xdc00;
        c.append((char) high);
        c.append((char) low);
      } else {
        c.append((char) ch);

        // thorws an exception when using &#38; or other references inside character data sections, which is not correct
        // uncomment with caution or make other checking
        if (c.charAt(0) != 38 && !Symbols.isChar(c.charAt(0))) {
          throw new ParserException("XMLParser : #" + (int) c.charAt(0) + " not allowed in Character data sections", is.getID(), is.getRow(), is.getCol());
        }
      }

      callCharData(c);
    } else if (ref.getType() == Reference.RENT) {
      if (getExpandingReferences()) {
        if (ref.isPredefined()) {
          c.append(ref.getChar());
          callCharData(c);
        } else {
          docHandler.onStartContentEntity(ref.getName(), true);
          Entity ent = getGEntity(ref.getName());

          if (ent.isPredefined()) {
            c.append(getGEntity(ref.getName()).getValue().charAt(0));
            callCharData(c);
          } else {
            r = 1; // GeneralParserEntity
            internal = makeGEReplacement(ref).isInternal();
            xmlDeclAllowed = true;
          }
        }
      } else {
        docHandler.onStartContentEntity(ref.getName(), false);
      }
    } else if (ref.getType() == Reference.RPE) {
      if (ref.isPredefined()) {
        c.append(ref.getChar());
        callCharData(c);
      } else {
        Entity ent = getPEntity(ref.getName());

        if (ent.isPredefined()) {
          c.append(getPEntity(ref.getName()).getValue().charAt(0));
          callCharData(c);
        } else {
          r = 2; //Parameter Entity
          internal = makePEReplacement(ref).isInternal();
        }
      }
    }

    fCharArrayPool.releaseObject(c);

    if (ref.getType() == Reference.RENT || ref.getType() == Reference.RPE) {
      fCharArrayPool.releaseObject(ref.getName());
    }

    fReferencePool.releaseObject(ref);
  }

  private Entity getGEntity(CharArray name) throws ParserException {
    Entity ent = (Entity) gentities.get(name);

    if (ent == null) {
      throw new ParserException("XMLParser: Entity '" + name + "' undefined", is.getID(), is.getRow(), is.getCol());
    }

    return ent;
  }

  /**
   * Returns the Parameter entity pointed by name
   */
  private Entity getPEntity(CharArray name) throws ParserException {
    Entity ent = (Entity) pentities.get(name);

    if (ent == null) {
      throw new ParserException("XMLParser: PEntity '" + name + "' undefined", is.getID(), is.getRow(), is.getCol());
    }

    return ent;
  }

  private Entity makeGEReplacement(Reference ref) throws Exception {
    if (++referencesProcessed > maximumReferencesCount) {
      throw new ParserException("Maximum processing references reached!", is.getID(), is.getRow(), is.getCol());
    }
    Entity pent = getGEntity(ref.getName());
    //    if (is.getRow() > 4700) {
    //      LogWriter.getSystemLogWriter().println("Using Entitiy:" + ref.getName());
    //    }
    is.addInputFromEntity(pent);
    return pent;
  }

  private Entity makePEReplacement(Reference ref) throws Exception {
    if (++referencesProcessed > maximumReferencesCount) {
      throw new ParserException("Maximum processing references reached!", is.getID(), is.getRow(), is.getCol());
    }
    //LogWriter.getSystemLogWriter().println("XMLParser.makePEReplacement:" + ref.getName());
    Entity pent = getPEntity(ref.getName());
    is.addInputFromCharArray(pent.getValue(), pent.getName());
    return pent;
  }

  public void scanContent() throws Exception {
    while (true) {
//      if (bDocFinished && getScanContentOnly()) {
//        return;
//      } else if (bDocFinished && bHTMLMode) {
//        return;
//      }

      if (bDocFinished) {
        return;
      }
    
      boolean processingReference = false;

      switch (checkMarkup()) {
        case M_LT: {
          scanElement();
          break;
        }
        case M_DTDINTEND:
        case M_CHDATA: {
          scanCharData();
          break;
        }
        case M_COMMENT: {
          scanComment();
          break;
        }
        case M_PI: {
          scanPI(false);
          break;
        }
        case M_CONTREF: {
          processingReference = true;
          handleContentReference(false);
          break;
        }
        case M_CDSECT: {
          scanCDSect();
          break;
        }
        case M_ENDEL: {
          if (bHTMLMode) {
            scanEndTag();
            break;
          } else {
            xmlDeclAllowed = false;
            return;
          }
        }
        default: {
          xmlDeclAllowed = false;
          return;
        }
      }

      if (!processingReference) {
        xmlDeclAllowed = false;
      }
    }
  }

  private void scanMisc() throws Exception {
    while (true) {
      if (bDocFinished) {
        return;
      }

      switch (checkMarkup()) {
        case M_PI: {
          scanPI(false);
          break;
        }
        case M_COMMENT: {
          scanComment();
          break;
        }
        case M_CHDATA: {
          if (is.scanS() == false) {
            throw new ParserException("XMLParser: No data allowed here", is.getID(), is.getRow(), is.getCol());
          }

          break;
        }
        default: {
          if (bEndDoc) {
            throw new ParserException("XMLParser: No data allowed here", is.getID(), is.getRow(), is.getCol());
          } else {
            return;
          }
        }
      }
    }
  }

  private void scanVersionInfo(CharArray sVersion) throws Exception {
    if (!is.scanString("version")) {
      throw new ParserException("Expected 'version'.", is.getID(), is.getRow(), is.getCol() );
    }

    is.scanS();
    if (!is.scanByte('=')) {
      throw new ParserException("Expected '=' in VersionInfo.", is.getID(), is.getRow(), is.getCol() );
    }
    is.scanS();
    readText(sVersion);

    if (!sVersion.getString().equals("1.0")) {
      throw new ParserException("XMLParser: version of xml file must be 1.0", is.getID(), is.getRow(), is.getCol());
    }
  }

  private void scanEncodingDecl(CharArray sEncoding) throws Exception {
    if (!is.scanString("encoding")) {
      throw new ParserException("Expected 'encoding'.", is.getID(), is.getRow(), is.getCol() );
    }
    is.scanS();
    if (!is.scanByte('=')) {
      throw new ParserException("Expected '=' in EncodingDeclaration.", is.getID(), is.getRow(), is.getCol() );
    }
    is.scanS();
    readText(sEncoding);
  }

  private void scanSSDecl(CharArray sSSDecl) throws Exception {
    if (!is.scanString("standalone")) {
      throw new ParserException("Expected 'standalone'.", is.getID(), is.getRow(), is.getCol() );
    }
    is.scanS();
    if (!is.scanByte('=')) {
      throw new ParserException("Expected '=' in Standalone Declaration.", is.getID(), is.getRow(), is.getCol() );
    }
    is.scanS();
    readText(sSSDecl);

    if (!sSSDecl.equals("yes") && !sSSDecl.equals("no")) {
      throw new ParserException("XML Error: Standalone declaration must be \'yes\' or \'no\', but not \'" + sSSDecl + "\'", is.getID(), is.getRow(), is.getCol());
    }
  }

  public boolean scanS() throws Exception {
    return is.scanS();
  }

  //  public void scanXMLDecl() throws Exception {
  //
  //    int err;
  //    CharArray sEncoding = fCharArrayPool.getObject().reuse();
  //    CharArray sVersion = fCharArrayPool.getObject().reuse();
  //    CharArray sSSDecl = fCharArrayPool.getObject().reuse();
  //
  //    if (!is.scanString("xml")) return;
  //    if (gotXMLDecl) {
  //      throw new ParserException("Only 1 XML-Declaration is allowed.", is.getID(), is.getRow(), is.getCol());
  //    } else {
  //      gotXMLDecl = true;
  //    }
  //
  //    boolean a = false, b = false;
  //    if (!is.scanS())  throw new ParserException("XMLParser: Error in XML Declaration", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
  //    if (!scanVersionInfo(sVersion))  throw new ParserException("XMLParser: Error in XML Declaration", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
  //    b = is.scanS();
  //    a = scanEncodingDecl(sEncoding);
  //    if (a && !b)  throw new ParserException("XMLParser: Error in XML Declaration", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
  //    else if (a) b = is.scanS();
  //    a = scanSSDecl(sSSDecl);
  //    if (a && !b)  throw new ParserException("XMLParser: Error in XML Declaration", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
  //    if (!is.scanChars(caEndPI)) throw new ParserException("XMLParser: Error in XML Declaration", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
  //    docHandler.onXMLDecl(sVersion.toString(), sEncoding.toString(), sSSDecl.toString());
  //    fCharArrayPool.releaseObject(sEncoding);
  //    fCharArrayPool.releaseObject(sVersion);
  //    fCharArrayPool.releaseObject(sSSDecl);
  //  }
  private boolean needXMLDeclVersion = true;
  public void scanXMLDeclEx() throws Exception {
//    int err;
    CharArray sEncoding = fCharArrayPool.getObject().reuse();
    sEncoding.set(is.getEncoding());
    CharArray sVersion = fCharArrayPool.getObject().reuse();
    CharArray sSSDecl = fCharArrayPool.getObject().reuse();
    try {
      boolean a = false, b = false, needSpace = false, scannedVersion = false, scannedEncoding = false, scannedStandalone = false, finished = false;
      if (!is.scanS()) {
        throw new ParserException("XMLParser: Error in XML Declaration: Space expected after '<?xml'", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
  
      }
      
      for (;;) {
      	switch (is.getLastChar()) {
          case 'v' : 
            if (scannedVersion) {
              throw new ParserException("Unexpected Version declaration", is.getID(), is.getRow(), is.getCol());
            } 
              
            scanVersionInfo(sVersion);
            scannedVersion = true;
            needSpace = true;
            break;
          case 's' :
            if (!scannedVersion && needXMLDeclVersion) {
              throw new ParserException("VersionInfo expected on first place in XMLDeclaration", is.getID(), is.getRow(), is.getCol()); 
            }

            if (!needXMLDeclVersion) {
              throw new ParserException("The standalone attribute cannot be used in external entities", is.getID(), is.getRow(), is.getCol()); 
            }
            
            scanSSDecl(sSSDecl);
            scannedStandalone = true;
            needSpace = true;
            break;
          case 'e' :
            if (!scannedVersion && needXMLDeclVersion) {
              throw new ParserException("VersionInfo expected on first place in XMLDeclaration", is.getID(), is.getRow(), is.getCol()); 
            }

            if (scannedStandalone) {
              throw new ParserException("Encoding Declaration must be before Standalone declaration", is.getID(), is.getRow(), is.getCol()); 
            }
            
            scanEncodingDecl(sEncoding);
            scannedEncoding = true;
            needSpace = true;
            break;
          case '?' :
            if (is.read() != '>') {
              throw new ParserException("XMLParser: Error in XML Declaration: > Expected", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
            }
            
            //check
            if (!scannedVersion && needXMLDeclVersion ) {
              throw new ParserException("XMLParser: Error in XML Declaration. Expected VersionInfo", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
            }
            if (!scannedEncoding && !needXMLDeclVersion) {
              throw new ParserException("XMLParser: Error in Text Declaration of External Entity. Expected Encoding", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
            }
            docHandler.onXMLDecl(sVersion.toString(), sEncoding.toString(), sSSDecl.toString());
            is.setEncoding(sEncoding);
            is.read();
            
            return;
          default:
            throw new ParserException("Unexpected character in XMLDeclaration - 0x" + Integer.toHexString(is.getLastChar()), is.getID(), is.getRow(), is.getCol());
        }
        if (is.getLastChar() == '?') {
          continue;
        }
        if (!is.scanS()) {
          throw new ParserException("Expected space in XML Declaration.", is.getID(), is.getRow(), is.getCol());
        }
      }
    } finally {
      fCharArrayPool.releaseObject(sEncoding);
      fCharArrayPool.releaseObject(sVersion);
      fCharArrayPool.releaseObject(sSSDecl);
    }
	
	
//    if (is.getLastChar() == 'v') { //  throw new ParserException("XMLParser: Error in XML Declaration: Missing Version attribute.", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//      scanVersionInfo(sVersion);
//      b = is.scanS();
//    } else {
//      b = true;
//    }
//
//    if (is.getLastChar() != 'e' && is.getLastChar() != 's' && is.getLastChar() != '?') { //check for encoding or standalone attributes
//      throw new ParserException("XMLParser: Error in XML Declaration: 'standalone' or 'encoding' attribute expected.", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//    }
//
//    if (is.getLastChar() == 'e') {
//      a = scanEncodingDecl(sEncoding);
//
//      if (a && !b) {
//        throw new ParserException("XMLParser: Error in XML Declaration: 'encoding = \"enc-name\"' expected.", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//      }
//
//      scannedEncoding = true;
//    } else if (is.getLastChar() == 's') {
//      a = scanSSDecl(sSSDecl);
//      if (a && !b) {
//        throw new ParserException("XMLParser: Error in XML Declaration: 'standalone = \"yes or no\"' expected.", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//
//      }
//      scannedStandalone = true;
//    } else if (is.getLastChar() == '?') {
//      if (is.read() != '>') {
//        throw new ParserException("XMLParser: Error in XML Declaration: > Expected", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//      }
//
//      is.read();
//      finished = true;
//    }
//
//    if (!finished) {
//      b = is.scanS();
//
//      if (is.getLastChar() != 'e' && is.getLastChar() != 's' && is.getLastChar() != '?') { //check for encoding or standalone attributes
//        throw new ParserException("XMLParser: Error in XML Declaration: 'standalone' or 'encoding' attribute expected.", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//      }
//
//      if (is.getLastChar() == 'e') {
//        if (scannedEncoding) {
//          throw new ParserException("XMLParser: Error in XML Declaration: 'standalone' attribute expected.", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//        }
//
//        a = scanEncodingDecl(sEncoding);
//        if (a && !b) {
//          throw new ParserException("XMLParser: Error in XML Declaration: 'encoding = \"enc-name\"' expected.", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//
//        }
//      } else if (is.getLastChar() == 's') {
//        if (scannedStandalone) {
//          throw new ParserException("XMLParser: Error in XML Declaration: 'encoding = \"enc-name\"' expected.", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//        }
//
//        a = scanSSDecl(sSSDecl);
//        if (a && !b) {
//          throw new ParserException("XMLParser: Error in XML Declaration: 'standalone = \"yes or no\"' expected.", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//
//        }
//      } else if (is.getLastChar() == '?') {
//        if (is.read() != '>') {
//          throw new ParserException("XMLParser: Error in XML Declaration: > Expected", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//        }
//
//        is.read();
//        finished = true;
//      }
//    }
//
//    if (!finished) {
//      is.scanS();
//      if (!is.scanChars(caEndPI)) {
//        throw new ParserException("XMLParser: Error in XML Declaration: ?> expected.", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
//
//      }
//    }

    //    else if (a) b = is.scanS();
    //    a = scanSSDecl(sSSDecl);
    //    if (a && !b)  throw new ParserException("XMLParser: Error in XML Declaration", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
    //    if (!is.scanChars(caEndPI)) throw new ParserException("XMLParser: Error in XML Declaration", is.getID(), is.getRow(), is.getCol()); //baEndPI = "?>"
  }

  public void scanProlog() throws Exception {
    boolean textdeclallowed = true;

    while (true) {
      switch (checkMarkup()) {
        case M_PI: {
          scanPI(textdeclallowed);
          break;
        }
        case M_COMMENT: {
          textdeclallowed = false;
          scanComment();
          break;
        }
        case M_CHDATA: {
          if (is.scanS() == false) {
            if (!bDocFinished) {
              throw new ParserException("XMLParser: No data allowed here: (hex) " + Integer.toHexString(is.getLastChar()) +  ", " + Integer.toHexString(is.read()) + ", "+ Integer.toHexString(is.read()), is.getID(), is.getRow(), is.getCol());
            } else {
              throw new ParserException("XMLParser: No data allowed here: (hex) " + Integer.toHexString(is.getLastChar()), is.getID(), is.getRow(), is.getCol());
            }
          }

          break;
        }
        case M_DOCTYPE: {
          textdeclallowed = false;
          scanDTD();
          break;
        }
        case M_LT: {
          textdeclallowed = false;
          if (!hasDTD && getAdditionalDTDLocation() != null ) {
            is.addInputSource(getAdditionalDTDLocation());
            setAdditionalDTDLocation(null);
            continue;
          }          
          return;
        }
        default: {
          throw new ParserException("XMLParser: This kind of data is not expected here", is.getID(), is.getRow(), is.getCol());
        }
      }

      textdeclallowed = false;
    }
  }

  public void scanDocument() throws Exception {
    bEndDoc = false;
//    bDocFinished = false;
    docHandler.startDocument();

    if (bValidation) {
      getXMLValidator().startDocument();
    }

    if (bActiveParse) {
      return;
    }

    if (bActiveParse == false) {
      scanProlog();
    }

    // Needed for Log4j compatibility
    // It uses a log4j.dtd and validaiotn but the system cannot set this
    // log4j.dtd file so it is required to remove valiation if there is no dtd defined
    if (!hasDTD && bValidation) {
      setValidation(false);
      if (! bDynamicValidation) {
        onWarning("XMLParserWarning: No DTD detected. Switching to non-validation mode!");
      }
    }

    if (!scanElement()) {
      throw new ParserException("No root element", is.getID(), is.getRow(), is.getCol());
    }

    if (bActiveParse == false) {
      try {
        bEndDoc = true;
        scanMisc();
      } catch (ParserEOFException e) {
        //$JL-EXC$
        // default behavoir, scanning has to proceed untill the end of the file is reached
      }
      docHandler.endDocument();

      if (bValidation) {
        getXMLValidator().endDocument();
      }
    }
  }

  public void finalizeActiveScan() throws Exception {
    bEndDoc = true;
  }

  public void endPrefixMapping(CharArray prefix) throws Exception {
    if (!prefix.equals("xml:lang") && !prefix.equals("xml:base") && !prefix.equals("xml:lang")) {
      docHandler.endPrefixMapping(prefix);
    }
  }

  public void onEncodedDataReaderEOF(EncodedDataReader src) throws Exception {
//    LogWriter.getSystemLogWriter().println("XMLParser.onEncDataReaderEOf: " + src.getID());
    if (src == null) {
      Thread.dumpStack();
    } else {
      if (docHandler == null) {
        throw new IOException("Parsing an empty source. Root element expected!");
      }
      if (src.getName() != null) {
        docHandler.onEndContentEntity(src.getName());
      } else {
        docHandler.onEndContentEntity(src.getID());
      }
    }

    try {
      urlLoader.pop();
    } catch (Exception e) {
      //$JL-EXC$
      // Ignore
    }
  }

  public void onDocumentEOF() throws ParserEOFException {
    //    urlLoader.pop();
   // LogWriter.getSystemLogWriter().println("XMLParser.onDocumentEOF: Document EOF");
    bDocFinished = true;
  }

//  private boolean arraycompareNOCHECK(byte[] a1, byte[] a2, int len) {
//    for (int i = 0; i < len; i++) {
//      if (a1[i] != a2[i]) {
//        return false;
//      }
//    }
//
//    return true;
//  }

//  private boolean arrayCompareCharNO(byte[] a1, char[] a2, int len) {
//    for (int i = 0; i < len; i++) {
//      if (a1[i] != a2[i]) {
//        return false;
//      }
//    }
//
//    return true;
//  }

//  private boolean arrayCompChar(char[] a1, CharArray a2) {
//    if (a1.length != a2.getSize()) {
//      return false;
//    }
//
//    char[] a2data = a2.getData();
//    int a2off = a2.getOffset();
//
//    for (int i = 0; i < a1.length; i++) {
//      if (a1[i] != a2data[i + a2off]) {
//        return false;
//      }
//    }
//
//    return true;
//  }

  // Getters and setters
  public CharArray getCarrNSQName() {
    return carrNS[0];
  }

  public CharArray getCarrNSPrefix() {
    return (carrNS[2].getSize() == 0) ? carrNS[2] : carrNS[1];
  }

  public CharArray getCarrNSLocalname() {
    return (carrNS[2].getSize() == 0) ? carrNS[0] : carrNS[2];
  }

  public CharArray getCharArrayRawName(CharArray[] arr) {
    return arr[0];
  }

  public CharArray getCharArrayLocalName(CharArray[] arr) {
    if (arr[2].getSize() == 0) {
      return arr[0];
    } else {
      return arr[2];
    }
  }

  public CharArray getCharArrayPrefix(CharArray[] arr) {
    if (arr[2].getSize() == 0) {
      return arr[2];
    } else {
      return arr[1];
    }
  }


  public void setEntityResolver(EntityResolver er) {
    this.entityResolver = er;
  }

  public EntityResolver getEntityResolver() {
    return this.entityResolver;
  }

  public void changingMapping(CharArray prefix, CharArray uri) {
    if (prefix.equals("xml:base")) {
      urlLoader.pop();
    }
  }

  public void onWarning(String warning) throws Exception {
    docHandler.onWarning(warning);
  }

  public boolean getDocFinished() {
    return bDocFinished;
  }

  private String getAttributeType(CharArray elname, CharArray atname) {
    Hashtable h1 = (Hashtable) attributeTypes.get(elname);

    if (h1 != null) {
      String s = (String) h1.get(atname);

      if (s != null && s.length() > 0) {
        return s;
      }
    }

    return "CDATA";
  }

  /**
   * Returns reference to ElementStack.
   * @return
   */
  public CharArrayStack getElementStack() {
    return caTagStack;
  }

  public void setEncoding(CharArray enc) throws Exception {
    is.setEncoding(enc);
  }

  public void setMaximumProcessingReferences(Object value) {
    if (value == null) {
      throw new IllegalArgumentException("Maximum references parameter value cannot be null!");
    } else if (value instanceof Integer) {
      int intValue = ((Integer) value).intValue();
      if (intValue < 0) {
        throw new IllegalArgumentException("Maximum references parameter value cannot be negative!");
      }
      maximumReferencesCount = intValue;
    } else {
      throw new IllegalArgumentException("Maximum references parameter value must be an Integer!");
    }
  }

  public int getMaximumProcessingReferences() {
    return(maximumReferencesCount);
  }
  
  public INamespaceHandler getNamespaceHandler() {
    if (namespaceHandler == null) {
      namespaceHandler = new NamespaceHandler(this);
    } 
    
    return namespaceHandler;
  }

  public void clearDocHandler() {
    this.docHandler = null;
  }
  
  public void setDocHandler(DocHandler docHandler) {
    this.docHandler = docHandler;
  }
}

/*
 [22]  prolog ::=  XMLDecl? Misc* (doctypedecl Misc*)?
 [23]  XMLDecl ::=  '<?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'
 [24]  VersionInfo ::=  S 'version' Eq (' VersionNum ' | " VersionNum ")
 [25]  Eq ::=  S? '=' S?
 [26]  VersionNum ::=  ([a-zA-Z0-9_.:] | '-')+
 [27]  Misc ::=  Comment | PI |  S
 [39]  element ::=  EmptyElemTag  | STag content ETag [  WFC: Element Type Match ]
 [40]  STag ::=  '<' Name (S Attribute)* S? '>' [  WFC: Unique Att Spec ]
 [41]  Attribute ::=  Name Eq AttValue
 [42]  ETag ::=  '</' Name S? '>'
 [43]  content ::=  (element | CharData | Reference | CDSect | PI | Comment)*
 [44]  EmptyElemTag ::=  '<' Name (S Attribute)* S? '/>' [  WFC: Unique Att Spec ]
 [70]  EntityDecl ::=  GEDecl | PEDecl
 [71]  GEDecl ::=  '<!ENTITY' S Name S EntityDef S? '>'
 [72]  PEDecl ::=  '<!ENTITY' S '%' S Name S PEDef S? '>'
 [73]  EntityDef ::=  EntityValue | (ExternalID NDataDecl?)
 [74]  PEDef ::=  EntityValue | ExternalID
 [9]  EntityValue ::=  '"' ([^%&"] | PEReference | Reference)* '"' |  "'" ([^%&'] | PEReference | Reference)* "'"
 [75]  ExternalID ::=  'SYSTEM' S SystemLiteral | 'PUBLIC' S PubidLiteral S SystemLiteral
 [76]  NDataDecl ::=  S 'NDATA' S Name [  VC: Notation Declared ]
 [45]  elementdecl ::=  '<!ELEMENT' S Name S contentspec S? '>'
 [46]  contentspec ::=  'EMPTY' | 'ANY' | Mixed | children
 [47]  children ::=  (choice | seq) ('?' | '*' | '+')?
 [48]  cp ::=  (Name | choice | seq) ('?' | '*' | '+')?
 [49]  choice ::=  '(' S? cp ( S? '|' S? cp )* S? ')'
 [50]  seq ::=  '(' S? cp ( S? ',' S? cp )* S? ')'
 [51]  Mixed ::=  '(' S? '#PCDATA' (S? '|' S? Name)* S? ')*'  | '(' S? '#PCDATA' S? ')'
 mixed = ( #PCDATA | Name
 [75]  ExternalID ::=  'SYSTEM' S SystemLiteral | 'PUBLIC' S PubidLiteral S SystemLiteral
 [11]  SystemLiteral ::=  ('"' [^"]* '"') | ("'" [^']* "'")
 [82]  NotationDecl ::=  '<!NOTATION' S Name S (ExternalID |  PublicID) S? '>'
 [83]  PublicID ::=  'PUBLIC' S PubidLiteral
 [12]  PubidLiteral ::=  '"' PubidChar* '"' | "'" (PubidChar - "'")* "'"
 [13]  PubidChar ::=  #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
 [75]  ExternalID ::=  'SYSTEM' S SystemLiteral | 'PUBLIC' S PubidLiteral S SystemLiteral
 [75]  ExternalID ::=  'SYSTEM' S SystemLiteral | 'PUBLIC' S PubidLiteral S SystemLiteral
 [12]  PubidLiteral ::=  '"' PubidChar* '"' | "'" (PubidChar - "'")* "'"
 [11]  SystemLiteral ::=  ('"' [^"]* '"') | ("'" [^']* "'")
 [28]  doctypedecl ::=  '<!DOCTYPE' S Name (S ExternalID)? S? ('[' (markupdecl | PEReference | S)* ']' S?)? '>' [  VC: Root Element Type ]
 [29]  markupdecl ::=  elementdecl | AttlistDecl | EntityDecl | NotationDecl | PI | Comment  [  VC: Proper Declaration/PE Nesting ]
 [30]  extSubset ::=  TextDecl? extSubsetDecl
 [31]  extSubsetDecl ::=  ( markupdecl | conditionalSect | PEReference | S )*
 [75]  ExternalID ::=  'SYSTEM' S SystemLiteral | 'PUBLIC' S PubidLiteral S SystemLiteral
 [11]  SystemLiteral ::=  ('"' [^"]* '"') | ("'" [^']* "'")
 [12]  PubidLiteral ::=  '"' PubidChar* '"' | "'" (PubidChar - "'")* "'"
 [13]  PubidChar ::=  #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
 [45]  elementdecl ::=  '<!ELEMENT' S Name S contentspec S? '>'
 [46]  contentspec ::=  'EMPTY' | 'ANY' | Mixed | children
 [47]  children ::=  (choice | seq) ('?' | '*' | '+')?
 [48]  cp ::=  (Name | choice | seq) ('?' | '*' | '+')?
 [49]  choice ::=  '(' S? cp ( S? '|' S? cp )* S? ')'
 [50]  seq ::=  '(' S? cp ( S? ',' S? cp )* S? ')'
 [51]  Mixed ::=  '(' S? '#PCDATA' (S? '|' S? Name)* S? ')*'  | '(' S? '#PCDATA' S? ')'
 [52]  AttlistDecl ::=  '<!ATTLIST' S Name AttDef* S? '>'
 [53]  AttDef ::=  S Name S AttType S DefaultDecl
 [54]  AttType ::=  StringType | TokenizedType | EnumeratedType
 [55]  StringType ::=  'CDATA'
 [56]  TokenizedType ::=  'ID' | 'IDREF'  | 'IDREFS' | 'ENTITY'|'ENTITIES'|'NMTOKEN'|'NMTOKENS'
 [57]  EnumeratedType ::=  NotationType | Enumeration
 [58]  NotationType ::=  'NOTATION' S '(' S? Name (S? '|' S? Name)* S? ')'
 [59]  Enumeration ::=  '(' S? Nmtoken (S? '|' S? Nmtoken)* S? ')'
 [60]  DefaultDecl ::=  '#REQUIRED' | '#IMPLIED' | (('#FIXED' S)? AttValue)
 Conditional Section
 [61]  conditionalSect ::=  includeSect | ignoreSect
 [62]  includeSect ::=  '<![' S? 'INCLUDE' S? '[' extSubsetDecl ']]>'
 [63]  ignoreSect ::=  '<![' S? 'IGNORE' S? '[' ignoreSectContents* ']]>'
 [64]  ignoreSectContents ::=  Ignore ('<![' ignoreSectContents ']]>' Ignore)*
 [65]  Ignore ::=  Char* - (Char* ('<![' | ']]>') Char*)
 [70]  EntityDecl ::=  GEDecl | PEDecl
 [71]  GEDecl ::=  '<!ENTITY' S Name S EntityDef S? '>'
 [72]  PEDecl ::=  '<!ENTITY' S '%' S Name S PEDef S? '>'
 [73]  EntityDef ::=  EntityValue | (ExternalID NDataDecl?)
 [74]  PEDef ::=  EntityValue | ExternalID
 External Entity Declaration
 [75]  ExternalID ::=  'SYSTEM' S SystemLiteral | 'PUBLIC' S PubidLiteral S SystemLiteral
 [76]  NDataDecl ::=  S 'NDATA' S Name [  VC: Notation Declared ]
 [82]  NotationDecl ::=  '<!NOTATION' S Name S (ExternalID |  PublicID) S? '>'
 [83]  PublicID ::=  'PUBLIC' S
 [67]  Reference ::=  EntityRef | CharRef
 [68]  EntityRef ::=  '&' Name ';' [  WFC: Entity Declared ]
 [69]  PEReference ::=  '%' Name ';' [  VC: Entity Declared ]
 [66]  CharRef ::=  '&#' [0-9]+ ';'| '&#x' [0-9a-fA-F]+ ';' [  WFC: Legal Character ]
 [52]  AttlistDecl ::=  '<!ATTLIST' S Name AttDef* S? '>'
 [53]  AttDef ::=  S Name S AttType S DefaultDecl
 [54]  AttType ::=  StringType | TokenizedType | EnumeratedType
 [55]  StringType ::=  'CDATA'
 [56]  TokenizedType ::=  'ID' | 'IDREF'  | 'IDREFS' | 'ENTITY'|'ENTITIES'|'NMTOKEN'|'NMTOKENS'
 [57]  EnumeratedType ::=  NotationType | Enumeration
 [58]  NotationType ::=  'NOTATION' S '(' S? Name (S? '|' S? Name)* S? ')'
 [59]  Enumeration ::=  '(' S? Nmtoken (S? '|' S? Nmtoken)* S? ')'
 [60]  DefaultDecl ::=  '#REQUIRED' | '#IMPLIED' | (('#FIXED' S)? AttValue)
 [10]  AttValue ::=  '"' ([^<&"] | Reference)* '"' |  "'" ([^<&'] | Reference)* "'"
 [40]  STag ::=  '<' Name (S Attribute)* S? '>' [  WFC: Unique Att Spec ]
 [41]  Attribute ::=  Name Eq AttValue
 [18]  CDSect ::=  CDStart CData CDEnd
 [19]  CDStart ::=  '<![CDATA['
 [20]  CData ::=  (Char* - (Char* ']]>' Char*))
 [21]  CDEnd ::=  ']]>'
 [16]  PI ::=  '<?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'
 [17]  PITarget ::=  Name - (('X' | 'x') ('M' | 'm') ('L' | 'l'))
 */

