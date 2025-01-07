package com.sap.engine.lib.xml.util.legacy;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;

/**
 * Wrapper of the EndElement event.
 * Rewrites all ns related data.
 * @author I056242
 *
 */
class SapEndElementEvent implements EndElement{

  private EndElement wrappedEvent;
  
  private String[] mappedNS;
  
  public SapEndElementEvent(EndElement nsEvent, String[] mappedNS) {
    this.wrappedEvent = nsEvent;
    this.mappedNS = mappedNS;
  }

  
  public QName getName() {
    QName qname = wrappedEvent.getName();

    String ns = qname.getNamespaceURI();
    if (mappedNS[SapEventDelegateReader.OLD_NS].equals(ns)){
      String localPart = qname.getLocalPart();

      qname = new QName(mappedNS[SapEventDelegateReader.NEW_NS], localPart);
    }

    return qname;
  }

  
  public Characters asCharacters() {
    return wrappedEvent.asCharacters();
  }

  
  public EndElement asEndElement() {
    return this;
  }

  
  public StartElement asStartElement() {
    return wrappedEvent.asStartElement();
  }

  
  public int getEventType() {
    return wrappedEvent.getEventType();
  }

  
  public Location getLocation() {
    return wrappedEvent.getLocation();
  }

  
  public QName getSchemaType() {
    return wrappedEvent.getSchemaType();
  }

  
  public boolean isAttribute() {
    return wrappedEvent.isAttribute();
  }

  
  public boolean isCharacters() {
    return wrappedEvent.isCharacters();
  }

  
  public boolean isEndDocument() {
    return wrappedEvent.isEndDocument();
  }

  
  public boolean isEndElement() {
    return wrappedEvent.isEndElement();
  }

  
  public boolean isEntityReference() {
    return wrappedEvent.isEntityReference();
  }

  
  public boolean isNamespace() {
    return wrappedEvent.isNamespace();
  }

  
  public boolean isProcessingInstruction() {
    return wrappedEvent.isProcessingInstruction();
  }

  
  public boolean isStartDocument() {
    return wrappedEvent.isStartDocument();
  }

  
  public boolean isStartElement() {
    return wrappedEvent.isStartElement();
  }

  
  public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
    wrappedEvent.writeAsEncodedUnicode(writer);
  }
 

  
  public Iterator getNamespaces() {
    
    List nsList = new ArrayList();
    
    for (Iterator it = wrappedEvent.getNamespaces(); it.hasNext();){
      Namespace namespace = (Namespace) it.next();
      
      String nsValue = namespace.getValue();
      
      if (mappedNS[SapEventDelegateReader.OLD_NS].equals(nsValue)){
         String nsPrefix = namespace.getPrefix();
         namespace = SapEventDelegateReader.eventFactory.createNamespace(nsPrefix, mappedNS[SapEventDelegateReader.NEW_NS]);
      }
            
      nsList.add(namespace);            
    }
    
    return nsList.iterator();
  }
  
  
  
}
