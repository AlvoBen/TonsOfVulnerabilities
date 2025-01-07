package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;

/**
 * Title:        xml2000
 * Description:  org.w3c.dom.* ;
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Chavdar Baykov, Chavdarb@abv.bg
 * @version      August 2001
 * @deprecated Use org.w3c.dom.* official API.
 */
@Deprecated
public class NodeListImplEmpty implements NodeList, java.io.Serializable {

  private NodeListImplEmpty(){
    
  }
  
  public int getLength() {
    return 0;
  }

  public Node item(int i) {
    return null;
  }
  
  protected static NodeListImplEmpty cache = new NodeListImplEmpty();
  
  public static NodeListImplEmpty getInstance(){
    return cache;
  }

}

