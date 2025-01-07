package com.sap.engine.lib.xml.parser;

import java.io.IOException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov
 * @version      May 2001
 */
public final class URLLoader extends URLLoaderBase implements URIResolver {

  public URLLoader() {
    super();
  }

  public Source resolve(String href, String base) throws TransformerException {
    //    LogWriter.getSystemLogWriter().println("--------- using internal URIResolver to resolve: " + href);
    //    LogWriter.getSystemLogWriter().println("URLLoader.resolve: href=" + href + ", base=" + base + ", id=" + hashCode());
    StreamSource r;
    try {
      URL url = load(load(null, base), href);
      //LogWriter.getSystemLogWriter().println("URLLoader.resolve: url=" + url.toExternalForm().toString());
      String surl = url.toExternalForm().toString();

      if (surl.startsWith("file:") && surl.charAt(5) != '/') {
        surl = surl.substring(5);
      }

      r = new StreamSource(surl);
      r.setInputStream(url.openStream());
    } catch (IOException e) {
      throw new TransformerException(e);
    }
    return r;
  }

}

