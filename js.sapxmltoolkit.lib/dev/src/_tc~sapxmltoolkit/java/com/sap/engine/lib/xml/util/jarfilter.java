package com.sap.engine.lib.xml.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.sap.engine.lib.log.LogWriter;

public class JarFilter {

  private final int FILTER_COUNT = 5;
  private JarFile jarFile;
  private Manifest manifest;
  private String[] filter;
  private int count;
  private int depth;
  private int first, second;
  private boolean isActive;

  public JarFilter(String jarName) throws IOException {
    if (jarName == null) {
      throw new IOException("Jar name is null");
    }

    jarFile = new JarFile(jarName);
    try {
      manifest = jarFile.getManifest();
    } catch (Exception e) {
      //$JL-EXC$
      manifest = null;
    }
    isActive = true;
    filter = new String[FILTER_COUNT];
    count = 0;
    Enumeration entries = jarFile.entries();

    while (entries.hasMoreElements()) {
      JarEntry entry = (JarEntry) entries.nextElement();

      if (entry.getName().endsWith(".class")) {
        addEntry(entry);
      }
    }
  }

  public void print() {
    for (int i = 0; i < FILTER_COUNT; i++) {
      LogWriter.getSystemLogWriter().println(i + " " + filter[i]); //$JL-SYS_OUT_ERR$
    } 
  }

  public boolean accept(String name) {
    if (!isActive) {
      return true;
    } else {
      for (int i = 0; i < count; i++) {
        if (name.startsWith(filter[i])) {
          return true;
        }
      } 

      return false;
    }
  }

  public JarFile getJar() {
    return jarFile;
  }

  public Manifest getManifest() {
    return manifest;
  }

  public void open(String jarName) throws IOException {
    if (jarName == null) {
      throw new IOException("Jar name is null");
    }

    jarFile = new JarFile(jarName);
    try {
      manifest = jarFile.getManifest();
    } catch (Exception e) {
      //$JL-EXC$
      manifest = null;
    }
  }

  private void addEntry(JarEntry entry) {
    if (!isActive) {
      return;
    }

    if (count < FILTER_COUNT) {
      filter[count++] = entry.getName();
      int maxDepth = -1;
      int pos = -1;

      for (int i = 0; i < (count - 1); i++) {
        int m = getEqualLength(filter[i], filter[count - 1]);

        if (m == -1) { //one is longer than the other but equal in the begining
          if (filter[i].length() > filter[count - 1].length()) {
            filter[i] = filter[count - 1];
            filter[--count] = null;
            recheck(i);
          } else {
            filter[--count] = null;
          }

          return;
        } else if (m > maxDepth) {
          maxDepth = m;
          pos = i;
        }
      } 

      if (maxDepth > depth) {
        depth = maxDepth;
        first = pos;
        second = count - 1;
      }
    } else {
      String name = entry.getName();
      int maxDepth = -1;
      int pos = -1;

      for (int i = 0; i < FILTER_COUNT; i++) {
        int m = getEqualLength(filter[i], name);

        if (m == -1) { //one is longer than the other but equal in the begining
          if (filter[i].length() > name.length()) {
            filter[i] = name;
            recheck(i);
          }

          return;
        } else if (m > maxDepth) {
          maxDepth = m;
          pos = i;
        }
      } 

      if (maxDepth > depth) {
        filter[pos] = name.substring(0, maxDepth);
        recheck(pos);
      } else {
        merge();

        if (isActive) {
          filter[second] = name;
          recheck(first);
          calculate();
        }
      }
    }
  }

  private int getEqualLength(String a, String b) {
    if (a.length() > b.length()) {
      String c = b;
      b = a;
      a = c;
    }

    for (int i = 0; i < a.length(); i++) {
      if (a.charAt(i) != b.charAt(i)) {
        return i;
      }
    } 

    return -1;
  }

  private void merge() {
    if (filter[first].length() > filter[second].length()) {
      String c = filter[first];
      filter[first] = filter[second];
      filter[second] = c;
    }

    if (filter[first].length() > depth) {
      filter[first] = filter[first].substring(0, depth);
    }

    if (depth == 0) {
      isActive = false;
      filter = null;
    }
  }

  private void calculate() {
    depth = -1;

    for (int i = 0; i < count; i++) {
      for (int j = i + 1; j < count; j++) {
        int m = getEqualLength(filter[i], filter[j]);

        if ((m != -1) && (m > depth)) {
          depth = m;
          first = i;
          second = j;
        }
      } 
    } 
  }

  private void recheck(int pos) {
    for (int i = 0; i < count; i++) {
      if ((i != pos) && (filter[i].startsWith(filter[pos]))) {
        if (i < pos) {
          filter[i] = filter[pos];
          filter[pos] = filter[--count];
          pos = i;
          filter[count] = null;
        } else {
          filter[i] = filter[--count];
          filter[count] = null;
          i--;
        }
      }
    } 
  }

}

