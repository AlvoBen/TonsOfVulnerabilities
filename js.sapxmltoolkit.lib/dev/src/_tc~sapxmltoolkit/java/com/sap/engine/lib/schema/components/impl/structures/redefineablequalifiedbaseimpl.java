/*
 * Created on 2005-5-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.Node;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class RedefineableQualifiedBaseImpl extends QualifiedBaseImpl {
  
  protected boolean isRedefined;
  
  public RedefineableQualifiedBaseImpl() {
    this(null, null, false, false);
  }

  public RedefineableQualifiedBaseImpl(boolean isTopLevel) {
    this(null, null, isTopLevel, false);
  }

  public RedefineableQualifiedBaseImpl(Node associatedNode, SchemaImpl schema, boolean isTopLevel, boolean isRedefined) {
    super(associatedNode, schema, isTopLevel);
    this.isRedefined = isRedefined;
  }
  
  public boolean isRedefined() {
    return(isRedefined);
  }
}
