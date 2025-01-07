package com.sap.engine.lib.xml.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.Properties;
import java.util.Stack;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.util.NestedRuntimeException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov
 * @version      May 2001
 */
public class URLLoaderBase implements EntityResolver {

  public static final String CACHE_INDEX_FILE_NAME = "index.properties";
  public static final String CACHED_FILE_EXTENSION = ".cached";
  private Stack stack = new Stack();
  private URL stackTop = null;
  // Proxy settings
  private String proxyHost = null;
  private int proxyPort = -1;
  private boolean useProxy = false;
  // Cache settings
  private String cacheDirName = null;
  private File cacheDir = null;
  private String cacheIndexFileName = null;
  private File cacheIndexFile = null;
  private boolean useCache = false;
  private Properties cacheIndex = new Properties();
  // Buffer, used when the loader is downloading from the Internet
  private byte[] buffer = new byte[1000];
  private static String workDir = null;
  static {
    try {
      workDir = new File("").getAbsolutePath().toString() + "/";
    } catch (Exception e) {
      //$JL-EXC$
    }
  }
  public URLLoaderBase() {
    init();
  }

  public void init() {
    stack.clear();
    stackTop = null;
    proxyHost = null;
    proxyPort = -1;
    useProxy = false;
    cacheDirName = null;
    cacheDir = null;
    cacheIndexFileName = null;
    cacheIndexFile = null;
    useCache = false;
    try {
      loadAndPush(workDir);
    } catch (Exception e) {
      //$JL-EXC$
    }
  }

  public URL load(String s) throws IOException {
    URL url = load(stackTop, s);
    return url;
  }

  public URL loadAndPush(String s) throws IOException {
    URL url = loadAndPush(stackTop, s);
    return url;
  }

  public URL load(URL base, String s) throws IOException {
    URL url = fileOrURLToURL(base, s);

    if (useCache) {
      url = passThroughCache(url);
    } else if (useProxy) {
      url = passThroughProxy(url);
    }

    return url;
  }

  public URL loadAndPush(URL base, String s) throws IOException {
    URL url = fileOrURLToURL(base, s);
    push(url);

    if (useCache) {
      url = passThroughCache(url);
    } else if (useProxy) {
      url = passThroughProxy(url);
    }

    return url;
  }

  //  public Source resolve(String href, String base) throws TransformerException {
  //    //    LogWriter.getSystemLogWriter().println("--------- using internal URIResolver to resolve: " + href);
  //    //    LogWriter.getSystemLogWriter().println("URLLoader.resolve: href=" + href + ", base=" + base + ", id=" + hashCode());
  //    StreamSource r;
  //    try {
  //      URL url = load(load(null, base), href);
  //      r = new StreamSource(url.toExternalForm());
  //      r.setInputStream(url.openStream());
  //    } catch (IOException e) {
  //      throw new TransformerException(e);
  //    }
  //    return r;
  //  }
  public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
    URL external = loadAndPush(systemId);
    //in some cases external.toString("xxxxxxx.dtd") returns http://localhost/xxxxxx.dtd, and if there is some server running on port 80, and returns some valid response (200 ok), then we get problems
    // this was the case with some server "dsrinfoserv" therefore this is removed now from the beginning on.
    // NOTE: may cause problems if someone explicitly wants to downloads files from http://localhost/
    if (!external.toString().substring(17).equals(systemId)) {
      systemId = external.toString();
    }
    InputSource is = new InputSource();
    is.setSystemId(systemId);
    is.setPublicId(publicId);
    InputStream urlStream;
    try {
      urlStream = new URL(systemId).openStream();
    } catch (IOException ioe) {
      //$JL-EXC$
      //trying to load DTD from classloader - fix for petstore
      if (systemId.startsWith("http://localhost/")) {
        systemId = systemId.substring(17); // "http://localhost/".length
      }
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      urlStream = loader.getResourceAsStream(systemId);
      if (urlStream == null) {
        throw new IOException("Failed to load resource from the context classloader of the current thread! Loading from classloader was caused by: " + ioe.toString());
      }
    }
    is.setByteStream(urlStream);
    return is;
  }

  public void push(URL url) {
//    LogWriter.getSystemLogWriter().println(">>>>>>>>>>>   URLLoader.push: url=" + url);
//    Thread.dumpStack();
    stack.push(url);
    stackTop = url;
  }

  public void pushTheSame() {
//    LogWriter.getSystemLogWriter().println(">>>>>>>>>>>   URLLoader.pushTheSame: ");
//      Thread.dumpStack();
//
    try {
      stack.push(stack.peek());
    } catch (EmptyStackException e) {
      //$JL-EXC$
      stack.push(null);
    }
  }

  public void pop() {
    try {
      URL u = (URL) stack.pop();
//      LogWriter.getSystemLogWriter().println(">>>>>>>>>>>   URLLoader.pop: " + u + ", isEmpty= " + stack.empty());
//      Thread.dumpStack();
//
      if (!stack.empty()) {
        stackTop = (URL) stack.peek();
      } else {
        stackTop = null;
      }
    } catch (EmptyStackException e) {
      // Shouldn't happen
      throw new RuntimeException("URLLoader's stack is empty.");
    }
  }

  public URL peek() {
    try {
//      LogWriter.getSystemLogWriter().println(">>>>>>>>>>>   URLLoader.peak: ");
//      Thread.dumpStack();
      return (URL) stack.peek();
    } catch (EmptyStackException e) {
      
      // Shouldn't happen
//      LogWriter.getSystemLogWriter().println("URLLoaderBase.peek: Stack Is Empty?! Returning:" + workDir);
//      e.printStackTrace();
//      try { 
//        return new URL(workDir);
//      } catch (Exception e1) {
//        return null;
//      }
      throw new NestedRuntimeException("URLLoader's stack is empty.", e);
    }
  }

  private URL passThroughProxy(URL url) throws IOException {
    return url;
    /*
     if (!useProxy || (!url.getProtocol().equals("http"))) {
     return url;
     }
     String s0 = url.toExternalForm();
     URL r = new URL("http", proxyHost, proxyPort, url.toExternalForm());
     String s1 = r.toExternalForm();
     return r;
     */
  }

  private URL passThroughCache(URL url) throws IOException {
    if (!useCache || (!url.getProtocol().equals("http"))) {
      return url;
    }

    String externalForm = url.toExternalForm();
    String s = cacheIndex.getProperty(externalForm);

    if (s != null) {
      return new URL("file:" + cacheDirName + "\\" + s);
    }

    s = createCachedFileName(url);
    url = passThroughProxy(url);
    InputStream in = url.openStream();
    (new File(cacheDirName + "\\" + s)).createNewFile();
    FileOutputStream out = new FileOutputStream(cacheDirName + "\\" + s);

    while (true) {
      int b = in.read(buffer);

      if (b == -1) {
        break;
      }

      out.write(buffer, 0, b);
    }

    out.close();
    cacheIndex.setProperty(externalForm, s);
    storeIndex();
    url = new URL("file:" + cacheDirName + "\\" + s);
    return url;
  }

  private String createCachedFileName(URL url) {
    String s = "_" + url.toExternalForm().replace('/', '_').replace('\\', '_').replace(':', '_').replace('?', '_').replace('~', '_').replace('#', '_');
    return "t" + System.currentTimeMillis() + s + CACHED_FILE_EXTENSION;
  }

  public void storeIndex() throws IOException {
    if (cacheIndexFile != null) {
      if (!cacheIndexFile.exists()) {
        File parentDir = cacheIndexFile.getParentFile();

        if (parentDir != null) {
          parentDir.mkdirs();
        }

        cacheIndexFile.createNewFile();
      }

      cacheIndex.store(new FileOutputStream(cacheIndexFileName), "Index file for the caching system of the XML parser.");
    }
  }

  public void setProxyHost(String s) {
    proxyHost = s;
  }

  public void setProxyPort(int p) {
    proxyPort = p;
  }

  public void setUseProxy(boolean b) {
    useProxy = b;
  }

  public void setCacheDir(String s) {
    if (s.startsWith("file:")) {
      s = s.substring(5);
    }

    cacheDirName = s;
    cacheDir = new File(s);
    cacheIndexFile = new File(cacheDir, CACHE_INDEX_FILE_NAME);
    cacheIndexFileName = cacheIndexFile.toString();
    cacheIndex.clear();
    try {
      cacheIndex.load(new FileInputStream(cacheIndexFileName));
    } catch (Exception e) {
      //$JL-EXC$
      // Ignore
    }
  }

  public void setUseCache(boolean b) {
    useCache = b;
  }

  public String getProxyHost() {
    return proxyHost;
  }

  public int getProxyPort() {
    return proxyPort;
  }

  public boolean getUseProxy() {
    return useProxy;
  }

  public String getCacheDir() {
    return cacheDirName;
  }

  public boolean getUseCache() {
    return useCache;
  }

  public void printStack() {
    LogWriter.getSystemLogWriter().println("URLLoader stack = " + stack);
  }

  public void clearStack() {
    stack.clear();
  }

  /**
   * Converts a String to a URL, assuming 'file:' as a default scheme.
   * Might return null, if the String is neither a valid URL, nor a
   * valid filename.
   */
  public static URL fileOrURLToURL(URL base, String s) throws IOException {
    if (s == null) {
      return null;
    }

    if (base == null) {
      base = new URL("file:");
    }

    URL url;
    s = s.replace('\\', '/');
    try {
      String protocol = base.getProtocol();
      if (!"http".equals(protocol) && !"https".equals(protocol) && ((s.charAt(1) == ':') || (s.charAt(0) == '/') || (s.charAt(0) == '\\'))) {
        //return new File(s).toURL();
        s = (new File(s)).toURL().toExternalForm();
      }
    } catch (IndexOutOfBoundsException e) {
      //$JL-EXC$
      //The code below will handle it
    }

    //    LogWriter.getSystemLogWriter().println("URLLoader.fileOrURLToURL1: s = " + s);
    if (s.startsWith("file:") && (s.length() > 5) && (s.charAt(5) != '/')) {
      s = "file:/".concat( s.substring(5));
    } else if (s.startsWith("file://") && !s.startsWith("file:////") && !s.startsWith("file://localhost/")) {
      s = "file:////".concat( s.substring("file://".length()));
    }

    //    LogWriter.getSystemLogWriter().println("URLLoader.fileOrURLToURL2: s = " + s);
    url = new URL(base, s);
    return url;
  }

  public static File fileURLToFile(URL url) {
    if (!url.getProtocol().equalsIgnoreCase("file")) {
      return null;
    }
    String host = url.getHost();
    String urlFile; 
    if (host != null && !host.equals("") && !host.equals("localhost")) {
      urlFile = "//" + host + url.getPath();
    } else {
      urlFile = url.getFile();
    }
    return new File(decode(urlFile));
  }
  
  private static String decode(String s) {
    StringBuffer stringbuffer = new StringBuffer();
    char c;
    for (int i = 0; i < s.length(); stringbuffer.append(c)) {
      c = s.charAt(i);
      if (c != '%') {
        i++;
        continue;
      }
      try {
        c = unescape(s, i);
        i += 3;
        if ((c & 0x80) == 0)
          continue;
        switch (c >> 4) {
          case 12 : // '\f'
          case 13 : // '\r'
            char c1 = unescape(s, i);
            i += 3;
            c = (char) ((c & 0x1f) << 6 | c1 & 0x3f);
            break;

          case 14 : // '\016'
            char c2 = unescape(s, i);
            i += 3;
            char c3 = unescape(s, i);
            i += 3;
            c = (char) ((c & 0xf) << 12 | (c2 & 0x3f) << 6 | c3 & 0x3f);
            break;

          default :
            throw new IllegalArgumentException();
        }
      } catch (NumberFormatException numberformatexception) {
        throw new IllegalArgumentException();
      }
    }

    return stringbuffer.toString();
  }
  
  static char unescape(String s, int i) {
    return (char) Integer.parseInt(s.substring(i + 1, i + 3), 16);
  }

  //  public static void main(String args[]) throws Exception {
  //    URLLoader ul = new URLLoader();
  //    ul.resolve("xml2htmlIfrMetaCommon2.xsl", "file:/d:/develop/xml2000/bugs/ifr/2/xml2htmlTypedef.xsl");
  //  }

}

