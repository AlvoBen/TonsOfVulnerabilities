/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.objectprofiler.graph;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * @author Georgi Stanev, Mladen Droshev
 * @version 7.10C
 */
public class ClassData implements Serializable{

  private String className = null;
  private ClassData parentClass = null;

  static final long serialVersionUID = 2694034680559651294L;

  public ClassData(Class current) {
    this.className = Node.formGenericType(current); //current.getName()
    Class superClass = current.getSuperclass();
    if (superClass == null || superClass.isAssignableFrom(Object.class)) {
      this.parentClass = null;
    } else {
      this.parentClass = new ClassData(current.getSuperclass());
    }
  }

  public ClassData(String className, ClassData parent){
    this.className = className;
    this.parentClass = parent;
  }

  public String getClassName() {
    return className;
  }

  public ClassData getParentClass() {
    return parentClass;
  }

  public String[] getInfo(){
    ArrayList res = new ArrayList();
    res.add("ClassName: " + className);
    if(parentClass != null){
      String[] dd = parentClass.getInfo();
      for (int i = 0; i < dd.length; i++) {
        res.add("  " + dd[i]);

      }
    }
    return (String[])res.toArray(new String[0]);
  }
}
