package com.sap.engine.lib.xsl.xslt.output;

import java.util.*;

/**
 * <p>
 * Encapsulates static final variables, describing the extra options
 * that this implementation of the output package supports, and
 * their defaults.
 * </p>
 * <p>
 * Names of options are URIs.
 * </p>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public final class Options {

  public static final HashSet ALLOWED = new HashSet();
  public static final HashSet DEFAULT = new HashSet();
  public static final String PREFIX = "http://inqmy.com/output/";
  public static final String PRINT_IGNORABLE_WHITESPACE = PREFIX + "print-ignorable-whitespace";
  public static final String SORT_ATTRIBUTES = PREFIX + "sort-attributes";
  public static final String SORT_BY_LOCAL_NAME = PREFIX + "sort-by-local-name";

  static {
    //
    ALLOWED.add(PRINT_IGNORABLE_WHITESPACE);
    ALLOWED.add(SORT_ATTRIBUTES);
    ALLOWED.add(SORT_BY_LOCAL_NAME);
    //
    DEFAULT.add(SORT_ATTRIBUTES);
    DEFAULT.add(SORT_BY_LOCAL_NAME);
  }

  private Options() {

  }

}

