/*
 * Created on 2004-10-27
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.validator.automat;

import com.sap.engine.lib.schema.components.ElementDeclaration;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class ElementDeclarationWrapper {
	
  protected ElementDeclaration elementDeclaration;
  protected boolean isSubstitution;
  
  protected void initToStringBuffer(StringBuffer toStringBuffer) {
    toStringBuffer.append("(");
    initQNameBuffer(toStringBuffer);
    toStringBuffer.append("; isSubstitution: ");
    toStringBuffer.append(isSubstitution);
    toStringBuffer.append(")");
  }
  
  protected void initQNameBuffer(StringBuffer qNameBuffer) {
    qNameBuffer.append(elementDeclaration.getQualifiedKey());
  }
}
