package com.sap.engine.lib.xml.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.sap.engine.lib.xml.SystemProperties;
import com.sap.engine.lib.xml.parser.dtd.XMLValidator;
import com.sap.engine.lib.xml.parser.handlers.INamespaceHandler;
import com.sap.engine.lib.xml.parser.handlers.NamespaceHandlerEx;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.IXMLStream;
import com.sap.engine.lib.xml.util.ReaderInputStream;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Vladimir Savchenko
 * @version      Apr 2001
 */

public abstract class AbstractXMLParser implements Locator {
  private static final String HTTP_PROXY_HOST = "http.proxyHost";
  private static final String HTTP_PROXY_PORT = "http.proxyPort";

  // Validator
  protected XMLValidator xmlValidator = new XMLValidator(this);

  //Locator
  protected String systemId = null;
  protected String publicId = null;

  // Input
  protected IXMLStream is = null; // A stack of readers
  public URLLoaderBase urlLoader = new URLLoaderBase();

  // Supported features
  protected boolean bValidation = false;
  protected boolean bDynamicValidation = false;
  protected boolean bNamespaces = true;
  protected boolean bWellformed = true;
  protected boolean bNamespacePrefixes = true;
  protected boolean alternativeDTD = false;
  protected boolean bDOMTrimWhiteSpaces = false;
  protected INamespaceHandler namespaceHandler = null;
  protected boolean bCloseStreams = true;


  protected boolean bUseAlternativeDTD = false;
  protected CharArray caAlternativeDTD = new CharArray();
  protected boolean bScanContentOnly = false;
  protected boolean bActiveParse = false;

  protected boolean bExpandingReferences = true;

  protected boolean bHTMLMode = false;
  protected HashSet HTML_OPEN_TAGS = null;
	protected boolean bExternalGeneralEntities = true;
  protected boolean bReadDTD = true;
  private InputSource additionalDTDLocation = null;
  protected boolean initializeNamespaceHandler = true;
  protected boolean backwardsCompatibilityMode = false;
  protected int maximumReferencesCount = 10000;
  
  public static int MAX_DEPTH = 512;
  
  protected boolean isNamespaceReplacing = false;
  protected Hashtable namespaceReplacements = null;

  //protected String isSystemId = null;

  /*
   * The specific settings of the parser can be configured through one of the following ways:
   *   (1) Check the system property 'xmlparser.properties-file', and if it is
   *       mapped to a valid .properties filename, load from it.
   *       This is always done when constructing a parser, and can be forced by
   *       the loadProperties() method.
   *   (2) Usage of setProperty(String, String), setProperties(JAXPProperties), or
   *       setFeature(String, boolean) either overrides the respective property,
   *       or throws a SAX-compatible exception.
   *   (3) Direct usage of methods like setValidation(boolean), setCacheDir(String), ..
   *       override the respective functionality. Nevertheless, they do not affect
   *       the JAXPProperties object associated to the parser!
   *
   */
  //private JAXPProperties properties = new JAXPProperties();


  public AbstractXMLParser() {
    try {
      loadProperties();
    } catch (ParserException e) {
      //$JL-EXC$
      // Ignore not significant
    }
  }

  protected abstract void parse0(InputStream inputStream, String systemId, DocHandler docHandler) throws Exception;
  public abstract void init() throws ParserException;

//  public abstract void onEncodedDataReaderEOF(EncodedDataReader x) throws Exception;

  public abstract void onDocumentEOF() throws ParserException;

  public final void activeParse(InputSource source, DocHandler handler) throws Exception {
    setActiveParse(true);
    parse(source, handler);
  }

  public void finalizeActiveParse() {
	  if (!bSoapProcessing)  {
	    urlLoader.init();
	  }
      
    if (((XMLParser)this).fs_is != null) {
      try {
        ((XMLParser)this).fs_is.close();
        ((XMLParser)this).fs_is = null;
      } catch (Exception e) {
        //$JL-EXC$
      }
    }
    setActiveParse(false);
  }

  private final void parseAndCatchException(InputStream inputStream, String sourceId, DocHandler docHandler) throws Exception {
    try {
      parse0(inputStream, sourceId, docHandler);
    } finally {
      if (!((XMLParser)this).getActiveParse() && ((XMLParser)this).fs_is != null) {
        
        ((XMLParser)this).fs_is.close();
        ((XMLParser)this).fs_is =  null;
      }
      if (!bSoapProcessing) {
        urlLoader.init();
      }
    }
  }

  public final void parse(InputStream inputStream, String systemId, DocHandler docHandler) throws Exception {
    //urlLoader.push(null);
    parseAndCatchException(inputStream, systemId, docHandler);
  }

  public final void parse(Reader reader, String systemId, DocHandler docHandler) throws Exception {
    //urlLoader.push(null);
    parseAndCatchException(new ReaderInputStream(reader), systemId, docHandler);
  }

  public final void parse(InputStream inputStream, DocHandler docHandler) throws Exception {
    //urlLoader.push(null);
    parseAndCatchException(inputStream, ":main:", docHandler);
  }

  public final void parse(Reader reader, DocHandler docHandler) throws Exception {
    //urlLoader.push(null);
    parseAndCatchException(new ReaderInputStream(reader), ":main:", docHandler);
  }

  public final void parse(String s, DocHandler docHandler) throws Exception {
    InputStream is = null;
    ZipFile zip = null; 
    try {
      s.replace('\\', '/');
      URL u = urlLoader.loadAndPush(s);
      if (u.getProtocol().equals("jar")) {
        String spec = u.getFile();
        int separator = spec.indexOf('!');
        if (separator == -1) {
          throw new MalformedURLException("no ! found in url spec:" + spec);
        }
        URL jarFileURL = new URL(spec.substring(0, separator++));
        if (jarFileURL.getProtocol().equals("file")) {
          File jarFile = URLLoaderBase.fileURLToFile(jarFileURL);
          zip = new ZipFile(jarFile);
          String entryName = spec.substring(separator + 1, spec.length());
          ZipEntry zipEntry = zip.getEntry(entryName);
          is = zip.getInputStream(zipEntry);
        } else {
          is = jarFileURL.openStream();
        }
      } else {
        is = u.openStream();
      }
      docHandler.onCustomEvent(DocHandler.DOCUMENT_URL, urlLoader.peek());
      parseAndCatchException(is, s, docHandler);
    } finally {
      if (!bActiveParse) {
        if (is != null) {
          is.close();
        }
        if (zip != null) {
          zip.close();
        }
      }
    }
  }

  /**
   * Redirects to one of the three methods above.
   */

  public static int id = 0;

  public final void parse(InputSource input, DocHandler handler) throws Exception {
    Reader reader           = input.getCharacterStream();
    InputStream inputStream = input.getByteStream();

//    String systemId         = input.getSystemId();
    systemId         = input.getSystemId();
    publicId         = input.getPublicId();

    if (reader != null) {
      //isSystemId = systemId;
      if (systemId != null && systemId.length() > 0) {
      	try {
        	urlLoader.push(new URL(systemId));
      	} catch (Exception e) {
          //$JL-EXC$
          // this is a fallback in case of an exception
      		urlLoader.push(new URL("http://localhost/"));
      	}
      }
      if (systemId == null) {
      	systemId = ":main:";
      }
      parse(reader, systemId, handler);
    } else if (inputStream != null) {
      if (systemId != null && systemId.length() > 0) {
      	try {
        	urlLoader.push(new URL(systemId));
      	} catch (Exception e) {
          //$JL-EXC$
          // a fallback in case of an exception
          
      		urlLoader.push(new URL("http://localhost/"));
      	}
      }
      //isSystemId = systemId;
      if (systemId == null) {
      	systemId = ":main:";
      }
      parse(inputStream, systemId, handler);
    } else {
      parse(systemId, handler);
    }
  }

  public final void loadProperties() throws ParserException {
    String s = null;
    try {
      s = SystemProperties.getProperty("http.proxyHost");
    } catch (SecurityException se) {
      //$JL-EXC$

      //Security Exception is thrown when the parser is used in an applet
      //An Applet does not have the opportunity to open a connection to a proxy
      s = "proxy";
    }
    if (s != null) {
      setUseProxy(true);
      setProxyURL(s);
    }
    try {
      s = SystemProperties.getProperty("http.proxyPort");
    } catch (SecurityException se) {
      //$JL-EXC$
      s = "8080";
    }
    if (s != null) {
      setUseProxy(true);
      try {
        setProxyPort(Integer.parseInt(s));
      } catch (NumberFormatException e) {
        //$JL-EXC$
        setProxyPort(8080);
      }
    }
    
    try {
      MAX_DEPTH = Integer.parseInt(SystemProperties.getProperty("sapxmltoolkit.maxdepth", "512"));
    } catch (NumberFormatException e) {
      MAX_DEPTH = 512;
    }
    
    String propertiesFile = null;
    try {
      propertiesFile = SystemProperties.getProperty("xmlparser.properties.file");
    } catch (Exception e) {
      //$JL-EXC$
      //it is not an issue if the properties file cannot be found (an exception is thrown here
      // in case that we are in a Servlet Engine
      return;
    }
    if (propertiesFile == null) {
      return;
    }
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(propertiesFile));
      setProperties(properties);
    } catch (Exception e) {
      throw new ParserException("Unable to load properties file, '" + propertiesFile + "'", 0, 0);
    }
    if (getUseCaches()) {
      if (getCacheDir() == null) {
        throw new ParserException("XMLParser: \'xmlparser.use-cache = 1\' property detected, but no \'xmlparser.cache-dir\'", 0,0);
      }
    }
    if (getUseProxy()) {
      if (getProxyURL() == null) {
        throw new ParserException("XMLParser: \'xmlparser.use-proxy = 1\' system property detected, but no \'xmlparser.proxy-host\'", 0,0);
      }
      if (getProxyPort() == -1) {
        throw new ParserException("XMLParser: \'xmlparser.use-proxy = 1\' system property detected, but no \'xmlparser.proxy-port\'", 0,0);
      }
    }
  }

  public final void setProperties(Properties p) throws SAXNotRecognizedException, SAXNotSupportedException {
    for (Enumeration e = p.keys(); e.hasMoreElements(); ) {
      String k = (String) e.nextElement();
      setProperty(k, p.getProperty(k));
    }
  }

  public final void setProperty(String key, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (Features.RECOGNIZED.contains(key)) {
      setFeature(key, Features.createBooleanValue(value));
    } else if (key.equals("xmlparser.use-proxy")) {
      setUseProxy(Features.createBooleanValue(value));
    } else if (key.equals("xmlparser.proxy-host")) {
      setProxyURL((String)value);
    } else if (key.equals("xmlparser.proxy-port")) {
      setProxyPort(Integer.parseInt((String)value));
    } else if (key.equals("xmlparser.cache-dir")) {
      setCacheDir((String)value);
    } else if (key.equals("xmlparser.use-cache")) {
      setUseCaches(Features.createBooleanValue(value));
    } else if (key.equals("xmlparser.alternate-dtd")) {
      setAlternativeDTD((String)value);
    } else if (key.equals("xmlparser.use-alternate-dtd")) {
      setUseAlternativeDTD(Features.createBooleanValue(value));
    } else if(key.equals(JAXPProperties.PROPERTY_PROXY_HOST)) {
      setProxyURL((String)value);
    } else if(key.equals(JAXPProperties.PROPERTY_PROXY_PORT)) {
      setProxyPort(determineIntValue(value));
    } else if(key.equals(JAXPProperties.PROPERTY_ALTERNATIVE_DTD)) {
      setAlternativeDTD((String)value);
    } else if(key.equals(JAXPProperties.PROPERTY_ADDITIONAL_DTD)) {
      if(!(value instanceof InputSource)) {
        throw new SAXNotSupportedException("The value of property " + JAXPProperties.PROPERTY_ADDITIONAL_DTD + " must be of type org.xml.sax.InputSource.");
      }
      setAdditionalDTDLocation((InputSource)value);
    } else if (key.equals(JAXPProperties.PROPERTY_ADD_NSMAPPINGS)) {
      setInitializeNamespaceHandler(false);
      INamespaceHandler nh = ((XMLParser)this).getNamespaceHandler();
      Hashtable h = (Hashtable) value;
      Enumeration e = h.keys();
      while (e.hasMoreElements()) {
        String pref = (String) e.nextElement();
        nh.add(new CharArray(pref), new CharArray((String)h.get(pref)));
      }
    } else if(key.equals(JAXPProperties.PROPERTY_MAX_REFERENCES)) {
      setMaximumProcessingReferences(value);
    } else if(key.equals(JAXPProperties.PROPERTY_REPLACE_NAMESPACE)) {
      setNamespaceReplacement(((String[])value)[0], ((String[])value)[1]); 
    } else {
      throw new SAXNotRecognizedException("Unable to set property '" + key + "'");
    }
  }

  private int determineIntValue(Object value) throws SAXNotSupportedException {
    if(value instanceof Integer) {
      return(((Integer)value).intValue());
    } else if(value instanceof String) {
      try {
        return(Integer.parseInt((String)value));
      } catch(NumberFormatException exc) {
        throw new SAXNotSupportedException("Attempting to determine an int value from incorrect string value '" + value.toString() + "'.");
      }
    } else {
      throw new SAXNotSupportedException("Attempting to determine an int value from incorrect object with class '" + value.getClass().getName() + "'. Only java.lang.String and java.lang.Integer classes are supported.");
    }
  }

  public final Object getProperty(String key) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (Features.RECOGNIZED.contains(key)) {
      return(new Boolean(getFeature(key)));
    } else if (key.equals("xmlparser.use-proxy")) {
      return(new Boolean(getUseProxy()));
    } else if (key.equals("xmlparser.proxy-host")) {
      return(getProxyURL());
    } else if (key.equals("xmlparser.proxy-port")) {
      return(new Integer(getProxyPort()));
    } else if (key.equals("xmlparser.cache-dir")) {
      return(getCacheDir());
    } else if (key.equals("xmlparser.use-cache")) {
      return(new Boolean(getUseCaches()));
    } else if (key.equals("xmlparser.alternate-dtd")) {
      return(new Boolean(getAlternativeDTD()));
    } else if (key.equals("xmlparser.use-alternate-dtd")) {
      return(new Boolean(getUseAlternativeDTD()));
    } else if(key.equals(com.sap.engine.lib.xml.parser.JAXPProperties.PROPERTY_PROXY_HOST)) {
      return(getProxyURL());
    } else if(key.equals(com.sap.engine.lib.xml.parser.JAXPProperties.PROPERTY_PROXY_PORT)) {
      return(new Integer(getProxyPort()));
    } else {
      throw new SAXNotRecognizedException("Unable to set property '" + key + "'");
    }
  }

  public final void setFeature(String key, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (!Features.RECOGNIZED.contains(key)) {
      throw new SAXNotRecognizedException(key);
    }
    if (!Features.SUPPORTED.contains(key)) {
      throw new SAXNotSupportedException(key);
    }
    if (key.startsWith(Features.PREFIX_VALIDATION)) {
      xmlValidator.setFeature(key, value);
      return;
    }
    if (key.equals(Features.FEATURE_VALIDATION)) {
      setValidation(value);
    } else if (key.equals(Features.FEATURE_NAMESPACES)) {
      setNamespaces(value);
    } else if (key.equals(Features.FEATURE_NAMESPACE_PREFIXES)) {
      setNamespacePrefixes(value);
    } else if (key.equals(Features.FEATURE_EXTERNAL_GENERAL_ENTITIES)) {
    	setExternalGeneralEntities(value);
    } else if (key.equals(Features.FEATURE_SOAP_DATA)) {
    	setSoapProcessing(value);
    } else if (key.equals(Features.FEATURE_READ_DTD)) {
      setReadDTD(value);
    } else if (key.equals(Features.APACHE_DYNAMIC_VALIDATION)) {
      setDynamicValidation(value);
    } else if (key.equals(Features.FEATURE_HTML_MODE)) {
      setHTMLMode(value);
    } else if(key.equals(Features.FEATURE_TRIM_WHITESPACES)) {
      setDOMTrimWhiteSpaces(value);
    } else if(key.equals(Features.FEATURE_EXPANDING_REFERENCES)) {
      setExpandingReferences(value);
    } else if(key.equals(Features.FEATURE_SCAN_ONLY_ROOT)) {
      setScanOnlyRoot(value);
    } else if(key.equals(Features.FEATURE_USE_PROXY)) {
      setUseProxy(value);
    } else if(key.equals(Features.FEATURE_CLOSE_STREAMS)) {
      setCloseStreams(value);
    } else if (key.equals(Features.FEATURE_BACKWARDS_COMPATIBILITY_MODE)) {
      setBackwardsCompatibilityMode(true);
    } else {
      // Shouldn't happen
      throw new SAXNotRecognizedException("Unable to set feature '" + key + "'");
    }
  }

  public final boolean getFeature(String key) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (!Features.RECOGNIZED.contains(key)) {
      throw new SAXNotRecognizedException(key);
    }
    if (!Features.SUPPORTED.contains(key)) {
      throw new SAXNotSupportedException(key);
    }
    if (key.startsWith(Features.PREFIX_VALIDATION)) {
      return xmlValidator.getFeature(key);
    }
    if (key.equals(Features.FEATURE_VALIDATION)) {
      return getValidation();
    } else if (key.equals(Features.FEATURE_NAMESPACES)) {
      return getNamespaces();
    } else if (key.equals(Features.FEATURE_NAMESPACE_PREFIXES)) {
    	return getNamespacePrefixes();
    } else if (key.equals(Features.FEATURE_EXTERNAL_GENERAL_ENTITIES)) {
    	return getExternalGeneralEntities();
    } else if (key.equals(Features.FEATURE_SOAP_DATA)) {
    	return getSoapProcessing();
    } else if (key.equals(Features.FEATURE_HTML_MODE)) {
      return getHTMLMode();
    } else if(key.equals(Features.FEATURE_TRIM_WHITESPACES)) {
      return(getDOMTrimWhiteSpaces());
    } else if(key.equals(Features.FEATURE_EXPANDING_REFERENCES)) {
      return(getExpandingReferences());
    } else if(key.equals(Features.FEATURE_SCAN_ONLY_ROOT)) {
      return(getScanOnlyRoot());
    } else if(key.equals(Features.FEATURE_USE_PROXY)) {
      return(getUseProxy());
    } else if(key.equals(Features.FEATURE_CLOSE_STREAMS)) {
      return(getCloseStreams());
    } else {
      // Shouldn't happen
      throw new RuntimeException("Unable to get feature '" + key + "'");
    }
  }

  public final void setProxyPort(int value) {
    urlLoader.setProxyPort(value);
  }

  public final int getProxyPort() {
    return urlLoader.getProxyPort();
  }

  public final void setProxyURL(String value) {
    urlLoader.setProxyHost(value);
  }

  public final String getProxyURL() {
    return urlLoader.getProxyHost();
  }

  public final void setUseProxy(boolean value) {
    urlLoader.setUseProxy(value);
  }

  public final boolean getUseProxy() {
    return urlLoader.getUseProxy();
  }

  public final void setUseAlternativeDTD(boolean value) {
    bUseAlternativeDTD = value;
  }

  public final boolean getUseAlternativeDTD() {
    return bUseAlternativeDTD;
  }

  public final void setCacheDir(String value) {
    urlLoader.setCacheDir(value);
  }

  public final String getCacheDir() {
    return urlLoader.getCacheDir();
  }

  public final void setUseCaches(boolean value) {
    urlLoader.setUseCache(value);
  }

  public final boolean getUseCaches() {
    return urlLoader.getUseCache();
  }

  public final void setValidation(boolean b) {
    bValidation = b;
  }

  public final void setDynamicValidation(boolean b) {
    bDynamicValidation = b;
  }


  public final boolean getValidation() {
    return bValidation;
  }

  public final boolean getNamespaces() {
    return this.bNamespaces;
  }

  public final void setNamespaces(boolean bNamespaces) {
    this.bNamespaces = bNamespaces;
  }

  public final void setWellformed(boolean bWellformed) {
    this.bWellformed = bWellformed;
  }

  public final boolean getWellformed() {
    return bWellformed;
  }

  public final boolean getNamespacePrefixes() {
    return bNamespacePrefixes;
  }

  public final void setNamespacePrefixes(boolean b) {
    bNamespacePrefixes = b;
  }

  protected void setXMLValidator(XMLValidator x) {
    xmlValidator = x;
  }

  public final XMLValidator getXMLValidator() {
    return xmlValidator;
  }

  public final boolean getDOMTrimWhiteSpaces() {
    return bDOMTrimWhiteSpaces;
  }

  public final void setDOMTrimWhiteSpaces(boolean v) {
    bDOMTrimWhiteSpaces = v;
  }

  public final void setAlternativeDTD(String value) {
    caAlternativeDTD.clear();
    caAlternativeDTD.set(value);
    alternativeDTD = true;
  }

  public boolean getAlternativeDTD() {
    return(alternativeDTD);
  }

  public abstract void onWarning(String w) throws Exception;

  public boolean getCloseStreams() {
    return this.bCloseStreams;
  }

  public void setCloseStreams(boolean bCloseStreams) {
    this.bCloseStreams = bCloseStreams;
  }

  public void setScanContentOnly(boolean value) {
    bScanContentOnly = value;
  }

  public boolean getScanContentOnly() {
    return bScanContentOnly;
  }

  public boolean getActiveParse() {
    return bActiveParse;
  }

  public void setActiveParse(boolean value) {
    bActiveParse = value;
  }

  public void setExpandingReferences(boolean value) {
    bExpandingReferences = value;
  }

  public boolean getExpandingReferences() {
    return bExpandingReferences;
  }



  public boolean getHTMLMode() {
    return this.bHTMLMode;
  }

  public void setHTMLMode(boolean bHTMLMode) {
    this.bHTMLMode = bHTMLMode;
    getHTMLOpenTags();
    bWellformed = !bHTMLMode;
    setExpandingReferences(false);
  }

  public HashSet getHTMLOpenTags() {
    if (HTML_OPEN_TAGS == null) {
      HTML_OPEN_TAGS = new HashSet();
    }
    return HTML_OPEN_TAGS;
  }

  //Method from interface org.xml.sax.Locator
  public String getPublicId() {
    return publicId;
  }

  //Method from interface org.xml.sax.Locator
  public String getSystemId() {
    return systemId;
  }

  //Method from interface org.xml.sax.Locator
  public int getLineNumber() {
    if (is == null) {
      return 0;
    }
    return is.getRow();
  }

  //Method from interface org.xml.sax.Locator
  public int getColumnNumber() {
    if (is == null) {
      return 0;
    }
    return is.getCol() - 1;
  }

  void setPublicId(String id) {
    publicId = id;
  }

  void setSystemId(String id) {
    systemId = id;
  }


	public boolean getExternalGeneralEntities() {
		return this.bExternalGeneralEntities;
	}

  public void setExternalGeneralEntities(boolean bExternalGeneralEntities) {
		this.bExternalGeneralEntities = bExternalGeneralEntities;
	}

	public boolean bSoapProcessing = false;
	public boolean getSoapProcessing() {
	  return this.bSoapProcessing;
  }

	public void setSoapProcessing(boolean bSoapProcessing) {
	  this.bSoapProcessing = bSoapProcessing;
    if (bSoapProcessing) {
      //namespaceHandler = new NamespaceHandlerEx(null);
      namespaceHandler = new NamespaceHandlerEx(null);
    }
  }

	public boolean bScanOnlyRoot = false;
	public boolean getScanOnlyRoot() {
   	   return this.bScanOnlyRoot;
	}

	public void setScanOnlyRoot(boolean bScanOnlyRoot) {
		this.bScanOnlyRoot = bScanOnlyRoot;
	}

  public void setReadDTD(boolean value) {
    bReadDTD = value;
  }

  public boolean getReadDTD() {
    return(bReadDTD);
  }

 
 /**
  * @return
  */
     public InputSource getAdditionalDTDLocation() {
       return additionalDTDLocation;
     }
   
 /**
  * Allows to set an AdditionalDTD. This DTD file will be read only if the XML File
  * that has been parsed does not contain a DTD. The requirements for this DTD are,
  * that it begins with <!DOCTYPE rootelement [ ... and ends with ]><, the final < is
  * required because the parser expects it to start the following element
  * 
  * @param string
  */
   public void setAdditionalDTDLocation(InputSource string) {
     additionalDTDLocation = string;
   }  

  /**
   * @return
   */
  public boolean isInitializeNamespaceHandler() {
    return initializeNamespaceHandler;
  }

  /**
   * @param b
   */
  public void setInitializeNamespaceHandler(boolean b) {
    initializeNamespaceHandler = b;
  }
  
  /**
   * @return
   */
  public boolean isBackwardsCompatibilityMode() {
    return backwardsCompatibilityMode;
  }

  /**
   * @param b
   */
  public void setBackwardsCompatibilityMode(boolean b) {
    backwardsCompatibilityMode = b;
  }
  
  public void setMaximumProcessingReferences(Object value) {
    if (value == null) {
      throw new IllegalArgumentException("Maximum references parameter value cannot be null!");
    } else if (value instanceof Integer) {
      initMaximumReferencesCount(((Integer) value).intValue());
    } else if(value instanceof String) {
      try {
        initMaximumReferencesCount(Integer.parseInt((String)value));
      } catch(NumberFormatException numberFormExc) {
        throw new IllegalArgumentException("Maximum references parameter value must be with correct number format!");
      }
    } else {
      throw new IllegalArgumentException("Maximum references parameter value must be an Integer or String!");
    }
  }
  
  private void initMaximumReferencesCount(int maximumReferencesCount) {
    if (maximumReferencesCount < 0) {
      throw new IllegalArgumentException("Maximum references parameter value cannot be negative!");
    }
    this.maximumReferencesCount = maximumReferencesCount;
  }

  public int getMaximumProcessingReferences() {
    return(maximumReferencesCount);
  }
  
  public void setNamespaceReplacement(String oldName, String newName) {
    if (isNamespaceReplacing == false) {
      isNamespaceReplacing = true;
      namespaceReplacements = new Hashtable();
    }
    namespaceReplacements.put(new CharArray(oldName).setStatic(), new CharArray(newName).setStatic());
  }

  public void closeStream() throws IOException {
  	if(is != null) {
  		is.close();
  	}
  }
}
