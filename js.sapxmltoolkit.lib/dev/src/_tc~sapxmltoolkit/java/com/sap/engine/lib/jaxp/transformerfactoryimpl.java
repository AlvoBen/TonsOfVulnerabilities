package com.sap.engine.lib.jaxp;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.EntityResolver;
import org.xml.sax.XMLFilter;

import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.SAXFilter;
import com.sap.engine.lib.xsl.xslt.XSLStylesheet;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 * @deprecated Please use standard JAXP api instead.
 */
@Deprecated
public class TransformerFactoryImpl extends SAXTransformerFactory {

  private static final HashSet SUPPORTED_FEATURES = new HashSet();

  static {
    SUPPORTED_FEATURES.add(SAXSource.FEATURE);
    SUPPORTED_FEATURES.add(SAXResult.FEATURE);
    SUPPORTED_FEATURES.add(DOMSource.FEATURE);
    SUPPORTED_FEATURES.add(DOMResult.FEATURE);
    SUPPORTED_FEATURES.add(StreamSource.FEATURE);
    SUPPORTED_FEATURES.add(StreamResult.FEATURE);
    SUPPORTED_FEATURES.add(SAXTransformerFactory.FEATURE);
    SUPPORTED_FEATURES.add(SAXTransformerFactory.FEATURE_XMLFILTER);
  }

  private URIResolver uriResolver = null;
  private ErrorListener errorListener = new EmptyErrorListener();
  public static final String EXT_CLASSLOADER = "http://sap.com/java/xslt/ext-classloader";
  public static final String TRANSFORMER_REUSE = "http://sap.com/java/xslt/transformer-reuse";
  private boolean bTransformerReuse = false;
  private EntityResolver entityResolver;
  private Hashtable factoryAttributes = new Hashtable();
  private ParserAttributes attributes = new ParserAttributes();

  public TransformerFactoryImpl() {
  }

  public Templates newTemplates(Source source) throws javax.xml.transform.TransformerConfigurationException {
    TemplatesImpl tImpl = new TemplatesImpl(source, uriResolver, entityResolver, attributes);
    tImpl.setExtensionClassLoader((ClassLoader) getAttribute(EXT_CLASSLOADER));
    tImpl.setTransformerReuse(bTransformerReuse);
//    try {
//      tImpl.setExtensionClassLoader((ClassLoader) getAttribute(EXT_CLASSLOADER));
//    } catch (IllegalArgumentException iae) {
//      tImpl.setExtensionClassLoader(null);
//    }
    return tImpl;
    //return new TemplatesImpl(source, uriResolver);
  }

  public Transformer newTransformer(Source source) throws javax.xml.transform.TransformerConfigurationException {
    TemplatesImpl tImpl = new TemplatesImpl(source, uriResolver, entityResolver, attributes);
    tImpl.setExtensionClassLoader((ClassLoader) getAttribute(EXT_CLASSLOADER));
    TransformerImpl transformer = (TransformerImpl)(tImpl.newTransformer());
    return(transformer);
    //return (new TemplatesImpl(source, uriResolver)).newTransformer();
  }

  public Transformer newTransformer() throws javax.xml.transform.TransformerConfigurationException {
    TransformerImpl transformer = new TransformerImpl((XSLStylesheet) null, (Properties) null, uriResolver, entityResolver, attributes);
    return(transformer);
  }

  public Source getAssociatedStylesheet(Source source, String media, String title, String charset) throws TransformerConfigurationException {
    AssociationFindingDocHandler handler = new AssociationFindingDocHandler();
    Result temp = new DocHandlerResult(handler.init(media, title, charset));
    try {
      newTransformer().transform(source, temp);
    } catch (Exception _) {
      //$JL-EXC$

      return handler.getAssociation();
    }
    return null;
  }

  public boolean getFeature(String s) {
    return SUPPORTED_FEATURES.contains(s);
  }

  public Object getAttribute(String name) {
    if (name != null && isFactorySpecificAttribute(name)) {
			if (name.equals(TRANSFORMER_REUSE)) {
				return(new Boolean(bTransformerReuse));
    }
			return(factoryAttributes.get(name));
    }
    return(attributes.get(name));
  }

  public URIResolver getURIResolver() {
    return uriResolver;
  }

  public void setURIResolver(URIResolver r) {
    uriResolver = r;
  }

  public void setAttribute(String name, Object value) throws java.lang.IllegalArgumentException {
  	if(name != null && isFactorySpecificAttribute(name)) {
			if (name.equals(TRANSFORMER_REUSE)) {
				bTransformerReuse = Boolean.TRUE.equals(value); 
			} else {
				if(value == null) {
					factoryAttributes.remove(name);
	  } else {
					factoryAttributes.put(name, value);
	  }
    }
  	} else {
  		attributes.set(name, value);
  	}
  }

  public ErrorListener getErrorListener() {
    return errorListener;
  }

  public void setErrorListener(ErrorListener errorListener) throws java.lang.IllegalArgumentException {
    if (errorListener == null) {
      throw new IllegalArgumentException();
    }

    this.errorListener = errorListener;
  }

  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result, based on the transformation
   * instructions specified by the argument.
   *
   * @param src The Source of the transformation instructions.
   *
   * @return TransformerHandler ready to transform SAX events.
   *
   * @throws TransformerConfigurationException If for some reason the
   * TransformerHandler can not be created.
   */
  public TransformerHandler newTransformerHandler(Source src) throws TransformerConfigurationException {
    return new TransformerHandlerImpl(TransformerFactory.newInstance().newTemplates(src));
  }

  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result, based on the Templates argument.
   *
   * @param templates The compiled transformation instructions.
   *
   * @return TransformerHandler ready to transform SAX events.
   *
   * @throws TransformerConfigurationException If for some reason the
   * TransformerHandler can not be created.
   */
  public TransformerHandler newTransformerHandler(Templates templates) throws TransformerConfigurationException {
    return new TransformerHandlerImpl(templates);
  }

  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result. The transformation
   * is defined as an identity (or copy) transformation, for example
   * to copy a series of SAX parse events into a DOM tree.
   *
   * @return A non-null reference to a TransformerHandler, that may
   * be used as a ContentHandler for SAX parse events.
   *
   * @throws TransformerConfigurationException If for some reason the
   * TransformerHandler cannot be created.
   */
  public TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
    return new TransformerHandlerImpl();
  }

  /**
   * Get a TemplatesHandler object that can process SAX
   * ContentHandler events into a Templates object.
   *
   * @return A non-null reference to a TransformerHandler, that may
   * be used as a ContentHandler for SAX parse events.
   *
   * @throws TransformerConfigurationException If for some reason the
   * TemplatesHandler cannot be created.
   */
  public TemplatesHandler newTemplatesHandler() throws TransformerConfigurationException {
    return new TemplatesHandlerImpl();
  }

  /**
   * Create an XMLFilter that uses the given Source as the
   * transformation instructions.
   *
   * @param src The Source of the transformation instructions.
   *
   * @return An XMLFilter object, or null if this feature is not supported.
   *
   * @throws TransformerConfigurationException If for some reason the
   * TemplatesHandler cannot be created.
   */
  public XMLFilter newXMLFilter(Source src) throws TransformerConfigurationException {
    Templates templates = newTemplates(src);
    return newXMLFilter(templates);
  }

  /**
   * Create an XMLFilter, based on the Templates argument..
   *
   * @param templates The compiled transformation instructions.
   *
   * @return An XMLFilter object, or null if this feature is not supported.
   *
   * @throws TransformerConfigurationException If for some reason the
   * TemplatesHandler cannot be created.
   */
  public XMLFilter newXMLFilter(Templates templates) throws TransformerConfigurationException {
    try {
      return new SAXFilter(templates);
    } catch (ParserException e) {
      throw new TransformerConfigurationException("Cannot create XMLFilter: " + e, e);
    }
  }

	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	public EntityResolver getEntityResolver() {
		return(entityResolver);
    }
	
  private boolean isFactorySpecificAttribute(String attribute) {
  	return(attribute.equals(EXT_CLASSLOADER) || attribute.equals(TRANSFORMER_REUSE) || SUPPORTED_FEATURES.contains(attribute));
  }
  
  public void setFeature(String name, boolean value) throws TransformerConfigurationException{
  	throw new NullPointerException("Not implemented!");
  }
  
}

