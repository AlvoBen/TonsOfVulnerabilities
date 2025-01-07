package com.sap.engine.lib.xsl.xslt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
//import java.util.Hashtable;
import java.util.Stack;
//import java.util.Vector;

import javax.xml.transform.Result;

import org.xml.sax.ContentHandler;
//import org.xml.sax.helpers.AttributesImpl;

import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.handlers.EncodingHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Encoding;

/**
 *  Class description, - Formats the XSLProcessor output to XML, HTML or text using byte or
 *  character stream or (for XML) DOM tree or user defined org.sax.ContentHandler.
 *
 *  @author Vladislav Velkov
 *  @author Vladimir Savtchenko
 *  @version 1.00
 */

public final class XSLOutputProcessor {

  // output modes
  static final byte TEXT_OUTPUT=1;
  static final byte HTML_OUTPUT=2;
  static final byte XML_OUTPUT=3;

  // error codes of XSLOutputException
  public static final byte IO_ERROR=0;
  public static final byte PROCESSING_ALREADY_STARTED_ERROR=1;
  public static final byte NO_OUTPUTSTREAM_SPECIFIED_ERROR=2;
  public static final byte INVALID_XML_ERROR=3;

  private byte outputmode = XML_OUTPUT;
  private boolean dochasstarted = false;    //Shows if the processing has begun
  private boolean hasprefixmapping = false;
  private boolean emptyel=false;
  //private boolean firstelement = false;

  private OutputStream xsloutstream = null;
  private Writer xslwriter = null;
  private ContentHandler xslhandler = null;
  private String version = "1.0";

  private boolean bProcessAttributeValueTemplate = false;
  private CharArray chAttributeValueProcessing = new CharArray(100);
//

//  private final static CharArray caDGNorm = new CharArray("dgnorm_document");
//  private final static CharArray caDoc = new CharArray("#document");
  private final static CharArray caEmpty = new CharArray().setStatic();
//  private final static CharArray caNewLine = new CharArray("\n");

  private final static CharArray OT = new CharArray("<").setStatic();
//  private final static CharArray ET = new CharArray(">");
  private final static CharArray SP = new CharArray(" ").setStatic();
  private final static CharArray EQ = new CharArray("=").setStatic();
  private final static CharArray ETN = new CharArray(">\n").setStatic();
//  private final static CharArray ETSN = new CharArray("/>\n");
  private final static CharArray ETS = new CharArray("/>").setStatic();
  private final static CharArray CH_ENDTAG_STD = new CharArray("</").setStatic();
  private final static CharArray CH_GT_NL = new CharArray(">\n").setStatic();
  private final static CharArray CH_COMMENT_START = new CharArray("<!--").setStatic();
  private final static CharArray CH_PISTART = new CharArray("<?").setStatic();
  private final static CharArray CH_PIEND_NL = new CharArray("?>\n").setStatic();
  private final static CharArray CH_COMMENT_END = new CharArray("-->").setStatic();
  private final static CharArray CH_PI_DISABLE_OUTPUT_ESCAPING = new CharArray(Result.PI_DISABLE_OUTPUT_ESCAPING).setStatic();
  private final static CharArray CH_PI_ENABLE_OUTPUT_ESCAPING = new CharArray(Result.PI_ENABLE_OUTPUT_ESCAPING).setStatic();

  private final static CharArray CH_SPACE = new CharArray(" ").setStatic();
//  private final static CharArray NL = new CharArray("\n");
//  private final static String CH_CDATA = "CDATA";
//  private final static String CH_EMPTY_STR = "";
  private final static CharArray chHtmlMetaAfterHead = new CharArray("<meta content='text/html; charset=utf-8' http-equiv='Content-Type'/>").setStatic();


  private CharArray normalizeCarr = new CharArray(100);
//  private CharArray tempcarr = new CharArray(100);


//  private Hashtable hash = new Hashtable();
  private CharArray attcarr = new CharArray(100);
//  private AttributesImpl attImpl = new AttributesImpl();

//  private Object tmp = null;
//  private CharArray tmp2 = null;
//  private String tmps = null;
//  private int maxsize = 100;
//  private int cursize = 0;
  private DocHandler dochandler = null;


  private Stack attributeValueChStack = new Stack();
  private EncodingHandler encodingHandler = new EncodingHandler();
  private Encoding encoding = encodingHandler.getEncoding(new CharArray("utf-8"));
  private boolean bDisableEscapingOverload = false;
  
  private Stack startedElementStack = new Stack();
  private Stack emptyelStack = new Stack();





  private byte [] bufwrite = new byte[2000];
  private int bufwriteidx = 0;

  /**
   *  Makes some initialisations
   *
   */
  public XSLOutputProcessor() {
    xsloutstream = null;
    xslwriter = null;
    xslhandler = null;
  }

  public void reuse() {
    bufwriteidx = 0;
    xsloutstream = null;
    xslwriter = null;
    xslhandler = null;
    dochasstarted = false;
    hasprefixmapping = false;
    emptyel = false;
    //firstelement = false;
    //xsloutstream = null;
    //xslwriter = null;
    //xslhandler = null;
    disableOutputEscapingCounter = 0;
    bProcessAttributeValueTemplate = false;
    chAttributeValueProcessing.clear();
    attributeValueChStack.clear();
    bStartedElement = false;
    iattList.clear();
  }
  
  public void clearAttributeValueProcessing() {
    bProcessAttributeValueTemplate = false;
    
    startedElementStack.clear();
    emptyelStack.clear();
    
    bStartedElement = false;
    emptyel = false;
  }
    


  public void setDocHandler(DocHandler dochandler) {
//    LogWriter.getSystemLogWriter().println("XSLOutputProcessor.setDocHandler(): setting dochandler: " + dochandler + ", this = " + this);
    this.dochandler = dochandler;
  }


  /**
   *  Sets output to be sent to OutputStream
   *  Throws XSLOutputException if method void startDocument() is already called/
   */
  public void setByteStream(OutputStream stream) throws XSLOutputException {
    if (!dochasstarted) xsloutstream = stream;
    else throw new XSLOutputException(PROCESSING_ALREADY_STARTED_ERROR);
  }

  /**
   *  Sets output to be sent to Writer
   *  Throws XSLOutputException if method void startDocument() is already called/
   *
   */
  public void setCharacterStream(Writer writer) throws XSLOutputException {
    if (!dochasstarted) xslwriter = writer;
    else throw new XSLOutputException(PROCESSING_ALREADY_STARTED_ERROR);
  }

  /**
   *  Sets output to be sent to user defined org.sax.ContentHandler
   *  Throws XSLOutputException if method void startDocument() is already called/
   *
   */
  public void setContentHandler(ContentHandler handler) throws XSLOutputException {
    if (!dochasstarted) xslhandler = handler;
    else throw new XSLOutputException(PROCESSING_ALREADY_STARTED_ERROR);
  }


  /**
   *  Sets some properties of the output, such as
   *  mode (XML,HTML or text), version, encoding.
   *
   */
  public void setOutputMode(XSLOutputNode node) {
    String mode = node.getMethod();
    if (mode.equals("xml")) outputmode = XML_OUTPUT;
    else if (mode.equals("text")) outputmode = TEXT_OUTPUT;
    else if (mode.equals("html")) outputmode = HTML_OUTPUT;
    //LogWriter.getSystemLogWriter().println(outputmode);
    version = node.getVersion();
    if (version == null) version="1.0";
    encoding = encodingHandler.getEncoding(new CharArray(node.getEncoding()));

    if (encoding == null) {
      encoding = encodingHandler.getEncoding(new CharArray("utf-8"));
    }
  }


  private void bufferWrite(byte b) throws IOException {
    if (bufwriteidx == bufwrite.length) {
      xsloutstream.write(bufwrite);
      bufwriteidx = 0;
    }
    //LogWriter.getSystemLogWriter().println("--writing to buffer: -" + (char)b + " = " + (int)b);
    bufwrite[bufwriteidx++] = b;
  }




  private void dowrite(CharArray w) throws XSLOutputException {
    try {
      if (bProcessAttributeValueTemplate) {
//        LogWriter.getSystemLogWriter().println("XSLOutputProcessor.dowrite: wrining to sht:" + w);
        chAttributeValueProcessing.append(w);
      } else {
        if (xsloutstream != null) {
          int o = w.getOffset();
          int l = w.length() + o;
          char [] ddd = w.getData();
          for (int i=o; i<l; i++) {
            bufferWrite((byte)ddd[i]);
          }
        }
        if (xslwriter != null) {
          xslwriter.write(w.getData(), w.getOffset(), w.getSize());
        }
      }
    } catch (IOException e) {
      throw new XSLOutputException(IO_ERROR, e.toString());
    }
  }

  private void dowrite(char w) throws XSLOutputException {
    try {
      if (bProcessAttributeValueTemplate) {
//        LogWriter.getSystemLogWriter().println("XSLOutputProcessor.dowrite: wrining to sht:" + w);
        chAttributeValueProcessing.append(w);
      } else {
        if (xsloutstream != null) {
          bufferWrite((byte)w);
        }
        if (xslwriter != null) {
          xslwriter.write(w);
        }
      }
    } catch (IOException e) {
      throw new XSLOutputException(IO_ERROR, e.toString());
    }
  }


  /**
   *  Tells the XSLOutputProcessor to send the output data to a char array
   *  instead of the specified output or instead of the current char array.
   *
   */
  public void startAttributeValueProcessing(CharArray charr) {
//    LogWriter.getSystemLogWriter().println("Starting attribute ValueProcssing, current startedel="+bStartedElement);
    if (charr != null) {
      bProcessAttributeValueTemplate = true;
      charr.clear();
      chAttributeValueProcessing = charr;
      attributeValueChStack.push(charr);
    }
    startedElementStack.push(bStartedElement?Boolean.TRUE:Boolean.FALSE);
    emptyelStack.push(emptyel?Boolean.TRUE:Boolean.FALSE);
    bStartedElement = false;
    emptyel=false;
  }

  /**
   *  Tells the XSLOutputProcessor to send the output data to the previous char array or if there
   *  is no such array then sends it to the specified output.
   *
   */
  public void stopAttributeValueProcessing() {
    stopAttributeValueProcessing(true);
  }
  public void stopAttributeValueProcessing(boolean chstack) {
    if (chstack) {
      attributeValueChStack.pop();
      if (attributeValueChStack.empty() == false) {
        chAttributeValueProcessing = (CharArray)attributeValueChStack.peek();
      } else {
        bProcessAttributeValueTemplate = false;
      }
    }
    
    bStartedElement = ((Boolean)startedElementStack.pop()).booleanValue();
    emptyel = ((Boolean)emptyelStack.pop()).booleanValue();
//    LogWriter.getSystemLogWriter().println("Finishing attribute ValueProcssing. new startedElement="+ bStartedElement);
//    Thread.dumpStack();
  }

  /**
   *  Initializes output (if necessary) and
   *  writes a header to it (again if necessary).
   *  If output mode is not specified then starts to build a DOM tree.
   */

  public void startDocument() throws XSLOutputException {
    try {
      if (dochandler != null) {
        dochandler.startDocument();
      } else {

        dochasstarted = true;
        if (xslhandler != null) {
          xslhandler.startDocument();
        }
      }
    } catch (Exception e) {
      throw new XSLOutputException(e.toString(), e);
    }
  }

  /**
   *  If output mode is not specified then finnishes DOM tree building, determines it
   *  and sends data to the output.
   *  Writes terminating string to the output (if necessary)
   *
   *
   */
  public void endDocument() throws XSLOutputException {
    try {
      if (dochandler != null) {
//        LogWriter.getSystemLogWriter().println("here");
        dochandler.endDocument();
      } else {
        if (xsloutstream != null) {
//          LogWriter.getSystemLogWriter().println("hereaaa4444");
          xsloutstream.write(bufwrite, 0, bufwriteidx);
          xsloutstream.close();
         }
         if (xslwriter != null) {
//          LogWriter.getSystemLogWriter().println("hereaaa");
           xslwriter.close();
         }

        if (xslhandler != null) {
            xslhandler.endDocument();
        }
      }

    } catch (Exception e) {
      //e.printStackTrace();
      throw new XSLOutputException(IO_ERROR, e);
    }
  }

  /**
   * Replaces special characters with appropriate sets of symbols
   * according to XML specification.
   */
  private byte convarr[] = new byte[10];
  private CharArray normalize(CharArray s) throws XSLOutputException {
    normalizeCarr.clear();

    int len = (s != null) ? s.getSize() : 0;
    char []data = s.getData();
    char ch = 0;
    for ( int i = s.getOffset(); i < len; i++ ) {
      ch = data[i];
      //LogWriter.getSystemLogWriter().println("ch = " + (byte)ch);
      if (ch == '<' || ch == '>' || ch == '&' || ch > 0x7f|| ch<0) {
        switch ( ch ) {
          case '<': {
            normalizeCarr.append("&lt;");
            break;
          }
          case '>': {
            normalizeCarr.append("&gt;");
            break;
          }
          case '&': {
            normalizeCarr.append("&amp;");
            break;
          }
          default: { // ch > 0x7f .. it must be transformed using the current encoding
            int enclen = encoding.reverseEncode(convarr, ch);
            for (int j=0; j< enclen; j++) {
              normalizeCarr.append((char)convarr[j]);
            }

//            while (enclen-- > 0) {
//            if (encoding.equalsIgnoreCase("us-ascii")) {
//              normalizeCarr.append((char)(ch & 0x7F));
//            } else if (encoding.equalsIgnoreCase("utf-8")) {
//              int ll = (new EncodingHandler()).convertUTF8(convarr, ch);
//              for (int j=0; j<ll; j++) {
//                normalizeCarr.append((char)convarr[j]);
//              }
//            } else {
//              throw new XSLOutputException("Unsupported Encoding: " + encoding);
//            }
          }
        }
      } else {
        normalizeCarr.append(ch);
      }
    }
    return (normalizeCarr);
  }


//  private void sortAttributes(InternalAttributeList attr) {
//    if (attr == null) return;
//    for (int i = 0; i < attr.getLength(); i++) {
//      for (int j = 0; j < attr.getLength() - (i+1); j++) {
//        if (attr.compare(j, j+1) > 0) {
//          attr.swap(j, j+1);
//        }
//      }
//    }
//  }


  /**
   *  In XML or HTML mode (if specified) sends opening element tag to the output
   *  and adds a DOM node to a DOM tree when it is not specified.
   *
   */


//  private String get(CharArray item) {
//    tmp = hash.get(item);
//    if (tmp == null && cursize < maxsize) {
//      tmp2 = item.copy();
//      tmps = tmp2.getString();
//      hash.put(tmp2, tmps);
//      cursize++;
//      return tmps;
//    } else if (cursize == maxsize) {
//      return item.getString();
//    } else {
//      return (String)tmp;
//    }
//  }


//  private CharArray crPrefix = new CharArray();
//  private Vector dhAttributes = new Vector();
  public void addAttribute(CharArray uri, CharArray prefix, CharArray qname, CharArray localName, CharArray value) throws XSLException {
    if (dochandler != null && !bProcessAttributeValueTemplate) {
      //int a = qname.indexOfColon();
      //if (a == -1) crPrefix.clear();
      //else crPrefix.substring(qname, 0, a);
      iattList.addAttribute(uri, prefix, qname, localName, value);      
//      try {
//        dochandler.addAttribute(get(uri), crPrefix, localName, qname, "CDATA", value);
//      } catch (Exception e) {
//        throw new XSLException("Exception while adding attribute to DocHandler", e);
//      }
    } else if (xsloutstream != null || xslwriter != null || bProcessAttributeValueTemplate) {
//      LogWriter.getSystemLogWriter().println("XSLOutputProcessor.addAttirubte: STREAM Attribute: " + localName + ", value=" + value);
      dowrite(SP);
      dowrite(((outputmode == HTML_OUTPUT)?localName: qname));
      dowrite(EQ);
      attcarr.clear();
      char usequot = '\"';
      if (value.indexOf('\'') == -1) usequot = '\'';
      dowrite(usequot);
      dowrite(value);
      dowrite(usequot);
      dowrite(attcarr);
    }
  }
  
  /**
   * Called to inform the XSLOuputProcessor that a new Element is about to start, so that the internal attributeList should be cleared
   *
   **/
  public void startElement0() throws XSLException {
    try {
//      LogWriter.getSystemLogWriter().println("XSLOutputProfessor.startElement0:" + bProcessAttributeValueTemplate + ", iattlist=" + iattList);
      if (dochandler != null && !bProcessAttributeValueTemplate && iattList != null) {
        for (int i=0; i<iattList.size(); i++) {
          InternalAttribute ia = (InternalAttribute)iattList.get(i);
//          LogWriter.getSystemLogWriter().println("XSLOutputProfessor.startElement: sending Att to docHandler: " + ia.qname);
          dochandler.addAttribute(ia.uri, ia.prefix, ia.localName, ia.qname, "CDATA", ia.value);
        }
        
        iattList.clear();
      }
    } catch (Exception e) {
      throw new XSLException("Error while sending attributes to DocHandler", e);
    }
  }

  private boolean bStartedElement = false;
  private InternalAttributeList iattList = new InternalAttributeList();;
  public void startElement(CharArray namespaceuri, CharArray localname, CharArray qname, InternalAttributeList attr) throws XSLOutputException {
//    LogWriter.getSystemLogWriter().println("XSLOutputprocessor: start Element qname=" + qname + " uri=" + namespaceuri + ", processAttValue=" + bProcessAttributeValueTemplate + ", startedEl="+ bStartedElement);
    try {

      if (bStartedElement) {
        if (dochandler != null && !bProcessAttributeValueTemplate) {
          startElement0();
          dochandler.startElementEnd(false);
        }
        bStartedElement = false;
      }
      bStartedElement = true;
      if (dochandler != null && !bProcessAttributeValueTemplate) {
        dochandler.startElementStart(namespaceuri, localname, qname);//crPrefix, get(namespaceuri), dhAttributes);
        if (attr != null) {
        	iattList.copy(attr);
        }
//        for (int i=0; i<attr.size(); i++) {
//          InternalAttribute ia = (InternalAttribute)attr.get(i);
//          //          LogWriter.getSystemLogWriter().println("XSLOutputProfessor.startElement: sending Att to docHandler: " + ia.qname);
//          dochandler.addAttribute(get(ia.uri), ia.prefix, ia.localName, ia.qname, "CDATA", ia.value);
//        }
        
//        LogWriter.getSystemLogWriter().println("XSLOutputprocessor: in DocHandler call: start Element qname=" + qname + " uri=" + namespaceuri + ", emtyel=" + emptyel);


      } else if (bProcessAttributeValueTemplate) {
          if (outputmode != HTML_OUTPUT) {
            if (localname.equalsIgnoreCase("html")) {
              outputmode = HTML_OUTPUT;
            }
          }
          if (emptyel) {
            dowrite(ETN);
            emptyel = false;
          }

          dowrite(OT);
          dowrite(((outputmode == HTML_OUTPUT)?localname:qname));
          for (int i=0; attr != null && i<attr.getLength(); i++) {
            addAttribute(attr.getAttr(i).uri, attr.getAttr(i).prefix, attr.getAttr(i).qname, attr.getAttr(i).localName, attr.getAttr(i).value);
          }
          emptyel = true;
          if (outputmode == HTML_OUTPUT && localname.equalsIgnoreCase("head")) {
            dowrite(ETN);
            emptyel = false;
            dowrite(chHtmlMetaAfterHead);
          }

      }
    } catch (Exception e) {
      throw new XSLOutputException("Error writing output.", e);
    }
  }

  /**
   *  In HTML or XML mode (if specified) sends a closing element tag to the output.
   *
   *
   */
  public void endElement(CharArray namespaceuri, CharArray localname, CharArray qname) throws XSLOutputException {
//    if (bProcessAttributeValueTemplate) {
//      throw new XSLOutputException("Ending element while processing content of an AttributeValueTemplate or attribute value");
//    }
//        LogWriter.getSystemLogWriter().println("XSLOutputprocessor.endElement : " + qname + ", startedel=" + bStartedElement);
//
    try {
      if (dochandler != null && !bProcessAttributeValueTemplate) {
        if (bStartedElement) {
          startElement0();
          dochandler.startElementEnd(true);
        }
        //LogWriter.getSystemLogWriter().println("XSLOutputprocessor.endElement DocHandler: " + qname);
        dochandler.endElement(namespaceuri, localname, qname, bStartedElement);
        bStartedElement = false;
      } else if (bProcessAttributeValueTemplate) {
//        LogWriter.getSystemLogWriter().println("XSLOutputprocessor.endElement DOWRITE: " + qname);
        if (emptyel) {
          dowrite(ETS);
          emptyel = false;
        } else {
          dowrite(CH_ENDTAG_STD);
          dowrite(((outputmode == HTML_OUTPUT)?localname:qname));
          dowrite(ETN);
        }
        bStartedElement = false;
      }
    } catch (Exception e) {
      throw new XSLOutputException(IO_ERROR, e);
    }
  }



  /**
   *  Sends text to the output if its mode is specified or adds a text DOM node to a DOM tree
   *
   */
  public void characters(CharArray cdata, boolean disableOutputEscaping) throws XSLOutputException {
    try {

//      LogWriter.getSystemLogWriter().println("XSLOutputProcessor.characters(): data=" + cdata +", docHandler=" + dochandler);
//      Thread.dumpStack();
//      for (int i=0; i<cdata.length(); i++) {
//        LogWriter.getSystemLogWriter().println((int)cdata.charAt(i));
//      }

      if (bProcessAttributeValueTemplate) {
        if (!disableOutputEscaping && outputmode != TEXT_OUTPUT) {
          if (!bDisableEscapingOverload) {
            cdata = normalize(cdata);
          }
        }
        if (emptyel && cdata.getSize() > 0) {
          dowrite(ETN);
          emptyel = false;
        }
        dowrite(cdata);
        //chAttributeValueProcessing.append(cdata);
      } else if (dochandler != null) {
        if (bStartedElement && cdata.getSize() > 0) {
          startElement0();
          dochandler.startElementEnd(false);
          bStartedElement = false;
        }
        if (disableOutputEscaping) {
          dochandler.onPI(CH_PI_DISABLE_OUTPUT_ESCAPING, caEmpty);
        }
//        LogWriter.getSystemLogWriter().println("XSLOutputProcessor.characters(): sending to DocHandler - data=" + cdata);
        dochandler.charData(cdata, true);
        if (disableOutputEscaping) {
          dochandler.onPI(CH_PI_ENABLE_OUTPUT_ESCAPING, caEmpty);
        }
      } else {
        //LogWriter.getSystemLogWriter().println("" + disableOutputEscaping +  outputmode + " characters: " + cdata);
        if (!disableOutputEscaping && outputmode != TEXT_OUTPUT) {
//          LogWriter.getSystemLogWriter().println("characters: " + cdata);
          cdata = normalize(cdata);
        }

        if (emptyel && cdata.getSize() > 0) {
          dowrite(ETN);
          emptyel = false;
        }

        dowrite(cdata);
      }
    } catch (Exception e) {
      //e.printStackTrace();
      throw new XSLOutputException(IO_ERROR, e);
    }
  }


  /**
   *  In XML or HTML mode (if specified) sends a comment tag to the output or adds a comment
   *  node to a DOM tree.
   *
   */
  public void comment(CharArray data) throws XSLOutputException {
//    LogWriter.getSystemLogWriter().println("XSLOutputProcessor.comment: startedEl=" + bStartedElement);
    if (bProcessAttributeValueTemplate) {
      throw new XSLOutputException("Comment while processing content of an AttributeValueTemplate or attribute value");
    }

    try {
      if (dochandler != null) {
        if (bStartedElement) {
          startElement0();
          dochandler.startElementEnd(false);
          bStartedElement = false;
        }
        dochandler.onComment(data);
      } else {
        if (outputmode == TEXT_OUTPUT) return;
        if (xsloutstream != null || xslwriter != null) {
          if (emptyel) {
            dowrite(CH_GT_NL);
            emptyel = false;
          }
          dowrite(CH_COMMENT_START);
          dowrite(data);
          dowrite(CH_COMMENT_END);
        }
      }
    } catch (Exception e) {
      throw new XSLOutputException(IO_ERROR, e);
    }
  }

  /**
   *  In XML mode (if specified) sends a processing instruction to the output or adds a
   *  processing instruction node to a DOM tree.
   *
   */
  public void processingInstruction(CharArray target, CharArray data) throws XSLOutputException {
    //LogWriter.getSystemLogWriter().println("PI: " + target + " : " + data);
     //CharArray mch= new CharArray("#");
    try {
      if (dochandler != null) {
        if (bStartedElement) {
          startElement0();
          dochandler.startElementEnd(false);
          bStartedElement = false;
        }
        dochandler.onPI(target, data);
      } else {
        if (outputmode == TEXT_OUTPUT) return;
        if (xsloutstream != null || xslwriter != null) {
          if (emptyel) {
            dowrite(CH_GT_NL);
            emptyel = false;
          }
          dowrite(CH_PISTART);
          dowrite(target);
          dowrite(CH_SPACE);
          dowrite(data);
          if (outputmode == HTML_OUTPUT) {
            dowrite(CH_GT_NL);
          } else {
            dowrite(CH_PIEND_NL);
          }
        }
      }
    } catch (Exception e) {
      throw new XSLOutputException(IO_ERROR, e);
    }
  }
    
  int disableOutputEscapingCounter = 0;
  public void setDisableOutputEscaping(boolean value) throws XSLException {
      if (value) {
        disableOutputEscapingCounter++; 
        bDisableEscapingOverload = value;
      } else {
        disableOutputEscapingCounter--;
        if (disableOutputEscapingCounter == 0) {
          bDisableEscapingOverload = value;
        }
      }
  }
  
  public DocHandler getDocHandler() {
    return this.dochandler;
  }
  
  public boolean getBProcessAVT() {
    return bProcessAttributeValueTemplate;
  }
  
  public CharArray getChProcessAVT() {
    return chAttributeValueProcessing;
  }

  public void setBProcessAVT(boolean bProcessAttributeValueTemplate) {
    this.bProcessAttributeValueTemplate = bProcessAttributeValueTemplate;
  }
  
  public void setChProcessAVT(CharArray chAttributeValueProcessing) {
    this.chAttributeValueProcessing = chAttributeValueProcessing;
  }

}