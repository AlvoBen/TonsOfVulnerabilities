package com.sap.engine.lib.xml.parser.handlers;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributeListImpl;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.NestedSAXParseException;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.helpers.Reference;

/**
 * Class description -
 *
 * @author Vladimir Savtchenko
 * @version 1.00
 */
public final class SAXDocHandler implements DocHandler {

  //private SAXParser saxParser = null;
//  private final static CharArray crEmpty = new CharArray(1);
//  private final static String sEmpty = "";
  private LexicalHandler lexicalHandler = null;
  private DocumentHandler documentHandler = null;
  private DeclHandler declHandler = null;
  private ContentHandler contentHandler = null;
  private DTDHandler dtdHandler = null;
  private ErrorHandler errorHandler = null;
  private Stack namestack = new Stack();
  public static final boolean DEBUG = true;
  boolean wroteDTDKv = false;
  private Hashtable hash = new Hashtable();
  Object tmp = null;
  CharArray tmp2 = null;
  String tmps = null;
  private int maxsize = 300;
  private int cursize = 0;
  private boolean ignorableWhitespace = false;

  public SAXDocHandler() {
//    LogWriter.getSystemLogWriter().println("SAXDOCHanlder new");
//    Thread.dumpStack();

  }

  public SAXDocHandler(DefaultHandler h) {
    setContentHandler(h);
    //setDeclHandler(h);
    setDTDHandler(h);
    //setLexicalHandler(h);
  }

  public String get(CharArray item) {
    item.bufferHash();
    tmp = hash.get(item);
    item.clearHashReady();

    if (tmp == null && cursize < maxsize) {
      tmp2 = item.copy();
      tmps = tmp2.getString();
      tmp2.bufferHash();
      //      LogWriter.getSystemLogWriter().println("bufferning: " + tmps);
      hash.put(tmp2, tmps);
      cursize++;
      return tmps;
    } else if (cursize == maxsize) {
      return item.getString();
    } else {
      return (String) tmp;
    }
  }

  public String mq(String str) {
    //if (str.length() == 0) return "";
    if (str.indexOf("\"") != -1) {
      return "\'" + str + "\'";
    } else {
      return "\"" + str + "\"";
    }
  }

  int se = 0, cd = 0, pi = 0, cm = 0, at = 0;

  public void onXMLDecl(String v, String e, String s) {

  }

  String stLN, stQN, stUR;

  public void startElementStart(CharArray uri, CharArray localName, CharArray qname) throws Exception {
    se++;

    if (contentHandler != null || documentHandler != null) {
      stUR = get(uri != null ? uri : CharArray.EMPTY);
      stLN = get(localName);
      stQN = get(qname);
      attImpl.clear();
      attListImpl.clear();
    }
  }

  public void addAttribute(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception {
    if (contentHandler != null || documentHandler != null) {
      at++;
      //      Attribute attr = (Attribute) attList.get(i);
      String sURI = get(uri != null ? uri : CharArray.EMPTY);
      String sQname = get(qname);
//    String sValue = value.getString();
      String sValue = get(value);
      String sType = type;

      if (contentHandler != null) {
        String sLname = null;

        if (localName.length() > 0) {
          sLname = get(localName);
        } else {
          sLname = sQname;
        }

        attImpl.addAttribute(sURI, sLname, sQname, sType, sValue);
      }

      if (documentHandler != null) {
        attListImpl.addAttribute(sQname, sType, sValue);
      }
    }
  }

  public void startElementEnd(boolean isEmpty) throws Exception {
    //    LogWriter.getSystemLogWriter().println("SAXDocHandler.startElementEnd: contentHandler=" + contentHandler + ", dochandler=" + documentHandler +
    //      ", stUR=" + stUR + ", stLN=" + stLN + ", stQN=" + stQN + ", attImpl="+ attImpl + ", attListImpl=" + attListImpl);
    if (contentHandler != null) {
      contentHandler.startElement(stUR, stLN, stQN, attImpl);
    }

    if (documentHandler != null) {
      documentHandler.startElement(stQN, attListImpl);
    }
  }

  public void endElement(CharArray uri, CharArray localName, CharArray qname, boolean isEmpty) throws Exception {
    if (contentHandler != null) {
      contentHandler.endElement(get(uri != null ? uri : CharArray.EMPTY), get(localName), get(qname));
    }

    if (documentHandler != null) {
      documentHandler.endElement(get(qname));
    }
  }

  //  public void callEmptyElementTag(CharArray QName, CharArray prefix, CharArray localName, String uri, Vector attList) throws Exception {
  //    startElement(QName, prefix, localName, uri, attList);
  //    endElement(QName, prefix, localName, uri);
  //  }
  //
  //  public void callEmptyElementTag(CharArray sName, Vector attList) throws Exception {
  //    startElement(sName, attList);
  //    endElement(sName);
  //  }
  AttributesImpl attImpl = new AttributesImpl();
  AttributeListImpl attListImpl = new AttributeListImpl();

  //  public void startElement(CharArray QName, CharArray prefix, CharArray localName, String uri, Vector attList) throws Exception {
  //    se++;
  //    //LogWriter.getSystemLogWriter().println("in SaxDocHandler: " + QName + "   -" + localName);
  //    if (contentHandler != null || documentHandler != null) {
  //      attImpl.clear();
  //      attListImpl.clear();
  //      for (int i = 0; i < attList.size(); i++) {
  //        at++;
  //        Attribute attr = (Attribute) attList.get(i);
  //        String name  = get(attr.crRawName);
  //        String value = attr.crValue.getString();
  //        String type  = attr.sType;
  //        if (contentHandler != null) {
  //          attImpl.addAttribute(get(attr.crUri), get(attr.crLocalName), name, type, value);
  //        }
  //        if (documentHandler != null) {
  //          attListImpl.addAttribute(name, type, value);
  //        }
  //      }
  //      if (contentHandler != null) {
  //        String a = get(localName);
  //        String b = get(QName);
  //        //LogWriter.getSystemLogWriter().println("in SaxDocHandler: " + QName + "   -" + localName);
  //        contentHandler.startElement(uri, a, b, attImpl);
  //      }
  //      if (documentHandler != null) {
  //        documentHandler.startElement(get(QName), attListImpl);
  //      }
  //    }
  //  }
  public void endElement(CharArray QName, CharArray prefix, CharArray localName, String uri) throws Exception {
    if (contentHandler != null) {
      contentHandler.endElement(uri, get(localName), get(QName));
    }

    if (documentHandler != null) {
      documentHandler.endElement(get(QName));
    }
  }

  //  public void startElement(CharArray sName, Vector attList) throws Exception  {
  //    startElement(sName, crEmpty, crEmpty, sEmpty, attList);
  //  }
  //
  //  public void endElement(CharArray sName) throws Exception  {
  //    endElement(sName, crEmpty, crEmpty, sEmpty);
  //  }
  public void startDocument() throws Exception {
    namestack.clear();
    cursize = 0;
    hash.clear();

    if (contentHandler != null) {
      contentHandler.startDocument();
    }

    if (documentHandler != null) {
      documentHandler.startDocument();
    }
  }

  public void endDocument() throws Exception {
    if (contentHandler != null) {
      contentHandler.endDocument();
    }

    if (documentHandler != null) {
      documentHandler.endDocument();
    }

    hash.clear();

    //LogWriter.getSystemLogWriter().print("(se="+se + ",cd=" + cd + ",pi=" + pi + ",cm=" + cm + ",at=" + at + ")");
  }

  public void charData(CharArray data, boolean bDisableOutputEscaping) throws Exception {
    //LogWriter.getSystemLogWriter().println("char data");
    cd++;
 
    if (contentHandler != null) {
      if (ignorableWhitespace) {
        contentHandler.ignorableWhitespace(data.getData(), data.getOffset(), data.getSize());
        ignorableWhitespace = false;
      } else {
        contentHandler.characters(data.getData(), data.getOffset(), data.getSize());
      }
    }

    if (documentHandler != null) {
      if (ignorableWhitespace) {
        documentHandler.ignorableWhitespace(data.getData(), data.getOffset(), data.getSize());
        ignorableWhitespace = false;
      } else {
        documentHandler.characters(data.getData(), data.getOffset(), data.getSize());
      }
    }
  }

  public void onPI(CharArray target, CharArray data) throws Exception {
    pi++;

    if (contentHandler != null) {
      contentHandler.processingInstruction(target.getString(), data.getString());
    }

    if (documentHandler != null) {
      documentHandler.processingInstruction(target.getString(), data.getString());
    }
  }

  public void onComment(CharArray text) throws Exception {
    cm++;

    if (lexicalHandler != null) {
      //char [] data = new char [text.length()];
      //text.getChars(0, data.length, data, 0);
      lexicalHandler.comment(text.getData(), text.getOffset(), text.getSize());
    }
  }

  public void onCDSect(CharArray text) throws Exception {
    if (lexicalHandler != null) {
      lexicalHandler.startCDATA();
    }

    if (contentHandler != null) {
      contentHandler.characters(text.getData(), text.getOffset(), text.getSize());
    }
    if (documentHandler != null) {
      documentHandler.characters(text.getData(), text.getOffset(), text.getSize());
    }

    if (lexicalHandler != null) {
      lexicalHandler.endCDATA();
    }
  }

  public void onDTDElement(CharArray name, CharArray model) throws Exception {
    if (declHandler != null) {
      //      try {
      declHandler.elementDecl(name.getString(), model.getString());
      //      } catch (SAXException e) {
      //        e.printStackTrace();
      //        throw new Exception("SAXException: " + e.getMessage());
      //      }
    }
  }

  public void onDTDAttListStart(CharArray name) throws Exception {

  }

  public void onDTDAttListItem(CharArray name, CharArray attName, String type, String defDecl, CharArray vAttValue, String note) throws Exception {
    if (declHandler != null) {
      if (defDecl != null && defDecl.length() == 0) {
        defDecl = null;
      }

      declHandler.attributeDecl(name.getString(), attName.getString(), type, defDecl,  vAttValue.length() > 0 ? vAttValue.getString() : null);
      //declHandler.attributeDecl(name.getString(), attName.getString(), type, defDecl,  vAttValue.getString());

    }
  }

  public void onDTDAttListEnd() throws Exception {

  }

  public void onDTDEntity(Entity ent) throws Exception {
    if (ent.isInternal()) {
      if (declHandler != null) {
        declHandler.internalEntityDecl(ent.getName().toString(), ent.getValue().toString());
      }
    } else if (!ent.isInternal() && !ent.isUnparsed()) {
      if (declHandler != null) {
        declHandler.externalEntityDecl(ent.getName().toString(), ent.getPub().toString(), ent.getSys().toString());
      }
    } else if (ent.isUnparsed()) {
      if (dtdHandler != null) {
        dtdHandler.unparsedEntityDecl(ent.getName().toString(), ent.getPub().toString(), ent.getSys().toString(), ent.getNote().toString());
      }
    }
  }

  public void onDTDNotation(CharArray name, CharArray pub, CharArray sys) throws Exception {
    if (dtdHandler != null) {
      String ps = pub.length() == 0 ? null : pub.getString();
      String ss = sys.length() == 0 ? null : sys.getString();
      dtdHandler.notationDecl(name.getString(), ps, ss);
      //      dtdHandler.notationDecl(name.getString(), pub.getString(), sys.getString());
    }
  }

  public void startDTD(CharArray name, CharArray pub, CharArray sys) throws Exception {
    if (lexicalHandler != null) {
      String ps = pub.length() == 0 ? null : pub.getString();
      String ss = sys.length() == 0 ? null : sys.getString();
      lexicalHandler.startDTD(name.getString(), ps, ss);
    }
  }

  public void endDTD() throws Exception {
    if (lexicalHandler != null) {
      lexicalHandler.endDTD();
    }
  }

  //TODO!!!!
  public void onContentReference(Reference ref) throws Exception {
    if (lexicalHandler != null && contentHandler != null) {
      lexicalHandler.startEntity(ref.getName().toString());
      String res = ref.resolve();
      char[] data = new char[res.length()];
      res.getChars(0, data.length, data, 0);
      contentHandler.characters(data, 0, data.length);
      lexicalHandler.endEntity(ref.getName().toString());
    }
  }

  public void startPrefixMapping(CharArray prefix, CharArray uri) throws Exception {
    if (contentHandler != null) {
      contentHandler.startPrefixMapping(get(prefix), get(uri != null ? uri : CharArray.EMPTY));
    }
  }

  public void endPrefixMapping(CharArray prefix) throws Exception {
    if (contentHandler != null) {
      contentHandler.endPrefixMapping(prefix.toString());
    }
  }

  public DeclHandler getDeclHandler() {
    return this.declHandler;
  }

  public void setDeclHandler(DeclHandler declHandler) {
    this.declHandler = declHandler;
  }

  public DocumentHandler getDocumentHandler() {
    return this.documentHandler;
  }

  public void setDocumentHandler(DocumentHandler documentHandler) {
    this.documentHandler = documentHandler;
  }

  public DTDHandler getDTDHandler() {
    return this.dtdHandler;
  }

  public void setDTDHandler(DTDHandler dtdHandler) {
    this.dtdHandler = dtdHandler;
  }

  public LexicalHandler getLexicalHandler() {
    return this.lexicalHandler;
  }

  public void setLexicalHandler(LexicalHandler lexicalHandler) {
    this.lexicalHandler = lexicalHandler;
  }

  public ContentHandler getContentHandler() {
    return this.contentHandler;
  }

  public void setContentHandler(ContentHandler contentHandler) {
    this.contentHandler = contentHandler;
  }

  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  public void onWarning(String warning) throws Exception {
    if (errorHandler != null) {
      if (warning.indexOf("No DTD detected") > -1) {
        errorHandler.error(new SAXParseException(warning, new LocatorImpl()));
      } else {
        errorHandler.warning(new SAXParseException(warning, new LocatorImpl()));
      }
    }
  }

  public void onStartContentEntity(CharArray name, boolean isExpanding) throws Exception {
    if (lexicalHandler != null) {
      lexicalHandler.startEntity(get(name));
    }
  }

  public void onEndContentEntity(CharArray name) throws Exception {
    if (lexicalHandler != null) {
      lexicalHandler.endEntity(get(name));
    }
  }

  public void onCustomEvent(int eventId, Object obj) throws Exception {
    if (eventId == SKIPPED_ENTITY) {
      if (contentHandler != null) {
        contentHandler.skippedEntity(((CharArray) obj).toString());
      }
    } else if (eventId == IGNORABLE_WHITESPACE) {
      ignorableWhitespace = ((Boolean)obj).booleanValue();
    } else if (eventId == XML_SPEC_ERROR) {
      if (errorHandler != null) {
        errorHandler.error(new NestedSAXParseException(((Exception) obj).toString(), new LocatorImpl(), (Exception) obj));
      }
    }
  }

}

