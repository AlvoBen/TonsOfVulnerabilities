package com.sap.engine.core.session.persistent.file.util;

import java.io.FilenameFilter;
import java.io.File;

/**
 * User: pavel-b
 * Date: 2006-8-30
 * Time: 17:06:59
 */
public class FilenameFilterImpl implements FilenameFilter {
  public static int FILES_ONLY = 0;
  public static int DIRS_ONLY = 1;
  public static int FILES_AND_DIRS = 2;

  public static final String SESSION_DIR_PREFIX = "s_";
  public static final String DOMAIN_DIR_PREFIX = "dom_";

  private String prefix = null;
  private int fileType = FILES_AND_DIRS;


  public FilenameFilterImpl(String prefix, int fileType) {
    this.prefix = prefix;
    this.fileType = fileType;
  }

  public boolean accept(File dir, String name) {
    boolean res = false;
    File file = new File(dir, name);


    if (checkType(file) && checkName(name)) {
      res = true;
    }

    return res;
  }

  private boolean checkType(File file) {
    boolean res = false;

    if (fileType == FILES_AND_DIRS) {
      res = true;
    } else if (fileType == FILES_ONLY) {
      if (file.isFile()) {
        res = true;
      }
    } else if (fileType == DIRS_ONLY) {
      if (file.isDirectory()) {
        res = true;
      }
    }

    return res;
  }

  private boolean checkName(String name) {
    boolean res = false;

    if (name.startsWith(prefix)) {
      res = true;
    }

    return res;
  }
}
