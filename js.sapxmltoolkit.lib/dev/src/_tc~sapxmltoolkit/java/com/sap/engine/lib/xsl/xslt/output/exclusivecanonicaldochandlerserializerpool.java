/*
 * Created on 2004-3-24
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xsl.xslt.output;

import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *  
 */
public class ExclusiveCanonicalDocHandlerSerializerPool extends CanonicalDocHandlerSerializerPool{


  public ExclusiveCanonicalDocHandlerSerializer get(OutputStream os, Properties ps, String[] inclusiveNamespaces,Hashtable namespacesInScope) throws OutputException {
    ExclusiveCanonicalDocHandlerSerializer serializer;// = new Encoder();

    synchronized (freePool) {
      int size = freePool.size();
      if (size > 0) {

        serializer = (ExclusiveCanonicalDocHandlerSerializer) freePool.remove(size - 1);
        serializer.init(os, ps, inclusiveNamespaces, namespacesInScope);
      } else {
        serializer = new ExclusiveCanonicalDocHandlerSerializer();
        serializer.init(os, ps, inclusiveNamespaces, namespacesInScope);
      }
    }

    return serializer;
  }
}
