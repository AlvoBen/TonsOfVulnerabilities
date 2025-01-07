/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.core.application;

import java.util.ArrayList;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class is used by the Portlet Cintainer when the portlet definition
 * does not define a resource bundle and the portlet information is defined 
 * inline in the deployment descriptor. In this case the Portlet Container 
 * creates this ResourceBundle and populate it with the inline values.
 *  
 * @author diyan-y
 * @version 7.10
 */
public class DefaultResourceBundle extends ListResourceBundle {

  public final static String TITLE = "javax.portlet.title";
  public final static String SHORT_TITLE = "javax.portlet.short-title";
  public final static String KEYWORDS = "javax.portlet.keywords";
  
  private Object[][] resources;
  
  /**
   * Creates a new resource bundle on top of the specified bundle.
   * @param title the title that should be displayed in the titlebar of the portlet.
   * @param shortTitle a short version of the title.
   * @param keywords keywords describing the functionality of the portlet.
   * @param bundle the base resource bundle.
   */
  public DefaultResourceBundle(String title, String shortTitle, String keywords, ResourceBundle bundle) {
    this(title, shortTitle, keywords);    
    super.setParent(bundle);
  }
 
  /**
   * Creates default resource bundle with title and optional shortTitle and keywords.
   * 
   * @param title the title that should be displayed in the titlebar of the portlet.
   * @param shortTitle a short version of the title.
   * @param keywords keywords describing the functionality of the portlet.
   */
  public DefaultResourceBundle(String title, String shortTitle, String keywords) {
    Vector v  = new Vector();
    if (title != null) {
      v.add(TITLE);
    }
    if (shortTitle != null) {
      v.add(SHORT_TITLE);
    }
    if (keywords != null) {
      v.add(KEYWORDS);
    }
    resources = new Object[v.size()][2];
    for(int i = 0; i < v.size(); i++) {
      resources[i][0] = v.get(i);
      if (TITLE.equals(v.elementAt(i))) {
        resources[i][1] = title;
      } else if (SHORT_TITLE.equals(v.elementAt(i))) {
        resources[i][1] = shortTitle;
      } else if (KEYWORDS.equals(v.elementAt(i))) {
        resources[i][1] = getKeywords(keywords);
      }
    }
  }

  
  /**
   * Returns the resource bundle contents.
   * @return an array, where each item in the array is a pair of objects.
   * The first element of each pair is the key, which must be a
   * <code>String</code>, and the second element is the value associated with
   * that key. 
   */
  protected Object[][] getContents() {
    return resources;
  }
  
  /**
   * Returns the keywirds defined for this resource bundle.
   * @param keywordString a list of keywords.
   * @return an array of keywords.
   */
  private String[] getKeywords(String keywordString) {
    ArrayList keywords = new ArrayList();
    StringTokenizer tokenizer = new StringTokenizer(keywordString, ",");
    while (tokenizer.hasMoreTokens()){
      keywords.add(tokenizer.nextToken());
    }
    String[] result = new String[keywords.size()];
    result = (String[]) keywords.toArray(result); 
    return result;
  }
}
