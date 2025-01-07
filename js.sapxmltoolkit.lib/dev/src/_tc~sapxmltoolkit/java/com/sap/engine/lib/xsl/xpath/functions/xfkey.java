package com.sap.engine.lib.xsl.xpath.functions;

import java.util.*;
import com.sap.engine.lib.xsl.xpath.*;
import com.sap.engine.lib.xsl.xpath.xobjects.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xslt.XSLKey;

public final class XFKey implements XFunction {

  //private Hashtable keyHash = new Hashtable();
  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    context.owner.initializeKeys(context);
    XPathContext ctx = new XPathContext(context);

    if (a.length != 2) {
      throw new XPathException("First argument must be a two-element array!");
    }

    XString a0xs = a[0].toXString();
    XString a1xs = a[1].toXString();
    CharArray keyName = a0xs.getValue();
    CharArray shouldBe = a1xs.getValue();
    int shouldBeHash = shouldBe.hashCode();

    if (context.owner.getXFKeyHash().get(keyName) != null) {
      Hashtable h1 = (Hashtable) context.owner.getXFKeyHash().get(keyName);

      if (h1.get(shouldBe) != null) {
        a0xs.close();
        a1xs.close();
        return context.getXFactCurrent().getXNodeSet((XNodeSet) h1.get(shouldBe));
      }
    }

    //    LogWriter.getSystemLogWriter().println("XFKey: name=" + keyName + ", shouldBe=" + shouldBe);
    XSLKey[] keyArray = (XSLKey[]) (context.owner.keyArrays.get(keyName));
    XNodeSet[] nodesetArray = (XNodeSet[]) (context.owner.nodesetArrays.get(keyName));
    CharArray[][] keyValues = (CharArray[][]) (context.owner.keyValuesArrays.get(keyName));
    //ctx.globalCurrentNode = context.globalCurrentNode;
    //    XNodeSet[] nodesetArray = evaluateMatch(context, a[0].toXString().toString());
    //    LogWriter.getSystemLogWriter().println("[[[" + nodesetArray.length + "]]]");
    XNodeSet result = context.getXFactCurrent().getXNodeSet(context.dtm);
    result.clear();

    for (int i = 0; i < nodesetArray.length; i++) {
      CharArray[] ch = keyValues[i];

      for (int j = 0; j < nodesetArray[i].size(); j++) {
        //ctx.node = node;
        //XSLKey     key  = keyArray[i];    
        //XNodeSet   nset = nodesetArray[i]; 
        //      ETObject etUse = context.owner.etBuilder.process(key.getUse());
        //        ETObject etUse = key.getETUse();
        //        XObject pr = context.owner.getXPathProcessor().process(etUse, ctx);
        //        LogWriter.getSystemLogWriter().println("Should be: " + shouldBe);
        //        LogWriter.getSystemLogWriter().println("and is: " + ch[j]);
        if (shouldBeHash == ch[j].hashCode() && shouldBe.equals(ch[j])) {
          int node = nodesetArray[i].getKth(j + 1);
          //          LogWriter.getSystemLogWriter().println("Appending " + pr + " to result ...");
          //result.add(nodesetArray[i].getKth(j+1));
          result.add(node);
        }

        //result.uniteWith(pr);
      } 
    } 

    a0xs.close();
    a1xs.close();
    XNodeSet res2 = context.getXFactCurrent().getXNodeSet(result);
    Hashtable h1 = (Hashtable) context.owner.getXFKeyHash().get(keyName);

    if (h1 == null) {
      h1 = new Hashtable();
      context.owner.getXFKeyHash().put(keyName.copy(), h1);
    }

    h1.put(shouldBe.copy(), res2);
    //    LogWriter.getSystemLogWriter().println("Returning " + result);
    return result;
  }

  public String getFunctionName() {
    return "key";
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 2) ? true : false;
  }

  //  public XNodeSet[] evaluateMatch(XPathContext context, String KeyName) throws XPathException {
  //    XSLKey[] keys = (XSLKey[])context.owner.keyArrays.get(KeyName);
  //    XNodeSet[] result = new XNodeSet[keys.length];
  //    for(int i = 0; i < keys.length; i++) {
  //        XSLKey key = keys[i];
  //        ETObject etMatch = context.owner.etBuilder.process(key.getMatch());
  //    //    XNodeSet xo = (xpath.process(etMatch, context)).toXNodeSet();
  //      
  //        XNodeSet xo = (XNodeSet)(context.owner.getXPathProcessor().process(etMatch, context));
  //        LogWriter.getSystemLogWriter().println(xo.size());
  //        result[i] = xo;
  //      }
  //
  //    return result;
  //  }
  //  public void initializeKeys(XPathContext context) throws XPathException {
  //    Enumeration e = keyArrays.keys();
  //    while (e.hasMoreElements()) {
  //      
  //      String s = (String)e.nextElement();
  //      XSLKey[] keys = (XSLKey[])keyArrays.get(s);
  //      XNodeSet[] nodeSets = new XNodeSet[keys.length];
  //      for(int i = 0; i < keys.length; i++) {
  //        XSLKey key = keys[i];
  //        LogWriter.getSystemLogWriter().println(key.getMatch());
  //        ETObject etMatch = etBuilder.process(key.getMatch());
  //    //    XNodeSet xo = (xpath.process(etMatch, context)).toXNodeSet();
  //      
  //        XNodeSet xo = (XNodeSet)xpath.process(etMatch, context);
  //        LogWriter.getSystemLogWriter().println(xo.size());
  //        nodeSets[i] = xo;
  //      }
  //      
  //      nodesetArrays.put(s, nodeSets);
  //    }
  //    
  //    /*ETObject etMatch = etBuilder.process(key.getMatch());
  //    XObject xo = xpath.process(etMatch, context);
  //    
  //    ETObject etUse = etBuilder.process(key.getUse());*/
  //
  //  }
  /*  xc.node = i;
   XSKey key = context.owner.getKey(a[0].toXString().toString());
   XObject pr = context.owner.getXPathProcessor().process(key.getEtUse(), xc);
   pr.toXString();*/

}

