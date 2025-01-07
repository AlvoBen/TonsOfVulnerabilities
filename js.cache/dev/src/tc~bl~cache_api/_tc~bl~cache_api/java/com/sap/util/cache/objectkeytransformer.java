/*
 * Created on 2004.12.2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.util.cache;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ObjectKeyTransformer {

  /**
   * This method must be implemented in a way that in most cases for different object instances,
   * the String returned is also different.
   * 
   * @param key The key in the form of Object
   * @return The key in the form of String
   */
  public String transform(Object key);
  
  /**
   * If true - the transformer supports <code>reverse</code> method, i.e. original object can
   * be recreated using the String representation
   * 
   * @return True if the transformer supports <code>reverse</code>
   */
  public boolean reversible();
  
  /**
   * Reverses the transformation, given the key in the form of String
   * 
   * @param key The key in the form of String
   * @return The key in the form of Object
   */
  public Object reverse(String key);
  
}
