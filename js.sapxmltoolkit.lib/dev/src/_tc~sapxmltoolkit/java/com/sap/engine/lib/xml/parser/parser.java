package com.sap.engine.lib.xml.parser;

import org.xml.sax.*;

import com.sap.engine.lib.jaxp.ParserAttributes;
import com.sap.engine.lib.schema.validator.SchemaDocHandler;
import com.sap.engine.lib.schema.validator.ValidationConfigContext;
import com.sap.engine.lib.schema.components.Schema;
import com.sap.engine.lib.schema.components.impl.LoaderImpl;
import com.sap.engine.lib.schema.exception.SchemaException;
import com.sap.engine.lib.schema.exception.SchemaValidationException;
import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.xml.parser.dtd.ValidationException;
import com.sap.engine.lib.xml.util.NS;

import java.util.StringTokenizer;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-12-16
 * Time: 16:19:08
 * To change this template use Options | File Templates.
 */
public class Parser implements Constants {

  protected static final String SCHEMA_LOCATION_ATTRIB_NAME = "schemaLocation";
  protected static final String NO_NS_SCHEMA_LOCATION_ATTRIB_NAME = "noNamespaceSchemaLocation";

  protected XMLParser xmlParser;
  protected String schemaLanguage;
  protected String dtdLanguage;
  protected Object schemaSourceObj;
  protected ErrorHandler errorHandler;
  protected boolean apacheSchemaValidationFeature;
  protected boolean schemaAwareFeature;
  protected boolean validationFeature;
  protected boolean dynamicValidationFeature;
  protected boolean validateXSDDoc;
  protected Object schemaSource;
  protected Schema externalSchema;
  protected LoaderImpl loader;
  protected boolean canonicalizationProcessing;

  public Parser() throws ParserException {
    this(new XMLParser());
  }

  public Parser(XMLParser xmlParser) throws ParserException {
    this.xmlParser = xmlParser;
    validateXSDDoc = true;
  }

  public ErrorHandler getErrorHandler() {
    return this.errorHandler;
  }

  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    if(name.equals(Features.APACHE_SCHEMA_VALIDATION)) {
      apacheSchemaValidationFeature = value;
    } else if(name.equals(Features.SCHEMA_AWARENESS)) {
      schemaAwareFeature = value;
    } else if(name.equals(Features.FEATURE_XSD_DOC_VALIDATION)) {
      validateXSDDoc = value;
    } else if(name.equals(Features.FEATURE_SCHEMA_CANONICALIZATION_PROCESSING)) {
      canonicalizationProcessing = value;
    } else {
      if(name.equals(Features.FEATURE_VALIDATION)) {
        validationFeature = value;
      } else if(name.equals(Features.APACHE_DYNAMIC_VALIDATION)) {
        dynamicValidationFeature = value;
      }
      xmlParser.setFeature(name, value);
    }
  }

  public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    if(name.equals(Features.APACHE_SCHEMA_VALIDATION)) {
      return(apacheSchemaValidationFeature);
    } else if(name.equals(Features.SCHEMA_AWARENESS)) {
      return(schemaAwareFeature);
    } else if(name.equals(Features.FEATURE_VALIDATION)) {
      return(validationFeature);
    } else if(name.equals(Features.APACHE_DYNAMIC_VALIDATION)) {
      return(dynamicValidationFeature);
    } else {
      return(xmlParser.getFeature(name));
    }
  }

  public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (name.equals(JAXPProperties.PROPERTY_EXTERNAL_SCHEMA_LOCATION)) {
      throw new SAXNotSupportedException("");
    } else if (name.equals(JAXPProperties.PROPERTY_EXTERNAL_NONAMESPACE_SCHEMA_LOCATION)) {
      throw new SAXNotSupportedException("");
    } else if(name.equals(JAXPProperties.PROPERTY_SCHEMA_OBJECT)) {
      return(externalSchema);
    } else if(name.equals(JAXPProperties.PROPERTY_SCHEMA_LANGUAGE)) {
      return(schemaLanguage);
    } else if(name.equals(JAXPProperties.PROPERTY_SCHEMA_SOURCE)) {
      return(schemaSourceObj);
    } else {
      return(xmlParser.getProperty(name));
    }
  }

  public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    if(name.equals(JAXPProperties.PROPERTY_EXTERNAL_SCHEMA_LOCATION)) {
      checkForceSchemaValidation();
      externalSchema = null;
      if(value == null) {
        schemaSource = null;
      } else {
        checkStringValue(name, value);
        String targetNsesAndLocations = (String)value;
        StringTokenizer tokenizer = new StringTokenizer(targetNsesAndLocations);
        int toukensCount = tokenizer.countTokens();
        if(toukensCount % 2 != 0) {
          throw new SAXNotSupportedException("The value property '" + JAXPProperties.PROPERTY_EXTERNAL_SCHEMA_LOCATION + "' must contain taget namespaces and schema locations, separated with white spaces.");
        }
        int count = toukensCount / 2;
        schemaSource = new String[2][count];
        int index = 0;
        while(tokenizer.hasMoreTokens()) {
          ((String[][])schemaSource)[0][index] = tokenizer.nextToken();
          ((String[][])schemaSource)[1][index++] = tokenizer.nextToken();
        }
      }
    } else if(name.equals(JAXPProperties.PROPERTY_EXTERNAL_NONAMESPACE_SCHEMA_LOCATION)) {
      checkForceSchemaValidation();
      externalSchema = null;
      if(value == null) {
        schemaSource = null;
      } else {
        checkStringValue(name, value);
        schemaSource = new String[2][1];
        ((String[][])schemaSource)[0][0] = "";
        ((String[][])schemaSource)[1][0] = (String)value;
      }
    } else if(name.equals(JAXPProperties.PROPERTY_SCHEMA_OBJECT)) {
      checkForceSchemaValidation();
      schemaSource = null;
      if(value == null) {
        externalSchema = null;
      } else {
        if(!(value instanceof Schema)) {
          throw new SAXNotSupportedException("Attempting to set property '" + name + "' to an object, which class is not com.sap.engine.lib.schema.components.Schema.");
        }
        externalSchema = (Schema)value;
      }
    } else if(name.equals(JAXPProperties.PROPERTY_SCHEMA_LANGUAGE)) {
      if(value == null) {
        schemaLanguage = null;
      } else {
        checkStringValue(name, value);
        schemaLanguage = (String)value;
      }
    } else if(name.equals(JAXPProperties.PROPERTY_SCHEMA_SOURCE)) {
      if(schemaLanguage == null || !schemaLanguage.equals(SCHEMA_LANGUAGE)) {
        throw new SAXNotSupportedException("Attempting to set property '" + JAXPProperties.PROPERTY_SCHEMA_SOURCE + "' without setting property '" + JAXPProperties.PROPERTY_SCHEMA_LANGUAGE + "' to value '" + SCHEMA_LANGUAGE + "'.");
      }
      externalSchema = null;
      if(value == null) {
        schemaSourceObj = null;
        schemaSource = null;
      } else {
        schemaSourceObj = value;
        schemaSource = schemaSourceObj;
        //externalSchema = loadSchema(schemaSourceObj);
      }
    } else if(name.equals(JAXPProperties.PROPERTY_DTD_LANGUAGE)) {
			if(value == null) {
        dtdLanguage = null;
			} else {
				checkStringValue(name, value);
				dtdLanguage = (String)value;
			}
    } else {
      xmlParser.setProperty(name, value);
    }
  }

  private Schema loadSchema(String[] targetNamespaces, String[] locations) throws SAXNotSupportedException {
    try {
      LoaderImpl loader = determineLoader();
      return(loader.load(targetNamespaces, locations));
    } catch(Exception exc) {
      throw new SAXNotSupportedException(exc.getMessage());
    }
  }

  private Schema loadSchema(Object schemaSource) throws SAXNotSupportedException {
    try {
      LoaderImpl loader = determineLoader();
      return(loader.load(schemaSource));
    } catch(Exception exc) {
      throw new SAXNotSupportedException(exc.getMessage());
    }
  }

  private LoaderImpl determineLoader() {
    if(loader == null) {
      loader = new LoaderImpl();
    }
    loader.setEntityResolver(getEntityResolver());
    loader.setValidateXSDDoc(validateXSDDoc);
    return(loader);
  }

  private void checkStringValue(String name, Object value) throws SAXNotSupportedException {
    if(!(value instanceof String)) {
      throw new SAXNotSupportedException("Attempting to set property '" + name + "' to an object, which class is not java.lang.String.");
    }
  }

  private void checkForceSchemaValidation() throws SAXNotSupportedException {
    if(!(schemaLanguage != null && schemaLanguage.equals(SCHEMA_LANGUAGE)) && !apacheSchemaValidationFeature && !schemaAwareFeature) {
      throw new SAXNotSupportedException("Attempting to set an external schema without setting any of the features : '" + Features.APACHE_SCHEMA_VALIDATION + "'; '" + Features.SCHEMA_AWARENESS + " or property '" + JAXPProperties.PROPERTY_SCHEMA_LANGUAGE + "' to value '" + SCHEMA_LANGUAGE + "'.");
    }
  }

  public void setEntityResolver(EntityResolver resolver) {
    xmlParser.setEntityResolver(resolver);
  }

  public EntityResolver getEntityResolver() {
    return(xmlParser.getEntityResolver());
  }

  protected void parse_SchemaValidation(InputSource input, DocHandler handler) throws Exception {
    configureParser_DTDLanguage();
    SchemaDocHandler schemaDocHandler = new SchemaDocHandler(createValidationConfigContext(handler));
    schemaDocHandler.setValidateXSDDoc(validateXSDDoc);
    schemaDocHandler.setEntityResolver(getEntityResolver());
    schemaDocHandler.setCanonicalizationProcessing(canonicalizationProcessing);
    xmlParser.parse(input, schemaDocHandler);
    if(errorHandler == null && schemaDocHandler.hasErrors()) {
      throw new SchemaValidationException(schemaDocHandler.getErrorsRepresentation());
    }
  }
  
  private ValidationConfigContext createValidationConfigContext(DocHandler handler) throws SchemaException {
    ValidationConfigContext validationConfigContext = new ValidationConfigContext();
    validationConfigContext.setDocHandler(handler);
    validationConfigContext.setDynamicValidationFeature(dynamicValidationFeature);
    validationConfigContext.setErrorHandler(errorHandler);
    validationConfigContext.setParser(xmlParser);
    validationConfigContext.setSchema(determineExternalSchema());
    return(validationConfigContext);
  }
  
  private void configureParser_DTDLanguage() {
  	if(dtdLanguage != null && dtdLanguage.equals(DTD_LANGUAGE)) {
  		xmlParser.setValidation(validationFeature);
  		xmlParser.setDynamicValidation(dynamicValidationFeature);
  	} else {
  		xmlParser.setValidation(false);
  	}
  }

  private Schema determineExternalSchema() throws SchemaException {
    if(externalSchema != null) {
      return(externalSchema);
    }
    if(schemaSource == null) {
      return(null);
    }
    LoaderImpl loader = determineLoader();
    externalSchema = loader.load(schemaSource);
    return(externalSchema);
  }

  private void parse_DTDValidation(InputSource input, DocHandler handler) throws Exception {
    xmlParser.setValidation(validationFeature);
    xmlParser.setDynamicValidation(dynamicValidationFeature);
    xmlParser.setActiveParse(false);
    xmlParser.parse(input, handler);
  }

  protected void parse(InputSource input, DocHandler handler) throws Exception {
    if(validationFeature) {
      if((schemaLanguage != null && schemaLanguage.equals(SCHEMA_LANGUAGE)) || apacheSchemaValidationFeature || schemaAwareFeature) {
        parse_SchemaValidation(input, handler);
      } else {
        parse_DTDValidation(input, handler);
      }
    } else {
      parse_DTDValidation(input, handler);
    }
  }
  
  public void setAttributes(ParserAttributes attributes) throws SAXNotSupportedException, SAXNotRecognizedException {
		for(int i = 0; i < attributes.size(); i++) {
			String name = attributes.getName(i);
			Object value = attributes.get(i);
			if(Features.RECOGNIZED.contains(name)) {
				setFeature(name, Features.createBooleanValue(value));
			} else {
				setProperty(name, value);
			}
		}
  }
}
