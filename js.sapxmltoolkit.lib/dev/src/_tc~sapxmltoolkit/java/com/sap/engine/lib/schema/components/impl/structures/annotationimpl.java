package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.Annotation;
import com.sap.engine.lib.schema.components.Base;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.xml.dom.DOM;

import java.util.Vector;
import java.util.Hashtable;

public final class AnnotationImpl extends BaseImpl implements Annotation {
	
	protected Vector attribs;
  protected Vector appInfos;
  protected Vector usrInfos;

  public AnnotationImpl() {
    this(null, null);
  }

	public AnnotationImpl(Node associatedNode, SchemaImpl schema) {
		super(associatedNode, schema);
    attribs = new Vector();
    appInfos = new Vector();
    usrInfos = new Vector();
	}
	
  public int getTypeOfComponent() {
    return(C_ANNOTATION);
  }

  public Vector getAttributes() {
    return(attribs);
  }

  public Vector getAppInformations() {
    return(appInfos);
  }

  public Vector getUserInformations() {
    return(usrInfos);
  }
  
  public void getAttributes(Vector collector) {
    Tools.removeFromVectorToVector(attribs, collector);
  }

  public void getAppInformations(Vector collector) {
    Tools.removeFromVectorToVector(appInfos, collector);
  }

  public void getUserInformations(Vector collector) {
    Tools.removeFromVectorToVector(usrInfos, collector);
  }

  public Node[] getAttributesArray() {
    return(createArray(attribs));
  }

  public Node[] getAppInformationsArray() {
    return(createArray(appInfos));
  }

  public Node[] getUserInformationsArray() {
    return(createArray(usrInfos));
  }

  private Node[] createArray(Vector collector) {
    Node[] result = new Node[collector.size()];
    collector.copyInto(result);
    return(result);
  }

  public boolean match(Base annotation) {
  	if(!super.match(annotation)) {
  		return(false);
  	}
  	AnnotationImpl annotImpl = (AnnotationImpl)annotation;
  	Vector attribsOfTheTargetAnnot = annotImpl.attribs;
  	if(attribs.size() != attribsOfTheTargetAnnot.size()) {
  		return(false);
  	}
  	boolean[] checked = new boolean[attribsOfTheTargetAnnot.size()];
  	for(int i = 0; i < attribs.size(); i++) {
  		Attr attr = (Attr)(attribs.get(i));
  		boolean found = false;
  		for(int j = 0; j < attribsOfTheTargetAnnot.size(); j++) {
  			if(!checked[j]) {
  				Attr targetAttr = (Attr)(attribsOfTheTargetAnnot.get(i));
  				if(DOM.areEquivalent(attr, targetAttr)) {
  					checked[j] = true;
  					found = true;
  					break;
  				}
  			}
  		}
  		if(!found) {
  			return(false);
  		}
  	}
  	return(true); 
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
      NamedNodeMap namedNodeMap = associatedNode.getAttributes();
      for(int i = 0; i < namedNodeMap.getLength(); i++) {
        Attr attr = (Attr)(namedNodeMap.item(i));
        String attribUri = attr.getNamespaceURI();
        if(!((attribUri == null || attribUri.equals("")) && attr.getLocalName().equals(NODE_ID_NAME))) {
          attribs.add(attr);
        }
      }
      NodeList nodeList = associatedNode.getChildNodes();
      for(int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if(node instanceof Element) {
          String nodeUri = node.getNamespaceURI();
          if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
            String nodeLocalName = node.getLocalName();
            if(nodeLocalName.equals(NODE_APPINFO_NAME)) {
              appInfos.add(node);
            } else if(nodeLocalName.equals(NODE_DOCUMENTATION_NAME)) {
              usrInfos.add(node);
            }
          }
        }
      }
    }
  }

  public BaseImpl clone(Hashtable typesCollector) {
    return(this);
  }
}

