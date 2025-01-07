package com.sap.engine.lib.xml.signature.transform.algorithms;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import org.w3c.dom.Node;
import com.sap.engine.lib.xml.parser.handlers.NamespaceHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.signature.Data;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.transform.Transformation;
import com.sap.engine.lib.xsl.xpath.*;
import com.sap.engine.lib.xsl.xpath.xobjects.XBoolean;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xslt.NamespaceManager;

public class XPathTransformation extends Transformation {
  private Hashtable mappings = new Hashtable();
  
  public XPathTransformation(Object[] args) {
    super(args);
  }
  
  public XPathTransformation() {
    this(null);
  }
  
  public void addPrefixMapping(String prefix, String $uri) {
    mappings.put(prefix, $uri);
  }
  
  public void setPrefixMappings(Hashtable newMappings) {
    this.mappings = newMappings;
  }
  
  public void setExclusive(boolean exclusive){
    exclusiveX = exclusive;
  }
  
  public boolean getExclusive(){
    return exclusiveX;
  }
  
  boolean exclusiveX = false;
  
  public void transform(Data data) throws SignatureException {
    if (additionalArgs.length != 2 || !(additionalArgs[0] instanceof String) || (additionalArgs[1] != null && !(additionalArgs[1] instanceof Hashtable))) {
      throw new SignatureException("XPathTransformation expects a two-elemnent array, containing a xpath query and a hashtable of prefix mappings", new java.lang.Object[]{data, additionalArgs});
    }
    String query = (String) additionalArgs[0];
    Hashtable bindings = null;
    if (additionalArgs[1] != null) {
      bindings = (Hashtable) additionalArgs[1];
    } else {
      bindings = this.mappings;
    }
    InputStream is = new ByteArrayInputStream(data.getOctets());
    data.setOctets(filterSetSpecialToOctets(is, query, bindings, exclusiveX, null, true, true));
  }
  
  public static byte[] filterSet(InputStream input, String query, Hashtable prefixMappings, boolean retainComments) throws SignatureException {
    return filterSetSpecial(input, query, prefixMappings, false, null, retainComments);
  }
  
  public static byte[] filterSetSpecial(InputStream input, String query, Hashtable prefixMappings, boolean exclusive, String[] specialPrefixes, boolean retainComments) throws SignatureException {
    return filterSetSpecialToOctets(input, query, prefixMappings, exclusive, specialPrefixes, retainComments, !exclusive); // not exclusive!!!!
  }
  
  private synchronized static byte[] filterSetSpecialToOctets(InputStream input, String query, Hashtable prefixMappings, boolean exclusive, String[] specialPrefixes, boolean retainComments, boolean retainNamespaces) throws SignatureException {
    try {
      SignatureContext.excluded = 0;
      NamespaceHandler nh = new NamespaceHandler(null);
      Enumeration vars = prefixMappings.keys();
      while (vars.hasMoreElements()) {
        String nextVar = (String) vars.nextElement();
        String nextVal = (String) prefixMappings.get(nextVar);
        nh.add(new CharArray(nextVar), new CharArray(nextVal));
      }
      DTM dtm = SignatureContext.getDTM();
//      if (dtm == null) {
//        dtm = new DTM();
//      } else {
//        dtm.clearDirty();
//      }
      DTMFactory factory= SignatureContext.getDTMFactory();
//      if (factory == null) {
//        factory = new DTMFactory();
//      }
      XPathContext ctx;
      XPathProcessor processor;
      ETObject processed;
      NamespaceManager nm = SignatureContext.getNamespaceManager();
//      if (nm == null) {
//        nm = new NamespaceManager();
//      } else {
//        nm.reuse();
//      }
      factory.initializeDirty(dtm, nm, input);
      ctx = dtm.getInitialContext();
      ctx.node = dtm.firstChild[0];
      processor = new XPathProcessor(dtm);
//      if (etBuilder == null) {
//        etBuilder = new ETBuilder();
//      } 
      ETBuilder etBuilder = SignatureContext.getETBuilder();
      etBuilder.setNamespaceStuff(nm, nh);
      processed = etBuilder.process(query);
      ctx.dtm = dtm;
      int length = dtm.size;
      XObject obj = processor.process(processed, ctx);
      int[] tempList = null;
      if ((obj instanceof XNodeSet)&&((XNodeSet) obj).size()!=0){
        XNodeSet nodeSet = (XNodeSet) obj;
        tempList = new int[length - nodeSet.count()];
        if (tempList.length == length) {
          // no nodes left after xpath
          return new byte[0];
        }
        int index = 0;
        int tempIndex = 1;
        for (int i = 0; i < length; i++) {
          int k = tempIndex > nodeSet.count() ? length : nodeSet.getKth(tempIndex);
          if (i < k) {
            tempList[index++] = i;
          } else if (i == k) {
            tempIndex++;
          }
        }
      } else {
        for (int i = dtm.firstChild[0]; i < length; i++) {
          ctx.node = i;
          XObject res = processor.process(processed, ctx); //XNODESET!!!
                                                           // TODO:test
          res = res.toXBoolean();
          if (!((XBoolean) res).getValue()) {
            if (SignatureContext.excluded == SignatureContext.excludeList.length) {
              int[] excludeListTemp = new int[SignatureContext.excluded + SignatureContext.ARRAY_SIZE];
              System.arraycopy(SignatureContext.excludeList, 0, excludeListTemp, 0, SignatureContext.excluded);
              SignatureContext.excludeList = excludeListTemp;
            }
            SignatureContext.excludeList[SignatureContext.excluded++] = i;
          }
        }
        tempList = new int[SignatureContext.excluded];
        System.arraycopy(SignatureContext.excludeList, 0, tempList, 0, SignatureContext.excluded);
      }
      // 30.04.2004
      DTMDOMBuilder domBuilder = new DTMDOMBuilder(dtm);
      domBuilder.reinitializeDOM(tempList, retainNamespaces, specialPrefixes, exclusive);
      Node n = domBuilder.getFragment();
// obsolete by 30.04.2004      
//      dtm.reinitializeDOM(tempList, retainNamespaces, specialPrefixes, exclusive);
//      Node n = dtm.getFragment();
      StringBuffer leadingText = dtm.getLeadingText();
      byte[] octets2 = Canonicalization.canonicalize(n, retainComments);
      // fix - no more left!
//      dtm.clearDirty();
//      nm.reuse();
      //LogWriter.getSystemLogWriter().println("B"+etBuilder.xobjectsPool.xStringPool.getSize() + ":"+etBuilder.xobjectsPool.xStringPool.getUsed());
//      LogWriter.getSystemLogWriter().println("D"+dtm.getCapacity() + ":"+((dtm.name==null)?0:dtm.name.length));
// see what happens with ET builder factories!!!!
      return appendDataOctets(leadingText.toString().getBytes(), octets2/* data2 */); //$JL-I18N$
    } catch (SignatureException e) {
      e.printStackTrace();
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new SignatureException("Error in XPathTransformation", new Object[]{input, query, prefixMappings,}, e);
    }
  }
  
  public static byte[] appendDataOctets(byte[] octets1, byte[] octets2) {
    if (octets1 == null || octets1.length == 0) {
      return octets2;
    }
    if (octets2 == null || octets2.length == 0) {
      return octets1;
    }
    int length1 = octets1.length;
    int length2 = octets2.length;
    int length3 = length1 + length2;
    byte[] octets3 = new byte[length3];
    System.arraycopy(octets1, 0, octets3, 0, length1);
    System.arraycopy(octets2, 0, octets3, length1, length2);
    return octets3;
  }
  
  public Transformation defineFrom(GenericElement el, HashMap $dataHashmap) throws SignatureException {
    throw new SignatureException("defineFrom not implemented for standard transformation: XPathTransformation!", new java.lang.Object[]{el, $dataHashmap});
  }
}
