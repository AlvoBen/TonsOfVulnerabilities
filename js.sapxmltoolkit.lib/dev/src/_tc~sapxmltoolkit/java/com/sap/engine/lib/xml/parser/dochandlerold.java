package com.sap.engine.lib.xml.parser;

/**
 * Class description, - If you would like to use the XMLParser you should implement
 * this interface
 *
 * @author Vladimir Savtchenko, e-mail: vlast@usa.net
 * @version 1.00
 */
import java.util.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.helpers.Reference;

public interface DocHandlerOld {

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
  public void callEmptyElementTag(CharArray QName, CharArray prefix, CharArray localName, String uri, Vector attList) throws Exception;


  /**
   * Called when an empty element tag
   *
   * @param   QName  the name of the element
   * @param   attList  a vector with elements of type Reference describing, the attrubute value
   * @exception   Exception
   */
  //public void callEmptyElementTag(String QName, Vector attList) throws Exception;
  public void callEmptyElementTag(CharArray QName, Vector attList) throws Exception;


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
  public void startElement(CharArray QName, CharArray localName, CharArray prefix, String uri, Vector attList) throws Exception;


  /**
   * Called when an empty element tag
   *
   * @param   QName  the name of the element
   * @param   attList  a vector with elements of type Reference describing, the attrubute value
   * @exception   Exception
   */
  //public void startElement(String QName, Vector attList) throws Exception;
  public void startElement(CharArray QName, Vector attList) throws Exception;


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
  public void endElement(CharArray QName, CharArray localName, CharArray prefix, String uri) throws Exception;


  /**
   * Called when an end element tag is detected
   *
   * @param   QName  the name of the element
   * @param   attList  a vector with elements of type Reference describing, the attrubute value
   * @exception   Exception
   */
  //public void endElement(String sName) throws Exception;
  public void endElement(CharArray sName) throws Exception;


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
  public void startPrefixMapping(String prefix, String uri) throws Exception;


  public void endPrefixMapping(String prefix) throws Exception;

}

