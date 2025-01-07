package com.sap.engine.services.rmi_p4.lite;

import java.util.HashSet;
import java.util.Properties;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FilenameFilter;

import com.sap.engine.services.rmi_p4.exception.P4Logger;

/**
 * User: I024084 e-mail:mladen.droshev@sap.com
 * Date: 2007-4-26
 */
public class ClassFilter {

  private static HashSet filters = new HashSet();

  public static final String DEFAULT_PROPERTY_FILE = "remoteLoader.properties";
  public static final String FILER_CLASSES = "p4.remote.filters";
  public static final String SYSTEM_PROP_FILE = "p4.remote.propertyFile";

  static {
    init();
  }

  public static void filter(String classname) throws ClassNotFoundException {
    FilenameFilter[] _f = (FilenameFilter[]) filters.toArray(new FilenameFilter[0]);
    if (_f != null && _f.length > 0) {
      for (FilenameFilter cfi : _f) {
        if(!cfi.accept(null, classname)){
          throw new ClassNotFoundException(cfi + " denied downloading of class: " + classname);
        }
      }
    }
  }

  public static void init() {
    Properties p = new Properties();
    Object o = System.getProperty(SYSTEM_PROP_FILE);
    File f;
    if (o != null && !((String) o).equals("")) {
      f = new File((String) o);
    } else {
      f = new File(DEFAULT_PROPERTY_FILE);
    }

    try {
      FileInputStream fis = new FileInputStream(f);
      p.load(fis);
    } catch (FileNotFoundException e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("ClassFilter", "Exception while search for filter for remote classloading: " + e.toString());
      }
    } catch (IOException io) {
      if (P4Logger.getLocation().beInfo()) {
        P4Logger.getLocation().infoT("ClassFilter", "Exception while read from filter for remote classloading: " + io.toString());
      }
    }
    HashSet<String> clazes = new HashSet<String>();

    /* parse from prop file */
    Object pr = p.getProperty(FILER_CLASSES);
    if (pr != null && !((String) pr).equals("")) {
      parse((String) pr, ";", clazes);
    }

    /* parse from SystemProperties */
    pr = System.getProperty(FILER_CLASSES);
    if (pr != null && !((String) pr).equals("")) {
      parse((String) pr, ";", clazes);
    }

    /* try to load parsed classes */
    load(clazes);

  }

  private static void parse(String text, String delim, HashSet<String> clazes) {
    StringTokenizer st = new StringTokenizer(text, delim);
    while (st.hasMoreTokens()) {
      String _toket = st.nextToken();
      clazes.add(_toket);
    }
  }

  private static void load(HashSet<String> clazes) {
    String[] _c = clazes.toArray(new String[0]);
    if (_c != null && _c.length > 0) {
      for (String claz : _c) {
        try {
          Class cl = Class.forName(claz);
          FilenameFilter cfi = (FilenameFilter) cl.newInstance();
          filters.add(cfi);
        } catch (ClassNotFoundException e) {
          if (P4Logger.getLocation().bePath()) {
            P4Logger.getLocation().pathT("ClassFilter", "Class for filter " + e.getMessage() + " could not be found. \r\nYou have to create class that implements java.io.FilenameFilter and add this class as filter in \"p4.remote.filters\" property in "+DEFAULT_PROPERTY_FILE+" file to use custom filter. \r\nLean Client will work correctly without custom filter for remote classloading using default settings");
          }
        } catch (InstantiationException e) {
          if (P4Logger.getLocation().beWarning()) {
            P4Logger.trace(P4Logger.WARNING, "ClassFilter.load()", "Cannot instantiate class for filter: {0}", "ASJ.rmip4.rt2031", new Object []{e.toString()});
          }
        } catch (IllegalAccessException e) {
          if (P4Logger.getLocation().beWarning()) {
            P4Logger.trace(P4Logger.WARNING, "ClassFilter.load()", "Cannot access class for filter, check default constructor: {0}", "ASJ.rmip4.rt2032", new Object []{e.toString()});
          }
        } catch (ClassCastException e) {
          if (P4Logger.getLocation().beWarning()) {
            P4Logger.trace(P4Logger.WARNING, "ClassFilter.load()", "Given filter is not an instance of java.io.FilenameFilter. Check entries in {0} file in \"{1}\" property. Cannot use {2} as custom filter: {3}", "ASJ.rmip4.rt2033", new Object []{DEFAULT_PROPERTY_FILE, FILER_CLASSES, claz, e.toString()});
          }
        }
      }
    }
  }
}
