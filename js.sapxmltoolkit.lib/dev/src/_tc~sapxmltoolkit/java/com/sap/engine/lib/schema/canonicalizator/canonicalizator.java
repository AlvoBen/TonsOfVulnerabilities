package com.sap.engine.lib.schema.canonicalizator;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

import com.sap.engine.lib.xml.parser.JAXPProperties;
import com.sap.engine.lib.xml.parser.DOMParser;
import com.sap.engine.lib.xml.parser.Features;
import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.exception.CanonicalizationException;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.Constants;

import javax.xml.transform.stream.StreamSource;
import java.io.*;


/**
 * @author ivan-m
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Canonicalizator implements Constants {
	
  private boolean useExternalSchema;
  private DOMParser parser;
  private Object schemaSource;

	public Canonicalizator() throws CanonicalizationException {
    try {
      useExternalSchema = true;
      parser = new DOMParser();
      parser.setFeature(Features.FEATURE_NAMESPACES, true);
      parser.setFeature(Features.FEATURE_VALIDATION, true);
      parser.setFeature(Features.FEATURE_SCHEMA_CANONICALIZATION_PROCESSING, true);
      parser.setProperty(JAXPProperties.PROPERTY_SCHEMA_LANGUAGE, SCHEMA_LANGUAGE);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
	}

  public void setSchema(Schema schema) {
    schemaSource = schema;
  }

  public void setSchema(String schemaLocation) {
    schemaSource = schemaLocation;
  }

  public void setSchema(String[] schemaLocations) {
    schemaSource = schemaLocations;
  }

  public void setSchema(Node schemaNode) {
    schemaSource = schemaNode;
  }

  public void setSchema(Node[] schemaNodes) {
    schemaSource = schemaNodes;
  }

  public void setSchema(StreamSource schemaSource) {
    this.schemaSource = schemaSource;
  }

  public void setSchema(StreamSource[] schemaSources) {
    schemaSource = schemaSources;
  }

  public void setSchema(InputStream schemaInput) {
    schemaSource = schemaInput;
  }

  public void setSchema(InputStream[] schemaInputs) {
    schemaSource = schemaInputs;
  }

  public void setSchema(InputSource schemaInput) {
    schemaSource = schemaInput;
  }

  public void setSchema(InputSource[] schemaInputs) {
    schemaSource = schemaInputs;
  }

  public void setSchema(File schemaFile) {
    schemaSource = schemaFile;
  }

  public void setSchema(File[] schemaFiles) {
    schemaSource = schemaFiles;
  }

  public void setEntityResolver(EntityResolver resolver) {
    parser.setEntityResolver(resolver);
  }

  public void setUseExternalSchema(boolean useExternalSchema) {
    this.useExternalSchema = useExternalSchema;
  }

  public void canonicalize(String inputXmlLocation, String outputXmlFile) throws CanonicalizationException {
    try {
      canonicalize(parse(inputXmlLocation), outputXmlFile);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  public void canonicalize(String inputXmlLocation, OutputStream xmlOutput) throws CanonicalizationException {
    try {
      canonicalize(parse(inputXmlLocation), xmlOutput);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  public void canonicalize(String inputXmlLocation, Writer xmlWriter) throws CanonicalizationException {
    try {
      canonicalize(parse(inputXmlLocation), xmlWriter);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  private void canonicalize(Element inputXmlElement, String xmlOutputFile) throws CanonicalizationException {
    OutputStream xmlOutput = null;
    try {
      xmlOutput = new FileOutputStream(xmlOutputFile);
    } catch(FileNotFoundException fNFExc) {
      throw new CanonicalizationException(xmlOutputFile);
    }
    CanonicalizationProcessor processor = new CanonicalizationProcessor(inputXmlElement, xmlOutput);
    processor.process();
  }

  private void canonicalize(Element inputXmlElement, OutputStream xmlOutput) throws CanonicalizationException {
    CanonicalizationProcessor processor = new CanonicalizationProcessor(inputXmlElement, xmlOutput);
    processor.process();
  }

  private void canonicalize(Element inputXmlElement, Writer xmlWriter) throws CanonicalizationException {
    CanonicalizationProcessor processor = new CanonicalizationProcessor(inputXmlElement, xmlWriter);
    processor.process();
  }

  public void canonicalize(StreamSource inputXmlSource, String outputXmlFile) throws CanonicalizationException {
    try {
      canonicalize(parse(Tools.createInputSource(inputXmlSource)), outputXmlFile);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  public void canonicalize(StreamSource inputXmlSource, OutputStream xmlOutput) throws CanonicalizationException {
    try {
      canonicalize(parse(Tools.createInputSource(inputXmlSource)), xmlOutput);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  public void canonicalize(StreamSource inputXmlSource, Writer xmlWriter) throws CanonicalizationException {
    try {
      canonicalize(parse(Tools.createInputSource(inputXmlSource)), xmlWriter);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  public void canonicalize(InputStream inputXmlInput, String outputXmlFile) throws CanonicalizationException {
    try {
      canonicalize(parse(inputXmlInput), outputXmlFile);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  public void canonicalize(InputStream inputXmlInput, OutputStream xmlOutput) throws CanonicalizationException {
    try {
      canonicalize(parse(inputXmlInput), xmlOutput);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  public void canonicalize(InputStream inputXmlInput, Writer xmlWriter) throws CanonicalizationException {
    try {
      canonicalize(parse(inputXmlInput), xmlWriter);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  public void canonicalize(InputSource inputXmlSource, String outputXmlFile) throws CanonicalizationException {
    try {
      canonicalize(parse(inputXmlSource), outputXmlFile);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  public void canonicalize(InputSource inputXmlSource, OutputStream xmlOutput) throws CanonicalizationException {
    try {
      canonicalize(parse(inputXmlSource), xmlOutput);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  public void canonicalize(InputSource inputXmlSource, Writer xmlWriter) throws CanonicalizationException {
    try {
      canonicalize(parse(inputXmlSource), xmlWriter);
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  private Element parse(String inputXml) throws CanonicalizationException {
    try {
      parser.setProperty((schemaSource instanceof Schema ? JAXPProperties.PROPERTY_SCHEMA_OBJECT : JAXPProperties.PROPERTY_SCHEMA_SOURCE), (useExternalSchema ? schemaSource : null));
      return(parser.parse(inputXml).getDocumentElement());
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  private Element parse(InputStream inputXml) throws CanonicalizationException {
    try {
      parser.setProperty((schemaSource instanceof Schema ? JAXPProperties.PROPERTY_SCHEMA_OBJECT : JAXPProperties.PROPERTY_SCHEMA_SOURCE), (useExternalSchema ? schemaSource : null));
      return(parser.parse(inputXml).getDocumentElement());
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }

  private Element parse(InputSource inputXml) throws CanonicalizationException {
    try {
      parser.setProperty((schemaSource instanceof Schema ? JAXPProperties.PROPERTY_SCHEMA_OBJECT : JAXPProperties.PROPERTY_SCHEMA_SOURCE), (useExternalSchema ? schemaSource : null));
      return(parser.parse(inputXml).getDocumentElement());
    } catch(Exception exc) {
      throw new CanonicalizationException(exc.getMessage());
    }
  }
}
