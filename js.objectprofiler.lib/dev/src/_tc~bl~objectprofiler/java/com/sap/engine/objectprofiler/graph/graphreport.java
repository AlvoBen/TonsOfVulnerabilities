package com.sap.engine.objectprofiler.graph;

import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;

import java.util.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.Serializable;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * <p/>
 * User: Pavel Bonev
 * Date: 2005-12-2
 * Time: 15:06:42
 */
public class GraphReport implements Serializable {
  public static final String line =   "-------------------------------------------------------------";
  public static final String title1 = "                            SUMMARY                          ";
  public static final String title2 = "                     NON-SHAREABLE CLASSES                   ";
  public static final String title3 = "                          ALL CLASSES                        ";

  private ArrayList reportRows = null;

  private boolean excludeList = true;
  private ClassesFilter filter = new ClassesFilter();

  static final long serialVersionUID = -5627454070385506062L;

  public GraphReport(ArrayList reportRows) {
    this.reportRows = reportRows;
  }

  public void setExcludeList(boolean flag) {
    excludeList = flag;
  }

  public void setClassesFilter(ClassesFilter filter) {
    this.filter = filter;
  }

  public static GraphReport buildReportForNonshareable(Object root) {
    return buildReportForNonshareable(root, false);
  }

  public static GraphReport buildReportForNonshareable(Object root, boolean includeTransients) {

    IdentityHashMap map = new IdentityHashMap();

    HashMap classInfo = new HashMap();

    Stack stack = new Stack();
    stack.push(root);

    while (!stack.isEmpty()) {
      int objectSize = 0;
      int refCounter = 0;
      Object obj = stack.pop();

      Class cl = obj.getClass();
      if (cl == Class.class) {
        continue;
      }

      if (map.containsKey(obj)) {
        continue;
      }
      map.put(obj,obj);

      if (cl.isArray()) {
        // CASE 1: Array of objects
        int size = Array.getLength(obj);
        Class compType = cl.getComponentType();
        objectSize += Node.arraySize(size, compType);

        int arLength = Array.getLength(obj);
        if (!cl.getComponentType().isPrimitive()) {
          for (int i = 0; i < arLength; i++) {
            Object child = Array.get(obj, i);
            if (child != null) {
              refCounter++;
              stack.push(child);
            }
          }
        }
      } else {
        // CASE 2: An Object
        objectSize += Node.OBJECT_SHELL_SIZE;

        // adding size of primitive fields
        Field[] _primitiveFields = Node.getPrimitiveFields(cl);
        if (_primitiveFields != null) {
          for (int i = 0; i < _primitiveFields.length; i++) {
            Class fieldType = _primitiveFields[i].getType();
            Class fieldCompType = fieldType.getComponentType();

            if (fieldType.isPrimitive()) {
              objectSize += Node.sizeofPrimitiveType(fieldType);
            } else if (fieldType.isArray() && fieldCompType.isPrimitive()) {
              try {
                if (_primitiveFields[i].get(obj) != null) {
                  int size = Array.getLength(_primitiveFields[i].get(obj));
                  objectSize += Node.arraySize(size, fieldCompType);
                }
              } catch (IllegalAccessException e) {
                //System.out.println(">> Exception : " + e.getMessage());
                if (Graph.isDebug()) {
                  e.printStackTrace();
                }
              }
            }
          }
        }

        // adding size of non-primitive fields and including the objects in the stack
        Field[] nonPrimitiveFields = Node.getNonPrimitiveFields(cl, includeTransients);
        if (nonPrimitiveFields != null) {
          for (int i = 0; i < nonPrimitiveFields.length; i++) {
            try {
              Object child = nonPrimitiveFields[i].get(obj);
              objectSize += Node.OBJECT_REF_SIZE;
              if (child != null) {
                refCounter++;
                stack.push(child);
              }
            } catch (IllegalAccessException e) {
              //System.out.println(">> Exception : " + e.getMessage());
              e.printStackTrace();
            }
          }
        }
      }

      // get shareability desc
      String className = cl.getName();
      //ShareabilityDescription desc = getShareabilityDescription(obj);
      ReportRow row = (ReportRow)classInfo.get(className);

      if (row == null) {
        row = new ReportRow();
        //row.desc = desc;
        classInfo.put(className, row);
      }

      row.objectCounter++;
      row.weight += objectSize;
      row.refCounter += refCounter;
    }

    return new GraphReport(new ArrayList(classInfo.values()));
  }

  public String[] generateTexts() {
    ArrayList texts = new ArrayList();
    ArrayList nonshareableClasses = new ArrayList();
    ArrayList allClasses = new ArrayList();

    int totalSize = 0;
    int objectCounter = 0;
    int refCounter = 0;
    int nonshareableClassesCounter = 0;
    int nonshareableObjectsCounter = 0;

    for (int i=0;i<reportRows.size();i++) {
      ReportRow row = (ReportRow)reportRows.get(i);
      String className = row.desc.getClassName();
      if (!(excludeList ^ filter.filterByDescription(className))) {
        continue;
      }

      totalSize += row.weight;
      objectCounter += row.objectCounter;
      refCounter += row.refCounter;
      if (!row.desc.getShareable()) {
        nonshareableObjectsCounter += row.objectCounter;
        nonshareableClassesCounter++;
        nonshareableClasses.add(row);
      }
      allClasses.add(row);
    }

    Collections.sort(nonshareableClasses);
    Collections.sort(allClasses);

    texts.add(line);
    texts.add(title1);
    texts.add(line);
    texts.add("Total size = " + totalSize + " byte(s)");
    texts.add("Number of instances = " + objectCounter);
    texts.add("Number of references = " + refCounter);
    texts.add("Non-shareable classes found = " + nonshareableClassesCounter);
    texts.add("Non-shareable class instances found = " + nonshareableObjectsCounter);

    texts.add(line);
    texts.add(title2);
    texts.add(line);

    for (int i=nonshareableClasses.size()-1;i>=0;i--) {
      ReportRow row = (ReportRow)nonshareableClasses.get(i);
      ShareabilityDescription desc = row.desc;
      texts.add(desc.getClassName() + " : " + desc.getReason() + " : " +  row.objectCounter +
                " instance(s) : " + row.weight + " byte(s)");
    }

    texts.add(line);
    texts.add(title3);
    texts.add(line);

    for (int i=allClasses.size()-1;i>=0;i--) {
      ReportRow row = (ReportRow)allClasses.get(i);
      String className = row.desc.getClassName();
      texts.add(className + " : " + row.objectCounter + " instance(s) : " + row.weight + " byte(s)");
    }

    return (String[])texts.toArray(new String[texts.size()]);
  }

  public static ShareabilityDescription getShareabilityDescription(Object value) {
//    Class _class = value.getClass();
//    ShareabilityDescription desc = null;
//
//    com.sap.vmc.core.sharing.ShareabilityAnalyzer analyzer = com.sap.vmc.core.sharing.ShareabilityAnalyzer.getInstance();
//    try {
//      com.sap.vmc.core.sharing.ShareabilityProperties props = analyzer.examineClass(_class);
//      desc = Graph.buildShareabilityDescription(props);
//
//      return desc;
//    } catch (Throwable t) {
//      desc = new ShareabilityDescription(_class.getName(), false, Graph.getStackTrace(t));
//      t.printStackTrace();
//    }
//    return desc;
    return null;
  }

  public void printReport(PrintWriter output) {
    String[] textLines = generateTexts();
    if (textLines == null) {
      return;
    }

    for (int i=0;i<textLines.length;i++) {
      output.println(textLines[i]);
    }
  }

  public void printReport(PrintStream output) {
    String[] textLines = generateTexts();
    if (textLines == null) {
      return;
    }

    for (int i=0;i<textLines.length;i++) {
      output.println(textLines[i]);
    }
  }
}
