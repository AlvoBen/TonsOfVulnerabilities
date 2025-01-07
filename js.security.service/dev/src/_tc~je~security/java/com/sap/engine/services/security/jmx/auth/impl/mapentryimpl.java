package com.sap.engine.services.security.jmx.auth.impl;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

import com.sap.engine.services.security.jmx.auth.MapEntry;
import com.sap.jmx.modelhelper.ChangeableCompositeData;

/**
 * @author Georgi Dimitrov
 */

public class MapEntryImpl extends ChangeableCompositeData implements MapEntry {

  private static CompositeType myType;

  public MapEntryImpl() throws OpenDataException {

    super( myType = AuthUtil.getCompositeType( myType, MapEntry.class ) );
    this.setKey( EMPTY );
    this.setValue( EMPTY );
  }

  public MapEntryImpl(CompositeData data) {
    super( data );
  }

  public MapEntryImpl(String key, String value) throws OpenDataException {
    this();
    setKey( key );
    setValue( value );
  }

  public String getKey() {
    return (String) get( KEY );
  }

  public String setKey(String key) {
    return (String) set( KEY, key );
  }

  public String getValue() {
    return (String) get( VALUE );
  }

  public String setValue(String value) {
    return (String) set( VALUE, value );
  }

}