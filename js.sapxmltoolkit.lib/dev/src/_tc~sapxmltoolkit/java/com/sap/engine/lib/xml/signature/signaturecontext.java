/*
 * Created on 2004-3-25
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

import com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl;
import com.sap.engine.lib.jaxp.TransformerFactoryImpl;
import com.sap.engine.lib.xml.dom.DocumentImpl;
import com.sap.engine.lib.xml.parser.*;
import com.sap.engine.lib.xml.parser.handlers.INamespaceHandler;
import com.sap.engine.lib.xml.parser.handlers.NamespaceHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.signature.encryption.aliases.DefaultKeyAliasResolver;
import com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver;
import com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolverImpl;
import com.sap.engine.lib.xml.signature.transform.algorithms.ByteArrayOutputStreamPool;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.DTMFactory;
import com.sap.engine.lib.xsl.xpath.ETBuilder;
import com.sap.engine.lib.xsl.xslt.NamespaceManager;
import com.sap.engine.lib.xsl.xslt.output.AttributeComparator;
import com.sap.engine.lib.xsl.xslt.output.CanonicalDocHandlerSerializerPool;
import com.sap.engine.lib.xsl.xslt.output.ExclusiveCanonicalDocHandlerSerializerPool;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public abstract class SignatureContext {
  private static CryptographicPool cryptographicPool = null;
  
  public static CryptographicPool getCryptographicPool(){
    if (cryptographicPool==null){
      cryptographicPool = new CryptographicPool();
    }
    return cryptographicPool;
  }

  public static void nullCryptographicPool() {
    cryptographicPool = null;
  }
  
  private static CanonicalDocHandlerSerializerPool canPool = null;
  
  public static CanonicalDocHandlerSerializerPool getCanonicalizationPool(){
    if (canPool == null){
      canPool = new CanonicalDocHandlerSerializerPool();
    }
    return canPool;
  }

  private static ExclusiveCanonicalDocHandlerSerializerPool exCanPool = null;
  
  public static ExclusiveCanonicalDocHandlerSerializerPool getExclusiveCanonicalizationPool(){
    if (exCanPool == null){
      exCanPool = new ExclusiveCanonicalDocHandlerSerializerPool();
    }
    return exCanPool;
  }
  
  private static ByteArrayOutputStreamPool bytePool= null;
  
  public static ByteArrayOutputStreamPool getByteArrayOutputStreamPool() {
    if (bytePool==null){
      bytePool = new ByteArrayOutputStreamPool();
    }
    return bytePool;
  }
  
  
  private static TransformerFactoryImpl tr = null;
  private static DocumentBuilderFactoryImpl documentBuilderFactoryTT = null;
  private static DocumentBuilderFactoryImpl documentBuilderFactoryFT = null;
  
  public synchronized static Transformer getTransformer() throws TransformerConfigurationException{
    if (tr == null){
      tr = new TransformerFactoryImpl();
       //TODO: URIResolver!
     }
     return tr.newTransformer();
  }
  
  public synchronized static DocumentBuilder getDocumentBuilderTT() throws ParserConfigurationException{
	  if (documentBuilderFactoryTT==null){
      documentBuilderFactoryTT = new DocumentBuilderFactoryImpl();
      documentBuilderFactoryTT.setNamespaceAware(true);
      documentBuilderFactoryTT.setValidating(true);
    }
    DocumentBuilder ret= documentBuilderFactoryTT.newDocumentBuilder();
    ret.setEntityResolver(resolver);
    return ret;
  }

  public synchronized static DocumentBuilder getDocumentBuilderFT() throws ParserConfigurationException{
    if (documentBuilderFactoryFT==null){
      documentBuilderFactoryFT = new DocumentBuilderFactoryImpl();
      documentBuilderFactoryFT.setNamespaceAware(true);
      documentBuilderFactoryFT.setValidating(false);
    }
    DocumentBuilder ret= documentBuilderFactoryFT.newDocumentBuilder();
    ret.setEntityResolver(resolver);
    return ret;
  }  
  
  private static EntityResolver resolver = new URLLoaderBase();
  public static void setEntityResolver(org.xml.sax.EntityResolver er) {
    resolver = er;
  }
  
  public static EntityResolver getEntityResolver(){
    return resolver;
  }
  
  public static void nullPools(){
    canPool = null;
    exCanPool = null;
    bytePool = null;
  }

  private static KeyAliasResolverImpl keyAliasResolver = null;
  
  public static KeyAliasResolver getKeyAliasResolver(){
    if (keyAliasResolver == null){
      keyAliasResolver = new KeyAliasResolverImpl();
      keyAliasResolver.addKeyAliasResolver(new DefaultKeyAliasResolver());
    }
    return keyAliasResolver;
  }
  
  public static void nullKeyAliasResolver(){
    keyAliasResolver = null;
  }
  
  public static boolean isNamespace(CharArray prefix, CharArray localName){
    return  XMLNS.equals(prefix)||(((prefix==null)||(prefix.length()==0))&&XMLNS.equals(localName));
  }

  public static CharArray getNamespaceDeclaration(CharArray prefix, CharArray localName){
    if (XMLNS.equals(prefix)){
      return localName;
    } else if (((prefix==null)||(prefix.length()==0))&&XMLNS.equals(localName)){
      return DEFAULT;
    } else {
      return null;
    }
  }

  public static boolean equals(String arg1, String arg2){
    return 
    ((arg1==null)||(arg1.length()==0))&&(((arg2==null)||(arg2.length()==0)))
    ||((arg1!=null)&&arg1.equals(arg2));
  }


  private static void fillMore(Properties p) {
    p.setProperty(OutputKeys.METHOD, "xml");
    p.setProperty(OutputKeys.ENCODING, "utf-8");
    p.setProperty(OutputKeys.INDENT, "no");
    p.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    p.setProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "");
  }

  public static Properties getProperties()
  {
    if (propertiesCache==null){
      propertiesCache = new Properties();
      fillMore(propertiesCache);
    }
    return propertiesCache;
  }

//  LIBRARY FUNCTIONS //  
  
  public static String getLocalName(String qualifiedName) {
    return qualifiedName.substring(qualifiedName.indexOf(":") + 1);
  }

  public static String getPrefix(String qualifiedName) {
    return qualifiedName.substring(0, qualifiedName.indexOf(":"));
  }

  public static String[] removeDuplicates(String[] source) {
    Vector v = new Vector();
    outer:
    for (int i = 0; i < source.length; i ++) {
      for (int j = 0; j < v.size(); j ++) {
        if (v.get(j).equals(source[i])) {
          continue outer;
        }
      }
      v.add(source[i]);
    }
    
    String[] res = new String[v.size()];
    v.copyInto(res);
    return res;
  }

  public static String removeAllOccurrencesOf(char what, String where) {
    StringBuffer result = new StringBuffer(where.length());
  
    for (int i = 0; i < where.length(); i++) {
      char temp = where.charAt(i);
  
      if (temp != what) {
        result.append(temp);
      }
    } 
  
    return result.toString();
  }

  public static String[] getTypes(Certificate[] certs){
    String[] certTypes = new String[certs.length];
    for (int i = 0; i < certs.length; i++ ) {
      certTypes[i] = certs[i].getType();
      certTypes[i] = removeAllOccurrencesOf('.', certTypes[i]);
    }
    return certTypes;
  }

  public static byte[] getBytes(BigInteger b) {
    int length = b.bitLength();
  
    if (((length >> 3) << 3) == length) {
      byte[] signed = b.toByteArray();
      byte[] unsigned = new byte[signed.length - 1];
      System.arraycopy(signed, 1, unsigned, 0, signed.length - 1);
      return unsigned;
    } else {
      byte[] bytes = b.toByteArray();
      return bytes;
    }
  }

  public static byte[] getBytes(BigInteger big, int bitlen) {
    //round bitlen
    //bitlen = ((bitlen + 7) >> 3) << 3;
    bitlen = (bitlen + 7) - ((bitlen + 7) % 8);
  
    if (bitlen < big.bitLength()) {
      throw new IllegalArgumentException("IllegalBitlength");
    }
  
    byte[] bigBytes = big.toByteArray();
  
    if (((big.bitLength() % 8) != 0) && (((big.bitLength() / 8) + 1) == (bitlen / 8))) {
      return bigBytes;
    } else {
      // some copying needed
      int startSrc = 0; // no need to skip anything
      int bigLen = bigBytes.length; //valid length of the string
  
      if ((big.bitLength() % 8) == 0) { // correct values
        startSrc = 1; // skip sign bit
        bigLen--; // valid length of the string
      }
  
      int startDst = bitlen / 8 - bigLen; //pad with leading nulls
      byte[] resizedBytes = new byte[bitlen / 8];
      System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, bigLen);
      return resizedBytes;
    }
  }
// END LIBRARY //
  // CONSTANTS //
  public final static CharArray DEFAULT = new CharArray("#default");
  public final static CharArray XMLNS = new CharArray("xmlns");
  public final static CharArray XML = new CharArray("xml");
  public final static Hashtable EMPTY = new Hashtable();
  public final static AttributeComparator ac = new AttributeComparator();
  
  public static int MAX_STACK_SIZE = 200;
  private static Properties propertiesCache = null;
  public static final String MIME_XML = "xml";
  public static final String MIME_HTML = "html";
  public static final String MIME_ANY = "";
  // END CONSTANTS //
  
  public static String DEFAULT_STR = "#default";
  public static Vector EMPTY_VECTOR = new Vector(0);
  
  public static boolean debug = false;
  public static int count=0;
  
  
// used by XPATHTransformation - 1000 calles and reinit!  
  public static final int ARRAY_SIZE = 16;
  public static int[] excludeList = new int[ARRAY_SIZE];
  public static int excluded = 0;
  private static NamespaceManager nm = null;
  private static DTM dtm = null;
  private static DTMFactory factory = null;
  private static ETBuilder etBuilder = null;
  
  private static int calls = 0;
  public static int MAX_CALL_VALUE = 1000;

  public static DTM getDTM() {
    calls++;
    if (calls>MAX_CALL_VALUE){
// prevents memory leaks in ETBuilder!!!      
      nm = null;
      dtm = null;
      factory = null;
      etBuilder = null;
      excludeList = new int[ARRAY_SIZE];
      calls = 0;
    }
    
    if (dtm == null) {
      dtm = new DTM();
    } else {
      dtm.clearDirty();
    }
    return dtm;
  }

  public static DTMFactory getDTMFactory() {
    if (factory == null) {
      factory = new DTMFactory();
    }
    return factory;
  }

  public static NamespaceManager getNamespaceManager() {
    if (nm == null) {
      nm = new NamespaceManager();
    } else {
      nm.reuse();
    }
    return nm;
  }

  public static ETBuilder getETBuilder() {

    if (etBuilder == null) {
      etBuilder = new ETBuilder();
    } 
    return etBuilder;
  }

  protected static CharArray defaultPrefixName = NamespaceHandler.defaultPrefixName;//new CharArray("<<<>>>");
  
  protected static XMLParser parser = null;
  protected static DOMParser domParser = null;
  
  public synchronized static Document parse(InputStream is, Hashtable mappings) throws SignatureException{
    try {
      //TODO: parser and DOM Parser not to be created every time
      
      if (domParser == null){
        parser = new XMLParser();
        parser.setInitializeNamespaceHandler(false);
        domParser = new DOMParser(parser);
        domParser.setFeature(Features.FEATURE_NAMESPACES, true);
        domParser.setFeature(Features.FEATURE_VALIDATION, false);
        domParser.setDocumentClassName(DocumentImpl.class.getName());
      }
      parser.init();
      
//      XMLParser parser = new XMLParser();
//      parser.setInitializeNamespaceHandler(false);
      if ((mappings!=null)&&(mappings.size()>0)){
        INamespaceHandler nh = parser.getNamespaceHandler();
        Enumeration enum1 = mappings.keys();
        while(enum1.hasMoreElements()){
          String key = (String) enum1.nextElement();
          
          CharArray mapping = key.length()==0?defaultPrefixName:new CharArray(key);
          CharArray value = new CharArray((String) mappings.get(key));
          //TODO: 2 new instances of char array!!
          value.setStatic();
          mapping.setStatic();
          nh.add(mapping, value);
        }
      }
//      DOMParser domParser = new DOMParser(parser);
//      domParser.setFeature(Features.FEATURE_NAMESPACES, true);
//      domParser.setFeature(Features.FEATURE_VALIDATION, false);
//      domParser.setDocumentClassName(DocumentImpl.class.getName());
      return domParser.parse(is);
    } catch (Exception ex){
      throw new SignatureException("Error while parsing input stream", new Object[]{is, mappings},ex);
    }
//    return getDocumentBuilderFT().parse(is);
  }

}
