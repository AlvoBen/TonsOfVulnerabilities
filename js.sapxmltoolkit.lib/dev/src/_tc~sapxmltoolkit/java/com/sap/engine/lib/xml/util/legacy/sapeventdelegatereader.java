package com.sap.engine.lib.xml.util.legacy;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;

/**
 * Overloading the default EventReader wrapper.
 * @author I056242
 *
 */
class SapEventDelegateReader extends EventReaderDelegate {
  private String[] mappedNS;
  
  static XMLEventFactory eventFactory = XMLEventFactory.newInstance();
  
  static int OLD_NS = 0;
  
  static int NEW_NS = 1;
  
  private static String DEFAULT_XML_VERSION = "1.0";
       
  public SapEventDelegateReader(XMLEventReader eventReader, String[] mappedNS){   
    super(eventReader);        
    
    this.mappedNS = mappedNS;
  }
  
  public XMLEvent nextEvent() throws XMLStreamException {
    XMLEvent event = super.nextEvent();

    if (event.isStartElement()) {
      StartElement startElementEvent = event.asStartElement();

      // Check if the start el is not in the correct ns.
      String ns = startElementEvent.getName().getNamespaceURI();
      if (mappedNS[OLD_NS].equals(ns)) {
        event = new SapStartElementEvent(startElementEvent, mappedNS);
      }
    }

    // Check if the end el is not in the correct ns.
    if (event.isEndElement()) {
      EndElement endElementEvent = event.asEndElement();

      String ns = endElementEvent.getName().getNamespaceURI();
      if (mappedNS[OLD_NS].equals(ns)) {
        event = new SapEndElementEvent(endElementEvent, mappedNS);
      }
    }
    
    if (event.isStartDocument()){
      StartDocument startDocumentEvent = (StartDocument)event;
      
      
      if (startDocumentEvent.getVersion() == null){        
        event = eventFactory.createStartDocument(startDocumentEvent.getCharacterEncodingScheme(),DEFAULT_XML_VERSION); 
      }
    }

    return event;
  }

}
