package com.sap.engine.lib.xml.dom.xpath;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sap.engine.lib.xml.SystemProperties;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version Feb 8, 2002, 9:36:32 AM
 */
public final class XPointerEvaluator {

  private Vector vSchemes = new Vector();
  private Vector vExpressions = new Vector();
  private XPathEvaluatorImpl evaluator = new XPathEvaluatorImpl(); //xxx
  private XPathResultImpl result = evaluator.createResult();
  private Hashtable documents = new Hashtable();
  private DocumentBuilder builder;

  public XPointerEvaluator() throws ParserConfigurationException {
    SystemProperties.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl");
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
    builder = factory.newDocumentBuilder();
  }

  public Node evaluateToNode(String url) throws IOException, SAXException {
    NodeList nl = evaluateToNodeList(url);
    return (nl.getLength() > 0) ? nl.item(0) : null;
  }

  public NodeList evaluateToNodeList(String url) throws IOException, SAXException {
    int p = url.indexOf('#');
    String query = (p == -1) ? "xpointer(/)" : url.substring(p + 1);
    url = (p == -1) ? url : url.substring(0, p);
    Document document = (Document) documents.get(url);

    if (document == null) {
      document = builder.parse(new URL(url).openStream());
      documents.put(url, document);
    }

    XPathResultImpl res = evaluate(document, query);
    NodeList nl = res.getSetSnapshot(false).getNodeList();

    if (nl.getLength() > 0) {
      documents.put(url, nl.item(0).getOwnerDocument());
    }

    return nl;
  }

  public XPathResultImpl evaluate(Document document, String query) throws XPathException {
    vSchemes.clear();
    vExpressions.clear();
    XPointerSplitter.split(query, vSchemes, vExpressions);
    XPathNSResolverImpl resolver = new XPathNSResolverImpl(document.getDocumentElement());
    int n = vSchemes.size();

    for (int i = 0; i < n; i++) {
      if ("xmlns".equals(vSchemes.get(i))) {
        String s = (String) vExpressions.get(i);
        int p = s.indexOf('=');

        if (p == -1) {
          continue;
        }

        resolver.addNamespaceMapping(s.substring(0, p).trim(), s.substring(p + 1).trim());
      } else if ("xpointer".equals(vSchemes.get(i))) {
        result = evaluator.evaluate((String) vExpressions.get(i), document.getDocumentElement(), resolver, XPathResultImpl.ANY_TYPE, result);

        if (result.getResultType() == XPathResultImpl.NODE_SET_TYPE) {
          if (result.getSetSnapshot(false).getLength() == 0) {
            continue;
          }
        }

        return result;
      }
    } 

    return null;
  }

  public Hashtable getDocuments() {
    return documents;
  }

}

