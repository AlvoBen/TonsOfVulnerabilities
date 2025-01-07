package com.sap.engine.lib.xml.parser.tokenizer;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.XMLParserConstants;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.util.NS;

public class XMLTokenReaderUtil {
  
  /**
   * Returns dom representation of current element.
   */
  public static Element getDOMRepresentation(XMLTokenReader reader, Element element) throws ParserException {
    if ((reader == null) || (element == null)){
      throw new IllegalArgumentException("Passed argument can no be null.");
    }
        
    if (reader.getState() != XMLTokenReader.STARTELEMENT) {
      return null;
    }
    
    Map<String, String> prefixMappings = reader.getNamespaceMappings();
    
    Set<String> keySet = prefixMappings.keySet();
        
    for (Iterator<String> it = keySet.iterator(); it.hasNext();){    
      String key = it.next();
      
      String namespace = prefixMappings.get(key);
      
      if (namespace.length() != 0) {
        if (key.length() == 0) {
          element.setAttributeNS(NS.XMLNS,"xmlns", namespace);
        } else {
          element.setAttributeNS(NS.XMLNS,"xmlns:"+key, namespace);
        }
      }
    }
    
    return fillElement(reader, element);
  }
  
  
  private static Element fillElement(XMLTokenReader reader, Element element) throws ParserException {
    Attributes attrs = reader.getAttributes();
    int attrsLeght = attrs.getLength();
    String attribUri, attribQName, attribValue;
    for (int i = 0; i < attrsLeght; i++) {
      attribUri = attrs.getURI(i);
      attribQName = attrs.getQName(i);
      attribValue = attrs.getValue(i);
      if (attribQName.equals("xmlns")) {
        attribUri = XMLParserConstants.sXMLNSNamespace;
      }
      element.setAttributeNS(attribUri, attribQName, attribValue);
    }
    
    int code;
    while (true) {
      code = reader.next();
      switch (code) {
        case XMLTokenReader.COMMENT: {
          element.appendChild(element.getOwnerDocument().createComment(reader.getValue()));
          break;
        }
        case XMLTokenReader.CHARS: {
          element.appendChild(element.getOwnerDocument().createTextNode(reader.getValue()));
          break;
        }
        case XMLTokenReader.STARTELEMENT: {
          Element child = element.getOwnerDocument().createElementNS(reader.getURI(), reader.getQName());
          element.appendChild(fillElement(reader, child));
          break;
        }
        case XMLTokenReader.ENDELEMENT: {
          return element;
        }
        case XMLTokenReader.EOF: {
          throw new ParserException("Unexpexted EOF.",0,0);
        }
      }
    }
  }
      
  /**
   * Write the content of the passed reade to the passed writer.
   * @param reader
   * @param writer
   * @throws ParserException
   * @throws IOException
   */
  public static void copyReader2Writer(XMLTokenReader reader, Writer writer) throws ParserException, IOException{
    if (reader.getState() != XMLTokenReader.CHARS && reader.getState() != XMLTokenReader.STARTELEMENT) {
      throw new ParserException("Invalid reader state.", 0, 0);
    }
    
    if (reader.getState() == XMLTokenReader.CHARS) { //read the chars and return
      writeCharsOrComment(reader, writer);
      writer.flush();
    } else { //this is start element
      //map all current prefix into effPrefMapsAttr table
      Map<String,String> effPrefMapsAttr = new HashMap<String,String>(); //key(string) - pref declaration; value(string) pref namespace 
      
      Map<String, String> prefixMappings = reader.getNamespaceMappings();            
      Set<String> nsKeySet = prefixMappings.keySet();
      for (String nsKey : nsKeySet){        
        String namespace = prefixMappings.get(nsKey);
        if (namespace.length() != 0) {
          if (nsKey.length() == 0) {
            effPrefMapsAttr.put("xmlns", namespace);
          } else {
            effPrefMapsAttr.put("xmlns:" + nsKey, namespace);
          }
        }
      }

      //writes first start element
      writer.write('<');
      CharArray charArr = reader.getQNameCharArray();
      writer.write(charArr.getData(), charArr.getOffset(), charArr.getSize());
      writer.write(' ');

      //write the attributes
      Attributes atts = reader.getAttributes();
      if (atts != null){
        for (int i = 0; i < atts.getLength(); i++) {        
          if (! NS.XMLNS.equals(atts.getURI(i)) && ! "xmlns".equals(atts.getLocalName(i))) {//do not write ns declarations, they are written later
            writer.write(atts.getQName(i));
            writer.write("=\'");
            writer.write(atts.getValue(i));
            writer.write("\' ");
          } else { //apply the current element pref declaration over the declaration decared earlier
            effPrefMapsAttr.put(atts.getQName(i), atts.getValue(i)); 
          }
        }
      }
      //write effective prefixes
      
      Set<String> keySet = effPrefMapsAttr.keySet();
      for(String prefD : keySet){
        writer.write(prefD + "='");
        writer.write(effPrefMapsAttr.get(prefD));
        writer.write("' ");            
      }
      
      writer.write('>');
      int level = 0; //counts start and end elements
      int code;       
      while (true) {
        code = reader.next();
        switch (code) {
          case XMLTokenReader.COMMENT: {
            writeCharsOrComment(reader, writer);
            continue;
          }
          case XMLTokenReader.CHARS: {
            writeCharsOrComment(reader, writer);
            continue;
          }
          case XMLTokenReader.STARTELEMENT: {
            writeStartEndElement(reader, writer);
            level++;
            continue;
          }
          case XMLTokenReader.ENDELEMENT: {
            writeStartEndElement(reader, writer);
            if (level == 0) { //this is the last end element
              writer.flush();
              return;
            }
            level--;
            continue;
          }
          case XMLTokenReader.EOF: {
            throw new ParserException("Unexpexted EOF." , 0, 0);
          }
        }
      }
    } 
  }
  
  
  /**
   * Writes char and comment data into the writer.
   * Parser should be in one of the states XMLTokenReader.CHARS or XMLTokenReader.COMMENT
   */
  private static void writeCharsOrComment(XMLTokenReader reader, Writer writer) throws IOException {
    CharArray charArr = reader.getValueCharArray();
    if (reader.getState() == XMLTokenReader.COMMENT) {
      writer.write("<!--");
      writer.write(charArr.getData(), charArr.getOffset(), charArr.getSize());
      writer.write("-->");
    } else { //this is char data
      writer.write(charArr.getData(), charArr.getOffset(), charArr.getSize());      
    }
  }
  
  
  /**
   * Readers start/stop element from this reader and writes it into the writer.
   * 
   * @param writer writer into which the element is writen.
   */
  private static void writeStartEndElement(XMLTokenReader reader, Writer writer) throws IOException {
    CharArray charArr;
    //writer start element
    if (reader.getState() == XMLTokenReader.STARTELEMENT) {
      writer.write('<');
      charArr = reader.getQNameCharArray();
      writer.write(charArr.getData(), charArr.getOffset(), charArr.getSize());
      writer.write(' ');
      Attributes atts = reader.getAttributes();
      for (int i = 0; i < atts.getLength(); i++) {
        writer.write(atts.getQName(i));
        writer.write("=\'");
        writer.write(atts.getValue(i));
        writer.write("\' ");            
      }
      writer.write('>');
    } else { //this is end element
      writer.write("</");
      charArr = reader.getQNameCharArray();
      writer.write(charArr.getData(), charArr.getOffset(), charArr.getSize());
      writer.write('>');
    }
  }
  
  
  
  
}
