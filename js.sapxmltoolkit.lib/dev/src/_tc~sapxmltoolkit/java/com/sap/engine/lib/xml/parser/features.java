package com.sap.engine.lib.xml.parser;

import org.xml.sax.SAXNotSupportedException;

import java.util.HashSet;


/**
 * Title:        xml2000
 * Description:  Encapsulates constants :
 *                  - names of prefixes for features
 *                  - some feature names
 *                  - a hashtable with recognized features
 *                  - a hashtable with supported features
 *               Cannot be instantiated.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      May 2001
 */

public final class Features {

  public static final String PREFIX_SAX = "http://xml.org/sax/features/";
  public static final String PREFIX_VALIDATION = "http://inqmy.com/dtd-validation/";
  public static final String PREFIX_APACHE = "http://apache.org/xml/features/";

  public static final String FEATURE_SCAN_ONLY_ROOT ="http://sap.com/xml/features/scan-only-root";
  public static final String FEATURE_EXPANDING_REFERENCES = "http://inqmy.com/dom/expanding-references";
  public static final String FEATURE_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
  public static final String FEATURE_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
  public static final String FEATURE_STRING_INTERNING = "http://xml.org/sax/features/string-interning";
  public static final String FEATURE_USE_PROXY = "http://inqmy.org/sax/features/use-proxy";
  public static final String FEATURE_TRIM_WHITESPACES = "http://inqmy.org/dom/features/trim-white-spaces";
  public static final String FEATURE_CLOSE_STREAMS = "http://inqmy.org/xml/features/close-streams";
  public static final String FEATURE_VALIDATION = PREFIX_SAX + "validation";
  public static final String FEATURE_NAMESPACES = PREFIX_SAX + "namespaces";
  public static final String FEATURE_NAMESPACE_PREFIXES = PREFIX_SAX + "namespace-prefixes";
  public static final String FEATURE_SOAP_DATA = "http://sap.com/xml/soap-input";
  public static final String SCHEMA_AWARENESS = "http://sap.com/xml/schema-awareness";
  public static final String APACHE_SCHEMA_VALIDATION = PREFIX_APACHE + "validation/schema";
  public static final String APACHE_DYNAMIC_VALIDATION = PREFIX_APACHE + "validation/dynamic";
  public static final String FEATURE_READ_DTD = "http://inqmy.org/sax/features/read-dtd";
  public static final String FEATURE_HTML_MODE = "http://sap.com/xml/html-mode";
  public static final String MAX_REFERENCES = "http://sap.com/xml/max-references";
  public static final String FEATURE_SCHEMA_DOM_VALIDATION = "http://sap.com/xml/schemaDOMValidation";
  public static final String FEATURE_XSD_DOC_VALIDATION = "http://sap.com/xml/validateXSDDoc";
  public static final String FEATURE_BACKWARDS_COMPATIBILITY_MODE = "http://sap.com/xml/features/backwards-compatibility-mode";
  public static final String FEATURE_SCHEMA_CANONICALIZATION_PROCESSING = "http://sap.com/xml/schemaCanonicalizationProcessing";

  public static final HashSet SUPPORTED = new HashSet();
  public static final HashSet RECOGNIZED = new HashSet();

  static {
    SUPPORTED.add(FEATURE_VALIDATION);
    SUPPORTED.add(FEATURE_NAMESPACES);
    SUPPORTED.add(FEATURE_NAMESPACE_PREFIXES);
    SUPPORTED.add(FEATURE_EXTERNAL_GENERAL_ENTITIES);
    SUPPORTED.add(FEATURE_SOAP_DATA);
    SUPPORTED.add(FEATURE_READ_DTD);
    SUPPORTED.add(APACHE_DYNAMIC_VALIDATION);
    SUPPORTED.add(FEATURE_HTML_MODE);
    SUPPORTED.add(FEATURE_TRIM_WHITESPACES);
    SUPPORTED.add(FEATURE_EXPANDING_REFERENCES);
    SUPPORTED.add(FEATURE_SCAN_ONLY_ROOT);
    SUPPORTED.add(FEATURE_USE_PROXY);
    SUPPORTED.add(FEATURE_CLOSE_STREAMS);
    SUPPORTED.add(PREFIX_VALIDATION + "printWarnings");
    SUPPORTED.add(PREFIX_VALIDATION + "throwWarnings");
    SUPPORTED.add(PREFIX_VALIDATION + "warnAttributeBeforeElement");
    SUPPORTED.add(PREFIX_VALIDATION + "warnMoreThanOneAttList");
    SUPPORTED.add(PREFIX_VALIDATION + "warnMoreThanOneAttribute");
    SUPPORTED.add(PREFIX_VALIDATION + "warnUndefinedIdRefs");
    SUPPORTED.add(PREFIX_VALIDATION + "warnDuplicatesInEnumerationAttributes");
    SUPPORTED.add(PREFIX_VALIDATION + "warnDuplicatesInNotationAttributes");
    SUPPORTED.add(PREFIX_VALIDATION + "warnElementMentionedButNotDeclared");
    SUPPORTED.add(SCHEMA_AWARENESS);
    SUPPORTED.add(APACHE_SCHEMA_VALIDATION);
    SUPPORTED.add(FEATURE_XSD_DOC_VALIDATION);
    SUPPORTED.add(MAX_REFERENCES);
    SUPPORTED.add(FEATURE_SCHEMA_DOM_VALIDATION);
    SUPPORTED.add(FEATURE_BACKWARDS_COMPATIBILITY_MODE);
    SUPPORTED.add(FEATURE_SCHEMA_CANONICALIZATION_PROCESSING);
    RECOGNIZED.addAll(SUPPORTED);
    RECOGNIZED.add(PREFIX_SAX + "external-parameter-entities");
    RECOGNIZED.add(PREFIX_SAX + "string-interning");
    RECOGNIZED.add(PREFIX_VALIDATION + "warnNonDeterministicContentModel");

  }

  public static final HashSet POSITIVE_ANSWERS = new HashSet();
  public static final HashSet NEGATIVE_ANSWERS = new HashSet();
  static {
    POSITIVE_ANSWERS.add("yes");  NEGATIVE_ANSWERS.add("no");
    POSITIVE_ANSWERS.add("true"); NEGATIVE_ANSWERS.add("false");
    POSITIVE_ANSWERS.add("1");    NEGATIVE_ANSWERS.add("0");
  }

  public static boolean createBooleanValue(Object value) throws SAXNotSupportedException {
    if(value == null) {
      throw new SAXNotSupportedException("Attempting to create a feature value from object, whose reference is null.");
    }
    if(value instanceof Boolean) {
      return(((Boolean)value).booleanValue());
    } else if(value instanceof String) {
      if(Features.POSITIVE_ANSWERS.contains(value)) {
        return(true);
      } else if(Features.NEGATIVE_ANSWERS.contains(value)) {
        return(false);
      } else {
        throw new SAXNotSupportedException("Attempting to create a feature value from string, whose value '" + value.toString() + "' is not supported.");
      }
    } else {
      throw new SAXNotSupportedException("Attempting to create a feature value from object, whose class name '" + value.getClass().getName() + "' is not supported.");
    }
  }

  private Features() {
  }
}
