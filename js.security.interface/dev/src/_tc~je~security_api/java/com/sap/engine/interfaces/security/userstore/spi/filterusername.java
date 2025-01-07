/**
 * Copyright:    2002 by SAP AG
 * Company:      SAP AG, http://www.sap.com
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.userstore.spi;

/**
 * The FilterUsername Interface.
 *
 * @author  Boris Koeberle
 * @version 6.30
 */
public interface FilterUsername
  extends java.io.Serializable {

  /**
   * Case sensitivity restriction.
   */
  public final static int CASE_SENSITIVITY = 0;
  /**
   * Digits usage restriction.
   */
  public final static int DIGITS_USAGE = 1;
  /**
   * Maximum length of username restriction.
   */
  public final static int MAXIMUM_LENGTH = 2;
  /**
   * Minimum length of username restriction.
   */
  public final static int MINIMUM_LENGTH = 3;
  /**
   * Spaces usage restriction.
   */
  public final static int SPACES_USAGE = 4;
  /**
   * Type of usage specifying that the subject is optional.
   */
  public final static int USAGE_ALLOWED = 0;
  /**
   * Type of usage specifying that the subject must not appear in the username.
   */
  public final static int USAGE_FORBIDDEN = 1;
  /**
   * Type of usage specifying that the subject appearances in the username are
   * ignored. Used for special characters ignored by some user manager implementations.
   */
  public final static int USAGE_IGNORED = 2;
  /**
   * Type of usage specifying that the subject is optional.
   */
  public final static int USAGE_MANDATORY = 3;


  /**
   * Convert the encoded username filter to a FilterUsername instance.
   *
   * @param  data  the encoding of the filter.
   */
  public FilterUsername deserialize(byte[] data);


  /**
   * Test a user name for the restrictions.
   *
   * @param   userName   the user name to test
   *
   * @return  true, if the user name is correct.
   */
  public boolean filterUsername(String userName);


  /**
   * Generate a random user name with the restrictions.
   *
   * @return   the generated user name
   */
  public String generateUsername();


  /**
   * Return the restriction for the user name.
   *
   * @param   restriction  the restriction type ( SPACES_USAGE, MAXIMUM_LENGTH, etc.).
   *
   * @return  the type of the restriction ( USAGE_ALLOWED, 16, etc.).
   */
  public int getRestriction(int restriction);


  /**
   * Return an array of printable strings describing the restriction. Each String
   * is a short description of the restriction with constant equal to the array index.
   * The array should be at least of lengh 5 because the use of the above declared
   * restrictions is mandatory.
   *
   * @return  the restriction types information.
   */
  public String[] getRestrictionsInfo();


  /**
   * Returns an array of printable strings describing the valid values of restrictions.
   * Each String is a short description of the usage with constant equal to the array index.
   * The array should be at least of lengh 4 because the use of the above declared
   * restrictions is mandatory.
   *
   * @return  the restriction values information.
   */
  public String[] getUsageInfo();


  /**
   * Converts the user name filter to a byte array.
   *
   * @return  the encoding of the filter.
   */
  public byte[] serialize();


  /**
   * Specifies a restriction for the user names.
   *
   * @param  restriction   the restriction type ( SPACES_USAGE, MAXIMUM_LENGTH, etc.).
   * @param  usage         the type of the restriction ( USAGE_ALLOWED, 16, etc.).
   */
  public void setRestriction(int restriction, int usage);

}

