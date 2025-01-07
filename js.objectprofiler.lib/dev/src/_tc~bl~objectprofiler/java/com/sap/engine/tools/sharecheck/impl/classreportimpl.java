package com.sap.engine.tools.sharecheck.impl;

import com.sap.engine.tools.sharecheck.ClassReport;

import java.util.List;


/**
 */
public class ClassReportImpl implements ClassReport {

  Class clazz;
  int shareable = 0;

  Throwable nonShareableError;

  /**
   * @param clazz class instance
   * @param shareable is shareble - old
   */
  ClassReportImpl(Class clazz, int shareable) {
    this.clazz = clazz;
    this.shareable = shareable;
  }

  void setCheckError(Throwable t) {
    this.nonShareableError = t;
  }

  public Class getClassObject() {
    return clazz;
  }

  public int getShareabilityProblemMask() {
    return shareable;
  }

  public Throwable getShareabilityError() {
    return nonShareableError;
  }

  /*
  * List of Strings .Each string is an error message.
  */
  public List getProblems() {
    return SessionSerializationReportImpl.getProblemList(shareable);
  }


  public String toString() {
    return "[ClsssReport] " + clazz.getName() + " , shareability= " + shareable;
  }

}