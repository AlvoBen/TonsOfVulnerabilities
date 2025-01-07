package com.sap.jmx.monitoring.api;

import java.util.Iterator;
import java.util.Vector;

/**
 * This class is used by <code>VersionResourceMBean</code> as return type of the
 * method {@link VersionResourceMBean#getVersion}.
 */
public final class VersionInfo implements TwinIterator
{
  /**
   * Maximal length of a version value (255 characters).
   */  
  public static final int MAX_VERSION_VALUE_LENGTH = 255;
  
  private static final int APPLICATION = 0;
  private static final int MAJOR_VERSION = 1;
  private static final int MINOR_VERSION = 2;
  private static final int SUPPORT_PACKAGE = 3;
  private static final int BUILD_TIME = 4;
  private static final int CHANGELIST_NUMBER = 5;
  private static final int ADDITIONAL_INFORMATION = 6;
  
  private Vector keys;
  private Vector values;
  
  /**
   * Constructs a new <code>VersionInfo</code> object.
   * Each version value must have at most 
   * {@link #MAX_VERSION_VALUE_LENGTH} characters, otherwise the
   * String is automatically truncated to {@link #MAX_VERSION_VALUE_LENGTH}
   * characters.
   * 
   * @param application the application name.
   * 
   * @param majorVersion the major version.
   * 
   * @param minorVersion the minor version.
   * 
   * @param supportPackage the support package.
   * 
   * @param buildTime the build time.
   * 
   * @param changelistNumber the changelist number.
   */
  public VersionInfo(
    final String application,
    final String majorVersion,
    final String minorVersion,
    final String supportPackage,
    final String buildTime,
    final String changelistNumber)
  {
    this(
      application,
      majorVersion,
      minorVersion,
      supportPackage,
      buildTime,
      changelistNumber,
      "");
  }
  
  /**
   * Constructs a new <code>VersionInfo</code> object.
   * Each version value must have at most 
   * {@link #MAX_VERSION_VALUE_LENGTH} characters, otherwise the
   * String is automatically truncated to {@link #MAX_VERSION_VALUE_LENGTH}
   * characters.
   * 
   * @param application the application name.
   * 
   * @param majorVersion the major version.
   * 
   * @param minorVersion the minor version.
   * 
   * @param supportPackage the support package.
   * 
   * @param buildTime the build time.
   * 
   * @param changelistNumber the changelist number.
   * 
   * @param additionalInformation additional information, e.g. JVM version in J2SE.
   */
  public VersionInfo(
    final String application,
    final String majorVersion,
    final String minorVersion,
    final String supportPackage,
    final String buildTime,
    final String changelistNumber,
    final String additionalInformation)
  {
    keys = new Vector();
    keys.add(APPLICATION, "Application");
    keys.add(MAJOR_VERSION, "Major version");
    keys.add(MINOR_VERSION, "Minor version");
    keys.add(SUPPORT_PACKAGE, "Support package");
    keys.add(BUILD_TIME, "Build time");
    keys.add(CHANGELIST_NUMBER, "Changelist number");
    keys.add(ADDITIONAL_INFORMATION, "Additional information");
    
    values = new Vector();
    values.add(APPLICATION, application);
    values.add(MAJOR_VERSION, majorVersion);
    values.add(MINOR_VERSION, minorVersion);
    values.add(SUPPORT_PACKAGE, supportPackage);
    values.add(BUILD_TIME, buildTime);
    values.add(CHANGELIST_NUMBER, changelistNumber);
    values.add(ADDITIONAL_INFORMATION, additionalInformation);
  }
  
  /**
   * Method getApplication.
   * 
   * @return String
   */
  public String getApplication()
  {
    return (String) values.get(APPLICATION);
  }
  
  /**
   * Method getMajorVersion.
   * 
   * @return String
   */
  public String getMajorVersion()
  {
    return (String) values.get(MAJOR_VERSION);
  }
  
  /**
   * Method getMinorVersion.
   * 
   * @return String
   */
  public String getMinorVersion()
  {
    return (String) values.get(MINOR_VERSION);
  }
  
  /**
   * Method getSupportPackage.
   * 
   * @return String
   */
  public String getSupportPackage()
  {
    return (String) values.get(SUPPORT_PACKAGE);
  }
  
  /**
   * Returns the buildTime.
   * 
   * @return String
   */
  public String getBuildTime()
  {
    return (String) values.get(BUILD_TIME);
  }

  /**
   * Returns the changelist number.
   * 
   * @return String
   */
  public String getChangelistNumber()
  {
    return (String) values.get(CHANGELIST_NUMBER);
  }
  
  /**
   * Returns the additional information.
   * 
   * @return String
   */
  public String getAdditionalInformation()
  {
    return (String) values.get(ADDITIONAL_INFORMATION);
  }
  
  /**
   * @see com.sap.jmx.monitoring.api.TwinIterator#getKeys()
   */
  public Iterator getKeys()
  {
    return keys.iterator();
  }
  
  /**
   * @see com.sap.jmx.monitoring.api.TwinIterator#getValues()
   */
  public Iterator getValues()
  {
    return values.iterator();
  }
  
  /**
   * Returns the whole information.
   * 
   * @return String[][]
   */
  public String[][] getInfo()
  {
    String[][] info = new String[2][6];
    for (int i = 0; i < 6; i++)
    {
      info[0][i] = (String) keys.elementAt(i);
      info[1][i] = (String) values.elementAt(i);
    }
    return info;
  }
}
