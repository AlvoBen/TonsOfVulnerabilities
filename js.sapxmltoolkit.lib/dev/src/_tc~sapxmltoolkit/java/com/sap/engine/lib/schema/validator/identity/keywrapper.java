/*
 * Created on 2004-10-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.validator.identity;

import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.components.impl.ffacets.Value;
import com.sap.engine.lib.schema.components.impl.ffacets.ValueComparator;
import com.sap.engine.lib.schema.validator.SchemaDocHandler;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class KeyWrapper implements Constants {
	
	private Value fFacetsValue;
	private String value;
	private String nodeRepresent;
  private SchemaDocHandler schemaDocHandler;

  protected void setSchemaDocHandler(SchemaDocHandler schemaDocHandler) {
    this.schemaDocHandler = schemaDocHandler;
  }
  
  protected void setFFacetsValue(Value fFacetsValue) {
    this.fFacetsValue = fFacetsValue;
    if(fFacetsValue != null) {
      fFacetsValue.use();
    }
  }
  
  public Value getFFacetsValue() {
    return(fFacetsValue);
  }
  
  protected void setValue(String value) {
    this.value = value;
  }
  
  protected void setNodeRepresentation(String nodeRepresent) {
    this.nodeRepresent = nodeRepresent;
  }

	public boolean match(KeyWrapper valueStructure) {
		if(fFacetsValue == null) {
			return(value.equals(valueStructure.value));
		}
		return(ValueComparator.compare(fFacetsValue, valueStructure.fFacetsValue) == COMPARE_RESULT_EQUAL);
	}
  
  protected String getRepresentation() {
    StringBuffer buffer = schemaDocHandler.getReusableObjectsPool().getStringBuffer();
    buffer.append("(");
    buffer.append(nodeRepresent);
    buffer.append("; value: ");
    buffer.append(value);
    buffer.append(")");
    String represent = buffer.toString();
    schemaDocHandler.getReusableObjectsPool().reuseStringBuffer(buffer);
    return(represent);
  }
}
