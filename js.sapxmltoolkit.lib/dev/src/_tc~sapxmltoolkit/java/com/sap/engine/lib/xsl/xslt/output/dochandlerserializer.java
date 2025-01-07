package com.sap.engine.lib.xsl.xslt.output;

import java.io.OutputStream;
import java.io.Writer;
import java.util.HashSet;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import com.sap.engine.lib.xml.dom.BinaryTextImpl;
import com.sap.engine.lib.xml.parser.handlers.EmptyDocHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;

/**
 * The general class for the output package.
 * Implements the handlers of the SAX API.
 * Before using this class, invokers should first set the
 * output Properies, then specify the Writer or OutputStream
 * to which data will be sent.
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public class DocHandlerSerializer extends EmptyDocHandler {

  protected InstanceHolder holder = new InstanceHolder();
  protected Method method = holder.getMethodXML();
  protected Encoder encoder; //= new Encoder(); // This class is reusable
  protected Indenter indenter = holder.getIndenterEmpty();
  protected HashSet cDataSectionElements = new HashSet();
  protected Properties outputProperties = null;
  protected HashSet options = new HashSet();
  protected boolean firstElementReached = false;
  protected boolean bMustCloseOnEnd = false;
  protected boolean escapingSaver;
  
  public DocHandlerSerializer() {
    encoder = new Encoder();//EncoderPool.getEncoderInstance();
    options.addAll(Options.DEFAULT);
    encoder.setOwner(this);
    method.setOwner(this);
    bMustCloseOnEnd = false;
    
  }

  public DocHandlerSerializer(Writer writer, Properties outputProperties) throws OutputException {
    this();
    setOutputProperties(outputProperties);
    setWriter(writer);
  }

  public DocHandlerSerializer(OutputStream outputStream, Properties outputProperties) throws OutputException {
    this();
    setOutputProperties(outputProperties);
    setOutputStream(outputStream);
  }

  public void setOutputProperties(Properties x) {
    outputProperties = x; //(Properties) x.clone();
  }

  public void setOutputStream(OutputStream outputStream) throws OutputException {
    encoder.init(outputStream, outputProperties.getProperty(OutputKeys.ENCODING));
  }

  public void setWriter(Writer writer) throws OutputException {
    encoder.init(writer, outputProperties.getProperty(OutputKeys.ENCODING));
  }

  public void startDocument() throws Exception {
    firstElementReached = false;
    // Setting the indentation
    String s = outputProperties.getProperty(OutputKeys.INDENT);

    if (Tools.isYes(s)) {
      indenter = holder.getIndenterImpl();
    } else {
      indenter = holder.getIndenterEmpty();
    }

    indenter.setOwner(this);
    /*
     Initialization of the Method field is delayed until the first element
     is processed. See method startDocument_internal.
     */
  }

  /**
   * Called immediately after reaching the first element of the document.
   * Such a 'delay' is needed, because the method of output might depend
   * on that element.
   */
  protected void startDocument_internal(String rootElementLocalName) throws Exception {
    Tools.parseNMTOKENS(outputProperties.getProperty(OutputKeys.CDATA_SECTION_ELEMENTS), cDataSectionElements);
    String s;
    // Setting the method
    s = outputProperties.getProperty(OutputKeys.METHOD);

    if (Tools.notEmpty(s)) {
      s = s.trim();

      if (s.equalsIgnoreCase("html")) {
        method = holder.getMethodHTML();
      } else if (s.equalsIgnoreCase("text")) {
        method = holder.getMethodText();
      } else if (s.equalsIgnoreCase("dump")) {
        method = holder.getMethodDump();
      } else {
        method = holder.getMethodXML();
      }
    } else {
      if (rootElementLocalName.equalsIgnoreCase("html")) {
        method = holder.getMethodHTML();
      } else {
        method = holder.getMethodXML();
      }
    }

    method.setOwner(this);
    encoder.setOwner(this);
    options.clear();
    String omit = outputProperties.getProperty(OutputKeys.OMIT_XML_DECLARATION);
    String version = outputProperties.getProperty(OutputKeys.VERSION);
    String encoding = encoder.getEncodingName();
    String standalone = outputProperties.getProperty(OutputKeys.STANDALONE);
    indenter.startDocument(omit, version, encoding, standalone);
    String doctypePublic = outputProperties.getProperty(OutputKeys.DOCTYPE_PUBLIC);
    String doctypeSystem = outputProperties.getProperty(OutputKeys.DOCTYPE_SYSTEM);

//    if ((Tools.notEmpty(doctypePublic) || Tools.notEmpty(doctypeSystem)) && (method instanceof MethodXML)) {
    if (Tools.notEmpty(doctypePublic) || Tools.notEmpty(doctypeSystem)) {
//      if (Tools.notEmpty(doctypePublic) && !Tools.notEmpty(doctypeSystem)) {
//        throw new OutputException("Illegal output XSLT properties for doctype : public id is specified, but system id is not.");
//      }

      indenter.startDTD(rootElementLocalName, doctypePublic, doctypeSystem);
      indenter.endDTD();
    }
  }

  public void endDocument() throws Exception {
    indenter.endDocument();
    encoder.flush();

    if (bMustCloseOnEnd) {
      encoder.close();
    }
  }

  protected void characters(char[] ch, int start, int length) throws Exception {
    if (!firstElementReached) {
      firstElementReached = true;
      startDocument_internal("");
    }

    indenter.characters(ch, start, length);
  }


  private void comment(char[] ch, int start, int length) throws Exception {
    if (!firstElementReached) {
      firstElementReached = true;
      startDocument_internal("");
    }

    indenter.comment(ch, start, length);
  }

  public void startDTD(String name, String publicId, String systemId) throws Exception {
    //    indenter.startDTD0();
    method.startDTD(name, publicId, systemId);
    //    indenter.startDTD1();
  }

  public void endDTD() throws Exception {
    //    indenter.endDTD0();
    method.endDTD();
    //    indenter.endDTD1();
  }

  public void onXMLDecl(String version, String encoding, String ssdecl) {
    // Ignore
  }

  public void startElementStart(CharArray uri, CharArray localName, CharArray qName) throws Exception {
    if (!firstElementReached) {
      firstElementReached = true;
      startDocument_internal(qName.getString());
    }

    indenter.startElement0(uri, localName, qName);
  }

  public void addAttribute(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception {
    indenter.attribute(uri, localName, qname, value);
  }

  public void startElementEnd(boolean b) throws Exception {
    indenter.startElement1(b);
  }

  public void endElement(CharArray uri, CharArray localName, CharArray qName, boolean b) throws Exception {
    indenter.endElement(uri, localName, qName, b);
  }

  public void charData(CharArray carr, boolean disableOutputEscaping) throws Exception {
    //    boolean escapingEnabled = encoder.isEscapingEnabled();
    //    if (disableOutputEscaping) {
    //      encoder.disableOutputEscaping();
    //    } else {
    //      encoder.enableOutputEscaping();
    //    }
    characters(carr.getData(), carr.getOffset(), carr.length());
    //    if (escapingEnabled) {
    //      encoder.enableOutputEscaping();
    //    } else {
    //      encoder.disableOutputEscaping();
    //    }
    /*
     if (disableOutputEscaping) {
     //encoder.disableOutputEscaping();
     // dump
     //indenter.characters("[0]".toCharArray(), 0, 3);
     characters(carr.getData(), carr.getOffset(), carr.length());
     // dump
     //indenter.characters("[1]".toCharArray(), 0, 3);
     //encoder.enableOutputEscaping();
     } else {
     //encoder.enableOutputEscaping();
     characters(carr.getData(), carr.getOffset(), carr.length());
     //encoder.disableOutputEscaping();
     }
     */
  }

  public void onPI(CharArray target, CharArray data) throws Exception {
    if (!firstElementReached) {
      firstElementReached = true;
      startDocument_internal("");
    }

    if (target.equals(javax.xml.transform.Result.PI_DISABLE_OUTPUT_ESCAPING)) {
      encoder.disableOutputEscaping();
      return;
    }

    if (target.equals(javax.xml.transform.Result.PI_ENABLE_OUTPUT_ESCAPING)) {
      encoder.enableOutputEscaping();
      return;
    }

    indenter.processingInstruction(target, data);
  }

  public void onComment(CharArray text) throws Exception {
    comment(text.getData(), text.getOffset(), text.length());
  }

  public void onCDSect(CharArray text) throws Exception {
    //    LogWriter.getSystemLogWriter().println("DocHandlerSerializer.onCDSect: text=" + text);
    indenter.startCDATA(); //added by SASHO
    encoder.disableOutputEscaping();
    indenter.characters(text.getData(), text.getOffset(), text.length()); //added by SASHO
    encoder.enableOutputEscaping();
    indenter.endCDATA(); //added by SASHO
  }


  public void onDTDEntity(Entity entity) throws Exception {
    method.onDTDEntity(entity);
    // Ignore
  }

  public void startDTD(CharArray name, CharArray pub, CharArray sys) throws Exception {
    method.startDTD(name.toString(), pub.toString(), sys.toString());
    // Ignore
  }

  public void setOption(String name, boolean value) throws OutputException {
    if (name == null) {
      throw new OutputIllegalOptionException("The name of the option is null.");
    }

    if (!Options.ALLOWED.contains(name)) {
      throw new OutputIllegalOptionException("Option '" + name + "' not recognized");
    }

    if (value) {
      options.add(name);
    } else {
      options.remove(name);
    }
  }

  public boolean getOption(String name) throws OutputException {
    if (name == null) {
      throw new OutputIllegalOptionException("The name of the option is null.");
    }

    if (!Options.ALLOWED.contains(name)) {
      throw new OutputIllegalOptionException("Option '" + name + "' not recognized");
    }

    return options.contains(name);
  }

  Encoder getEncoder() {
    return encoder;
  }

  Indenter getIndenter() {
    return indenter;
  }

  Method getMethod() {
    return method;
  }

  public Properties getOutputProperties() {
    return outputProperties;
  }

  HashSet getCDataSectionElements() {
    return cDataSectionElements;
  }

  public void setCloseOnEnd(boolean value) {
    bMustCloseOnEnd = value;
  }

  public void onStartContentEntity(CharArray name, boolean isExpandingReferences) throws OutputException {
    escapingSaver = encoder.isEscapingEnabled();
    encoder.disableOutputEscaping();
    indenter.startContentReference(name);
  }

  public void onEndContentEntity(CharArray name) throws OutputException {
    indenter.endContentReference(name);
    encoder.setEscaping(escapingSaver);
  }

  public void onCustomEvent(int eventId, Object obj) throws Exception {
     if (eventId == BinaryTextImpl.TYPE){
       BinaryTextImpl text = (BinaryTextImpl) obj;
       byte[] b = text.getBinaryData();
       if (b!=null){
         for (int i=0;i<b.length;i++){
           encoder.out((char) b[i]);
         }
       }
     }
  }

}

