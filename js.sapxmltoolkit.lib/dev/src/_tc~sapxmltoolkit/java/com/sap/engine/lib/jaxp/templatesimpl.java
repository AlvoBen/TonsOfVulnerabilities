package com.sap.engine.lib.jaxp;

import java.util.*;
import java.util.Properties;
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;
import com.sap.engine.lib.xsl.xslt.*;
import com.sap.engine.lib.xsl.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import com.sap.engine.lib.xml.parser.*;
import com.sap.engine.lib.xml.util.*;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public final class TemplatesImpl implements Templates {

  static ResourceBundle res = ResourceBundle.getBundle("com.sap.engine.lib.jaxp.Res", new Locale("", ""));
  private XSLStylesheet sheet;
  private Properties outputProperties = new Properties(TransformerImpl.DEFAULT_OUTPUT_PROPERTIES);
  private Properties explProperties = new Properties();
  private Properties templProperties = new Properties();  
  private DOMSource stylesheetDomSource = null;
  private URIResolver stylesheetUriResolver = null;
  private ClassLoader extensionClassLoader = null;
  private Stack tStack = null;
  private TransformerImpl cachedTransformer = null;  
  private boolean bTransformerReuse = false;
  private EntityResolver entityResolver;
  private ParserAttributes attributes;

  protected void setExtensionClassLoader(ClassLoader extensionClassLoader) {
    this.extensionClassLoader = extensionClassLoader;
  }

  protected TemplatesImpl(Source source, URIResolver uriResolver, EntityResolver entityResolver, ParserAttributes attributes) throws TransformerConfigurationException {
    if (source == null) {
      throw new IllegalArgumentException(res.getString("XSL_Source_is_null_"));
    }

    try {
      //sheet = InstanceManager.getXSLStylesheet();
      stylesheetUriResolver = uriResolver;

      this.entityResolver = entityResolver;
			this.attributes = attributes;
			
      //sheet = InstanceManager.getXSLStylesheet();
      /*
       DOMResult temp = new DOMResult();
       Transformer t0 = new TransformerImpl(null, outputProperties, uriResolver);
       t0.transform(source, temp);
       Node node = temp.getNode();
       if (node == null) {
       throw new TransformerConfigurationException("Unable to produce a DOM tree out of the XSL source");
       }
       sheet.init(node, uriResolver);
       */
      if (source instanceof DOMSource) {
        // DOMSource
        //stylesheetDocument = ((DOMSource)source).getNode();
        //        LogWriter.getSystemLogWriter().println("TemplatesImpl.<init> sheet is dom: " + ((DOMSource)source).getNode());
        stylesheetDomSource = new DOMSource(((DOMSource) source).getNode(), source.getSystemId());
        //sheet.init(((DOMSource) source).getNode(), uriResolver);
      } else if (source instanceof SAXSource) {
        // SAXSource
        SAXSource s = (SAXSource) source;
        XMLReader xmlReader = s.getXMLReader();

        if (xmlReader == null) {
          xmlReader = new SAXParser(); // Use our SAXParser as a default
        }

        InputSource inputSource = s.getInputSource();

        if (inputSource == null) {
          throw new TransformerException(res.getString("SAXSource_whose"));
        }

        SAXToDOMHandler handler = new SAXToDOMHandler();
        xmlReader.setContentHandler((ContentHandler) handler);
        xmlReader.setDTDHandler((DTDHandler) handler);
        xmlReader.setEntityResolver((EntityResolver) handler);
        xmlReader.setErrorHandler((ErrorHandler) handler);
        xmlReader.setProperty("http://xml.org/sax/properties/declaration-handler", handler);
        xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        xmlReader.parse(inputSource);
        stylesheetDomSource = new DOMSource(handler.getRoot(), s.getSystemId());
        //sheet.init(domSource, uriResolver, 0);
      } else if (source instanceof StreamSource) {
        // StreamSource
        InputStream inputStream = ((StreamSource) source).getInputStream();
        Reader reader = ((StreamSource) source).getReader();
        String id = ((StreamSource) source).getSystemId();
        //        LogWriter.getSystemLogWriter().println("TemplatesInmpl.<init>: systemId = " + id);
        //        LogWriter.getSystemLogWriter().println("TemplatesImpl.<init> sheet is stream: id=" + id + ", inputStream="  + inputStream  + ", reader=" + reader);
        boolean hasId = false;
        InputSource inputSource;

        if (inputStream != null) {
          inputSource = new InputSource(inputStream);
        } else if (reader != null) {
          inputSource = new InputSource(reader);
        } else if (id != null) {
          inputSource = new InputSource(id);
          hasId = true;
        } else {
          throw new TransformerException(res.getString("StreamSource_whose"));
        }

        DOMParser domParser = new DOMParser();
        domParser.setFeature("http://inqmy.org/dom/features/trim-white-spaces", false);
        domParser.setFeature("http://xml.org/sax/features/namespaces", true);
        domParser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        
        domParser.setAttributes(attributes);
        domParser.setEntityResolver(entityResolver);
        
        Document document = domParser.parse(inputSource);
        stylesheetDomSource = new DOMSource(document, source.getSystemId());
        //sheet.init(new DOMSource(document, source.getSystemId()), uriResolver, 0);
        //        sheet.init(document, uriResolver, id);
        //        if (hasId) {
        //          sheet.init(document, id);
        //        } else {
        //          sheet.init(document);
        //        }
      } else {
        throw new TransformerException(res.getString("Only_output_to"));
      }
    } catch (XSLException e) {
//      e.printStackTrace();
      throw new TransformerConfigurationException("Could not load stylesheet. " + e, e);
    } catch (Exception e) {
//      e.printStackTrace();
      throw new TransformerConfigurationException("Could not load stylesheet. " + e, e);
    }

  if (stylesheetDomSource != null && sheet == null) {
    NodeList nl = ((Document) stylesheetDomSource.getNode()).getElementsByTagNameNS(XSLStylesheet.sXSLNamespace, "output");
    String localName = ((Document) stylesheetDomSource.getNode()).getDocumentElement().getLocalName();
    

    if (nl.getLength() > 0) {
      String methodValue = ((Element) nl.item(0)).getAttribute("method");
      if (methodValue.equals("html")) {
        setNonNullOutputProperty(explProperties, OutputKeys.MEDIA_TYPE, "text/html");
        setNonNullOutputProperty(explProperties, OutputKeys.VERSION, "4.0");
        setNonNullOutputProperty(explProperties, OutputKeys.INDENT, "yes");
      } else if (methodValue.equals("text")) {
        setNonNullOutputProperty(explProperties, OutputKeys.MEDIA_TYPE, "text/plain");
      }
      if (!"".equals(((Element) nl.item(0)).getAttribute("method"))) {
        setNonNullOutputProperty(explProperties, OutputKeys.METHOD, ((Element) nl.item(0)).getAttribute("method"));
      }
    } else if (localName.equalsIgnoreCase("html")) {
      setNonNullOutputProperty(explProperties, OutputKeys.METHOD, "html");
      setNonNullOutputProperty(explProperties, OutputKeys.MEDIA_TYPE, "text/html");
      setNonNullOutputProperty(explProperties, OutputKeys.VERSION, "4.0");
      setNonNullOutputProperty(explProperties, OutputKeys.INDENT, "yes");
    }
  }
  
  outputProperties = mergeProperties();

    //    setOutputProperties(sheet.getOutputProperties());
    //    if (!sheet.isIndentAllowed()) {
    //      outputProperties.setProperty(OutputKeys.INDENT, "no");
    //    }
  }

  public synchronized Transformer newTransformer() throws TransformerConfigurationException {
    try {
      if (bTransformerReuse && !tStack.empty()) {
        return (Transformer)tStack.pop();
      }      
      sheet = InstanceManager.getXSLStylesheet();
      sheet.setExtClassLoader(extensionClassLoader);
      sheet.init(stylesheetDomSource, stylesheetUriResolver, 0);
      setStylesheetOutputProperties(sheet.getOutputProperties());
      outputProperties = mergeProperties();

      if (!sheet.isIndentAllowed()) {
        outputProperties.setProperty(OutputKeys.INDENT, "no");
      }
      
      if (bTransformerReuse) {
        cachedTransformer = new TransformerImpl(sheet, getOutputProperties(), stylesheetUriResolver, entityResolver, attributes);
        cachedTransformer.setPool(tStack);
        
        return cachedTransformer;
      } else {
        return new TransformerImpl(sheet, getOutputProperties(), stylesheetUriResolver, entityResolver, attributes);
      }
      //return new TransformerImpl(sheet, getOutputProperties(), stylesheetUriResolver);
    } catch (XSLException e) {
//      e.printStackTrace();
      throw new TransformerConfigurationException("Could not load stylesheet." + e, e);
    } finally {
      sheet = null;
    }
  }

  public synchronized Properties getOutputProperties() {
    return (Properties)outputProperties.clone();
//    if (stylesheetDomSource != null && sheet == null) {
//      NodeList nl = ((Document) stylesheetDomSource.getNode()).getElementsByTagNameNS(XSLStylesheet.sXSLNamespace, "output");
//
//      if (nl.getLength() > 0) {
//        String methodValue = ((Element) nl.item(0)).getAttribute("method");
//        if (methodValue.equals("html")) {
//          setNonNullOutputProperty(OutputKeys.MEDIA_TYPE, "text/html");
//          setNonNullOutputProperty(OutputKeys.VERSION, "4.0");
//          setNonNullOutputProperty(OutputKeys.INDENT, "yes");
//        } else if (methodValue.equals("text")) {
//          setNonNullOutputProperty(OutputKeys.MEDIA_TYPE, "text/plain");
//        }
//        if (!"".equals(((Element) nl.item(0)).getAttribute("method"))) {
//          setNonNullOutputProperty(OutputKeys.METHOD, ((Element) nl.item(0)).getAttribute("method"));
//        }
//      }
//    }
//
//    return (Properties) outputProperties.clone();
  }

  private void setStylesheetOutputProperties(XSLOutputNode o) {
    outputProperties.clear();

    if (o == null) {
      return;
    }

    setNonNullOutputProperty(templProperties, OutputKeys.CDATA_SECTION_ELEMENTS, o.getCdataSectionElements());
    setNonNullOutputProperty(templProperties, OutputKeys.DOCTYPE_PUBLIC, o.getDoctypePublic());
    setNonNullOutputProperty(templProperties, OutputKeys.DOCTYPE_SYSTEM, o.getDoctypeSystem());
    setNonNullOutputProperty(templProperties, OutputKeys.ENCODING, o.getEncoding());
    setNonNullOutputProperty(templProperties, OutputKeys.INDENT, o.getIndent());
    setNonNullOutputProperty(templProperties, OutputKeys.MEDIA_TYPE, o.getMediaType());
    setNonNullOutputProperty(templProperties, OutputKeys.METHOD, o.getMethod());
    setNonNullOutputProperty(templProperties, OutputKeys.OMIT_XML_DECLARATION, o.getOmitXmlDeclaration());
    setNonNullOutputProperty(templProperties, OutputKeys.STANDALONE, o.getStandalone());
    setNonNullOutputProperty(templProperties, OutputKeys.VERSION, o.getVersion());
    outputProperties = mergeProperties();
    
    if (outputProperties.get(OutputKeys.METHOD).equals("html")) {
      explProperties.setProperty(OutputKeys.MEDIA_TYPE, "text/html");
      explProperties.setProperty(OutputKeys.VERSION, "4.0");
      explProperties.setProperty(OutputKeys.INDENT, "yes");
    } else if (outputProperties.get(OutputKeys.METHOD).equals("text")) {
      explProperties.setProperty(OutputKeys.MEDIA_TYPE, "text/plain");
    } else if (outputProperties.get(OutputKeys.METHOD).equals("xml")) {
      explProperties.setProperty(OutputKeys.MEDIA_TYPE, "text/xml");
    }
    
    outputProperties = mergeProperties();
    
  }

  private void setNonNullOutputProperty(Properties p, String key, String value) {
    if (value != null && value.length() > 0) {
      p.setProperty(key, value);
    }
  }
  
  public void setTransformerReuse(boolean value) {
    bTransformerReuse = value;
    if (value = true && tStack == null) {
      tStack = new Stack();
    }
  }  

  private Properties mergeProperties() {
    Properties p = (Properties)TransformerImpl.DEFAULT_OUTPUT_PROPERTIES.clone();
    Enumeration e = templProperties.keys();
    while (e.hasMoreElements()) {
      Object next = e.nextElement();
      p.setProperty((String) next, (String)templProperties.getProperty((String)next));
    }
    
    e = explProperties.keys();
    while (e.hasMoreElements()) {
      Object next = e.nextElement();
      p.put((String) next, (String)explProperties.getProperty((String)next));
    }
    
    return p;
  }

	public void setAttribute(String key, Object value) throws Exception {
		attributes.set(key, value);
	}
  
	public Object getAttribute(String key, boolean value) throws Exception {
		return(attributes.get(key));
	}
	
	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}
	
	public EntityResolver getEntityResolver() {
		return(entityResolver);
	}
}

