package com.sap.jmx.monitoring.api;

import java.util.Iterator;

/**
 * The <code>TwinIterator</code> is only a helper interface. It must be
 * public for visibility reasons.
 */
public interface TwinIterator
{
  /**
   * Returns an Iterartor over the keys.
   * @return an Iterartor over the keys.
   */
  public Iterator getKeys();
  
  /**
   * Returns an Iterartor over the values.
   * @return an Iterartor over the values.
   */
  public Iterator getValues();
}
