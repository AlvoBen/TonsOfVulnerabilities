package com.sap.engine.lib.xml.util.legacy;

import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXSource;

import org.w3c.dom.Document;

/**
 * Utility class responsible for load dom documents with mapped namespaces.
 * Used to replace the functionality of JAXPProperties.PROPERTY_REPLACE_NAMESPACE 
 * previously used in the SAP DOM implementation.
 * 
 * 
 * @author I056242
 *
 */
public class StaxDOMLoader {     
 
  /**
   * 
   * @param input the document as stream
   * @param output An empty w3c dom document instance.
   * @param mappedNS replaces the [0] ns if found in the document with [1] ns.
   * @throws XMLStreamException
   * @throws TransformerException
   */
  public static void load(InputStream input, Document output, String[] mappedNS) throws XMLStreamException, TransformerException{
    if (mappedNS == null || mappedNS.length != 2){
      throw new IllegalArgumentException("Use of the StaxDOMLoader: load a document where you want to replace one ns with other in the dom tree. If you don't want to map " +
      		"namespaces just use the standrt DocumentBuilder.");
    }
            
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
       
    XMLEventReader eventReader = inputFactory.createXMLEventReader(input,"UTF-8");

    //Get all event though the sap event reader.   
    Source source = new StAXSource(new SapEventDelegateReader(eventReader, mappedNS));    
    DOMResult result = new DOMResult(output);
        
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
                         
    transformer.transform(source, result);
  }

}
