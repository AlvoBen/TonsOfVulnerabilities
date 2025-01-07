package com.sap.engine.services.security.jmx.auth.impl;

import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

import com.sap.engine.services.security.jmx.auth.AuthStackEntry;
import com.sap.engine.services.security.jmx.auth.MapEntry;
import com.sap.jmx.modelhelper.ChangeableCompositeData;

/**
 * @author Georgi Dimitrov
 */
public class AuthStackEntryImpl extends ChangeableCompositeData implements
    AuthStackEntry {

  private static CompositeType myType;

  public AuthStackEntryImpl(CompositeData data) {
    super( data );
  }

  public AuthStackEntryImpl(AppConfigurationEntry entry)
      throws OpenDataException {

    super( myType = AuthUtil.getCompositeType( myType, AuthStackEntry.class ) );

    String className = entry.getLoginModuleName();
    Map map = entry.getOptions();
    MapEntry[] options = AuthUtil.convertMapToMapEntries( map );
    LoginModuleControlFlag controlFlag = entry.getControlFlag();
    String flag = AuthUtil.flagToString( controlFlag );

    setClassName( className );
    setOptions( options );
    setFlag( flag );

  }

  public AuthStackEntryImpl(String className, MapEntry[] options, String flag)
      throws OpenDataException {

    super( myType = AuthUtil.getCompositeType( myType, AuthStackEntry.class ) );

    setClassName( className );
    setOptions( options );
    setFlag( flag );

  }

  public String getFlag() {
    return (String) get( FLAG );
  }

  public String setFlag(String flag) {
    return (String) set( FLAG, flag );
  }

  public String getClassName() {
    return (String) get( CLASS_NAME );
  }

  public String setClassName(String className) {
    return (String) set( CLASS_NAME, className );
  }

  public MapEntry[] getOptions() {
    CompositeData[] data = (CompositeData[]) get( OPTIONS );
    MapEntry[] options = AuthUtil.makeMapEntryArray( data );
    return options;
  }

  public MapEntry[] setOptions(MapEntry[] options) {
    return (MapEntry[]) set( OPTIONS, options );
  }

}