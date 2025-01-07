package com.sap.engine.services.security.remote;

import java.rmi.Remote;

public interface RemoteFilterUsername extends Remote {

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
   * Specifies a restriction for the user names.
   *
   * @param  restriction   the restriction type ( SPACES_USAGE, MAXIMUM_LENGTH, etc.).
   * @param  usage         the type of the restriction ( USAGE_ALLOWED, 16, etc.).
   */
  public void setRestriction(int restriction, int usage);
}