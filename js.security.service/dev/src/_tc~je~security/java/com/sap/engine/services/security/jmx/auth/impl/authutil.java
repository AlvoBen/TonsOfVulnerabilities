package com.sap.engine.services.security.jmx.auth.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

import com.sap.engine.interfaces.security.userstore.config.RuntimeLoginModuleConfiguration;
import com.sap.engine.services.security.jmx.auth.AuthStackEntry;
import com.sap.engine.services.security.jmx.auth.LoginModule;
import com.sap.engine.services.security.jmx.auth.MapEntry;
import com.sap.engine.services.security.jmx.auth.PolicyConfiguration;
import com.sap.jmx.modelhelper.OpenTypeFactory;

/**
 * @author Georgi Dimitrov
 */
public class AuthUtil {

  /**
   * Converts CompositeData to MapEntry[]
   * 
   * @param data
   * @return MapEntry[]
   */
  public static MapEntry[] makeMapEntryArray(CompositeData[] data) {

    if (data == null) {
      return new MapEntry[0];
    }
    MapEntry[] entries = new MapEntry[data.length];
    for (int i = 0; i < data.length; i++) {
      entries[i] = new MapEntryImpl( data[i] );
    }
    return entries;
  }

  /**
   * * Converts CompositeData to LoginModule[]
   * 
   * @param data
   * @return LoginModule[] or null in case of OpenDataException
   */
  public static LoginModule[] makeLoginModuleArray(CompositeData[] data) {

    if (data == null) {
      return new LoginModule[0];
    }

    LoginModule[] loginModules = new LoginModule[data.length];
    for (int i = 0; i < data.length; i++) {
      loginModules[i] = new LoginModuleImpl( data[i] );
    }
    return loginModules;
  }

  public static PolicyConfiguration[] makePolicyConfigurationArray(
      CompositeData[] data) {

    if (data == null) {
      return new PolicyConfiguration[0];
    }
    PolicyConfiguration[] policyConfigurations = new PolicyConfiguration[data.length];
    for (int i = 0; i < data.length; i++) {
      policyConfigurations[i] = new PolicyConfigurationImpl( data[i] );
    }
    return policyConfigurations;
  }

  public static AuthStackEntry[] makeAuthStackEntryArray(CompositeData[] data) {

    if (data == null) {
      return new AuthStackEntry[0];
    }
    AuthStackEntry[] entries = new AuthStackEntry[data.length];
    for (int i = 0; i < data.length; i++) {
      entries[i] = new AuthStackEntryImpl( data[i] );
    }
    return entries;
  }

  /**
   * Converts Map to MapEntry[]
   * 
   * @param map
   * @return MapEntry[]
   */
  public static MapEntry[] convertMapToMapEntries(Map map) {
    if (map == null) {
      return new MapEntry[0];
    }

    MapEntry[] mapEntries = new MapEntry[map.size()];

    Iterator entries = map.entrySet().iterator();
    Map.Entry currentMapEntry = null;
    int i = 0;

    while (entries.hasNext()) {
      currentMapEntry = (Map.Entry) entries.next();
      String key = (String)currentMapEntry.getKey();
      String value = (String)currentMapEntry.getValue();
      if ( value == null ) {
        value = ""; 	
      }
      try {
        mapEntries[i++] = new MapEntryImpl( key, value );
      } catch (OpenDataException e) {
        mapEntries = null;
        break;
      }
    }

    return mapEntries;
  }

  /**
   * Converts MapEntry[] to Map
   * 
   * @param mapEntries
   * @return Map
   */
  public static Map convertMapEntriesToMap(MapEntry[] mapEntries) {

    Map map = new HashMap( mapEntries.length );

    for (int i = 0; i < mapEntries.length; i++) {
      String key = mapEntries[i].getKey();
      String value = mapEntries[i].getValue();
      if( value==null ) {
        value = "";
      }
      map.put( key, value );
    }

    return map;
  }

  /**
   * Create AppConfigurationEntry for AuthStackEntry
   * 
   * @param jmxStackEntry the auth stack entry
   */
  public static AppConfigurationEntry makeAppConfigurationEntry(
      AuthStackEntry jmxStackEntry) {

    String className = jmxStackEntry.getClassName();
    String flag = jmxStackEntry.getFlag();
    AppConfigurationEntry.LoginModuleControlFlag controlFlag = getLoginModuleControlFlag( flag );
    Map options = convertMapEntriesToMap( jmxStackEntry.getOptions() );

    if (className == null) {
      throw new IllegalArgumentException(
          "Null value for login module class name." );
    }
    if (options == null) {
      options = new Properties();
    }
    AppConfigurationEntry appConfigurationEntry = new AppConfigurationEntry(
        className, controlFlag, options );
    return appConfigurationEntry;
  }

  /**
   * Get String for LoginModuleControlFlag
   * 
   * @param controlFlag
   * @return @throws IllegalArgumentException
   */
  public static String flagToString(LoginModuleControlFlag controlFlag)
      throws IllegalArgumentException {
    if (controlFlag.equals( LoginModuleControlFlag.OPTIONAL ))
      return "OPTIONAL";
    else if (controlFlag.equals( LoginModuleControlFlag.SUFFICIENT ))
      return "SUFFICIENT";
    else if (controlFlag.equals( LoginModuleControlFlag.REQUISITE ))
      return "REQUISITE";
    else if (controlFlag.equals( LoginModuleControlFlag.REQUIRED ))
      return "REQUIRED";
    else
      throw new IllegalArgumentException(
          "Incorrect value for LoginModuleControlFlag" );
  }

  /**
   * Get LoginModuleControlFlag for String
   * 
   * @param flag
   * @return
   */
  public static AppConfigurationEntry.LoginModuleControlFlag getLoginModuleControlFlag(
      String flag) {

    if (flag.equals( "SUFFICIENT" ))
      return AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
    else if (flag.equals( "REQUIRED" ))
      return AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
    else if (flag.equals( "REQUISITE" ))
      return AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
    else if (flag.equals( "OPTIONAL" ))
      return AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
    else {
      throw new IllegalArgumentException( "Incorrect value for control flag: "
          + flag );
    }
  }

  /**
   * @param type
   * @param classObject
   * @return @throws OpenDataException
   */
  public static CompositeType getCompositeType(CompositeType type,
      Class classObject) throws OpenDataException {

    CompositeType newType = type;
    if (newType == null) {
      newType = OpenTypeFactory.getCompositeType( classObject );
    }
    return newType;

  }

  /**
   * Create new RuntimeLoginModuleConfiguration
   * 
   * @param displayName
   * @param description
   * @param className
   * @param options
   * @return new RuntimeLoginModuleConfiguration
   */

  public static RuntimeLoginModuleConfiguration makeRuntimeLoginModuleConfiguration(
      String displayName, String description, String className, Map options) {

    String[] suitableAuth = new String[0];
    String[] notSuitableAuth = new String[0];
    String editor = "";

    RuntimeLoginModuleConfiguration runtimeLoginModuleConfiguration = new RuntimeLoginModuleConfiguration(
        displayName, description, className, options, suitableAuth,
        notSuitableAuth, editor );
    return runtimeLoginModuleConfiguration;

  }

}