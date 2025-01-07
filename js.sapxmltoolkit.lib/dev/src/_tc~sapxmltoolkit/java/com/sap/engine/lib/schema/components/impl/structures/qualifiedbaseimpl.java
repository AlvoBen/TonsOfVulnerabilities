package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.Base;
import com.sap.engine.lib.schema.components.QualifiedBase;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.util.LexicalParser;
import com.sap.engine.lib.schema.exception.SchemaRuntimeException;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.xml.dom.DOM;

import java.util.Hashtable;

public abstract class QualifiedBaseImpl extends BaseImpl implements QualifiedBase {

  protected String name;
  protected boolean isTopLevel;
  protected boolean isQualified;

  public QualifiedBaseImpl() {
    this(null, null, false);
  }

  public QualifiedBaseImpl(boolean isTopLevel) {
    this(null, null, isTopLevel);
  }

  public QualifiedBaseImpl(Node associatedNode, SchemaImpl schema, boolean isTopLevel) {
		super(associatedNode, schema);
    this.isTopLevel = isTopLevel;
    this.isQualified = true;
    if(schema != null) {
      name = loadAttribsCollector.getProperty(NODE_NAME_NAME);
      if(name == null) {
        if(isTopLevel) {
          throw new SchemaRuntimeException("[location : '" + schema.getLocation() + "'] ERROR : Definition of qualified base " + DOM.toXPath(associatedNode) + " is not correct. The value of {name} property must not be absent.");
        }
        name = "";
      } else {
        if(LexicalParser.parseNCName(name) == null) {
          throw new SchemaRuntimeException("[location : '" + schema.getLocation() + "'] ERROR : Definition of qualified base " + DOM.toXPath(associatedNode) + " is not correct. The value of {name} property is not a valid NCName.");
        }
      }
    }
  }

  public final String getName() {
    return(name);
  }

  protected void setName(String name) {
    this.name = name;
  }

  public String getTargetNamespace() {
    return(isQualified ? schema.getTargetNamespace() : "");
  }

  public boolean isAnonymous() {
    return(name.equals(""));
  }

  public boolean match(Base qualifiedBase) {
  	if(!super.match(qualifiedBase)) {
  		return(false);
  	}
  	QualifiedBaseImpl targetQualifiedBase = (QualifiedBaseImpl)qualifiedBase;
  	return(Tools.compareObjects(name, targetQualifiedBase.name) &&
  				 getTargetNamespace().equals(targetQualifiedBase.getTargetNamespace()));
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    QualifiedBaseImpl result = (QualifiedBaseImpl)(super.initializeBase(base, clonedCollector));
    result.name = name;
    result.isTopLevel = isTopLevel;
    result.isQualified = isQualified;
    return(result);
  }

  public boolean isTopLevel() {
    return(isTopLevel);
  }
  
  public String getQualifiedKey() {
  	return(Tools.generateKey(getTargetNamespace(), name));
  }

  public String toString() {
  	return("{type : " + getNameOfComponent() + "; uri : " + getTargetNamespace() + "; name : " + name + "}");
  }
}

