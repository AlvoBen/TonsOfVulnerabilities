package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.Facet;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.schema.validator.regexp.RegularExpression;
import com.sap.engine.lib.xml.dom.DOM;

import java.util.Hashtable;

public final class FacetImpl extends BaseImpl implements Facet {

  protected String facetName;
  protected String valueString;
  protected boolean isFixed;
  private RegularExpression regularExpression;

  public FacetImpl(String facetName, String valueString) {
    super(null, null);
    this.facetName = facetName;
    this.valueString = valueString;
  }

	public FacetImpl(Node associatedNode, SchemaImpl schema) {
		super(associatedNode, schema);
	}

  public int getTypeOfComponent() {
    return(C_FACET);
  }

  public String getName() {
    return facetName;
  }

  public String getValue() {
    return valueString;
  }

  public boolean isFixed() {
    return isFixed;
  }
  
  public RegularExpression getRegularExpression() {
  	return(regularExpression);
  }

  public boolean match(Facet facet) {
  	FacetImpl targetFacet = (FacetImpl)facet;
  	return(facetName.equals(targetFacet.facetName) &&
  					valueString.equals(targetFacet.valueString) &&
  					isFixed == targetFacet.isFixed);
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      facetName = associatedNode.getLocalName();
      String value = loadAttribsCollector.getProperty(NODE_FIXED_NAME);
      if(value != null) {
        isFixed = value.equals(VALUE_TRUE_NAME);
      }
      valueString = loadAttribsCollector.getProperty(NODE_VALUE_NAME);
      if((facetName.equals(FACET_LENGTH_NAME) ||
          facetName.equals(FACET_MIN_LENGTH_NAME) ||
          facetName.equals(FACET_MAX_LENGTH_NAME) ||
          facetName.equals(FACET_FRACTION_DIGITS_NAME)) &&
         !isValueNonNeagtiveInteger()) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of facet " + DOM.toXPath(associatedNode) + " is not correct. Number format error occured or the value is not a non negative integer.");
      } else if(facetName.equals(FACET_TOTAL_DIGITS_NAME) && !isValuePositiveInteger()) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of facet " + DOM.toXPath(associatedNode) + " is not correct. Number format error occured or the value is not a positive integer.");
      } else if(facetName.equals(FACET_PATTERN_NAME) && schema.getLoader().getLoadPatternRegularExpressions()) {
      	try {
	      	regularExpression = new RegularExpression(valueString);
      	} catch(Exception exc) {
					throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of facet " + DOM.toXPath(associatedNode) + " is not correct.", exc);
      	}
      }
    }
  }

  private boolean isValueNonNeagtiveInteger() {
    int intValue = -1;
    try {
      intValue = Integer.parseInt(valueString);
    } catch(NumberFormatException numberFormExc) {
      //$JL-EXC$

      return(false);
    }
    return(intValue >= 0);
  }

  private boolean isValuePositiveInteger() {
    int intValue = -1;
    try {
      intValue = Integer.parseInt(valueString);
    } catch(NumberFormatException numberFormExc) {
      //$JL-EXC$

      return(false);
    }
    return(intValue > 0);
  }

  public String toString() {
    return(facetName + " : " + valueString);
  }

  public BaseImpl clone(Hashtable clonedCollector) {
    return(this);
  }
}

