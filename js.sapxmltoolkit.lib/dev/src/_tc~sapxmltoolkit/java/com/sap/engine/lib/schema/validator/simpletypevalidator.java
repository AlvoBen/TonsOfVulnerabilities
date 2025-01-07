package com.sap.engine.lib.schema.validator;

import com.sap.engine.lib.schema.components.SimpleTypeDefinition;
import com.sap.engine.lib.schema.components.FundamentalFacets;
import com.sap.engine.lib.schema.components.Facet;
import com.sap.engine.lib.schema.components.impl.ffacets.ValueComparator;
import com.sap.engine.lib.schema.components.impl.ffacets.Value;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.validator.regexp.RegularExpression;
import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.exception.RegularExpressionException;
import com.sap.engine.lib.xml.dom.NodeImpl;

import java.util.StringTokenizer;
import java.util.Vector;

import org.xml.sax.SAXException;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-2-9
 * Time: 12:03:03
 * To change this template use Options | File Templates.
 */
public final class SimpleTypeValidator implements Constants {

  public static boolean validateSimpleTypeDefinition(SimpleTypeDefinition simpleTypeDef, SchemaDocHandler schemaDocHandler, Value ffacetsValue, String normalizedValue, String nodeRepresent, NodeImpl node) throws SAXException {
    if (simpleTypeDef.isVarietyList()) {
      return(validateListSimpleType(simpleTypeDef, normalizedValue, schemaDocHandler, ffacetsValue, nodeRepresent, node));
    } else if(simpleTypeDef.isVarietyUnion()) {
      return(validateUnionSimpleType(simpleTypeDef, normalizedValue, schemaDocHandler, ffacetsValue, nodeRepresent, node));
    }
    return(validateAtomicSimpleType(simpleTypeDef, normalizedValue, schemaDocHandler, ffacetsValue, nodeRepresent, node));
  }

  private static ReusableObjectsPool determineReusableObjectsPool(SchemaDocHandler schemaDocHandler) {
    return(schemaDocHandler == null ? null : schemaDocHandler.getReusableObjectsPool());
  }
  
  private static void collectError(SchemaDocHandler schemaDocHandler, String nodeRepresent, String errorMessage) throws SAXException {
    if(schemaDocHandler != null) {
      schemaDocHandler.collectError(nodeRepresent, errorMessage);
    }
  }
  
  private static String determineNormalizedValue(ReusableObjectsPool pool, String value, String whiteSpaceNormalizationValue) {
    return(pool == null ? Tools.normalizeValue(value, whiteSpaceNormalizationValue) : pool.getNormalizedString(value, whiteSpaceNormalizationValue));
  }
  
  private static boolean validateAtomicSimpleType(SimpleTypeDefinition simpleTypeDef, String normalizedValue, SchemaDocHandler schemaDocHandler, Value ffacetsValue, String nodeRepresent, NodeImpl node) throws SAXException {
    if (ffacetsValue == null) {
      collectError(schemaDocHandler, nodeRepresent, "Node is declared to be with simple type. It's value '" + normalizedValue + "' does not belong to the lexical space of it's type definition.");
      return(false);
    }
    return(validateFacets(simpleTypeDef, normalizedValue, schemaDocHandler, ffacetsValue, nodeRepresent));
  }
  
  private static boolean validateFacets(SimpleTypeDefinition simpleTypeDef, String normalizedValue, SchemaDocHandler schemaDocHandler, Value ffacetsValue, String nodeRepresent) throws SAXException {
    ReusableObjectsPool pool = determineReusableObjectsPool(schemaDocHandler);
    FundamentalFacets fundamentalFacets = simpleTypeDef.getFundamentalFacets();
    Vector facets = simpleTypeDef.getFacets();
    boolean patternPresent = false;
    boolean patternValidation = false;
    boolean enumerationPresent = false;
    boolean enumerationValidation = false;
    
    for (int i = 0; i < facets.size(); i++) {
      Facet facet = (Facet)(facets.get(i));
      String facetValue = facet.getValue();
      String facetName = facet.getName();
      if (facetName.equals("pattern")) {
        if(!patternValidation) {
          if(!patternPresent) {
            patternPresent = true;
          }
          if(validateFacetPattern(facet.getRegularExpression(), normalizedValue)) {
            patternValidation = true;
          }
        }
      } else if(facetName.equals("enumeration")) {
        if(!enumerationValidation) {
          if (!enumerationPresent) {
            enumerationPresent = true;
          }
          if(validateFacetEnumeration(determineReusableObjectsPool(schemaDocHandler), fundamentalFacets, ffacetsValue, facetValue)) {
            enumerationValidation = true;
          }
        }
      } else {
        boolean result = true;
        if(facetName.equals("length")) {
          result = validateFacetLength(ffacetsValue, normalizedValue, facetValue); 
        } else if(facetName.equals("minLength")) {
          result = validateFacetMinLength(ffacetsValue, normalizedValue, facetValue);
        } else if(facetName.equals("maxLength")) {
          result = validateFacetMaxLength(ffacetsValue, normalizedValue, facetValue);
        } else if(facetName.equals("minExclusive")) {
          result = validateFacetMinExclusive(pool, fundamentalFacets, ffacetsValue, facetValue);
        } else if(facetName.equals("maxExclusive")) {
          result = validateFacetMaxExclusive(pool, fundamentalFacets, ffacetsValue, facetValue);
        } else if(facetName.equals("minInclusive")) {
          result = validateFacetMinInclusive(pool, fundamentalFacets, ffacetsValue, facetValue);
        } else if(facetName.equals("maxInclusive")) {
          result = validateFacetMaxInclusive(pool, fundamentalFacets, ffacetsValue, facetValue);
        } else if(facetName.equals("totalDigits")) {
          result = validateFacetTotalDigits(facetValue, normalizedValue);
        } else if(facetName.equals("fractionDigits")) {
          result = validateFacetFractionDigits(facetValue, normalizedValue);
        } else if(facetName.equals("whitespace")) {
          result = validateFacetWhitespace();
        }
        if(!result) {
          collectError(schemaDocHandler, nodeRepresent, "Node is declared to be with simple type. It's value '" + normalizedValue + "' does not satisfy the constraints of the facet '" + facetName + "' with value '" + facetValue + "'.");
          return(false);
        }
      }
    }

    if(enumerationPresent && !enumerationValidation) {
      collectError(schemaDocHandler, nodeRepresent, "Node is declared to be with simple type. It's value '" + normalizedValue + "' does not match to any of the enumeration facets.");
      return(false);
    }
    if(patternPresent && !patternValidation) {
      collectError(schemaDocHandler, nodeRepresent, "Node is declared to be with simple type. It's value '" + normalizedValue + "' does not match to any of the pattern facets.");
      return(false);
    }
    return(true);
  }

  private static boolean validateUnionSimpleType(SimpleTypeDefinition simpleTypeDef, String value, SchemaDocHandler schemaDocHandler, Value fFacetsValue, String nodeRepresent, NodeImpl node) throws SAXException {
    if(!validateFacets(simpleTypeDef, value, schemaDocHandler, fFacetsValue, nodeRepresent)) {
      return(false);
    }
    ReusableObjectsPool pool = determineReusableObjectsPool(schemaDocHandler);
    Vector unionMemeberTypeDefs = simpleTypeDef.getMemberTypeDefinitions();
    for (int i = 0; i < unionMemeberTypeDefs.size(); i++) {
      SimpleTypeDefinition unionMemeberTypeDef = (SimpleTypeDefinition)(unionMemeberTypeDefs.get(i));
      if(validateUnionMemeberValue(pool, unionMemeberTypeDef, value, nodeRepresent, node)) {
        return(true);
      }
    }
    collectError(schemaDocHandler, nodeRepresent, "Node is declared to be with simple type with variety 'union'. It's value '" + value + "' is not valid according to any of the union members simple type definition.");
    return(false);
  }
  
  private static boolean validateUnionMemeberValue(ReusableObjectsPool pool, SimpleTypeDefinition unionMemeberTypeDef, String value, String nodeRepresent, NodeImpl node) throws SAXException {
    Value fFacetsValue = null;
    try {
      String normalizedValue = determineNormalizedValue(pool, value, unionMemeberTypeDef.getWhiteSpaceNormalizationValue());
      fFacetsValue = unionMemeberTypeDef.getFundamentalFacets().parse(normalizedValue, pool);
      if(validateSimpleTypeDefinition(unionMemeberTypeDef, null, fFacetsValue, normalizedValue, nodeRepresent, node)) {
        if(node != null) {
          node.setAugmentation(AUG_MEMBER_TYPE_DEFINITION, unionMemeberTypeDef);
        }
        return(true);
      }
    } finally {
      reuseFFacetsValue(pool, fFacetsValue);
    }
    return(false);
  }
  
  private static boolean validateListSimpleType(SimpleTypeDefinition simpleTypeDef, String normalizedValue, SchemaDocHandler schemaDocHandler, Value fFacetsValue, String nodeRepresent, NodeImpl node) throws SAXException {
    ReusableObjectsPool pool = determineReusableObjectsPool(schemaDocHandler);
    SimpleTypeDefinition listMememberTypeDef = simpleTypeDef.getItemTypeDefinition();
    if(normalizedValue.trim().equals("")) {
      return(validateListMemeberValue(pool, schemaDocHandler, listMememberTypeDef, normalizedValue, nodeRepresent, node));
    }
    StringTokenizer listMemebersTokenizer = new StringTokenizer(normalizedValue, " ");
    while (listMemebersTokenizer.hasMoreElements()) {
      String listMemeberValue = listMemebersTokenizer.nextToken();
      if(!validateListMemeberValue(pool, schemaDocHandler, listMememberTypeDef, listMemeberValue, nodeRepresent, node)) {
        return(false);
      }
    }
    return(validateFacets(simpleTypeDef, normalizedValue, schemaDocHandler, fFacetsValue, nodeRepresent));
  }
  
  private static boolean validateListMemeberValue(ReusableObjectsPool pool, SchemaDocHandler schemaDocHandler, SimpleTypeDefinition listMememberTypeDef, String listMemberValue, String nodeRepresent, NodeImpl node) throws SAXException {
    Value listMemeberFFacetsValue = null; 
    try {
      String listMemeberNormalizedValue = determineNormalizedValue(pool, listMemberValue, listMememberTypeDef.getWhiteSpaceNormalizationValue());
      listMemeberFFacetsValue = listMememberTypeDef.getFundamentalFacets().parse(listMemeberNormalizedValue, pool);
      if(!validateSimpleTypeDefinition(listMememberTypeDef, null, listMemeberFFacetsValue, listMemeberNormalizedValue, nodeRepresent, node)) {
        collectError(schemaDocHandler, nodeRepresent, "Node is declared to be with simple type with variety 'list'. The value '" + listMemberValue + "' is not valid according to the item type definition.");
        return(false);
      }
    } finally {
      reuseFFacetsValue(pool, listMemeberFFacetsValue);
    }
    return(true);
  }
  
  private static void reuseFFacetsValue(ReusableObjectsPool reusableObjectsPool, Value fFacetsValue) {
    if(reusableObjectsPool != null) {
      reusableObjectsPool.reuseFFacetValue(fFacetsValue);
    }
  }

  public static boolean validateFacetPattern(RegularExpression regularExpression, String value) throws RegularExpressionException {
  	return(regularExpression == null || regularExpression.matches(value));
  }

  public static boolean validateFacetEnumeration(ReusableObjectsPool pool, FundamentalFacets fFacets, Value fFacetsValue, String enumValue) {
    Value enumFFacetValue = fFacets.parse(enumValue, pool);
    boolean result = ValueComparator.compare(fFacetsValue, enumFFacetValue) == COMPARE_RESULT_EQUAL;
    reuseFFacetsValue(pool, enumFFacetValue);
  	return(result);
  }

  public static boolean validateFacetLength(Value ffactesValue, String value, String lengthValue) {
  	return(ffactesValue.getLength() == Integer.parseInt(lengthValue));
  }

  public static boolean validateFacetMinLength(Value ffactesValue, String value, String minLengthValue) {
	  return(ffactesValue.getLength() >= Integer.parseInt(minLengthValue));
  }

  public static boolean validateFacetMaxLength(Value ffactesValue, String value, String maxLengthValue) {
	  return(ffactesValue.getLength() <= Integer.parseInt(maxLengthValue));
  }

  public static boolean validateFacetMinExclusive(ReusableObjectsPool pool, FundamentalFacets fFacets, Value fFacetsValue, String minExclValue) {
    Value minExclFFacetsValue = fFacets.parse(minExclValue, pool);
    boolean result = ValueComparator.compare(fFacetsValue, minExclFFacetsValue) == COMPARE_RESULT_GREATER;
    reuseFFacetsValue(pool, minExclFFacetsValue);
    return(result);
  }

  public static boolean validateFacetMaxExclusive(ReusableObjectsPool pool, FundamentalFacets fFacets, Value fFacetsValue, String maxExclValue) {
    Value maxExclFFacetsValue = fFacets.parse(maxExclValue, pool);
    boolean result = ValueComparator.compare(fFacetsValue, maxExclFFacetsValue) == COMPARE_RESULT_LESS;
    reuseFFacetsValue(pool, maxExclFFacetsValue);
    return(result);
  }

  public static boolean validateFacetMinInclusive(ReusableObjectsPool pool, FundamentalFacets fFacets, Value fFacetsValue, String minInclValue) {
    Value minInclFFacetsValue = fFacets.parse(minInclValue, pool);
    boolean result = ValueComparator.compare(fFacetsValue, minInclFFacetsValue) != COMPARE_RESULT_LESS;
    reuseFFacetsValue(pool, minInclFFacetsValue);
    return(result);
  }

  public static boolean validateFacetMaxInclusive(ReusableObjectsPool pool, FundamentalFacets fFacets, Value fFacetsValue, String maxInclValue) {
    Value maxInclFFacetsValue = fFacets.parse(maxInclValue, pool);
    boolean result = ValueComparator.compare(fFacetsValue, maxInclFFacetsValue) != COMPARE_RESULT_GREATER;
    reuseFFacetsValue(pool, maxInclFFacetsValue);
    return(result);
  }

  public static boolean validateFacetTotalDigits(String totalDigitsValue, String value) {
	  int facetRestrictionTotalDigitsCount = Integer.parseInt(totalDigitsValue);
    int delimiterIndex = value.indexOf(".");
    int integerDigitsCount = getIntegerDigitsCount(delimiterIndex, value);
    int fractionDigitsCount = getFractionDigitsCount(delimiterIndex, value);
    return(integerDigitsCount + fractionDigitsCount <= facetRestrictionTotalDigitsCount);
  }
  
  private static int getIntegerDigitsCount(int fractionDelimiterIndex, String value) {
    int integerDigitsCount = 0;
    int length = fractionDelimiterIndex < 0 ? value.length() : fractionDelimiterIndex;
    boolean preceedingZeros = true;
    for(int i = 0; i < length; i++) {
      char ch = value.charAt(i);
      if(preceedingZeros) {
        if(ch >= '1' && ch <= '9') {
          integerDigitsCount++;
          preceedingZeros = false;
        }
      } else if(ch >= '0' && ch <= '9') {
        integerDigitsCount++;
      }
    }
    return(integerDigitsCount == 0 ? 1 : integerDigitsCount);
  }
  
  private static int getFractionDigitsCount(int fractionDelimiterIndex, String value) {
    int fractionDigitsCount = 0;
    boolean trailingZeros = true;
    if(fractionDelimiterIndex >= 0) {
      for(int i = value.length() - 1; i > fractionDelimiterIndex; i--) {
        char ch = value.charAt(i);
        if(trailingZeros) {
          if(ch >= '1' && ch <= '9') {
            fractionDigitsCount++;
            trailingZeros = false;
          }
        } else if(ch >= '0' && ch <= '9') {
          fractionDigitsCount++;
        }
      }
    }
    return(fractionDigitsCount);
  }

  public static boolean validateFacetFractionDigits(String fractionDigitsValue, String value) {
    int facetRestrictionFractionDigitsCount = Integer.parseInt(fractionDigitsValue);
    int delimiterIndex = value.indexOf(".");
    int fractionDigitsCount = getFractionDigitsCount(delimiterIndex, value);
  	return(fractionDigitsCount <= facetRestrictionFractionDigitsCount);
  }

  public static boolean validateFacetWhitespace() {
  	return(true);
  }
}
