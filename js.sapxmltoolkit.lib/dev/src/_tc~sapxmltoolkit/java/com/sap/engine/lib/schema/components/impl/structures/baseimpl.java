package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;
import java.util.*;

import com.sap.engine.lib.schema.components.Schema;
import com.sap.engine.lib.schema.components.Annotation;
import com.sap.engine.lib.schema.components.Base;
import com.sap.engine.lib.schema.exception.SchemaComponentException;

public abstract class BaseImpl implements Base {

  protected boolean isBuiltIn;
  protected Node associatedNode;
  protected AnnotationImpl annotation;
  protected String id;
  protected boolean isLoaded;
  protected boolean isLoading;
  protected SchemaImpl schema;
  protected Properties loadAttribsCollector;

  public BaseImpl() {
    this(null, null);
  }

  public BaseImpl(Node associatedNode, SchemaImpl schema) {
  	this.associatedNode = associatedNode;
  	this.schema = schema;
    loadAttribsCollector = new Properties();
    if(associatedNode != null) {
      NamedNodeMap namedNodeMap = associatedNode.getAttributes();
      for(int i = 0; i < namedNodeMap.getLength(); i++) {
        Attr attr = (Attr)(namedNodeMap.item(i));
        String attribUri = attr.getNamespaceURI();
        if(attribUri == null || attribUri.equals("")) {
          loadAttribsCollector.put(attr.getLocalName(), attr.getValue());
        }
      }
    } else {
      isLoaded = true;
    }
  }

  public final String getNameOfComponent() {
    return(NAMES_OF_COMPONENTS[getTypeOfComponent()]);
  }

  public final Schema getOwnerSchema() {
    return(schema);
  }

  public final Node getAssociatedDOMNode() {
    return(associatedNode);
  }

  public void setAssociateNode(Node associatedNode) {
    this.associatedNode = associatedNode;
  }

  public final boolean isBuiltIn() {
    return(isBuiltIn);
  }

  public final Annotation getAnnotation() {
    return(annotation);
  }

  public String getId() {
    return(id);
  }

  public boolean match(Base base) {
  	BaseImpl targetBase = (BaseImpl)base;
  	if(annotation == null ^ targetBase.annotation == null) {
  		return(false);
  	}
  	
  	if(annotation != null) {
  		return(annotation.match(targetBase.annotation));
  	}
  	return(true);
  }

  public boolean isLoading() {
    return(isLoading);
  }

  public void setLoading(boolean isLoading) {
    this.isLoading = isLoading;
  }

  public boolean isLoaded() {
    return(isLoaded);
  }

  public void setLoaded(boolean isLoaded) {
    this.isLoaded = isLoaded;
  }

  public void destroy() {
    loadAttribsCollector = null;
  }

  public void load() throws SchemaComponentException {
  }

  public BaseImpl clone(Hashtable clonedCollector) {
    BaseImpl result = clonedCollector == null ? null : (BaseImpl)(clonedCollector.get(this));
    if(result == null) {
      try {
        result = initializeBase((BaseImpl)(getClass().newInstance()), clonedCollector);
        clonedCollector.put(this, result);
      } catch(Exception exc) {
        //$JL-EXC$

        exc.printStackTrace();
      }
    }
    return(result);
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    base.isBuiltIn = isBuiltIn;
    base.schema = schema;
    base.associatedNode = associatedNode;
    base.annotation = annotation;
    base.id = id;
    base.isLoaded = isLoaded;
    base.isLoading = isLoading;
    return(base);
  }
}

