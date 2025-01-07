/*
 * Copyright (c) 2006 by SAP AG, Walldorf. http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 * 
 * $Id$
 */

package com.sap.engine.lib.converter.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.engine.lib.converter.AbstractConverter;

/**
 * This resolver combines EntityResolver and LSResourceResolver in one.
 * Wraps several resource resolvers into one.
 *  
 * @author Petar Zhechev
 * @version 1.0, 2006-4-5 
 */
public class MultiResolver implements LSResourceResolver, EntityResolver {

  private ArrayList resolvers;
  
  public MultiResolver() {
    this.resolvers = new ArrayList();
  }
  
  public boolean addResolver(EntityResolver resolver) {
    if (resolvers.contains(resolver)) {
      return false;
    }
    resolvers.add(resolver);
    return true;
  }
  
  public LSInput resolveResource(String type, String namespaceURI,
      final String publicId, final String systemId, final String baseURI) {
    final InputSource is;
    try {
      is = resolveEntity(publicId,
          systemId);
      if (is == null) {
        return null;
      }
      return new LSInput() {
        public Reader getCharacterStream() {
          return is.getCharacterStream();
        }

        public void setCharacterStream(Reader characterStream) {
          // empty implementation
        }

        public InputStream getByteStream() {
          return is.getByteStream();
        }

        public void setByteStream(InputStream byteStream) {
          // empty implementation
        }

        public String getStringData() {
          return null;
        }

        public void setStringData(String stringData) {
          // empty implementation
        }

        public String getSystemId() {
          return systemId;
        }

        public void setSystemId(String systemId) {
          // empty implementation              
        }

        public String getPublicId() {
          return publicId;
        }

        public void setPublicId(String publicId) {
          // empty implementation              
        }

        public String getBaseURI() {
          return baseURI;
        }

        public void setBaseURI(String baseURI) {
          // empty implementation              
        }

        public String getEncoding() {
          return null;
        }

        public void setEncoding(String encoding) {
          // empty implementation              
        }

        public boolean getCertifiedText() {
          return false;
        }

        public void setCertifiedText(boolean certifiedText) {
          // empty implementation              
        }
      };      
    } catch (SAXException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public InputSource resolveEntity(String publicId, String systemId)
      throws SAXException, IOException {
    for (Iterator iter = resolvers.iterator(); iter.hasNext();) {
      EntityResolver nextResolver = (EntityResolver) iter.next();
      InputSource source = nextResolver.resolveEntity(publicId, systemId);
      if (source != null) {
        return source;
      }
    }
    return null;
  }

  public String toString() {
    StringBuffer toStringBuffer = new StringBuffer();
    toStringBuffer.append("MultiResourceResolver from: ");
    for (Iterator iter = resolvers.iterator(); iter.hasNext();) {
      EntityResolver nextResolver = (EntityResolver) iter.next();
      toStringBuffer.append(nextResolver.getClass().getName());
      toStringBuffer.append(' ');
    }
    return toStringBuffer.toString();
  }
  
  
}
