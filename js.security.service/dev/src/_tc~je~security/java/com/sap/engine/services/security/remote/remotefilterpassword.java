package com.sap.engine.services.security.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteFilterPassword extends Remote {

  /**
   * Tests a password for the restrictions.
   *
   * @param   password   the password to test
   *
   * @return  true, if the password is correct.
   */
  public boolean filterPassword(char[] password);
  
  /**
   * Test password for the provided user name against predefined restrictions.
   *
   * @param   password  password as char array
   * @param   userName  user name as String
   *
   * @return  true, if the password is correct.
   */
  public boolean filterPassword(char[] password, String userName);


  /**
   * Generates a random password with the restrictions.
   *
   * @return   the generated password
   */
  public char[] generatePassword();
  
  /**
   * Generate random password for the provided user name against predefined restrictions.
   *
   * @param   userName  user name as String 
   * 
   * @return  random password as char array
   */
  public char[] generatePassword(String userName);


  /**
   * Returns the restriction for the passwords.
   *
   * @param   restriction   the restriction type ( SPACES_USAGE, MAXIMUM_LENGTH, etc.).
   *
   * @return  the type of the restriction ( USAGE_ALLOWED, 16, etc.).
   */
  public int getRestriction(int restriction);


  /**
   * Returns an array of printable strings describing the restriction. Each String
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
   * Specifies a restriction for the passwords.
   *
   * @param  restriction   the restriction type ( SPACES_USAGE, MAXIMUM_LENGTH, etc.).
   * @param  usage         the type of the restriction ( USAGE_ALLOWED, 16, etc.).
   */
  public void setRestriction(int restriction, int usage);
}