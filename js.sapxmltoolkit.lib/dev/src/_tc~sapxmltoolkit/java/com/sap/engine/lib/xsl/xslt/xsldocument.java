package com.sap.engine.lib.xsl.xslt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.URLLoader;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xslt.output.DocHandlerSerializer;
import com.sap.engine.lib.xsl.xslt.output.OutputException;

public final class XSLDocument extends XSLContentNode {

  private String method = null;
  private String version = null;
  private String encoding = null;
  private String omitXmlDeclaration = null;
  private String standalone = null;
  private String doctypePublic = null;
  private String doctypeSystem = null;
  private String cdataSectionElements = null;
  private String indent = null;
  private String mediaType = null;
  private String href = null;
  private Properties prop = null;
  private AttributeValueTemplateHandler hrefTemplate = new AttributeValueTemplateHandler();
  private CharArray instHref = new CharArray();

  public String[] getRequiredParams() {
    return new String[] {"href"};
  }

  public String[] getOptionalParams() {
    return new String[] {"method", "version", "indent", "encoding", "standalone"};
  }

  public XSLDocument(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    init(el, parent);
  }

  /*allowed are:href, method, version, indent, encoding, standalone */
  public void init(Element el, XSLNode parent) throws XSLException {
    //    LogWriter.getSystemLogWriter().println("Initializing...");
    prop = new Properties();

    if (el.hasAttribute("method")) {
      method = el.getAttribute("method");
      prop.setProperty(OutputKeys.METHOD, method);
    }

    if (el.hasAttribute("version")) {
      version = el.getAttribute("version");
      prop.setProperty(OutputKeys.VERSION, version);
    }

    if (el.hasAttribute("encoding")) {
      encoding = el.getAttribute("encoding");
      prop.setProperty(OutputKeys.ENCODING, encoding);
    }

    if (el.hasAttribute("standalone")) {
      standalone = el.getAttribute("standalone");
      prop.setProperty(OutputKeys.STANDALONE, standalone);
    }

    if (el.hasAttribute("ident")) {
      indent = el.getAttribute("indent");
      prop.setProperty(OutputKeys.INDENT, indent);
    }

    if (el.hasAttribute("href")) {
      href = el.getAttribute("href");
    } else {
      throw new XSLException("Attribute href is obligatory for an xsl:document !");
    }
  }

//  private void fillBlanksFromParent(XSLDocument par) {
//    if (method == null) {
//      method = par.getMethod();
//    }
//
//    if (version == null) {
//      version = par.getVersion();
//    }
//
//    if (encoding == null) {
//      encoding = par.getEncoding();
//    }
//
//    if (standalone == null) {
//      standalone = par.getStandalone();
//    }
//
//    if (indent == null) {
//      indent = par.getIndent();
//    }
//
//    cdataSectionElements = par.getCdataSectionElements();
//    doctypeSystem = par.getDoctypeSystem();
//    doctypePublic = par.getDoctypePublic();
//    mediaType = par.getMediaType();
//    omitXmlDeclaration = par.getOmitXmlDeclaration();
//  }

  private void fillBlanksFromParent(XSLOutputNode par) throws XSLException {
    if (method == null) {
      method = par.getMethod();
    }

    if (version == null) {
      version = par.getVersion();
    }

    if (encoding == null) {
      encoding = par.getEncoding();
    }

    if (standalone == null) {
      standalone = par.getStandalone();
    }

    if (indent == null) {
      indent = par.getIndent();
    }

    if (indent == null) {
      cdataSectionElements = par.getCdataSectionElements();
    }

    if (doctypeSystem == null) {
      doctypeSystem = par.getDoctypeSystem();
    }

    if (doctypePublic == null) {
      doctypePublic = par.getDoctypePublic();
    }

    if (mediaType == null) {
      mediaType = par.getMediaType();
    }

    if (omitXmlDeclaration == null) {
      omitXmlDeclaration = par.getOmitXmlDeclaration();
    }

  }

  public Object getParentDocument(Element e) {
    Node n = e.getParentNode();

    if (n.getNodeType() == Node.DOCUMENT_NODE) {
      return n;
    }

    if (((Element) n).getTagName().equals("xsl:document")) {
      return (Element) n;
    }

    return getParentDocument((Element) n);
  }

  public String getCdataSectionElements() {
    return cdataSectionElements;
  }

  public String getDoctypePublic() {
    return doctypePublic;
  }

  public String getEncoding() {
    return encoding;
  }

  public String getDoctypeSystem() {
    return doctypeSystem;
  }

  public String getIndent() {
    return indent;
  }

  public String getMediaType() {
    return mediaType;
  }

  public String getMethod() {
    return method;
  }

  public String getOmitXmlDeclaration() {
    return omitXmlDeclaration;
  }

  public String getVersion() {
    return version;
  }

  public String getStandalone() {
    return standalone;
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    try {
      //      LogWriter.getSystemLogWriter().println("Processing...");
      DocHandler h = owner.getOutputProcessor().getDocHandler();

      if (h instanceof DocHandlerSerializer) {
        prop = ((DocHandlerSerializer) h).getOutputProperties();
      } else if (owner.getOutputProperties() != null) {
        //        Object par = getParentDocument(el);
        //        fillBlanksFromPar(());
        //        LogWriter.getSystemLogWriter().println(owner.getOutputProcessor());
        fillBlanksFromParent(owner.getOutputProperties());
        prop = new Properties();

        if (method != null) {
          prop.setProperty(OutputKeys.METHOD, method);
        }

        if (version != null) {
          prop.setProperty(OutputKeys.VERSION, version);
        }

        if (encoding != null) {
          prop.setProperty(OutputKeys.ENCODING, encoding);
        }

        if (standalone != null) {
          prop.setProperty(OutputKeys.STANDALONE, standalone);
        }

        if (indent != null) {
          prop.setProperty(OutputKeys.INDENT, indent);
        }

        if (cdataSectionElements != null) {
          prop.setProperty(OutputKeys.CDATA_SECTION_ELEMENTS, cdataSectionElements);
        }

        if (doctypeSystem != null) {
          prop.setProperty(OutputKeys.DOCTYPE_SYSTEM, doctypeSystem);
        }

        if (doctypePublic != null) {
          prop.setProperty(OutputKeys.DOCTYPE_PUBLIC, doctypePublic);
        }

        if (mediaType != null) {
          prop.setProperty(OutputKeys.MEDIA_TYPE, mediaType);
        }

        if (omitXmlDeclaration != null) {
          prop.setProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlDeclaration);
        }
      }

      instHref.clear();
      hrefTemplate.init(new CharArray(href), owner.etBuilder);
      hrefTemplate.processTo(instHref, owner.getXPathProcessor(), xcont, varContext);
      //      LogWriter.getSystemLogWriter().println("XSLDocument: href = " + instHref);
      URLLoader urlloader = new URLLoader();
      URL u = urlloader.load(urlloader.load(null, owner.getBaseURI()), instHref.toString());
      File f = new File(u.getFile());
      File dir = f.getParentFile();

      if (dir != null) {
        dir.mkdirs();
      }

      OutputStream o = new FileOutputStream(u.getFile());
      DocHandlerSerializer dhs = new DocHandlerSerializer(o, fillDefaultProperties(prop));
      dhs.startDocument();
      owner.getOutputProcessor().setDocHandler(dhs);

      //owner.getOutputProcessor().setDocHandler(new DocHandlerSerializer(o, fillDefaultProperties(prop)));
      processFromFirst(xcont, node);
//      if (getFirst() != null) {
//        getFirst().process(xcont, node);
//      }

      dhs.endDocument();
      o.close();
      owner.getOutputProcessor().setDocHandler(h);

//      if (getNext() != null) {
//        getNext().process(xcont, node);
//      }
    } catch (FileNotFoundException e) {
      throw new XSLException(e.toString());
    } catch (OutputException e) {
      throw new XSLException(e.toString());
    } catch (IOException e) {
      throw new XSLException(e.toString());
    } catch (SAXException e) {
      throw new XSLException("SAXException!!" + e.toString());
    } catch (Exception e) {
      throw new XSLException("Exception:" + e.toString());
    }
  }

  public static Properties fillDefaultProperties(Properties p) {
    if (!p.containsKey(OutputKeys.METHOD)) {
      //      LogWriter.getSystemLogWriter().println("1");
      p.setProperty(OutputKeys.METHOD, "xml");
    }

    //    if(!p.containsKey(OutputKeys.VERSION)) {
    //      LogWriter.getSystemLogWriter().println("2");
    //      p.setProperty(OutputKeys.VERSION, "1");
    //    }
    if (!p.containsKey(OutputKeys.ENCODING)) {
      //      LogWriter.getSystemLogWriter().println("3");
      p.setProperty(OutputKeys.ENCODING, "utf-8");
    }

    //    if(!p.containsKey(OutputKeys.STANDALONE)) {
    //      LogWriter.getSystemLogWriter().println("4");
    //      p.setProperty(OutputKeys.STANDALONE, "yes");
    //    }
    //    if(!p.containsKey(OutputKeys.MEDIA_TYPE)) {
    //      LogWriter.getSystemLogWriter().println("5");
    //      p.setProperty(OutputKeys.MEDIA_TYPE, "text/xml");
    //    }
    //    if(!p.containsKey(OutputKeys.INDENT)) {
    //      LogWriter.getSystemLogWriter().println("6");
    //      p.setProperty(OutputKeys.INDENT, "yes");
    //    }
    //    if(!p.containsKey(OutputKeys.OMIT_XML_DECLARATION)) {
    //      LogWriter.getSystemLogWriter().println("7");
    //      p.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    //    }
    if (!p.containsKey(OutputKeys.CDATA_SECTION_ELEMENTS)) {
      //      LogWriter.getSystemLogWriter().println("8");
      p.setProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "");
    }

    //    if(!p.containsKey(OutputKeys.DOCTYPE_SYSTEM)) {
    //      LogWriter.getSystemLogWriter().println("9");
    //      p.setProperty(OutputKeys.DOCTYPE_SYSTEM, "csa");
    //    }
    //    if(!p.containsKey(OutputKeys.DOCTYPE_PUBLIC)) {
    //      LogWriter.getSystemLogWriter().println("10");
    //      p.setProperty(OutputKeys.DOCTYPE_PUBLIC, "csa1s");
    //    }
    //    LogWriter.getSystemLogWriter().println(p.get(OutputKeys.MEDIA_TYPE));
    return p;
  }

}

