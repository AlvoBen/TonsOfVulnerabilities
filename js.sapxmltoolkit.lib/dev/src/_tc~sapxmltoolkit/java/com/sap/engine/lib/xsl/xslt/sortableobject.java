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
package com.sap.engine.lib.xsl.xslt;

/**
 *
 <!ELEMENT xsl:copy-of EMPTY>
 <!ATTLIST xsl:copy-of select %expr; #REQUIRED>
 *
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 * @version 0.0.1
 *
 *
 * First Edition: 17.01.2001
 *
 */
import java.util.Vector;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

public final class SortableObject implements Comparable {

  protected Vector keys = null;
  protected int node = -1;

  public SortableObject() {
    keys = new Vector();
  }

  public SortableObject reuse() {
    keys.clear();
    node = -1;
    return this;
  }

  public void addKey(SortKey key) {
    keys.add(key);
  }

  public void setNode(int node) {
    this.node = node;
  }

  public int getNode() {
    return node;
  }

  public SortKey getKey(int i) {
    return (SortKey) keys.get(i);
  }

  public Vector getKeys() {
    return keys;
  }

  public int compareTo(Object anob) {
    SortableObject sobj = (SortableObject) anob;

    for (int i = 0; i < keys.size(); i++) {
      SortKey mykey = getKey(i);
      SortKey otkey = sobj.getKey(i);
      double res;

      if (mykey.getDataType() == XSLSort.TYPE_TEXT) {
        CharArray s1 = mykey.getTextKey();
        CharArray s2 = otkey.getTextKey();

        if ((res = s1.compareToIgnoreCase(s2)) == 0) {
          for (int j = 0; j < s1.length(); j++) {
            if ((res = s1.charAt(j) - s2.charAt(j)) != 0) {
              if (Character.isLowerCase(s1.charAt(j))) {
                if (mykey.getCaseOrder() == XSLSort.CASE_DOWN) {
                  res = -res;
                }
              }

              //if (mykey.getOrder() == XSLSort.ORD_DESC) {
              //res = -res;
              //}
              break;
            }
          } 
        }

        //r//es = .compareTo(otkey.getTextKey());
      } else {
        res = mykey.getNumKey() - otkey.getNumKey();
      }

      if (res == 0) {
        continue;
      } else {
        if (mykey.getOrder() == XSLSort.ORD_ASC) {
          return (int) res;
        } else {
          return (int) -res;
        }
      }
    } 

    return node - sobj.getNode();
  }

}

