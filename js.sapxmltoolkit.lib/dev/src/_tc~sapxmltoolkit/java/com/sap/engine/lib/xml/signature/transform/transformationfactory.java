package com.sap.engine.lib.xml.signature.transform;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;

public class TransformationFactory {
  
  public HashMap dataHashmap = null;
  public Vector customTransforms = null;

  public static TransformationFactory newInstance() {
    return new TransformationFactory();
  }
   
  protected TransformationFactory() {
    dataHashmap = new HashMap();
    customTransforms = new Vector();
  }
  
  public void registerTransformation(Transformation tr) {
    synchronized (customTransforms){
      if (!customTransforms.contains(tr)){
        customTransforms.add(tr);
      }
    }
  }
  
  public Transformation getInstance(String uri, Object[] additionalArgs) throws SignatureException {
    synchronized (customTransforms){
      int length = customTransforms.size();
      for (int i = 0; i < length; i ++) {
        Transformation next = (Transformation) customTransforms.get(i);
        if(uri.equals(next.uri)) {
          Transformation cloned = next.defineFrom(additionalArgs,dataHashmap);
          return cloned;
        }
      }
    }
    return Transformation.getInstance(uri, additionalArgs);

  }
  public Transformation getInstance(GenericElement e) throws SignatureException {
    Transformation custom = getCustom(e);
    if (custom != null) {
      return custom;
    }
    
    String uri = e.getAttribute("Algorithm", null, null);

    if (uri == null) {
      throw new SignatureException("Attribute \"Algorithm \" not found on element " + ((Element) e.getDomRepresentation()).getLocalName(), new Object[]{e});
    }

    Object[] additionalArgs = null;

    if (uri.equals(Constants.TR_XPATH)) {
      GenericElement xpath = e.getDirectChild(Constants.SIGNATURE_SPEC_NS, "XPath");
      String query = xpath.getNodeValue();
      Hashtable mappings = xpath.getNamespaceMappingsInScope();

      additionalArgs = new Object[2];
      additionalArgs[0] = query;
      additionalArgs[1] = mappings;
    } else if (uri.startsWith(Constants. TR_C14N_EXCL_OMIT_COMMENTS)){
      GenericElement inclusiveNamespaces = e.getDirectChild(Constants.TR_C14N_EXCL_OMIT_COMMENTS, "InclusiveNamespaces");
      if (inclusiveNamespaces!=null){
      Hashtable attribs = inclusiveNamespaces.getAttributes();
      String prefixList = (String) attribs.get("PrefixList");
      if (prefixList!=null) {
      	StringTokenizer tokenizer = new StringTokenizer(prefixList);
				String[] iN = new String[tokenizer.countTokens()];
      	for(int i = 0; i < iN.length; i++) {
      		iN[i] = tokenizer.nextToken();
      	}
        additionalArgs = new Object[]{iN};
      }
      
      }
    } else if (uri.startsWith(Constants.DECRYPT_SPEC_NS)){
      Element el = (Element) e.getDomRepresentation();
      NodeList except = el.getElementsByTagNameNS(Constants.DECRYPT_SPEC_NS,"Except");
      additionalArgs = new Object[except.getLength()];
      for(int i =0;i<additionalArgs.length;i++){
        additionalArgs[i] = ((Element) except.item(i)).getAttribute("URI");
      }
    } else if (Constants.TR_ENVELOPED_SIGNATURE.equals(uri)){
      additionalArgs = new Object[]{e.getDomRepresentation()};
    }


    return getInstance(uri, additionalArgs);
  }
  
  public Transformation getCustom(GenericElement ge) throws SignatureException {
    synchronized (customTransforms){
      int length = customTransforms.size();
      for (int i = 0; i < length; i ++) {
        Transformation next = (Transformation) customTransforms.get(i);
        Transformation cloned = next.defineFrom(ge, dataHashmap);
        if (cloned != null) {
          return cloned;
        }
      }
    }
    return null;
  }

  public void add(String key, Object value) {
    synchronized (dataHashmap){
      dataHashmap.put(key, value);
    }
  }


}

