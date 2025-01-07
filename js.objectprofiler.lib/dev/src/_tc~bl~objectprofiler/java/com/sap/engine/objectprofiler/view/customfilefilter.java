package com.sap.engine.objectprofiler.view;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class CustomFileFilter extends FileFilter {
  private String fileExtension = null;
  private String fileDesc = null;

  public CustomFileFilter(String fileExtension, String fileDesc) {
    this.fileDesc = fileDesc;
    this.fileExtension = fileExtension;
  }

  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }

    if (fileExtension == null) {
      return false;
    }

    String extension = getExtension(f);

    if (extension != null) {
      if (extension.equals(fileExtension))
        return true;
      else
        return false;
    }

    return false;
  }

  public String getDescription() {
    return fileDesc;
  }

  private String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 &&  i < s.length() - 1) {
      ext = s.substring(i+1).toLowerCase();
    }
    return ext;
  }
}
