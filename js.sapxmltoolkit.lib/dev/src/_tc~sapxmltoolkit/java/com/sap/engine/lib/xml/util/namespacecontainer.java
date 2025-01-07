/**
 * Title:        xml2000
 * Description:  Serves as namespace container
 *
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Chavdar Baykov, Chavdarb@abv.bg
 * @version      July 2001
 */
package com.sap.engine.lib.xml.util;

import java.util.Hashtable;
import java.util.ArrayList;

public class NamespaceContainer {

  Hashtable prefixHash = new Hashtable();

  public void mapPrefix(String prefix, String uri) {
    ArrayList prefixList = (ArrayList) prefixHash.get(prefix);

    if (prefixList == null) {
      prefixList = new ArrayList();
      prefixList.add(uri);
      prefixHash.put(prefix, prefixList);
    } else {
      prefixList.add(uri);
    }
  }

  public void demapPrefix(String prefix) throws Exception {
    ArrayList prefixList = (ArrayList) prefixHash.get(prefix);

    if (prefixList == null) {
      throw new Exception(" Name space error !" + prefix + " never mapped ");
    } else {
      prefixList.remove(prefixList.size() - 1);

      if (prefixList.size() == 0) {
        prefixHash.remove(prefix);
      }
    }
  }

  public String getPrefixURI(String prefix) {
    ArrayList prefixList = (ArrayList) prefixHash.get(prefix);

    if (prefixList != null) {
      return (String) prefixList.get(prefixList.size() - 1);
    }

    return null;
  }

}

