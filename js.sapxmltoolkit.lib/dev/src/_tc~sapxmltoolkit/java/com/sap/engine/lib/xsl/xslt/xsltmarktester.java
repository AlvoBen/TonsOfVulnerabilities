package com.sap.engine.lib.xsl.xslt;

import java.util.*;
import java.io.*;

public class XSLTMarkTester {

  protected Vector infos = new Vector();

  public Vector getTests() {
    return infos;
  }

  public XSLTMarkTester(String fname) throws Exception {
    init(fname);
  }

  private Vector init(String fname) throws FileNotFoundException {
    BufferedReader in = new BufferedReader(new FileReader(fname));
    String line;
    String basedir = fname.substring(0, fname.lastIndexOf("\\") + 1);
    try {
      XSLTestInfo ti = null;

      while ((line = in.readLine()) != null) {
        line.trim();
        if (line.length() == 0) {
          continue;
        }

        if (line.charAt(0) == '[') {
          if (ti != null) {
            infos.add(ti);
          }

          ti = new XSLTestInfo();
          ti.name = line.substring(1, line.length() - 1);
        } else if (getKey(line).equals("input")) {
          ti.xml = basedir + getValue(line);
        } else if (getKey(line).equals("stylesheet")) {
          ti.xsl = basedir + (getValue(line));
        } else if (getKey(line).equals("output")) {
          ti.out = basedir + (getValue(line));
        } else if (getKey(line).equals("comment")) {
          ti.comment = (getValue(line));
        } else if (getKey(line).equals("reference")) {
          ti.ref = basedir + (getValue(line));
        } else if (getKey(line).equals("skipdriver")) {
          ti.skip = getValue(line).indexOf("inqmy") > -1;
        }
      }

      if (ti != null) {
        infos.add(ti);
      }

      return infos;
    } catch (IOException e) {
      //$JL-EXC$
      e.printStackTrace();
    }
    return infos;
  }

  private String getValue(String line) {
    return line.substring(line.indexOf("=") + 1).trim();
  }

  private String getKey(String line) {
    return line.substring(0, line.indexOf("=")).trim();
  }

  public XSLTestInfo getTest(String name) throws Exception {
    for (int i = 0; i < infos.size(); i++) {
      if (((XSLTestInfo) infos.get(i)).name.equals(name)) {
        return (XSLTestInfo) infos.get(i);
      }
    } 

    throw new Exception("Could not fine test named: " + name);
  }

}

