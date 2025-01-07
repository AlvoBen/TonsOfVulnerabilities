package com.sap.engine.services.security.server.jaas.cclm;

import java.util.Map;

/**
 * Implementation of Map.Entry interface for an object representing a pair of strings.
 * In our case used for storing a DistinguishedName type ( <key,value> pair ) in a java.util.Map
 *
 * @version 1.00 2005-12-20
 * @author Rumen Barov i033802
 */
class StringPair implements Map.Entry {

  protected String key = null;
  protected String value = null;

  StringPair( String key, String value ){
    this.key = key;
    this.value = value;
  }

  public Object getKey(){
    return key;
  }

  public Object getValue(){
    return value;
  }

  public Object setKey( Object key ){
    String res = new String( this.key );
    if ( key instanceof String )
      this.key = (String) key;
    else
      this.key = value.toString();
    return res;
  }

  public Object setValue( Object value ){
    String res = new String( this.value );
    if ( value instanceof String )
      this.value = (String) value;
    else
      this.value = value.toString();
    return res;
  }

  public String toString(){
    String res = "\n";
    res += super.toString();
    res += "\n\tkey  ->" + key;
    res += "\n\tvalue->" + value;
    return res;
  }

  public boolean equals( Object o ){
    if ( ! ( o instanceof Map.Entry ) ) {
      return false;
    }

    Map.Entry entry = (Map.Entry) o;
    if ( !key.equals( entry.getKey() ) ) {
      return false;
    }
    if ( !value.equals( entry.getValue() ) ) {
      return false;
    }
    return true;
  }

  public int hashCode(){
    if ( null != key && null != value ) {
      return key.hashCode() + value.hashCode();
    }
    return 0;
  }
}
