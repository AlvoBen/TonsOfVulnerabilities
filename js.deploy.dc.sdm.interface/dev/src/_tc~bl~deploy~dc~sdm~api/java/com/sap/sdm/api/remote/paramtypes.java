package com.sap.sdm.api.remote;

/**
 * Summarizes the permitted types of <code>Param</code> objects.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * @see com.sap.sdm.api.remote.Param
 * @see com.sap.sdm.api.remote.ParamType
 * 
 *  * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used.
 */
public interface ParamTypes {

  public static final int UNKNOWN = -1;

  /**
   * An <code>int</code> representation of the <code>String</code> type.
   */
  public static final int STRING  = 0;

  /**
   * An <code>int</code> representation of the <code>boolean</code> type.
   */
  public static final int BOOLEAN = 1;

  /**
   * An <code>int</code> representation of the <code>byte</code> type.
   */
  public static final int BYTE    = 2;

  /**
   * An <code>int</code> representation of the <code>short</code> type.
   */
  public static final int SHORT   = 3;

  /**
   * An <code>int</code> representation of the <code>int</code> type.
   */
  public static final int INT     = 4;

  /**
   * An <code>int</code> representation of the <code>long</code> type.
   */
  public static final int LONG    = 5;

  /**
   * An <code>int</code> representation of the <code>float</code> type.
   */
  public static final int FLOAT   = 6;

  /**
   * An <code>int</code> representation of the <code>double</code> type.
   */
  public static final int DOUBLE  = 7;


  public static final int MAX_TYPE = 8;


  public static final String UNKNOWN_S = "UNKNOWN";

  /**
   * A <code>String</code> representation of the <code>String</code> type.
   */
  public static final String STRING_S  = "STRING";

  /**
   * A <code>String</code> representation of the <code>boolean</code> type.
   */
  public static final String BOOLEAN_S = "BOOLEAN";

  /**
   * A <code>String</code> representation of the <code>byte</code> type.
   */
  public static final String BYTE_S    = "BYTE";

  /**
   * A <code>String</code> representation of the <code>short</code> type.
   */
  public static final String SHORT_S   = "SHORT";

  /**
   * A <code>String</code> representation of the <code>int</code> type.
   */
  public static final String INT_S     = "INT";

  /**
   * A <code>String</code> representation of the <code>long</code> type.
   */
  public static final String LONG_S    = "LONG";

  /**
   * A <code>String</code> representation of the <code>float</code> type.
   */
  public static final String FLOAT_S   = "FLOAT";

  /**
   * A <code>String</code> representation of the <code>double</code> type.
   */
  public static final String DOUBLE_S  = "DOUBLE";

  public static final String MAX_TYPE_S   = "No Type: internally used as maximum";

}
