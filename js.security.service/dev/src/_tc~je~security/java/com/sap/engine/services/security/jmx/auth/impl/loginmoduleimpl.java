package com.sap.engine.services.security.jmx.auth.impl;

import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.services.security.jmx.auth.LoginModule;
import com.sap.engine.services.security.jmx.auth.MapEntry;
import com.sap.jmx.modelhelper.ChangeableCompositeData;

/**
 * @author Georgi Dimitrov
 */
public class LoginModuleImpl extends ChangeableCompositeData implements
    LoginModule {

  private static CompositeType myType;

  /**
   * Conctructs LoginModule from LoginModuleConfiguration
   * 
   * @param loginModule LoginModuleConfiguration
   * @throws OpenDataException
   */
  public LoginModuleImpl(LoginModuleConfiguration loginModule)
      throws OpenDataException {

    super( myType = AuthUtil.getCompositeType( myType, LoginModule.class ) );

    String description = loginModule.getDescription();
    String displayName = loginModule.getName();
    String className = loginModule.getLoginModuleClassName();
    Map map = loginModule.getOptions();
    MapEntry[] options = AuthUtil.convertMapToMapEntries( map );

    setDisplayName( displayName );
    setClassName( className );
    setDescription( description );
    setOptions( options );

  }

  public LoginModuleImpl(CompositeData data) {
    super( data );
  }

  /**
   * @param displayName
   * @param className
   * @param description
   * @param entries
   * @throws OpenDataException
   */
  public LoginModuleImpl(String displayName, String className,
      String description, MapEntry[] options) throws OpenDataException {

    super( myType = AuthUtil.getCompositeType( myType, LoginModule.class ) );
    setDisplayName( displayName );
    setClassName( className );
    setDescription( description );
    setOptions( options );

  }

  public String getDescription() {
    return (String) get( DESCRIPTION );
  }

  public String setDescription(String description) {
    return (String) set( DESCRIPTION, description );
  }

  public String getDisplayName() {
    return (String) get( DISPLAY_NAME );
  }

  public String setDisplayName(String displayName) {
    return (String) set( DISPLAY_NAME, displayName );
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