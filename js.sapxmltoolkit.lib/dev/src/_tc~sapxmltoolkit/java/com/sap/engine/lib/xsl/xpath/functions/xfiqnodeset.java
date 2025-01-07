package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.DTMFactory;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFIQNodeset implements XFunction {

  public boolean confirmArgumentTypes(XObject[] a) {
    return ((a.length == 1) || (a.length == 2));
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    XNodeSet r = context.getXFactCurrent().getXNodeSet(context.dtm);
    r.clear();
    //LogWriter.getSystemLogWriter().println("XFIQNodeset.execut a[0]=" + a[0].getClass());
    String s = a[0].toXString().getValue().getString();
    //Integer x = (Integer) context.dtm.xfDocumentCache.get(s);
    //LogWriter.getSystemLogWriter().println("XFDocument s= " + s + " , x= " + x);
    //if (x != null) {
    //r.add(x.intValue());
    //} else {
    ///Source source = null;
    //try {
    //        source = context.owner.getURIResolver().resolve(s, context.owner.getBaseURI());
    //} catch (Exception e) {
    /*
     e.printStackTrace();
     return r;
     */
    //        throw new XPathException("Unable to load external XML source" + e);
    //      }
    //LogWriter.getSystemLogWriter().println("XFIQNodeSet.execute: s=" + s);  
    Source source = new StreamSource(new StringReader(s));
    int index = (new DTMFactory()).appendDocument(context.dtm, source, true);
    //r.add(context.dtm.firstChild[index]);
    r.add(index);
    //      LogWriter.getSystemLogWriter().println("IQNodeset: index=" + index);
    int fc = context.dtm.firstChild[index];

    for (int i = context.dtm.firstChild[index]; i != -1; i = context.dtm.nextSibling[i]) {
      r.add(i);
    } 

    //      LogWriter.getSystemLogWriter().println("IQNodeset: fc=" + fc);
    //      LogWriter.getSystemLogWriter().println("IQNodeset: nextsibling=" + context.dtm.name[fc] + ", " +(fc=context.dtm.nextSibling[fc]));
    //      LogWriter.getSystemLogWriter().println("IQNodeset: nextsibling=" + context.dtm.name[fc] + ", " +(fc=context.dtm.nextSibling[fc]));
    //      LogWriter.getSystemLogWriter().println("IQNodeset: nextsibling=" + context.dtm.name[fc] + ", " +(fc=context.dtm.nextSibling[fc]));
    //      LogWriter.getSystemLogWriter().println("IQNodeset: nextsibling=" + context.dtm.name[fc] + ", " +(fc=context.dtm.nextSibling[fc]));
    //      LogWriter.getSystemLogWriter().println("IQNodeset: nextsibling=" + context.dtm.name[fc] + ", " +(fc=context.dtm.nextSibling[fc]));
    //      LogWriter.getSystemLogWriter().println("IQNodeset: nextsibling=" + context.dtm.name[fc] + ", " +(fc=context.dtm.nextSibling[fc]));
    //      LogWriter.getSystemLogWriter().println("IQNodeset: nextsibling=" + context.dtm.name[fc] + ", " +(fc=context.dtm.nextSibling[fc]));
    //context.owner.urlLoader.pop();
    //context.dtm.xfDocumentCache.put(s, new Integer(index));
    //    }
    return r;
  }

  public String getFunctionName() {
    return "iq-nodeset";
  }

}

