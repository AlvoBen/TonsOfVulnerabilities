package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.DTMFactory;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import org.w3c.dom.Document;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFDocument implements XFunction {

  public boolean confirmArgumentTypes(XObject[] a) {
    return ((a.length == 1) || (a.length == 2));
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    XNodeSet r = context.getXFactCurrent().getXNodeSet(context.dtm);
    r.clear();
    String s = a[0].toXString().getValue().getString();
    Integer x = (Integer) context.dtm.xfDocumentCache.get(s);
    //    x = null;
    //    LogWriter.getSystemLogWriter().println("XFDocument s= " + s +", base=" + context.owner.getSourceBaseURI() + ", x= " + x);
    String base = null;

    if (a.length == 1) {
      if (context.currentNode != null && context.currentNode.realBaseURI != null) {
        base = context.currentNode.realBaseURI;
      } else {
        base = context.owner.getBaseURI();
      }
    } else {
      base = context.owner.getSourceBaseURI();
    }

    //    LogWriter.getSystemLogWriter().println("XFDocument s= " + s +", base=" + context.owner.getSourceBaseURI() + ", x= " + x);
    if (x != null) {
      r.add(x.intValue());
    } else {
      Source source = null;
      try {
        source = context.owner.getURIResolver().resolve(s, base);

        if (source == null) {
          source = context.owner.getDefaultResolver().resolve(s, base);
        } 
        
        if (source != null && source.getSystemId() == null && s != null) {
          source.setSystemId(s);
        }

        if (source == null) {
          return r;
        } else if (source instanceof DOMSource) {
          DOMSource ds = (DOMSource) source;

          if (ds.getNode() == null || (ds.getNode() instanceof Document && ds.getNode().getChildNodes().getLength() == 0)) {
            return r;
          }
        } else if (source instanceof StreamSource) {
          StreamSource ss = (StreamSource) source;
          if (ss.getInputStream() == null && ss.getReader() == null && ss.getSystemId() == null) {
            return r;
          }
        } else if (source instanceof SAXSource) {
          SAXSource ss = (SAXSource) source;
          if (ss.getInputSource() == null && ss.getXMLReader() == null && ss.getSystemId() == null) {
            return r;
          }
        }
      } catch (Exception e) {
        //$JL-EXC$
        try {
          context.owner.sendWarning("Exception while loading External XMLSource in 'document': " + e);
          return r;
        } catch (Exception ez) {
          throw new XPathException("Exception while sending warning. in 'document' - function", e);
        }
        //throw new XPathException("Unable to load external XML source" + e);
      }
      DTMFactory dtmfact = context.owner.getDTMFactory();
      dtmfact.setDTM(context.dtm);
      int index = dtmfact.appendDocument(context.dtm, source);
      r.add(index);
      //context.owner.urlLoader.pop();
      //      LogWriter.getSystemLogWriter().println("XFDocument : Returnining node:" + index + ", type=" + context.dtm.nodeType[index] + ", DOC=" + context.dtm.DOCUMENT_NODE);
      context.dtm.xfDocumentCache.put(s, new Integer(index));
    }

    return r;
  }

  public String getFunctionName() {
    return "document";
  }

}

