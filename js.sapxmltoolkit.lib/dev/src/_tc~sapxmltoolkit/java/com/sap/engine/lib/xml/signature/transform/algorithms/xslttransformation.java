package com.sap.engine.lib.xml.signature.transform.algorithms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sap.engine.lib.jaxp.DocHandlerResult;
import com.sap.engine.lib.jaxp.TransformerFactoryImpl;
import com.sap.engine.lib.xml.signature.Data;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.transform.Transformation;
import com.sap.engine.lib.xsl.xpath.DTMDOMBuilder;
import com.sap.engine.lib.xsl.xpath.XPathMatcher;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xslt.output.CanonicalDocHandlerSerializer;


public class XSLTTransformation extends Transformation {


  public XSLTTransformation(Object[] args) {
    super(args);
  }

  public XSLTTransformation() {
    this(null);
  }

  public void transform(Data data) throws SignatureException {
    if (additionalArgs.length != 1 || (!(additionalArgs[0] instanceof String) && !(additionalArgs[0] instanceof InputStream))) {
      throw new SignatureException("XSLTTransformation expects a one-elemnent array, containing a xslt stylesheet either as a stirng or as an input stream", new java.lang.Object[]{data, additionalArgs});
    }

    InputStream xslInput = null;

    if (additionalArgs[0] instanceof String) {
      xslInput = new ByteArrayInputStream(((String) additionalArgs[0]).getBytes()); //$JL-I18N$
    } else {
      xslInput = (InputStream) additionalArgs[0];
    }

    InputStream is = new ByteArrayInputStream(data.getOctets());//.getInputStream();
    data.setOctets(transformXSLT(is, xslInput));
  }

  public static byte[] transformXSLT(InputStream xmlInput, InputStream xslInput) throws SignatureException {
    try {
      ByteArrayOutputStream baos = SignatureContext.getByteArrayOutputStreamPool().getInstance();//new ByteArrayOutputStream(1000);
      TransformerFactory factory = new TransformerFactoryImpl();


      StreamSource xmlSource = null;
      StreamSource xslSource = null;
      Result result;
      xmlSource = new StreamSource(xmlInput);
      xslSource = new StreamSource(xslInput);
      result = new StreamResult(baos);
      factory.newTransformer(xslSource).transform(xmlSource, result);
      byte[] res = baos.toByteArray();
      return res;
    } catch (Exception e) {
      throw new SignatureException("Error in XSLTTransforation transform",new Object[]{xmlInput, xslInput},e);
    } 
  }

  public static byte[] filter(Node n, String query) throws SignatureException {
    CanonicalDocHandlerSerializer cdhs = null;
    try {
      Transformer tr = new TransformerFactoryImpl().newTransformer();
      ByteArrayOutputStream baos = SignatureContext.getByteArrayOutputStreamPool().getInstance();//new ByteArrayOutputStream(1000);
      Properties prop = SignatureContext. getProperties(); //new Properties();
      cdhs = Canonicalization.get(baos, prop);//new CanonicalDocHandlerSerializer(baos, prop);
      Source source = new DOMSource(n);
      Result result = new DocHandlerResult(cdhs);
      tr.transform(source, result);
      return baos.toByteArray();
    } catch (Exception e) {
      throw new SignatureException("Error filtering XSLTTransformation",new Object[]{n, query},e);
    } finally{
      Canonicalization.release(cdhs);
    }
  }

  public static byte[] filter(byte[] input, String query) throws SignatureException {
    ByteArrayInputStream bais = new ByteArrayInputStream(input);
    return filter(bais, query);
  }

  public static Node filter(String fileName, String query) throws SignatureException {
    try {
      XPathMatcher matcher = new XPathMatcher(fileName);
      XObject result = matcher.process(query);
      result.print(0);

      if (result instanceof XNodeSet) {
        DTMDOMBuilder builder = new DTMDOMBuilder(((XNodeSet) result).dtm);
        return builder.domtree[0];
// obsolete 30.04.2004        
//        ((XNodeSet) result).dtm.initializeDOM();
//        return ((XNodeSet) result).dtm.domtree[0];
      } else {
        throw new SignatureException("Unexpected return type from XPathQuerry", new java.lang.Object[]{fileName, query});
      }
    } catch (SignatureException e) {
      throw e;
    } catch (Exception e) {
      throw new SignatureException("Error filtering XSLTTransformation",new Object[]{fileName,query},e);
    }
  }

  public static byte[] filter(InputStream input, String query) throws SignatureException {
    try {
      XPathMatcher matcher = new XPathMatcher(input);
      XObject result = matcher.process(query);

      if (result instanceof XNodeSet) {
        DTMDOMBuilder builder = new DTMDOMBuilder(((XNodeSet) result).dtm);
        Document doc = (Document) builder.domtree[0];
//obsolete 30.04.2004        
//        ((XNodeSet) result).dtm.initializeDOM();
//        Document doc = (Document) ((XNodeSet) result).dtm.domtree[0];
        return Canonicalization.canonicalize(doc, true);
      } else {
        throw new SignatureException("Unexpected return type from XPathQuerry", new java.lang.Object[]{input, query});
      }
    } catch (SignatureException e) {
      throw e;
    } catch (Exception e) {
      throw new SignatureException("Error filtering input stream",new Object[]{input, query}, e);
    }
  }
    
  public Transformation defineFrom(GenericElement el, HashMap $dataHashmap) throws SignatureException {
    throw new SignatureException("defineFrom not implemented for standard transformation: XSLTTransformation!", new java.lang.Object[]{el,$dataHashmap});
  }


}

