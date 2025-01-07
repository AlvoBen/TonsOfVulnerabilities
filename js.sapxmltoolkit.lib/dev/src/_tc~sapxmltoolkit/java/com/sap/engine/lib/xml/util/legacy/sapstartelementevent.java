package com.sap.engine.lib.xml.util.legacy;


import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;

/**
 * Wrapper of the StartElement event.
 * Rewrites all ns related data.
 * @author I056242
 *
 */
class SapStartElementEvent implements StartElement{   
  private StartElement wrappedEvent;
  
  private String[] mappedNS;
  
  
  public SapStartElementEvent(StartElement nsEvent, String[] mappedNS){
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
    return wrappedEvent.asEndElement();
  }

  
  public StartElement asStartElement() {
    return this;
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

  
  public Attribute getAttributeByName(QName name) {
    return wrappedEvent.getAttributeByName(name);
  }

  
  public Iterator getAttributes() {   
    return wrappedEvent.getAttributes();
  }

  
  public NamespaceContext getNamespaceContext() {
    //TODO: check what does the context carry - overwrite it if needed.
    return wrappedEvent.getNamespaceContext();
  }

  
  public String getNamespaceURI(String prefix) {
    String ns = wrappedEvent.getNamespaceURI(prefix);

    if (mappedNS[SapEventDelegateReader.OLD_NS].equals(ns)) {
      ns = mappedNS[SapEventDelegateReader.NEW_NS];
    }

    return ns;
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

