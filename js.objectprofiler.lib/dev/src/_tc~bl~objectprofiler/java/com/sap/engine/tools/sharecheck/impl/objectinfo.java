package com.sap.engine.tools.sharecheck.impl;

import java.util.*;
import java.util.logging.*;

import com.sap.engine.tools.sharecheck.*;

public class ObjectInfo implements ObjectReport {

  static Logger logger = Logger.getLogger("com.sap.engine.tools.sharecheck.impl.ObjectInfo");


  //ClassInfo classInfo ;
  Object obj;
  ArrayList allf;
  LinkedList path;

  int shareable = 0;


  Throwable nonShareableError;
  Throwable nonSerializableError;

  StringBuffer pathToString;

  public ObjectInfo(int shareable, Object obj) {
    this.shareable = shareable;
    this.obj = obj;
  }


  public Object getObject() {
    return obj;
  }


  public String getClassName() {
    return obj.getClass().getName();
  }

  public void setPath(LinkedList path) {
    this.path = path;
  }

  public List getPath() {
    return path;
  }


  /*
  * List of Strings .Each string is an error message.
  */
  public List getProblems() {
    return SessionSerializationReportImpl.getProblemList(shareable);
  }


  public int getShareabilityProblemMask() {
    return shareable;
  }

  public Throwable getSerializationError() {
    return nonSerializableError;
  }

  public Throwable getShareabilityError() {
    return nonShareableError;
  }

  public String toString() {
    return "[" + obj.getClass().getName() + "] , shareable = " + shareable;
  }

  public String getPathAsString() {
    return toPathString(path);
  }


  public static String toPathString(LinkedList pathList) {
    StringBuffer pathToString = new StringBuffer();

    if (pathList != null) {
      pathToString.append("/");
      Iterator it = pathList.iterator();

      int i = 0;
      for (; i < pathList.size() - 1; i++) {
        pathToString.append(it.next());
        pathToString.append("/");
      }//for

      if (pathList.size() > 0) {
        pathToString.append(it.next());
      }
    } else {
      pathToString.append("<Path Not Found>");
    }

    //pathToString.append(obj.getClass().getName());
    return pathToString.toString();
  }

}
