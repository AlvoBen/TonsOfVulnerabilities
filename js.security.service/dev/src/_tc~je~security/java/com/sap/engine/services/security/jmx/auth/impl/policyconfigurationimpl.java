package com.sap.engine.services.security.jmx.auth.impl;

import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.security.auth.login.AppConfigurationEntry;

import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.services.security.jmx.auth.AuthStackEntry;
import com.sap.engine.services.security.jmx.auth.MapEntry;
import com.sap.engine.services.security.jmx.auth.PolicyConfiguration;
import com.sap.jmx.modelhelper.ChangeableCompositeData;

/**
 * @author Georgi Dimitrov
 */
public class PolicyConfigurationImpl extends ChangeableCompositeData implements
    PolicyConfiguration {
 
  public static CompositeType myType;

  /**
   * 
   * @param policyConfigurationName
   * @param policyConfiguration
   * @throws OpenDataException
   */
  public PolicyConfigurationImpl(String policyConfigurationName,
      SecurityContext policyConfiguration) throws OpenDataException {

    super( myType = AuthUtil.getCompositeType( myType, PolicyConfiguration.class ) );
 
    byte typeByte = policyConfiguration.getPolicyConfigurationType();
    Byte type = new Byte(typeByte);
    
    String template = policyConfiguration.getAuthenticationContext().getTemplate();
    
    Map map = policyConfiguration.getAuthenticationContext().getProperties();
    MapEntry[] properties = AuthUtil.convertMapToMapEntries(map);

    AppConfigurationEntry[] loginModules = policyConfiguration
        .getAuthenticationContext().getLoginModules();
    AuthStackEntry[] authStackEntries = new AuthStackEntry[loginModules.length];
    for (int i = 0; i < loginModules.length; i++) {
      authStackEntries[i] = new AuthStackEntryImpl(loginModules[i]);
    }
     
    setName(policyConfigurationName);
    setType(type);
    setTemplate(template);
    setAuthStack(authStackEntries);
    setProperties(properties);

  }

  /**
   * 
   * @param policyConfigurationName
   * @param type
   * @param template
   * @param authStackEntries
   * @param properties
   * @throws OpenDataException
   */
  public PolicyConfigurationImpl(String policyConfigurationName, Byte type,
      String template, AuthStackEntry[] authStackEntries, MapEntry[] properties)
      throws OpenDataException {

    super(myType = AuthUtil.getCompositeType( myType, PolicyConfiguration.class ));

    setName(policyConfigurationName);
    setType(type);
    setTemplate(template);
    setAuthStack(authStackEntries);
    setProperties(properties);

  }

  public PolicyConfigurationImpl(CompositeData data) {
    super(data);
  }

  public String getName() {
    return (String) get(NAME);
  }

  public String setName(String name) {
    return (String) set(NAME, name);
  }

  public Byte getType() {
    return (Byte) get(TYPE);
  }

  public Byte setType(Byte type) {
    return (Byte) set(TYPE, type);
  }

  public String getTemplate() {
    return (String) get(TEMPLATE);
  }

  public String setTemplate(String template) {
    return (String) set(TEMPLATE, template);
  }

  public AuthStackEntry[] getAuthStack() {
    CompositeData[] data = (CompositeData[]) get(STACK_ENTRIES);
    AuthStackEntry[] authStackEntries = AuthUtil.makeAuthStackEntryArray(data);
    return authStackEntries;
  }

  public AuthStackEntry[] setAuthStack(AuthStackEntry[] authStack) {
    return (AuthStackEntry[]) set(STACK_ENTRIES, authStack);
  }

  public MapEntry[] getProperties() {
    CompositeData[] data = (CompositeData[]) get(PROPERTIES);
    MapEntry[] mapEntries = AuthUtil.makeMapEntryArray(data); 
    return mapEntries;
  }

  public MapEntry[] setProperties(MapEntry[] properties) {
    return (MapEntry[]) set(PROPERTIES, properties);
  }

}