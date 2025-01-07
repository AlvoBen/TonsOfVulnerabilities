package com.sap.engine.lib.xml.parser;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.helpers.Reference;

/**
 * Class description, - If you would like to use the XMLParser you should implement
 * this interface
 *
 * @author Vladimir Savtchenko, e-mail: vlast@usa.net
 * @version 1.00
 */
public interface DocHandler {

  /**
   * Called immediately before calling addAttribute
   * when the attribute has a default or fixed value
   * and has not been explicitly specified in the XML.
   */
  public static final int ATTRIBUTE_IS_NOT_SPECIFIED = 1000; 
  /**
   * Called before calling startDocument,
   * whenever the URL of the XML being parsed is known
   */
  public static final int DOCUMENT_URL = 1001;
  /**
   * Called before calling startDocument.
   * The second argument is a non-null Boolean,
   * either Boolean.TRUE or Boolean.FALSE
   */
  public static final int NAMESPACE_AWARENESS = 1002;
  /**
   * Called from AdvancedXMLStreamReader.addInputFromEntity, if the feature
   * http://xml.org/sax/features/external-general-entities. is set to true.
   * If the implentation is SAXDocHandler, then the method skippedEntity will be called,
   * the second parameter is a CharArray specifying the name of the entity
   */
  public static final int SKIPPED_ENTITY = 1003;

  /**
   * Called to indicate that ignorable whitespace is following. (In case of true)
   */
  public static final int IGNORABLE_WHITESPACE = 1004;

  /**
   * Called to indicate that an "error" according the XML Spec is following
   */
  public static final int XML_SPEC_ERROR = 1005;


  /**
   * This method is called when the XMLDecl is encounted
   *
   * @param   version  the XML version, empty string if not found
   * @param   encoding  the encoding used in XML, empty string if not found
   * @param   ssdecl  the standalone declaration, empty string if not found
   * @exception   Exception  explaining the error
   */
  public void onXMLDecl(String version, String encoding, String ssdecl) throws Exception;


  /**
   * Called when an empty element tag is detected
   *
   * @param   QName  the qualified name
   * @param   uri  the URI, empty string if no uri
   * @param   prefix  the PREFIX, empty string if no prefix
   * @param   localName  the LOCALNAME, same as QName if no prefix
   * @param   attList  a vector with elements of type Reference describing, the attrubute value
   * @exception   Exception  explaining the error
   */
  //public void callEmptyElementTag(String QName, String uri, String prefix, String localName, Vector attList) throws Exception;
  //public void callEmptyElementTag(CharArray QName, CharArray prefix, CharArray localName, String uri, Vector attList) throws Exception;
  /**
   * Called when an empty element tag
   *
   * @param   QName  the name of the element
   * @param   attList  a vector with elements of type Reference describing, the attrubute value
   * @exception   Exception
   */
  //public void callEmptyElementTag(String QName, Vector attList) throws Exception;
  //public void callEmptyElementTag(CharArray QName, Vector attList) throws Exception;
  /**
   * Called when an start element tag is detected
   *
   * @param   QName  the qualified name
   * @param   uri  the URI, empty string if no uri
   * @param   prefix  the PREFIX, empty string if no prefix
   * @param   localName  the LOCALNAME, same as QName if no prefix
   * @param   attList  a vector with elements of type Reference describing, the attrubute value
   * @exception   Exception  explaining the error
   */
  //public void startElement(String QName, String uri, String prefix, String localName, Vector attList) throws Exception;
  //public void startElement(CharArray QName, CharArray localName, CharArray prefix, String uri, Vector attList) throws Exception;
  public void startElementStart(CharArray uri, CharArray localName, CharArray qname) throws Exception;


  public void addAttribute(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception;


  public void startElementEnd(boolean isEmpty) throws Exception;


  /**
   * Called when an end element tag is detected
   *
   * @param   QName  the qualified name
   * @param   uri  the URI, empty string if no uri
   * @param   prefix  the PREFIX, empty string if no prefix
   * @param   localName  the LOCALNAME, same as QName if no prefix
   * @exception   Exception  explaining the error
   */
  //public void endElement(String QName, String uri, String prefix, String localName) throws Exception;
  public void endElement(CharArray uri, CharArray localName, CharArray qname, boolean isEmpty) throws Exception;


  /**
   * Called when an end element tag is detected
   *
   * @param   QName  the name of the element
   * @param   attList  a vector with elements of type Reference describing, the attrubute value
   * @exception   Exception
   */
  //public void endElement(String sName) throws Exception;
  //public void endElement(CharArray sName) throws Exception;
  /**
   * Called on document start
   *
   * @exception   Exception
   */
  public void startDocument() throws Exception;


  /**
   * Called on document end
   *
   * @exception   Exception
   */
  public void endDocument() throws Exception;


  /**
   * Called when CharData is dectected
   *
   * @param   data  an array containing the data
   * @param   len  the length of the data, that has to be read from the array
   * @exception   Exception
   */
  //public void charData(char [] data, int len) throws Exception;
  public void charData(CharArray carr, boolean bDisableOutputEscaping) throws Exception;


  /**
   * Called when a Processing Instruction is detected
   *
   * @param   target  pi-target
   * @param   data  pi-data
   * @exception   Exception
   */
  public void onPI(CharArray target, CharArray data) throws Exception;


  /**
   * Called when a comment is detected
   *
   * @param   text  the comment text
   * @exception   Exception
   */
  public void onComment(CharArray text) throws Exception;


  /**
   * Called when a <!CDATA[]> is detected
   *
   * @param   text  the text of the CDATA - section
   * @exception   Exception
   */
  public void onCDSect(CharArray text) throws Exception;


  /**
   * Called when an DTD Element is detected
   *
   * @param   name  the name of the element
   * @param   model  the model of the structure that this element must have
   * @exception   Exception
   */
  public void onDTDElement(CharArray name, CharArray model) throws Exception;


  /**
   * Called on start of Element Attlist in DTD
   *
   * @param   name  the name of the element
   * @exception   Exception
   */
  public void onDTDAttListStart(CharArray name) throws Exception;


  /**
   * Called when a DTD Attlist Item is detected
   *
   * @param   name  the name of the element
   * @param   attname  the name of the attribute
   * @param   type  the attribute type
   * @param   defDecl  the default value for this attribute
   * @param   vAttValue  the value of the Attribute as a Vector containing References
   * @param   note  the name of the notation associated with this attribute
   * @exception   Exception
   */
  public void onDTDAttListItem(CharArray name, CharArray attname, String type, String defDecl, CharArray vAttValue, String note) throws Exception;


  /**
   * Called on dtd AttListEnd
   *
   * @exception   Exception
   */
  public void onDTDAttListEnd() throws Exception;


  //public void onDTDEntity(String name, String pub, String sys, Vector vEntityValue, boolean pe, String note);
  /**
   * Called when a DTD Entitiy is detected
   *
   * @param   entity  the entity as an Entity object
   * @exception   Exception
   */
  public void onDTDEntity(Entity entity) throws Exception;


  //public void onDTDUnparsedEntity(String name, String pub, String sys, String note, boolean pe);
  /**
   * Called when a dtd notation is detected
   *
   * @param   name  of the notation
   * @param   pub  public id
   * @param   sys  system id
   * @exception   Exception
   */
  public void onDTDNotation(CharArray name, CharArray pub, CharArray sys) throws Exception;


  /**
   * Called on start of dtd
   *
   * @param   name  name of document which this DTD belongs to
   * @param   pub  public id, if any
   * @param   sys  system id, if any
   * @exception   Exception
   */
  public void startDTD(CharArray name, CharArray pub, CharArray sys) throws Exception;


  /**
   * Called on dtd End
   *
   * @exception   Exception
   */
  public void endDTD() throws Exception;


  /**
   * Called when an entity refrence is detected into the content
   *
   * @param   ref  the refrence as a Reference object
   * @exception   Exception
   */
  public void onContentReference(Reference ref) throws Exception;


  /**
   * Called when a new namespace mapping is started
   *
   * @param   prefix  the prefix
   * @param   uri  the uri of the new namespace
   * @exception   Exception
   */
  public void startPrefixMapping(CharArray prefix, CharArray uri) throws Exception;


  public void endPrefixMapping(CharArray prefix) throws Exception;


  public void onWarning(String warning) throws Exception;


  public void onCustomEvent(int eventId, Object obj) throws Exception;


  public void onStartContentEntity(CharArray name, boolean isExpandingReferences) throws Exception;


  public void onEndContentEntity(CharArray name) throws Exception;

}

