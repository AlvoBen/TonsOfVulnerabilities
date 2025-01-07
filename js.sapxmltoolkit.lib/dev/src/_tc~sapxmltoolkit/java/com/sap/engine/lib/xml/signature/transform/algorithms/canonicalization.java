package com.sap.engine.lib.xml.signature.transform.algorithms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.sap.engine.lib.jaxp.DocHandlerResult;
import com.sap.engine.lib.xml.SystemProperties;
import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.Data;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.transform.Transformation;
import com.sap.engine.lib.xsl.xslt.output.CanonicalDocHandlerSerializer;
import com.sap.engine.lib.xsl.xslt.output.OutputException;

public class Canonicalization extends Transformation {

  private boolean commented = false;

  public Canonicalization(Object[] args, boolean commented) {
    super(args);
    this.commented = commented;

    if (commented) {
      this.uri = Constants.TR_C14N_WITH_COMMENTS;
    } else {
      this.uri = Constants.TR_C14N_OMIT_COMMENTS;
    }
  }
  
  public Canonicalization(boolean commented) {
    this(null, commented);
  }

  
  private Node canonicalize0(Node n) throws SignatureException {
    CanonicalDocHandlerSerializer cdhs = null;
    try {

      ByteArrayOutputStream baos = SignatureContext.getByteArrayOutputStreamPool().getInstance();// new ByteArrayOutputStream(10000);
//      Properties prop = new Properties();
//      Canonicalization.fillMore(prop);
      cdhs = get(baos, SignatureContext.getProperties());//new CanonicalDocHandlerSerializer(baos, Canonicalization.getProperties());
      cdhs.retainComments(commented);
      Source source = new DOMSource(n);
      Result result = new DocHandlerResult(cdhs);
      SignatureContext.getTransformer().transform(source, result);
      
      
      byte[] resOctets = baos.toByteArray();
//      EncoderPool.releaseEncoder(cdhs.getEncoder());
      SystemProperties.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl");

      InputSource is = new InputSource(new ByteArrayInputStream(resOctets));
      Document document = SignatureContext.getDocumentBuilderTT().parse(is);
      return document;
    } catch (Exception e) {
      throw new SignatureException("Exception while canonicalizing "+n.toString(), new java.lang.Object[]{n}, e);
    } finally {
      release(cdhs);
    }
  }



  private byte[] octeticalizeOmitting(Node n) throws SignatureException {
    return canonicalize(n, false);
  }

  private byte[] octeticalizeIncluding(Node n) throws SignatureException {
    return canonicalize(n, true);
  }

  public void retainComments(boolean $commented) {
    this.commented = $commented;
  }

  public static Node canonicalizeToNode(byte[] input, boolean retainComments) throws SignatureException {
    try {
      input = Canonicalization.canonicalize(input, retainComments);
      return SignatureContext.getDocumentBuilderTT().parse(new ByteArrayInputStream(input));
    } catch (Exception e) {
      throw new SignatureException("Error while canonicalizing to node",new Object[]{input}, e);
    } 
  }

  public static byte[] canonicalize(Node n, boolean retainComments) throws SignatureException {
    CanonicalDocHandlerSerializer cdhs = null;
    try {
      ByteArrayOutputStream baos = SignatureContext.getByteArrayOutputStreamPool().getInstance();// new ByteArrayOutputStream(1000);
//      Properties prop = new Properties();
//      Canonicalization.fillMore(prop);
      cdhs = get(baos, SignatureContext.getProperties());//new CanonicalDocHandlerSerializer(baos,Canonicalization.getProperties());
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

  public static byte[] canonicalize(byte[] input, boolean retainComments) throws SignatureException {
    ByteArrayInputStream bais = new ByteArrayInputStream(input);
    return canonicalize(bais, retainComments);
  }

  public static byte[] canonicalize(InputStream input, boolean retainComments) throws SignatureException {
    CanonicalDocHandlerSerializer cdhs = null;
    try {
      ByteArrayOutputStream baos = SignatureContext.getByteArrayOutputStreamPool().getInstance();//new ByteArrayOutputStream(10000);
      cdhs = get(baos, SignatureContext.getProperties());//new CanonicalDocHandlerSerializer(baos, Canonicalization.getProperties());
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

  public byte[] transformToOctets(Node n) throws SignatureException {
    if (!commented) {
      return octeticalizeOmitting(n);
    } else {
      return octeticalizeIncluding(n);
    }
  }

  public Node transform(Node n) throws SignatureException {
    return canonicalize0(n);
  }

  public void transform(Data d) throws SignatureException {
    d.setOctets(canonicalize(d.getOctets(), commented));
  }

  public Transformation defineFrom(GenericElement el, HashMap $dataHashmap) throws SignatureException {
    throw new SignatureException("defineFrom not implemented for standard transformation: Canonicalization!",new java.lang.Object[]{el, $dataHashmap});
  }

  public static void release(CanonicalDocHandlerSerializer cdhs) {
    SignatureContext.getCanonicalizationPool().release(cdhs);
  }

  public static CanonicalDocHandlerSerializer get(ByteArrayOutputStream baos, Properties properties) throws OutputException{
    return SignatureContext.getCanonicalizationPool().get(baos,properties);
  }

}

