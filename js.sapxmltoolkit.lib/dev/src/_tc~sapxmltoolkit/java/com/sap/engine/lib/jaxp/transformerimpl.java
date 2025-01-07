package com.sap.engine.lib.jaxp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

import com.sap.engine.lib.schema.components.Schema;
import com.sap.engine.lib.schema.components.SchemaComponentResult;
import com.sap.engine.lib.schema.components.impl.LoaderImpl;
import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.parser.*;
import com.sap.engine.lib.xml.parser.handlers.SAXDocHandler;
import com.sap.engine.lib.xml.util.DOMToDocHandler;
import com.sap.engine.lib.xml.util.DTMToDocHandler;
import com.sap.engine.lib.xml.util.NamespaceAppender;
import com.sap.engine.lib.xml.util.SAXToDocHandler;
import com.sap.engine.lib.xsl.xslt.XSLStylesheet;
import com.sap.engine.lib.xsl.xslt.output.DocHandlerSerializer;

/**
 * <p>
 * Implementation of the Transformer abstract class.
 *
 * <p>
 * This implementation supports Source/Result objects of the following classes:<br>
 *
 * <table border=1>
 *   <caption>Sources</caption>
 *   <tr><td>StreamSource<td>The most frequently used one, wraps a systemId and/or an InputStream and/or a Reader
 *   <tr><td>DOMSource<td>Wraps a DOM Node
 *   <tr><td>SAXSource<td>Wraps an XMLReader which should be used instead of the default xml parser
 *   <tr><td>MultiSource<td>Can only be used with SchemaComponentResult
 * </table>
 *
 * <table border=1>
 *   <caption>Results</caption>
 *   <tr><td>StreamResult<td>Wraps an OutputStream or a Writer
 *   <tr><td>DOMResult<td>Wraps a DOM Node, which usually is created at transform-time, but can also be provided by the user before that
 *   <tr><td>SAXResult<td>Wraps a SAX handler, whose events are fired
 *   <tr><td>DocHandlerResult<td>Similar to SAXResult, but uses the internal (more efficient) handler for the parser
 *   <tr><td>SchemaComponentResult<td>After the transformation the result object contains the "Schema Components API" view of the Source.
 *                                    The Source should represent an XML Schema document.
 * </table>
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version 1.0
 * @deprecated Please use standard JAXP api instead.
 */
@Deprecated
public final class TransformerImpl extends Transformer {

  private static final boolean ALLOW_RESTRICTED_XPATH_EXPRESSION_IN_THE_END_OF_URLS = true;
  static ResourceBundle res = ResourceBundle.getBundle("com.sap.engine.lib.jaxp.Res", new Locale("", ""));
  public static final Properties DEFAULT_OUTPUT_PROPERTIES = new Properties();
  public static final String TARGET_NS_ATTRIB_NAME = "targetNamespace";

  static {
    DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.METHOD, "");
    DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.VERSION, "1.0");
    DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.ENCODING, "UTF-8");
    DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.STANDALONE, "yes");
    DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.DOCTYPE_PUBLIC, "");
    DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.DOCTYPE_SYSTEM, "");
    DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "");
    DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.INDENT, "no");
    DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.MEDIA_TYPE, "text/xml");
  }

  private Properties outputProperties = new Properties(DEFAULT_OUTPUT_PROPERTIES);
  private Properties explProperties = new Properties();
  private Properties templProperties = new Properties();
  private ErrorListener errorListener = new EmptyErrorListener();
  private Hashtable parameters = new Hashtable();
  private URIResolver uriResolver = null;
  private XSLStylesheet sheet;
  // Reusable resources
  private XMLParser xmlParser;
  private DOMParser domParser; // wraps the XMLParser above
  private SAXParser saxParser; // wraps the XMLParser above
  private DocHandlerSerializer serializer = InstanceManager.getDocHandlerSerializer();
  private DocHandler nsa = new NamespaceAppender(serializer);
  private DOMToDocHandler domToDocHandler = new DOMToDocHandler();
  private DOMImplementation domImplementation = null;
  private InputSource inputSource = new InputSource();
  private String lastSystemIdProcessed = null;
  private boolean inUse = false;  
  private ParserAttributes attributes;  
  private EntityResolver entityResolver;

  /**
   * The parameter sheet need not be cloned before calling this constructor.
   * It is cloned here.
   */
  protected XMLParser getXMLParser() throws Exception {
    if (xmlParser == null) {
      try {
        xmlParser = new XMLParser();
        xmlParser.setFeature("http://xml.org/sax/features/namespaces", true);
        xmlParser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        
        domParser = new DOMParser(xmlParser);
        domParser.setEntityResolver(entityResolver);
        domParser.setAttributes(attributes);
        
        saxParser = new SAXParser(xmlParser);
				saxParser.setEntityResolver(entityResolver);
				saxParser.setAttributes(attributes);
      } catch (Exception e) {
        throw new TransformerConfigurationException(e);
      }
    }

    return xmlParser;
  }
  
  private Properties mergeProperties() {
    Properties p = (Properties)DEFAULT_OUTPUT_PROPERTIES.clone();
    Enumeration e = templProperties.keys();
    while (e.hasMoreElements()) {
      Object next = e.nextElement();
      p.setProperty((String) next, (String)templProperties.getProperty((String)next));
    }
    
    e = explProperties.keys();
    while (e.hasMoreElements()) {
      Object next = e.nextElement();
      p.setProperty((String) next, (String)explProperties.getProperty((String)next));
    }
    
    return p;
  }

  protected TransformerImpl(XSLStylesheet sheet, Properties outputProperties, URIResolver uriResolver, EntityResolver entityResolver, ParserAttributes attributes) throws TransformerConfigurationException {
		this.attributes = attributes;
		this.entityResolver = entityResolver;
    if (sheet == null) {
      return;
    }

    this.sheet = sheet; // may cause problems in multiple threads

    /*
     try {
     this.sheet = sheet.cloneSheet();
     } catch (XSLException e) {
     throw new TransformerConfigurationException(e);
     }
     */
     
    templProperties = (Properties)outputProperties.clone();
    this.outputProperties = mergeProperties();
//    if (outputProperties == null) {
//      this.outputProperties = new Properties(DEFAULT_OUTPUT_PROPERTIES);
//    } else {
//      this.outputProperties = (Properties) outputProperties.clone();
//    }

    setURIResolver(uriResolver); // Might be null
  }

  public void transform(Source source, Result result) throws TransformerException {
    if (tStack != null) {
      if (inUse) {
        throw new TransformerException("Transformer is inUse. You have probbably selected 'http://sap.com/java/xslt/transformer-reuse' option, and the transformer has still not finished computation");
      }
      inUse = true;
    }
    try {
  
      if (source == null) {
        throw new IllegalArgumentException(res.getString("XML_Source_is_null_"));
      }
  
      if (result == null) {
        throw new IllegalArgumentException(res.getString("Result_is_null_"));
      }
  
      if (result instanceof SchemaComponentResult) {
        SchemaComponentResult schemaCompResult = (SchemaComponentResult) result;
        try {
          result.setSystemId(source.getSystemId()); // this applies to all Sources and Results
          Source[] sources = null;
          if(source instanceof MultiSource) {
            sources = ((MultiSource)source).getSources();
          } else {
            sources = new Source[]{source};
          }
          LoaderImpl loader = new LoaderImpl();
          loader.setUriResolver(uriResolver);
          Schema schema = loader.load(sources);
          schemaCompResult.setSchema(schema);
          schemaCompResult.setSystemId(source.getSystemId());
          return;
        } catch (Exception sce) {
          throw new TransformerException(sce);
        }
      }
  
      try {
        if (sheet == null) {
          transformSimple(source, result);
        } else {
          transformWithStylesheet(source, result);
        }
      } catch (TransformerException e) {
  //      e.printStackTrace();
        //$JL-EXC$
        //according to API we must use errorListener

        errorListener.fatalError(e);
      } catch (Exception e) {
        //$JL-EXC$
        //according to API we must use errorListener
        errorListener.fatalError(new TransformerException(e));
      }
    } finally {
      if (tStack != null) {
        inUse = false;        
        tStack.push(this);
      }
    }
      
      
  }

  /**
   * Handles the case when sheet is null
   */
  private void transformSimple(Source source, Result result) throws Exception {
    DocHandler handler = getDocHandlerFor(result);
    invokeDocHandlerMethodsFor(source, handler);

    if (ALLOW_RESTRICTED_XPATH_EXPRESSION_IN_THE_END_OF_URLS) {
      considerXPointer(source, result);
    }
  }

  private void considerXPointer(Source source, Result result) {
    String s = source.getSystemId();

    if (s == null) {
      return;
    }

    int indexOfSharp = s.indexOf('#');

    if (indexOfSharp == -1) {
      return;
    }

    if (!(result instanceof DOMResult)) {
      return;
    }

    DOMResult domResult = (DOMResult) result;
    Node node = domResult.getNode();

    if (node == null) {
      return;
    }

    s = s.substring(indexOfSharp + 1);

    if (s.startsWith("xpointer(") && s.endsWith(")")) {
      s = s.substring("xpointer(".length(), s.length() - 1);
    }

    node = DOM.toNode(s, node);

    if (node == null) {
      return;
    }

    domResult.setNode(node);
  }

  /**
   * Handles the case when sheet is not null
   */
  private void transformWithStylesheet(Source source, Result result) throws Exception {
    if (source.getSystemId() != null) {
      sheet.setSourceBaseURI(source.getSystemId());
    }

    if (source instanceof StreamSource) {
      // StreamSource
      StreamSource s = (StreamSource) source;
      String systemId = s.getSystemId();
      InputStream inputStream = s.getInputStream();
      Reader reader = s.getReader();

      if (inputStream != null) {
        inputSource.setByteStream(inputStream);
      } else if (reader != null) {
        inputSource.setCharacterStream(reader);
      } else if (systemId != null) {
        inputSource.setSystemId(systemId);
      } else {
        throw new TransformerException("Could not load StreamSource: InputStream, Reader and SystemId are null");
      }

      if ((inputSource.getSystemId() != null) && !systemId.equals(lastSystemIdProcessed)) {
        getXMLParser().parse(systemId, sheet.getInputHandler(systemId));
        //lastSystemIdProcessed = systemId;
      } else if (inputSource.getSystemId() != null && systemId.equals(lastSystemIdProcessed)) {
        sheet.reuseLastSource();
      } else {
        DocHandler inputHandler = sheet.getInputHandler();
        try {
          getXMLParser().parse(inputSource, inputHandler);
        } catch (IOException ioe) {
          //$JL-EXC$
          //actually the exception is used
          errorListener.error(new TransformerException("IOException occurred while parsing stream. An empty document will be used!", ioe));
          inputHandler.startDocument();
          inputHandler.endDocument();
        }
      }
    } else if (source instanceof DOMSource) {
      // DOMSource
      DocHandler inputHandler = sheet.getInputHandler();
      Node node = ((DOMSource) source).getNode();

      if (node == null) {
        throw new TransformerException(res.getString("DOMSource_whose_Node"));
      }

      domToDocHandler.process(node, inputHandler);
    } else if (source instanceof SAXSource) {
      // SAXSource
      DocHandler inputHandler = sheet.getInputHandler();
      SAXSource s = (SAXSource) source;
      XMLReader xmlReader = s.getXMLReader();

      if (xmlReader == null) {
        getXMLParser(); //called to load saxParser
        xmlReader = saxParser;
      }

      InputSource inputSource = s.getInputSource();

      if (inputSource == null) {
        throw new TransformerException(res.getString("SAXSource_whose"));
      }

      SAXToDocHandler saxToDocHandler = new SAXToDocHandler(inputHandler);
      xmlReader.setContentHandler(saxToDocHandler);
      xmlReader.setDTDHandler(saxToDocHandler);
      xmlReader.setErrorHandler(saxToDocHandler);
      xmlReader.parse(inputSource);
    } else {
      throw new TransformerException(res.getString("Only_input_from"));
    }

    DocHandler handler = getDocHandlerFor(result);

    if (handler == null) {
      throw new TransformerException(res.getString("Unable_to_create_SAX"));
    }

    sheet.process(handler);
  }

  public void setOutputProperty(String s, String s1) throws IllegalArgumentException {
    if ((s == null) || (s1 == null) || (!DEFAULT_OUTPUT_PROPERTIES.containsKey(s))) {
      throw new IllegalArgumentException();
    }
    
    explProperties.setProperty(s, s1);
    outputProperties = mergeProperties();

    //outputProperties.setProperty(s, s1);
  }

  public Properties getOutputProperties() {
    return (Properties) outputProperties.clone();
  }

  public URIResolver getURIResolver() {
    return uriResolver;
  }

  public void setURIResolver(URIResolver uriResolver) {
    this.uriResolver = uriResolver;

    if (sheet != null) {
      sheet.setURIResolver((uriResolver == null) ? sheet.getDefaultResolver() : uriResolver);
    }
  }

  public void setOutputProperties(Properties properties) throws IllegalArgumentException {
    if (properties == null) {
      explProperties.clear();
      mergeProperties();
      return;
    }

    // Checking for validity of properties
    for (Enumeration e = properties.keys(); e.hasMoreElements();) {
      String a = "";
      try {
        a = (String) e.nextElement();
      } catch (ClassCastException ex) {
        //$JL-EXC$
        //according to API we must throw this exception

        throw new IllegalArgumentException("ClassCast exception:" + ex);
      }

      if (!DEFAULT_OUTPUT_PROPERTIES.containsKey(a)) {
        throw new IllegalArgumentException();
      }
    } 

    //outputProperties = (Properties) properties.clone();
    outputProperties.putAll(properties);
  }

  public void clearParameters() {
    parameters.clear();

    if (sheet != null) {
      sheet.clearParameters();
    }
  }

  public Object getParameter(String s) {
    return parameters.get(s);
  }

  public void setParameter(String s, Object obj) {
    parameters.put(s, obj);

    if (sheet != null) {
      sheet.setParameters(parameters);
    }
  }

  public ErrorListener getErrorListener() {
    return errorListener;
  }

  public void setErrorListener(ErrorListener errorListener) throws IllegalArgumentException {
    if (errorListener == null) {
      throw new IllegalArgumentException();
    }

    this.errorListener = errorListener;

    if (sheet != null) {
      sheet.setErrorListener(errorListener);
    }
  }

  public String getOutputProperty(String s) throws IllegalArgumentException {
    if (s == null) {
      throw new IllegalArgumentException();
    }

    String r = outputProperties.getProperty(s);

    if (r == null) {
      throw new IllegalArgumentException();
    }

    return r;
  }

  private DocHandler getDocHandlerFor(Result result) throws Exception {
    DocHandler handler = null;

    if (result instanceof StreamResult) {
      // StreamResult
      StreamResult r = (StreamResult) result;
      serializer.setOutputProperties(outputProperties);
      if (r.getOutputStream() != null) {
        serializer.setOutputStream(r.getOutputStream());
        serializer.setCloseOnEnd(false);
      } else if (r.getWriter() != null) {
        serializer.setWriter(r.getWriter());
        serializer.setCloseOnEnd(false);
      } else if (r.getSystemId() != null) {
        String systemId = r.getSystemId();
        if (systemId.startsWith("file:")) {
          int slashCount = 0;
          int protocolLength = "file:".length();
          int len = systemId.length();        
          while (systemId.charAt(protocolLength + slashCount) == '/') {
            slashCount++;
            if (protocolLength + slashCount >= len) {
              throw new TransformerException("Cannot create file: " + systemId);
            }
          }
          systemId = systemId.substring(protocolLength + slashCount);
          if (slashCount % 2 == 0) {
            systemId = "\\\\" + systemId;
          } else if (systemId.charAt(1) != ':') {
            systemId = "/" + systemId;
          }
        }
        OutputStream out;
//        
//        try {
//          URL url = new URL(systemId);
//          URLConnection connection = url.openConnection();
//          connection.setDoOutput(true);
//          LogWriter.getSystemLogWriter().println("File: " + url.getFile()); 
//          out = connection.getOutputStream();
//        } catch (MalformedURLException mfue) {
//          //this is a file
          File dir = new File(systemId).getParentFile();
          if (dir != null) {
            dir.mkdirs();
          }
          out = new FileOutputStream(systemId);
//        }

        serializer.setOutputStream(out);
        serializer.setCloseOnEnd(true);
      } else {
        throw new TransformerException(res.getString("StreamResult_whose"));
      }
      
      handler = nsa;
    } else if (result instanceof SAXResult) {
      // SAXResult
      SAXResult r = (SAXResult) result;
      ContentHandler contentHandler = r.getHandler();
      LexicalHandler lexicalHandler = r.getLexicalHandler();
      SAXDocHandler sdh = new SAXDocHandler();

      if (contentHandler != null) {
        sdh.setContentHandler(contentHandler);
      }

      if (lexicalHandler != null) {
        sdh.setLexicalHandler(lexicalHandler);
      }

      handler = sdh;
    } else if (result instanceof DOMResult) {
      // DocHandlerResult
      DOMResult dr = (DOMResult) result;

      if (dr.getNode() == null) {
        ensureDOMImplementation();
        dr.setNode(domImplementation.createDocument(null, null, null));
      }

      com.sap.engine.lib.xml.dom.DOMDocHandler1 ddh = new com.sap.engine.lib.xml.dom.DOMDocHandler1(dr.getNode());
      handler = ddh;
    } else if (result instanceof DocHandlerResult) {
      return ((DocHandlerResult) result).getDocHandler();
    } else {
      throw new TransformerException(res.getString("Only_output_to"));
    }

    return handler;
  }

  private void invokeDocHandlerMethodsFor(Source source, DocHandler handler) throws Exception {
    if (source instanceof StreamSource) {
      // StreamSource
      StreamSource s = (StreamSource) source;
      String systemId = s.getSystemId();

      if (systemId != null) {
        getXMLParser().parse(systemId, handler);
      } else {
        InputStream inputStream = s.getInputStream();

        if (inputStream != null) {
          getXMLParser().parse(inputStream, handler);
        } else {
          Reader reader = s.getReader();

          if (reader != null) {
            getXMLParser().parse(reader, handler);
          } else {
            throw new TransformerException(res.getString("StreamSource_whose"));
          }
        }
      }
    } else if (source instanceof SAXSource) {
      // SAXSource
      SAXSource s = (SAXSource) source;
      XMLReader xmlReader = s.getXMLReader();

      if (xmlReader == null) {
        getXMLParser(); //called to load saxParser
        xmlReader = saxParser;
      }

      InputSource inputSource = s.getInputSource();

      if (inputSource == null) {
        throw new TransformerException(res.getString("SAXSource_whose"));
      }

      setHandlerOnXMLReader(new SAXToDocHandler(handler), xmlReader);
      xmlReader.parse(inputSource);
    } else if (source instanceof DOMSource) {
      // DOMSource
      Node node = ((DOMSource) source).getNode();

      if (node == null) {
        throw new TransformerException(res.getString("DOMSource_whose_Node"));
      }

      domToDocHandler.process(node, handler);
    } else if (source instanceof DTMSource) {
      // DTMSource
      (new DTMToDocHandler()).process(((DTMSource) source).getDTM(), handler);
    } else {
      throw new TransformerException(res.getString("Only_input_from"));
    }
  }

  private void setHandlerOnXMLReader(Object handler, XMLReader xmlReader) throws SAXNotSupportedException, SAXNotRecognizedException {
    if (handler instanceof ContentHandler) {
      xmlReader.setContentHandler((ContentHandler) handler);
    }

    /*
     if (handler instanceof DocumentHandler) {
     xmlReader.setDocumentHandler((DocumentHandler) handler);
     }
     */
    if (handler instanceof DTDHandler) {
      xmlReader.setDTDHandler((DTDHandler) handler);
    }

//    if (handler instanceof EntityResolver) {
//      xmlReader.setEntityResolver((EntityResolver) handler);
//    }
//
//    if (handler instanceof ErrorHandler) {
//      xmlReader.setErrorHandler((ErrorHandler) handler);
//    }

    if (handler instanceof DeclHandler) {
      xmlReader.setProperty("http://xml.org/sax/properties/declaration-handler", handler);
    }

    if (handler instanceof LexicalHandler) {
      xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
    }

    /*
     if (handler instanceof DocHandler) {
     setHandlerOnXMLReader(new SAXToDocHandler(handler), xmlReader);
     }
     */
  }

  private void ensureDOMImplementation() {
    if (domImplementation == null) {
      domImplementation = new com.sap.engine.lib.xml.dom.DOMImplementationImpl();
    }
  }
  
  private Stack tStack; 
  public void setPool(Stack p) {
    tStack = p;
  }  
  
  public void setAttribute(String key, Object value) throws Exception {
  	attributes.set(key, value);
  }
  
  public Object getAttribute(String name) {
  	return(attributes.get(name));
  }
  
	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}
	
	public EntityResolver getEntityResolver() {
		return(entityResolver);
	}
}



