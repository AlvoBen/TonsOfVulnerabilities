/*
 * Created on 2004-3-4
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.w3c.dom.Node;

import com.sap.engine.lib.xml.SystemProperties;
import com.sap.engine.lib.xml.signature.transform.algorithms.Canonicalization;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class Data {

  public String mimeType = SignatureContext.MIME_ANY;
  
  private byte[] data = null;
  private Node domRepr = null;
  private InputStream stream = null;
  
  public Data(String urlString, String proxyHost, String proxyPort) throws MalformedURLException, IOException {
    if (proxyHost != null && proxyPort != null) {
      SystemProperties.setProperty("http.proxyHost", proxyHost);
      SystemProperties.setProperty("http.proxyPort", proxyPort);
    }

    URL url = new URL(urlString);
    URLConnection con = url.openConnection();
    String contentType = con.getContentType();

    if (contentType == null) {
      mimeType = SignatureContext.MIME_ANY;
    } else {
      if ((contentType.indexOf("xml") >= 0) || (contentType.indexOf("xhtml") >= 0)) {
        mimeType = SignatureContext.MIME_XML;
      } else {
        if (contentType.indexOf("html") >= 0) {
          mimeType = SignatureContext.MIME_HTML;
        } else {
          mimeType = SignatureContext.MIME_HTML;
        }
      }
    }

    stream = con.getInputStream();
  }  
 
  public Data(byte[] constructor){
    data = constructor;
  }
  
  public Data(InputStream is){
    stream = is;
  }
  
  public Data(Node node) {
    mimeType = SignatureContext.MIME_XML;
    domRepr = node;
  }
  
  private void buildFromDOM() throws SignatureException {
    if (domRepr==null){
      buildFromIS();
      return;
    }
    data = Canonicalization.canonicalize(domRepr, true);
  }
  
  private void buildFromIS() throws SignatureException{
    try {
    byte[] temp = new byte[0];
    byte[] buffer = new byte[5000];
      
      int read;
      
      while ((read = stream.read(buffer)) != -1) {
        byte[] temp1 = new byte[temp.length + read];
        System.arraycopy(temp, 0, temp1, 0, temp.length);
        System.arraycopy(buffer, 0, temp1, temp.length, read);
        temp = temp1;
      }
    
      data = temp;
    } catch (Exception ex){
      throw new SignatureException("Error reading stream",new Object[]{stream},ex);
    }
  }

  private void buildFromOctets() throws SignatureException{
    if (data == null) {
      buildFromIS();
    }
    domRepr = Canonicalization.canonicalizeToNode(data, true);
  }
  
  public InputStream getInputStream() throws SignatureException{
    if (data == null){
      buildFromDOM();
    }    
    return new ByteArrayInputStream(data);
  }
  
  public Node getNode() throws SignatureException{
    if (domRepr==null){
      buildFromOctets();
    }
    return domRepr;
  }

  public byte[] getOctets() throws SignatureException{
    if (data==null){
      buildFromDOM();
    }
    return data;
  }
  
  public void reuse(){
    mimeType = SignatureContext.MIME_ANY;
    data = null;
    domRepr = null;
    stream = null;
  }
  public void setInputStream(InputStream is) {
    reuse();
    stream = is;
  }
  
  public void setNode(Node node){
    reuse();
    mimeType = SignatureContext.MIME_XML;
    domRepr = node;
  }
  
  public void setOctets(byte[] octets){
    reuse();
    data = octets;
  }
  
  public void synch() throws SignatureException{
   if (data == null){
     buildFromDOM();
   } else if (domRepr == null){
     buildFromOctets();
   }
  }
  
}
