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
import java.util.Arrays;
import java.util.Vector;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.XPathProcessor;
import com.sap.engine.lib.xsl.xpath.xobjects.XNumber;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xslt.pool.ObjectPool;

public final class Sorter {

  public ObjectPool sortableObjectPool = new ObjectPool(new SortableObject().getClass(), 100, 100);
  public ObjectPool sortKeyPool = new ObjectPool(new SortKey().getClass(), 100, 100);
  XPathContext newxcont = new XPathContext();

  public void sort(XPathContext xcont, int[] nodes, int beg, int len, Vector sortNodes, XPathProcessor xproc) throws XPathException {
    //LogWriter.getSystemLogWriter().println(nodes.count());
    SortableObject arr[] = new SortableObject[len];
    int inode;

    //IntSetIterator iter = nodes.iterator();
    for (int i = 0; i < arr.length; i++) {
      SortableObject obj = ((SortableObject) sortableObjectPool.getObject()).reuse();
      inode = nodes[i + beg];

      for (int j = 0; j < sortNodes.size(); j++) {
        XSLSort xs = (XSLSort) sortNodes.get(j);

        if (xs.getDataType() == XSLSort.TYPE_TEXT) {
          newxcont.reuse(xcont, inode, i);
          CharArray strKey = xproc.process(xs.getSelect(), newxcont, xs.getVarContext()).toXString().getValue();
          //LogWriter.getSystemLogWriter().println(strKey)
          //obj.addKey(new SortKey(strKey, xs.getOrder(), xs.getCaseOrder(), xs.getLang(), xs.getDataType()));
          obj.addKey(((SortKey) sortKeyPool.getObject()).reuse(strKey, xs.getOrder(), xs.getCaseOrder(), xs.getLang(), xs.getDataType()));
        } else if (xs.getDataType() == XSLSort.TYPE_NUMBER) {
          newxcont.reuse(xcont, inode, i);
          XObject xo = xproc.process(xs.getSelect(), newxcont, xs.getVarContext());
          //XString xss = xo.toXString();
          XNumber xn = xo.toXNumber();
          double numKey = (Double.isNaN(xn.getValue())) ? Double.MAX_VALUE : xn.getValue();
          obj.addKey(((SortKey) sortKeyPool.getObject()).reuse(numKey, xs.getOrder(), xs.getCaseOrder(), xs.getLang(), xs.getDataType()));
        }
      } 

      obj.setNode(inode);
      arr[i] = obj;
    } 

    Arrays.sort(arr);

    //nodes.clear();
    for (int i = 0; i < arr.length; i++) {
      nodes[i + beg] = arr[i].getNode();
      Vector v = arr[i].getKeys();

      for (int j = 0; j < v.size(); j++) {
        sortKeyPool.releaseObject(v.get(j));
      } 

      sortableObjectPool.releaseObject(arr[i]);
    } 

    //iter = nodes.iterator();
    //LogWriter.getSystemLogWriter().println("co99999999999999unt = " + nodes.count());
    //while (iter.hasNext()) {
    // int bb=iter.next();
    // LogWriter.getSystemLogWriter().println(bb + " - " + ((Text)xcont.dtm.table[bb+1]).getData());
    // LogWriter.getSystemLogWriter().println("count = " + nodes.count());
    // }
  }

}

