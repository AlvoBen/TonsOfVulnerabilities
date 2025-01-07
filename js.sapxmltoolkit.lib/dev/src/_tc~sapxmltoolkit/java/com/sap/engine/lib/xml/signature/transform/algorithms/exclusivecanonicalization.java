package com.sap.engine.lib.xml.signature.transform.algorithms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Node;

import com.sap.engine.lib.jaxp.DocHandlerResult;
import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.signature.Data;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.transform.Transformation;
import com.sap.engine.lib.xsl.xslt.output.ExclusiveCanonicalDocHandlerSerializer;
import com.sap.engine.lib.xsl.xslt.output.OutputException;


public class ExclusiveCanonicalization extends Transformation {
 // public static boolean skipPrefixList = Boolean.getBoolean("com.sap.xml.security.c14.skiplist");

  private boolean commented = false;

  public ExclusiveCanonicalization(Object[] args, boolean commented) {
    super(args);
    this.commented = commented;
  }

  public ExclusiveCanonicalization(boolean commented) {
    this(null, commented);
  }


  public void transform(Data data) throws SignatureException {
    if (additionalArgs != null && additionalArgs.length > 1) {
      throw new SignatureException("ExclusiveCanonicalization expects at most one argument - InclusiveNamespaces.", new java.lang.Object[]{data, additionalArgs});
    }
    String[] inclusiveNamespaces = null;
    if ((additionalArgs!=null)&&(additionalArgs.length==1)&&(additionalArgs[0] instanceof String[])){
      inclusiveNamespaces = (String[]) additionalArgs[0];
    }
    data.setOctets(canonicalize(data.getOctets(), commented, inclusiveNamespaces));    
  }
  
  
  public static byte[] canonicalize(byte[] input, boolean retainComments, String[] inclusiveNamespaces) throws SignatureException {
    ByteArrayInputStream bais = new ByteArrayInputStream(input);
    return canonicalize(bais, retainComments, inclusiveNamespaces);
  }

  public static byte[] canonicalize(InputStream input, boolean retainComments,String[] inclusiveNamespaces) throws SignatureException {
    ExclusiveCanonicalDocHandlerSerializer cdhs = null;
    try {
      ByteArrayOutputStream baos = SignatureContext.getByteArrayOutputStreamPool().getInstance();//new ByteArrayOutputStream(10000);
      if (inclusiveNamespaces!=null){
        Arrays.sort(inclusiveNamespaces);
      }
      cdhs = get(baos, SignatureContext.getProperties(), inclusiveNamespaces);//new ExclusiveCanonicalDocHandlerSerializer();//CanonicalDocHandlerSerializerPool.get(baos, Canonicalization.getProperties());//new CanonicalDocHandlerSerializer(baos, Canonicalization.getProperties());
      cdhs.retainComments(retainComments);
      XMLParser parser = new XMLParser();
      parser.setEntityResolver(SignatureContext.getEntityResolver());
      parser.parse(input, cdhs);
            
      return baos.toByteArray();
    } catch (Exception e) {
      throw new SignatureException("Exception while canonicalization", new java.lang.Object[]{input}, e);
    } finally {
      release(cdhs);
    }
  }
  
  public static byte[] canonicalize(Node n, boolean retainComments,String[] inclusiveNamespaces) throws SignatureException {
    ExclusiveCanonicalDocHandlerSerializer cdhs = null;
    try {
      ByteArrayOutputStream baos = SignatureContext.getByteArrayOutputStreamPool().getInstance();// new ByteArrayOutputStream(1000);
      if (inclusiveNamespaces!=null){
        Arrays.sort(inclusiveNamespaces);
      }
      Hashtable namespacesInScope = DOM.getNamespaceMappingsInScope(n);
      
      cdhs = get(baos, SignatureContext.getProperties(), inclusiveNamespaces, namespacesInScope);//new CanonicalDocHandlerSerializer(baos,Canonicalization.getProperties());
      cdhs.retainComments(retainComments);
      Source source = new DOMSource(n);
      Result result = new DocHandlerResult(cdhs);
      SignatureContext.getTransformer().transform(source, result);
      return baos.toByteArray();
    } catch (Exception e) {
      throw new SignatureException("Exception while canonicalizing "+ n.toString(), new Object[]{n}, e);
    } finally {
      release(cdhs);
    }
  }
  

  
  private static ExclusiveCanonicalDocHandlerSerializer get(ByteArrayOutputStream baos, Properties properties, String[] inclusiveNamespaces, Hashtable namespacesInScope)  throws OutputException{
    return SignatureContext.getExclusiveCanonicalizationPool().get(baos,properties, inclusiveNamespaces, namespacesInScope);
  }

  public Transformation defineFrom(GenericElement el, HashMap $dataHashmap) throws SignatureException {
    throw new SignatureException("defineFrom not implemented for standard transformation: ExclusiveCanonicailization!", new java.lang.Object[]{el, $dataHashmap});
  }
  
  private static void release(ExclusiveCanonicalDocHandlerSerializer cdhs) {
    SignatureContext.getExclusiveCanonicalizationPool().release(cdhs);
  }

  private static ExclusiveCanonicalDocHandlerSerializer get(ByteArrayOutputStream baos, Properties properties, String[] inclusiveNamespaces) throws OutputException{
    return SignatureContext.getExclusiveCanonicalizationPool().get(baos,properties, inclusiveNamespaces, null);
  }  

}

