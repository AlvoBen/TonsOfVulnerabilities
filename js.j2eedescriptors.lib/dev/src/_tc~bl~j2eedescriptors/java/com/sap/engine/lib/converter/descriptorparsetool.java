/*
 * Copyright (c) 2004 by SAP AG, Walldorf.
 * http://www.sap.com
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 *
 *  $Id: //engine/js.j2eedescriptors.lib/dev/src/_tc~bl~j2eedescriptors/java/com/sap/engine/lib/converter/DescriptorParseTool.java#1 $
 */

package com.sap.engine.lib.converter;

import java.io.InputStream;
import java.rmi.UnmarshalException;

import org.w3c.dom.Document;

import com.sap.engine.lib.converter.impl.ApplicationConverter;
import com.sap.engine.lib.converter.impl.ConnectorConverter;
import com.sap.engine.lib.converter.impl.EJBConverter;
import com.sap.engine.lib.converter.impl.WebConverter;
import com.sap.engine.lib.descriptors.application.ApplicationType;
import com.sap.engine.lib.descriptors.applicationj2eeengine.ApplicationJ2EeEngine;
import com.sap.engine.lib.descriptors.connector.ConnectorType;
import com.sap.engine.lib.descriptors.ejb.EjbJarType;
import com.sap.engine.lib.descriptors.ejbj2eeengine.EjbJ2EeEngine;
import com.sap.engine.lib.descriptors.persistent.PersistentEjbMap;
import com.sap.engine.lib.descriptors.web.WebAppType;
import com.sap.engine.lib.descriptors.webj2eeengine.WebJ2EeEngineType;
import com.sap.engine.lib.descriptors.webjsptld.TldTaglibType;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.lib.processor.SchemaProcessorFactory;

/**
 * Utility class for building typed DOMs of all J2EE descriptor types by
 * (optionally) replacing substitution variables, then parsing XML streams and
 * automatically converting to latest J2EE revision on the fly if necessary.
 * 
 * @author d037913
 */
public class DescriptorParseTool {

  private static DescriptorParseTool instance;

  private DescriptorParseTool() {
  }

  public static synchronized DescriptorParseTool getInstance() {
    if (instance != null) {
      return instance;
    }
    return instance = new DescriptorParseTool();
  }

  public EjbJarType parseEjbJar(InputStream inputStream, boolean xmlValidate)
      throws DescriptorParseException {
    return parseEjbJar(createConversionContext(EJBConverter.EJBJAR_FILENAME,
        inputStream, xmlValidate));
  }

  /**
   * Parses an ejb-jar.xml provided inside the conversion context.
   * @throws DescriptorParseException
   */
  public EjbJarType parseEjbJar(ConversionContext context)
      throws DescriptorParseException {
    try {
      ConverterTool.getInstance().convert(IJ2EEDescriptorConverter.EJB, context);
      Document ejbDoc = context
          .getConvertedDocument(EJBConverter.EJBJAR_FILENAME);
      if (ejbDoc != null) {
        return (EjbJarType) SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.EJB).parse(ejbDoc);
      } 
      return null;
    } catch (UnmarshalException e) {
      throw new DescriptorParseException(e);
    } catch (ConversionException e) {
      throw new DescriptorParseException(e);
    }
  }

  public EjbJ2EeEngine parseEjbJ2EeEngine(InputStream inputStream,
      boolean xmlValidate) throws DescriptorParseException {
    return parseEjbJ2EeEngine(createConversionContext(
        EJBConverter.EJBJ2EE_FILENAME, inputStream, xmlValidate));
  }

  /**
   * Parses an ejb-j2ee-engine.xml provided inside the conversion context.
   * @throws DescriptorParseException
   */
  public EjbJ2EeEngine parseEjbJ2EeEngine(ConversionContext context)
      throws DescriptorParseException {
    try {
      ConverterTool.getInstance().convert(IJ2EEDescriptorConverter.EJB, context);
      Document ejbJ2eeDoc = context
          .getConvertedDocument(EJBConverter.EJBJ2EE_FILENAME);
      if (ejbJ2eeDoc != null) {
        return (EjbJ2EeEngine) SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.EJBJ2EE).parse(ejbJ2eeDoc);
      } 
      return null;
    } catch (UnmarshalException e) {
      throw new DescriptorParseException(e);
    } catch (ConversionException e) {
      throw new DescriptorParseException(e);
    }
  }

  public PersistentEjbMap parsePersistentEjb(InputStream inputStream,
      boolean xmlValidate) throws DescriptorParseException {
    return parsePersistentEjb(createConversionContext(
        EJBConverter.PERSISTENT_FILENAME, inputStream, xmlValidate));
  }

  /**
   * Parses a persistent.xml InputStream provided inside the conversion context.
   * @throws DescriptorParseException
   */
  public PersistentEjbMap parsePersistentEjb(ConversionContext context)
      throws DescriptorParseException {
    try {
      ConverterTool.getInstance().convert(IJ2EEDescriptorConverter.EJB, context);
      Document persistentDoc = context
          .getConvertedDocument(EJBConverter.PERSISTENT_FILENAME);
      if (persistentDoc != null) {
        return (PersistentEjbMap) SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.EJBPERSISTENT).parse(persistentDoc);
      }
      return null;
    } catch (ConversionException e) {
      throw new DescriptorParseException(e);
    } catch (UnmarshalException e) {
      throw new DescriptorParseException(e);
    }
  }

  /**
   * Parses a storage.xml InputStream provided inside the conversion context.
   * @throws DescriptorParseException
   */
  public PersistentEjbMap parseStorageEJB(ConversionContext context)
      throws DescriptorParseException {
    try {
      ConverterTool.getInstance().convert(IJ2EEDescriptorConverter.EJB, context);
      Document storageDoc = context
          .getConvertedDocument(EJBConverter.STORAGE_FILENAME);
      if (storageDoc != null) {
        return (PersistentEjbMap) SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.EJBPERSISTENT).parse(storageDoc);
      }
      return null;
    } catch (ConversionException e) {
      throw new DescriptorParseException(e);
    } catch (UnmarshalException e) {
      throw new DescriptorParseException(e);
    }
  }

  public ApplicationType parseApplication(InputStream inputStream,
      boolean xmlValidate) throws DescriptorParseException {
    return parseApplication(createConversionContext(
        ApplicationConverter.APPLICATION_FILENAME, inputStream, xmlValidate));
  }

  /**
   * Parses an application.xml provided inside the conversion context.
   */
  public ApplicationType parseApplication(ConversionContext context)
      throws DescriptorParseException {
    try {
      ConverterTool.getInstance().convert(IJ2EEDescriptorConverter.APPLICATION, context);
      Document appDoc = context
          .getConvertedDocument(ApplicationConverter.APPLICATION_FILENAME);
      if (appDoc != null) {
        return (ApplicationType) SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.APP).parse(appDoc);
      } 
      return null;
    } catch (ConversionException e) {
      throw new DescriptorParseException(e);
    } catch (UnmarshalException e) {
      throw new DescriptorParseException(e);
    }
  }

  public ApplicationJ2EeEngine parseApplicationJ2ee(InputStream inputStream,
      boolean xmlValidate) throws DescriptorParseException {
    return parseApplicationJ2ee(createConversionContext(
        ApplicationConverter.APPLICATION_J2EE_FILENAME, inputStream,
        xmlValidate));
  }

  /**
   * Parses an application-j2ee-engine.xml InputStream  provided inside the conversion context.
   * @throws DescriptorParseException
   */
  public ApplicationJ2EeEngine parseApplicationJ2ee(ConversionContext context)
      throws DescriptorParseException {
    try {
      ConverterTool.getInstance().convert(IJ2EEDescriptorConverter.APPLICATION, context);
      Document appJ2eeDoc = context
          .getConvertedDocument(ApplicationConverter.APPLICATION_J2EE_FILENAME);
      SchemaProcessor appProcessor = SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.APPJ2EE);
      appProcessor.switchOffValidation();
      if (appJ2eeDoc != null) {
        return (ApplicationJ2EeEngine) SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.APPJ2EE).parse(appJ2eeDoc);
      }
      return null;
    } catch (ConversionException e) {
      throw new DescriptorParseException(e);
    } catch (UnmarshalException e) {
      throw new DescriptorParseException(e);
    }
  }

  public WebAppType parseWeb(InputStream inputStream, boolean xmlValidate)
      throws DescriptorParseException {
    return parseWeb(createConversionContext(WebConverter.WEB_FILENAME,
        inputStream, xmlValidate));
  }
  
  /**
   * Parses a web.xml InputStream provided inside the conversion context.
   * @throws DescriptorParseException
   */
  public WebAppType parseWeb(ConversionContext context)
      throws DescriptorParseException {
    try {
      ConverterTool.getInstance().convert(IJ2EEDescriptorConverter.WEB, context);
      Document webDoc = context.getConvertedDocument(WebConverter.WEB_FILENAME);
      if (webDoc != null) {
        return (WebAppType) SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.WEB).parse(webDoc);
      } 
      return null;
    } catch (UnmarshalException e) {
      throw new DescriptorParseException(e);
    } catch (ConversionException e) {
      throw new DescriptorParseException(e);
    }
  }

  public WebJ2EeEngineType parseWebJ2ee(InputStream inputStream,
      boolean xmlValidate) throws DescriptorParseException {
    return parseWebJ2ee(createConversionContext(WebConverter.WEBJ2EE_FILENAME,
        inputStream, xmlValidate));
  }

  /**
   * Parses an web-j2ee-engine.xml InputStream provided inside the conversion context.
   * @throws DescriptorParseException
   */
  public WebJ2EeEngineType parseWebJ2ee(ConversionContext context)
      throws DescriptorParseException {
    try {
      ConverterTool.getInstance().convert(IJ2EEDescriptorConverter.WEB, context);
      Document webJ2eeDoc = context
          .getConvertedDocument(WebConverter.WEBJ2EE_FILENAME);
      if (webJ2eeDoc != null) {
        return (WebJ2EeEngineType) SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.WEBJ2EE).parse(webJ2eeDoc);
      } 
      return null;
    } catch (UnmarshalException e) {
      throw new DescriptorParseException(e);
    } catch (ConversionException e) {
      throw new DescriptorParseException(e);
    }
  }

  public TldTaglibType parseWebJspTld(InputStream inputStream)
      throws DescriptorParseException {
    try {
      return (TldTaglibType) SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.WEBJSPTLD).parse(inputStream);
    } catch (Exception e) {
      throw new DescriptorParseException(e);
    }
  }

  public ConnectorType parseConnector(InputStream inputStream,
      boolean xmlValidate) throws DescriptorParseException {
    return parseConnector(createConversionContext(
        ConnectorConverter.CONNECTOR_FILENAME, inputStream, xmlValidate));
  }

  /**
   * Parses a ra.xml InputStream provided inside the conversion context.
   * @throws DescriptorParseException
   */
  public ConnectorType parseConnector(ConversionContext context) throws DescriptorParseException {
    try {
      Boolean attribute = (Boolean) context.getAttribute("convert");
      AbstractConverter myConverter = (AbstractConverter) ConverterTool.getInstance().getConverter(IJ2EEDescriptorConverter.CONNECTOR);
      if (myConverter == null) {
        throw new IllegalArgumentException("no converter registered for container type: " + IJ2EEDescriptorConverter.CONNECTOR);
      }

      boolean convert = attribute != null ? attribute.booleanValue() : true;

      if (convert) {
        myConverter.convert(context);
      }
      boolean validate = context.isXmlValidating();
      SchemaProcessor connectorSchemaProcessor = SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.CONNECTOR);
      synchronized (connectorSchemaProcessor) {
        if (validate) {
          connectorSchemaProcessor.switchOnValidation();
        } else {
          connectorSchemaProcessor.switchOffValidation();
        }
        if (convert) {
          //validation is done in the converter
          if (validate) {
            connectorSchemaProcessor.switchOffValidation();
          }
          Document raDoc = context.getConvertedDocument(ConnectorConverter.CONNECTOR_FILENAME);
          if (raDoc != null) {
            return (ConnectorType) connectorSchemaProcessor.parse(context.getConvertedDocument(ConnectorConverter.CONNECTOR_FILENAME));
          }
        }
        try {
          InputStream connectorStream = context.getInputStream(ConnectorConverter.CONNECTOR_FILENAME);
          if (connectorStream != null) {
              return (ConnectorType) connectorSchemaProcessor.parse(connectorStream);
          }
          return null;
        } finally {
          connectorSchemaProcessor.setErrorHandler(null);
        }
      }
    } catch (Exception exc) {
      throw new DescriptorParseException(exc);
    }
  }
  
  public com.sap.engine.lib.descriptors.connectorj2eeengine.ConnectorType parseConnectorJ2EE(
      InputStream inputStream, boolean xmlValidate)
      throws DescriptorParseException {
    return parseConnectorJ2EE(createConversionContext(
        ConnectorConverter.CONNECTOR_J2EE_FILENAME, inputStream, xmlValidate));
  }
  
  /**
   * Parses a connector-j2ee-engine.xml InputStream provided inside the conversion context.
   * @throws DescriptorParseException
   */
  public com.sap.engine.lib.descriptors.connectorj2eeengine.ConnectorType parseConnectorJ2EE(ConversionContext context) throws DescriptorParseException {
    try {
      Boolean attribute = (Boolean) context.getAttribute("convert");
      AbstractConverter myConverter = (AbstractConverter)ConverterTool.getInstance().getConverter(IJ2EEDescriptorConverter.CONNECTOR);
      if (myConverter == null) {
        throw new IllegalArgumentException("no converter registered for container type: " + IJ2EEDescriptorConverter.CONNECTOR);
      }

      boolean convert = attribute != null ? attribute.booleanValue() : true;
      if (convert) {
        ConverterTool.getInstance().convert(IJ2EEDescriptorConverter.CONNECTOR, context);
      }
      boolean validate = context.isXmlValidating();
      SchemaProcessor connectorJ2EESchemaProcessor = SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.CONNECTORJ2EE);
      synchronized (connectorJ2EESchemaProcessor) {
        if (validate) {
          connectorJ2EESchemaProcessor.switchOnValidation();
        } else {
          connectorJ2EESchemaProcessor.switchOffValidation();
        }
        if (convert) {
          //validation is done in the converter
          if (validate) {
            connectorJ2EESchemaProcessor.switchOffValidation();
          }
          Document sapDoc = context.getConvertedDocument(ConnectorConverter.CONNECTOR_J2EE_FILENAME);
          if (sapDoc != null) {
            return (com.sap.engine.lib.descriptors.connectorj2eeengine.ConnectorType) connectorJ2EESchemaProcessor.parse(sapDoc);
          }
        }
        try {
          InputStream connectorj2eeInStream = context.getInputStream(ConnectorConverter.CONNECTOR_J2EE_FILENAME);
        if (connectorj2eeInStream != null) {
            return (com.sap.engine.lib.descriptors.connectorj2eeengine.ConnectorType)
                    connectorJ2EESchemaProcessor.parse(connectorj2eeInStream);
        }
        return null;
        } finally {
          connectorJ2EESchemaProcessor.setErrorHandler(null);
        }
      }
    } catch (Exception exc) {
      throw new DescriptorParseException(exc);
    }
  }  
  
  public Object parseDescriptor(String fileName, int containerType,
      ConversionContext context) throws DescriptorParseException {
    switch (containerType) {
    case IJ2EEDescriptorConverter.EJB:
      if (fileName.indexOf("ejb-jar.xml") != -1) {
          return parseEjbJar(context);
      } else if (fileName.indexOf("ejb-j2ee-engine.xml") != -1) {
          return parseEjbJ2EeEngine(context);
      } else if (fileName.indexOf("persistent.xml") != -1) {
          return parsePersistentEjb(context);
        } else if (fileName.indexOf("storage.xml") != -1) {
          return parseStorageEJB(context);
        } else {
        throw new IllegalArgumentException("unsupported file name: " + fileName);
      }
      case IJ2EEDescriptorConverter.WEB:
        if (fileName.indexOf("web.xml") != -1) {
          return parseWeb(context);
        } else if (fileName.indexOf("web-j2ee-engine.xml") != -1) {
          return parseWebJ2ee(context);
        } else {
        throw new IllegalArgumentException("unsupported file name: " + fileName);
        }
      case IJ2EEDescriptorConverter.APPLICATION:
        if (fileName.indexOf("application.xml") != -1) {
          return parseApplication(context);
        } else if (fileName.indexOf("application-j2ee-engine.xml") != -1) {
          return parseApplicationJ2ee(context);
        } else {
        throw new IllegalArgumentException("unsupported file name: " + fileName);
        }
      case IJ2EEDescriptorConverter.APPCLIENT:
        throw new UnsupportedOperationException("not yet implemented");
      case IJ2EEDescriptorConverter.CONNECTOR:
        if (fileName.indexOf("ra.xml") != -1) {
          return parseConnector(context);
        } else if (fileName.indexOf("connector-j2ee-engine.xml") != -1) {
          return parseConnectorJ2EE(context);
        } else {
        throw new IllegalArgumentException("unsupported file name: " + fileName);
        }
      default:
        throw new IllegalArgumentException("unknown container type: "
            + containerType);
    }
  }

  public Object unmarshal(String docFileName, Document doc, int containerType)
      throws UnmarshalException {
    switch (containerType) {
      case IJ2EEDescriptorConverter.EJB:
        if (docFileName.indexOf("ejb-jar.xml") != -1) {
          return SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.EJB).parse(doc);
        } else if (docFileName.indexOf("ejb-j2ee-engine.xml") != -1) {
          return SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.EJBJ2EE).parse(doc);
        } else if (docFileName.indexOf("persistent.xml") != -1) {
          return SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.EJBPERSISTENT).parse(doc);
        } else {
          throw new IllegalArgumentException("unsupported file name: "
              + docFileName);
        }
      case IJ2EEDescriptorConverter.WEB:
        if (docFileName.indexOf("web.xml") != -1) {
          return SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.WEB).parse(doc);
        } else if (docFileName.indexOf("web-j2ee-engine.xml") != -1) {
          return SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.WEBJ2EE).parse(doc);
        } else {
          throw new IllegalArgumentException("unsupported file name: "
              + docFileName);
        }
      case IJ2EEDescriptorConverter.APPLICATION:
        if (docFileName.indexOf("application.xml") != -1) {
          return SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.APP).parse(doc);
        } else if (docFileName.indexOf("application-j2ee-engine.xml") != -1) {
          return SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.APPJ2EE).parse(doc);
        } else {
          throw new IllegalArgumentException("unsupported file name: "
              + docFileName);
        }
      case IJ2EEDescriptorConverter.APPCLIENT:
        throw new UnsupportedOperationException("not yet implemented");
      case IJ2EEDescriptorConverter.CONNECTOR:
        if (docFileName.indexOf("ra.xml") != -1) {
          return SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.CONNECTOR).parse(doc);
        } else if (docFileName.indexOf("connector-j2ee-engine.xml") != -1) {
          return SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.CONNECTORJ2EE).parse(doc);
        } else {
          throw new IllegalArgumentException("unsupported file name: "
              + docFileName);
        }
      default:
        throw new IllegalArgumentException("unknown container type: "
            + containerType);
    }
  }

  private ConversionContext createConversionContext(String inStreamFileName,
      InputStream inStream, boolean xmlValidate) {
    ConversionContext ctx = new ConversionContext(null, xmlValidate);
    // "forgiving" conversion by default
    ctx.setAttribute(ConversionContext.FORGIVING_ATTR, Boolean.TRUE);
    ctx.setInputStream(inStreamFileName, inStream);
    return ctx;
  }

}