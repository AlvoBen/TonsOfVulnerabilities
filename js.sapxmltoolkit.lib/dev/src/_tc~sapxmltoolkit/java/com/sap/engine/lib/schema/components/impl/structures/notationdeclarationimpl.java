package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.NotationDeclaration;
import com.sap.engine.lib.schema.components.Base;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.exception.SchemaComponentException;

import java.util.Hashtable;

public final class NotationDeclarationImpl extends QualifiedBaseImpl implements NotationDeclaration {

  protected String systemIdentifier;
  protected String publicIdentifier;

  public NotationDeclarationImpl() {
    this(null, null);
  }

  public NotationDeclarationImpl(Node associatedNode, SchemaImpl schema) {
  	super(associatedNode, schema, true);
  }

  public String getSystemIdentifier() {
    return systemIdentifier;
  }

  public String getPublicIdentifier() {
    return publicIdentifier;
  }

  public int getTypeOfComponent() {
    return C_NOTATION_DECLARATION;
  }

  public boolean match(Base notationDeclr) {
  	if(!super.match(notationDeclr)) {
  		return(false);
  	}
  	NotationDeclarationImpl targetNotaionDeclr = (NotationDeclarationImpl)notationDeclr;
  	return(Tools.compareObjects(systemIdentifier, targetNotaionDeclr.systemIdentifier) &&
  					Tools.compareObjects(publicIdentifier, targetNotaionDeclr.publicIdentifier));
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
      publicIdentifier = loadAttribsCollector.getProperty(NODE_PUBLIC_NAME);
      systemIdentifier = loadAttribsCollector.getProperty(NODE_SYSTEM_NAME);
      NodeList nodeList = associatedNode.getChildNodes();
      for(int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if(node instanceof Element) {
          String nodeUri = node.getNamespaceURI();
          if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
            String nodeLocalName = node.getLocalName();
            if(nodeLocalName.equals(NODE_ANNOTATION_NAME)) {
              annotation = SchemaStructuresLoader.createAnnotation(node, schema);
							SchemaStructuresLoader.loadBase(annotation);
            }
          }
        }
      }
    }
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    NotationDeclarationImpl result = (NotationDeclarationImpl)(super.initializeBase(base, clonedCollector));
    result.systemIdentifier = systemIdentifier;
    result.publicIdentifier = publicIdentifier;
    return(result);
  }
}

