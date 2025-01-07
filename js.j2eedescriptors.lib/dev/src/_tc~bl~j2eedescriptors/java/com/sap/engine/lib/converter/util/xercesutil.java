package com.sap.engine.lib.converter.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.sap.engine.lib.converter.AbstractConverter;
import com.sap.engine.lib.xml.StandardDOMParser;
import com.sap.tc.logging.Severity;

public class XercesUtil {
  
  private static final Properties STANDARD_OUTPUT_PROPS = new Properties();
  private static final String DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";
  
  // XERCES attributes, order is important
  private static final Map XERCES_NONVALIDATING_ATTR        = new LinkedHashMap(3);
  private static final Map XERCES_SCHEMA_ATTR               = new LinkedHashMap(6);
  private static final Map XERCES_VALIDATING_ATTR           = new LinkedHashMap(2); 
  private static final Map SAPXMLTOOLKIT_NONVALIDATING_ATTR = new LinkedHashMap(5);
  private static final Map SAPXMLTOOLKIT_SCHEMA_ATTR        = new LinkedHashMap(7);
  private static final Map SAPXMLTOOLKIT_VALIDATING_ATTR    = new LinkedHashMap(5);
  private static final Map<String, Boolean> SAPXMLTOOLKIT_NONVALIDATING_SAX = new HashMap<String, Boolean>();
  private static final Map<String, Boolean> XERCES_NONVALIDATING_SAX = new HashMap<String, Boolean>();
   
  
  public static final int NON_VALIDATING    = 0;
  public static final int SCHEMA_VALIDATING = 1;
  public static final int VALIDATING        = 2;
  
  private static Class documentBuilderFactoryClass;

  
  private static Transformer identityTrafo;
  private static boolean sapXmlToolkitBuilder;
  private static boolean sapXmlToolkitSAXParser;
  
  
  static {
    XERCES_NONVALIDATING_ATTR.put("http://xml.org/sax/features/validation", Boolean.FALSE);
    XERCES_NONVALIDATING_ATTR.put("http://apache.org/xml/features/nonvalidating/load-external-dtd",  Boolean.FALSE);
    XERCES_NONVALIDATING_ATTR.put("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", Boolean.FALSE);
    XERCES_NONVALIDATING_ATTR.put("http://apache.org/xml/features/dom/defer-node-expansion",  Boolean.FALSE);
    XERCES_NONVALIDATING_ATTR.put("http://apache.org/xml/features/dom/include-ignorable-whitespace", Boolean.FALSE);

    // from the description 'By default, validation will occur against DTD', we
    // don't want that,
    // so turn it off which allows us to set
    // http://apache.org/xml/features/nonvalidating/load-external-dtd as well
    XERCES_SCHEMA_ATTR.put("http://xml.org/sax/features/validation", Boolean.FALSE);
    // we turn on the XML Schema Validation
    XERCES_SCHEMA_ATTR.put("http://apache.org/xml/features/validation/schema", Boolean.TRUE);
    XERCES_SCHEMA_ATTR.put("http://apache.org/xml/features/validation/dynamic", Boolean.TRUE);
    // needed to do only a XML Schema Validation, even if document has a DOCTYPE
    // declaration with specified DTD
    XERCES_SCHEMA_ATTR.put("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
    XERCES_SCHEMA_ATTR.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
    // do not load external DTD (true by default, even if non-validating)
    XERCES_SCHEMA_ATTR.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.FALSE);    
    
    XERCES_VALIDATING_ATTR.put("http://apache.org/xml/features/validation/dynamic", Boolean.TRUE);
    XERCES_VALIDATING_ATTR.put("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
    
    SAPXMLTOOLKIT_NONVALIDATING_ATTR.put("http://xml.org/sax/features/validation", Boolean.FALSE);
    SAPXMLTOOLKIT_NONVALIDATING_ATTR.put("http://inqmy.org/sax/features/read-dtd", Boolean.FALSE);
    SAPXMLTOOLKIT_NONVALIDATING_ATTR.put("http://inqmy.org/dom/features/trim-white-spaces", Boolean.TRUE);
    
    SAPXMLTOOLKIT_VALIDATING_ATTR.put("http://apache.org/xml/features/validation/dynamic", Boolean.TRUE);
    SAPXMLTOOLKIT_VALIDATING_ATTR.put("http://inqmy.org/dom/features/trim-white-spaces", Boolean.TRUE);
    SAPXMLTOOLKIT_VALIDATING_ATTR.put("http://sap.com/xml/jaxp/properties/dtdLanguage", "http://sap.com/XMLDTD");
    SAPXMLTOOLKIT_VALIDATING_ATTR.put("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
    SAPXMLTOOLKIT_VALIDATING_ATTR.put("http://sap.com/xml/validateXSDDoc", Boolean.FALSE);
    
    SAPXMLTOOLKIT_SCHEMA_ATTR.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
    SAPXMLTOOLKIT_SCHEMA_ATTR.put("http://apache.org/xml/features/validation/dynamic", Boolean.TRUE);
    SAPXMLTOOLKIT_SCHEMA_ATTR.put("http://xml.org/sax/features/validation", Boolean.TRUE);
    SAPXMLTOOLKIT_SCHEMA_ATTR.put("http://inqmy.org/sax/features/read-dtd", Boolean.FALSE);
    SAPXMLTOOLKIT_SCHEMA_ATTR.put("http://inqmy.org/dom/features/trim-white-spaces", Boolean.TRUE);
    SAPXMLTOOLKIT_SCHEMA_ATTR.put("http://sap.com/xml/validateXSDDoc", Boolean.FALSE);
    SAPXMLTOOLKIT_SCHEMA_ATTR.put("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");

    SAPXMLTOOLKIT_NONVALIDATING_SAX.put("http://xml.org/sax/features/validation", Boolean.FALSE);
    SAPXMLTOOLKIT_NONVALIDATING_SAX.put("http://inqmy.org/sax/features/read-dtd", Boolean.FALSE);
    
    XERCES_NONVALIDATING_SAX.put("http://xml.org/sax/features/validation", Boolean.FALSE);
    XERCES_NONVALIDATING_SAX.put("http://apache.org/xml/features/nonvalidating/load-external-dtd",
        Boolean.FALSE);
    XERCES_NONVALIDATING_SAX.put("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
        Boolean.FALSE);

    STANDARD_OUTPUT_PROPS.setProperty(OutputKeys.METHOD, "xml");
    STANDARD_OUTPUT_PROPS.setProperty(OutputKeys.ENCODING, "UTF-8");
    STANDARD_OUTPUT_PROPS.setProperty(OutputKeys.INDENT, "yes");
  }
  
  private static TransformerFactory transformerFactory = getTransformerFactory();

  private static DocumentBuilder nonValidatingBuilder;
  private static DocumentBuilder schemaValidatingBuilder;
  private static DocumentBuilder validating; 
  
  /**
   * Wraps an <code>InputStream</code> with <code>InputSource</code>
   * 
   * @param stream
   *          the stream to be wrapped
   * @return an <code>InputSource</code> created from the
   *         <code>InputStream</code>
   */
  public static InputSource toInputSource(InputStream stream) {
    InputSource is = new InputSource(stream);
    is.setSystemId(StandardDOMParser.SAP_DTD_PREFIX);
    return is;
  }

  /**
   * Wraps an <code>InputStream</code> with <code>StreamSource</code>
   * 
   * @param stream
   *          the stream to be wrapped
   * @return an <code>StreamSource</code> created from the
   *         <code>InputStream</code>
   */
  public static StreamSource toStreamSource(InputStream stream) {
    StreamSource streamSource = new StreamSource(stream);
    streamSource.setSystemId(StandardDOMParser.SAP_DTD_PREFIX);
    return streamSource;
  }
  
  /**
   * Factory method which returns a class.
   * 
   * @param fqn
   *          a fully qualified class name that will be tried to be loaded first
   * @param fallbackFqn
   *          a fallback fully qualified name that will be tried to be loaded if
   *          the first fqn cannot be loaded
   * @return a class representing the fqn, or class representing the fallbackFqn
   *         if fqn cannot be found or null if none of the two classes can be
   *         loaded
   */
  public static Object createObject(String fqn, String fallbackFqn) {
    try {
      return Class.forName(fqn).newInstance();
    } catch (Exception e) {
      //$JL-EXC$ try the fallback 
    } catch (NoClassDefFoundError e) {
      //$JL-EXC$ try the fallback
    }
    try {
      return Class.forName(fallbackFqn).newInstance();
    } catch (Exception e) {
      //$JL-EXC$
    } catch (NoClassDefFoundError e) {
      //$JL-EXC$ try the fallback
    }
    return null;
  }
 
  /**
   * Checks if the specified document contains a schema location.
   * 
   * @param doc
   *          the DOM that should be checked
   * @return true if the specified document has a schema location, false
   *         otherwise
   */
  public static boolean hasSchemaLocation(Document doc) {    
      Element root = doc.getDocumentElement();
      String schemaLocation = root.getAttribute("xsi:noNamespaceSchemaLocation");
      if (schemaLocation == null || "".equals(schemaLocation)) {
        schemaLocation = root.getAttribute("xsi:schemaLocation");
      }
      return(schemaLocation != null && !"".equals(schemaLocation));
  }
  
  /**
   * Gets the schema location specified in the document 
   * 
   * @param doc the DOM representation of the document
   * @return the schema location or null if there is no such specified
   */
  public static String getSchemaLocation(Document doc) {
    Element root = doc.getDocumentElement();
    String schemaLocation = root.getAttribute("xsi:noNamespaceSchemaLocation");
    if (schemaLocation == null || "".equals(schemaLocation)) {
      schemaLocation = root.getAttribute("xsi:schemaLocation");
    }    
    if (schemaLocation != null) {
      schemaLocation = schemaLocation.toLowerCase(Locale.ENGLISH);
      schemaLocation = schemaLocation.trim();
    }
    return schemaLocation;
  }
    
  
  /**
   * Serializes a DOM document representation to <code>StreamSource</code>.
   * 
   * @param source
   *          a <code>DOMSource</code> representing a document in memory
   * @return a <code>StreamSource</code> document representation
   * @throws SAXException
   *           if there is an error while document has been serialized
   */
  public static StreamSource serialize(DOMSource source) throws SAXException {
    if (identityTrafo == null) {
      try {
        identityTrafo = getIdentityTransformer();
      } catch (TransformerConfigurationException e) {
        throw new TransformerFactoryConfigurationError(e);
      }
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DocumentType docType = ((Document) source.getNode()).getDoctype();
    if (docType != null) {
      String publicId = docType.getPublicId();
      String systemId = docType.getSystemId();
      String internalSubset = docType.getInternalSubset();
      if (internalSubset != null && internalSubset.length() > 0) {
        identityTrafo.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        try {
          baos.write(DECLARATION.getBytes());
        } catch (IOException e) {
          throw new SAXException(e);
        }
        serializeDocType(docType, baos);
      } else {
        if (publicId != null) {
          identityTrafo.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicId);
        }
        if (systemId != null) {
          identityTrafo.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemId);
        }
      }         
    }
    try {
      // This is a workaround for http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6296446
      StreamResult result = new StreamResult(new OutputStreamWriter(baos, "utf-8"));
      identityTrafo.transform(source, result);
    } catch (TransformerException e) {
      if( e.getException() instanceof SAXException )
          throw (SAXException)e.getException();
        throw new SAXException(e);
    } catch (UnsupportedEncodingException e) {
      AbstractConverter.LOCATION.catching(e);
      throw new RuntimeException(e);
    }
    // Workaround for Transformer#reset() bug
    if (docType != null) {
      identityTrafo = null;
    }
    StreamSource streamSource = new StreamSource(new ByteArrayInputStream(baos.toByteArray()));
    streamSource.setSystemId(source.getSystemId());
    return streamSource;    
  }
    
  /**
   * Deserializes a serialized document from <code>StreamSource</code> to
   * a <code>DOMSource</code> which is a representation in memory of the document. 
   * 
   * @param streamSource a <code>StreamSource</code> to the serialized document
   * @return a <code>DOMSource</code> representation of the document
   * @throws SAXException
   */
  public static DOMSource deserialize(StreamSource streamSource) throws SAXException {
    if (identityTrafo == null) {
      try {
        identityTrafo = transformerFactory.newTransformer();
      } catch (TransformerConfigurationException e) {
        throw new TransformerFactoryConfigurationError(e);
      }
    }
    DOMResult result = new DOMResult();
    identityTrafo.setOutputProperties(STANDARD_OUTPUT_PROPS);
    try {
      identityTrafo.transform(streamSource, result);
    } catch (TransformerException e) {
      if( e.getException() instanceof SAXException )
          throw (SAXException)e.getException();
        throw new SAXException(e);
    }
    identityTrafo.setOutputProperties(null);
    return new DOMSource(result.getNode());    
  }
  
  public static void trimWhiteSpaces(Element element) {
    Node child;
    Node next = element.getFirstChild();
    while ((child = next) != null) {
      next = child.getNextSibling();
      if (child.getNodeType() == Node.TEXT_NODE) {
        String trimmed = child.getNodeValue().trim();
        if (trimmed.length() == 0) {
          element.removeChild(child);
        } else {
          child.setNodeValue(trimmed);
        }
      } else if (child.getNodeType() == Node.ELEMENT_NODE) {
        trimWhiteSpaces((Element) child);
      }
    }
  }
  
  /**
   * Creates a new document builder with the specified type.
   * 
   * @param type
   *          one of {@link XercesUtil#NON_VALIDATING}
   *                 {@link XercesUtil#SCHEMA_VALIDATING} 
   *                 {@link XercesUtil#VALIDATING}
   * @return
   * @throws ParserConfigurationException
   */
  public static DocumentBuilder getDocumentBuilder(int type) throws ParserConfigurationException {
    DocumentBuilderFactory dbf = getDocumentBuilderFactory();
    // REVISE check for other standard properties (exclude comments for example)
    dbf.setNamespaceAware(true);
    dbf.setIgnoringElementContentWhitespace(true);
    switch (type) {
    case NON_VALIDATING:
      dbf.setValidating(false);
      setAttributes(dbf, sapXmlToolkitBuilder ? 
                         SAPXMLTOOLKIT_NONVALIDATING_ATTR : 
                         XERCES_NONVALIDATING_ATTR);
      break;
    case SCHEMA_VALIDATING:
      dbf.setValidating(true);
      setAttributes(dbf, sapXmlToolkitBuilder ? 
          SAPXMLTOOLKIT_SCHEMA_ATTR : 
          XERCES_SCHEMA_ATTR);
      break;
    case VALIDATING:
      dbf.setValidating(true);
      setAttributes(dbf, sapXmlToolkitBuilder ? 
          SAPXMLTOOLKIT_VALIDATING_ATTR : 
          XERCES_VALIDATING_ATTR);
      break;
    default:
      throw new IllegalArgumentException("Invalid document builder type " + type);
    }
    return dbf.newDocumentBuilder();
  }
  
  
  /**
   * Creates a new document builder with the specified type.
   * 
   * @param type
   *          one of {@link XercesUtil#NON_VALIDATING}
   */
  public static SAXParser getSAXParser(int type)
      throws ParserConfigurationException, SAXException {
    SAXParserFactory factory = getSAXParserFactory();
    factory.setNamespaceAware(true);
    switch (type) {
    case NON_VALIDATING:
      factory.setValidating(false);
      setAttributes(factory,
          sapXmlToolkitSAXParser ? SAPXMLTOOLKIT_NONVALIDATING_SAX
              : XERCES_NONVALIDATING_SAX);
      break;
    default:
      throw new IllegalArgumentException("Invalid document builder type "
          + type);
    }
    return factory.newSAXParser();
  }

  private static void setAttributes(SAXParserFactory factory,
      Map<String, Boolean> attributes) throws SAXNotRecognizedException,
      SAXNotSupportedException, ParserConfigurationException {
    Set<Map.Entry<String, Boolean>> entries = attributes.entrySet();
    for (Entry<String, Boolean> attribute : entries) {
      factory.setFeature(attribute.getKey(), attribute.getValue());
    }
  }

  private static SAXParserFactory getSAXParserFactory() {
    SAXParserFactory factory = null;
    try {
      factory = (SAXParserFactory) Class.forName(
          "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl")
          .newInstance();
    } catch (Throwable ignore) {
      // $JL-EXC$
    }
    if (factory == null) {
      try {
        factory = (SAXParserFactory) Class.forName(
            "com.sap.engine.lib.jaxp.SAXParserFactoryImpl").newInstance();
        sapXmlToolkitSAXParser = true;
      } catch (Throwable ignore) {
        // $JL-EXC$
      }
    }
    if (factory == null) {
      factory = SAXParserFactory.newInstance();
    }
    return factory;
  }

  public static Transformer getIdentityTransformer() throws TransformerConfigurationException {
    Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperties(STANDARD_OUTPUT_PROPS);
    return transformer;      
  }
  
  public static Transformer getTransformer(InputStream xslStream) throws TransformerConfigurationException {
    try {
      Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslStream));
      transformer.setOutputProperties(STANDARD_OUTPUT_PROPS);
      return transformer;
    } finally {
      try {
        xslStream.close();
      } catch (IOException e) {
        AbstractConverter.LOCATION.traceThrowableT(Severity.ERROR,
            "caught IO exception while closing XSL stream", e);
        throw new RuntimeException(e);
      }
    }
  }

  // Helpers ...
  private static void serializeDocType(DocumentType docType, OutputStream out) {
    int i = 0;
    PrintWriter writer = new PrintWriter(out);
    String systemId = docType.getSystemId();
    String publicId = docType.getPublicId();
    String internalSubset = docType.getInternalSubset();
    String rootTagName = docType.getOwnerDocument().getDocumentElement()
        .getTagName();
    if (systemId != null) {
      writer.write("<!DOCTYPE ");
      writer.write(rootTagName);
      if (publicId != null) {
        writer.write(" PUBLIC \"");
        writer.write(publicId + "\"\r\n");
        for (i = 0; i < 18 + rootTagName.length(); ++i)
          writer.write(" \"");
        writer.write(systemId);
        writer.write("\"");
      } else {
        writer.write(" SYSTEM \"");
        writer.write(systemId);
        writer.write("\"");
      }
      if (internalSubset != null && internalSubset.length() > 0) {
        writer.write(" [");
        writer.write(internalSubset);
        writer.write(']');
      }
      writer.write(">\r\n");
    } else if (internalSubset != null && internalSubset.length() > 0) {
      writer.write("<!DOCTYPE ");
      writer.write(rootTagName);
      writer.write(" [");
      writer.write(internalSubset);
      writer.write("]>\r\n");
    }
    writer.flush();    
  }
  
  private static DocumentBuilderFactory getDocumentBuilderFactory() {
    DocumentBuilderFactory documentBuilderFactory = null;
    if (documentBuilderFactoryClass != null) {
      try {
        return (DocumentBuilderFactory) documentBuilderFactoryClass
            .newInstance();
      } catch (Exception e) {
        // $JL-EXC$ go on
      }
    }

    try {
      documentBuilderFactoryClass = Class
          .forName("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
      documentBuilderFactory = (DocumentBuilderFactory) documentBuilderFactoryClass
          .newInstance();
    } catch (Exception e) {
      // $JL-EXC$
      try {
        documentBuilderFactoryClass = Class
            .forName("com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl");
        documentBuilderFactory = (DocumentBuilderFactory) documentBuilderFactoryClass
            .newInstance();
        sapXmlToolkitBuilder = true;
      } catch (Exception e1) {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        if (documentBuilderFactory != null) {
          documentBuilderFactoryClass = documentBuilderFactory.getClass();
        }
      }
    }
    if (AbstractConverter.LOCATION.beDebug()) {
      AbstractConverter.LOCATION.debugT("DocumentBuilderFactory implementation "
          + documentBuilderFactory.getClass());
    }
    return documentBuilderFactory;
  }
  
  private static void setAttributes(DocumentBuilderFactory factory,
      Map attributes) {
    Set entrySet = attributes.entrySet();
    for (Iterator iter = entrySet.iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      factory.setAttribute((String) entry.getKey(), entry.getValue());
    }
  }
  
  private static TransformerFactory getTransformerFactory() {
    TransformerFactory transformerFactory = null;
    
    if (transformerFactory == null) {
      // try JDK 1.5 xalan
      try {
        transformerFactory = (TransformerFactory) Class.forName("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl").newInstance();
      } catch (Exception e) {
        // $JL-EXC$ try next
      }
    }
    
    if (transformerFactory == null) {
      try {
        // try JDK 1.4 xalan
        transformerFactory = (TransformerFactory) Class.forName(
            "org.apache.xalan.processor.TransformerFactoryImpl").newInstance();
      } catch (Exception e) {
        // $JL-EXC$ try next
      }
    }
    
    if (transformerFactory == null) {
      // fallback to default transformer factory
      AbstractConverter.LOCATION.warningT("xalan not found, fallback to default transformer factory");
      transformerFactory = TransformerFactory.newInstance();
    }
    AbstractConverter.LOCATION.infoT("transformer factory implementation: " + transformerFactory.getClass().getName());
    
    try {
      transformerFactory.setAttribute("indent-number", new Integer(2));
    } catch (IllegalArgumentException iae) {
      //$JL-EXC$ ignore
      AbstractConverter.LOCATION.debugT("indent-number attribute not supported for factory " + transformerFactory.getClass().getName());
    }
    
     
    return transformerFactory;
  }
}
