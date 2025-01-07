package com.sap.engine.services.security.userstore;

import com.sap.engine.services.security.remote.RemoteFilterPassword;
import com.sap.engine.interfaces.security.userstore.spi.FilterPassword;
import com.sap.engine.interfaces.security.userstore.context.UserContext;

public class RemoteFilterPasswordImpl extends javax.rmi.PortableRemoteObject implements RemoteFilterPassword {
    private UserContext context = null;
    private FilterPassword filter = null;

  public RemoteFilterPasswordImpl(UserContext context) throws java.rmi.RemoteException {
      this.context = context;
      this.filter = context.getFilterPassword();
  }

  /**
   * Tests a password for the restrictions.
   *
   * @param   password   the password to test
   *
   * @return  true, if the password is correct.
   */
  public boolean filterPassword(char[] password) {
    return filter.filterPassword(password);
  }
  
  public boolean filterPassword(char[] password, String userName) {
    return filter.filterPassword(password, userName);
  }


  /**
   * Generates a random password with the restrictions.
   *
   * @return   the generated password
   */
  public char[] generatePassword() {
    return filter.generatePassword();
  }
  
  public char[] generatePassword(String userName) {
    return filter.generatePassword(userName);
  }


  /**
   * Returns the restriction for the passwords.
   *
   * @param   restriction   the restriction type ( SPACES_USAGE, MAXIMUM_LENGTH, etc.).
   *
   * @return  the type of the restriction ( USAGE_ALLOWED, 16, etc.).
   */
  public int getRestriction(int restriction) {
    return filter.getRestriction(restriction);
  }


  /**
   * Returns an array of printable strings describing the restriction. Each String
   * is a short description of the restriction with constant equal to the array index.
   * The array should be at least of lengh 5 because the use of the above declared
   * restrictions is mandatory.
   *
   * @return  the restriction types information.
   */
  public String[] getRestrictionsInfo() {
    return filter.getRestrictionsInfo();
  }


  /**
   * Returns an array of printable strings describing the valid values of restrictions.
   * Each String is a short description of the usage with constant equal to the array index.
   * The array should be at least of lengh 4 because the use of the above declared
   * restrictions is mandatory.
   *
   * @return  the restriction values information.
   */
  public String[] getUsageInfo() {
    return filter.getUsageInfo();
  }

  /**
   * Specifies a restriction for the passwords.
   *
   * @param  restriction   the restriction type ( SPACES_USAGE, MAXIMUM_LENGTH, etc.).
   * @param  usage         the type of the restriction ( USAGE_ALLOWED, 16, etc.).
   */
  public void setRestriction(int restriction, int usage) {
    filter.setRestriction(restriction, usage);
    context.setFilterPassword(filter);
  }
}